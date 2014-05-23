package com.r0adkll.alphahex;

import com.google.common.io.Files;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.sun.deploy.ui.AboutDialog;
import org.apache.log4j.Priority;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AlphaHexDialog extends JDialog {
    private JPanel contentPane;
    private JTextField tf_name;
    private JTextField tf_alpha;
    private JTextField tf_color;
    private JButton btn_create;
    private TextFieldWithBrowseButton colorTextField;

    private Project mProject;

    /**
     * Constructor
     * @param project   the current project reference (Must be android)
     */
    public AlphaHexDialog(Project project) {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(btn_create);

        mProject = project;

        btn_create.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onGenerate();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onGenerate() {

        String name = tf_name.getText();
        String color = tf_color.getText();
        String alpha = tf_alpha.getText();

        float percent = Float.valueOf(alpha);
        int code = (int) (percent * 255);
        String hex = Integer.toHexString(code);

        // Form the full hex code
        String fullHex = "#".concat(hex).concat(color);

        // Generate the resource definition to write in the colors.xml file
        String resourceDef = String.format("\t<color name=\"%s\">%s</color>", name, fullHex);

        // Find the strings.xml file from the project and insert the generated line into it
        VirtualFile resDir = findResFolder(mProject.getBaseDir());
        if(resDir != null) {
            String colorFilePath = resDir.getPath().concat("/values/colors.xml");
            try {

                // Get the color file and read all the lines from it
                File colorFile = new File(colorFilePath);
                List<String> lines = Files.readLines(colorFile, Charset.defaultCharset()); //readAllLines(colorFile.toPath(), Charset.defaultCharset());

                // Iterate and find where to insert color
                boolean didInsert = false;
                for(int i=0; i<lines.size()-1; i++){
                    String line = lines.get(i);
                    String nextLine = lines.get(i+1);
                    if(line.contains("color") && !nextLine.contains("color")){
                        lines.add(i, resourceDef);
                        didInsert = true;
                        break;
                    }
                }

                if(!didInsert){
                    for(int i=lines.size()-1; i>=0; i--){
                        String line = lines.get(i);
                        if(line.contains("</resources>")){
                            lines.add(i, resourceDef);
                            didInsert = true;
                            break;
                        }
                    }
                }

                // If it failed to insert don't bother writing
                if(!didInsert) return;

                // Combine into uber string
                String outString = "";
                for (String line : lines) {
                    outString = outString.concat(line).concat("\n");
                }

                // Write out the changes
                FileWriter writer = new FileWriter(colorFile);
                writer.write(outString);
                writer.close();

            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error creating color: " + e.getLocalizedMessage());
            }

        }else{
            JOptionPane.showMessageDialog(null, "Invalid res path");
        }

        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    /**
     * Recursively search the project directory for the android
     * resource folder
     *
     * @param dir   the current directory to search
     * @return      the resource folder, or null
     */
    private VirtualFile findResFolder(VirtualFile dir){
        VirtualFile[] children = dir.getChildren();
        if(children != null){
            for(int i=0; i<children.length; i++){
                VirtualFile child = children[i];
                if(child.isDirectory()){
                    if(child.getName().equalsIgnoreCase("res")){
                        return child;
                    }else{
                        VirtualFile childPotential = findResFolder(child);
                        if(childPotential != null && childPotential.getName().equalsIgnoreCase("res")){
                            return childPotential;
                        }
                    }
                }
            }
        }
        return null;
    }


}

package com.r0adkll.alphahex;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.wm.WindowManager;

import javax.swing.*;
import java.awt.*;

/**
 * Created by r0adkll on 5/22/14.
 */
public class CreateNewAlphaHex extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        // Show GUI form
        AlphaHexDialog dialog = new AlphaHexDialog(e.getProject());
        dialog.setLocationRelativeTo(WindowManager.getInstance().getFrame(e.getProject()));
        dialog.pack();
        dialog.setVisible(true);
    }

}

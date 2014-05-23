package com.r0adkll.alphahex;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.awt.*;

/**
 * Created by r0adkll on 5/22/14.
 */
public class CreateNewAlphaHex extends AnAction {

    public void actionPerformed(AnActionEvent e) {
        // Show GUI form
        AlphaHexDialog dialog = new AlphaHexDialog(e.getProject());
        dialog.setLocation(GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint());
        dialog.pack();
        dialog.setVisible(true);
    }

}

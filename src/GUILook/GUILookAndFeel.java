package org.example;

import javax.swing.*;

public class GUILookAndFeel {
    public static void setLookAndFeel() {
        try {
            //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

package fr.ign.cogit.geoxygene.util;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;

public class ImageUtil {
  public static void displayImageInWindow(BufferedImage image) {
    if (image != null) {
        // Use a JLabel in a JFrame to display the image
        javax.swing.JFrame frame = new javax.swing.JFrame();
        javax.swing.JLabel label = new javax.swing.JLabel(
                new javax.swing.ImageIcon(image));
        frame.getContentPane().add(label, BorderLayout.CENTER);

        frame.pack();
        frame.setVisible(true);
    }
  }
}

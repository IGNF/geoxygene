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
  
  /**
   * This method create and return a BufferdImage
   * with the same characteristics (size, data type) than the input image
   * @param img
   * @return
   */
  public static BufferedImage createBufferedImage(BufferedImage img) {
    BufferedImage newImage = new BufferedImage(
        img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
    return newImage;
  }
}

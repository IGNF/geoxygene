/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/
package fr.ign.cogit.geoxygene.util;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Utility class for image management
 * 
 */
public class ImageUtil {

    /**
     * Display a BufferedImage in a very simple new Frame
     * 
     * @param image
     *            image to display
     */
    public static void displayImageInWindow(BufferedImage image) {
        if (image != null) {
            // Use a JLabel in a JFrame to display the image
            javax.swing.JFrame frame = new javax.swing.JFrame();
            javax.swing.JLabel label = new javax.swing.JLabel(new javax.swing.ImageIcon(image));
            frame.getContentPane().add(label, BorderLayout.CENTER);

            frame.pack();
            frame.setVisible(true);
        }
    }

    /**
     * This method create and return a BufferedImage
     * with the same characteristics (size, data type) than the input image
     * 
     * @param img
     *            image to copy
     * @return empty newly created image with same size and data type
     */
    public static BufferedImage createBufferedImage(BufferedImage img) {
        BufferedImage newImage = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        return newImage;
    }

    /**
     * convert src image to given image format. if the image is already
     * in the specified format, it does nothing (image is not duplicated)
     * 
     * @param src
     *            image to convert
     * @param bufImgType
     *            destination image type (BufferedImage.**IMAGE TYPE**)
     * @return
     */
    public static BufferedImage convert(BufferedImage src, int bufImgType) {
        if (src == null) {
            return null;
        }
        if (src.getType() == bufImgType) {
            return src;
        }
        BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), bufImgType);
        Graphics2D g2d = img.createGraphics();
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();
        return img;
    }

}

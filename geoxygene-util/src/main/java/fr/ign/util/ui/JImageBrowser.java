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

package fr.ign.util.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * @author JeT
 * 
 */
public class JImageBrowser extends JPanel implements ActionListener {

    private static final long serialVersionUID = 8445828422009196670L; // Serializable UID

    private static final String CURRENT_DIRECTORY_PREFERENCE = "fr.ign.util.imageBrowser.currentDirectory";

    private JButton browseButton = null;
    private JLabel previewLabel = null;
    private JTextField filenameTextField = null;
    private final static ImageIcon loadingIcon = new ImageIcon(JImageBrowser.class.getResource("/images/loader64.gif"));
    private final static ImageIcon invalidIcon = new ImageIcon(JImageBrowser.class.getResource("/images/file_broken.png"));

    /**
     * 
     */
    public JImageBrowser() {
        this.setLayout(new BorderLayout());
        JPanel panel1 = new JPanel(new GridLayout(2, 1));
        panel1.add(this.getFilenameTextField());
        panel1.add(this.getBrowseButton());
        this.add(panel1, BorderLayout.CENTER);
        this.add(this.getPreviewLabel(), BorderLayout.EAST);
    }

    /**
     * filename text field
     */
    private JTextField getFilenameTextField() {
        if (this.filenameTextField == null) {
            this.filenameTextField = new JTextField(40);
        }
        return this.filenameTextField;
    }

    /**
     * filename text field
     */
    private JButton getBrowseButton() {
        if (this.browseButton == null) {
            this.browseButton = new JButton("browse...");
            this.browseButton.addActionListener(this);
        }
        return this.browseButton;
    }

    /**
     * filename text field
     */
    private JLabel getPreviewLabel() {
        if (this.previewLabel == null) {
            this.previewLabel = new JLabel();
            this.previewLabel.setSize(64, 64);
            this.previewLabel.setIcon(invalidIcon);
            this.previewLabel.setBorder(BorderFactory.createLineBorder(Color.white, 1));
        }
        return this.previewLabel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.getBrowseButton()) {
            ThumbnailFileChooser browser = new ThumbnailFileChooser();
            Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
            browser.setCurrentDirectory(new File(prefs.get(CURRENT_DIRECTORY_PREFERENCE, ".")));
            if (browser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                this.setImage(browser.getSelectedFile());
                prefs.put(CURRENT_DIRECTORY_PREFERENCE, browser.getCurrentDirectory().getAbsolutePath());
            }
        }

    }

    private static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = bi.createGraphics();
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(image, 0, 0, width, height, null);
        g2d.dispose();
        return bi;
    }

    /**
     * load the image
     * 
     * @param file
     */
    private void setImage(final File file) {
        // change label
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JImageBrowser.this.getFilenameTextField().setText(file.getAbsolutePath());
                JImageBrowser.this.getFilenameTextField().setEnabled(false);
                JImageBrowser.this.getPreviewLabel().setIcon(loadingIcon);
            }
        });

        // load image
        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    final BufferedImage image = ImageIO.read(file);
                    // change label
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            JImageBrowser.this.getFilenameTextField().setText(file.getAbsolutePath());
                            JImageBrowser.this.getFilenameTextField().setEnabled(true);
                            JImageBrowser.this.getPreviewLabel().setIcon(new ImageIcon(resize(image, 64, 64)));
                        }
                    });
                } catch (IOException e) {
                    // change label
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            JImageBrowser.this.getFilenameTextField().setText(file.getAbsolutePath());
                            JImageBrowser.this.getFilenameTextField().setEnabled(true);
                            JImageBrowser.this.getPreviewLabel().setIcon(invalidIcon);
                        }
                    });

                }

            }
        }).start();

    }

}

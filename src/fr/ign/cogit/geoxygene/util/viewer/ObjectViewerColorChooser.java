/*
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
 */

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * This class instanciates the GUI of the (Geo)Object Viewer Color chooser.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class ObjectViewerColorChooser extends JFrame {

  /**
	 * 
	 */
  private static final long serialVersionUID = -1288258040354502017L;

  public static final String FRAME_TITLE = "GeOxygene Color Chooser";

  public Color selectedColor;
  public JButton frameButton;

  public ObjectViewerColorChooser(Color presetColor, JButton button) {
    super(ObjectViewerColorChooser.FRAME_TITLE);

    this.selectedColor = presetColor;
    this.frameButton = button;

    // Set up the banner at the top of the window
    // final JLabel banner = new JLabel("GeOxygene",JLabel.CENTER);
    // banner.setForeground(Color.yellow);
    // banner.setOpaque(true);
    // banner.setFont(new Font("SansSerif", Font.BOLD, 24));
    // banner.setPreferredSize(new Dimension(100, 65));

    // JPanel bannerPanel = new JPanel(new BorderLayout());
    // bannerPanel.add(banner, BorderLayout.CENTER);
    // bannerPanel.setBorder(BorderFactory.createTitledBorder("Banner"));

    // Set up color chooser for setting text color
    // final JColorChooser tcc = new JColorChooser(banner.getForeground());
    final JColorChooser tcc = new JColorChooser(presetColor);
    tcc.getSelectionModel().addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        ObjectViewerColorChooser.this.selectedColor = tcc.getColor();
        // Color newColor = tcc.getColor();
        // banner.setForeground(newColor);
      }
    });
    tcc.setBorder(BorderFactory.createTitledBorder("Choose a Color"));

    // Add the control panel at the bottom of the window
    final JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER,
        20, 10));

    JButton okButton = new JButton("Ok");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        // Set the choosen color here !!!
        ObjectViewerColorChooser.this.frameButton.setIcon(new RectIcon(
            ObjectViewerColorChooser.this.selectedColor));
        ObjectViewerColorChooser.this.dispose();
      }
    });

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        ObjectViewerColorChooser.this.dispose();
      }
    });
    controlPanel.add(okButton);
    controlPanel.add(cancelButton);

    // Add the components to the colorchooser frame
    Container contentPane = this.getContentPane();
    // contentPane.add(bannerPanel, BorderLayout.NORTH);
    contentPane.add(tcc, BorderLayout.CENTER);
    contentPane.add(controlPanel, BorderLayout.SOUTH);
  }
}

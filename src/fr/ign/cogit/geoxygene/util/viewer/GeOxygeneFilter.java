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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Chooser for selecting geographic classes stemming from a mapping file, in
 * order to be displayed in the viewer.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

class GeOxygeneFilter {

  private static final String FRAME_TITLE = "GeOxygene Geographic Classes in Mapping - ";
  private String[] selectedData = new String[0];
  private String[] data;
  private JList dataList;
  private String userName;

  public GeOxygeneFilter(Object[] classesNames, String user) {
    this.userName = user;
    this.data = new String[classesNames.length];
    for (int i = 0; i < this.data.length; i++) {
      this.data[i] = (String) classesNames[i];
    }
    this.sortData();
    this.dataList = new JList(this.data);
  }

  public String[] showDialog(Frame parent) {
    final JDialog dialog = this.createDialog(parent);
    // dialog.show();
    dialog.setVisible(true);
    dialog.dispose();
    return this.selectedData;
  }

  void getSelectedValues() {
    Object[] selectedObjects = this.dataList.getSelectedValues();
    this.selectedData = new String[selectedObjects.length];
    for (int i = 0; i < selectedObjects.length; i++) {
      this.selectedData[i] = (String) selectedObjects[i];
    }
  }

  private JDialog createDialog(Frame parent) {

    String title = GeOxygeneFilter.FRAME_TITLE + this.userName;
    final JDialog dialog = new JDialog(parent, title, true);

    JScrollPane scrollPane = new JScrollPane(this.dataList);
    scrollPane.setPreferredSize(new Dimension(500, 600));

    JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

    JButton okButton = new JButton("Ok");
    okButton.setActionCommand("Ok");
    okButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GeOxygeneFilter.this.getSelectedValues();
        dialog.dispose();
      }
    });

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dialog.dispose();
      }
    });

    controlPanel.add(okButton);
    controlPanel.add(cancelButton);

    Container contentPane = dialog.getContentPane();
    contentPane.add(scrollPane, BorderLayout.CENTER);
    contentPane.add(controlPanel, BorderLayout.SOUTH);

    dialog.pack();
    dialog.setLocationRelativeTo(parent);

    return dialog;
  }

  private void sortData() {
    if (this.data.length > 1) {
      for (int i = 0; i < this.data.length - 1; i++) {
        for (int j = i + 1; j < this.data.length; j++) {
          String A = new String(this.data[i]);
          String B = new String(this.data[j]);
          if (B.compareTo(A) < 0) {
            this.data[i] = B;
            this.data[j] = A;
          }
        }
      }
    }
  }

}

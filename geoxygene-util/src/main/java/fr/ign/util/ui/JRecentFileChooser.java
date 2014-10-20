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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;

import org.apache.log4j.Logger;

/**
 * @author JeT
 * 
 */
@SuppressWarnings("rawtypes")
public class JRecentFileChooser extends JFileChooser implements
        ListSelectionListener, ActionListener {

    private static final Logger logger = Logger
            .getLogger(JRecentFileChooser.class.getName()); // logger

    private static final long serialVersionUID = -3866423372204857275L;
    private static final Color selectionBackgroundColor = new Color(0.7f, 0.7f,
            0.7f);
    private static final Color focusBackgroundColor = Color.blue;
    private static final Color backgroundColor = Color.white;
    private static final Color validDirectoryForegroundColor = Color.black;
    private static final Color invalidDirectoryForegroundColor = Color.red;
    private JList recentList = null;

    private JPanel recentPanel = null;

    /**
     * 
     */
    public JRecentFileChooser() {
        super();
        this.initializeGUI();
    }

    /**
     * @param currentDirectory
     * @param fsv
     */
    public JRecentFileChooser(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);
        this.initializeGUI();
    }

    /**
     * @param currentDirectory
     */
    public JRecentFileChooser(File currentDirectory) {
        super(currentDirectory);
        this.initializeGUI();
    }

    /**
     * @param fsv
     */
    public JRecentFileChooser(FileSystemView fsv) {
        super(fsv);
        this.initializeGUI();
    }

    /**
     * @param currentDirectoryPath
     * @param fsv
     */
    public JRecentFileChooser(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
        this.initializeGUI();
    }

    /**
     * @param currentDirectoryPath
     */
    public JRecentFileChooser(String currentDirectoryPath) {
        super(currentDirectoryPath);
        this.initializeGUI();
    }

    public JRecentFileChooser(File previousDirectory, List<String> recents) {
        super(previousDirectory);
        this.initializeGUI();
        for (String recent : recents) {
            this.addRecentDirectories(new File(recent));
        }
    }

    /**
     * @return the list of recent directories
     */
    public List<String> getRecentDirectories() {
        ArrayList<String> recents = new ArrayList<String>();
        for (int n = 0; n < this.recentList.getModel().getSize(); n++) {
            recents.add(((File) this.recentList.getModel().getElementAt(n))
                    .getAbsolutePath());
        }
        return recents;
    }

    /**
     * set the list of recent directories
     */
    public void setRecentDirectories(List<File> directories) {
        DefaultListModel lm = new DefaultListModel();
        ArrayList<File> recents = new ArrayList<File>();
        for (int n = 0; n < directories.size(); n++) {
            lm.addElement(directories.get(n));
        }
        this.recentList.setModel(lm);
    }

    /**
     * add a directory to the list of recent directories
     */
    public void addRecentDirectories(File directory) {
        DefaultListModel lm = new DefaultListModel();
        List<String> recents = this.getRecentDirectories();
        recents.add(directory.getAbsolutePath());
        for (int n = 0; n < recents.size(); n++) {
            File elem = new File(recents.get(n));
            if (!lm.contains(elem)) {
                lm.addElement(elem);
            }
        }
        this.recentList.setModel(lm);
    }

    /**
     * clear recent directories list
     */
    public void clearRecentDirectories() {
        DefaultListModel lm = new DefaultListModel();
        this.recentList.setModel(lm);
    }

    private void initializeGUI() {
        this.recentPanel = new JPanel(new BorderLayout());
        // this.recentPanel.setBorder(BorderFactory
        // .createBevelBorder(BevelBorder.LOWERED));
        this.recentList = new JList();
        this.recentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.recentList.setCellRenderer(new CellRendererFile());
        this.recentPanel.add(new JScrollPane(this.recentList),
                BorderLayout.CENTER);
        this.recentPanel.add(new JLabel("previous"), BorderLayout.NORTH);
        this.setAccessory(this.recentPanel);
        this.recentList.addListSelectionListener(this);
        this.addActionListener(this);
        this.recentPanel.setPreferredSize(new Dimension(200, 0));
    }

    private static class CellRendererFile implements ListCellRenderer {

        private final JLabel label = new JLabel();

        /**
         * Constructor
         */
        public CellRendererFile() {
            super();
            this.label.setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            File file = (File) value;
            if (file == null) {
                this.label.setText("null");
                return this.label;
            }
            this.label.setText(file.getName());
            this.label.setToolTipText(file.getAbsolutePath());
            if (file.isDirectory()) {
                this.label.setForeground(validDirectoryForegroundColor);
            } else {
                this.label.setForeground(invalidDirectoryForegroundColor);
            }
            if (cellHasFocus) {
                this.label.setBackground(focusBackgroundColor);
            } else {
                this.label.setBackground(backgroundColor);
            }
            if (isSelected) {
                this.label.setBackground(selectionBackgroundColor);
            }
            return this.label;
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        File selectedRecent = (File) this.recentList.getSelectedValue();
        this.setCurrentDirectory(selectedRecent);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("ApproveSelection")) {
            File selectedFile = this.getSelectedFile();
            if (selectedFile == null) {
                return;
            }
            File parentFile = selectedFile.getParentFile();
            if (parentFile == null) {
                return;
            }
            this.addRecentDirectories(parentFile);
        }
    }
}

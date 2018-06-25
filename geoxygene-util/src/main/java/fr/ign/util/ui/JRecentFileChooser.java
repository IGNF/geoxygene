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

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author JeT
 * 
 */
@SuppressWarnings("rawtypes")
public class JRecentFileChooser extends JFileChooser implements
        ListSelectionListener, ActionListener {

//    private static final Logger logger = Logger
//            .getLogger(JRecentFileChooser.class.getName()); // logger

    private static final long serialVersionUID = -3866423372204857275L;
    private static final Color selectionBackgroundColor = new Color(0.7f, 0.7f,
            0.7f);
    private static final Color focusBackgroundColor = Color.blue;
    private static final Color backgroundColor = Color.white;
    private static final Color validDirectoryForegroundColor = Color.black;
    private static final Color invalidDirectoryForegroundColor = Color.red;
    private JList<File> recentList;
    private DefaultListModel<File> lm; // mutable list model for JList

    /**
     * 
     */
    public JRecentFileChooser() {
        super();
        this.initializeGUI();
    }

    /**
     * @param currentDirectory current
     * @param fsv view
     */
    public JRecentFileChooser(File currentDirectory, FileSystemView fsv) {
        super(currentDirectory, fsv);
        this.initializeGUI();
    }

    /**
     * @param currentDirectory current
     */
    public JRecentFileChooser(File currentDirectory) {
        super(currentDirectory);
        this.initializeGUI();
    }

    /**
     * @param fsv view
     */
    public JRecentFileChooser(FileSystemView fsv) {
        super(fsv);
        this.initializeGUI();
    }

    /**
     * @param currentDirectoryPath current
     * @param fsv view
     */
    public JRecentFileChooser(String currentDirectoryPath, FileSystemView fsv) {
        super(currentDirectoryPath, fsv);
        this.initializeGUI();
    }

    /**
     * @param currentDirectoryPath current
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
        List<String> recents = new ArrayList<>();
        for (int n = 0; n < this.lm.getSize(); n++) recents.add(this.lm.getElementAt(n).getAbsolutePath());
        return recents;
    }

    /**
     * set the list of recent directories
     */
    public void setRecentDirectories(List<File> directories) {
        this.lm.removeAllElements();
        for (File directory : directories) this.lm.addElement(directory);
    }

    /**
     * add a directory to the list of recent directories
     */
    public void addRecentDirectories(File directory) {
        if (!this.lm.contains(directory)) this.lm.addElement(directory);
    }

    /**
     * clear recent directories list
     */
    public void clearRecentDirectories() {
        this.lm.removeAllElements();
    }

    private void initializeGUI() {
        JPanel recentPanel = new JPanel(new BorderLayout());
        // this.recentPanel.setBorder(BorderFactory
        // .createBevelBorder(BevelBorder.LOWERED));
        this.lm = new DefaultListModel<>();
        this.recentList = new JList<>(this.lm);
        this.recentList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.recentList.setCellRenderer(new CellRendererFile());
        this.recentList.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                // everything is done in keyPressed
            }

            @Override
            public void keyReleased(KeyEvent e) {
                // everything is done in keyPressed
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    JRecentFileChooser.this.lm
                            .removeElementAt(JRecentFileChooser.this.recentList
                                    .getSelectedIndex());
                }

            }
        });
        recentPanel.add(new JScrollPane(this.recentList),
                BorderLayout.CENTER);
        recentPanel.add(new JLabel("previous"), BorderLayout.NORTH);
        this.setAccessory(recentPanel);
        this.recentList.addListSelectionListener(this);
        this.addActionListener(this);
        recentPanel.setPreferredSize(new Dimension(200, 0));
    }

    private static class CellRendererFile implements ListCellRenderer<File> {

        private final JLabel label = new JLabel();

        /**
         * Constructor
         */
        public CellRendererFile() {
            super();
            this.label.setOpaque(true);
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends File> list, File file,
                int index, boolean isSelected, boolean cellHasFocus) {
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
        File selectedRecent = this.recentList.getSelectedValue();
        this.setCurrentDirectory(selectedRecent);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
            File selectedFile = this.getSelectedFile();
            if (selectedFile == null) {
                File[] selectedFiles = this.getSelectedFiles();
                if (selectedFiles == null || selectedFiles.length <= 0) {
                    return;
                }
                selectedFile = selectedFiles[0];
            }
            File file = selectedFile;
            if (!file.isDirectory()) {
                file = file.getParentFile();
            }
            if (file == null) {
                return;
            }
            this.addRecentDirectories(file);
        }
    }
}

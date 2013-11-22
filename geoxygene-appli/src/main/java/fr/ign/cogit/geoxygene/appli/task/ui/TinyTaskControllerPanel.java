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

package fr.ign.cogit.geoxygene.appli.task.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;

/**
 * @author JeT
 *         small panel with a progress bar, the name and 3 button play, pause,
 *         stop
 *         it is linked to a task and update it's GUI dynamically
 */
public class TinyTaskControllerPanel implements TaskControllerPanel, ActionListener, TaskListener {

    private JPanel mainPanel = null;
    private Task task = null;
    private JLabel taskNameLabel = null;
    private JButton playButton = null;
    private JButton pauseButton = null;
    private JButton stopButton = null;
    private JProgressBar progressBar = null;
    private static final String playIconFilename = "/images/icons/Play.png";
    private static final String pauseIconFilename = "/images/icons/Pause.png";
    private static final String stopIconFilename = "/images/icons/Stop.png";
    private static final ImageIcon playIcon = new ImageIcon(TinyTaskControllerPanel.class.getResource(playIconFilename));
    private static final ImageIcon pauseIcon = new ImageIcon(TinyTaskControllerPanel.class.getResource(pauseIconFilename));
    private static final ImageIcon stopIcon = new ImageIcon(TinyTaskControllerPanel.class.getResource(stopIconFilename));

    /**
     * @param task
     */
    public TinyTaskControllerPanel(Task task) {
        super();
        this.task = task;
        this.task.addTaskListener(this);
    }

    /**
     * @return the mainPanel
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel(new BorderLayout());

            JPanel northPanel = new JPanel(new BorderLayout());
            northPanel.add(this.getTaskNamelabel(), BorderLayout.CENTER);

            JPanel controllerPanel = new JPanel(new GridLayout(1, 0));
            northPanel.add(controllerPanel);
            controllerPanel.add(this.getPlayButton());
            controllerPanel.add(this.getPauseButton());
            controllerPanel.add(this.getStopButton());

            this.mainPanel.add(northPanel, BorderLayout.EAST);
            this.mainPanel.add(this.getProgressBar(), BorderLayout.CENTER);

        }
        return this.mainPanel;
    }

    private JButton getPlayButton() {
        if (this.playButton == null) {
            this.playButton = new JButton("");
            this.playButton.setToolTipText("restart task");
            this.playButton.setIcon(playIcon);
            this.playButton.addActionListener(this);
            this.playButton.setBorder(BorderFactory.createEmptyBorder());
        }
        return this.playButton;
    }

    private JButton getPauseButton() {
        if (this.pauseButton == null) {
            this.pauseButton = new JButton("");
            this.pauseButton.setToolTipText("pause task");
            this.pauseButton.setIcon(pauseIcon);
            this.pauseButton.addActionListener(this);
            this.pauseButton.setBorder(BorderFactory.createEmptyBorder());
        }
        return this.pauseButton;
    }

    private JButton getStopButton() {
        if (this.stopButton == null) {
            this.stopButton = new JButton("");
            this.stopButton.setToolTipText("stop task");
            this.stopButton.setIcon(stopIcon);
            this.stopButton.addActionListener(this);
            this.stopButton.setBorder(BorderFactory.createEmptyBorder());
        }
        return this.stopButton;
    }

    private JLabel getTaskNamelabel() {
        if (this.taskNameLabel == null) {
            this.taskNameLabel = new JLabel(this.getTask().getName());
        }
        return this.taskNameLabel;
    }

    private JProgressBar getProgressBar() {
        if (this.progressBar == null) {
            this.progressBar = new JProgressBar();
            this.progressBar.setMaximum(100);
            this.progressBar.setValue(0);
            this.progressBar.setIndeterminate(!this.getTask().isProgressable());
            this.progressBar.setPreferredSize(new Dimension(100, 20));
            this.progressBar.setStringPainted(true);
            this.progressBar.setString(this.getTask().getName());
        }
        return this.progressBar;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.ui.TaskControllerPanel#getGui()
     */
    @Override
    public JComponent getGui() {
        return this.getMainPanel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.ui.TaskControllerPanel#getTask()
     */
    @Override
    public Task getTask() {
        return this.task;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == this.getPlayButton()) {
            this.getTask().requestPlay();
        }
        if (e.getSource() == this.getPauseButton()) {
            this.getTask().requestPause();
        }
        if (e.getSource() == this.getStopButton()) {
            this.getTask().requestStop();
        }

    }

    @Override
    public void onStateChange(Task task, TaskState oldState) {
        this.updateController();
    }

    @Override
    synchronized public void updateController() {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                // update progress bar
                TinyTaskControllerPanel.this.getProgressBar().setValue((int) (TinyTaskControllerPanel.this.getTask().getProgress() * 100));
                // update button depending on current state
                switch (TinyTaskControllerPanel.this.getTask().getState()) {
                case WAITING:
                    //            this.getProgressBar().setEnabled(false);
                    TinyTaskControllerPanel.this.getStopButton().setEnabled(false);
                    TinyTaskControllerPanel.this.getPauseButton().setEnabled(false);
                    TinyTaskControllerPanel.this.getPlayButton().setEnabled(true);
                    break;
                case INITIALIZING:
                case FINALIZING:
                case RUNNING:
                    //            this.getProgressBar().setEnabled(true);
                    TinyTaskControllerPanel.this.getStopButton().setEnabled(true && TinyTaskControllerPanel.this.task.isStopable());
                    TinyTaskControllerPanel.this.getPauseButton().setEnabled(true && TinyTaskControllerPanel.this.task.isPausable());
                    TinyTaskControllerPanel.this.getPlayButton().setEnabled(false);
                    break;
                case PAUSED:
                    //            this.getProgressBar().setEnabled(true);
                    TinyTaskControllerPanel.this.getStopButton().setEnabled(true && TinyTaskControllerPanel.this.task.isStopable());
                    TinyTaskControllerPanel.this.getPauseButton().setEnabled(false);
                    TinyTaskControllerPanel.this.getPlayButton().setEnabled(true);
                    break;
                case FINISHED:
                case ERROR:
                case STOPPED:
                    //            this.getProgressBarBar().setEnabled(false);
                    TinyTaskControllerPanel.this.getStopButton().setEnabled(false);
                    TinyTaskControllerPanel.this.getPauseButton().setEnabled(false);
                    TinyTaskControllerPanel.this.getPlayButton().setEnabled(false);
                    break;
                default:
                    break;

                }

            }
        });

    }
}

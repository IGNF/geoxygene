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
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.appli.task.TaskManager;
import fr.ign.cogit.geoxygene.appli.task.TaskManagerListener;

/**
 * @author JeT when tasks are running, this component fetches progress
 *         iteratively and updates TaskControllers (to show their status and
 *         progress)
 */
public class TaskManagerPopup implements TaskManagerListener, ItemListener,
        ComponentListener {

    private static final Logger logger = Logger
            .getLogger(TaskManagerPopup.class.getName()); // logger
    private static final int PROGRESS_TIMER_INTERVAL = 500;
    private static final Dimension EpandButtonDimension = new Dimension(20, 20);
    private JPanel expandedPanel = null;
    private JDialog expandedDialog = null;
    private JPanel summaryPanel = null;
    private JProgressBar summaryProgress = null;
    private JPanel controllerPanel = null;
    private JToggleButton expandButton = null;
    private TaskManager taskManager = null;
    private final Map<Task, TaskControllerPanel> taskControllers = new HashMap<Task, TaskControllerPanel>();
    private final Set<Task> prefinishedTasks = new HashSet<Task>(); // prefinishedTasks
                                                                    // are those
                                                                    // very
                                                                    // shorts
                                                                    // tasks
                                                                    // finished
                                                                    // before
                                                                    // their GUI
                                                                    // are
                                                                    // displayed
    private Timer progressTimer = null;
    private final Object progressTimerLock = new Object();
    private static final String expandIconFilename = "/images/icons/16x16/up.png";
    private static final String collapseIconFilename = "/images/icons/16x16/down.png";
    private static final ImageIcon expandIcon = new ImageIcon(
            TinyTaskControllerPanel.class.getResource(expandIconFilename));
    private static final ImageIcon collapseIcon = new ImageIcon(
            TinyTaskControllerPanel.class.getResource(collapseIconFilename));

    /**
     * @param taskManager
     */
    public TaskManagerPopup(TaskManager taskManager) {
        super();
        this.setTaskManager(taskManager);
    }

    /**
     * @return a newly timer task that launches update method repeatedly
     */
    private TimerTask createProgressTimerTask() {
        return new TimerTask() {

            @Override
            public void run() {
                boolean isProgressable = false;
                double minProgress = 1;
                synchronized (TaskManagerPopup.this.taskControllers) {
                    for (TaskControllerPanel controller : TaskManagerPopup.this.taskControllers
                            .values()) {
                        controller.updateController();
                    }
                    for (Task task : TaskManagerPopup.this.taskControllers
                            .keySet()) {
                        if (task.isProgressable()) {
                            isProgressable = true;
                            double progress = task.getProgress();
                            if (progress < minProgress) {
                                minProgress = progress;
                            }
                        }

                    }
                }
                if (isProgressable) {
                    TaskManagerPopup.this.summaryProgress
                            .setIndeterminate(false);
                    TaskManagerPopup.this.summaryProgress
                            .setValue((int) (minProgress * 100.));
                } else {
                    try {
                        TaskManagerPopup.this.summaryProgress
                                .setIndeterminate(true);
                    } catch (java.lang.ArithmeticException e) {
                        // There is a bug in Nimbus PLAF when setting
                        // progressBar to indeterminate
                    }
                }
            }

        };
    }

    /**
     * @return the taskManager
     */
    public TaskManager getTaskManager() {
        return this.taskManager;
    }

    /**
     * @param taskManager
     *            the taskManager to set
     */
    public void setTaskManager(TaskManager taskManager) {
        if (this.taskManager != null) {
            this.taskManager.removeTaskManagerListener(this);
        }
        this.taskManager = taskManager;
        if (this.taskManager != null) {
            this.taskManager.addTaskManagerListener(this);
        }
    }

    /**
     * @return the mainPanel
     */
    private JPanel getExpandedPanel() {
        if (this.expandedPanel == null) {
            this.expandedPanel = new JPanel(new BorderLayout());
            this.expandedPanel.add(new JScrollPane(this.getControllerPanel()),
                    BorderLayout.CENTER);
        }
        return this.expandedPanel;
    }

    /**
     * expanded dialog is used to keep the expanded panel on top of all other
     * windows
     * 
     * @return the expanded dialog containing the expanded panel
     */
    private JDialog getExpandedDialog() {
        if (this.expandedDialog == null) {
            this.expandedDialog = new JDialog(
                    SwingUtilities.getWindowAncestor(this.getGui()),
                    "expanded task visualizer");
            this.expandedDialog.add(this.getExpandedPanel());
            this.expandedDialog.setModalityType(ModalityType.MODELESS);
            this.expandedDialog.setUndecorated(true);
        }
        return this.expandedDialog;
    }

    /**
     * display the expanded panel on top of all windows
     */
    public void displayExpandedPanel() {
        if (this.taskControllers.size() <= 0) {
            return;
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TaskManagerPopup.this.getExpandButton().setIcon(collapseIcon);
                TaskManagerPopup.this.getExpandedDialog().setVisible(true);
                TaskManagerPopup.this.relocate();
            }
        });
    }

    /**
     * hide the expanded panel
     */
    public void hideExpandedPanel() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TaskManagerPopup.this.getExpandButton().setIcon(expandIcon);
                TaskManagerPopup.this.getExpandedDialog().setVisible(false);
            }
        });
    }

    /**
     * compute expanded dialog position to be exactly above the summary panel
     */
    private void relocate() {
        if (!this.getExpandedDialog().isShowing()
                || !this.getSummaryPanel().isShowing()) {
            return;
        }

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TaskManagerPopup.this.getExpandedDialog().pack();
                Dimension expandedDialogSize = TaskManagerPopup.this
                        .getExpandedDialog().getSize();
                expandedDialogSize.width = TaskManagerPopup.this
                        .getSummaryPanel().getWidth();
                // limit dialog height
                if (expandedDialogSize.height > Toolkit.getDefaultToolkit()
                        .getScreenSize().getHeight() / 2) {
                    expandedDialogSize.height = (int) Toolkit
                            .getDefaultToolkit().getScreenSize().getHeight() / 2;
                }
                TaskManagerPopup.this.getExpandedDialog().setSize(
                        expandedDialogSize);
                Point location = TaskManagerPopup.this.getSummaryPanel()
                        .getLocationOnScreen();
                if (location == null) {
                    return;
                }
                TaskManagerPopup.this.getExpandedDialog().setLocation(
                        location.x,
                        location.y
                                - TaskManagerPopup.this.getExpandedDialog()
                                        .getHeight());
            }
        });
    }

    /**
     * @return the summaryPanel
     */
    private JPanel getSummaryPanel() {
        if (this.summaryPanel == null) {
            this.summaryPanel = new JPanel(new BorderLayout());
            this.summaryPanel.add(this.getSummaryProgress(),
                    BorderLayout.CENTER);
            this.summaryPanel.add(this.getExpandButton(), BorderLayout.EAST);
            this.summaryPanel.addComponentListener(this);
        }
        return this.summaryPanel;
    }

    /**
     * @return the summary progress bar
     */
    private JProgressBar getSummaryProgress() {
        if (this.summaryProgress == null) {
            this.summaryProgress = new JProgressBar();
            this.summaryProgress.setValue(0);
            this.summaryProgress.setMinimum(0);
            this.summaryProgress.setMaximum(100);
            this.summaryProgress.setStringPainted(true);
            this.summaryProgress.setIndeterminate(true);
            this.summaryProgress.setBorderPainted(false);
            // bugfix in Motif LnF
            UIManager.put("ProgressBar.repaintInterval", new Integer(250));
            UIManager.put("ProgressBar.cycleTime", new Integer(5000));
        }
        return this.summaryProgress;
    }

    /**
     * @return the getControllerPanel
     */
    private JPanel getControllerPanel() {
        if (this.controllerPanel == null) {
            this.controllerPanel = new JPanel(new GridLayout(0, 1));
        }
        return this.controllerPanel;
    }

    /**
     * @return the expand button
     */
    private JToggleButton getExpandButton() {
        if (this.expandButton == null) {
            this.expandButton = new JToggleButton("");
            this.expandButton.setBorder(BorderFactory.createEmptyBorder());
            this.expandButton.setFont(this.expandButton.getFont().deriveFont(
                    Font.BOLD));
            this.expandButton.addItemListener(this);
            this.expandButton.setSize(EpandButtonDimension);
            this.hideExpandedPanel();
        }
        return this.expandButton;
    }

    /**
     * @return the main graphic component
     */
    public JComponent getGui() {
        return this.getSummaryPanel();
    }

    /**
     * Update the message displayed in the summary progress bar
     */
    private void updateTaskCount() {
        final int pendingTaskCount = this.getTaskManager()
                .getPendingTaskCount();
        final int runningTaskCount = this.getTaskManager()
                .getRunningTaskCount();
        final String runningMsg = (runningTaskCount == 0 ? "no task"
                : (runningTaskCount == 1 ? "a task is" : runningTaskCount
                        + " tasks "))
                + " running";
        final String pendingMsg = (pendingTaskCount == 0 ? "no task"
                : (pendingTaskCount == 1 ? "a task is" : pendingTaskCount
                        + " tasks "))
                + " pending";
        if (runningTaskCount == 0) {
            synchronized (this.taskControllers) {
                TaskManagerPopup.this.taskControllers.clear();
            }
            TaskManagerPopup.this.getControllerPanel().removeAll();
            TaskManagerPopup.this.getSummaryProgress().setIndeterminate(false);
            TaskManagerPopup.this.getSummaryProgress().setValue(0);
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                TaskManagerPopup.this.getSummaryProgress().setString(
                        runningMsg + " / " + pendingMsg);
                TaskManagerPopup.this.getSummaryProgress().setEnabled(
                        (runningTaskCount != 0));
            }
        });

    }

    @Override
    public void onTaskAdded(Task task) {
        this.updateTaskCount();
    }

    @Override
    public void onTaskStarted(final Task task) {
        final TinyTaskControllerPanel taskControllerPanel = new TinyTaskControllerPanel(
                task);
        int taskControllerCount = 0;
        // System.err.println("Task Manager Popup receive 'task started' " +
        // task);
        synchronized (this.taskControllers) {
            TaskControllerPanel previousPanel = this.taskControllers.put(task,
                    taskControllerPanel);
            if (previousPanel != null) {
                logger.error("A task controller panel already exists for task "
                        + task);
            }
            taskControllerCount = this.taskControllers.size();
        }
        // System.err.println("START TaskControllerPanel is created for " + task
        // + " => " + this.taskControllers.size());
        if (!task.getState().isFinished()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JComponent gui;
                    synchronized (TaskManagerPopup.this.prefinishedTasks) {
                        if (!TaskManagerPopup.this.prefinishedTasks
                                .contains(task)) {
                            gui = taskControllerPanel.getGui();
                            TaskManagerPopup.this.getControllerPanel().add(gui);
                            // System.err
                            // .println("GUI ADD !!!!!!!!!! Task Manager Popup added task from GUI "
                            // + gui.hashCode() + " task " + task);
                        }
                    }
                }
            });
        }
        if (taskControllerCount > 0 && this.progressTimer == null) {
            // start the timer
            synchronized (this.progressTimerLock) {
                // System.err.println("Create a new progress timer");
                this.progressTimer = new Timer();
                this.progressTimer.scheduleAtFixedRate(
                        this.createProgressTimerTask(), 0,
                        PROGRESS_TIMER_INTERVAL);
                this.displayExpandedPanel();
            }
        }
        this.updateTaskCount();
        this.relocate();
    }

    @Override
    public void onTaskRemoved(final Task task) {
        int taskControllerCount = 0;
        TaskControllerPanel taskControllerPanel = null;
        synchronized (this.taskControllers) {
            taskControllerPanel = this.taskControllers.remove(task);
            taskControllerCount = this.taskControllers.size();
            // System.err.println("Task Manager Popup removed task from controller "
            // + task + " controller size = " + this.taskControllers.size() +
            // " value = " + taskControllerPanel);
        }
        // System.err.println("STOP " + task + " => " +
        // this.taskControllers.size());
        if (taskControllerPanel == null) {
            synchronized (this.prefinishedTasks) {
                this.prefinishedTasks.add(task);
            }
        } else {
            final JComponent gui = taskControllerPanel.getGui();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    TaskManagerPopup.this.getControllerPanel().remove(gui);
                    // System.err.println("GUI REMOVE !!!!!!!!!! Task Manager Popup removed task from GUI "
                    // + gui.hashCode() + " task " + task);
                }
            });
        }
        if (taskControllerCount == 0) {
            this.stopTimer();
            this.hideExpandedPanel();
        }
        this.updateTaskCount();
        this.relocate();
    }

    /**
     * Stop progress timer if in use
     */
    public void stopTimer() {
        // System.err.println("Stop timer " + this.progressTimer);
        if (this.progressTimer == null) {
            return;
        }
        synchronized (this.progressTimerLock) {
            if (this.progressTimer != null) {
                this.progressTimer.cancel();
                this.progressTimer.purge();
                this.progressTimer = null;
            }
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == this.getExpandButton()) {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                this.displayExpandedPanel();
            }
            if (e.getStateChange() == ItemEvent.DESELECTED) {
                this.hideExpandedPanel();
            }
        }
    }

    @Override
    public void componentResized(ComponentEvent e) {
        // nothing to do

    }

    @Override
    public void componentMoved(ComponentEvent e) {
        if (e.getSource() == this.getSummaryPanel()) {
            this.relocate();
        }
    }

    @Override
    public void componentShown(ComponentEvent e) {
        // nothing to do
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        // nothing to do
    }

    /**
     * terminate task management and dispose GUI
     */
    public void close() {
        this.stopTimer();
        this.getExpandedDialog().dispose();
    }

}

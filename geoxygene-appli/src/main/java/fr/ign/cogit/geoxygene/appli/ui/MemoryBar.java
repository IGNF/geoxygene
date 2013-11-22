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

package fr.ign.cogit.geoxygene.appli.ui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * @author JeT
 *         display the memory usage by fetching memory usage iteratively
 */
public class MemoryBar implements MouseListener {
    private long timerInterval = 2000; // in milliseconds
    protected static final long MB = 1024l * 1024l;
    private JPanel mainPanel = null;
    private JProgressBar memoryProgressBar = null;
    private final Object memoryProgressBarLock = new Object();
    private Timer memoryTimer = null;

    /**
     * Constructor
     */
    public MemoryBar() {
        super();
        this.start();
    }

    /**
     * create a new Timer Task for memory
     */
    private TimerTask createMemoryTimerTask() {
        return new TimerTask() {

            @Override
            public void run() {
                Runtime runtime = Runtime.getRuntime();
                int totalMemory = (int) (runtime.totalMemory() / MB);
                int maxMemory = (int) (runtime.maxMemory() / MB);
                int freeMemory = (int) (runtime.freeMemory() / MB);
                MemoryBar.this.getMemoryProgressBar().setMinimum(0);
                MemoryBar.this.getMemoryProgressBar().setMaximum(totalMemory);
                MemoryBar.this.getMemoryProgressBar().setValue(totalMemory - freeMemory);
                //                System.err.println("-------------------------------------------------- Set String to " + String.valueOf(totalMemory - freeMemory) + "Mb / "
                //                        + String.valueOf(totalMemory) + "Mb");
                MemoryBar.this.getMemoryProgressBar().setString(String.valueOf(totalMemory - freeMemory) + "Mb / " + String.valueOf(totalMemory) + "Mb");
                MemoryBar.this.getMemoryProgressBar().setToolTipText(
                        "<html>total  = <b>" + String.valueOf(totalMemory) + "Mb</b><br>" + "max   = <b>" + String.valueOf(maxMemory) + "Mb</b><br>"
                                + "free  = <b>" + String.valueOf(freeMemory) + "Mb</b></html>");
            }
        };
    }

    /**
     * @return the timerInterval
     */
    public long getTimerInterval() {
        return this.timerInterval;
    }

    /**
     * @param timerInterval
     *            the timerInterval to set
     */
    public void setTimerInterval(long timerInterval) {
        this.stop();
        this.timerInterval = timerInterval;
        this.start();
    }

    /**
     * get message console gui
     */
    public JComponent getGui() {
        return this.getMainPanel();
    }

    /**
     * Main panel containing the last message label
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel(new BorderLayout());
            this.mainPanel.add(this.getMemoryProgressBar(), BorderLayout.CENTER);
            this.mainPanel.addMouseListener(this);
        }
        return this.mainPanel;
    }

    private JProgressBar getMemoryProgressBar() {
        synchronized (this.memoryProgressBarLock) {
            if (this.memoryProgressBar == null) {
                this.memoryProgressBar = new JProgressBar();
                this.memoryProgressBar.setOrientation(JProgressBar.HORIZONTAL);
                this.memoryProgressBar.setStringPainted(true);
            }
            return this.memoryProgressBar;
        }
    }

    /**
     * terminate the timer loop
     */
    public void stop() {
        if (this.memoryTimer != null) {
            this.memoryTimer.cancel();
        }
    }

    /**
     * start the timer loop
     */
    public void start() {
        this.stop();
        this.memoryTimer = new Timer();
        this.memoryTimer.scheduleAtFixedRate(this.createMemoryTimerTask(), 0, this.timerInterval);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        this.start();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // nothing to do
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // nothing to do
    }

}

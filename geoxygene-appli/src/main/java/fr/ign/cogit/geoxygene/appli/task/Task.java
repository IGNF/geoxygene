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

package fr.ign.cogit.geoxygene.appli.task;

/**
 * @author JeT
 *         A task is an asynchronous process which has some properties:
 *         - a name
 *         - its progress value can be followed between 0 and 1
 *         - task can be paused or stopped
 */
public interface Task extends Runnable {
    /**
     * get the task name
     */
    public String getName();

    /**
     * start the task
     * 
     * @return true if the task is correctly launched
     */
    public Thread start();

    /**
     * @return the task progress between 0 & 1
     */
    public double getProgress();

    /**
     * @return the task state
     */
    public TaskState getState();

    /**
     * request the task to pause
     */
    public void requestPause();

    /**
     * request the task to play
     */
    public void requestPlay();

    /**
     * request the task to stop
     */
    public void requestStop();

    /**
     * add a taskListener
     */
    void addTaskListener(final TaskListener listener);

    /**
     * remove a taskListener
     */
    void removeTaskListener(final TaskListener listener);

    /**
     * remove all taskListener
     */
    void removeAllTaskListener();

    /**
     * @return true if the progress value is filled during the task progress
     */
    boolean isProgressable();

    /**
     * @return true if the pauseRequest can be called or not
     */
    boolean isPausable();

    /**
     * @return true if the stopRequest can be called or not
     */
    boolean isStoppable();

    /**
     * @return the exception that has been thrown if Task is in error state
     */
    public Exception getError();

    /**
     * @return the thread associated with this task. null if not started
     */
    public Thread getThread();
}

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

import java.util.HashSet;
import java.util.Set;

/**
 * @author JeT
 * 
 */
public abstract class AbstractTask implements Task {

    private static final TaskListener[] DUMMYTASKLISTENERARRAY = new TaskListener[] {};
    private double progress = 0.;
    private final Set<TaskListener> listeners = new HashSet<TaskListener>();
    private TaskState state = TaskState.WAITING;
    private boolean pauseRequested = false;
    private boolean stopRequested = false;
    private String name = null;
    private Thread taskThread = null;
    private Exception error = null;

    /**
     * Constructor
     * 
     * @param name
     *            task name
     */
    public AbstractTask(String name) {
        super();
        this.name = name;
    }

    /**
     * @return the name
     */
    @Override
    public final String getName() {
        return this.name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#start()
     */
    @Override
    public Thread start() {
        if (this.taskThread != null) {
            return null;
        }
        this.taskThread = new Thread(this, this.getName() + "-thread");
        this.taskThread.start();
        return this.taskThread;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#start()
     */
    @Override
    public Thread start(Thread.UncaughtExceptionHandler uncaughtExceptionHandler) {
        if (this.taskThread != null) {
            return this.taskThread;
        }
        this.taskThread = new Thread(this, this.getName() + "-thread");
        this.taskThread.setUncaughtExceptionHandler(uncaughtExceptionHandler);
        this.taskThread.start();
        return this.taskThread;
    }

    /**
     * @return the thread associated with this task
     */
    @Override
    public Thread getThread() {
        return this.taskThread;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#getProgress()
     */
    @Override
    public double getProgress() {
        return this.progress;
    }

    /**
     * progress value range = 0 .. 1
     * 
     * @param progress
     *            the progress to set
     */
    public final void setProgress(double progress) {
        this.progress = progress;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#getState()
     */
    @Override
    public TaskState getState() {
        return this.state;
    }

    /**
     * @param state
     *            the state to set
     */
    public final void setState(TaskState state) {
        TaskState oldState = null;
        synchronized (this.state) {
            oldState = this.state;
            if (oldState == state) {
                return;
            }
            this.state = state;
        }
        this.fireStateChanged(oldState);
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#requestPause()
     */
    @Override
    public void requestPause() {
        this.pauseRequested = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#requestStop()
     */
    @Override
    public void requestStop() {
        this.stopRequested = true;
    }

    /**
     * @return the pauseRequested
     */
    public boolean isPauseRequested() {
        return this.pauseRequested;
    }

    /**
     * @return the stopRequested
     */
    public boolean isStopRequested() {
        return this.stopRequested;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#requestPlay()
     */
    @Override
    public void requestPlay() {
        if (this.isPauseRequested()) {
            this.pauseRequested = false;
        } else if (this.getState() == TaskState.WAITING) {
            this.start();
        }
    }

    /**
     * @return the error
     */
    @Override
    public Exception getError() {
        return this.error;
    }

    /**
     * @param error
     *            the error to set
     */
    public void setError(Exception error) {
        this.error = error;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.task.Task#addTaskListener(fr.ign.cogit.geoxygene
     * .appli.task.TaskListener)
     */
    @Override
    public void addTaskListener(TaskListener listener) {
        synchronized (this.listeners) {
            this.listeners.add(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.task.Task#removeTaskListener(fr.ign.cogit
     * .geoxygene.appli.task.TaskListener)
     */
    @Override
    public void removeTaskListener(TaskListener listener) {
        synchronized (this.listeners) {
            this.listeners.remove(listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#removeAllTaskListener()
     */
    @Override
    public void removeAllTaskListener() {
        synchronized (this.listeners) {
            this.listeners.clear();
        }
    }

    @Override
    public int getTaskListenerCount() {
        synchronized (this.listeners) {
            return this.listeners.size();
        }
    }

    /**
     * fire an event 'state changed'
     * 
     * @param oldState
     *            old task state
     */
    @SuppressWarnings("unchecked")
    public void fireStateChanged(final TaskState oldState) {
        final TaskListener<Task>[] listeners;
        synchronized (this.listeners) {
            listeners = this.listeners.toArray(DUMMYTASKLISTENERARRAY);
        }
        for (TaskListener<Task> listener : listeners) {
            listener.onStateChange(this, oldState);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " [" + this.name + "]";
    }

}

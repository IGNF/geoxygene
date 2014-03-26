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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author JeT
 *         manage a set of tasks
 */
public class TaskManager implements TaskListener {

    private static final Logger logger = Logger.getLogger(TaskManager.class.getName()); // logger

    private static final TaskManagerListener[] DUMMYTASKMANAGERLISTENERARRAY = new TaskManagerListener[] {};
    private final Set<Task> tasks = new HashSet<Task>();
    private final Map<Thread, Task> associatedTasks = new HashMap<Thread, Task>();
    private final Set<TaskManagerListener> listeners = new HashSet<TaskManagerListener>();
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;

    /**
     * Constructor
     */
    public TaskManager() {
        this.uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread th, Throwable ex) {
                Task task = TaskManager.this.associatedTasks.get(th);
                if (task == null) {
                    logger.error("Impossible case. Exception caught from a non referenced thread " + th.getName());
                    ex.printStackTrace();
                    return;
                }
                logger.error("Uncaught exception from task " + task.getName() + ": " + ex);
                TaskManager.this.removeTask(task);
                ex.printStackTrace();
            }
        };
    }

    /**
     * add a new Managed task. duplicates are discarded
     * 
     * @return true if task is correctly added
     */
    public boolean addTask(final Task task) {
        if (task == null) {
            return false;
        }
        // check that this task is not already managed
        if (this.tasks.contains(task)) {
            return false;
        }
        // check if the task has already been launched
        if (task.getState() != TaskState.WAITING) {
            logger.warn("task " + task.getName() + " added to task manager with state " + task.getState());
        }
        this.tasks.add(task);
        task.addTaskListener(this);
        this.fireTaskAdded(task);
        Thread thread = task.start();
        if (thread != null) {
            this.associatedTasks.put(thread, task);
        } else {
            logger.error("Task Manager cannot start task " + task.getName());
            this.removeTask(task);
            return false;
        }
        //        System.err.println("[TaskManager] add and start task " + task.getName());
        return true;
    }

    /**
     * remove a task from managed tasks
     */
    public boolean removeTask(final Task task) {
        if (this.tasks.remove(task)) {
            this.associatedTasks.remove(task.getThread());
            task.removeTaskListener(this);
            this.fireTaskRemoved(task);
            //            System.err.println("[TaskManager] remove task " + task.getName());
            return true;
        }
        return false;
    }

    @Override
    public void onStateChange(Task task, TaskState oldState) {
        if (task.getState().isFinished()) {
            this.removeTask(task);
        }

    }

    /**
     * a new task has been added to this manager
     * 
     * @param task
     *            newly managed task
     */
    private void fireTaskAdded(Task task) {
        TaskManagerListener listeners[] = this.listeners.toArray(DUMMYTASKMANAGERLISTENERARRAY);
        for (TaskManagerListener listener : listeners) {
            listener.onTaskAdded(task);
        }

    }

    /**
     * a new task has been added to this manager
     * 
     * @param task
     *            old managed task
     */
    private void fireTaskRemoved(Task task) {
        TaskManagerListener listeners[] = this.listeners.toArray(DUMMYTASKMANAGERLISTENERARRAY);
        for (TaskManagerListener listener : listeners) {
            listener.onTaskRemoved(task);
        }

    }

    /**
     * @return the listeners
     */
    public boolean addTaskManagerListener(TaskManagerListener listener) {
        synchronized (this.listeners) {
            return this.listeners.add(listener);
        }
    }

    /**
     * @return the listeners
     */
    public boolean removeTaskManagerListener(TaskManagerListener listener) {
        synchronized (this.listeners) {
            return this.listeners.remove(listener);
        }
    }

    /**
     * @return the listeners
     */
    public void removeAllTaskManagers() {
        synchronized (this.listeners) {
            this.listeners.clear();
        }
    }

    /**
     * Wait for task termination. We should use a wait/notify method to avoid
     * time consuming wait
     * 
     * @param task
     *            task to wait for
     * @param maxWaitTime
     *            max time to wait for termination (0 = infinite)
     * @return true if max time has not been reached
     */
    public static boolean wait(Task task, long maxWaitTime) {
        boolean[] finished = { false };
        TaskListener taskWaiter = new TaskWaiter(finished);
        long startTime = new Date().getTime();
        task.addTaskListener(taskWaiter);
        while (finished[0] == false && (maxWaitTime <= 0 || new Date().getTime() - startTime < maxWaitTime)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // no matter
            }
        }
        task.removeTaskListener(taskWaiter);
        return finished[0];
    }
}

class TaskWaiter implements TaskListener {
    private final boolean[] finished;

    public TaskWaiter(boolean[] finished) {
        this.finished = finished;
        this.finished[0] = false;
    }

    @Override
    public void onStateChange(Task task, TaskState oldState) {
        if (task.getState().isFinished()) {
            this.finished[0] = true;
        }
    }

}
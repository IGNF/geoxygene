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
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * @author JeT manage a set of tasks
 */
public class TaskManager implements TaskListener<Task> {

    private static final Logger logger = Logger.getLogger(TaskManager.class
            .getName()); // logger

    private static final boolean debug = false;
    private static final TaskManagerListener[] DUMMYTASKMANAGERLISTENERARRAY = new TaskManagerListener[] {};
    private final Object taskLock = new Object();
    private final Queue<Task> pendingTasks = new LinkedList<Task>();
    private final Map<Thread, Task> runningTasks = new HashMap<Thread, Task>();
    private final Map<Task, Date> removedTasks = new HashMap<Task, Date>();
    private final Set<TaskManagerListener> listeners = new HashSet<TaskManagerListener>();
    private Thread.UncaughtExceptionHandler uncaughtExceptionHandler = null;
    private int maximumRunningThreadNumber = 10;

    /**
     * Constructor
     */
    public TaskManager() {
        this.uncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread th, Throwable ex) {
                Task task = TaskManager.this.runningTasks.get(th);
                if (task == null) {
                    logger.error("Impossible case. Exception caught from a non referenced thread "
                            + th.getName());
                    ex.printStackTrace();
                    return;
                }
                logger.error("Uncaught exception from task " + task.getName()
                        + ": " + ex);
                TaskManager.this.removeRunningTask(task);
                ex.printStackTrace();
            }
        };
    }

    /**
     * @return the maximumRunningThreadNumber
     */
    public int getMaximumRunningThreadNumber() {
        return this.maximumRunningThreadNumber;
    }

    /**
     * @param maximumRunningThreadNumber
     *            the maximumRunningThreadNumber to set
     */
    public void setMaximumRunningThreadNumber(int maximumRunningThreadNumber) {
        this.maximumRunningThreadNumber = maximumRunningThreadNumber;
    }

    /**
     * add a new Managed task. This method acts as producer in producer/consumer
     * pattern
     * 
     * @return true if task is correctly added
     */
    public boolean addTask(final Task task) {
        if (task == null) {
            if (debug) {
                System.err.println("asked to add a null task");
            }
            return false;
        }
        if (debug) {
            System.err.println("asked to add task '" + task.getName()
                    + "' hash=" + task.hashCode());
        }
        // check if the task has already been launched
        if (task.getState() != TaskState.WAITING) {
            logger.warn("task " + task.getName()
                    + " added to task manager with state " + task.getState());
        }

        synchronized (this.taskLock) {
            if (this.pendingTasks.contains(task)) {
                logger.warn("Try to ask task " + task.getName() + " ("
                        + task.hashCode()
                        + ") which is already in pending list. Skip it.");
                return false;
            }
            if (this.runningTasks.containsValue(task)) {
                logger.warn("Try to ask task " + task.getName() + " ("
                        + task.hashCode()
                        + ") which is already in running list. Skip it.");
                return false;
            }
            if (!this.pendingTasks.add(task)) {
                if (debug) {
                    System.err.println("unable to add task " + task.getName()
                            + " to pending tasks");
                }
                return false;
            } else if (debug) {
                System.err.println("task " + task.getName()
                        + " added to pending tasks");
            }
            // System.err.println("task added to pending list " + task + " => "
            // + this.pendingTasks.size());
        }
        // //System.err.println("fire 'task added' event " + task);
        this.fireTaskAdded(task);
        this.tryToStartTasks();
        return true;
    }

    /**
     * If there some pending tasks and running limit is not reached, start tasks
     */
    private void tryToStartTasks() {
        if (debug) {
            System.err.println("try to start a task running = "
                    + this.runningTasks.size() + "/"
                    + this.getMaximumRunningThreadNumber() + " pending = "
                    + this.pendingTasks.size());
        }
        while (this.runningTasks.size() < this.getMaximumRunningThreadNumber()) {
            Task task = null;
            synchronized (this.taskLock) {
                task = this.pendingTasks.poll(); // removed from pending
                // System.err.println("task removed to pending list " + task +
                // " => " + this.pendingTasks.size());
                // //System.err.println("task removed from pending " + task);
                if (task == null) {
                    return; // no task to start
                }
                if (debug) {
                    System.err.println("task " + task.getName()
                            + " removed from pending tasks");
                }
                // //System.err.println("start task " + task);
                task.addTaskListener(this);
                Thread taskThread = task.start(this.uncaughtExceptionHandler); // start
                                                                               // task
                if (taskThread == null) {
                    logger.error("task " + task.getName()
                            + " cannot be started");
                    continue;
                }
                if (debug) {
                    System.err.println("task " + task.getName() + " started");
                }
                // System.err.println("task added to running list " + task +
                // " => " + this.runningTasks.size());
                // //System.err.println("task added to running " + task);
                Task previousTask = this.runningTasks.put(taskThread, task); // added

                if (debug) {
                    System.err.println("task " + task.getName()
                            + " added to running list : "
                            + this.runningTasks.size() + "/"
                            + this.getMaximumRunningThreadNumber());
                }
                if (previousTask != null) {
                    logger.error("Impossible case: the new thread already exists in running tasks ! "
                            + task);
                }
            }
            this.fireTaskStarted(task);
            // //System.err.println("fire 'task started' event " + task);
        }
    }

    /**
     * remove a task from managed tasks
     */
    public boolean removeRunningTask(final Task task) {
        // //System.err.println("ask to remove task " + task);
        if (task.getThread() == null) {
            logger.error("I was asked to remove running task " + task
                    + " which has no valid thread : " + task.getThread());
            return false;
        }
        synchronized (this.taskLock) {
            if (this.runningTasks.remove(task.getThread()) == null) {
                logger.warn("trying to remove task " + task.getName()
                        + " which is not in the list of running tasks");
                if (debug) {
                    Date removedDate = this.removedTasks.get(task);
                    if (removedDate != null) {
                        System.err
                                .println("task "
                                        + task.getName()
                                        + " has already been removed "
                                        + (new Date().getTime() - removedDate
                                                .getTime()) + " ms ago");
                    } else {
                        System.err
                                .println("task "
                                        + task.getName()
                                        + " has never been removed but does not exist in running tasks:");
                        for (Task runningTask : this.runningTasks.values()) {
                            System.err.println("\t task : "
                                    + runningTask.getName() + " state: "
                                    + runningTask.getState());
                        }
                    }
                }
                return false;
            } else {
                if (debug) {
                    System.err.println("task " + task
                            + " correctly finished with state "
                            + task.getState()
                            + " and removed from running state");
                    this.removedTasks.put(task, new Date());
                }
            }
            // System.err.println("task removed from running list " + task +
            // " => " + this.runningTasks.size());
        }
        this.fireTaskRemoved(task);
        task.removeTaskListener(this);
        // //System.err.println("fire 'task removed' event " + task);
        this.tryToStartTasks();
        // //System.err.println("[TaskManager] remove task " + task.getName());
        return true;
    }

    /**
     * @return the number of pending tasks
     */
    public int getPendingTaskCount() {
        synchronized (this.taskLock) {
            return this.pendingTasks.size();
        }
    }

    /**
     * @return the number of currently running tasks
     */
    public int getRunningTaskCount() {
        synchronized (this.taskLock) {
            return this.runningTasks.size();
        }
    }

    @Override
    public void onStateChange(Task task, TaskState oldState) {
        if (debug) {
            System.err.println("task " + task.getName() + " stated from "
                    + oldState + " to " + task.getState());
        }
        if (!task.getState().isRunning()) {
            if (debug) {
                System.err.println("Task " + task.getName()
                        + " is no longer running. Stop listening to it");
            }
            this.removeRunningTask(task);
        }

    }

    /**
     * a new task has been added to this manager
     * 
     * @param task
     *            newly managed task
     */
    private void fireTaskAdded(Task task) {
        TaskManagerListener listeners[] = this.listeners
                .toArray(DUMMYTASKMANAGERLISTENERARRAY);
        for (TaskManagerListener listener : listeners) {
            listener.onTaskAdded(task);
        }

    }

    /**
     * a new task has been started in this manager
     * 
     * @param task
     *            started task
     */
    private void fireTaskStarted(Task task) {
        TaskManagerListener listeners[] = null;
        synchronized (this.listeners) {
            listeners = this.listeners.toArray(DUMMYTASKMANAGERLISTENERARRAY);
        }
        // System.err.println("task started sent to " + listeners.length +
        // " listeners");
        for (TaskManagerListener listener : listeners) {
            // System.err.println("task started sent to " + listener);
            listener.onTaskStarted(task);
        }

    }

    /**
     * a new task has been added to this manager
     * 
     * @param task
     *            old managed task
     */
    private void fireTaskRemoved(Task task) {
        TaskManagerListener listeners[] = null;
        synchronized (this.listeners) {
            listeners = this.listeners.toArray(DUMMYTASKMANAGERLISTENERARRAY);
        }
        // System.err.println("task removed sent to " + listeners.length +
        // " listeners");
        for (TaskManagerListener listener : this.listeners) {
            // System.err.println("task removed sent to " + listener);
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

    public static void startAndWait(final Task task)
            throws InterruptedException {
        if (task == null) {
            return;
        }
        TaskTerminationWaiter taskTerminationWaiter = new TaskTerminationWaiter(
                task);
        task.addTaskListener(taskTerminationWaiter);
        if (task.getState().isRunning() == false) {
            task.start(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread th, Throwable ex) {
                    logger.error("Uncaught exception from task "
                            + task.getName() + ": " + ex);
                    ex.printStackTrace();
                }
            });
        }
        while (task.getState().isRunning()
                || task.getState() == TaskState.WAITING) {
            synchronized (task) {
                task.wait(100);
            }
        }
        task.removeTaskListener(taskTerminationWaiter);

    }

    public static void waitForCompletion(final Task task)
            throws InterruptedException {
        if (task == null) {
            return;
        }
        TaskTerminationWaiter taskTerminationWaiter = new TaskTerminationWaiter(
                task);
        task.addTaskListener(taskTerminationWaiter);

        while (!task.getState().isFinished()) {
            synchronized (task) {
                task.wait(100);
            }
        }
        task.removeTaskListener(taskTerminationWaiter);

    }

    // /**
    // * Wait for task termination. We should use a wait/notify method to avoid
    // * time consuming wait
    // *
    // * @param task
    // * task to wait for
    // * @param maxWaitTime
    // * max time to wait for termination (0 = infinite)
    // * @return true if max time has not been reached
    // */
    // public static boolean wait(Task task, long maxWaitTime) {
    // boolean[] finished = { false };
    // TaskListener taskWaiter = new TaskWaiter(finished);
    // long startTime = new Date().getTime();
    // task.addTaskListener(taskWaiter);
    // while (finished[0] == false && (maxWaitTime <= 0 || new Date().getTime()
    // - startTime < maxWaitTime)) {
    // try {
    // Thread.sleep(100);
    // } catch (InterruptedException e) {
    // // no matter
    // }
    // }
    // task.removeTaskListener(taskWaiter);
    // return finished[0];
    // }
    private static class TaskTerminationWaiter implements TaskListener<Task> {
        private Task task = null;

        public TaskTerminationWaiter(Task task) {
            super();
            this.task = task;
        }

        @Override
        public void onStateChange(Task task, TaskState oldState) {
            if (task == null) {
                return;
            }
            synchronized (task) {
                task.notifyAll();
            }
        }

    }
}

// class TaskWaiter implements TaskListener {
// private final boolean[] finished;
//
// public TaskWaiter(boolean[] finished) {
// this.finished = finished;
// this.finished[0] = false;
// }
//
// @Override
// public void onStateChange(Task task, TaskState oldState) {
// if (task.getState().isFinished()) {
// this.finished[0] = true;
// }
// }
//
// }
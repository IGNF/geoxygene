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
 *         An Atomic task is a task which can give its progress value, can be
 *         paused and stopped.
 *         All state changes are already managed in the run method which call
 *         atomicLoop() method
 *         iteratively. The only pending points to implements
 *         are:
 *         - implement initializeTask() method which is called before entering
 *         loop
 *         - implement atomicLoop() method with a short computation task
 *         - implement finalizeTask() method which is called when atomicLoop
 *         won't be ever re-entered
 *         - use setProgress( 0..1 ) method to set task progress value
 *         - use setFinished(true) to end main loop
 */
public abstract class AtomicAbstractTask extends AbstractTask {

    private boolean finished = false;

    /**
     * constructor
     * 
     * @param name
     *            task name
     */
    public AtomicAbstractTask(String name) {
        super(name);
    }

    /**
     * main loop executed when running normally
     */
    public abstract void atomicLoop();

    /**
     * method called before entering main loop
     */
    public abstract void initializeTask();

    /**
     * method called when exiting main loop (use getError() method to know
     * if an end is normal or due to an error
     */
    public abstract void finalizeTask();

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        this.setFinished(false);

        try {
            // initialization step
            this.setState(TaskState.INITIALIZING);
            this.initializeTask();

            // main loop run
            this.setState(TaskState.RUNNING);
            while (!this.isFinished()) {
                // stop task case
                if (this.isStopRequested()) {
                    this.setState(TaskState.STOPPED);
                    return;
                }
                // pause/unpause task case
                if (this.getState() == TaskState.PAUSED && this.isPauseRequested() == false) {
                    this.setState(TaskState.RUNNING);
                } else if (this.getState() == TaskState.RUNNING && this.isPauseRequested() == true) {
                    this.setState(TaskState.PAUSED);
                }
                if (this.getState() == TaskState.RUNNING) {
                    this.atomicLoop(); // main loop
                }
            }
            // finalizing task
            this.setError(null);
            this.setState(TaskState.FINALIZING);
            this.finalizeTask();
            // success
            this.setProgress(1.);
            this.setState(TaskState.FINISHED);
        } catch (Exception e) {
            this.setError(e);
            // finalizing task
            this.setState(TaskState.FINALIZING);
            this.finalizeTask();
            // error
            this.setState(TaskState.ERROR);
        }
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return this.finished;
    }

    /**
     * @param finished
     *            the finished to set
     */
    private void setFinished(boolean finished) {
        this.finished = finished;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isProgressable()
     */
    @Override
    public boolean isProgressable() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isPausable()
     */
    @Override
    public boolean isPausable() {
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isStopable()
     */
    @Override
    public boolean isStoppable() {
        return true;
    }

}

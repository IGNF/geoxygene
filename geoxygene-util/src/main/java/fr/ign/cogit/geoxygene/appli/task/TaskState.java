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
 * 
 */
public enum TaskState {

    WAITING(false, false, false), INITIALIZING(true, false, false), RUNNING(true, false, false), PAUSED(false, false, false), STOPPED(false, true, false), FINALIZING(
            true, false, false), FINISHED(false, true, false), ERROR(false, true, true);

    private boolean running = false;
    private boolean finished = false;
    private boolean error = false;

    private TaskState(boolean running, boolean finished, boolean error) {
        this.running = running;
        this.finished = finished;
        this.error = error;
    }

    /**
     * @return the running
     */
    public boolean isRunning() {
        return this.running;
    }

    /**
     * @return the error
     */
    public boolean isError() {
        return this.error;
    }

    /**
     * @return the finished
     */
    public boolean isFinished() {
        return this.finished;
    }

}

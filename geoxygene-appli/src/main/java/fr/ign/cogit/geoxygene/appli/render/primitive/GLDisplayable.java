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

package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.util.Collection;
import java.util.Date;

import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;

/**
 * @author JeT A GLDisplayable is an object drawn in a GL Panel. Those objects
 *         are generated asynchronously and are able to give a fast and simple
 *         representation of themselves during asynchronous GLComplex generation
 *         fast representation is called "partialRepresentation" complete is
 *         called "fullRepresentation"
 */
public interface GLDisplayable extends Task {

    /**
     * get the number of time this displayable has been displayed on screen
     */
    public long getDisplayCount();

    /**
     * @return the last time this displayable has been displayed
     */
    public Date getLastDisplayTime();

    /**
     * Synchronous method returning a quick and dirty representation of the
     * object
     */
    public GLComplex getPartialRepresentation();

    /**
     * Asynchronous method returning a full representation of the object. It
     * returns null if the computation is not finished. Listen to the task state
     * change to know when this method will return a non null value
     */
    public Collection<GLComplex> getFullRepresentation();
}

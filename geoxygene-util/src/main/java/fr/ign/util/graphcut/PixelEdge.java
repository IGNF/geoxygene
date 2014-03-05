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

package fr.ign.util.graphcut;

import org.jgrapht.EdgeFactory;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * @author JeT
 *         graph edges used in graph cut algorithm
 */
public class PixelEdge extends DefaultWeightedEdge {

    private static final long serialVersionUID = -4576588914291793472L; // Serializable UID
    private PixelVertex source = null;
    private PixelVertex target = null;

    /**
     * 
     */
    public PixelEdge(PixelVertex source, PixelVertex target) {
        this.source = source;
        this.target = target;

    }

    /**
     * @return the source
     */
    @Override
    public PixelVertex getSource() {
        return this.source;
    }

    /**
     * @return the target
     */
    @Override
    public PixelVertex getTarget() {
        return this.target;
    }

    /**
     * Factory used to generate edges during Max-Flow Graph algorithm
     * 
     * @author JeT
     * 
     */
    public static class PixelEdgeFactory implements EdgeFactory<PixelVertex, PixelEdge> {

        /**
         * private constructor
         */
        public PixelEdgeFactory() {
        }

        @Override
        public PixelEdge createEdge(PixelVertex sourceVertex, PixelVertex targetVertex) {
            return new PixelEdge(sourceVertex, targetVertex);
        }

    }

}

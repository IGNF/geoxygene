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

package fr.ign.cogit.geoxygene.util.gl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * @author JeT
 *         A GL mesh is the definition of a topology in a vertices cloud. It is
 *         based
 *         on GL primitives definition (TRIANGLE, QUAD, FAN, STRIP, etc...)
 */
public class GLMesh {

    private GLComplex complexParent = null;
    private int glType = -1; // type of GL primitive of this mesh (TRIANGLE, QUAD, FAN, STRIP, etc...)
    private final List<Integer> indices = new ArrayList<Integer>();
    private int firstIndex = -1; // first index in the parent vertex indices list (included)
    private int lastIndex = -1; // last index in the parent vertex indices list (included)

    //    private final float[] color = new float[4];

    /**
     * Default constructor
     * 
     * @param complexParent
     *            parent of this topology containing the vertices
     */
    public GLMesh(int glType, GLComplex complexParent) {
        super();
        this.glType = glType;
        this.complexParent = complexParent;
    }

    /**
     * get the list of indices composing this Mesh
     */
    public List<Integer> getIndices() {
        return this.indices;
    }

    //    /**
    //     * @return the color
    //     */
    //    public float[] getColor() {
    //        return this.color;
    //    }

    /**
     * Set the given to all points pointed by this mesh. If points are shared
     * between meshes, the color is overridden !
     * The full vbo is reconstructed
     */
    public void setColor(final java.awt.Color color2) {
        for (int vertexIndex : this.indices) {
            this.complexParent.getVertices().get(vertexIndex).setRGBA(color2);
        }
        this.complexParent.invalidateBuffers();
    }

    /**
     * @return the glType
     */
    public int getGlType() {
        return this.glType;
    }

    /**
     * Add a list of indices for this mesh
     */
    public void addIndex(final Integer index) {
        this.indices.add(index);
    }

    /**
     * Add a list of indices for this mesh
     */
    public void addIndices(final Integer... primitiveIndices) {
        for (int index : primitiveIndices) {
            this.indices.add(index);
        }
    }

    /**
     * Add a list of indices for this mesh
     */
    public void addIndices(final Collection<Integer> primitiveIndices) {
        this.indices.addAll(primitiveIndices);
    }

    /**
     * @return the firstIndex
     */
    public int getFirstIndex() {
        return this.firstIndex;
    }

    /**
     * @param firstIndex
     *            the firstIndex to set
     */
    public void setFirstIndex(int firstIndex) {
        this.firstIndex = firstIndex;
    }

    /**
     * @return the lastIndex
     */
    public int getLastIndex() {
        return this.lastIndex;
    }

    /**
     * @param lastIndex
     *            the lastIndex to set
     */
    public void setLastIndex(int lastIndex) {
        this.lastIndex = lastIndex;
    }

    /**
     * Compare meshes on their type
     */
    public static class MeshTypeComparator implements Comparator<GLMesh> {

        @Override
        public int compare(final GLMesh mesh1, final GLMesh mesh2) {
            return mesh1.getGlType() - mesh2.getGlType();
        }

    }
}

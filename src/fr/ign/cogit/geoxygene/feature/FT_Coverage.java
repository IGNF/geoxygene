/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut G�ographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut G�ographique National
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
 */
package fr.ign.cogit.geoxygene.feature;

import java.awt.geom.Rectangle2D;

import org.geotools.coverage.grid.GridCoverage2D;
import org.opengis.geometry.Envelope;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

/**
 * 
 * FT_Coverage is used to store image data (i.e. geotiffs). It stores the image
 * itself as a Geotools GridCoverage and the envelope of this image. This class
 * is intended to be a temporary class until the 2dCoverages are fully managed.
 * 
 * @see GridCoverage2D
 * @author Bertrand Dumenieu
 * 
 */
public class FT_Coverage extends AbstractFeature {

    private GridCoverage2D coverage;
    private GM_Envelope box;

    public FT_Coverage(GridCoverage2D coverage) {
        this.setCoverage(coverage);
        Envelope envelope = coverage.getEnvelope();
        this.setBox(new GM_Envelope(new DirectPosition(envelope
                .getUpperCorner().getCoordinate()), new DirectPosition(envelope
                .getLowerCorner().getCoordinate())));
    }

    public GridCoverage2D coverage() {
        return this.coverage;

    }

    public void setCoverage(GridCoverage2D _coverage) {
        this.coverage = _coverage;
        Rectangle2D rec = coverage.getEnvelope2D().getBounds2D();
        this.geom = new GM_Envelope(rec.getMinX(), rec.getMaxX(),
                rec.getMinY(), rec.getMaxY()).getGeom();
    }

    public void setBox(GM_Envelope box) {
        this.box = box;
    }

    public GM_Envelope getBox() {
        return box;
    }

    @Override
    public IFeature cloneGeom() throws CloneNotSupportedException {
        return null;
    }
}

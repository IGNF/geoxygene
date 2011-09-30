/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.appli.geopensim.feature.meso;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * @author Julien Perret
 *
 */
public class ZoneSurfacique extends MesoRepresentation {
	//static Logger logger=Logger.getLogger(ZoneSurfacique.class.getName());

	//Forme
	protected double elongation;
	protected double convexite;
	//Aire
	protected double aire = 0.0;

    /**
     * Constructeur vide
     */
    public ZoneSurfacique() {
        super();
    }

    /**
     * Constructeur à partir d'une géométrie
     */
    public ZoneSurfacique(IPolygon polygone) {
        super(polygone);
    }

    /**
     * @return élongation de la zone surfacique
     */
    public double getElongation() {
        return this.elongation;
    }

    /**
     * @param elongation
     *            élongation de la zone surfacique
     */
    public void setElongation(double elongation) {
        this.elongation = elongation;
    }

    /**
     * @return convexite de la zone surfacique
     */
    public double getConvexite() {
        return this.convexite;
    }

    /**
     * @param convexite
     *            convexite de la zone surfacique
     */
    public void setConvexite(double convexite) {
        this.convexite = convexite;
    }

    /**
     * @return Aire de la zone surfacique
     */
    public double getAire() {
        return this.aire;
    }

    /**
     * @param aire
     *            aire de la zone surfacique
     */
    public void setAire(double aire) {
        this.aire = aire;
    }

    @Override
    public void qualifier() {
        super.qualifier();
        // Surface
        if (getGeom() == null) {
            logger.error("géométrie de la zone surfacique nulle ");
            return;
        }
        this.setAire(getGeom().area());
        // Forme
        Geometry geometry = null;
        try {
            geometry = AdapterFactory.toGeometry(new GeometryFactory(), this
                    .getGeom());
        } catch (Exception e) {
            logger.error("Erreur dans la construction de la géométrie "
                    + this.getGeom() + " pour un objet de type "
                    + this.getClass());
            if (logger.isDebugEnabled()) {
                logger.debug(e.getMessage());
            }
        }
        if (geometry instanceof Polygon) {
            Polygon polygon = (Polygon) geometry;
            this.elongation = JtsUtil.elongation(polygon);
            this.convexite = JtsUtil.convexite(polygon);
        }
    }

    /**
     * Renvoie la géométrie de la zone surfacique sous la forme d'un GM_Polygon.
     * 
     * @return la géométrie de la zone surfacique sous la forme d'un GM_Polygon.
     */
    public GM_Polygon getGeometrie() {
        return (GM_Polygon) this.getGeom();
    }
}

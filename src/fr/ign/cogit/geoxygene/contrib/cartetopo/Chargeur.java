/*
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at
 * the Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation; either version 2.1 of the License, or any later
 * version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details. You should have received a copy of the GNU Lesser General
 * Public License along with this library (see file LICENSE if present); if
 * not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Primitive;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * Chargeur permettant de créer une carte topo à partir de classes de
 * "FT_Feature".
 * @author Sébastien Mustière
 * @author Olivier Bonin
 * @author Julien Perret
 */

public class Chargeur {
    /**
     * Logger.
     */
    static Logger logger = Logger.getLogger(Chargeur.class.getName());
    /**
     * Charge en mémoire les éléments de la classe 'nomClasseGeo'
     * et remplit la carte topo 'carte' avec des correspondants de ces
     * éléments.
     * @param nomClasseGeo class name
     * @param carte topological map
     */
    public static void importClasseGeo(
            final String nomClasseGeo, final CarteTopo carte) {
        Class<?> clGeo;

        try {
            clGeo = Class.forName(nomClasseGeo);
        } catch (Exception e) {
            logger.warn(I18N.getString(
            "Chargeur.ClassWarning") //$NON-NLS-1$
            + nomClasseGeo + I18N.getString(
            "Chargeur.DoesNotExist")); //$NON-NLS-1$
            logger.warn(I18N.getString(
            "Chargeur.ImportImpossible")); //$NON-NLS-1$
            e.printStackTrace();
            return;
        }

        FT_FeatureCollection<?> listeFeatures =
            DataSet.db.loadAllFeatures(clGeo);
        importClasseGeo(listeFeatures, carte);
    }

    /**
     * Remplit la carte topo 'carte' avec des correspondants des éléments
     * de 'listeFeature'.
     * @param listeFeatures éléments
     * @param carte carte topo
     */
    public static void importClasseGeo(
            final FT_FeatureCollection<?> listeFeatures,
            final CarteTopo carte) {
        importClasseGeo(listeFeatures, carte, false);
    }

    /**
     * Remplit la carte topo 'carte' avec des correspondants des éléments de
     * 'listeFeature'.
     * @param listeFeatures éléments
     * @param carte carte topo
     * @param convert2d si vrai, alors convertir les géométries en 2d
     */
    public static void importClasseGeo(
            final FT_FeatureCollection<?> listeFeatures,
            final CarteTopo carte, final boolean convert2d) {
        if (listeFeatures.isEmpty()) {
            logger.warn(I18N.getString(
            "Chargeur.NothingImported")); //$NON-NLS-1$
            return;
        }
        if (listeFeatures.get(0).getGeom() instanceof GM_Point) {
            int nbElements =
                importClasseGeo(listeFeatures,carte.getPopNoeuds(), convert2d);
            if (logger.isDebugEnabled()) {
                logger.debug(I18N.getString(
                "Chargeur.NumberOfImportedNodes") + nbElements); //$NON-NLS-1$
            }
            return;
        }
        if ((listeFeatures.get(0).getGeom() instanceof GM_LineString)
                || (listeFeatures.get(0).getGeom() instanceof GM_MultiCurve<?>)
        ) {
            int nbElements =
                importClasseGeo(listeFeatures, carte.getPopArcs(),convert2d);
            if (logger.isDebugEnabled()) {
                logger.debug(I18N.getString(
                "Chargeur.NumberOfImportedEdges") + nbElements); //$NON-NLS-1$
            }
            return;
        }
        if ((listeFeatures.get(0).getGeom() instanceof GM_Polygon)
                || (listeFeatures.get(0).getGeom() instanceof
                        GM_MultiSurface<?>)) {
            int nbElements =
                importClasseGeo(listeFeatures,carte.getPopFaces(), convert2d);
            if (logger.isDebugEnabled()) {
                logger.debug(I18N.getString(
                "Chargeur.NumberOfImportedFaces") + nbElements); //$NON-NLS-1$
            }
            return;
        }
        logger.warn(I18N.getString(
        "Chargeur.WarningNothingImported") //$NON-NLS-1$
        + listeFeatures.get(0).getClass().getName());
    }

    /**
     * Remplit la carte topo 'carte' avec des correspondants des éléments
     * de 'listeFeature'.
     * @param listeFeatures éléments
     * @param population the population to import
     * @param convert2d si vrai, alors convertir les géométries en 2d
     */
    @SuppressWarnings("unchecked")
    private static int importClasseGeo(
            final FT_FeatureCollection<?> listeFeatures,
            final Population<?> population, final boolean convert2d) {
        int nbElements = 0;
        for(FT_Feature feature : listeFeatures) {
            if (feature.getGeom() instanceof GM_Primitive) {
                creeElement(feature, feature.getGeom(),population,convert2d);
                nbElements++;
            } else {
                for (GM_Object geom : 
                    ((GM_Aggregate<GM_Object>) feature.getGeom())) {
                    try {
                        creeElement(feature, geom,population, convert2d);
                        nbElements++;
                    } catch (Exception e) { e.printStackTrace(); }
                }
            }
        }
        return nbElements;
    }

    /**
     * crée un élément de la carte topo comme correspondant de l'objet
     * feature et la géométrie geom.
     * @param geom géométrie du nouvel élément
     * @param population population à laquelle ajout le nouvel élément
     * @param convert2d si vrai alors la géométrie du nouvel élément est
     * convertie en 2d 
     */
    private static void creeElement(final FT_Feature feature,
            final GM_Object geom, final Population<?> population,
            final boolean convert2d) {
        FT_Feature nouvelElement;
        try {
            nouvelElement = population.nouvelElement(
                    convert2d ? AdapterFactory.to2DGM_Object(geom) : geom);
            nouvelElement.addCorrespondant(feature);
        } catch (Exception e) { e.printStackTrace(); }
    }

	/**
	 * Seuls les points des éléments sont importés comme noeuds de la carte.
	 * @param listeFeatures
	 * @param carteTopo
	 */
	public static void importAsNodes(FT_FeatureCollection<?> listeFeatures,
			CarteTopo carteTopo) {
		Class<Noeud> nodeClass = carteTopo.getPopNoeuds().getClasse();
		try {
			Constructor<Noeud> constructor = nodeClass.getConstructor(DirectPosition.class);
			for (FT_Feature f : listeFeatures) {
	    		for (DirectPosition p : f.getGeom().coord()) {
	    			try {
						Noeud n = constructor.newInstance(p);
						carteTopo.getPopNoeuds().add(n);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (InstantiationException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
	    		}
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}
}

/*
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
 */

package fr.ign.cogit.geoxygene.contrib.graphe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.index.Tiling;



/**
 * Recherche du plus proche voisin (meilleur projete) d'un ensemble de points sur un réseau linéaire.
 * 
 * @author R. Cuissard - ENSG
 */
public class PPV {
  
  private static Logger LOGGER = LogManager.getLogger(PPV.class.getName());
  
  /**
   * Calcul du PPV points / lignes graviSHP.
   * 
   * @param gravi : population des points à traiter
   * @param bdT : réseau linéaire  
   * @param deltaZOption : si true, ajout d'un champ deltaZ pour connaissance du gain Z
   * @param distappa : distance maximale 
   * @return 
   */
  public static Population<DefaultFeature> run(IFeatureCollection<?> gravi, IFeatureCollection<?> bdT,
      boolean deltaZOption, double distappa) {
    
    // Lecture de la couche gravi
    LOGGER.info("Lecture de la couche gravi...");
    LOGGER.info("nb de points = " + gravi.getElements().size());
    
    // Lecture de la couche bd topo
    LOGGER.info("Lecture de la couche bdtopo...");
    LOGGER.info("nb d'arcs = " + bdT.getElements().size());
    
    // Préparation de la couche résultat
    FeatureType ftPoints = new FeatureType();
    ftPoints.setGeometryType(GM_Point.class);
    AttributeType idAtt = new AttributeType("id", "int");
    AttributeType distanceAtt = new AttributeType("distance", "double");
    AttributeType deltaZAtt = new AttributeType("deltaZ", "double");
    ftPoints.addFeatureAttribute(idAtt);
    ftPoints.addFeatureAttribute(distanceAtt);
    ftPoints.addFeatureAttribute(deltaZAtt);

    SchemaDefaultFeature schema = new SchemaDefaultFeature();
    schema.setFeatureType(ftPoints);
    ftPoints.setSchema(schema);

    Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);

    attLookup.put(new Integer(0),
        new String[] { idAtt.getNomField(), idAtt.getMemberName() });
    attLookup.put(new Integer(1), new String[] { distanceAtt.getNomField(),
        distanceAtt.getMemberName() });
    attLookup.put(new Integer(2), new String[] { deltaZAtt.getNomField(),
        deltaZAtt.getMemberName() });
    schema.setAttLookup(attLookup);

    Population<DefaultFeature> pointsProjetes = new Population<DefaultFeature>(
        "Points projetes"); 
    pointsProjetes.setFeatureType(ftPoints);
    pointsProjetes.setClasse(DefaultFeature.class);

    // Creation de l'index spatial sur la bd topo...
    if (!bdT.hasSpatialIndex()) {
      bdT.initSpatialIndex(Tiling.class, true, 20);
    }
    
    // Parcours des points gravi
    int cpt = 1;
    for (IFeature ptGravi : gravi) {
      LOGGER.trace("Pt " + cpt + "/" + gravi.size());
      
      // Ajout d'un Z bidon au point gravi
      ptGravi.getGeom().coord().get(0).setZ(0);

      // Filtrage des troncons de route à moins de 100 000 m du point gravi
      Collection<?> selection = bdT.select(ptGravi.getGeom()
          .coord().get(0), 100000);

      double distmin = distappa;
      IDirectPosition ptPlusProche = null;
      
      // Parcours des troncons de route pour détermination PPV et distance
      for (Object ligneCourante : selection) {
        
        // Projection du point sur la ligne
        IDirectPositionList listePoints = ((IFeature) ligneCourante).getGeom().coord();
        GM_LineString gmL = new GM_LineString(listePoints);
        IDirectPosition ptProjete = Operateurs.projection(ptGravi
            .getGeom().coord().get(0), gmL);
        // Determination de la distance entre le point courant et le
        // point projeté
        // et vérification distance minimale ou pas...
        double dist = ptProjete.distance(ptGravi.getGeom().coord()
            .get(0));
        // System.out.println("dist = " + dist);
        if (dist < distmin) {
          distmin = dist;
          ptPlusProche = ptProjete;
        }
      }

      // S'il n'y a pas de route à l'issue de la sélection il n'y a pas
      // de point projeté...
      if (ptPlusProche != null) {
        // Ajout du point projeté à la couche résultat
        DefaultFeature projectedPoint = pointsProjetes.nouvelElement();
        projectedPoint.setGeom(ptPlusProche.toGM_Point());
        projectedPoint.setId(ptGravi.getId());
        projectedPoint.setFeatureType(ftPoints);
        // projectedPoint.setAttribute("distance", distmin);
        projectedPoint.setSchema(schema);

        double deltaZ = 0;
        if (deltaZOption == true) {
          deltaZ = projectedPoint.getGeom().coord().get(0).getZ()
              - new Double((Double) ptGravi.getAttribute("H"))
                  .doubleValue();
        }

        Object[] attributes = new Object[] { ptGravi.getId(), distmin,
            deltaZ };

        projectedPoint.setAttributes(attributes);
        LOGGER.trace("pt " + cpt + " ajouté à la collection !");
      }
      cpt++;
    }

    return pointsProjetes;
  }

}

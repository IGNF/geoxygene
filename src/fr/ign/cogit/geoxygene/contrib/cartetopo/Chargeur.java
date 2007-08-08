/*
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
 *  
 */

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import java.util.Iterator;

import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * Chargeur permettant de créer une carte topo à partir de classes de "FT_Feature"
 * @author  Mustière/Bonin
 * @version 1.0
 */

public class Chargeur {

	/** Charge en mémoire les élements de la classe 'nomClasseGeo'
	 * et remplit la carte topo 'carte' avec des correspondants de ces éléments.
	 */ 
    public static void importClasseGeo(String nomClasseGeo, CarteTopo carte) {
        Class clGeo;
        
        try {
            clGeo = Class.forName(nomClasseGeo);
        } catch (Exception e) {
            System.out.println("ATTENTION : La classe nommée "+nomClasseGeo+ " n'existe pas");
            System.out.println("            Impossible donc de l'importer");
            e.printStackTrace();
            return;
        }

        FT_FeatureCollection listeFeatures = DataSet.db.loadAllFeatures(clGeo);
        importClasseGeo(listeFeatures,carte);
    }   

	/** Remplit la carte topo 'carte' avec des correspondants des éléments de 'listeFeature'.
	 */ 
	public static void importClasseGeo(FT_FeatureCollection listeFeatures, CarteTopo carte) {
		FT_Feature objGeo;
		Noeud noeud;
		Arc arc;
		Face face;
		int nbNoeuds=0, nbArcs=0, nbFaces=0;
		Iterator itFeatures = listeFeatures.getElements().iterator();

		while (itFeatures.hasNext()) {
		  objGeo = (FT_Feature)itFeatures.next();
          if ( objGeo.getGeom() instanceof fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point ) {
              noeud = (Noeud)carte.getPopNoeuds().nouvelElement();
              noeud.setGeometrie( (GM_Point)objGeo.getGeom() );
              noeud.addCorrespondant(objGeo);
              nbNoeuds++;
              continue;
          }
          if ( objGeo.getGeom() instanceof fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString ) {
              arc = (Arc)carte.getPopArcs().nouvelElement();
              arc.setGeometrie( (GM_LineString)objGeo.getGeom() );
			  arc.addCorrespondant(objGeo);
			  nbArcs++;
		      continue;
          } 
          if ( objGeo.getGeom() instanceof fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon ) {
              face = (Face)carte.getPopFaces().nouvelElement();
              face.setGeometrie( (GM_Polygon)objGeo.getGeom() );
			  face.addCorrespondant(objGeo);
			  nbFaces++;
			  continue;
          }
          System.out.println("Attention: objet non importé (id="+objGeo.getId()+", géométrie de type "+objGeo.getGeom().getClass()+")");
		}
		System.out.println("Nb de noeuds importés : "+nbNoeuds);
		System.out.println("Nb d'arcs importés    : "+nbArcs);
		System.out.println("Nb de faces importées : "+nbFaces);
	}   
}

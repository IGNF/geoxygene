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

package fr.ign.cogit.appli.geopensim.geom;

import java.util.Arrays;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * A class used to generate geometric shapes.
 * <p>
 * Une classe pour générer des formes géométriques.
 * @author Julien Perret
 */
public class ShapeFactory {
	/**
	 * Crée un carré.
	 * @param centre centre du carré
	 * @param size largeur du carré
	 * @return un carré centré sur le centre en paramètre
	 */
	public static IGeometry createCarre(IDirectPosition centre, double size) {
	  return ShapeFactory.createRectangle(centre, size, size);
	}
	/**
	 * Crée un rectangle.
	 * @param centre centre du rectangle
	 * @param largeur largeur du rectangle
	 * @param hauteur hauteur du rectangle
	 * @return un rectangle centré sur le centre en paramètre
	 */
	public static IGeometry createRectangle(IDirectPosition centre, double largeur, double hauteur) {
      double minx = centre.getX() - largeur / 2;
      double maxx = centre.getX() + largeur / 2;
      double miny = centre.getY() - hauteur / 2;
      double maxy = centre.getY() + hauteur / 2;
      IDirectPosition p1 = new DirectPosition(maxx, maxy);
      IDirectPosition p2 = new DirectPosition(minx, maxy);
      IDirectPosition p3 = new DirectPosition(minx, miny);
      IDirectPosition p4 = new DirectPosition(maxx, miny);
      return new GM_Polygon(new GM_LineString(new DirectPositionList(Arrays.asList(p1, p2, p3, p4, p1))));
	}
	/**
	 * Crée une forme de L.
	 * @param centre centre de la forme
	 * @param largeur1 largeur totale de la forme
	 * @param hauteur1 hauteur totale de la forme
	 * @param largeur2 largeur de la barre du L
	 * @param hauteur2 hauteur de la base du L
	 * @return un L centré sur le centre en paramètre
	 */
	public static IGeometry createL(IDirectPosition centre, double largeur1, double hauteur1, double largeur2, double hauteur2) {
		return new GM_Polygon(new GM_LineString(new DirectPositionList(Arrays.asList(
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2+largeur2,centre.getY()-hauteur1/2+hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2+largeur2,centre.getY()+hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2,centre.getY()+hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2,centre.getY()-hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur1/2,centre.getY()-hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur1/2,centre.getY()-hauteur1/2+hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2+largeur2,centre.getY()-hauteur1/2+hauteur2)))));
	}
	/**
	 * Crée une forme de U.
	 * @param centre centre de la forme
	 * @param largeur1 largeur totale de la forme
	 * @param hauteur1 hauteur totale de la forme
	 * @param largeur2 largeur de la barre du U
	 * @param hauteur2 hauteur de la base du U
	 * @return un U centré sur le centre en paramètre
	 */
	public static IGeometry createU(IDirectPosition centre, double largeur1, double hauteur1, double largeur2, double hauteur2) {
		return new GM_Polygon(new GM_LineString(new DirectPositionList(Arrays.asList(
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2+largeur2,centre.getY()-hauteur1/2+hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2+largeur2,centre.getY()+hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2,centre.getY()+hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2,centre.getY()-hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur1/2,centre.getY()-hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur1/2,centre.getY()+hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur1/2-largeur2,centre.getY()+hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur1/2-largeur2,centre.getY()-hauteur1/2+hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2+largeur2,centre.getY()-hauteur1/2+hauteur2)))));
	}
	/**
	 * Crée une forme de T.
	 * @param centre centre de la forme
	 * @param largeur1 largeur totale de la forme
	 * @param hauteur1 hauteur totale de la forme
	 * @param largeur2 largeur de la base du T
	 * @param hauteur2 hauteur de la barre du T
	 * @return un T centré sur le centre en paramètre
	 */
	public static IGeometry createT(IDirectPosition centre, double largeur1, double hauteur1, double largeur2, double hauteur2) {
		return new GM_Polygon(new GM_LineString(new DirectPositionList(Arrays.asList(
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2,centre.getY()+hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2,centre.getY()+hauteur1/2-hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur2/2,centre.getY()+hauteur1/2-hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur2/2,centre.getY()-hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur2/2,centre.getY()-hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur2/2,centre.getY()+hauteur1/2-hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur1/2,centre.getY()+hauteur1/2-hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur1/2,centre.getY()+hauteur1/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur1/2,centre.getY()+hauteur1/2)))));
	}
	/**
	 * Crée une forme d'escalier.
	 * @param centre centre de la forme
	 * @param largeur largeur totale de la forme
	 * @param hauteur hauteur totale de la forme
	 * @param largeur1 largeur 
	 * @param hauteur1 hauteur 
	 * @param largeur2 largeur 
	 * @param hauteur2 hauteur 
	 * @return un escalier centré sur le centre en paramètre
	 */
	public static IGeometry createEscalier(IDirectPosition centre, double largeur, double hauteur, double largeur1, double hauteur1, double largeur2, double hauteur2) {
		return new GM_Polygon(new GM_LineString(new DirectPositionList(Arrays.asList(
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2,centre.getY()-hauteur/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2+largeur1,centre.getY()-hauteur/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2+largeur1,centre.getY()-hauteur/2+hauteur1),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur/2,centre.getY()-hauteur/2+hauteur1),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur/2,centre.getY()+hauteur/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur/2-largeur2,centre.getY()+hauteur/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur/2-largeur2,centre.getY()+hauteur/2-hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2,centre.getY()+hauteur/2-hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2,centre.getY()-hauteur/2)))));
	}
	
	/**
	 * Crée une forme d'escalier.
	 * @param centre centre de la forme
	 * @param largeur largeur totale de la forme
	 * @param hauteur hauteur totale de la forme
	 * @param largeur1 largeur 
	 * @param hauteur1 hauteur 
	 * @param largeur2 largeur 
	 * @param hauteur2 hauteur 
	 * @return un escalier centré sur le centre en paramètre
	 */
	public static IGeometry createEscalier2(IDirectPosition centre, double largeur, double hauteur, double largeur1, double hauteur1, double largeur2, double hauteur2) {
		return new GM_Polygon(new GM_LineString(new DirectPositionList(Arrays.asList(
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2,centre.getY()-hauteur/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur/2-largeur2,centre.getY()-hauteur/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur/2-largeur2,centre.getY()+hauteur/2-hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur/2,centre.getY()+hauteur/2-hauteur2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur/2,centre.getY()+hauteur/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2+largeur1,centre.getY()+hauteur/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2+largeur1,centre.getY()-hauteur/2+hauteur1),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2,centre.getY()-hauteur/2+hauteur1),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2,centre.getY()-hauteur/2)))));
	}
	
	/**
	 * Crée une forme de cercle.
	 * @param centre centre de la forme
	 * @param rayon du cercle
	 * @param n nombre de segments par quart de cercle 
	 * @return un cercle centré sur le centre en paramètre
	 */
	public static IGeometry createCercle(IDirectPosition centre, double rayon, int n) {
		DirectPositionList liste = new DirectPositionList();
		double increment = Math.PI/(2*n);
		DirectPosition premierPoint = new DirectPosition(centre.getX()+rayon,centre.getY());
		liste.add(premierPoint);
		for ( int i = 1 ; i < 4*n ; i ++ ) {
			double angle = i*increment;
			DirectPosition point = new DirectPosition(centre.getX()+rayon*Math.cos(angle),centre.getY()+rayon*Math.sin(angle));
			liste.add(point);
		}
		liste.add(premierPoint);
		return new GM_Polygon(new GM_LineString(liste));
	}
	
	/**
	 * Crée un losange.
	 * @param centre centre du losange
	 * @param largeur largeur du losange
	 * @param hauteur hauteur du losange
	 * @return un losange centré sur le centre en paramètre
	 */
	public static IGeometry createLosange(IDirectPosition centre, double largeur, double hauteur) {
		return new GM_Polygon(new GM_LineString(new DirectPositionList(Arrays.asList(
		        (IDirectPosition) new DirectPosition(centre.getX(),centre.getY()+hauteur/2),
		        (IDirectPosition) new DirectPosition(centre.getX()-largeur/2,centre.getY()),
		        (IDirectPosition) new DirectPosition(centre.getX(),centre.getY()-hauteur/2),
		        (IDirectPosition) new DirectPosition(centre.getX()+largeur/2,centre.getY()),
		        (IDirectPosition) new DirectPosition(centre.getX(),centre.getY()+hauteur/2)))));
	}
	
	/**
	 * Crée une barre avec indentation.
	 * @param centre centre de la barre
	 * @param largeur largeur totale de la barre
	 * @param hauteur hauteur totale de la barre
	 * @param largeur1 largeur des indentations de la barre
	 * @param hauteur1 hauteur de la base de la barre
	 * @return une barre avec indentation centrée sur le centre en paramètre
	 */
	public static IGeometry createBarre(IDirectPosition centre, double largeur, double hauteur, double largeur1, double hauteur1) {
		
		double reste = largeur%largeur1;
		int nb = (int)((largeur-reste)/largeur1);
		DirectPositionList listePoints = new DirectPositionList();
		
		if (reste<0.0001) reste = 0;
		// le petit bout supplémentaire
		if (reste!=0){
			double positionY = 0;
			if (nb%2!=0){// on est en bas
				positionY = centre.getY()+((hauteur/2)-(hauteur-hauteur1));
			}else{// on est en haut
				positionY = centre.getY()+hauteur/2;
			}
			double positionX1 = centre.getX()+(largeur/2);
			double positionX2 = centre.getX()+(largeur/2)-reste;
			listePoints.add(new DirectPosition(positionX1,positionY));
			listePoints.add(new DirectPosition(positionX2,positionY));
		}
		// Les autres points
		int compt = 0;
		for (int i = nb;i>0;i--){
			double positionY = 0;
			if (i%2!=0){// segment impair on est en haut
				positionY = centre.getY()+hauteur/2;
			}else{// segment pair on est en bas
				positionY = centre.getY()+((hauteur/2)-(hauteur-hauteur1));
			}
			double positionX1 = centre.getX()+(largeur/2)-reste-(compt)*largeur1;
			double positionX2 = centre.getX()+(largeur/2)-reste-(compt+1)*largeur1;
			
			listePoints.add(new DirectPosition(positionX1,positionY));
			listePoints.add(new DirectPosition(positionX2,positionY));
			compt++;
		}
		// On ajoute les derniers points nécessaires
		listePoints.add(new DirectPosition(centre.getX()-largeur/2,centre.getY()-hauteur/2));
		listePoints.add(new DirectPosition(centre.getX()+largeur/2,centre.getY()-hauteur/2));
		listePoints.add(listePoints.get(0));
				
		return new GM_Polygon(new GM_LineString(listePoints));
	}
	
	/**
	 * Crée une barre en forme d'escalier.
	 * @param centre centre de la barre
	 * @param largeur largeur totale de la barre
	 * @param hauteur hauteur totale de la barre
	 * @param largeur1 largeur des barres verticales de la barre
	 * @param hauteur1 hauteur des barres horizontales de la barre
	 * @return une barre en forme d'escalier centrée sur le centre en paramètre
	 */
	public static IGeometry createBarreEscalier(IDirectPosition centre, double largeur, double hauteur, double largeur1, double hauteur1) {
		double reste = largeur%largeur1;
//		System.out.println("reste : "+reste);
		int nb = (int)((largeur-reste)/largeur1);
//		System.out.println("nb : "+nb);
		// Calcul de la hauteur des barres verticales
		int n = ((nb)/2);
		if ((nb%2!=0)&&(reste>0)){
			n = n+1;
		}
		double hauteurBarreV = (hauteur-((n+1)*hauteur1))/n;
//		System.out.println("hauteurBarreV : "+hauteurBarreV);
//		System.out.println(new DirectPosition(centre.getX()+largeur/2,centre.getY()+hauteur/2).toGM_Point());
		DirectPositionList listePoints = new DirectPositionList();
		// La partie inférieure
		int compt = 0;
		int compt2 = 0;
		for (int i = 1;i<nb+1;i++){
			if (i%2!=0){// bloc impair 
				double positionY = centre.getY()-(hauteur/2)+compt*(hauteurBarreV+hauteur1);
				double positionX = centre.getX()-(largeur/2)+(compt2)*largeur1;
				listePoints.add(new DirectPosition(positionX,positionY));
				compt2++;
			}else{ // bloc pair 
				double positionY = centre.getY()-(hauteur/2)+(compt)*(hauteurBarreV+hauteur1);
				double positionX1 = centre.getX()-(largeur/2)+(compt2+1)*largeur1;
				listePoints.add(new DirectPosition(positionX1,positionY));
				compt++;
				compt2++;
			}
		}
		// Le bout
		if (reste!=0){
			if (nb%2==0){// Nombre de bloc pair
				double positionY1 = centre.getY()-(hauteur/2)+compt*(hauteurBarreV+hauteur1);
				double positionY2 = centre.getY()+(hauteur/2);
				double positionX1 = centre.getX()-(largeur/2)+(compt2)*largeur1;
				double positionX2 = centre.getX()-(largeur/2)+(compt2)*largeur1+reste;
				listePoints.add(new DirectPosition(positionX1,positionY1));
				listePoints.add(new DirectPosition(positionX2,positionY1));
				listePoints.add(new DirectPosition(positionX2,positionY2));
			}else{// A rempplir
				double positionY1 = centre.getY()-(hauteur/2)+compt*(hauteurBarreV+hauteur1);
				double positionY2 = centre.getY()+(hauteur/2);
				double positionX1 = centre.getX()+(largeur/2);
				double positionX2 = centre.getX()+(largeur/2)-reste;
				listePoints.add(new DirectPosition(positionX1,positionY1));
				listePoints.add(new DirectPosition(positionX1,positionY2));
				listePoints.add(new DirectPosition(positionX2,positionY2));
			}
		}
		// La partie supérieure
		compt = 0;
		compt2 = 0;
		if ((nb%2!=0)&&(reste>0)){
			compt = 1;
		}
		for (int i = nb;i>0;i--){
			if (i%2==0){// bloc pair 
				double positionY = centre.getY()+(hauteur/2)-compt*(hauteurBarreV+hauteur1);
				double positionX = centre.getX()+(largeur/2)-reste-(compt2+1)*largeur1;
				listePoints.add(new DirectPosition(positionX,positionY));
				compt++;
				compt2++;
			}else{// bloc impair
				double positionY = centre.getY()+(hauteur/2)-compt*(hauteurBarreV+hauteur1);
				double positionX = centre.getX()+(largeur/2)-reste-(compt2)*largeur1;
				listePoints.add(new DirectPosition(positionX,positionY));
				compt2++;
			}
		}
//		for (IDirectPosition point:listePoints){
//			System.out.println(point.toGM_Point());
//		}
		// On ajoute les derniers points nécessaires
		listePoints.add(new DirectPosition(centre.getX()-largeur/2,centre.getY()-hauteur/2+hauteur1));
		listePoints.add(listePoints.get(0));		
		return new GM_Polygon(new GM_LineString(listePoints));
	}
}

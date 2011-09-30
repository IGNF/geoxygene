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
package fr.ign.cogit.geoxygene.util.algo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * 
 * Cette classe contient des fonction géométriques courantes.
 * Elle fonctionne sur des géométries JTS.
 * 
 * @author Julien Gaffuri
 * @author Julien Perret
 * 
 */
public class JtsUtil {
	static Logger logger=Logger.getLogger(JtsUtil.class.getName());
	static JtsUtil singleton = new JtsUtil();
	//static GeometryFactory geometryFactory = new GeometryFactory();

	protected static EventListenerList listenerList = new EventListenerList();

	/**
	 * Adds an <code>ActionListener</code>.
	 * @param l the <code>ActionListener</code> to be added
	 */
	public static void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created.
	 * @see EventListenerList
	 */
	protected static void fireActionPerformed(ActionEvent event) {
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) {
			if (listeners[i]==ActionListener.class) {
				// Lazily create the event:
				((ActionListener)listeners[i+1]).actionPerformed(event);
			}
		}
	}

	/**
	 * Détermine la liste des géométries appartenant à la liste elements qui intersectent le point point.
	 * @param point un point
	 * @param elements liste de géométries
	 * @return liste des géométries de la liste elements qui intersectent le point.
	 */
	public static List<Geometry> getListeGeometriesIntersectant(Point point,List<Geometry> elements) {
		List<Geometry> resultat = new ArrayList<Geometry>();
		for (Geometry g : elements) if (g.intersects(point)) {
			resultat.add(g);
		}
		return resultat;
	}

	/**
	 * Détermine la liste des géométries appartenant à la liste elements qui intersectent la géométrie geom.
	 * @param geom une géométrie
	 * @param elements liste de géométries
	 * @return liste des géométries de la liste elements qui intersectent geom.
	 */
	public static List<Geometry> getListeGeometriesIntersectant(Geometry geom,List<Geometry> elements) {
		List<Geometry> resultat = new ArrayList<Geometry>();
		for (Geometry g : elements) if (g.intersects(geom)) {
			Geometry inter = g.intersection(geom);
			if (!(inter.isEmpty() || (inter instanceof Point) || (inter instanceof MultiPoint)))
				resultat.add(g);
		}
		return resultat;
	}

	/**
	 * Calcul de la fermeture d'un polygone.
	 * @param polygon polygone de départ
	 * @param distance distance utilisée pour le buffer positif puis pour le buffer négatif
	 * @param distanceTolerance distance utilisée pour la simplification par l'algorithme de Douglas-Peucker
	 * @param quadrantSegments nombre de segments utilisés pour la simplification par l'algorithme de Douglas-Peucker
	 * @param endCapStyle type d'approximation utilisée pour la simplification par l'algorithme de Douglas-Peucker
	 * @param factory factory pour la géométrie
	 * @return la fermeture du polygone passé en paramètre
	 */
	public static Polygon fermeture(Polygon polygon,double distance,double distanceTolerance,int quadrantSegments,int endCapStyle,GeometryFactory factory) {
		LinearRing exterior = (LinearRing) polygon.getExteriorRing();
		Geometry boundary = factory.createPolygon(exterior, null);
		Polygon result = null;
		try{
			boundary = boundary.buffer(distance,quadrantSegments,endCapStyle);
			boundary = boundary.buffer(-distance,quadrantSegments,endCapStyle);
			if (boundary.isEmpty()) return polygon;
			result = (Polygon)JtsAlgorithms.filtreDouglasPeucker(boundary,distanceTolerance);
		} catch (Exception e) {
			logger.error(polygon.toText());
			logger.error(boundary.toText());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Calcul de la fermeture d'une géométrie quelconque.
	 * @param geometry géométrie de départ
	 * @param distance distance utilisée pour le buffer positif puis pour le buffer négatif
	 * @param quadrantSegments nombre de segments utilisés pour la simplification par l'algorithme de Douglas-Peucker
	 * @param endCapStyle type d'approximation utilisée pour la simplification par l'algorithme de Douglas-Peucker
	 * @return la fermeture ++ de la géométrie passée en paramètre
	 */
	public static Geometry fermetureSuppTrous(Geometry geometry,double distance, int quadrantSegments, int endCapStyle) {
		//fermeture
		Geometry geom = JtsAlgorithms.fermeture(geometry, distance, quadrantSegments, endCapStyle );

		//suppression trous
		if (geom instanceof Polygon) return JtsAlgorithms.supprimeTrous((Polygon)geom).buffer(0);
		if (geom instanceof MultiPolygon) return JtsAlgorithms.supprimeTrous((MultiPolygon)geom).buffer(0);

		logger.error("geometrie de type non géré "+geom.getGeometryType());
		return null;
	}

	/**
	 * Calcul de l'affinité vectorielle.
	 * 
	 * @param geom une géométrie, a geometry
	 * @param c coordonnées d'un point par lequel passe l'axe de l'affinité
	 * @param angle angle de la direction de l'affinite, à partir de l'axe des x
	 * @param coef coefficient de l'homothétie
	 * @return polygone polygone résultant de l'application de l'affinité, resulting polygon
	 */
	public static Polygon affinite(Polygon geom, Coordinate c, double angle, double coef){
		//pivote le polygone
		Polygon rot=rotation(geom, c, -1.0*angle);

		//le contour externe
		Coordinate[] coord=rot.getExteriorRing().getCoordinates();
		Coordinate[] coord_=new Coordinate[coord.length];
		for(int i=0;i<coord.length;i++) coord_[i]=new Coordinate(c.x+coef*(coord[i].x-c.x), coord[i].y);
		LinearRing lr=geom.getFactory().createLinearRing(coord_);

		//les trous
		LinearRing[] trous=new LinearRing[rot.getNumInteriorRing()];
		for(int j=0;j<rot.getNumInteriorRing();j++){
			Coordinate[] hole_coord=rot.getInteriorRingN(j).getCoordinates();
			Coordinate[] hole_coord_=new Coordinate[hole_coord.length];
			for(int i=0;i<hole_coord.length;i++) hole_coord_[i]=new Coordinate(c.x+coef*(hole_coord[i].x-c.x), coord[i].y);
			trous[j]=geom.getFactory().createLinearRing(hole_coord_);
		}
		return rotation(geom.getFactory().createPolygon(lr,trous), c, angle);
	}


	/**
	 * Calcul de l'affinité vectorielle.
	 * Cette Méthode utilise la Méthode prenant comme paramètre supplèmentaire les coordonnées d'un point par lequel passe l'axe de l'affinité.
	 * Ici, on utilise le centroïde de la géométrie.
	 * @see JtsUtil#affinite(Polygon, Coordinate, double, double)
	 * 
	 * @param geom une géométrie, a geometry
	 * @param angle angle de la direction de l'affinite, à partir de l'axe des x
	 * @param scale coefficient de l'homothétie
	 * @return polygone résultant de l'application de l'affinité, resulting polygon
	 */
	public static Polygon affinite(Polygon geom, double angle, double scale){
		return affinite(geom, geom.getCentroid().getCoordinate(), angle, scale);
	}



	/**
	 * Plus Petit Rectangle Englobant d'une géométrie préservant son aire.
	 * Smallest Enclosing Rectangle of a geometry preserving its area.
	 * 
	 * @param geom une géométrie, a geometry
	 * @return le Plus Petit Rectangle Englobant, the Smallest Enclosing Rectangle
	 */
	public static Polygon PPREAirePreservee(Geometry geom){
		Polygon out=PPRE(geom);
		return homothetie(out, (float) Math.sqrt(geom.getArea()/out.getArea()));
	}


	/**
	 * Plus Petit Rectangle Englobant d'une géométrie respectant un aire donnée.
	 * Smallest Enclosing Rectangle of a geometry with a given area.
	 * 
	 * @param geom une géométrie, a geometry
	 * @param aireCible aire visée, target area
	 * @return le Plus Petit Rectangle Englobant, the Smallest Enclosing Rectangle
	 */
	public static Polygon PPREAireCible(Geometry geom, double aireCible){
		Polygon out=PPRE(geom);
		return homothetie(out, (float) Math.sqrt(aireCible/out.getArea()));
	}



	/**
	 * Plus Petit Rectangle Englobant d'une géométrie.
	 * Smallest Enclosing Rectangle of a geometry.
	 * 
	 * @param geom une géométrie, a geometry
	 * @return le Plus Petit Rectangle Englobant, the Smallest Enclosing Rectangle
	 */
	public static Polygon PPRE(Geometry geom){
		//recupere l'enveloppe convexe
		Geometry convexHull=geom.convexHull();
		//si ce n'est pas un polygone, le MBR n'est pas defini: on revoit null
		if (!(convexHull instanceof Polygon)) {
			logger.error("Le PPRE calculé n'est pas un polygone. Son type est "+convexHull.getGeometryType());
			return null;
		}
		Polygon env=(Polygon)convexHull;
		//prend les coordonnes de l'enveloppe convexe
		Coordinate[] coord=env.getExteriorRing().getCoordinates();
		Coordinate centre=geom.getCentroid().getCoordinate();
		//parcours les segments
		double aire_min=Double.MAX_VALUE, angle_=0.0;
		Polygon ppre=null;
		for(int i=0;i<coord.length-1;i++){
			//calcul de la rotation de l'enveloppe convexe
			double angle=Math.atan2(coord[i+1].y-coord[i].y, coord[i+1].x-coord[i].x);
			try {
			Polygon rot=(Polygon)rotation(env, centre, -1.0*angle).getEnvelope();
			//calcul l'aire de l'enveloppe rectangulaire
			double aire=rot.getArea();
			//verifie si elle est minimum
			if (aire<aire_min) {aire_min=aire; ppre=rot; angle_=angle; }
			} catch (ClassCastException e) {
			    logger.error(geom);
			    logger.error(env);
			    logger.error(rotation(env, centre, -1.0*angle).getEnvelope());
			}
		}
		return rotation(ppre, centre, angle_);
	}




	/**
	 * Calcule l'homothétie d'une géométrie.
	 * @param geom géométrie, geometry
	 * @param x0 position en X du centre de l'homothétie, X position of the center of the operation
	 * @param y0 position en Y du centre de l'homothétie, Y position of the center of the operation
	 * @param scale facteur d'échelle, scale factor
	 * @return polygon résultant de l'homothétie, resulting polygon
	 */
	public static Polygon homothetie(Polygon geom, double x0, double y0, double scale){
		//le contour externe
		Coordinate[] coord=geom.getExteriorRing().getCoordinates();
		Coordinate[] coord_=new Coordinate[coord.length];
		for(int i=0;i<coord.length;i++) coord_[i]=new Coordinate(x0+scale*(coord[i].x-x0), y0+scale*(coord[i].y-y0));
		LinearRing lr=geom.getFactory().createLinearRing(coord_);

		//les trous
		LinearRing[] trous=new LinearRing[geom.getNumInteriorRing()];
		for(int j=0;j<geom.getNumInteriorRing();j++){
			Coordinate[] hole_coord=geom.getInteriorRingN(j).getCoordinates();
			Coordinate[] hole_coord_=new Coordinate[hole_coord.length];
			for(int i=0;i<hole_coord.length;i++) hole_coord_[i]=new Coordinate(x0+scale*(hole_coord[i].x-x0), y0+scale*(hole_coord[i].y-y0));
			trous[j]=geom.getFactory().createLinearRing(hole_coord_);
		}
		return geom.getFactory().createPolygon(lr,trous);
	}

	   /**
     * Calcule l'homothétie d'une géométrie.
     * @param geom géométrie, geometry
     * @param x0 position en X du centre de l'homothétie, X position of the center of the operation
     * @param y0 position en Y du centre de l'homothétie, Y position of the center of the operation
     * @param scaleX facteur d'échelle en X, X scale factor
     * @param scaleY facteur d'échelle en Y, Y scale factor
     * @return polygon résultant de l'homothétie, resulting polygon
     */
    public static Polygon homothetie(Polygon geom, double x0, double y0, double scaleX, double scaleY){
        //le contour externe
        Coordinate[] coord=geom.getExteriorRing().getCoordinates();
        Coordinate[] coord_=new Coordinate[coord.length];
        for(int i=0;i<coord.length;i++) coord_[i]=new Coordinate(x0+scaleX*(coord[i].x-x0), y0+scaleY*(coord[i].y-y0));
        LinearRing lr=geom.getFactory().createLinearRing(coord_);

        //les trous
        LinearRing[] trous=new LinearRing[geom.getNumInteriorRing()];
        for(int j=0;j<geom.getNumInteriorRing();j++){
            Coordinate[] hole_coord=geom.getInteriorRingN(j).getCoordinates();
            Coordinate[] hole_coord_=new Coordinate[hole_coord.length];
            for(int i=0;i<hole_coord.length;i++) hole_coord_[i]=new Coordinate(x0+scaleY*(hole_coord[i].x-x0), y0+scaleY*(hole_coord[i].y-y0));
            trous[j]=geom.getFactory().createLinearRing(hole_coord_);
        }
        return geom.getFactory().createPolygon(lr,trous);
    }

	/**
	 * Calcule l'homothétie d'une géométrie.
	 * @param geom géométrie, geometry
	 * @param scaleX facteur d'échelle en X, X scale factor
     * @param scaleY facteur d'échelle en Y, Y scale factor
	 * @return polygon résultant de l'homothétie, resulting polygon
	 */
	public static Polygon homothetie(Polygon geom, double scaleX, double scaleY){
		return homothetie(geom, geom.getCentroid().getX(), geom.getCentroid().getY(), scaleX, scaleY);
	}

    /**
     * Calcule l'homothétie d'une géométrie.
     * @param geom géométrie, geometry
     * @param scale facteur d'échelle, scale factor
     * @return polygon résultant de l'homothétie, resulting polygon
     */
    public static Polygon homothetie(Polygon geom, double scale){
        return homothetie(geom, geom.getCentroid().getX(), geom.getCentroid().getY(), scale);
    }


	/**
	 * Translate une géométrie.
	 * Translate a geometry.
	 * 
	 * @param geom une géométrie, a geometry
	 * @param dx translation suivant l'axe des x, translation along the X axis
	 * @param dy translation suivant l'axe des Y, translation along the Y axis
	 * @return polygone résultant de la translation, resulting polygon.
	 */
	public static Polygon translation(Polygon geom, double dx, double dy){
		GeometryFactory gf=new GeometryFactory();

		//le contour externe
		Coordinate[] coord=geom.getExteriorRing().getCoordinates();
		Coordinate[] coord_=new Coordinate[coord.length];
		for(int i=0;i<coord.length;i++) coord_[i]=new Coordinate(coord[i].x+dx, coord[i].y+dy);
		LinearRing lr=gf.createLinearRing(coord_);

		//les trous
		LinearRing[] trous=new LinearRing[geom.getNumInteriorRing()];
		for(int j=0;j<geom.getNumInteriorRing();j++){
			Coordinate[] hole_coord=geom.getInteriorRingN(j).getCoordinates();
			Coordinate[] hole_coord_=new Coordinate[hole_coord.length];
			for(int i=0;i<hole_coord.length;i++) hole_coord_[i]=new Coordinate(hole_coord[i].x+dx, hole_coord[i].y+dy);
			trous[j]=gf.createLinearRing(hole_coord_);
		}
		return gf.createPolygon(lr,trous);
	}


	/**
	 * Effectue une rotation sur une géométrie.
	 * Rotate a geometry.
	 * 
	 * @param geom une géométrie, a geometry
	 * @param c centre de la rotation, center of the rotation
	 * @param angle angle de rotation, angle of rotation
	 * @return polygone résultant de la rotation, resulting polygon.
	 */

	public static Polygon rotation(Polygon geom, Coordinate c, double angle){
		double cos=Math.cos(angle), sin=Math.sin(angle);
		//rotation de l'enveloppe
		Coordinate[] coord=geom.getExteriorRing().getCoordinates();
		Coordinate[] coord_=new Coordinate[coord.length];
		for(int i=0;i<coord.length;i++){
			double x=coord[i].x, y=coord[i].y;
			coord_[i]=new Coordinate(c.x+cos*(x-c.x)-sin*(y-c.y), c.y+sin*(x-c.x)+cos*(y-c.y));
		}
		LinearRing shell=geom.getFactory().createLinearRing(coord_);

		//rotation des trous
		LinearRing[] trous=new LinearRing[geom.getNumInteriorRing()];
		for(int j=0;j<geom.getNumInteriorRing();j++){
			Coordinate[] coord2=geom.getInteriorRingN(j).getCoordinates();
			Coordinate[] coord2_=new Coordinate[coord2.length];
			for(int i=0;i<coord2.length;i++){
				double x=coord2[i].x, y=coord2[i].y;
				coord2_[i]=new Coordinate(c.x+cos*(x-c.x)-sin*(y-c.y), c.y+sin*(x-c.x)+cos*(y-c.y));
			}
			trous[j]=geom.getFactory().createLinearRing(coord2);
		}
		return geom.getFactory().createPolygon(shell, trous);
	}

	/**
	 * Effectue une rotation sur une géométrie autour de son centroide.
	 * Rotate a geometry around its centroid.
	 * 
	 * @param geom une géométrie, a geometry
	 * @param angle angle de rotation, angle of rotation
	 * @return polygone résultant de la rotation, resulting polygon.
	 */
	public static Polygon rotation(Polygon geom, double angle){
		return rotation(geom, geom.getCentroid().getCoordinate(), angle);
	}

	/**
	 * Calcul de l'élongation d'une géométrie.
	 * L'elongation est un reel entre 0 et 1.
	 * <ul>
	 * <li> 1: carré,
	 * <li> plus proche de 0: tend vers un segment
	 * </ul>
	 * C'est le quotient de la largeur et de la longueur du PPRE.
	 * Par convention, le résultat vaut -999.9 si le ppre n'existe pas (géométrie nulle ou vide par exemple)
	 * @param geom  une géométrie, a geometry
	 * @return l'élongation du polygone passé en paramètre, -999.9 si la géométrie est nulle ou vide.
	 */
	public static double elongation(Geometry geom){
		Polygon ppre=PPRE(geom);
		if (ppre==null) return -999.9;
		Coordinate[] coords=ppre.getCoordinates();
		double lg1=coords[0].distance(coords[1]);
		double lg2=coords[1].distance(coords[2]);
		if (lg1>lg2) return lg2/lg1;
		return lg1/lg2;
	}

	/**
	 * Orientation d'une ligne (en radian, dans l'intervalle [0, Pi[, par rapport a l'axe Ox)
	 * @param pt1 et pt2 les DirectPosition des points Définissant la ligne
	 * @return l'orientation de la ligne
	 */
	public static double orientationLigne(IDirectPosition pt1,IDirectPosition pt2){
        double angle = Math.atan((pt1.getY() - pt2.getY())
                / (pt1.getX() - pt2.getX()));
        if (angle < 0) angle += Math.PI;
        return angle;
	}
	
	/**
	 * Projection d'un point(Directposition) sur une GM_LineString et retour de l'orientation du segment ayant reçu la projection (en radian, dans l'intervalle [0, Pi[, par rapport a l'axe Ox).
	 * @param directPosition un point 
	 * @param geometrieTroncon une géométrie
	 * @return l'orientation du segment (en radian, dans l'intervalle [0, Pi[, par rapport a l'axe Ox)
	 */
	public static double projectionPointOrientationTroncon(IDirectPosition directPosition,ILineString geometrieTroncon) {
		IDirectPositionList listePoints = geometrieTroncon.coord();
		double distance , distanceMin;
		double orientationSegment = 999.9;

		if (listePoints.size() == 0) return 999.9;
		IDirectPosition pointProj = Operateurs.projection(directPosition,listePoints.get(0),listePoints.get(1));
		distanceMin = directPosition.distance(pointProj);
		orientationSegment = JtsUtil.orientationLigne(listePoints.get(0), listePoints.get(1));
		for (int i=1;i<listePoints.size()-1;i++) {
		    IDirectPosition point = Operateurs.projection(directPosition,listePoints.get(i),listePoints.get(i+1));
			distance = directPosition.distance(point);
			if ( distance < distanceMin ) {
				pointProj = point;
				distanceMin = distance;
				orientationSegment = JtsUtil.orientationLigne(listePoints.get(i), listePoints.get(i+1));
			}
		}

		// Si le point projeté tombe sur un noeud : Calcul de l'orientation du plus grand segment jouxtant ce noeud
		for (int k=0;k<listePoints.size();k++){
			if (pointProj.equals(listePoints.get(k))) {
				if (k==0){
					orientationSegment = JtsUtil.orientationLigne(listePoints.get(k), listePoints.get(k+1));}
				else if (k==listePoints.size()-1){
					orientationSegment = JtsUtil.orientationLigne(listePoints.get(k-1), listePoints.get(k));}
				else{
					double longueur1 = listePoints.get(k-1).distance(listePoints.get(k));
					double longueur2 = listePoints.get(k).distance(listePoints.get(k+1));
					if (longueur1>longueur2){
						orientationSegment = JtsUtil.orientationLigne(listePoints.get(k-1), listePoints.get(k));}
					else{
						orientationSegment = JtsUtil.orientationLigne(listePoints.get(k), listePoints.get(k+1));
					}
				}
			}
		}
		
		return orientationSegment;
	}
	
	
	/**
	 * Calcul de la convexité d'un polygone. La convexité est Définie comme le rapport de l'aire du polygone par l'aire de son enveloppe convexe.
	 * La convexité prends sa valeur entre 0 (peu convexe) et 1 (parfaitement convexe).
	 * La convexité vaut -999.9 si elle n'est pas definie (quand l'enveloppe convexe est d'aire nulle)
	 * @param geom une géométrie, a geometry
	 * @return convexité du polygone donné en paramètre - prend sa valeur dans ]0,1] ou -999.9 si elle n'est pas definie
	 */
	public static double convexite(Geometry geom){
		if (geom.getNumGeometries()==0) return -999.9;
		double aireC=geom.convexHull().getArea();
		if (aireC==0.0) return -999.9;
		return geom.getArea()/aireC;
	}

	/**
	 * Calcule la longueur du plus petit cété d'un polygone.
	 * 
	 * @param poly un polygone, a polygon
	 * @return longueur du plus petit cété d'un polygone
	 */
	public static double plusPetitCote(Polygon poly){
		double ppc=Double.MAX_VALUE;
		double lg;
		Coordinate[] coord;

		//l'enveloppe
		coord=poly.getExteriorRing().getCoordinates();
		for(int i=0;i<coord.length-1;i++){
			lg=coord[i].distance(coord[i+1]);
			if (lg<ppc) ppc=lg;
		}

		//les trous
		for(int j=0;j<poly.getNumInteriorRing();j++){
			coord=poly.getInteriorRingN(j).getCoordinates();
			for(int i=0;i<coord.length-1;i++){
				lg=coord[i].distance(coord[i+1]);
				if (lg<ppc) ppc=lg;
			}
		}
		return ppc;
	}

	/**
	 * Calcule l'indice de circularité de Miller pour un polygone.
	 * Cet indice, compris entre 0 et 1, vaut 1 si le polygone est parfaitement circulaire et tend vers 0 pour un segment.
	 * 
	 * Cet indice n'est qu'un avatar de l'indice de compacité de Gravelius K=Perimètre / (2*sqrt(PI*Aire)).
	 * On vérifie en effet simplement que K=1/sqrt(C) avec C l'indice de circularité.
	 * @param poly
	 * @return indice de circularité compris entre 0 et 1. Il vaut  1 si le polygone est un cercle parfait.
	 */
	public static double circularite(Polygon poly) {
		return 4*Math.PI*poly.getArea()/(poly.getBoundary().getLength()*poly.getBoundary().getLength());
	}

	public static IPolygon convexHull(IFeatureCollection<?> collection) {
		//recupere l'enveloppe convexe
		Geometry geom;
		try {
			geom = AdapterFactory.toGeometry(new GeometryFactory(), collection.getGeomAggregate());
			Geometry convexHull=geom.convexHull();
			return (IPolygon) AdapterFactory.toGM_Object(convexHull);
		} catch (Exception e) {
			if (logger.isDebugEnabled()) {
				logger.warn("Echec du calcul de l'envelope convexe "+e.getMessage());
				logger.warn("collection = "+collection);
				logger.warn("avec une géométrie = "+collection.getGeomAggregate());
			}
		}
		return null;
	}
	
	/**
	 * fermeture sur des features.
	 * @param collection
	 * @param seuilBuffer
	 * @return
	 * TODO la fermeture pose problème : à modifier !
	 */
	public static IGeometry fermeture(Collection<? extends IFeature> collection, double seuilBuffer) {
		List<IGeometry> listeBuffers = new ArrayList<IGeometry>(0);
		for(IFeature feature:collection) {listeBuffers.add(feature.getGeom().buffer(seuilBuffer));}
        IGeometry buffer = JtsAlgorithms.union(listeBuffers);
		return buffer.buffer(-seuilBuffer);
	}
	
	/**
	 * buffer sur des features.
	 * @param collection
	 * @param seuilBuffer
	 * @return
	 */
	public static IGeometry bufferPolygones(Collection<? extends IFeature> collection, double seuilBuffer) {
		List<IGeometry> listeBuffers = new ArrayList<IGeometry>(0);
		for(IFeature feature:collection) {listeBuffers.add(feature.getGeom().buffer(seuilBuffer));}
		IGeometry buffer = JtsAlgorithms.union(listeBuffers);
		return buffer;
	}
}



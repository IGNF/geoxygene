/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/

package fr.ign.cogit.geoxygene.contrib.algorithms;


import java.util.ArrayList;
import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.triangulate.ConformingDelaunayTriangulationBuilder;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

// ======================================================================================
// Classe de calcul de la triangulation sur un polygone									|
// Utilisation de la triangulation contrainte de Delaunay								|
// --------------------------------------------------------------------------------------
// ALGORITHMES																			|
// --------------------------------------------------------------------------------------
// TRIANGULATE : triangulation simple sans ajouter de vertices							|
// --------------------------------------------------------------------------------------
// MAKECONVEX : partitionnement en ensembles convexes (majoritairement à base de		|
// triangles (triangulation jusqu'à ce que la surface résultante soit convexe).			|
// --------------------------------------------------------------------------------------
// GENERATEMESH : triangulations contraintes avec JTS									|
// Paramètres à fixer avant de lancer la génération de mesh								|
// - TOLERANCE : tolérance d'intersection des calculs d'aires avec JTS					|
//   (problèmes d'imprécisions numériques si fixée à 0)									|
// - OVERSAMPLING : false -> vertices pris uniquement au niveau des sommets du 			|
//   polygone en entrée, true -> sur-échantillonnage du nombre de vertices suivant 		|
//   les paramètres REGULAR_GRID et SAMPLING_RESOLUTION.								|
// - REGULAR_GRID ->  sur-échantillonnage par une grille régulière de résolution 		|
//   égale à SAMPLING_RESOLUTION, RANDOM_GRID -> répartition aléatoire de résolution 	|
//   moyenne égale à SAMPLING_RESOLUTION,  ADAPTIVE_GRID -> sur-échantillonnage au      |
//   niveau des bords du polygone.														|
// --------------------------------------------------------------------------------------
// Date : 05/11/2014																	|
// ======================================================================================
public class PolygonMeshBuilder {

	// Modes de génération de grilles
	public static int REGULAR_GRID = 1;
	public static int RANDOM_GRID = 2;
	public static int ADAPTIVE_GRID = 3;

	// Facteur de tolérance (entre 0 et 1)
	private static double tolerance = 0.001;

	// Sur-échantillonnage du nombre de vertices
	private static boolean oversampling = false;

	// Régularité du sur-échantillonnage
	private static int gridGeneration = REGULAR_GRID;

	// Résolution du sur-échantillonnage
	private static double samplingResolution = 1;

	// Mode verbeux
	private static boolean verbose = false;

	// -----------------------------------------------------------------------------

	public static void setTolerance(double t){tolerance = t;}
	public static void setOverSampling(boolean os){oversampling = os;}
	public static void setGridGeneration(int gg){gridGeneration = gg;}
	public static void setSamplingResolution(double sr){samplingResolution = sr;}
	public static void setVerbose(boolean v){verbose = true;}

	// -----------------------------------------------------------------------------
	// Fonction de triangulation contrainte d'un polygone
	// Entrée : géométrie
	// Sortie : multi-géométrie
	// -----------------------------------------------------------------------------
	@SuppressWarnings({ "deprecation"})
	public static IMultiSurface<GM_Polygon> generateMesh(IGeometry polygon){
		
		ConformingDelaunayTriangulationBuilder triangulator = new ConformingDelaunayTriangulationBuilder();

		GeometryFactory fact = new GeometryFactory();


		ArrayList<Coordinate> POINTSUP = new ArrayList<Coordinate>();


		// Densification éventuelle
		if (oversampling){

			POINTSUP = overSample(polygon);

		}


		Coordinate coordinates[] = new Coordinate[((IPolygon)polygon).getExterior().coord().size()];

		//  Creation d'un polygone pour les vertices de la triangulation
		for (int i=0; i<((IPolygon)polygon).getExterior().coord().size(); i++){

			coordinates[i] = new Coordinate(((IPolygon)polygon).getExterior().coord().get(i).getX(), ((IPolygon)polygon).getExterior().coord().get(i).getY());

		}

		// List of all points
		Point points[] = new Point[coordinates.length-1+POINTSUP.size()];

		for (int i=0; i<coordinates.length-1; i++){points[i] = new Point(coordinates[i], new PrecisionModel(), 0);}
		for (int i=0; i<POINTSUP.size(); i++){points[i+coordinates.length-1] = new Point(POINTSUP.get(i), new PrecisionModel(), 0);}


		MultiPoint mp = new MultiPoint(points, new GeometryFactory());


		LinearRing linear = new GeometryFactory().createLinearRing(coordinates);

		// Trous dans la géométrie du polygone
		List<IRing> RINGS = ((IPolygon)polygon).getInterior();
		LinearRing linearHoles[] = new LinearRing[RINGS.size()];

		for (int i=0; i<linearHoles.length; i++){

			Coordinate[] coordsHoles = new Coordinate[RINGS.get(i).numPoints()];

			for (int j=0; j<RINGS.get(i).numPoints(); j++){

				coordsHoles[j] = new Coordinate(RINGS.get(i).coord().get(j).getX(), RINGS.get(i).coord().get(j).getY());

			}

			linearHoles[i] = new GeometryFactory().createLinearRing(coordsHoles);

		}

		Polygon poly = new Polygon(linear, linearHoles, fact);

		triangulator.setSites(mp);

		// Segment constraints
		triangulator.setConstraints(poly);


		Geometry triangles = triangulator.getTriangles(new GeometryFactory());

		GM_MultiSurface<GM_Polygon> ms = new GM_MultiSurface<GM_Polygon>();

		// ---------------------------------------------------------------------------
		// Conversion en géométries Geoxygene
		// ---------------------------------------------------------------------------s
		for (int i=0; i<triangles.getNumGeometries(); i++){

			DirectPositionList list = new DirectPositionList();

			for (int j=0; j<triangles.getGeometryN(i).getNumPoints(); j++){

				list.add(new DirectPosition(triangles.getGeometryN(i).getCoordinates()[j].x, triangles.getGeometryN(i).getCoordinates()[j].y));

			}

			GM_LineString lsGeoxygene = new GM_LineString(list);

			GM_Polygon triangle = new GM_Polygon(lsGeoxygene);

			// ---------------------------------------------------------------------------
			// Test d'inclusion
			// ---------------------------------------------------------------------------
			if(polygon.intersection(triangle).area() > (1-tolerance)*triangle.area()){ms.add(triangle);}

		}


		if (verbose){
			System.out.println("------------------------");
			System.out.println("Triangulation results : ");
			System.out.println("------------------------");
			System.out.println("Initial number of vertices : "+(polygon.coord().size()-1));
			System.out.println("Final number of vertices : "+(mp.getNumPoints()));
			System.out.println("Triangle number : "+ms.size());
		}

		return ms;

	}


	// -----------------------------------------------------------------------------
	// Fonction de sur-échantillonnage du nombre de vertices
	// Entrée : géométrie du polygone
	// Sortie : liste de coordonnées
	// -----------------------------------------------------------------------------
	private static ArrayList<Coordinate> overSample(IGeometry polygon){

		ArrayList<Coordinate> POINTSUP = new ArrayList<Coordinate>();


		// Création d'une bounding box
		GM_Polygon bbox =  (GM_Polygon) polygon.mbRegion();

		// Récupération des tailles
		double xmin = Double.MAX_VALUE;
		double ymin = Double.MAX_VALUE;
		double xmax = Double.MIN_VALUE;
		double ymax = Double.MIN_VALUE;

		for (int i=0; i<bbox.coord().size()-1; i++){

			if (bbox.coord().get(i).getX() < xmin){xmin = bbox.coord().get(i).getX();}
			if (bbox.coord().get(i).getY() < ymin){ymin = bbox.coord().get(i).getY();}
			if (bbox.coord().get(i).getX() > xmax){xmax = bbox.coord().get(i).getX();}
			if (bbox.coord().get(i).getY() > ymax){ymax = bbox.coord().get(i).getY();}

		}

		double lx = xmax-xmin;
		double ly = ymax-ymin;

		// ---------------------------------------------------------------------------
		// Si la grille est régulière
		// ---------------------------------------------------------------------------

		if (gridGeneration == REGULAR_GRID){

			POINTSUP = generateRegularGrid(polygon, samplingResolution, xmin, xmax, ymin, ymax);

		}
		// ---------------------------------------------------------------------------
		// Si la grille est aléatoire
		// ---------------------------------------------------------------------------
		if (gridGeneration == RANDOM_GRID){

			double aire = polygon.area();
			double aire_bbox = lx*ly;

			int nbPoints = (int)((lx/samplingResolution)*(ly/samplingResolution)*aire/aire_bbox);

			while(POINTSUP.size() < nbPoints){

				// Génération aléatoire (loi uniforme)
				double x = xmin + lx*Math.random();
				double y = ymin + ly*Math.random();

				// Création d'un point
				DirectPosition pointSup = new DirectPosition(x, y);

				// Test d'inclusion
				if (!polygon.contains(new GM_Point(pointSup))){continue;}

				// Ajout du point supplémentaire
				POINTSUP.add(new Coordinate(pointSup.getX(), pointSup.getY()));

			}

		}

		// ---------------------------------------------------------------------------
		// Si la grille est densifiée sur les bords
		// ---------------------------------------------------------------------------
		if (gridGeneration == ADAPTIVE_GRID){

			// Recherche de la valeur de distance moyenne
			POINTSUP = generateRegularGrid(polygon, samplingResolution, xmin, xmax, ymin, ymax);

			double mean = 0;

			for (int i=0; i<POINTSUP.size(); i++){

				GM_Point point_courant = new GM_Point(new DirectPosition(POINTSUP.get(i).x, POINTSUP.get(i).y));

				mean += point_courant.distance(((IPolygon)polygon).getExterior());

			}

			mean /= POINTSUP.size();

			POINTSUP.clear();

			// Définition de 3 niveaux
			double niv2 = mean;
			double niv3 = 0.35*mean;

			// Niveau 1
			ArrayList<Coordinate> POINTSUPNIV1 = generateRegularGrid(polygon, 1.5*samplingResolution, xmin, xmax, ymin, ymax);

			// Niveau 2
			ArrayList<Coordinate> POINTSUPNIV2 = generateRegularGrid(polygon, samplingResolution, xmin, xmax, ymin, ymax);

			// Niveau 3
			ArrayList<Coordinate> POINTSUPNIV3 = generateRegularGrid(polygon, 0.5*samplingResolution, xmin, xmax, ymin, ymax);



			// Superposition des niveaux
			for (int i=0; i<POINTSUPNIV1.size(); i++){

				GM_Point point_courant = new GM_Point(new DirectPosition(POINTSUPNIV1.get(i).x, POINTSUPNIV1.get(i).y));

				if (distanceToBoundary(point_courant, (GM_Polygon)polygon) < niv2){continue;}

				POINTSUP.add(POINTSUPNIV1.get(i));

			}



			for (int i=0; i<POINTSUPNIV2.size(); i++){

				GM_Point point_courant = new GM_Point(new DirectPosition(POINTSUPNIV2.get(i).x, POINTSUPNIV2.get(i).y));

				if (distanceToBoundary(point_courant, (GM_Polygon)polygon) > niv2){continue;}
				if (distanceToBoundary(point_courant, (GM_Polygon)polygon) < niv3){continue;}

				POINTSUP.add(POINTSUPNIV2.get(i));

			}


			for (int i=0; i<POINTSUPNIV3.size(); i++){

				GM_Point point_courant = new GM_Point(new DirectPosition(POINTSUPNIV3.get(i).x, POINTSUPNIV3.get(i).y));

				if (distanceToBoundary(point_courant, (GM_Polygon)polygon) > niv3){continue;}

				POINTSUP.add(POINTSUPNIV3.get(i));

			}


		}

		return POINTSUP;

	}

	// -----------------------------------------------------------------------------
	// Fonction de génération d'une grille régulière à la résolution r
	// Entrée : Polygon, Résolution r (double), xmin, xmax, ymin, ymax, point list
	// Sortie : remplissage de la liste de points
	// -----------------------------------------------------------------------------
	private static ArrayList<Coordinate> generateRegularGrid(IGeometry polygon, double r, double xmin, double xmax, double ymin, double ymax){

		ArrayList<Coordinate> POINTSUP = new ArrayList<Coordinate>();

		double offset = r/2;

		for (double y=ymin; y<=ymax; y+=r){

			for (double x=xmin+offset; x<=xmax; x+=r){

				// Création d'un point
				DirectPosition pointSup = new DirectPosition(x, y);

				// Test d'inclusion
				if (!polygon.contains(new GM_Point(pointSup))){continue;}

				// Ajout du point supplémentaire
				POINTSUP.add(new Coordinate(pointSup.getX(), pointSup.getY()));

			}

			if (offset == 0){
				offset = r/2;
			}  
			else{
				offset = 0;
			}

		}

		return POINTSUP;

	}

	// -----------------------------------------------------------------------------
	// Calcul de la distance entre un point et la frontière d'un polygone
	// Entrée : GM_Point et polygon
	// Sortie : distance (double)
	// -----------------------------------------------------------------------------
	private static double distanceToBoundary(GM_Point point, GM_Polygon polygon ){

		double dist = point.distance(polygon.getExterior());

		for (int i=0; i<polygon.getInterior().size(); i++){

			double distIntern = point.distance(polygon.getInterior(i));

			dist = Math.min(dist, distIntern);

		}

		return dist;

	}


	// -----------------------------------------------------------------------------
	// Fonction de triangulation (quelconque) d'un polygone
	// Prise en compte des zones internes
	// Entrée : géométrie
	// Sortie : multi-géométrie
	// -----------------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IMultiSurface triangulate(IGeometry polygon){

		// Copie
		IGeometry poly = (IGeometry) polygon.clone();

		if (poly instanceof GM_MultiSurface){

			poly = ((GM_MultiSurface)poly).get(0);

		}

		// On teste s'il y a un intérieur
		ArrayList<IRing> RINGS = (ArrayList<IRing>) ((IPolygon)poly).getInterior();
		if(RINGS.size() != 0){

			// Centre de masse de la partie évidée
			IPolygon hole = new GM_Polygon(RINGS.get(0));
			DirectPosition hc = (DirectPosition) hole.centroid();

			// Univers omega
			IGeometry omega = poly.mbRegion();

			// Préparation à la division

			double xmin = Double.MAX_VALUE;
			double ymin = Double.MAX_VALUE;
			double xmax = Double.MIN_VALUE;
			double ymax = Double.MIN_VALUE;

			for (int i=0; i<omega.coord().size(); i++){

				if (omega.coord().get(i).getX() <= xmin){xmin = omega.coord().get(i).getX();}
				if (omega.coord().get(i).getY() <= ymin){ymin = omega.coord().get(i).getY();}
				if (omega.coord().get(i).getX() >= xmax){xmax = omega.coord().get(i).getX();}
				if (omega.coord().get(i).getY() >= ymax){ymax = omega.coord().get(i).getY();}

			}

			DirectPositionList dpl = new DirectPositionList();

			dpl.add(new DirectPosition(xmin,ymin));
			dpl.add(new DirectPosition(xmax,ymin));
			dpl.add(new DirectPosition(xmax, hc.getY()));
			dpl.add(new DirectPosition(xmin, hc.getY()));
			dpl.add(new DirectPosition(xmin,ymin));

			GM_LineString ls = new GM_LineString(dpl);
			GM_Polygon below = new GM_Polygon(ls);

			// Division
			IGeometry poly1 = poly.intersection(below);
			IGeometry poly2 = poly.difference(poly1);

			// Récursivité
			IMultiSurface ms1 = triangulate(poly1);
			IMultiSurface ms2 = triangulate(poly2);

			// Fusion
			for (int i=0; i<ms2.size(); i++){

				ms1.add(ms2.get(i));

			}

			// Retour
			return ms1;

		}


		GM_MultiSurface output = new GM_MultiSurface();

		GM_Triangle triangle;


		// Boucle tant que la partie terminale n'est pas un triangle
		while(poly.coord().size() > 4){


			// Recherche d'une oreille (cf computational geometry chap 1) 
			for (int i=1; i<poly.coord().size()-1; i++){

				DirectPositionList POINTS = new DirectPositionList();

				POINTS.add(poly.coord().get(i-1));
				POINTS.add(poly.coord().get(i));
				POINTS.add(poly.coord().get(i+1));

				triangle = new GM_Triangle(POINTS);

				// On teste si c'est bien une oreille
				if (poly.contains(triangle)){

					// Détachement du triangle
					output.add(triangle);

					// Réduction du polygone d'origine
					poly = (IPolygon) poly.difference(triangle);

					break;

				}

			}

		}

		output.add(poly);

		return output;

	}

	// -----------------------------------------------------------------------------
	// Fonction de décomposition convexe d'un polygone
	// Entrée : géométrie
	// Sortie : multi-géométrie
	// -----------------------------------------------------------------------------
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static IMultiSurface makeConvex(IGeometry polygon){

		GM_MultiSurface output = new GM_MultiSurface();

		GM_Triangle triangle;

		// Nettoyage des polygones
		IGeometry poly = (IGeometry) polygon.clone();
		((IPolygon)poly).getInterior().clear();


		// Boucle tant que la partie terminale n'est pas un triangle
		while(!Minkowski.isConvex(poly)){


			// Recherche d'une oreille (cf computational geometry chap 1) 
			for (int i=1; i<poly.coord().size()-1; i++){

				DirectPositionList POINTS = new DirectPositionList();

				POINTS.add(poly.coord().get(i-1));
				POINTS.add(poly.coord().get(i));
				POINTS.add(poly.coord().get(i+1));

				triangle = new GM_Triangle(POINTS);

				// On teste si c'est bien une oreille
				if (poly.contains(triangle)){

					// Détachement du triangle
					output.add(triangle);

					// Réduction du polygone d'origine
					poly = (IPolygon) poly.difference(triangle);

					break;

				}

			}

		}

		output.add(poly);

		return output;

	}

}
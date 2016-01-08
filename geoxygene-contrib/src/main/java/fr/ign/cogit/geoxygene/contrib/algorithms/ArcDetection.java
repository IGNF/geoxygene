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

import javax.swing.JFrame;

import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;



public class ArcDetection {

	// ---------------------------------------------------------------------------
	// Paramètres de calcul
	// ---------------------------------------------------------------------------

	// Seuil de précision
	private static double epsilon = 0.5;

	// Seuil de détail
	private static int nb_min_pts = 10;

	// Rayon de courbure maximal
	private static double Rmax = 100;

	// Facteur de sur-échantillonnage
	private static int factor = 2;
	
	// Sauvegardes
	private static DirectPositionList dpl;
	private static DirectPositionList simplified_dpl;

	// Algorithme utilisé
	public static int ALGO_LEAST_SQUARES = 1;
	public static int ALGO_TANGENT_SPACE = 2;
	private static int algo = ALGO_TANGENT_SPACE;

	// ---------------------------------------------------------------------------
	// Réglage des paramètres
	// ---------------------------------------------------------------------------
	public static void setEpsilon(double epsilon){ArcDetection.epsilon = epsilon;}
	public static void setNbMinPts(int nbMinPts){ArcDetection.nb_min_pts = nbMinPts;}
	public static void setRmax(double Rmax){ArcDetection.Rmax = Rmax;}
	public static void setFactor(int factor){ArcDetection.factor = factor;}
	public static void setAlgorithm(int algorithm){ArcDetection.algo = algorithm;}

	
	
	// ---------------------------------------------------------------------------
	// Méthode principale de calcul
	// Entrée : polygone -> algorithme à fixer avant avec setAlgorithm()
	// 	LEAST_SQUARES : méthode par moindres carrés
	//		epsilon = écart-type d'estimation maximal
	//      nb_min_pts = nombre minimal de points du cercle
	//      factor = facteur de sur-échantillonnage
	//      Rmax = rayon de courbure maximal
	//  TANGENT_SPACE : méthode par le plan tangent
	//		epsilon = écart-type d'estimation maximal
	//      nb_min_pts = nombre minimal de points du cercle
	// ---------------------------------------------------------------------------
	public static ArrayList<GM_LineString> compute(GM_Polygon polygon){
		
		if (algo == ALGO_LEAST_SQUARES){
			
			return computeByLeastSquares(polygon);
			
		}
		
		if (algo == ALGO_TANGENT_SPACE){
			
			return computeByTangentSpace(polygon);
			
		}
		return null;
		
	}
	
	// ---------------------------------------------------------------------------
	// Méthode principale de calcul
	// Entrée : polygone GeOxygene
	// Sortie : liste de lignes LineString correspondant aux arcs détectés
	// ---------------------------------------------------------------------------
	public static ArrayList<GM_LineString> computeByTangentSpace(GM_Polygon polygon){

		// Calcul de l'espace tangent
		dpl = buildTangentSpace(polygon);

		// Simplification de la fonction d'angle
		simplified_dpl = simplification(dpl);

		// Récupération des positions d'origine
		DirectPositionList ref_dpl = (DirectPositionList) polygon.coord();

		// Extraction des lignes dans la fonction d'angle
		ArrayList<GM_LineString> arcs = extractArcs(dpl, simplified_dpl, ref_dpl);

		// Retour des géométries détectées
		return arcs;

	}

	// ---------------------------------------------------------------------------
	// Méthode de tracé de la fonction dans l'espace tangent
	// ---------------------------------------------------------------------------
	public static void plotTangentSpaceFunction(){

		plotTangentSpace(dpl);

	}

	// ---------------------------------------------------------------------------
	// Méthode de tracé de la fonction simplifiée dans l'espace tangent
	// ---------------------------------------------------------------------------
	public static void plotSimplifiedTangentSpaceFunction(){

		plotTangentSpace(simplified_dpl);

	}

	// ---------------------------------------------------------------------------
	// Méthode de calcul de la représentation dans l'espace tangent f :
	// f : [0,P] -> R avec P le périmètre total du polygone.
	// Abscisse : abscisse curviligne du contour du polygone
	// Ordonnée : sommation des déviations à chaque sommet du polygone.
	// ---------------------------------------------------------------------------
	private static DirectPositionList buildTangentSpace(GM_Polygon polygon){

		double s = 0;
		double g = 0;

		DirectPositionList dpl = new DirectPositionList();

		for (int i=1; i<polygon.coord().size()-1; i++){

			double x0 = polygon.coord().get(i-1).getX();
			double y0 = polygon.coord().get(i-1).getY();

			double x1 = polygon.coord().get(i).getX();
			double y1 = polygon.coord().get(i).getY();

			double x2 = polygon.coord().get(i+1).getX();
			double y2 = polygon.coord().get(i+1).getY();

			DirectPosition p0 = new DirectPosition(x0,y0);
			DirectPosition p1 = new DirectPosition(x1,y1);
			DirectPosition p2 = new DirectPosition(x2,y2);

			double dg = (180-Angle.angleTroisPoints(p0, p1, p2).getValeur())*Math.PI/180;

			if (Double.isNaN(dg)){dg = 0;}

			g += dg;
			s += Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1))/2;

			dpl.add(new DirectPosition(s, g));

		}

		return dpl;

	}


	// ---------------------------------------------------------------------------
	// Méthode de tracé d'une fonction représentée dans le plan tangent : 
	// f : [0,P] -> R avec P le périmètre total du polygone.
	// Abscisse : abscisse curviligne du contour du polygone
	// Ordonnée : sommation des déviations à chaque sommet du polygone.
	// ---------------------------------------------------------------------------
	private static void plotTangentSpace(DirectPositionList dpl){

		XYSeries series = new XYSeries("Average Size");

		for (int i=0; i<dpl.size(); i++){

			series.add(dpl.get(i).getX(), dpl.get(i).getY());

		}

		XYDataset xyDataset = new XYSeriesCollection(series);
		JFreeChart chart = ChartFactory.createXYLineChart(
				"Espace tangent",
				"Abscisse curviligne (m)", 
				"Somme des déviations d'angles (rad)", 
				xyDataset,
				PlotOrientation.VERTICAL,
				true,
				true,
				false
				);


		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		plot.setRenderer(renderer);


		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));


		JFrame fenetre = new JFrame();

		fenetre.setContentPane(chartPanel);
		fenetre.setSize(1000, 500);
		fenetre.setLocationRelativeTo(null);

		fenetre.setVisible(true);
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	// ---------------------------------------------------------------------------
	// Méthode de calcul de la simplification "type" Douglas-Peucker
	// ---------------------------------------------------------------------------
	private static DirectPositionList simplification(DirectPositionList dpl){

		DirectPositionList dpl_out = (DirectPositionList) Filtering.DouglasPeuckerList(dpl, epsilon);

		return dpl_out;

	}

	// ---------------------------------------------------------------------------
	// Méthode d'extraction des arcs de cercles
	// ---------------------------------------------------------------------------
	private static ArrayList<GM_LineString> extractArcs(DirectPositionList dpl, DirectPositionList simplified_dpl, IDirectPositionList ref_dpl){

		ArrayList<GM_LineString> DETECTION = new ArrayList<GM_LineString>();

		int counter = 0;

		for (int i=0; i<simplified_dpl.size()-1; i++){

			DirectPosition p2 = (DirectPosition) simplified_dpl.get(i+1);

			DirectPositionList temp = new DirectPositionList();

			while(!dpl.get(counter).equals(p2)){

				temp.add(ref_dpl.get(counter));
				counter ++;

			}


			if ((temp.size() >= nb_min_pts)){

				// Coupures des extrémités

				temp.remove(temp.size()-1);
				temp.remove(temp.size()-1);

				temp.remove(0);
				temp.remove(0);


				// Test de corrélation
				double[][] TAB = new double[temp.size()][2];

				for (int k=0; k<temp.size(); k++){

					TAB[k][0] = temp.get(k).getX(); 
					TAB[k][1] = temp.get(k).getY();

				}

				SimpleRegression regression = new SimpleRegression();

				regression.addData(TAB);

				if (regression.getRSquare() > 0.999){continue;}

				// Ajout à la liste de détections
				DETECTION.add(new GM_LineString(temp));

			}


		}

		return DETECTION;

	}


	
	// ---------------------------------------------------------------------------
	// Méthode principale de calcul
	// Entrée : polygone GeOxygene
	// Sortie : liste de lignes LineString correspondant aux arcs détectés
	// ---------------------------------------------------------------------------
	public static ArrayList<GM_LineString> computeByLeastSquares(GM_Polygon building){


		// ----------------------------------------------------------------
		// PARAMETRES
		// ----------------------------------------------------------------
		int Nmin = nb_min_pts;        // Nombre minimal de points
		double S = epsilon;	          // Ecart-type d'estimation minimal
		// ----------------------------------------------------------------

		// ----------------------------------------------------------------
		// Liste de line strings en sortie
		// ----------------------------------------------------------------
		ArrayList<GM_LineString> ARCS = new ArrayList<GM_LineString>();

		// ----------------------------------------------------------------
		// Test nombre minimal de coordonnées
		// ----------------------------------------------------------------
		if (building.coord().size() < Nmin){

			return ARCS;

		}

		// ----------------------------------------------------------------
		// Conversion éventuelle en coordonnées 2D
		// ----------------------------------------------------------------
		DirectPositionList dpl2 = new DirectPositionList();

		for (int i=0; i<building.coord().size(); i++){

			dpl2.add(new DirectPosition(building.coord().get(i).getX(), building.coord().get(i).getY()));

		}

		GM_Polygon batiment = new GM_Polygon(new GM_LineString(dpl2));


		// ----------------------------------------------------------------
		// Sur-échantillonnage éventuel (1 = pas de sur-chantillonnage)
		// ----------------------------------------------------------------
		GM_Polygon bati = oversample(batiment,factor, S/10);

		// ----------------------------------------------------------------
		// Nombre de points dans le polugone de travail
		// ----------------------------------------------------------------
		int N = bati.coord().size();

		System.out.println("Nombre de points : "+N);

		// ----------------------------------------------------------------
		// Recherche de l'indice d'extrémité du segment le plus long
		// ----------------------------------------------------------------

		int jmax = 0;
		int k = 1;
		double distance_max = 0;

		for (int j=1; j<N-1; j++){

			double distance = bati.coord().get(j).distance(bati.coord().get(j+1));

			if (distance >= distance_max){

				distance_max = distance;
				jmax = j+1;

			}

		}

		System.out.println("Initialisation au point : "+jmax);

		// ----------------------------------------------------------------
		// Re-indexation des sommets du polygone
		// ----------------------------------------------------------------

		System.out.print("Réordonnancement...");

		DirectPositionList idl = new DirectPositionList();

		for (int i=jmax; i<N; i++){idl.add(bati.coord().get(i));}
		for (int i=0; i<jmax; i++){idl.add(bati.coord().get(i));}

		GM_Polygon polygon = new GM_Polygon(new GM_LineString(idl));

		System.out.println(" ok");

		// ----------------------------------------------------------------
		// Balayage de la fenêtre glissante
		// ----------------------------------------------------------------

		int i=0; int f=Nmin-1;

		while(f <= N){


			// Distance entre points
			if (polygon.coord().get(i).distance(polygon.coord().get(i+1)) >= 1){

				i++; f++;
				continue;

			}


			// Calcul sur la première fenêtre
			double s = adjust(polygon, i, f)[3];

			// Décalage éventuel
			if (s >= S){

				i++; f++;

			}

			// Dichotomie éventuelle
			else{

				double q = 2;
				double delta = N-f;

				f += (int)(delta/q);

				s = adjust(polygon, i, f)[3];

				while (delta/q > 1){

					// Calcul sur la fenêtre ajustée
					s = adjust(polygon, i, f)[3];


					// Contraction ou dilatation

					q = 2*q;

					if (s > S){

						f = f - (int)(delta/q);

					}
					else{

						f = f + (int)(delta/q);

					}

				}

				// Test rayon de courbure

				double[] RESULTS = adjust(polygon, i, f);

				if (RESULTS[2] <= Rmax){

					// Test de corrélation

					double c = correlation(polygon, i+1, f-1);
					System.out.println("Correlation = "+c);

					if (c <= 0.95){

						System.out.println(k+" - Arc détecté de  "+(i+1)+" à "+f);
						k++;


						// Ajout à la liste de sortie

						DirectPositionList dpl = new DirectPositionList();

						for (int index=i; index<f; index++){

							dpl.add(polygon.coord().get(index));

						}

						ARCS.add(new GM_LineString(dpl));

					}

				}

				// Décalage de la fenêtre
				i = f;
				f = i+Nmin-1;


			}

		}


		System.out.println("--------------------------------------");
		System.out.println("Procédure terminée : "+(k-1)+" arcs détecté(s)");
		System.out.println("--------------------------------------");


		return ARCS;

	}



	// ---------------------------------------------------------------------------
	// Méthode de regression circulaire sur un polygone (moindres carrés)
	// Entrée : polygone GeOxygene, indices de sommet initial et final
	// Sortie : [X,Y,R,S] avec :
	// 	- (X,Y) : coordonnées du centre du cercle estimé
	//  - R : rayon du cercle estimé
	//  - S : écart-type d'estimation sur le rayon
	// ---------------------------------------------------------------------------
	public static double[] adjust(GM_Polygon polygon, int ini, int fin){

		ArrayList<Double> X = new ArrayList<Double>();
		ArrayList<Double> Y = new ArrayList<Double>();

		for (int i=ini; i<fin+1; i++){

			X.add(polygon.coord().get(i).getX());
			Y.add(polygon.coord().get(i).getY());

		}

		return adjust(X,Y);

	}

	// ---------------------------------------------------------------------------
	// Méthode de regression circulaire sur un set de points (moindres carrés)
	// Entrée : liste de coordonnées X et Y
	// Sortie : [X,Y,R,S] avec :
	// 	- (X,Y) : coordonnées du centre du cercle estimé
	//  - R : rayon du cercle estimé
	//  - S : écart-type d'estimation sur le rayon
	// ---------------------------------------------------------------------------
	public static double[] adjust(ArrayList<Double> X, ArrayList<Double> Y){

		double[] RESULTS = new double[4];

		int n = X.size();

		Matrix A = new Matrix(n, 3);
		Matrix B = new Matrix(n, 1);

		Matrix P = new Matrix(3,1);

		double Xc = X.get(0);
		double Yc = Y.get(0);
		double Rc = 1;

		double dX = 1;
		double dY = 1;
		double dR = 1;

		while(Math.abs(dX)+Math.abs(dY)+Math.abs(dR) > 0.3){

			for (int i=0; i<X.size(); i++){

				// Jacobienne

				A.set(i, 0, 2*(Xc-X.get(i)));    // Dérivée par rapport à Xc
				A.set(i, 1, 2*(Yc-Y.get(i)));    // Dérivée par rapport à Yc
				A.set(i, 2, -2*Rc);              // Dérivée par rapport à Rc

				// Observations
				B.set(i, 0, -((Xc-X.get(i))*(Xc-X.get(i))+(Yc-Y.get(i))*(Yc-Y.get(i))-Rc*Rc));

			}


			// Résolution
			P = A.transpose().times(A).solve(A.transpose().times(B));

			// Mise-à-jour
			dX = P.get(0, 0);
			dY = P.get(1, 0);
			dR = P.get(2, 0); 

			Xc += dX;
			Yc += dY;
			Rc += dR;

		}

		// Correction de signe
		Rc = Math.abs(Rc);

		// Ecart-type d'estimation sur le rayon

		double sigma = 0;

		for (int i=0; i<X.size(); i++){

			sigma += Math.pow((Rc-Math.sqrt((Xc-X.get(i))*(Xc-X.get(i))+(Yc-Y.get(i))*(Yc-Y.get(i)))),2);

		}

		sigma /= X.size();
		sigma += Math.sqrt(sigma);

		RESULTS[0] = Xc;
		RESULTS[1] = Yc;
		RESULTS[2] = Rc;
		RESULTS[3] = sigma;

		return RESULTS;

	}


	// ---------------------------------------------------------------------------
	// Méthode de tracé Matlab d'un fragment de polygone
	// Entrée : polygone GeOxygene, indices de sommet initial et final
	// ---------------------------------------------------------------------------
	public static void plot(GM_Polygon polygon, int ini, int fin, String name){

		System.out.println(name+"=[");

		for (int i=ini; i<fin+1; i++){

			System.out.println(polygon.coord().get(i).getX()+", "+polygon.coord().get(i).getY());

		}

		System.out.println("];");

	}

	// ---------------------------------------------------------------------------
	// Méthode de tracé Matlab d'un cercle
	// Entrée : coordonnées du centre et rayon
	// ---------------------------------------------------------------------------
	public static void plotCircle(double Xc, double Yc, double Rc){

		System.out.println("C=[");

		for (double theta=0; theta<=2*Math.PI; theta+=0.1){

			System.out.println((Xc+Rc*Math.cos(theta))+", "+(Yc+Rc*Math.sin(theta)));

		}

		System.out.println("];");

	}

	// ---------------------------------------------------------------------------
	// Méthode de sur-échantillonnage (bruité) d'un polygone
	// Entrée : polygone GeOxygene, facteur de sur-échantillonnage, bruit
	// Sortie : polygon sur-échantillonné
	// ---------------------------------------------------------------------------
	public static GM_Polygon oversample(GM_Polygon poly, int factor, double noise){

		DirectPositionList dpl = new DirectPositionList();

		for (int i=0; i<poly.coord().size()-1; i++){

			dpl.add((IDirectPosition) poly.coord().get(i).clone());

			double x1 = poly.coord().get(i).getX();
			double y1 = poly.coord().get(i).getY();

			double x2 = poly.coord().get(i+1).getX();
			double y2 = poly.coord().get(i+1).getY();

			for (int j=1; j<factor; j++){

				double xi = (j*x2+(factor-j)*x1)/((double)(factor))+(Math.random()-0.5)*noise; 
				double yi = (j*y2+(factor-j)*y1)/((double)(factor))+(Math.random()-0.5)*noise; 

				dpl.add(new DirectPosition(xi,yi));

			}

		}

		dpl.add((IDirectPosition) poly.coord().get(poly.coord().size()-1).clone());

		GM_LineString line = new GM_LineString(dpl);

		return new GM_Polygon(line);


	}

	// ---------------------------------------------------------------------------
	// Méthode de création d'un cercle en GM_Linestring
	// Entrée : DirectPosition du centre, rayon et nombre de points
	// Sortie : GM_Linestring représentant le cercle
	// ---------------------------------------------------------------------------
	@SuppressWarnings("unused")
	private static GM_LineString createCircle(DirectPosition center, double radius, int nbPoints){

		DirectPositionList dpl = new DirectPositionList();

		// Résolution angulaire
		double dtheta = 2*Math.PI/((double)nbPoints);

		// Balayage du cercle
		for (double theta=0; theta<2*Math.PI; theta+=dtheta){

			double x = center.getX()+radius*Math.cos(theta);
			double y = center.getY()+radius*Math.sin(theta);

			DirectPosition dp = new DirectPosition(x, y);

			dpl.add(dp);

		}

		// Bouclage sur le premier point
		dpl.add((IDirectPosition) dpl.get(0).clone());

		return new GM_LineString(dpl);

	}


	// ---------------------------------------------------------------------------
	// Méthode de calcul de corrélation sur un set de points
	// Entrée : polygone, indices de départ et de fin
	// Sortie : coefficient de corrélation
	// ---------------------------------------------------------------------------
	private static double correlation(GM_Polygon polygon, int i, int f){

		PearsonsCorrelation pc = new PearsonsCorrelation();

		double[] X = new double[f-i+1];
		double[] Y = new double[f-i+1];

		for (int id=i; id<=f; id++){

			X[id-i] = polygon.coord().get(id).getX();
			Y[id-i] = polygon.coord().get(id).getY();

		}

		return Math.abs(pc.correlation(X, Y));

	}

}

package fr.ign.cogit.geoxygene.contrib.algorithms;

import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;


public class ShapeMoments {


	// Nombre de tirages
	private static int npts = 5000;

	// Echantillonnage
	private static int n_sample = 500;

	// Fenêtre de lissage statistique
	private static int w = 10;


	public static double[] compute(IGeometry bati){


		// Valeurs extremales
		double xmin = Double.MAX_VALUE;
		double xmax = Double.MIN_VALUE;
		double ymin = Double.MAX_VALUE;
		double ymax = Double.MIN_VALUE;

		
		ArrayList<Double> D = new ArrayList<Double>();
		

		// Ajout de z dans le polygone
		for (int i=0; i<bati.coord().size(); i++){

			if (bati.coord().get(i).getX() < xmin){xmin = bati.coord().get(i).getX();}
			if (bati.coord().get(i).getY() < ymin){ymin = bati.coord().get(i).getY();}
			if (bati.coord().get(i).getX() > xmax){xmax = bati.coord().get(i).getX();}
			if (bati.coord().get(i).getY() > ymax){ymax = bati.coord().get(i).getY();}

		} 

		// Tirage de p1
		for (int i=0; i<npts; i++){


			double x1 = (xmax-xmin)*Math.random()+xmin;
			double y1 = (ymax-ymin)*Math.random()+ymin;

			DirectPosition p1 = new DirectPosition(x1,y1);

			while (!bati.contains(new GM_Point(p1))){

				x1 = (xmax-xmin)*Math.random()+xmin;
				y1 = (ymax-ymin)*Math.random()+ymin;

				p1 = new DirectPosition(x1,y1);

			}


			// Tirage de p2
			double x2 = (xmax-xmin)*Math.random()+xmin;
			double y2 = (ymax-ymin)*Math.random()+ymin;

			DirectPosition p2 = new DirectPosition(x2,y2);

			while (!bati.contains(new GM_Point(p2))){

				x2 = (xmax-xmin)*Math.random()+xmin;
				y2 = (ymax-ymin)*Math.random()+ymin;

				p2 = new DirectPosition(x2,y2);

			}

			// Calcul de la distance
			double distance = Math.sqrt((x2-x1)*(x2-x1)+(y2-y1)*(y2-y1));

			// Sauvegarde
			D.add(distance);

		}

		// Borne supérieure

		double dmax = Double.MIN_VALUE;

		for (int i=0; i<npts; i++){

			if (D.get(i) > dmax){dmax = D.get(i);}

		}


		// Calcul histogramme

		ArrayList<Double> HISTOGRAMME = new ArrayList<Double>();
		ArrayList<Double> DHISTOGRAMME = new ArrayList<Double>();

		for (int i=0; i<n_sample; i++){

			double d = ((double)(i))/((double)(n_sample))*dmax;

			HISTOGRAMME.add(0.0);
			DHISTOGRAMME.add(d);

			for (int j=0; j<D.size(); j++){

				if ((d < D.get(j)) && (D.get(j) < d+dmax/((double)(n_sample)))){

					HISTOGRAMME.set(HISTOGRAMME.size()-1, HISTOGRAMME.get(HISTOGRAMME.size()-1)+1);

				}

			}	

		}



		// Lissage statistique

		@SuppressWarnings("unchecked")
		ArrayList<Double> HISTO = (ArrayList<Double>) HISTOGRAMME.clone();



		for (int i=w; i<HISTOGRAMME.size()-w; i++){

			double window = 0;

			int w2 = Math.min(w, i);
			w2 = Math.min(w2, HISTOGRAMME.size()-w);

			for (int j=i-w2; j<i+w2+1; j++){

				window += HISTOGRAMME.get(j);

			}

			HISTO.set(i, window/(2*w2+1));

		}

		// Normalisation loi de probabilité
		double sum = 0;
		for (int i=0; i<HISTO.size(); i++){sum += HISTO.get(i);}
		for (int i=0; i<HISTO.size(); i++){HISTO.set(i, HISTO.get(i)/sum);}


		// Calcul des moments (et moments centrés, réduits...)

		double m1 = 0;
		double m2 = 0;
		double m3 = 0;
		double m4 = 0;

		// Moyenne
		for (int i=0; i<HISTO.size(); i++){

			m1 += DHISTOGRAMME.get(i)*HISTO.get(i);

		}

		// Ecart-type
		for (int i=0; i<HISTO.size(); i++){

			m2 += Math.pow((DHISTOGRAMME.get(i)-m1),2)*HISTO.get(i);

		}

		// Ecart-type
		for (int i=0; i<HISTO.size(); i++){

			m2 += Math.pow((DHISTOGRAMME.get(i)-m1),2)*HISTO.get(i);

		}

		m2 = Math.sqrt(m2);

		// Coefficient d'assymétrie
		for (int i=0; i<HISTO.size(); i++){

			m3 += Math.pow((DHISTOGRAMME.get(i)-m1)/m2,3)*HISTO.get(i);

		}

		// Kurtosis non-normalisé
		for (int i=0; i<HISTO.size(); i++){

			m4 += Math.pow((DHISTOGRAMME.get(i)-m1)/m2,4)*HISTO.get(i);

		}

		// Outputs
		double[] results = new double[4];
		
		results[0] = m1;
		results[1] = m2;
		results[2] = m3;
		results[3] = m4;
		
		return results;
	

	}

}

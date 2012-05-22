/**
 * 
 */
package fr.ign.cogit.appli.geopensim.feature.meso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.TypeFonctionnel;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.algo.MathUtil;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;


/**
 * @author Julien Perret
 *
 */
public abstract class SurfaceBatie extends MesoRepresentation {
	//static Logger logger=Logger.getLogger(SurfaceBatie.class.getName());

	public SurfaceBatie() {
		super();
	}
	//Forme
	double elongation;
	double convexite;
	
	//Aires Batiments
	double moyenneAiresBatiments;
	double ecartTypeAiresBatiments;
	double maxAiresBatiments;
	double minAiresBatiments;
	double medianeAiresBatiments;
	
	//Forme Batiments
	double moyenneElongationBatiments;
	double ecartTypeElongationBatiments;

	double moyenneConvexiteBatiments;
	double ecartTypeConvexiteBatiments;
	
	//HomogeneiteTypeFonctionnelBatiments
	int homogeneiteTypesFonctionnelsBatiments;
	int homogeneiteTailleBatiments;

	//Orientation des batiments
	List<Double> orientationsPrincipalesBatiments = new ArrayList<Double>();
	List<Double> dispersionOrientationsPrincipalesBatiments = new ArrayList<Double>();

	List<Double> orientationsPrincipalesMursBatiments = new ArrayList<Double>();
	List<Double> dispersionOrientationsPrincipalesMursBatiments = new ArrayList<Double>();

    protected List<Batiment> batiments = new ArrayList<Batiment>();
	int nombreBatiments;
	
	//ville
	UniteUrbaine uniteUrbaine = null;

    /**
	 * @return ville
	 */
	public UniteUrbaine getUniteUrbaine() {return uniteUrbaine;}

	/**
	 * @param uniteUrbaine ville à Définir
	 */
	public void setUniteUrbaine(UniteUrbaine uniteUrbaine) {this.uniteUrbaine = uniteUrbaine;}

	/**
	 * @return the batiments
	 */
	public List<Batiment> getBatiments() {return batiments;}

	/**
	 * @param batiments the batiments to set
	 */
	public void setBatiments(List<Batiment> batiments) {
		this.batiments = batiments;
		nombreBatiments=batiments.size();
	}
    
	/**
	 * @param e
	 */
	public void addBatiment(Batiment e) {
        if ( e == null ) return;
		batiments.add(e);
		nombreBatiments=batiments.size();
	}

	/**
	 * @param c
	 */
	public void addAllBatiment(Collection<Batiment> c) {
		batiments.addAll(c);
		nombreBatiments=batiments.size();
	}

	/**
	 * @param o
	 */
	public void removeBatiment(Batiment o) {
        if ( o == null ) return;
		batiments.remove(o);
		nombreBatiments=batiments.size();
	}

	/**
	 * 
	 */
	public void emptyBatiments() {
//		for (Batiment b:batiments) {
//			b.setZoneElementaireUrbaine(null);
//		}
		batiments.clear();
		nombreBatiments=batiments.size();
	}

	/**
	 * @param i
	 * @return le bâtiment d'indice i
	 */
	public Batiment getBatiment(int i) {return batiments.get(i);}

	/**
	 * 
	 */
	public void clearBatiments() {
		batiments.clear();
		nombreBatiments=batiments.size();
	}

	/**
	 * @param o
	 * @return vrai si la surface bâtie contient le bâtiment passé en paramètre, faux sinon
	 */
	public boolean containsBatiments(Object o) {return batiments.contains(o);}

	/**
	 * @return vrai si la liste des bâtiments est vide, faux sinon
	 */
	public boolean isEmptyBatiments() {return batiments.isEmpty();}
	
	/**
	 * @return le nombre de bâtiments appartenant à la surface bâtie
	 */
	public int sizeBatiments() {return batiments.size();}

	/**
	 * @return the nombreBatiments
	 */
	public int getNombreBatiments() {return nombreBatiments;}

	/**
	 * @param nombreBatiments the nombreBatiments to set
	 */
	public void setNombreBatiments(int nombreBatiments) {this.nombreBatiments = nombreBatiments;}

	/**
	 * @return the elongation
	 */
	public double getElongation() {return elongation;}

	/**
	 * @param elongation the elongation to set
	 */
	public void setElongation(double elongation) {this.elongation = elongation;}

	/**
	 * @return the convexite
	 */
	public double getConvexite() {return convexite;}

	/**
	 * @param convexite the convexite to set
	 */
	public void setConvexite(double convexite) {this.convexite = convexite;}

	/**
	 * @return the moyenneAiresBatiments
	 */
	public double getMoyenneAiresBatiments() {return moyenneAiresBatiments;}

	/**
	 * @param moyenneAiresBatiments the moyenneAiresBatiments to set
	 */
	public void setMoyenneAiresBatiments(double moyenneAiresBatiments) {this.moyenneAiresBatiments = moyenneAiresBatiments;}

	/**
	 * @return the ecartTypeAiresBatiments
	 */
	public double getEcartTypeAiresBatiments() {return ecartTypeAiresBatiments;}

	/**
	 * @param ecartTypeAiresBatiments the ecartTypeAiresBatiments to set
	 */
	public void setEcartTypeAiresBatiments(double ecartTypeAiresBatiments) {this.ecartTypeAiresBatiments = ecartTypeAiresBatiments;}

	/**
	 * @return the maxAiresBatiments
	 */
	public double getMaxAiresBatiments() {return maxAiresBatiments;}

	/**
	 * @param maxAiresBatiments the maxAiresBatiments to set
	 */
	public void setMaxAiresBatiments(double maxAiresBatiments) {this.maxAiresBatiments = maxAiresBatiments;}

	/**
	 * @return the minAiresBatiments
	 */
	public double getMinAiresBatiments() {return minAiresBatiments;}

	/**
	 * @param minAiresBatiments the minAiresBatiments to set
	 */
	public void setMinAiresBatiments(double minAiresBatiments) {this.minAiresBatiments = minAiresBatiments;}

	/**
	 * @return the medianeAiresBatiments
	 */
	public double getMedianeAiresBatiments() {return medianeAiresBatiments;}

	/**
	 * @param medianeAiresBatiments the medianeAiresBatiments to set
	 */
	public void setMedianeAiresBatiments(double medianeAiresBatiments) {this.medianeAiresBatiments = medianeAiresBatiments;}

	/**
	 * @return the moyenneElongationBatiments
	 */
	public double getMoyenneElongationBatiments() {return moyenneElongationBatiments;}

	/**
	 * @param moyenneElongationBatiments the moyenneElongationBatiments to set
	 */
	public void setMoyenneElongationBatiments(double moyenneElongationBatiments) {this.moyenneElongationBatiments = moyenneElongationBatiments;}

	/**
	 * @return the ecartTypeElongationBatiments
	 */
	public double getEcartTypeElongationBatiments() {return ecartTypeElongationBatiments;}

	/**
	 * @param ecartTypeElongationBatiments the ecartTypeElongationBatiments to set
	 */
	public void setEcartTypeElongationBatiments(double ecartTypeElongationBatiments) {this.ecartTypeElongationBatiments = ecartTypeElongationBatiments;}

	/**
	 * @return the moyenneConvexiteBatiments
	 */
	public double getMoyenneConvexiteBatiments() {return moyenneConvexiteBatiments;}

	/**
	 * @param moyenneConvexiteBatiments the moyenneConvexiteBatiments to set
	 */
	public void setMoyenneConvexiteBatiments(double moyenneConvexiteBatiments) {this.moyenneConvexiteBatiments = moyenneConvexiteBatiments;}

	/**
	 * @return the ecartTypeConvexiteBatiments
	 */
	public double getEcartTypeConvexiteBatiments() {return ecartTypeConvexiteBatiments;}

	/**
	 * @param ecartTypeConvexiteBatiments the ecartTypeConvexiteBatiments to set
	 */
	public void setEcartTypeConvexiteBatiments(double ecartTypeConvexiteBatiments) {this.ecartTypeConvexiteBatiments = ecartTypeConvexiteBatiments;}

	/**
	 * @return the homogeneiteTypesFonctionnelsBatiments
	 */
	public int getHomogeneiteTypesFonctionnelsBatiments() {return homogeneiteTypesFonctionnelsBatiments;}

	/**
	 * @param homogeneiteTypesFonctionnelsBatiments the homogeneiteTypesFonctionnelsBatiments to set
	 */
	public void setHomogeneiteTypesFonctionnelsBatiments(int homogeneiteTypesFonctionnelsBatiments) {this.homogeneiteTypesFonctionnelsBatiments = homogeneiteTypesFonctionnelsBatiments;}

	/**
	 * @return the homogeneiteTailleBatiments
	 */
	public int getHomogeneiteTailleBatiments() {return homogeneiteTailleBatiments;}

	/**
	 * @param homogeneiteTailleBatiments the homogeneiteTailleBatiments to set
	 */
	public void setHomogeneiteTailleBatiments(int homogeneiteTailleBatiments) {this.homogeneiteTailleBatiments = homogeneiteTailleBatiments;}

	/**
	 * @return the orientationsPrincipalesBatiments
	 */
	public List<Double> getOrientationsPrincipalesBatiments() {return orientationsPrincipalesBatiments;}

	/**
	 * @param orientationsPrincipalesBatiments the orientationsPrincipalesBatiments to set
	 */
	public void setOrientationsPrincipalesBatiments(List<Double> orientationsPrincipalesBatiments) {this.orientationsPrincipalesBatiments = orientationsPrincipalesBatiments;}

	/**
	 * @return the dispersionOrientationsPrincipalesBatiments
	 */
	public List<Double> getDispersionOrientationsPrincipalesBatiments() {return dispersionOrientationsPrincipalesBatiments;}

	/**
	 * @param dispersionOrientationsPrincipalesBatiments the dispersionOrientationsPrincipalesBatiments to set
	 */
	public void setDispersionOrientationsPrincipalesBatiments(List<Double> dispersionOrientationsPrincipalesBatiments) {this.dispersionOrientationsPrincipalesBatiments = dispersionOrientationsPrincipalesBatiments;}

	/**
	 * @return the orientationsPrincipalesMursBatiments
	 */
	public List<Double> getOrientationsPrincipalesMursBatiments() {return orientationsPrincipalesMursBatiments;}

	/**
	 * @param orientationsPrincipalesMursBatiments the orientationsPrincipalesMursBatiments to set
	 */
	public void setOrientationsPrincipalesMursBatiments(List<Double> orientationsPrincipalesMursBatiments) {this.orientationsPrincipalesMursBatiments = orientationsPrincipalesMursBatiments;}

	/**
	 * @return the dispersionOrientationsPrincipalesMursBatiments
	 */
	public List<Double> getDispersionOrientationsPrincipalesMursBatiments() {return dispersionOrientationsPrincipalesMursBatiments;}

	/**
	 * @param dispersionOrientationsPrincipalesMursBatiments the dispersionOrientationsPrincipalesMursBatiments to set
	 */
	public void setDispersionOrientationsPrincipalesMursBatiments(List<Double> dispersionOrientationsPrincipalesMursBatiments) {this.dispersionOrientationsPrincipalesMursBatiments = dispersionOrientationsPrincipalesMursBatiments;}

	protected double aire = 0.0;

	/**
	 * @return aire
	 */
	public double getAire() {return aire;}
	/**
	 * @param aire aire à Définir
	 */
	public void setAire(double aire) {this.aire = aire;}
	
	protected double densite=0.0;

	/**
	 * @return densite
	 */
	public double getDensite() {return densite;}

	/**
	 * @param densite densite à Définir
	 */
	public void setDensite(double densite) {this.densite = densite;}

	protected int tailleBatiments;

	/**
	 * @return tailleBatiments
	 */
	public int getTailleBatiments() {return tailleBatiments;}

	/**
	 * @param tailleBatiments tailleBatiments à Définir
	 */
	public void setTailleBatiments(int tailleBatiments) {this.tailleBatiments = tailleBatiments;}


	/* (non-Javadoc)
	 * @see geoxygene.geodata.feature.MesoRepresentation#qualifier()
	 */
	@Override
	public void qualifier() {
		super.qualifier();
		// Surface
		this.setAire(getGeom().area());
		// Batiments
		nombreBatiments = batiments.size();
		//Forme
		Polygon polygon = null;
		try {
			polygon = (Polygon)AdapterFactory.toGeometry(new GeometryFactory(), this.getGeom());
		} catch (Exception e) {
			logger.error("Erreur dans la construction de la géométrie "+e.getMessage());
		}
		if (polygon!=null) {
			elongation = JtsUtil.elongation(polygon);
			convexite =  JtsUtil.convexite(polygon);
		}
		//statistiques sur les aires des bâtiments
		List<Double> listeAires = new ArrayList<Double>();
		List<Double> listeElongations = new ArrayList<Double>();
		List<Double> listeConvexites = new ArrayList<Double>();
		for (Object b:batiments) {
			Batiment bat = (Batiment) b;
			Polygon batPolygon = null;
			try {
				batPolygon = (Polygon)AdapterFactory.toGeometry(new GeometryFactory(), bat.getGeom());
			} catch (Exception e) {
			    logger.error("Erreur dans la construction de la géométrie "+e.getMessage());
			}
			listeAires.add(bat.getAire());
			listeElongations.add(JtsUtil.elongation(batPolygon));
			listeConvexites.add(JtsUtil.convexite(batPolygon));
		}
		minAiresBatiments = MathUtil.min(listeAires);
		maxAiresBatiments = MathUtil.max(listeAires);
		moyenneAiresBatiments = MathUtil.moyenne(listeAires);
		ecartTypeAiresBatiments=MathUtil.ecartType(listeAires, moyenneAiresBatiments);
		medianeAiresBatiments = MathUtil.mediane(listeAires);
		// statistiques sur la forme des batiments
		moyenneElongationBatiments = MathUtil.moyenne(listeElongations);
		ecartTypeElongationBatiments = MathUtil.ecartType(listeElongations, moyenneElongationBatiments);

		moyenneConvexiteBatiments = MathUtil.moyenne(listeConvexites);
		ecartTypeConvexiteBatiments = MathUtil.ecartType(listeConvexites, moyenneConvexiteBatiments);

		// homogénéité
		homogeneiteTailleBatiments = HomogeneiteTailleBatiments.Mixte;
		if (uniteUrbaine!=null) {
			/*
			 * TODO on utilise la moyenne des aires des bâtiments comme seuil pour Déterminer
			 * si les bâtiments sont petits ou grands. 
			 */
			if (!batiments.isEmpty()) {
				double seuil = uniteUrbaine.getMoyenneAiresBatiments();
				int nbBatimentsGrands = 0;
				for(Batiment batiment:batiments) if (batiment.getAire()>seuil) nbBatimentsGrands++;
				if (nbBatimentsGrands==0) {
					homogeneiteTailleBatiments = HomogeneiteTailleBatiments.HomogenePetit;
				} else if (nbBatimentsGrands==batiments.size()) {
					homogeneiteTailleBatiments = HomogeneiteTailleBatiments.HomogeneGrand;
				} else if (100*nbBatimentsGrands/batiments.size()>70) {
					homogeneiteTailleBatiments = HomogeneiteTailleBatiments.QuasiHomogeneGrand;
				} else if (100*nbBatimentsGrands/batiments.size()<30) {
					homogeneiteTailleBatiments = HomogeneiteTailleBatiments.QuasiHomogenePetit;
				}
			}			
		}
		
		homogeneiteTypesFonctionnelsBatiments = HomogeneiteTypeFonctionnelBatiments.Heterogene;
		int nbBatimentsHabitat = 0;
		int nbBatimentsPublic = 0;
		int nbBatimentsIndustriel = 0;
		for(Batiment batiment:batiments) {
			if (batiment.getTypeFonctionnel()==TypeFonctionnel.Habitat) nbBatimentsHabitat++;
			else if (batiment.getTypeFonctionnel()==TypeFonctionnel.Public) nbBatimentsPublic++;
			else if (batiment.getTypeFonctionnel()==TypeFonctionnel.Industriel) nbBatimentsIndustriel++;
		}
		if (nbBatimentsHabitat==batiments.size()) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.HomogeneHabitat);
		} else if (nbBatimentsPublic==batiments.size()) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.HomogenePublic);			
		} else if (nbBatimentsIndustriel==batiments.size()) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.HomogeneIndustriel);
		} else if (100*nbBatimentsHabitat/batiments.size()>70) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.QuasiHomogeneHabitat);
		} else if (100*nbBatimentsPublic/batiments.size()>70) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.QuasiHomogenePublic);
		} else if (100*nbBatimentsIndustriel/batiments.size()>70) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.QuasiHomogeneIndustriel);
		}
	}
}

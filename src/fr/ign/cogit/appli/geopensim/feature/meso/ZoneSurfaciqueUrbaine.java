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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.CollectionOfElements;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.TypeFonctionnel;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.algo.MathUtil;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationFeuille;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;


/**
 * @author Julien Perret
 *
 */
@Entity
public abstract class ZoneSurfaciqueUrbaine extends ZoneSurfacique {

	/**
	 * Constructeur vide
	 */
	public ZoneSurfaciqueUrbaine() {super();}

	/**
	 * Constructeur à partir d'une géométrie
	 */
	public ZoneSurfaciqueUrbaine(IPolygon polygone) {super(polygone);}

	/**
	 * Unité
	 */
	UniteUrbaine uniteUrbaine = null;

	/**
	 * @return Unité
	 */
	@OneToOne
	public UniteUrbaine getUniteUrbaine() {return uniteUrbaine;}

	/**
	 * @param unite Unité
	 */
	public void setUniteUrbaine(UniteUrbaine unite) {this.uniteUrbaine = unite;}

	//Aires Batiments
	double moyenneAiresBatiments = 0;
	double ecartTypeAiresBatiments = 0;
	double maxAiresBatiments = 0;
	double minAiresBatiments = 0;
	double medianeAiresBatiments = 0;

	//Forme Batiments
	double moyenneElongationBatiments = 0;
	double ecartTypeElongationBatiments = 0;

	double moyenneConvexiteBatiments = 0;
	double ecartTypeConvexiteBatiments = 0;

	//HomogeneiteTypeFonctionnelBatiments
	int homogeneiteTypesFonctionnelsBatiments = 0;
	int homogeneiteTailleBatiments = 0;

	//Orientation des batiments
	List<Double> orientationsPrincipalesBatiments = new ArrayList<Double>();
	List<Double> dispersionOrientationsPrincipalesBatiments = new ArrayList<Double>();

	List<Double> orientationsPrincipalesMursBatiments = new ArrayList<Double>();
	List<Double> dispersionOrientationsPrincipalesMursBatiments = new ArrayList<Double>();

	/**
	 * @return moyenne des aires des bâtiments de la surface bâtie
	 */
	public double getMoyenneAiresBatiments() {return moyenneAiresBatiments;}

	/**
	 * @param moyenneAiresBatiments moyenne des aires des bâtiments de la surface bâtie
	 */
	public void setMoyenneAiresBatiments(double moyenneAiresBatiments) {this.moyenneAiresBatiments = moyenneAiresBatiments;}

	/**
	 * @return écart type des aires des bâtiments de la surface bâtie
	 */
	public double getEcartTypeAiresBatiments() {return ecartTypeAiresBatiments;}

	/**
	 * @param ecartTypeAiresBatiments écart type des aires des bâtiments de la surface bâtie
	 */
	public void setEcartTypeAiresBatiments(double ecartTypeAiresBatiments) {this.ecartTypeAiresBatiments = ecartTypeAiresBatiments;}

	/**
	 * @return maximum des aires des bâtiments de la surface bâtie
	 */
	public double getMaxAiresBatiments() {return maxAiresBatiments;}

	/**
	 * @param maxAiresBatiments maximum des aires des bâtiments de la surface bâtie
	 */
	public void setMaxAiresBatiments(double maxAiresBatiments) {this.maxAiresBatiments = maxAiresBatiments;}

	/**
	 * @return minimum des aires des bâtiments de la surface bâtie
	 */
	public double getMinAiresBatiments() {return minAiresBatiments;}

	/**
	 * @param minAiresBatiments minimum des aires des bâtiments de la surface bâtie
	 */
	public void setMinAiresBatiments(double minAiresBatiments) {this.minAiresBatiments = minAiresBatiments;}

	/**
	 * @return médiane des aires des bâtiments de la surface bâtie
	 */
	public double getMedianeAiresBatiments() {return medianeAiresBatiments;}

	/**
	 * @param medianeAiresBatiments médiane des aires des bâtiments de la surface bâtie
	 */
	public void setMedianeAiresBatiments(double medianeAiresBatiments) {this.medianeAiresBatiments = medianeAiresBatiments;}

	/**
	 * @return moyenne des élongations des bâtiments de la surface bâtie
	 */
	public double getMoyenneElongationBatiments() {return moyenneElongationBatiments;}

	/**
	 * @param moyenneElongationBatiments moyenne des élongations des bâtiments de la surface bâtie
	 */
	public void setMoyenneElongationBatiments(double moyenneElongationBatiments) {this.moyenneElongationBatiments = moyenneElongationBatiments;}

	/**
	 * @return écart type des élongations des bâtiments de la surface bâtie
	 */
	public double getEcartTypeElongationBatiments() {return ecartTypeElongationBatiments;}

	/**
	 * @param ecartTypeElongationBatiments écart type des élongations des bâtiments de la surface bâtie
	 */
	public void setEcartTypeElongationBatiments(double ecartTypeElongationBatiments) {this.ecartTypeElongationBatiments = ecartTypeElongationBatiments;}

	/**
	 * @return moyenne des convexités des bâtiments de la surface bâtie
	 */
	public double getMoyenneConvexiteBatiments() {return moyenneConvexiteBatiments;}

	/**
	 * @param moyenneConvexiteBatiments moyenne des convexités des bâtiments de la surface bâtie
	 */
	public void setMoyenneConvexiteBatiments(double moyenneConvexiteBatiments) {this.moyenneConvexiteBatiments = moyenneConvexiteBatiments;}

	/**
	 * @return écart type des convexités des bâtiments de la surface bâtie
	 */
	public double getEcartTypeConvexiteBatiments() {return ecartTypeConvexiteBatiments;}

	/**
	 * @param ecartTypeConvexiteBatiments écart type des convexités des bâtiments de la surface bâtie
	 */
	public void setEcartTypeConvexiteBatiments(double ecartTypeConvexiteBatiments) {this.ecartTypeConvexiteBatiments = ecartTypeConvexiteBatiments;}

	/**
	 * @return homogénéité des types fonctionnels des bâtiments
	 */
	public int getHomogeneiteTypesFonctionnelsBatiments() {return homogeneiteTypesFonctionnelsBatiments;}

	/**
	 * @param homogeneiteTypesFonctionnelsBatiments homogénéité des types fonctionnels des bâtiments
	 */
	public void setHomogeneiteTypesFonctionnelsBatiments(int homogeneiteTypesFonctionnelsBatiments) {this.homogeneiteTypesFonctionnelsBatiments = homogeneiteTypesFonctionnelsBatiments;}

	/**
	 * @return homogénéité des tailles des bâtiments
	 */
	public int getHomogeneiteTailleBatiments() {return homogeneiteTailleBatiments;}

	/**
	 * @param homogeneiteTailleBatiments homogénéité des tailles des bâtiments
	 */
	public void setHomogeneiteTailleBatiments(int homogeneiteTailleBatiments) {this.homogeneiteTailleBatiments = homogeneiteTailleBatiments;}

	/**
	 * @return the orientationsPrincipalesBatiments
	 */
	@CollectionOfElements
	public List<Double> getOrientationsPrincipalesBatiments() {return orientationsPrincipalesBatiments;}

	/**
	 * @param orientationsPrincipalesBatiments the orientationsPrincipalesBatiments to set
	 */
	public void setOrientationsPrincipalesBatiments(List<Double> orientationsPrincipalesBatiments) {this.orientationsPrincipalesBatiments = orientationsPrincipalesBatiments;}

	/**
	 * @return the dispersionOrientationsPrincipalesBatiments
	 */
	@CollectionOfElements
	public List<Double> getDispersionOrientationsPrincipalesBatiments() {return dispersionOrientationsPrincipalesBatiments;}

	/**
	 * @param dispersionOrientationsPrincipalesBatiments the dispersionOrientationsPrincipalesBatiments to set
	 */
	public void setDispersionOrientationsPrincipalesBatiments(List<Double> dispersionOrientationsPrincipalesBatiments) {this.dispersionOrientationsPrincipalesBatiments = dispersionOrientationsPrincipalesBatiments;}

	/**
	 * @return the orientationsPrincipalesMursBatiments
	 */
	@CollectionOfElements
	public List<Double> getOrientationsPrincipalesMursBatiments() {return orientationsPrincipalesMursBatiments;}

	/**
	 * @param orientationsPrincipalesMursBatiments the orientationsPrincipalesMursBatiments to set
	 */
	public void setOrientationsPrincipalesMursBatiments(List<Double> orientationsPrincipalesMursBatiments) {this.orientationsPrincipalesMursBatiments = orientationsPrincipalesMursBatiments;}

	/**
	 * @return the dispersionOrientationsPrincipalesMursBatiments
	 */
	@CollectionOfElements
	public List<Double> getDispersionOrientationsPrincipalesMursBatiments() {return dispersionOrientationsPrincipalesMursBatiments;}

	/**
	 * @param dispersionOrientationsPrincipalesMursBatiments the dispersionOrientationsPrincipalesMursBatiments to set
	 */
	public void setDispersionOrientationsPrincipalesMursBatiments(List<Double> dispersionOrientationsPrincipalesMursBatiments) {this.dispersionOrientationsPrincipalesMursBatiments = dispersionOrientationsPrincipalesMursBatiments;}

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

	protected int classificationTailleBatiments;
	/**
	 * @return classificationTailleBatiments
	 */
	public int getClassificationTailleBatiments() {return classificationTailleBatiments;}

	/**
	 * @param classificationTailleBatiments classificationTailleBatiments à Définir
	 */
	public void setClassificationTailleBatiments(int classificationTailleBatiments) {this.classificationTailleBatiments = classificationTailleBatiments;}
	protected int dispersionTailleBatimentsRelativeUniteUrbaine;
	/**
	 * @return dispersionTailleBatimentsRelativeVille
	 */
	public int getDispersionTailleBatimentsRelativeUniteUrbaine() {return dispersionTailleBatimentsRelativeUniteUrbaine;}

	/**
	 * @param dispersionTailleBatimentsRelativeUniteUrbaine dispersionTailleBatimentsRelativeVille à Définir
	 */
	public void setDispersionTailleBatimentsRelativeUniteUrbaine(int dispersionTailleBatimentsRelativeUniteUrbaine) {this.dispersionTailleBatimentsRelativeUniteUrbaine = dispersionTailleBatimentsRelativeUniteUrbaine;}
	protected Densite densiteRelativeUniteUrbaine;
	/**
	 * @return densiteRelativeVille
	 */
	public Densite getDensiteRelativeUniteUrbaine() {return densiteRelativeUniteUrbaine;}

	/**
	 * @param densiteRelativeUniteUrbaine densiteRelativeVille à Définir
	 */
	public void setDensiteRelativeUniteUrbaine(Densite densiteRelativeUniteUrbaine) {this.densiteRelativeUniteUrbaine = densiteRelativeUniteUrbaine;}

	/**
	 * @return bâtiments de la surface bâtie
	 */
	@OneToMany
	public abstract Collection<Batiment> getBatiments();
	/**
	 * @return le nombre de bâtiments appartenant à la surface bâtie
	 */
//	public abstract int sizeBatiments();

	int nombreBatiments = 0;
	/**
	 * @return nombre de bâtiments de la surface bâtie
	 */
	public int getNombreBatiments() {return nombreBatiments;}

	/**
	 * @param nombreBatiments nombre de bâtiments de la surface bâtie
	 */
	public void setNombreBatiments(int nombreBatiments) {this.nombreBatiments = nombreBatiments;}

	@Override
	public void qualifier() {
		super.qualifier();
		// Batiments
		nombreBatiments = this.getBatiments().size();
		if (logger.isDebugEnabled()) logger.debug(nombreBatiments+" batiments dans la zone surfacique");
		//statistiques sur les aires des bâtiments
		List<Double> listeAires = new ArrayList<Double>(0);
		List<Double> listeElongations = new ArrayList<Double>(0);
		List<Double> listeConvexites = new ArrayList<Double>(0);
		List<Double> listeOrientationsBatiment = new ArrayList<Double>(0);
		List<Double> listeOrientationsMurs = new ArrayList<Double>(0);
		for (Batiment bat:getBatiments()) {
			Polygon batPolygon = null;
			try {
				bat.qualifier();
				batPolygon = (Polygon)AdapterFactory.toGeometry(new GeometryFactory(), bat.getGeom());
				listeAires.add(bat.getAire());
				listeElongations.add(JtsUtil.elongation(batPolygon));
				listeConvexites.add(JtsUtil.convexite(batPolygon));
				listeOrientationsBatiment.add(bat.getOrientationGenerale());
				listeOrientationsMurs.add(bat.getOrientationCotes());
			}
			catch (Exception e) {logger.error("Erreur dans la construction de la géométrie "+e.getMessage());}
		}
		minAiresBatiments = MathUtil.min(listeAires);
		maxAiresBatiments = MathUtil.max(listeAires);
		if (logger.isTraceEnabled()) logger.trace("maxAiresBatiments"+maxAiresBatiments);
		if (maxAiresBatiments<0.001) maxAiresBatiments=0;
		moyenneAiresBatiments = MathUtil.moyenne(listeAires);
		ecartTypeAiresBatiments=MathUtil.ecartType(listeAires, moyenneAiresBatiments);
		medianeAiresBatiments = MathUtil.mediane(listeAires);
		// statistiques sur la forme des batiments
		moyenneElongationBatiments = MathUtil.moyenne(listeElongations);
		ecartTypeElongationBatiments = MathUtil.ecartType(listeElongations, moyenneElongationBatiments);
		moyenneConvexiteBatiments = MathUtil.moyenne(listeConvexites);
		ecartTypeConvexiteBatiments = MathUtil.ecartType(listeConvexites, moyenneConvexiteBatiments);
		// statistiques sur l'orientation des bâtiments et des murs des bâtiments
		MesureOrientationFeuille mesureOrientationFeuille = new MesureOrientationFeuille(listeOrientationsBatiment,Math.PI);
		orientationsPrincipalesBatiments = mesureOrientationFeuille.getOrientationsPrincipales();
		dispersionOrientationsPrincipalesBatiments = mesureOrientationFeuille.getDispersionOrientationsPrincipales();
		MesureOrientationFeuille mesureOrientationFeuille2 = new MesureOrientationFeuille(listeOrientationsMurs,Math.PI/2);
		orientationsPrincipalesMursBatiments = mesureOrientationFeuille2.getOrientationsPrincipales();
		dispersionOrientationsPrincipalesMursBatiments = mesureOrientationFeuille2.getDispersionOrientationsPrincipales();
		// homogénéité
		homogeneiteTailleBatiments = HomogeneiteTailleBatiments.Mixte;
		if ((uniteUrbaine!=null)&&!getBatiments().isEmpty()) {
			/*
			 * TODO on utilise la moyenne des aires des bâtiments sur l'unite urbaine comme seuil
			 * pour Déterminer si les bâtiments sont petits ou grands.
			 */
			double seuil = uniteUrbaine.getMoyenneAiresBatiments();
			int nbBatimentsGrands = 0;
			for(Batiment batiment:getBatiments()) if (batiment.getAire()>seuil) nbBatimentsGrands++;
			if (nbBatimentsGrands==0) {
				homogeneiteTailleBatiments = HomogeneiteTailleBatiments.HomogenePetit;
			} else if (nbBatimentsGrands==this.nombreBatiments) {
				homogeneiteTailleBatiments = HomogeneiteTailleBatiments.HomogeneGrand;
			} else if (100*nbBatimentsGrands/this.nombreBatiments>70) {
				homogeneiteTailleBatiments = HomogeneiteTailleBatiments.QuasiHomogeneGrand;
			} else if (100*nbBatimentsGrands/this.nombreBatiments<30) {
				homogeneiteTailleBatiments = HomogeneiteTailleBatiments.QuasiHomogenePetit;
			}
		}

		homogeneiteTypesFonctionnelsBatiments = HomogeneiteTypeFonctionnelBatiments.Heterogene;
		int nbBatimentsHabitat = 0;
		int nbBatimentsPublic = 0;
		int nbBatimentsIndustriel = 0;
		for(Batiment batiment:getBatiments()) {
			if (batiment.getTypeFonctionnel()==TypeFonctionnel.Habitat) nbBatimentsHabitat++;
			else if (batiment.getTypeFonctionnel()==TypeFonctionnel.Public) nbBatimentsPublic++;
			else if (batiment.getTypeFonctionnel()==TypeFonctionnel.Industriel) nbBatimentsIndustriel++;
		}
		if (this.nombreBatiments == 0){
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.Vide);
		} else if (nbBatimentsHabitat==this.nombreBatiments) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.HomogeneHabitat);
		} else if (nbBatimentsPublic==this.nombreBatiments) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.HomogenePublic);
		} else if (nbBatimentsIndustriel==this.nombreBatiments) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.HomogeneIndustriel);
		} else if (100*nbBatimentsHabitat/this.nombreBatiments>70) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.QuasiHomogeneHabitat);
		} else if (100*nbBatimentsPublic/this.nombreBatiments>70) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.QuasiHomogenePublic);
		} else if (100*nbBatimentsIndustriel/this.nombreBatiments>70) {
			this.setHomogeneiteTypesFonctionnelsBatiments(HomogeneiteTypeFonctionnelBatiments.QuasiHomogeneIndustriel);
		}
		// classificationTailleBatiments;
		// dispersionTailleBatimentsRelativeVille;
	}
}

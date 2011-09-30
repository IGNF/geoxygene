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
package fr.ign.cogit.appli.geopensim.feature.micro;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.appli.geopensim.feature.meso.Alignement;
import fr.ign.cogit.appli.geopensim.feature.meso.GroupeBatiments;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationFeuille;
import fr.ign.cogit.geoxygene.util.algo.MesureOrientationV2;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

/**
 * Classe mère pour les bâtiments.
 * @author Julien Perret
 */
@Entity
public abstract class Batiment extends MicroRepresentation {
	static Logger logger=Logger.getLogger(Batiment.class.getName());

	/**
	 * Hauteur du bâtiment.
	 * Vaut -1 si la hauteur n'est pas renseignée.
	 */
	protected int hauteur = 0;
	/**
	 * @return hauteur hauteur du bâtiment
	 */
	public int getHauteur() {return hauteur;}
	/**
	 * @param hauteur hauteur du bâtiment
	 */
	public void setHauteur(int hauteur) {this.hauteur = hauteur;}

	/**
	 * Zone élémentaire urbaine à laquelle appartient le bâtiment.
	 * S'il appartient à plusieurs zones élémentaires urbaines, il est affecté à celle auquel son centroïde appartient.
	 */
//	protected ZoneElementaireUrbaine zoneElementaireUrbaine = null;
	/**
	 * @return Zone élémentaire urbaine à laquelle appartient le bâtiment
	 */
    @ManyToOne(targetEntity = ZoneElementaireUrbaine.class, optional = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    public ZoneElementaireUrbaine getZoneElementaireUrbaine() {
      return this.getGroupeBatiments().getZoneElementaireUrbaine();
      //        return this.zoneElementaireUrbaine;
    }

	/**
	 * @param zone Zone élémentaire urbaine à laquelle appartient le bâtiment
	 */
//    public void setZoneElementaireUrbaine(ZoneElementaireUrbaine zone) {
//        this.zoneElementaireUrbaine = zone;
//    }
    public int getZoneElem() {
        return this.getZoneElementaireUrbaine().getIdGeo();
    }

	/**
	 * Route la plus proche du bâtiment.
	 */
	protected TronconRoute routeLaPlusProche = null;
	/**
	 * @return routeLaPlusProche route la plus proche du bâtiment
	 */
	@ManyToOne
    public TronconRoute getRouteLaPlusProche() {
        return this.routeLaPlusProche;
    }

    /**
     * @param routeLaPlusProche
     *            route la plus proche du bâtiment
     */
    public void setRouteLaPlusProche(TronconRoute routeLaPlusProche) {
        this.routeLaPlusProche = routeLaPlusProche;
    }

    /**
     * Distance à la route la plus proche du bâtiment.
     * vaut -1 si il n'y a pas de route la plus proche
     */
    protected double distanceRouteLaPlusProche = -1;

    /**
     * @return distanceRouteLaPlusProche distance à la route la plus proche du
     *         bâtiment.
     */
    public double getDistanceRouteLaPlusProche() {
        return this.distanceRouteLaPlusProche;
    }

    /**
     * @param distanceRouteLaPlusProche
     *            distance à la route la plus proche du bâtiment.
     */
    public void setDistanceRouteLaPlusProche(double distanceRouteLaPlusProche) {
        this.distanceRouteLaPlusProche = distanceRouteLaPlusProche;
    }

    /**
     * Troncon le plus proche du bâtiment.
     */
    protected Troncon tronconLePlusProche = null;

    /**
     * @return tronconLePlusProche tronçon le plus proche du bâtiment
     */
    @ManyToOne
    public Troncon getTronconLePlusProche() {
        return this.tronconLePlusProche;
    }

	/**
	 * @param tronconLePlusProche tronçon le plus proche du bâtiment
	 */
    public void setTronconLePlusProche(Troncon tronconLePlusProche) {
        this.tronconLePlusProche = tronconLePlusProche;
    }

	/**
	 * Distance au tronçon le plus proche du bâtiment.
	 * vaut -1 si il n'y a pas de troncon le plus proche
	 */
	protected double distanceTronconLePlusProche = -1;
	/**
	 * @return distanceTronconLePlusProche distance au tronçon le plus proche du bâtiment.
	 */
    public double getDistanceTronconLePlusProche() {
        return this.distanceTronconLePlusProche;
    }
	/**
	 * @param distanceTronconLePlusProche distance au tronçon le plus proche du bâtiment.
	 */
    public void setDistanceTronconLePlusProche(
            double distanceTronconLePlusProche) {
        this.distanceTronconLePlusProche = distanceTronconLePlusProche;
    }
	/**
	 * bâtiment le plus proche du bâtiment.
	 */
	protected Batiment batimentLePlusProche = null;
	/**
	 * @return batimentLePlusProche bâtiment le plus proche du bâtiment
	 */
	@ManyToOne
    public Batiment getBatimentLePlusProche() {
        return this.batimentLePlusProche;
    }
    /**
	 * @param batimentLePlusProche bâtiment le plus proche du bâtiment
	 */
    public void setBatimentLePlusProche(Batiment batimentLePlusProche) {
        this.batimentLePlusProche = batimentLePlusProche;
    }

    /**
     * Distance au bâtiment le plus proche du bâtiment.
     * vaut -1 si il n'y a pas de bâtiment le plus proche
     */
    protected double distanceBatimentLePlusProche = -1;
    /**
     * @return distanceBatimentLePlusProche distance au bâtiment le plus proche
     *         du bâtiment
     */
    public double getDistanceBatimentLePlusProche() {
        return this.distanceBatimentLePlusProche;
    }
	/**
	 * @param distanceBatimentLePlusProche distance au bâtiment le plus proche du bâtiment
	 */
	public void setDistanceBatimentLePlusProche(double distanceBatimentLePlusProche) {this.distanceBatimentLePlusProche = distanceBatimentLePlusProche;}

	/**
	 * Liste des surfaces bâties auxquelles appartient le bâtiment.
	 */
	//protected List<ZoneSurfaciqueUrbaine> surfacesBaties = new ArrayList<ZoneSurfaciqueUrbaine>();
	/**
	 * return surfacesBaties surfaces bâties auxquelles appartient le bâtiment
	 */
	@ManyToMany
	//public List<ZoneSurfaciqueUrbaine> getSurfacesBaties() {return surfacesBaties;}
	/**
	 * @param surfacesBaties surfaces bâties auxquelles appartient le bâtiment
	 */
	//public void setSurfacesBaties(List<ZoneSurfaciqueUrbaine> surfacesBaties) {this.surfacesBaties = surfacesBaties;}

	/**
	 * Aire du bâtiment.
	 */
	protected double aire = 0.0;
	/**
	 * @return aire aire du bâtiment
	 */
    public double getAire() {
        return this.aire;
    }
	/**
	 * @param aire aire du bâtiment
	 */
	public void setAire(double aire) {this.aire = aire;}
	//Forme
	protected double elongation;
	protected double convexite;

	/**
	 * @return élongation de la zone surfacique
	 */
    public double getElongation() {
        return this.elongation;
    }

    /**
     * @param elongation
     *            élongation de la zone surfacique
     */
    public void setElongation(double elongation) {
        this.elongation = elongation;
    }
	/**
	 * @return convexite de la zone surfacique
	 */
    public double getConvexite() {
        return this.convexite;
    }
    /**
     * @param convexite
     *            convexite de la zone surfacique
     */
    public void setConvexite(double convexite) {
        this.convexite = convexite;
    }
	/**
	 * biscornuité du bâtiment :
	 * <ul>
	 * <li> aucune_orientation,
	 * <li> plusieurs_orientations ( plusieurs orientations ou un mur différent),
	 * <li> escalier,
	 * <li> rectangulaire
	 * </ul>
	 */
	protected String biscornuite = "";
	/**
	 * @return biscornuité du bâtiment
	 */
    public String getBiscornuite() {
        return this.biscornuite;
    }
    /**
     * @param biscornuite
     *            biscornuité du bâtiment
     */
    public void setBiscornuite(String biscornuite) {
        this.biscornuite = biscornuite;
    }
	/**
	 * Type fonctionnel du bâtiment.
	 */
	protected int typeFonctionnel = TypeFonctionnel.Quelconque;
	/**
	 * @return typeFonctionnel type fonctionnel du bâtiment
	 */
    public int getTypeFonctionnel() {
        return this.typeFonctionnel;
    }
    /**
     * @param typeFonctionnel
     *            type fonctionnel du bâtiment
     */
    public void setTypeFonctionnel(int typeFonctionnel) {
        this.typeFonctionnel = typeFonctionnel;
    }
	/**
	 * centroïde du bâtiment.
	 */
	protected DirectPosition centroid = null;
	/**
	 * TODO voir pour la sauvegarde des DirectPosition dans Postgis !!!
	 * @return centroïde du bâtiment
	 */
	@Transient
    public DirectPosition getCentroid() {
        return this.centroid;
    }
	/**
	 * @param centroid centroïde du bâtiment
	 */
    public void setCentroid(DirectPosition centroid) {
        this.centroid = centroid;
    }
	/**
	 * Orientation générale du bâtiment.
	 */
	protected double orientationGenerale;
	/**
	 * @return orientationGenerale du bâtiment
	 */
    public double getOrientationGenerale() {
        return this.orientationGenerale;
    }
	/**
	 * @param orientationGenerale du bâtiment
	 */
    public void setOrientationGenerale(double orientationGenerale) {
        this.orientationGenerale = orientationGenerale;
    }
	/**
	 * Orientation des côtés du bâtiment.
	 */
	private double orientationCotes;
	/**
	 * @return l'orientation des côtés du bâtiment
	 */
    public double getOrientationCotes() {
        return this.orientationCotes;
    }
	/**
	 * @param orientationCotes l'orientation des côtés du bâtiment
	 */
    public void setOrientationCotes(double orientationCotes) {
        this.orientationCotes = orientationCotes;
    }
	/**
	 * Orientation générale du bâtiment par rapport à la route
	 */
	protected double orientationGeneraleRoute;
	/**
	 * @return orientationGeneraleRoute orientation générale du bâtiment par rapport à la route
	 */
    public double getOrientationGeneraleRoute() {
        return this.orientationGeneraleRoute;
    }
	/**
	 * @param orientationGeneraleRoute orientation générale du bâtiment par rapport à la route
	 */
    public void setOrientationGeneraleRoute(double orientationGeneraleRoute) {
        this.orientationGeneraleRoute = orientationGeneraleRoute;
    }
	/**
	 * Orientation des murs du bâtiment par rapport à la route
	 */
	protected double orientationMursRoute;
	/**
	 * @return orientationMursRoute orientation des murs du bâtiment par rapport à la route
	 */
    public double getOrientationMursRoute() {
        return this.orientationMursRoute;
    }
	/**
	 * @param orientationMursRoute orientation des murs du bâtiment par rapport à la route
	 */
    public void setOrientationMursRoute(double orientationMursRoute) {
        this.orientationMursRoute = orientationMursRoute;
    }
	/**
	 * vrai si la notion d'orientation du bâtiment par rapport à la route a un sens, faux sinon.
	 */
	protected boolean estOrienteRoute;
	/**
	 * @return vrai si la notion d'orientation du bâtiment par rapport à la route a un sens, faux sinon.
	 */
    public boolean getEstOrienteRoute() {
        return this.estOrienteRoute;
    }
	/**
	 * @param estOrienteRoute vrai si la notion d'orientation du bâtiment par rapport à la route a un sens, faux sinon.
	 */
    public void setEstOrienteRoute(boolean estOrienteRoute) {
        this.estOrienteRoute = estOrienteRoute;
    }
	/**
	 * vrai si le batiment est parallèle à la route, faux sinon.
	 */
	protected boolean estParalleleRoute;
	/**
	 * @return vrai si le batiment est parallèle à la route, faux sinon.
	 */
    public boolean getEstParalleleRoute() {
        return this.estParalleleRoute;
    }
	/**
	 * @param estParalleleRoute vrai si le batiment est parallèle à la route, faux sinon.
	 */
    public void setEstParalleleRoute(boolean estParalleleRoute) {
        this.estParalleleRoute = estParalleleRoute;
    }
	private GroupeBatiments groupeBatiments = null;
	/**
	 * @return la valeur de l'attribut groupeBatiments
	 */
	@ManyToOne
    public GroupeBatiments getGroupeBatiments() {
        return this.groupeBatiments;
    }
	/**
	 * @param groupeBatiments l'attribut groupeBatiments à affecter
	 */
    public void setGroupeBatiments(GroupeBatiments groupeBatiments) {
        this.groupeBatiments = groupeBatiments;
    }
	private Set<Alignement> alignements = new HashSet<Alignement>();
	/**
	 * @return la valeur de l'attribut alignements
	 */
	@ManyToMany
    public Set<Alignement> getAlignements() {
        return this.alignements;
    }
    /**
     * @param alignements
     *            l'attribut alignements à affecter
     */
    public void setAlignements(Set<Alignement> alignements) {
        this.alignements = alignements;
    }
	@Override
	public void qualifier() {
	    if (logger.isTraceEnabled()) {
	        logger.trace("Début Qualification Batiment");
	    }
        this.aire = this.geom.area();
		//type fonctionnel
		String natureLowerCase = this.getNature().toLowerCase();
		if (logger.isTraceEnabled()) {
            logger.trace("Nature = "+natureLowerCase);
		}
		if (natureLowerCase.contains("industriel")
		            || (natureLowerCase.contains("activite") && !natureLowerCase.contains("sport"))
		            || natureLowerCase.contains("commercial")
		            || natureLowerCase.contains("serre")
		            || natureLowerCase.contains("silo")) {
		    this.setTypeFonctionnel(TypeFonctionnel.Industriel);
		} else if (natureLowerCase.contains("habitat")
		            || natureLowerCase.contains("maison")) {
		    this.setTypeFonctionnel(TypeFonctionnel.Habitat);
		} else if (natureLowerCase.contains("sport")
		            || natureLowerCase.contains("tribune")
		            || natureLowerCase.contains("religieux")
		            || natureLowerCase.contains("eglise")
                    || natureLowerCase.contains("ecole")
		            || natureLowerCase.contains("chapelle")
		            || natureLowerCase.contains("administratif")
		            || natureLowerCase.contains("transport")
		            || natureLowerCase.contains("mairie")
		            || natureLowerCase.contains("préfecture")
		            || natureLowerCase.contains("gare")
		            || natureLowerCase.contains("fort")) {
			this.setTypeFonctionnel(TypeFonctionnel.Public);
		} else {
			if (!(natureLowerCase.equals("autre")
			            || natureLowerCase.contains("tour"))
			            && logger.isTraceEnabled()) {
			    logger.trace(natureLowerCase+" n'est pas reconnu comme type fonctionnel");
	            this.setTypeFonctionnel(TypeFonctionnel.Quelconque);
			}
		}
		if (logger.isTraceEnabled()) {
		    logger.trace("Type fonctionnel = "+this.getTypeFonctionnel());
		}
		//Forme
		Polygon polygon = null;
		try {polygon = (Polygon)AdapterFactory.toGeometry(new GeometryFactory(), this.getGeom());}
		catch (Exception e) {
			logger.error("Erreur pendant la qualification du batiment "+this);
			logger.error("Erreur dans la construction de la géométrie "+e.getMessage());
			logger.error("La géométrie du bâtiment n'est pas un polygone");
			logger.error(this.getGeom());
			logger.error("La qualification du bâtiment est abandonnée");
			return;
		}
		if (polygon!=null) {
		    this.elongation = JtsUtil.elongation(polygon);
		    this.convexite =  JtsUtil.convexite(polygon);
			Polygon ppre = JtsUtil.PPRE(polygon);
			Point point = ppre.getCentroid();
			this.centroid = new DirectPosition(point.getX(), point.getY());
			this.orientationGenerale = MesureOrientationV2.getOrientationGenerale(polygon);
			this.biscornuite = MesureOrientationV2.getBiscornuite(polygon);
			MesureOrientationFeuille mesureOrientation = new MesureOrientationFeuille(polygon,Math.PI * 0.5);
			this.orientationCotes = mesureOrientation.getOrientationPrincipale();
		}
		// contexte
		// détection de la route la plus proche
		double distanceMin = Double.MAX_VALUE;
		if (this.getZoneElementaireUrbaine()==null) {
		    if(logger.isTraceEnabled()) {
		        logger.trace(this+" ne possède pas de zone élémentaire");
		    }
		} else {
			if (logger.isTraceEnabled()) {
			    logger.trace(this.getZoneElementaireUrbaine()
			                + " possède "+this.getZoneElementaireUrbaine().getTroncons().size()
			                + " troncons");
			}
			for  (Troncon troncon : this.getZoneElementaireUrbaine().getTroncons()) {
				if (logger.isTraceEnabled()) {
				    logger.trace("troncon de type "+troncon.getClass());
				}
				if ((TronconRoute.class.isAssignableFrom(troncon.getClass()))){//)||
//					(TronconChemin.class.isAssignableFrom(troncon.getClass()))){
					double distance = troncon.getGeom().distance(this.getGeom());
					if (distance < distanceMin) {
						distanceMin = distance;
						if (logger.isTraceEnabled()) {
						    logger.trace("affecte la route la plus proche");
						}
						this.setRouteLaPlusProche((TronconRoute)troncon);
						this.setDistanceRouteLaPlusProche(distanceMin);
					}
				}
			}
			if (logger.isTraceEnabled() && (this.getRouteLaPlusProche()==null)) {
			    logger.trace(this+" Route la plus proche non affectée");
			}
		}
		// détection du tronçon le plus proche
		double distanceMinTroncon = Double.MAX_VALUE;
		if (this.getZoneElementaireUrbaine()==null) {
			if(logger.isDebugEnabled()) logger.debug(this+" ne possède pas de zone élémentaire");
		} else {
			if (logger.isTraceEnabled()) logger.trace(this.getZoneElementaireUrbaine()+" possède "+this.getZoneElementaireUrbaine().getTroncons().size()+" troncons");
			for (Troncon troncon:this.getZoneElementaireUrbaine().getTroncons()) {
				double distance = troncon.getGeom().distance(this.getGeom());
				if (distance < distanceMinTroncon) {
					distanceMinTroncon = distance;
					if (logger.isTraceEnabled()) logger.trace("affecte le tronçon le plus proche");
					this.setTronconLePlusProche(troncon);
					this.setDistanceTronconLePlusProche(distanceMinTroncon);
				}
			}
			if (logger.isDebugEnabled()&&(this.getTronconLePlusProche()==null)) logger.debug(this+" tronçon le plus proche non affecté");
		}
		// Détection du batiment le plus proche
		double distanceMinBatiment = Double.MAX_VALUE;
		if (this.getZoneElementaireUrbaine()==null) {
			if(logger.isDebugEnabled()) logger.debug(this+" ne possède pas de zone élémentaire");
		} else {
			if (logger.isTraceEnabled()) logger.trace(this.getZoneElementaireUrbaine()+" possède "+this.getZoneElementaireUrbaine().getBatiments().size()+" troncons");
			for (Batiment batiment:this.getZoneElementaireUrbaine().getBatiments()) {
				double distance = batiment.getGeom().distance(this.getGeom());
				if ((distance < distanceMinBatiment)&&(!batiment.equals(this))) {
					distanceMinBatiment = distance;
					if (logger.isTraceEnabled()) logger.trace("affecte le batiment le plus proche");
					this.setBatimentLePlusProche(batiment);
					this.setDistanceBatimentLePlusProche(distanceMinBatiment);
				}
			}
			if (logger.isTraceEnabled()&&(this.getBatimentLePlusProche()==null)) logger.trace(this+" Batiment le plus proche non affecté");
		}
		// Orientation par rapport à la route la plus proche
		if (polygon==null) return;
		if (this.getRouteLaPlusProche()==null) {
			if (logger.isTraceEnabled()) {
			    logger.trace(this+" ne possède pas de route la plus proche");
			    logger.trace("Fin Qualification Batiment");
			}
			return;
		}
		GM_LineString geometrieRoute = (GM_LineString)this.getRouteLaPlusProche().getGeom();
		double orientationRoute = JtsUtil.projectionPointOrientationTroncon(this.getCentroid(), geometrieRoute);
		// Orientation générale du bâtiment par rapport à la route (en radian, dans l'intervalle ]-Pi/2, Pi/2], par rapport a l'axe Ox)
		this.orientationGeneraleRoute = this.getOrientationGenerale() - orientationRoute;
		if (this.orientationGeneraleRoute<=-0.5*Math.PI) this.orientationGeneraleRoute += Math.PI;
		else if (this.orientationGeneraleRoute>0.5*Math.PI) this.orientationGeneraleRoute -= Math.PI;
		// Orientation des murs du bâtiment par rapport à la route (en radian, dans l'intervalle ]-Pi/4, Pi/4], par rapport a l'axe Ox)
		this.orientationMursRoute = this.getOrientationCotes() - orientationRoute;
		if (this.orientationMursRoute<=-0.5*Math.PI) this.orientationMursRoute += Math.PI*0.5;
		else if (this.orientationMursRoute>0.5*Math.PI) this.orientationMursRoute -= Math.PI*0.5;
		if (this.orientationMursRoute<=-Math.PI/4) this.orientationMursRoute += Math.PI*0.5;
		else if (this.orientationMursRoute>Math.PI/4) this.orientationMursRoute -= Math.PI*0.5;
		// Sens de la notion d'orientation du batiment par rapport à la route
		if ((this.biscornuite.contains("aucune_orientation"))||(this.biscornuite.contains("plusieurs_orientations"))){
		    this.estOrienteRoute = false;
		}
		else{this.estOrienteRoute = true;}
		// Test le fait que le batiment soit parallèle à la route
		if (((this.biscornuite.contains("rectangulaire_mur_droit"))||(this.biscornuite.contains("rectangulaire_mur_peu_droit"))||
				(this.biscornuite.contains("un_mur_different")))&&(Math.abs(this.orientationMursRoute)<Math.PI*15/180)){
		    this.estParalleleRoute = true;
		}
		else {this.estParalleleRoute = false;}

		if (logger.isTraceEnabled()) logger.trace("Fin Qualification Batiment");
	}
}

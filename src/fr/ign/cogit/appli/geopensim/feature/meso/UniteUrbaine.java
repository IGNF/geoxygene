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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.index.Tiling;


/**
 * Cette classe représente les villes.
 * @author Julien Perret
 *
 */
@Entity
public class UniteUrbaine extends ZoneSurfaciqueUrbaine implements Unite<ZoneElementaireUrbaine,ZoneAgregeeUrbaine> {
	//static Logger logger=Logger.getLogger(UniteUrbaine.class.getName());

    UniteImpl<ZoneElementaireUrbaine,ZoneAgregeeUrbaine> uniteImpl = new UniteImpl<ZoneElementaireUrbaine,ZoneAgregeeUrbaine>();

    public static final int Hameau = 0;
    public static final int Bourg = 1;
    public static final int VilleMoyenne = 2;
    public static final int GrandeVille = 3;

    public static final int Ha = 10000;

    //double densite;
    double moyenneDensiteZonesElementaires;

    int typeSelonTaille;

    /**
     * Constructeur vide
     */
    public UniteUrbaine() {
        super();
    }

    /**
     * @return une nouvelle Unité urbaine
     */
    public static UniteUrbaine newInstance() {
        return new UniteUrbaine();
    }

    /**
     * @return moyenneDensiteZonesElementaires
     */
    public double getMoyenneDensiteZonesElementaires() {
        return this.moyenneDensiteZonesElementaires;
    }

    /**
     * @param moyenneDensiteZonesElementaires
     *            moyenneDensiteZonesElementaires à Définir
     */
    public void setMoyenneDensiteZonesElementaires(
            double moyenneDensiteZonesElementaires) {
        this.moyenneDensiteZonesElementaires = moyenneDensiteZonesElementaires;
    }

    /**
     * @return typeSelonTaille
     */
    public int getTypeSelonTaille() {
        return this.typeSelonTaille;
    }

    /**
     * @param typeSelonTaille typeSelonTaille à Définir
     */
    public void setTypeSelonTaille(int typeSelonTaille) {
        this.typeSelonTaille = typeSelonTaille;
    }

    @Override
    public void qualifier() {
    	// pour la requalification des unites urbaines
    	if (this.getBatiments().isEmpty()) {
    		// si on a pas de batiment, c'est que le lien a été perdu et il faut le reconstruire
    		// FIXME
    	}
    	super.qualifier();
    	// calcul de la densite
    	/*
    	 * TODO Pour l'instant, la densité de la ville est héritée de la densité de la surface bâtie, i.e.
    	 * c'est la somme des aires des bâtiments sur la surface de la ville.
    	 */
    	if (logger.isDebugEnabled()) {
    	    logger.debug(this.getZonesElementaires().size()+" zones élémentaires");
    	}
        for (ZoneElementaireUrbaine zone : this.getZonesElementaires()) {
            zone.qualifier();
        }
        // calcul de la moyennes de la densite des ilots
        this.moyenneDensiteZonesElementaires = 0.0;
        for (ZoneElementaireUrbaine zone : this.getZonesElementaires()) {
            this.moyenneDensiteZonesElementaires += zone.getDensite();
        }
        if (this.getZonesElementaires().size() > 0) {
            this.moyenneDensiteZonesElementaires /= this.getZonesElementaires()
                    .size();
        }
        // calcul du type selon la taille de la ville
        if (this.getAire()<50*Ha) {
            this.setTypeSelonTaille(UniteUrbaine.Hameau);
        } else {
            if (this.getAire()<100*Ha) {
                this.setTypeSelonTaille(UniteUrbaine.Bourg);
            } else {
                if (this.getAire()<1000*Ha) {
                    this.setTypeSelonTaille(UniteUrbaine.VilleMoyenne);
                } else {
                    this.setTypeSelonTaille(UniteUrbaine.GrandeVille);
                }
            }
        }
    	//this.qualifyCenter();
    }

    /**
     *
     */
    private void qualifyCenter() {
        // calcul du centre ville
        double seuilDensite = 0.8;
        double seuilDensiteVoisin = 0.5;
        int idGroupe = 1;
        Map<Integer, FT_FeatureCollection<ZoneElementaireUrbaine>> groupes = new HashMap<Integer, FT_FeatureCollection<ZoneElementaireUrbaine>>();
        // affectation de l'id du groupe
        for (ZoneElementaireUrbaine zone : this.getZonesElementaires()) {
            if (zone.getDensite() >= seuilDensite) {
                zone.setDistanceAuCentre(idGroupe);
                FT_FeatureCollection<ZoneElementaireUrbaine> nouveauVoisins = new FT_FeatureCollection<ZoneElementaireUrbaine>();
                nouveauVoisins.add(zone);
                groupes.put(idGroupe, nouveauVoisins);
                idGroupe++;
            } else {
                zone.setDistanceAuCentre(0);
            }
        }
        int nbGroupes = groupes.keySet().size();
        boolean change = true;
        while (change) {
            if (logger.isDebugEnabled()) {
                logger.debug(nbGroupes+" groupes");
            }
            change=false;
            for(Integer groupe:groupes.keySet()) {
                if (groupes.get(groupe).isEmpty()) {
                    continue;
                }
                IPolygon convexHull = JtsUtil.convexHull(groupes.get(groupe));
                if (convexHull == null) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("convexHull null à partir de "
                                + groupes.get(groupe).getGeomAggregate()
                                + " construit avec "
                                + groupes.get(groupe).size() + " features");
                    }
                }
                List<ZoneElementaireUrbaine> nouveauVoisins = new ArrayList<ZoneElementaireUrbaine>();
                for (ZoneElementaireUrbaine zone : groupes.get(groupe)) {
                    for (ZoneElementaire voisin : zone.getVoisins()) {
                        if (voisin instanceof ZoneElementaireUrbaine) {
                            int groupeVoisin = ((ZoneElementaireUrbaine) voisin)
                                    .getDistanceAuCentre();
                            if (groupeVoisin == 0) {
                                if ((((ZoneElementaireUrbaine) voisin)
                                        .getDensite() >= seuilDensiteVoisin)
                                        || ((convexHull != null) && (convexHull
                                                .contains(voisin.getGeom())))) {
                                    ((ZoneElementaireUrbaine) voisin)
                                            .setDistanceAuCentre(groupe);
                                    nouveauVoisins
                                            .add(((ZoneElementaireUrbaine) voisin));
                                }
                            } else
                                if (groupeVoisin != groupe) {
                                    // appartient à un autre groupe
                                    if (groupes.get(groupeVoisin) != null) {
                                        for (ZoneElementaireUrbaine nouveauVoisin : groupes
                                                .get(groupeVoisin)) {
                                            nouveauVoisin
                                                    .setDistanceAuCentre(groupe);
                                            nouveauVoisins.add(nouveauVoisin);
                                        }
                                        groupes.get(groupeVoisin).clear();
                                    }
                                }
                        }
                    }
                }
                if (!nouveauVoisins.isEmpty()) {
                    groupes.get(groupe).addAll(nouveauVoisins);
                    change = true;
                }
            }
            //nettoyage
            List<Integer> groupesASupprimer = new ArrayList<Integer>();
            for(Integer groupe:groupes.keySet()) if (groupes.get(groupe).isEmpty()) groupesASupprimer.add(groupe);
            for(Integer groupe:groupesASupprimer) groupes.remove(groupe);
            //noChange=(nbGroupes == groupes.keySet().size());
            nbGroupes = groupes.keySet().size();
        }// for
        logger.info(nbGroupes+" centres");
        // FIXME gérer le centre avec une base de données extérieure ? le demander à l'utilisateur ?
        if((nbGroupes>0)&&(groupes.values().iterator().next().size()>0)) {
            IPolygon contourCentre = JtsUtil.convexHull(groupes.values().iterator().next());
            logger.info("contourCentre = "+contourCentre);
            IDirectPosition centreVille = contourCentre.centroid();
            int nbArcs = 100;
            double increment = 2*Math.PI/nbArcs;
            logger.info("centreVille = "+centreVille);
            double tailleSegment = 100000;
            List<GM_Point> points = new ArrayList<GM_Point>();
            for (int i = 0; i < nbArcs; i++) {
                double angle = i * increment;
                // logger.info("angle = "+angle);
                IDirectPosition extremite = new DirectPosition(centreVille
                        .getX()
                        + tailleSegment * Math.cos(angle), centreVille.getY()
                        + tailleSegment * Math.sin(angle));
                // logger.info("extremite = "+extremite);
                GM_LineString line = new GM_LineString(new DirectPositionList(
                        Arrays.asList(centreVille, extremite)));
                IGeometry intersection = line.intersection(contourCentre
                        .exteriorLineString());
                // logger.debug("intersection 1 = "+intersection);
                GM_Point p1 = (GM_Point) ((intersection instanceof GM_Point) ? intersection
                        : new GM_Point(intersection.coord().get(0)));
                intersection = line.intersection(((GM_Polygon) getGeom())
                        .exteriorLineString());
                // logger.debug("intersection 2 = "+intersection);
                GM_Point p2 = (GM_Point) ((intersection instanceof GM_Point) ? intersection
                        : new GM_Point(intersection.coord().get(0)));
                points.add(p1);
                points.add(p2);
            }
            List<GM_Polygon> listeAnneaux = new ArrayList<GM_Polygon>();
            int nbAnneaux = 2;
            for (int anneau = 0; anneau < nbAnneaux; anneau++) {
                DirectPositionList liste = new DirectPositionList();
                for (int i = 0; i < nbArcs; i++) {
                    GM_Point p1 = points.get(2 * i);
                    GM_Point p2 = points.get(2 * i + 1);
                    liste.add(new DirectPosition(p1.getPosition().getX()
                            + (p2.getPosition().getX() - p1.getPosition()
                                    .getX()) * (anneau + 1) / (nbAnneaux + 1),
                            p1.getPosition().getY()
                                    + (p2.getPosition().getY() - p1
                                            .getPosition().getY())
                                    * (anneau + 1) / (nbAnneaux + 1)));
                }
                liste.add(liste.get(0));
                GM_Polygon polygone = new GM_Polygon(new GM_LineString(liste));
                logger.info("ligne " + anneau + " = "
                        + new GM_LineString(liste));
                logger.info("polygone " + anneau + " = " + polygone);
                listeAnneaux.add(polygone);
            }
            for (ZoneElementaireUrbaine zone : this.getZonesElementaires()) {
                zone.setDistanceAuCentre(nbAnneaux + 1);
            }
            FT_FeatureCollection<ZoneElementaireUrbaine> zones = new FT_FeatureCollection<ZoneElementaireUrbaine>(
                    this.getZonesElementaires());
            zones.initSpatialIndex(Tiling.class, false);
            // anneaux
            for (int anneau = nbAnneaux-1 ; anneau >= 0 ; anneau--) {
                for(ZoneElementaireUrbaine zone:zones.select(listeAnneaux.get(anneau))) {
                    if(listeAnneaux.get(anneau).contains(zone.getGeom())) zone.setDistanceAuCentre(anneau+1);
                }
            }
            // centre ville
            for(ZoneElementaireUrbaine zone:zones.select(contourCentre)) {
                if(contourCentre.contains(zone.getGeom())) zone.setDistanceAuCentre(0);
            }
        } else {
            // on a pas de centre ville
            for(ZoneElementaireUrbaine zone:this.getZonesElementaires()) {
                zone.setDistanceAuCentre(3);
            }
        }

        // qualification externe des zones élémentaires : Détermination du max de la densité des voisins
        for(ZoneElementaireUrbaine zone:this.getZonesElementaires()) {
            double maxDensiteVoisins = 0;
            for(ZoneElementaire voisin:zone.getVoisins()) {
                if (voisin instanceof ZoneElementaireUrbaine) {
                    ZoneElementaireUrbaine voisinUrbain = (ZoneElementaireUrbaine) voisin;
                    maxDensiteVoisins=Math.max(maxDensiteVoisins,voisinUrbain.getDensite());
                }
            }
            zone.setMaxDensiteVoisins(maxDensiteVoisins);
        }
    }

    /**
     * Construire les zones élémentaires en utilisant une carte topologique et
     * une date associée aux zones créées.
     * 
     * @param carteTopo
     *            carte topologique utilisée
     * @param date
     *            date utilisée
     */
    public void construireZonesElementaires(CarteTopo carteTopo, int date) {
        // parcours des îlots
        for (Face face : carteTopo.getPopFaces().select(this.getGeom())) {
            IPolygon polygone = face.getGeometrie();
            if (!this.getGeom().contains(polygone.buffer(-0.01))) {
                if (logger.isDebugEnabled()) {
                    logger
                            .debug("zone élémentaire non conservée car hors de l'Unité urbaine : "
                                    + polygone);
                }
                continue;
            }
            try {
                polygone = (GM_Polygon) AdapterFactory.to2DGM_Object(polygone);
            } catch (Exception e) {
                logger
                        .error("Echec pendant la convertion de la géométrie de la zone élémentaire en 2D : "
                                + e.getMessage());
                logger.error(polygone.toString());
                continue;
            }
            if (polygone.area() < 0.001) {
                if (logger.isDebugEnabled()) {
                    logger
                            .debug("zone élémentaire non conservée car trop petite");
                }
                continue;
            }
            ZoneElementaireUrbaine zoneElementaire = ZoneElementaireUrbaine
                    .newInstance(polygone);
            boolean bordeUniteUrbaine = false;
            List<Arc> arcs = face.getArcsDirects();
            arcs.addAll(face.getArcsIndirects());
            for (Arc arc : arcs) {
                for (IFeature feature : arc.getCorrespondants()) {
                    // si l'un des tronçons est un defaultfeature, c'est un
                    // contour de ville
                    if (feature instanceof DefaultFeature) {
                        bordeUniteUrbaine = true;
                    } else {
                        zoneElementaire.addTroncon((Troncon) feature);
                    }
                }
            }

            zoneElementaire.setBordeUniteUrbaine(bordeUniteUrbaine);
            zoneElementaire.setDateSourceSaisie(date);
            this.addZoneElementaire(zoneElementaire);
            /*
             * On affecte la zone élémentaire crée comme correspondant de sa
             * face dans la carte topo
             * Ce lien est notamment utilisé pendant la création des carrefours
             * comme optimisation pour éviter d'avoir à refaire une requête
             * savoir si la face contient des bâtiments
             */
            face.addCorrespondant(zoneElementaire);
            if (logger.isDebugEnabled()) {
                logger.debug(zoneElementaire + " s'est fait ajouter "
                        + zoneElementaire.getTroncons().size() + " troncons");
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug(this.getZonesElementaires().size()
                    + " zones élémentaires créées");
        }
    }

    @OneToMany(targetEntity = ZoneElementaireUrbaine.class)
    @org.hibernate.annotations.Cascade( { org.hibernate.annotations.CascadeType.ALL })
    public List<ZoneElementaireUrbaine> getZonesElementaires() {
        return this.uniteImpl.getZonesElementaires();
    }
    public void setZonesElementaires(
            List<ZoneElementaireUrbaine> zonesElementaires) {
        for (ZoneElementaireUrbaine zone : this.getZonesElementaires()) {
            zone.setUniteUrbaine(null);
        }
        this.uniteImpl.setZonesElementaires(zonesElementaires);
        for (ZoneElementaireUrbaine zone : this.getZonesElementaires()) {
            zone.setUniteUrbaine(this);
        }
    }

    public void addZoneElementaire(ZoneElementaireUrbaine zone) {
        this.uniteImpl.addZoneElementaire(zone);
        zone.setUniteUrbaine(this);
    }

    public void removeZoneElementaire(ZoneElementaireUrbaine zone) {
        this.uniteImpl.removeZoneElementaire(zone);
        zone.setUniteUrbaine(null);
    }

    public void emptyZonesElementaires() {
        this.uniteImpl.emptyZonesElementaires();
    }

    public void setZonesAgregees(List<ZoneAgregeeUrbaine> agreg) {
        for (ZoneAgregeeUrbaine zone : this.getZonesAgregees()) {
            zone.setUniteUrbaine(null);
        }
        this.uniteImpl.setZonesAgregees(agreg);
        for (ZoneAgregeeUrbaine zone : this.getZonesAgregees()) {
            zone.setUniteUrbaine(this);
        }
    }

    @OneToMany(targetEntity = ZoneAgregeeUrbaine.class)
    public List<ZoneAgregeeUrbaine> getZonesAgregees() {
        return this.uniteImpl.getZonesAgregees();
    }

    @Override
    public String toString() {
        String s = "Unité urbaine " + this.getId() + " contenant "
                + this.getZonesElementaires().size() + " zones élémentaires\n";
        for (ZoneElementaireUrbaine zone : this.getZonesElementaires()) {
            s += zone.toString();
        }
        return s;
    }

    @Override
    public List<Batiment> getBatiments() {
        List<Batiment> batiments = new ArrayList<Batiment>();
        for (ZoneElementaireUrbaine zone : this.getZonesElementaires())
            batiments.addAll(zone.getBatiments());
        return batiments;
    }
//
//    @Override
//    public int sizeBatiments() {
//        int nbBatiments = 0;
//        for (ZoneElementaireUrbaine zone : this.getZonesElementaires())
//            nbBatiments += zone.sizeBatiments();
//        return nbBatiments;
//    }
}

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
package fr.ign.cogit.appli.geopensim.feature.macro;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.event.EventListenerList;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.operation.buffer.BufferParameters;

import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicRepresentationFactory;
import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaire;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.appli.geopensim.feature.micro.Batiment;
import fr.ign.cogit.appli.geopensim.feature.micro.Carrefour;
import fr.ign.cogit.appli.geopensim.feature.micro.Troncon;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconChemin;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconCoursEau;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconRoute;
import fr.ign.cogit.appli.geopensim.feature.micro.TronconVoieFerree;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.util.algo.JtsAlgorithms;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * Population d'Unités urbaines contenant, de plus, une Unité péri-urbaine.
 *
 * @author Julien Perret
 *
 */
public class PopulationUnites extends MacroRepresentation<UniteUrbaine> {
	//static Logger logger=Logger.getLogger(PopulationUnites.class.getName());

	private GeometryFactory geomFactory = new GeometryFactory();

	UniteUrbaine unitePeriUrbaine = UniteUrbaine.newInstance();

	PopulationBatiments populationBatiments = null;

	PopulationTronconsRoute populationRoutes = null;

	PopulationTronconsChemin populationChemins = null;

	PopulationTronconsVoieFerree populationVoiesFerrees = null;

	PopulationTronconsHydrographiques populationHydrographiques = null;

	PopulationZonesElementaires populationZonesElementaires = null;

	Population<Carrefour> populationCarrefours = null;

	GeometryFactory factory = new GeometryFactory();

	Collection<?> polygons = null;

	CarteTopo carteTopo = new CarteTopo("carte");

	private boolean storeDB=false;

	protected EventListenerList listenerList = new EventListenerList();

	/**
	 * Adds an <code>ActionListener</code> to the button.
	 * @param l the <code>ActionListener</code> to be added
	 */
	public void addActionListener(ActionListener l) {
		listenerList.add(ActionListener.class, l);
	}

	/**
	 * Returns the list of action listeners.
	 * @return the list of action listeners
	 */
	public EventListenerList getActionListeners() {return this.listenerList;}

	/**
	 * Notifies all listeners that have registered interest for
	 * notification on this event type.  The event instance
	 * is lazily created.
	 * @see EventListenerList
	 */
	protected void fireActionPerformed(ActionEvent event) {
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
	 * @param classeBatiments
	 * @param classeRoutes
	 * @param classeChemins
	 * @param classeVoieFerree
	 * @param classeHydrographique
	 * @param date
	 */
	public PopulationUnites(Class<? extends Batiment> classeBatiments, Class<? extends TronconRoute> classeRoutes,
			Class<? extends TronconChemin> classeChemins, Class<? extends TronconVoieFerree> classeVoieFerree,
			Class<? extends TronconCoursEau> classeHydrographique, int date) {
		super(UniteUrbaine.class,date);
		this.populationBatiments = new PopulationBatiments(classeBatiments,date);
		this.populationRoutes = new PopulationTronconsRoute(classeRoutes,date);
		this.populationChemins = new PopulationTronconsChemin(classeChemins,date);
		this.populationVoiesFerrees = new PopulationTronconsVoieFerree(classeVoieFerree,date);
		this.populationHydrographiques = new PopulationTronconsHydrographiques(classeHydrographique,date);
		this.populationZonesElementaires = new PopulationZonesElementaires(date);
		this.populationCarrefours = new Population<Carrefour>();
	}

	/**
	 * @param classeBatiments
	 * @param classeRoutes
	 * @param classeChemins
	 * @param classeVoieFerree
	 * @param classeHydrographique
	 */
	public PopulationUnites(Class<? extends Batiment> classeBatiments, Class<? extends TronconRoute> classeRoutes,
			Class<? extends TronconChemin> classeChemins, Class<? extends TronconVoieFerree> classeVoieFerree,
			Class<? extends TronconCoursEau> classeHydrographique) {
		super(UniteUrbaine.class);

		this.populationBatiments = new PopulationBatiments(classeBatiments);
		this.populationRoutes = new PopulationTronconsRoute(classeRoutes);
		this.populationChemins = new PopulationTronconsChemin(classeChemins);
		this.populationVoiesFerrees = new PopulationTronconsVoieFerree(classeVoieFerree);
		this.populationHydrographiques = new PopulationTronconsHydrographiques(classeHydrographique);
		this.populationZonesElementaires = new PopulationZonesElementaires();
		this.populationCarrefours = new Population<Carrefour>();
	}

	@Override
	public void chargerElements() {
		super.chargerElements();
		this.populationBatiments.chargerElements();
		this.populationRoutes.chargerElements();
		this.populationChemins.chargerElements();
		this.populationVoiesFerrees.chargerElements();
		this.populationHydrographiques.chargerElements();
		this.populationZonesElementaires.chargerElements();
		this.populationCarrefours.chargeElements();
	}

	/**
	 * Contruire les villes par agrégation des buffers sur les bâtiments
	 * Cette Méthode Définit les paramètres par Défaut :
	 * 25m pour la taille des buffers,
	 * 3 segments comme approximation des courbes
	 * le style arrondi pour les terminaisons de segments
	 * une tolérance d'1m pour la simplification des contours des villes par douglas peucker  .
	 * @see #construireUnites(double, int, int, double)
	 */
	public void construireUnites() {
		if (this.populationBatiments == null || this.populationBatiments.isEmpty()) {
			logger.error("Population de bâtiments vide ou null");
			return;
		} else {
			if (storeDB) this.sauverPopulations();
			logger.info("--- Contruction des villes --- ");
			if (!this.isExtraction()) {
			    this.construireUnites(25.0, 4, BufferParameters.CAP_ROUND, 1.0);
			} else {
				logger.info("Les données font partie d'une extraction");
				IEnvelope envelope = this.populationRoutes.envelope();
				envelope.expand(this.populationChemins.envelope());
				envelope.expand(this.populationHydrographiques.envelope());
				envelope.expand(this.populationVoiesFerrees.envelope());
                envelope.expand(this.populationBatiments.envelope());
                double expandedSize = 100.0;
                GM_Polygon polygone = new GM_Polygon(new GM_Envelope(envelope
                        .minX() - expandedSize, envelope.maxX() + expandedSize,
                        envelope.minY() - expandedSize, envelope.maxY() + expandedSize));
				UniteUrbaine uniteUrbaine = UniteUrbaine.newInstance();
				uniteUrbaine.setDateSourceSaisie(this.date);
				uniteUrbaine.setGeom(polygone);
				this.add(uniteUrbaine);
                GM_Polygon polygonePeriUrbain = new GM_Polygon(new GM_Envelope(
                        envelope.minX() - 2 * expandedSize, envelope.maxX() + 2
                                * expandedSize, envelope.minY() - 2
                                * expandedSize, envelope.maxY() + 2
                                * expandedSize));
				polygonePeriUrbain.addInterior(((GM_Polygon)uniteUrbaine.getGeom()).getExterior());
				this.unitePeriUrbaine.setGeom(polygonePeriUrbain);
				this.unitePeriUrbaine.setDateSourceSaisie(this.date);
			}
			this.construireZonesElementaires();
			//this.construireGroupesBatiments();
			/**
			 * TODO ajouter une Méthode contruire Groupes de routes ou le
			 * faire pendant la construction des zones élémentaires
			 */
			//this.construireCarrefours();
		}
	}

//	/**
//	 * Construit les groupes de bâtiments de la population
//	 * et lance le calcul des alignements dans ces groupes.
//	 */
//	public void construireGroupesBatiments() {
//		if (logger.isDebugEnabled()) {
//		    logger.debug("Début construireGroupesBatiments");
//		}
//		int i = 0;
//		for(ZoneElementaire zone:this.getPopulationZonesElementaires()) {
//			if (ZoneElementaireUrbaine.class.isAssignableFrom(zone.getClass())) {
//	            if (logger.isDebugEnabled()) {
//	                logger.debug("ConstruireGroupesBatiments pour Zone " + (i++) + " / " + this.getPopulationZonesElementaires().size());
//	            }
//				((ZoneElementaireUrbaine)zone).construireGroupes();
//			}
//		}
//		if (logger.isDebugEnabled()) {
//		    logger.debug("Fin construireGroupesBatiments");
//		}
//	}

	/**
	 * Contruire les villes par agrégation des buffers sur les bâtiments
	 * @param distance taille des buffers construits autour des bâtiments
	 * @param quadrantSegments approximation des courbes pendant la construction des buffers
	 * @param endCapStyle style utilisé pour les terminaisons de segments pendant la construction des buffers
	 * @param distanceTolerance tolérance utilisée pour la simplification des contours des villes par l'algorithme de Douglas Peucker
	 */
	public void construireUnites(double distance, int quadrantSegments, int endCapStyle, double distanceTolerance) {
		logger.info("Creation buffers de batiments");

		Geometry[] GeometryList = this.populationBatiments.buffersBatiments(distance, quadrantSegments,endCapStyle);
		System.gc();
		logger.info("il y a "+this.populationBatiments.size()+" batiments et "+GeometryList.length+" buffers");

		//logger.info("Creation de la collection");
		//GeometryCollection bufferCollection = geomFactory.createGeometryCollection(GeometryList);
		logger.info("Creation des villes");
		Geometry buffer = JtsAlgorithms.union(GeometryList);//bufferCollection.buffer(0.0, quadrantSegments, endCapStyle);
		GeometryList = null;
		System.gc();

		logger.info("Fermeture des villes");
		buffer = JtsUtil.fermetureSuppTrous(buffer, distance, quadrantSegments, endCapStyle);

		logger.info("agrégation des villes");
		if (!( (buffer instanceof MultiPolygon) || (buffer instanceof Polygon) ) ) {
			logger.info("Buffer construit à partir des batiments de type non gere : "+buffer.getGeometryType());
		}

		this.clear();
		List<Geometry> villesMoyennesEtGrandes = new ArrayList<Geometry>();
		List<Geometry> villesPetites = new ArrayList<Geometry>();
		MultiPolygon jtsVilles;
		if (buffer instanceof Polygon) {
			// il n'y a qu'une seule ville
			Polygon geom = (Polygon)buffer;
			LinearRing exterior = (LinearRing) geom.getExteriorRing();
			Polygon boundary = (Polygon) geomFactory.createPolygon(exterior, null).buffer(0.0, quadrantSegments, endCapStyle);
			if (boundary.getArea()>1000000) {
				geom = (Polygon)boundary.buffer(distance, quadrantSegments, endCapStyle);
				geom = JtsUtil.fermeture(geom, distance, distanceTolerance, quadrantSegments, endCapStyle, geomFactory);
				villesMoyennesEtGrandes.add(geom);
			} else {
				geom = boundary;
				villesPetites.add(geom);
			}
		} else {
			// il y en a plusieurs
			jtsVilles = (MultiPolygon) buffer;

			List<Geometry> geometryList = new ArrayList<Geometry>();
			for (int index=0; index<jtsVilles.getNumGeometries(); index++)
				geometryList.add(jtsVilles.getGeometryN(index));
			buffer = JtsAlgorithms.union(geometryList);
			logger.info("Aggréation des buffers terminée : "+buffer.getNumGeometries());
			//dilatation des buufers de superficie > 100 ha et suppression des trous
			for (int i = 0 ; i < jtsVilles.getNumGeometries() ; i++) {
				Geometry geom = jtsVilles.getGeometryN(i);
				Polygon result = (Polygon) geom;
				LinearRing exterior = (LinearRing) result.getExteriorRing();
				Polygon boundary = (Polygon) geomFactory.createPolygon(exterior, null).buffer(0.0, quadrantSegments, endCapStyle);
				if (boundary.getArea()>1000000) {
					geom = boundary.buffer(distance, quadrantSegments, endCapStyle);
					geom = JtsUtil.fermeture((Polygon)geom, distance, distanceTolerance, quadrantSegments, endCapStyle, geomFactory);
					villesMoyennesEtGrandes.add(geom);
				} else {
					geom = boundary;
					villesPetites.add(geom);
				}
			}

		}

		logger.info("séparation des petites et grandes villes terminée : "+villesPetites.size()+" petites - "+villesMoyennesEtGrandes.size()+" grandes");

		List<Geometry> villesToutes = new ArrayList<Geometry>();

		for (Geometry geom:villesMoyennesEtGrandes) {
			geom = JtsUtil.fermeture((Polygon)geom, distance, distanceTolerance, quadrantSegments, endCapStyle, geomFactory);
			List<Geometry> geometriesIntersectant = JtsUtil.getListeGeometriesIntersectant(geom, villesPetites);
			while (!geometriesIntersectant.isEmpty()) {
				villesPetites.removeAll(geometriesIntersectant);
				List<Geometry> geomList = new ArrayList<Geometry>();
				for(Geometry g:geometriesIntersectant) {
					geomList.add(g.buffer(distance, quadrantSegments, endCapStyle));
				}
				geomList.add(geom);
				buffer = JtsAlgorithms.union(geomList);
				if (!(buffer instanceof Polygon)) {
					logger.error("Buffer aggrege de type non gere : "+buffer.getGeometryType()+" - "+((MultiPolygon)buffer).getNumGeometries());
				}
				geom = JtsUtil.fermeture((Polygon)buffer, distance, distanceTolerance, quadrantSegments, endCapStyle, geomFactory);
				geometriesIntersectant = JtsUtil.getListeGeometriesIntersectant(geom, villesPetites);
			}
			villesToutes.add(geom);
		}

		villesToutes.addAll(villesPetites);
		logger.info("agrégation des petites et grandes villes terminée : "+villesToutes.size());

		GeometryList = villesToutes.toArray(new Geometry[0]);

		buffer = JtsAlgorithms.union(GeometryList);

		logger.info("agrégation des villes inclues terminée : "+buffer.getNumGeometries());

		if (buffer instanceof MultiPolygon) {
			jtsVilles = (MultiPolygon) buffer;
			villesToutes.clear();
			for (int i = 0 ; i < jtsVilles.getNumGeometries() ; i++) {
				Geometry geom = jtsVilles.getGeometryN(i);
				if (!(geom instanceof Polygon)) {
					logger.error("géométrie de ville de type : "+geom.getGeometryType());
					if (geom instanceof MultiPolygon) {
						logger.debug(((MultiPolygon)geom).getNumGeometries()+" géométries");
					}
				}
				// fermeture du contour
				geom = JtsUtil.fermetureSuppTrous(geom, distance, quadrantSegments, endCapStyle);
				geom = JtsAlgorithms.filtreDouglasPeucker(geom, distanceTolerance);

				//if (((Polygon)geom).getNumInteriorRing()!=0) logger.warn(" possède "+((Polygon)geom).getNumInteriorRing()+" trous");
				villesToutes.add(geom);
			}
			logger.info("Suppression des trous du MultiPolygon terminée");
			buffer=JtsAlgorithms.union(villesToutes);
			logger.info("agrégation après suppression des trous du MultiPolygon terminée");
		}

		// construction à proprement parler des villes
		if (buffer instanceof MultiPolygon) {
			jtsVilles = (MultiPolygon) buffer;
			// construction des villes
			for (int i = 0 ; i < jtsVilles.getNumGeometries() ; i++) {
				Geometry geom = jtsVilles.getGeometryN(i);
				UniteUrbaine uniteUrbaine = UniteUrbaine.newInstance();
				Polygon dernierPolygone = null;
				if (geom instanceof Polygon) {
					dernierPolygone = (Polygon) geom;
				} else {
					logger.error("géométrie de ville de type : "+geom.getGeometryType());
					if (geom instanceof MultiPolygon) {
						logger.error(((MultiPolygon)geom).getNumGeometries()+" géométries");
					}
				}
				uniteUrbaine.setDateSourceSaisie(date);
				// fermeture du contour
				logger.debug("avant fermeture "+geom);
				geom = JtsAlgorithms.fermeture(geom, distance, quadrantSegments, endCapStyle);
				if (geom instanceof Polygon) {dernierPolygone = (Polygon) geom;}
				if (geom.isEmpty()) continue;
				logger.debug("après fermeture "+geom);
				geom = JtsAlgorithms.filtreDouglasPeucker(geom, distanceTolerance);
				if (geom instanceof Polygon) {dernierPolygone = (Polygon) geom;}
				logger.debug("après filtre "+geom);
				geom = geom.buffer(0.0);
				if (geom instanceof Polygon) {dernierPolygone = (Polygon) geom;}
				logger.debug("après buffer "+geom);
				if (!(geom instanceof Polygon)) {
					logger.error("géométrie de ville de type : "+geom.getGeometryType());
					logger.error(geom);
					if (geom instanceof MultiPolygon) {
						logger.error(((MultiPolygon)geom).getNumGeometries()+" géométries");
					}
				}
				if (dernierPolygone!=null) {
					if (dernierPolygone.getNumInteriorRing()!=0)
						logger.warn("la ville "+uniteUrbaine.getIdRep()+" possède "+dernierPolygone.getNumInteriorRing()+" trous");
					try {
						GM_Polygon p = (GM_Polygon) AdapterFactory.toGM_Object(dernierPolygone);
						// suppression des trous de la géométrie
						while (!p.getInterior().isEmpty()) p.removeInterior(0);
						uniteUrbaine.setGeom(p);
					}
					catch (Exception e) {logger.error("Erreur dans la construction de la géométrie "+e.getMessage());}
					this.add(uniteUrbaine);
				} else {
					logger.error("pas de polygone trouvé");
				}
			}
		} else if (buffer instanceof Polygon) {
			Polygon jtsVille = (Polygon) buffer;
			UniteUrbaine uniteUrbaine = UniteUrbaine.newInstance();
			uniteUrbaine.setDateSourceSaisie(date);
			// fermeture du contour
			jtsVille = JtsUtil.fermeture(jtsVille, distance, distanceTolerance, quadrantSegments, endCapStyle, geomFactory);
			try {uniteUrbaine.setGeom(AdapterFactory.toGM_Object(jtsVille));}
			catch (Exception e) {logger.error("Erreur dans la construction de la géométrie "+e.getMessage());}
			this.add(uniteUrbaine);
		} else logger.info("Buffer construit apres aggregation des buffers moyens et grands de type non gere : "+buffer.getGeometryType());
		logger.info("Creation des Unités urbaines terminée");
		this.construireUnitePeriUrbaine();
	}

	/**
	 * Construire l'Unité péri-Urbaine
	 */
	public void construireUnitePeriUrbaine() {
		IEnvelope envelope = this.populationRoutes.envelope();
		envelope.expand(this.populationChemins.envelope());
		envelope.expand(this.populationHydrographiques.envelope());
		envelope.expand(this.populationVoiesFerrees.envelope());
		GM_Polygon polygone = new GM_Polygon(new GM_Envelope(envelope.minX()-100,envelope.maxX()+100,envelope.minY()-100,envelope.maxY()+100));
		for (UniteUrbaine uniteUrbaine:this) {
			if (((GM_Polygon)uniteUrbaine.getGeom()).getExterior().coord().size()!=0)
				polygone.addInterior(((GM_Polygon)uniteUrbaine.getGeom()).getExterior());
			else {
				logger.warn("Unite urbaine vide ??? "+uniteUrbaine.getGeom());
			}
		}
		this.unitePeriUrbaine.setGeom(polygone);
		this.unitePeriUrbaine.setDateSourceSaisie(date);
	}

	/**
	 * Construction des carrefours des villes à partir de leurs îlots.
	 */
	public void construireCarrefours() {
		logger.info("--- Contruction des carrefours --- ");
		fireActionPerformed(new ActionEvent(this,0,"troncons",populationRoutes.size()+populationChemins.size()));
		int indexProgres = 0;
		BasicRepresentationFactory representationfactory = new BasicRepresentationFactory();
		if (logger.isDebugEnabled()) logger.debug("Création des carrefours à partir des cycles ("+carteTopo.getListeFaces().size()+")");
		fireActionPerformed(new ActionEvent(this,0,"polygones",carteTopo.getListeFaces().size()));
		indexProgres=0;
		//Population<Carrefour> populationCarrefours = new Population<Carrefour>();
		if (storeDB) DataSet.db.begin();
		for (Face face : carteTopo.getListeFaces()) {
			IFeature correspondant=face.getCorrespondant(0);
			if(correspondant instanceof ZoneElementaireUrbaine) {
				ZoneElementaireUrbaine zone=(ZoneElementaireUrbaine) correspondant;
				if (logger.isTraceEnabled()) logger.trace("Correspondant "+zone+" trouvé pour la face "+face.getId()+ " de géométrie "+face.getGeom());
				// si l'ilot contient des bâtiments, ce n'est pas un carrefour
				if (!zone.getBatiments().isEmpty()) continue;
			} else {
				logger.warn("Pas de correspondant trouvé pour la face "+face.getId()+ " de géométrie "+face.getGeom());
			}
			IPolygon polygone = face.getGeometrie();
			if (polygone.sizeInterior()!=0) continue;
			int nbPoints = polygone.getExterior().coord().size();
			if (nbPoints<7) continue;
			double area = polygone.area();
			if (area<15.0) continue;
			Polygon geometry = null;
			try {geometry = (Polygon)AdapterFactory.toGeometry(factory,polygone);}
			catch (Exception e) {
				logger.error("Echec pendant la convertion de la géométrie du carrefour "+e.getMessage());
				continue;
			}
			double circularite = JtsUtil.circularite(geometry);
			if (circularite<0.90) continue;
			Carrefour carrefour = (Carrefour) representationfactory.creerCarrefour();
			try {
				carrefour.setGeom(AdapterFactory.to2DGM_Object(polygone));
			} catch (Exception e) {
				logger.error("Echec pendant la convertion de la géométrie du carrefour en 2D "+e.getMessage());
				continue;
			}
			carrefour.setNature("Rond Point");
			carrefour.setSource("calculé");
			carrefour.setEcartTypeDistances(circularite);
			carrefour.setDateSourceSaisie(date);
			populationCarrefours.add(carrefour);
			if (storeDB) DataSet.db.makePersistent(carrefour);
			fireActionPerformed(new ActionEvent(this,1,"polygone traite",indexProgres++));
		}
		//this.setPopulationCarrefours(populationCarrefours);
		if (storeDB) DataSet.db.commit();
		fireActionPerformed(new ActionEvent(this,4,"fin"));
	}

	/**
	 * Destruction de toutes les populations
	 */
	public void detruirePopulations() {
		logger.info("Destruction de toutes les populations");
		DataSet.db.begin();
		if (date==0) {
			DataSet.db.exeSQL("DELETE FROM unite_urbaine;");
			DataSet.db.exeSQL("DELETE FROM unite_peri_urbaine;");
			DataSet.db.exeSQL("DELETE FROM zone_elementaire;");
			DataSet.db.exeSQL("DELETE FROM relation_zone_elem_zone_elem;");
			DataSet.db.exeSQL("DELETE FROM relation_zone_elem_troncon;");
			DataSet.db.exeSQL("DELETE FROM batiment;");
			DataSet.db.exeSQL("DELETE FROM troncon_route;");
			DataSet.db.exeSQL("DELETE FROM troncon_chemin;");
			DataSet.db.exeSQL("DELETE FROM troncon_cours_eau;");
			DataSet.db.exeSQL("DELETE FROM troncon_voie_ferree;");
		} else {
			DataSet.db.exeSQL("DELETE FROM unite_urbaine WHERE datesource = "+Integer.toString(date)+";");
			DataSet.db.exeSQL("DELETE FROM unite_peri_urbaine WHERE datesource = "+Integer.toString(date)+";");
			DataSet.db.exeSQL("DELETE FROM zone_elementaire WHERE datesource = "+Integer.toString(date)+";");
			DataSet.db.exeSQL("DELETE FROM relation_zone_elem_zone_elem USING ilot WHERE relation_ilot_ilot.ilot = ilot.cogitid AND ilot.datesource = "+Integer.toString(date)+";");
			DataSet.db.exeSQL("DELETE FROM relation_zone_elem_troncon USING ilot WHERE relation_ilot_troncon.ilot = ilot.cogitid AND ilot.datesource = "+Integer.toString(date)+";");
			DataSet.db.exeSQL("DELETE FROM batiment WHERE datesource = "+Integer.toString(date)+";");
			DataSet.db.exeSQL("DELETE FROM troncon_route WHERE datesource = "+Integer.toString(date)+";");
			DataSet.db.exeSQL("DELETE FROM troncon_chemin WHERE datesource = "+Integer.toString(date)+";");
			DataSet.db.exeSQL("DELETE FROM troncon_cours_eau WHERE datesource = "+Integer.toString(date)+";");
			DataSet.db.exeSQL("DELETE FROM troncon_voie_ferree WHERE datesource = "+Integer.toString(date)+";");
		}
		DataSet.db.clearCache();
		DataSet.db.commit();
	}

	//static int nbCarte=0;

	/**
	 * Construction des zones élémentaires à partir des Unités et du réseau de communication.
	 * <p>
	 * Pour ce faire, on crée une carte topologique à l'aide du réseau et des contours des Unités.
	 * Les faces du graphe (planaire) ainsi créé sont les zones élémentaires.
	 */
	public void construireZonesElementaires() {
		logger.info("--- Contruction des îlots --- ");
		this.initSpatialIndex(false);
		populationBatiments.initSpatialIndex(false);
		populationRoutes.initSpatialIndex(false);
		populationVoiesFerrees.initSpatialIndex(false);
		populationChemins.initSpatialIndex(false);
		populationHydrographiques.initSpatialIndex(false);

		// TODO unification des villes reliées par un pont
		/*
		for (UniteUrbaine uniteUrbaine : this) {
		    if (uniteUrbaine.getTypeSelonTaille()==UniteUrbaine.GrandeVille) {
			    GM_Polygon polygone = (GM_Polygon)uniteUrbaine.getGeom();
			    populationRoutes.select(polygone);
		    }
		}
		*/

		carteTopo.importClasseGeo(populationRoutes.getElements(),true);
		carteTopo.importClasseGeo(populationVoiesFerrees.getElements(),true);
		carteTopo.importClasseGeo(populationChemins.getElements(),true);
		carteTopo.importClasseGeo(populationHydrographiques.getElements(),true);
		FT_FeatureCollection<DefaultFeature> contours = new FT_FeatureCollection<DefaultFeature>();
		for (UniteUrbaine uniteUrbaine : this) {
		    GM_Polygon polygone = (GM_Polygon)uniteUrbaine.getGeom();
		    DefaultFeature contour = new DefaultFeature();
		    contour.setGeom(polygone.exteriorLineString());
		    contours.add(contour);
		}

		carteTopo.importClasseGeo(contours,true);

		carteTopo.setActionListeners(this.getActionListeners());

		if (logger.isDebugEnabled()) logger.debug("--- creation des noeuds --- ");
		carteTopo.creeNoeudsManquants(1.0);

		/*
			if (logger.isDebugEnabled()) logger.debug("--- filtrage des doublons --- ");
			carteTopo.filtreDoublons(1.0);
		 */

		if (logger.isDebugEnabled()) logger.debug("--- fusion des noeuds --- ");
		carteTopo.fusionNoeuds(1.0);

		if (logger.isDebugEnabled()) logger.debug("--- filtrage des arcs doublons --- ");
		carteTopo.filtreArcsDoublons();

		/*
			if (logger.isDebugEnabled()) logger.debug("--- creation de la topologie Arcs-Noeuds --- ");
			carteTopo.creeTopologieArcsNoeuds(1.0);

			if (logger.isDebugEnabled()) logger.debug("--- fusion des noeuds --- ");
			carteTopo.fusionNoeuds(1.0);
		 */

		if (logger.isDebugEnabled()) logger.debug("--- rend planaire --- ");
		carteTopo.rendPlanaire(1.0);
		//affecte les identifiants des ft_features aux noeuds, faces et arcs

		/*
			int id=1;
			for(Object n:carteTopo.getPopNoeuds()) {
				Noeud noeud = (Noeud)n;
				noeud.setId(id++);
			}
			for(Object a:carteTopo.getPopArcs()) {
				Arc arc = (Arc)a;
				arc.setId(id++);
			}
		 */

		if (logger.isDebugEnabled()) logger.debug("--- fusion des doublons --- ");
		carteTopo.fusionNoeuds(1.0);

		if (logger.isDebugEnabled()) logger.debug("--- filtrage des arcs doublons --- ");
		carteTopo.filtreArcsDoublons();

		if (logger.isDebugEnabled()) logger.debug("--- creation de la topologie des Faces --- ");
		carteTopo.creeTopologieFaces();

		logger.info(carteTopo.getListeFaces().size()+" faces trouvées");
		if (logger.isDebugEnabled()) logger.debug("Création des zones élémentaires urbaines");

		/*
		ShapefileWriter.write(carteTopo.getPopFaces(),"/home/jperret/cartetopo/faces"+nbCarte+".shp");
		ShapefileWriter.write(carteTopo.getPopArcs(),"/home/jperret/cartetopo/arcs"+nbCarte+".shp");
		nbCarte++;
		*/

		carteTopo.getPopFaces().initSpatialIndex(Tiling.class,false);

		logger.info("Index spatial initialisé");

		if (storeDB) DataSet.db.begin();
		for(UniteUrbaine uniteUrbaine:this) {
	        logger.info("Début de la construction des zones élémentaires pour l'unite " + uniteUrbaine.getId());
			uniteUrbaine.construireZonesElementaires(carteTopo,date);
            logger.info("Fin de la construction des zones élémentaires pour l'unite " + uniteUrbaine.getId());
			//ajouter les batiments aux ilots
			for(ZoneElementaireUrbaine zoneElementaire:uniteUrbaine.getZonesElementaires()) {
//				zoneElementaire.setBatiments(this.getPopulationBatiments().select(zoneElementaire.getGeom()));
				//zoneElementaire.construireEspacesVides();
				//uniteUrbaine.addAllBatiment(zoneElementaire.getBatiments());
			  zoneElementaire.construireGroupes(this.getPopulationBatiments().select(zoneElementaire.getGeom()));
//				for (Batiment batiment:zoneElementaire.getBatiments()) {
//				  batiment.setZoneElementaireUrbaine(zoneElementaire);
//				}
			}
			// on rend l'unité urbaine persistente
			if (storeDB) {
			    logger.info("Stockage des zones élémentaires pour l'unite " + uniteUrbaine.getId());
			    DataSet.db.makePersistent(uniteUrbaine);
                logger.info("Fin du stockage des zones élémentaires pour l'unite " + uniteUrbaine.getId());
			}
			//ajouter ilots à populationIlots
			this.getPopulationZonesElementaires().addAll(uniteUrbaine.getZonesElementaires());
		}
		if (storeDB) DataSet.db.checkpoint();
		logger.info(this.getPopulationZonesElementaires().size()+" zones élémentaires créées");
		this.getPopulationZonesElementaires().initSpatialIndex();
		// parcours des zones élémentaires et détermination des trous
		if (logger.isDebugEnabled()) logger.debug("Détermination des trous");
		for (ZoneElementaireUrbaine zone:this.getPopulationZonesElementaires()) {
			GM_Polygon polygon = zone.getGeometrie();
			if (polygon.sizeInterior()>0) {
				Set<ZoneElementaire> trous = new HashSet<ZoneElementaire>();
				for (int indice=0;indice<polygon.sizeInterior();indice++) {
					ILineString interieur = polygon.interiorLineString(indice);
					Collection<ZoneElementaireUrbaine> intersection = this.getPopulationZonesElementaires().select(interieur);
					intersection.remove(zone);
					trous.addAll(intersection);
				}
				zone.setTrous(trous);
			}
		}
		if (logger.isDebugEnabled()) logger.debug("Caractérisation des impasses");
		for (TronconRoute route:this.getPopulationRoutes()) {
			if ((route.getCorrespondant(0)!=null)&&(route.getCorrespondant(0) instanceof Arc))
				route.setImpasse(((Arc)route.getCorrespondant(0)).isPendant());
		}
		if (storeDB) {
			if (logger.isDebugEnabled()) logger.debug("Stockage des zones élémentaires urbaines");
			DataSet.db.commit();
		}
		if (logger.isDebugEnabled()) logger.debug("Création des zones élémentaires périurbaines");
		unitePeriUrbaine.construireZonesElementaires(carteTopo,date);
	}

	@Override
	public void qualifier() {
		if (elements!=null) {
			for (Object v : this) {
				UniteUrbaine uniteUrbaine = (UniteUrbaine) v;
				uniteUrbaine.qualifier();
			}
		}
		this.unitePeriUrbaine.qualifier();
		this.populationBatiments.qualifier();
		this.populationRoutes.qualifier();
		this.populationChemins.qualifier();
		this.populationVoiesFerrees.qualifier();
		this.populationHydrographiques.qualifier();
		this.populationZonesElementaires.qualifier();
	}

	/**
	 *
	 */
	public void sauverPopulations() {
		DataSet.db.begin();
		//DataSet.db.clearCache();
		if (logger.isDebugEnabled()) {
			logger.debug("sauve - "+this.size()+ " Unités urbaines");
			logger.debug("      - 1 Unité péri-urbaine");
			logger.debug("      - "+this.getPopulationZonesElementaires().size()+ " zones élémentaires urbaines");
			logger.debug("      - "+this.getPopulationBatiments().size()+ " bâtiments");
			logger.debug("      - "+this.getPopulationRoutes().size()+ " tronçons de route");
			logger.debug("      - "+this.getPopulationChemins().size()+ " tronçons de chemin");
			logger.debug("      - "+this.getPopulationHydrographiques().size()+ " tronçons de cours d'eau");
			logger.debug("      - "+this.getPopulationVoiesFerrees().size()+ " tronçons de voie ferrée");
		}
		//DataSet.db.setImpliciteWriteLocks(false);
		for (UniteUrbaine uniteUrbaine:this) {
			if (logger.isDebugEnabled()) logger.debug("sauve la ville "+uniteUrbaine.getId());
			DataSet.db.makePersistent(uniteUrbaine);
		}
		DataSet.db.makePersistent(unitePeriUrbaine);
		for (ZoneElementaire zone:this.getPopulationZonesElementaires()) {
			if (logger.isDebugEnabled()) logger.debug("sauve l'îlot "+zone.getId());
			DataSet.db.makePersistent(zone);
		}
		//DataSet.db.setImpliciteWriteLocks(true);
		for (Batiment batiment:this.getPopulationBatiments()) {
			if (logger.isDebugEnabled()) logger.debug("sauve le batiment "+batiment.getId());
			DataSet.db.makePersistent(batiment);
		}
		for (Troncon troncon:this.getPopulationRoutes()) {
			if (logger.isDebugEnabled()) logger.debug("sauve le tronçon de route "+troncon.getId());
			DataSet.db.makePersistent(troncon);
		}
		for (Troncon troncon:this.getPopulationChemins()) {
			if (logger.isDebugEnabled()) logger.debug("sauve le tronçon de chemin "+troncon.getId());
			DataSet.db.makePersistent(troncon);
		}
		for (Troncon troncon:this.getPopulationHydrographiques()) {
			if (logger.isDebugEnabled()) logger.debug("sauve le tronçon de cours d'eau "+troncon.getId());
			DataSet.db.makePersistent(troncon);
		}
		for (Troncon troncon:this.getPopulationVoiesFerrees()) {
			if (logger.isDebugEnabled()) logger.debug("sauve le tronçon de voie ferrée "+troncon.getId());
			DataSet.db.makePersistent(troncon);
		}
		for (Carrefour carrefour:this.getPopulationCarrefours()) {
			if (logger.isDebugEnabled()) logger.debug("sauve le carrefour "+carrefour.getId());
			DataSet.db.makePersistent(carrefour);
		}
		DataSet.db.commit();
	}
	/**
	 * @return populationBatiments
	 */
	public PopulationBatiments getPopulationBatiments() {return populationBatiments;}
	/**
	 * @return populationChemins
	 */
	public PopulationTronconsChemin getPopulationChemins() {return populationChemins;}
	/**
	 * @return populationHydrographiques
	 */
	public PopulationTronconsHydrographiques getPopulationHydrographiques() {return populationHydrographiques;}
	/**
	 * @return populationRoutes
	 */
	public PopulationTronconsRoute getPopulationRoutes() {return populationRoutes;}
	/**
	 * @return populationVoiesFerrees
	 */
	public PopulationTronconsVoieFerree getPopulationVoiesFerrees() {return populationVoiesFerrees;}
	/**
	 * @return populationIlots
	 */
	public PopulationZonesElementaires getPopulationZonesElementaires() {return populationZonesElementaires;}
	/**
	 * @param populationBatiments the populationBatiments to set
	 */
	public void setPopulationBatiments(PopulationBatiments populationBatiments) {this.populationBatiments = populationBatiments;}
	/**
	 * @param populationRoutes the populationRoutes to set
	 */
	public void setPopulationRoutes(PopulationTronconsRoute populationRoutes) {this.populationRoutes = populationRoutes;}
	/**
	 * @param populationChemins the populationChemins to set
	 */
	public void setPopulationChemins(PopulationTronconsChemin populationChemins) {this.populationChemins = populationChemins;}
	/**
	 * @param populationVoiesFerrees the populationVoiesFerrees to set
	 */
	public void setPopulationVoiesFerrees(PopulationTronconsVoieFerree populationVoiesFerrees) {this.populationVoiesFerrees = populationVoiesFerrees;}
	/**
	 * @param populationHydrographiques the populationHydrographiques to set
	 */
	public void setPopulationHydrographiques(PopulationTronconsHydrographiques populationHydrographiques) {this.populationHydrographiques = populationHydrographiques;}
	/**
	 * @param populationZonesElementaires the populationIlots to set
	 */
	public void setPopulationZonesElementaires(PopulationZonesElementaires populationZonesElementaires) {this.populationZonesElementaires = populationZonesElementaires;}
	/**
	 * @param listeRepresentations
	 */
	public void addBatiments(List<ElementRepresentation> listeRepresentations) {
		for (ElementRepresentation rep:listeRepresentations) this.populationBatiments.add((Batiment)rep);
	}
	/**
	 * @param listeRepresentations
	 */
	public void addTronconsRoute(List<ElementRepresentation> listeRepresentations) {
		for (ElementRepresentation rep:listeRepresentations) this.populationRoutes.add((TronconRoute)rep);
	}
	/**
	 * @param listeRepresentations
	 */
	public void addTronconsChemin(List<ElementRepresentation> listeRepresentations) {
		for (ElementRepresentation rep:listeRepresentations) this.populationChemins.add((TronconChemin)rep);
	}
	/**
	 * @param listeRepresentations
	 */
	public void addTronconsVoieFerree(List<ElementRepresentation> listeRepresentations) {
		for (ElementRepresentation rep:listeRepresentations) this.populationVoiesFerrees.add((TronconVoieFerree)rep);
	}
	/**
	 * @param listeRepresentations
	 */
	public void addTronconsCoursEau(List<ElementRepresentation> listeRepresentations) {
		for (ElementRepresentation rep:listeRepresentations) this.populationHydrographiques.add((TronconCoursEau)rep);
	}
	@Override
	public String toString() {
		String s="Population contenant "+this.size()+" Unités\n";
		for(UniteUrbaine unite:this) {s+=unite.toString();}
		return s;
	}
	/**
	 * Booléen utilisé quand on travaille sur une extraction :
	 * on construit un seul objet uniteUrbaine qui englobe toute l'extraction.
	 */
	private boolean extraction = false;

	/**
	 * Affecte vrai si les données chargées sont extraites d'une base plus grande, faux sinon.
	 * @param extraction vrai si les données chargées sont extraites d'une base plus grande, faux sinon.
	 */
	public void setExtraction(boolean extraction) {this.extraction=extraction;}
	/**
	 * Renvoie vrai si les données chargées sont extraites d'une base plus grande, faux sinon.
	 * @return vrai si les données chargées sont extraites d'une base plus grande, faux sinon.
	 */
	public boolean isExtraction() {return this.extraction;}

	/**
	 * Renvoie la valeur de l'attribut unitePeriUrbaine.
	 * @return la valeur de l'attribut unitePeriUrbaine
	 */
	public UniteUrbaine getUnitePeriUrbaine() {return this.unitePeriUrbaine;}
	/**
	 * Affecte la valeur de l'attribut unitePeriUrbaine.
	 * @param unitePeriUrbaine l'attribut unitePeriUrbaine à affecter
	 */
	public void setUnitePeriUrbaine(UniteUrbaine unitePeriUrbaine) {this.unitePeriUrbaine = unitePeriUrbaine;}

	/**
	 * Renvoie la valeur de l'attribut populationCarrefours.
	 * @return la valeur de l'attribut populationCarrefours
	 */
	public Population<Carrefour> getPopulationCarrefours() {return this.populationCarrefours;}
	/**
	 * Affecte la valeur de l'attribut populationCarrefours.
	 * @param populationCarrefours l'attribut populationCarrefours à affecter
	 */
	public void setPopulationCarrefours(Population<Carrefour> populationCarrefours) {this.populationCarrefours = populationCarrefours;}
}

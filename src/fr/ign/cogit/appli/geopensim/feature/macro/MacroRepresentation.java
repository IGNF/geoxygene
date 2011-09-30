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

import java.sql.Time;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.feature.ElementRepresentation;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.index.Tiling;

/**
 * @author Julien Perret
 *
 */
public abstract class MacroRepresentation<Representation extends ElementRepresentation> implements Iterable<Representation> {
	static Logger logger=Logger.getLogger(MacroRepresentation.class.getName());

	protected IFeatureCollection<Representation> elements = new FT_FeatureCollection<Representation>();
	protected Class<? extends ElementRepresentation> classe = ElementRepresentation.class;
	protected int date;

	/**
	 * @param Classe
	 */
	public MacroRepresentation(Class<? extends ElementRepresentation> Classe) {
		this.classe = Classe;
		this.date = 0;
	}

	/**
	 * @param Classe
	 * @param date
	 */
	public MacroRepresentation(Class<? extends ElementRepresentation> Classe, int date) {
		this.classe = Classe;
		this.date = date;
		this.elements = new FT_FeatureCollection<Representation>();
	}

	/**
	 * Charge les représentations depuis la base de données.
	 */
	@SuppressWarnings("unchecked")
	public void chargerElements() {
		if (logger.isDebugEnabled()) logger.debug("Classe = "+classe);
		if (logger.isDebugEnabled()) logger.debug("Element Classe = "+elements.getClass());
		if (logger.isDebugEnabled()) logger.debug("Date = "+date);
		if (date==0) elements = DataSet.db.loadAllFeatures(classe,elements.getClass());
		else elements = DataSet.db.loadAllFeatures(classe,elements.getClass(),"datesource",Integer.toString(date));
		if (logger.isDebugEnabled()) logger.debug((new Time(System.currentTimeMillis())).toString()+" fin du chargement : "+elements.size()+" elements chargés");
	}

	/**
	 * @return taille de l'objet macro, i.e. le nombre d'éléments qu'il contient
	 */
	public int size() {
		return elements.size();
	}

	/* (non-Javadoc)
	 * @see java.lang.Iterable#iterator()
	 */
	public Iterator<Representation> iterator() {
		return elements.iterator();
	}

	/**
	 * @return vrai si l'iobjet macro est vide, faux sinon
	 */
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	/**
	 *
	 */
	public void clear() {
		elements.clear();
	}

	/**
	 * @param feature
	 * @return vrai si le feature a bien été ajouté, faux sinon
	 */
	public boolean add(Representation feature) {
		return elements.add(feature);
	}

	/**
	 * @param featureCollection
	 * @return vrai si les features ont bien été ajoutés, faux sinon
	 */
	public boolean addAll(Collection<Representation> featureCollection) {
		return elements.addAll(featureCollection);
	}

	/**
	 * Qualifier les éléments de la macro représentation.
	 */
	public void qualifier() {
		for(Representation rep:this) rep.qualifier();
	}

	/**
	 *
	 */
	public void initSpatialIndex() {
		elements.initSpatialIndex(Tiling.class, true);
	}

	/**
	 *
	 */
	public void initSpatialIndex(boolean automaticUpdates) {
		elements.initSpatialIndex(Tiling.class, automaticUpdates);
	}

	/**
	 * @param geom une géométrie quelconque
	 * @return une featureCollection contenant toutes les représentations intersectant la géométrie geom
	 */
	public Collection<Representation> select(IGeometry geom) {
		return elements.getSpatialIndex().select(geom);
	}
	/**
	 * @return the elements
	 */
	public IFeatureCollection<Representation> getElements() {
		return elements;
	}
	/**
	 * @param elements the elements to set
	 */
	public void setElements(IFeatureCollection<Representation> elements) {
		this.elements = elements;
	}
	/**
	 * Calcul l'emprise rectangulaire des geometries de la collection
	 * @return emprise rectangulaire des geometries de la collection
	 */
	public IEnvelope envelope () {
		if (this.elements.hasGeom())
			return this.elements.getGeomAggregate().envelope();
		else {
			logger.error("ATTENTION appel de envelope() sur une FT_FeatureCollection sans geometrie ! (renvoie null) ");
			return null;
		}
	}
}

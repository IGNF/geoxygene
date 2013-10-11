/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.geomatching;

import java.util.Collection;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.api.spatial.toporoot.ITopology;

/**
 * @author Bertrand Dumenieu
 */
public class SimpleGeomHypothesis extends GeomHypothesis {

  /**
   * @param feature
   */
  public SimpleGeomHypothesis(IFeature feature) {
    super(feature);
  }

  /**
   * 
   */
  protected SimpleGeomHypothesis() {
    super();
  }

  @Override
  public int getId() {
    return this.decoratedFeature.getId();
  }

  @Override
  public void setId(int Id) {
    this.decoratedFeature.setId(Id);
  }

  @Override
  public IGeometry getGeom() {
    return this.decoratedFeature.getGeom();
  }

  @Override
  public void setGeom(IGeometry g) {
    this.decoratedFeature.setGeom(g);
  }

  @Override
  public boolean hasGeom() {
    return this.decoratedFeature.hasGeom();
  }

  @Override
  public ITopology getTopo() {
    return this.decoratedFeature.getTopo();
  }

  @Override
  public void setTopo(ITopology t) {
    this.decoratedFeature.setTopo(t);
  }

  @Override
  public boolean hasTopo() {
    return this.decoratedFeature.hasTopo();
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return this.decoratedFeature.cloneGeom();
  }

  @Override
  public List<IFeatureCollection<IFeature>> getFeatureCollections() {
    return this.decoratedFeature.getFeatureCollections();
  }

  @Override
  public IFeatureCollection<IFeature> getFeatureCollection(int i) {
    return this.decoratedFeature.getFeatureCollection(i);
  }

  @Override
  public List<IFeature> getCorrespondants() {
    return this.decoratedFeature.getCorrespondants();
  }

  @Override
  public void setCorrespondants(List<IFeature> L) {
    this.decoratedFeature.setCorrespondants(L);
  }

  @Override
  public IFeature getCorrespondant(int i) {
    return this.decoratedFeature.getCorrespondant(i);
  }

  @Override
  public void addCorrespondant(IFeature O) {
    this.decoratedFeature.addCorrespondant(O);
  }

  @Override
  public void removeCorrespondant(IFeature O) {
    this.decoratedFeature.removeCorrespondant(O);
  }

  @Override
  public void clearCorrespondants() {
    this.decoratedFeature.clearCorrespondants();
  }

  @Override
  public void addAllCorrespondants(Collection<IFeature> c) {
    this.decoratedFeature.addAllCorrespondants(c);
  }

  @Override
  public Collection<IFeature> getCorrespondants(IFeatureCollection<? extends IFeature> pop) {
    return this.decoratedFeature.getCorrespondants(pop);
  }

  @Override
  public IPopulation<? extends IFeature> getPopulation() {
    return this.decoratedFeature.getPopulation();
  }

  @Override
  public void setPopulation(IPopulation<? extends IFeature> population) {
    this.decoratedFeature.setPopulation(population);
  }

  @Override
  public void setFeatureType(GF_FeatureType featureType) {
    this.decoratedFeature.setFeatureType(featureType);
  }

  @Override
  public GF_FeatureType getFeatureType() {
    return this.decoratedFeature.getFeatureType();
  }

  @Override
  public Object getAttribute(GF_AttributeType attribute) {
    return this.decoratedFeature.getAttribute(attribute);
  }

  @Override
  public void setAttribute(GF_AttributeType attribute, Object valeur) {
    this.decoratedFeature.setAttribute(attribute, valeur);
  }

  @Override
  public List<? extends IFeature> getRelatedFeatures(GF_FeatureType ftt, GF_AssociationRole role) {
    return this.decoratedFeature.getRelatedFeatures(ftt, role);
  }

  @Override
  public Object getAttribute(String nomAttribut) {
    return this.decoratedFeature.getAttribute(nomAttribut);
  }

  @Override
  public List<? extends IFeature> getRelatedFeatures(String nomFeatureType, String nomRole) {
    return this.decoratedFeature.getRelatedFeatures(nomFeatureType, nomRole);
  }

  @Override
  public Representation getRepresentation() {
    return this.decoratedFeature.getRepresentation();
  }

  @Override
  public void setRepresentation(Representation rep) {
    this.decoratedFeature.setRepresentation(rep);
  }

  @Override
  public boolean isDeleted() {
    return this.decoratedFeature.isDeleted();
  }

  @Override
  public void setDeleted(boolean deleted) {
    this.decoratedFeature.setDeleted(deleted);
  }

  @Override
  public boolean intersecte(IEnvelope env) {
    return this.decoratedFeature.intersecte(env);
  }
}

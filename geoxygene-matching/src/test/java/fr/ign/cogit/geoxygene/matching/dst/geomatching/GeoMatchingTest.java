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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.matching.dst.evidence.ChoiceType;
import fr.ign.cogit.geoxygene.matching.dst.evidence.EvidenceResult;
import fr.ign.cogit.geoxygene.matching.dst.sources.Source;
import fr.ign.cogit.geoxygene.matching.dst.sources.linear.LineOrientation;
import fr.ign.cogit.geoxygene.matching.dst.sources.linear.PartialFrechetDistance;
import fr.ign.cogit.geoxygene.matching.dst.sources.surface.SurfaceDistance;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;


public class GeoMatchingTest {
	
	private static final Logger LOGGER = Logger.getLogger(GeoMatchingTest.class);

  @Test
  public void testRunLine() throws Exception {
	  
    try {
	  Collection<Source<IFeature, GeomHypothesis>> criteria = new ArrayList<Source<IFeature, GeomHypothesis>>();
	  criteria.add(new PartialFrechetDistance());
	  criteria.add(new LineOrientation());
    
	  URL urlReseau1 = new URL("file", "", "./data/ign-lineaire/reseau1.shp");
	  IPopulation<IFeature> reseau1 = ShapefileReader.read(urlReseau1.getPath());
	  IFeature reference = reseau1.get(0);
	  LOGGER.trace("Reference id = " + reference.getId());
	  
	  URL urlReseau2 = new URL("file", "", "./data/ign-lineaire/reseau2.shp");
	  IPopulation<IFeature> reseau2 = ShapefileReader.read(urlReseau2.getPath());
	  
	  List<IFeature> candidates = new ArrayList<IFeature>(reseau2.select(reference.getGeom().buffer(50)));
	  boolean closed = true;
	  LOGGER.debug(candidates.size() + " candidates");
	  
	  GeoMatching matching = new GeoMatching();
	  EvidenceResult<GeomHypothesis> result = matching.run(criteria, reference, candidates,
	        ChoiceType.PIGNISTIC, closed);
	  LOGGER.info("result = " + result);
	  // LOGGER.info("reference = " + reference.getGeom());
	  LOGGER.info("value = " + result.getValue());
	  LOGGER.info("conflict = " + result.getConflict());
	  LOGGER.info("with " + result.getHypothesis().size());
	  for (int i = 0; i < result.getHypothesis().size(); i++) {
		  LOGGER.trace("\tobj " + i + " = " + result.getHypothesis().get(i));
	  }
	  
	  Assert.assertTrue(true);
	  
	  LOGGER.trace("\n====== Fin du test ======");
	  
    } catch (Exception e) {
    	e.printStackTrace();
    }
  }
  
}

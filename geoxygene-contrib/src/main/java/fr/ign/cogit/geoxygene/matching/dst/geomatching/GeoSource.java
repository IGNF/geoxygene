/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.geomatching;

import java.util.List;

import fr.ign.cogit.geoxygene.matching.dst.evidence.Source;
import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * A source expressing its belief on geometries.
 * @author Bertrand Dumenieu
 */
public abstract class GeoSource implements Source<GeomHypothesis> {

  @Override
  public String toString() {
    return this.getName();
  }

  protected String name = this.getClass().getName().substring(
      this.getClass().getName().lastIndexOf('.') + 1);

  /**
   * Source name is used to identify hypothessi values.
   * @return
   */
  @Override
  public String getName() {
    return "Default Source";
  }

  /**
   * @param candidates
   * @param encoded
   */
  @Override
  public List<Pair<byte[], Float>> evaluate(final List<GeomHypothesis> candidates,
      EvidenceCodec codec) {
    return null;
  }
}

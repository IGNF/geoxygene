/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.evidence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Encode et décode un hypothèse selon le comportement par défaut décrit dans
 * [WILSON04].
 * @author Bertrand Dumenieu
 * 
 */
public class DefaultCodec implements EvidenceCodec{

  Logger logger = Logger.getLogger(DefaultCodec.class);
  // L'ordre de la liste ne doit pas être modifié!.
  private final List<Hypothesis> hypotheses;

  public DefaultCodec(List<Hypothesis> hyps) {
    this.hypotheses = Collections.unmodifiableList(hyps);
  }

  public List<Hypothesis> decode(byte[] encoded) {
    List<Hypothesis> decoded = new ArrayList<Hypothesis>();
    for (int i = 0; i < encoded.length; i++) {
      if (encoded[i] == (byte) 1) {
        decoded.add(this.hypotheses.get(i));
      }
    }
    return hypotheses;
  }

  public byte[] encode(Hypothesis... hyps) {

    byte[] encoded = new byte[this.hypotheses.size()];
    for (int i = 0; i < hyps.length; i++) {
      if (!this.hypotheses.contains(hyps[i])) {
        logger.error("Unknown hypothesis");
        return null;
      }
      int id = this.hypotheses.indexOf(hyps[i]);
      encoded[id] = (byte) 1;
    }
    return encoded;
  }
}

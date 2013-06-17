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

package fr.ign.cogit.geoxygene.matching.dst.evidence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.matching.dst.util.Pair;

/**
 * @author Bertrand Dumenieu
 */
public class AppriouMapper {
  private Logger logger = Logger.getLogger(AppriouMapper.class);

  public AppriouMapper() {
  }

  /**
   * @param h
   * @return
   */
  public List<AppriouHyp> mapToFocalised(Hypothesis h) {
    List<AppriouHyp> hyps = new ArrayList<AppriouHyp>();
    hyps.add(new AppriouHyp(AppriouType.A, h));
    hyps.add(new AppriouHyp(AppriouType.NOTA, h));
    hyps.add(new AppriouHyp(AppriouType.UNKNOWN, h));
    return hyps;
  }

  /**
   * @param hypIndex
   *        l'index de l'hypothèse au sein de la liste totale des hypothèses.
   * @param hypsize
   *        Nombre d'hypothèses totales
   * @param encoded
   * @return
   * @throws Exception
   */
  public List<Pair<byte[], Float>> mapFocalisedToGlobal(int hypIndex, int hypsize,
      List<Pair<byte[], Float>> encoded) throws Exception {
    List<Pair<byte[], Float>> decodeds = new ArrayList<Pair<byte[], Float>>(encoded.size());

    for (Pair<byte[], Float> pair : encoded) {
      // Ensemble vide
      if (Arrays.equals(pair.getFirst(), new byte[] { 0, 0 })) {
        byte[] decodedvoid = new byte[hypsize];
        Arrays.fill(decodedvoid, (byte) 0);
        decodeds.add(new Pair<byte[], Float>(decodedvoid, pair.getSecond()));
      }
      // Non apparié
      else
        if (Arrays.equals(pair.getFirst(), new byte[] { 0, 1 })) {
          byte[] decodedNotApp = new byte[hypsize];
          Arrays.fill(decodedNotApp, (byte) 1);
          decodedNotApp[hypIndex] = (byte) 0;
          decodeds.add(new Pair<byte[], Float>(decodedNotApp, pair.getSecond()));
        }
        // Apparié
        else
          if (Arrays.equals(pair.getFirst(), new byte[] { 1, 0 })) {
            byte[] decodedApp = new byte[hypsize];
            decodedApp[hypIndex] = (byte) 1;
            decodeds.add(new Pair<byte[], Float>(decodedApp, pair.getSecond()));
          }
          // Inconnu
          else
            if (Arrays.equals(pair.getFirst(), new byte[] { 1, 1 })) {
              byte[] decodedUnknown = new byte[hypsize];
              Arrays.fill(decodedUnknown, (byte) 1);
              decodeds.add(new Pair<byte[], Float>(decodedUnknown, pair.getSecond()));
            } else {
              logger.error("Cannot decode focalised hypothesis " + pair.getFirst());
            }
    }
    return decodeds;
  }
}

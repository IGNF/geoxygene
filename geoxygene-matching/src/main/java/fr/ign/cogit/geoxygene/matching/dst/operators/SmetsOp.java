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

package fr.ign.cogit.geoxygene.matching.dst.operators;

import java.util.ArrayList;
// import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.matching.dst.util.Pair;
import fr.ign.cogit.geoxygene.matching.dst.util.Utils;

/**
 * @author Julien Perret
 */
public class SmetsOp implements CombinationOp {
  
  private static final Logger LOGGER = Logger.getLogger(SmetsOp.class);
  private boolean worldclosed = true;

  /**
   * @param isworldclosed
   */
  public SmetsOp(boolean isworldclosed) {
    this.worldclosed = isworldclosed;
  }

  @Override
  public List<Pair<byte[], Float>> combine(List<List<Pair<byte[], Float>>> masspotentials) {

    LOGGER.info("Début SmetsOp.");
    // logger.info(masspotentials.size());
    if (masspotentials.size() == 1) {
      return masspotentials.get(0);
    }
    
    if (masspotentials.size() >= 2) {
      
      // 1 - Calculer le noyau combiné de toutes les masses de croyance;
      LOGGER.info("1 - Calculer le noyau combiné de toutes les masses de croyance.");
      List<List<byte[]>> cores = new ArrayList<List<byte[]>>();
      for (List<Pair<byte[], Float>> massvalues : masspotentials) {
        List<byte[]> core = new ArrayList<byte[]>();
        for (Pair<byte[], Float> pair : massvalues) {
          core.add(pair.getFirst());
        }
        cores.add(core);
      }
      
      try {
        byte[] combined = CombinationAlgos.combine(cores);
        if (Utils.isEmpty(combined)) {
          LOGGER.info("Les masses de croyances sont en total désaccord...");
          if (this.worldclosed) {
            List<Pair<byte[], Float>> result = new ArrayList<Pair<byte[], Float>>();
            result.add(new Pair<byte[], Float>(new byte[combined.length], 1.0f));
          }
        }
        // 2 - Conditionnement des masses existantes par le noyau combiné.
        LOGGER.info("2 - Conditionnement des masses existantes par le noyau combiné.");
        List<List<Pair<byte[], Float>>> conditionnedMassPotentials = new ArrayList<List<Pair<byte[], Float>>>();
        for (List<Pair<byte[], Float>> mass : masspotentials) {
          List<Pair<byte[], Float>> conditionned = CombinationAlgos.conditionning(mass, combined,
              this.worldclosed);
          if (conditionned.isEmpty()) {
            conditionnedMassPotentials.add(mass);
            LOGGER.error("CAS MAL GERE : MASSE CONDITIONNEE NON DEFINIE!");
          } else {
            conditionnedMassPotentials.add(conditionned);
          }
        }
        // 3 - Trier les masses de croyance : on utilise l'heuristique simple de
        // la longueur moyenne du coeur.
        LOGGER.info("3 - Trier les masses de croyance : on utilise l'heuristique simple de la longueur moyenne du coeur.");
        List<List<Pair<byte[], Float>>> orderedmass = CombinationAlgos
            .orderMass(conditionnedMassPotentials);

        // 4 - Fusion 2 à 2 des masses de croyances
        LOGGER.info("4 - Fusion 2 à 2 des masses de croyances");
        try {
          List<Pair<byte[], Float>> m1values = conditionnedMassPotentials.get(0);
          for (int i = 1; i < orderedmass.size(); i++) {
            List<Pair<byte[], Float>> m2values = conditionnedMassPotentials.get(i);
            m1values = this.smetsOp2mass(m1values, m2values);
            LOGGER.info("Taille des listes = " + m1values.size() + " et " + m2values.size());
          }
          return m1values;
        
        } catch (Exception e) {
          LOGGER
              .error("Dammit captain, the combination of 2 masses bloody crashed! Maybe ya should"
                  + " take a look at this damn report just below!");
          e.printStackTrace();
        }
      } catch (Exception e1) {
        e1.printStackTrace();
      }
    }
    return null;
  }

  /**
   * @param mass1
   * @param mass2
   * @return
   */
  private List<Pair<byte[], Float>> smetsOp2mass(List<Pair<byte[], Float>> mass1,
      List<Pair<byte[], Float>> mass2) {

    // Le résultat est une masse de croyance.
    List<Pair<byte[], Float>> massresult = new ArrayList<Pair<byte[], Float>>();
    // Fusion des masses
    for (Pair<byte[], Float> hypothesis1 : mass1) {
      for (Pair<byte[], Float> hypothesis2 : mass2) {
        massresult.add(new Pair<byte[], Float>(Utils.byteIntersection(hypothesis1.getFirst(),
            hypothesis2.getFirst()), hypothesis1.getSecond() * hypothesis2.getSecond()));
      }
    }
    // Tri et suppression des doublons
    CombinationAlgos.sortKernel(massresult);
    CombinationAlgos.deleteDoubles(massresult);

    return massresult;
  }

  @Override
  public Float getConflict() {
    return new Float(0f);
  }
}

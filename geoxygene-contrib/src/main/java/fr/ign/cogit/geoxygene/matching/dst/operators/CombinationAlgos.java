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
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.matching.dst.util.Pair;
import fr.ign.cogit.geoxygene.matching.dst.util.Utils;

/**
 * @author Bertrand Dumenieu
 */
public final class CombinationAlgos {
  static Logger logger = Logger.getLogger(CombinationAlgos.class);

  /**
   * @param mass_sets
   * @return
   * @throws Exception
   */
  public static final byte[] combine(List<List<byte[]>> mass_sets) throws Exception {
//    if (logger.isDebugEnabled()) {
//      logger.debug("----COMBINING CORE----");
//      for (List<byte[]> core : mass_sets) {
//        logger.debug("on focal set...");
//        for (byte[] focal : core) {
//          logger.debug(Arrays.toString(focal));
//        }
//      }
//    }
    // 1 : calculer l'union des éléments focaux de chaque masse de croyance.
    List<byte[]> cores = new ArrayList<byte[]>();
    for (List<byte[]> focalsets : mass_sets) {
      if (focalsets.size() > 1) {
        byte[] set1 = focalsets.get(0);
        for (int i = 1; i < focalsets.size(); i++) {
          byte[] set2 = focalsets.get(i);
          set1 = Utils.byteUnion(set1, set2);
        }
        cores.add(set1);
      } else
        if (focalsets.size() == 1) {
          cores.add(focalsets.get(0));
        } else {
          logger.error("Trying to combine a mass function with no focal sets");
        }
    }
    // 2 : Calculer l'intersection des noyaux de chaque masse de croyance
    if (cores.size() > 1) {
      byte[] core1 = cores.get(0);
      for (int i = 1; i < cores.size() - 1; i++) {
        byte[] core2 = cores.get(i);
        core1 = Utils.byteIntersection(core1, core2);
      }
//      logger.debug("Combined core : " + Arrays.toString(core1));
      return core1;
    } else
      if (cores.size() == 1) {
//        logger.debug("Combined core : " + Arrays.toString(cores.get(0)));
        return cores.get(0);
      } else {
        logger.error("Combined core is null!");
      }
    throw new Exception("An error occured during the cores combination");
  }

  /**
   * @param masscore
   * @param conditionner
   * @param closedworld
   * @return
   */
  public static List<Pair<byte[], Float>> conditionning(List<Pair<byte[], Float>> masscore,
      byte[] conditionner, boolean closedworld) {
    if (masscore.size() == 1 && Utils.isEmpty(masscore.get(0).getFirst())) {
      logger.error("Conditionning only defined if there exist CinterB != void");
      return masscore;
    }
//    if (logger.isDebugEnabled()) {
//      logger.debug("Conditionner : " + Arrays.toString(conditionner));
//      logger.debug("Core to condition:");
//      for (Pair<byte[], Float> focal : masscore) {
//        logger.debug(focal.getSecond() + " " + Arrays.toString(focal.getFirst()));
//      }
//    }
    List<Pair<byte[], Float>> conditionnedlist = new ArrayList<Pair<byte[], Float>>();
    Float k = 0.0f;
    for (Pair<byte[], Float> focal : masscore) {
      byte[] intersection = Utils.byteIntersection(focal.getFirst(), conditionner);

      if (!Utils.isEmpty(intersection)) {
        conditionnedlist.add(new Pair<byte[], Float>(intersection, focal.getSecond()));
        k += focal.getSecond();
      }

    }
    CombinationAlgos.sortKernel(conditionnedlist);
    // Suppression des doublons éventuels
    deleteDoubles(conditionnedlist);
    if (logger.isDebugEnabled()) {
      if (k == 0) {
        logger
            .error("In conditionning : normalization by 0. The conditionned mass potential is not well defined.");
      }
      if (k < 0) {
        logger.error("In conditionning : k is < 0");
      }
    }
    // normalization with K, K !=0!;
    for (Pair<byte[], Float> mass : conditionnedlist) {
      mass.setSecond(mass.getSecond() / k);
    }
//    if (logger.isDebugEnabled()) {
//      logger.debug("Conditionned List:");
//      for (Pair<byte[], Float> focal : conditionnedlist) {
//        logger.debug(focal.getSecond() + " " + Arrays.toString(focal.getFirst()));
//      }
//    }
    return conditionnedlist;
  }

  /**
   * @param kernel
   */
  public static void sortKernel(List<Pair<byte[], Float>> kernel) {
    Comparator<Pair<byte[], Float>> comparator = new Comparator<Pair<byte[], Float>>() {
      @Override
      public int compare(Pair<byte[], Float> o1, Pair<byte[], Float> o2) {
        Comparator<byte[]> internalcomp = Utils.byteArrayComparator();
        return internalcomp.compare(o1.getFirst(), o2.getFirst());
      }
    };
    Collections.sort(kernel, comparator);
  }

  /**
   * @param conditionnedlist
   */
  public static void deleteDoubles(List<Pair<byte[], Float>> conditionnedlist) {
    List<Pair<byte[], Float>> toremove = new ArrayList<Pair<byte[], Float>>(10);
    for (int i = 0; i < conditionnedlist.size() - 1; i++) {
      Pair<byte[], Float> pair = conditionnedlist.get(i);
      Pair<byte[], Float> pair2 = conditionnedlist.get(i + 1);
      if (Arrays.equals(pair.getFirst(), pair2.getFirst())) {
        pair2.setSecond(pair2.getSecond() + pair.getSecond());
        toremove.add(pair);
      }
    }
    conditionnedlist.removeAll(toremove);
  }

  /**
   * @param masspotentials
   * @return
   */
  public static List<List<Pair<byte[], Float>>> orderMass(
      List<List<Pair<byte[], Float>>> masspotentials) {
//    logger.debug("Sorting the mass potentials");
    List<Pair<Integer, Float>> toSort = new ArrayList<Pair<Integer, Float>>();
    List<List<Pair<byte[], Float>>> newpotentials = new ArrayList<List<Pair<byte[], Float>>>();
    for (List<Pair<byte[], Float>> src : masspotentials) {
      float nb = 1;
      float sum = 0;
      for (Pair<byte[], Float> hypcore : src) {
        for (byte b : hypcore.getFirst()) {
          if (b == 1) {
            sum += 1;
          }
        }
        nb++;
      }
      toSort.add(new Pair<Integer, Float>(masspotentials.indexOf(src), sum / nb));
    }
    Collections.sort(toSort, new Comparator<Pair<Integer, Float>>() {
      @Override
      public int compare(Pair<Integer, Float> o1, Pair<Integer, Float> o2) {
        return o1.getSecond().compareTo(o2.getSecond());
      }
    });

    for (Pair<Integer, Float> src : toSort) {
      newpotentials.add(masspotentials.get(src.getFirst()));
    }
//    if (logger.isDebugEnabled()) {
//      logger.debug("Sorted mass list:");
//      for (Pair<Integer, Float> src : toSort) {
//        logger.debug(src.getSecond() + " score for " + src.getFirst());
//      }
//    }
    return newpotentials;
  }
}

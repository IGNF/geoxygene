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

package fr.ign.cogit.geoxygene.matching.dst;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.matching.dst.evidence.AppriouMapper;
import fr.ign.cogit.geoxygene.matching.dst.evidence.CriteriaLoader;
import fr.ign.cogit.geoxygene.matching.dst.evidence.Hypothesis;
import fr.ign.cogit.geoxygene.matching.dst.evidence.Source;
import fr.ign.cogit.geoxygene.matching.dst.geoAppariement.GeoAppariement;
import fr.ign.cogit.geoxygene.matching.dst.geoAppariement.SimpleGeomHypothesis;
import fr.ign.cogit.geoxygene.matching.dst.geoAppariement.SingleLinkageAHC;
import fr.ign.cogit.geoxygene.matching.dst.operators.DempsterOp;
import fr.ign.cogit.geoxygene.matching.dst.operators.SmetsOp;
import fr.ign.cogit.geoxygene.matching.dst.util.Combinations;
import fr.ign.cogit.geoxygene.matching.dst.util.FuzzyInterval;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * 
 * *Tests unitaires
 * @author Julien Perret
 * 
 */
public class Unitests {
  Logger logger = Logger.getLogger(Unitests.class);

  public static void main(String[] argv) {
    Unitests unitests = new Unitests();
    // unitests.testCombinations();
    // unitests.testIncrementalList();
    // unitests.testCHA();
    // unitests.testFuzzyIntervals();
    // unitests.testnewAppariement();
    // unitests.testPermutations();
    // unitests.geoAppAnaMariaTest();
    // System.exit(1);
    File f1 = new File("/home/BDumenieu/Bureau/temp/poubelle_light.shp");
    File f2 = new File("/home/BDumenieu/Bureau/temp/verniquet_light.shp");

    IPopulation<IFeature> pop1 = ShapefileReader.read(f1.getPath());
    IPopulation<IFeature> pop2 = ShapefileReader.read(f2.getPath());

    Collection<Source> criteria = CriteriaLoader.load(FactoryType.SURFACE);
    LinkedList<List<IFeature>> combinations = Combinations.enumerate(pop1
        .getElements());
    List<Hypothesis> hypotheses = new ArrayList<Hypothesis>();
    for (List<IFeature> l : combinations) {
      
      if (l.size() == 1) {
        hypotheses.add(new SimpleGeomHypothesis(l.get(0)));
      } else if (l.size() > 1) {

        IFeature[] featarray = new IFeature[l.size()];
        // hypotheses.add(new ComplexGeomHypothesis(l.toArray(featarray)));
      }
    }
    for (Hypothesis h : hypotheses) {
      h.toString();
    }
    int n = 11;
    try {
      GeoAppariement mp = GeoAppariement.getInstance();
      //mp.run(criteria, pop2.get(n), hypotheses);
    } catch (Exception e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
    // MatchingProcess<GeomHypothesis> mp =new
    // MatchingProcess<GeomHypothesis>(criteria,(GeomHypothesis)new
    // SimpleGeomHypothesis(pop2.getElements().get(n)),hypotheses,ChoiceType.CREDIBILITY,true,true);//
    // new MatchingProcess<IFeature>(criteria, pop1.get(0), pop1.getElements(),
    // true);

    try {
      /*
       * List<Pair<ChoiceType, Pair<List<GeomHypothesis>, Float>>> result =
       * mp.run();
       * 
       * Population<IFeature> popres = new Population<IFeature>();
       * System.out.println
       * ("CHOSEN WITH VALUE : "+result.get(0).getSecond().getSecond());
       * popres.add((IFeature)result.get(0).getSecond().getFirst().get(0));
       * popres.add(pop2.getElements().get(n)); ShapefileWriter.write(popres,
       * "/home/BDumenieu/Bureau/temp/result.shp");
       */
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void testPermutations() {
    Character[] perm = { 'A', 'B', 'C', 'D', 'E', 'F' };
    LinkedList<List<Character>> res = Combinations.permutation(Arrays
        .asList(perm));
    Iterator<List<Character>> it = res.iterator();
    while (it.hasNext()) {
      List<Character> c = it.next();
      System.out.println(c);
    }

  }



  private void testCombinations() {
    long t = System.currentTimeMillis();
    Character[] array = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M' };
    List<List<Character>> res = Combinations.enumerate(Arrays.asList(array));
    System.out.println("Initialement " + array.length + " élements "
        + res.size() + "résultats, étaient attendus "
        + Combinations.combination(array.length));
    System.out.println("Took : " + (System.currentTimeMillis() - t) + "ms");
  }

  private void testCHA() {
    File f1 = new File("/home/BDumenieu/Bureau/poubelle.shp");
    IPopulation<IFeature> pop = ShapefileReader.read(f1.getPath());
    SingleLinkageAHC ahc = new SingleLinkageAHC(pop, 10);

  }

  /**
   * Test de l'appariement en utilisant les valeurs d'exemples tirées de la
   * thèse d'Ana Maria. Sert uniquement à vérifier si le résultat est cohérent.
   */
  private void geoAppAnaMariaTest() {
    List<List<Pair<byte[], Float>>> values = new ArrayList<List<Pair<byte[], Float>>>();
    // INitialisation des masses de croyance

    // Pour C1
    List<Pair<byte[], Float>> m1c1 = new ArrayList<Pair<byte[], Float>>();
    m1c1.add(new Pair<byte[], Float>(new byte[] { 1, 0 }, 0.4f));
    m1c1.add(new Pair<byte[], Float>(new byte[] { 1, 1 }, 0.6f));

    List<Pair<byte[], Float>> m2c1 = new ArrayList<Pair<byte[], Float>>();
    m2c1.add(new Pair<byte[], Float>(new byte[] { 1, 0 }, 0.3f));
    m2c1.add(new Pair<byte[], Float>(new byte[] { 1, 1 }, 0.7f));
    List<List<Pair<byte[], Float>>> c1 = new ArrayList<List<Pair<byte[], Float>>>();
    c1.add(m1c1);
    c1.add(m2c1);
    List<Pair<byte[], Float>> mergedc1 = new SmetsOp(false).combine(c1);

    // Pour C2
    List<Pair<byte[], Float>> m1c2 = new ArrayList<Pair<byte[], Float>>();
    m1c2.add(new Pair<byte[], Float>(new byte[] { 1, 0 }, 0.1f));
    m1c2.add(new Pair<byte[], Float>(new byte[] { 0, 1 }, 0.9f));

    List<Pair<byte[], Float>> m2c2 = new ArrayList<Pair<byte[], Float>>();
    m2c2.add(new Pair<byte[], Float>(new byte[] { 0, 1 }, 1f));
    List<List<Pair<byte[], Float>>> c2 = new ArrayList<List<Pair<byte[], Float>>>();
    c2.add(m1c2);
    c2.add(m2c2);
    List<Pair<byte[], Float>> mergedc2 = new SmetsOp(false).combine(c2);

    // Pour C3
    List<Pair<byte[], Float>> m1c3 = new ArrayList<Pair<byte[], Float>>();
    m1c3.add(new Pair<byte[], Float>(new byte[] { 1, 0 }, 0.35f));
    m1c3.add(new Pair<byte[], Float>(new byte[] { 1, 1 }, 0.65f));

    List<Pair<byte[], Float>> m2c3 = new ArrayList<Pair<byte[], Float>>();
    m2c3.add(new Pair<byte[], Float>(new byte[] { 1, 0 }, 0.3f));
    m2c3.add(new Pair<byte[], Float>(new byte[] { 1, 1 }, 0.7f));
    List<List<Pair<byte[], Float>>> c3 = new ArrayList<List<Pair<byte[], Float>>>();
    c3.add(m1c3);
    c3.add(m2c3);
    List<Pair<byte[], Float>> mergedc3 = new SmetsOp(false).combine(c3);

    AppriouMapper mapper = new AppriouMapper();

    try {
      List<Pair<byte[], Float>> mapped1 = mapper.mapFocalisedToGlobal(0, 3,
          mergedc1);
      List<Pair<byte[], Float>> mapped2 = mapper.mapFocalisedToGlobal(1, 3,
          mergedc2);
      List<Pair<byte[], Float>> mapped3 = mapper.mapFocalisedToGlobal(2, 3,
          mergedc3);
      values.clear();
      values.add(mapped1);
      values.add(mapped2);
      values.add(mapped3);
      new DempsterOp(true).combine(values);

    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }


  private void testFuzzyIntervals() {

    String msg = "Normal Fuzzy Interval creation : ";
    // Intervalle correct
    try {
      float[] x = { 0, 1, 2, 3 };
      float[] y = { 0, 1, 0, 0 };
      new FuzzyInterval(x, y);
      logger.info(msg + "SUCCESS");
    } catch (Exception e) {
      logger.info(msg + "FAIL");
    }

    msg = "Ys out of bounds fuzzy interval creation : ";
    try {
      float[] x = { 0, 1, 2, 3 };
      float[] y = { 0, 5, 0, 0 };
      new FuzzyInterval(x, y);
      logger.info(msg + "SUCCESS");
    } catch (Exception e) {
      logger.info(msg + "FAIL");
    }
    msg = "Non sorted fuzzy interval creation : ";
    try {
      float[] x = { 0, 5, 1, 3 };
      float[] y = { 0, 1, 0, 0 };
      new FuzzyInterval(x, y);
      logger.info(msg + "SUCCESS");
    } catch (Exception e) {
      logger.info(msg + "FAIL");
    }
    msg = "Getting value in well formed Fuzzy interval : ";
    try {
      float[] x = { 0, 1, 2, 3 };
      float[] y = { 0, 1, 0, 0 };
      FuzzyInterval fi = new FuzzyInterval(x, y);
      double v = fi.getValue(0.3d);
      logger.info("Value : " + v);
      v = fi.getValue(1.3f);
      logger.info("Value : " + v);
      v = fi.getValue(2.8f);
      logger.info("Value : " + v);
      logger.info(msg + "SUCCESS");
    } catch (Exception e) {
      logger.info(msg + "FAIL");
    }
    msg = "Getting out of bounds value : ";
    try {
      float[] x = { 0, 1, 2, 3 };
      float[] y = { 0, 1, 0, 0 };
      FuzzyInterval fi = new FuzzyInterval(x, y);
      double v = fi.getValue(8.0d);
      if (v < 0)
        logger.info(msg + "FAIL");
      else
        logger.debug(msg + "SUCCESS");

      logger.info("Value : " + v);
    } catch (Exception e) {
      logger.info(msg + "FAIL");
    }

  }

}

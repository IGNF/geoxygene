package fr.ign.cogit.geoxygene.contrib.graph;

import java.util.Collection;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.graphe.ARM;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

/**
 * 
 *
 */
public class ARMTest {
  
  Collection<IFeature> points;
  IPopulation<Noeud> popNoeudsToCompare;
  IPopulation<Arc> popArcsToCompare;
  
  private static Logger LOGGER = LogManager.getLogger(ARMTest.class.getName());
  
  /**
   * 
   * @throws Exception
   */
  @Before
  public void setUp() throws Exception {
    
    points = new HashSet<IFeature>();
    popNoeudsToCompare = new Population<Noeud>();
    popArcsToCompare = new Population<Arc>();
    
    Noeud nA = new Noeud(new DirectPosition(0, 0));
    nA.setId(1);
    System.out.println("A = " + nA.getId());
    points.add(nA);
    popNoeudsToCompare.add(nA);
    
    Noeud nB = new Noeud(new DirectPosition(2, 7));
    nB.setId(2);
    System.out.println("B = " + nB.getId());
    points.add(nB);
    popNoeudsToCompare.add(nB);
    
    Noeud nC = new Noeud(new DirectPosition(3, 3));
    nC.setId(3);
    System.out.println("C = " + nC.getId());
    points.add(nC);
    popNoeudsToCompare.add(nC);
    
    Noeud nD = new Noeud(new DirectPosition(6, 3));
    nD.setId(4);
    System.out.println("D = " + nD.getId());
    points.add(nD);
    popNoeudsToCompare.add(nD);
    
    Noeud nE = new Noeud(new DirectPosition(10, 1));
    nE.setId(5);
    System.out.println("E = " + nE.getId());
    points.add(nE);
    popNoeudsToCompare.add(nE);
    
    Noeud nF = new Noeud(new DirectPosition(9, 4));
    nF.setId(6);
    System.out.println("F = " + nF.getId());
    points.add(nF);
    popNoeudsToCompare.add(nF);
    
    Noeud nG = new Noeud(new DirectPosition(13, 2));
    nG.setId(7);
    System.out.println("G = " + nG.getId());
    points.add(nG);
    popNoeudsToCompare.add(nG);
    
    Noeud nH = new Noeud(new DirectPosition(16, 4));
    nH.setId(8);
    System.out.println("H = " + nH.getId());
    points.add(nH);
    popNoeudsToCompare.add(nH);
    
    LOGGER.debug("DateSet for ARM tests : " + points.toString());
  }

  @Test
  public void testCreeARM() {
    LOGGER.debug("Start testing createARM");
    
    CarteTopo arm = ARM.creeARM(points);
    
    // Compare Node
    System.out.println("Nombre de noeuds = " + arm.getPopNoeuds().size());
    System.out.println("Nombre de noeuds = " + popNoeudsToCompare.size());
    
    // Compare Edge
    System.out.println("Nombre d'arcs = " + arm.getPopArcs().size());
    for (Arc arc : arm.getPopArcs()) {
      System.out.println(arc.getIdNoeudIni() + " - " + arc.getIdNoeudFin());
    }
    
    Assert.assertTrue(true);
  }

}

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

public class CarteTopoTest {
  CarteTopo carte;
  Noeud n1;
  Noeud n2;
  Noeud n3;
  Noeud n4;
  Noeud n5;
  Noeud n6;
  Noeud n7;
  Arc a12;
  Arc a23;
  Arc a14;
  Arc a45;
  Arc a16;
  Arc a67;
  @Before
  public void setUp() throws Exception {
    carte = new CarteTopo("Carte Test");
    n1 = new Noeud(new DirectPosition(0, 0));
    n1.setId(1);
    n2 = new Noeud(new DirectPosition(1, 0));
    n2.setId(2);
    n3 = new Noeud(new DirectPosition(2, 0));
    n3.setId(3);
    n4 = new Noeud(new DirectPosition(0, 1));
    n4.setId(4);
    n5 = new Noeud(new DirectPosition(0, 2));
    n5.setId(5);
    n6 = new Noeud(new DirectPosition(-1, 0));
    n6.setId(6);
    n7 = new Noeud(new DirectPosition(-2, 0));
    n7.setId(7);
    a12 = new Arc(n1, n2);
    a12.setOrientation(-1);
    a23 = new Arc(n2, n3);
    a23.setOrientation(-1);
    a14 = new Arc(n1, n4);
    a14.setOrientation(1);
    a45 = new Arc(n5, n4);
    a45.setOrientation(-1);
    a16 = new Arc(n6, n1);
    a16.setOrientation(-1);
    a67 = new Arc(n6, n7);
    a67.setOrientation(1);
    carte.addNoeud(n1);
    carte.addNoeud(n2);
    carte.addNoeud(n3);
    carte.addNoeud(n4);
    carte.addNoeud(n5);
    carte.addNoeud(n6);
    carte.addNoeud(n7);
    carte.addArc(a12);
    carte.addArc(a23);
    carte.addArc(a14);
    carte.addArc(a45);
    carte.addArc(a16);
    carte.addArc(a67);
  }

  @Test
  public void testFiltreNoeudsSimples() {
    carte.filtreNoeudsSimples();
    for (Arc a : carte.getPopArcs()) {
      // node between nodes 1 and 7
      if (a.getNoeudIni() == n7) {
        Assert.assertEquals(n1, a.getNoeudFin());
        Assert.assertEquals(-1, a.getOrientation());
      }
      if (a.getNoeudFin() == n7) {
        Assert.assertEquals(n1, a.getNoeudIni());
        Assert.assertEquals(1, a.getOrientation());
      }
      // node between nodes 1 and 5
      if (a.getNoeudIni() == n5) {
        Assert.assertEquals(n1, a.getNoeudFin());
        Assert.assertEquals(-1, a.getOrientation());
      }
      if (a.getNoeudFin() == n5) {
        Assert.assertEquals(n1, a.getNoeudIni());
        Assert.assertEquals(1, a.getOrientation());
      }
      // node between nodes 1 and 3
      if (a.getNoeudIni() == n3) {
        Assert.assertEquals(n1, a.getNoeudFin());
        Assert.assertEquals(1, a.getOrientation());
      }
      if (a.getNoeudFin() == n3) {
        Assert.assertEquals(n1, a.getNoeudIni());
        Assert.assertEquals(-1, a.getOrientation());
      }
    }
    for (Noeud n : carte.getPopNoeuds()) {
      System.out.println(n);
      for (Arc a : n.getEntrants()) {
        System.out.println("entrant " + a);
      }
      for (Arc a : n.getSortants()) {
        System.out.println("sortant " + a);
      }
    }
    for (Arc a : carte.getPopArcs()) {
      System.out.println(a);
    }
  }
  
  /**
   * Test method for
   * {@link fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo#creeTopologieFaces()}
   * .
   */
  @Test
  public void testCreeTopologieFaces() {
    CarteTopo carte = new CarteTopo("test"); //$NON-NLS-1$
    IPopulation<Noeud> noeuds = carte.getPopNoeuds();
    noeuds.nouvelElement(new GM_Point(new DirectPosition(0, 0)));// n0
    noeuds.nouvelElement(new GM_Point(new DirectPosition(5, 0)));// n1
    noeuds.nouvelElement(new GM_Point(new DirectPosition(5, 2)));// n2
    noeuds.nouvelElement(new GM_Point(new DirectPosition(5, 5)));// n3
    noeuds.nouvelElement(new GM_Point(new DirectPosition(0, 5)));// n4
    noeuds.nouvelElement(new GM_Point(new DirectPosition(1, 1)));// n5
    noeuds.nouvelElement(new GM_Point(new DirectPosition(3, 1)));// n6
    noeuds.nouvelElement(new GM_Point(new DirectPosition(4, 1)));// n7
    noeuds.nouvelElement(new GM_Point(new DirectPosition(4, 4)));// n8
    noeuds.nouvelElement(new GM_Point(new DirectPosition(1, 4)));// n9
    noeuds.nouvelElement(new GM_Point(new DirectPosition(1, 3)));// n10
    noeuds.nouvelElement(new GM_Point(new DirectPosition(3, 3)));// n11
    noeuds.nouvelElement(new GM_Point(new DirectPosition(2, 3)));// n12
    noeuds.nouvelElement(new GM_Point(new DirectPosition(2, 2)));// n13
    noeuds.nouvelElement(new GM_Point(new DirectPosition(6, 2)));// n14
    noeuds.nouvelElement(new GM_Point(new DirectPosition(7, 2)));// n15
    noeuds.nouvelElement(new GM_Point(new DirectPosition(6, 3)));// n16

    IPopulation<Arc> arcs = carte.getPopArcs();
    Class<?>[] signaturea = { carte.getPopNoeuds().getClasse(),
        carte.getPopNoeuds().getClasse() };
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(0), noeuds.get(1) });// a0
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(1), noeuds.get(2) });// a1
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(2), noeuds.get(3) });// a2
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(3), noeuds.get(4) });// a3
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(4), noeuds.get(0) });// a4

    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(2), noeuds.get(14) });// a5
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(14), noeuds.get(15) });// a6
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(14), noeuds.get(16) });// a7

    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(5), noeuds.get(6) });// a8
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(6), noeuds.get(7) });// a9
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(7), noeuds.get(8) });// a10
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(8), noeuds.get(9) });// a11
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(9), noeuds.get(10) });// a12
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(10), noeuds.get(5) });// a13

    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(6), noeuds.get(11) });// a14
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(11), noeuds.get(12) });// a15
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(12), noeuds.get(10) });// a16
    arcs.nouvelElement(signaturea,
        new Object[] { noeuds.get(12), noeuds.get(13) });// a17

    carte.creeTopologieFaces();
    for (Face face : carte.getListeFaces()) {
      System.out.println(face);
      System.out.println("arcs directs : "); //$NON-NLS-1$
      for (Arc arc : face.getArcsDirects()) {
        System.out.println(arc);
      }
      System.out.println("arcs indirects : "); //$NON-NLS-1$
      for (Arc arc : face.getArcsIndirects()) {
        System.out.println(arc);
      }
      System.out.println("arcs pendants : "); //$NON-NLS-1$
      for (Arc arc : face.getArcsPendants()) {
        System.out.println(arc);
      }
      System.out.println();
    }
    assert (carte.getListeFaces().size() == 4);
    // fail("Not yet implemented");
  }

}

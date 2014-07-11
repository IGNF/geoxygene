package fr.ign.cogit.geoxygene.util.algo.geomstructure;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class Vector2DTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testIsColinear() {
    IDirectPosition p1 = new DirectPosition(0.0, 0.0);
    IDirectPosition p2 = new DirectPosition(0.0, 10.0);
    IDirectPosition p3 = new DirectPosition(10.0, 0.0);
    IDirectPosition p4 = new DirectPosition(0.0, 5.0);
    IDirectPosition p5 = new DirectPosition(5.0, 0.0);

    Vector2D vect1 = new Vector2D(p1, p2);
    Vector2D vect2 = new Vector2D(p1, p3);
    Vector2D vect3 = new Vector2D(p1, p4);
    Vector2D vect4 = new Vector2D(p1, p5);
    Vector2D vect5 = new Vector2D(p5, p1);
    Assert.assertTrue(vect1.isColinear(vect3));
    Assert.assertTrue(vect2.isColinear(vect4));
    Assert.assertTrue(vect5.isColinear(vect2));
  }

  @Test
  public void testProject() {
    IDirectPosition p1 = new DirectPosition(0.0, 0.0);
    IDirectPosition p2 = new DirectPosition(10.0, 10.0);
    IDirectPosition p3 = new DirectPosition(0.0, 5.0);
    IDirectPosition p4 = new DirectPosition(5.0, 0.0);

    Vector2D vect = new Vector2D(p1, p2);
    Vector2D vect2 = new Vector2D(p1, p3);
    Vector2D vect3 = new Vector2D(p1, p4);
    Vector2D proj = vect.project(0.0);
    Assert.assertTrue(Math.abs(10 - proj.norme()) < 0.001);
    Assert.assertTrue(proj.isColinear(vect3));
    Vector2D proj2 = vect.project(Math.PI * 2);
    Assert.assertTrue(Math.abs(10 - proj2.norme()) < 0.001);
    Assert.assertTrue(proj2.isColinear(vect3));
    Vector2D proj3 = vect.project(Math.PI / 2);
    Assert.assertTrue(Math.abs(10 - proj3.norme()) < 0.001);
    Assert.assertTrue(proj3.isColinear(vect2, 0.0001));

  }

}

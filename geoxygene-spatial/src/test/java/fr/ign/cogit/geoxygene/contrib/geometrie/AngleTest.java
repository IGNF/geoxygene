package fr.ign.cogit.geoxygene.contrib.geometrie;

import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class AngleTest {
    IDirectPosition p;
    IDirectPosition p1;
    IDirectPosition p2;
    IDirectPosition p3;
    IDirectPosition p4;
    IDirectPosition p5;
    Angle a1;
    Angle a2;
    Angle a3;
    Angle a4;
    Angle a5;
   @Before
    public void setUp() throws Exception {
        p = new DirectPosition(0,0);
        p1 = new DirectPosition(1,0);
        p2 = new DirectPosition(1,1);
        p3 = new DirectPosition(0,1);
        p4 = new DirectPosition(-1,0);
        p5 = new DirectPosition(0,-1);
    }

    @Test
    public void testAngleIDirectPositionIDirectPosition() {
        a1 = new Angle(p,p1);
        System.out.println(a1.getValeur());
        assert(Math.abs(a1.getValeur()) < 0.01);
        a2 = new Angle(p,p2);
        System.out.println(a2.getValeur());
        assert(Math.abs(a2.getValeur() - Math.PI / 4) < 0.01);
        a3 = new Angle(p,p3);
        System.out.println(a3.getValeur());
        assert(Math.abs(a3.getValeur() - Math.PI / 2) < 0.01);
        a4 = new Angle(p,p4);
        System.out.println(a4.getValeur());
        assert(Math.abs(a4.getValeur() - Math.PI) < 0.01);
        a5 = new Angle(p,p5);
        System.out.println(a5.getValeur());
        assert(Math.abs(a5.getValeur() - 3 * Math.PI / 2) < 0.01);
    }

    @Test
    public void testEcarttrigo() {
        a1 = new Angle(p,p1);
        a2 = new Angle(p,p2);
        a5 = new Angle(p,p5);
        Angle ecart1 = Angle.ecarttrigo(a1, a2);
        System.out.println(ecart1.getValeur());
        assert(Math.abs(ecart1.getValeur() - Math.PI / 4) < 0.01);
        Angle ecart2 = Angle.ecarttrigo(a1, a5);
        System.out.println(ecart2.getValeur());
        assert(Math.abs(ecart2.getValeur() - 3 * Math.PI / 2) < 0.01);
    }

    @Test
    public void testAngleTroisPoints() {
        System.out.println("testAngleTroisPoints");
        a1 = Angle.angleTroisPoints(p4, p, p1);
        System.out.println(a1.getValeur());
        assert(Math.abs(a1.getValeur() - Math.PI) < 0.01);
        a2 = Angle.angleTroisPoints(p4, p, p2);
        System.out.println(a2.getValeur());
        assert(Math.abs(a2.getValeur() - 5 * Math.PI / 4) < 0.01);
        a3 = Angle.angleTroisPoints(p4, p, p3);
        System.out.println(a3.getValeur());
        assert(Math.abs(a3.getValeur() - 3 * Math.PI / 2) < 0.01);
        a4 = Angle.angleTroisPoints(p4, p, p4);
        System.out.println(a4.getValeur());
        assert(Math.abs(a4.getValeur()) < 0.01);
        a5 = Angle.angleTroisPoints(p4, p, p5);
        System.out.println(a5.getValeur());
        assert(Math.abs(a5.getValeur() - Math.PI / 2) < 0.01);
    }

}

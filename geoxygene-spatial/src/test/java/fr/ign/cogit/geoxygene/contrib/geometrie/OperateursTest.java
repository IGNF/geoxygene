package fr.ign.cogit.geoxygene.contrib.geometrie;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class OperateursTest {
    IDirectPosition point1;
    IDirectPosition point2;
    IDirectPosition point3;
    IDirectPosition point4;
    IDirectPosition point5;
    IDirectPosition point6;
    IDirectPosition point7;
    ILineString line1;
    @Before
    public void setUp() throws Exception {
        this.point1 = new DirectPosition(1,0);
        this.point2 = new DirectPosition(3,0);
        this.point3 = new DirectPosition(0,1);
        this.point4 = new DirectPosition(1,1);
        this.point5 = new DirectPosition(2,1);
        this.point6 = new DirectPosition(3,1);
        this.point7 = new DirectPosition(4,1);
        this.line1 = new GM_LineString(this.point1, this.point2);
    }
    @Test
    public void testProjectionIDirectPositionIDirectPositionIDirectPosition() {
        IDirectPosition projection1 = Operateurs.projection(this.point3, this.point1, this.point2);
        assert(projection1.equals(this.point1));
        IDirectPosition projection2 = Operateurs.projection(this.point4, this.point1, this.point2);
        assert(projection2.equals(this.point1));
        IDirectPosition projection3 = Operateurs.projection(this.point5, this.point1, this.point2);
        assert(projection3.equals(new DirectPosition(2,0)));
        IDirectPosition projection4 = Operateurs.projection(this.point6, this.point1, this.point2);
        assert(projection4.equals(this.point2));
        IDirectPosition projection5 = Operateurs.projection(this.point7, this.point1, this.point2);
        assert(projection5.equals(this.point2));
    }
    @Test
    public void testProjectionIDirectPositionILineString() {
        IDirectPosition projection1 = Operateurs.projection(this.point3, this.line1);
        assert(projection1.equals(this.point1));
        IDirectPosition projection2 = Operateurs.projection(this.point4, this.line1);
        assert(projection2.equals(this.point1));
        IDirectPosition projection3 = Operateurs.projection(this.point5, this.line1);
        assert(projection3.equals(new DirectPosition(2,0)));
        IDirectPosition projection4 = Operateurs.projection(this.point6, this.line1);
        assert(projection4.equals(this.point2));
        IDirectPosition projection5 = Operateurs.projection(this.point7, this.line1);
        assert(projection5.equals(this.point2));
    }
    @Test
    public void testMilieuIDirectPositionIDirectPosition() {
        IDirectPosition projection1 = Operateurs.milieu(this.point1, this.point2);
        assert(projection1.equals(new DirectPosition(2,0)));
    }
    @Test
    public void testCompileArcs() {
      WKTReader reader = new WKTReader();
      try {
        Geometry g1 = reader.read("LINESTRING ( 651124.9 6861379.5 1365, 651134 6861382.300000001 1364, 651156.2000000001 6861384.5 1363 )");
        Geometry g2 = reader.read("LINESTRING ( 651156.2000000001 6861384.5 1363, 651189.3 6861387.9 1361 )");
        List<ILineString> edges = new ArrayList<ILineString>();
        ILineString line1 = (ILineString) AdapterFactory.toGM_Object(g1);
        ILineString line2 = (ILineString) AdapterFactory.toGM_Object(g2);
        edges.add(line1);
        edges.add(line2);
        ILineString result = Operateurs.compileArcs(edges);
        System.out.println(result);
        edges.remove(line1);
        edges.add(line1);
        result = Operateurs.compileArcs(edges);
        System.out.println(result);
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      
    }
}

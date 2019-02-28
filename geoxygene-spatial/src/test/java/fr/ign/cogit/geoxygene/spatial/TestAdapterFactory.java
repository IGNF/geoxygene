package fr.ign.cogit.geoxygene.spatial;

import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class TestAdapterFactory {
    GeometryFactory factory;
    GM_LineString line;
    DirectPosition position;
    DirectPositionList positionList;

    @Before
    public void setUp() throws Exception {
        this.factory = new GeometryFactory();
        this.line = new GM_LineString(new DirectPosition(0, 0),
                new DirectPosition(1, 0));
        this.position = new DirectPosition(19, 25);
        this.positionList = new DirectPositionList(new DirectPosition(0, 0),
                new DirectPosition(1, 1));
    }

    @Test
    public void testToGeometry() throws Exception {
        Geometry result = AdapterFactory.toGeometry(this.factory, this.line);
        assert (result != null);
        assert (result.getClass() == LineString.class);
        assert (result.getLength() == 1);
    }

    @Test
    public void testToLineString() {
        LineString result = AdapterFactory
                .toLineString(this.factory, this.line);
        assert (result != null);
        assert (result.getLength() == 1);
    }

    @Test
    public void testToCoordinate() {
        Coordinate result = AdapterFactory.toCoordinate(this.position);
        assert (result != null);
        assert (result.x == 19);
        assert (result.y == 25);
    }

    @Test
    public void testToCoordinateSequence() {
        CoordinateSequence result = AdapterFactory.toCoordinateSequence(
                this.factory, this.positionList);
        assert (result != null);
        assert (result.size() == 2);
    }

    //
    // @Test
    // public void testToLinearRingArray() {
    // fail("Not yet implemented"); // TODO
    // }

    @Test
    public void testToGM_Object() throws Exception {
        Geometry result = AdapterFactory.toGeometry(this.factory, this.line);
        IGeometry geom = AdapterFactory.toGM_Object(result);
        assert (geom != null);
        assert (this.line.equals(geom));
    }

    @Test
    public void testToDirectPosition() {
        Coordinate result = AdapterFactory.toCoordinate(this.position);
        IDirectPosition p = AdapterFactory.toDirectPosition(result);
        assert (p != null);
        assert (this.position.equals(p));
    }

    @Test
    public void testToDirectPositionList() {
        CoordinateSequence result = AdapterFactory.toCoordinateSequence(
                this.factory, this.positionList);
        IDirectPositionList list = AdapterFactory.toDirectPositionList(result
                .toCoordinateArray());
        assert (list != null);
        assert (list.size() == this.positionList.size());
        for (int i = 0; i < list.size(); i++) {
            assert(list.get(i).equals(this.positionList.get(i)));
        }
    }

    // @Test
    // public void testTo2DCoordinateSequence() {
    // fail("Not yet implemented"); // TODO
    // }

    // @Test
    // public void testTo2DCoordinate() {
    // fail("Not yet implemented"); // TODO
    // }
    //
    // @Test
    // public void testTo2DDirectPosition() {
    // fail("Not yet implemented"); // TODO
    // }

    // @Test
    // public void testTo2DDirectPositionList() {
    // fail("Not yet implemented"); // TODO
    // }

    // @Test
    // public void testTo2DGM_Object() {
    // fail("Not yet implemented"); // TODO
    // }

    @Test
    public void testToGeometryType() {
        Class<?> result = AdapterFactory.toGeometryType(Point.class);
        assert (result == IPoint.class);
        result = AdapterFactory.toGeometryType(LineString.class);
        assert (result == ILineString.class);
        result = AdapterFactory.toGeometryType(Polygon.class);
        assert (result == IPolygon.class);
    }

    @Test
    public void testToJTSGeometryType() {
        Class<?> result = AdapterFactory.toJTSGeometryType(GM_Point.class);
        assert (result == Point.class);
        result = AdapterFactory.toJTSGeometryType(GM_LineString.class);
        assert (result == LineString.class);
        result = AdapterFactory.toJTSGeometryType(GM_Polygon.class);
        assert (result == Polygon.class);
    }
}

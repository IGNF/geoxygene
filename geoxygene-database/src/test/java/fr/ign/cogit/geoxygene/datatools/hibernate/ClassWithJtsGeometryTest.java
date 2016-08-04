package fr.ign.cogit.geoxygene.datatools.hibernate;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.datatools.hibernate.data.ClassWithJtsGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import junit.framework.TestCase;

public class ClassWithJtsGeometryTest extends TestCase {
	
	private GeodatabaseHibernate db ;
	
	private GeometryFactory geometryFactory = new GeometryFactory();
	
	@Override
	protected void setUp() throws Exception {
		db = new GeodatabaseHibernate();
		db.exeSQL("DELETE FROM database.classwithjtsgeometry");
	}
	
	public void testCreateWithoutSrids(){
		// create entry
		{
			db.begin();
			ClassWithJtsGeometry item = new ClassWithJtsGeometry();
			item.setGeom(new GM_Point(new DirectPosition(3.0, 4.0)));
			item.setJtsGeometry(geometryFactory.createPoint(new Coordinate(5.0,6.0)));
			db.makePersistent(item);
			db.commit();
		}
		
		// count entries
		int count = db.countObjects(ClassWithJtsGeometry.class);
		assertEquals(1, count);
		
		// read entries
		{
			List<ClassWithJtsGeometry> items = db.loadAll(ClassWithJtsGeometry.class);
			ClassWithJtsGeometry item = items.get(0);
			assertEquals("POINT (3.0 4.0 0.0)", item.getGeom().toString());
			assertEquals("POINT (5 6)", item.getJtsGeometry().toText());
		}
	}
	
	public void testCreateWithSrids(){
		// create entry
		{
			db.begin();
			ClassWithJtsGeometry item = new ClassWithJtsGeometry();
			
			IGeometry g = new GM_Point(new DirectPosition(3.0, 4.0));
			g.setCRS(8888);
			item.setGeom(g);
			
			Point jg = geometryFactory.createPoint(new Coordinate(5.0,6.0));
			jg.setSRID(9999);
			item.setJtsGeometry(jg);
			db.makePersistent(item);
			db.commit();
		}
		
		// count entries
		int count = db.countObjects(ClassWithJtsGeometry.class);
		assertEquals(1, count);
		
		// read entries
		{
			List<ClassWithJtsGeometry> items = db.loadAll(ClassWithJtsGeometry.class);
			ClassWithJtsGeometry item = items.get(0);
			
			assertEquals("POINT (3.0 4.0 0.0)", item.getGeom().toString());
			assertEquals(8888, item.getGeom().getCRS());
			
			assertEquals("POINT (5 6)", item.getJtsGeometry().toText());
			assertEquals(9999, item.getJtsGeometry().getSRID());
		}
	}

}

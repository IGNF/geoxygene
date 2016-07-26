package fr.ign.cogit.geoxygene.sig3d.tetraedrisation;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import junit.framework.Assert;

public class TestTetra {
	
	
	private static Logger log = Logger.getLogger(TestTetra.class);

	public static void main(String[] args) {


			new TestTetra().processTest();


	}

	@Test
	public void processTest() {

		IFeature feat = TestTetra.createCube(0, 0, 0, 10, 10, 10);
		Tetraedrisation tet = null;
		
	
		try {

			tet = new Tetraedrisation(feat);
		
		} catch (Error e) {
			log.warn("Native library tetraedrize not found, the test processTest is not ran");
			return;

		}
		tet.tetraedrise(false, true);

		int nbTri = tet.getTriangles().size();
		log.debug("Nombre de triangles : " + nbTri);

		Assert.assertEquals(12, nbTri);

		tet = new Tetraedrisation(new DefaultFeature(new GM_Solid(tet.getTriangles())));
		tet.tetraedrise(true, false);

		int nbTetra = tet.getTetraedres().size();

		log.debug("Nombre de tétraèdres : " + nbTetra);

		Assert.assertEquals(6, nbTetra);


	}


	private static IFeature createCube(double x0, double y0, double z0, double dx, double dy, double dz) {
		// On crée les 6 sommets du cube
		IDirectPosition p1 = new DirectPosition(x0, y0, z0);
		IDirectPosition p2 = new DirectPosition(x0 + dx, y0, z0);
		IDirectPosition p3 = new DirectPosition(x0 + dx, y0, z0 + dz);
		IDirectPosition p4 = new DirectPosition(x0, y0, z0 + dz);

		IDirectPosition p5 = new DirectPosition(x0, y0 + dy, z0);
		IDirectPosition p6 = new DirectPosition(x0 + dx, y0 + dy, z0);
		IDirectPosition p7 = new DirectPosition(x0 + dx, y0 + dy, z0 + dz);
		IDirectPosition p8 = new DirectPosition(x0, y0 + dy, z0 + dz);

		IDirectPositionList LPoint1 = new DirectPositionList();
		IDirectPositionList LPoint2 = new DirectPositionList();
		IDirectPositionList LPoint3 = new DirectPositionList();
		IDirectPositionList LPoint4 = new DirectPositionList();
		IDirectPositionList LPoint5 = new DirectPositionList();
		IDirectPositionList LPoint6 = new DirectPositionList();

		// On crée chaque face à l'aide de ces 4 sommets
		LPoint1.add(p1);
		LPoint1.add(p2);
		LPoint1.add(p3);
		LPoint1.add(p4);
		GM_LineString ls = new GM_LineString(LPoint1);
		GM_OrientableSurface surf1 = new GM_Polygon(ls);

		LPoint2.add(p4);
		LPoint2.add(p3);
		LPoint2.add(p7);
		LPoint2.add(p8);

		ls = new GM_LineString(LPoint2);
		GM_OrientableSurface surf2 = new GM_Polygon(ls);

		LPoint3.add(p3);
		LPoint3.add(p2);
		LPoint3.add(p6);
		LPoint3.add(p7);

		ls = new GM_LineString(LPoint3);
		GM_OrientableSurface surf3 = new GM_Polygon(ls);

		LPoint4.add(p2);
		LPoint4.add(p1);
		LPoint4.add(p5);
		LPoint4.add(p6);

		ls = new GM_LineString(LPoint4);
		GM_OrientableSurface surf4 = new GM_Polygon(ls);

		LPoint5.add(p1);
		LPoint5.add(p4);
		LPoint5.add(p8);
		LPoint5.add(p5);

		ls = new GM_LineString(LPoint5);
		GM_OrientableSurface surf5 = new GM_Polygon(ls);

		LPoint6.add(p6);
		LPoint6.add(p5);
		LPoint6.add(p8);
		LPoint6.add(p7);

		ls = new GM_LineString(LPoint6);
		GM_OrientableSurface surf6 = new GM_Polygon(ls);
		// On créé le solide à partir des 6 faces
		ArrayList<IOrientableSurface> LFace = new ArrayList<IOrientableSurface>();
		LFace.add(surf1);
		LFace.add(surf2);
		LFace.add(surf3);
		LFace.add(surf4);
		LFace.add(surf5);
		LFace.add(surf6);
		return new DefaultFeature(new GM_Solid(LFace));

	}

}

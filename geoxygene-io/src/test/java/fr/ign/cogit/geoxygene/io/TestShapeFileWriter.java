package fr.ign.cogit.geoxygene.io;

import java.io.File;

import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.google.common.io.Files;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import junit.framework.TestCase;

public class TestShapeFileWriter extends TestCase {

	@Test
    public void testWritePoint() {
		IFeatureCollection<IFeature> featColl = new FT_FeatureCollection<>();
		
		IFeature feat = new DefaultFeature(new GM_Point(new DirectPosition(1,1)));
		AttributeManager.addAttribute(feat, "ID", 1, "Integer");
		AttributeManager.addAttribute(feat, "Nom", "Canard", "String");
		AttributeManager.addAttribute(feat, "Taile", 42.42 , "Double");
		
		featColl.add(feat);
		File folder = Files.createTempDir();
		String pathFolder = folder.getAbsolutePath();
		System.out.println(pathFolder);
		try {
			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
			ShapefileWriter.write(featColl,pathFolder+ ".shp", sourceCRS);
		} catch (NoSuchAuthorityCodeException e) {
			fail(e.getMessage());
		} catch (FactoryException e) {
			fail(e.getMessage());
		}
		
		assertTrue((new File(pathFolder+".shp").exists()));
		assertTrue((new File(pathFolder+".dbf").exists()));
		assertTrue((new File(pathFolder+".shx").exists()));
		assertTrue((new File(pathFolder+".prj").exists()));
	}

}

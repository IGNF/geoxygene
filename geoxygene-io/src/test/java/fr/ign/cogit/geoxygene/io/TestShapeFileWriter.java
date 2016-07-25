package fr.ign.cogit.geoxygene.io;

import java.io.File;

import org.geotools.referencing.CRS;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

public class TestShapeFileWriter {

	
	@Test
    public void testWritePoint() {
		
		
		
		IFeatureCollection<IFeature> featColl = new FT_FeatureCollection<>();
		
		IFeature feat = new DefaultFeature(new GM_Point(new DirectPosition(1,1)));
		AttributeManager.addAttribute(feat, "ID", 1, "Integer");
		AttributeManager.addAttribute(feat, "Nom", "Canard", "String");
		AttributeManager.addAttribute(feat, "Taile", 42.42 , "Double");
		
		featColl.add(feat);
		String pathFolder = "/home/mickael/Bureau/Anouk/test"; // getClass().getClassLoader().getResource("shp/").toString()+"test";
		try {
			CoordinateReferenceSystem sourceCRS = CRS.decode("EPSG:4326");
			ShapefileWriter.write(featColl,pathFolder+ ".shp", sourceCRS);
		} catch (NoSuchAuthorityCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FactoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		
		assert((new File(pathFolder+".shp").exists()));
		assert((new File(pathFolder+".dbf").exists()));
		assert((new File(pathFolder+".shx").exists()));
		assert((new File(pathFolder+".prj").exists()));
		
		IFeatureCollection<IFeature> featCollIn = ShapefileReader.read(pathFolder+".shp");
		
		assert(!featCollIn.isEmpty());
		
		
		
		assert(featColl.get(0).equals(featCollIn.get(0)));
		assert(featColl.get(0).getGeom().equals(featCollIn.get(0).getGeom()));

	}

}

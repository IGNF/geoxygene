package fr.ign.cogit.geoxygene.filter;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 */
public class ShapeFilter implements FilenameFilter {

    @Override
    public boolean accept(File dir, String name) {
        return (name.endsWith(".shp"));
    }
    
    public boolean accept(final File f) {
      return (f.isFile() && (f.getAbsolutePath().endsWith(".shp") || f.getAbsolutePath().endsWith(".SHP")) || f.isDirectory());
    }

    public String getDescription() {
        return "ShapefileReader.ESRIShapefiles";
    }
}

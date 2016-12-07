package fr.ign.cogit.geoxygene.util.conversion;

import java.io.File;
import java.io.FilenameFilter;

public class ScanShapeFile {

  public static String[] getShapeFileInDiretory(String dir) {

    File f = new File(dir);
    return f.list(new FilenameFilter() {

      @Override
      public boolean accept(File dir, String name) {

        return name.contains(".shp");
      }
    });

  }
  
}

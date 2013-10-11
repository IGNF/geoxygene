package fr.ign.cogit.geoxygene.filter;

import java.io.File;
import java.io.FilenameFilter;

public class XMLFilter implements FilenameFilter {
  
  @Override
  public boolean accept(File dir, String name) {
      return (name.endsWith(".xml"));
  }
  
  public boolean accept(final File f) {
    return (f.isFile() && (f.getAbsolutePath().endsWith(".xml") //$NON-NLS-1$
        || f.getAbsolutePath().endsWith(".XML") //$NON-NLS-1$
        ) || f.isDirectory());
  }

  public String getDescription() {
    return "XML Files"; //$NON-NLS-1$
  }
}

package fr.ign.cogit.geoxygene.sig3d.io.obj;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import fr.ign.cogit.geoxygene.sig3d.semantic.Map3D;

public class OBJExport {
  
  
  /**
   * Permet d'exporter dans un fichier f, une carte 3D m
   * @param f
   * @param m
   */
  public static void export(File f, Map3D m){
    OBJWriter ob;
    try {
      ob = new OBJWriter(f);
      ob.writeNode(m.getIMap3D().getBgeneral().getParent()
          .getParent().getParent().getParent());
      ob.close();
    } catch (FileNotFoundException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
    }
  }
  
  
  /**
   * Permet d'exporter dans un fichier f, une carte 3D m
   * @param f
   * @param m
   */
  public static void export(String path, Map3D m){
    export(new File(path),m);
  }


}

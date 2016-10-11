/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.dataloading;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.geotools.data.shapefile.files.ShpFiles;
import org.geotools.data.shapefile.shp.ShapefileException;
import org.geotools.data.shapefile.shp.ShapefileReader;
import org.geotools.data.shapefile.shp.ShapefileReader.Record;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDocOld;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileDB;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.util.conversion.AdapterFactory;

public class LoaderUtil {
  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(LoaderUtil.class.getName());

  // Supported file extensions
  public static final String ext[] = { "shp", "shx", "dbf" };

  // Supported data types
  public static final String type[] = { "batiment", "troncon_route",
      "troncon_chemin", "troncon_electrique", "troncon_voie_ferree",
      "troncon_cours_eau", "surface_eau", "cn", "ligne_orographique",
      "occ_sol", "administratif", "mnt", "masque" };

  // Supported data types
  public static final Hashtable<String, String> layerTypes() {
    Hashtable<String, String> table = new Hashtable<String, String>();
    table.put("batiment", "Buildings");
    table.put("troncon_chemin", "Pathways");
    table.put("troncon_route", "Road sections");
    table.put("troncon_electrique", "Electric sections");
    table.put("troncon_voie_ferree", "Railway sections");
    table.put("troncon_cours_eau", "Water sections");
    table.put("surface_eau", "Water areas");
    table.put("cn", "Contour lines");
    table.put("ligne_orographique", "Relief lines");
    table.put("occ_sol", "Land use");
    table.put("administratif", "Admin units");
    table.put("mnt", "DEM");
    table.put("masque", "Dataset mask");
    return table;
  }

  /**
   * returns the list of files from dataset to be traducted
   */

  public static Vector<?> listerRepertoire(File repertoire) {
    Vector<String> listeFinaleDesFichiers = new Vector<String>();
    String[] listefichiers;
    listefichiers = repertoire.list();
    for (String listefichier : listefichiers) {
      if (((listefichier.toLowerCase().endsWith(".shp"))) // && avec i+1 et i+2
          || listefichier.endsWith("_TDT") == true)
      // ||listefichiers[i].endsWith(".dbf")==true)
      // ||(listefichiers[i].endsWith(".shx")==true))
      {
        listeFinaleDesFichiers.add(listefichier.substring(0,
            listefichier.length() - 4));
      }
    }
    return listeFinaleDesFichiers;
  }

  /**
   * Creates a new dataset
   * @param srcPath : source path
   * @param dstPath : destination path
   * @return true if the dataset is copied, else false
   * @throws IOException
   */
  public static boolean copyDirectory(File srcPath, File dstPath)
      throws IOException {
    if (dstPath.exists()) {
      int rep = JOptionPane.showConfirmDialog(null,
          "The file already exists\nDo you want to erase it");
      if (rep == JOptionPane.OK_OPTION) {
        LoaderUtil.deleteDirectory(dstPath);
      } else {
        return false;
      }
    }
    if (srcPath.isDirectory()) {
      if (!dstPath.exists()) {
        dstPath.mkdir();
      }
      String files[] = srcPath.list();
      for (String file : files) {
        LoaderUtil.copyDirectory(new File(srcPath, file), new File(dstPath,
            file));
      }
    } else {
      if (!srcPath.exists()) {
        System.out.println("The directory doesn't exist");
        System.exit(0);
      } else {
        InputStream in = new FileInputStream(srcPath);
        OutputStream out = new FileOutputStream(dstPath);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
          out.write(buf, 0, len);
        }
        in.close();
        out.close();
      }
    }
    return true;
  }

  /**
   * Erases an old dataset
   * @param path : path of the dataset
   */

  static public void deleteDirectory(File path) {
    if (path.exists()) {
      File[] files = path.listFiles();
      for (File file : files) {
        if (file.isDirectory()) {
          LoaderUtil.deleteDirectory(file);
        } else {
          file.delete();
        }
      }
    }
    path.delete();
  }

  /**
   * Computes the traduction of the dataset
   * @param fos
   * @param modele
   * @param dest : destination path of the dataset
   * @param newNom : new name of the dataset
   */

  public static void traductionDe(FileOutputStream fos,
      DefaultListModel modele, File dest, String newNom) {

    Properties prop = new Properties();
    String cle;
    String val;

    try {
      int i = 0;
      while (i < modele.size()) {
        for (String extension : LoaderUtil.ext) {
          System.out.println(dest + "  i = " + i
              + (modele.elementAt(i).toString()) + " ext = " + extension); // to
          // drop
          File fichierSource = new File(dest + "//"
              + (modele.elementAt(i).toString()) + "." + extension);
          File fichierDest = new File(dest + newNom + "." + extension);
          cle = newNom + "." + extension;
          val = modele.elementAt(i).toString() + "." + extension;
          System.out.println(cle + "  " + val);
          prop.setProperty(cle, val);
          LoaderUtil.renameFile(fichierSource, fichierDest);
        }
        i++;
      }
      prop.store(fos, newNom);

    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  /**
   * Traduction to file
   * @param source : source
   * @param dest : destination
   */

  public static void renameFile(File source, File dest) {
    source.renameTo(dest);
  }

  /**
   * Reconstruction of a file
   * @param destination : destination path
   * @param nomRepertoire : name of the directory
   */

  public static void reconstructionDe(File destination, String nomRepertoire) {
    Properties prop = new Properties();
    FileInputStream fis;
    String fichierExtrait = null;
    System.out.println(nomRepertoire);
    try {
      fis = new FileInputStream("donnees_TDT//" + nomRepertoire
          + "_TDT.properties");
      prop.load(fis);

      // ////////////////////////////////////////////////////////"donnees_TDT//"
      // + source.getName()+"_TDT"
      String[] listefichiers;
      listefichiers = destination.list();
      for (String listefichier : listefichiers) {
        fichierExtrait = listefichier;// .substring(0,listefichiers[i].length()-4);
        System.out.println(fichierExtrait);
        // donnees_TDT\\allevard_TDT\\administratif.shp
        String reNom = prop.getProperty("\\" + fichierExtrait);
        System.out.println(reNom);
        if (fichierExtrait.endsWith(".shp") || fichierExtrait.endsWith(".shx")
            || fichierExtrait.endsWith(".dbf")) {
          LoaderUtil.renameFile(new File(destination + "\\" + fichierExtrait),
              new File(destination + "\\" + reNom));
        }
      }
      JOptionPane.showMessageDialog(null, "The export of " + nomRepertoire
          + " has successfully achieved");
      fis.close();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "The export of " + nomRepertoire
          + " failed");
      e.printStackTrace();
    }
  }

  /**
   * Main method computing data loading
   * @throws IOException
   * @throws ShapefileException
   */

  /*
   * public static void computeDataLoading(SourceDLM source, int scale) throws
   * ShapefileException, IOException { if
   * (EnrichFrame.getInstance().isResetSelected()) {
   * GeneralisationDataSet.getInstance().resetDataSet(); } String systemPath =
   * CartagenApplication.getInstance().getCheminDonnees(); DataLoadingConfig ccd
   * = new DataLoadingConfig(); systemPath = systemPath.replace("\\", "/"); if
   * (LoaderUtil.logger.isInfoEnabled()) {
   * LoaderUtil.logger.info("Data loading: " + systemPath); }
   * ccd.configuration(systemPath);
   * CartagenApplication.getInstance().setCheminDonnees(systemPath);
   * CartagenApplication.getInstance().loadData(source, scale); // load the
   * extent of the zone from a shapefile if needed
   * if(ImportDataFrame.extentFile) { String path = systemPath + "/" +
   * ImportDataFrame.extentClass + ".shp"; ShpFiles shpf = new ShpFiles(path);
   * ShapefileReader shr = new ShapefileReader(shpf, true, false); Record objet
   * = shr.nextRecord(); IPolygon geom = null; try { geom = (IPolygon)
   * AdapterFactory.toGM_Object((Geometry) objet.shape()); } catch (Exception e)
   * { e.printStackTrace(); return; }
   * GeneralisationDataSet.getInstance().getCartAGenDataSet().getZone().
   * setExtent(geom); shr.close(); } ((ShapeFileDataSet)
   * CartagenApplication.getInstance().getDataSet().
   * getCartAGenDataSet()).setSystemPath(LoadingFrame.cheminAbsolu);
   * CartagenApplication.getInstance().initialiserPositionGeographique(
   * !ImportDataFrame.extentFile);
   * CartagenApplication.getInstance().enrichData();
   * CartagenApplication.getInstance().initGeneralisation(); // add generated
   * cartagen ids in the shapefiles
   * CartagenApplication.getInstance().getDataSet(
   * ).getCartAGenDataSet().addCartagenId(); }
   */
  // pour le noveau chargeur
  public static void computeDataLoading(SourceDLM source, int scale)
      throws ShapefileException, IOException {
    if (EnrichFrameOld.getInstance().isResetSelected()) {
      CartAGenDocOld.getInstance().getCurrentDataset().resetDataSet();
    }
    // Modif Cecile: here, the system path (path where the shapefiled from which
    // the current dataset will be loaded) should be retrieved from the
    // ShapefileDB associated to the current dataset
    // OLd code:
    // String systemPath = CartagenApplication.getInstance().getCheminDonnees();
    // New code:
    CartAGenDocOld doc = CartAGenDocOld.getInstance();
    CartAGenDataSet curDS = doc.getCurrentDataset();
    ShapeFileDB curDB = (ShapeFileDB) curDS.getCartAGenDB();
    String systemPath = curDB.getSystemPath();
    // End modif Cecile
    // CartagenApplication.getInstance().loadData(source, scale);
    CartagenApplication.getInstance().loadData(systemPath, source, scale,
        CartagenApplication.getInstance().getDocument().getCurrentDataset());
    // load the extent of the zone from a shapefile if needed
    if (ImportDataFrame.extentFile) {
      String path = systemPath + "/" + ImportDataFrame.extentClass + ".shp";
			ShpFiles shpf = new ShpFiles(path);
      ShapefileReader shr = new ShapefileReader(shpf, true, false,
          new GeometryFactory());
      Record objet = shr.nextRecord();
      IPolygon geom = null;
      try {
        geom = (IPolygon) AdapterFactory.toGM_Object((Geometry) objet.shape());
      } catch (Exception e) {
        e.printStackTrace();
        return;
      }
      shr.close();
      CartAGenDocOld.getInstance().getZone().setExtent(geom);
    }
    // Modif Cecile: the systemPath of the ShapefileDB associated to the current
    // dataset being loaded should not be modified here
    // Old code:
    // ((ShapeFileDB) CartAGenDocOld.getInstance().getCurrentDataset()
    // .getCartAGenDB()).setSystemPath(LoadingFrame.cheminAbsolu);
    // End modif Cecile

    CartagenApplication.getInstance().initialiserPositionGeographique(
        !ImportDataFrame.extentFile);
    CartagenApplication.getInstance().enrichData(
        CartAGenDocOld.getInstance().getCurrentDataset());
    CartagenApplication.getInstance().initGeneralisation();
    // add generated cartagen ids in the shapefiles
    CartAGenDocOld.getInstance().getCurrentDataset().getCartAGenDB()
        .addCartagenId();

  }

}

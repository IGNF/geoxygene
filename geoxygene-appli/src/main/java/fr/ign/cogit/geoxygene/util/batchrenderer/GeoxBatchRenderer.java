package fr.ign.cogit.geoxygene.util.batchrenderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory.LayerType;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewAwtPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.Grid;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * Utility Class to grossly use geoxygene for batch rendering
 * it expects a data connector (postgis implementation provided)
 * referencing the features tables,
 * the SLD files and optionally some rasters
 * @author imran
 *
 */
public class GeoxBatchRenderer {

  private GeOxygeneApplication application;
  private String outputDir;
  private List<StyledLayerDescriptor> slds;
  private List<String> rasters;
  private DataConnector pgc;
  private Grid g;
  private IEnvelope currentTileEnv;
  private IEnvelope currentTileBuff;
  private int largeur;
  private int hauteur;
  private String prefix = "";
  private boolean debug = false;

  public GeoxBatchRenderer(DataConnector pgc, Grid g, String outputDir,
      List<String> slds, List<String> rasters, int largeur) {
    this.outputDir = outputDir;
    setSLDs(slds);
    application = new GeOxygeneApplication();
    if (!this.debug)
      application.getMainFrame().display(false);
    this.rasters = rasters;
    this.largeur = largeur;
    this.g = g;
    this.pgc = pgc;
  }

  public GeoxBatchRenderer(DataConnector pgc, Grid g, String outputDir,
      List<String> slds, List<String> rasters) {
    /* by default largeur is 1 pixel/m */
    this(pgc, g, outputDir, slds, rasters,
        (int) (g.xmax() - g.xmin()) / (g.nbCols()));
  }

  public void renderTiles(Grid g) {
    renderTiles(0, g.nbRows(), 0, g.nbCols());
  }

  public void renderTiles(int fromLine, int toLine, int fromCol,
      int toCol) {
    Map<String, String> extentsij = g.getIJextents(fromLine, toLine, fromCol,
        toCol);
    for (Map.Entry<String, String> eij : extentsij.entrySet()) {
      setEnvelope(eij.getValue());
      String filename = outputDir + "/" + prefix + eij.getKey() + ".png";
      System.out.println(eij.getKey());
      ProjectFrame pf = buildTile(filename);
      try {
        Thread.sleep(2000);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
      if (!this.debug)
        pf.dispose();
    }
  }

  public void renderExtent(String extent, String name) {
    setEnvelope(extent);
    String filename = outputDir + "/" + prefix + name + ".png";
    ProjectFrame pf = buildTile(filename);
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    if (!this.debug)
      pf.dispose();
  }

  private BufferedImage buildTileImage() {
    ProjectFrame pf = this.application.getMainFrame().getSelectedProjectFrame();
    LayerViewAwtPanel lvawt = (LayerViewAwtPanel) pf.getLayerViewPanel();
    Color bg = pf.getLayerViewPanel().getBackground();
    BufferedImage outImage = new BufferedImage(this.largeur, this.hauteur,
        BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = outImage.createGraphics();
    graphics.setColor(bg);
    graphics.fillRect(0, 0, largeur, hauteur);
    // lvawt.setSize(largeur, hauteur);
    addDataLayers();
    addRasters();
    LayerViewPanel lvp = pf.getLayerViewPanel();
    try {
      lvp.getViewport().zoom(this.currentTileBuff);
      lvp.repaint();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }

    System.out.println("finished rasters, now waiting 2 secs");
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    while (pf.getLayerViewPanel().getRenderingManager().isRendering()) {
      ; /* do nothing until the rendering is finished */
    }
    lvawt.getRenderingManager().copyTo(graphics);
    return outImage;
  }

  private ProjectFrame buildTile(String outputName) {
    ProjectFrame projectFrame = application.getMainFrame().newProjectFrame();
    // projectFrame.setSize(largeur, hauteur);
    // projectFrame.getLayerViewPanel().setSize(largeur, hauteur);
    addDataLayers();
    addRasters();
    // System.out.println("rasters finished, now waiting 2 secs");
    // try {
    // Thread.sleep(7000);
    // } catch (InterruptedException e1) {
    // e1.printStackTrace();
    // }
    // while
    // (projectFrame.getLayerViewPanel().getRenderingManager().isRendering()) {
    // System.out.println("waiting raster");
    // ; /* do nothing until the rendering is finished */
    // }
    LayerViewAwtPanel lvp = (LayerViewAwtPanel) projectFrame
        .getLayerViewPanel();
    // System.out.println("printing..." + lvp.getWidth() + " x " +
    // lvp.getHeight());
    // System.out.println("klodo..." + largeur + " x " + hauteur);
    // lvp.saveAsImage("/home/imran/makaaak3DT.png", 1240, 1333);
    // lvp.saveAsImage("/home/imran/makaaak3.png", 867, 652);
    saveImageas(outputName);
    return projectFrame;
  }

  private void addDataLayers() {
    ProjectFrame projectFrame = application.getMainFrame()
        .getSelectedProjectFrame();
    List<String> tables = pgc.getCouches();
    for (String table : tables) {
      System.out.println("************** Loading " + table);
      IPopulation<IFeature> pop = null;
      try {
        pop = pgc.getPopulation(table, this.currentTileEnv);
      } catch (Exception e) {
        e.printStackTrace();
      }
      projectFrame.addUserLayer(pop, table, null);
      System.out.println("************** Finished " + table + " : " + pop.size()
          + " entities");
    }
    for (StyledLayerDescriptor sldFile : slds) {
      projectFrame.loadSLD(sldFile, true);
      // while
      // (projectFrame.getLayerViewPanel().getRenderingManager().isRendering())
      // {
      // ; /* do nothing until the rendering is finished */
      // }
    }
    LayerViewPanel lvp = projectFrame.getLayerViewPanel();
    // try {
    // lvp.getViewport().zoom(this.currentTileEnv);
    // } catch (NoninvertibleTransformException e) {
    // e.printStackTrace();
    // }
    // while (lvp.getRenderingManager().isRendering()) {
    // ; /* do nothing until the rendering is finished */
    // }

  }

  public void setEnvelope(String emprise) {
    IGeometry ext = null;
    IGeometry extB = null;
    try {
      ILineString lineExt = (ILineString) WktGeOxygene.makeGeOxygene(emprise);
      ext = (new GM_Polygon(lineExt));// .buffer(largeur / BUFFERDIVIDE);
      extB = (new GM_Polygon(lineExt)).buffer(largeur / 3);
    } catch (ParseException e1) {
      e1.printStackTrace();
    }
    this.currentTileEnv = ext.envelope();
    this.currentTileBuff = extB.envelope();
    this.hauteur = getHauteur();
  }

  private void setSLDs(List<String> sldFiles) {
    for (String f : sldFiles) {
      this.slds = new ArrayList<>();
      File sldf = new File(f);
      StyledLayerDescriptor new_sld = null;
      try {
        new_sld = StyledLayerDescriptor.unmarshall(sldf.getAbsolutePath());
        this.slds.add(new_sld);
      } catch (FileNotFoundException | JAXBException e) {
        e.printStackTrace();
      }
    }
  }

  public void saveImageas(String outputName) {
    LayerViewAwtPanel lvp = (LayerViewAwtPanel) application.getMainFrame()
        .getSelectedProjectFrame().getLayerViewPanel();
    // application.getMainFrame().getSelectedProjectFrame().setSize(largeur,
    // hauteur);
    // try {
    // Thread.sleep(2000);
    // } catch (InterruptedException e1) {
    // e1.printStackTrace();
    // }

    // while (lvp.getRenderingManager().isRendering()) {
    // ; /* do nothing until the rendering is finished */
    // }
    try {
      lvp.getViewport().zoom(this.currentTileEnv);
      // lvp.repaint();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
    System.out.println("saving image...");
    lvp.saveAsImage(outputName, lvp.getWidth(), lvp.getHeight(), true, false);
    // lvp.saveAsImage(outputName + "g", largeur, hauteur, true, false);
    // lvp.saveAsImage(outputName, largeur, hauteur);
    System.out.println("Image size computed : " + largeur + " x " + hauteur);
    System.out.println("Image size from layerviexpanel :" + lvp.getWidth()
        + " x " + lvp.getHeight());
    System.out.println("Done ");
  }

  private void addRasters() {
    ProjectFrame projectFrame = application.getMainFrame()
        .getSelectedProjectFrame();
    LayerFactory factory = new LayerFactory(projectFrame.getSld());
    for (String raster : rasters) {
      // File r = new File(raster);
      Layer l = factory.createLayer(raster, LayerType.GEOTIFF);
      projectFrame.addLayer(l);
      try {
        projectFrame.getLayerViewPanel().getViewport()
            .zoom(this.currentTileEnv);
      } catch (NoninvertibleTransformException e) {
        e.printStackTrace();
      }
      // projectFrame.addLayer(r);
      // while
      // (projectFrame.getLayerViewPanel().getRenderingManager().isRendering())
      // {
      // ; /* do nothing until the rendering is finished */
      // }
    }
  }

  private int getHauteur() {
    double w = this.currentTileEnv.width();
    double l = this.currentTileEnv.length();
    double r = w / l;
    return (int) (largeur * r);
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public void disposeGeoxApp() {
    this.application.exit();
    this.application = null;
  }

  public void respawn() {
    application = new GeOxygeneApplication();
    application.getMainFrame().display(false);
  }

  public void setDebug(boolean flag) {
    this.debug = flag;
  }

  public double getMetersPerPixel() {
    return this.application.getMainFrame().getSelectedProjectFrame()
        .getLayerViewPanel().getMETERS_PER_PIXEL();
  }

  public static void main(String[] args) {
    List<String> couches = new ArrayList<>();
    /*
     * marion
     */
    // couches.add("vegetation_25");
    // couches.add("nature_du_sol_25");
    // couches.add("surface_d_eau_25");
    // couches.add("troncon_cours_d_eau_25");
    // couches.add("cimetiere_25");
    // couches.add("terrain_de_sport_25");
    // // couches.add("batiment_25_all_clean");
    // // couches.add("batiment_fonctionnel_25");
    // // couches.add("batiment_quelconque_25");
    // // couches.add("batiment_special_surf_25");
    // // couches.add("batiment_religieux_surf_25");
    // couches.add("limite_administrative_25");
    // // couches.add("troncon_route_25_clean");
    // couches.add("troncon_route_25");
    // couches.add("pont_route_25");
    // couches.add("troncon_chemin_25");
    // couches.add("troncon_voie_ferree_25");
    /*
     * marion
     */

    couches.add("nature_du_sol_25");
    couches.add("surface_d_eau_25");
    couches.add("vegetation_25");
    couches.add("courbe_de_niveau_25");
    couches.add("troncon_cours_d_eau_25");
    couches.add("batiment_fonctionnel_25");
    couches.add("batiment_quelconque_25");
    couches.add("batiment_special_surf_25");
    couches.add("batiment_religieux_surf_25");
    // couches.add("reservoir_surf_25");
    couches.add("limite_administrative_25");
    couches.add("troncon_voie_ferree_25");
    couches.add("troncon_route_25");
    couches.add("tunnel_route_25");
    couches.add("troncon_chemin_25");
    couches.add("amenagement_comm_25");
    // couches.add("construction_surfacique_25");
    couches.add("liaison_maritime_25");
    couches.add("limite_reglementee_25");
    couches.add("pont_route_25");
    couches.add("tunnel_hydro_25");
    couches.add("pont_ferre_25");
    couches.add("tunnel_ferre_25");
    couches.add("tunnel_chemin_25");
    couches.add("pont_chemin_25");
    couches.add("construction_lineaire_25");
    couches.add("haie_rangee_d_arbres_25");
    couches.add("ligne_electrique_25");
    couches.add("itineraire_touristique_25");
    couches.add("batiment_religieux_ponct_25");
    couches.add("batiment_special_ponct_25");
    couches.add("construction_ponctuelle_25");
    couches.add("gue_chemin_25");
    couches.add("gue_route_25");
    // couches.add("cimetiere_25");
    couches.add("terrain_de_sport_25");
    couches.add("reservoir_ponct_25");
    couches.add("pylone_25");
    couches.add("poste_transformation_25");
    couches.add("point_topo_25");
    couches.add("point_d_eau_25");
    // couches.add("aerodrome_25");
    couches.add("transport_par_cable_25");
    couches.add("point_remarquable_25");
    couches.add("detail_touristique_25");
    couches.add("activite_sportive_25");
    couches.add("toponyme_droit_25");
    couches.add("toponyme_courbe_25");

    List<String> rasters = new ArrayList<>();
    //rasters.add("/home/imran/jeanluz/rasters/27_84.tif");
    rasters.add("/home/imran/jeanluz/rasters/27_86.tif");
    //rasters.add("/home/imran/jeanluz/rasters/25_84.tif");
    //rasters.add("/home/imran/jeanluz/rasters/25_86.tif");

    List<String> slds = new ArrayList<>();
    slds.add("/home/imran/jeanluz/ign_standart/big_one_2.xml");
    // slds.add("/home/imran/jeanluz/SE_standard_25K_MD_pg.xml");
    // slds.add("/home/imran/Téléchargements/ign_standart/toponyme_droit_25_2.xml");

    /** St Jean de Luz **/
    double decal = 0;
    double xmin = 310900 - decal;
    double xmax = 340000 ;
    double ymin = 6249500 + decal;
    double ymax = 6280500;
    Map<String, String> params = new HashMap<String, String>();
    params.put("dbtype", "postgis");
    params.put("host", "localhost");
    params.put("port", "5432");
    params.put("database", "carnacLuz");
    params.put("schema", "public");
    params.put("user", "imrandb");
    params.put("passwd", "imrandb");

    int nbLignes = 50;
    int nbCols = 50;
    IDirectPosition dmin = new DirectPosition(xmin, ymin);
    IDirectPosition dmax = new DirectPosition(xmax, ymax);
    //String outputDir = "/home/imran/jeanluz/outputMarion";
    String outputDir = "/home/imran/jeanluz/output_klo";
    int largeur = (int) ((xmax - xmin) / (nbCols));

    Grid g = new Grid(nbLignes, nbCols, dmin, dmax);
    // g.toShapeFile("/home/imran/jeanluz/grids/st_jean_20x20.shp","EPSG:2154");
    DataConnector pg = new PostgisConnector(params, couches);
    // System.out.println(pg.getCouches().get(5));
    // GeoxBatchRenderer bg = new GeoxBatchRenderer(pg, g, outputDir, slds,
    // rasters, largeur);
    GeoxBatchRenderer bg = new GeoxBatchRenderer(pg, g, outputDir, slds,
        rasters);
    bg.setPrefix("st_jean_rem_");
    // 22-29 done
    // for (int k = 35; k < 40; k = k + pas) {
    // bg.renderPostGisTiles(40, 50, k, k + pas);
    // bg.disposeGeoxApp();
    // try {
    // Thread.sleep(2000);
    // } catch (InterruptedException e1) {
    // e1.printStackTrace();
    // }
    // bg.respawn();
    // }
    bg.renderTiles(16, 17, 34, 35);
    // bg.renderPostGisTiles(19, 20, 32, 33);
    // bg.renderPostGisTiles(49, 50, 40, 41);
    // System.out.println(g.getTile(12, 14));
    // bg.renderPostGisTiles(12, 13, 14, 15);
    // bg.renderPostGisTiles(12, 16, 14, 17);
    // String extentTest = "LINESTRING(330446.91602844640146941
    // 6268946.43736323807388544, 331901.91602844640146941
    // 6268946.43736323807388544, 331901.91602844640146941
    // 6270496.43736323807388544, 330446.91602844640146941
    // 6270496.43736323807388544, 330446.91602844640146941
    // 6268946.43736323807388544)";
    // bg.renderExtent(extentTest, "recon2");
    // bg.renderPostGisTiles(13, 14, 10, 11);
    // System.out.println(bg.getMetersPerPixel());
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e1) {
      e1.printStackTrace();
    }
    // bg.saveImageas("/home/imran/test.png");
    bg.disposeGeoxApp();

    System.out.println("*************** Done");
  }
}

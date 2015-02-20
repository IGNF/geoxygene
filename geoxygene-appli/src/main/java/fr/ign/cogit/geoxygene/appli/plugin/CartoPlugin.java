/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.media.jai.TiledImage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.ImageFileFilter;
import fr.ign.cogit.cartagen.spatialanalysis.tessellations.gridtess.GridTessellation;
import fr.ign.cogit.carto.evaluation.clutter.MapLegibilityMethod;
import fr.ign.cogit.carto.evaluation.clutter.RasterClutterMethod;
import fr.ign.cogit.carto.evaluation.clutter.quadtree.FeuilleQuadtree;
import fr.ign.cogit.carto.evaluation.clutter.quadtree.QuadtreeClutterMethod;
import fr.ign.cogit.carto.evaluation.clutter.subbandentropy.SubbandClutter;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerFactory;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.render.MultithreadedRenderingManager;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.UserStyle;

public class CartoPlugin implements GeOxygeneApplicationPlugin {

  private GeOxygeneApplication application = null;

  public CartoPlugin() {
  }

  @Override
  public void initialize(GeOxygeneApplication application) {
    this.application = application;
    JMenu menu = new JMenu("Cartography");
    JMenu clutterMenu = new JMenu("Clutter");
    JMenu textMenu = new JMenu("Text Placement");
    clutterMenu.add(new JMenuItem(new SubbandClutterAction()));
    clutterMenu.addSeparator();
    clutterMenu.add(new JMenuItem(new EdgeDensityClutterAction()));
    clutterMenu.add(new JMenuItem(new EdgeDensityClutterFileAction()));
    clutterMenu.addSeparator();
    clutterMenu.add(new JMenuItem(new QuadtreeClutterAction()));
    clutterMenu.add(new JMenuItem(new QuadtreeExportAction()));
    clutterMenu.addSeparator();
    clutterMenu.add(new JMenuItem(new OlssonMapLegibilityAction()));
    menu.add(clutterMenu);
    menu.add(textMenu);
    application.getMainFrame().getMenuBar()
        .add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);

  }

  class SubbandClutterAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // On choisit le fichier
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new ImageFileFilter());
      int returnVal = fc.showDialog(null, "Choose the image to assess");
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }

      // path est le chemin jusqu'au fichier
      File path = fc.getSelectedFile();
      // On fait une instance de buffered image pour pouvoir y recopier l'image
      // choisie
      BufferedImage image;

      try {
        // On lit l'image à partir du fichier
        image = ImageIO.read(path);

        SubbandClutter main = new SubbandClutter();
        main.appelParCanal(image);

      } catch (IOException e1) {
        e1.printStackTrace();
        System.out.println(path);
      }

    }

    public SubbandClutterAction() {
      super();
      this.putValue(
          Action.SHORT_DESCRIPTION,
          "Compute an the Subband Entropy Clutter global measure (Rosenholtz et al 2007) of the given file image");
      this.putValue(Action.NAME, "Compute Subband Entropy Clutter of the file");
    }
  }

  class EdgeDensityClutterAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // get the map as an image
      GeOxygeneApplication application = CartAGenPlugin.getInstance()
          .getApplication();
      LayerViewPanel panel = application.getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel();
      Color bg = panel.getBackground();
      BufferedImage image = new BufferedImage(panel.getWidth(),
          panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
      Graphics2D graphics = image.createGraphics();
      graphics.setColor(bg);
      graphics.fillRect(0, 0, panel.getWidth(), panel.getHeight());
      ((MultithreadedRenderingManager) panel.getRenderingManager())
          .copyTo(graphics);
      panel.paintOverlays(graphics);
      graphics.dispose();

      TiledImage pImage = new TiledImage(image, true);
      RasterClutterMethod clutterMethod = new RasterClutterMethod(pImage);
      System.out.println(clutterMethod.getEdgeDensityClutter());
    }

    public EdgeDensityClutterAction() {
      super();
      this.putValue(
          Action.SHORT_DESCRIPTION,
          "Compute an Edge Density Clutter global measure of the current displayed map as an image");
      this.putValue(Action.NAME, "Compute Edge Density Clutter of the map");
    }
  }

  class EdgeDensityClutterFileAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // get the filename
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new ImageFileFilter());
      int returnVal = fc.showDialog(null, "Choose the image to assess");
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File path = fc.getSelectedFile();
      BufferedImage image;
      try {
        image = ImageIO.read(path);
        TiledImage pImage = new TiledImage(image, true);
        RasterClutterMethod clutterMethod = new RasterClutterMethod(pImage);
        System.out.println(clutterMethod.getEdgeDensityClutter());
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    public EdgeDensityClutterFileAction() {
      super();
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compute an Edge Density Clutter global measure of the given file image");
      this.putValue(Action.NAME, "Compute Edge Density Clutter of the file");
    }
  }

  class OlssonMapLegibilityAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      // get the map as an image
      ProjectFrame pFrame = application.getMainFrame()
          .getSelectedProjectFrame();
      LayerViewPanel panel = pFrame.getLayerViewPanel();

      MapLegibilityMethod method = new MapLegibilityMethod(pFrame.getSld(),
          panel.getViewport().getEnvelopeInModelCoordinates());

      GridTessellation<Boolean> grid = method
          .getOlssonThresholdLegibility(50.0);

      // Layer layer = new NamedLayer(pFrame.getSld(), "legibilityGrid");
      Layer layer = pFrame.getSld().createLayer("legibilityGrid",
          IPolygon.class, Color.RED);
      UserStyle style = new UserStyle();
      //style.setName("Style créé pour le layer legibilityGrid");//$NON-NLS-1$
      FeatureTypeStyle fts = new FeatureTypeStyle();
      Rule rule = LayerFactory.createRule(IPolygon.class, Color.RED.darker(),
          Color.RED, 0.8f, 0.8f, 1.0f);
      rule.setTitle("grid display false");
      Filter filter = new PropertyIsEqualTo(new PropertyName("value"),
          new Literal("false"));
      rule.setFilter(filter);
      fts.getRules().add(rule);
      Rule rule2 = LayerFactory.createRule(IPolygon.class,
          Color.GREEN.darker(), Color.GREEN, 0.8f, 0.8f, 1.0f);
      rule2.setTitle("grid display true");
      Filter filter2 = new PropertyIsEqualTo(new PropertyName("value"),
          new Literal("true"));
      rule2.setFilter(filter2);
      fts.getRules().add(rule2);
      style.getFeatureTypeStyles().add(fts);
      layer.getStyles().add(style);
      IPopulation<IFeature> pop = new Population<>("legibilityGrid");
      pop.addAll(grid.getCells());
      pFrame.getSld().getDataSet().addPopulation(pop);
      pFrame.getSld().add(layer);

    }

    public OlssonMapLegibilityAction() {
      super();
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compute the grid method for map legibility from Olsson et al 2011");
      this.putValue(Action.NAME, "Compute Olsson Grid Map Legibility");
    }
  }

  class QuadtreeClutterAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
      // On choisit le fichier
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new ImageFileFilter());
      int returnVal = fc.showDialog(null, "Choose the image to assess");
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }

      // path est le chemin jusqu'au fichier
      File path = fc.getSelectedFile();
      // On fait une instance de buffered image pour pouvoir y recopier l'image
      // choisie
      BufferedImage image;
      try {
        // On lit l'image et on rend l'écriture possible dessus
        image = ImageIO.read(path);
        // On transforme l'image en noir et blanc -> image bi
        QuadtreeClutterMethod qcluttermethod = new QuadtreeClutterMethod(image);
        BufferedImage bi = qcluttermethod.toGray();

        // On traite l'image noir et blanc avec la méthode des Quadtree
        FeuilleQuadtree feuille = new FeuilleQuadtree(0, 0, bi.getWidth(),
            bi.getHeight(), bi, null);
        int toto = 1;
        int clutter;
        Stack<FeuilleQuadtree> pile = new Stack<>();
        ArrayList<FeuilleQuadtree> liste = new ArrayList<>();
        liste.add(feuille);
        pile.add(feuille);
        qcluttermethod.computeQuadTree(pile, liste);
        for (FeuilleQuadtree f : liste) {
          if (!f.hasChild()) {
            toto = toto + 1;
          }
        }
        clutter = toto - 1;
        System.out.println("QuadTree clutter = " + clutter);
      } catch (IOException e1) {
        e1.printStackTrace();
      }

    }

    public QuadtreeClutterAction() {
      super();
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compute a quadtree Clutter local measure of the given file image");
      this.putValue(Action.NAME, "Compute quadtree Clutter of the file");
    }

  }

  class QuadtreeExportAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    public void actionPerformed(ActionEvent e) {
      // On choisit le fichier
      JFileChooser fc = new JFileChooser();
      fc.setFileFilter(new ImageFileFilter());
      int returnVal = fc.showDialog(null, "Choose the image to assess");
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }

      // path est le chemin jusqu'au fichier
      File path = fc.getSelectedFile();

      String result = JOptionPane.showInputDialog(
          "Grayscale difference threshold", "5");
      int threshold = Integer.parseInt(result);

      // On fait une instance de buffered image pour pouvoir y recopier l'image
      // choisie
      BufferedImage image;
      try {
        // On lit l'image et on rend l'écriture possible dessus
        image = ImageIO.read(path);
        // On transforme l'image en noir et blanc -> image bi
        QuadtreeClutterMethod qcluttermethod = new QuadtreeClutterMethod(image,
            threshold);
        BufferedImage bi = qcluttermethod.toGray();

        // On traite l'image noir et blanc avec la méthode des Quadtree
        FeuilleQuadtree feuille = new FeuilleQuadtree(0, 0, bi.getWidth(),
            bi.getHeight(), bi, null);
        int clutter = 1;
        Stack<FeuilleQuadtree> pile = new Stack<>();
        ArrayList<FeuilleQuadtree> liste = new ArrayList<>();
        liste.add(feuille);
        pile.add(feuille);
        qcluttermethod.computeQuadTree(pile, liste);
        Graphics2D g2d = image.createGraphics();
        for (FeuilleQuadtree f : liste) {
          if (!f.hasChild()) {
            clutter++;
            g2d.setColor(Color.RED);
            g2d.drawRect(f.column1, f.line1, f.largeur, f.longueur);
          }
        }
        clutter--;
        System.out.println("QuadTree clutter = " + clutter);

        // On écrit l'image
        System.out.println(path.getParent());
        File outputfile = new File(path.getParent() + "\\"
            + path.getName().substring(0, path.getName().length() - 4)
            + "_quadtree.png");
        ImageIO.write(image, "png", outputfile);
      } catch (IOException e1) {
        e1.printStackTrace();
      }

    }

    public QuadtreeExportAction() {
      super();
      this.putValue(
          Action.SHORT_DESCRIPTION,
          "Compute a quadtree Clutter local measure of the given file image and export result as a file with the quadtree displayed");
      this.putValue(Action.NAME, "Compute quadtree Clutter and export result");
    }

  }
}

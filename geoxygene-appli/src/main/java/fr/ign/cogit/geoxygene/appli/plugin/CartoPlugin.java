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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;
import javax.media.jai.KernelJAI;
import javax.media.jai.RenderedOp;
import javax.media.jai.TiledImage;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
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
    clutterMenu.add(new JMenuItem(new SubbandClutterWindowAction()));
    clutterMenu.addSeparator();
    clutterMenu.add(new JMenuItem(new EdgeDensityClutterAction()));
    clutterMenu.add(new JMenuItem(new EdgeDensityClutterFileAction()));
    clutterMenu.add(new JMenuItem(new EdgeDensityExportFileAction()));
    clutterMenu.addSeparator();
    clutterMenu.add(new JMenuItem(new QuadtreeClutterAction()));
    clutterMenu.add(new JMenuItem(new QuadtreeExportAction()));
    clutterMenu.addSeparator();
    clutterMenu.add(new JMenuItem(new BravoFaridClutterFileAction()));
    clutterMenu.add(new JMenuItem(new BravoFaridExportFileAction()));
    clutterMenu.addSeparator();
    clutterMenu.add(new JMenuItem(new OlssonMapLegibilityAction()));
    clutterMenu.add(new JMenuItem(new OlssonForegroundAction()));
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
        System.out.println(main.appelParCanal(image));

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

  class SubbandClutterWindowAction extends AbstractAction {

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

      SubbandClutter main = new SubbandClutter();
      System.out.println(main.appelParCanal(image));

    }

    public SubbandClutterWindowAction() {
      super();
      this.putValue(
          Action.SHORT_DESCRIPTION,
          "Compute an the Subband Entropy Clutter global measure (Rosenholtz et al 2007) of the window map");
      this.putValue(Action.NAME,
          "Compute Subband Entropy Clutter of the window map");
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

  class EdgeDensityExportFileAction extends AbstractAction {

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

        // export image
        KernelJAI sobelVertKernel = KernelJAI.GRADIENT_MASK_SOBEL_VERTICAL;
        KernelJAI sobelHorizKernel = KernelJAI.GRADIENT_MASK_SOBEL_HORIZONTAL;
        ParameterBlock pb = new ParameterBlock();
        pb.addSource(image);
        pb.add(sobelHorizKernel);
        pb.add(sobelVertKernel);
        RenderedOp renderedOp = JAI.create("gradientmagnitude", pb);
        BufferedImage edgeImage = renderedOp.getAsBufferedImage();
        File outputfile = new File(path.getParent() + "\\"
            + path.getName().substring(0, path.getName().length() - 4)
            + "_edge.png");
        ImageIO.write(edgeImage, "png", outputfile);
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    public EdgeDensityExportFileAction() {
      super();
      this.putValue(
          Action.SHORT_DESCRIPTION,
          "Compute an Edge Density Clutter global measure of the given file image and export the edge image");
      this.putValue(Action.NAME,
          "Compute and Export Edge Density Clutter of the file");
    }
  }

  class OlssonMapLegibilityAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {

      computeOlssonMapLegibility(new HashSet<String>());
    }

    public OlssonMapLegibilityAction() {
      super();
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compute the grid method for map legibility from Olsson et al 2011");
      this.putValue(Action.NAME, "Compute Olsson Grid Map Legibility");
    }
  }

  class OlssonForegroundAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      OlssonForegroundFrame frame = new OlssonForegroundFrame();
      frame.setVisible(true);
    }

    public OlssonForegroundAction() {
      super();
      this.putValue(
          Action.SHORT_DESCRIPTION,
          "Compute the grid method for map legibility from Olsson et al 2011, with a selection of foreground layers");
      this.putValue(Action.NAME,
          "Compute Olsson Grid Map Legibility with foreground layers");
    }
  }

  class OlssonForegroundFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private List<String> foreground = new ArrayList<>();
    private JList<String> jlist;
    private JComboBox<String> comboLayers;

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("ok")) {
        computeOlssonMapLegibility(foreground);
        this.dispose();
      } else if (e.getActionCommand().equals("add")) {
        // add the layer selected in the combo box in the list
        String selected = comboLayers.getItemAt(comboLayers.getSelectedIndex());
        if (selected != null && !selected.equals("")) {
          foreground.add(selected);
          updateList();
        }
      } else if (e.getActionCommand().equals("remove")) {
        // remove the selected layer from the foreground list
        if (!jlist.isSelectionEmpty()) {
          foreground.remove(jlist.getSelectedValue());
          updateList();
        }
      } else
        this.dispose();
    }

    private void updateList() {
      DefaultListModel<String> model = new DefaultListModel<>();
      for (String layer : foreground) {
        model.addElement(layer);
      }
      jlist.setModel(model);
      pack();
    }

    OlssonForegroundFrame() {
      super("Select Foreground Layers");
      this.setSize(600, 400);
      this.setPreferredSize(new Dimension(600, 400));
      ProjectFrame pFrame = application.getMainFrame()
          .getSelectedProjectFrame();

      JPanel mainPanel = new JPanel();
      comboLayers = new JComboBox<>();
      DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
      for (Layer layer : pFrame.getSld().getLayers())
        model.addElement(layer.getName());
      comboLayers.setModel(model);
      comboLayers.setPreferredSize(new Dimension(150, 20));
      comboLayers.setMaximumSize(new Dimension(150, 20));
      comboLayers.setMinimumSize(new Dimension(150, 20));
      JPanel addPanel = new JPanel();
      JButton addBtn = new JButton("->");
      addBtn.addActionListener(this);
      addBtn.setActionCommand("add");
      JButton removeBtn = new JButton("<-");
      removeBtn.addActionListener(this);
      removeBtn.setActionCommand("remove");
      addPanel.add(addBtn);
      addPanel.add(removeBtn);
      addPanel.setLayout(new BoxLayout(addPanel, BoxLayout.Y_AXIS));
      jlist = new JList<String>();
      jlist.setPreferredSize(new Dimension(150, 400));
      jlist.setMaximumSize(new Dimension(150, 400));
      jlist.setMinimumSize(new Dimension(150, 400));
      mainPanel.add(comboLayers);
      mainPanel.add(Box.createHorizontalGlue());
      mainPanel.add(addPanel);
      mainPanel.add(Box.createHorizontalGlue());
      mainPanel.add(new JScrollPane(jlist));
      mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

      // a panel for OK and Cancel buttons
      JPanel pBoutons = new JPanel();
      JButton btnFermer = new JButton(I18N.getString("MainLabels.lblCancel"));
      btnFermer.addActionListener(this);
      btnFermer.setActionCommand("cancel");
      btnFermer.setPreferredSize(new Dimension(100, 50));
      JButton btnCharger = new JButton("OK");
      btnCharger.addActionListener(this);
      btnCharger.setActionCommand("ok");
      btnCharger.setPreferredSize(new Dimension(100, 50));
      pBoutons.add(btnCharger);
      pBoutons.add(btnFermer);
      pBoutons.setLayout(new BoxLayout(pBoutons, BoxLayout.X_AXIS));

      // *********************************
      this.getContentPane().add(mainPanel);
      this.getContentPane().add(pBoutons);
      this.getContentPane().setLayout(
          new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
      this.setAlwaysOnTop(true);
    }
  }

  private void computeOlssonMapLegibility(Collection<String> foregroundLayers) {
    // get the map as an image
    ProjectFrame pFrame = application.getMainFrame().getSelectedProjectFrame();
    LayerViewPanel panel = pFrame.getLayerViewPanel();

    MapLegibilityMethod method = new MapLegibilityMethod(pFrame.getSld(), panel
        .getViewport().getEnvelopeInModelCoordinates());
    for (String layerName : foregroundLayers)
      method.addForegroundLayer(layerName);
    GridTessellation<Boolean> grid = method.getOlssonThresholdLegibility(50.0);

    // Layer layer = new NamedLayer(pFrame.getSld(), "legibilityGrid");
    Layer layer = pFrame.getSld().createLayer("legibilityGrid", IPolygon.class,
        Color.RED);
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
    Rule rule2 = LayerFactory.createRule(IPolygon.class, Color.GREEN.darker(),
        Color.GREEN, 0.8f, 0.5f, 1.0f);
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
        double normalized = ((double) clutter)
            / (image.getHeight() * image.getWidth());
        System.out.println("QuadTree clutter = " + clutter);
        System.out.println("QuadTree normalized clutter = " + normalized);

        // clutter avec un seuil de 50
        qcluttermethod = new QuadtreeClutterMethod(image);
        qcluttermethod.setSeuil(50);
        // On traite l'image noir et blanc avec la méthode des Quadtree
        feuille = new FeuilleQuadtree(0, 0, bi.getWidth(), bi.getHeight(), bi,
            null);
        toto = 1;
        clutter = 0;
        pile = new Stack<>();
        liste = new ArrayList<>();
        liste.add(feuille);
        pile.add(feuille);
        qcluttermethod.computeQuadTree(pile, liste);
        for (FeuilleQuadtree f : liste) {
          if (!f.hasChild()) {
            toto = toto + 1;
          }
        }
        clutter = toto - 1;
        normalized = ((double) clutter)
            / (image.getHeight() * image.getWidth());
        System.out.println("QuadTree clutter (50) = " + clutter);
        System.out.println("QuadTree normalized clutter (50) = " + normalized);

        // clutter avec un seuil de 100
        qcluttermethod = new QuadtreeClutterMethod(image);
        qcluttermethod.setSeuil(100);
        // On traite l'image noir et blanc avec la méthode des Quadtree
        feuille = new FeuilleQuadtree(0, 0, bi.getWidth(), bi.getHeight(), bi,
            null);
        toto = 1;
        clutter = 0;
        pile = new Stack<>();
        liste = new ArrayList<>();
        liste.add(feuille);
        pile.add(feuille);
        qcluttermethod.computeQuadTree(pile, liste);
        for (FeuilleQuadtree f : liste) {
          if (!f.hasChild()) {
            toto = toto + 1;
          }
        }
        clutter = toto - 1;
        normalized = ((double) clutter)
            / (image.getHeight() * image.getWidth());
        System.out.println("QuadTree clutter (100) = " + clutter);
        System.out.println("QuadTree normalized clutter (100) = " + normalized);
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

  class BravoFaridClutterFileAction extends AbstractAction {

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
        int minSize = Math.round(image.getHeight() / 100);
        System.out.println(path.getName()
            + ": "
            + clutterMethod.getBravoFaridClutter((float) 5.0, minSize,
                (float) 0.5));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    public BravoFaridClutterFileAction() {
      super();
      this.putValue(Action.SHORT_DESCRIPTION,
          "Compute a Bravo & Farid Clutter global measure of the given file image");
      this.putValue(Action.NAME, "Compute Bravo & Farid Clutter of the file");
    }
  }

  class BravoFaridExportFileAction extends AbstractAction {

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
      File outputfile = new File(path.getParent() + "\\"
          + path.getName().substring(0, path.getName().length() - 4)
          + "_segmented.png");
      try {
        image = ImageIO.read(path);
        TiledImage pImage = new TiledImage(image, true);
        int minSize = Math.round(image.getHeight() / 100);
        RasterClutterMethod clutterMethod = new RasterClutterMethod(pImage);
        System.out.println(clutterMethod.getAndExportBravoFaridClutter(
            (float) 5.0, minSize, (float) 0.5, outputfile));
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }

    public BravoFaridExportFileAction() {
      super();
      this.putValue(
          Action.SHORT_DESCRIPTION,
          "Compute a Bravo & Farid Clutter global measure of the given file image and export segmentation");
      this.putValue(Action.NAME,
          "Compute and Export Bravo & Farid Clutter of the file");
    }
  }

}

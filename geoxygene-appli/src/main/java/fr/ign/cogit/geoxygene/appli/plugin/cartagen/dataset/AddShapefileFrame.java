package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.xml.bind.JAXBException;

import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuildPoint;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.DigitalLandscapeModel;
import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileClass;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileDB;
import fr.ign.cogit.cartagen.software.dataset.ShapeFileLoader;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolGroup;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolList;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.SymbolsUtil;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;

public class AddShapefileFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;

  private JTabbedPane tabs;

  public AddShapefileFrame() {
    super();
    this.setTitle(I18N.getString("AddShapefileFrame.title"));
    this.setSize(400, 400);
    this.setPreferredSize(new Dimension(400, 400));

    // a tabpane with a tab by layer type
    tabs = new JTabbedPane();
    ShapeFileToLayerPanel buildingsTab = new ShapeFileToLayerPanel(
        CartAGenDataSet.BUILDINGS_POP, new String[] { "Nature" });
    tabs.addTab(buildingsTab.getLayerName(), buildingsTab);
    ShapeFileToLayerPanel railsTab = new ShapeFileToLayerPanel(
        CartAGenDataSet.RAILWAY_LINES_POP, new String[] { "Sidetrack" });
    tabs.addTab(railsTab.getLayerName(), railsTab);
    ShapeFileToLayerPanel waterLineTab = new ShapeFileToLayerPanel(
        CartAGenDataSet.WATER_LINES_POP, null);
    tabs.addTab(waterLineTab.getLayerName(), waterLineTab);
    ShapeFileToLayerPanel buildingPtTab = new ShapeFileToLayerPanel(
        CartAGenDataSet.BUILD_PT_POP, null);
    tabs.addTab(buildingPtTab.getLayerName(), buildingPtTab);
    tabs.setSelectedIndex(0);

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
    this.getContentPane().add(tabs);
    this.getContentPane().add(pBoutons);
    this.getContentPane()
        .setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
    this.setAlwaysOnTop(true);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("cancel")) {
      this.dispose();
    } else if (e.getActionCommand().equals("ok")) {
      // first create a new document if there is none
      CartAGenDoc doc = CartAGenDoc.getInstance();
      if (doc.getName() == null) {
        doc.setName("default");
        // then, create a default empty database
        ShapeFileDB database = new ShapeFileDB("default");
        database.setSourceDLM(SourceDLM.SPECIAL_CARTAGEN);
        database.setSymboScale(25000);
        database
            .setGeneObjImpl(GeneObjImplementation.getDefaultImplementation());
        CartAGenDataSet dataset = new CartAGenDataSet();
        doc.addDatabase("default", database);
        database.setDataSet(dataset);
        database.setType(new DigitalLandscapeModel());

        SymbolGroup symbGroup = SymbolsUtil
            .getSymbolGroup(SourceDLM.SPECIAL_CARTAGEN, 25000);
        dataset.setSymbols(SymbolList.getSymbolList(symbGroup));
      }

      // then get the current panel
      ShapeFileToLayerPanel panel = (ShapeFileToLayerPanel) tabs
          .getSelectedComponent();
      CartAGenDB currentDb = doc.getCurrentDataset().getCartAGenDB();
      if (panel.getLayerName().equals(CartAGenDataSet.RAILWAY_LINES_POP)) {
        // load railway lines from the shapefile
        SymbolList symbols = SymbolList
            .getSymbolList(SymbolsUtil.getSymbolGroup(currentDb.getSourceDLM(),
                currentDb.getSymboScale()));
        currentDb
            .addClass(new ShapeFileClass(currentDb, panel.getFile().getParent(),
                IRailwayLine.FEAT_TYPE_NAME, ILineString.class));
        try {
          ShapeFileLoader.loadRailwayLineFromSHP(panel.getFile().getPath(),
              symbols, panel.getAttributeMapping().get("Sidetrack"),
              currentDb.getDataSet());
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      } else if (panel.getLayerName().equals(CartAGenDataSet.BUILDINGS_POP)) {
        currentDb
            .addClass(new ShapeFileClass(currentDb, panel.getFile().getParent(),
                IBuilding.FEAT_TYPE_NAME, IPolygon.class));
        try {
          ShapeFileLoader.loadBuildingsFromSHP(panel.getFile().getPath(),
              currentDb.getDataSet(),
              panel.getAttributeMapping().get("Nature"));
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      } else if (panel.getLayerName().equals(CartAGenDataSet.BUILD_PT_POP)) {
        currentDb
            .addClass(new ShapeFileClass(currentDb, panel.getFile().getParent(),
                IBuildPoint.FEAT_TYPE_NAME, IPoint.class));
        try {
          ShapeFileLoader.loadBuildingPointsFromSHP(panel.getFile().getPath(),
              currentDb.getDataSet());
        } catch (IOException e1) {
          e1.printStackTrace();
        }
      }

      // on crée la projectFrame associée à la BD
      try {
        CartAGenPlugin.getInstance().addDatabaseToFrame(currentDb);
      } catch (JAXBException e1) {
        e1.printStackTrace();
      }

      this.dispose();
    }
  }

}

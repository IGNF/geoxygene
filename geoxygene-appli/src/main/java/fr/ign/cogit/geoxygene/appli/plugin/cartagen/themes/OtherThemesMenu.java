/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.core.genericschema.airport.IAirportArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.IRunwayLine;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayArea;
import fr.ign.cogit.cartagen.core.genericschema.airport.ITaxiwayLine;
import fr.ign.cogit.cartagen.core.genericschema.network.INetwork;
import fr.ign.cogit.cartagen.core.genericschema.network.INetworkSection;
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.genealgorithms.facilities.AirportTypification;
import fr.ign.cogit.cartagen.genealgorithms.facilities.AirportTypification.TaxiwayBranching;
import fr.ign.cogit.cartagen.genealgorithms.facilities.AirportTypification.TaxiwayBranchingCouple;
import fr.ign.cogit.cartagen.genealgorithms.facilities.AirportTypification.TaxiwayBranchingGroup;
import fr.ign.cogit.cartagen.genealgorithms.rail.CollapseParallelRailways;
import fr.ign.cogit.cartagen.genealgorithms.rail.TypifySideTracks;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeometryPool;
import fr.ign.cogit.cartagen.spatialanalysis.network.NetworkEnrichment;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.cartagen.spatialanalysis.network.railways.ParallelRailsGroup;
import fr.ign.cogit.cartagen.spatialanalysis.network.railways.ParallelStroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.railways.ParallelismEndingType;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.NoeudReseau;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineSegment;
import fr.ign.cogit.geoxygene.spatialrelation.properties.ConvergingPoint;
import fr.ign.cogit.geoxygene.spatialrelation.properties.ParallelSection;
import fr.ign.cogit.geoxygene.spatialrelation.relation.PartialParallelism2Lines;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;

public class OtherThemesMenu extends JMenu {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @SuppressWarnings("unused")
  private static Logger logger = Logger.getLogger(OtherThemesMenu.class
      .getName());

  public OtherThemesMenu(String title) {
    super(title);
    JMenu airportMenu = new JMenu("Airports");
    this.add(airportMenu);
    airportMenu.add(new JMenuItem(new SelectAction("airport")));
    airportMenu.add(new JMenuItem(new BuildAirportsAction()));
    airportMenu.add(new JMenuItem(new GeneraliseAirportsAction()));
    airportMenu.addSeparator();
    airportMenu.add(new JMenuItem(new TypifyTaxiwaysAction()));
    airportMenu.add(new JMenuItem(new SelectTaxiwaysAction()));
    airportMenu.add(new JMenuItem(new CollapseTaxiwayAreasAction()));
    airportMenu.add(new JMenuItem(new CollapseRunwaysAction()));
    airportMenu.add(new JMenuItem(new AmalgamateApronsAction()));
    this.addSeparator();
    JMenu railMenu = new JMenu("Railroads");
    this.add(railMenu);
    railMenu.add(new JMenuItem(new SelectAction("railroad")));
    railMenu.add(new JMenuItem(new DisplaySideTracksAction()));
    railMenu.addSeparator();
    railMenu.add(new JMenuItem(new TypifySideTracksAction()));
    railMenu.add(new JMenuItem(new CollapseRailsAction()));
    railMenu.add(new JMenuItem(new GroupParallelRailsAction()));
    railMenu.add(new JMenuItem(new CollapseParallelRailsAction()));

  }

  private class SelectAction extends AbstractAction {

    private IPopulation<? extends IGeneObj> pop;
    private String popName;

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      if (popName.equals("airport"))
        pop = CartAGenDoc.getInstance().getCurrentDataset().getAirports();
      else if (popName.equals("railroad"))
        pop = CartAGenDoc.getInstance().getCurrentDataset().getRailwayLines();
      for (IGeneObj obj : pop) {
        SelectionUtil.addFeatureToSelection(CartAGenPlugin.getInstance()
            .getApplication(), obj);
      }
    }

    public SelectAction(String popName) {
      this.putValue(Action.NAME, "Select all objects");
      this.popName = popName;
    }
  }

  private class BuildAirportsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      IPopulation<IGeneObj> runPop = CartAGenDoc.getInstance()
          .getCurrentDataset().getCartagenPop(CartAGenDataSet.RUNWAY_AREA_POP);
      IPopulation<IGeneObj> runLPop = CartAGenDoc.getInstance()
          .getCurrentDataset().getCartagenPop(CartAGenDataSet.RUNWAY_LINE_POP);
      IPopulation<IGeneObj> taxPop = CartAGenDoc.getInstance()
          .getCurrentDataset().getCartagenPop(CartAGenDataSet.TAXIWAY_AREA_POP);
      IPopulation<IGeneObj> taxLPop = CartAGenDoc.getInstance()
          .getCurrentDataset().getCartagenPop(CartAGenDataSet.TAXIWAY_LINE_POP);
      IPopulation<IBuilding> buildPop = CartAGenDoc.getInstance()
          .getCurrentDataset().getBuildings();
      for (IAirportArea obj : CartAGenDoc.getInstance().getCurrentDataset()
          .getAirports()) {
        // create airports
        for (IGeneObj runway : runPop.select(obj.getGeom())) {
          obj.getRunwayAreas().add((IRunwayArea) runway);
          ((IRunwayArea) runway).setAirport((IAirportArea) obj);
        }
        for (IGeneObj runwayL : runLPop.select(obj.getGeom())) {
          obj.getRunwayLines().add((IRunwayLine) runwayL);
          ((IRunwayLine) runwayL).setAirport((IAirportArea) obj);
        }
        for (IGeneObj taxL : taxLPop.select(obj.getGeom())) {
          obj.getTaxiwayLines().add((ITaxiwayLine) taxL);
          ((ITaxiwayLine) taxL).setAirport((IAirportArea) obj);
        }
        for (IGeneObj tax : taxPop.select(obj.getGeom())) {
          obj.getTaxiwayAreas().add((ITaxiwayArea) tax);
          ((ITaxiwayArea) tax).setAirport((IAirportArea) obj);
        }
        for (IBuilding building : buildPop.select(obj.getGeom())) {
          if (building.getNature().equals("terminal"))
            obj.getTerminals().add(building);
        }
      }
    }

    public BuildAirportsAction() {
      this.putValue(Action.NAME, "Build airports as complex objects");
    }
  }

  private class TypifyTaxiwaysAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());
      for (IFeature sel : SelectionUtil.getSelectedObjects(appli)) {
        if (!(sel instanceof IAirportArea))
          continue;
        IAirportArea airport = (IAirportArea) sel;
        AirportTypification algo = new AirportTypification(airport, CartAGenDoc
            .getInstance().getCurrentDataset());
        algo.setBranchingMaxArea(7000.0);
        algo.setMaxAngleBranching(14 * Math.PI / 20);
        algo.detectBranchingPatterns();
        for (TaxiwayBranching branch : algo.getBranchings()) {
          pool.addFeatureToGeometryPool(branch.getGeom(), Color.RED, 1);
          branch.collapse();
        }
        for (TaxiwayBranchingGroup branch : algo.getDoubleBranchings()) {
          pool.addFeatureToGeometryPool(branch.getGeom(), Color.PINK, 1);
          branch.collapse();
        }
        for (TaxiwayBranchingCouple branch : algo.getBranchingCouples()) {
          pool.addFeatureToGeometryPool(branch.getGeom(), Color.CYAN, 1);
          branch.collapse();
        }
      }
    }

    public TypifyTaxiwaysAction() {
      this.putValue(Action.NAME, "Typify taxiway lines");
    }
  }

  private class SelectTaxiwaysAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());
      for (IFeature sel : SelectionUtil.getSelectedObjects(appli)) {
        if (!(sel instanceof IAirportArea))
          continue;
        IAirportArea airport = (IAirportArea) sel;
        AirportTypification algo = new AirportTypification(airport, CartAGenDoc
            .getInstance().getCurrentDataset());
        algo.setTaxiwayLengthThreshold(500.0);
        algo.makeTaxiwaysPlanar();
        algo.selectTaxiwayLines();

      }
    }

    public SelectTaxiwaysAction() {
      this.putValue(Action.NAME, "Select taxiway lines by strokes");
    }
  }

  private class CollapseTaxiwayAreasAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());
      if (SelectionUtil.isEmpty(appli)) {
        AirportTypification algo = new AirportTypification(CartAGenDoc
            .getInstance().getCurrentDataset());
        algo.setOpenThreshTaxi(30.0);
        algo.collapseThinTaxiways();
        return;
      }
      for (IFeature sel : SelectionUtil.getSelectedObjects(appli)) {
        if (!(sel instanceof IAirportArea))
          continue;
        IAirportArea airport = (IAirportArea) sel;
        AirportTypification algo = new AirportTypification(airport, CartAGenDoc
            .getInstance().getCurrentDataset());
        algo.setTaxiwayLengthThreshold(500.0);
        algo.makeTaxiwaysPlanar();
        algo.selectTaxiwayLines();

      }
    }

    public CollapseTaxiwayAreasAction() {
      this.putValue(Action.NAME, "Collapse taxiway areas");
    }
  }

  private class AmalgamateApronsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());
      if (SelectionUtil.isEmpty(appli)) {
        AirportTypification algo = new AirportTypification(CartAGenDoc
            .getInstance().getCurrentDataset());
        algo.setApronClosingSize(50.0);
        algo.setApronMinArea(1000.0);
        algo.setApronSegLength(10.0);
        algo.amalgamateAprons();
        algo.simplifyAprons();
        return;
      }
      for (IFeature sel : SelectionUtil.getSelectedObjects(appli)) {
        if (!(sel instanceof IAirportArea))
          continue;
        IAirportArea airport = (IAirportArea) sel;
        AirportTypification algo = new AirportTypification(airport, CartAGenDoc
            .getInstance().getCurrentDataset());
        algo.setApronClosingSize(25.0);
        algo.setApronMinArea(1000.0);
        algo.setApronSegLength(8.0);
        algo.amalgamateAprons();
        algo.simplifyAprons();
      }
    }

    public AmalgamateApronsAction() {
      this.putValue(Action.NAME, "Amalgamate apron areas");
    }
  }

  private class CollapseRunwaysAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());
      if (SelectionUtil.isEmpty(appli)) {
        AirportTypification algo = new AirportTypification(CartAGenDoc
            .getInstance().getCurrentDataset());
        try {
          algo.collapseRunways();
        } catch (Exception e1) {
          e1.printStackTrace();
        }
        for (IRunwayLine runway : algo.getAirport().getRunwayLines()) {
          pool.addFeatureToGeometryPool(runway.getGeom(), Color.RED, 3);
        }
        return;
      }
      for (IFeature sel : SelectionUtil.getSelectedObjects(appli)) {
        if (!(sel instanceof IAirportArea))
          continue;
        IAirportArea airport = (IAirportArea) sel;
        AirportTypification algo = new AirportTypification(airport, CartAGenDoc
            .getInstance().getCurrentDataset());
        try {
          algo.collapseRunways();
        } catch (Exception e1) {
          e1.printStackTrace();
        }
        for (IRunwayLine runway : airport.getRunwayLines()) {
          pool.addFeatureToGeometryPool(runway.getGeom(), Color.RED, 3);
        }
      }
    }

    public CollapseRunwaysAction() {
      this.putValue(Action.NAME, "Collapse runway areas");
    }
  }

  private class GeneraliseAirportsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());

      for (IFeature sel : SelectionUtil.getSelectedObjects(appli)) {
        if (!(sel instanceof IAirportArea))
          continue;
        IAirportArea airport = (IAirportArea) sel;
        AirportTypification algo = new AirportTypification(airport, CartAGenDoc
            .getInstance().getCurrentDataset());

        // collapse runways
        try {
          algo.collapseRunways();
        } catch (Exception e1) {
          e1.printStackTrace();
        }

        // typify taxiways
        algo.setBranchingMaxArea(7000.0);
        algo.setMaxAngleBranching(14 * Math.PI / 20);
        algo.detectBranchingPatterns();
        for (TaxiwayBranching branch : algo.getBranchings()) {
          pool.addFeatureToGeometryPool(branch.getGeom(), Color.RED, 1);
          branch.collapse();
        }
        for (TaxiwayBranchingGroup branch : algo.getDoubleBranchings()) {
          pool.addFeatureToGeometryPool(branch.getGeom(), Color.PINK, 1);
          branch.collapse();
        }
        for (TaxiwayBranchingCouple branch : algo.getBranchingCouples()) {
          pool.addFeatureToGeometryPool(branch.getGeom(), Color.CYAN, 1);
          branch.collapse();
        }

        // select taxiways
        algo.setTaxiwayLengthThreshold(500.0);
        algo.makeTaxiwaysPlanar();
        algo.selectTaxiwayLines();

        // amalgamate aprons
        algo.setApronClosingSize(50.0);
        algo.setApronMinArea(1000.0);
        algo.setApronSegLength(10.0);
        algo.amalgamateAprons();
        algo.simplifyAprons();

        // simplify terminals
        algo.setTerminalMinArea(600.0);
        algo.setTerminalSegLength(5.0);
        algo.simplifyTerminals();
      }
    }

    public GeneraliseAirportsAction() {
      this.putValue(Action.NAME, "Generalise airports");
    }
  }

  private class TypifySideTracksAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      TypifySideTracks typify = new TypifySideTracks(100.0, CartAGenDoc
          .getInstance().getCurrentDataset(), 6.0);
      typify.typifySideTracks();

    }

    public TypifySideTracksAction() {
      this.putValue(Action.NAME, "Typify Sidetracks");
    }
  }

  private class DisplaySideTracksAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());
      for (IRailwayLine rail : CartAGenDoc.getInstance().getCurrentDataset()
          .getRailwayLines()) {
        if (rail.isSidetrack())
          pool.addFeatureToGeometryPool(rail.getGeom(), Color.PINK, 2);
      }
    }

    public DisplaySideTracksAction() {
      this.putValue(Action.NAME, "Display Sidetracks in geometry pool");
    }
  }

  private class CollapseRailsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());
      Iterator<IFeature> iter = SelectionUtil.getSelectedObjects(appli)
          .iterator();
      IFeature feat1 = iter.next();
      IFeature feat2 = iter.next();
      PartialParallelism2Lines relation = new PartialParallelism2Lines(feat1,
          feat2);
      relation.achievementAssessedBy().compute();

      for (ConvergingPoint pt : relation.getConvergencePts()) {
        if (pt.isConverging())
          pool.addFeatureToGeometryPool(pt.getPosition().toGM_Point(),
              Color.RED, 2);
        if (pt.isDiverging())
          pool.addFeatureToGeometryPool(pt.getPosition().toGM_Point(),
              Color.GREEN, 2);
      }

      for (ParallelSection section : relation.getParallelSections()) {
        ILineString line1 = ((ILineString[]) section.getValue())[0];
        ILineString line2 = ((ILineString[]) section.getValue())[1];
        IDirectPosition start1 = line1.coord().get(0);
        IDirectPosition end1 = line1.coord().get((line1.coord().size() - 1));
        IDirectPosition start2 = line2.coord().get(0);
        IDirectPosition end2 = line2.coord().get((line2.coord().size() - 1));
        pool.addFeatureToGeometryPool(line1, Color.ORANGE, 1);
        pool.addFeatureToGeometryPool(line2, Color.ORANGE, 1);
        ILineString middle = CommonAlgorithmsFromCartAGen.getMeanLine(line1,
            line2);
        // reconnection
        middle.removeControlPoint(0);
        middle.removeControlPoint(middle.coord().size() - 1);
        // extend middle at its start
        IDirectPosition start = middle.coord().get(0);
        if (start.distance2D(start1) < start.distance2D(end1)) {
          pool.addFeatureToGeometryPool(new GM_LineSegment(start, start1),
              Color.PINK, 2);
        } else {
          pool.addFeatureToGeometryPool(new GM_LineSegment(start, end1),
              Color.PINK, 2);
        }
        if (start.distance2D(start2) < start.distance2D(end2)) {
          pool.addFeatureToGeometryPool(new GM_LineSegment(start, start2),
              Color.PINK, 2);
        } else {
          pool.addFeatureToGeometryPool(new GM_LineSegment(start, end2),
              Color.PINK, 2);
        }

        // extend middle at its end
        IDirectPosition end = middle.coord().get((middle.coord().size() - 1));
        if (end.distance2D(start1) < end.distance2D(end1)) {
          pool.addFeatureToGeometryPool(new GM_LineSegment(end, start1),
              Color.PINK, 2);
        } else {
          pool.addFeatureToGeometryPool(new GM_LineSegment(end, end1),
              Color.PINK, 2);
        }
        if (end.distance2D(start2) < end.distance2D(end2)) {
          pool.addFeatureToGeometryPool(new GM_LineSegment(end, start2),
              Color.PINK, 2);
        } else {
          pool.addFeatureToGeometryPool(new GM_LineSegment(end, end2),
              Color.PINK, 2);
        }

        pool.addFeatureToGeometryPool(middle, Color.PINK, 2);
      }
    }

    public CollapseRailsAction() {
      this.putValue(Action.NAME, "Collapse parallel railways");
    }
  }

  /**
   * @author GTouya
   * 
   */
  class GroupParallelRailsAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent arg0) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      // enrich the network if necessary
      Set<IFeature> selectedObjs = SelectionUtil.getSelectedObjects(appli);
      if (selectedObjs.size() == 0)
        return;
      IFeature feature = selectedObjs.iterator().next();
      if (!(feature instanceof IGeneObj))
        return;
      INetwork net = dataset
          .getNetworkFromClass((Class<? extends IGeneObj>) feature.getClass());
      if (net.getNodes().size() == 0) {
        if (net.getSections().size() == 0) {
          for (IFeature section : selectedObjs)
            net.addSection((INetworkSection) section);
        }
        NetworkEnrichment.buildTopology(dataset, net, false);
      }

      HashSet<ArcReseau> arcs = new HashSet<ArcReseau>();
      HashSet<NoeudReseau> noeuds = new HashSet<NoeudReseau>();
      for (IFeature feat : selectedObjs) {
        if (feat instanceof IGeneObj) {
          arcs.add((ArcReseau) ((IGeneObj) feat).getGeoxObj());
          NoeudReseau noeudIni = ((ArcReseau) ((IGeneObj) feat).getGeoxObj())
              .getNoeudInitial();
          NoeudReseau noeudFin = ((ArcReseau) ((IGeneObj) feat).getGeoxObj())
              .getNoeudFinal();
          noeuds.add(noeudIni);
          noeuds.add(noeudFin);
        }
      }

      StrokesNetwork network = new StrokesNetwork(arcs);
      HashSet<String> attributeNames = new HashSet<String>();
      attributeNames.add("nom");
      network.buildStrokes(attributeNames, 112.5, 45.0, true);

      GeometryPool pool = CartAGenDoc.getInstance().getCurrentDataset()
          .getGeometryPool();
      pool.setSld(appli.getMainFrame().getSelectedProjectFrame().getSld());

      Set<ParallelRailsGroup> groups = ParallelRailsGroup
          .findParallelRailsGroup(network, 800.0, 10.0);
      System.out.println(groups.size());
      for (ParallelRailsGroup group : groups) {
        /*
         * IPolygon bufferL = BufferComputing.buildLineHalfBuffer(group
         * .getCentreStroke().getGeomStroke(), 12, Side.LEFT); IPolygon bufferR
         * = BufferComputing.buildLineHalfBuffer(group
         * .getCentreStroke().getGeomStroke(), 12, Side.RIGHT);
         * pool.addFeatureToGeometryPool(bufferL, Color.ORANGE, 1);
         * pool.addFeatureToGeometryPool(bufferR, Color.PINK, 1);
         */
        pool.addFeatureToGeometryPool(group.getCentreStroke().getGeomStroke(),
            Color.RED, 4);

        System.out.println("centreStroke: " + group.getCentreStroke());
        for (ParallelStroke pStroke : group.getParallelStrokes()) {
          System.out.println("position: " + pStroke.getPosition());
          Color colorStart = Color.YELLOW;
          if (pStroke.getStart().getType()
              .equals(ParallelismEndingType.CONVERGING))
            colorStart = Color.GREEN;
          if (pStroke.getStart().getType()
              .equals(ParallelismEndingType.DANGLING))
            colorStart = Color.PINK;
          pool.addFeatureToGeometryPool(pStroke.getStart().getPosition()
              .toGM_Point(), colorStart, 5);
          Color colorEnd = Color.ORANGE;
          if (pStroke.getEnd().getType()
              .equals(ParallelismEndingType.CONVERGING))
            colorEnd = Color.GREEN;
          if (pStroke.getEnd().getType().equals(ParallelismEndingType.DANGLING))
            colorEnd = Color.PINK;
          pool.addFeatureToGeometryPool(pStroke.getEnd().getPosition()
              .toGM_Point(), colorEnd, 5);
          pool.addFeatureToGeometryPool(pStroke.getStroke().getGeomStroke(),
              pStroke.getColor(), 3);
        }
      }
    }

    public GroupParallelRailsAction() {
      this.putValue(Action.NAME, "Group parallel railways");
    }
  }

  /**
   * @author GTouya
   * 
   */
  class CollapseParallelRailsAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      CartAGenDataSet dataset = CartAGenDoc.getInstance().getCurrentDataset();
      Collection<IRailwayLine> railways = dataset.getRailwayLines();

      CollapseParallelRailways algorithm = new CollapseParallelRailways(
          railways);
      algorithm.collapseParallelRailwayGroups();
    }

    public CollapseParallelRailsAction() {
      this.putValue(Action.NAME, "Collapse parallel railway groups");
    }
  }

}

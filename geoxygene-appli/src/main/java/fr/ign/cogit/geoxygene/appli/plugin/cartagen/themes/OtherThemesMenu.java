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
import fr.ign.cogit.cartagen.core.genericschema.railway.IRailwayLine;
import fr.ign.cogit.cartagen.core.genericschema.urban.IBuilding;
import fr.ign.cogit.cartagen.genealgorithms.facilities.AirportTypification;
import fr.ign.cogit.cartagen.genealgorithms.facilities.AirportTypification.TaxiwayBranching;
import fr.ign.cogit.cartagen.genealgorithms.facilities.AirportTypification.TaxiwayBranchingCouple;
import fr.ign.cogit.cartagen.genealgorithms.facilities.AirportTypification.TaxiwayBranchingGroup;
import fr.ign.cogit.cartagen.genealgorithms.rail.TypifySideTracks;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.dataset.GeometryPool;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SelectionUtil;

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
    airportMenu.addSeparator();
    airportMenu.add(new JMenuItem(new TypifyTaxiwaysAction()));
    airportMenu.add(new JMenuItem(new SelectTaxiwaysAction()));
    airportMenu.add(new JMenuItem(new CollapseTaxiwayAreasAction()));
    this.addSeparator();
    JMenu railMenu = new JMenu("Railroads");
    this.add(railMenu);
    railMenu.add(new JMenuItem(new SelectAction("railroad")));
    railMenu.add(new JMenuItem(new DisplaySideTracksAction()));
    railMenu.addSeparator();
    railMenu.add(new JMenuItem(new TypifySideTracksAction()));

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
        AirportTypification algo = new AirportTypification(airport);
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
        AirportTypification algo = new AirportTypification(airport);
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
        AirportTypification algo = new AirportTypification();
        algo.setOpenThreshTaxi(30.0);
        algo.collapseThinTaxiways();
        return;
      }
      for (IFeature sel : SelectionUtil.getSelectedObjects(appli)) {
        if (!(sel instanceof IAirportArea))
          continue;
        IAirportArea airport = (IAirportArea) sel;
        AirportTypification algo = new AirportTypification(airport);
        algo.setTaxiwayLengthThreshold(500.0);
        algo.makeTaxiwaysPlanar();
        algo.selectTaxiwayLines();

      }
    }

    public CollapseTaxiwayAreasAction() {
      this.putValue(Action.NAME, "Collapse taxiway areas");
    }
  }

  private class TypifySideTracksAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent e) {
      final GeOxygeneApplication appli = CartAGenPlugin.getInstance()
          .getApplication();
      TypifySideTracks typify = new TypifySideTracks(100.0, CartAGenDoc
          .getInstance().getCurrentDataset());
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
}

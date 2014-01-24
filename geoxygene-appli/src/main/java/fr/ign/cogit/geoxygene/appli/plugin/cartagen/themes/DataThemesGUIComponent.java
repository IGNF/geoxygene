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

import javax.swing.JMenu;

public class DataThemesGUIComponent extends JMenu {

  private static DataThemesGUIComponent menu;

  public static DataThemesGUIComponent getInstance() {
    if (DataThemesGUIComponent.menu == null) {
      return new DataThemesGUIComponent("Themes");
    }
    return DataThemesGUIComponent.menu;
  }

  private ReliefMenu reliefMenu;
  private LandUseMenu landUseMenu;
  private RoadNetworkMenu roadNetMenu;
  private HydroNetworkMenu hydroNetMenu;
  private BuildingMenu buildingMenu;
  private BlockMenu blockMenu;
  private UrbanAlignmentMenu alignmentMenu;
  private TownMenu townMenu;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public DataThemesGUIComponent(String title) {
    super(title);

    this.reliefMenu = new ReliefMenu("Relief");
    this.landUseMenu = new LandUseMenu("Land Use");
    this.roadNetMenu = new RoadNetworkMenu("Road Network");
    this.hydroNetMenu = new HydroNetworkMenu("Hydro Network");
    this.buildingMenu = new BuildingMenu("Building");
    this.blockMenu = new BlockMenu("Block");
    this.alignmentMenu = new UrbanAlignmentMenu("Urban alignment");
    this.townMenu = new TownMenu("Town");
    this.add(this.reliefMenu);
    this.add(this.landUseMenu);
    this.add(this.roadNetMenu);
    this.add(this.hydroNetMenu);
    this.add(this.buildingMenu);
    this.add(this.blockMenu);
    this.add(this.alignmentMenu);
    this.add(this.townMenu);

    DataThemesGUIComponent.menu = this;

  }

  public ReliefMenu getReliefMenu() {
    return this.reliefMenu;
  }

  public void setReliefMenu(ReliefMenu reliefMenu) {
    this.reliefMenu = reliefMenu;
  }

  public LandUseMenu getLandUseMenu() {
    return this.landUseMenu;
  }

  public void setLandUseMenu(LandUseMenu landUseMenu) {
    this.landUseMenu = landUseMenu;
  }

  public RoadNetworkMenu getRoadNetMenu() {
    return this.roadNetMenu;
  }

  public void setRoadNetMenu(RoadNetworkMenu roadNetMenu) {
    this.roadNetMenu = roadNetMenu;
  }

  public HydroNetworkMenu getHydroNetMenu() {
    return this.hydroNetMenu;
  }

  public void setHydroNetMenu(HydroNetworkMenu hydroNetMenu) {
    this.hydroNetMenu = hydroNetMenu;
  }

  public BuildingMenu getBuildingMenu() {
    return this.buildingMenu;
  }

  public void setBuildingMenu(BuildingMenu buildingMenu) {
    this.buildingMenu = buildingMenu;
  }

  public BlockMenu getBlockMenu() {
    return this.blockMenu;
  }

  public void setBlockMenu(BlockMenu blockMenu) {
    this.blockMenu = blockMenu;
  }

  public UrbanAlignmentMenu getUrbanAlignmentMenu() {
    return this.alignmentMenu;
  }

  public void setUrbanAlignmentMenu(UrbanAlignmentMenu alignmentMenu) {
    this.alignmentMenu = alignmentMenu;
  }

  public TownMenu getTownMenu() {
    return this.townMenu;
  }

  public void setTownMenu(TownMenu townMenu) {
    this.townMenu = townMenu;
  }

}

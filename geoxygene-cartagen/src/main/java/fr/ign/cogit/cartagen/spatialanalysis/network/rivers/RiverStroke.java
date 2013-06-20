package fr.ign.cogit.cartagen.spatialanalysis.network.rivers;

import fr.ign.cogit.cartagen.spatialanalysis.network.Stroke;
import fr.ign.cogit.cartagen.spatialanalysis.network.StrokesNetwork;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.schemageo.api.hydro.TronconHydrographique;
import fr.ign.cogit.geoxygene.schemageo.api.support.reseau.ArcReseau;

public class RiverStroke extends Stroke {

  private int hortonOrder = 0;
  private boolean isBraided = false;

  public RiverStroke(StrokesNetwork network, ArcReseau root) {
    super(network, root);
    // TODO Auto-generated constructor stub
  }

  public int getHortonOrder() {
    return hortonOrder;
  }

  public void setHortonOrder(int hortonOrder) {
    this.hortonOrder = hortonOrder;
  }

  /**
   * Gives the name of the last section of the stroke.
   * @return
   */
  public String getLastName() {
    return ((TronconHydrographique) this.getFeatures().get(
        this.getFeatures().size() - 1)).getNom();
  }

  /**
   * Get the linear geometry of the last section of the stroke.
   * @return
   */
  public ILineString getLastLine() {
    return (ILineString) this.getFeatures().get(this.getFeatures().size() - 1)
        .getGeom();
  }

  /**
   * Get the last section of the stroke.
   * @return
   */
  public ArcReseau getLastFeat() {
    return this.getFeatures().get(this.getFeatures().size() - 1);
  }

  public void computeHortonOrder() {
    this.hortonOrder = 1;
    for (ArcReseau arc : getFeatures()) {
      int strahler = ((RiverStrokesNetwork) this.getNetwork())
          .getStrahlerOrders().get(arc);
      if (strahler > this.hortonOrder)
        this.hortonOrder = strahler;
    }

  }

  public boolean isBraided() {
    return isBraided;
  }

  public void setBraided(boolean isBraided) {
    this.isBraided = isBraided;
  }
}

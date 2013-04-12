/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.hexagonaltess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class HexagonalTessellation {

  private IDirectPosition corner;
  private double width;
  private IEnvelope envelope;
  private int rowNb, colNb;
  private List<HexagonalCell> cells;
  private FT_FeatureCollection<DefaultFeature> index;

  public HexagonalTessellation(IEnvelope envelope, double width) {
    this.envelope = envelope;
    this.width = width;
    this.corner = new DirectPosition(envelope.center().getX()
        - (envelope.width() / 2), envelope.center().getY()
        + (envelope.length() / 2));
    computeRowColNb();
    computeCells();
  }

  public IDirectPosition getCorner() {
    return corner;
  }

  public void setCorner(IDirectPosition corner) {
    this.corner = corner;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public IEnvelope getEnvelope() {
    return envelope;
  }

  public void setEnvelope(IEnvelope envelope) {
    this.envelope = envelope;
  }

  public int getRowNb() {
    return rowNb;
  }

  public void setRowNb(int rowNb) {
    this.rowNb = rowNb;
  }

  public int getColNb() {
    return colNb;
  }

  public void setColNb(int colNb) {
    this.colNb = colNb;
  }

  public List<HexagonalCell> getCells() {
    return cells;
  }

  public void setCells(List<HexagonalCell> cells) {
    this.cells = cells;
  }

  private void computeCells() {
    this.cells = new ArrayList<HexagonalCell>();
    for (int i = 1; i <= rowNb; i++) {
      for (int j = 1; j <= colNb; j++) {
        if (isEven(i) && (!isEven(j)))
          continue;
        if (!isEven(i) && isEven(j))
          continue;
        // now compute the center for hexagon (i,j)
        double xCenter = corner.getX() + (0.75 * width * (j - 1));
        double yCenter = corner.getY() + (Math.sqrt(3) * width / 4)
            - (Math.sqrt(3) * width * (i - 1) / 4);

        this.cells.add(new HexagonalCell(this, i, j, new DirectPosition(
            xCenter, yCenter), width));
      }
    }
  }

  private boolean isEven(int number) {
    int rest = number % 2;
    if (rest == 0)
      return true;
    return false;
  }

  private void computeRowColNb() {
    double colSize = width * 3 / 4;
    this.colNb = Math.round((float) this.envelope.width() / (float) colSize) + 1;
    this.rowNb = Math.round((float) this.envelope.length() / (float) colSize) * 2 + 2;
  }

  private void buildIndex() {
    this.index = new FT_FeatureCollection<DefaultFeature>();
    for (HexagonalCell cell : cells) {
      index.add(new DefaultFeature(cell.getGeom()));
    }
  }

  public Set<HexagonalCell> getContainingCells(IDirectPosition point) {
    Set<HexagonalCell> set = new HashSet<HexagonalCell>();
    if (!envelope.contains(point))
      return set;

    if (index == null)
      buildIndex();

    // get the features containing the point
    Collection<DefaultFeature> query = this.index.select(point.toGM_Point());
    for (DefaultFeature feat : query) {
      int i = this.index.getElements().indexOf(feat);
      set.add(cells.get(i));
    }
    return set;
  }
}

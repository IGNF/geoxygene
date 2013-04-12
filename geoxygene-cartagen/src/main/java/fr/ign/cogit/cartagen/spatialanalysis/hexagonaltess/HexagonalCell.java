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

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.GeometryFactory;

public class HexagonalCell {

  private HexagonalTessellation tessellation;
  private int row, column;
  private IDirectPosition center;
  private double width, segSize, radius;

  public HexagonalCell(HexagonalTessellation tessellation, int row, int column,
      IDirectPosition center, double width) {
    this.tessellation = tessellation;
    this.row = row;
    this.column = column;
    this.center = center;
    this.segSize = width / 2;
    this.width = width;
    this.radius = Math.sqrt(3) / 2 * width;
  }

  public HexagonalTessellation getTessellation() {
    return tessellation;
  }

  public void setTessellation(HexagonalTessellation tessellation) {
    this.tessellation = tessellation;
  }

  public int getRow() {
    return row;
  }

  public void setRow(int row) {
    this.row = row;
  }

  public int getColumn() {
    return column;
  }

  public void setColumn(int column) {
    this.column = column;
  }

  public IDirectPosition getCenter() {
    return center;
  }

  public void setCenter(IDirectPosition center) {
    this.center = center;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getSegSize() {
    return segSize;
  }

  public void setSegSize(double segSize) {
    this.segSize = segSize;
  }

  public double getRadius() {
    return radius;
  }

  public void setRadius(double radius) {
    this.radius = radius;
  }

  /**
   * Builds the hexagon geometry
   * @return
   */
  public IPolygon getGeom() {
    IPolygon geom = GeometryFactory.buildHexagon(center, width);
    return geom;
  }

  @Override
  public String toString() {
    return "Cell (" + row + "," + column + ")";
  }

}

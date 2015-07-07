package fr.ign.cogit.cartagen.software.dataset;

import java.awt.Color;
import java.util.Map;

import fr.ign.cogit.cartagen.graph.IEdge;
import fr.ign.cogit.cartagen.graph.IGraph;
import fr.ign.cogit.cartagen.graph.INode;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool.ColouredFeature;
import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class GeometryPool {

  private IDataSet<?> dataset;
  private StyledLayerDescriptor sld;

  /**
   * Simple Constructor using fields.
   * @param dataset
   * @param sld
   */
  public GeometryPool(CartAGenDataSet dataset, StyledLayerDescriptor sld) {
    super();
    this.dataset = dataset;
    this.sld = sld;
  }

  /**
   * Simple Constructor using fields.
   * @param dataset
   * @param sld
   */
  public GeometryPool(IDataSet<?> dataset, StyledLayerDescriptor sld) {
    super();
    this.dataset = dataset;
    this.sld = sld;
  }

  public IDataSet<?> getDataset() {
    return dataset;
  }

  public void setDataset(IDataSet<?> dataset) {
    this.dataset = dataset;
  }

  public StyledLayerDescriptor getSld() {
    return sld;
  }

  public void setSld(StyledLayerDescriptor sld) {
    this.sld = sld;
  }

  /**
   * Add a given feature to the geometry pool with the given color.
   * @param feat
   * @param color
   */
  @SuppressWarnings("unchecked")
  public void addFeatureToGeometryPool(IGeometry geom, Color color,
      int widthPixels) {
    ColouredFeature colFeat = new ColouredFeature(geom, color, widthPixels);
    ((IPopulation<IFeature>) dataset.getPopulation(CartAGenDataSet.GEOM_POOL))
        .add(colFeat);
    Layer poolLayer = sld.getLayer(CartAGenDataSet.GEOM_POOL);
    if (poolLayer == null)
      return;
    Style style = poolLayer.getStyles().get(0);
    style.getFeatureTypeStyles().add(colFeat.computeFeatureStyle());
  }

  /**
   * Add a given feature to the geometry pool with the given color.
   * @param feat
   * @param color
   */
  @SuppressWarnings("unchecked")
  public void addPointFeatureToGeometryPool(IGeometry geom, Color color,
      int widthPixels, String symbolName) {
    ColouredFeature colFeat = new ColouredFeature(geom, color, widthPixels);
    colFeat.setSymbolName(symbolName);
    ((IPopulation<IFeature>) dataset.getPopulation(CartAGenDataSet.GEOM_POOL))
        .add(colFeat);
    Layer poolLayer = sld.getLayer(CartAGenDataSet.GEOM_POOL);
    if (poolLayer == null)
      return;
    Style style = poolLayer.getStyles().get(0);
    style.getFeatureTypeStyles().add(colFeat.computeFeatureStyle());
  }

  /**
   * Add a given feature to the geometry pool with the given color.
   * @param feat
   * @param color
   */
  @SuppressWarnings("unchecked")
  public void addPolygonToGeometryPool(IPolygon geom, Color fillColor,
      Color strokeColor, int widthPixels) {
    ColouredFeature colFeat = new ColouredFeature(geom, fillColor, strokeColor,
        widthPixels);
    ((IPopulation<IFeature>) dataset.getPopulation(CartAGenDataSet.GEOM_POOL))
        .add(colFeat);
    Layer poolLayer = sld.getLayer(CartAGenDataSet.GEOM_POOL);
    if (poolLayer == null)
      return;
    Style style = poolLayer.getStyles().get(0);
    style.getFeatureTypeStyles().add(colFeat.computeFeatureStyle());
  }

  /**
   * Add a given feature to the geometry pool with the given color.
   * @param feat
   * @param color
   */
  @SuppressWarnings("unchecked")
  public void addFeatureToGeometryPool(IFeature feat, Color color) {
    ColouredFeature colFeat = new ColouredFeature(feat.getGeom(), color);
    ((IPopulation<IFeature>) dataset.getPopulation(CartAGenDataSet.GEOM_POOL))
        .add(colFeat);
    Layer poolLayer = sld.getLayer(CartAGenDataSet.GEOM_POOL);
    if (poolLayer == null)
      return;
    Style style = poolLayer.getStyles().get(0);
    style.getFeatureTypeStyles().add(colFeat.computeFeatureStyle());
  }

  /**
   * Display a graph in the geometry pool with the given colors.
   * @param feat
   * @param color
   */
  @SuppressWarnings("unchecked")
  public void addGraphToGeometryPool(IGraph graph, Color edgeColor,
      Color nodeColor) {
    for (IEdge edge : graph.getEdges()) {
      ColouredFeature colFeat = new ColouredFeature(edge.getGeom(), edgeColor);
      ((IPopulation<IFeature>) dataset.getPopulation(CartAGenDataSet.GEOM_POOL))
          .add(colFeat);
      Layer poolLayer = sld.getLayer(CartAGenDataSet.GEOM_POOL);
      if (poolLayer == null)
        return;
      Style style = poolLayer.getStyles().get(0);
      style.getFeatureTypeStyles().add(colFeat.computeFeatureStyle());
    }
    for (INode node : graph.getNodes()) {
      ColouredFeature colFeat = new ColouredFeature(node.getGeom(), nodeColor);
      ((IPopulation<IFeature>) dataset.getPopulation(CartAGenDataSet.GEOM_POOL))
          .add(colFeat);
      Layer poolLayer = sld.getLayer(CartAGenDataSet.GEOM_POOL);
      if (poolLayer == null)
        return;
      Style style = poolLayer.getStyles().get(0);
      style.getFeatureTypeStyles().add(colFeat.computeFeatureStyle());
    }
  }

  /**
   * Add a given vector to the geometry pool with the given color, at the given
   * position.
   * @param feat
   * @param color
   */
  @SuppressWarnings("unchecked")
  public void addVectorToGeometryPool(Vector2D vector, IDirectPosition startPt,
      Color color, int widthPixels) {
    IMultiCurve<ILineString> geom = new GM_MultiCurve<ILineString>();
    geom.add(vector.toGeom(startPt));
    // draw the arrow of the vector
    IDirectPosition endPt = vector.translate(startPt);
    double direction1 = vector.direction().getValeur() + 0.75 * Math.PI;
    if (direction1 > 2 * Math.PI)
      direction1 -= 2 * Math.PI;
    IDirectPositionList arrowList = new DirectPositionList();
    Vector2D vect1 = new Vector2D(new Angle(direction1), vector.norme() / 5);
    arrowList.add(vect1.translate(endPt));
    arrowList.add(endPt);
    double direction2 = vector.direction().getValeur() - 0.75 * Math.PI;
    if (direction2 < 0)
      direction2 += 2 * Math.PI;
    Vector2D vect2 = new Vector2D(new Angle(direction2), vector.norme() / 5);
    arrowList.add(vect2.translate(endPt));
    geom.add(new GM_LineString(arrowList));
    ColouredFeature colFeat = new ColouredFeature(geom, color, widthPixels);
    ((IPopulation<IFeature>) dataset.getPopulation(CartAGenDataSet.GEOM_POOL))
        .add(colFeat);
    Layer poolLayer = sld.getLayer(CartAGenDataSet.GEOM_POOL);
    if (poolLayer == null)
      return;
    Style style = poolLayer.getStyles().get(0);
    style.getFeatureTypeStyles().add(colFeat.computeFeatureStyle());
  }

  public void addGridValueToPool(Map<Integer, Map<Integer, Double>> grid,
      Color color, IEnvelope env) {
    // first get the min and max values
    double min = Double.MAX_VALUE;
    double max = 0.0;
    for (Integer i : grid.keySet()) {
      for (Integer j : grid.get(i).keySet()) {
        double value = grid.get(i).get(j);
        if (value > max)
          max = value;
        if (value < min)
          min = value;
      }
    }

    double step = (max - min) / 8;
    // compute cell size
    double cellSize = env.width() / grid.get(0).size();
    System.out.println("max: " + max);
    System.out.println("min: " + min);
    System.out.println("step: " + step);

    for (Integer i : grid.keySet()) {
      for (Integer j : grid.get(i).keySet()) {
        double value = grid.get(i).get(j);
        // compute the color
        Color cellColor = new Color(255, 247, 236);
        if (value > max - 7 * step)
          cellColor = new Color(254, 232, 200);
        if (value > max - 6 * step)
          cellColor = new Color(253, 212, 158);
        if (value > max - 5 * step)
          cellColor = new Color(253, 187, 132);
        if (value > max - 4 * step)
          cellColor = new Color(252, 141, 89);
        if (value > max - 3 * step)
          cellColor = new Color(239, 101, 72);
        if (value > max - 2 * step)
          cellColor = new Color(215, 48, 31);
        if (value > max - step)
          cellColor = new Color(153, 0, 0);

        // compute the cell geometry
        IDirectPositionList coords = new DirectPositionList();
        coords.add(new DirectPosition(env.minX() + j * cellSize, env.minY() + i
            * cellSize));
        coords.add(new DirectPosition(env.minX() + (j + 1) * cellSize, env
            .minY() + i * cellSize));
        coords.add(new DirectPosition(env.minX() + (j + 1) * cellSize, env
            .minY() + (i + 1) * cellSize));
        coords.add(new DirectPosition(env.minX() + j * cellSize, env.minY()
            + (i + 1) * cellSize));
        coords.add(new DirectPosition(env.minX() + j * cellSize, env.minY() + i
            * cellSize));

        addPolygonToGeometryPool(new GM_Polygon(new GM_LineString(coords)),
            cellColor, color, 2);
      }
    }
  }
}

package fr.ign.cogit.cartagen.software.dataset;

import java.awt.Color;

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
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
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
  public void addFeatureToGeometryPool(IFeature feat, Color color) {
    ColouredFeature colFeat = new ColouredFeature(feat.getGeom(), color);
    System.out.println(colFeat);
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

}

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
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;

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
}

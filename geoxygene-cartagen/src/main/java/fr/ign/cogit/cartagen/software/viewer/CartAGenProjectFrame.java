package fr.ign.cogit.cartagen.software.viewer;

import java.awt.geom.NoninvertibleTransformException;

import javax.swing.ImageIcon;

import fr.ign.cogit.cartagen.core.genericschema.AbstractCreationFactory;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool.ColouredFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.UserLayer;

public class CartAGenProjectFrame extends ProjectFrame {

  /****/
  private static final long serialVersionUID = 1L;

  private CartAGenDB db;
  /**
   * The creation factory (for objects creation depending on the geographic
   * schema)
   */
  private AbstractCreationFactory creationFactory;
  /**
   * Geometries pool layer
   */
  private Layer geometriesPoolLayer = null;
  private IFeatureCollection<ColouredFeature> geometriesPool = new FT_FeatureCollection<ColouredFeature>();

  public CartAGenProjectFrame(MainFrame frame, ImageIcon iconImage) {
    super(frame, iconImage);
  }

  public CartAGenDB getDb() {
    return db;
  }

  public void setDb(CartAGenDB db) {
    this.db = db;
  }

  public AbstractCreationFactory getCreationFactory() {
    return this.creationFactory;
  }

  public void setCreationFactory(AbstractCreationFactory creationFactory) {
    this.creationFactory = creationFactory;
  }

  public Layer getGeometriesPoolLayer() {
    if (this.geometriesPoolLayer == null) {
      this.geometriesPoolLayer = new UserLayer(this.geometriesPool,
          "geometryPool");
    }
    return this.geometriesPoolLayer;
  }

  /**
   * Empty the pool layer
   */
  public void emptyGeometriesPool() {
    this.geometriesPool.clear();
    try {
      this.getLayerViewPanel().getViewport().update();
    } catch (NoninvertibleTransformException e) {
      e.printStackTrace();
    }
  }

}

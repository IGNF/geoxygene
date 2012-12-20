/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * 
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractButton;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.core.genericschema.partition.IMask;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool.ColouredFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;

/**
 * @author JGaffuri
 */
public class LayerManager {

  private static Logger logger = Logger.getLogger(LayerManager.class.getName());

  /**
   * Liste des couches d'objets a partir desquelles creer des couches
   * symbolisees
   */
  private ArrayList<Layer> layers = null;

  /**
   * @return
   */
  public ArrayList<Layer> getLayers() {
    if (this.layers == null) {
      this.layers = new ArrayList<Layer>();
    }
    return this.layers;
  }

  /**
   * Geometries pool layer
   */
  private LoadedLayer geometriesPoolLayer = null;
  private IFeatureCollection<ColouredFeature> geometriesPool = new FT_FeatureCollection<ColouredFeature>();

  public LoadedLayer getGeometriesPoolLayer() {
    if (this.geometriesPoolLayer == null) {
      this.geometriesPoolLayer = new LoadedLayer(this.geometriesPool, false);
    }
    return this.geometriesPoolLayer;
  }

  /**
   * Symbolised geometries pool layer
   */
  private SymbolisedLayer geometriesPoolSymbolisedLayer;

  public SymbolisedLayer getGeometriesPoolSymbolisedLayer() {
    if (this.geometriesPoolSymbolisedLayer == null) {
      this.geometriesPoolSymbolisedLayer = new SymbolisedLayer(
          this.getGeometriesPoolLayer(),
          Symbolisation.colouredGeom(),
          CartagenApplication.getInstance().getFrame().getMenu().mGeomPoolVisible);
    }
    return this.geometriesPoolSymbolisedLayer;
  }

  /**
   * Add a red geometry to the pool layer
   * @param geom
   */
  public void addToGeometriesPool(IGeometry geom) {
    this.geometriesPool.add(new ColouredFeature(geom));
    CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
  }

  /**
   * Add a coloured geometry to the pool layer
   * @param geom
   * @param colour
   */
  public void addToGeometriesPool(IGeometry geom, Color colour) {
    this.geometriesPool.add(new ColouredFeature(geom, colour));
    CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
  }

  /**
   * Add a symbolised geometry to the pool layer
   * @param geom the geom to add
   * @param symbolisation the associated {@code Symbolisation} - can be created
   *          with one of the static methods of the {@link Symbolisation} class
   */
  public void addToGeometriesPool(IGeometry geom, Symbolisation symbolisation) {
    this.geometriesPool.add(new ColouredFeature(geom, symbolisation));
    CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
  }

  /**
   * Add several symbolised geometries to the pool layer, all with the same
   * symbolisation
   * @param geoms the collection of geometries to add
   * @param symbolisation the associated {@code Symbolisation} - can be created
   *          with one of the static methods of the {@link Symbolisation} class
   */
  public void addToGeometriesPool(Collection<IGeometry> geoms,
      Symbolisation symbolisation) {
    Collection<ColouredFeature> colFeatColn = new ArrayList<ColouredFeature>();
    for (IGeometry geom : geoms) {
      colFeatColn.add(new ColouredFeature(geom, symbolisation));
    }
    this.geometriesPool.addCollection(colFeatColn);
    CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
  }

  /**
   * Add a symbolised geometry to the pool layer, with a symbolisation such
   * that: if the geometry is a point, it is drawn as a "+" cross; if it is a
   * line, it is drawn with the colour and width in parameters; if it is a
   * polygon, its border is drawn with the colour and width in parameter and it
   * is filled with the same colour but semi-transparent.
   * @param geom the geom to add
   * @param colour the colour with which the geometry will be drawn
   * @param widthPixels the width in pixels (width of the line, resp. the
   *          contour of the polygon, resp. the stokes of the "+" cross symbol
   *          on the point
   */
  public void addToGeometriesPool(IGeometry geom, Color colour, int widthPixels) {
    this.geometriesPool.add(new ColouredFeature(geom, colour, widthPixels));
    CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
  }

  /**
   * Add several symbolised geometries to the pool layer, with a symbolisation
   * such that: if the geometry is a point, it is drawn as a "+" cross; if it is
   * a line, it is drawn with the colour and width in parameters; if it is a
   * polygon, its border is drawn with the colour and width in parameter and it
   * is filled with the same colour but semi-transparent.
   * @param geoms the collection of geoms to add
   * @param colour the colour with which the geometries will be drawn
   * @param widthPixels the width in pixels (width of the line, resp. the
   *          contour of the polygon, resp. the stokes of the "+" cross symbol
   *          on the point
   */
  public void addToGeometriesPool(Collection<IGeometry> geoms, Color colour,
      int widthPixels) {
    Collection<ColouredFeature> colFeatColn = new ArrayList<ColouredFeature>();
    for (IGeometry geom : geoms) {
      colFeatColn.add(new ColouredFeature(geom, colour, widthPixels));
    }
    this.geometriesPool.addCollection(colFeatColn);
    CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
  }

  /**
   * Add several symbolised geometries to the pool layer, with a symbolisation
   * such that for each geometry: if the geometry is a point, it is drawn as a
   * "+" cross; if it is a line, it is drawn with the colour and width in
   * parameters; if it is a polygon, its border is drawn with the colour and
   * width in parameter and it is filled with the same colour but
   * semi-transparent.
   * @param geoms the array of geometries to add
   * @param colours the colours with which the geometries will be drawn
   * @param widthsPixels the widths in pixels (widths of the line, resp. the
   *          contour of the polygon, resp. the stokes of the "+" cross symbol
   *          on the point
   */
  public void addToGeometriesPool(IGeometry[] geoms, Color colours[],
      int widthsPixels[]) {
    // Check the lists in parameter have the same lengths
    if ((geoms.length != colours.length)
        || (geoms.length != widthsPixels.length)
        || (colours.length != widthsPixels.length)) {
      LayerManager.logger
          .error("Problem in "
              + this.getClass().getSimpleName()
              + "#addToGeometriesPool: not the same number of geoms, colours and widths");
      return;
    }
    this.emptyGeometriesPool();
    for (int i = 0; i < geoms.length; i++) {
      this.geometriesPool.add(new ColouredFeature(geoms[i], colours[i],
          widthsPixels[i]));
    }
    CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
  }

  /**
   * Remove a geometry from the pool layer
   * @param geom
   */
  public void removeFromGeometriesPool(IGeometry geom) {
    for (ColouredFeature feat : this.geometriesPool) {
      if (geom.equals(feat.getGeom())) {
        this.geometriesPool.remove(feat);
      }
    }
    CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
  }

  /**
   * Empty the pool layer
   */
  public void emptyGeometriesPool() {
    this.geometriesPool.clear();
    CartagenApplication.getInstance().getFrame().getVisuPanel().activate();
  }

  /**
   * Layer masque
   */
  private LoadedLayer masqueLayer = null;
  private IFeatureCollection<IMask> masque = new FT_FeatureCollection<IMask>();

  public LoadedLayer getMasqueLayer() {
    if (this.masqueLayer == null) {
      this.masqueLayer = new LoadedLayer(this.masque, false);
    }
    return this.masqueLayer;
  }

  public IFeatureCollection<IMask> getMasque() {
    return this.masque;
  }

  public void addMasque(IMask mask) {
    this.masque.add(mask);
  }

  public void emptyMasque() {
    this.masque.clear();
  }

  /**
   * Cree et ajoute une couche d'objets charges (depuis un fichier shp par
   * exemple) a l'interface. Elle peut alors etre utilisee pour construire une
   * couche symbolisee qui, elle, sera affichable dans l'interface
   * @param fc
   * @return
   */
  public LoadedLayer addLayer(IFeatureCollection<? extends IFeature> fc) {
    LoadedLayer c = new LoadedLayer(fc);
    this.getLayers().add(c);
    return c;
  }

  public LoadedLayer addLayer(IFeatureCollection<? extends IFeature> fc,
      boolean selectionnable) {
    LoadedLayer c = new LoadedLayer(fc, selectionnable);
    this.getLayers().add(c);
    return c;
  }

  public void addLayer(LoadedLayer layer) {

    this.getLayers().add(layer);

  }

  /**
   * Cree et ajoute une couche d'objets d'une classe persistante mappee avec
   * hibernate.
   * 
   * @param classe
   * @return
   */
  @SuppressWarnings("unchecked")
  public HibernateLayer addLayer(Class<?> classe) {
    HibernateLayer c = new HibernateLayer((Class<IFeature>) classe);
    this.getLayers().add(c);
    return c;
  }

  @SuppressWarnings("unchecked")
  public HibernateLayer addLayer(Class<?> classe, boolean selectionnable) {
    HibernateLayer c = new HibernateLayer((Class<IFeature>) classe,
        selectionnable);
    this.getLayers().add(c);
    return c;
  }

  /**
   * Liste des couches d'objets symbolises a afficher dans l'interface. Les
   * couches symbolisees sont affichees dans l'ordre dans lequel elles sont
   * placees dans la liste.
   */
  private ArrayList<SymbolisedLayer> symbolisedLayers = null;

  /**
   * @return
   */
  public ArrayList<SymbolisedLayer> getSymbolisedLayers() {
    if (this.symbolisedLayers == null) {
      this.symbolisedLayers = new ArrayList<SymbolisedLayer>();
    }
    return this.symbolisedLayers;
  }

  /**
   * Ajoute une couche symbolisee a la fin de la liste des couches symbolisees e
   * afficher
   * @param cs
   */
  public void addSymbolisedLayer(Layer c, Symbolisation s, AbstractButton b) {
    this.getSymbolisedLayers().add(new SymbolisedLayer(c, s, b));
  }

  public void addSymbolisedLayer(Layer c, Symbolisation s) {
    this.getSymbolisedLayers().add(new SymbolisedLayer(c, s));
  }

  public synchronized void updateCache() {
    // parcours toutes les couches pour forcer la maj de leur cache
    for (Layer c : this.getLayers()) {
      if (c == null) {
        continue;
      }
      c.setCacheUpdate(true);
    }
    this.getGeometriesPoolLayer().setCacheUpdate(true);
  }

  public void removeLayer(LoadedLayer loadedLayer) {

    this.getLayers().remove(loadedLayer);

  }
}

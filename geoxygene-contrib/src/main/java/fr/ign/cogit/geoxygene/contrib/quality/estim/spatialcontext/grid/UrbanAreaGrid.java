package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopoFactory;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat.GridCell;
import fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat.UrbanGrid;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 *
 * A class to delineate urban areas using a road network (from Touya, 2011)
 * @author JFGirres
 * 
 */
public class UrbanAreaGrid extends AbstractGrid {

  private IFeatureCollection<IFeature> jddRoadsIn;

  public void setJddRoadsIn(IFeatureCollection<IFeature> jddRoadsIn) {
    this.jddRoadsIn = jddRoadsIn;
  }

  public IFeatureCollection<IFeature> getJddRoadsIn() {
    return jddRoadsIn;
  }

  private IFeatureCollection<IFeature> jddNodesRoadsIn;

  public void setJddNodesRoadsIn(IFeatureCollection<IFeature> jddNodesRoadsIn) {
    this.jddNodesRoadsIn = jddNodesRoadsIn;
  }

  public IFeatureCollection<IFeature> getJddNodesRoadsIn() {
    return jddNodesRoadsIn;
  }

  private IFeatureCollection<IFeature> jddUrbanArea;

  public void setJddUrbanArea(IFeatureCollection<IFeature> jddUrbanArea) {
    this.jddUrbanArea = jddUrbanArea;
  }

  public IFeatureCollection<IFeature> getJddUrbanArea() {
    return jddUrbanArea;
  }

  private IFeatureCollection<IFeature> jddFilterUrbanArea;

  public void setJddFilterUrbanArea(
      IFeatureCollection<IFeature> jddFilterUrbanArea) {
    this.jddFilterUrbanArea = jddFilterUrbanArea;
  }

  public IFeatureCollection<IFeature> getJddFilterUrbanArea() {
    return jddFilterUrbanArea;
  }

  private int seuilBasNoeud;

  public void setSeuilBasNoeud(int seuilBasNoeud) {
    this.seuilBasNoeud = seuilBasNoeud;
  }

  public int getSeuilBasNoeud() {
    return seuilBasNoeud;
  }

  private int seuilHautNoeud;

  public void setSeuilHautNoeud(int seuilHautNoeud) {
    this.seuilHautNoeud = seuilHautNoeud;
  }

  public int getSeuilHautNoeud() {
    return seuilHautNoeud;
  }

  private int seuilBasArc;

  public void setSeuilBasArc(int seuilBasArc) {
    this.seuilBasArc = seuilBasArc;
  }

  public int getSeuilBasArc() {
    return seuilBasArc;
  }

  private int seuilHautArc;

  public void setSeuilHautArc(int seuilHautArc) {
    this.seuilHautArc = seuilHautArc;
  }

  public int getSeuilHautArc() {
    return seuilHautArc;
  }

  @SuppressWarnings("unused")
  private boolean boucheTrou = true;

  public void setBoucheTrou(boolean boucheTrou) {
    this.boucheTrou = boucheTrou;
  }

  private double superficieMin;

  public double getSuperficieMin() {
    return superficieMin;
  }

  public void setSuperficieMin(double superficieMin) {
    this.superficieMin = superficieMin;
  }

  public UrbanAreaGrid(IFeatureCollection<IFeature> jddRoadsIn) {
    super();
    this.setEnvelope(jddRoadsIn);
    this.setJddRoadsIn(jddRoadsIn);
    generateNodes();
  }

  /**
   * Generate road nodes using topological map
   */
  private void generateNodes() {
    IFeatureCollection<IFeature> jddNodes = new FT_FeatureCollection<IFeature>();
    CarteTopo carteTopoRoads = CarteTopoFactory.newCarteTopo(jddRoadsIn);
    for (Noeud noeud : carteTopoRoads.getPopNoeuds()) {
      IDirectPosition dpNoeud = noeud.getCoord();
      jddNodes.add(new DefaultFeature(dpNoeud.toGM_Point()));
    }
    setJddNodesRoadsIn(jddNodes);
  }

  /**
   * Create the grid using a ratio criteria
   */
  public void createAreaFromRoads() {
    UrbanGrid grid = new UrbanGrid(this.getTailleCellule(), this.getRayon(),
        this.getMinX(), this.getMaxX(), this.getMinY(), this.getMaxY(), 0,
        jddRoadsIn, jddNodesRoadsIn);
    try {
      grid.setCriteres(true, 0.5, seuilBasNoeud, seuilHautNoeud, true, 0.5,
          seuilBasArc, seuilHautArc);
      System.out.println("OK");
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    }

    IFeatureCollection<IFeature> jddCluster = new FT_FeatureCollection<IFeature>();

    HashMap<HashSet<GridCell>, Double> map = grid.creerClusters("total");
    for (HashSet<GridCell> cluster : map.keySet()) {
      GM_Polygon geom = null;
      try {
        geom = grid.creerGeomCluster(cluster);
      } catch (Exception e) {
        e.printStackTrace();
      }
      double classe = map.get(cluster);

      if (classe > 2) {
        jddCluster.add(new DefaultFeature(geom));
      }
    }

    setJddUrbanArea(jddCluster);
  }

}

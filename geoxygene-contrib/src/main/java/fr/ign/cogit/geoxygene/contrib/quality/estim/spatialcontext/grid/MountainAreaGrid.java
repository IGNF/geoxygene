package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat.GridCell;
import fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat.MountainGrid;
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
 *            A class to delineate mountain areas using contours (from Touya,
 *            2011)
 * 
 * @author JFGirres
 * 
 */
public class MountainAreaGrid extends AbstractGrid {

    private IFeatureCollection<Contour> jddContourIn;

    public void setJddContourIn(IFeatureCollection<Contour> jddContourIn) {
        this.jddContourIn = jddContourIn;
    }

    public IFeatureCollection<Contour> getJddContourIn() {
        return jddContourIn;
    }

    private IFeatureCollection<IFeature> jddMoutainArea;

    public void setJddMoutainArea(IFeatureCollection<IFeature> jddMoutainArea) {
        this.jddMoutainArea = jddMoutainArea;
    }

    public IFeatureCollection<IFeature> getJddMoutainArea() {
        return jddMoutainArea;
    }

    private IFeatureCollection<IFeature> jddFilterMoutainArea;

    public void setJddFilterMoutainArea(IFeatureCollection<IFeature> jddFilterMoutainArea) {
        this.jddFilterMoutainArea = jddFilterMoutainArea;
    }

    public IFeatureCollection<IFeature> getJddFilterMoutainArea() {
        return jddFilterMoutainArea;
    }

    private double seuilBasRatio;

    public void setSeuilBasRatio(double seuilBasRatio) {
        this.seuilBasRatio = seuilBasRatio;
    }

    public double getSeuilBasRatio() {
        return seuilBasRatio;
    }

    private double seuilHautRatio;

    public void setSeuilHautRatio(double seuilHautRatio) {
        this.seuilHautRatio = seuilHautRatio;
    }

    public double getSeuilHautRatio() {
        return seuilHautRatio;
    }

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

    public MountainAreaGrid(IFeatureCollection<Contour> jddContourIn) {
        super();
        this.setEnvelope(jddContourIn);
        this.setJddContourIn(jddContourIn);
    }

    /**
     * Create the grid using a ratio criteria
     */
    public void createArea() {
        MountainGrid grid = new MountainGrid(this.getTailleCellule(), this.getRayon(), this.getMinX(), this.getMaxX(),
                this.getMinY(), this.getMaxY(), 0, jddContourIn);
        try {
            grid.setCriteres(false, 0.0, 0.0, 0.0, true, 1, seuilBasRatio, seuilHautRatio);
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
        setJddMoutainArea(jddCluster);
    }

    /**
     * Filter the grid
     */
    @SuppressWarnings("unchecked")
    public void filterArea() {
        IFeatureCollection<IFeature> jddRawMoutain = getJddMoutainArea();
        IFeatureCollection<IFeature> jddFilterMoutain = new FT_FeatureCollection<IFeature>();

        for (IFeature ftFeature : jddRawMoutain) {
            // Boucher les trous et vire les polygones trop petits
            if (ftFeature.getGeom().isMultiSurface()) {
                IMultiSurface<IPolygon> multiMontagne = (IMultiSurface<IPolygon>) ftFeature.getGeom();
                for (IPolygon polygon : multiMontagne.getList()) {
                    if (polygon.area() > superficieMin) {
                        if (boucheTrou) {
                            polygon.getInterior().clear();
                        }
                        // vire les trous
                        DefaultFeature newFeature = new DefaultFeature();
                        newFeature.setGeom(polygon);
                        jddFilterMoutain.add(newFeature);
                    }
                }
            } else {
                if (ftFeature.getGeom().isPolygon() && ftFeature.getGeom().area() > superficieMin) {
                    IPolygon polygon = (IPolygon) ftFeature.getGeom();
                    if (boucheTrou) {
                        polygon.getInterior().clear();
                    }
                    DefaultFeature newFeature = new DefaultFeature();
                    newFeature.setGeom(polygon);
                    jddFilterMoutain.add(newFeature);
                }
            }
        }
        setJddFilterMoutainArea(jddFilterMoutain);
    }
}

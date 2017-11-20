package fr.ign.cogit.geoxygene.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CRSAuthorityFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.AbstractGeomFactory;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomengine.AbstractGeometryEngine;
import fr.ign.cogit.geoxygene.spatial.geomengine.GeometryEngine;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @version 0.1
 * 
 *          Class allowing to create rectangular grids
 *          and save them as shapefiles
 */

public class Grid {
    private ILineString extent;
    private IPolygon zone;
    private ILineString[][] tiles;
    private AbstractGeomFactory factory;
    private double meanLength = 0;
    private IDirectPosition ll;
    private double pasX = 0;
    private double pasY = 0;

    /**
     * Creates a rectangular grid of nbRows x nbCols
     * 
     * @param ll
     *            lowerleft coordinate
     * @param ur
     *            upper right coordinate
     * 
     */
    public Grid(int rows, int cols, IDirectPosition ll, IDirectPosition ur) {
        GeometryEngine.init();
        this.pasX = (ur.getX() - ll.getX()) / cols;
        this.pasY = (ur.getY() - ll.getY()) / rows;
        this.ll = ll;
        this.factory = AbstractGeometryEngine.getFactory();
        this.extent = this.constructLineString(ll, ur);
        this.tiles = new ILineString[rows][cols];
        this.zone = factory.createIPolygon(extent);

        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < cols; ++j) {
                IDirectPosition d1 = new DirectPosition(ll.getX() + j * pasX,
                        ll.getY() + i * pasY);
                IDirectPosition d2 = new DirectPosition(
                        ll.getX() + (j + 1) * pasX, ll.getY() + (i + 1) * pasY);
                tiles[i][j] = this.constructLineString(d1, d2);
            }
        }
        if (tiles.length > 0)
            this.meanLength = tiles[0][0].length() / 4;

    }

    // build a rectangular linestring from diagonal points
    private ILineString constructLineString(IDirectPosition a,
            IDirectPosition b) {
        List<IDirectPosition> listePoints = new ArrayList<>();
        listePoints.add(a);
        listePoints.add(new DirectPosition(b.getX(), a.getY()));
        listePoints.add(b);
        listePoints.add(new DirectPosition(a.getX(), b.getY()));
        listePoints.add(a);
        ILineString l = factory.createILineString(listePoints);
        return l;
    }

    /**
     * 
     * @return global extent as LineString
     */
    public String getExtent() {
        return extent.toString();
    }

    public ILineString[][] getTiles() {
        return tiles;
    }

    /**
     * 
     * @param i
     * @param j
     * @return extent of cell i x j
     */
    public String getTile(int i, int j) {
        return tiles[i][j].toString();
    }

    public int nbRows() {
        return this.tiles.length;
    }

    public int nbCols() {
        return this.tiles[0].length;
    }

    public String getBufferForTile(int i, int j) {
        IPolygon inter = (IPolygon) this.zone
                .intersection(tiles[i][j].buffer(meanLength));
        return inter.exteriorLineString().toString();
    }

    /**
     * 
     * @param i
     * @param j
     * @return lower left coordinate of tile (i,j)
     */
    public IDirectPosition getTileExtentLowerLeft(int i, int j) {
        IDirectPosition llTile = new DirectPosition(
                this.ll.getX() + j * this.pasX, this.ll.getY() + i * this.pasY);
        return llTile;
    }

    /**
     * 
     * @param i
     * @param j
     * @return upper right coordinate of tile (i,j)
     */
    public IDirectPosition getTileExtentUpperRight(int i, int j) {
        IDirectPosition urTile = new DirectPosition(
                this.ll.getX() + (j + 1) * this.pasX,
                this.ll.getY() + (i + 1) * this.pasY);
        return urTile;
    }

    public double xmin() {
        return zone.envelope().minX();
    }

    public double xmax() {
        return zone.envelope().maxX();
    }

    // epsg string corresponding to a coordinate ref system
    //
    /**
     * write a shapefile representing the grid in a coordinate ref system ex :
     * "EPSG:2975" -> RGR92, "EPSG:2154" -> L93, "EPSG:4326" -> WGS84
     * 
     * @param fileName
     * @param epsg
     */
    public void toShapeFile(String fileName, String epsg) {
        FT_FeatureCollection<IFeature> pop = new FT_FeatureCollection<>();
        System.out.println("writing..." + fileName);
        for (int i = 0; i < nbRows(); ++i)
            for (int j = 0; j < nbCols(); ++j)
                pop.add(new DefaultFeature(tiles[i][j]));

        CRSAuthorityFactory factory = CRS.getAuthorityFactory(true);
        CoordinateReferenceSystem crs = null;
        try {
            crs = factory.createCoordinateReferenceSystem(epsg);
        } catch (FactoryException e) {
            e.printStackTrace();
        }
        ShapefileWriter.write(pop, fileName, crs);
        System.out.println("writing done");
    }

    /**
     * 
     * @param fromLine
     * @param toLine
     * @param fromCol
     * @param toCol
     * @return a map associating "i_j" to the extent of tile(i,j) for tiles in
     *         [fromLine, toLine[ x [fromCol, toCol[
     */
    public Map<String, String> getIJextents(int fromLine, int toLine,
            int fromCol, int toCol) {
        final Map<String, String> extentsij = new HashMap<>();
        for (int i = fromLine; i < toLine; ++i)
            for (int j = fromCol; j < toCol; ++j) {
                extentsij.put("" + i + "_" + j, getTile(i, j));
            }
        return extentsij;
    }

    /**
     * 
     * @return a map associating "i_j" to the extent of tile(i,j) for all tiles
     */
    public Map<String, String> getIJextents() {
        final Map<String, String> extentsij = new HashMap<>();
        for (int i = 0; i < nbRows(); ++i)
            for (int j = 0; j < nbCols(); ++j) {
                extentsij.put("" + i + "_" + j, getTile(i, j));
            }
        return extentsij;
    }

    public static void main(String[] args) {

        /** Small extent **/
        IDirectPosition dmin = new DirectPosition(335000, 7640800);
        IDirectPosition dmax = new DirectPosition(340500, 7646000);
        /********************/
        /** Reunion entiere **/
        // IDirectPosition dmin = new DirectPosition(313800, 7633000);
        // IDirectPosition dmax = new DirectPosition(379500, 7691600);
        /********************/
        String fileName = "/home/imran/grid_reunion_totale_100_100.shp";
        String epsg = "EPSG:2975";
        Grid g = new Grid(20, 20, dmin, dmax);
        System.out.println(g.getExtent());
        System.out.println(g.getTile(0, 0));
        System.out.println(g.getBufferForTile(0, 0));
        // for (int i = 0; i < g.nbRows(); ++i)
        // for (int j = 0; j < g.nbCols(); ++j)
        // System.out.println(g.getTile(i, j));
        // g.toShapeFile(fileName, epsg);
        
        for (Map.Entry<String, String > ext : g.getIJextents().entrySet())
            System.out.println( ext.getKey() + " : " + ext.getValue());

    }

}

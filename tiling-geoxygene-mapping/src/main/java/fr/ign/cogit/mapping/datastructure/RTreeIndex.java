package fr.ign.cogit.mapping.datastructure;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.deegree.io.rtree.HyperBoundingBox;
import org.deegree.io.rtree.HyperPoint;
import org.deegree.io.rtree.RTree;
import org.deegree.io.rtree.RTreeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.IntersectionMatrix;
import com.vividsolutions.jts.io.ParseException;

import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.storage.NodeData;
import fr.ign.cogit.mapping.storage.NodeKey;
import fr.ign.cogit.mapping.util.FileUtil;
import fr.ign.cogit.mapping.util.NodeUtil;
import fr.ign.cogit.mapping.webentity.spatial.GeometryRecord;
import fr.ign.cogit.mapping.webentity.spatial.Operation;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndex;
import fr.ign.cogit.mapping.webentity.spatial.SpatialIndexException;

/*
 * Stratégie pour determinier le grand rectangle qui contient les données
 * d'une echelle
 * @author Dr Tsatcha D.
 */

public class RTreeIndex extends SpatialIndex {
    private static final Logger LOG = LoggerFactory.getLogger(RTreeIndex.class);
    private static final int MAX_NODE_LOAD = 50;
    private static final int DATA_DIMENSION = 2;
    /*
     * Information necessaires pour la gestion du rtree
     */
    // le fichier d'indexation
    private static final String INDEX_FILE_NAME = "spatial.idx";
    // la structure Rtree
    private RTree tree;
    // le chemin de depot du repertoire
    private String indexPath;
    private Object lock = new Object();

    /*
     * les coordonnées réelles de l'echelle
     */
    /*
     * @param le coin inférieur droite
     */
    protected HyperPoint bornInfScale = new HyperPoint(new double[] { 0, 0 });
    /*
     * @param bornSupScale designe le coin supérieure gauche
     */

    protected HyperPoint bornSupScale = new HyperPoint(new double[] { 0, 0 });

    /*
     * Designe le plus grand echelle utilisée pour les elements couramment
     * MaxRectangle
     */

    public HyperBoundingBox MaxRectangle = null;

    public HyperBoundingBox getMaxRectangle() {
        return MaxRectangle;
    }

    public void setMaxRectangle(HyperBoundingBox maxRectangleParam) {
        MaxRectangle = maxRectangleParam;
    }

    public RTreeIndex(String indexDir) {
        super(indexDir);
        this.indexPath = this.indexDir + INDEX_FILE_NAME;
    }

    // le rtree suivant est destiné pour le chargement
    // de chaque echelle...
    public RTreeIndex(String indexDir, String dir) {
        super(indexDir, dir);
        this.indexPath = this.indexDir + INDEX_FILE_NAME;
    }

    /*
     * Ce contructeur permet aussi d'identifier à chaque le plus grand rectangle
     * courant qui contient toutes les autres rectangles;
     */
    public RTreeIndex(String indexDir, String dir,
            HyperBoundingBox MaxRectangleParam) {
        super(indexDir, dir);
        this.indexPath = this.indexDir + INDEX_FILE_NAME;
        MaxRectangle = MaxRectangleParam;

    }

    @Override
    protected void indexClose() throws SpatialIndexException {
        try {
            synchronized (lock) {
                if (null != tree) {
                    tree.close();
                }
            }
        } catch (RTreeException e) {
            throw new SpatialIndexException(this, e);
        }
        tree = null;
    }

    @Override
    protected void indexOpen() throws SpatialIndexException {
        createTree();
    }

    protected void createTree() throws SpatialIndexException {
        try {
            File f = new File(indexPath);
            if (f.exists()) {
                tree = new RTree(indexPath);
            } else {
                tree = new RTree(DATA_DIMENSION, MAX_NODE_LOAD, indexPath);

            }
        } catch (RTreeException e) {
            throw new SpatialIndexException(this, e);
        }
    }

    @Override
    protected void indexDelete() {
        deleteTree();
    }

    protected void deleteTree() {
        File f = new File(this.indexPath);
        FileUtil.delete(f);
    }

    /** {@inheritDoc} */
    @Override
    protected long estimate(Geometry g, Operation operation) {
        try {
            synchronized (lock) {
                return tree.intersects(createBoundingBox(this, g)).length;
            }
        } catch (RTreeException e) {
            return Long.MAX_VALUE;
        }
    }

    @Override
    protected void indexClear() throws SpatialIndexException {
        try {
            synchronized (lock) {
                tree.close();
            }
        } catch (RTreeException e) {
            throw new SpatialIndexException(this, e);
        }
        deleteTree();
        createTree();
    }

    static HyperBoundingBox createBoundingBox(SpatialIndex index, Geometry g)
            throws SpatialIndexException {
        Geometry targetGeometry = g;
        // String code = (String) g.getUserData();
        // if (null == code) {
        // code = Constants.DEFAULT_CRS;
        // }
        // try {
        // CoordinateReferenceSystem crs = CRS.decode(code);
        // MathTransform transform = CRS.findMathTransform(crs,
        // DefaultGeographicCRS.WGS84);
        // targetGeometry = JTS.transform(g, transform);
        // } catch (MismatchedDimensionException e) {
        // throw new SpatialIndexException(index,
        // "Could not create bounding box", e);
        // } catch (NoSuchAuthorityCodeException e) {
        // throw new SpatialIndexException(index,
        // "Could not create bounding box", e);
        // } catch (FactoryException e) {
        // throw new SpatialIndexException(index,
        // "Could not create bounding box", e);
        // } catch (TransformException e) {
        // throw new SpatialIndexException(index,
        // "Could not create bounding box", e);
        // }

        Envelope e = targetGeometry.getEnvelopeInternal();
        HyperPoint min = new HyperPoint(
                new double[] { e.getMinX(), e.getMinY() });
        HyperPoint max = new HyperPoint(
                new double[] { e.getMaxX(), e.getMaxY() });
        return new HyperBoundingBox(min, max);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean indexAdd(Record<Geometry> r) throws SpatialIndexException {
        synchronized (lock) {
            boolean inserted = false;
            try {
                // String n = NodeUtil.getStringRepresentation(r.getKey());
                NodeKey key = new NodeKey(r.getKey().toString());
                NodeData data = nodes.get(key);
                int id = data.getId();
                HyperBoundingBox currentBox = createBoundingBox(this,
                        r.getValue());
                inserted = tree.insert(id, currentBox);

                // le deux rectangles se rencontrent ou bien sont distance
                // dans ce cas on construit une geometrie qui est leur reunion
                // et on prend l'enveloppe qui les recouvrent.
                /*
                 * 
                 * 
                 */

                // System.out.println("je suis executer-------"+MaxRectangle.toString());
                // System.out.println("je suis currentBox-------"+currentBox.toString());
                // permet de creer le premier Rectangle...
                if (MaxRectangle == null) {
                    MaxRectangle = currentBox;
                } else {
                    if (MaxRectangle.contains(currentBox)) {
                    //    System.out.println("je suis executer1-------");

                    } else {
                        if (currentBox.contains(MaxRectangle)) {
                            MaxRectangle = currentBox;
                      //      System.out.println("je suis executer2-------");

                        } else {
                            HyperBoundingBox SumBox = MaxRectangle
                                    .unionBoundingBox(currentBox);
                            MaxRectangle = SumBox;
                        //    System.out.println("je suis executer-------");

                        }

                    }
                }
            } catch (RTreeException e) {
                throw new SpatialIndexException(this, e);
            }

            return inserted;
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SpatialIndexException
     *             if an error occurs parsing the geometry from the index
     */
    @Override
    protected boolean indexRemove(Record<Geometry> r)
            throws SpatialIndexException {
        synchronized (lock) {

            // String n = NodeUtil.getStringRepresentation(r.getKey());
            NodeKey key = new NodeKey(r.getKey().toString());
            NodeData data = nodes.get(key);
            if (null == data) {
                LOG.error("Could not find id for: " + r.getKey() + " ("
                        + r.getKey() + ")");
                return false;
            }

            int id = data.getId();
            Geometry g;
            try {
                g = getGeometryRepresentation(data);
            } catch (ParseException e) {
                throw new SpatialIndexException(this, e);
            }
            if (!g.equals(r.getValue())) {
                LOG.error(
                        "The value for {} is different from the value to be deleted",
                        r.getKey());
                return false;
            }
            try {
                return tree.delete(createBoundingBox(this, g), id);
            } catch (RTreeException e) {
                throw new SpatialIndexException(this, e);
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws SpatialIndexException
     *             if an error occurs querying the index or parsing a geometry.
     */
    @Override
    public Iterator<Record<Geometry>> query(final Geometry value)
            throws SpatialIndexException {
        return iterator(value, Operation.SimpleFeatures.EQUALS);
    }

    /**
     * {@inheritDoc}
     *
     * @throws SpatialIndexException
     *             if an error occurs querying the index or parsing the geometry
     */
    @Override
    public Iterator<Record<Geometry>> iterator(final Geometry value,
            final Operation operation) throws SpatialIndexException {

        final Iterator<Record<Geometry>> records;

        boolean isIntersection = false;

        for (IntersectionMatrix m : operation.getIntersectionMatrices()) {
            if (m.isIntersects()) {
                isIntersection = true;
                break;
            }
        }

        if (isIntersection) {
            final Object[] objs;
            try {
                synchronized (lock) {
                    objs = tree.intersects(createBoundingBox(this, value));
                }
            } catch (RTreeException e) {
                throw new SpatialIndexException(this, e);
            }

            records = new RecordIterator(objs);
        } else {
            records = this.iterator();
            // TODO: Perform intersection and skip those ids
        }
        return new Operation.OperationIterator(records, value, operation);
    }

    private class RecordIterator implements ClosableIterator<Record<Geometry>> {

        private Object[] ids;
        private int pos;

        public RecordIterator(Object[] ids) {
            this.ids = ids;
            this.pos = 0;
        }

        @Override
        public boolean hasNext() {
            return pos < this.ids.length;
        }

        @Override
        public Record<Geometry> next() {
            int id = (Integer) ids[pos++];
            NodeData data = idNodes.get(id);
            // Node n = NodeUtil.getNodeRepresentation(data.getNode());
            Geometry g;
            try {
                g = getGeometryRepresentation(data);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            return GeometryRecord.create(NodeUtil.buildNode(data.getNode()), g);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() {
            ids = null;
        }

    }

    @Override
    public Record<Geometry> find(fr.ign.cogit.cartagen.graph.Node node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void register(Graph graph, fr.ign.cogit.cartagen.graph.Node graphName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregister(Graph graph,
            fr.ign.cogit.cartagen.graph.Node graphName) {
        // TODO Auto-generated method stub

    }

}
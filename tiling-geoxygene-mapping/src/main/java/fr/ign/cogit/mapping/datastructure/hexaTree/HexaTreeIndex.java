package fr.ign.cogit.mapping.datastructure.hexaTree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.deegree.io.rtree.HyperBoundingBox;
import org.deegree.io.rtree.RTree;
import org.deegree.io.rtree.RTreeException;
import org.geotools.referencing.operation.matrix.AffineTransform2D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

import fr.ign.cogit.cartagen.graph.Graph;
import fr.ign.cogit.cartagen.graph.Node;
import fr.ign.cogit.mapping.clients.ControllerI;
import fr.ign.cogit.mapping.clients.ControllerII;
import fr.ign.cogit.mapping.clients.Converter;
import fr.ign.cogit.mapping.clients.frameworks.CadrageArea;
import fr.ign.cogit.mapping.datastructure.RTreeIndex;
import fr.ign.cogit.mapping.datastructure.management.ManageRtreeMultiLevel;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
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
 * @author Dr Tsatcha D.
 */

public class HexaTreeIndex extends SpatialIndex {

    private static final Logger LOG = LoggerFactory
            .getLogger(HexaTreeIndex.class);
    private static final int MAX_NODE_LOAD = 50;
    private static final int DATA_DIMENSION = 2;

    // le fichier d'indexation
    private static final String INDEX_FILE_NAME = "spatial.idx";
    // la structure Rtree
    private HexaTree hexatree;
    // le chemin de depot du repertoire
    private String indexPath;
    private Object lock = new Object();

    public ManageRtreeMultiLevel getManager() {
        return manager;
    }

    public void setManager(ManageRtreeMultiLevel manager) {
        this.manager = manager;
    }

    /*
     * @param manager designe le manager
     */
    protected ManageRtreeMultiLevel manager;

    /*
     * @param convertisseur designe le convertisseur avec tous ces paramètres de
     * visualisation....
     */
    protected Converter convertisseur;

    // un hexaTree est destiné pour le chargement et stockage
    // de chaque echelle...du modèle...
    public HexaTreeIndex(String indexDir, String dir) {
        super(indexDir, dir);
        this.indexPath = this.indexDir + INDEX_FILE_NAME;
    }

    /*
     * Initialisation d'un hexaTree index basé sur du hexatree et le
     * convertiseur et les differentes echelles
     */
    public HexaTreeIndex(String indexDir, String dir,
            Converter convertisseurParam, ManageRtreeMultiLevel managerParam) {
        super(indexDir, dir);
        this.indexPath = this.indexDir + INDEX_FILE_NAME;
        manager = managerParam;
        convertisseur = convertisseurParam;
    }
    
    /*
     * utiliser l'indexation de l'hexatree au niveau
     * du client
     */
    
    public HexaTreeIndex(String indexDir, String dir, String subDir,
            Converter convertisseurParam, ManageRtreeMultiLevel managerParam) {
        super(indexDir, dir, subDir);
        this.indexPath = this.indexDir + INDEX_FILE_NAME;
        manager = managerParam;
        convertisseur = convertisseurParam;
    }
    
    
    /*
     * utiliser pour l'indexation au niveau du client..
     */
    
    public HexaTreeIndex(String indexDir, String dir, String subDir) {
        super(indexDir, dir, subDir);
        this.indexPath = this.indexDir + INDEX_FILE_NAME;
       
    }
    
  

    @Override
    public Record<Geometry> find(Node node) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Iterator<Record<Geometry>> query(Geometry value) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void register(Graph graph, Node graphName) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregister(Graph graph, Node graphName) {
        // TODO Auto-generated method stub

    }

    @Override
    public Iterator<Record<Geometry>> iterator(Geometry geometry,
            Operation operation) throws SpatialIndexException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected long estimate(Geometry geometry, Operation operation)
            throws SpatialIndexException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    protected void indexOpen() throws SpatialIndexException {
        createHexaTree();
    }

    /*
     * creation de l'hexatree... Si le fichier de stockage n'hesite pas on la
     * crèe sinon on charge tous les données dans la structure de données.
     */
    protected void createHexaTree() throws SpatialIndexException {
        try {

            hexatree = new HexaTree(manager, convertisseur, indexPath);

           } catch (HexaTreeException e) {
            throw new SpatialIndexException(this, e);
        }
    }

    public HexaTree getHexatree() {
        return hexatree;
    }

    public void setHexatree(HexaTree hexatree) {
        this.hexatree = hexatree;
    }

    public Converter getConvertisseur() {
        return convertisseur;
    }

    public void setConvertisseur(Converter convertisseur) {
        this.convertisseur = convertisseur;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.mapping.webentity.spatial.SpatialIndex#indexDelete()
     * 
     * s
     */
    @Override
    protected void indexDelete() {
        deleteTree();
    }

    /*
     * on detruit le fichier de stockage de l'index...
     */

    protected void deleteTree() {
        File f = new File(this.indexPath);
        FileUtil.delete(f);
    }

    @Override
    protected void indexClear() throws SpatialIndexException {
        try {
            synchronized (lock) {

                // to do trre
                hexatree.close();
            }
        } catch (HexaTreeException e) {
            throw new SpatialIndexException(this, e);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        deleteTree();
        createHexaTree();
    }

    @Override
    protected boolean indexAdd(Record<Geometry> r) throws SpatialIndexException {
        synchronized (lock) {
            boolean inserted = false;

            // String n = NodeUtil.getStringRepresentation(r.getKey());
            NodeKey key = new NodeKey(r.getKey().toString());
            NodeData data = nodes.get(key);
            int id = data.getId();

            inserted = hexatree.insert(r, r.getKey().getScale(), id);

            return inserted;

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.mapping.webentity.spatial.SpatialIndex#indexClose()
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
                // /*************************to do....
                return hexatree.delete(g, r.getKey().getScale(), id);
            } catch (HexaTreeException e) {
                throw new SpatialIndexException(this, e);
            }
        }
    }

  
    @Override
    protected void indexClose() throws SpatialIndexException {
        // TODO Auto-generated method stub

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
    
    /*
     * Un deplacement peut entrainer 
     * les paramètres du convertisseur ainsi pour garder 
     * la conformité du cadrage et les contenus des differentes 
     * echelles...
     * Il faut reograniser l'indexation de tel sorte
     * que les elements dans l'index aient les configuration
     * du nouveau convertisseur...
     */
    
   
}

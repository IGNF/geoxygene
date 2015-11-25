package fr.ign.cogit.mapping.webentity.spatial;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import com.hp.hpl.jena.util.iterator.ClosableIterator;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.ClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.collections.StoredSortedMap;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.EnvironmentLockedException;
import com.sleepycat.je.SecondaryConfig;
import com.sleepycat.je.SecondaryDatabase;
import com.sleepycat.je.Transaction;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKBReader;
import com.vividsolutions.jts.io.WKBWriter;

import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.generalindex.IndexBase;
import fr.ign.cogit.mapping.generalindex.IndexException;
import fr.ign.cogit.mapping.generalindex.QueryCache;
import fr.ign.cogit.mapping.generalindex.QueryableIndex;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.generalindex.RecordFactory;
import fr.ign.cogit.mapping.storage.NodeData;
import fr.ign.cogit.mapping.storage.NodeIdKeyCreator;
import fr.ign.cogit.mapping.storage.NodeKey;
import fr.ign.cogit.mapping.util.FileUtil;
import fr.ign.cogit.mapping.util.NodeUtil;

/** @author Dtsatcha */
public abstract class SpatialIndex extends IndexBase<Geometry> implements
        QueryableIndex<Geometry> {

    private static final String ID_NODE_INDEX = "id_node_index";
    private static final String DATA_DB = "data";
    private static final String CATALOG_DB = "catalog";

    private static ThreadLocal<WKBWriter> WKB_WRITER = new ThreadLocal<WKBWriter>() {
        @Override
        protected WKBWriter initialValue() {
            return new WKBWriter();
        }
    };

    private static ThreadLocal<WKBReader> WKB_READER = new ThreadLocal<WKBReader>() {

        @Override
        protected WKBReader initialValue() {
            return new WKBReader(SpatialGeometryFactory.GEOMETRY_FACTORY);
        }

    };

    private static final AtomicInteger INDEX_COUNTER = new AtomicInteger(0);

    /**
     * Get the <code>Geometry</code> give the well known binary representation.
     *
     * @param data
     *            the indexed data.
     * @return a <code>Geometry</code>.
     * @throws ParseException
     *             if an error occurs parsing the data.
     */
    protected static final Geometry getGeometryRepresentation(NodeData data)
            throws ParseException {
        byte[] wkb = data.getExtent();
        Geometry g = WKB_READER.get().read(wkb);
        g.setUserData(data.getCRSCode());
        return g;
    }

    // paramètres de la base de données
    private ClassCatalog catalog;
    private Environment env;
    private Database db;
    private SecondaryDatabase nodeDb;
    private DatabaseConfig dbConfig;
    private SecondaryConfig secConfig;

    private AtomicInteger idCounter;

    private Object lock = new Object();
    private boolean bulkLoading = false;
    private boolean hasChanged = false;

    protected ThreadLocal<QueryCache<Geometry>> cache;

    protected StoredSortedMap<NodeKey, NodeData> nodes;
    protected StoredSortedMap<Integer, NodeData> idNodes;
    protected Properties configuration;

    protected String indexDir;

    protected long size;

    protected Profile profile;

    // intialisation de l'index en utilisant des configurations
    public SpatialIndex(Profile profile, Properties configuration,
            String indexDir) {
        this.profile = profile;

        this.cache = new ThreadLocal<QueryCache<Geometry>>() {
            @Override
            protected QueryCache<Geometry> initialValue() {
                return new QueryCache<>(Constants.QUERY_CACHE_SIZE);
            }
        };
        // chemin du fichier de stockage des informations necessaires
        // à l'indexation spatiales
        this.indexDir = indexDir + File.separatorChar + "spatialIndex"
                + File.separatorChar;
        this.idCounter = new AtomicInteger();

        this.dbConfig = new DatabaseConfig();
        this.dbConfig.setTransactional(true);
        this.dbConfig.setAllowCreate(true);

        this.secConfig = new SecondaryConfig();
        this.secConfig.setTransactional(true);
        this.secConfig.setAllowCreate(true);
        this.secConfig.setSortedDuplicates(false);
    }

    // indexation en indiquant uniquement le chemin de l'index
    // elle se base sur le projet sleepcat
    public SpatialIndex(String indexDir) {
        this.cache = new ThreadLocal<QueryCache<Geometry>>() {
            @Override
            protected QueryCache<Geometry> initialValue() {
                return new QueryCache<>(Constants.QUERY_CACHE_SIZE);
            }
        };
        // chemin du repertoire de stockage ...
        this.indexDir = indexDir + File.separatorChar + "spatialIndex"
                + File.separatorChar;
        this.idCounter = new AtomicInteger();

        this.dbConfig = new DatabaseConfig();
        this.dbConfig.setTransactional(true);
        this.dbConfig.setAllowCreate(true);

        this.secConfig = new SecondaryConfig();
        this.secConfig.setTransactional(true);
        this.secConfig.setAllowCreate(true);
        this.secConfig.setSortedDuplicates(false);
    }

    /*
     * Ce nouveau constructeur permet de fabriquer plusieurs index dans le
     * repertoire indexDir... il permet ains de gerer le multi-echelles
     */

    public SpatialIndex(String indexDir, String dir) {
        this.cache = new ThreadLocal<QueryCache<Geometry>>() {
            @Override
            protected QueryCache<Geometry> initialValue() {
                return new QueryCache<>(Constants.QUERY_CACHE_SIZE);
            }
        };
        // chemin du repertoire de stockage ...
        this.indexDir = indexDir + File.separatorChar + "spatialIndex"
                + File.separatorChar + dir + File.separatorChar;
        this.idCounter = new AtomicInteger();

        this.dbConfig = new DatabaseConfig();
        this.dbConfig.setTransactional(true);
        this.dbConfig.setAllowCreate(true);

        this.secConfig = new SecondaryConfig();
        this.secConfig.setTransactional(true);
        this.secConfig.setAllowCreate(true);
        this.secConfig.setSortedDuplicates(false);
    }
    
    
    public SpatialIndex(String indexDir, String dir, String subDir) {
        this.cache = new ThreadLocal<QueryCache<Geometry>>() {
            @Override
            protected QueryCache<Geometry> initialValue() {
                return new QueryCache<>(Constants.QUERY_CACHE_SIZE);
            }
        };
        // chemin du repertoire de stockage ...
        this.indexDir = indexDir + File.separatorChar + "spatialIndex"
                + File.separatorChar + dir + File.separatorChar + subDir + File.separatorChar;
        this.idCounter = new AtomicInteger();

        this.dbConfig = new DatabaseConfig();
        this.dbConfig.setTransactional(true);
        this.dbConfig.setAllowCreate(true);

        this.secConfig = new SecondaryConfig();
        this.secConfig.setTransactional(true);
        this.secConfig.setAllowCreate(true);
        this.secConfig.setSortedDuplicates(false);
    }

    // retourne le chemin de stockage de l'index
    public String getIndexDir() {
        return indexDir;
    }

    public void setIndexDir(String indexDir) {
        this.indexDir = indexDir;
    }

    // paramètres de cache de l'index utilisé
    /** {@inheritDoc} */
    @Override
    public final QueryCache<Geometry> getQueryCache() {
        return cache.get();
    }

    // initialisation de l'environnement necessaire à la base de
    // données utilisée pour l'indexation
    private void initializeEnvironment() throws SpatialIndexException {
        File f = new File(indexDir);
        f.mkdirs();

        try {
            EnvironmentConfig envConfig = new EnvironmentConfig();
            envConfig.setTransactional(true);
            envConfig.setAllowCreate(true);
            // envConfig.setCacheMode(CacheMode.DEFAULT);
            envConfig.setCachePercent(20);
            envConfig.setSharedCache(true);
            envConfig.setConfigParam(
                    EnvironmentConfig.CHECKPOINTER_BYTES_INTERVAL, "40000000"); // 40M
            envConfig
                    .setConfigParam(EnvironmentConfig.LOG_FILE_MAX, "10000000"); // 10M
            envConfig.setConfigParam(EnvironmentConfig.CLEANER_EXPUNGE, "true");

            env = new Environment(f, envConfig);

            openDB();

        } catch (EnvironmentLockedException e) {
            throw new SpatialIndexException(this,
                    "Error while initializing environment", e);
        } catch (DatabaseException e) {
            throw new SpatialIndexException(this,
                    "Error while initializaing maps", e);
        }
    }

    // decoupage de la carte dans la base de données suivant
    // l'index utilisé
    private void createMaps() {
        EntryBinding<NodeKey> nodeKeyBinding = new SerialBinding<>(catalog,
                NodeKey.class);
        EntryBinding<NodeData> nodeDataBinding = new SerialBinding<>(catalog,
                NodeData.class);
        EntryBinding<Integer> nodeBinding = new SerialBinding<>(catalog,
                Integer.class);

        nodes = new StoredSortedMap<>(db, nodeKeyBinding, nodeDataBinding, true);
        idNodes = new StoredSortedMap<>(nodeDb, nodeBinding, nodeDataBinding,
                true);

        Integer maxInt = idNodes.lastKey();
        if (null == maxInt) {
            maxInt = 0;
        } else {
            maxInt = maxInt + 1;
        }
        idCounter.set(maxInt);

        size = db.count();
    }

    // fermeture de la base de données deployée
    private void closeDB() throws SpatialIndexException {
        try {
            if (nodeDb != null) {
                nodeDb.close();
            }
            if (db != null) {
                db.close();
            }
            if (catalog != null) {
                catalog.close();
            }
        } catch (DatabaseException e) {
            throw new SpatialIndexException(this, "Error closing databases", e);
        }
    }

    // fermeture des environnements appropriés à la creatoion de la base de
    // données
    private void closeEnvironment() {
        if (env != null) {
            try {
                env.cleanLog();
                env.close();
            } catch (DatabaseException e) {
                throw new SpatialIndexException(this,
                        "Error closing environment", e);
            }
        }
    }

    // ouverture de la base de données
    private void openDB() throws SpatialIndexException {
        try {
            Transaction t = env.beginTransaction(null, null);
            Database catalogDb = env.openDatabase(t, CATALOG_DB, dbConfig);
            catalog = new StoredClassCatalog(catalogDb);

            db = env.openDatabase(t, DATA_DB, dbConfig);

            secConfig.setKeyCreator(new NodeIdKeyCreator(catalog));
            nodeDb = env.openSecondaryDatabase(t, ID_NODE_INDEX, db, secConfig);
            t.commit();
        } catch (Exception e) {
            throw new SpatialIndexException(this, "Error opening databases", e);
        }
        createMaps();
    }

    // fermeture de la base de données avec tous les autres environnements

    /** {@inheritDoc} */
    @Override
    public final void doClose() throws IndexException {
        synchronized (lock) {
            indexClose();
            closeDB();
            closeEnvironment();
        }
    }

    // ouverture de la base de données indexée
    /**
     * {@inheritDoc} This calls {@link SpatialIndex#indexOpen()} to open any
     * resources held by the concrete implementation.
     */
    @Override
    public final void doOpen() throws IndexException {
        synchronized (lock) {
            initializeEnvironment();
            indexOpen();
        }
    }

    // suprression des ressources de la base de données indexées
    /**
     * {@inheritDoc} This calls {@link SpatialIndex#indexDelete()} to delete any
     * resources held by the concrete implementation.
     */
    @Override
    public final void doDelete() throws IndexException {
        synchronized (lock) {
            indexDelete();
            FileUtil.delete(new File(indexDir));
        }
    }

    /**
     * {@inheritDoc} This calls {@link SpatialIndex#indexClose()} to close any
     * resources held by the concrete implementation.
     */
    @Override
    public final void doClear() throws IndexException {
        synchronized (lock) {
            indexClear();
            closeDB();
            env.truncateDatabase(null, ID_NODE_INDEX, false);
            env.truncateDatabase(null, DATA_DB, false);
            openDB();
            idCounter.set(0);
            hasChanged = true;
        }
    }

    /**
     * {@inheritDoc} This calls {@link SpatialIndex#indexAdd(Record)} to
     * actually add the record to the spatial index.
     *
     * @throws SpatialIndexException
     *             if an error occurs parsing a geometry that already exists in
     *             the index for the given key.
     */
    @Override
    protected final boolean doAdd(Record<Geometry> r)
            throws SpatialIndexException {
        return doAdd(r, null);
    }

    private final boolean doAdd(Record<Geometry> r, Transaction t) {
        synchronized (lock) {
            // String n = NodeUtil.getStringRepresentation(r.getKey());
            NodeKey key = new NodeKey(r.getKey().toString());
            NodeData data = nodes.get(key);

            Geometry previous = null;

            int id = 0;
            if (null != data) {
                id = data.getId();
                try {
                    previous = getGeometryRepresentation(data);
                } catch (ParseException e) {
                    throw new SpatialIndexException(this, e);
                }

                if (r.getValue().equals(previous)) {
                    return false;
                }
            } else {
                id = idCounter.getAndIncrement();
            }
            byte[] extent = WKB_WRITER.get().write(r.getValue());
            data = new NodeData(id, r.getKey().toString(), extent, (String) r
                    .getValue().getUserData());

            nodes.put(key, data);
            hasChanged = true;
            if (null != previous && !previous.equals(r.getValue())) {
                return indexUpdate(r,
                        GeometryRecord.create(r.getKey(), previous));
            }
            boolean add = indexAdd(r);
            if (add) {
                size++;
            }
            return add;
        }
    }

    /**
     * Update a record.
     *
     * @param r
     *            the record to update.
     * @param previous
     *            the previous data.
     * @return true if the data was updated.
     * @throws SpatialIndexException
     *             if an error occurs.
     */
    protected boolean indexUpdate(Record<Geometry> r, Record<Geometry> previous)
            throws SpatialIndexException {
        synchronized (lock) {
            boolean remove = indexRemove(previous);
            boolean add = indexAdd(r);
            return remove && add;
        }
    }

    /**
     * {@inheritDoc} This calls {@link SpatialIndex#indexRemove(Record)} to
     * actually remove the record from the spatial index.
     */
    @Override
    protected final boolean doRemove(Record<Geometry> r) {
        synchronized (lock) {
            boolean removed = indexRemove(r);
            if (removed) {
                // String n = NodeUtil.getStringRepresentation(r.getKey());
                NodeKey key = new NodeKey(r.getKey().toString());
                NodeData value = nodes.remove(key);
                if (null == value) {
                    return false;
                }
                size--;
                hasChanged = true;
                return true;
            }
            return false;
        }
    }

    protected boolean isBulkLoading() {
        return bulkLoading;
    }

    protected boolean hasChanged() {
        return hasChanged;
    }

    /** {@inheritDoc} */
    @Override
    protected void doAdd(Iterator<Record<Geometry>> records)
            throws IndexException {
        synchronized (lock) {
            bulkLoading = true;
            while (records.hasNext()) {
                doAdd(records.next());
            }
            bulkLoading = false;
        }
    }

    /** {@inheritDoc} */
    @Override
    protected void doRemove(Iterator<Record<Geometry>> records)
            throws IndexException {
        synchronized (lock) {
            while (records.hasNext()) {
                doRemove(records.next());
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public final long doSize() throws IndexException {
        return size;
    }

    /** {@inheritDoc} */
    @Override
    public final RecordFactory<Geometry> getRecordFactory() {
        return profile.getRecordFactory();
    }

    /**
     * Return all records that intersect with the given geometry. This is a full
     * intersection test, not just the bounding box.
     *
     * @param geometry
     *            the geometry to test
     * @return all records that intersect with the geometry.
     */
    public abstract Iterator<Record<Geometry>> iterator(Geometry geometry,
            Operation operation) throws SpatialIndexException;

    protected abstract long estimate(Geometry geometry, Operation operation)
            throws SpatialIndexException;

    /**
     * Open this instance.
     *
     * @throws SpatialIndexException
     *             if an error occurs while opening
     */
    protected abstract void indexOpen() throws SpatialIndexException;

    /**
     * Delete any resources handled by this instance.
     *
     * @throws SpatialIndexException
     *             if an error occurs while deleting.
     */
    protected abstract void indexDelete() throws SpatialIndexException;

    /**
     * Clear all records.
     *
     * @throws SpatialIndexException
     *             if an error occurs while clearing.
     */
    protected abstract void indexClear() throws SpatialIndexException;

    /**
     * Close this instance.
     *
     * @throws SpatialIndexException
     *             if an error occurs while clearing.
     */
    protected abstract void indexClose() throws SpatialIndexException;

    /**
     * Add a record to the index. If a previous record exists, delete it.
     *
     * @param r
     *            a record to add.
     * @return <code>true</code> if the record is added; otherwise
     *         <code>false</code>.
     * @throws SpatialIndexException
     *             if an error occurs while adding the record.
     */
    protected abstract boolean indexAdd(Record<Geometry> r)
            throws SpatialIndexException;

    /**
     * Remove the record from the index.
     *
     * @param r
     *            a record to delete.
     * @return <code>true</code> if the record is removed; otherwise
     *         <code>false</code>.
     * @throws SpatialIndexException
     *             if an error occurs while removing the record.
     */
    protected abstract boolean indexRemove(Record<Geometry> r)
            throws SpatialIndexException;

    /** {@inheritDoc} */
    @Override
    public void flush() throws IndexException {
        synchronized (lock) {
            env.cleanLog();
            env.sync();
            hasChanged = false;
        }
    }

    /*
     * Ajouter ke 09/10/2015..; Cette methode permet de rechercher une geometrie
     * connaissant son node de representation...;
     * 
     * @param node designe le noeud
     * 
     * @return retour un enregistrement de ce noeud
     */

    public final Record<Geometry> find(Node node) {
        String n = node.toString();
        NodeKey key = new NodeKey(n);
        NodeData data = null;
        Geometry g = null;

        data = nodes.get(key);

        if (null == data) {
            return null;
        }

        try {
            g = getGeometryRepresentation(data);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (g != null) {
            return GeometryRecord.create(node, g);
        } else {
            return null;
        }

    }

    /** {@inheritDoc} */
    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.mapping.generalindex.IndexBase#doIterator() Cette
     * methode permet de fabriquee un iterateur à l'index contenu dans la base
     * d'index..
     */
    @Override
    public final Iterator<Record<Geometry>> doIterator() {

        final Iterator<NodeData> iter = nodes.values().iterator();
        final SpatialIndex index = this;
        ClosableIterator<Record<Geometry>> it = new ClosableIterator<Record<Geometry>>() {

            @Override
            public void remove() {

            }

            @Override
            public Record<Geometry> next() {
                NodeData data = iter.next();
                
                System.out.println("le noeud imprimé"+data.getNode());
                Node n = NodeUtil.buildNode(data.getNode());
                Geometry g;
                try {
                    g = getGeometryRepresentation(data);
                } catch (ParseException e) {
                    throw new SpatialIndexException(index, e);
                }
                GeometryRecord r = GeometryRecord.create(n, g);
                return r;
            }

            @Override
            public boolean hasNext() {
                return iter.hasNext();
            }

            @Override
            public void close() {
                // iter is a BlockIterator and does not need closing
            }
        };
        return it;
    }

}

package fr.ign.cogit.mapping.storage.database.extractor.sql;



import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * @author Dr Dieudonne Tsatcha 
 *
 * Cette classe est utilisée pour gerer les differentes phases
 * de connexion pour des bases de données sql.
 */


public class PersistentStore {
	private static Logger LOG = LoggerFactory.getLogger(PersistentStore.class);

	private static class PersistentStoreHolder {
		public static final PersistentStore INSTANCE = new PersistentStore();
	}

	public static PersistentStore getInstance() {
		return PersistentStoreHolder.INSTANCE;
	}

	private DataSource ds;

	private ConnectionFactory connFactory;

	private PoolableConnectionFactory poolableFactory;

	private ObjectPool connectionPool;

	private boolean closed;

	private boolean initialized;

	private Object lock = new Object();

	public PersistentStore() {
		initialized = false;
		closed = true;
	}

//	public void initialize(String jdbcUrl, String userName, String password,
//		Properties p) {
//		synchronized (lock) {
//
//			if (initialized) {
//				return;
//			}
//			GenericObjectPool.Config config = new GenericObjectPool.Config();
//
//			config.maxActive = Integer.parseInt(p.getProperty("maxActive", "30"));
//			config.maxIdle = Integer.parseInt(p.getProperty("maxIdle", "5"));
//			config.maxWait = Integer.parseInt(p.getProperty("maxWait", "20000"));
//			config.testOnReturn = Boolean.parseBoolean(p
//				.getProperty("testOnReturn", Boolean.FALSE.toString()));
//			config.testOnBorrow = Boolean.parseBoolean(p
//				.getProperty("testOnBorrow", Boolean.FALSE.toString()));
//			config.testWhileIdle = Boolean.parseBoolean(p
//				.getProperty("testWhileIdle", Boolean.TRUE.toString()));
//			config.timeBetweenEvictionRunsMillis = Integer.parseInt(p
//				.getProperty("timeBetweenEvictionRunsMillis", "500"));
//			config.minEvictableIdleTimeMillis = Integer.parseInt(p
//				.getProperty("minEvictableIdleTimeMillis", "20000"));
//			config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
//
//			connectionPool = new GenericObjectPool(null, config);
//			connFactory = new DriverManagerConnectionFactory(jdbcUrl, userName,
//				password);
//			poolableFactory = new PoolableConnectionFactory(connFactory,
//				connectionPool,
//				null, null, false,
//				true);
//
//			ds = new PoolingDataSource(connectionPool);
//			closed = false;
//			initialized = true;
//		}
//	}
	
	/*
	 * Cette methode intialise le driver de connexion et les paramètres
	 * de connexion.
	 * Ces paramètres de connexions peuvent etre renseignés
	 * dans un fichier et qui seront indiqués dans le fichier comme une Map 
	 *  maxActive", "30"
	 *  maxIdle", "5"
	 *  maxWait", "20000
	 *  minEvictableIdleTimeMillis", "20000"
	 *  etc.
	 *  Ces informations sont par la suite extraites par l'expression:
	 *  p.getProperty("maxIdle", "5") ou p est de type Properties
	 *  
	 */
	
	public void initializeD(String jdbcUrl, String userName, String password
                ) {
                synchronized (lock) {

                        if (initialized) {
                                return;
                        }
                        GenericObjectPool.Config config = new GenericObjectPool.Config();

                        config.maxActive =30; 
                        config.maxIdle =5;
                        config.maxWait =20000; 
                        config.testOnReturn = false;
                        config.testOnBorrow = false ; 
                        config.testWhileIdle = true; 
                        config.timeBetweenEvictionRunsMillis =500; 
                        config.minEvictableIdleTimeMillis = 20000;
                        config.whenExhaustedAction = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;

                        connectionPool = new GenericObjectPool(null, config);
                        connFactory = new DriverManagerConnectionFactory(jdbcUrl, userName,
                                password);
                        poolableFactory = new PoolableConnectionFactory(connFactory,
                                connectionPool,
                                null, null, false,
                                true);

                        ds = new PoolingDataSource(connectionPool);
                        closed = false;
                        initialized = true;
                }
        }


	public Connection getConnection() throws PersistentStoreException {
		Connection con = null;
		try {
			con = ds.getConnection();
		} catch (SQLException e) {
			LOG.error("Could not get a connection", e);
			LOG.error("{} active, {} idle", getNumActive(), getNumIdle());
			throw new PersistentStoreException("Could not get a connection", e);
		}
		return con;
	}

	public int getNumActive() {
		return poolableFactory.getPool().getNumActive();
	}

	public int getNumIdle() {
		return poolableFactory.getPool().getNumIdle();
	}

	public void close() {
		synchronized (lock) {
			if (closed) {
				return;
			}
			try {
				LOG.debug("{} active, {} idle", getNumActive(), getNumIdle());
				poolableFactory.getPool().close();
				closed = true;
			} catch (Exception e) {
				LOG.error("Exception: ", e);
			}
		}
	}

	/** Ferme la connexion donnée s'il n'est pas nulle. */
	public static void close(Connection s) {
		if (s == null) {
			return;
		}
		try {
			if (!s.isClosed()) {
				s.close();
			}
		} catch (SQLException e) {
			LOG.error("Error while closing connection", e);
		}
	}
}

package fr.ign.cogit.mapping.webentity.spatial;
/*
 * Dr Tsatcha D.
 */

public class Constants {
    /**
     * Size of query cache.
     */
    public static final int QUERY_CACHE_SIZE = 100;

    public static final String INDEX_PREFIX = "spatial";
    // configuration properties

    /*
     * Properties of postgres connexion
     */
    public static final String DBTYPE = "postgis";
    public static final String HOST = "localhost";
    public static final String PORT = "5432";
    public static final String DATABASE = "test";
    public static final String SCHEMA = "public";
    public static final String USERNAME = "postgres";
    public static final String PASSWORD = "bageaco81";
    public static final String CHARSET = "UTF-8";
    public static final String JDBCURL = "jdbc:postgresql://localhost:5432/test";

    public static final String JDBC_URL = "jdbcUrl";
    public static final String INDEX_DIRECTORY = "indexDirectory";
    public static final String GEOSPARQL_ENABLED = "geoSPARQL";
    public static final String GEOMETRY_INDEX_TYPE = "indexType";
    public static final String GEOMETRY_INDEX_JTS = "JTS";
    public static final String GEOMETRY_INDEX_POSTGRESQL = "PostgreSQL";
    public static final String GEOMETRY_INDEX_RTREE = "RTree";

    /**
     * Spatial Reference ID for WGS84
     */
    public static final int WGS84_SRID = 4326;
    public static final int DEFAULT_SRID = 0;
    public static final String DEFAULT_CRS = "CRS:84";

    /**
     * Internal coordinate reference system code. All geometries are represented
     * in this CRS.
     */
    // public static final String INTERNAL_CRS = "EPSG:4326";
    public static final String INTERNAL_CRS = "CRS:84";

    public static final String SPATIAL_FUNCTION_NS = "http://parliament.semwebcentral.org/spatial/pfunction#";
}

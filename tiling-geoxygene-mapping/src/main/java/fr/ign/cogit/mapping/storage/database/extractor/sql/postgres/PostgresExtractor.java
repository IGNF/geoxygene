package fr.ign.cogit.mapping.storage.database.extractor.sql.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.geotools.coverage.processing.operation.Scale;
import org.postgis.PGgeometry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vividsolutions.jts.geom.Geometry;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.jdbc.postgis.PostgisReader;
import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.generalindex.Record;
import fr.ign.cogit.mapping.storage.database.extractor.sql.PersistentStore;
import fr.ign.cogit.mapping.storage.database.extractor.sql.PersistentStoreException;
import fr.ign.cogit.mapping.webentity.spatial.Constants;

/*
 * @author Dr Dieudonné
 * Cette classe permet de recuperer les données dans une base de données
 * postgres..et les charger au niveau de l'index multi-echelle via le biais
 * du manager...Enfin que la connexion soit faite il faut definir les propriétés
 * de votre base de données dans le fichier de constant...
 * @author Dr Tsatcha D.
 * @see Constants
 * @see InteratorIndexForTable pour fabriquer un iterateur d'index d'une table
 * de la base de données
 */
public class PostgresExtractor {

    private static Logger LOG = LoggerFactory.getLogger(PersistentStore.class);

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            LOG.error("Could not load Postgres driver");
        }
    }

    /*
     * Differentes informations necessaires pour la connexion dans la base de
     * données postgres
     */

    protected Connection conn = null;
    // recuperation des elements de configuration
    protected Properties configuration;
    protected PersistentStore store;
    protected Map<String, String> databaseInfo;
    // contenu d'une table de la base de données
    protected IPopulation<IFeature> tableContains = null;;
    // recupère toutes les contenus de la base de données
    Map<String, IPopulation<IFeature>> allDatabase;
    // resultat de la requete

    ResultSet rs = null;

    // public static final String SPATIAL_TABLE = "spatial";
    public static final String GEOMETRY_COLUMN = "geometrie";

    /**
     * 
     */

    /*
     * @param table
     * 
     * @return tableContains;
     */

    // ce constructeur recupère toutes les informations des tables
    // de la base de données renseignées en parametre // etendre à toutes
    // les tables de la base de données...
    public PostgresExtractor() {

        // realisation d'une connexion persistante
        store = new PersistentStore();
        store.initializeD(Constants.JDBCURL, Constants.USERNAME,
                Constants.PASSWORD);
        try {
            conn = store.getConnection();
        } catch (PersistentStoreException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

    }

    // recuperation de toutes les informations de la table
    /*
     * permet de retourner toutes les informations des elements de la table qui
     * sont dans la base de données renseignées au niveau du Constants.JDBCURL
     * et initialisé par le constructeur de la classe
     */
    public Map<Integer, Map<String, String>> readAllTables() {

        Map<Integer, Map<String, String>> resultat = null;
        // System.out.println("hellooooo");

        try {
            // recupere les objets de la table geometrique
            Statement stm = conn.createStatement();
            ResultSet rs = stm
                    .executeQuery(" SELECT F_TABLE_SCHEMA as schema, F_TABLE_NAME as table, TYPE as type, "
                            + "            F_GEOMETRY_COLUMN as geometry_colum, SRID as srid "
                            + " FROM GEOMETRY_COLUMNS" + " ORDER BY 1, 2 ");

            int cpt = 0;

            resultat = new HashMap<Integer, Map<String, String>>();
            // Parcours des objets de la table geometrique
            while (rs.next()) {
                // recupere nom table geometrique
                Map<String, String> row = new HashMap<String, String>();
                row.put("schema", rs.getString("schema"));
                row.put("table", rs.getString("table"));
                row.put("type", rs.getString("type"));
                row.put("geometry_colum", rs.getString("geometry_colum"));
                row.put("srid", rs.getString("srid"));
                // //

                cpt++;
                resultat.put(cpt, row);
            }

            // System.out.println("hellooooo");
            rs.close();
            stm.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultat;
    }

    // recuperation de toutes les informations de la table
    /*
     * @param table designe le nom de la table
     * 
     * @param signature qui peut etre une empreinte ou une geometry qui est generalement
     * cripté et on utilise des fonctions speciales pour recuperer des contenus
     */
    public Map<Integer, String> readTableGeometry(String table, String signature) {

        Map<Integer, String> resultat = new HashMap<Integer, String>();
        // System.out.println("hellooooo");
        int cpt = 0;

        try {
            // recupere les objets de la table geometrique
            Statement stm = conn.createStatement();
        
            String spatialSignature = signature;
            String schema ="schema";
            String type ="type";
            String srid="srid";
            String blank=" ";
            String comma=",";
            String tableP = table;
            String begin = "SELECT";
            String signaturef="ST_AsText(";
            String sridf = "st_srid(";
            String typef="st_type(";
            String closedbracket = ")";
            String endl = "FROM";

            String query = begin + blank+ signaturef+ spatialSignature + closedbracket + comma+
                    sridf+spatialSignature+closedbracket+ comma+
                    spatialSignature + blank+
                    endl + blank + tableP;
            System.out.println(query);
            rs = stm.executeQuery(query);

            while (rs.next()) {

                cpt++;
                // permet de recuperer la composante de la requete postgis
                resultat.put(cpt, rs.getObject(1).toString());
            //    System.out.println(rs.getObject(2).toString()+ rs.getObject(3).toString());
               
            }

            rs.close();
            stm.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultat;
    }
    
    /*
     * @param table designe le nom de la table
     * 
     * @param signature qui peut etre une empreinte ou une geometry
     * 
     * @param return un resultSet qui sera exploitér par la classe 
     * @see InteratorIndexForTable utilisée pour fabriqué un iterateur
     * des geometries de la table...
     * La requete sql des bases de données permet de recuperer un wkt ascii
     * qui code la geometrie et son type..;
     */
    public ResultSet resultSetTableGeometry(String table, String signature) {

        Map<Integer, String> resultat = new HashMap<Integer, String>();
        // System.out.println("hellooooo");
        int cpt = 0;

        try {
            // recupere les objets de la table geometrique
            Statement stm = conn.createStatement();

            String spatialSignature = signature;
            String schema ="schema";
            String type ="type";
            String srid="srid";
            String blank=" ";
            String comma=",";
            String tableP = table;
            String begin = "SELECT";
            String signaturef="ST_AsText(";
            String sridf = "st_srid(";
            String typef="st_type(";
            String closedbracket = ")";
            String endl = "FROM";

            String query = begin + blank+ signaturef+ spatialSignature + closedbracket + comma+
                    sridf+spatialSignature+closedbracket+ comma+
                    spatialSignature + blank+
                    endl + blank + tableP;
          //  System.out.println(query);
          // execution de la requete
            rs = stm.executeQuery(query);
          

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rs;
    }

    
   
    public IPopulation<IFeature> getTableContains() {
        return tableContains;
    }

    public void setTableContains(IPopulation<IFeature> tableContains) {
        this.tableContains = tableContains;
    }

    public Map<String, IPopulation<IFeature>> getAllDatabase() {
        return allDatabase;
    }

    public void setAllDatabase(
            HashMap<String, IPopulation<IFeature>> allDatabase) {
        this.allDatabase = allDatabase;
    }

  /*
   * @param table
   * designe une table de notre base de données stockée dans postgid
   * @param signature
   * designe le choix de representation de la géometrie
   */

    public InteratorIndexForTable generateIteratorTable(String table, String signature, ScaleInfo scale) {
        Iterator<Record<Geometry>> it = null;
        InteratorIndexForTable test =null;

        try {
            // recupere les objets de la table geometrique
            Statement stm = conn.createStatement();
            rs = resultSetTableGeometry(table,signature); 
            
            if(rs==null){
                throw new RuntimeException("pas de données dans la base de donées");
            }else{

            //InteratorIndexForTable test =  new InteratorIndexForTable(rs, table,signature, scale) ;   
            //System.out.print("h oooooo"+ test.getContent().size());
              //  System.out.println("it is there before");

             test = new InteratorIndexForTable(rs, table,signature, scale);
            
          
           
            return test;

//            if (it != null) {
//
//                while (it.hasNext()) {
//
//                    Record<Geometry> test = it.next();
//
//                    System.out.print(test.toString());
//                }
//            }
           
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return test;

    
    }

}

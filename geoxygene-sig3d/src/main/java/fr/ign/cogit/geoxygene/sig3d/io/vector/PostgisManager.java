package fr.ign.cogit.geoxygene.sig3d.io.vector;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

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
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 *          Classe permettant de charger de manière générique une table
 *          géométrique de PostGIS Class to load postGIS Geometries
 */
public class PostgisManager {
	public static void main(String[] args) throws Exception {
		List<String> ls = PostgisManager.tableWithGeom("localhost", "5432", "Lyon_Database", "schematest", "postgres",
				"postgres");

		System.out.println("List of table");

		System.out.println("-------------------------");
		for (String s : ls) {
			System.out.println(s);
		}
		System.out.println("-------------------------");

		IFeatureCollection<IFeature> featC = loadGeometricTable( "localhost", "5432", "Lyon_Database","schematest" , "BiBi",
				"postgres", "postgres");

		System.out.println("Number of features : " + featC.size());

	}

	private final static Logger logger = Logger.getLogger(PostgisManager.class.getName());
	// Noms de tables contenant les informations géographiques dans PostGIS
	public static String NAME_TABLE_SPATIALREF = "geometry_columns";
	public static String NAME_TABLE = "f_table_name";
	public static String NAME_COLUMN = "f_geometry_column";
	public static String NAME_COLUMN_SCHEMA = "f_table_schema";

	// Nom de la colonne geom pour la sauvegarde
	public static String NAME_COLUMN_GEOM = "the_geom";

	// Noms des types d'attributs GeoXygene
	public static String GE_STRING = "String";
	public static String GE_INTEGER = "Integer";
	public static String GE_DOUBLE = "Double";
	public static String GE_BOOLEAN = "Boolean";
	public static String GE_UNKNOWN = "Unknown";
	public static String GE_OTHER = "Other";

	// Noms des types d'attributs SQL
	public static String SQL_STRING = "varchar";
	public static String SQL_INTEGER = "integer";
	public static String SQL_DOUBLE = "numeric";
	public static String SQL_BOOLEAN = "boolean";
	public static String SQL_UNKNOWN = "varchar";
	public static String SQL_OTHER = "varchar";

	// Nom de l'opérateur traduisant en wkt (cela dépend de la version de
	// PostGIS)
	public static String OP_ASEWKT = "st_asewkt";
	public static String OP_GEOM_FROM_TEXT = "ST_GeomFromText";


	public static void setLoggerLevel(Level level) {
		PostgisManager.logger.setLevel(level);
	}

	/**
	 * Cette fonction permet de récupérer la liste des tables possédant de la
	 * géométrie
	 * 
	 * @param schema
	 *            nom du schéma
	 * @param host
	 *            hote (localhost accepté)
	 * @param port
	 *            port d'écoute
	 * @param database
	 *            nom de la pase de données
	 * @param user
	 *            utilisateur
	 * @param pw
	 *            mot de passe
	 * @return la liste des noms de table ayant une géométrie
	 */
	public static List<String> tableWithGeom(String host, String port, String database, String schema, String user,
			String pw) throws Exception {
		java.sql.Connection conn;
		// Liste des noms
		List<String> lNoms = new ArrayList<String>();
		try {

			// Création de l'URL de chargement
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
			PostgisManager.logger.info(Messages.getString("PostGIS.Try") + url);
			// Connexion
			conn = DriverManager.getConnection(url, user, pw);

			// Requete sur la table contenant les colonnes
			// De géométrie PostGIS
			Statement s = conn.createStatement();

			// SELECT * FROM geometry_columns where f_table_schema='schematest';

			String query = "select " + PostgisManager.NAME_TABLE + " from " + PostgisManager.NAME_TABLE_SPATIALREF
					+ " where " + PostgisManager.NAME_COLUMN_SCHEMA + " = '" + schema + "'";
			PostgisManager.logger.debug(query);
			ResultSet r = s.executeQuery(query);

			while (r.next()) {
				lNoms.add(r.getString(1));
			}
			s.close();
			conn.close();
			PostgisManager.logger.info(Messages.getString("PostGIS.End"));
		} catch (Exception e) {
			throw e;
		}

		return lNoms;

	}

	/**
	 * Cette fonction permet de récupérer la liste des tables possédant de la
	 * géométrie
	 * 
	 * @param host
	 *            hote (localhost accepté)
	 * @param port
	 *            port d'écoute
	 * @param database
	 *            nom de la pase de données
	 * @param user
	 *            utilisateur
	 * @param pw
	 *            mot de passe
	 * @return la liste des noms de table ayant une géométrie
	 */
	public static List<String> tableWithGeom(String host, String port, String database, String user, String pw)
			throws Exception {

		return tableWithGeom(host, port, database, "public", user, pw);
	}

	/**
	 * Charge une table géométrique dans une collection à partir d'un connexion
	 * PostGIS
	 * 
	 * @param schema
	 *            schema
	 * @param host
	 *            hote (localhost accepté)
	 * @param port
	 *            port d'écoute
	 * @param database
	 *            nom de la pase de données
	 * @param user
	 *            utilisateur
	 * @param pw
	 *            mot de passe
	 * @param table
	 *            le nom de la table que l'on souhaite charger
	 * @return les entités de la table avec les attributs renseignés. Null
	 *         renvoyé pour table non-géométriques
	 */
	public static IFeatureCollection<IFeature> loadGeometricTable(String host, String port, String database,
			String schema, String table, String user, String pw) throws Exception {

		return loadGeometricTableWhereClause(host, port, database, schema, table, user, pw, "");

	}

	/**
	 * Charge une table géométrique dans une collection à partir d'un connexion
	 * PostGIS
	 * 
	 * @param host
	 *            hote (localhost accepté)
	 * @param port
	 *            port d'écoute
	 * @param database
	 *            nom de la pase de données
	 * @param user
	 *            utilisateur
	 * @param pw
	 *            mot de passe
	 * @param table
	 *            le nom de la table que l'on souhaite charger
	 * @return les entités de la table avec les attributs renseignés. Null
	 *         renvoyé pour table non-géométriques
	 */
	public static IFeatureCollection<IFeature> loadGeometricTable(String host, String port, String database,
			String table, String user, String pw) throws Exception {

		return loadGeometricTableWhereClause(host, port, database, user, pw, table, "");

	}

	/**
	 * Charge une table géométrique dans une collection à partir d'une connexion
	 * PostGIS et en fonction de paramètres de sélection
	 * 
	 * @param schema
	 * @param host
	 * @param port
	 * @param database
	 * @param user
	 * @param pw
	 * @param table
	 * @param whereClause
	 * @return
	 * @throws Exception
	 */
	public static IFeatureCollection<IFeature> loadGeometricTableWhereClause(String host, String port, String database,
			String schema, String table, String user, String pw, String whereClause) throws Exception {

		// Liste des entités que l'on souhaite charger
		FT_FeatureCollection<IFeature> fColl = new FT_FeatureCollection<IFeature>();
		java.sql.Connection conn;

		try {
			// Création des paramètres de connexion
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
			PostgisManager.logger.info(Messages.getString("PostGIS.Try") + url);
			conn = DriverManager.getConnection(url, user, pw);

			// On récupère le nom de la colonne géométrique
			Statement s = conn.createStatement();
			Statement s1 = conn.createStatement();

			// On trouve le nom de la colonne géométrie
			String query = "select " + PostgisManager.NAME_COLUMN + ",type from " + PostgisManager.NAME_TABLE_SPATIALREF + " WHERE " + PostgisManager.NAME_TABLE + "='" + table + "'"
					+ " AND " +PostgisManager.NAME_COLUMN_SCHEMA+ "='" + schema + "'";
			//System.out.println(query);
			ResultSet r = s.executeQuery(query);
			
			// Pas de colonne géométrique on renvoie null
			if (r == null ||!r.next()) {
				s.close();
				s1.close();
				conn.close();
				return null;
			}

			String nomColonneGeom = "";
			while (r.next()) {
				nomColonneGeom = r.getString(1);
			}

			// Pas de colonne géométrique on renvoie null
			if (nomColonneGeom == "") {
				s.close();
				s1.close();
				conn.close();
				return null;
			}

			query = "SELECT column_name,data_type FROM INFORMATION_SCHEMA.COLUMNS  WHERE TABLE_NAME='"  + table+"'"  + "AND table_schema = '"+schema+"'";
			//System.out.println(query);
			// On récupère la liste des attributs et on crée les FeatureType
			ResultSet r1 = s.executeQuery(query);

			FeatureType fType = new FeatureType();
			SchemaDefaultFeature featureSchema = new SchemaDefaultFeature();

			Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>();

			int indColonGeom = -1;
			int compt = 0;
			String sql = "SELECT * FROM \"" + schema + "\".\"" + table+"\"";
			//System.out.println(sql);
			PostgisManager.logger.debug("Récupération données : " + sql);
			// //Schéma contruit on instancie les features
			// On récupère la liste des attributs et on crée les FeatureType
			ResultSet r2 = s1.executeQuery(sql);

			// On ne récupère pas la 1er colonne

			// Les indices commencent à 1

			int shiftIni = 1;

			while (r1.next()) {

				String nomField = r1.getString(1);

				if (nomField.equals(nomColonneGeom)) {
					// Si c'est la colonne géométrique
					// On récupère son nom
					indColonGeom = compt;
					shiftIni++;
					continue;

				} else {
					// On renseigne les attributs du type
					AttributeType type = new AttributeType();

					String memberName = nomField;

					// On indique le type en fonction du type SQL
					String valueType = PostgisManager
							.fromSQLTypeToJava(r2.getMetaData().getColumnType(compt + shiftIni)); // on
																									// invite
																									// le
																									// asewkt,
					// les indexes
					// commencent à 1

					type.setNomField(nomField);
					type.setMemberName(memberName);
					type.setValueType(valueType);

					fType.addFeatureAttribute(type);

					attLookup.put(new Integer(compt), new String[] { nomField, memberName });

					compt++;
				}

			}

			// Maintenant on s'occupe de renseigner les informations
			// Pour chacune des lignes de la table
			int nbAttribut = fType.getFeatureAttributes().size();
			// On prépare le schéma
			featureSchema.setFeatureType(fType);
			fType.setSchema(featureSchema);
			featureSchema.setAttLookup(attLookup);

			String requestSelect = "SELECT " + OP_ASEWKT + "(" + nomColonneGeom + ") as asewkt, * FROM \"" + schema + "\".\""+ table+"\"";

			if (!whereClause.isEmpty()) {

				requestSelect = requestSelect + " WHERE " + whereClause;

			}

			//System.out.println(requestSelect);
			r2 = s.executeQuery(requestSelect);

			while (r2.next()) {
				// On crée l'entité et on lui associé ses métadonnées
				DefaultFeature deF = new DefaultFeature();
				deF.setSchema(featureSchema);
				deF.setFeatureType(fType);

				deF.setAttributes(new Object[nbAttribut]);
				int shift = 1;
				boolean colGeom = true;
				// Pour chaque attribut (l'index des row commence à 1)
				for (int i = 1; i <= nbAttribut + shiftIni; i++) {
					if (i == 1) {
						// D'après la requête la géométrie est en tête
						
						String geomAsWKT = r2.getString(1);
						
						if(geomAsWKT == null || geomAsWKT.isEmpty()){
							
							deF.setGeom(null);
							
						}else{
							
							IGeometry geom = WktGeOxygene.makeGeOxygene(geomAsWKT);
							deF.setGeom(geom);

						}
						
					
						// le shift sert à faire la correspondance entre l'index
						// de l'attribut et l'index du résultat de la requete

						shift++;
						continue;
					} else if ((i == indColonGeom + shift) && colGeom) {
						// On arrive à la colonne géométrie, on passe
						// (renseignée précédemment));

						shift++;
						colGeom = false;
						continue;

					}
					// On renseigne l'attribut
					deF.setAttribute(i - shift, r2.getString(i));
				}
				// On ajoute à la collection
				fColl.add(deF);
			}
			// On ferme les connexions
			PostgisManager.logger.info(Messages.getString("PostGIS.End"));
			s1.close();
			s.close();
			conn.close();

		} catch (Exception e) {

			throw e;

		}finally{
			
		}

		return fColl;

	}

	/**
	 * Charge une table géométrique dans une collection à partir d'une connexion
	 * PostGIS et en fonction de paramètres de sélection
	 * 
	 * @param host
	 * @param port
	 * @param database
	 * @param user
	 * @param pw
	 * @param table
	 * @param whereClause
	 * @return
	 * @throws Exception
	 */

	public static IFeatureCollection<IFeature> loadGeometricTableWhereClause(String host, String port, String database,
			String user, String pw, String table, String whereClause) throws Exception {
		return loadGeometricTableWhereClause(host, port, database, "public", table, user, pw, whereClause);
	}

	/**
	 * Charge une table non géométrique dans une collection à partir d'une
	 * connexion PostGIS et en fonction de paramètres de sélection
	 * 
	 * @param host
	 * @param port
	 * @param database
	 * @param user
	 * @param pw
	 * @param table
	 * @param whereClause
	 * @return
	 * @throws Exception
	 */
	public static IFeatureCollection<IFeature> loadNonGeometricTableWhereClause(String host, String port,
			String database, String schema, String table, String user, String pw, String whereClause) throws Exception {

		// Liste des entités que l'on souhaite charger
		FT_FeatureCollection<IFeature> fColl = new FT_FeatureCollection<IFeature>();
		java.sql.Connection conn;

		try {
			// Création des paramètres de connexion
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
			PostgisManager.logger.info(Messages.getString("PostGIS.Try") + url);
			conn = DriverManager.getConnection(url, user, pw);

			// On prépare les requêtes SQL
			Statement s = conn.createStatement();
			Statement s1 = conn.createStatement();

			// On cherche si la table comporte une colonne géométrique
			ResultSet r = s.executeQuery("select " + PostgisManager.NAME_COLUMN + ",type from "
					+ PostgisManager.NAME_TABLE_SPATIALREF + " WHERE " + PostgisManager.NAME_TABLE + "='" + table
					+ "' AND " + PostgisManager.NAME_COLUMN_SCHEMA + "='" + schema + "'");

			String nomColonneGeom = "";
			while (r.next()) {
				nomColonneGeom = r.getString(1);
			}

			// Cas : présence d'une colonne géométrique dans la table
			if (!(nomColonneGeom.isEmpty())) {
				s.close();
				s1.close();
				conn.close();

				PostgisManager.logger.info(Messages.getString("La colonne '" + nomColonneGeom
						+ "' contient de la géométrie. La valeur 'null' est renvoyée."));

				return null;
			}

			// On récupère la liste des attributs et leurs types
			ResultSet r1 = s.executeQuery(
					"SELECT column_name, data_type FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = '" + table + "'" + "AND table_schema = '"+schema+"'");

			// On construit le FeatureType, le Schema et la Map
			FeatureType fType = new FeatureType();
			SchemaDefaultFeature schemaFeature = new SchemaDefaultFeature();
			Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>();

			// On initialise différents indices
			int compt = 0;

			// On récupère l'ensemble des données de la table
			String sql = "SELECT * FROM " + table;
			PostgisManager.logger.debug("Récupération données : " + sql);

			ResultSet r2 = s1.executeQuery(sql);

			// On ne récupère pas la première colonne
			// On commenece donc à 1
			int shiftIni = 1;

			while (r1.next()) {

				String nomField = r1.getString(1);
				AttributeType type = new AttributeType();
				String memberName = nomField;

				// On récupère le type et on le convertit le type SQL en type
				// Java
				String valueType = PostgisManager.fromSQLTypeToJava(r2.getMetaData().getColumnType(compt + shiftIni));

				// On complète FeatureType
				type.setNomField(nomField);
				type.setMemberName(memberName);
				type.setValueType(valueType);

				fType.addFeatureAttribute(type);

				attLookup.put(new Integer(compt), new String[] { nomField, memberName });

				compt++;

			}

			// On renseigne les informations pour chaque ligne de la table
			int nbAttribut = fType.getFeatureAttributes().size();

			schemaFeature.setFeatureType(fType);
			fType.setSchema(schemaFeature);
			schemaFeature.setAttLookup(attLookup);

			String requestSelect = "SELECT * FROM \"" + schema + "\".\"" + table+"\"";
			if (!(whereClause.equals(""))) {

				requestSelect = requestSelect + " WHERE " + whereClause;

			}

			r2 = s.executeQuery(requestSelect);

			while (r2.next()) {

				// On crée l'entité et on lui associé ses métadonnées
				DefaultFeature deF = new DefaultFeature();
				deF.setSchema(schemaFeature);
				deF.setFeatureType(fType);
				deF.setAttributes(new Object[nbAttribut]);

				if (nbAttribut == 1) {

					deF.setAttribute(0, r2.getString(1));

				} else {

					// Pour chaque attribut (l'index des row commence à 1)
					for (int i = 1; i < nbAttribut /* + shiftIni */; i++) {

						if (i <= nbAttribut) {
							// On renseigne l'attribut

							deF.setAttribute(i, r2.getString(i + shiftIni));

						}

					}

				}

				// On ajoute à la collection
				fColl.add(deF);

			}

			// On ferme les connexions
			PostgisManager.logger.info(Messages.getString("PostGIS.End"));
			s1.close();
			s.close();
			conn.close();

		} catch (Exception e) {

			throw e;

		}

		return fColl;

	}

	/**
	 * Charge une table non géométrique dans une collection à partir d'un
	 * connexion PostGIS
	 * 
	 * @param host
	 *            hote (localhost accepté)
	 * @param port
	 *            port d'écoute
	 * @param database
	 *            nom de la pase de données
	 * @param user
	 *            utilisateur
	 * @param pw
	 *            mot de passe
	 * @param table
	 *            le nom de la table que l'on souhaite charger
	 * @return les entités de la table avec les attributs renseignés.
	 */
	public static IFeatureCollection<IFeature> loadNonGeometricTable(String host, String port, String database,
			String user, String pw, String table) throws Exception {

		return loadNonGeometricTableWhereClause(host, port, database, "public", user, pw, table, "");

	}

	/**
	 * Charge une collection dans une table géométrique à partir d'un connexion
	 * PostGIS
	 * 
	 * @param host
	 *            hote (localhost accepté)
	 * @param port
	 *            port d'écoute
	 * @param database
	 *            nom de la pase de données
	 * @param user
	 *            utilisateur
	 * @param pw
	 *            mot de passe
	 * @param table
	 *            le nom de la table que l'on souhaite charger
	 * @param adapteListToAttribute
	 *            si true permet de regénérer la liste des attributs stockés
	 *            pour chaque entité
	 * @return les entités de la table avec les attributs renseignés. Null
	 *         renvoyé pour table non-géométriques
	 */
	public static boolean insertInGeometricTable(String host, String port, String database, String schema, String table, String user,
			String pw,  IFeatureCollection<? extends IFeature> featColl, boolean adapteListToAttribute)
			throws Exception {

		// Liste des entités que l'on souhaite charger
		java.sql.Connection conn;

		try {
			// Création des paramètres de connexion
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
			PostgisManager.logger.info(Messages.getString("PostGIS.Try") + url);
			conn = DriverManager.getConnection(url, user, pw);

			Statement s = conn.createStatement();
			

			ResultSet rExist = s.executeQuery(
					"SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='" + table + "'"+ " AND table_schema = '"+schema+"'");
			
			
			if(! rExist.next()) {
				
				return saveGeometricTable(host, port, database, schema, table, user, pw, featColl, false);
			}


			IFeature feat = featColl.get(0);

			List<GF_AttributeType> lAtt = getAttributeList(feat);

			// On ajoute les éléments
			int nbElem = featColl.size();

			for (int i = 0; i < nbElem; i++) {

				IFeature featTemp = featColl.get(i);
				if (adapteListToAttribute) {
					lAtt = getAttributeList(featTemp);
				}

				// On ajoute la géométrie
				String geom = WktGeOxygene.makeWkt(featTemp.getGeom());
				
			

				String columns = " (" + NAME_COLUMN_GEOM;

				if (lAtt != null) {
					for (GF_AttributeType nomAtt : lAtt) {
						columns = columns + "," + nomAtt.getMemberName();

					}
				}
				
				int crs = featTemp.getGeom().getCRS();
				columns = columns + ")";

				String sql_insert = "insert into \"" + schema + "\".\"" + table +"\""+ columns + " VALUES(" + OP_GEOM_FROM_TEXT
						+ "('" + geom + "', " + crs+ " )";

				if (lAtt != null) {
					// On parcourt les attributs et on crée les colonnes ad hoc
					for (int j = 0; j < lAtt.size(); j++) {

						GF_AttributeType att = lAtt.get(j);

						if (att.getValueType().equals(PostgisManager.GE_STRING)
								|| att.getValueType().equals(PostgisManager.GE_OTHER)) {

							sql_insert = sql_insert + "," + "'" + featTemp.getAttribute(att.getMemberName()) + "'";
						} else {

							sql_insert = sql_insert + "," + featTemp.getAttribute(att.getMemberName());
						}

					}

				}
				sql_insert = sql_insert + ")";
				// System.out.println(sql_insert);
				PostgisManager.logger.debug(Messages.getString(sql_insert));
				s.execute(sql_insert);
				PostgisManager.logger.debug(Messages.getString("Sauvegarde.AddColum") + " : " + sql_insert);
			}

			// On ferme les connexions
			s.close();
			conn.close();
			PostgisManager.logger.info(Messages.getString("PostGIS.End"));

		} catch (Exception e) {

			throw e;

		}

		return true;
	}

	/**
	 * Charge une collection dans une table géométrique à partir d'un connexion
	 * PostGIS
	 * 
	 * @param host
	 *            hote (localhost accepté)
	 * @param port
	 *            port d'écoute
	 * @param database
	 *            nom de la pase de données
	 * @param user
	 *            utilisateur
	 * @param pw
	 *            mot de passe
	 * @param table
	 *            le nom de la table que l'on souhaite charger
	 * @return les entités de la table avec les attributs renseignés. Null
	 *         renvoyé pour table non-géométriques
	 */
	public static boolean insertInGeometricTable(String host, String port, String database, String table, String user, String pw,
			 IFeatureCollection<? extends IFeature> featColl) throws Exception {
		return insertInGeometricTable(host, port, database, "public",  table, user, pw, featColl, false);
	}

	private static List<GF_AttributeType> getAttributeList(IFeature feat) {

		List<GF_AttributeType> lAtt = null;
		GF_FeatureType fType = feat.getFeatureType();
		if (fType != null && fType.getFeatureAttributes() != null) {

			lAtt = fType.getFeatureAttributes();

		}

		return lAtt;
	}

	/**
	 * Charge une collection dans une table non-géométrique à partir d'un
	 * connexion PostGIS
	 * 
	 * @param host
	 *            hote (localhost accepté)
	 * @param port
	 *            port d'écoute
	 * @param database
	 *            nom de la pase de données
	 * @param user
	 *            utilisateur
	 * @param pw
	 *            mot de passe
	 * @param table
	 *            le nom de la table que l'on souhaite charger
	 * @return les entités de la table avec les attributs renseignés.
	 */
	public static boolean insertInNonGeometricTable(String host, String port, String database, String user, String pw,
			String table, IFeatureCollection<? extends IFeature> featColl) throws Exception {

		// Liste des entités que l'on souhaite charger
		java.sql.Connection conn;

		try {
			// Création des paramètres de connexion
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
			PostgisManager.logger.info(Messages.getString("PostGIS.Try") + url);
			conn = DriverManager.getConnection(url, user, pw);

			Statement s = conn.createStatement();

			IFeature feat = featColl.get(0);

			GF_FeatureType fType = feat.getFeatureType();

			int nbAttribut = 0;

			List<GF_AttributeType> lAtt = null;

			if (fType != null && fType.getFeatureAttributes() != null) {

				lAtt = fType.getFeatureAttributes();
				nbAttribut = lAtt.size();
				//System.out.println("Attributs pris en charge : " + lAtt);
				//System.out.println("Nombre d'attributs : " + nbAttribut);

			}

			// On ajoute les éléments
			int nbElem = featColl.size();

			for (int i = 0; i < nbElem; i++) {

				IFeature featTemp = featColl.get(i);

				String columns = " (";

				if (lAtt != null) {

					for (int k = 0; k < lAtt.size(); k++) {

						for (GF_AttributeType nomAtt : lAtt) {

							if (k == (lAtt.size() - 1)) {

								columns = columns + nomAtt.getMemberName();

							} else {

								columns = columns + nomAtt.getMemberName() + " , ";

							}

							k++;
						}

					}

				}

				columns = columns + ")";

				String sql_insert = "insert into " + table + columns + " VALUES(";

				if (lAtt != null) {

					for (int j = 0; j < nbAttribut; j++) {

						GF_AttributeType att = lAtt.get(j);

						// Si format string ou inconnu
						if (att.getValueType().equals(PostgisManager.GE_STRING)
								|| att.getValueType().equals(PostgisManager.GE_OTHER)) {

							// Cas : il n'y a qu'une colonne à compléter
							if (nbAttribut == 1) {

								sql_insert = sql_insert + "'" + featTemp.getAttribute(att.getMemberName()) + "'";
								//System.out.println("Un seul attribut à ajouter");

								// Cas : il y a plusieurs colonnes à compléter
							} else {

								// Cas : dernier attribut
								if (j == (nbAttribut - 1)) {

									sql_insert = sql_insert + ", '" + featTemp.getAttribute(att.getMemberName()) + "'";

									// Cas : Attributs autres que le dernier
								} else {

									// Cas : premier attribut
									if (j == 0) {

										sql_insert = sql_insert + "'" + featTemp.getAttribute(att.getMemberName())
												+ "'";

										// Cas : Attributs autres que premier et
										// dernier
									} else {

										sql_insert = sql_insert + ", '" + featTemp.getAttribute(att.getMemberName())
												+ "'";

									}

								}

							}

							// Si format autre que string mais connu
						} else {

							// Cas : il n'y a qu'une colonne à compléter
							if (nbAttribut == 1) {

								sql_insert = sql_insert + featTemp.getAttribute(att.getMemberName());
								//System.out.println("Un seul attribut à ajouter");

								// Cas : il y a plusieurs colonnes à compléter
							} else {

								// Cas : dernier attribut
								if (j == (nbAttribut - 1)) {

									sql_insert = sql_insert + ", " + featTemp.getAttribute(att.getMemberName());

									// Cas : Attributs autres que le dernier
								} else {

									// Cas : premier attribut
									if (j == 0) {

										sql_insert = sql_insert + featTemp.getAttribute(att.getMemberName());

										// Cas : Attributs autres que le premier
										// ou le dernier
									} else {

										sql_insert = sql_insert + "," + featTemp.getAttribute(att.getMemberName());

									}

								}

							}

						}

					}

				}

				// On termine la requête
				sql_insert = sql_insert + ")";
				//System.out.println(sql_insert);

				s.execute(sql_insert);

				PostgisManager.logger.debug(Messages.getString("Sauvegarde.AddColum") + " : " + sql_insert);
			}

			// On ferme les connexions
			s.close();
			conn.close();

			PostgisManager.logger.info(Messages.getString("PostGIS.End"));

		} catch (Exception e) {

			throw e;

		}

		return true;

	}

	/**
	 * Exécute une requête spécifiée en paramètre dans PostGis. Fonction
	 * principalement adaptée à l'insertion de données au sein d'une table ou
	 * d'une colonne.
	 * 
	 * @param host
	 *            hote (localhost accepté)
	 * @param port
	 *            port d'écoute
	 * @param database
	 *            nom de la pase de données
	 * @param user
	 *            utilisateur
	 * @param pw
	 *            mot de passe
	 * @param sqlRequest
	 *            la requête à exécuter
	 * @return indique si l'opération s'est déroulée avec succès
	 * @throws Exception
	 */
	public static boolean executeSimpleRequestInsert(String host, String port, String database, String user, String pw,
			String sqlRequest) throws Exception {

		if (sqlRequest != null && sqlRequest.isEmpty()) {

			java.sql.Connection conn;

			try {

				String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
				PostgisManager.logger.info(Messages.getString("PostGIS.Try") + url);
				conn = DriverManager.getConnection(url, user, pw);

				Statement s = conn.createStatement();

				s.execute(sqlRequest);
				//System.out.println(sqlRequest);

				s.close();
				conn.close();

				PostgisManager.logger.info(Messages.getString("PosGIS.End"));

			} catch (Exception e) {
				throw e;
			}

			return true;

		} else {

			System.out.println("La requête sql envoyée est vide");

			return false;
		}

	}

	/**
	 * Sauvegarde une couche dans PostGIS à partir d'une collection d'entité. On
	 * considère que toutes les entités utilisent le même schéma geo (nous
	 * prenons le schéma de la première entité).
	 * 
	 * @param host
	 *            hote (localhost accepté)
	 * @param port
	 *            port d'écoute
	 * @param database
	 *            nom de la pase de données
	 * @param user
	 *            utilisateur
	 * @param pw
	 *            mot de passe
	 * @param table
	 *            le nom de la table que l'on souhaite charger
	 * @param featColl
	 *            la collection que l'on souhaite sauver
	 * @param erase
	 *            indique si l'on souhaite écraser la table (si elle existe)
	 * @return indique si l'opération s'est déroulée avec succès
	 * @throws Exception
	 */
	public static boolean saveGeometricTable(String host, String port, String database, String schema, String table, String user, String pw,
			 IFeatureCollection<? extends IFeature> featColl, boolean erase) throws Exception {

		if (featColl == null || featColl.size() == 0) {
			throw new Exception(Messages.getString("3DGIS.LayerEmpty"));

		}

		// Liste des entités que l'on souhaite charger
		java.sql.Connection conn;

		try {
			// Création des paramètres de connexion
			String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
			PostgisManager.logger.info(Messages.getString("PostGIS.Try") + url);
			conn = DriverManager.getConnection(url, user, pw);

			// On récupère le nom de la colonne géométrique
			Statement s = conn.createStatement();

			ResultSet r = s.executeQuery(
					"SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME='" + table + "'"+ " AND table_schema = '"+schema+"'");

			// On regarde si il y a un élément
			if (r.next()) {

				if (erase) {

					// C'est le cas on supprime la table
					String sqlSupp = "drop table \"" + schema + "\".\""+ table + "\";";
					PostgisManager.logger.debug(Messages.getString("Sauvegarde") + " : " + sqlSupp);
					s.execute(sqlSupp);
					// On supprime la colonne géométrique
					String sqlSuppGeom = "delete  from geometry_columns where "+PostgisManager.NAME_TABLE+" = '" + table + "' AND "+PostgisManager.NAME_COLUMN_SCHEMA+"='"+schema+"' ;";
					PostgisManager.logger
							.debug(Messages.getString("Sauvegarde.DeleteGeomColumn") + " : " + sqlSuppGeom);
					s.execute(sqlSuppGeom);

				} else {
					s.close();
					conn.close();
					

				}

			}
			
			
			
			// On sait qu'il n'y a plus de table de type nom table
			// On crée la table
			String sqlCreat = "create table \"" + schema+"\".\""+table + "\"();";
			System.out.println(sqlCreat);
			PostgisManager.logger.debug(Messages.getString("Sauvegarde.CreateTable") + " : " + sqlCreat);
			s.execute(sqlCreat);

			// On utilise la première entité pour tout créer
			IFeature feat = featColl.get(0);
			IGeometry the_geom = feat.getGeom();

			int CRS = the_geom.getCRS();

			if (CRS == 0) {
				CRS = -1;
			}

			String requestGeometryColumn = "SELECT AddGeometryColumn( '" +schema +"','"+table + "', '"
					+ PostgisManager.NAME_COLUMN_GEOM + "', " + CRS + ", 'GEOMETRY', "
					+ ((GM_Object) the_geom).coordinateDimension() + ", true )";
			
			System.out.println(requestGeometryColumn);
			// On ajoute une collonne géométrie
			s.execute(requestGeometryColumn);

			GF_FeatureType fType = feat.getFeatureType();

			int nbAttribut = 0;
			List<GF_AttributeType> lAtt = null;
			if (fType.getFeatureAttributes() != null) {

				lAtt = fType.getFeatureAttributes();
				nbAttribut = lAtt.size();
			}
			if (lAtt != null) {
				// On parcourt les attributs et on crée les colonnes ad hoc
				for (int i = 0; i < nbAttribut; i++) {

					GF_AttributeType att = lAtt.get(i);
					// On créer une colonne en fonction de la correspondance
					int intType = PostgisManager.fromJavaTypeToSQL(att.getValueType());
					String type = "";
					String nom_col = att.getMemberName();

					if (intType == Types.VARCHAR) {
						type = PostgisManager.SQL_STRING;

					} else if (intType == Types.INTEGER) {
						type = PostgisManager.SQL_INTEGER;

					} else if (intType == Types.DOUBLE) {

						type = PostgisManager.SQL_DOUBLE;

					} else if (intType == Types.BOOLEAN) {

						type = PostgisManager.SQL_BOOLEAN;

					} else {
						type = PostgisManager.SQL_UNKNOWN;
					}

					String sql = "ALTER table \"" +schema +"\".\"" + table + "\" add " + nom_col + " " + type;
					PostgisManager.logger.info(Messages.getString("Sauvegarde.AddColum") + " : " + sql);
					System.out.println(sql);
					s.execute(sql);

				}
			}

			conn.close();

		} catch (Exception e) {

			throw e;

		}

		return insertInGeometricTable(host, port, database,schema, table, user, pw, featColl,false);
	}

	/**
	 * Convertit un type SQL de la classe java.sql.type en type GeOxygene défini
	 * par des constantes de type String
	 * 
	 * @param sqlType
	 *            l'entier représentant le type SQL voire classe java.sql.Types
	 * @return l'entier du type Java correspondant au type SQL en entrée
	 */
	public static String fromSQLTypeToJava(int sqlType) {

		if (sqlType == Types.BIT) {

			return PostgisManager.GE_BOOLEAN;
		} else if (sqlType == Types.TINYINT) {

			return PostgisManager.GE_INTEGER;
		} else if (sqlType == Types.SMALLINT) {

			return PostgisManager.GE_INTEGER;
		} else if (sqlType == Types.INTEGER) {

			return PostgisManager.GE_INTEGER;
		} else if (sqlType == Types.BIGINT) {
			return PostgisManager.GE_INTEGER;
		} else if (sqlType == Types.FLOAT) {

			return PostgisManager.GE_DOUBLE;

		} else if (sqlType == Types.REAL) {

			return PostgisManager.GE_DOUBLE;
		} else if (sqlType == Types.DOUBLE) {

			return PostgisManager.GE_DOUBLE;
		} else if (sqlType == Types.NUMERIC) {

			return PostgisManager.GE_DOUBLE;
		} else if (sqlType == Types.DECIMAL) {

			return PostgisManager.GE_DOUBLE;
		} else if (sqlType == Types.CHAR) {

			return PostgisManager.GE_STRING;
		} else if (sqlType == Types.VARCHAR) {

			return PostgisManager.GE_STRING;
		} else if (sqlType == Types.LONGVARCHAR) {

			return PostgisManager.GE_STRING;
		} else if (sqlType == Types.OTHER) {
			return PostgisManager.GE_OTHER;
		}

		return PostgisManager.GE_UNKNOWN;
	}

	/**
	 * Convertit un type GeOxygene (String, Double, Integer, Boolean) en type
	 * SQL par des constantes de type String
	 * 
	 * @param javaType
	 *            le type GeOxygene
	 * @return renvoie l'entier qui correspond au type SQL du type Java en
	 *         paramètre
	 */
	public static int fromJavaTypeToSQL(String javaType) {

		if (javaType.equalsIgnoreCase(PostgisManager.GE_STRING)) {

			return Types.VARCHAR;

		} else if (javaType.equalsIgnoreCase(PostgisManager.GE_INTEGER)) {

			return Types.INTEGER;
		} else if (javaType.equalsIgnoreCase(PostgisManager.GE_DOUBLE) || javaType.equalsIgnoreCase("Long")) {
			return Types.DOUBLE;

		} else if (javaType.equalsIgnoreCase(PostgisManager.GE_BOOLEAN)) {

			return Types.BOOLEAN;
		} else if (javaType.equalsIgnoreCase(PostgisManager.GE_UNKNOWN)
				|| javaType.equalsIgnoreCase(PostgisManager.GE_OTHER)) {
			return Types.OTHER;
		} else {
			return Types.OTHER;
		}
	}

}

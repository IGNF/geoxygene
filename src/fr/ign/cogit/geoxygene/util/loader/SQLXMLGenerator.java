/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.util.loader;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JOptionPane;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.util.loader.gui.GUISelectionGeometrie;

/**
 * Usage interne. Appelé par la Console.
 * Génére à partir d'une classe Java, la table dans le SGBD et le fichier de mapping
 * correspondants.
 *
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class SQLXMLGenerator {

	private Geodatabase data;
	private OjbXMLGenerator theXMLGenerator;
	private String geOxygeneMapping; // path
	private String extentMappingFileName;
	private String tableName;
	private String javaFilePath;
	private String userName;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public SQLXMLGenerator (Geodatabase Data, String JavaFilePath, String mappingDirectory, String TableName, String mappingFileName){
		data = Data;
		tableName = TableName;
		javaFilePath = JavaFilePath;
		String extentClassName = "";
		extentMappingFileName = null;
		geOxygeneMapping = mappingDirectory;
		theXMLGenerator = new OjbXMLGenerator(data,geOxygeneMapping,mappingFileName, extentClassName, extentMappingFileName );
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void writeAll()  {

		//ecriture de la table correspondante à la classe java
		boolean heritage = true; // à paramétrer par la suite

		System.out.println("Création de la table");
		try {
			if (data.getDBMS() == Geodatabase.ORACLE) querySQLOracle(javaFilePath,tableName,heritage);
			else if (data.getDBMS() == Geodatabase.POSTGIS)querySQLPostgis(javaFilePath,tableName,heritage);
			else {
				JOptionPane.showMessageDialog(null,"Problème de SGBD non supporté : il apparaît que" +
						" ce n'est ni Oracle ni PostgreSQL","SGBD non supporté",JOptionPane.WARNING_MESSAGE);
				return;
			}
			System.out.println("Table créée");
		}
		catch (Exception e){
			System.out.println("Table non créée");
			e.printStackTrace();
		}

		System.out.println("Création de la table terminée");

		//ecriture du fichier de mapping correspondant
		try {
			Connection conn = data.getConnection();
			Statement stm = conn.createStatement();

			System.out.println("");

			theXMLGenerator.writeClassHeader(javaFilePath,tableName.toUpperCase());

			String query = "";
			if (data.getDBMS() == Geodatabase.ORACLE){
				query = getQueryColumnNameOracle(tableName.toUpperCase());
				conn.commit();
				ResultSet rs = stm.executeQuery(query);
				// Boucle sur les colonnes
				while (rs.next()) {

					// La colonne SQL
					String sqlColumnName = rs.getString(1);

					// Le type SQL
					String sqlDbmsType = rs.getString(2);

					// Si c'est le champ COGITID : on passe
					if (sqlColumnName.compareToIgnoreCase("COGITID") == 0)
						continue;

					// bidouille speciale Oracle pour traiter le cas des entiers et des booléens...
					if (rs.getObject(3) != null) {
						int dataScale = ((BigDecimal)rs.getObject(3)).intValue();
						//cas des entiers
						if ((sqlDbmsType.compareToIgnoreCase("NUMBER")==0) && (dataScale == 0))
							sqlDbmsType ="INTEGER";
						//cas des booleans (ne sont pas reconnus par Oracle JDBC)
						//On suppose que CHAR(1) est un boolean
						if ((sqlDbmsType.compareToIgnoreCase("CHAR")==0) && (dataScale == 1))
							sqlDbmsType ="BOOLEAN";
					}
					// fin de la bidouille

					// Le nom Java
					String javaFieldName = sqlColumnName.toLowerCase();

					theXMLGenerator.writeField(javaFieldName,sqlColumnName,sqlDbmsType);

					System.out.println("    nom sql : "+sqlColumnName+"\n   nom java : "+javaFieldName);
				}
				rs.close();
				theXMLGenerator.writeClassBottom();
			}
			else if (data.getDBMS() == Geodatabase.POSTGIS){
				try {
					userName = conn.getMetaData().getUserName();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				query = getQueryColumnNamePostgis(tableName,userName);
				conn.commit();
				ResultSet rs = stm.executeQuery(query);
				// Boucle sur les colonnes
				while (rs.next()) {

					// La colonne SQL
					String sqlColumnName = rs.getString(1);

					// Le type SQL
					String sqlDbmsType = rs.getString(2);

					// Si c'est le champ COGITID : on passe
					if (sqlColumnName.compareToIgnoreCase("COGITID") == 0) continue;

					// Le nom Java
					String javaFieldName = sqlColumnName.toLowerCase();

					theXMLGenerator.writeField(javaFieldName,sqlColumnName,sqlDbmsType);

					System.out.println("    nom sql : "+sqlColumnName+"\n   nom java : "+javaFieldName);
				}
				rs.close();
				theXMLGenerator.writeClassBottom();
			}

			stm.close();

		}
		catch (Exception e) {
			e.printStackTrace();
		}

		theXMLGenerator.writeFileBottom();
		theXMLGenerator.writeInFile();
	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private String getQueryColumnNameOracle(String tableName) {
		return "SELECT COLUMN_NAME, DATA_TYPE, DATA_SCALE FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '"+tableName+"'";
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private String getQueryColumnNamePostgis(String tableName, String user) {
		return 	"select pg_attribute.attname, pg_type.typname "+
		"from pg_attribute, pg_type, pg_class, pg_user "+
		"where pg_class.oid = pg_attribute.attrelid "+
		"and pg_attribute.attnum>0 "+
		"and pg_attribute.atttypid = pg_type.oid "+
		"and pg_class.relowner = pg_user.usesysid "+
		"and pg_user.usename = '"+user.toLowerCase()+"' "+
		"and pg_class.relname='"+tableName.toLowerCase()+"'";
	}


	///////////////////////////////////////////////////////////////////////
	//////////////////////////////// ORACLE ///////////////////////////////
	///////////////////////////////////////////////////////////////////////

	//l'identifiant COGITID est créé par défaut en tant que clé primaire de la table
	//HERITAGE :
	//héritage jusqu'à ce que la classe mère soit différente de java.lang.Object
	//n'est hérité de FT_Feature que la géométrie et l'identifiant
	private void querySQLOracle(String cheminClasse, String nomTable, boolean flagHeritage) throws Exception {
		String query="";
		Field[] attributs;
		Class<?> classe = Class.forName(cheminClasse);

		try {
			Connection conn = data.getConnection();
			Statement stm = conn.createStatement();

			if (flagHeritage){
				//String [][] dataNomAttributs, dataTypeAttributs;
				String nomAttribut, type;
				query = "CREATE TABLE "+nomTable+" (COGITID INTEGER PRIMARY KEY";
				while (!classe.getName().equals("java.lang.Object")){
					attributs = classe.getDeclaredFields();
					//dataNomAttributs = new String[attributs.length][1];
					//dataTypeAttributs = new String[attributs.length][1];
					if (!classe.getName().equals("fr.ign.cogit.geoxygene.feature.FT_Feature")){
						int i = 0;
						while(i!=attributs.length){
							query = query+", ";
							Field attribut = attributs[i];
							nomAttribut = attribut.getName();
							if (!nomAttribut.equals("id")){
								type = javaType2OracleType(attribut.getType().getName());
								query = query + attribut.getName() +" "+type;
								if (type.equals("MDSYS.SDO_GEOMETRY")) {
									stm.executeQuery("INSERT INTO USER_SDO_GEOM_METADATA VALUES ('"+nomTable+"','"+nomAttribut+"',NULL,NULL)");
								}
							}
							i++;
						}
					}
					else {
						query = query + ",GEOM MDSYS.SDO_GEOMETRY";
						stm.executeQuery("INSERT INTO USER_SDO_GEOM_METADATA VALUES ('"+nomTable+"','GEOM',NULL,NULL)");
					}
					classe = classe.getSuperclass();
				}
				query = query + ")";
			}
			else {
				attributs = classe.getDeclaredFields();
				//String [][] dataNomAttributs = new String[attributs.length][1], dataTypeAttributs = new String[attributs.length][1];
				String nomAttribut, type;
				query = "CREATE TABLE "+nomTable+" (COGITID INTEGER PRIMARY KEY";
				int i = 0;
				while(i!=attributs.length){
					query = query+",";
					Field attribut = attributs[i];
					nomAttribut = attribut.getName();
					if (!nomAttribut.equals("id")){
						type = javaType2OracleType(attribut.getType().getName());
						query = query +attribut.getName() +" "+type;
						if (type.equals("MDSYS.SDO_GEOMETRY")) {
							stm.executeQuery("INSERT INTO USER_SDO_GEOM_METADATA VALUES ('"+nomTable+"','"+nomAttribut+"',NULL,NULL)");
						}
					}
					i++;
				}
				query = query + ")";
			}

			stm.executeQuery(query);
			stm.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	///////////////////////////////////////////////////////////////////////
	//////////////////////////////// POSTGIS //////////////////////////////
	///////////////////////////////////////////////////////////////////////

	/**
	 * Création de la table postgis.
	 * L'identifiant COGITID est créé par défaut en tant que clé primaire de la table.
	 * Si flagHeritage est vrai, la fonction parcours les superclasses de la classe cheminClasse jusqu'à java.lang.Object.
	 * Ne sont hérités de FT_Feature que la géométrie et l'identifiant.
	 * @param cheminClasse nom de la classe Java des objets à stocker dans le SGBD
	 * @param nomTable nom de la table dans le SGBD
	 * @param flagHeritage autorise ou non le parcours de la hiérarchie des classes Java.
	 * @throws Exception renvoie des exceptions si le type des attributs n'est pas reconnu
	 */
	private void querySQLPostgis(String cheminClasse, String nomTable, boolean flagHeritage) throws Exception {
		String query="";
		Field[] attributs;
		Class<?> classe = Class.forName(cheminClasse);
		boolean flagGeomFeature = false;

		try {
			Connection conn = data.getConnection();
			Statement stm = conn.createStatement();

			if (flagHeritage){
				//String [][] dataNomAttributs, dataTypeAttributs;
				String nomAttribut, type;
				query = "CREATE TABLE "+nomTable+" (COGITID INTEGER PRIMARY KEY";
				while (!classe.getName().equals("java.lang.Object")){
					attributs = classe.getDeclaredFields();
					//dataNomAttributs = new String[attributs.length][1];
					//dataTypeAttributs = new String[attributs.length][1];
					if (!classe.getName().equals("fr.ign.cogit.geoxygene.feature.FT_Feature")){
						int i = 0;
						while(i!=attributs.length){
							//query = query+", ";
							Field attribut = attributs[i];
							nomAttribut = attribut.getName();
							if (!nomAttribut.equals("id")){
								try {
									type = javaType2PostgisType(attribut.getType().getName());
									query = query + ", "+ attribut.getName() +" "+type;
									//if (type.equals("GEOMETRY")) {
									//interface pour connaitre le type de géométrie (impossible à savoir interactivement)
									//stm.executeQuery("SELECT AddGeometrycolumn ('','"+nomTable+"','"+nomAttribut+"','-1','GEOMETRY',2)");
									//}
								} catch(Exception e) {
									System.out.println("Attribut "+attribut.getName()+ " non ajouté car son type n'est pas reconnu");
								}
							}
							i++;
						}
					}
					else {
						flagGeomFeature = true;
					}
					classe = classe.getSuperclass();
				}
				query = query + ")";
			}
			else {
				attributs = classe.getDeclaredFields();
				//String [][] dataNomAttributs = new String[attributs.length][1], dataTypeAttributs = new String[attributs.length][1];
				String nomAttribut, type;
				query = "CREATE TABLE "+nomTable+" (COGITID INTEGER PRIMARY KEY";
				int i = 0;
				while(i!=attributs.length){
					query = query+",";
					Field attribut = attributs[i];
					nomAttribut = attribut.getName();
					if (!nomAttribut.equals("id")){
						type = javaType2PostgisType(attribut.getType().getName());
						query = query +attribut.getName() +" "+type;
						if (type.equals("GEOMETRY")) {
							flagGeomFeature = true;
						}
					}
					i++;
				}
				query = query + ")";
			}

			try {
				System.out.println("query = "+query);
				stm.executeUpdate(query);//executeQuery(query);
			} catch (SQLException e) {
				System.out.println("création table échouée : "+e.getMessage());
				//e.printStackTrace();
			}

			//interface pour connaitre le type de géométrie (impossible à savoir interactivement)
			if (flagGeomFeature){
				GUISelectionGeometrie sg = new GUISelectionGeometrie();
				switch (sg.getTypeGeometrie()) {
				case 0:
					stm.executeUpdate("SELECT AddGeometrycolumn ('','"+nomTable+"','geom','-1','MULTIPOINT',"+sg.getDimensionGeometrie()+")");
					try {
						stm.executeUpdate("ALTER TABLE "+nomTable+" DROP CONSTRAINT enforce_geotype_geom");
					}
					catch (Exception e) {
					}
					stm.executeUpdate("ALTER TABLE "+nomTable+" ADD CONSTRAINT enforce_geotype_geom CHECK (geometrytype(geom) = 'POINT'::text OR geometrytype(geom) = 'MULTIPOINT'::text OR isempty(geom) OR geom IS NULL)");
					break;
				case 1:
					stm.executeUpdate("SELECT AddGeometrycolumn ('','"+nomTable+"','geom','-1','MULTILINESTRING',"+sg.getDimensionGeometrie()+")");
					try {
						stm.executeUpdate("ALTER TABLE "+nomTable+" DROP CONSTRAINT enforce_geotype_geom");
					}
					catch (Exception e) {
					}
					stm.executeUpdate("ALTER TABLE "+nomTable+" ADD CONSTRAINT enforce_geotype_geom CHECK (geometrytype(geom) = 'LINESTRING'::text OR geometrytype(geom) = 'MULTILINESTRING'::text OR isempty(geom) OR geom IS NULL)");
					break;
				case 2:
					stm.executeUpdate("SELECT AddGeometrycolumn ('','"+nomTable+"','geom','-1','MULTIPOLYGON',"+sg.getDimensionGeometrie()+")");
					try {
						stm.executeUpdate("ALTER TABLE "+nomTable+" DROP CONSTRAINT enforce_geotype_geom");
					}
					catch (Exception e) {
					}
					stm.executeUpdate("ALTER TABLE "+nomTable+" ADD CONSTRAINT enforce_geotype_geom CHECK (geometrytype(geom) = 'POLYGON'::text OR geometrytype(geom) = 'MULTIPOLYGON'::text OR isempty(geom) OR geom IS NULL)");
					break;
				case 3:
					stm.executeUpdate("SELECT AddGeometrycolumn ('','"+nomTable+"','geom','-1','GEOMETRYCOLLECTION',"+sg.getDimensionGeometrie()+")");
					stm.executeUpdate("ALTER TABLE "+nomTable+" DROP CONSTRAINT enforce_geotype_geom");
					break;
				default:
					break;
				}
			}
			stm.close();
		}
		catch (Exception e) {
			System.out.println("échec dans querySQLPostgis");
			e.printStackTrace();
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static String javaType2OracleType(String javaType) throws Exception {

		if (javaType.compareToIgnoreCase("java.lang.String")==0)return "VARCHAR(255)";
		else if (javaType.compareToIgnoreCase("double")==0)return "NUMBER";
		else if (javaType.compareToIgnoreCase("int")==0)return "INTEGER";
		else if (javaType.compareToIgnoreCase("boolean")==0)return "CHAR(1)";
		else if (javaType.compareToIgnoreCase("fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object") == 0) return "MDSYS.SDO_GEOMETRY";
		else if (javaType.compareToIgnoreCase("fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point") == 0) return "MDSYS.SDO_GEOMETRY";
		else if (javaType.compareToIgnoreCase("fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString") == 0) return "MDSYS.SDO_GEOMETRY";
		else if (javaType.compareToIgnoreCase("fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon")==0)return "MDSYS.SDO_GEOMETRY";
		else throw new Exception("type non reconnu : "+javaType);
	}

	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static String javaType2PostgisType(String javaType) throws Exception {

		if (javaType.endsWith("GM_LineString"))return "ligne";
		else if (javaType.endsWith("String"))return "VARCHAR";
		else if (javaType.endsWith("boolean"))return "boolean";
		else if (javaType.endsWith("double"))return "double precision";
		else if (javaType.endsWith("GM_Point"))return "point";
		else if (javaType.endsWith("int"))return "INTEGER";
		else if (javaType.endsWith("GM_Polygon"))return "surface";
		//else if (javaType.endsWith("List")) return "liste";
		else if (javaType.compareToIgnoreCase("fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object") == 0) return "GEOMETRY";
		else throw new Exception("type non reconnu : "+javaType);
	}

}
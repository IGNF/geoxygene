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

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;

/**
 * Usage interne. Appele par EasyLoader ou par la Console.
 * En faisant une boucle sur la liste allTables, genere le fichier de mapping,
 * remplit le dico et genere les classes java.
 * On peut eventuellement choisir le nom des tables (si on a acces a une ligne de commande :
 * passer un BufferedReader (parametre "in" non null au constructeur).
 *
 * <br> AB 18 juillet 2005 : gestion des clés primaires (possiblité d'utiliser une clé primaire existante).
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

public class XMLJavaDicoGenerator {

	private BufferedReader in;
	private Geodatabase data;
	private OjbXMLGenerator theXMLGenerator;
	private DicoGenerator theDicoGenerator;
	private String geOxygeneData; // path
	private String geOxygeneMapping; // path
	private List<String> allTables;
	private String extentClassName;
	//private String extentMappingFileName;
	private boolean flagInterroTable;
	private String packageName;
	private String userName;





	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public XMLJavaDicoGenerator(BufferedReader In, Geodatabase Data,
			boolean FlagInterroTable,List<String> AllTables,
			String GeOxygeneData,String GeOxygeneMapping,
			String PackageName,String ExtentClassName,
			String mappingFileName,String extentMappingFileName) {
		if (In != null) this.in = In;
		this.data = Data;
		try {
			this.userName = this.data.getConnection().getMetaData().getUserName();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		this.theDicoGenerator = new DicoGenerator(this.data);
		this.extentClassName = ExtentClassName;
		this.geOxygeneData = GeOxygeneData;
		this.geOxygeneMapping = GeOxygeneMapping;
		this.theXMLGenerator = new OjbXMLGenerator(this.data,this.geOxygeneMapping,mappingFileName, this.extentClassName, extentMappingFileName );
		this.flagInterroTable = FlagInterroTable;
		this.allTables =AllTables;
		this.packageName = PackageName;

	}


	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void writeAll()  {

		this.theXMLGenerator.writeFileHeader();

		try {
			Connection conn = this.data.getConnection();
			Statement stm = conn.createStatement();

			// boucle sur les tables geographiques a traiter
			for (int i=0; i<this.allTables.size(); i++) {

				boolean flagInterroFields = false;  // examen individuel des champs ?
				System.out.println();

				String sqlTableName = this.allTables.get(i);
				String javaClassName = this.packageName+"."+sqlTableName.substring(0,1).toUpperCase()+sqlTableName.substring(1).toLowerCase(); //$NON-NLS-1$

				if (this.flagInterroTable) {
					Message m = new Message(this.in,"table : "+sqlTableName+"\nnom propose pour la classe Java : "+javaClassName+"\nOK ?","o","n");
					String r = m.getAnswer();
					if (r.compareToIgnoreCase("n")==0)  {
						m = new Message(this.in,"entre le nom de la classe Java (ATTENTION sans oublier le package en prefixe):");
						javaClassName = m.getAnswer();
					}
				}

				System.out.println("table : "+sqlTableName+"\nnom de la classe java : "+javaClassName);
				JavaGenerator aJavaGenerator = new JavaGenerator(this.geOxygeneData,javaClassName, this.extentClassName, this.packageName);
				aJavaGenerator.writeHeader();
				this.theXMLGenerator.writeClassHeader(javaClassName,sqlTableName);
				this.theDicoGenerator.writeFeature(javaClassName);

				if (this.flagInterroTable) {
					Message m = new Message(this.in,"examen individuel des attributs (sinon on les charge tous) ?","o","n");
					String r = m.getAnswer();
					if (r.compareToIgnoreCase("o")==0)  flagInterroFields = true;
				}


				this.theDicoGenerator.writeAttribute(javaClassName,"id","int");  //$NON-NLS-1$//$NON-NLS-2$

				// Boucle sur les colonnes
				String query = getQueryColumnName(sqlTableName,this.userName);
				conn.commit();
				ResultSet rs = stm.executeQuery(query);
				while (rs.next()) {

					// La colonne SQL
					String sqlColumnName = rs.getString(1);

					// Le type SQL
					String sqlDbmsType = rs.getString(2);

					// Si c'est le champ COGITID : on passe
					if (sqlColumnName.compareToIgnoreCase("COGITID") == 0) //$NON-NLS-1$
						continue;

					// bidouille speciale Oracle pour traiter le cas des entiers ...
					if (this.data.getDBMS() == Geodatabase.ORACLE)
						if (rs.getObject(3) != null) {
							int dataScale = ((BigDecimal)rs.getObject(3)).intValue();
							if ((sqlDbmsType.compareToIgnoreCase("NUMBER")==0) && //$NON-NLS-1$
									(dataScale == 0)) sqlDbmsType ="INTEGER"; //$NON-NLS-1$
						}
					// fin de la bidouille

					// bidouille speciale Oracle pour traiter le cas des booleans ...
					// Les booleans ne sont pas reconnus par Oracle JDBC.
					// On suppose que CHAR(1) est un boolean
					if (this.data.getDBMS() == Geodatabase.ORACLE)
						if (rs.getObject(3) != null) {
							int dataScale = ((BigDecimal)rs.getObject(3)).intValue();
							if ((sqlDbmsType.compareToIgnoreCase("CHAR")==0) && //$NON-NLS-1$
									(dataScale == 1)) sqlDbmsType ="BOOLEAN"; //$NON-NLS-1$
						}
					// fin de la bidouille

					// Le type Java
					String javaType = getJavaType(sqlDbmsType);

					// Gestion exception
					if (javaType.compareTo("") == 0) continue; //$NON-NLS-1$

					// Le nom Java
					String javaFieldName = sqlColumnName.toLowerCase();

					// attention : le champ portant la geometrie doit s'appeler geom (heritage de FT_Feature")
					if ((javaType.compareToIgnoreCase("GM_Object")==0)) { //$NON-NLS-1$
						javaFieldName = "geom"; //$NON-NLS-1$
					}
					// attention : population est un nom de champ de FT_Feature
					if (javaFieldName.equals("population")) javaFieldName = "population_"; //$NON-NLS-1$ //$NON-NLS-2$

					if (flagInterroFields) {
						if ((javaFieldName.compareToIgnoreCase("id") != 0) && //$NON-NLS-1$
								(javaFieldName.compareToIgnoreCase("geom")!=0)) { //$NON-NLS-1$
							Message m = new Message(this.in,"colonne "+sqlColumnName+" : on la charge ?","o","n");
							String r = m.getAnswer();
							if (r.compareToIgnoreCase("o")==0)  {
								m = new Message(this.in,"colonne : "+sqlColumnName+"\nnom propose pour l'attribut Java : "+javaFieldName+"\nOK ?","o","n");
								r = m.getAnswer();
								if (r.compareToIgnoreCase("n")==0)  {
									m = new Message(this.in,"entre le nom de l'attribut Java :");
									javaFieldName = m.getAnswer();
								}
								this.theXMLGenerator.writeField(javaFieldName,sqlColumnName,sqlDbmsType);
								if ((javaFieldName.compareToIgnoreCase("id")!=0) && //$NON-NLS-1$
										(javaFieldName.compareToIgnoreCase("geom")!=0)) //$NON-NLS-1$
									aJavaGenerator.writeField(javaType,javaFieldName);
								this.theDicoGenerator.writeAttribute(javaClassName,javaFieldName,javaType);
							}
						} else {
							this.theXMLGenerator.writeField(javaFieldName,sqlColumnName,sqlDbmsType);
							this.theDicoGenerator.writeAttribute(javaClassName,javaFieldName,javaType);
						}

					} else {
						this.theXMLGenerator.writeField(javaFieldName,sqlColumnName,sqlDbmsType);

						// Ecriture du champ dans la classe java
						// Ces champs ne sont pas écrits dans la classe java car ils héritent de FT_Feature
						if ((javaFieldName.compareToIgnoreCase("id")!=0) && //$NON-NLS-1$
								(javaFieldName.compareToIgnoreCase("geom")!=0)) //$NON-NLS-1$
							aJavaGenerator.writeField(javaType,javaFieldName);

						// Ecriture dans le dictionnaire des données
						this.theDicoGenerator.writeAttribute(javaClassName,javaFieldName,javaType);
					}
					System.out.println("    nom sql : "+sqlColumnName+"\n   nom java : "+javaFieldName);

				}

				rs.close();
				this.theXMLGenerator.writeClassBottom();
				aJavaGenerator.writeBottom();
			}

			stm.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		this.theXMLGenerator.writeFileBottom();
		this.theXMLGenerator.writeInFile();
	}



	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private String getQueryColumnName(String tableName, String user) {
		if (this.data.getDBMS() == Geodatabase.ORACLE)
			return getQueryColumnNameOracle(tableName);
		else if (this.data.getDBMS() == Geodatabase.POSTGIS)
			return getQueryColumnNamePostgis(tableName,user);
		return null;
	}



	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private String getQueryColumnNameOracle(String tableName) {
		return "SELECT COLUMN_NAME, DATA_TYPE, DATA_SCALE FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '"+tableName+"'"; //$NON-NLS-1$ //$NON-NLS-2$
	}



	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private String getQueryColumnNamePostgis(String tableName, String user) {
		return 	"select pg_attribute.attname, pg_type.typname "+ //$NON-NLS-1$
		"from pg_attribute, pg_type, pg_class, pg_user "+ //$NON-NLS-1$
		"where pg_class.oid = pg_attribute.attrelid "+ //$NON-NLS-1$
		"and pg_attribute.attnum>0 "+ //$NON-NLS-1$
		"and pg_attribute.atttypid = pg_type.oid "+ //$NON-NLS-1$
		"and pg_class.relowner = pg_user.usesysid "+ //$NON-NLS-1$
		"and pg_user.usename = '"+user.toLowerCase()+"' "+ //$NON-NLS-1$ //$NON-NLS-2$
		"and pg_class.relname='"+tableName.toLowerCase()+"'"; //$NON-NLS-1$ //$NON-NLS-2$
	}



	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private String getJavaType(String sqlType) {
		try {
			if (this.data.getDBMS() == Geodatabase.ORACLE)
				return oracleType2javaType(sqlType);
			else if (this.data.getDBMS() == Geodatabase.POSTGIS)
				return postgisType2javaType(sqlType);
			return ""; //$NON-NLS-1$
		} catch (Exception e) {
			e.printStackTrace();
			return ""; //$NON-NLS-1$
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private String oracleType2javaType(String oracle) throws Exception {
		if (oracle.compareToIgnoreCase("VARCHAR2") == 0) return "String"; //$NON-NLS-1$ //$NON-NLS-2$
		else if (oracle.compareToIgnoreCase("VARCHAR") == 0) return "String";  //$NON-NLS-1$//$NON-NLS-2$
		else if (oracle.compareToIgnoreCase("CHAR") == 0) return "String";  //$NON-NLS-1$//$NON-NLS-2$
		else if (oracle.compareToIgnoreCase("NUMBER") == 0) return "double";  //$NON-NLS-1$//$NON-NLS-2$
		else if (oracle.compareToIgnoreCase("FLOAT") == 0) return "double";  //$NON-NLS-1$//$NON-NLS-2$
		else if (oracle.compareToIgnoreCase("INTEGER") == 0) return "int";  //$NON-NLS-1$//$NON-NLS-2$
		else if (oracle.compareToIgnoreCase("BOOLEAN") == 0) return "boolean";  //$NON-NLS-1$//$NON-NLS-2$
		else if (oracle.compareToIgnoreCase("SDO_GEOMETRY") == 0) return "GM_Object";  //$NON-NLS-1$//$NON-NLS-2$
		else throw new Exception("type non reconnu : "+oracle);  //$NON-NLS-1$
	}



	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////////////////
	private String postgisType2javaType(String postgis) throws Exception {
		if (postgis.compareToIgnoreCase("varchar") == 0) return "String";  //$NON-NLS-1$//$NON-NLS-2$
		else if (postgis.compareToIgnoreCase("bpchar") == 0) return "String";  //$NON-NLS-1$//$NON-NLS-2$
		else if (postgis.compareToIgnoreCase("float8") == 0) return "double";  //$NON-NLS-1$//$NON-NLS-2$
		else if (postgis.compareToIgnoreCase("float4") == 0) return "float";  //$NON-NLS-1$//$NON-NLS-2$
		else if (postgis.compareToIgnoreCase("int4") == 0) return "int";  //$NON-NLS-1$//$NON-NLS-2$
		else if (postgis.compareToIgnoreCase("int8") == 0) return "long";  //$NON-NLS-1$//$NON-NLS-2$
		else if (postgis.compareToIgnoreCase("bool") == 0) return "boolean";  //$NON-NLS-1$//$NON-NLS-2$
		else if (postgis.compareToIgnoreCase("geometry") == 0) return "GM_Object";  //$NON-NLS-1$//$NON-NLS-2$
		else throw new Exception("type non reconnu : "+postgis);  //$NON-NLS-1$
	}

}

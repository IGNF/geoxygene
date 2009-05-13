/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.feature;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;

/**
 * Description du schéma logique d'un DefaultFeature. Le plus souvent ce schéma est lu à partir de
 * la table de SGBD où sont stockées les données.
 * Ce schéma contient le nom de la table (ou du fichier GML ou autre...) et une lookup table
 * indiquant le nom des attributs et leur emplacement dans la table attributes[] du defaultFeature.
 * Dans le cas où une métadonnée de structure était disponible (soit stockée quelque part soit
 * donnée par l'utilisateur lors du chargement), ce schéma contient aussi une référence vers le
 * schéma conceptuel : le featureType correspondant au DefaultFeature.
 * 
 * @author Sandrine Balley
 */

public class SchemaDefaultFeature extends SchemaConceptuelJeu {
	/**
	 * "postgis" ou "oracle" ou "GML"
	 */
	private int typeBD;
	public static final int POSTGIS=1;
	public static final int ORACLE=2;
	public static final int GML=3;
	/**
	 * nom de la table ou du gml:featureType
	 */
	private String nom;
	/**
	 * valable dans les cas POSTGIS et ORACLE
	 */
	private List<String> colonnes;
	/**
	 * Map utilisée pour retrouver la position d'un attribut en fonction de son nom ou vice-versa.
	 * <K, V>
	 * Key = le numéro de l'attribut dans la table feature.attributes
	 * Value = [nom colonne, nom FeatureType]. Par défaut, si le second est renseigné,
	 * c'est lui qui est utilisé. Sinon c'est le premier (utile quand on ne connaît
	 * pas le schéma conceptuel et qu'on souhaite utiliser le schéma logique à la place.)
	 */
	private Map<Integer,String[]> attLookup;
	/**
	 * Constructeur vide.
	 */
	public SchemaDefaultFeature(){this.attLookup = new HashMap<Integer,String[]>();}
	/**
	 * Charge le schéma à partir d'une base de données
	 * @param conn connection à la base de données
	 */
	public void chargeLookup(Connection conn){
		this.attLookup.clear();
		if ((typeBD==POSTGIS)||(typeBD==ORACLE)){
			try {
				String sqlText = "select * from "+this.nom;
				Statement sql = conn.createStatement();
				ResultSet results = sql.executeQuery(sqlText);
				System.out.println("nb col = "+results.getMetaData().getColumnCount());
				String[] tabNoms;
				for (int i=1 ; i<=results.getMetaData().getColumnCount() ; i++){
					tabNoms=new String[2];
					System.out.println("nom col = "+results.getMetaData().getColumnName(i));
					//System.out.println("schemaName = "+results.getMetaData().getSchemaName(i));
					System.out.println("column type = "+results.getMetaData().getColumnTypeName(i));
					System.out.println("className = "+results.getMetaData().getColumnClassName(i));
					//je mets la géométrie nommée "geom" dans la case 0
					if(results.getMetaData().getColumnName(i).equals("geom")){
						tabNoms[0]="geom";
						this.attLookup.put(0, tabNoms);
					}
					//puis les autres attributs dans les cases à partir de 1
					if ((!results.getMetaData().getColumnName(i).equals("geom"))&(!results.getMetaData().getColumnName(i).equals("cogitid"))){
						tabNoms[0]=results.getMetaData().getColumnName(i);
						this.attLookup.put(i, tabNoms);
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			System.out.println("\nje relis ce que j'ai trouvé : ");
			//System.out.println("0 = "+attLookup.get(0)[0]+" - "+attLookup.get(0)[1]);
			System.out.println(attLookup.keySet());
			for (int i = 0 ; i<attLookup.size() ; i++){
				System.out.println(i+" = "+attLookup.get(i)[0]+" - "+attLookup.get(i)[1]);
			}
		}
		else{
			System.out.println("je ne sais pas encore charger le schéma " +
			"d'un defaultFeature depuis un GML");
		}
	}
	/**
	 * lien avec le schéma conceptuel
	 */
	private FeatureType featureType;
	/**
	 * @return the colonnes
	 */
	public List<String> getColonnes() {return colonnes;}
	/**
	 * @param colonnes the colonnes to set
	 */
	public void setColonnes(List<String> colonnes) {this.colonnes = colonnes;}
	/**
	 * @return the lookup
	 */
	public Map<Integer,String[]> getAttLookup() {return attLookup;}
	/**
	 * @param lookup the lookup to set
	 */
	public void setAttLookup(Map<Integer,String[]> lookup) {this.attLookup = lookup;}
	/**
	 * Le nom du schéma
	 * @return Le nom du schéma
	 */
	public String getNom() {return nom;}
	/**
	 * Affecte le nom du schéma.
	 * @param nom le nom du schéma
	 */
	public void setNom(String nom) {this.nom = nom;}
	/**
	 * Renvoie le type de Base de données.
	 * @return le type de Base de données
	 */
	public int getTypeBD() {return typeBD;}
	/**
	 * Affecte le type de Base de données.
	 * @param typeBD le type de Base de données
	 */
	public void setTypeBD(int typeBD) {this.typeBD = typeBD;}
	/**
	 * Le feature type.
	 * @return le feature type
	 */
	public FeatureType getFeatureType() {return featureType;}
	/**
	 * Affecte le feature type.
	 * @param featureType le feature type
	 */
	public void setFeatureType(FeatureType featureType) {this.featureType = featureType;}
}

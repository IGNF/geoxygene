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

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import fr.ign.cogit.geoxygene.datatools.Geodatabase;

/**
 * Genere les identifiant d'une table (colonne COGITID).
 * Dans l'ideal, plusieurs types de generation devraient etre possibles :
 * Simple (i.e. de 1 a N), unicite sur toutes les tables geographiques de la base,
 * empreinte numerique, recopie d'une autre colonne ...
 * Pour l'instant seuls simple,
 * et unicite sur toutes les tables geographiques de la base (par l'algo du max) fonctionnent.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

public class GenerateIds {

	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	private Geodatabase data;
	private String tableName;
	private int maxID = 0;  // identifiant maximum des cogitid dans tout le jeu
	private boolean unique;  // veut-on des identifiants uniques sur toute la base ?

	private final static String ORACLE_COLUMN_QUERY = "SELECT TABLE_NAME FROM USER_SDO_GEOM_METADATA"; //$NON-NLS-1$
	private final static String POSTGIS_COLUMN_QUERY = "SELECT F_TABLE_NAME FROM GEOMETRY_COLUMNS"; //$NON-NLS-1$



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	public GenerateIds (Geodatabase Data, String TableName, boolean Unique) {
		this.data = Data;
		this.tableName = TableName;
		this.unique = Unique;
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	public void genere() {
		dropColumnID();
		addColumnID();
		if (this.unique) {
			if (this.data.getDBMS() == Geodatabase.ORACLE) maxCOGITID(ORACLE_COLUMN_QUERY);
			else if (this.data.getDBMS() == Geodatabase.POSTGIS) maxCOGITID(POSTGIS_COLUMN_QUERY);
		}
		if (this.data.getDBMS() == Geodatabase.ORACLE) genereIDOracle();
		else if (this.data.getDBMS() == Geodatabase.POSTGIS) genereIDPostgres();
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// ajoute une colonne "COGITID" et appelle la methode genereID
	void addColumnID () {
		try {
			Connection conn = this.data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			String query = "ALTER TABLE "+this.tableName+" ADD COGITID INTEGER"; //$NON-NLS-1$ //$NON-NLS-2$
			stm.executeUpdate(query);
			System.out.println(this.tableName+" : colonne CogitID creee");
			stm.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// supprime la colonne cogitid
	void dropColumnID () {
		try {
			Connection conn = this.data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			try {
				String query = "ALTER TABLE "+this.tableName+" DROP COLUMN COGITID"; //$NON-NLS-1$ //$NON-NLS-2$
				stm.executeUpdate(query);
				System.out.println(this.tableName+" : colonne CogitID effacee");
			} catch (Exception ee) { // pas de colonne cogitid !!
				conn.commit();
			}
			stm.close();
			conn.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// genere les identifiants dans la colonne COGITID, puis cree une cle primaire sur cette colonne
	void genereIDOracle() {
		try {
			Connection conn = this.data.getConnection();
			Statement stm = conn.createStatement();
			String query = "SELECT COUNT(*) FROM "+this.tableName; //$NON-NLS-1$
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) {
				int nbCount = ((BigDecimal)rs.getObject(1)).intValue();
				System.out.println(nbCount+" objets dans la table "+this.tableName+" ... generation des identifiants ...");
			}

			// creation de la procedure PL/SQL de generation des clés
			// A FAIRE : y a moyen de faire plus simple : utiliser séquence ?
			// Ou utiliser la fonction 'cursor for update' et 'current of'
			String proc = "CREATE OR REPLACE PROCEDURE genere_cogitid AS"; //$NON-NLS-1$
			proc=proc+" BEGIN"; //$NON-NLS-1$
			proc=proc+" DECLARE"; //$NON-NLS-1$
			proc=proc+" i integer := "+this.maxID+";"; //$NON-NLS-1$ //$NON-NLS-2$
			proc=proc+" cursor c is select rowid from "+this.tableName+";"; //$NON-NLS-1$ //$NON-NLS-2$
			proc=proc+" therowid rowid;"; //$NON-NLS-1$
			proc=proc+" BEGIN"; //$NON-NLS-1$
			proc=proc+" if i is null then i := 0; end if;"; //$NON-NLS-1$
			proc=proc+" open c;"; //$NON-NLS-1$
			proc=proc+" LOOP"; //$NON-NLS-1$
			proc=proc+" fetch c into therowid;"; //$NON-NLS-1$
			proc=proc+" exit when c%notfound;"; //$NON-NLS-1$
			proc=proc+" i := i+1;"; //$NON-NLS-1$
			proc=proc+" update "+this.tableName+" set cogitid=i where rowid=therowid;"; //$NON-NLS-1$ //$NON-NLS-2$
			proc=proc+" END LOOP;"; //$NON-NLS-1$
			proc=proc+" close c;"; //$NON-NLS-1$
			proc=proc+" END;"; //$NON-NLS-1$
			proc=proc+" END genere_cogitid;"; //$NON-NLS-1$
			stm.execute(proc);

			// execution de la procedure
			CallableStatement cstm = conn.prepareCall ("begin GENERE_COGITID; end;"); //$NON-NLS-1$
			cstm.execute();
			cstm.close();

			// on enleve si ancienne cle primaire
			try {
				String update = "ALTER TABLE "+this.tableName+" DROP PRIMARY KEY"; //$NON-NLS-1$ //$NON-NLS-2$
				stm.executeUpdate(update);
				System.out.println("cle primaire sur "+this.tableName+" supprimee");
			} catch (Exception e1) {
				System.out.println("aucune cle primaire sur "+this.tableName);
			}

			// ajout de la cle primaire
			String update = "ALTER TABLE "+this.tableName+" ADD PRIMARY KEY (COGITID)"; //$NON-NLS-1$ //$NON-NLS-2$
			stm.executeUpdate(update);
			System.out.println("cle primaire sur "+this.tableName+" ajoutee (colonne COGITID)");

			// fin
			stm.close();
			conn.commit();
		} catch (Exception e) {
			System.out.println(this.tableName);
			e.printStackTrace();
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// genere les identifiants dans la colonne COGITID, puis cree une cle primaire sur cette colonne
	void genereIDPostgres() {
		try {
			Connection conn = this.data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			String query = "SELECT COUNT(*) FROM "+this.tableName; //$NON-NLS-1$
			ResultSet rs = stm.executeQuery(query);
			while (rs.next()) {
				int nbCount = ((Number)rs.getObject(1)).intValue();
				System.out.println(nbCount+" objets dans la table "+this.tableName+" ... generation des identifiants ...");
			}

			// création d'une séquence
			try {
				String update = "create SEQUENCE seq_genere_cogitid"; //$NON-NLS-1$
				stm.executeUpdate(update);
			} catch (Exception ee) {
				// La séquence existe déjà !
				conn.commit();
			}
			conn.commit();

			// Si le maxID vaut 0 (il n'y a pas encore d'identifiant dans la base), on le force à 1
			if (this.maxID==0) this.maxID=1;
			// Affectation du maxID à la séquence
			// On a pas besoin de l'affecter à maxID+1 puisque l'on utilise toujours nextval pour affecter les identifiants
			query = "SELECT setval ('seq_genere_cogitid', "+this.maxID+")"; //$NON-NLS-1$ //$NON-NLS-2$
			rs = stm.executeQuery(query);
			while (rs.next()) { }
			conn.commit();

			// Mise à jour de la table à l'aide de la sequence
			String update = "update "+this.tableName+" set cogitid = nextval('seq_genere_cogitid')"; //$NON-NLS-1$ //$NON-NLS-2$
			stm.executeUpdate(update);
			conn.commit();

			// on enleve si ancienne cle primaire
			// Arnaud 28 oct : modif
			query = "select con.conname, con.contype from pg_constraint con, pg_class cl"; //$NON-NLS-1$
			query = query+" where con.conrelid = cl.oid"; //$NON-NLS-1$
			query = query+" and cl.relname='"+this.tableName+"'"; //$NON-NLS-1$ //$NON-NLS-2$
			rs = stm.executeQuery(query);
			String conName = ""; //$NON-NLS-1$
			while (rs.next()) {
				String conType = rs.getString(2);
				if (conType.compareToIgnoreCase("p") == 0) //$NON-NLS-1$
					conName = rs.getString(1);
			}
			if (conName.compareTo("") != 0) { //$NON-NLS-1$
				update = "ALTER TABLE "+this.tableName+" DROP CONSTRAINT "+conName; //$NON-NLS-1$ //$NON-NLS-2$
				stm.executeUpdate(update);
				System.out.println("cle primaire sur "+this.tableName+" supprimé : "+conName);
			}

			// ajout de la cle primaire
			update = "ALTER TABLE "+this.tableName+" ADD PRIMARY KEY (COGITID)"; //$NON-NLS-1$ //$NON-NLS-2$
			stm.executeUpdate(update);
			System.out.println("cle primaire sur "+this.tableName+" ajoutee (colonne COGITID)");

			// fin
			stm.close();
			conn.commit();
		} catch (Exception e) {
			System.out.println(this.tableName);
			e.printStackTrace();
		}
	}



	///////////////////////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////
	// recherche du COGITID maximum parmi les tables Géographiques (variable globale maxID)
	public void maxCOGITID(String query) {
		try {
			Connection conn = this.data.getConnection();
			conn.commit();
			Statement stm = conn.createStatement();
			ResultSet rs = stm.executeQuery(query);
			List<String> listOfTables = new ArrayList<String>();
			while (rs.next())
				listOfTables.add(rs.getString(1));
			Iterator<String> it = listOfTables.iterator();
			while (it.hasNext()) {
				String aTableName = it.next();
				try {
					query = "SELECT MAX(COGITID) FROM "+aTableName; //$NON-NLS-1$
					rs= stm.executeQuery(query);
					int max = 0;
					while (rs.next())
						max = ((Number)rs.getObject(1)).intValue();
					if (max > this.maxID) this.maxID = max;
				} catch (Exception ee) {    // pas de colonne cogitID
					conn.commit();
				}
			}
			stm.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

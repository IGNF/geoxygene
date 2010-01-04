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

package fr.ign.cogit.geoxygene.datatools.ojb;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.PersistenceBrokerSQLException;
import org.apache.ojb.broker.accesslayer.ConnectionManagerIF;
import org.apache.ojb.broker.accesslayer.LookupException;
import org.apache.ojb.broker.accesslayer.StatementManager;
import org.apache.ojb.broker.accesslayer.StatementManagerIF;
import org.apache.ojb.broker.accesslayer.StatementsForClassFactory;
import org.apache.ojb.broker.accesslayer.StatementsForClassIF;
import org.apache.ojb.broker.core.ValueContainer;
import org.apache.ojb.broker.metadata.ArgumentDescriptor;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.ProcedureDescriptor;
import org.apache.ojb.broker.platforms.Platform;
import org.apache.ojb.broker.platforms.PlatformException;
import org.apache.ojb.broker.platforms.PlatformFactory;
import org.apache.ojb.broker.platforms.PlatformOracle9iImpl;
import org.apache.ojb.broker.platforms.PlatformOracleImpl;
import org.apache.ojb.broker.platforms.PlatformPostgreSQLImpl;
import org.apache.ojb.broker.query.BetweenCriteria;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.ExistsCriteria;
import org.apache.ojb.broker.query.FieldCriteria;
import org.apache.ojb.broker.query.InCriteria;
import org.apache.ojb.broker.query.NullCriteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.SelectionCriteria;
import org.apache.ojb.broker.query.SqlCriteria;
import org.apache.ojb.broker.util.JdbcTypesHelper;
import org.postgis.PGgeometry;

//import fr.ign.cogit.geoxygene.datatools.oracle.ArrayGeOxygene2Oracle;
//import fr.ign.cogit.geoxygene.datatools.oracle.GeomGeOxygene2Oracle;

/**
 * Meme chose que la classe org.apache.ojb.broker.accesslayer.StatementManager.
 * Permet de gerer les structures Oracle en passant un parametre de connection a un endroit.
 * Par rapport a la version originale de StatementManager :
 *  les imports ont ete reorganises,
 *  le constructeur renomme,
 *  changement de la nature de l'attribut m_broker en GeOxygenePersistenceBrokerImpl,
 *  changement d'une affectation de cet attribut dans le constructeur,
 *  un ajout dans la methode bindStatementValue(),
 *  changement dans getAllValues,
 *  changement dans getKeyValues (2 methodes),
 *  changement dans getNonKeyValues
 *  modification des " setNull " (plusieurs) pour gerer le cas de l'ecriture de geometries nulles.
 * 
 * AB 11 juillet 2005 :
 * <br> Utilisation des noms de classes et de la réflection pour permettre la compilation séparée pour Oracle.
 * <br> Patch pour permettre l'utilisation de la meme classe de "FieldConversion" pour Oracle et Postgis.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */

public class GeOxygeneStatementManager implements StatementManagerIF {
	static Logger logger=Logger.getLogger(StatementManager.class.getName());

	/** internal table of StatementForClass objects */
	private Map<ClassDescriptor,StatementsForClassIF> m_statementTable = new WeakHashMap<ClassDescriptor,StatementsForClassIF>();
	/** the associated broker */

	//    private final PersistenceBroker m_broker;   initial OJB
	private final GeOxygenePersistenceBrokerImpl m_broker;   // GeOxygene

	private Platform m_platform;
	/**
	 * Used when OJB run in JBoss
	 * Find a better solution to handle OJB within JBoss
	 * --> the JCA implementation should solve this problem
	 */
	private boolean m_eagerRelease;
	private ConnectionManagerIF m_conMan;

	// AJOUT pour GeOxygene ---------------------------------------------------
	// Nom des classes relatives à Oracle,
	//en String pour permettre la compilation séparée
	private final String GeomGeOxygene2Oracle_CLASS_NAME =
		"fr.ign.cogit.geoxygene.datatools.oracle.GeomGeOxygene2Oracle"; //$NON-NLS-1$
	private final String ArrayGeOxygene2Oracle_CLASS_NAME =
		"fr.ign.cogit.geoxygene.datatools.oracle.ArrayGeOxygene2Oracle"; //$NON-NLS-1$
	private final String GeomGeOxygene2Postgis_CLASS_NAME =
		"fr.ign.cogit.geoxygene.datatools.postgis.GeomGeOxygene2Postgis"; //$NON-NLS-1$
	private Class<?> arrayGeOxygene2OracleClass;
	private Method geomGeOxygene2OracleMethod;
	private Method arrayGeOxygene2OracleMethod;
	private Method geomGeOxygene2PostgisMethod;
	// FIN AJOUT pour GeOxygene ---------------------------------------------------


	public GeOxygeneStatementManager(final PersistenceBroker pBroker)
	{
		//        this.m_broker = pBroker;   // initial OJB
		this.m_broker = (GeOxygenePersistenceBrokerImpl) pBroker; // GeOxygene

		this.m_conMan = this.m_broker.serviceConnectionManager();
		this.m_eagerRelease = this.m_conMan.getConnectionDescriptor().getEagerRelease();
		this.m_platform = PlatformFactory.getPlatformFor(this.m_conMan.getConnectionDescriptor());

		// AJOUT pour GeOxygene -----------------------------------------------------------
		// ORACLE
		if (this.m_platform instanceof PlatformOracle9iImpl || this.m_platform instanceof PlatformOracleImpl)
			try {
				Class<?> geomGeOxygene2OracleClass = Class.forName(this.GeomGeOxygene2Oracle_CLASS_NAME);
				this.arrayGeOxygene2OracleClass = Class.forName(this.ArrayGeOxygene2Oracle_CLASS_NAME);
				this.geomGeOxygene2OracleMethod = geomGeOxygene2OracleClass.getMethod("javaToSql", //$NON-NLS-1$
						new Class[] {Object.class, Connection.class});
				this.arrayGeOxygene2OracleMethod = this.arrayGeOxygene2OracleClass.getMethod("javaToSql", //$NON-NLS-1$
						new Class[] {Object.class, Connection.class});
			} catch (Exception e) {
				e.printStackTrace();
			}

			// POSTGIS
			else if (this.m_platform instanceof PlatformPostgreSQLImpl)
				try {
					Class<?> geomGeOxygene2PostgisClass = Class.forName(this.GeomGeOxygene2Postgis_CLASS_NAME);
					this.geomGeOxygene2PostgisMethod = geomGeOxygene2PostgisClass.getMethod("javaToSql", //$NON-NLS-1$
							new Class[] {Object.class});
				} catch (Exception e) {
					e.printStackTrace();
				}

				// AUTRE DBMS
				else {
					logger.fatal("## Le SGBD n'est ni Oracle, ni PostgreSQL ##");
					logger.fatal("## Le programme s'arrête ##");
					System.exit(0);
				}
		// FIN AJOUT pour GeOxygene ---------------------------------------------------


	}



	/**
	 * return a StatementsForClass object for the given ClassDescriptor\
	 * Note; not important to synchronize completely as a threading issue in this code
	 * will only result in a little extra code being executed
	 */
	protected StatementsForClassIF getStatementsForClass(ClassDescriptor cds) throws PersistenceBrokerException
	{
		StatementsForClassIF sfc = this.m_statementTable.get(cds);
		if (sfc == null)
		{
			synchronized (this.m_statementTable)
			{
				// 07.17.2003 - RB: StatementsForClassImpl is now configurable
				//sfc = (StatementsForClassIF) new StatementsForClassImpl(m_conMan.getConnectionDescriptor(), cds);
				sfc = StatementsForClassFactory.getInstance().getStatementsForClass(this.m_conMan.getConnectionDescriptor(), cds);
				this.m_statementTable.put(cds, sfc);
			}
		}
		return sfc;
	}

	public void closeResources(Statement stmt, ResultSet rs)
	{
		//if (logger.isDebugEnabled()) logger.debug("closeResources was called");
		try
		{
			this.m_platform.beforeStatementClose(stmt, rs);
			//close statement on wrapped statement class, or real statement
			if (stmt != null)
			{
				//log.info("## close: "+stmt);
				stmt.close();

				/*
				 *********************************************
                special stuff for OJB within JBoss
				 ********************************************
				 */
				if (this.m_eagerRelease)
				{
					this.m_conMan.releaseConnection();
				}

			}
			this.m_platform.afterStatementClose(stmt, rs);
		}
		catch (PlatformException e)
		{
			logger.error("Platform dependent operation failed", e);
		}
		catch (SQLException ignored)
		{
			if (logger.isDebugEnabled()) logger.debug("Statement closing failed", ignored);
		}
	}

	/**
	 * binds the Identities Primary key values to the statement
	 */
	public void bindDelete(PreparedStatement stmt, Identity oid, ClassDescriptor cld) throws SQLException
	{
		Object[] pkValues = oid.getPrimaryKeyValues();
		FieldDescriptor[] pkFields = cld.getPkFields();
		int i = 0;
		try
		{
			for (; i < pkValues.length; i++)
			{
				this.m_platform.setObjectForStatement(stmt, i + 1, pkValues[i], pkFields[i].getJdbcType().getType());
			}
		}
		catch (SQLException e)
		{
			logger.error("bindDelete failed for: " + oid.toString() + ", while set value '" +
					pkValues[i] + "' for column " + pkFields[i].getColumnName());
			throw e;
		}
	}

	/**
	 * binds the objects primary key and locking values to the statement, BRJ
	 */
	public void bindDelete(PreparedStatement stmt, ClassDescriptor cld, Object obj) throws SQLException
	{
		if (cld.getDeleteProcedure() != null)
		{
			this.bindProcedure(stmt, cld, obj, cld.getDeleteProcedure());
		}
		else
		{
			int index = 1;
			ValueContainer[] values, currentLockingValues;

			currentLockingValues = cld.getCurrentLockingValues(obj);
			// parameters for WHERE-clause pk
			values = getKeyValues(this.m_broker, cld, obj);
			for (int i = 0; i < values.length; i++)
			{
				this.m_platform.setObjectForStatement(stmt, index, values[i].getValue(), values[i].getJdbcType().getType());
				index++;
			}

			// parameters for WHERE-clause locking
			values = currentLockingValues;
			for (int i = 0; i < values.length; i++)
			{
				this.m_platform.setObjectForStatement(stmt, index, values[i].getValue(), values[i].getJdbcType().getType());
				index++;
			}
		}
	}

	/**
	 * bind attribute and value
	 * @param stmt
	 * @param index
	 * @param attributeOrQuery
	 * @param value
	 * @param cld
	 * @return the incremented index
	 * @throws SQLException
	 */
	private int bindStatementValue(PreparedStatement stmt, int index, Object attributeOrQuery, Object value, ClassDescriptor cld)
	throws SQLException
	{
		FieldDescriptor fld = null;
		// if value is a subQuery bind it
		if (value instanceof Query)
		{
			Query subQuery = (Query) value;
			return bindStatement(stmt, subQuery, cld.getRepository().getDescriptorFor(subQuery.getSearchClass()), index);
		}

		// if attribute is a subQuery bind it
		if (attributeOrQuery instanceof Query)
		{
			Query subQuery = (Query) attributeOrQuery;
			bindStatement(stmt, subQuery, cld.getRepository().getDescriptorFor(subQuery.getSearchClass()), index);
		}
		else
		{
			fld = cld.getFieldDescriptorForPath((String) attributeOrQuery);
		}

		if (fld != null)
		{
			// BRJ: use field conversions and platform
			if (value != null)
			{
				// =======  DEBUT AJOUT POUR GeOxygene ====================
				// Gestion des géométrie
				if (fld.getFieldConversion() instanceof GeomGeOxygene2Dbms) {
					// ORACLE
					if (this.m_platform instanceof PlatformOracle9iImpl || this.m_platform instanceof PlatformOracleImpl) {
						try {
							Object sql = this.geomGeOxygene2OracleMethod.invoke(fld.getFieldConversion(), new Object[]{value, stmt.getConnection()});
							this.m_platform.setObjectForStatement(stmt, index, sql, Types.STRUCT);
						} catch (Exception e) {
							e.printStackTrace();
						}
					} // POSTGIS
					if (this.m_platform instanceof PlatformPostgreSQLImpl) {
						try {
							Object sql = this.geomGeOxygene2PostgisMethod.invoke(fld.getFieldConversion(), new Object[]{value});
							this.m_platform.setObjectForStatement(stmt, index, sql, Types.CHAR);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

					// Gestion des tableaux
				} else if (fld.getFieldConversion().getClass() == this.arrayGeOxygene2OracleClass) {
					try {
						Object sql = this.arrayGeOxygene2OracleMethod.invoke(fld.getFieldConversion(), new Object[]{value, stmt.getConnection()});
						this.m_platform.setObjectForStatement(stmt, index, sql, Types.ARRAY);
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else
					// =======  FIN AJOUT POUR GeOxygene ======================
					// S'applique aux types d'objets standards (ni geometrie, ni tableau)

					this.m_platform.setObjectForStatement(stmt, index, fld.getFieldConversion().javaToSql(value), fld.getJdbcType().getType());
			}
			else
			{
				// =======  DEBUT AJOUT POUR GeOxygene ====================
				// Gestion des géométries nulles sous Oracle (plante sinon)
				if (fld.getJdbcType().getType() == Types.STRUCT)
					try {
						stmt.setNull(index, fld.getJdbcType().getType(), "MDSYS.SDO_GEOMETRY"); //$NON-NLS-1$
					} catch (Exception e) {
						e.printStackTrace();
					}
					else
						// =======  FIN AJOUT POUR GeOxygene ====================

						this.m_platform.setNullForStatement(stmt, index, fld.getJdbcType().getType());
			}
		}
		else
		{
			if (value != null)
			{
				stmt.setObject(index, value);
			}
			else
			{
				stmt.setNull(index, Types.NULL);
			}
		}

		return index+1; // increment before return
	}

	/**
	 * bind SelectionCriteria
	 * @param stmt the PreparedStatement
	 * @param index the position of the parameter to bind
	 * @param crit the Criteria containing the parameter
	 * @param cld the ClassDescriptor
	 * @return next index for PreparedStatement
	 */
	private int bindStatement(PreparedStatement stmt, int index, SelectionCriteria crit, ClassDescriptor cld) throws SQLException
	{
		return bindStatementValue(stmt, index, crit.getAttribute(), crit.getValue(), cld);
	}

	/**
	 * bind NullCriteria
	 * @param stmt the PreparedStatement
	 * @param index the position of the parameter to bind
	 * @param crit the Criteria containing the parameter
	 * @return next index for PreparedStatement
	 * @throws SQLException 
	 */
	private int bindStatement(PreparedStatement stmt, int index, NullCriteria crit) throws SQLException
	{
		return index;
	}

	/**
	 * bind FieldCriteria
	 * @param stmt , the PreparedStatement
	 * @param index , the position of the parameter to bind
	 * @param crit , the Criteria containing the parameter
	 * @return next index for PreparedStatement
	 * @throws SQLException 
	 */
	private int bindStatement(PreparedStatement stmt, int index, FieldCriteria crit) throws SQLException
	{
		return index;
	}

	/**
	 * bind SqlCriteria
	 * @param stmt the PreparedStatement
	 * @param index the position of the parameter to bind
	 * @param crit the Criteria containing the parameter
	 * @return next index for PreparedStatement
	 * @throws SQLException 
	 */
	private int bindStatement(PreparedStatement stmt, int index, SqlCriteria crit) throws SQLException
	{
		return index;
	}

	/**
	 * bind BetweenCriteria
	 * @param stmt the PreparedStatement
	 * @param index the position of the parameter to bind
	 * @param crit the Criteria containing the parameter
	 * @param cld the ClassDescriptor
	 * @return next index for PreparedStatement
	 */
	private int bindStatement(PreparedStatement stmt, int index, BetweenCriteria crit, ClassDescriptor cld) throws SQLException
	{
		int index2 = bindStatementValue(stmt, index, crit.getAttribute(), crit.getValue(), cld);

		return bindStatementValue(stmt, index2, crit.getAttribute(), crit.getValue2(), cld);
	}

	/**
	 * bind InCriteria
	 * @param stmt the PreparedStatement
	 * @param index the position of the parameter to bind
	 * @param crit the Criteria containing the parameter
	 * @param cld the ClassDescriptor
	 * @return next index for PreparedStatement
	 */
	private int bindStatement(PreparedStatement stmt, int index, InCriteria crit, ClassDescriptor cld) throws SQLException
	{
		if (crit.getValue() instanceof Collection<?>)
		{
			Collection<?> values = (Collection<?>) crit.getValue();
			Iterator<?> iter = values.iterator();

			while (iter.hasNext())
			{
				index = bindStatementValue(stmt, index, crit.getAttribute(), iter.next(), cld);
			}
		}
		else
		{
			index = bindStatementValue(stmt, index, crit.getAttribute(), crit.getValue(), cld);
		}
		return index;
	}

	/**
	 * bind ExistsCriteria
	 * @param stmt the PreparedStatement
	 * @param index the position of the parameter to bind
	 * @param crit the Criteria containing the parameter
	 * @param cld the ClassDescriptor
	 * @return next index for PreparedStatement
	 */
	private int bindStatement(PreparedStatement stmt, int index, ExistsCriteria crit, ClassDescriptor cld) throws SQLException
	{
		Query subQuery = (Query) crit.getValue();

		// if query has criteria, bind them
		if (subQuery.getCriteria() != null && !subQuery.getCriteria().isEmpty())
		{
			return bindStatement(stmt, subQuery.getCriteria(), cld.getRepository().getDescriptorFor(subQuery.getSearchClass()), index);

			// otherwise, just ignore it
		}
		return index;
	}

	/**
	 * bind a Query based Select Statement
	 */
	public int bindStatement(PreparedStatement stmt, Query query, ClassDescriptor cld, int param) throws SQLException
	{
		int result;

		result = bindStatement(stmt, query.getCriteria(), cld, param);
		result = bindStatement(stmt, query.getHavingCriteria(), cld, result);

		return result;
	}

	/**
	 * bind a Query based Select Statement
	 */
	protected int bindStatement(PreparedStatement stmt, Criteria crit, ClassDescriptor cld, int param) throws SQLException
	{
		if (crit != null)
		{
			Enumeration<?> e = crit.getElements();

			while (e.hasMoreElements())
			{
				Object o = e.nextElement();
				if (o instanceof Criteria)
				{
					Criteria pc = (Criteria) o;
					param = bindStatement(stmt, pc, cld, param);
				}
				else
				{
					SelectionCriteria c = (SelectionCriteria) o;
					// BRJ : bind once for the criterion's main class
					param = bindSelectionCriteria(stmt, param, c, cld);

					// BRJ : and once for each extent
					for (int i = 0; i < c.getNumberOfExtentsToBind(); i++)
					{
						param = bindSelectionCriteria(stmt, param, c, cld);
					}
				}
			}
		}
		return param;
	}

	/**
	 * bind SelectionCriteria
	 * @param stmt the PreparedStatement
	 * @param index the position of the parameter to bind
	 * @param crit the Criteria containing the parameter
	 * @param cld the ClassDescriptor
	 * @return next index for PreparedStatement
	 */
	private int bindSelectionCriteria(PreparedStatement stmt, int index, SelectionCriteria crit, ClassDescriptor cld) throws SQLException
	{
		if (crit instanceof NullCriteria)
			index = bindStatement(stmt, index, (NullCriteria) crit);
		else if (crit instanceof BetweenCriteria)
			index = bindStatement(stmt, index, (BetweenCriteria) crit, cld);
		else if (crit instanceof InCriteria)
			index = bindStatement(stmt, index, (InCriteria) crit, cld);
		else if (crit instanceof SqlCriteria)
			index = bindStatement(stmt, index, (SqlCriteria) crit);
		else if (crit instanceof FieldCriteria)
			index = bindStatement(stmt, index, (FieldCriteria) crit);
		else if (crit instanceof ExistsCriteria)
			index = bindStatement(stmt, index, (ExistsCriteria) crit, cld);
		else
			index = bindStatement(stmt, index, crit, cld);

		return index;
	}

	/**
	 * binds the values of the object obj to the statements parameters
	 */
	public void bindInsert(PreparedStatement stmt, ClassDescriptor cld, Object obj) throws java.sql.SQLException
	{
		ValueContainer[] values;
		cld.updateLockingValues(obj); // BRJ : provide useful defaults for locking fields

		if (cld.getInsertProcedure() != null)
		{
			this.bindProcedure(stmt, cld, obj, cld.getInsertProcedure());
		}
		else
		{
			values = getAllValues(cld, obj);
			for (int i = 0; i < values.length; i++)
			{
				ValueContainer val = values[i];

				// DEBUT AJOUT POUR GEOXYGENE ----------------------------------------------
				// Pour PostGIS le type JDBC STRUC n'est pas reconnu
				// On caste en un autre type (OTHER)
				// qui a été ajouté dans le "JDBCTypesHelper" réécrit pour l'occasion
				// ( ce type n'existe pas dans OJB par défaut)
				if (val.getValue() instanceof PGgeometry)
					val.setJdbcType(JdbcTypesHelper.getJdbcTypeByName("other")); //$NON-NLS-1$
				// FIN AJOUT POUR GEOXYGENE ----------------------------------------------

				if (val.getValue() != null)
				{
					this.m_platform.setObjectForStatement(stmt, i + 1, val.getValue(), val.getJdbcType().getType());
				}
				else
				{
					// =======  DEBUT AJOUT POUR GeOxygene ====================
						// Gestion des géométries nulles sous Oracle (plante sinon)
					if (val.getJdbcType().getType() == Types.STRUCT)
						try {
							stmt.setNull(i + 1, val.getJdbcType().getType(), "MDSYS.SDO_GEOMETRY"); //$NON-NLS-1$
						} catch (Exception e) {
							e.printStackTrace();
						}
						else
							// =======  FIN AJOUT POUR GeOxygene ====================

							this.m_platform.setNullForStatement(stmt, i + 1, val.getJdbcType().getType());
				}
			}
		}
	}

	/**
	 * binds the Identities Primary key values to the statement
	 */
	public void bindSelect(PreparedStatement stmt, Identity oid, ClassDescriptor cld) throws SQLException
	{
		ValueContainer[] values = null;
		int i = 0;

		if (cld == null)
		{
			cld = this.m_broker.getClassDescriptor(oid.getObjectsRealClass());
		}
		try
		{
			values = getKeyValues(this.m_broker, cld, oid);
			for (i = 0; i < values.length; i++)
			{
				ValueContainer valContainer = values[i];
				if (valContainer.getValue() != null)
				{
					this.m_platform.setObjectForStatement(stmt, i + 1, valContainer.getValue(), valContainer.getJdbcType().getType());
				}
				else
				{
					this.m_platform.setNullForStatement(stmt, i + 1, valContainer.getJdbcType().getType());
				}
			}
		}
		catch (SQLException e)
		{
			logger.error("bindSelect failed for: " + oid.toString() + ", PK: " + i);
			throw e;
		}
	}

	/**
	 * binds the values of the object obj to the statements parameters
	 */
	public void bindUpdate(PreparedStatement stmt, ClassDescriptor cld, Object obj) throws java.sql.SQLException
	{
		if (cld.getUpdateProcedure() != null)
		{
			this.bindProcedure(stmt, cld, obj, cld.getUpdateProcedure());
		}
		else
		{
			int index = 1;
			ValueContainer[] values, valuesSnapshot;
			// first take a snapshot of current locking values
			valuesSnapshot = cld.getCurrentLockingValues(obj);
			cld.updateLockingValues(obj); // BRJ
			values = getNonKeyValues(this.m_broker, cld, obj);

			// parameters for SET-clause
			for (int i = 0; i < values.length; i++)
			{

				// DEBUT AJOUT POUR GEOXYGENE ----------------------------------------------
				// Pour PostGIS le type JDBC STRUC n'est pas reconnu
				// On caste en un autre type (OTHER)
				// qui a été ajouté dans le "JDBCTypesHelper" réécrit pour l'occasion
				// ( ce type n'existe pas dans OJB par défaut)
				if (values[i].getValue() instanceof PGgeometry)
					values[i].setJdbcType(JdbcTypesHelper.getJdbcTypeByName("other")); //$NON-NLS-1$
				// FIN AJOUT POUR GEOXYGENE ----------------------------------------------

				if (values[i].getValue() != null)
				{
					this.m_platform.setObjectForStatement(stmt, index, values[i].getValue(), values[i].getJdbcType().getType());
				}
				else
				{
					// =======  DEBUT AJOUT POUR GeOxygene ====================
						// Gestion des géométries nulles sous Oracle (plante sinon)
					if (values[i].getJdbcType().getType() == Types.STRUCT)
						try {
							stmt.setNull(index, values[i].getJdbcType().getType(), "MDSYS.SDO_GEOMETRY"); //$NON-NLS-1$
						} catch (Exception e) {
							e.printStackTrace();
						}
						else
							// =======  FIN AJOUT POUR GeOxygene ====================

							this.m_platform.setNullForStatement(stmt, index, values[i].getJdbcType().getType());
				}

				index++;
			}
			// parameters for WHERE-clause pk
			values = getKeyValues(this.m_broker, cld, obj);
			for (int i = 0; i < values.length; i++)
			{
				if (values[i].getValue() != null)
				{
					stmt.setObject(index, values[i].getValue(), values[i].getJdbcType().getType());
				}
				else
				{
					stmt.setNull(index, values[i].getJdbcType().getType());
				}
				index++;
			}
			// parameters for WHERE-clause locking
			// take old locking values
			values = valuesSnapshot;
			for (int i = 0; i < values.length; i++)
			{
				if (values[i].getValue() != null)
				{
					stmt.setObject(index, values[i].getValue(), values[i].getJdbcType().getType());
				}
				else
				{
					// =======  DEBUT AJOUT POUR GeOxygene ====================
						// Gestion des géométries nulles sous Oracle (plante sinon)
					if (values[i].getJdbcType().getType() == Types.STRUCT)
						try {
							stmt.setNull(index, values[i].getJdbcType().getType(), "MDSYS.SDO_GEOMETRY"); //$NON-NLS-1$
						} catch (Exception e) {
							e.printStackTrace();
						}
						else
							// =======  FIN AJOUT POUR GeOxygene ====================

							stmt.setNull(index, values[i].getJdbcType().getType());
				}
				index++;
			}
		}
	}

	/**
	 * binds the given array of values (if not null) starting from the given
	 * parameter index
	 * @return the next parameter index
	 */
	public int bindValues(PreparedStatement stmt, ValueContainer[] values, int index) throws SQLException
	{
		if (values != null)
		{
			for (int i = 0; i < values.length; i++)
			{
				this.m_platform.setObjectForStatement(stmt, index, values[i].getValue(), values[i].getJdbcType().getType());
				index++;
			}
		}
		return index;
	}

	/**
	 * return a prepared DELETE Statement fitting for the given ClassDescriptor
	 */
	public PreparedStatement getDeleteStatement(ClassDescriptor cld) throws PersistenceBrokerSQLException, PersistenceBrokerException
	{
		try
		{
			return getStatementsForClass(cld).getDeleteStmt(this.m_conMan.getConnection());
		}
		catch (SQLException e)
		{
			throw new PersistenceBrokerSQLException("Could not build statement ask for", e);
		}
		catch (LookupException e)
		{
			throw new PersistenceBrokerException("Used ConnectionManager instance could not obtain a connection", e);
		}
	}

	/**
	 * return a generic Statement for the given ClassDescriptor.
	 * Never use this method for UPDATE/INSERT/DELETE if you want to use the batch mode.
	 */
	public Statement getGenericStatement(ClassDescriptor cds, boolean scrollable) throws PersistenceBrokerException
	{
		try
		{
			return getStatementsForClass(cds).getGenericStmt(this.m_conMan.getConnection(), scrollable);
		}
		catch (LookupException e)
		{
			throw new PersistenceBrokerException("Used ConnectionManager instance could not obtain a connection", e);
		}
	}

	/**
	 * return a prepared Insert Statement fitting for the given ClassDescriptor
	 */
	public PreparedStatement getInsertStatement(ClassDescriptor cds) throws PersistenceBrokerSQLException, PersistenceBrokerException
	{
		try
		{
			return getStatementsForClass(cds).getInsertStmt(this.m_conMan.getConnection());
		}
		catch (SQLException e)
		{
			throw new PersistenceBrokerSQLException("Could not build statement ask for", e);
		}
		catch (LookupException e)
		{
			throw new PersistenceBrokerException("Used ConnectionManager instance could not obtain a connection", e);
		}
	}

	/**
	 * return a generic Statement for the given ClassDescriptor
	 */
	public PreparedStatement getPreparedStatement(ClassDescriptor cds, String sql, boolean scrollable) throws PersistenceBrokerException
	{
		try
		{
			return getStatementsForClass(cds).getPreparedStmt(this.m_conMan.getConnection(), sql, scrollable);
		}
		catch (LookupException e)
		{
			throw new PersistenceBrokerException("Used ConnectionManager instance could not obtain a connection", e);
		}
	}

	/**
	 * return a prepared Select Statement for the given ClassDescriptor
	 */
	public PreparedStatement getSelectByPKStatement(ClassDescriptor cds) throws PersistenceBrokerSQLException, PersistenceBrokerException
	{
		try
		{
			return getStatementsForClass(cds).getSelectByPKStmt(this.m_conMan.getConnection());
		}
		catch (SQLException e)
		{
			throw new PersistenceBrokerSQLException("Could not build statement ask for", e);
		}
		catch (LookupException e)
		{
			throw new PersistenceBrokerException("Used ConnectionManager instance could not obtain a connection", e);
		}
	}

	/**
	 * return a prepared Update Statement fitting to the given ClassDescriptor
	 */
	public PreparedStatement getUpdateStatement(ClassDescriptor cds) throws PersistenceBrokerSQLException, PersistenceBrokerException
	{
		try
		{
			return getStatementsForClass(cds).getUpdateStmt(this.m_conMan.getConnection());
		}
		catch (SQLException e)
		{
			throw new PersistenceBrokerSQLException("Could not build statement ask for", e);
		}
		catch (LookupException e)
		{
			throw new PersistenceBrokerException("Used ConnectionManager instance could not obtain a connection", e);
		}
	}

	/**
	 * returns an array containing values for all the Objects attribute
	 * @throws PersistenceBrokerException if there is an erros accessing obj field values
	 */
	protected ValueContainer[] getAllValues(ClassDescriptor cld, Object obj) throws PersistenceBrokerException
	{
		Connection conn = null;
		try {
			conn = this.m_broker.serviceConnectionManager().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return this.m_broker.serviceOxygeneBrokerHelper().getAllRwValues(cld, obj, conn);
	}

	/**
	 * returns an Array with an Objects PK VALUES
	 * @throws PersistenceBrokerException if there is an erros accessing o field values
	 */
	protected ValueContainer[] getKeyValues(PersistenceBroker broker, ClassDescriptor cld, Object obj) throws PersistenceBrokerException
	{
		GeOxygenePersistenceBrokerImpl geOxyBroker = (GeOxygenePersistenceBrokerImpl)broker;
		Connection conn = null;
		try {
			conn = geOxyBroker.serviceConnectionManager().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (geOxyBroker.serviceOxygeneBrokerHelper().getKeyValues(cld, obj, conn));
	}

	/**
	 * returns an Array with an Identities PK VALUES
	 * @throws PersistenceBrokerException if there is an erros accessing o field values
	 */
	protected ValueContainer[] getKeyValues(PersistenceBroker broker, ClassDescriptor cld, Identity oid) throws PersistenceBrokerException
	{
		GeOxygenePersistenceBrokerImpl geOxyBroker = (GeOxygenePersistenceBrokerImpl)broker;
		//Connection conn = null;
		try {
			/*conn = */geOxyBroker.serviceConnectionManager().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (geOxyBroker.serviceOxygeneBrokerHelper().getKeyValues(cld, oid));
	}

	/**
	 * returns an Array with an Objects NON-PK VALUES
	 * @throws PersistenceBrokerException if there is an erros accessing o field values
	 */
	protected ValueContainer[] getNonKeyValues(PersistenceBroker broker, ClassDescriptor cld, Object obj ) throws PersistenceBrokerException
	{
		GeOxygenePersistenceBrokerImpl geOxyBroker = (GeOxygenePersistenceBrokerImpl)broker;
		Connection conn = null;
		try {
			conn = geOxyBroker.serviceConnectionManager().getConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return (geOxyBroker.serviceOxygeneBrokerHelper().getNonKeyRwValues(cld, obj, conn));
	}

	/**
	 * Bind a prepared statment that represents a call to a procedure or
	 * user-defined function.
	 *
	 * @param stmt the statement to bind.
	 * @param cld the class descriptor of the object that triggered the
	 *        invocation of the procedure or user-defined function.
	 * @param obj the object that triggered the invocation of the procedure
	 *        or user-defined function.
	 * @param proc the procedure descriptor that provides information about
	 *        the arguments that shoudl be passed to the procedure or
	 *        user-defined function
	 */
	private void bindProcedure(PreparedStatement stmt, ClassDescriptor cld, Object obj, ProcedureDescriptor proc)
	throws SQLException
	{
		int valueSub = 0;

		// Figure out if we are using a callable statement.  If we are, then we
		// will need to register one or more output parameters.
		CallableStatement callable = null;
		if (stmt instanceof CallableStatement)
		{
			callable = (CallableStatement) stmt;
		}

		// If we have a return value, then register it.
		if ((proc.hasReturnValue()) && (callable != null))
		{
			int jdbcType = proc.getReturnValueFieldRef().getJdbcType().getType();
			this.m_platform.setNullForStatement(stmt, valueSub + 1, jdbcType);
			callable.registerOutParameter(valueSub + 1, jdbcType);
			valueSub++;
		}

		// Process all of the arguments.
		Iterator<?> iterator = proc.getArguments().iterator();
		while (iterator.hasNext())
		{
			ArgumentDescriptor arg = (ArgumentDescriptor) iterator.next();
			Object val = arg.getValue(obj);
			int jdbcType = arg.getJdbcType();
			if (val != null)
			{
				this.m_platform.setObjectForStatement(stmt, valueSub + 1, val, jdbcType);
			}
			else
			{
				this.m_platform.setNullForStatement(stmt, valueSub + 1, jdbcType);
			}
			if ((arg.getIsReturnedByProcedure()) && (callable != null))
			{
				callable.registerOutParameter(valueSub + 1, jdbcType);
			}
			valueSub++;
		}
	}
}

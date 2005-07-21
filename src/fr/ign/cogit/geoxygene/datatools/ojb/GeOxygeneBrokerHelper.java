/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.StringTokenizer;

import org.apache.commons.lang.SystemUtils;
import org.apache.ojb.broker.Identity;
import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.PersistenceBrokerException;
import org.apache.ojb.broker.VirtualProxy;
import org.apache.ojb.broker.accesslayer.IndirectionHandler;
import org.apache.ojb.broker.core.ValueContainer;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.metadata.MetadataException;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.apache.ojb.broker.metadata.fieldaccess.PersistentField;
import org.apache.ojb.broker.platforms.Platform;
import org.apache.ojb.broker.platforms.PlatformFactory;
import org.apache.ojb.broker.platforms.PlatformOracle9iImpl;
import org.apache.ojb.broker.platforms.PlatformOracleImpl;
import org.apache.ojb.broker.platforms.PlatformPostgreSQLImpl;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.MtoNQuery;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryBySQL;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByMtoNCriteria;
import org.apache.ojb.broker.util.sequence.SequenceManagerException;

//import fr.ign.cogit.geoxygene.datatools.oracle.ArrayGeOxygene2Oracle;
//import fr.ign.cogit.geoxygene.datatools.oracle.GeomGeOxygene2Oracle;
//#else
/*
import com.develop.java.lang.reflect.Proxy;
*/
//#endif

/**
 * Redefinition de la classe org.apache.ojb.util.BrokerHelper d'OJB, 
 * permettant d'appeler une methode "javaToSql(Object, Connection)
 * pour ecrire les structures dans Oracle.
 * Par rapport a la version originale de BrokerHelper :
 *  les imports ont ete reorganises,
 *  le constructeur renomme,
 *  un parametre connection ajoute dans la signature de getValuesForObject (ligne 352),
 *  un ajout dans la methode getValuesForObject (ligne 375),
 *  un parametre connection ajoute dans getAllRwValues,
 *  un parametre connection ajoute dans getNonKeyRwValues,
 *  un parametre connection ajoute dans getKeyValues,
 *  un parametre connection ajoute dans getKeyValues.
 * Les 4 dernieres modifs se font suite a des erreurs de compile, 
 * suite au premier ajout dans getValuesForObject.
 * 
 * AB 11 juillet 2005 : 
 * <br> Utilisation des noms de classes et de la réflection pour permettre la compilation sépérée pour Oracle.
 * <br> Patch pour permettre l'utilisation de la meme classe de "FieldConversion" pour Oracle et Postgis. 
 *  
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 * 
 */
public class GeOxygeneBrokerHelper
{
	
	// AJOUT pour GeOxygene ---------------------------------------------------
	// Nom des classes relatives à Oracle, 
	//en String pour permettre la compilation séparée
	private final String GeomGeOxygene2Oracle_CLASS_NAME = 
		"fr.ign.cogit.geoxygene.datatools.oracle.GeomGeOxygene2Oracle";
	private final String GeomGeOxygene2Postgis_CLASS_NAME = 
		"fr.ign.cogit.geoxygene.datatools.postgis.GeomGeOxygene2Postgis";	
	private Method geomGeOxygene2OracleMethod; 
	private Method geomGeOxygene2PostgisMethod;
	// SGBD
	private Platform m_platform;
	// FIN AJOUT pour GeOxygene ---------------------------------------------------	
	
	
	public static final String REPOSITORY_NAME_SEPARATOR = "#";
	private PersistenceBroker m_broker;

	public GeOxygeneBrokerHelper(PersistenceBroker broker)
	{
		this.m_broker = broker;
		
		// AJOUT pour GeOxygene -----------------------------------------------------------
		// Definition du SGBD
		m_platform = PlatformFactory.getPlatformFor(m_broker.serviceConnectionManager().getConnectionDescriptor());
		
		// ORACLE
		if (m_platform instanceof PlatformOracle9iImpl || m_platform instanceof PlatformOracleImpl)
			try {
				Class geomGeOxygene2OracleClass = Class.forName(GeomGeOxygene2Oracle_CLASS_NAME);
				geomGeOxygene2OracleMethod = geomGeOxygene2OracleClass.getMethod("javaToSql",
																new Class[] {Object.class, Connection.class});	
			} catch (Exception e) {
				e.printStackTrace();	
			}
			
		// POSTGIS
		else if (m_platform instanceof PlatformPostgreSQLImpl)
			try {
				Class geomGeOxygene2PostgisClass = Class.forName(GeomGeOxygene2Postgis_CLASS_NAME);
				geomGeOxygene2PostgisMethod = geomGeOxygene2PostgisClass.getMethod("javaToSql",
																new Class[] {Object.class});
			} catch (Exception e) {
				e.printStackTrace();	
			}	
			
		// AUTRE DBMS	
		else {	
			System.out.println("## Le SGBD n'est ni Oracle, ni PostgreSQL ##");
			System.out.println("## Le programme s'arrête ##");
			System.exit(0);
		}			
		// FIN AJOUT pour GeOxygene ---------------------------------------------------			
	
	}

	/**
	 * splits up the name string and extract db url,
	 * user name and password and build a new PBKey
	 * instance - the token '#' is used to separate
	 * the substrings.
	 * @throws PersistenceBrokerException if given name was <code>null</code>
	 */
	public static PBKey extractAllTokens(String name)
	{
		if (name == null)
		{
			throw new PersistenceBrokerException("Could not extract PBKey, given argument is 'null'");
		}
		String user = null;
		String passwd = null;
		StringTokenizer tok = new StringTokenizer(name, REPOSITORY_NAME_SEPARATOR);
		String dbName = tok.nextToken();
		if (tok.hasMoreTokens())
		{
			user = tok.nextToken();
			if (user != null && user.trim().equals(""))
			{
				user = null;
			}
		}
		if (tok.hasMoreTokens())
		{
			if (user != null)
				passwd = tok.nextToken();
		}
		if (user != null && passwd == null)
		{
			passwd = "";
		}
		PBKey key = new PBKey(dbName, user, passwd);
		return key;
	}

	/**
	 * Check if the user of the given PBKey was <code>null</code>, if so we try to
	 * get user/password from the jdbc-connection-descriptor matching the given
	 * PBKey.getAlias().
	 */
	public static PBKey crossCheckPBKey(PBKey key)
	{
		if (key.getUser() == null)
		{
			PBKey defKey = MetadataManager.getInstance().connectionRepository().getStandardPBKeyForJcdAlias(key.getAlias());
			if (defKey != null)
			{
				return defKey;
			}
		}
		return key;
	}

	/**
	 * Answer the real ClassDescriptor for anObj
	 * ie. aCld may be an Interface of anObj, so the cld for anObj is returned
	 */
	protected ClassDescriptor getRealClassDescriptor(ClassDescriptor aCld, Object anObj)
	{
		ClassDescriptor result;

		if (aCld.getClassOfObject() == anObj.getClass())
		{
			result = aCld;
		}
		else
		{         
			result = aCld.getRepository().getDescriptorFor(anObj.getClass());
		}

		return result;
	}

	/**
	 * returns an Array with an Objects PK VALUES if convertToSql is true, any
	 * associated java-to-sql conversions are applied. If the Object is a Proxy
	 * or a VirtualProxy NO conversion is necessary.
	 *
	 * @param objectOrProxy
	 * @param convertToSql
	 * @return Object[]
	 * @throws PersistenceBrokerException
	 */
	public ValueContainer[] getKeyValues(ClassDescriptor cld, Object objectOrProxy, boolean convertToSql, Connection conn) throws PersistenceBrokerException
	{
		/*
		arminw
		Check it out. Because the isProxyClass method is costly and most objects
		aren't proxies, I add a instanceof check before. Is every Proxy a instance of Proxy?
		*/
		if ((objectOrProxy instanceof Proxy) && Proxy.isProxyClass(objectOrProxy.getClass()))
		{
			IndirectionHandler handler;
			handler = (IndirectionHandler) Proxy.getInvocationHandler(objectOrProxy);
			return getKeyValues(cld, handler.getIdentity(), convertToSql);  //BRJ: convert Identity
		}
		else if (objectOrProxy instanceof VirtualProxy)
		{
			IndirectionHandler handler;
			handler = VirtualProxy.getIndirectionHandler((VirtualProxy) objectOrProxy);
            return getKeyValues(cld, handler.getIdentity(), convertToSql);  //BRJ: convert Identity
		}
		else
		{
			ClassDescriptor realCld = getRealClassDescriptor(cld, objectOrProxy);
			return getValuesForObject(realCld.getPkFields(), objectOrProxy, convertToSql, conn);
		}
	}

	/**
	 * Return key Values of an Identity
	 * @param cld
	 * @param oid
	 * @return Object[]
	 * @throws PersistenceBrokerException
	 */
	public ValueContainer[] getKeyValues(ClassDescriptor cld, Identity oid) throws PersistenceBrokerException
	{
		return getKeyValues(cld, oid, true);
	}

	/**
	 * Return key Values of an Identity
	 * @param cld
	 * @param oid
	 * @param convertToSql
	 * @return Object[]
	 * @throws PersistenceBrokerException
	 */
	public ValueContainer[] getKeyValues(ClassDescriptor cld, Identity oid, boolean convertToSql) throws PersistenceBrokerException
	{
		FieldDescriptor[] pkFields = cld.getPkFields();
		ValueContainer[] result = new ValueContainer[pkFields.length];
		Object[] pkValues = oid.getPrimaryKeyValues();

		try
		{
			for (int i = 0; i < result.length; i++)
			{
				FieldDescriptor fd = pkFields[i];
				Object cv = pkValues[i];
				if (convertToSql)
				{
					// BRJ : apply type and value mapping
					cv = fd.getFieldConversion().javaToSql(cv);
				}
                result[i] = new ValueContainer(cv, fd.getJdbcType());
			}
		}
		catch (Exception e)
		{
			throw new PersistenceBrokerException("Can't generate primary key values for given Identity " + oid,e);
		}
		return result;
	}

	/**
	 * returns an Array with an Objects PK VALUES, with any java-to-sql
	 * FieldConversion applied. If the Object is a Proxy or a VirtualProxy NO
	 * conversion is necessary.
	 *
	 * @param objectOrProxy
	 * @return Object[]
	 * @throws PersistenceBrokerException
	 */
	public ValueContainer[] getKeyValues(ClassDescriptor cld, Object objectOrProxy, Connection conn) throws PersistenceBrokerException
	{
		return getKeyValues(cld, objectOrProxy, true, conn);
	}

    /**
     * Return true if aValue is regarded as null<br>
     * null, Number(0) or empty String
     * @param aValue
     * @return
     */
    private boolean isNull(Object aValue)
    {
        return (
            (aValue == null)
                || ((aValue instanceof Number) && (((Number) aValue).longValue() == 0))
                || ((aValue instanceof String) && (((String) aValue).length() == 0)));
    }


	/**
	 * Get an autoincremented value that has already
	 * had a field conversion run on it.
	 * <p>
	 * The data type of the value that is returned by this method is
	 * compatible with the java-world.  The return value has <b>NOT</b>
	 * been run through a field conversion and converted to a corresponding
	 * sql-type.
	 *
	 * @throws MetadataException if there is an erros accessing obj field values
	 */
	protected Object getAutoIncrementValue(FieldDescriptor fd, Object obj, Object cv)
    {
        if (isNull(cv))
        {
            PersistentField f = fd.getPersistentField();
            try
            {
                // lookup SeqMan for a value matching db column an
				// fieldconversion
                Object result = m_broker.serviceSequenceManager().getUniqueValue(fd);
                // reflect autoincrement value back into object
                f.set(obj, result);
                return result;
            }
            catch (MetadataException e)
            {
                throw new PersistenceBrokerException(
                    "Error while trying to autoincrement field " + f.getDeclaringClass() + "#" + f.getName(),
                    e);
            }
            catch (SequenceManagerException e)
            {
                throw new PersistenceBrokerException("Could not get key value", e);
            }
        }
        else
        {
            return cv;
        }
    }

	/**
	 * Get the values of the fields for an obj
	 * @param fields
	 * @param obj
	 * @throws PersistenceBrokerException
	 */
	public ValueContainer[] getValuesForObject(FieldDescriptor[] fields, Object obj, boolean convertToSql, Connection conn) throws PersistenceBrokerException
	{
		ValueContainer[] result = new ValueContainer[fields.length];

		for (int i = 0; i < fields.length; i++)
		{
			FieldDescriptor fd = fields[i];
			Object cv = fd.getPersistentField().get(obj);

			// handle autoincrement attributes if not filled
			if (fd.isAutoIncrement())
			{
				// getAutoIncrementValue returns a value that is
				// properly typed for the java-world.  This value
				// needs to be converted to it's corresponding
				// sql type so that the entire result array contains
				// objects that are properly typed for sql.
				cv = getAutoIncrementValue(fd, obj, cv);
			}
			if (convertToSql)
			{
				// apply type and value conversion
                
                // DEBUT AJOUT  POUR GeOxygene -------------------------------------------------------------
				// Gestion des géométrie
			   if (fd.getFieldConversion() instanceof GeomGeOxygene2Dbms) {   
				   // ORACLE
				   if (m_platform instanceof PlatformOracle9iImpl || m_platform instanceof PlatformOracleImpl) {
					   try {
						   cv = geomGeOxygene2OracleMethod.invoke(fd.getFieldConversion(), new Object[]{cv, conn});	
					   } catch (Exception e) {
						   e.printStackTrace();					
					   }  
				   } // POSTGIS
				   if (m_platform instanceof PlatformPostgreSQLImpl) {
					   try {
						   cv = geomGeOxygene2PostgisMethod.invoke(fd.getFieldConversion(), new Object[]{cv});	
					   } catch (Exception e) {
						   e.printStackTrace();					
					   }                 	
				   }
				   			
				} else                
                // FIN AJOUT  POUR GeOxygene----------------------------------------------------------------
                // Types non géométriques
                                    
				cv = fd.getFieldConversion().javaToSql(cv);
			}
            // create ValueContainer
            result[i] = new ValueContainer(cv, fd.getJdbcType());
		}
		return result;
	}

	/**
	 * returns an Array with an Objects NON-PK VALUES (READ/WRITE only)
	 * @throws MetadataException if there is an erros accessing o field values
	 */
	public ValueContainer[] getNonKeyRwValues(ClassDescriptor cld, Object obj, Connection conn) throws PersistenceBrokerException
	{
		ClassDescriptor realCld = getRealClassDescriptor(cld, obj);
		return getValuesForObject(realCld.getNonPkRwFields(), obj, true, conn);
	}

	/**
	 * returns an array containing values for all the Objects attribute (READ/WRITE only)
	 * @throws MetadataException if there is an erros accessing obj field values
	 */
	public ValueContainer[] getAllRwValues(ClassDescriptor cld, Object obj, Connection conn) throws PersistenceBrokerException
	{
		ClassDescriptor realCld = getRealClassDescriptor(cld, obj);
		return getValuesForObject(realCld.getAllRwFields(), obj, true, conn);
	}

    /**
     * Extract a value array of the given {@link ValueContainer} array.
     * @param containers
     * @return a value array
     */
    public Object[] extractValueArray(ValueContainer[] containers)
    {
        Object[] result = new Object[containers.length];
        for (int i = 0; i < containers.length; i++)
        {
            result[i] = containers[i].getValue();
        }
        return result;
    }

    /**
     * returns true if the primary key fields are valid, else false.
     * PK fields are valid if each of them is either an OJB managed
     * attribute (autoincrement or locking) or if it contains
     * a valid non-null value
     * @param fieldDescriptors the array of PK fielddescriptors
     * @param pkValues the array of PK values
     * @return boolean
     */
    public boolean assertValidPkFields(FieldDescriptor[] fieldDescriptors, Object[] pkValues)
    {
        int fieldDescriptorSize = fieldDescriptors.length;
        for (int i = 0; i < fieldDescriptorSize; i++)
        {
            /**
             * a pk field is valid if it is either managed by OJB
             * (autoincrement or locking) or if it does contain a
             * valid non-null value.
             */
            if (! (fieldDescriptors[i].isAutoIncrement()
                    || fieldDescriptors[i].isLocking()
                    || assertValidPkValue(pkValues[i]) ))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * returns true if a value is non-null, STring instances are also checked,
     * if they are non-empty.
     * @param pkValue the value to check
     * @return boolean
     */
    private boolean assertValidPkValue(Object pkValue)
    {
        // null as value of a primary key is not acceptable
        if (pkValue == null)
        {
            return false;
        }
        if (pkValue instanceof String)
        {
            // the toString() method on a String-object is maybe faster
            // than the downcast to String. Also use length() to test
            // if a String empty or not, this is faster than the comparing
            // a String-object with an empty string using the equals()-method.
            if (pkValue.toString().trim().length() == 0)
            {
                return false;
            }
        }
        return true;
    }

    /**
         * Build a Count-Query based on aQuery
         * @param aQuery
         * @return a Query
         */
        public Query getCountQuery(Query aQuery)
        {
            if (aQuery instanceof QueryBySQL)
            {
                return getCountQuery((QueryBySQL)aQuery);
            }
            else
            {
                return getCountQuery((QueryByCriteria)aQuery);
            }
        }

        /**
         * Create a Count-Query for QueryBySQL
         * @param aQuery
         * @return
         */
        private Query getCountQuery(QueryBySQL aQuery)
        {
            String countSql = aQuery.getSql();

            int fromPos = countSql.toUpperCase().indexOf(" FROM ");
            if (fromPos >= 0)
            {
                countSql = "select count(*)" + countSql.substring(fromPos);
            }

            int orderPos = countSql.toUpperCase().indexOf(" ORDER BY ");
            if (orderPos >= 0)
            {
                countSql = countSql.substring(0, orderPos);
            }

            return new QueryBySQL(aQuery.getSearchClass(), countSql);
        }


        /**
         * Create a Count-Query for QueryByCriteria
         * @param aQuery
         * @return
         */
        private Query getCountQuery(QueryByCriteria aQuery)
        {
            Class searchClass = aQuery.getSearchClass();
            ReportQueryByCriteria countQuery;
            Criteria countCrit = null;
            FieldDescriptor[] pkFields = m_broker.getClassDescriptor(searchClass).getPkFields();
            String[] columns = new String[pkFields.length];

            // build a ReportQuery based on query orderby needs to be cleared
            if (aQuery.getCriteria() != null)
            {
                countCrit = aQuery.getCriteria().copy(false, false, false);
            }

            // BRJ: add a column for each pkField, make it distinct if query is distinct
            // TBD check if it really works for multiple keys ?
            for (int i = 0; i < pkFields.length; i++)
            {
                if (aQuery.isDistinct())
                {
                    columns[i] = "count(distinct " + pkFields[i].getAttributeName() + ")";
                }
                else
                {
                    columns[i] = "count(" + pkFields[i].getAttributeName() + ")";
                }
            }

            // BRJ: we have to preserve indirection table !
            if (aQuery instanceof MtoNQuery)
            {
                MtoNQuery mnQuery = (MtoNQuery)aQuery;
                ReportQueryByMtoNCriteria mnReportQuery = new ReportQueryByMtoNCriteria(searchClass, columns, countCrit);
                mnReportQuery.setIndirectionTable(mnQuery.getIndirectionTable());

                countQuery = mnReportQuery;
            }
            else
            {
                countQuery = new ReportQueryByCriteria(searchClass, columns, countCrit);
            }

            return countQuery;
        }


	public static String buildMessageString(Object obj, Object value, Field field)
	{
		String eol = SystemUtils.LINE_SEPARATOR;
		StringBuffer buf = new StringBuffer();
		buf
			.append(eol + "object class[ " + (obj != null ? obj.getClass().getName() : null))
			.append(eol + "target field: " + field.getName())
			.append(eol + "target field type: " + (field != null ? field.getType() : null))
			.append(eol + "object value class: " + (value != null ? value.getClass().getName() : null))
			.append(eol + "object value: " + (value != null ? value : null))
			.append("]");
		return buf.toString();
	}
}

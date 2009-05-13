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

package fr.ign.cogit.geoxygene.datatools.castor.conf;

/*
 * This class was automatically generated with
 * <a href="http://castor.exolab.org">Castor 0.9.2</a>, using an
 * XML Schema.
 * Creee a partir du schema jdo-conf.xsd (modifie en jdo-conf.xsd.2001).
 * Cette classe a ete creee pour recuperer les donnees du fichier "database.xml" (driver, user, etc...)
 * utiles pour etablir une connection JDBC. Elle est utilisee par la classe GeodatabaseCastorOracle.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 */

import java.util.ArrayList;

/** Usage interne. */

public class Driver {

	protected String _url;
	protected String _className;
	protected ArrayList<Param> _param = new ArrayList<Param>();

	public Driver() {super();}

	public String getClassName() {return this._className;}

	public String getUrl() {return this._url;}

	public void setClassName(String className) {this._className = className;}

	public void setUrl(String url) {this._url = url;}

	public Param createParam() {return new Param();}

	public ArrayList<?> getParam() {return _param;}

	public void addParam (Param param) {_param.add(param);}

	public String getUserName() throws Exception {
		for (int i=0; i<_param.size(); i++) {
			Param p = _param.get(i);
			if (p.getName().compareToIgnoreCase("user") == 0)
				return  p.getValue();
		}
		throw new Exception("Utilisateur non défini dans le fichier database.xml");
	}

	public String getPassword() throws Exception {
		for (int i=0; i<_param.size(); i++) {
			Param p = _param.get(i);
			if (p.getName().compareToIgnoreCase("password") == 0)
				return  p.getValue();
		}
		throw new Exception("Password non défini dans le fichier database.xml");
	}

}

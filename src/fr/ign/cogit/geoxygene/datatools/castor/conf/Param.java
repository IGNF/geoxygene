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

/** Usage interne. */

public class Param  {

	protected String _name;
	protected String _value;

	public Param() { super();}

	public String getName() {return this._name;}

	public String getValue() {return this._value;}

	public void setName(String name) {this._name = name;}

	public void setValue(String value) {this._value = value;}

}

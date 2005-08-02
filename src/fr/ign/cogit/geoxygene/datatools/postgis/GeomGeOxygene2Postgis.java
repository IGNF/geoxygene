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

package fr.ign.cogit.geoxygene.datatools.postgis;

import java.sql.SQLException;

import org.postgis.PGgeometry;

import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.util.conversion.ParseException;
import fr.ign.cogit.geoxygene.util.conversion.WktGeOxygene;

/**
 * Conversion des geometries PostGIS dans le format GeOxygene, et reciproquement.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.1
 *
 */

public class GeomGeOxygene2Postgis  {
	
	
	public static Object sqlToJava (Object geom) {
		PGgeometry pgGeom = (PGgeometry)geom;
		
		try {
			GM_Object geOxyGeom = WktGeOxygene.makeGeOxygene(pgGeom.toString());
			
			if (geOxyGeom instanceof GM_MultiPoint) {
				GM_MultiPoint aggr = (GM_MultiPoint)geOxyGeom;
				if (aggr.size() == 1) 
					return aggr.get(0);
			}
			
			if (geOxyGeom instanceof GM_MultiCurve) {
				GM_MultiCurve aggr = (GM_MultiCurve)geOxyGeom;
				if (aggr.size() == 1) 
					return aggr.get(0);
			}
						
			if (geOxyGeom instanceof GM_MultiSurface) {
				GM_MultiSurface aggr = (GM_MultiSurface)geOxyGeom;
				if (aggr.size() == 1) 
					return aggr.get(0);
			}
						
			return geOxyGeom;
			
		} catch (ParseException e) {
			System.out.println("## WARNING ## Postgis to GeOxygene returns NULL ");
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static Object javaToSql (Object geom) {	
		try {
			if (geom == null) return null;
			PGgeometry pgGeom = new PGgeometry(((GM_Object)geom).toString());
			return pgGeom;					
		} catch (SQLException e) {
			System.out.println("## WARNING ## GeOxygene to Postgis returns NULL ");
			e.printStackTrace();
			return null;
		}	
	}

}

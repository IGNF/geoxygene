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

package fr.ign.cogit.geoxygene.util.conversion;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

public class IsEmptyUtil
{
 
	public static boolean isEmpty(GM_Object geom)
	{
		if (geom==null) return true;
		if (geom instanceof GM_Point)        return isEmpty((GM_Point)geom);
		if (geom instanceof GM_Polygon)      return isEmpty((GM_Polygon)geom);
		if (geom instanceof GM_LineString)   return isEmpty((GM_LineString)geom);
		if (geom instanceof GM_Aggregate)    return isEmpty((GM_Aggregate)geom);
		return false;
	}
	
	public static boolean isEmpty(GM_Point point)
	{
		DirectPosition position=point.getPosition();
		double x=position.getX();
		double y=position.getY();
		double z=position.getZ();
		return (x==Double.NaN || y==Double.NaN || z==Double.NaN);
	}
	
	public static boolean isEmpty(GM_Polygon poly)
	{
		return poly.coord().size()==0;
	}
	
	public static boolean isEmpty(GM_LineString lineString)
	{
		return lineString.sizeControlPoint()==0;
	}
	
	static boolean isEmpty(GM_Aggregate aggr)
	{
		if (aggr.size()==0)	
			return true;
		else {
			aggr.initIterator();
			while (aggr.hasNext()) {
				GM_Object geom=aggr.next();
				if (!isEmpty(geom)) return false;
			}
			return true;
		}
	}
}

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

package fr.ign.cogit.geoxygene.util.conversion;

import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/** 
  * Conversions entre les GM_Object GeOxygene et les Geometry JTS.
  *
  * @author Thierry Badard, Arnaud Braun & Christophe Pele 
  * @version 1.0
  * 
  */

public class JtsGeOxygene
{
	/*------------------------------------------------------------*/
	/*-- Fields --------------------------------------------------*/    
	/*------------------------------------------------------------*/
	
//	private static int jtsSRID=0;
	private static PrecisionModel jtsPrecision=new PrecisionModel();
//	private static GeometryFactory jtsGeomFactory=new GeometryFactory(JtsGeOxygene.jtsPrecision,JtsGeOxygene.jtsSRID);
//	private static WKTReader jtsWktReader=new WKTReader(JtsGeOxygene.jtsGeomFactory);
//	private static WKTWriter jtsWktWriter=new WKTWriter();

	/*------------------------------------------------------------*/
	/*-- Conversion beetween JTS and GeOxygene objects -----------*/    
	/*------------------------------------------------------------*/
	
	public static Geometry makeJtsGeom(GM_Object geOxyGeom)
	throws Exception
	{
		GeometryFactory jtsGeomFactory=new GeometryFactory(JtsGeOxygene.jtsPrecision,geOxyGeom.getCRS());
		WKTReader jtsWktReader=new WKTReader(jtsGeomFactory);
	    String wktGeom=WktGeOxygene.makeWkt(geOxyGeom);
	    Geometry jtsGeom=jtsWktReader.read(wktGeom);
	    return jtsGeom;    
	}

	public static GM_Object makeGeOxygeneGeom(Geometry jtsGeom)
	throws Exception
	{
		WKTWriter jtsWktWriter=new WKTWriter();
	    String wktResult=jtsWktWriter.write(jtsGeom);
	    GM_Object geOxyGeom=WktGeOxygene.makeGeOxygene(wktResult);        
	    return geOxyGeom;
	}

	public static DirectPosition makeDirectPosition(CoordinateSequence jtsCoord)
	throws Exception
	{
		GeometryFactory jtsGeomFactory=new GeometryFactory(JtsGeOxygene.jtsPrecision,0);
	    Geometry jtsPoint=new Point(jtsCoord, jtsGeomFactory);
	    GM_Point geOxyPoint=(GM_Point)JtsGeOxygene.makeGeOxygeneGeom(jtsPoint);
	    DirectPosition geOxyDirectPos=geOxyPoint.getPosition();
	    return geOxyDirectPos;
	}

	public static DirectPositionList makeDirectPositionList(CoordinateSequence[] jtsCoords)
	throws Exception
	{
	    DirectPositionList list=new DirectPositionList();
	    for (int i=0; i<jtsCoords.length; i++) {
	        list.add(JtsGeOxygene.makeDirectPosition(jtsCoords[i]));
	    }
	    return list;
	}
}

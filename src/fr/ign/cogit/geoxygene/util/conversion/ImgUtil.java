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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import javax.imageio.ImageIO;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.fop.svg.PDFTranscoder;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;


public class ImgUtil {

/*--------------------------------------------------------------*/
/*-- drawGeom() ------------------------------------------------*/
/*--------------------------------------------------------------*/

private static
void drawGeom(
		Graphics2D g,
		GM_Object[] geoms, Color[] colors,
		AffineTransform transform)
throws Exception
{
	if (geoms.length!=colors.length)
		throw new IllegalArgumentException("geoms.length!=colors.length");
	for (int i=0; i<geoms.length; i++) {
		drawGeom(g, geoms[i], colors[i], transform);
	}
}

private static
void drawGeom(
        Graphics2D g, GM_Object geom, Color color, AffineTransform transform)
throws Exception
{
	if (isEmpty(geom)) return;
    else drawGeom(g, WktGeOxygene.makeWkt(geom), color, transform);
}

private static
void drawGeom(
        Graphics2D g, String wkt, Color color, AffineTransform transform)
throws Exception
{
    drawGeom(g, WktAwt.makeAwtShape(wkt), color, transform);
}

private static void
drawGeom(Graphics2D g, AwtShape geom, Color color, AffineTransform transform)
{
    g.setTransform(transform);
    g.setColor(color);
    geom.draw(g);
}

/*--------------------------------------------------------------*/
/*-- makeScaleTransform() --------------------------------------*/
/*--------------------------------------------------------------*/

private static AffineTransform
makeScaleTransform(GM_Object[] geoms, int width, int height)
throws Exception
{
	GeneralPath all=new GeneralPath();
	for (int i=0; i<geoms.length; i++) {
		if (!isEmpty(geoms[i])) {
			all.append(
				WktAwt.makeAwtShape(WktGeOxygene.makeWkt(geoms[i])).getBounds(),
				false);
		}
	}
	AffineTransform transform=ImgUtil.makeScaleTransform(all,width,height);
	return transform;
}

private static AffineTransform
makeScaleTransform(Shape geom, int width, int height)
{
	Rectangle2D bbox=geom.getBounds2D();
	bbox.setRect(bbox.getX(), bbox.getY(),
				 bbox.getWidth(), bbox.getHeight());

	double scaleX=width/bbox.getWidth();
	double scaleY=height/bbox.getHeight();
	double scale=(scaleX<scaleY)?scaleX:scaleY;
	double offsetX=-bbox.getX();
	double offsetY=-bbox.getY()-bbox.getHeight();
	AffineTransform transform=new AffineTransform();
	transform.scale(scale,-scale);
	transform.translate(offsetX,offsetY);
	return transform;        
}

private static AffineTransform
makeScaleTransform(Shape[] geoms, int width, int height)
{
	GeneralPath geom=new GeneralPath();
	for (int i=0; i<geoms.length; i++) {
		geom.append(geoms[i], false);
	}
	return makeScaleTransform(geom,width,height);
}

/*--------------------------------------------------------------*/
/*--------------------------------------------------------------*/
/*--------------------------------------------------------------*/

public static BufferedImage make(Color bg, int width, int height)
{
    BufferedImage image=
        new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g=image.createGraphics();
    g.setColor(bg);
    g.fillRect(0,0,width,height);
    g.dispose();
    return image;
}

/*-----------------------------------------------------------*/
/*-- save() -------------------------------------------------*/
/*-----------------------------------------------------------*/

public static void
saveImage(BufferedImage image, String path)
throws IOException
{
    String format="";
    String[] formatNames=ImageIO.getWriterFormatNames();
    for (int i=0; i<formatNames.length; i++) {
        if (path.endsWith("."+formatNames[i]))
            format=formatNames[i];
    }
    if (format.equals("")) {
        path+=".png";
        format="png";
    }
    ImageIO.write(image, format, new File(path));
}

public static void saveImage(GM_Object geom, String path)
throws Exception
{
    saveImage(geom, path, Color.BLACK, Color.WHITE, 800, 800);
}

public static void saveImage(List geomList, String path)
throws Exception
{
    saveImage(geomList, path, Color.BLACK, Color.WHITE, 800, 800);
}

public static void
saveImage(
	List geomList, String path,
	Color fg, Color bg,
	int width, int height)
throws Exception
{
	saveImage(new GM_Aggregate(geomList), path, fg, bg, width, height);
}

public static void saveImage(
	GM_Object geom, String path,
	Color fg, Color bg, int width, int height)
throws Exception
{
	String wkt= WktGeOxygene.makeWkt(geom);
	AwtShape awt;

	awt= WktAwt.makeAwtShape(wkt);

	Rectangle2D bbox= awt.getBounds();

	AffineTransform transform= ImgUtil.makeScaleTransform(bbox, width, height);
	BufferedImage image= ImgUtil.make(bg, width, height);
	ImgUtil.drawGeom(image.createGraphics(), awt, fg, transform);

	ImgUtil.saveImage(image, path);
}

public static void saveImage(
					GM_Object[] geoms, String path,
					Color[] foregrounds, Color background,
					int width, int height)
throws Exception
{
	AffineTransform transform=ImgUtil.makeScaleTransform(geoms,width,height);
	BufferedImage image=ImgUtil.make(background,width,height);
	Graphics2D g=image.createGraphics();
	ImgUtil.drawGeom(g,geoms,foregrounds,transform);
	ImgUtil.saveImage(image,path);
}

public static void savePdf(
					GM_Object[] geoms, String path,
					Color[] foregrounds, Color background,
					int width, int height)
throws Exception
{
	DOMImplementation impl=GenericDOMImplementation.getDOMImplementation();
	Document svgDoc=impl.createDocument(null, "svg", null);
	
	AffineTransform transform=ImgUtil.makeScaleTransform(geoms,width,height);
	final SVGGraphics2D svgGenerator=new SVGGraphics2D(svgDoc);
	svgGenerator.setSVGCanvasSize(new Dimension(width,height));
	ImgUtil.drawGeom(svgGenerator,geoms,foregrounds,transform);
	
	final PipedWriter svgGenOut=new PipedWriter();
	final PipedReader pdfTransIn=new PipedReader(svgGenOut);
	final OutputStream pdfOut=new FileOutputStream(path);

	Thread generateSvg=new Thread() {
		public void run() {
			boolean useCss=true;
			try {
				svgGenerator.stream(svgGenOut, useCss);
				svgGenOut.flush();
				svgGenOut.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	Thread transcodeToPdf=new Thread() {
		public void run() {
			PDFTranscoder pdfTranscoder=new PDFTranscoder();
			TranscoderInput tIn=new TranscoderInput(pdfTransIn);
			TranscoderOutput tOut=new TranscoderOutput(pdfOut);
			try {
				pdfTranscoder.transcode(tIn, tOut);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	};
	
	generateSvg.start();
	transcodeToPdf.start();
	generateSvg.join();
	transcodeToPdf.join();	
}

public static void saveSvgz(
					GM_Object[] geoms, String path,
					Color[] foregrounds, Color background,
					int width, int height)
throws Exception
{
	DOMImplementation impl=GenericDOMImplementation.getDOMImplementation();
	Document svgDoc=impl.createDocument(null, "svg", null);
	
	AffineTransform transform=ImgUtil.makeScaleTransform(geoms,width,height);
	
	final SVGGraphics2D svgGenerator=new SVGGraphics2D(svgDoc);
	svgGenerator.setSVGCanvasSize(new Dimension(width, height));

//	Stroke stroke=new BasicStroke(.4f); 
//	svgGenerator.setStroke(stroke);
	
	ImgUtil.drawGeom(svgGenerator,geoms,foregrounds,transform);
	svgGenerator.setTransform(new AffineTransform());
	
	Writer svgGenOut=
		new OutputStreamWriter(
				new GZIPOutputStream(new FileOutputStream(path)), "UTF-8");
	

	boolean useCss=true;
	svgGenerator.stream(svgGenOut, useCss);
	svgGenOut.flush();
	svgGenOut.close();
}

// utils //
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


} // class

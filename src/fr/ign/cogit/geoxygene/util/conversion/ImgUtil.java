/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.conversion;

import java.awt.AlphaComposite;
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

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_Aggregate;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * Class used for writing image files.
 */
public class ImgUtil {

  /*--------------------------------------------------------------*/
  /*-- drawGeom() ------------------------------------------------*/
  /*--------------------------------------------------------------*/

  private static void drawGeom(Graphics2D g, IGeometry[] geoms, Color[] colors,
      AffineTransform transform) throws Exception {
    if (geoms.length != colors.length) {
      throw new IllegalArgumentException("geoms.length!=colors.length"); //$NON-NLS-1$
    }
    for (int i = 0; i < geoms.length; i++) {
      ImgUtil.drawGeom(g, geoms[i], colors[i], transform);
    }
  }

  private static void drawGeom(Graphics2D g, IGeometry geom, Color color,
      AffineTransform transform) throws Exception {
    if (ImgUtil.isEmpty(geom)) {
      return;
    }
    ImgUtil.drawGeom(g, WktGeOxygene.makeWkt(geom), color, transform);
  }

  private static void drawGeom(Graphics2D g, String wkt, Color color,
      AffineTransform transform) throws Exception {
    ImgUtil.drawGeom(g, WktAwt.makeAwtShape(wkt), color, transform);
  }

  private static void drawGeom(Graphics2D g, AwtShape geom, Color color,
      AffineTransform transform) {
    g.setTransform(transform);
    g.setColor(color);
    geom.draw(g);
  }

  /*--------------------------------------------------------------*/
  /*-- makeScaleTransform() --------------------------------------*/
  /*--------------------------------------------------------------*/

  private static AffineTransform makeScaleTransform(IGeometry[] geoms,
      int width, int height) throws Exception {
    GeneralPath all = new GeneralPath();
    for (int i = 0; i < geoms.length; i++) {
      if (!ImgUtil.isEmpty(geoms[i])) {
        all.append(WktAwt.makeAwtShape(WktGeOxygene.makeWkt(geoms[i]))
            .getBounds(), false);
      }
    }
    System.out.println("scale all");
    AffineTransform transform = ImgUtil.makeScaleTransform(all, width, height);
    System.out.println("scale finished");
    return transform;
  }

  private static AffineTransform makeScaleTransform(Shape geom, int width,
      int height) {
    Rectangle2D bbox = geom.getBounds2D();
    bbox.setRect(bbox.getX(), bbox.getY(), bbox.getWidth(), bbox.getHeight());

    double scaleX = width / bbox.getWidth();
    double scaleY = height / bbox.getHeight();
    double scale = (scaleX < scaleY) ? scaleX : scaleY;
    double offsetX = -bbox.getX();
    double offsetY = -bbox.getY() - bbox.getHeight();
    AffineTransform transform = new AffineTransform();
    transform.scale(scale, -scale);
    transform.translate(offsetX, offsetY);
    return transform;
  }

  @SuppressWarnings("unused")
  private static AffineTransform makeScaleTransform(Shape[] geoms, int width,
      int height) {
    GeneralPath geom = new GeneralPath();
    for (Shape geom2 : geoms) {
      geom.append(geom2, false);
    }
    return ImgUtil.makeScaleTransform(geom, width, height);
  }

  /*--------------------------------------------------------------*/
  /*--------------------------------------------------------------*/
  /*--------------------------------------------------------------*/

  public static BufferedImage make(Color bg, int width, int height) {
    BufferedImage image = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    g.setColor(bg);
    g.fillRect(0, 0, width, height);
    g.dispose();
    return image;
  }

  public static BufferedImage makeWithoutBackground(int width, int height) {
    BufferedImage image = new BufferedImage(width, height,
        BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = image.createGraphics();
    // make sure the background is filled with transparent pixels when
    // cleared !
    g.setBackground(new Color(0, 0, 0, 0));
    g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_IN, 0.0f));

    g.fillRect(0, 0, width, height);
    g.dispose();
    return image;
  }

  /*-----------------------------------------------------------*/
  /*-- save() -------------------------------------------------*/
  /*-----------------------------------------------------------*/

  /**
   * Save an image with the given file name. The method tries to save the image
   * using the file extension. If the file name is not supported or if there is
   * none, the image is saved as PNG.
   * @param image image to save
   * @param path file name
   * @throws IOException
   */
  public static void saveImage(BufferedImage image, String path)
      throws IOException {
    String format = ""; //$NON-NLS-1$
    String[] formatNames = ImageIO.getWriterFormatNames();
    for (String formatName : formatNames) {
      if (path.endsWith("." + formatName)) { //$NON-NLS-1$
        format = formatName;
      }
    }
    String newPath = path;
    if (format.equals("")) { //$NON-NLS-1$
      newPath += ".png"; //$NON-NLS-1$
      format = "png"; //$NON-NLS-1$
    }
    ImageIO.write(image, format, new File(newPath));
  }

  public static void saveImage(IGeometry geom, String path) throws Exception {
    ImgUtil.saveImage(geom, path, Color.BLACK, Color.WHITE, 800, 800);
  }

  public static void saveImage(List<IGeometry> geomList, String path)
      throws Exception {
    ImgUtil.saveImage(geomList, path, Color.BLACK, Color.WHITE, 800, 800);
  }

  public static void saveImage(List<IGeometry> geomList, String path, Color fg,
      Color bg, int width, int height) throws Exception {
    ImgUtil.saveImage(new GM_Aggregate<IGeometry>(geomList), path, fg, bg,
        width, height);
  }

  public static void saveImage(IGeometry geom, String path, Color fg, Color bg,
      int width, int height) throws Exception {
    String wkt = WktGeOxygene.makeWkt(geom);
    AwtShape awt;

    awt = WktAwt.makeAwtShape(wkt);

    Rectangle2D bbox = awt.getBounds();

    AffineTransform transform = ImgUtil.makeScaleTransform(bbox, width, height);
    BufferedImage image = ImgUtil.make(bg, width, height);
    ImgUtil.drawGeom(image.createGraphics(), awt, fg, transform);

    ImgUtil.saveImage(image, path);
  }

  /**
   * Save a collection of geometries in the given image file.
   * @param geoms geometries
   * @param path image file
   * @param foregrounds colors associated with each geometry
   * @param background background color
   * @param width width of the image
   * @param height height of the image
   * @throws Exception
   */
  public static void saveImage(IGeometry[] geoms, String path,
      Color[] foregrounds, Color background, int width, int height)
      throws Exception {
    AffineTransform transform = ImgUtil
        .makeScaleTransform(geoms, width, height);
    BufferedImage image = ImgUtil.make(background, width, height);
    Graphics2D g = image.createGraphics();
    System.out.println("draw");
    ImgUtil.drawGeom(g, geoms, foregrounds, transform);
    System.out.println("saving the actual image");
    ImgUtil.saveImage(image, path);
  }

  public static void saveImageWithoutBackground(GM_Object[] geoms, String path,
      Color[] foregrounds, int width, int height) throws Exception {
    AffineTransform transform = ImgUtil
        .makeScaleTransform(geoms, width, height);
    BufferedImage image = ImgUtil.makeWithoutBackground(width, height);
    Graphics2D g = image.createGraphics();
    ImgUtil.drawGeom(g, geoms, foregrounds, transform);
    ImgUtil.saveImage(image, path);
  }

  /**
   * @param geoms
   * @param path
   * @param foregrounds
   * @param background
   * @param width
   * @param height
   * @throws Exception
   */
  public static void savePdf(IGeometry[] geoms, String path,
      Color[] foregrounds, Color background, int width, int height)
      throws Exception {
    DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();
    Document svgDoc = impl.createDocument(null, "svg", null); //$NON-NLS-1$

    AffineTransform transform = ImgUtil
        .makeScaleTransform(geoms, width, height);
    final SVGGraphics2D svgGenerator = new SVGGraphics2D(svgDoc);
    svgGenerator.setSVGCanvasSize(new Dimension(width, height));
    ImgUtil.drawGeom(svgGenerator, geoms, foregrounds, transform);

    final PipedWriter svgGenOut = new PipedWriter();
    final PipedReader pdfTransIn = new PipedReader(svgGenOut);
    final OutputStream pdfOut = new FileOutputStream(path);

    Thread generateSvg = new Thread() {
      @Override
      public void run() {
        boolean useCss = true;
        try {
          svgGenerator.stream(svgGenOut, useCss);
          svgGenOut.flush();
          svgGenOut.close();
        } catch (Exception e) {
          throw new RuntimeException(e);
        }
      }
    };

    Thread transcodeToPdf = new Thread() {
      @Override
      public void run() {
        PDFTranscoder pdfTranscoder = new PDFTranscoder();
        TranscoderInput tIn = new TranscoderInput(pdfTransIn);
        TranscoderOutput tOut = new TranscoderOutput(pdfOut);
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

  /**
   * @param geoms
   * @param path
   * @param foregrounds
   * @param background
   * @param width
   * @param height
   * @throws Exception
   */
  public static void saveSvgz(IGeometry[] geoms, String path,
      Color[] foregrounds, Color background, int width, int height)
      throws Exception {
    DOMImplementation impl = GenericDOMImplementation.getDOMImplementation();
    Document svgDoc = impl.createDocument(null, "svg", null); //$NON-NLS-1$

    AffineTransform transform = ImgUtil
        .makeScaleTransform(geoms, width, height);

    final SVGGraphics2D svgGenerator = new SVGGraphics2D(svgDoc);
    svgGenerator.setSVGCanvasSize(new Dimension(width, height));

    // Stroke stroke=new BasicStroke(.4f);
    // svgGenerator.setStroke(stroke);

    ImgUtil.drawGeom(svgGenerator, geoms, foregrounds, transform);
    svgGenerator.setTransform(new AffineTransform());

    Writer svgGenOut = new OutputStreamWriter(new GZIPOutputStream(
        new FileOutputStream(path)), "UTF-8"); //$NON-NLS-1$

    boolean useCss = true;
    svgGenerator.stream(svgGenOut, useCss);
    svgGenOut.flush();
    svgGenOut.close();
  }

  // utils //
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public static boolean isEmpty(IGeometry geom) {
    if (geom == null) {
      return true;
    }
    if (geom instanceof IPoint) {
      return ImgUtil.isEmpty((IPoint) geom);
    }
    if (geom instanceof IPolygon) {
      return ImgUtil.isEmpty((IPolygon) geom);
    }
    if (geom instanceof ILineString) {
      return ImgUtil.isEmpty((ILineString) geom);
    }
    if (geom instanceof IAggregate) {
      return ImgUtil.isEmpty((IAggregate) geom);
    }
    return false;
  }

  public static boolean isEmpty(IPoint point) {
    IDirectPosition position = point.getPosition();
    double x = position.getX();
    double y = position.getY();
    double z = position.getZ();
    return (x == Double.NaN || y == Double.NaN || z == Double.NaN);
  }

  public static boolean isEmpty(IPolygon poly) {
    return poly.coord().isEmpty();
  }

  public static boolean isEmpty(GM_LineString lineString) {
    return lineString.getControlPoint().isEmpty();
  }

  static boolean isEmpty(IAggregate<IGeometry> aggr) {
    for (IGeometry geom : aggr) {
      if (!ImgUtil.isEmpty(geom)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Save collections of features in an image file.
   * @param <Feature> Type of the features to save
   * @param collections collections of features
   * @param colors colors associated with each collection
   * @param background background color
   * @param path file name
   * @param scale scale used for the rendering
   * @throws Exception
   */
  public static <Feature extends IFeature> void collectionsToImage(
      List<? extends IFeatureCollection<Feature>> collections,
      List<Color> colors, Color background, String path, double scale)
      throws Exception {
    int totalSize = 0; // number of features
    IEnvelope envelope = null;
    for (IFeatureCollection<Feature> c : collections) {
      IEnvelope env = c.envelope();
      if (envelope == null) {
        envelope = env;
      } else {
        envelope.expand(env);
      }
      totalSize = totalSize + c.size();
    }
    if (envelope == null) {
      return;
    }
    Color[] foregrounds = new Color[totalSize];
    double widthReal = envelope.width() / scale;
    double heightReal = envelope.length() / scale;
    int width = new Double(widthReal).intValue();
    int height = new Double(heightReal).intValue();
    System.out.println("Envelope " + envelope.width() + " x "
        + envelope.length());
    System.out.println("Envelope out " + width + " x " + height);
    int i = 0, j = 0;
    IGeometry[] geometries = new IGeometry[totalSize];
    for (IFeatureCollection<Feature> c : collections) {
      for (IFeature f : c) {
        geometries[i] = f.getGeom();
        foregrounds[i] = colors.get(j);
        i++;
      }
      j++;
    }
    System.out.println("save");
    ImgUtil.saveImage(geometries, path, foregrounds, background, width, height);
  }
} // class

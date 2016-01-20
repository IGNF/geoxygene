/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.awt.Color;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLComplexFactory;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.appli.gl.GLTextComplex;
import fr.ign.cogit.geoxygene.appli.render.methods.NamedRenderingParametersMap;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;

/**
 * @author JeT
 * 
 */
public class DisplayablePoint extends AbstractDisplayable {

    private static final Logger logger = Logger.getLogger(DisplayablePoint.class.getName()); // logger
    private static final Color color = Color.GREEN;
    private final List<IGeometry> geometries = new ArrayList<IGeometry>();
    private Map<Object, List<GLComplex>> markscomplexesmap = new WeakHashMap<Object, List<GLComplex>>();

    /**
     * Constructor
     * 
     * @param name
     * @param viewport
     * @param multiPoints
     * @param symbolizer
     * @param textures_root_uri
     */
    public DisplayablePoint(String name, IGeometry geometry, IFeature feature, Symbolizer symbolizer, Viewport p, URI tex_root_uri) {
        super(name, feature, symbolizer, p, tex_root_uri);
        this.addGeometry(geometry);
        this.tex_root_uri = tex_root_uri;
        this.generatePartialRepresentation();
    }

    private final void generatePartialRepresentation() {
        IEnvelope envelope = IGeometryUtil.getEnvelope(this.geometries);
        double minX = envelope.minX();
        double minY = envelope.minY();
        this.setPartialRepresentation(GLComplexFactory.createQuickPoints(this.getName() + "-partial", this.geometries, color, null, minX, minY));
    }

    /**
     * @param point
     */
    public final void addGeometry(IGeometry geometry) {
        this.geometries.add(geometry);
    }

    @Override
    public boolean isProgressable() {
        return false;
    }

    @Override
    public boolean isPausable() {
        return false;
    }

    @Override
    public boolean isStoppable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()
     */
    @Override
    public List<GLComplex> generateFullRepresentation() {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        Symbolizer sym = this.getSymbolizer();

        if (sym.isPointSymbolizer()) {
            PointSymbolizer pointSymbolizer = (PointSymbolizer) sym;
            complexes.addAll(this.generateWithPointSymbolizer(pointSymbolizer));
            return complexes;
        } else if (sym.isTextSymbolizer()) {
            GLTextComplex primitive = new GLTextComplex("toponym-" + this.getName(), 0, 0, this.getFeature());
            complexes.add(primitive);
            return complexes;
        }
        logger.error("Do not handle points with " + sym.getClass().getSimpleName() + " symbolizer");
        super.setError(new UnsupportedOperationException("Do not handle points with " + sym.getClass().getSimpleName() + " symbolizer"));
        super.setState(TaskState.ERROR);
        return null;
    }

    private List<GLComplex> generateWithPointSymbolizer(PointSymbolizer symbolizer) {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        IEnvelope envelope = IGeometryUtil.getEnvelope(this.geometries);
        double minX = envelope.minX();
        double minY = envelope.minY();

        for (Mark mark : symbolizer.getGraphic().getMarks()) {
            Shape markShape = mark.toShape();
            float size = symbolizer.getGraphic().getSize();
            // create the shape fo the mark. Adaptive transform will be done for
            // each point (location, size, etc...)
            AffineTransform at = new AffineTransform();
            at.scale(size, size);
            markShape = at.createTransformedShape(markShape);

            List<GLComplex> inners = new ArrayList<>();
            List<GLComplex> outlines = new ArrayList<>();
            for (IGeometry geometry : this.geometries) {
                AffineTransform atGeometry = new AffineTransform();
                atGeometry.translate(geometry.centroid().getX(), geometry.centroid().getY());
                Shape markShapeGeometry = atGeometry.createTransformedShape(markShape);
                // TODO: add scale (viewport depend) and rotation
                GLSimpleComplex markFillComplex = GLComplexFactory.toGLComplex(this.getName() + "-mark-filled", markShapeGeometry, minX, minY);
                markFillComplex.setColor(mark.getFill().getColor());
                inners.add(markFillComplex);

                if (mark.getStroke() != null) {
                    // TODO: add scale (viewport depend) and rotation
                    GLSimpleComplex markOutlineComplex = GLComplexFactory.createShapeOutline(this.getName() + "-mark-outline", markShapeGeometry, mark.getStroke(), minX, minY);
                    markOutlineComplex.setColor(mark.getStroke().getColor());
                    outlines.add(markOutlineComplex);
                } else {
                    logger.warn("Mark point has a null stroke. They won't be renderered until a default stroke is set");
                }
            }
            if (mark.getStroke() != null)
                this.markscomplexesmap.put(mark.getStroke(), outlines);
            if (mark.getFill() != null)
                this.markscomplexesmap.put(mark.getFill(), inners);
            complexes.addAll(inners);
            complexes.addAll(outlines);
        }

        for (ExternalGraphic eg : symbolizer.getGraphic().getExternalGraphics()) {

            Image img = null;
            try {
                URI uri = new URI(eg.getHref());
                BasicTexture t = (BasicTexture) TextureManager.getTexture(uri);
                img = t.getTextureImage();
                if(img == null){
                    System.out.println("The texture "+uri+" is NULL...");
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            int imgw = img == null ? 0 : img.getWidth(null);
            int imgh = img == null ? 0 : img.getHeight(null);
            if (imgw == 0 || imgh == 0) {
                logger.error("Trying to create a DisplayablePoint with an ExternalGraphic but the image " + eg.getHref() + " is null or of size 0x0");
            } else {
                for (IGeometry g : this.geometries) {
                    float symwidth= symbolizer.getGraphic().getSize(); //C'est la largeur 
                    float symheight = imgh * symwidth / imgw;
                    DirectPosition topleft = new DirectPosition(0, symheight);
                    DirectPosition topright = new DirectPosition(symwidth,symheight );
                    DirectPosition botright = new DirectPosition(symwidth, 0);
                    DirectPosition botleft = new DirectPosition(0, 0);
                    IPolygon genvelope;
                    try {
                        genvelope = new GM_Polygon(new GM_LineString(topleft, topright, botright, botleft,topleft), 0.1);
                        genvelope = (IPolygon) genvelope.translate(g.centroid().getX()-symwidth/2, g.centroid().getY()-symheight/2, 0);
                        envelope = genvelope.getEnvelope();
                        BasicParameterizer parameterizer = new BasicParameterizer(envelope, false, true);
                        List<IPolygon> toFill = new ArrayList<IPolygon>();
                        toFill.add(genvelope);
                        GLSimpleComplex shapeComplex = GLComplexFactory.createFilledPolygons("1", toFill, Color.PINK, parameterizer, envelope.minX(), envelope.minY());
                        shapeComplex.setColor(Color.WHITE);
                        complexes.add(shapeComplex);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        return complexes;
    }

    public List<GLComplex> getMarkGLComplexes(Object styleElement) {
        return this.markscomplexesmap.get(styleElement);
    }

    @Override
    public void setCustomRenderingParameters(NamedRenderingParametersMap p) {
        // Nothing to do?
    }

}

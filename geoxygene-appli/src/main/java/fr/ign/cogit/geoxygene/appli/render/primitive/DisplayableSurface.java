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
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.lwjgl.opengl.GL11;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLComplexFactory;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.appli.gl.GLTextComplex;
import fr.ign.cogit.geoxygene.appli.gl.RasterImage;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;
import fr.ign.cogit.geoxygene.appli.render.methods.NamedRenderingParametersMap;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodParameterDescriptor;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.feature.FT_Coverage;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.RasterSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameter;
import fr.ign.cogit.geoxygene.style.texture.Texture;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLComplexRenderer;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLSimpleVertex;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;

/**
 * @author JeT A displayable surface is a displayable containing GL geometries
 *         matching IMultiSurfaces or IPolygon geoxygen geometries
 */
public class DisplayableSurface extends AbstractDisplayable {

    private static final Logger logger = Logger.getLogger(DisplayableSurface.class.getName()); // logger
    private static final Color color = Color.BLUE;

    private final List<IPolygon> polygons = new ArrayList<IPolygon>();
    private List<GLComplex> innerGLComplexes = new ArrayList<GLComplex>();
    private List<GLComplex> outlineGLComplexes = new ArrayList<GLComplex>();
    private WeakReference<RasterImage> rasterImageRef;

    /**
     * Constructor using a IMultiSurface
     * 
     * @param textures_root_uri
     */
    public DisplayableSurface(String name, IMultiSurface<?> multiSurface, IFeature feature, Symbolizer symbolizer, Viewport p, URI tex_root_uri) {
        super(name, feature, symbolizer, p, tex_root_uri);
        for (Object polygon : multiSurface.getList()) {
            if (polygon instanceof IPolygon) {
                this.polygons.add((IPolygon) polygon);

            } else {
                logger.warn("multisurface does not contains only IPolygons but " + polygon.getClass().getSimpleName());
            }
        }
        this.generatePartialRepresentation();
    }

    /**
     * @param textures_root_uri
     * 
     */
    public DisplayableSurface(String name, IPolygon polygon, IFeature feature, Symbolizer symbolizer, Viewport p, URI tex_root_uri) {
        super(name, feature, symbolizer, p, tex_root_uri);
        this.polygons.add(polygon);
        this.generatePartialRepresentation();
    }

    private void generatePartialRepresentation() {
        IEnvelope envelope = IGeometryUtil.getEnvelope(this.polygons);
        double minX = envelope.minX();
        double minY = envelope.minY();
        this.setPartialRepresentation(GLComplexFactory.createQuickPolygons(this.getName() + "-partial", this.polygons, color, null, minX, minY));
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isProgressable()
     */
    @Override
    public boolean isProgressable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isPausable()
     */
    @Override
    public boolean isPausable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.appli.task.Task#isStopable()
     */
    @Override
    public boolean isStoppable() {
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Runnable#run()data.getPolygons()
     */
    @Override
    public List<GLComplex> generateFullRepresentation() {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        if (this.getSymbolizer().isRasterSymbolizer()) {
            RasterSymbolizer rasterSymbolizer = (RasterSymbolizer) this.getSymbolizer();
            List<GLComplex> fullRep = this.generateWithRasterSymbolizer(rasterSymbolizer);
            if (fullRep != null) {
                complexes.addAll(fullRep);
            }
            return complexes;
        } else if (this.getSymbolizer().isPolygonSymbolizer()) {
            PolygonSymbolizer polygonSymbolizer = (PolygonSymbolizer) this.getSymbolizer();
            List<GLComplex> fullRep = this.generateWithPolygonSymbolizer(polygonSymbolizer);
            if (fullRep != null) {
                complexes.addAll(fullRep);
            }
            return complexes;
        } else if (this.getSymbolizer().isTextSymbolizer()) {
            GLTextComplex primitive = new GLTextComplex("toponym-" + this.getName(), 0, 0, this.getFeature());
            complexes.add(primitive);
            return complexes;

        } else if (this.getSymbolizer().isRasterSymbolizer()) {
            // Ajout @amasse 29/07/2015
            // Get back the symbolizer
            RasterSymbolizer rasterSymbolizer = (RasterSymbolizer) this.getSymbolizer();

            // Use the rasterSymbolizer for GLComplex generation
            List<GLComplex> fullRep = this.generateWithRasterSymbolizer(rasterSymbolizer);

            if (fullRep != null) {
                complexes.addAll(fullRep);
            }
            return complexes;
            // END Ajout @amasse 29/07/2015
        }
        logger.error("Surface rendering do not handle " + this.getSymbolizer().getClass().getSimpleName());
        super.setState(TaskState.ERROR);
        return null;
    }

    synchronized private List<GLComplex> generateWithRasterSymbolizer(RasterSymbolizer rasterSymbolizer) {

        // Check : if more than 1 Collection, select the first one
        // -> I am not okay with that, but who am i to judge ? just a developer.
        if (this.getFeature().getFeatureCollections().size() != 1) {
            logger.error("Feature " + this.getFeature() + " belongs to more than one feature collection, choose the first one arbitrarily");
        }

        // Get back the feature collection an create a GLComplex list
        IFeatureCollection<IFeature> featureCollection = this.getFeature().getFeatureCollection(0);
        List<GLComplex> complexes = new ArrayList<GLComplex>();

        // Get back the envelope
        IEnvelope envelope = featureCollection.getEnvelope();
        double minX = envelope.minX();
        double minY = envelope.minY();

        // FIXME Is it really necessary ?
        BasicParameterizer parameterizer = new BasicParameterizer(envelope, false, true);

        // Create a GLSimpleComplex object
        GLSimpleComplex content = GLComplexFactory.createFilledPolygons(this.getName() + "-texture-filled", this.polygons, color, parameterizer, minX, minY);

        // Create RasterImage and read it, once for all (the all shader life of
        // course)
        GridCoverage2D coverage = ((FT_Coverage) featureCollection.get(0)).coverage();
        try {
            //TODO : the name is not an unique identifier
            URI rimguri = new URI("RasterImage-" + coverage.getName());
            RasterImage raster = (RasterImage) TextureManager.retrieveTexture(rimguri);
            if (raster == null) {
                RasterImage rasterImage = new RasterImage();
                rasterImage.readImage(((FT_Coverage) featureCollection.get(0)).coverage(), rasterSymbolizer);
                TextureManager.addTexture(rimguri, rasterImage);
                raster = rasterImage;
            }
            this.rasterImageRef = new WeakReference<RasterImage>(raster);

            // retrive the colormap
            raster.readColormap(rasterSymbolizer);
            URI colormap_uri = new URI("ColorMap-" + coverage.getName() + "-" + rasterSymbolizer.hashCode());
            GLTexture colormap = TextureManager.retrieveTexture(colormap_uri);
            if (colormap == null) {
                TextureManager.addTexture(colormap_uri, raster.getImageColorMap());
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        content.setOverallOpacity(rasterSymbolizer.getOpacity());
        complexes.add(content);
        this.innerGLComplexes.add(content);
        return complexes;
    }

    synchronized private List<GLComplex> generateWithPolygonSymbolizer(PolygonSymbolizer symbolizer) {

        if (this.getFeature().getFeatureCollections().size() != 1) {
            logger.error("Feature " + this.getFeature() + " belongs to more than one feature collection, choose the first one arbitrarily");
        }
        IFeatureCollection<IFeature> featureCollection = this.getFeature().getFeatureCollection(0);
        // this complexes collection will contain inner and outline

        List<GLComplex> total = new ArrayList<GLComplex>();

        // generate Inner Polygon part)
        this.createInnerPolygon(symbolizer, featureCollection, this.innerGLComplexes);

        // generate Polygon Outline
        this.generatePolygonOutline(symbolizer, featureCollection, this.outlineGLComplexes);
        total.addAll(this.outlineGLComplexes);
        total.addAll(this.innerGLComplexes);

        return total;

    }

    /**
     * @param symbolizer
     * @param featureCollection
     * @param complexes
     */
    private boolean createInnerPolygon(PolygonSymbolizer symbolizer, IFeatureCollection<IFeature> featureCollection, List<GLComplex> complexes) {
        if (symbolizer.getFill() == null)
            return false;
        RenderingMethodDescriptor rm = null;
        if (symbolizer.getFill().getExpressiveFill() != null) {
            String rmethod = symbolizer.getFill().getExpressiveFill().getRenderingMethod();
            if (rmethod != null && !rmethod.isEmpty())
                rm = (RenderingMethodDescriptor) ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName).getResourceByName(rmethod);
            // Get the textures
            if (rm != null) {
                for (ExpressiveParameter p : symbolizer.getFill().getExpressiveFill().getExpressiveParameters()) {
                    RenderingMethodParameterDescriptor texparameter = null;
                    if (p.getValue() instanceof Texture) {
                        // Does this texture exists in the method?
                        if (rm.hasParameter(p.getName())) {
                            texparameter = rm.getParameter(p.getName());
                        }
                        if (texparameter == null) {
                            logger.error("The expressive element used to draw the feature " + this.getFeature() + " provide a texture named " + p.getName() + " but the rendering method "
                                    + rm.getName() + " has no such texture parameter.");
                            complexes.addAll(this.generateWithSolidColor(symbolizer, featureCollection));
                        } else {
                            this.createWithTextureDescriptor(featureCollection, complexes, texparameter, (Texture) p.getValue());
                        }
                    }
                }
            }
        }
        if (rm == null) {
            complexes.addAll(this.generateWithSolidColor(symbolizer, featureCollection));
        }
        return !complexes.isEmpty();
    }

    /**
     * @param symbolizer
     * @param featureCollection
     * @return
     */
    private boolean generatePolygonOutline(PolygonSymbolizer symbolizer, IFeatureCollection<IFeature> featureCollection, List<GLComplex> complexes) {
        if (symbolizer.getStroke() == null)
            return false;

        IEnvelope envelope = featureCollection.getEnvelope();
        double minX = envelope.minX();
        double minY = envelope.minY();
        // GeoxComplexRenderer renderer = GeoxRendererManager
        // .getOrCreateLineRenderer(symbolizer, this.getLayerRenderer());
        GLComplex outline = GLComplexFactory.createPolygonOutlines(this.getName() + "-outline", this.polygons, symbolizer.getStroke(), minX, minY);
        complexes.add(outline);

        return true;
    }

    /**
     * Generate GL-geometry for a given feature collection using a simple
     * texture. A basic Parameterizer is used and a BasicRenderer is assigned to
     * GL-geometry
     * 
     * @param featureCollection
     *            symbolizer containing the textureDescriptor
     * @param complexes
     *            GeOx-geometry to be transformed to GL-geometry
     * @param texparameter
     *            GL-complexes list to be filled with new GL-geometry
     * @param p
     *            texture descriptor extracted from symbolizer
     * @return
     */
    private boolean createWithTextureDescriptor(IFeatureCollection<IFeature> featureCollection, List<GLComplex> complexes, RenderingMethodParameterDescriptor texparameter, Texture tex) {
        IEnvelope envelope = featureCollection.getEnvelope();
        if (tex == null) {
            logger.error("Creating a DisplayableSurface with a texture (" + texparameter.getName() + ") but no such texture is provided by the symbolizer");
            return false;
        }
        this.createTexture(tex, false); // Launch the texture task
                                        // asynchronously.

        // should put it back?
        // draw the texture image into resulting image
        switch (tex.getTextureDrawingMode()) {
        case VIEWPORTSPACE:

            BasicParameterizer parameterizer = new BasicParameterizer(envelope, false, true);
            parameterizer.scaleX(1. / tex.getScaleFactor().getX());
            parameterizer.scaleY(1. / tex.getScaleFactor().getY());
            GLSimpleComplex inner = this.generateWithTextureAndParameterizer((PolygonSymbolizer) this.getSymbolizer(), parameterizer, envelope);
            inner.setOverallOpacity(((PolygonSymbolizer) this.getSymbolizer()).getFill().getFillOpacity());
            complexes.add(inner);

            break;
        case SCREENSPACE:
            logger.warn("Screenspace coordinates textures are not yet implemented in GL rendering");
            break;
        default:
            logger.warn("Do not know how to draw texture type " + tex.getTextureDrawingMode());
        }
        return true;
    }

    /**
     * Generates the GL-geometry for the inner part of 'this.polygons'
     * GeOx-geometry using a polygon symbolizer containing a simple color. A
     * 
     * @param symbolizer
     * @param featureCollection
     * @return
     */
    private List<GLComplex> generateWithSolidColor(PolygonSymbolizer symbolizer, IFeatureCollection<IFeature> featureCollection) {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        IEnvelope envelope = featureCollection.getEnvelope();

        double minX = envelope.minX();
        double minY = envelope.minY();

        GLSimpleComplex content = GLComplexFactory.createFilledPolygons(this.getName() + "-filled", this.polygons, symbolizer.getFill().getColor(), new BasicParameterizer(envelope, false, false),
                minX, minY);
        content.setColor(symbolizer.getFill().getColor());
        content.setOverallOpacity(symbolizer.getFill().getFillOpacity());
        complexes.add(content);
        return complexes;
    }

    /**
     * @param texture
     * @param parameterizer
     * @return
     */
    private GLSimpleComplex generateWithTextureAndParameterizer(PolygonSymbolizer symbolizer, Parameterizer parameterizer, IEnvelope envelope) {
        double minX = envelope.minX();
        double minY = envelope.minY();
        GLSimpleComplex content = GLComplexFactory.createFilledPolygons(this.getName() + "-texture-filled", this.polygons, symbolizer.getFill().getColor(), parameterizer, minX, minY);
        return content;
    }

    /**
     * Create a quad on an envelope (useful for debug purpose)
     * 
     * @param name
     * @param envelope
     * @param object
     * @param parameterizer
     * @param minX
     * @param minY
     * @param renderer
     * @return
     */
    private GLSimpleComplex GLComplexQuadEnvelope(String name, IEnvelope envelope, Object object, Parameterizer parameterizer, double minX, double minY, GLComplexRenderer renderer) {
        GLSimpleComplex primitive = new GLSimpleComplex(name + "-envelope", minX, minY);
        GLMesh quad = primitive.addGLMesh(GL11.GL_TRIANGLES);
        int nw = primitive.addVertex(new GLSimpleVertex(new float[] { (float) (envelope.getLowerCorner().getX() - minX), (float) (envelope.getLowerCorner().getY() - minY), 0 }, new float[] { 0, 1 }));
        int ne = primitive.addVertex(new GLSimpleVertex(new float[] { (float) (envelope.getLowerCorner().getX() - minX), (float) (envelope.getUpperCorner().getY() - minY), 0 }, new float[] { 0, 0 }));
        int sw = primitive.addVertex(new GLSimpleVertex(new float[] { (float) (envelope.getUpperCorner().getX() - minX), (float) (envelope.getLowerCorner().getY() - minY), 0 }, new float[] { 1, 1 }));
        int se = primitive.addVertex(new GLSimpleVertex(new float[] { (float) (envelope.getUpperCorner().getX() - minX), (float) (envelope.getUpperCorner().getY() - minY), 0 }, new float[] { 1, 0 }));
        quad.addIndices(nw, ne, se);
        quad.addIndices(nw, se, sw);
        // primitive.setRenderer(new GeoxComplexRendererBasic(this
        // .getLayerRenderer(), this.getSymbolizer()));
        return primitive;
    }

    public Collection<GLComplex> getInnerPrimitives() {
        return this.innerGLComplexes;
    }

    public Collection<GLComplex> getOutlinePrimitives() {
        return this.outlineGLComplexes;
    }

    @Override
    public void setCustomRenderingParameters(NamedRenderingParametersMap p) {
        if (this.getSymbolizer().isRasterSymbolizer() && this.rasterImageRef.get() != null) {
            try {
                GridCoverage2D coverage = ((FT_Coverage) this.getFeature().getFeatureCollection(0).get(0)).coverage();
                URI rasteruri = new URI("RasterImage-" + coverage.getName());
                URI colormapuri = new URI("ColorMap-" + coverage.getName() + "-" + this.getSymbolizer().hashCode());
                RenderingMethodParameterDescriptor rasterParameter = new RenderingMethodParameterDescriptor("bufferImage");
                p.put(rasterParameter, rasteruri);
                RenderingMethodParameterDescriptor colorMapParameter = new RenderingMethodParameterDescriptor("bufferColormap");
                p.put(colorMapParameter, colormapuri);
            } catch (URISyntaxException e) {
                e.printStackTrace();

            }
        }
    }

}
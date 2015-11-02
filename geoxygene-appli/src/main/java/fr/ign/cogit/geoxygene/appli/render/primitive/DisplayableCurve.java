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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLBezierShadingComplex;
import fr.ign.cogit.geoxygene.appli.gl.GLComplexFactory;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.appli.gl.GLTextComplex;
import fr.ign.cogit.geoxygene.appli.gl.LineTesselator;
import fr.ign.cogit.geoxygene.appli.render.methods.NamedRenderingParametersMap;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameter;
import fr.ign.cogit.geoxygene.style.texture.Texture;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;

/**
 * @author JeT, Bertrand Duménieu
 * 
 */
public class DisplayableCurve extends AbstractDisplayable {

    private static final Logger logger = Logger.getLogger(DisplayableCurve.class.getName()); // logger

    private static final Color partialColor = Color.RED;
    public static final Function1D DefaultLineWidthFunction = new ConstantFunction(1);
    public static final Function1D DefaultLineShiftFunction = new ConstantFunction(0);
    private final List<ILineString> curves = new ArrayList<ILineString>();

    /**
     * Constructor using a IMultiCurve
     * 
     * @param textures_root_uri
     */
    public DisplayableCurve(String name, IMultiCurve<?> multiCurve, IFeature feature, Symbolizer symbolizer, Viewport p, URI tex_root_uri) {
        super(name, feature, symbolizer, p, tex_root_uri);

        for (Object lineString : multiCurve.getList()) {
            if (lineString instanceof ILineString) {
                this.curves.add((ILineString) lineString);
            } else {
                logger.warn("A MultiCurve does not contain only ILineString but " + this.curves.getClass().getSimpleName());
            }
        }
        this.generatePartialRepresentation();
    }

    /**
     * Constructor using a ILineString
     */
    public DisplayableCurve(String name, ILineString lineString, IFeature feature, Symbolizer symbolizer, Viewport p, URI tex_root_uri) {
        super(name, feature, symbolizer, p, tex_root_uri);
        this.curves.add(lineString);
        this.generatePartialRepresentation();
    }

    public void generatePartialRepresentation() {
        IEnvelope envelope = IGeometryUtil.getEnvelope(this.curves);
        double minX = envelope.minX();
        double minY = envelope.minY();
        GLSimpleComplex partialRep = GLComplexFactory.createQuickLine(this.getName() + "-partial", this.curves, partialColor, null, minX, minY);
        this.setPartialRepresentation(partialRep);
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
     * @see java.lang.Runnable#run()
     */
    @Override
    public List<GLComplex> generateFullRepresentation() {
        super.setState(TaskState.RUNNING);
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        if (this.getSymbolizer().isLineSymbolizer()) {
            LineSymbolizer lineSymbolizer = (LineSymbolizer) this.getSymbolizer();
            String method_name = null;
            if (lineSymbolizer.getStroke() != null) {
                if (lineSymbolizer.getStroke().getExpressiveStroke() != null) {
                    complexes.addAll(this.generateWithExpressiveStroke(lineSymbolizer, method_name));
                } else {
                    // No rendering method or no expressive rendering ->
                    // AWT-like
                    // rendering
                    complexes.addAll(this.generateWithLineSymbolizer(lineSymbolizer));
                }
            }
            return complexes;
        } else if (this.getSymbolizer().isTextSymbolizer()) {
            GLTextComplex primitive = new GLTextComplex("toponym-" + this.getName(), 0, 0, this.getFeature());
            complexes.add(primitive);
            return complexes;
        }
        logger.error("Curve rendering do not handle " + this.getSymbolizer().getClass().getSimpleName());
        super.setState(TaskState.ERROR);
        return null;
    }

    private List<GLComplex> generateWithLineSymbolizer(LineSymbolizer symbolizer) {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        IEnvelope envelope = IGeometryUtil.getEnvelope(this.curves);
        double minX = envelope.minX();
        double minY = envelope.minY();
        GLSimpleComplex line = LineTesselator.createThickLine(this.getName() + "-full", this.curves, symbolizer.getStroke(), minX, minY);
        line.setColor(symbolizer.getStroke().getColor());
        line.setOverallOpacity(symbolizer.getStroke().getColor().getAlpha() / 255.);
        complexes.add(line);
        return complexes;
    }

    private List<GLComplex> generateWithExpressiveStroke(LineSymbolizer symbolizer, String method_name) {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        /*
         * XXX : Here there is a hack. At this time, there is no clear
         * distinction between the tessellation of the geometries and the
         * rendering step. The Tessellation must know some of the rendering
         * parameters to initialize UV coordinates of the triangle vertices.
         * This is not a good behavior since it causes two different steps
         * (geometry generation and styling) to mix together.
         * 
         * As long as this is not fixed, here is the hack : - We get the
         * RenderingMethod associated with the Stroke and we check that the
         * needed parameters are present (if not -> exception) - We get or
         * create the needed textures.
         */

        RenderingMethodDescriptor method = this.getRenderingMethod("BrushStroke");
        if (method == null) {
            throw new NullPointerException("The method used to create Bezier Displayable does not exist");
        }

        // Check that the Stroke contains the needed parameters
        if (symbolizer.getStroke().getExpressiveStroke() != null) {
            ExpressiveDescriptor exstroke = symbolizer.getStroke().getExpressiveStroke();
            Texture paperTexture = (Texture) exstroke.getExpressiveParameter("paperTexture").getValue();
            ExpressiveParameter paperHeightInCm = exstroke.getExpressiveParameter("paperSizeInCm"); // --Nope
            ExpressiveParameter mapScale = exstroke.getExpressiveParameter("paperReferenceMapScale"); // --Nope
            ExpressiveParameter transitionSize = exstroke.getExpressiveParameter("transitionSize");
            if (paperTexture == null || paperHeightInCm == null || mapScale == null || transitionSize == null) {
                logger.error("The ExpressiveStroke does not have the needed parameters to create an Expressive Stroke geometry");
                return null;
            }

            // We need the paperTexture so we have to create it new.
            URI glPaperTextureURI = this.createTexture(paperTexture, true);
            GLTexture glPaperTexture = TextureManager.retrieveTexture(glPaperTextureURI);
            if (glPaperTexture == null) {
                logger.error("Failed to build a BezierCurbe because the paper texture " + glPaperTextureURI + " cannot be found");
            }
            // We can finally create the Complex
            IEnvelope envelope = IGeometryUtil.getEnvelope(this.curves);
            double minX = envelope.minX();
            double minY = envelope.minY();
            GLBezierShadingComplex complex = GLComplexFactory.createBezierThickCurves(this.getName() + "-expressive-bezier", symbolizer.getStroke(), minX, minY, curves, glPaperTexture,
                    (Double) paperHeightInCm.getValue(), (Double) mapScale.getValue(), (Double) transitionSize.getValue());
            complexes.add(complex);
            this.setFullRepresentation(complexes);
            return complexes;
        }
        logger.error("Cannot build an Expressive Stroke if no ExpressiveStroke is described in the Stroke style element");
        return null;
    }

    @Override
    public void dispose() {
        super.dispose();
        this.curves.clear();
    }

    @Override
    public void setCustomRenderingParameters(NamedRenderingParametersMap p) {
        //Nothing to do?
    }

}

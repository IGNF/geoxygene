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
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLBezierShadingComplex;
import fr.ign.cogit.geoxygene.appli.gl.GLComplexFactory;
import fr.ign.cogit.geoxygene.appli.gl.GLPaintingComplex;
import fr.ign.cogit.geoxygene.appli.gl.GLSimpleComplex;
import fr.ign.cogit.geoxygene.appli.gl.LineTesselator;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.render.texture.BasicTextureExpressiveRendering;
import fr.ign.cogit.geoxygene.appli.render.texture.StrokeTextureExpressiveRendering;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.function.ConstantFunction;
import fr.ign.cogit.geoxygene.function.Function1D;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.expressive.BasicTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.StrokeTextureExpressiveRenderingDescriptor;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLComplexRenderer;

/**
 * @author JeT
 * 
 */
public class DisplayableCurve extends AbstractDisplayable {

    private static final Logger logger = Logger
            .getLogger(DisplayableCurve.class.getName()); // logger

    private static final Colorizer partialColorizer = new SolidColorizer(
            Color.red);
    public static final Function1D DefaultLineWidthFunction = new ConstantFunction(
            1);
    public static final Function1D DefaultLineShiftFunction = new ConstantFunction(
            0);

    private final List<ILineString> curves = new ArrayList<ILineString>();

    // private Colorizer colorizer = null;
    // private Parameterizer parameterizer = null;

    // private Texture texture = null;

    /**
     * Constructor using a IMultiCurve
     */
    public DisplayableCurve(String name, Viewport viewport,
            IMultiCurve<?> multiCurve, Symbolizer symbolizer,
            LwjglLayerRenderer lwjglLayerRenderer,
            GLComplexRenderer partialRenderer) {
        super(name, viewport, lwjglLayerRenderer, symbolizer);

        for (Object lineString : multiCurve.getList()) {
            if (lineString instanceof ILineString) {
                this.curves.add((ILineString) lineString);
            } else {
                logger.warn("multisurface does not contain only ILineString but "
                        + this.curves.getClass().getSimpleName());
            }
        }
        this.generatePartialRepresentation(partialRenderer);
    }

    /**
     * Constructor using a ILineString
     */
    public DisplayableCurve(String name, Viewport viewport,
            ILineString lineString, Symbolizer symbolizer,
            LwjglLayerRenderer lwjglLayerRenderer,
            GLComplexRenderer partialRenderer) {
        super(name, viewport, lwjglLayerRenderer, symbolizer);
        this.curves.add(lineString);
        this.generatePartialRepresentation(partialRenderer);
    }

    public void generatePartialRepresentation(GLComplexRenderer partialRenderer) {
        IEnvelope envelope = IGeometryUtil.getEnvelope(this.curves);
        double minX = envelope.minX();
        double minY = envelope.minY();
        this.setPartialRepresentation(GLComplexFactory.createQuickLine(
                this.getName() + "-partial", this.curves, partialColorizer,
                null, minX, minY, partialRenderer));
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
        // System.err.println("Displayable curve start");
        super.setState(TaskState.RUNNING);

        // if (this.getTexture() != null) {
        // this.generateWithDistanceField();
        // }

        if (this.getSymbolizer() instanceof LineSymbolizer) {
            List<GLComplex> complexes = new ArrayList<GLComplex>();
            LineSymbolizer lineSymbolizer = (LineSymbolizer) this
                    .getSymbolizer();
            if (lineSymbolizer.getStroke().getExpressiveRendering() == null) {
                complexes.addAll(this
                        .generateWithLineSymbolizer(lineSymbolizer));
            } else {
                complexes.addAll(this
                        .generateWithExpressiveStroke(lineSymbolizer));
            }
            return complexes;
        } else {
            logger.error("Curve rendering do not handle "
                    + this.getSymbolizer().getClass().getSimpleName());
            super.setState(TaskState.ERROR);
            return null;
        }
    }

    private List<GLComplex> generateWithLineSymbolizer(LineSymbolizer symbolizer) {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        // return GLComplexFactory.createFilledPolygon(multiSurface,
        // symbolizer.getStroke().getColor());
        IEnvelope envelope = IGeometryUtil.getEnvelope(this.curves);
        double minX = envelope.minX();
        double minY = envelope.minY();

        // BasicStroke awtStroke =
        // GLComplexFactory.geoxygeneStrokeToAWTStroke(this.viewport,
        // symbolizer);
        // GLComplex outline =
        // GLComplexFactory.createOutlineMultiSurface(this.polygons, awtStroke,
        // minX, minY);
        GLSimpleComplex line = LineTesselator.createThickLine(this.getName()
                + "-full", this.curves, symbolizer.getStroke(), minX, minY);
        line.setColor(symbolizer.getStroke().getColor());
        line.setOverallOpacity(symbolizer.getStroke().getColor().getAlpha() / 255.);
        line.setRenderer(GeoxRendererManager.getOrCreateLineRenderer(
                symbolizer, this.getLayerRenderer()));

        complexes.add(line);
        return complexes;
    }

    private List<GLComplex> generateWithExpressiveStroke(
            LineSymbolizer symbolizer) {
        List<GLComplex> complexes = new ArrayList<GLComplex>();
        ExpressiveRenderingDescriptor style = symbolizer.getStroke()
                .getExpressiveRendering();
        if (style == null) {
            throw new IllegalStateException(
                    "this method can only be called with a valid expressive stroke");
        }

        if ((style instanceof StrokeTextureExpressiveRenderingDescriptor)) {
            StrokeTextureExpressiveRenderingDescriptor strtex = (StrokeTextureExpressiveRenderingDescriptor) style;
            // return GLComplexFactory.createFilledPolygon(multiSurface,
            // symbolizer.getStroke().getColor());
            IEnvelope envelope = IGeometryUtil.getEnvelope(this.curves);
            double minX = envelope.minX();
            double minY = envelope.minY();

            GeoxComplexRenderer renderer = GeoxRendererManager
                    .getOrCreateLineRenderer(symbolizer,
                            this.getLayerRenderer());

            GLPaintingComplex complex = GLComplexFactory
                    .createPaintingThickCurves(this.getName()
                            + "-expressive-painting", symbolizer.getStroke(),
                            minX, minY, strtex, this.curves, renderer);
            complex.setExpressiveRendering(new StrokeTextureExpressiveRendering(
                    strtex));
            complexes.add(complex);
            return complexes;
        } else if ((style instanceof BasicTextureExpressiveRenderingDescriptor)) {
            BasicTextureExpressiveRenderingDescriptor strtex = (BasicTextureExpressiveRenderingDescriptor) style;
            // return GLComplexFactory.createFilledPolygon(multiSurface,
            // symbolizer.getStroke().getColor());
            IEnvelope envelope = IGeometryUtil.getEnvelope(this.curves);
            double minX = envelope.minX();
            double minY = envelope.minY();

            GeoxComplexRenderer renderer = GeoxRendererManager
                    .getOrCreateLineRenderer(symbolizer,
                            this.getLayerRenderer());
            GLBezierShadingComplex complex = GLComplexFactory
                    .createBezierThickCurves(this.getName()
                            + "-expressive-bezier", symbolizer.getStroke(),
                            minX, minY, strtex, this.curves, renderer);
            complex.setExpressiveRendering(new BasicTextureExpressiveRendering(
                    strtex));
            complexes.add(complex);
            this.setFullRepresentation(complexes);
            return complexes;
        }
        throw new IllegalStateException("LineSymbolizer cannot handle "
                + style.getClass().getSimpleName());
    }
    // /**
    // * @param widthFunction
    // * @param shiftFunctioncurrentTask
    // * @return
    // */
    // public GLPaintingComplex createComplex(String id, List<ILineString>
    // lines,
    // double minX, double minY, Function1D widthFunction,
    // Function1D shiftFunction, double sampleSize, double minAngle) {
    // GLPaintingComplex complex = new GLPaintingComplex(id, minX, minY);
    // synchronized (this.currentTaskLock) {
    // this.taskCount = lines.size();
    // }
    // for (ILineString line : lines) {
    // try {
    // Task tesselateThickLineTask = LinePaintingTesselator
    // .tesselateThickLine(id, complex,
    // line.getControlPoint(), widthFunction,
    // shiftFunction, sampleSize, minAngle, minX,
    // minY, new SolidColorizer(Color.black));
    // synchronized (this.currentTaskLock) {
    // this.currentTask = tesselateThickLineTask;
    // }
    // } catch (FunctionEvaluationException e) {
    // e.printStackTrace();
    // }
    // }
    //
    // return complex;
    // }

}

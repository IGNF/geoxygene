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

package fr.ign.cogit.geoxygene.appli.render;

import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.GLContext;
import fr.ign.cogit.geoxygene.appli.gl.setters.GLProgramUniformSetter;
import fr.ign.cogit.geoxygene.appli.render.groups.RenderingGroup;
import fr.ign.cogit.geoxygene.appli.render.groups.RenderingGroupFactory;
import fr.ign.cogit.geoxygene.appli.render.methods.NamedRenderingParametersMap;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodParameterDescriptor;
import fr.ign.cogit.geoxygene.appli.render.primitive.GLDisplayable;
import fr.ign.cogit.geoxygene.appli.render.stats.RenderingStatistics;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLMesh;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTools;
import fr.ign.cogit.geoxygene.util.gl.RenderingException;

/**
 * @author Bertrand Duménieu. Render a Displayable
 */
public abstract class DisplayableRenderer<T extends GLDisplayable> implements GeoxygeneGLRenderer {

    protected boolean wireframe_rendering = false;
    protected Viewport viewport = null;

    private Map<Object, RenderingGroup> groups; // One group pet object

    // Global renderingParameters
    private static final RenderingMethodParameterDescriptor global_opacity = new RenderingMethodParameterDescriptor("globalOpacity");
    private static final RenderingMethodParameterDescriptor fboWidth = new RenderingMethodParameterDescriptor(GeoxygeneConstants.GL_VarName_FboWidth);
    private static final RenderingMethodParameterDescriptor fboHeight = new RenderingMethodParameterDescriptor(GeoxygeneConstants.GL_VarName_FboHeight);
    private static final RenderingMethodParameterDescriptor screenWidth = new RenderingMethodParameterDescriptor(GeoxygeneConstants.GL_VarName_ScreenWidth);
    private static final RenderingMethodParameterDescriptor screenHeight = new RenderingMethodParameterDescriptor(GeoxygeneConstants.GL_VarName_ScreenHeight);
    private static final RenderingMethodParameterDescriptor time = new RenderingMethodParameterDescriptor("time");

    public DisplayableRenderer(Viewport _viewport) {
        this.viewport = _viewport;
        this.groups = new WeakHashMap<>();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.GeoxComplexRenderer#activateRenderer
     * ()
     */
    @Override
    public void activateRenderer() throws RenderingException {
        RenderingStatistics.doActivateRenderer(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.GeoxComplexRenderer#swicthRenderer()
     */
    @Override
    public void switchRenderer() throws RenderingException {
        RenderingStatistics.doSwitchRenderer(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * fr.ign.cogit.geoxygene.appli.render.GeoxComplexRenderer#activateRenderer
     * ()
     */
    @Override
    public void initializeRendering() throws RenderingException {
        RenderingStatistics.doInitializeRenderer(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @seesymbolizer
     * fr.ign.cogit.geoxygene.appli.render.GeoxComplexRenderer#swicthRenderer()
     */
    @Override
    public void finalizeRendering() throws RenderingException {
        RenderingStatistics.doFinalizeRenderer(this);
    }

    public abstract boolean render(T displayable_to_draw, double global_opacity);

    /**
     * Render a GlDisplayable with a set of RenderingGroups.
     */
    protected boolean render(T displayable_to_draw, double opacity, Object[] displayable_style_elements_to_draw) {
        boolean global_success = true;
        for (Object element : displayable_style_elements_to_draw) {
            RenderingGroup g = this.groups.get(element);
            if (g == null) {
                // Create a new Rendering Group
                g = RenderingGroupFactory.createRenderingGroup(element);
                if (g != null) {
                    this.groups.put(element, g);
                }
            }
            if (g != null) {
                Collection<GLComplex> c = this.getComplexesForGroup(g, displayable_to_draw);
                if (c != null && !c.isEmpty()) {
                    Map<RenderingMethodParameterDescriptor, Object> params = g.getRenderingParameters(displayable_to_draw);
                    params.put(global_opacity, opacity);
                    try {
                        global_success &= this.render(c, g.getRenderingParameters(displayable_to_draw), g.getMethod().getGLProgram());
                    } catch (GLException | NoninvertibleTransformException e) {
                        Logger.getRootLogger().error("Failed to render the group " + g.getName() + " for the GLDisplayable " + displayable_to_draw);
                        e.printStackTrace();
                        global_success = false;
                    }
                } else {
                    // The complexes may not be ready
                    global_success = false;
                    Logger.getRootLogger().debug("Render but no GL primitive is available for drawing!");
                }
            } else {
                Logger.getRootLogger().warn("No RenderingGroup found for the style element " + element + " applied to the Displayable " + displayable_to_draw);
                global_success = false;
            }
            if (!global_success) {
                this.render(displayable_to_draw.getPartialRepresentation());
            }
        }
        return global_success;
    }

    abstract protected Collection<GLComplex> getComplexesForGroup(RenderingGroup g, T displayable_to_draw);

    /**
     * Degraded Rendering
     * 
     * @param collection
     * @return
     */
    boolean render(GLComplex complex) {
        RenderingMethodDescriptor degraded = RenderingMethodDescriptor.retrieveMethod("Degraded");
        if (degraded == null)
            return false;
        GLProgram degraded_program = degraded.getGLProgram();
        if (degraded_program == null)
            return false;
        Collection<GLComplex> comp = new ArrayList<GLComplex>(1);
        comp.add(complex);
        try {
            return this.render(comp, new NamedRenderingParametersMap(), degraded_program);
        } catch (GLException | NoninvertibleTransformException e) {
            e.printStackTrace();
        }
        return false;

    }

    private boolean render(Collection<GLComplex> cComplexes, NamedRenderingParametersMap cParameters, GLProgram glpRenderProgram) throws GLException, NoninvertibleTransformException {
        boolean success = false;
        glEnable(GL11.GL_BLEND);
        glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        // Set the general shared uniforms.
        cParameters.put(fboWidth, GLContext.getActiveGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_FboWidth));
        cParameters.put(fboHeight, GLContext.getActiveGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_FboHeight));
        cParameters.put(screenWidth, GLContext.getActiveGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_ScreenWidth));
        cParameters.put(screenHeight, GLContext.getActiveGlContext().getSharedUniform(GeoxygeneConstants.GL_VarName_ScreenHeight));
        cParameters.put(time, GLContext.getActiveGlContext().getSharedUniform("time"));
        // Activate the Program
        GLContext active_context = GLContext.getActiveGlContext();
        GLProgram program = null;
        if (this.wireframe_rendering) {
            glDisable(GL_TEXTURE_2D);
            GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE);
            program = RenderingMethodDescriptor.retrieveMethod("Degraded").getGLProgram();
        } else {
            program = glpRenderProgram;
        }
        
        // Get the UniformSetter for this program.
        active_context.setCurrentProgram(program);
        GLProgramUniformSetter setter = active_context.getSetter(program);
        success = setter.set(cParameters, program);    
        if (success) {
            AffineTransform atWorld2Screen = this.viewport.getModelToViewTransform();
            RenderingStatistics.doStartRendering(this);
            for (GLComplex complex : cComplexes) {
                success &= DisplayableRenderer.rendercomplex(complex, program, atWorld2Screen);
            }
        }
        glDisable(GL11.GL_BLEND);
        GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
        active_context.setCurrentProgram(null);
        return success;
    }

    private static boolean rendercomplex(GLComplex c, GLProgram program, AffineTransform modelToViewTransform) throws GLException {
        boolean success = true;
        double m00 = modelToViewTransform.getScaleX();
        double m02 = modelToViewTransform.getTranslateX() + c.getMinX() * modelToViewTransform.getScaleX();
        double m11 = modelToViewTransform.getScaleY();
        double m12 = modelToViewTransform.getTranslateY() + c.getMinY() * modelToViewTransform.getScaleY();
//        success &= GLTools.glCheckError("GL4FeatureRenderer::setGLViewMatrix()");
        program.setUniform(GeoxygeneConstants.GL_VarName_M00ModelToViewMatrix, m00);
        program.setUniform(GeoxygeneConstants.GL_VarName_M02ModelToViewMatrix, m02);
        program.setUniform(GeoxygeneConstants.GL_VarName_M11ModelToViewMatrix, m11);
        program.setUniform(GeoxygeneConstants.GL_VarName_M12ModelToViewMatrix, m12);
        GL30.glBindVertexArray(c.getVaoId());
//        success &= GLTools.glCheckError("direct rendering binding vaoId = " + c.getVaoId());
        DisplayableRenderer.drawComplex(c);
//        success &= GLTools.glCheckError("direct rendering drawing GLSimpleComplex class = " + c.getClass().getSimpleName());
        GL30.glBindVertexArray(0);
        success &= GLTools.glCheckError("exiting direct rendering");
        return success;
    }

    /**
     * do a GL draw call for all complex meshes
     * 
     * @param primitive
     *            primitive to render
     */
    protected final static void drawComplex(GLComplex primitive) {
        RenderingStatistics.drawGLComplex(primitive);
        for (GLMesh mesh : primitive.getMeshes()) {
            RenderingStatistics.doDrawCall();
            GL11.glDrawElements(mesh.getGlType(), mesh.getLastIndex() - mesh.getFirstIndex() + 1, GL11.GL_UNSIGNED_INT, mesh.getFirstIndex() * (Integer.SIZE / 8));
        }
    }

    @Override
    public void reset() {
        this.groups.clear();
    }

    public void setWireframeRendering(boolean b) {
        this.wireframe_rendering = b;
    }

}
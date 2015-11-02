package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;
import fr.ign.cogit.geoxygene.appli.render.methods.NamedRenderingParametersMap;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureTask;
import fr.ign.cogit.geoxygene.appli.task.AbstractTask;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.texture.SimpleTexture;
import fr.ign.cogit.geoxygene.style.texture.Texture;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;

public abstract class AbstractDisplayable extends AbstractTask implements GLDisplayable {

    private static final Logger logger = Logger.getLogger(AbstractDisplayable.class.getName()); // logger

    private List<GLComplex> fullRepresentation;
    private GLComplex partialRepresentation;
    private long displayCount;
    private Date lastDisplayTime;
    private IFeature feature = null;
    // XXX Ugly : the viewport should not be stored in the displayable...
    protected Viewport viewport;
    private Symbolizer symbolizer;

    protected URI tex_root_uri;

    public AbstractDisplayable(String name, IFeature feature, Symbolizer symbolizer, Viewport p, URI tex_root_uri) {
        super(name);
        this.setFeature(feature);
        this.viewport = p;
        this.symbolizer = symbolizer;
    }

    /**
     * @return the feature
     */
    public IFeature getFeature() {
        return this.feature;
    }

    /**
     * @param feature
     *            the feature to set
     */
    private void setFeature(IFeature feature) {
        this.feature = feature;
    }

    @Override
    public final List<GLComplex> getFullRepresentation() {
        return this.fullRepresentation;
    }

    public void setFullRepresentation(List<GLComplex> fullRepresentation) {
        this.fullRepresentation = fullRepresentation;
    }

    @Override
    public final GLComplex getPartialRepresentation() {
        return this.partialRepresentation;
    }

    public final void setPartialRepresentation(GLComplex partialRepresentation) {
        this.partialRepresentation = partialRepresentation;
    }

    @Override
    public final long getDisplayCount() {
        return this.displayCount;
    }

    public final void setDisplayCount(long displayCount) {
        this.displayCount = displayCount;
    }

    @Override
    public final Date getLastDisplayTime() {
        return this.lastDisplayTime;
    }

    public final void setLastDisplayTime(Date lastDisplayTime) {
        this.lastDisplayTime = lastDisplayTime;
    }

    public final Symbolizer getSymbolizer() {
        return this.symbolizer;
    }

    /**
     * Asynchronous method launching the GLComplex full representation It checks
     * if the result is not empty
     */
    @Override
    public final void run() {
        super.setState(TaskState.INITIALIZING);
        this.setFullRepresentation(null);
        this.setState(TaskState.RUNNING);
        List<GLComplex> primitives = this.generateFullRepresentation();
        this.setState(TaskState.FINALIZING);
        if (primitives == null || primitives.size() == 0) {
            if (this.getError() == null) {
                logger.error(this.getClass().getSimpleName() + " returns an invalid full representation: " + primitives + " without explanation");
                this.setError(new IllegalStateException(this.getClass().getSimpleName() + " returns an invalid full representation: " + primitives + " without explanation"));
            }
            this.setState(TaskState.ERROR);
            return;
        }
        this.setState(TaskState.FINALIZING);
        this.setFullRepresentation(primitives);
        this.setState(TaskState.FINISHED);

    }

    /**
     * This asynchronous method generates a collection of GLComplex repesenting
     * the input geometry
     */
    public abstract List<GLComplex> generateFullRepresentation();

    /**
     * FIXME this method should not exist since Geometry generation and style
     * should be separated.
     * 
     * @param methodname
     * @return
     */
    protected RenderingMethodDescriptor getRenderingMethod(String methodname) {
        if (methodname == null || methodname.isEmpty()) {
            return null;
        }
        RenderingMethodDescriptor method = (RenderingMethodDescriptor) ResourcesManager.Root().getSubManager(GeoxygeneConstants.GEOX_Const_RenderingMethodsManagerName).getResourceByName(methodname);
        if (method == null) {
            logger.error("Abstract Displayable : method " + methodname + " not found.");
        }
        return method;
    }

    @Override
    public void dispose() {
        this.feature = null;
        this.fullRepresentation.clear();
        this.fullRepresentation = null;
        this.partialRepresentation = null;
        this.viewport = null;
    }

    /**
     * Create a texture for this Displayable with the given texture descriptor.
     * If the texture doesn't exist already in the {@link TextureManagern}, run
     * a new {@link TextureTask} to build the texture.
     * 
     * @param tex_desc
     *            : the texture descriptor
     * @param dosynchronously
     *            : Do we wait for the texture task to end?
     * @return the URI of the created texture.
     */
    public URI createTexture(Texture tex_desc, boolean dosynchronously) {
        // FIXME The texture is common to all the Displayable in the same
        // feature collection. Is this REALLY a good behavior?
        try {

            URI tex_uri = (tex_desc instanceof SimpleTexture) ? TextureManager.createTexID(tex_desc, null) : TextureManager.createTexID(tex_desc, this.feature.getFeatureCollection(0));
            if (TextureManager.retrieveTexture(tex_uri) == null) {
                synchronized (TextureManager.getInstance()) {
                    // Gotta build it!
                    if (dosynchronously) {
                        TextureManager.getTexture(tex_desc, this.feature.getFeatureCollection(0), this.viewport);
                    } else {
                        TextureTask<? extends GLTexture> task = TextureManager.buildTexture(tex_desc, this.feature.getFeatureCollection(0), this.viewport);
                        task.start();
                    }
                }
            }
            return tex_uri;
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Viewport getViewport() {
        return this.viewport;
    }

    
    /**
     * Allow to customize the rendering parameters before calling the DisplayableRenderer.
     * @param p
     */
    public abstract void setCustomRenderingParameters(NamedRenderingParametersMap p);

}
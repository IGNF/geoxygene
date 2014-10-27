package fr.ign.cogit.geoxygene.appli.render.primitive;

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.render.LwjglLayerRenderer;
import fr.ign.cogit.geoxygene.appli.task.AbstractTask;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.util.gl.GLComplex;

public abstract class AbstractDisplayable extends AbstractTask implements
        GLDisplayable {

    private static final Logger logger = Logger
            .getLogger(AbstractDisplayable.class.getName()); // logger

    private Viewport viewport;
    private LwjglLayerRenderer layerRenderer;
    private Symbolizer symbolizer;
    private List<GLComplex> fullRepresentation;
    private GLComplex partialRepresentation;
    private long displayCount;
    private Date lastDisplayTime;

    public AbstractDisplayable(String name, Viewport viewport,
            LwjglLayerRenderer layerRenderer, Symbolizer symbolizer) {
        super(name);
        this.setViewport(viewport);
        this.setLayerRenderer(layerRenderer);
        this.setSymbolizer(symbolizer);
    }

    public final Viewport getViewport() {
        return this.viewport;
    }

    public final void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    public final LwjglLayerRenderer getLayerRenderer() {
        return this.layerRenderer;
    }

    public final void setLayerRenderer(LwjglLayerRenderer layerRenderer) {
        this.layerRenderer = layerRenderer;
    }

    public final Symbolizer getSymbolizer() {
        return this.symbolizer;
    }

    public final void setSymbolizer(Symbolizer symbolizer) {
        this.symbolizer = symbolizer;
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
                logger.error(this.getClass().getSimpleName()
                        + " returns an invalid full representation: "
                        + primitives + " without explanation");
                this.setError(new IllegalStateException(this.getClass()
                        .getSimpleName()
                        + " returns an invalid full representation: "
                        + primitives + " without explanation"));
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
}
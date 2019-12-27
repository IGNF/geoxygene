package fr.ign.cogit.geoxygene.appli.render.texture;

import java.net.URI;

import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;

public interface TextureTask<TextureType extends GLTexture> extends Task {

    /**
     * @return the texture
     */
    public abstract TextureType getTexture();

    /**
     * @return task unique ID
     */
    public abstract URI getID();

    public abstract void setID(URI identifier);

    /**
     * @return the texture size once it will be generated
     */
    public abstract int getTextureWidth();

    /**
     * @return the texture size once it will be generated
     */
    public abstract int getTextureHeight();

    /**
     * @return true if the result of the task has to be stored in cache
     */
    public abstract boolean needCaching();


    
}
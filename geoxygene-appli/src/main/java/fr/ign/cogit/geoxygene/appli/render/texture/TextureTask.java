package fr.ign.cogit.geoxygene.appli.render.texture;

import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.util.gl.Texture;

public interface TextureTask<TextureType extends Texture> extends Task {

    /**
     * @return the texture
     */
    public abstract TextureType getTexture();

    /**
     * @return task unique ID
     */
    public String getID();

    public abstract void setID(String id);

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
    public abstract boolean needWriting();
}
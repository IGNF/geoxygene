package fr.ign.cogit.geoxygene.appli.render.texture;

import fr.ign.cogit.geoxygene.appli.task.Task;
import fr.ign.cogit.geoxygene.style.texture.Texture;

public interface TextureTask<TextureType extends Texture> extends Task {

    /**
     * @return the texture
     */
    public abstract TextureType getTexture();

    /**
     * @param texture
     *            the texture to set
     */
    public abstract void setTexture(TextureType texture);

    /**
     * @return the texture size once it will be generated
     */
    public abstract int getTextureWidth();

    /**
     * @return the texture size once it will be generated
     */
    public abstract int getTextureHeight();

}
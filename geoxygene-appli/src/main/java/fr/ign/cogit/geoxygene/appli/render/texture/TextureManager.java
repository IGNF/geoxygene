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

package fr.ign.cogit.geoxygene.appli.render.texture;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.texture.Texture;

/**
 * @author JeT
 *         texture manager
 */
public class TextureManager implements TaskListener<TextureTask<Texture>> {

    private static final Logger logger = Logger.getLogger(TextureManager.class.getName()); // logger

    private static final Map<Texture, TextureTask<? extends Texture>> tasksMap = new HashMap<Texture, TextureTask<? extends Texture>>();
    private final static TextureManager instance = new TextureManager();

    /**
     * private singleton constructor
     */
    private TextureManager() {
        // singleton
    }

    /**
     * @return the instance
     */
    public static TextureManager getInstance() {
        return instance;
    }

    /**
     * return the texture image if it has finished being computed or launch the
     * texture image computation. the task is automatically started
     * To be alerted about texture computation completion use getTextureTask()
     * 
     * @param texture
     * @param iFeatureCollection
     * @param viewport
     * @return
     */
    public BufferedImage getTextureImage(Texture texture, IFeatureCollection<IFeature> iFeatureCollection, Viewport viewport) {
        BufferedImage textureImage = texture.getTextureImage();
        if (textureImage != null) {
            return textureImage;
        }
        TextureTask<? extends Texture> textureTask = this.getTextureTask(texture, iFeatureCollection, viewport);
        textureTask.start();
        return textureTask.getTexture().getTextureImage();
    }

    /**
     * Create or return a texture task. The task is NOT automatically started
     * when completed Task.getTextureImage() won't be null
     * 
     * @param texture
     * @param iFeatureCollection
     * @param viewport
     * @return
     */
    public TextureTask<? extends Texture> getTextureTask(Texture texture, IFeatureCollection<IFeature> iFeatureCollection, Viewport viewport) {
        if (texture == null) {
            return null;
        }
        // create a task to generate texture image
        synchronized (tasksMap) {
            TextureTask<? extends Texture> textureTask = tasksMap.get(texture);
            if (textureTask == null) {
                textureTask = TextureTaskFactory.createTextureTask(texture, iFeatureCollection, viewport);
                if (textureTask == null) {
                    logger.error("Unable to create texture task for texture " + texture.getClass().getSimpleName());
                    return null;
                }
                tasksMap.put(texture, textureTask);
                GeOxygeneEventManager.getInstance().getApplication().getTaskManager().addTask(textureTask);
                textureTask.addTaskListener(this);
            }
            return textureTask;
        }
    }

    @Override
    public void onStateChange(TextureTask<Texture> task, TaskState oldState) {
        synchronized (tasksMap) {
            switch (task.getState()) {
            case FINISHED:
                //                tasksMap.remove(task.getTexture());
                task.removeTaskListener(this);
                if (task.getTexture().getTextureImage() == null) {
                    logger.error("TextureTask has finished with no error but a null texture (its role IS to fill texture.getTextureImage() method)");
                }
                GeOxygeneEventManager.refreshApplicationGui();
                break;
            case ERROR:
            case STOPPED:
                //                tasksMap.remove(task.getTexture());
                task.removeTaskListener(this);
                break;
            default:
                // do nothing special;
            }
        }
    }

    /**
     * Invalidate all textures. They will be regenerated next display call
     */
    public void invalidateTextures(Layer layer) {
        for (Style style : layer.getStyles()) {
            Symbolizer symbolizer = style.getSymbolizer();
            symbolizer.reset();
        }
    }
}

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
import fr.ign.cogit.geoxygene.style.texture.BasicTextureDescriptor;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;

/**
 * @author JeT texture manager
 */
public class TextureManager implements TaskListener<TextureTask<BasicTexture>> {

    private static final Logger logger = Logger.getLogger(TextureManager.class
            .getName()); // logger

    private static final Map<BasicTextureDescriptor, TextureTask<BasicTexture>> tasksMap = new HashMap<BasicTextureDescriptor, TextureTask<BasicTexture>>();
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
     * texture image computation. the task is automatically started To be
     * alerted about texture computation completion use getTextureTask()
     * 
     * @param texture
     * @param iFeatureCollection
     * @param viewport
     * @return
     */
    public BufferedImage getTextureImage(String name,
            BasicTextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> iFeatureCollection, Viewport viewport) {
        TextureTask<BasicTexture> textureTask = this.getTextureTask(name,
                textureDescriptor, iFeatureCollection, viewport);
        BasicTexture texture = textureTask.getTexture();
        if (texture != null) {
            BufferedImage textureImage = texture.getTextureImage();
            if (textureImage != null) {
                return textureImage;
            }
        }
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
    public TextureTask<BasicTexture> getTextureTask(String name,
            BasicTextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> iFeatureCollection, Viewport viewport) {
        if (textureDescriptor == null) {
            return null;
        }
        TextureTask<BasicTexture> textureTask = null;
        // create a task to generate texture image
        synchronized (tasksMap) {
            textureTask = tasksMap.get(textureDescriptor);
            if (textureTask != null) {
                return textureTask;
            }
            textureTask = TextureTaskFactory.createTextureTask(name,
                    textureDescriptor, iFeatureCollection, viewport);
            if (textureTask == null) {
                logger.error("Unable to create texture task for texture "
                        + textureDescriptor.getClass().getSimpleName());
                return null;
            }
            tasksMap.put(textureDescriptor, textureTask);
        }
        System.err.println("add task in cache. Cache size is now "
                + tasksMap.size());

        // do not add texture task to the task manager, they may not be
        // launched and geometry is waiting for them (to be verified)
        // GeOxygeneEventManager.getInstance().getApplication()
        // .getTaskManager().addTask(textureTask);
        textureTask.addTaskListener(this);
        textureTask.start();
        return textureTask;
    }

    /**
     * remove the texture from the cache. If it exists, task generation is
     * requested to stop and is removed from listened tasks
     * 
     * @param texture
     *            texture to remove from cache
     */
    public boolean uncacheTexture(BasicTextureDescriptor textureDescriptor) {
        TextureTask<BasicTexture> textureTask = null;
        synchronized (tasksMap) {
            textureTask = tasksMap.get(textureDescriptor);
            if (textureTask == null) {
                return false;
            }
            tasksMap.remove(textureDescriptor);
        }
        textureTask.requestStop();
        textureTask.removeTaskListener(this);
        return true;
    }

    @Override
    public void onStateChange(TextureTask<BasicTexture> task, TaskState oldState) {
        switch (task.getState()) {
        case FINISHED:
            synchronized (tasksMap) {
                tasksMap.remove(task.getTexture());
            }
            task.removeTaskListener(this);
            if (task.getTexture().getTextureImage() == null) {
                logger.error("TextureTask has finished with no error but a null texture (its role IS to fill texture.getTextureImage() method)");
            }
            GeOxygeneEventManager.refreshApplicationGui();
            break;
        case ERROR:
        case STOPPED:
            synchronized (tasksMap) {
                tasksMap.remove(task.getTexture());
            }
            task.removeTaskListener(this);
            break;
        default:
            // do nothing special;
        }
    }

    /**
     * Invalidate all textures. They will be regenerated next display call
     */
    public void invalidateTextures(Layer layer) {
        for (Style style : layer.getStyles()) {
            Symbolizer symbolizer = style.getSymbolizer();
            if (symbolizer != null) {
                symbolizer.reset();
            }
        }
    }
}

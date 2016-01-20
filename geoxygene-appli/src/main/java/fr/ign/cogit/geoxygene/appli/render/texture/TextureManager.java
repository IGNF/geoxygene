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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.GeoxygeneConstants;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.ResourcesManager;
import fr.ign.cogit.geoxygene.appli.resources.ResourceLocationResolver;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskManager;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.texture.ProbabilistTileDescriptor;
import fr.ign.cogit.geoxygene.style.texture.SimpleTexture;
import fr.ign.cogit.geoxygene.style.texture.BinaryGradientImageDescriptor;
import fr.ign.cogit.geoxygene.style.texture.Texture;
import fr.ign.cogit.geoxygene.style.texture.TileDistributionTexture;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;

/**
 * @author JeT texture manager
 */
public class TextureManager {

    private static final Logger logger = Logger.getLogger(TextureManager.class.getName()); // logger
    private static final Map<URI, TextureTask<? extends GLTexture>> tasksMap = new HashMap<URI, TextureTask<? extends GLTexture>>();
    private static final Map<URI, GLTexture> textureMap = new HashMap<URI, GLTexture>();

    private final static TextureManager instance = new TextureManager();

    public static String DIRECTORY_CACHE_NAME = "cache";

    private BasicTextureTaskListener basicListener = null;

    /**
     * private singleton constructor
     */
    private TextureManager() {
        this.basicListener = new BasicTextureTaskListener(this);
    }

    /**
     * @return the instance
     */
    public static TextureManager getInstance() {
        return instance;
    }

    public static TextureTask<? extends GLTexture> getTextureTask(URI texture_uri) {
        return TextureManager.tasksMap.get(texture_uri);
    }

    public static GLTexture retrieveTexture(Texture desc, int feature_collection_hashcode) {
        try {
            if (desc instanceof SimpleTexture) {
                return TextureManager.textureMap.get(createTexID(desc));
            }
            return TextureManager.textureMap.get(createTexID(desc, feature_collection_hashcode));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieve a texture with its ID. If the texture is not built, return null.
     */
    public static GLTexture retrieveTexture(URI tex_id) {
        return TextureManager.textureMap.get(tex_id);
    }

    public static GLTexture getTexture(URI path) {
        synchronized (textureMap) {
            GLTexture t = textureMap.get(path);
            try {
                if (t == null) {
                    TextureTask<? extends GLTexture> task;
                    task = TextureManager.buildTexture(path.toURL());
                    task.start();
                    TaskManager.waitForCompletion(task);
                    t = textureMap.get(path);
                }
            } catch (MalformedURLException | URISyntaxException e1) {
                e1.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return t;
        }
    }

    /**
     * Retrieve or create a simple texture. <br/>
     * This method is <b> synchrone</b> and wait for the texture task to finish. <br/>
     * To make asynchrone calls, use the methods {@link #buildTexture(...)}
     * 
     * @param tex_descriptor
     *            : the simple texture descriptor.
     * @return a Basic
     */
    public static GLTexture getTexture(SimpleTexture tex_descriptor) {
        return TextureManager.getTexture(tex_descriptor, null, null);
    }

    public static GLTexture getTexture(Texture tex_descriptor, IFeatureCollection<IFeature> textured_objects, Viewport p) {
        if (tex_descriptor != null) {
            try {
                URI tex_uri = (tex_descriptor instanceof SimpleTexture) ? TextureManager.createTexID(tex_descriptor) : TextureManager.createTexID(tex_descriptor, textured_objects);
                if (TextureManager.textureMap.get(tex_uri) == null) {
                    TextureTask<? extends GLTexture> task = TextureManager.tasksMap.get(tex_uri);
                    if (task != null) {
                        TaskManager.waitForCompletion(task);
                        return task.getTexture();
                    }
                } else {
                    return TextureManager.textureMap.get(tex_uri);
                }
                TextureTask<? extends GLTexture> task;
                if (tex_descriptor instanceof SimpleTexture) {
                    task = TextureManager.buildTexture((SimpleTexture) tex_descriptor);
                } else {
                    task = TextureManager.buildTexture(tex_descriptor, textured_objects, p);
                }
                task.start();
                TaskManager.waitForCompletion(task);
                return task.getTexture();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Basic method that build a texture from a location
     * 
     * @param path
     *            : the URl of the texture.
     * @return
     * @throws URISyntaxException
     */
    public static TextureTask<? extends GLTexture> buildTexture(URL path) throws URISyntaxException {

        if (tasksMap.get(path) == null) {
            TextureTask<BasicTexture> tt = TextureTaskFactory.createTextureTask(path.toURI(), path);
            tt.addTaskListener(TextureManager.getInstance().basicListener);
            tasksMap.put(path.toURI(), tt);
            return tt;
        }
        return tasksMap.get(path);
    }

    /**
     * @param tex_descriptor
     * @return
     */
    public static TextureTask<? extends GLTexture> buildTexture(SimpleTexture tex_descriptor) {
        return TextureManager.buildTexture(tex_descriptor, null, null);
    }

    /**
     * Build texture tasks. The tasks are not started automatically.
     * 
     * @param tex_uri
     * @param tex_descriptor
     * @param tex_url
     * @return
     */
    public static TextureTask<? extends GLTexture> buildTexture(Texture tex_descriptor, IFeatureCollection<IFeature> textured_objects, Viewport p) {
        try {
            URI tex_uri = (tex_descriptor instanceof SimpleTexture) ? TextureManager.createTexID(tex_descriptor) : TextureManager.createTexID(tex_descriptor, textured_objects);
            // Check if a task already exists
            synchronized (tasksMap) {
                TextureTask<? extends GLTexture> tt = TextureManager.tasksMap.get(tex_uri);
                if (tt == null) {
                    // Check if the texture is cached on the disk
                    String ext = (tex_descriptor instanceof BinaryGradientImageDescriptor) ? "bgi" : "png";
                    File file = TextureManager.getCachedTextureFile(tex_uri, ext);
                    if (file.isFile() && file.exists()) {
                        tt = TextureTaskFactory.createTextureTask(tex_uri, file.getAbsoluteFile().toURI().toURL());
                        tt.addTaskListener(TextureManager.getInstance().basicListener);
                        tasksMap.put(tex_uri, tt);
                    } else {
                        // Create a new texture task and resolve the texture
                        // resource.
                        URI root_uri = (URI) ResourcesManager.Root().getResourceByName(GeoxygeneConstants.GEOX_Const_CurrentStyleRootURIName);
                        if (tex_descriptor instanceof SimpleTexture) {
                            SimpleTexture st = (SimpleTexture) tex_descriptor;
                            URL resolved_location = ResourceLocationResolver.resolve(st.getTextureURI(), root_uri);
                            if (resolved_location == null) {
                                logger.error("Failed to resolve the location of the texture " + st.getTextureURI() + ". The texture will not be loaded.");
                                return null;
                            }
                            st.setLocation(resolved_location);
                        }
                        if (tex_descriptor instanceof TileDistributionTexture) {
                            TileDistributionTexture tdt = (TileDistributionTexture) tex_descriptor;
                            for (ProbabilistTileDescriptor tile : tdt.getTiles()) {
                                URL resolved_location = ResourceLocationResolver.resolve(tile.getTextureURI(), root_uri);
                                if (resolved_location == null) {
                                    logger.error("Failed to resolve the location of the Tile texture " + tile.getTextureURI() + ". This Tile will not be loaded.");
                                    return null;
                                }
                                tile.setLocation(resolved_location);
                            }
                        }
                        tt = TextureTaskFactory.createTextureTask(tex_uri, tex_descriptor, textured_objects, p);
                        tt.addTaskListener(TextureManager.getInstance().basicListener);
                        tasksMap.put(tex_uri, tt);
                    }

                } else {
                    System.out.println("Texture task already exists and its state is " + tt.getState());
                }
                return tt;
            }
        } catch (URISyntaxException e) {
            logger.error("Failed to generate an URI for the texture " + tex_descriptor);
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * remove the texture from the cache. If it exists, task generation is
     * requested to stop and is removed from listened tasks
     * 
     * @param texture
     *            texture to remove from cache
     */
    public boolean uncacheTexture(SimpleTexture textureDescriptor) {
        TextureTask<? extends GLTexture> textureTask = null;
        synchronized (tasksMap) {
            textureTask = tasksMap.get(textureDescriptor);
            if (textureTask == null) {
                return false;
            }
            tasksMap.remove(textureDescriptor);
        }
        textureTask.requestStop();
        textureTask.removeTaskListener(this.basicListener);
        return true;
    }

    private class BasicTextureTaskListener implements TaskListener<TextureTask<BasicTexture>> {
        private TextureManager manager = null;

        /**
         * @param manager
         */
        public BasicTextureTaskListener(TextureManager manager) {
            super();
            this.manager = manager;
        }

        @Override
        public void onStateChange(TextureTask<BasicTexture> task, TaskState oldState) {
            switch (task.getState()) {
            case FINISHED:
                synchronized (tasksMap) {
                    tasksMap.remove(task.getID());
                }
                task.removeTaskListener(this);
                if (task.getTexture().getTextureImage() == null) {
                    logger.error("TextureTask has finished with no error but the resulting texture has no image data.");
                }
                // Save the texture in the map
                synchronized (textureMap) {
                    textureMap.put(task.getID(), task.getTexture());
                }
                // save texture on disk
                if (task.needCaching()) {
                    this.manager.saveTexture(task);
                }
                GeOxygeneEventManager.refreshApplicationGui();
                break;
            case ERROR:
            case STOPPED:
                synchronized (tasksMap) {
                    tasksMap.remove(task);
                }
                task.removeTaskListener(this);
                break;
            default:
                // do nothing special;
            }
        }
    }

    /**
     * Save texture on disk in cache directory asynchronously
     * 
     * @param texture
     *            texture to save
     */
    private void saveTexture(final TextureTask<BasicTexture> task) {

        if (task == null || task.getID() == null) {
            throw new IllegalArgumentException("Cannot save " + task + " task");
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                BasicTexture texture = task.getTexture();
                if (texture.getTextureImage() == null) {
                    logger.error("Asked to save texture that has no generated image");
                    return;
                }
                logger.debug("save image task id " + task.getID() + " task name = " + task.getName());
                File file = new File(DIRECTORY_CACHE_NAME + File.separator + task.getID() + ".png");
                File directory = file.getParentFile();
                if (!directory.exists() && !directory.mkdirs()) {
                    logger.error("Cannot create directory '" + directory.getAbsolutePath() + "'");
                    logger.error("texture '" + file.getAbsolutePath() + "' won't be saved on disk");
                    return;
                }

                try {
                    logger.info("save texture on disk '" + file.getAbsolutePath() + "'");
                    ImageIO.write(texture.getTextureImage(), "PNG", file);
                } catch (IOException e) {
                    logger.error("Cannot write texture " + file.getAbsolutePath() + " on disk");
                    e.printStackTrace();
                }

            }

        }, "save texture on disk").start();
    }

    private static File getCachedTextureFile(URI id, String extension) {
        return new File(DIRECTORY_CACHE_NAME + File.separator + id + "." + extension);

    }

    public synchronized static int generateFtColHashCode(IFeatureCollection<? extends IFeature> iFeatureCollection) {
        if (iFeatureCollection == null || iFeatureCollection.isEmpty())
            return -1;
        int result = 0;
        synchronized (iFeatureCollection) {
            for (IFeature feature : iFeatureCollection) {
                result = 31 * result + feature.getId();
            }
        }
        return result;
    }

    public static URI createTexID(Texture tex_desc, IFeatureCollection<? extends IFeature> iFeatureCollection) throws URISyntaxException {
        if (iFeatureCollection == null) {
            return createTexID(tex_desc);
        }
        return createTexID(tex_desc, generateFtColHashCode(iFeatureCollection));
    }

    private static URI createTexID(Texture tex_desc, int displayable_id) throws URISyntaxException {
        return new URI(tex_desc.getClass().getSimpleName() + "-" + tex_desc.hashCode() + "-" + displayable_id);

    }

    private static URI createTexID(Texture tex_desc) throws URISyntaxException {
        return new URI(tex_desc.getClass().getSimpleName() + "-" + tex_desc.hashCode());
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

    /**
     * empty cache content
     */
    public void clearCache() {
        synchronized (tasksMap) {
            tasksMap.clear();
        }

    }

    public static void addTexture(URI id, BasicTexture rasterImage) {
        if (!textureMap.containsKey(id)) {
            textureMap.put(id, rasterImage);
            return;
        }
        logger.info("Texture " + id + " was not added to the TextureManager since a texture with the same URI is already registered.");
    }

}

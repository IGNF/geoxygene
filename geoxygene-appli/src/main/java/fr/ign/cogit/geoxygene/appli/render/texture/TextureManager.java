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
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.GeOxygeneEventManager;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.BinaryGradientImage;
import fr.ign.cogit.geoxygene.appli.task.TaskListener;
import fr.ign.cogit.geoxygene.appli.task.TaskState;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.texture.BasicTextureDescriptor;
import fr.ign.cogit.geoxygene.style.texture.BinaryGradientImageDescriptor;
import fr.ign.cogit.geoxygene.style.texture.TextureDescriptor;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;

/**
 * @author JeT texture manager
 */
public class TextureManager {

    private static final Logger logger = Logger.getLogger(TextureManager.class
            .getName()); // logger

    private static final Map<TextureDescriptor, TextureTask<BasicTexture>> tasksMap = new HashMap<TextureDescriptor, TextureTask<BasicTexture>>();
    private static final Map<File, TextureTask<BasicTexture>> readersMap = new HashMap<File, TextureTask<BasicTexture>>();

    private static final Map<BinaryGradientImageDescriptor, BinaryGradientImageTask> gradientTasksMap = new HashMap<BinaryGradientImageDescriptor, BinaryGradientImageTask>();
    private static final Map<File, BinaryGradientImageTask> gradientReadersMap = new HashMap<File, BinaryGradientImageTask>();

    private final static TextureManager instance = new TextureManager();

    public static String DIRECTORY_CACHE_NAME = "cache";

    private BasicTextureTaskListener basicListener = null;
    // 2 different readers for reading or generating binary gradient image
    private BinaryGradientImageTaskListener gradientListener = null;

    /**
     * private singleton constructor
     */
    private TextureManager() {
        this.basicListener = new BasicTextureTaskListener(this);
        this.gradientListener = new BinaryGradientImageTaskListener(this);
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
            TextureDescriptor textureDescriptor,
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
     * @param featureCollection
     * @param viewport
     * @return
     */
    public TextureTask<BasicTexture> getTextureTask(String name,
            TextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection, Viewport viewport) {
        if (textureDescriptor == null) {
            return null;
        }
        TextureTask<BasicTexture> textureTask = null;
        // create a task to generate texture image
        synchronized (tasksMap) {
            textureTask = tasksMap.get(textureDescriptor);
            // look for texture in memory
            if (textureTask != null) {
                return textureTask;
            }
            // look for texture on cache disk
            File file = TextureManager.generateTextureUniqueFile(
                    textureDescriptor, featureCollection);
            // logger.debug("Look for file '" + file.getAbsolutePath() + "'");
            // logger.debug(textureDescriptor.toString());
            if (file.isFile()) {
                // logger.info("reading disk-cached texture '"
                // + file.getAbsolutePath() + "'");
                textureTask = this.getTextureReaderTask(file);
                textureTask.addTaskListener(this.basicListener);
                textureTask.start();
                return textureTask;
            }
            // generate texture
            textureTask = TextureTaskFactory.createTextureTask(name,
                    textureDescriptor, featureCollection, viewport);
            if (textureTask == null) {
                logger.error("Unable to create texture task for texture "
                        + textureDescriptor.getClass().getSimpleName());
                return null;
            }
            textureTask.setID(TextureManager.generateTextureUniqueFilename(
                    textureDescriptor, featureCollection));

            tasksMap.put(textureDescriptor, textureTask);
        }

        // do not add texture task to the task manager, they may not be
        // launched and geometry is waiting for them (to be verified)
        // GeOxygeneEventManager.getInstance().getApplication()
        // .getTaskManager().addTask(textureTask);
        textureTask.addTaskListener(this.basicListener);
        textureTask.start();
        return textureTask;
    }

    /**
     * Create or return a Gradient Image task. The task is NOT automatically
     * started when completed Task.getTextureImage() won't be null. Gradient
     * Image is a float image containing gradient values for a given feature
     * collection. This image is not directly displayable
     * 
     * @param name
     *            task name
     * @param gradientTextureDescriptor
     *            gradient texture descriptor described in the SLD
     * @param featureCollection
     *            feature collection to generate gradient image
     * @param viewport
     *            display viewport
     * @return a non started GradientTexture task
     */
    public BinaryGradientImageTask getGradientTextureTask(String name,
            BinaryGradientImageDescriptor gradientTextureDescriptor,
            IFeatureCollection<IFeature> featureCollection, Viewport viewport) {
        if (gradientTextureDescriptor == null) {
            return null;
        }
        BinaryGradientImageTask textureTask = null;
        // create a task to generate texture image
        synchronized (gradientTasksMap) {
            textureTask = gradientTasksMap.get(gradientTextureDescriptor);
            // look for texture in memory
            if (textureTask != null) {
                return textureTask;
            }
            // look for texture on cache disk
            File file = TextureManager.generateBinaryGradientImageUniqueFile(
                    gradientTextureDescriptor, featureCollection);
            // logger.debug("Look for file '" + file.getAbsolutePath() + "'");
            // logger.debug(textureDescriptor.toString());
            if (file.isFile()) {
                // logger.info("reading disk-cached texture '"
                // + file.getAbsolutePath() + "'");
                textureTask = this.getGradientImageReaderTask(file);
                textureTask.addTaskListener(this.gradientListener);
                textureTask.start();
                return textureTask;
            }
            // generate texture
            textureTask = new BinaryGradientImageTask(name,
                    gradientTextureDescriptor, featureCollection);
            // if (textureTask == null) {
            // logger.error("Unable to create texture task for texture "
            // + gradientTextureDescriptor.getClass().getSimpleName());
            // return null;
            // }
            gradientTasksMap.put(gradientTextureDescriptor, textureTask);
        }

        // do not add texture task to the task manager, they may not be
        // launched and geometry is waiting for them (to be verified)
        // GeOxygeneEventManager.getInstance().getApplication()
        // .getTaskManager().addTask(textureTask);
        textureTask.addTaskListener(this.gradientListener);
        textureTask.start();
        return textureTask;
    }

    /**
     * @param file
     * @return
     */
    private TextureTask<BasicTexture> getTextureReaderTask(File file) {
        TextureTask<BasicTexture> textureTask = readersMap.get(file);
        if (textureTask != null) {
            return textureTask;
        }
        textureTask = TextureTaskFactory.createBasicTextureTask(
                "reading texture " + file.getName(), file);
        readersMap.put(file, textureTask);
        textureTask.addTaskListener(this.basicListener);
        return textureTask;
    }

    /**
     * @param file
     * @return
     */
    private BinaryGradientImageTask getGradientImageReaderTask(File file) {
        BinaryGradientImageTask textureTask = gradientReadersMap.get(file);
        if (textureTask != null) {
            return textureTask;
        }
        textureTask = new BinaryGradientImageTask("reading gradient texture "
                + file.getName(), file);
        gradientReadersMap.put(file, textureTask);
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
        textureTask.removeTaskListener(this.basicListener);
        return true;
    }

    private class BasicTextureTaskListener implements
            TaskListener<TextureTask<BasicTexture>> {
        private TextureManager manager = null;

        /**
         * @param manager
         */
        public BasicTextureTaskListener(TextureManager manager) {
            super();
            this.manager = manager;
        }

        @Override
        public void onStateChange(TextureTask<BasicTexture> task,
                TaskState oldState) {
            switch (task.getState()) {
            case FINISHED:
                if (task.getTexture().getTextureFilename() != null) {
                    synchronized (readersMap) {
                        readersMap.remove(task.getTexture()
                                .getTextureFilename());
                    }

                }
                synchronized (tasksMap) {
                    tasksMap.remove(task.getTexture());
                }
                task.removeTaskListener(this);
                if (task.getTexture().getTextureImage() == null) {
                    logger.error("TextureTask has finished with no error but a null texture (its role IS to fill texture.getTextureImage() method)");
                }
                // save texture on disk
                if (task.needWriting()) {
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

    private class BinaryGradientImageTaskListener implements
            TaskListener<BinaryGradientImageTask> {
        private TextureManager manager = null;

        /**
         * @param manager
         */
        public BinaryGradientImageTaskListener(TextureManager manager) {
            super();
            this.manager = manager;
        }

        @Override
        public void onStateChange(BinaryGradientImageTask task,
                TaskState oldState) {
            switch (task.getState()) {
            case FINISHED:
                BinaryGradientImage binaryGradientImage = task
                        .getBinaryGradientImage();
                if (binaryGradientImage != null) {
                    synchronized (gradientReadersMap) {
                        gradientReadersMap.remove(task
                                .getBinaryGradientImageFile());
                    }

                }
                synchronized (gradientTasksMap) {
                    gradientTasksMap.remove(task);
                }
                task.removeTaskListener(this);
                if (binaryGradientImage == null) {
                    logger.error("TextureTask has finished with no error but a null texture (its role IS to fill texture.getTextureImage() method)");
                }
                // save texture on disk if it has been generated
                if (task.needWriting()) {
                    this.manager.saveBinaryGradientImage(task);
                }
                GeOxygeneEventManager.refreshApplicationGui();
                break;
            case ERROR:
            case STOPPED:
                synchronized (gradientTasksMap) {
                    gradientTasksMap.remove(task);
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
                logger.debug("save image task id " + task.getID()
                        + " task name = " + task.getName());
                File file = new File(DIRECTORY_CACHE_NAME + File.separator
                        + task.getID() + ".png");
                File directory = file.getParentFile();
                if (!directory.exists() && !directory.mkdirs()) {
                    logger.error("Cannot create directory '"
                            + directory.getAbsolutePath() + "'");
                    logger.error("texture '" + file.getAbsolutePath()
                            + "' won't be saved on disk");
                    return;
                }

                try {
                    logger.info("save texture on disk '"
                            + file.getAbsolutePath() + "'");
                    ImageIO.write(texture.getTextureImage(), "PNG", file);
                } catch (IOException e) {
                    logger.error("Cannot write texture "
                            + file.getAbsolutePath() + " on disk");
                    e.printStackTrace();
                }

            }

        }, "save texture on disk").start();
    }

    private void saveBinaryGradientImage(final BinaryGradientImageTask task) {
        if (task == null) {
            throw new IllegalArgumentException("Cannot save null task");
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                BinaryGradientImage binaryGradientImage = task.getBinaryGradientImage();
                if (binaryGradientImage == null) {
                    logger.error("Asked to save gradient image that has no generated content");
                    return;
                }
                File file = task.getBinaryGradientImageFile();
                File directory = file.getParentFile();
                if (!directory.exists() && !directory.mkdirs()) {
                    logger.error("Cannot create directory '"
                            + directory.getAbsolutePath() + "'");
                    logger.error("texture '" + file.getAbsolutePath()
                            + "' won't be saved on disk");
                    return;
                }

                try {
                    // use a temp file to avoid writing and reading at the same
                    // time
                    File tmpFile = File.createTempFile("gradientBinaryImage",
                            ".tmp", directory);
                    logger.info("save gradient image on disk '"
                            + file.getAbsolutePath() + "' tmp file = '"
                            + tmpFile.getAbsolutePath() + "'");
                    BinaryGradientImage.writeBinaryGradientImage(tmpFile,
                            binaryGradientImage);
                    // move temp file to regular file
                    Files.move(tmpFile.toPath(), file.toPath(),
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.ATOMIC_MOVE);
                } catch (IOException e) {
                    logger.error("Cannot write gradient image "
                            + file.getAbsolutePath() + " on disk");
                    e.printStackTrace();
                }

            }

        }, "save texture on disk").start();

    }

    /**
     * Generate a unique filename for a texture decriptor and a feature
     * collection
     * 
     * @param textureDescriptor
     * @param featureCollection
     * @return
     */
    public static String generateTextureUniqueFilename(
            TextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection) {
        return textureDescriptor.hashCode() + "-"
                + generateHashCode(featureCollection);
    }

    /**
     * Generate a unique file for a texture descriptor and a feature collection.
     * It uses the texture filename
     * 
     * @param textureDescriptor
     * @param featureCollection
     * @return
     */
    public static File generateTextureUniqueFile(
            TextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection) {
        return new File(DIRECTORY_CACHE_NAME
                + File.separator
                + generateTextureUniqueFilename(textureDescriptor,
                        featureCollection) + ".png");
    }

    /**
     * Generate a unique file for a binary gradient image decriptor and a
     * feature collection. It uses the gradientImage filename
     * 
     * @param descriptor
     * @param featureCollection
     * @return
     */
    public static File generateBinaryGradientImageUniqueFile(
            BinaryGradientImageDescriptor descriptor,
            IFeatureCollection<IFeature> featureCollection) {
        return new File(DIRECTORY_CACHE_NAME
                + File.separator
                + generateBinaryGradientImageUniqueFilename(descriptor,
                        featureCollection) + ".bgi");
    }

    /**
     * Generate a unique filename for a binary gradient image decriptor and a
     * feature collection
     * 
     * @param descriptor
     * @param featureCollection
     * @return
     */
    public static String generateBinaryGradientImageUniqueFilename(
            BinaryGradientImageDescriptor descriptor,
            IFeatureCollection<IFeature> featureCollection) {
        return descriptor.hashCode() + "-"
                + generateHashCode(featureCollection);
    }

    private static int generateHashCode(
            IFeatureCollection<IFeature> featureCollection) {
        int result = 0;
        for (IFeature feature : featureCollection) {
            result = 31 * result + feature.getId();
        }
        return result;
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

}

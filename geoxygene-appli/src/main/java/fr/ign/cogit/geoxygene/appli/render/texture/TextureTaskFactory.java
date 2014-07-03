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

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.style.texture.BasicTextureDescriptor;
import fr.ign.cogit.geoxygene.style.texture.GradientTextureDescriptor;
import fr.ign.cogit.geoxygene.style.texture.PerlinNoiseTextureDescriptor;
import fr.ign.cogit.geoxygene.style.texture.TextureDescriptor;
import fr.ign.cogit.geoxygene.style.texture.TileDistributionTextureDescriptor;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;

/**
 * @author JeT
 * 
 */
public class TextureTaskFactory {

    /**
     * private factory constructor
     */
    private TextureTaskFactory() {
        // factory class
    }

    public static TextureTask<BasicTexture> createTextureTask(String name,
            TextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection, Viewport viewport) {
        if (textureDescriptor instanceof TileDistributionTextureDescriptor) {
            return createTileDistributionTextureTask(name,
                    (TileDistributionTextureDescriptor) textureDescriptor,
                    featureCollection, viewport);

        }
        if (textureDescriptor instanceof PerlinNoiseTextureDescriptor) {
            return createPerlinNoiseTextureTask(name,
                    (PerlinNoiseTextureDescriptor) textureDescriptor,
                    featureCollection, viewport);

        }
        if (textureDescriptor instanceof BasicTextureDescriptor) {
            return createBasicTextureTask(name,
                    (BasicTextureDescriptor) textureDescriptor,
                    featureCollection, viewport);

        }
        if (textureDescriptor instanceof GradientTextureDescriptor) {
            return createGradientTextureTask(name,
                    (GradientTextureDescriptor) textureDescriptor,
                    featureCollection, viewport);

        }
        throw new UnsupportedOperationException("texture descriptor type "
                + textureDescriptor.getClass().getSimpleName()
                + " is not supported");
    }

    public static BasicTextureTask createBasicTextureTask(String name,
            BasicTextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection, Viewport viewport) {
        return new BasicTextureTask(name, textureDescriptor);
    }

    public static PerlinNoiseTextureTask createPerlinNoiseTextureTask(
            String name, PerlinNoiseTextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection, Viewport viewport) {
        return new PerlinNoiseTextureTask(name, textureDescriptor,
                featureCollection, viewport);
    }

    public static TileDistributionTextureTask createTileDistributionTextureTask(
            String name, TileDistributionTextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection, Viewport viewport) {
        return new TileDistributionTextureTask(name, textureDescriptor,
                featureCollection, viewport);
    }

    public static GradientTextureTask createGradientTextureTask(String name,
            GradientTextureDescriptor textureDescriptor,
            IFeatureCollection<IFeature> featureCollection, Viewport viewport) {
        return new GradientTextureTask(name, textureDescriptor,
                featureCollection);
    }

    public static TextureTask<BasicTexture> createBasicTextureTask(String name,
            File file) {
        return new BasicTextureTask(name, file);
    }

}

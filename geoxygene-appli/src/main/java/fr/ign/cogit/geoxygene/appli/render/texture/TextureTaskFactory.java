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

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.gl.BinaryGradientImage;
import fr.ign.cogit.geoxygene.style.texture.PerlinNoiseTexture;
import fr.ign.cogit.geoxygene.style.texture.SimpleTexture;
import fr.ign.cogit.geoxygene.style.texture.BinaryGradientImageDescriptor;
import fr.ign.cogit.geoxygene.style.texture.Texture;
import fr.ign.cogit.geoxygene.style.texture.TileDistributionTexture;
import fr.ign.cogit.geoxygene.util.gl.BasicTexture;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;

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

    /**
     * For generated textures
     * 
     * @param identifier
     * @param textureDescriptor
     * @param featureCollection
     * @param p
     * @return
     */
    public static TextureTask<? extends GLTexture> createTextureTask(URI identifier, Texture textureDescriptor, IFeatureCollection<IFeature> featureCollection, Viewport p) {
        if (textureDescriptor instanceof TileDistributionTexture) {
            return createTileDistributionTextureTask(identifier, (TileDistributionTexture) textureDescriptor, featureCollection, p);

        }
        if (textureDescriptor instanceof PerlinNoiseTexture) {
            return createPerlinNoiseTextureTask(identifier, (PerlinNoiseTexture) textureDescriptor, featureCollection);

        }
        if (textureDescriptor instanceof SimpleTexture) {
            return createBasicTextureTask(identifier, (SimpleTexture) textureDescriptor);

        }
        if (textureDescriptor instanceof BinaryGradientImageDescriptor) {
            return createGradientTextureTask(identifier, (BinaryGradientImageDescriptor) textureDescriptor, featureCollection);

        }
        throw new UnsupportedOperationException("texture descriptor type " + textureDescriptor.getClass().getSimpleName() + " is not supported");
    }

    public static BasicTextureTask createBasicTextureTask(URI identifier, SimpleTexture textureDescriptor) {
        BasicTextureTask bt = new BasicTextureTask(identifier, textureDescriptor);
        return bt;
    }

    public static PerlinNoiseTextureTask createPerlinNoiseTextureTask(URI identifier, PerlinNoiseTexture textureDescriptor, IFeatureCollection<IFeature> featureCollection) {
        PerlinNoiseTextureTask t = new PerlinNoiseTextureTask(identifier, textureDescriptor, featureCollection);
        return t;
    }

    public static TileDistributionTextureTask createTileDistributionTextureTask(URI identifier, TileDistributionTexture textureDescriptor, IFeatureCollection<IFeature> featureCollection, Viewport p) {
        TileDistributionTextureTask t = new TileDistributionTextureTask(identifier, textureDescriptor, featureCollection, p);
        return t;
    }

    public static GradientTextureTask createGradientTextureTask(URI identifier, BinaryGradientImageDescriptor textureDescriptor, IFeatureCollection<IFeature> featureCollection) {
        GradientTextureTask t = new GradientTextureTask(identifier, textureDescriptor, featureCollection);
        return t;
    }

    /**
     * A very simple texture task that create a simple texture from an URL.
     * 
     * @param texture_uri
     *            : the unique identifier of the texture to create.
     * @param texture_location
     *            : the absolute location of the texture.
     * @return a texture task
     */
    public static TextureTask<BasicTexture> createTextureTask(URI texture_uri, URL texture_location) {
        SimpleTexture st = new SimpleTexture();
        st.setAbsoluteLocation(texture_location);
        st.setTextureURI(texture_uri);
        return new BasicTextureTask(texture_uri, st);
    }

}

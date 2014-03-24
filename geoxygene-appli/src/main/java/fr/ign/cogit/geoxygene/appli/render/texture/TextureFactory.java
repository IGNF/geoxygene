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
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import com.jhlabs.image.LinearColormap;
import com.jhlabs.image.TextureFilter;

import fr.ign.cogit.geoxygene.style.texture.BasicTexture;
import fr.ign.cogit.geoxygene.style.texture.PerlinNoiseTexture;
import fr.ign.cogit.geoxygene.util.ImageUtil;

/**
 * @author JeT
 * 
 */
public class TextureFactory {

    /**
     * private constructor
     */
    private TextureFactory() {
        // factory class
    }

    /*
     * (non-Javadoc)
     * 
     * @see fr.ign.cogit.geoxygene.style.texture.Texture#getImage()
     */
    public static BufferedImage generatePerlinNoiseTexture(PerlinNoiseTexture texture) {
        TextureFilter filter = new TextureFilter();
        filter.setColormap(new LinearColormap(texture.getColor1().getRGB(), texture.getColor2().getRGB()));
        filter.setScale(texture.getScale());
        filter.setStretch(texture.getStretch());
        filter.setAmount(texture.getAmount());
        filter.setAngle(texture.getAngle());
        BufferedImage imgTexture = ImageUtil.createBufferedImage(texture.getTextureWidth(), texture.getTextureHeight());
        //        imgTexture.setData(img.getData());
        filter.filter(imgTexture, imgTexture);
        return imgTexture;
    }

    public static BufferedImage generateBasicTexture(BasicTexture texture) throws MalformedURLException, IOException {
        return ImageIO.read(new URL(texture.getUrl()));
    }

}

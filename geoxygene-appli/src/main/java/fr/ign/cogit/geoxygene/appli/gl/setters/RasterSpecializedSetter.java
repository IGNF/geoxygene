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

package fr.ign.cogit.geoxygene.appli.gl.setters;

import java.net.URI;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.gl.RasterImage;
import fr.ign.cogit.geoxygene.appli.render.methods.NamedRenderingParametersMap;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;

public class RasterSpecializedSetter implements UserDefinedGLProgramSetter {

    @Override
    public boolean preSetActions(NamedRenderingParametersMap parameters, GLProgram program) {
        return true;
    }

    @Override
    public boolean postSetActions(NamedRenderingParametersMap parameters, GLProgram program) {
        URI raster_image_uri = (URI) parameters.getByName("bufferImage");
        if (raster_image_uri == null)
            return false;
        RasterImage raster = (RasterImage) TextureManager.getTexture(raster_image_uri);
        if (raster == null) {
            Logger.getRootLogger().error("Raster image " + raster_image_uri + " was not found in the TextureManager");
            return false;
        }
        try {
            if (raster.getDefColormap()) {
                program.setUniform("typeColormap", raster.getImageColorMap().getTypeColormap());
                program.setUniform("nbPointsColormap", raster.getImageColorMap().getNbPoints());
            }else{
                program.setUniform("typeColormap", 0);
            }
        } catch (GLException e) {
            e.printStackTrace();
            return false;
        }
        
        try {
          if (raster.getDefTide()) {
              program.setUniform("tideRange", raster.getTideRange());
              program.setUniform("tidePhase", raster.getTidePhase());
              program.setUniform("waterHeightMean", raster.getWaterHeightMean());
              program.setUniform("timeAcceleration", raster.getTimeAcceleration());
              program.setUniform("tideCycleLength", raster.getTideCycleLength());
          }
        } catch (GLException e) {
          e.printStackTrace();
          return false;
        }
        
        
        return true;
    }

}

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
import fr.ign.cogit.geoxygene.appli.render.methods.NamedRenderingParametersMap;
import fr.ign.cogit.geoxygene.appli.render.texture.TextureManager;
import fr.ign.cogit.geoxygene.util.gl.GLException;
import fr.ign.cogit.geoxygene.util.gl.GLProgram;
import fr.ign.cogit.geoxygene.util.gl.GLTexture;

public class BrushStrokeSetter implements UserDefinedGLProgramSetter {

    Integer current_start_width = 1;
    Integer current_end_width = 1;

    @Override
    public boolean preSetActions(NamedRenderingParametersMap parameters, GLProgram program) {
        return true;
    }

    @Override
    public boolean postSetActions(NamedRenderingParametersMap parameters, GLProgram program) {
        try {
            if(parameters.getByName("paperReferenceMapScale") != null)
                program.setUniform("mapScaleDiv1000", (Double)parameters.getByName("paperReferenceMapScale")/1000.);
            URI brushtexuri = (URI) parameters.getByName("brushTexture");
            GLTexture bt = TextureManager.retrieveTexture(brushtexuri);
            if(bt != null){
                program.setUniform("brushWidth", bt.getTextureWidth());
                program.setUniform("brushHeight", bt.getTextureHeight());
                program.setUniform("paperScale", 1); // Forced to 1 in the original
                                                     // Geoxygene because of
                                                     // reasons.
                Float swidth= Float.parseFloat((String) parameters.getByName("stroke-width"));
                program.setUniform("brushScale", swidth / bt.getTextureHeight());    
            }
            return true;
        } catch (GLException e) {
            e.printStackTrace();
        }
        return false;
    }

}

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

package fr.ign.cogit.geoxygene.appli.ui;

import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.style.expressive.DefaultFill2DShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.DefaultLineShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.RandomVariationShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.UserFill2DShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.UserLineShaderDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.UserShaderDescriptor;

/**
 * @author JeT
 * 
 */
public class ShaderUIFactory {

    public static GenericParameterUI getShaderUI(
            ShaderDescriptor descriptor, ProjectFrame projectFrame) {
        if (descriptor instanceof RandomVariationShaderDescriptor) {
            return new RandomVariationShaderUI(
                    ((RandomVariationShaderDescriptor) descriptor),
                    projectFrame);

        }
        if (descriptor instanceof DefaultLineShaderDescriptor) {
            return new DefaultLineShaderUI(
                    (DefaultLineShaderDescriptor) (descriptor), projectFrame);

        }
        if (descriptor instanceof UserLineShaderDescriptor) {
            return new UserLineShaderUI(
                    (UserShaderDescriptor) (descriptor), projectFrame);

        }
        if (descriptor instanceof DefaultFill2DShaderDescriptor) {
            return new DefaultFill2DShaderUI(
                    (DefaultFill2DShaderDescriptor) (descriptor), projectFrame);

        }
        if (descriptor instanceof UserFill2DShaderDescriptor) {
            return new UserFill2DShaderUI(
                    (UserFill2DShaderDescriptor) (descriptor), projectFrame);

        }
        return new NoShaderUI(descriptor, projectFrame);
    }

}

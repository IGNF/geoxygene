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

import fr.ign.cogit.geoxygene.style.expressive.ParameterDescriptor;
import fr.ign.cogit.geoxygene.style.expressive.ParameterDescriptorFloat;

/**
 * @author JeT
 * 
 */
public final class ParameterUIFactory {

    private ParameterUIFactory() {
        // private constructor for factory
    }

    /**
     * Create a user interface for given descriptor
     * 
     * @param descriptor
     * @return
     */
    public static ParameterUI createUI(ParameterDescriptor descriptor) {
        if (descriptor instanceof ParameterDescriptorFloat) {
            return createFloatUI((ParameterDescriptorFloat) descriptor);

        }
        throw new IllegalStateException("Parameter Descriptor "
                + descriptor.getClass().getSimpleName()
                + " is not associated with any User Interface");
    }

    /**
     * Create a User interface for Float parameter types
     * 
     * @param descriptor
     * @return
     */
    private static ParameterUI createFloatUI(ParameterDescriptorFloat descriptor) {
        return new ParameterUIFloat(descriptor);
    }
}

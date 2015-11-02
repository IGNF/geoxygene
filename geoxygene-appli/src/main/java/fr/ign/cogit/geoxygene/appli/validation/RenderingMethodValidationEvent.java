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

package fr.ign.cogit.geoxygene.appli.validation;

import javax.xml.bind.ValidationEventLocator;
import javax.xml.bind.helpers.ValidationEventImpl;
import javax.xml.stream.Location;

import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodDescriptor;
import fr.ign.cogit.geoxygene.appli.render.methods.RenderingMethodParameterDescriptor;

public class RenderingMethodValidationEvent  extends ValidationEventImpl{
    private Location location;
    private RenderingMethodParameterDescriptor param_descriptor;
    private RenderingMethodDescriptor method;
    
    // 0 : missing, 1 : invalid type, 2: out of bounds, 3 : invalid choice value  
    private int status;
    
    public RenderingMethodValidationEvent(int _severity, String _message, ValidationEventLocator _locator) {
        super(_severity, _message, _locator);
    }
    
    public RenderingMethodValidationEvent(int _severity, String _message, Location _location) {
        super(_severity, _message, null);
        this.location = _location;
    }

}

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

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;

public class LocationListener extends Listener {

    private XMLStreamReader xsr;
    // We cannot use a map because the SLD and its sub-objets has mutable
    // properties used in the hashcode method. These properties change between
    // beforeUnmarshalling and getLocation.
    private List<Object> targets = new ArrayList<Object>();
    private List<Location> locations = new ArrayList<Location>();
    
    public LocationListener(XMLStreamReader xsr) {
        this.xsr = xsr;
    }

    @Override
    public void beforeUnmarshal(Object target, Object parent) {
        targets.add(target);
        locations.add(xsr.getLocation());
    }

    public Location getLocation(Object o) {
        int id = this.targets.indexOf(o);
        if(id ==-1)
            return null;
        return this.locations.get(id);
    }

}
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

package fr.ign.cogit.geoxygene.appli.render.methods;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import fr.ign.cogit.geoxygene.style.StylingParameter;
import fr.ign.cogit.geoxygene.style.SvgParameter;
import fr.ign.cogit.geoxygene.style.expressive.ExpressiveParameter;

@XmlAccessorType(XmlAccessType.FIELD)
public class ChoiceRestriction implements RenderingMethodParameterRestriction {

    @XmlElementWrapper(name = "Choices")
    @XmlElements({ @XmlElement(name = "Choice", type = SvgParameter.class), @XmlElement(name = "Choice", type = ExpressiveParameter.class) })
    List<StylingParameter> list_of_choices = new ArrayList<StylingParameter>();

    @Override
    public boolean validate(StylingParameter p) {
        for (StylingParameter choice : this.list_of_choices) {
            if (choice.equals(p))
                return true;
        }
        return false;
    }

    public ChoiceRestriction(StylingParameter... choices) {
        for (StylingParameter choice : choices) {
            this.list_of_choices.add(choice);
        }
    }

    public ChoiceRestriction() {
    }

    public void addChoice(StylingParameter new_choice) {
        this.list_of_choices.add(new_choice);
    }

}

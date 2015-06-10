/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package fr.ign.cogit.geoxygene.style.thematic;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

@XmlAccessorType(XmlAccessType.FIELD)
public class DiagramSymbolizer {
  @XmlElement(required = true, name = "DiagramType")
  private String diagramType = null;
  @XmlElements(@XmlElement(name = "DiagramRadius", type = DiagramRadius.class))
  @XmlElementWrapper(name = "DiagramSize")
  private List<DiagramSizeElement> diagramSize = new ArrayList<DiagramSizeElement>(
      0);
  @XmlElement(required = true, name = "ThematicClass")
  private List<ThematicClass> thematicClass = new ArrayList<ThematicClass>(0);

  public String getDiagramType() {
    return this.diagramType;
  }

  public void setDiagramType(String diagramType) {
    this.diagramType = diagramType;
  }

  public List<DiagramSizeElement> getDiagramSize() {
    return this.diagramSize;
  }

  public void setDiagramSize(List<DiagramSizeElement> diagramSize) {
    this.diagramSize = diagramSize;
  }

  public void setThematicClass(List<ThematicClass> thematicClass) {
    this.thematicClass = thematicClass;
  }

  public List<ThematicClass> getThematicClass() {
    return this.thematicClass;
  }
}

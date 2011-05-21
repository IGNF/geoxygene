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
  @XmlElements(
      @XmlElement(name = "DiagramRadius", type = DiagramRadius.class)
  )
  @XmlElementWrapper(name = "DiagramSize")
  private List<DiagramSizeElement> diagramSize = new ArrayList<DiagramSizeElement>(0);
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
    return thematicClass;
  }
}

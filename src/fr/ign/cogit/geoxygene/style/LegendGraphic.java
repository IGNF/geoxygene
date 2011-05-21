package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class LegendGraphic {
  @XmlElement(name = "Graphic")
  private Graphic graphic = null;
  public void setGraphic(Graphic graphic) {
    this.graphic = graphic;
  }
  public Graphic getGraphic() {
    return this.graphic;
  }
}

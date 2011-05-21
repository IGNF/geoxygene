package fr.ign.cogit.geoxygene.style.thematic;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;

import fr.ign.cogit.geoxygene.filter.expression.Expression;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.style.Fill;

@XmlAccessorType(XmlAccessType.FIELD)
public class ThematicClass {
  @XmlElement(required = false, name = "ClassLabel")
  private String classLabel = null;
  @XmlElements(
      @XmlElement(name = "PropertyName", type = PropertyName.class)
  )
  @XmlElementWrapper(name = "ClassValue")
  private Expression[] classValue = null;
  @XmlElement(required = true, name = "Fill")
  private Fill fill = null;
  public String getClassLabel() {
    return this.classLabel;
  }
  public void setClassLabel(String classLabel) {
    this.classLabel = classLabel;
  }
  public Expression getClassValue() {
    if (this.classValue == null) {
      return null;
    }
    return this.classValue[0];
  }
  public void setClassValue(Expression classValue) {
    if (this.classValue == null) {
      this.classValue = new Expression[1];
    }
    this.classValue[0] = classValue;
  }
  public Fill getFill() {
    return fill;
  }
  public void setFill(Fill fill) {
    this.fill = fill;
  }
}

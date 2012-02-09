package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

@XmlAccessorType(XmlAccessType.FIELD)
public class Label {

	@XmlElement(name="PropertyName")
	private PropertyName propertyName;
	
	public void setPropertyName(PropertyName propertyName) {
		this.propertyName = propertyName;
	}

	public PropertyName getPropertyName(){
		return this.propertyName;
	}
}

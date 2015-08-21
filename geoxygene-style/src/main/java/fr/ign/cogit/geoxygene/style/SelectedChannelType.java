package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 * 
 * @author AMasse
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class SelectedChannelType {
    
    @XmlElement(name = "SourceChannelName")
    private int sourceChannelName;
    
    public int getSourceChannelName() {
        return sourceChannelName;
    }
    
    public void SetSourceChannelName(int sourceChannelName) {
        this.sourceChannelName = sourceChannelName;
    }
    
    // TODO : contrast enhancement here too
    
    
}

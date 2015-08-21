package fr.ign.cogit.geoxygene.style;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author AMasse
 *
 */

@XmlAccessorType(XmlAccessType.FIELD)
public class ChannelSelection {

    // TODO : Norm extension: AlphaChannel
    
    @XmlElement(name = "RedChannel")
    private SelectedChannelType redChannel = null;
    
    public SelectedChannelType getRedChannel() {
        return redChannel;
    }
    
    public void setRedChannel(SelectedChannelType redChannel) {
        this.redChannel = redChannel;
    }

    @XmlElement(name = "GreenChannel")
    private SelectedChannelType greenChannel = null;
    
    public SelectedChannelType getGreenChannel() {
        return greenChannel;
    }
    
    public void setGreenChannel(SelectedChannelType greenChannel) {
        this.greenChannel = greenChannel;
    }

    @XmlElement(name = "BlueChannel")
    private SelectedChannelType blueChannel = null;
    
    public SelectedChannelType getBlueChannel() {
        return blueChannel;
    }
    
    public void setBlueChannel(SelectedChannelType blueChannel) {
        this.blueChannel = blueChannel;
    }
    

    @XmlElement(name = "GrayChannel")
    private SelectedChannelType grayChannel = null;
    
    public SelectedChannelType getGrayChannel() {
        return redChannel;
    }
    
    public void setGrayChannel(SelectedChannelType grayChannel) {
        this.grayChannel = grayChannel;
    }
    
    
    // Booleans
    public boolean isGrayChannel() {
        if (grayChannel != null) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isRGBChannels() {
        if ((redChannel != null)&&(greenChannel != null)&&(blueChannel != null)) {
            return true;
        } else {
            return false;
        }
    }
}

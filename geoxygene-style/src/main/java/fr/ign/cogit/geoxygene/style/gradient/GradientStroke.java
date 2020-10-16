package fr.ign.cogit.geoxygene.style.gradient;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlTransient;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.style.GraphicType;
import fr.ign.cogit.geoxygene.style.SvgParameter;

@XmlAccessorType(XmlAccessType.FIELD)
public class GradientStroke implements GraphicType {

  @XmlTransient
  private static final Logger logger = LogManager.getLogger(GradientStroke.class);
  @XmlTransient
  private Color color1 = Color.YELLOW;

  public synchronized Color getColor1() {
    this.updateValues();
    return this.color1;
  }

  public synchronized void setColor1(Color color1) {
    this.color1 = color1;
    boolean found = false;
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("gradient-color1")) { //$NON-NLS-1$
          String rgb = Integer.toHexString(color1.getRGB());
          rgb = rgb.substring(2, rgb.length());
          parameter.setValue("#" + rgb); //$NON-NLS-1$
          found = true;
        }
      }
      if (!found) {
        SvgParameter parameter = new SvgParameter();
        parameter.setName("gradient-color1"); //$NON-NLS-1$
        String rgb = Integer.toHexString(color1.getRGB());
        rgb = rgb.substring(2, rgb.length());
        parameter.setValue("#" + rgb); //$NON-NLS-1$
        this.svgParameters.add(parameter);
      }
    }
  }

  @XmlTransient
  private Color color2 = Color.RED;

  public synchronized Color getColor2() {
    this.updateValues();
    return this.color2;
  }

  public synchronized void setColor2(Color color2) {
    this.color2 = color2;
    boolean found = false;
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("gradient-color2")) { //$NON-NLS-1$
          String rgb = Integer.toHexString(color2.getRGB());
          rgb = rgb.substring(2, rgb.length());
          parameter.setValue("#" + rgb); //$NON-NLS-1$
          found = true;
        }
      }
      if (!found) {
        SvgParameter parameter = new SvgParameter();
        parameter.setName("gradient-color2"); //$NON-NLS-1$
        String rgb = Integer.toHexString(color2.getRGB());
        rgb = rgb.substring(2, rgb.length());
        parameter.setValue("#" + rgb); //$NON-NLS-1$
        this.svgParameters.add(parameter);
      }
    }
  }

  /**
   * Returns the color of the stroke, considering the opacity attribute.
   * 
   * @return The color of the stroke, considering the opacity attribute.
   */
  public synchronized Color getColor1(Object object) {

    if (object == null) {
      return this.getColor1();
    }
    if (this.colorPropertyName1 == null) {
      this.updateValues();
    }
    if (this.colorPropertyName1 == null) {
      return this.getColor1();
    } else {
      Color compColor = (Color) this.colorPropertyName1.evaluate(object);
      // if (this.getStrokeOpacity(object) != 1.0f) {
      // compColor = new Color(compColor.getRed(), compColor.getGreen(),
      // compColor.getBlue(), (int) (this.getStrokeOpacity(object) * 255f));
      // }
      return compColor;
    }
  }

  /**
   * Returns the color of the stroke, considering the opacity attribute.
   * 
   * @return The color of the stroke, considering the opacity attribute.
   */
  public synchronized Color getColor2(Object object) {

    if (object == null) {
      return this.getColor2();
    }
    if (this.colorPropertyName2 == null) {
      this.updateValues();
    }
    if (this.colorPropertyName2 == null) {
      return this.getColor2();
    } else {
      Color compColor = (Color) this.colorPropertyName2.evaluate(object);
      // if (this.getStrokeOpacity(object) != 1.0f) {
      // compColor = new Color(compColor.getRed(), compColor.getGreen(),
      // compColor.getBlue(), (int) (this.getStrokeOpacity(object) * 255f));
      // }
      return compColor;
    }
  }

  @XmlElements({ @XmlElement(name = "SvgParameter", type = SvgParameter.class),
      @XmlElement(name = "CssParameter", type = SvgParameter.class) })
  private List<SvgParameter> svgParameters = new ArrayList<SvgParameter>(0);

  /**
   * Renvoie la valeur de l'attribut cssParameters.
   * 
   * @return la valeur de l'attribut cssParameters
   */
  public List<SvgParameter> getSvgParameters() {
    return this.svgParameters;
  }

  /**
   * Affecte la valeur de l'attribut cssParameters.
   * 
   * @param svgParameters l'attribut cssParameters à affecter
   */
  public void setSvgParameters(List<SvgParameter> svgParameters) {
    this.svgParameters = svgParameters;
    this.updateValues();
  }

  @XmlTransient
  private PropertyName colorPropertyName1;

  @XmlTransient
  private PropertyName colorPropertyName2;

  private synchronized void updateValues() {
    synchronized (this.svgParameters) {
      for (SvgParameter parameter : this.svgParameters) {
        if (parameter.getName().equalsIgnoreCase("gradient-color1")) { //$NON-NLS-1$
          if (parameter.getPropertyName() != null) {
            this.colorPropertyName1 = parameter.getPropertyName();
          } else if (parameter.getValue() != null) {
            this.color1 = Color.decode(parameter.getValue().trim());
          }
        } else if (parameter.getName().equalsIgnoreCase("gradient-color2")) { //$NON-NLS-1$
          if (parameter.getPropertyName() != null) {
            this.colorPropertyName2 = parameter.getPropertyName();
          } else if (parameter.getValue() != null) {
            this.color2 = Color.decode(parameter.getValue().trim());
          }
        }
      }
    }
  }

  public PropertyName getColorPropertyName1() {
    return colorPropertyName1;
  }

  public void setColorPropertyName1(PropertyName colorPropertyName1) {
    this.colorPropertyName1 = colorPropertyName1;
  }

  public PropertyName getColorPropertyName2() {
    return colorPropertyName2;
  }

  public void setColorPropertyName2(PropertyName colorPropertyName2) {
    this.colorPropertyName2 = colorPropertyName2;
  }
}

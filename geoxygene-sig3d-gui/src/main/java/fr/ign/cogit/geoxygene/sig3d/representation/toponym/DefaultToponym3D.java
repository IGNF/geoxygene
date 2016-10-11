package fr.ign.cogit.geoxygene.sig3d.representation.toponym;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;

import fr.ign.cogit.geoxygene.sig3d.representation.Default3DRep;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 * Classe abstraite pour l'affichage de toponymes 3D. Le toponyme est un texte
 * s'affichant à l'emplacement d'un point 3D (classes GM_Point ou
 * GM_MultiPoints)
 * 
 * Angles de rotations suivant les angles X,Y,Z lambda; teta; phi;
 * 
 * Valeur du texte à afficher : text;
 * 
 * Police de caractère : police; Taille du toponyme : taille;
 * 
 * Couleur du toponyme couleur;
 * 
 * 
 * Abstract class for toponym rendering. Toponym is a text displayed on a 3D
 * point (Object GM_Point or GM_MultiPoint). Parameters are : - the text - the
 * color - the size - the police - angular orientation (or allways facing
 * camera)
 * 
 */
public abstract class DefaultToponym3D extends Default3DRep {

  protected double lambda;
  protected double teta;
  protected double phi;
  protected String text;
  protected String police;
  protected double size;
  protected Color color = null;

  /**
   * Permet de récupèrer la couleur de l'objet
   * 
   * @return la couleur du toponyme
   */
  public Color getColor() {
    return this.color;
  }

  protected double opacity = 0;

  /**
   * Indique le taux d'opacité de la représentation de l'objet Entre 0 et 1 (0
   * invisible)
   * 
   * @return la valeur de l'opacité
   */
  public double getOpacity() {
    return this.opacity;
  }

  /**
   * 
   * 
   * @return Angle de rotation suivant X
   */
  public double getLambda() {
    return this.lambda;
  }

  /**
   * 
   * 
   * @return Angle de rotation suivant Y
   */
  public double getTeta() {
    return this.teta;
  }

  /**
   * 
   * 
   * @return Angle de rotation suivant Z
   */
  public double getPhi() {
    return this.phi;
  }

  /**
   * 
   * 
   * @return Le texte affiché
   */
  public String getText() {
    return this.text;
  }

  /**
   * 
   * 
   * @return Police du texte
   */
  public String getPolice() {
    return this.police;
  }

  /**
   * 
   * 
   * @return Taille du texte
   */
  public double getSize() {
    return this.size;
  }

  @Override
  public Component getRepresentationComponent() {
    JLabel lab = new JLabel("Abc");
    lab.setForeground(this.getColor());
    lab.setFont(new Font(this.getPolice(), Font.PLAIN, 20));
    lab.setOpaque(true);

    return lab;
  }

}

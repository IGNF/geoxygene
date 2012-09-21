package fr.ign.cogit.geoxygene.sig3d.representation.basic;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.media.j3d.Shape3D;
import javax.swing.JButton;
import javax.swing.SwingConstants;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
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
 * Cette classe astraite contient un ensemble de méthodes communes à la
 * représentation de base proposée Elle permet de construire les représentations
 * de bases pour les différentes géométries 3D Sont implémentés : This class
 * contain some methods for basic-representation classes
 */
public abstract class BasicRep3D extends Default3DRep {

  /**
   * Constructeur commun, permet de renseigner les différents attributs et de
   * créer un BranchGroup avec les bonnes autorisation
   * 
   * @param feat l'entité à laquelle est rattachée cette représentation
   * @param isClrd indique si l'objet à une couleur prédéfinie
   * @param color indique quelle est cette couleur prédéfinie
   * @param coefOpacite indique le coefficient
   * @param isSolid ^indique si l'objet est représenté de manière solid ou en
   *          fil de fer
   */
  public BasicRep3D(IFeature feat, boolean isClrd, Color color,
      double coefOpacite, boolean isSolid) {
    // On créer le BG avec les autorisations ad hoc
    super();

    // Affectations de base
    this.feat = feat;
    this.color = color;
    this.isColored = isClrd;
    this.opacity = coefOpacite;
    this.isSolid = isSolid;

  }

  protected Color color = null;

  /**
   * Permet de récupèrer la couleur de l'objet
   * 
   * @return la couleur si elle est défini
   */
  public Color getColor() {
    return this.color;
  }

  protected boolean isColored = false;

  /**
   * Indique si l'objet possède une couleur ou si il est représenté de manière
   * multicolore
   * 
   * @return Indique si une couleur est attachée à l'objet
   */
  public boolean isColored() {
    return this.isColored;
  }

  protected double opacity = 0;

  /**
   * Indique le taux de transparence de la représentation de l'objet Entre 0 et
   * 1 0 = opaque
   * 
   * @return le coefficient de transparence
   */
  public double getOpacity() {
    return this.opacity;
  }

  /**
   * Permet d'indique si l'objet est représenté de manière solide
   */
  protected boolean isSolid = true;

  /**
   * @return Indique si l'objet est représenté de manière solide
   */
  public boolean isSolid() {
    return this.isSolid;
  }

  /**
   * @return Permet de renvoyer la liste des géométries Java3D stockées dans les
   *         différentes représentations
   */
  protected ArrayList<Shape3D> getShapes() {

    ArrayList<Shape3D> shapes = new ArrayList<Shape3D>(1);
    Enumeration<?> enumGroup = this.bGRep.getAllChildren();

    while (enumGroup.hasMoreElements()) {

      Object objTemp = enumGroup.nextElement();
      // Théoriquement la géométrie Java3D n'est rattachée qu'au premier
      // noeud
      if (objTemp instanceof Shape3D) {
        shapes.add((Shape3D) objTemp);
      }
    }

    return shapes;
  }

  @Override
  public Component getRepresentationComponent() {

    JButton jb = new JButton();
    jb.setBackground((this).getColor());
    jb.setHorizontalAlignment(SwingConstants.HORIZONTAL);
    return jb;
  }

}

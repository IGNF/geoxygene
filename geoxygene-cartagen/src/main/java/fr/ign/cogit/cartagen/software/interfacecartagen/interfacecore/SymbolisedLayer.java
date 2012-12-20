/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/**
 * @author julien Gaffuri 5 févr. 2009
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;

/**
 * une couche symbolisee: il s'agit de couple composé d'une couche et d'une
 * symbolisation, pouvant être dessiné dans un panelvisu.
 * 
 * @author julien Gaffuri 5 févr. 2009
 * 
 */
public class SymbolisedLayer {
  final static Logger logger = Logger
      .getLogger(SymbolisedLayer.class.getName());

  /**
   * La couche d'objets a symboliser
   */
  private Layer layer;

  /**
   * @return
   */
  public Layer getLayer() {
    return this.layer;
  }

  /**
   * La symbolisation a appliquer a la couche
   */
  private Symbolisation symbolisation;

  /**
   * @return
   */
  public Symbolisation getSymbolisation() {
    return this.symbolisation;
  }

  /**
   * Le bouton de l'interface (case a cocher) eventuel permettant de controler
   * la visibilite de la couche symbolise
   */
  private AbstractButton visibilityButton = null;

  /**
   * Donne l'etat de visibilite de la couche dans l'interface
   * @return
   */
  public boolean isVisible() {

    // s'il n'y a pas de case a cocher assignee, renvoyer true
    if (this.visibilityButton == null) {
      return true;
    }

    // sinon renvoyer l'etat du bouton
    return this.visibilityButton.isSelected();
  }

  /**
   * @param couche
   * @param symbolisation
   */
  public SymbolisedLayer(Layer layer, Symbolisation symbolisation,
      AbstractButton visibilityButton) {

    // symbolisation
    this.symbolisation = symbolisation;

    // lien avec couche
    this.layer = layer;
    layer.symbolisedLayers.add(this);

    // verifie que le bouton est bien une case a cocher
    if (visibilityButton != null && !(visibilityButton instanceof JCheckBox)
        && !(visibilityButton instanceof JCheckBoxMenuItem)) {
      SymbolisedLayer.logger
          .error("Probleme dans la creation de couche symbolisee: mauvais type de bouton: "
              + visibilityButton.getClass().getSimpleName());
      this.visibilityButton = null;
    } else {
      this.visibilityButton = visibilityButton;
    }
  }

  /**
   * @param couche
   * @param symbolisation
   */
  public SymbolisedLayer(Layer layer, Symbolisation symbolisation) {
    this(layer, symbolisation, null);
  }

  /**
   * constructeur avec symbolisation par defaut
   * @param couche
   */
  public SymbolisedLayer(LoadedLayer layer) {
    this(layer, Symbolisation.defaut(), null);
  }

  /**
   * dessine la couche dans une fenetre avec un certain style
   * @param pv le panneau dans lequel dessiner
   * @param style le style a utiliser
   * @throws InterruptedException
   */
  public void draw(VisuPanel pv) throws InterruptedException {
    // si la couche symbolisee n'est pas a afficher, sortir
    if (!this.isVisible()) {
      return;
    }

    // sinon, afficher les objets du cache de la couche avec la symbolisation
    for (IFeature obj : this.layer.getDisplayCache(pv)) {

      pv.stopDisplayTest();
      this.symbolisation.draw(pv, obj);
    }
  }

}

package fr.ign.cogit.geoxygene.sig3d.semantic;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Group;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

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
 * La carte 3D est visualisée dans une InterfaceCarte3D. Elle permet de gérer
 * les différentes couches notamment : - l'ajout - la suppression - la recherche
 * Attention dans une carte, il ne peut y avoir 2 couches du même nom Carte3D
 * are visualised in a InterfaceCarte3D. It can manage layers
 * 
 */
public class Map3D {

  public static final double EMPRISE_MAX = Math.pow(10, 15);

  /**
   * Renvoi l'emprise de la carte. Un pavé englobant aux dont les faces sont
   * parallèles aux bords de la carte
   * 
   * @return le pavé englobant
   */
  public Box3D getBoundingBox() {
    return this.boundingBox;
  }

  private InterfaceMap3D iMap3D = null;

  // Il s'agit du BranhcGroup supportant les couches
  private BranchGroup BgCarte = null;
  // L'emprise de la carte
  private Box3D boundingBox = null;

  private List<Layer> lLayers = new ArrayList<Layer>();

  private final static Logger logger = Logger.getLogger(Map3D.class.getName());

  /**
   * Crée un objet de carte et l'associe à une interface
   * @param iMap3D l'interface à laquelle est associée la carte
   */
  public Map3D(InterfaceMap3D iMap3D) {
    this.iMap3D = iMap3D;
  }

  /**
   * Ajoute une couche dans la carte. Si le nom de la couche est déjà pris ou si
   * l'ajout n'a pu être effectué, renvoie false
   * 
   * @param layer la couche à ajouter
   * @return indique si l'ajout s'est bien déroulé
   */
  public boolean addLayer(Layer layer) {

    // On ajoute l'affichage à la carte
    if (layer == null) {
      return false;
    }

    String nomcouche = layer.getLayerName();

    // On regarde si une couche de même nom existe déjà
    if (this.getLayer(nomcouche) == null) {

      // On ne peut ajouter la couche que si il n'y a pas de parents
      // (sinon on n'a pas un arbre)
      if (layer.getBranchGroup().getParent() == null) {

        Box3D b = layer.get3DEnvelope();

        if (b == null) {
          return false;
        }

        if (b.getLLDP().getX() < -Map3D.EMPRISE_MAX
            || b.getURDP().getX() > Map3D.EMPRISE_MAX) {
          Map3D.logger.error(Messages.getString("3DGIS.Infinity") + " : X");

          JOptionPane.showMessageDialog(null,
              Messages.getString("3DGIS.Infinity") + " : X",
              Messages.getString("FenetreChargement.Error"),
              JOptionPane.ERROR_MESSAGE);
          return false;
        }

        if (b.getLLDP().getY() < -Map3D.EMPRISE_MAX
            || b.getURDP().getY() > Map3D.EMPRISE_MAX) {
          Map3D.logger.error(Messages.getString("3DGIS.Infinity") + " : Y");

          JOptionPane.showMessageDialog(null,
              Messages.getString("3DGIS.Infinity") + " : Y",
              Messages.getString("FenetreChargement.Error"),
              JOptionPane.ERROR_MESSAGE);
          return false;
        }

        if (b.getLLDP().getZ() < -Map3D.EMPRISE_MAX
            || b.getURDP().getZ() > Map3D.EMPRISE_MAX) {
          Map3D.logger.error(Messages.getString("3DGIS.Infinity") + " : Z " + nomcouche);

          JOptionPane.showMessageDialog(null,
              Messages.getString("3DGIS.Infinity") + " : Z",
              Messages.getString("FenetreChargement.Error"),
              JOptionPane.ERROR_MESSAGE);
          return false;
        }

        if (this.boundingBox == null) {

          this.boundingBox = b;

        } else {

          this.boundingBox = this.boundingBox.union(b);

        }

        if (this.BgCarte == null) {
          this.BgCarte = new BranchGroup();
          this.BgCarte.setCapability(Group.ALLOW_CHILDREN_READ);
          this.BgCarte.setCapability(Group.ALLOW_CHILDREN_EXTEND);
          this.BgCarte.setCapability(Group.ALLOW_CHILDREN_WRITE);

        }
        this.getBgCarte().addChild(layer.getBranchGroup());
        this.iMap3D.addLayerInterface(layer);
        this.lLayers.add(layer);

        return true;
      }

      return false;

    } else {
      return false;

    }
  }

  /**
   * Supprime une couche de la carte (donc de l'affichage et du menu de gestion
   * de couches) Met à jour l'emprise de la carte
   * 
   * @param layerName le nom de la couche à supprimer (si elle n'existe pas, il
   *          ne se passe rien)
   */
  public void removeLayer(String layerName) {
    Layer c = this.getLayer(layerName);
    if (c == null) {
      Map3D.logger.warn(Messages.getString("Carte3D.UnkLayer"));
      return;
    }

    if (c.getBranchGroup().getParent() != null) {

      c.getBranchGroup().detach();
    }
    this.getLayerList().remove(c);

    int nbCouches = this.getLayerList().size();

    if (nbCouches == 0) {

      this.boundingBox = null;
      return;
    }

    Box3D b = this.getLayerList().get(0).get3DEnvelope();

    for (int i = 1; i < nbCouches; i++) {
      b = b.union(this.getLayerList().get(i).get3DEnvelope());

    }

    this.boundingBox = b;

  }

  /**
   * @return la liste des couches dans la carte
   */
  public List<Layer> getLayerList() {

    return this.lLayers;
  }

  /**
   * Permet d'obtenir une couche à partir de son nom
   * 
   * @param layerName le nom de la couche que l'on souhaite retrouver
   * @return la couche de nom "layerName"
   */
  public Layer getLayer(String layerName) {

    int nb = this.lLayers.size();

    if (nb == 0) {

      return null;
    }

    for (int i = 0; i < nb; i++) {

      Layer c = this.lLayers.get(i);

      if (c.getLayerName().equalsIgnoreCase(layerName)) {

        return c;
      }

    }

    return null;
  }

  /**
   * 
   * @return l'interface de carte à laquelle est associée la carte
   */
  public InterfaceMap3D getIMap3D() {
    return this.iMap3D;
  }

  /**
   * 
   * @return le BranchGroup attaché à la carte
   */
  public BranchGroup getBgCarte() {
    return this.BgCarte;
  }

  /**
   * Renvoie le centre de la carte, à savoir le centre du pavé englobant
   * 
   * @return le centre du pavé englobant
   */
  public DirectPosition getCenter() {
    IDirectPosition dp1 = this.boundingBox.getLLDP();
    IDirectPosition dp2 = this.boundingBox.getURDP();

    return new DirectPosition((dp1.getX() + dp2.getX()) / 2,
        (dp1.getY() + dp2.getY()) / 2, (dp1.getZ() + dp2.getZ()) / 2);
  }

  /**
   * Rafraichit la carte en utilisant la méthode refresh sur chacune des
   * couches.
   */
  public void refresh() {
    int nbElem = this.lLayers.size();
    for (int i = 0; i < nbElem; i++) {

      this.lLayers.get(i).refresh();
    }
  }
}

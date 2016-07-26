package fr.ign.cogit.geoxygene.sig3d.gui.table.textures;

import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;

import fr.ign.cogit.geoxygene.sig3d.representation.texture.TextureManager;

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
 * JCheckbox permettant de représenter des textures pour l'utilisation de
 * textures génériques JCheckBox to represent textures
 */
public class GenericTexturesList extends JCheckBox {

  /**
     * 
     */
  private static final long serialVersionUID = -9111553164783291527L;

  GenericTexturesListModel model;

  /**
   * JCheckBox permettant de représenter les textures chargées dans le manager
   */
  public GenericTexturesList() {
    super();
    this.model = new GenericTexturesListModel();
    List<String> lNomsTextures = TextureManager.getlTexturesName();

    JComboBox<String> jcb = new JComboBox<String>();
    jcb.setRenderer(new GenericTexturesListRenderer());
    jcb.setVisible(true);
    jcb.setModel(this.model);

    if (lNomsTextures.size() > 0) {
      jcb.setSelectedIndex(0);
    }

    this.add(jcb);

  }

  // On fait le lien avec le model spécifique
  public GenericTexturesListModel getModelList() {

    return this.model;
  }

}

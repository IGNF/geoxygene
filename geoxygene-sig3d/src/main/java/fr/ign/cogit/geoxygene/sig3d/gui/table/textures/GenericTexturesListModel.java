package fr.ign.cogit.geoxygene.sig3d.gui.table.textures;

import javax.swing.DefaultComboBoxModel;

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
 * Modèle pour la génération de Textures génériques
 * 
 * Model for generation of geometrique texture
 */
public class GenericTexturesListModel extends DefaultComboBoxModel {

  /**
     * 
     */
  private static final long serialVersionUID = -2553934579872802547L;

  public GenericTexturesListModel() {
    super();

  }

  @Override
  public Object getElementAt(int index) {

    if (index > this.getSize() - 1) {
      return null;
    }

    return TextureManager.getlTexturesName().get(index);
  }

  @Override
  public int getSize() {
    // TODO Auto-generated method stub
    return TextureManager.getlTexturesName().size();
  }

  @Override
  public void fireContentsChanged(Object source, int index0, int index1) {
    // TODO Auto-generated method stub
    super.fireContentsChanged(source, index0, index1);
  }

}

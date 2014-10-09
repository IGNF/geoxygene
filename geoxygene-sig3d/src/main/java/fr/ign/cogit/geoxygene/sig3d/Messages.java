package fr.ign.cogit.geoxygene.sig3d;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 *          Classe de gestion des textes. Class managing texts
 * 
 */
public class Messages {
  /**
   * L'emplacement des fichiers ".properties"
   */
  private static final String BUNDLE_NAME = "fr.ign.cogit.geoxygene.sig3d.messages"; //$NON-NLS-1$

  private static ResourceBundle RESOURCE_BUNDLE = null;

  /**
   * Constructeur vide
   */
  private Messages() {
    super();
  }

  /**
   * Permet d'obtenir un texte à partir d'une clef
   * 
   * @param key la clef que l'on utilise pour récupérer un texte
   * @return le texte associé à la clef
   */
  public static String getString(String key) {
    if (RESOURCE_BUNDLE == null) {
      try {
        RESOURCE_BUNDLE = ResourceBundle.getBundle(Messages.BUNDLE_NAME);
      } catch (Exception e) {
        return '!' + key + '!';
      }
    }

    try {
      return Messages.RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}

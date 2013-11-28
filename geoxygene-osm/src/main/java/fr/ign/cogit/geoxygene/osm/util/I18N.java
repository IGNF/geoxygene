/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.osm.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * OSM module GUI Internationalisation class. Uses the default locale.
 * <p>
 * Classe d'Internationalisation des interfaces du module OSM. Utilise la locale
 * par défaut.
 * 
 * @author Julien Perret
 */
public final class I18N {
  /**
   * Private Bundle name pointing to where the language files are.
   */
  private static final String BUNDLE_NAME = "language/osm"; //$NON-NLS-1$

  /**
   * Private resource Bundle using the bundle name and default locale.
   * @see #BUNDLE_NAME
   * @see #getString(String)
   */
  private static ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(
      I18N.BUNDLE_NAME, Locale.getDefault(), I18N.class.getClassLoader());

  /**
   * Private Default Constructor.
   */
  private I18N() {
  }

  /**
   * @param key string identifier of the internationalised test
   * @return the internationalised string corresponding to the given key
   */
  public static String getString(final String key) {
    try {
      return I18N.RESOURCE_BUNDLE.getString(key);
    } catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }

  public static void changeLocale(Locale loc) {
    RESOURCE_BUNDLE = ResourceBundle.getBundle(I18N.BUNDLE_NAME, loc);
  }
}

package fr.ign.cogit.geoxygene.util;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * GeOxygene Internationalisation class. Uses the default locale.
 * <p>
 * Classe d'Internationalisation de GeOxygene. Utilise la locale par défaut.
 * 
 * @author Julien Perret
 */
public final class I18N {
  /**
   * Private Bundle name pointing to where the language files are.
   */
  private static final String BUNDLE_NAME = "language/geoxygene-util"; //$NON-NLS-1$

  /**
   * Private resource Bundle using the bundle name and default locale.
   * @see #BUNDLE_NAME
   * @see #getString(String)
   */
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
      .getBundle(I18N.BUNDLE_NAME, Locale.getDefault());

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
}

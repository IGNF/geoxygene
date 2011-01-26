/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene;

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
  private static final String BUNDLE_NAME = "language/geoxygene"; //$NON-NLS-1$

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

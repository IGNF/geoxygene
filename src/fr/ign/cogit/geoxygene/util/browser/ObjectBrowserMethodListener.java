/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Method;

/**
 * Cette classe fournit l'implémentation de l'écouteur d'évènement pour les
 * objets cliquables de type méthode. <BR/>
 * Elle permet l'invocation générique des méthodes, sans paramètres, en
 * utilisant le package reflection du J2SDK.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class ObjectBrowserMethodListener implements ActionListener {

  /** Objet porteur de la méthode qui doit être invoquée. */
  private Object obj;
  /** méthode qui doit être invoquée. */
  private Method method;
  /** Nom de la méthode qui doit être invoquée. */
  private String methodName;

  /**
   * Constructeur principal de l'écouteur d'évènement
   * ObjectBrowserMethodListener.
   * 
   * @param obj l'objet porteur de la méthode qui doit être invoquée.
   * @param method la méthode qui doit être invoquée.
   */
  public ObjectBrowserMethodListener(Object obj, Method method) {
    this.obj = obj;
    this.method = method;
    this.methodName = method.getName();
  }

  /**
   * Redéfinition de la méthode actionPerformed() fournie par l'interface
   * ActionListener, afin de déclencher l'affichage de l'argument de retour de
   * la méthode (instance de la classe ObjectBrowserPrimitiveFrame).
   */
  @SuppressWarnings("unused")
  public void actionPerformed(ActionEvent e) {

    Object[] nulObjArray = {};
    Class<?> methodReturnType = this.method.getReturnType();
    String returnedStringValue;

    if ((methodReturnType.getName() == "java.lang.String") //$NON-NLS-1$
        || (methodReturnType.isPrimitive())) {
      try {
        try {
          returnedStringValue = (this.method.invoke(this.obj, nulObjArray))
              .toString();
          ObjectBrowserPrimitiveFrame result = new ObjectBrowserPrimitiveFrame(
              this.methodName, returnedStringValue);
        } catch (NullPointerException npex) {
          ObjectBrowserNullPointerFrame nullFrame = new ObjectBrowserNullPointerFrame();
        }
      } catch (Exception ex) {
        ObjectBrowserIllegalAccessFrame illegalAccessFrame = new ObjectBrowserIllegalAccessFrame();
        // ex.printStackTrace();
      }
    } else {
      try {
        try {
          ObjectBrowser.browse(this.method.invoke(this.obj, nulObjArray));
        } catch (NullPointerException npex) {
          ObjectBrowserNullPointerFrame nullFrame = new ObjectBrowserNullPointerFrame();
        }
      } catch (Exception ex) {
        ObjectBrowserIllegalAccessFrame illegalAccessFrame = new ObjectBrowserIllegalAccessFrame();
        // ex.printStackTrace();
      }
    }

  }
}

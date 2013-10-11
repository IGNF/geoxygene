/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.geoxygene.function.ui;

import java.awt.Component;
import java.lang.reflect.Constructor;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import fr.ign.cogit.geoxygene.function.Function1D;

/**
 * @author JeT
 * Describes how to represent a function into a CellList
 */
public class FunctionClassRenderer extends JLabel implements ListCellRenderer {

  private static final long serialVersionUID = 3339214365714832883L;

  @Override
  public Component getListCellRendererComponent(final JList list, final Object object, final int index, final boolean isSelected,
      final boolean cellHasFocus) {
    StringBuilder str = new StringBuilder();
    Class<?> clazz = (Class<?>) object;
    String shortClassName = clazz.getSimpleName();
    String fullClassName = clazz.getName();
    str.append(shortClassName);
    Constructor<?>[] constructors = ((Class<?>) clazz).getConstructors();
    for (Constructor<?> constructor : constructors) {
      str.append("(");
      boolean one = true;
      for (Class<?> pClazz : constructor.getParameterTypes()) {
        if (!one) {
          str.append(", ");
        }
        str.append(pClazz.getSimpleName());
        one = false;
      }
      str.append(")");
    }
    Function1D instance;
    try {
      instance = (Function1D) clazz.newInstance();
      str.append(" " + instance.help());
    } catch (InstantiationException e) {
      // no default constructor
    } catch (IllegalAccessException e) {
      // cannot create a new class
    }
    this.setText(str.toString());
    return this;
  }
}

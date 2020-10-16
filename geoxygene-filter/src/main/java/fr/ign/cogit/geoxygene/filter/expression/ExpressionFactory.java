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

package fr.ign.cogit.geoxygene.filter.expression;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Julien Perret
 */
public class ExpressionFactory {
  private static Logger logger = LogManager.getLogger(ExpressionFactory.class
      .getName());

  /**
   * FIXME voir comment créer des fonctions
   * @param type
   * @return a new Expression corresponding to the given type
   */
  public static Expression createExpression(String type) {
    if (type.equalsIgnoreCase("add")) {
      return new Add();
    }
    if (type.equalsIgnoreCase("sub")) {
      return new Subtract();
    }
    if (type.equalsIgnoreCase("mul")) {
      return new Multiply();
    }
    if (type.equalsIgnoreCase("div")) {
      return new Divide();
    }
    if (type.equalsIgnoreCase("literal")) {
      return new Literal();
    }
    if (type.equalsIgnoreCase("propertyName")) {
      return new PropertyName();
    }
    return null;
  }

  /**
   * @param name
   * @return a new function corresponding to the given name
   */
  public static Function createFunction(String name) {
    String nomClasse = "fr.ign.cogit.geoxygene.filter.function." //$NON-NLS-1$
        + name.substring(0, 1).toUpperCase() + name.substring(1);
    try {
      Class<?> classe = Class.forName(nomClasse);
      return (Function) classe.newInstance();
    } catch (ClassNotFoundException e) {
      ExpressionFactory.logger
          .error("La classe " + nomClasse + " n'existe pas");
    } catch (InstantiationException e) {
      ExpressionFactory.logger
          .error("Impossible d'instancier un objet de la classe " + nomClasse);
    } catch (IllegalAccessException e) {
      ExpressionFactory.logger.error("Constructeur de la classe " + nomClasse
          + " inaccessible");
    }
    return null;
  }
}

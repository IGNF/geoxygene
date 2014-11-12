/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.evidence;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

import fr.ign.cogit.geoxygene.matching.dst.FactoryType;
import fr.ign.cogit.geoxygene.matching.dst.sources.Source;

/**
 * A class to load sources. It should be made more flexible.
 * <p>
 * Classe fournissant les méthodes statiques permettant la création des fonctions de masse de
 * croyance définies pour un type de donnée choisi.
 * @author Bertrand Dumenieu
 */
public class CriteriaLoader {

  /**
   * Définition des critères associés à un type de donnée (pt, ligne,etc). Cette
   * classe a pour finalité d'être remplacée par quelque chose de plus souple.
   * @return
   */
  public static <F, H extends Hypothesis> Collection<Source<F, H>> load(FactoryType type) {
    String path = CriteriaLoader.class.getPackage().toString();
    path = path.substring(0, path.lastIndexOf('.'));
    path += ".sources.";
    path = path.substring(path.lastIndexOf(' ') + 1);
    if (type == FactoryType.LINEAR) {
      path += "linear";
    } else
      if (type == FactoryType.SURFACE) {
        path += "surface";
      } else
        if (type == FactoryType.PUNCTUAL) {
          path += "point";
        }
    return CriteriaLoader.load(path);
  }

  /**
   * @param path
   * @return
   */
  @SuppressWarnings("unchecked")
  private static <F, H extends Hypothesis> Collection<Source<F, H>> load(String path) {
    Collection<Source<F, H>> loadedSources = new HashSet<Source<F, H>>();
    String realPath = path;
    realPath = path.replace('.', File.separatorChar);
    realPath = File.separatorChar + realPath;
    File packagefile = new File(CriteriaLoader.class.getResource(realPath).getFile());
    for (File file : packagefile.listFiles()) {
      String classpath = path + '.'
          + file.getName().substring(0, file.getName().lastIndexOf(".class"));
      Class<?> clazz = null;
      try {
        clazz = Class.forName(classpath);
        loadedSources.add((Source<F, H>) clazz.newInstance());
      } catch (ClassNotFoundException e) {
        System.out.println("Loading of class " + file.getName() + " failed : no class found");
        e.printStackTrace();
      } catch (InstantiationException e) {
        System.out.println("Loading of class " + file.getName() + " failed : instanciation failed");
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        System.out.println("Loading of class " + file.getName()
            + " failed : cannot access the class");
        e.printStackTrace();
      } catch (ClassCastException e) {
        System.out.println("cannot cast" + ((clazz == null) ? "null" : clazz.getName())
            + " to Source");
        continue;
      }
    }
    return loadedSources;
  }
}

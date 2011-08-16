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

package fr.ign.cogit.geoxygene.contrib.appariement.stockageLiens;

import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.DataSet;

/**
 * Classe permettant le stockage et le chargement d'ensemble de liens résultants
 * d'appariement
 * 
 * @author Eric Grosso - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class StockageLiens {

  /**
   * Méthode permettant de stocker automatiquement un ensemble de liens
   * d'appariement et les liens correspondants dans le SGBD
   * @param ensembleLiens
   */
  public static void stockageDesLiens(EnsembleDeLiens ensembleLiens, int rouge,
      int vert, int bleu) {
    DataSet.db = GeodatabaseOjbFactory.newInstance();
    DataSet.db.begin();
    EnsembleDeLiensSGBD liensSGBD = new EnsembleDeLiensSGBD(true);
    liensSGBD.conversionEnsembleLiensVersSGBD(ensembleLiens, rouge, vert, bleu);
    DataSet.db.commit();
  }

  /**
   * Méthode permettant de déstocker automatiquement un ensemble de liens
   * d'appariement SGBD et les liens correspondants, en renvoyant l'ensemble de
   * liens correspondants
   * @param ensembleLiensSGBD
   */
  public static EnsembleDeLiens destockageDesLiens(
      EnsembleDeLiensSGBD ensembleLiensSGBD) {
    EnsembleDeLiens liens = new EnsembleDeLiens();
    ensembleLiensSGBD.conversionSGBDVersEnsembleLiens(liens);
    return liens;
  }

}

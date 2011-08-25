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

package fr.ign.cogit.geoxygene.spatial.geomprim;

import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IShell;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSurface;

/**
 * Représente un composant de GM_SolidBoundary. Un GM_Shell est un
 * GM_CompositeSurface fermé. TODO à implémenter.
 * 
 * @author Thierry Badard
 * @author Arnaud Braun
 */

public class GM_Shell extends GM_CompositeSurface implements IShell {
  /**
   * Permet de créer un Shell à partir d'une liste de face ATTENTION : Ne permet
   * pas de vérifier qu'il s'agit d'un objet fermé
   * @param lOS la liste des facettes composant la surface
   */
  public GM_Shell(List<IOrientableSurface> lOS) {
    super();
    this.setListeFaces(lOS);
  }
  @Override
  public boolean isSimple() {
    return true;
  }
  @Override
  public List<IOrientableSurface> getlisteFaces() {
    return this.getGenerator();
  }
  /**
   * Renseigne la liste des facettes composant la surface
   * @param lOS la liste des facettes composant la surface
   */
  private void setListeFaces(List<IOrientableSurface> lOS) {
    this.generator.clear();
    this.generator.addAll(lOS);
  }
}

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

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IShell;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ISolidBoundary;

/**
 * NON UTILISE. Représente la frontière d'un GM_Solid. Un GM_SolidBoundary est
 * constitué de 0 ou 1 shell extérieur et d'une liste de shells intérieurs pour
 * éventuellement représenter les solides à trous. (le cas de 0 shell extérieur
 * est prévu par le modèle mais je n'ai pas compris pourquoi).
 * <P>
 * A revoir : redéfinir les constructeurs, et tester.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_SolidBoundary extends GM_PrimitiveBoundary implements
    ISolidBoundary {
  /** Shell extérieur. */
  protected IShell exterior;

  @Override
  public IShell getExterior() {
    return this.exterior;
  }

  /** Affecte un shell extérieur. */
  protected void setExterior(IShell value) {
    this.exterior = value;
  }

  @Override
  public int sizeExterior() {
    if (this.exterior == null) {
      return 0;
    }
    return 1;
  }

  /** Liste des shells intérieurs. */
  protected List<IShell> interior = new ArrayList<IShell>(0);

  @Override
  public List<IShell> getInterior() {
    return this.interior;
  }

  @Override
  public IShell getInterior(int i) {
    return this.interior.get(i);
  }

  /** Affecte une valeur au shell intérieur de rang i. */
  protected void setInterior(int i, IShell value) {
    this.interior.set(i, value);
  }

  /** Ajoute un shell intérieur en fin de liste. */
  protected void addInterior(IShell value) {
    this.interior.add(value);
  }

  /** Ajoute un shell intérieur au rang i. */
  protected void addInterior(int i, IShell value) {
    this.interior.add(i, value);
  }

  /** Efface le shell intérieur de valeur "value". */
  protected void removeInterior(IShell value) {
    this.interior.remove(value);
  }

  /** Efface le shell intérieur de rang i. */
  protected void removeInterior(int i) {
    this.interior.remove(i);
  }

  @Override
  public int sizeInterior() {
    return this.interior.size();
  }

  /**
   * Permet de créer un GM_SolidBoundary ne possédant pas de trous
   */
  public GM_SolidBoundary(List<? extends IOrientableSurface> lOS) {
    GM_Shell inShell = new GM_Shell(lOS);
    this.exterior = inShell;
  }
}

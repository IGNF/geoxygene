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

package fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.LienReseaux;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;

/**
 * Face d'un graphe à apparier. La notion de face est très peu utilisée pour
 * l'appariement de réseaux, mais elle l'est néanmoins pour des cas
 * particuliers, comme la gestion des rond-points dans le réseau routier.
 * 
 * @author Mustiere - IGN / Laboratoire COGIT
 * @version 1.0
 * 
 */

public class FaceApp extends Face {

  /** Evaluation du résultat de l'appariement sur la face. */
  private String resultatAppariement;

  public String getResultatAppariement() {
    return this.resultatAppariement;
  }

  public void setResultatAppariement(String resultat) {
    this.resultatAppariement = resultat;
  }

  /** Liens qui référencent l'objet apparié */
  private List<LienReseaux> liens = new ArrayList<LienReseaux>();

  public List<LienReseaux> getLiens() {
    return this.liens;
  }

  public void setLiens(List<LienReseaux> liens) {
    this.liens = liens;
  }

  public void addLiens(LienReseaux liensReseaux) {
    this.liens.add(liensReseaux);
  }

  /** Renvoie les liens de l'objet qui appartiennent à la liste liensPertinents */
  public List<LienReseaux> getLiens(List<LienReseaux> liensPertinents) {
    List<LienReseaux> listeTmp = new ArrayList<LienReseaux>(this.getLiens());
    listeTmp.retainAll(liensPertinents);
    return listeTmp;
  }

}

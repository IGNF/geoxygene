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

package fr.ign.cogit.geoxygene.spatial.toporoot;

import fr.ign.cogit.geoxygene.spatial.topoprim.TP_DirectedEdge;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_DirectedFace;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_DirectedNode;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Edge;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Face;
import fr.ign.cogit.geoxygene.spatial.topoprim.TP_Node;

/**
 * Classe mere abstraite pour les objets topologiques. Un TP_Object est soit un
 * TP_Primitive, soit un TP_Complex. On appelle id l'identifiant topologique
 * qu'on retrouve dans la table TP_Object du SGBD.
 * <P>
 * Tout ce qui est relatif aux TP_Complex n'est pas implemente.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */
abstract public class TP_Object implements Cloneable {
  // / pour le mapping ///////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////
  /** Constructeur necessaire pour affecter le type de la sous-classe concrete. */
  public TP_Object() {
    this.setClasstype(this.getClass().getName());
    this.setOjbConcreteClass(this.getClass().getName());
  }

  // castor
  protected String classtype;

  public String getClasstype() {
    return this.classtype;
  }

  public void setClasstype(String Classtype) {
    this.classtype = Classtype;
  }

  // ojb
  protected String ojbConcreteClass;

  public String getOjbConcreteClass() {
    return this.ojbConcreteClass;
  }

  public void setOjbConcreteClass(String OjbConcreteClass) {
    this.ojbConcreteClass = OjbConcreteClass;
  }

  // //////////////////////////////////////////////////////////////////////
  // / identifiant ////////////////////////////////////////////////////////
  // //////////////////////////////////////////////////////////////////////
  /** Identifiant topologique. */
  protected int id;

  /** Renvoie l'identifiant topologique. */
  public int getId() {
    return this.id;
  }

  /**
   * Affecte une valeur a l'identifiant topologique. Assure la coherence avec
   * featureID. OBSOLETE
   * @param Id
   */
  public void setId(int Id) {
    this.id = Id;
  }

  /**
   * Dimension topologique de l'objet (0 pour node, 1 pour edge, 2 pour face, 3
   * pour solid).
   * @return dimension
   */
  public int dimension() {
    if (this instanceof TP_Node || this instanceof TP_DirectedNode) {
      return 0;
    }
    if (this instanceof TP_Edge || this instanceof TP_DirectedEdge) {
      return 1;
    }
    if (this instanceof TP_Face || this instanceof TP_DirectedFace) {
      return 2;
    }
    // else if (this instanceof TP_Solid
    // || this instanceof TP_DirectedSolid) return 3;
    System.out
        .println("this n'est pas une primitive topologique. La fonction dimension renvoie -1."); //$NON-NLS-1$
    return -1;
  }

  // /////////////////////////////////////////////////////////////////////
  // / methodes de la norme supprimees pour simplification
  // / ces methodes sont definies dans les sous-classes avec un bon typage
  // en sortie
  // ////////////////////////////////////////////////////////////////////////
  /**
   * Set de TP_DirectedTopo structures en TP_Boundary, qui representent la
   * frontiere de self. Si le TP_Object est associe a un GM_Object, sa frontiere
   * doit etre coherente en terme d'orientation avec celle du GM_Object. Les
   * TP_Boundary sont des TP_Expression et ont donc une forme polynomiale
   * (exemple : +edge1-edge2+edge3, ...)
   */
  // abstract public TP_Boundary boundary() ;

  /** Tous les TP_DirectedTopo qui ont self pour fronti?re. */
  // abstract public List coBoundary() ;

  // ////////////////////////////////////////////////////////////////////////
  // / non implemente, relatif aux TP_Complex ///////////////////////////////
  // ////////////////////////////////////////////////////////////////////////
  /**
   * Set de TP_Primitive constituant l'intrrieur de self (c'est-a-dire sans la
   * frontiere) dans le complexe maximal de l'objet. Pour un TP_Primitive, le
   * resultat sera self. Pour un complexe, le resultat sera les primitives de ce
   * complexe qui ne sont pas sur la frontiere du complexe. Le resultat est
   * equivalent a l'interieur de la realisation geometrique de self.
   */
  // public List interior() {
  // return null;
  // }

  /**
   * Union de l'interieur et de la frontiere de self.
   * closure()=interior().union(boundary())
   */
  // public List closure() {
  // return null;
  // }

  /**
   * Set de TP_Primitive constituant exterieur de self dans le complexe maximal
   * de l'objet : toutes les TP_Primitives du complexe maximal qui ne sont pas a
   * l'interieur, ou la frontiere du TP_Object.
   */
  // public List exterior() {
  // return null;
  // }

  /** Le TP_Complex maximal qui contient self. */
  // public TP_Complex maximalComplex() {
  // return null;
  // }

  // ///////////////////////////////////////////////////////////////////////////////////
  // / equals
  // //////////////////////////////////////////////////////////////////////////
  // ///////////////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie true si les 2 objets appartiennent a la meme classe et ont le meme
   * identifiant (champs id). ATTENTION : si aucun id n'a encore ete affecte aux
   * objets, alors les deux id vaudront 0 et le test renverra true.
   */
  public boolean equalsID(java.lang.Object obj) {
    if (obj == null || !this.getClass().equals(obj.getClass())) {
      return false;
    }
    TP_Object o = (TP_Object) obj;
    return (this.id == o.id);
  }

  // /////////////////////////////////////////////////////////////////////
  // / clone /////////////////////////////////////////////////////////////
  // /////////////////////////////////////////////////////////////////////
  /** Clone l'objet. */
  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}

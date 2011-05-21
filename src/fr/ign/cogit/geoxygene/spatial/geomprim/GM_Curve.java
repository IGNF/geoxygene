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

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_CurveSegment;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;

/**
 * Courbe. L'orientation vaut nécessairement +1, la primitive est self. Une
 * courbe est composée de un ou plusieurs segments de courbe. Chaque segment à
 * l'intérieur d'une courbe peut être défini avec une interpolation différente.
 * Dans la pratique nous n'utiliserons a priori que des polylignes comme
 * segment(GM_LineString).
 * <P>
 * Modification de la norme suite au retour d'utilisation : on fait hériter
 * GM_CurveSegment de GM_Curve. Du coup, on n'implémente plus l'interface
 * GM_GenericCurve.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class GM_Curve extends GM_OrientableCurve
/* implements GM_GenericCurve */{
  static Logger logger = Logger.getLogger(GM_Curve.class.getName());

  // ////////////////////////////////////////////////////////////////////////////////
  // Attribut "segment" et méthodes pour le traiter
  // ////////////////////////////////////////////////////////////////////////////////
  /** Liste de tous les segment de courbe (GM_CurveSegment) constituant self. */
  protected List<GM_CurveSegment> segment;

  /** Renvoie la liste des segments. */
  public List<GM_CurveSegment> getSegment() {
    return this.segment;
  }

  /** Renvoie le segment de rang i */
  public GM_CurveSegment getSegment(int i) {
    if ((GM_CurveSegment.class).isAssignableFrom(this.getClass())) {
      if (i != 0) {
        GM_Curve.logger
            .error("Recherche d'un segment avec i<>0 alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme"); //$NON-NLS-1$
        return null;
      }
      return this.segment.get(i);
    }
    return this.segment.get(i);
  }

  /** Affecte un segment au i-ème rang de la liste */
  public void setSegment(int i, GM_CurveSegment value) {
    if ((GM_CurveSegment.class).isAssignableFrom(this.getClass())) {
      if (i != 0) {
        GM_Curve.logger
            .error("Affection d'un segment avec i<>0 alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.segment.set(i, value);
      }
    } else {
      this.segment.set(i, value);
    }
  }

  /** Ajoute un segment en fin de liste sans vérifier la continuité du chaînage. */
  public void addSegment(GM_CurveSegment value) {
    if ((GM_CurveSegment.class).isAssignableFrom(this.getClass())) {
      if (this.sizeSegment() > 0) {
        GM_Curve.logger
            .error("Ajout d'un segment alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.segment.add(value);
      }
    } else {
      this.segment.add(value);
    }
  }

  /**
   * A TESTER. Ajoute un segment en fin de liste en vérifiant la continuité du
   * chaînage. Capte une exception en cas de problème. Nécessité de passer une
   * tolérance en paramètre.
   */
  public void addSegment(GM_CurveSegment value, double tolerance)
      throws Exception {
    if ((GM_CurveSegment.class).isAssignableFrom(this.getClass())) {
      if (this.sizeSegment() > 0) {
        GM_Curve.logger
            .error("Ajout d'un segment alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.segment.add(value);
      }
    } else {
      if (this.sizeSegment() == 0) {
        this.segment.add(value);
      } else {
        int n = this.sizeSegment();
        GM_CurveSegment lastSegment = this.getSegment(n - 1);
        if (value.startPoint().equals(lastSegment.endPoint(), tolerance)) {
          this.segment.add(value);
        } else {
          throw new Exception(
              "Rupture de chaînage avec le segment passée en paramètre"); //$NON-NLS-1$
        }
      }
    }
  }

  /**
   * A TESTER. Ajoute un segment en fin de liste en vérifiant la continuité du
   * chaînage, et en retournant le segment si necessaire. Capte une exception en
   * cas de problème. Nécessité de passer une tolérance en paramètre.
   */
  public void addSegmentTry(GM_CurveSegment value, double tolerance)
      throws Exception {
    if ((GM_CurveSegment.class).isAssignableFrom(this.getClass())) {
      if (this.sizeSegment() > 0) {
        GM_Curve.logger
            .error("Ajout d'un segment alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.segment.add(value);
      }
    } else {
      try {
        this.addSegment(value, tolerance);
      } catch (Exception e1) {
        try {
          this.addSegment(value.reverse(), tolerance);
        } catch (Exception e2) {
          throw new Exception(
              "Rupture de chaînage avec le segment passée en paramètre(après avoir essayé de le retourner)."); //$NON-NLS-1$
        }
      }
    }
  }

  /**
   * Ajoute un segment au i-ème rang de la liste, sans vérifier la continuité du
   * chaînage.
   */
  public void addSegment(int i, GM_CurveSegment value) {
    if ((GM_CurveSegment.class).isAssignableFrom(this.getClass())) {
      if (i != 0) {
        GM_Curve.logger
            .error("Ajout d'un segment avec i<>0 alors qu'un GM_CurveSegment ne contient qu'un segment qui est lui-meme. La méthode ne fait rien."); //$NON-NLS-1$
      } else {
        this.segment.add(value);
      }
    } else {
      this.segment.add(i, value);
    }
  }

  /** Efface de la liste le (ou les) segment passé en paramètre */
  public void removeSegment(GM_CurveSegment value) {
    if ((GM_CurveSegment.class).isAssignableFrom(this.getClass())) {
      GM_Curve.logger
          .error("removeSegment() : Ne fait rien car un GM_CurveSegment ne contient qu'un segment qui est lui-meme."); //$NON-NLS-1$
    } else {
      this.segment.remove(value);
    }
  }

  /** Efface le i-ème segment de la liste */
  public void removeSegment(int i) {
    if ((GM_CurveSegment.class).isAssignableFrom(this.getClass())) {
      GM_Curve.logger
          .error("removeSegment() : Ne fait rien car un GM_CurveSegment ne contient qu'un segment qui est lui-meme."); //$NON-NLS-1$
    } else {
      this.segment.remove(i);
    }
  }

  /** Renvoie le nombre de segment */
  public int sizeSegment() {
    return this.segment.size();
  }

  public void clearSegments() {
    this.segment.clear();
  }

  /**
   * A TESTER. Vérifie le chaînage des segments. renvoie TRUE s'ils sont
   * chaînés, FALSE sinon. Nécessité de définir une tolérance.
   */
  public boolean validate(double tolerance) {
    if (this.sizeSegment() <= 1) {
      return true;
    }
    int n = this.sizeSegment();
    for (int i = 0; i < n - 1; i++) {
      GM_CurveSegment segment1 = this.getSegment(i);
      GM_CurveSegment segment2 = this.getSegment(i + 1);
      if (!(segment2.startPoint().equals(segment1.endPoint(), tolerance))) {
        return false;
      }
    }
    return true;
  }

  // ////////////////////////////////////////////////////////////////////////////////
  // Constructeurs
  // /////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////
  /** Constructeur par défaut */
  public GM_Curve() {
    this.segment = new ArrayList<GM_CurveSegment>(0);
    this.orientation = +1;
    this.primitive = this;
    /*
    this.proxy[0] = this;
    GM_OrientableCurve proxy1 = new GM_OrientableCurve();
    proxy1.orientation = -1;
    proxy1.proxy[0] = this;
    proxy1.proxy[1] = proxy1;
    proxy1.primitive = new GM_Curve(this);
    this.proxy[1] = proxy1;
    */
  }

  /** Constructeur à partir d'un et d'un seul GM_CurveSegment */
  public GM_Curve(GM_CurveSegment C) {
    this();
    this.segment.add(C);
  }

  /**
   * Usage interne. Utilisé en interne (dans les constructeurs publics) pour
   * construire la courbe opposé, qui est la primitive de proxy[1]. On définit
   * ici les références nécessaires. Le but est de retrouver la propriete :
   * curve.getNegative().getPrimitive().getNegative().getPrimitive() = curve.
   * Les segment de la courbe sont calcule en dynamique lors de l'appel a la
   * methode getNegative().
   */
  public GM_Curve(GM_Curve curve) {
    this.segment = new ArrayList<GM_CurveSegment>(0);
    this.orientation = +1;
    this.primitive = this;
    /*
    this.proxy[0] = this;
    GM_OrientableCurve proxy1 = new GM_OrientableCurve();
    proxy1.orientation = -1;
    proxy1.proxy[0] = this;
    proxy1.proxy[1] = proxy1;
    proxy1.primitive = curve;
    this.proxy[1] = proxy1;
     */
  }

  // ////////////////////////////////////////////////////////////////////////////////
  // Implémentation de GM_GenericCurve
  // /////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////
  /**
   * Retourne le DirectPosition du premier point. Différent de l'opérateur
   * "boundary" car renvoie la valeur du point et non pas l'objet géométrique
   * représentatif. Méthode d'implémentation de l'interface GM_GenericCurve.
   */
  public DirectPosition startPoint() {
    return this.getSegment(0).coord().get(0);
  }

  /**
   * Retourne le DirectPosition du dernier point. Différent de l'opérateur
   * "boundary" car renvoie la valeur du point et non pas l'objet géométrique
   * représentatif. Méthode d'implémentation de l'interface GM_GenericCurve.
   */
  public DirectPosition endPoint() {
    GM_CurveSegment lastSegment = this.getSegment(this.sizeSegment() - 1);
    DirectPositionList pointArray = lastSegment.coord();
    return pointArray.get(pointArray.size() - 1);
  }

  /**
   * NON IMPLEMENTE - A FAIRE. Renvoie un point à l'abcsisse curviligne s.
   */
  /*
   * public DirectPosition param(double s) { return null; }
   */

  /**
   * NON IMPLEMENTE. Vecteur tangent a la courbe, à l'abscisse curviligne passée
   * en paramètre. Le vecteur résultat est normé.
   */
  /*
   * public Vecteur tangent(double s) { return null; }
   */

  /**
   * NON IMPLEMENTE. Renvoie 0.0 . Méthode d'implémentation de l'interface
   * GM_GenericCurve.
   */
  /*
   * public double startParam() { return 0.0; }
   */

  /**
   * NON IMPLEMENTE. Longueur de la courbe pour une GM_Curve.
   */
  /*
   * double endParam() { return 0.0; }
   */

  /**
   * NON IMPLEMENTE. Renvoie le paramètre au point P (le paramètre étant a
   * priori la distance). Si P n'est pas sur la courbe, on cherche alors pour le
   * calcul le point le plus proche de P sur la courbe (qui est aussi renvoyé en
   * résultat). On renvoie en général une seule distance, sauf si la courbe
   * n'est pas simple.
   */
  /*
   * List paramForPoint(DirectPosition P) { return null; }
   */

  /**
   * NON IMPLEMENTE. Représentation alternative d'une courbe comme l'image
   * continue d'un intervalle de réels, sans imposer que cette paramétrisation
   * représente la longueur de la courbe, et sans imposer de restrictions entre
   * la courbe et ses segments. Utilité : pour les courbes paramétrées, pour
   * construire une surface paramétrée.
   */
  /*
   * DirectPosition constrParam(double cp) { return null; }
   */

  /**
   * NON IMPLEMENTE. Paramètre au startPoint pour une courbe paramétrée,
   * c'est-à-dire : constrParam(startConstrParam())=startPoint(). Méthode
   * d'implémentation de l'interface GM_GenericCurve. NON IMPLEMENTE
   */
  /*
   * double startConstrParam() { return 0.0; }
   */

  /**
   * NON IMPLEMENTE. Paramètre au endPoint pour une courbe paramétrée,
   * c'est-à-dire : constrParam(endConstrParam())=endPoint().
   */
  /*
   * double endConstrParam() { return 0.0; }
   */

  /**
   * Longueur totale de la courbe. (code au niveau de GM_Object)
   */
  /*
   * public double length() { System.out.println("appel ##"); return
   * SpatialQuery.length(this); }
   */

  /**
   * NON IMPLEMENTE. Longueur entre 2 points.
   */
  /*
   * public double length(GM_Position p1, GM_Position p2) { return 0.0; }
   */

  /**
   * NON IMPLEMENTE. Longueur d'une courbe paramétrée "entre 2 réels".
   */
  /*
   * double length(double cparam1, double cparam2) { return 0.0; }
   */

  /**
   * Approximation linéaire d'une courbe avec les points de contrôle. Elimine
   * les points doublons consécutifs (qui apparaissent quand la courbe est
   * composée de plusieurs segments).
   * <P>
   * Le paramètre spacing indique la distance maximum entre 2 points de contrôle
   * ; le paramètre offset indique la distance maximum entre la polyligne
   * générée et la courbe originale. Si ces 2 paramètres sont à 0, alors aucune
   * contrainte n'est imposée. Dans l'IMPLEMENTATION ACTUELLE : on impose que
   * ces paramètres soient à 0.
   * <P>
   * Le paramètre tolérance est nécessaire pour éliminer les doublons. On peut
   * passer 0.0.
   * <P>
   * Méthode d'implémentation de l'interface GM_GenericCurve.
   */
  // Dans la norme, les paramètres spacing et offset sont de type Distance.
  // Dans la norme, il n'y a pas de paramètre tolérance.
  public GM_LineString asLineString(double spacing, double offset,
      double tolerance) {
    GM_LineString theLineString = null;
    if ((spacing != 0.0) || (offset != 0.0)) {
      GM_Curve.logger
          .error("GM_Curve::asLineString() : Spacing et Offset ne sont pas implémentés. Passer (0.0, 0.0, tolerance) en paramètres"); //$NON-NLS-1$
      return null;
    }
    theLineString = new GM_LineString();
    synchronized (this.segment) {
      for (GM_CurveSegment theSegment : this.segment) {
        DirectPositionList aListOfPoints = theSegment.coord();
        for (DirectPosition pt1 : aListOfPoints) {
          if (theLineString.sizeControlPoint() > 0) {
            DirectPosition pt2 = theLineString.getControlPoint().get(
                theLineString.getControlPoint().size() - 1);
            if (!pt1.equals(pt2, tolerance)) {
              theLineString.getControlPoint().add(pt1);
            }
          } else {
            theLineString.getControlPoint().add(pt1);
          }
        }
      }
    }
    return theLineString;
  }

  // ////////////////////////////////////////////////////////////////////////////////
  // Méthodes d'accès aux coordonnées
  // //////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////
  /** Méthode pour afficher les coordonnées d'une courbe. */
  /*
   * public String toString () { String result = new String(); if (sizeSegment()
   * == 0) { result = "GM_Curve : geometrie vide"; return result; } for(int i=0;
   * i<this.sizeSegment(); i++) { GM_CurveSegment theSegment =
   * this.getSegment(i); DirectPositionList theList = theSegment.coord(); if
   * (theList.size() != 0) { result = result+theList.toString(); result =
   * result+"\n"; } else result = "GM_CurveSegment vide\n"; } return
   * result.substring(0,result.length()-1); // on enleve le dernier "\n"; }
   */

  /**
   * Renvoie la liste des coordonnées d'une courbe sous forme d'une liste de
   * DirectPosition .
   */
  @Override
  public DirectPositionList coord() {
    DirectPositionList result = new DirectPositionList();
    if (this.sizeSegment() == 0) {
      return result;
    }
    synchronized (this.segment) {
      for (GM_CurveSegment theSegment : this.segment) {
        DirectPositionList theList = theSegment.coord();
        result.addAll(theList);
      }
    }
    return result;
  }
}

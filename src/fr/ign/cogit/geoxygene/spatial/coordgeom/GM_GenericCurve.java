/**
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
 * 
 */

package fr.ign.cogit.geoxygene.spatial.coordgeom;

import java.util.List;

/**
 * NON UTILISE. Cette interface de la norme n'a plus de sens depuis qu'on a fait
 * heriter GM_CurveSegment de GM_Curve.
 * 
 * <P>
 * Definition de la norme : les classes GM_Curve et GM_CurveSegment representent
 * toutes deux des geometries à une dimension, et partagent donc plusieurs
 * signatures d'operation. Celles-ci sont definies dans l'interface
 * GM_GenericCurve. La parametrisation employee dans les methodes se fait par la
 * longueur de l'arc (absisse curviligne) ou par une autre parametrisation.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

interface GM_GenericCurve {

  /**
   * Retourne le DirectPosition du premier point. Different de l'operateur
   * "boundary" car renvoie la valeur du point et non pas l'objet geometrique
   * representatif.
   */
  DirectPosition startPoint();

  /**
   * Retourne le DirectPosition du dernier point. Different de l'operateur
   * "boundary" car renvoie la valeur du point et non pas l'objet geometrique
   * representatif.
   */
  DirectPosition endPoint();

  /**
   * NON IMPLEMENTE. Renvoie un point à l'abcsisse curviligne s.
   */
  // NORME : le parametre en entree est de type Distance.
  DirectPosition param(double s);

  /**
   * NON IMPLEMENTE. Vecteur tangent a la courbe, à l'abscisse curviligne passee
   * en parametre. Le vecteur resultat est norme.
   */
  // NORME : le parametre en entree est de type Distance.
  // Vecteur tangent(double s);

  /**
   * Renvoie O pour une GM_Curve. Pour un GM_CurveSegment, egal au endParam du
   * precedent segment dans la segmentation (0 pour le premier segment).
   */
  // NORME : le resultat est de type Distance.
  double startParam();

  /**
   * Longueur de la courbe pour une GM_Curve. Pour un GM_CurveSegment, egale à
   * startParam plus la longueur du segment.
   */
  // NORME : le resultat est de type Distance.
  double endParam();

  /**
   * NON IMPLEMENTE. Renvoie le parametre au point P (le parametre etant a
   * priori la distance). Si P n'est pas sur la courbe, on cherche alors pour le
   * calcul le point le plus proche de P sur la courbe (qui est aussi renvoye en
   * resultat). On renvoie en general une seule distance, sauf si la courbe
   * n'est pas simple.
   */
  // NORME : le resultat est de type Distance.
  List<?> paramForPoint(DirectPosition P);

  /**
   * NON IMPLEMENTE. Representation alternative d'une courbe comme l'image
   * continue d'un intervalle de reels, sans imposer que cette parametrisation
   * represente la longueur de la courbe, et sans imposer de restrictions entre
   * la courbe et ses segments. Utilite : pour les courbes parametrees, pour
   * construire une surface parametree.
   */
  DirectPosition constrParam(double cp);

  /**
   * NON IMPLEMENTE. Parametre au startPoint pour une courbe parametree,
   * c'est-e-dire : constrParam(startConstrParam())=startPoint().
   */
  double startConstrParam();

  /**
   * NON IMPLEMENTE. Parametre au endPoint pour une courbe parametree,
   * c'est-e-dire : constrParam(endConstrParam())=endPoint().
   */
  double endConstrParam();

  /**
   * NON IMPLEMENTE. Longueur entre 2 points.
   */
  // NORME : le resultat est de type Length.
  double length(GM_Position p1, GM_Position p2);

  /**
   * NON IMPLEMENTE. Longueur d'une courbe parametree "entre 2 reels".
   */
  // NORME : le resultat est de type Length.
  double length(double cparam1, double cparam2);

  /**
   * Approximation lineaire d'une courbe avec les points de contrele. Le
   * parametre spacing indique la distance maximum entre 2 points de contrele;
   * le parametre offset indique la distance maximum entre la polyligne generee
   * et la courbe originale. Si ces 2 parametres sont à 0, alors aucune
   * contrainte n'est imposee. Le parametre tolerance permet d'eliminer les
   * points consecutifs doublons qui peuvent apparaetre quand la courbe est
   * composee de plusieurs segments.
   */
  // NORME : spacing et offset sont de type Distance. tolerance n'est pas en
  // parametre.
  GM_LineString asLineString(double spacing, double offset, double tolerance);
}

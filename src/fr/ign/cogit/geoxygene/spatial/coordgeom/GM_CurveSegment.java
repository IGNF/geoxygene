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

import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;

/**
 * Segment homogene d'une GM_Curve. Classe mere abstraite.
 * <P>
 * Modification de la norme : cette classe herite de GM_Curve. Du coup on a fait
 * sauter le lien d'implementation de GM_GenericCurve. Un GM_CurveSegment sera
 * une GM_Curve composee d'un et d'un seul segment qui sera lui-meme. Les
 * methodes addSegment, removeSegment, etc... seront interdites.
 * 
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

abstract public class GM_CurveSegment extends GM_Curve
/* implements GM_GenericCurve */{

  // ////////////////////////////////////////////////////////////////////////////////
  // Attributs
  // /////////////////////////////////////////////////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////
  /**
   * Mecanisme d'interpolation, selon une liste de codes. Vaut "linear" par
   * defaut.
   * <P>
   * La liste de codes est la suivante : {linear, geodesic, circularArc3Points,
   * circularArc2PointsWithBulge, elliptical, clothoid, conic, polynomialSpline,
   * cubicSpline, rationalSpline}.
   */
//  protected String interpolation = "linear"; //$NON-NLS-1$

  /** Renvoie l'attribut interpolation. */
  abstract public String getInterpolation();
  /**
   * Type de continuite entre un segment et son predecesseur (ignore pour le
   * premier segment). Pour des polylignes on aura une continuite C0.
   */
//  protected int numDerivativesAtStart = 0;

  /** Renvoie l'attribut numDerivativesAtStart. */
  public int getNumDerivativesAtStart() {
    return 0;
  }

  /**
   * Type de continuite entre un segment et son successeur (ignore pour le
   * dernier segment). Pour des polylignes on aura une continuite C0.
   */
//  protected int numDerivativeAtEnd = 0;

  /** Renvoie l'attribut numDerivativeAtEnd. */
  public int getNumDerivativeAtEnd() {
    return 0;
  }

  /**
   * Type de continuite garantie à l'interieur de la courbe. Pour des polylignes
   * on aura une continuite C0.
   */
//  protected int numDerivativeInterior = 0;

  /** Renvoie l'attribut numDerivativeInterior. */
  public int getNumDerivativeInterior() {
    return 0;
  }
  // ////////////////////////////////////////////////////////////////////////////////
  // Methodes (abstaites, implementee dans les
  // sous-classes)////////////////////////
  // ////////////////////////////////////////////////////////////////////////////////
  /**
   * Renvoie un GM_CurveSegment de sens oppose. Methode abstraite implementee
   * dans les sous-classes.
   */
  abstract public GM_CurveSegment reverse();
}

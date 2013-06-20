/*
 * Cr�� le 29 avr. 2008
 * 
 * Pour changer le mod�le de ce fichier g�n�r�, allez � :
 * Fen�tre&gt;Pr�f�rences&gt;Java&gt;G�n�ration de code&gt;Code et commentaires
 */
package fr.ign.cogit.cartagen.leastsquares.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author G. Touya
 * 
 *         Cette contrainte interne adaptée aux objets rigides comme les
 *         bâtiments cherche à conserver la longueur des segments en minimisant
 *         les différences de déplacement entre deux points consécutifs.
 */
public class LSStiffnessConstraint extends LSInternalConstraint {

  public LSStiffnessConstraint(LSPoint pt, LSScheduler scheduler) {
    super(pt, scheduler);
  }

  /**
   * True if the constraint is applicable on point.
   * @param point
   * @return
   */
  public static boolean appliesTo(LSPoint point) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @seefr.ign.gothic.cogit.guillaume.moindresCarres.ContrainteInterneMC#
   * calculeSystemeEquations(gothic.main.GothicObject,
   * fr.ign.gothic.cogit.guillaume.moindresCarres.MCPoint)
   */
  @Override
  public EquationsSystem calculeSystemeEquations(IFeature obj, LSPoint point) {

    EquationsSystem systeme = this.sched.initSystemeLocal();
    // on commence par récupérer le point précédent et le suivant
    IDirectPosition coordPrec = null;
    IDirectPosition coordSuiv = null;
    // on commence par récupérer la géométrie
    IGeometry geom = obj.getGeom();
    ILineString ligne;
    boolean isLine = false;
    if (geom instanceof ILineString) {
      ligne = (ILineString) geom;
      isLine = true;
    } else {
      ligne = ((IPolygon) geom).exteriorLineString();
    }
    boolean start = false;
    boolean end = false;
    for (int i = 0; i < ligne.numPoints(); i++) {
      IDirectPosition coord = ligne.coord().get(i);
      if (!coord.equals(point.getIniPt())) {
        continue;
      }

      // si on est là, c'est le bon vertex
      // on marque le vertex précédent
      int prevIndex, nextIndex;
      if (i == 0) {
        prevIndex = ligne.numPoints() - 2;
        start = true;
      } else {
        prevIndex = i - 1;
      }

      // on marque le vertex suivant
      if (i + 1 == ligne.numPoints()) {
        nextIndex = 0;
        end = true;
      } else {
        nextIndex = i + 1;
      }

      // on récupère les coordonnées précédentes
      coordPrec = ligne.coord().get(prevIndex);
      // on récupère les coordonnées suivantes
      coordSuiv = ligne.coord().get(nextIndex);
      break;
    }

    // on récupère maintenant les MCPoints correspondant à ces coordonnées
    ArrayList<LSPoint> listePoints = this.sched.getMapObjPts().get(obj);
    LSPoint pointPrec = null, pointSuiv = null;
    Iterator<LSPoint> iter = listePoints.iterator();
    while (iter.hasNext()) {
      LSPoint pt = iter.next();
      if (pt.getIniPt().equals(coordPrec)) {
        pointPrec = pt;
      }
      if (pt.getIniPt().equals(coordSuiv)) {
        pointSuiv = pt;
      }
    }// while boucle sur setPoints

    // construction du vecteur des inconnues
    systeme.setUnknowns(new Vector<LSPoint>());
    if ((isLine == false) || (start == false)) {
      systeme.getUnknowns().addElement(pointPrec);
      systeme.getUnknowns().addElement(pointPrec);
    }
    systeme.getUnknowns().addElement(point);
    systeme.getUnknowns().addElement(point);
    if ((isLine == false) || (end == false)) {
      systeme.getUnknowns().addElement(pointSuiv);
      systeme.getUnknowns().addElement(pointSuiv);
    }

    // construction du vecteur des contraintes
    systeme.setConstraints(new Vector<LSConstraint>());

    // construction de la matrice des observations
    // c'est une matrice (4,1) contenant deux 0 dans le cas g�n�ral
    // (2,1) dans le cas d'un bout d'une ligne
    systeme.initObservations(4);

    // construction de la matrice A
    if (isLine == false) {
      systeme.initMatriceA(4, 6);
      systeme.setA(0, 0, 1.0);
      systeme.setA(0, 2, -1.0);
      systeme.setA(1, 1, 1.0);
      systeme.setA(1, 3, -1.0);
      systeme.setA(2, 2, 1.0);
      systeme.setA(2, 4, -1.0);
      systeme.setA(3, 3, 1.0);
      systeme.setA(3, 5, -1.0);
      systeme.setNonNullValues(8);
      for (int i = 0; i < 4; i++) {
        systeme.getConstraints().add(this);
      }
    } else if (start) {
      systeme.initMatriceA(2, 4);
      systeme.setA(0, 0, 1.0);
      systeme.setA(0, 2, -1.0);
      systeme.setA(1, 1, 1.0);
      systeme.setA(1, 3, -1.0);
      systeme.setNonNullValues(4);
      for (int i = 0; i < 2; i++) {
        systeme.getConstraints().add(this);
      }
    } else if (end) {
      systeme.initMatriceA(2, 4);
      systeme.setA(0, 0, 1.0);
      systeme.setA(0, 2, -1.0);
      systeme.setA(1, 1, 1.0);
      systeme.setA(1, 3, -1.0);
      systeme.setNonNullValues(4);
      for (int i = 0; i < 2; i++) {
        systeme.getConstraints().add(this);
      }
    } else {
      systeme.initMatriceA(4, 6);
      systeme.setA(0, 0, 1.0);
      systeme.setA(0, 2, -1.0);
      systeme.setA(1, 1, 1.0);
      systeme.setA(1, 3, -1.0);
      systeme.setA(2, 2, 1.0);
      systeme.setA(2, 4, -1.0);
      systeme.setA(3, 3, 1.0);
      systeme.setA(3, 5, -1.0);
      systeme.setNonNullValues(8);
      for (int i = 0; i < 4; i++) {
        systeme.getConstraints().add(this);
      }
    }

    return systeme;
  }

}

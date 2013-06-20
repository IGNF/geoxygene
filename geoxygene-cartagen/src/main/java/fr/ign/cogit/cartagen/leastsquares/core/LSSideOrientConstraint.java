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

import fr.ign.cogit.cartagen.mrdb.scalemaster.GeometryType;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;

/**
 * @author G. Touya
 * 
 *         Contrainte interne pour un objet rigide qui tente de conserver
 *         l'orientation initiale d'un côté (ou d'un segment) de l'objet rigide.
 */
public class LSSideOrientConstraint extends LSInternalConstraint {

  public static boolean appliesTo(LSPoint point) {
    if ((point.getTypeGeom().equals(GeometryType.POLYGON))
        || ((point.getTypeGeom().equals(GeometryType.LINE)) && ((point
            .getPosition() == 1.0) || (!point.isPointIniFin())))) {
      return true;
    }
    return false;
  }

  public LSSideOrientConstraint(LSPoint pt, LSScheduler scheduler) {
    super(pt, scheduler);
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
    // on commence par récupérer le point suivant
    IDirectPosition coordSuiv = null;
    // on commence par récupérer la géométrie
    IGeometry geom = obj.getGeom();
    ILineString ligne;
    if (geom instanceof ILineString) {
      ligne = (ILineString) geom;
    } else {
      ligne = ((IPolygon) geom).exteriorLineString();
    }
    for (int i = 0; i < ligne.numPoints(); i++) {
      IDirectPosition coord = ligne.coord().get(i);
      if (!coord.equals(point.getIniPt())) {
        continue;
      }

      // si on est là, c'est le bon vertex
      // on marque le vertex suivant
      int nextIndex;
      if (i + 1 > ligne.numPoints()) {
        nextIndex = 0;
      } else {
        nextIndex = i + 1;
      }
      // on r�cup�re les coordonn�es suivantes
      coordSuiv = ligne.coord().get(nextIndex);
      break;
    }

    // on r�cup�re maintenant le MCPoint correspondant à ces coordonnées
    ArrayList<LSPoint> listePoints = this.sched.getMapObjPts().get(obj);
    LSPoint pointSuiv = null;
    Iterator<LSPoint> iter = listePoints.iterator();
    while (iter.hasNext()) {
      LSPoint pt = iter.next();
      if (pt.getIniPt().equals(coordSuiv)) {
        pointSuiv = pt;
      }
    }// while boucle sur setPoints

    // construction du vecteur des inconnues
    systeme.setUnknowns(new Vector<LSPoint>());
    systeme.getUnknowns().addElement(point);
    systeme.getUnknowns().addElement(point);
    systeme.getUnknowns().addElement(pointSuiv);
    systeme.getUnknowns().addElement(pointSuiv);

    // construction du vecteur des contraintes
    systeme.setConstraints(new Vector<LSConstraint>());

    // construction de la matrice des observations
    // c'est une matrice (1,1) contenant l'orientation initiale
    // du côté
    double x1 = point.getIniPt().getX();
    double x2 = pointSuiv.getIniPt().getX();
    double y1 = point.getIniPt().getY();
    double y2 = pointSuiv.getIniPt().getY();
    double orient = Math.atan((x2 - x1) / (y2 - y1));
    systeme.initObservations(1);
    systeme.setObs(0, orient);

    // construction de la matrice A
    double a = 0.0, b = 0.0, c = 0.0, d = 0.0;
    a = 1 / (y1 - y2 + (x2 - x1) * (x2 - x1) / (y2 - y1));
    c = -a;
    b = (x2 - x1) / ((y2 - y1) * (y2 - y1) + (x2 - x1) * (x2 - x1));
    d = -b;
    systeme.initMatriceA(1, 4);
    systeme.setA(0, 0, a);
    systeme.setA(0, 1, b);
    systeme.setA(0, 2, c);
    systeme.setA(0, 3, d);
    systeme.setNonNullValues(4);
    systeme.getConstraints().add(this);

    return systeme;
  }

}

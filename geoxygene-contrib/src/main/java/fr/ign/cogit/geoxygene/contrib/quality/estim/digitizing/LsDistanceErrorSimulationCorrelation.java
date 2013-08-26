package fr.ign.cogit.geoxygene.contrib.quality.estim.digitizing;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Operateurs;
import fr.ign.cogit.geoxygene.contrib.quality.util.RandomGenerator;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author JFGirres
 */
public class LsDistanceErrorSimulationCorrelation extends
    AbstractErrorSimulation<GM_LineString> {

  public LsDistanceErrorSimulationCorrelation() {
  }

  /**
   * A method to simulate digitizing error (by determining a random angle and a
   * distance based on a gaussian distribution) according to the distance to
   * extremity nodes
   */
  @Override
  public void executeSimulation() {
    IPopulation<Noeud> popNoeudInitial = this.getCarteTopoIn().getPopNoeuds();
    IPopulation<Arc> popArcInitial = this.getCarteTopoIn().getPopArcs();

    double ecartType = this.getEcartType();
    double moyenne = this.getMoyenne();

    IFeatureCollection<IFeature> jddNoeudSimule = new FT_FeatureCollection<IFeature>();
    IFeatureCollection<IFeature> jddArcSimule = new FT_FeatureCollection<IFeature>();

    // Pour illustrer les vecteurs de déplacements de points
    IFeatureCollection<IFeature> jddVecteurDeplacement = new FT_FeatureCollection<IFeature>();

    // copie du jeu et création d'une nouvelle geom
    for (Noeud noeud : popNoeudInitial) {
      IFeature featureSimule = null;
      try {
        featureSimule = (IFeature) noeud.cloneGeom();
      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      IDirectPosition dp = new DirectPosition(noeud.getGeom().coord().get(0)
          .getX(), noeud.getGeom().coord().get(0).getY());
      featureSimule.setGeom(new GM_Point(dp));
      jddNoeudSimule.add(featureSimule);
    }

    // copie du jeu et création d'une nouvelle geom
    for (Arc arc : popArcInitial) {
      IFeature featureSimule = null;
      try {
        featureSimule = (IFeature) arc.cloneGeom();
      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      IDirectPositionList dpl = new DirectPositionList();
      for (IDirectPosition dp : arc.getGeom().coord()) {
        dpl.add(new DirectPosition(dp.getX(), dp.getY()));
      }
      featureSimule.setGeom(new GM_LineString(dpl));
      jddArcSimule.add(featureSimule);
    }

    double noeudInitialX[] = new double[jddNoeudSimule.size()];
    double noeudInitialY[] = new double[jddNoeudSimule.size()];
    double noeudSimuleX[] = new double[jddNoeudSimule.size()];
    double noeudSimuleY[] = new double[jddNoeudSimule.size()];

    int i = 0;

    // pour les noeuds
    for (IFeature feature : jddNoeudSimule) {
      noeudInitialX[i] = feature.getGeom().coord().get(0).getX();
      noeudInitialY[i] = feature.getGeom().coord().get(0).getY();

      // pour illustrer les vecteurs de déplacement
      IDirectPosition dpInitial = new DirectPosition();
      dpInitial.setCoordinate(noeudInitialX[i], noeudInitialY[i]);

      double angleErreurNoeud = Math.abs(RandomGenerator.genereAngle());
      double distanceAleatoireNoeud = Math.abs((RandomGenerator
          .genereNumLoiNormale() * ecartType) + moyenne);
      double offsetX = (Math.cos(angleErreurNoeud) * distanceAleatoireNoeud)
          * RandomGenerator.genereSigne(), offsetY = (Math
          .sin(angleErreurNoeud) * distanceAleatoireNoeud)
          * RandomGenerator.genereSigne();
      feature.getGeom().coord().get(0).move(offsetX, offsetY);
      noeudSimuleX[i] = feature.getGeom().coord().get(0).getX();
      noeudSimuleY[i] = feature.getGeom().coord().get(0).getY();

      // pour illustrer les vecteurs de déplacement
      IDirectPosition dpSimule = new DirectPosition();
      dpSimule.setCoordinate(noeudSimuleX[i], noeudSimuleY[i]);

      ILineString lsVecteurDeplacement = new GM_LineString(
          new DirectPositionList(dpInitial, dpSimule));
      jddVecteurDeplacement.add(new DefaultFeature(lsVecteurDeplacement));

      i = i + 1;
    }

    // affectation des deux noeuds simulés en extremité de chaque arc
    // pour les arcs
    for (IFeature featureArc : jddArcSimule) {
      IDirectPositionList dpl = featureArc.getGeom().coord();
      IDirectPosition dpStart = dpl.get(0);
      IDirectPosition dpEnd = dpl.get(dpl.size() - 1);

      double offsetXStart = 0;
      double offsetYStart = 0;
      double offsetXEnd = 0;
      double offsetYEnd = 0;

      // affectation des noeuds simulés aux extremités de l'arc à simuler
      for (i = 0; i < jddNoeudSimule.size(); i++) {
        if ((dpStart.getX() == noeudInitialX[i])
            && (dpStart.getY() == noeudInitialY[i])) {
          dpStart.setCoordinate(noeudSimuleX[i], noeudSimuleY[i]);
          offsetXStart = noeudSimuleX[i] - noeudInitialX[i];
          offsetYStart = noeudSimuleY[i] - noeudInitialY[i];
        }
      }
      for (i = 0; i < jddNoeudSimule.size(); i++) {
        if ((dpEnd.getX() == noeudInitialX[i])
            && (dpEnd.getY() == noeudInitialY[i])) {
          dpEnd.setCoordinate(noeudSimuleX[i], noeudSimuleY[i]);
          offsetXEnd = noeudSimuleX[i] - noeudInitialX[i];
          offsetYEnd = noeudSimuleY[i] - noeudInitialY[i];
        }
      }

      ILineString ls = new GM_LineString(featureArc.getGeom().coord());

      // Calcul de l'offset de chaque sommet en fonction de l'offset aux
      // extrémités
      // et de la distance aux extrémités
      if (dpl.size() > 2) {
        for (int j = 1; j < dpl.size() - 1; j++) {
          IDirectPosition dp = dpl.get(j);

          // Pour visualiser les vecteurs de déplacements
          IDirectPosition dpVecteurStart = new DirectPosition(dp.getX(),
              dp.getY());
          double absCurvDP = Operateurs.abscisseCurviligne(ls, j);
          double poidsOffsetStart = (ls.length() - absCurvDP) / ls.length();
          double poidsOffsetEnd = absCurvDP / ls.length();

          // Solution initiale: on détermine la position du point en fonction
          // des erreurs aux extrémités
          double offsetX = offsetXStart * poidsOffsetStart + offsetXEnd
              * poidsOffsetEnd;
          double offsetY = offsetYStart * poidsOffsetStart + offsetYEnd
              * poidsOffsetEnd;
          dp.move(offsetX, offsetY);
          DirectPosition dpVecteurEnd = new DirectPosition(dp.getX(), dp.getY());
          GM_LineString lsVecteurDeplacement = new GM_LineString(
              dpVecteurStart, dpVecteurEnd);
          jddVecteurDeplacement.add(new DefaultFeature(lsVecteurDeplacement));
        }
      }
    }
    this.setJddOut(jddArcSimule);
  }
}

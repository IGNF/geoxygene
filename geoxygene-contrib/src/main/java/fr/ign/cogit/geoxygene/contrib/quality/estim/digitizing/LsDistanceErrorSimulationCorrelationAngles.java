package fr.ign.cogit.geoxygene.contrib.quality.estim.digitizing;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
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
public class LsDistanceErrorSimulationCorrelationAngles extends
    AbstractErrorSimulation<GM_LineString> {

  public LsDistanceErrorSimulationCorrelationAngles() {
  }

  private List<Double> errorParArc;

  /**
   * A method to simulate digitizing error (by determining a random angle and a
   * distance based on a gaussian distribution) according to: 1- the angle
   * between successive points of the linestring, 2- the distance to extremity
   * nodes, 3- the value of nodes (number of arcs connected)
   */
  @Override
  public void executeSimulation() {
    IPopulation<Noeud> popNoeudInitial = this.getCarteTopoIn().getPopNoeuds();
    IPopulation<Arc> popArcInitial = this.getCarteTopoIn().getPopArcs();

    double ecartType = this.getEcartType();
    double moyenne = this.getMoyenne();

    FT_FeatureCollection<IFeature> jddNoeudSimule = new FT_FeatureCollection<IFeature>();
    FT_FeatureCollection<IFeature> jddArcSimule = new FT_FeatureCollection<IFeature>();

    // copie du jeu et création d'une nouvelle geom
    for (Noeud noeud : popNoeudInitial) {
      IFeature featureSimule;
      try {
        featureSimule = (IFeature) noeud.cloneGeom();
        IDirectPosition dp = new DirectPosition(noeud.getGeom().coord().get(0)
            .getX(), noeud.getGeom().coord().get(0).getY());
        featureSimule.setGeom(new GM_Point(dp));
        jddNoeudSimule.add(featureSimule);
      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    IFeatureCollection<IFeature> jddInitial = new FT_FeatureCollection<IFeature>();

    // copie du jeu et création d'une nouvelle geom
    for (Arc arc : popArcInitial) {
      jddInitial.add(new DefaultFeature(arc.getGeom()));
      IFeature featureSimule;
      try {
        featureSimule = (IFeature) arc.cloneGeom();
        IDirectPositionList dpl = new DirectPositionList();
        for (IDirectPosition dp : arc.getGeom().coord()) {
          dpl.add(new DirectPosition(dp.getX(), dp.getY()));
        }
        featureSimule.setGeom(new GM_LineString(dpl));
        jddArcSimule.add(featureSimule);
      } catch (CloneNotSupportedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
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
      int valueNoeud = popNoeudInitial.get(i).arcs().size();
      // System.out.println("Noeud n°" + i + " : value = " + valueNoeud);
      double angleErreurNoeud = Math.abs(RandomGenerator.genereAngle());
      double distanceAleatoireNoeud = Math.abs((RandomGenerator
          .genereNumLoiNormale() * ecartType) + moyenne)
          / valueNoeud;

      // Solution distance
      double offsetX = (Math.cos(angleErreurNoeud) * distanceAleatoireNoeud)
          * RandomGenerator.genereSigne(), offsetY = (Math
          .sin(angleErreurNoeud) * distanceAleatoireNoeud)
          * RandomGenerator.genereSigne();

      // Modif des points
      feature.getGeom().coord().get(0).move(offsetX, offsetY);
      noeudSimuleX[i] = feature.getGeom().coord().get(0).getX();
      noeudSimuleY[i] = feature.getGeom().coord().get(0).getY();
      i = i + 1;
    }

    // affectation des deux noeuds simulés en extremité de chaque arc

    // pour les arcs
    for (int index = 0; index < jddArcSimule.size(); index++) {
      IFeature featureArc = jddArcSimule.get(index);
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

        }
        List<Double> listAngles123 = new ArrayList<Double>();
        for (int j = 1; j < dpl.size() - 1; j++) {
          IDirectPosition dp1 = dpl.get(j - 1);
          IDirectPosition dp2 = dpl.get(j);
          IDirectPosition dp3 = dpl.get(j + 1);
          // Calcul de l'angle 123 et de son inverse
          Angle angle12 = new Angle(dp2, dp1);
          Angle angle23 = new Angle(dp2, dp3);
          Double angle123 = Angle.ecart(angle12, angle23).getValeur();
          listAngles123.add(angle123);
        }

        for (int j = 1; j < dpl.size() - 1; j++) {
          IDirectPosition dp = dpl.get(j);
          Double poidsAngle = Math.abs(listAngles123.get(j - 1) - Math.PI)
              / Math.PI;

          // Pondération du bruit selon les résultats de la thèse
          Double poidsAngle4 = 0.25 * poidsAngle + 0.75;

          double angleErreurNoeud = Math.abs(RandomGenerator.genereAngle());
          double distanceAleatoireNoeud = Math.abs((RandomGenerator
              .genereNumLoiNormale() * ecartType) + moyenne);
          distanceAleatoireNoeud = distanceAleatoireNoeud * poidsAngle4;
          double offsetX = (Math.cos(angleErreurNoeud) * distanceAleatoireNoeud)
              * RandomGenerator.genereSigne(), offsetY = (Math
              .sin(angleErreurNoeud) * distanceAleatoireNoeud)
              * RandomGenerator.genereSigne();

          dp.move(offsetX, offsetY);
        }
      }
    }

    this.setJddOut(jddArcSimule);
  }

  public void setErrorParArc(List<Double> errorParArc) {
    this.errorParArc = errorParArc;
  }

  public List<Double> getErrorParArc() {
    return errorParArc;
  }

}

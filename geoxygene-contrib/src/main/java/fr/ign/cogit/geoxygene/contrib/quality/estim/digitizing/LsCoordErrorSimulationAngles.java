package fr.ign.cogit.geoxygene.contrib.quality.estim.digitizing;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.contrib.quality.util.RandomGenerator;
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
public class LsCoordErrorSimulationAngles extends AbstractErrorSimulation<GM_LineString> {

    public LsCoordErrorSimulationAngles() {
    }

    /**
     * A method to simulate digitizing error (by determining the offset distance
     * in X and Y with a gaussian distribution)according to: 1- the angle
     * between successive points of the linestring, 2- the distance to extremity
     * nodes
     */
    @Override
    public void executeSimulation() {

        IPopulation<Noeud> popNoeudInitial = this.getCarteTopoIn().getPopNoeuds();
        IPopulation<Arc> popArcInitial = this.getCarteTopoIn().getPopArcs();
        double ecartType = this.getEcartType();
        double moyenne = this.getMoyenne();
        IFeatureCollection<IFeature> jddNoeudSimule = new FT_FeatureCollection<IFeature>();
        IFeatureCollection<IFeature> jddArcSimule = new FT_FeatureCollection<IFeature>();

        // copie du jeu et création d'une nouvelle geom
        for (Noeud noeud : popNoeudInitial) {
            IFeature featureSimule = null;
            try {
                featureSimule = (IFeature) noeud.cloneGeom();
            } catch (CloneNotSupportedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            IDirectPosition dp = new DirectPosition(noeud.getGeom().coord().get(0).getX(), noeud.getGeom().coord()
                    .get(0).getY());
            featureSimule.setGeom(new GM_Point(dp));
            jddNoeudSimule.add(featureSimule);
        }

        // copie du jeu et cr�ation d'une nouvelle geom
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
            double offsetX = ((RandomGenerator.genereNumLoiNormale() * ecartType) + moyenne), offsetY = ((RandomGenerator
                    .genereNumLoiNormale() * ecartType) + moyenne);
            feature.getGeom().coord().get(0).move(offsetX, offsetY);
            noeudSimuleX[i] = feature.getGeom().coord().get(0).getX();
            noeudSimuleY[i] = feature.getGeom().coord().get(0).getY();
            i = i + 1;
        }

        // pour les arcs
        for (IFeature featureArc : jddArcSimule) {
            IDirectPositionList dpl = featureArc.getGeom().coord();
            IDirectPosition dpEntrant = dpl.get(0);
            IDirectPosition dpSortant = dpl.get(dpl.size() - 1);

            List<Double> listAngles123 = new ArrayList<Double>();

            // Calcul de la valeur des angles de 3 sommets consécutifs inversés
            // en
            // chaque sommet de la polyligne
            if (dpl.size() > 2) {
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
            }

            // cas où la ligne ne fait que 2 points (=segment)
            if (dpl.size() == 2)
                continue;

            for (i = 0; i < jddNoeudSimule.size(); i++) {
                if ((dpEntrant.getX() == noeudInitialX[i]) && (dpEntrant.getY() == noeudInitialY[i])) {
                    dpEntrant.setCoordinate(noeudSimuleX[i], noeudSimuleY[i]);
                }
            }
            for (i = 0; i < jddNoeudSimule.size(); i++) {
                if ((dpSortant.getX() == noeudInitialX[i]) && (dpSortant.getY() == noeudInitialY[i])) {
                    dpSortant.setCoordinate(noeudSimuleX[i], noeudSimuleY[i]);
                }
            }
            if (dpl.size() > 2) {
                for (int j = 1; j < dpl.size() - 1; j++) {
                    IDirectPosition dp = dpl.get(j);
                    Double poidsAngle = Math.abs(listAngles123.get(j - 1) - Math.PI) / Math.PI;
                    double offsetX = ((RandomGenerator.genereNumLoiNormale() * ecartType * poidsAngle) + moyenne), offsetY = ((RandomGenerator
                            .genereNumLoiNormale() * ecartType * poidsAngle) + moyenne);
                    dp.move(offsetX, offsetY);
                }
            }
        }
        this.setJddOut(jddArcSimule);
    }
}

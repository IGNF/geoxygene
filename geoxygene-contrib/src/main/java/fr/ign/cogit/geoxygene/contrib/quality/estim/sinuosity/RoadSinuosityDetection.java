package fr.ign.cogit.geoxygene.contrib.quality.estim.sinuosity;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopoFactory;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.GaussianFilter;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

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
 *            A class to classify the sinuosity of a road network using the
 *            decision tree of (Mustière, 2001)
 * @author JFGirres
 * 
 */
public class RoadSinuosityDetection {

    private IFeatureCollection<IFeature> jddIn = new FT_FeatureCollection<IFeature>();

    public IFeatureCollection<IFeature> getJddIn() {
        return jddIn;
    }

    public void setJddIn(IFeatureCollection<IFeature> jddIn) {
        this.jddIn = jddIn;
    }

    private IFeatureCollection<IFeature> jddSinuosityNull = new FT_FeatureCollection<IFeature>();

    public IFeatureCollection<IFeature> getJddSinuosityNull() {
        return jddSinuosityNull;
    }

    public void setJddSinuosityNull(IFeatureCollection<IFeature> jddSinuosityNull) {
        this.jddSinuosityNull = jddSinuosityNull;
    }

    private IFeatureCollection<IFeature> jddSinuosityLow = new FT_FeatureCollection<IFeature>();

    public IFeatureCollection<IFeature> getJddSinuosityLow() {
        return jddSinuosityLow;
    }

    public void setJddSinuosityLow(IFeatureCollection<IFeature> jddSinuosityLow) {
        this.jddSinuosityLow = jddSinuosityLow;
    }

    private IFeatureCollection<IFeature> jddSinuosityHeterogeneous = new FT_FeatureCollection<IFeature>();

    public IFeatureCollection<IFeature> getJddSinuosityHeterogeneous() {
        return jddSinuosityHeterogeneous;
    }

    public void setJddSinuosityHeterogeneous(IFeatureCollection<IFeature> jddSinuosityHeterogeneous) {
        this.jddSinuosityHeterogeneous = jddSinuosityHeterogeneous;
    }

    private IFeatureCollection<IFeature> jddSinuosityHigh = new FT_FeatureCollection<IFeature>();

    public IFeatureCollection<IFeature> getJddSinuosityHigh() {
        return jddSinuosityHigh;
    }

    public void setJddSinuosityHigh(IFeatureCollection<IFeature> jddSinuosityHigh) {
        this.jddSinuosityHigh = jddSinuosityHigh;
    }

    private double sigma;

    public void setSigma(double sigma) {
        this.sigma = sigma;
    }

    public double getSigma() {
        return sigma;
    }

    private double step;

    public void setStep(double step) {
        this.step = step;
    }

    public double getStep() {
        return step;
    }

    public RoadSinuosityDetection(IFeatureCollection<IFeature> jddIn) {
        setJddIn(jddIn);
    }

    /**
     * Execute the classification of roads by sinuosity criteria
     */
    @SuppressWarnings("unchecked")
    public void execute() {

        // on convertit en carte topologique
        CarteTopo carteTopo = CarteTopoFactory.newCarteTopo(jddIn);
        carteTopo.filtreNoeudsSimples();

        for (Arc arc : carteTopo.getPopArcs()) {
            ILineString lsInitiale = arc.getGeometrie();
            // On applique le filtre gaussien
            ILineString lsLisse = GaussianFilter.gaussianFilter(lsInitiale, sigma, step);
            // On determine les séquences de virages
            List<Integer> listSequence = determineSequences(lsLisse);
            // On crée une collection de points qui servira à découper tous les
            // virages
            IFeatureCollection<IFeature> jddPtsInflexionLsInitiale = new FT_FeatureCollection<IFeature>();

            if (listSequence.size() > 0) {
                List<Integer> listSequenceFiltre = filtrageSequences(listSequence, 1);
                IDirectPositionList dplPointsInflexionLsLissee = determinePointsInflexion(lsInitiale,
                        listSequenceFiltre);

                for (IDirectPosition directPosition : dplPointsInflexionLsLissee) {
                    jddPtsInflexionLsInitiale.add(new DefaultFeature(directPosition.toGM_Point()));
                }
                sinuosityClassificationSeb(lsInitiale, jddPtsInflexionLsInitiale);
            }
            // Grosse bricole au cas où on détecte pas d'inflexions (mais ça
            // peut être
            // sinueux quand même)
            if (listSequence.size() == 0) {
                sinuosityClassificationJFG(lsInitiale);
            }
        }
    }

    /**
     * Classification de la sinuosité d'une route selon la base de règles de Seb
     * @param lsInitiale
     * @param jddPtsInflexionLsInitiale
     */
    private void sinuosityClassificationSeb(ILineString lsInitiale,
            IFeatureCollection<IFeature> jddPtsInflexionLsInitiale) {

        // Calcul des indicateurs de sinuosité
        int nbVirages = jddPtsInflexionLsInitiale.size();
        double base = lsInitiale.startPoint().distance2D(lsInitiale.endPoint());
        double longueur = lsInitiale.length();
        DefaultFeature dfFeature = new DefaultFeature(lsInitiale);

        // base de règles de seb
        if ((base / longueur) <= 0.7) {
            if (nbVirages >= 16) {
                jddSinuosityHeterogeneous.add(dfFeature);
            }
            if (nbVirages < 16) {
                jddSinuosityHigh.add(dfFeature);
            }
        }
        if ((base / longueur) >= 0.96) {
            jddSinuosityNull.add(dfFeature);
        }
        if ((base / longueur) > 0.7 && (base / longueur) < 0.96) {
            jddSinuosityLow.add(dfFeature);
        }
    }

    /**
     * Classification de la sinuosité d'une route selon la base de règles de
     * Seb, mais uniquement pour les routes sur lesquelles on a pas détécté
     * d'infleions (donc a priori pas sinueuses)
     * @param lsInitiale
     * @param jddPtsInflexionLsInitiale
     */
    private void sinuosityClassificationJFG(ILineString lsInitiale) {

        // Calcul des indicateurs de sinuosité
        double base = lsInitiale.startPoint().distance2D(lsInitiale.endPoint());
        double longueur = lsInitiale.length();
        DefaultFeature dfFeature = new DefaultFeature(lsInitiale);

        // base de règles de seb
        if ((base / longueur) <= 0.7) {
            jddSinuosityHigh.add(dfFeature);
        }
        if ((base / longueur) >= 0.96) {
            jddSinuosityNull.add(dfFeature);
        }
        if ((base / longueur) > 0.7 && (base / longueur) < 0.96) {
            jddSinuosityLow.add(dfFeature);
        }
    }

    /**
     * Méthode pour identifier les points d'inflexion dans une polyligne de
     * manière basique. Un point d'inflexion est identifié à chaque changement
     * de direction d'angles consécutifs. Renvoie une liste contenant le nombre
     * de sommet consécutifs (une séquence) avec le même sens d'angle.
     * @param lsLisse
     * @return
     */

    private static List<Integer> determineSequences(ILineString lsLisse) {

        List<Integer> listeSequences = new ArrayList<Integer>();
        IDirectPositionList listePoints = lsLisse.getControlPoint();
        Angle angleTrigo;
        String stAngleCourant;
        String stAnglePrecedent = null;

        if (!(listePoints.size() < 3)) {
            int nbSommets = 1;
            for (int i = 0; i < listePoints.size() - 2; i++) {
                angleTrigo = Angle.angleTroisPoints(listePoints.get(i), listePoints.get(i + 1), listePoints.get(i + 2));
                // Determine la direction de l'angle
                if (angleTrigo.getValeur() > Math.PI) {
                    stAngleCourant = "Tourne à Gauche";
                } else {
                    stAngleCourant = "Tourne à Droite";
                }
                if (i > 0) {
                    // Identifie un changement de direction
                    if (!(stAngleCourant.equals(stAnglePrecedent))) {
                        listeSequences.add(nbSommets);
                        nbSommets = 1;
                    }
                    // Pas de changement de direction
                    if (stAngleCourant.equals(stAnglePrecedent)) {
                        nbSommets = nbSommets + 1;
                    }
                }
                stAnglePrecedent = stAngleCourant;
            }
        }
        return listeSequences;
    }

    /**
     * Méthode pour virer toutes les micros inflexions dans une polyligne à
     * partir d'une liste d'angles consécutifs
     * @param dplPointsInflexion
     * @return
     */

    private static List<Integer> filtrageSequences(List<Integer> listeSequence, int filtrage) {

        List<Integer> listeSequenceFiltre = new ArrayList<Integer>();
        // Filtrage des micros inflexions (1 par défaud)
        listeSequenceFiltre = listeSequence;

        if (listeSequenceFiltre.size() == 2) {
            if (listeSequenceFiltre.get(0) == filtrage) {
                int nbSommetsFiltre = listeSequenceFiltre.get(1) + 1;
                listeSequenceFiltre.clear();
                listeSequenceFiltre.add(nbSommetsFiltre);
            } else if (listeSequenceFiltre.get(1) == filtrage) {
                int nbSommetsFiltre = listeSequenceFiltre.get(0) + 1;
                listeSequenceFiltre.clear();
                listeSequenceFiltre.add(nbSommetsFiltre);
            }
        } else if (listeSequenceFiltre.size() > 2) {
            for (int i = 0; i < listeSequenceFiltre.size(); i++) {
                if (listeSequenceFiltre.get(i) == filtrage) {
                    // cas où il faut virer une micro-inflexion en début de
                    // liste
                    if (i == 0) {
                        int nbSommetsFiltre = listeSequenceFiltre.get(1) + 1;
                        listeSequenceFiltre.remove(0);
                        // listeSequenceFiltre.remove(0);
                        listeSequenceFiltre.set(0, nbSommetsFiltre);
                        i = -1;
                    }
                    // cas où il faut virer une micro-inflexion en fin de liste
                    else if (i == listeSequenceFiltre.size() - 1) {
                        int nbSommetsFiltre = listeSequenceFiltre.get(listeSequenceFiltre.size() - 2) + 1;
                        listeSequenceFiltre.remove(i);
                        listeSequenceFiltre.remove(listeSequenceFiltre.size() - 1);
                        listeSequenceFiltre.add(nbSommetsFiltre);
                    }
                    // pour tous les autres cas
                    else {
                        int nbSommetsFiltre = listeSequenceFiltre.get(i - 1) + listeSequenceFiltre.get(i + 1) + 1;
                        listeSequenceFiltre.remove(i - 1);
                        // listNbInflexionsCorrect.remove(i+1);
                        listeSequenceFiltre.remove(i);
                        listeSequenceFiltre.set(i - 1, nbSommetsFiltre);
                        i = -1;
                    }
                }
            }
        }
        return listeSequenceFiltre;
    }

    /**
     * Détermine les points d'inflexions à partir de la lineString en entrée et
     * de la liste de séquence de sommets consécutifs dans la même direction
     * @param lsInitiale
     * @param listeSequence
     * @return
     */

    private static IDirectPositionList determinePointsInflexion(ILineString lsInitiale, List<Integer> listeSequence) {
        IDirectPositionList dplPointsInflexion = new DirectPositionList();
        IDirectPositionList listePoints = lsInitiale.getControlPoint();

        // Détermine les points d'inflexion sur la polyligne originale
        dplPointsInflexion.add(listePoints.get(0));
        int positionPoint = 0;
        List<Integer> listPosition = new ArrayList<Integer>();
        listPosition.add(positionPoint);

        for (int i = 0; i < listeSequence.size(); i++) {
            positionPoint = positionPoint + listeSequence.get(i);
            dplPointsInflexion.add(listePoints.get(positionPoint));
            listPosition.add(positionPoint);
        }
        dplPointsInflexion.add(listePoints.get(listePoints.size() - 1));
        listPosition.add(listePoints.size() - 1);
        return dplPointsInflexion;
    }

}

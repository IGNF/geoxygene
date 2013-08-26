package fr.ign.cogit.geoxygene.contrib.quality.estim.spatialcontext.grid.gridcat;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Vector;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;

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
public class UrbanGrid extends RasterGrid {

    static final String FC_ROADS = "ROADS";
    static final String FC_NODES = "NODES";

    public UrbanGrid(int cellule, int radius, double xB, double xH, double yB, double yH, double similarite,
            IFeatureCollection<IFeature> roads, IFeatureCollection<IFeature> nodes) {
        super(cellule, radius, xB, xH, yB, yH, similarite);
        this.setMapCriteres(new HashMap<String, Vector<Number>>());
        this.construireCellules();
        this.getData().put(FC_ROADS, roads);
        this.getData().put(FC_NODES, nodes);
    }

    public void setCriteres(boolean critNoeuds, double poidsC, int seuilBasC, int seuilHautC, boolean critRect,
            double poidsD, int seuilBasD, int seuilHautD) throws IllegalArgumentException, SecurityException,
            InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException,
            NoSuchMethodException {
        if (critNoeuds) {
            // création du critère de densité
            String nomCrit = "RoadNodeDensityCriterion";
            // définition des paramètres
            // création du vecteur de paramètres
            Vector<Number> params1 = new Vector<Number>();
            params1.add(0, poidsC);
            params1.add(1, seuilBasC);
            params1.add(2, seuilHautC);
            // on ajoute le critère
            this.getMapCriteres().put(nomCrit, params1);
        }
        // if(critRect){
        // // création du critère de dénivelée
        // String nomCrit = "RectangularRoadCriterion";
        // // définition des paramètres
        // // création du vecteur de paramètres
        // Vector<Number> params2 = new Vector<Number>();
        // params2.add(0,poidsD);
        // params2.add(1,seuilBasD);
        // params2.add(2,seuilHautD);
        // // on ajoute le critère
        // this.getMapCriteres().put(nomCrit,params2);
        // }
        if (critRect) {
            // création du critère de dénivelée
            String nomCrit = "RoadArcDensityCriterion";
            // définition des paramètres
            // création du vecteur de paramètres
            Vector<Number> params2 = new Vector<Number>();
            params2.add(0, poidsD);
            params2.add(1, seuilBasD);
            params2.add(2, seuilHautD);
            // on ajoute le critère
            this.getMapCriteres().put(nomCrit, params2);
        }

        // on construit les critères dans chaque cellule
        for (GridCell cell : getListCellules()) {
            cell.calculerCriteres();
            cell.setClasseFinale();
        }
    }
}

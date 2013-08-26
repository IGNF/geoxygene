package fr.ign.cogit.geoxygene.contrib.quality.comparison.parameters;

import fr.ign.cogit.geoxygene.contrib.appariement.surfaces.ParametresAppSurfaces;

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
 *            Parameters used for polygons matching
 * 
 * @author JFGirres
 */
public class PolygonMatchingParameters {

    public static ParametresAppSurfaces parametresDefaut() {

        ParametresAppSurfaces param = new ParametresAppSurfaces();

        param.surface_min_intersection = 1;
        param.pourcentage_min_intersection = 0.2;
        param.pourcentage_intersection_sur = 0.8;
        param.minimiseDistanceSurfacique = true;
        param.distSurfMaxFinal = 0.6;
        param.completudeExactitudeMinFinal = 0.3;
        param.regroupementOptimal = true;
        param.filtrageFinal = false;
        param.ajoutPetitesSurfaces = true;
        param.seuilPourcentageTaillePetitesSurfaces = 0.1;
        param.persistant = false;
        param.resolutionMin = 1;
        param.resolutionMax = 11;
        param.clone();

        return param;

    }

}

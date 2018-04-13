package fr.ign.cogit.geoxygene.sig3d.topology;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangulationJTS;


/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 1.7
 * 
 **/
public class TriangulationLoader {

  public static TriangulationJTS generate(
      IMultiSurface<? extends IOrientableSurface> iMS) {
	  
	  return generateFromList(iMS.getList());

  }
  
  public static TriangulationJTS generateFromList(List<? extends IOrientableSurface> iMS) {
	    
	    // On triangule la surface
	    TriangulationJTS triJTS = new TriangulationJTS("TriangulationJTS");

	    
	    
	    for(IOrientableSurface surf : iMS){
	      List<List<Noeud>> lNoeudOut = generateNode((IPolygon) surf);



	      triJTS.getPopNoeuds().addAll(lNoeudOut.get(0));
	      triJTS.getPopArcs().addAll(generateArc(lNoeudOut.get(1), lNoeudOut.get(2)));
	    }

	    
	    return triJTS;
  }
  
  
  public static TriangulationJTS generateFromSurface(IOrientableSurface surf) {
	    
	    // On triangule la surface
	    TriangulationJTS triJTS = new TriangulationJTS("TriangulationJTS");

	    

	      List<List<Noeud>> lNoeudOut = generateNode((IPolygon) surf);



	      triJTS.getPopNoeuds().addAll(lNoeudOut.get(0));
	      triJTS.getPopArcs().addAll(generateArc(lNoeudOut.get(1), lNoeudOut.get(2)));
	 

	    
	    return triJTS;
}

  private static List<List<Noeud>> generateNode(IPolygon surf) {
    List<List<Noeud>> lNoeudOut = new ArrayList<List<Noeud>>();

    // /////////////////////////
    // ///On triangule la face supérieure
    // //////////////////////////

    // Les noeuds
    List<Noeud> lNoeud = new ArrayList<Noeud>();

    // Les arrêtes contraignant la surface
    List<Noeud> lNoeudIni = new ArrayList<Noeud>();
    List<Noeud> lNoeudFin = new ArrayList<Noeud>();

    // On ferme si nécessaire l'extérieur
    IDirectPositionList dpl = surf.getExterior().coord();
    if (!dpl.get(0).equals(dpl.get(dpl.size() - 1))) {
      dpl.add(dpl.get(0));

    }

    lNoeud.add(new Noeud(dpl.get(0)));

    int nbPoints = dpl.size();
    // On génère les murs
    for (int i = 0; i < nbPoints - 1; i++) {

      lNoeud.add(new Noeud(dpl.get(i + 1)));
      lNoeudIni.add(lNoeud.get(i));
      lNoeudFin.add(lNoeud.get(i + 1));

    }

    List<IRing> lInterior = surf.getInterior();
    int nbInterior = lInterior.size();

    // On génère les murs apparaissent dans les polygones troués extrudés
    for (int i = 0; i < nbInterior; i++) {
      IRing ringActu = lInterior.get(i);

      IDirectPositionList dplInterior = ringActu.coord();
      if (!dplInterior.get(0).equals(dplInterior.get(dplInterior.size() - 1))) {
        dplInterior.add(dplInterior.get(0));

      }

      int nbPInt = dplInterior.size();

      lNoeud.add(new Noeud(dplInterior.get(0)));

      for (int j = 0; j < nbPInt - 1; j++) {

        lNoeud.add(new Noeud(dplInterior.get(j + 1)));
        lNoeudIni.add(lNoeud.get(lNoeud.size() - 2));
        lNoeudFin.add(lNoeud.get(lNoeud.size() - 1));

      }

    }

    lNoeudOut.add(lNoeud);
    lNoeudOut.add(lNoeudIni);
    lNoeudOut.add(lNoeudFin);

    return lNoeudOut;

  }

  private static List<Arc> generateArc(List<Noeud> lNoeudIni,
      List<Noeud> lNoeudFin) {
    List<Arc> lArc = new ArrayList<Arc>();
    int nbArcs = lNoeudIni.size();

    for (int i = 0; i < nbArcs; i++) {

      lArc.add(new Arc(lNoeudIni.get(i), lNoeudFin.get(i)));
    }

    return lArc;
  }

  public static TriangulationJTS generate(IPolygon surf) {

    List<List<Noeud>> lNoeudOut = generateNode(surf);

    // On triangule la surface
    TriangulationJTS triJTS = new TriangulationJTS("TriangulationJTS");

    triJTS.getPopNoeuds().addAll(lNoeudOut.get(0));
    triJTS.getPopArcs().addAll(generateArc(lNoeudOut.get(1), lNoeudOut.get(2)));

    return triJTS;
  }

}

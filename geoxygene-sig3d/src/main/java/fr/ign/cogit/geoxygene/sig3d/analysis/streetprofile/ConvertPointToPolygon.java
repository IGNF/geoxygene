package fr.ign.cogit.geoxygene.sig3d.analysis.streetprofile;

import java.util.ArrayList;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.algorithms.SwingingArmNonConvexHull;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;


/**
 * Classe permettant de transformer des points projetés en polygones
 * 
 * @author MFund
 * @author MBrasebin
 * @author JPerret
 * @author YMeneroux
 * 
 */
public class ConvertPointToPolygon {


  public static IFeatureCollection<IFeature> convert(
      IFeatureCollection<IFeature> featCPoints, double radius) {

    // On récupère la liste des tous les identifiants
    List<String> lValAtt = new ArrayList<String>();

    for (IFeature feat : featCPoints) {
      String id = feat.getAttribute(BuildingProfileParameters.ID)
          .toString();

      if (lValAtt.contains(id)) {
        continue;
      }

      lValAtt.add(id);

    }

    // On prépare une liste de collection pour dispatcher les entités par id
    List<IDirectPositionList> lDPL = new ArrayList<IDirectPositionList>();
    for (String s : lValAtt) {
      lDPL.add(new DirectPositionList());
    }

    // On dispatch les entités
    for (IFeature feat : featCPoints) {

      String id = feat.getAttribute(BuildingProfileParameters.ID)
          .toString();
      int index = lValAtt.indexOf(id);

      lDPL.get(index).addAll(feat.getGeom().coord());

    }

    // On génère les polygones
    IFeatureCollection<IFeature> featCollOut = new FT_FeatureCollection<IFeature>();

    int nbList = lDPL.size();

    for (int i = 0; i < nbList; i++) {
      IDirectPositionList dpl = lDPL.get(i);

      String id = lValAtt.get(i);

      SwingingArmNonConvexHull sANCH = new SwingingArmNonConvexHull(
          dpl.getList(), radius);
      IGeometry geom = sANCH.compute();
      //moins de 4 sommets ? impossible
      if(geom == null || geom.coord().size() < 4){
        continue;
      }
      

      IFeature feat = new DefaultFeature(geom);
      AttributeManager.addAttribute(feat, BuildingProfileParameters.ID,
          id, "String");

      featCollOut.add(feat);
    }

    return featCollOut;

  }

}

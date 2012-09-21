package fr.ign.cogit.geoxygene.sig3d.calculation.buffer3d.VUnion;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.calculation.BooleanOperators;
import fr.ign.cogit.geoxygene.sig3d.geometry.Sphere;
import fr.ign.cogit.geoxygene.sig3d.semantic.Map3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.sig3d.tetraedrisation.Tetraedrisation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 * @version 0.1
 * 
 *
 * Permet de calculer un buffer 3D à partir de l'unions des buffers des
 * composantes convexes Cet algorithme est partiuliérement long mais très rapide
 * sur les objets décomposables en peu de formes convexe. Pour décomposer un
 * objet en forme convexes, l'algorithme de tetraedrisation est utilise Seules
 * les geometries solides fermees sont utilisables TODO ameliorer la
 * decomposition en forme convexe
 * 
 */

public class UnionAlgorithmBuffer3D {
  public static final String DECOMP = "DECOMP";
  private final static Logger logger = Logger
      .getLogger(UnionAlgorithmBuffer3D.class.getName());

  /**
   * On exécute le buffer 3D sur l'entité feat Val traduit la taille du buffer
   * Detail le niveau de détail de la sphère qui sera utilisée Carte maintient
   * le lien pour l'affichage
   * 
   * @param feat l'entité dont on calcule le buffer
   * @param val taille du buffer
   * @param detail détail des sphères utilisées
   * @param map3D la carte dans laquelle seront affichés les résultats
   */
  public static void offsetting(IFeature feat, double val, int detail,
      Map3D map3D) {

    long t = System.currentTimeMillis();

    // Décomposition de l'objet en convexes (ici tétraèdres)
    Tetraedrisation tetraedrisation = new Tetraedrisation(feat);
    tetraedrisation.tetraedrise(true, false);

    UnionAlgorithmBuffer3D.logger.info(Messages
        .getString("Triangulation.Tetraedrisation")
        + " : "
        + (System.currentTimeMillis() - t));
    t = System.currentTimeMillis();

    List<GM_Solid> lSol = tetraedrisation.getTetraedres();

    int nbTet = lSol.size();

    GM_Solid sol;
    List<GM_Solid> lSolides = new ArrayList<GM_Solid>();

    // Pour chaque tétraèdres, on calcule son buffer
    for (int i = 0; i < nbTet; i++) {

      t = System.currentTimeMillis();

      IDirectPositionList dpl = lSol.get(i).coord();

      dpl = UnionAlgorithmBuffer3D.orderPoints(dpl);

      GM_MultiSolid<GM_Solid> multis = new GM_MultiSolid<GM_Solid>();

      for (int j = 0; j < 4; j++) {
        multis.add(new Sphere(dpl.get(j), val, detail));

      }

      Tetraedrisation tet = new Tetraedrisation(new DefaultFeature(multis));
      tet.tetraedrise(false, true);

      UnionAlgorithmBuffer3D.logger.info(Messages
          .getString("CalculBuffer3D.Contribution")
          + (System.currentTimeMillis() - t));

      sol = new GM_Solid(tet.getTriangles());

      lSolides.add(sol);

    }

    // On fait 2 à 2 l'union des buffers en liant entre eux les plus petits
    // d'abord
    while (nbTet != 1) {

      GM_Solid sol1 = lSolides.remove(0);
      GM_Solid sol2 = lSolides.remove(0);

      t = System.currentTimeMillis();

      lSolides.add(BooleanOperators.compute(new DefaultFeature(sol1),
          new DefaultFeature(sol2), BooleanOperators.UNION));

      UnionAlgorithmBuffer3D.logger.info(Messages
          .getString("CalculBuffer3D.CalculUnion")
          + (System.currentTimeMillis() - t));

      nbTet = lSolides.size();

      UnionAlgorithmBuffer3D.logger.debug(Messages
          .getString("CalculBuffer3D.ElementsFinaux") + " : " + nbTet);

    }

    // on prépare à l'affichage
    FT_FeatureCollection<IFeature> ftColl = new FT_FeatureCollection<IFeature>();
    ftColl.add(new DefaultFeature(lSolides.get(0)));

    map3D.removeLayer(UnionAlgorithmBuffer3D.DECOMP);
    Color c = new Color((int) (Math.random() * 255),
        (int) (Math.random() * 255), (int) (Math.random() * 255));
    // On ajoute les triangles à la carte
    map3D.addLayer(new VectorLayer(ftColl, UnionAlgorithmBuffer3D.DECOMP, true,
        c, 1, true));

  }

  /**
   * Garantit l'unicité des points dans une liste
   * 
   * @param lPos
   * @return
   */
  private static IDirectPositionList orderPoints(IDirectPositionList lPos) {

    IDirectPositionList lPosAjoute = new DirectPositionList();

    int nbElem = lPos.size();

    for (int i = 0; i < nbElem; i++) {

      IDirectPosition tempP = lPos.get(i);

      if (!UnionAlgorithmBuffer3D.contain(lPosAjoute, tempP)) {

        lPosAjoute.add(tempP);

      }

    }

    return lPosAjoute;

  }

  /**
   * Détermine si DP appartient à l
   * 
   * @param l
   * @param dp
   * @return
   */
  private static boolean contain(IDirectPositionList l, IDirectPosition dp) {
    int nb = l.size();

    for (int i = 0; i < nb; i++) {

      if (l.get(i).equals(dp, 0.05)) {

        return true;
      }

    }
    return false;
  }

}

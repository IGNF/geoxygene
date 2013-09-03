package fr.ign.cogit.geoxygene.sig3d.io.vector;

import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.convert.transform.Extrusion2DObject;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

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
 *  @version 0.1
 * 
 * Classe permettant de charger un shape et de le porter en 3D Ce portage se
 * fait à partir des attributs portés par le feature Une géométrie de dimension
 * n en 2D peut devenir une géométrie de dimension n ou n+1 en 3D (en fonction
 * des paramètres) La classe From2DGeomTo3DGeom est utilisée This class is used
 * to load shapefile in the 3D environment
 */
public class ShapeFileLoader {

  private final static Logger logger = Logger.getLogger(ShapeFileLoader.class
      .getName());

  private static boolean IsPosAttZMinDouble = false;
  private static int posAttHaut;

  private static boolean IsPosAttHeigthDouble = false;
  private static int posAttZMin;

  // Vrai si l'attribut de position posAttHaut correspond à une hauteur
  // Faux sinon (pas de hauteur et attribut valant ZMax)

  private static boolean hasHeigth;

  /**
   * Propose de transformer un shape 2D en 3D Une chaine vide de caractère
   * indique que l'on ne prend pas en compte d'attribut
   * 
   * @param file : Chemin du fichier
   * @param attZMin : Attribut de Z minimum - Une chaine vide de caractère
   *          indique que l'on ne prend pas en compte d'attribut (valeur 0)
   * @param attHigth : Attribut de Z maximum ou de hauteur - Une chaine vide de
   *          caractère indique que l'on ne prend pas en compte d'attribut
   * @param isHeigth : Indique si attHaut correspond à une hauteur ou à un z Max
   * @return renvoie une collection correspondant au chargement du fichier Shape
   *         avec les paramètres d'extrusions appliqués
   */
  @SuppressWarnings("unchecked")
  public static FT_FeatureCollection<IFeature> loadingShapeFile(String file,
      String attZMin, String attHigth, boolean isHeigth) {
    try {

      // On récupère le fichier
      IPopulation<IFeature> ftColl = ShapefileReader.read(file);

      // On effectue la transformation

      return ShapeFileLoader.transformFrom2D(
          (FT_FeatureCollection<IFeature>) ftColl, attZMin, attHigth, isHeigth);

    } catch (Exception e) {

      e.printStackTrace();
      return null;

    }

  }

  /**
   * Transforme des données de type jts en donné 3D A l'aide d'attribut
   * 
   * @param attZMin : Attribut de Z minimum - Une chaine vide de caractère
   *          indique que l'on ne prend pas en compte l'attribut
   * @param attHeigth : Attribut de Z maximum ou de hauteur - Une chaine vide de
   *          caractère indique que l'on ne prend pas en compte l'attribut
   *          (hauteur 0)
   * @param isHeigth : Indique si attHaut correspond à une hauteur ou à un z Max
   * @return renvoie une collection correspondant au chargement du fichier Shape
   *         avec les paramètres d'extrusions appliqués
   */
  @SuppressWarnings("unchecked")
  public static FT_FeatureCollection<IFeature> transformFrom2D(
      FT_FeatureCollection<IFeature> ftColl, String attZMin, String attHeigth,
      boolean isHeigth) {
    List<GF_AttributeType> listeAttributs = ftColl.get(0).getFeatureType()
        .getFeatureAttributes();// dataset.getPopulation(0).getFeatureType().getFeatureAttributes();
    // On récupère la position des attributs

    ShapeFileLoader.posAttZMin = -1;
    ShapeFileLoader.posAttHaut = -1;
    ShapeFileLoader.hasHeigth = isHeigth;

    // Si la position reste -1, on considère les attributs comme inexistant
    // on placera zmin = 0 si AttZMin n'existe pas
    // Les objets seront plans si attHaut n'existe pas

    int nbAttribut = listeAttributs.size();

    for (int i = 0; i < nbAttribut; i++) {

      GF_AttributeType attTypeTemp = listeAttributs.get(i);

      if (attTypeTemp.getMemberName().equalsIgnoreCase(attZMin)) {

        ShapeFileLoader.posAttZMin = i;
        ShapeFileLoader.IsPosAttZMinDouble = attTypeTemp.getValueType().equals(
            "Double");

      }

      if (attTypeTemp.getMemberName().equalsIgnoreCase(attHeigth)) {
        ShapeFileLoader.posAttHaut = i;
        ShapeFileLoader.IsPosAttHeigthDouble = attTypeTemp.getValueType()
            .equals("Double");
      }

    }

    // Nombre d'élements
    int nb = ftColl.size();

    FT_FeatureCollection<IFeature> lObjFinal = new FT_FeatureCollection<IFeature>();

    for (int i = 0; i < nb; i++) {

      IFeature feat = ftColl.get(i);

      // On traduit la géométrie en une liste de points
      IGeometry geom = feat.getGeom();

      // On récupère le Z
      double[] Z = ShapeFileLoader.trouveZ((DefaultFeature) feat);

      IGeometry geomFinale = null;

      // On essaie de retrouver la classe de la géométrie
      // Ordre de test simple puis complexe de la dim la plus élevée à la
      // plus faible

      if (geom instanceof GM_Polygon) {

        geomFinale = Extrusion2DObject.convertFromPolygon((IPolygon) geom,
            Z[0], Z[1]);

      } else if (geom instanceof GM_MultiSurface) {

        GM_MultiSurface<IOrientableSurface> multiS = (GM_MultiSurface<IOrientableSurface>) geom;

        if (multiS.size() == 1) {

          geomFinale = Extrusion2DObject.convertFromPolygon(
              (GM_Polygon) multiS.get(0), Z[0], Z[1]);

        } else {

          geomFinale = Extrusion2DObject.convertFromMultiPolygon(multiS, Z[0],
              Z[1]);

        }

      } else if (geom instanceof GM_LineString) {

        geomFinale = Extrusion2DObject.convertFromLine((GM_LineString) geom,
            Z[0], Z[1]);

      } else if (geom instanceof GM_MultiCurve) {

        geomFinale = Extrusion2DObject.convertFromMultiLine(
            (GM_MultiCurve<?>) geom, Z[0], Z[1]);

      } else if (geom instanceof GM_Point) {

        geomFinale = Extrusion2DObject.convertFromPoint((GM_Point) geom, Z[0],
            Z[1]);

      } else if (geom instanceof GM_MultiPoint) {
        geomFinale = Extrusion2DObject.convertFromMultiPoint(
            (GM_MultiPoint) geom, Z[0], Z[1]);

      } else {

        ShapeFileLoader.logger.warn(Messages
            .getString("Representation.GeomUnk") + geom.getClass().getName());
        continue;
      }

      if (geomFinale == null) {
        ShapeFileLoader.logger.warn(Messages.getString("Géométrie vide"));
        continue;
      }
      feat.setGeom(geomFinale);
      // On complète la liste des objets
      lObjFinal.add(feat);

    }

    return lObjFinal;

  }

  /**
   * Permet de calculer le Zmin et le Zmax pour un feature à partir des options
   * utilisateurs Zmax est Double.NaN quand non défini
   */
  private static double[] trouveZ(DefaultFeature feat) {

    double resultat[] = new double[2];

    double z1 = 0;
    double z2 = 0;

    // zmin n'est pas défini
    if (ShapeFileLoader.posAttZMin != -1) {

      // Zmin est défini
      if (ShapeFileLoader.IsPosAttZMinDouble) {
        z1 = Double.parseDouble(feat.getAttribute(ShapeFileLoader.posAttZMin)
            .toString());
      } else {

        z1 = Integer.parseInt(feat.getAttribute(ShapeFileLoader.posAttZMin)
            .toString());
      }

    }

    // zmax n'est pas défini
    if (ShapeFileLoader.posAttHaut == -1) {
      z2 = Double.NaN;

    } else {
      // Zmax est défini
      if (ShapeFileLoader.IsPosAttHeigthDouble) {
        z2 = Double.parseDouble(feat.getAttribute(ShapeFileLoader.posAttHaut)
            .toString());
      } else {

        z2 = Integer.parseInt(feat.getAttribute(ShapeFileLoader.posAttHaut)
            .toString());
      }

    }

    // C'est une hauteur !!! on additionne
    if (ShapeFileLoader.hasHeigth) {
      z2 = z2 + z1;
    }

    // On ordonne (cas de hauteur négative ou inversion zmin zmax ....
    resultat[0] = Math.min(z1, z2);
    resultat[1] = Math.max(z1, z2);

    return resultat;
  }

}

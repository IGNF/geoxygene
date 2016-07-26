package fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.feature;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.model.citygml.core.CityObject;
import org.citygml4j.model.gml.AbstractGeometry;
import org.citygml4j.model.gml.SurfaceProperty;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.ConvertCityGMLAppearance;
import fr.ign.cogit.geoxygene.sig3d.io.xml.citygml.ConvertyCityGMLGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomcomp.GM_CompositeSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

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
 * Classe utilitaire pour le chargement de données CityGML Utility class for
 * loading of CityGML datasets
 * 
 */
public class Util {

  /**
   * Constructeur vide
   */
  private Util() {
    super();
  }

  /**
   * Convertit une liste de surfacespropertyes d'un city object en entité
   * GeOxygene. Cette classe permet de faire le lien entre une entité chargée de
   * CityGML et la représentation éventuelle sous jacente (notamment la
   * texturation)
   * 
   * @param lSP la list de surface property dont on veut extraire une
   *          représentation
   * @param obj l'objet CityObject associé qui peut également contenir des
   *          informations de représentation
   * @return une collection de features issus de la conversion avec une
   *         représentation attachée (qui peut ne pas être définie)
   */
  public static IFeatureCollection<IFeature> processSurfaceProperties(
      List<SurfaceProperty> lSP, CityObject obj) {

    IFeatureCollection<IFeature> featColl = new FT_FeatureCollection<IFeature>();

    int nbSurfaces = lSP.size();

    List<String> ind = new ArrayList<String>();
    List<IOrientableSurface> gmOS = new ArrayList<IOrientableSurface>();

    for (int j = 0; j < nbSurfaces; j++) {

      AbstractGeometry geom = lSP.get(j).getSurface();

      if (geom != null) {

        ConvertyCityGMLGeometry c = new ConvertyCityGMLGeometry();

        IGeometry o = c.convertGMLGeometry(geom);

        if (o instanceof IOrientableSurface) {

          gmOS.add((GM_OrientableSurface) o);
        } else if (o instanceof GM_MultiSurface<?>) {
          gmOS.addAll(((GM_MultiSurface<?>) o).getList());
        } else if (o instanceof GM_CompositeSurface) {
          gmOS.addAll(((GM_CompositeSurface) o).getGenerator());
        } else {
          System.out.println("error");
        }

        ind.addAll(c.getlRingID());

      }

    }

    IFeature feat = new DefaultFeature(
        new GM_MultiSurface<GM_OrientableSurface>(gmOS));
    if (obj.getAppearance() != null && obj.getAppearance().size() != 0) {

      feat.setRepresentation(new ConvertCityGMLAppearance(feat, obj
          .getAppearance(), ind));
    } else {

      feat.setRepresentation(new ConvertCityGMLAppearance(feat, null, ind));
    }
    featColl.add(feat);
    return featColl;

  }

  /**
   * Convertit une liste de surfacespropertyes d'un city object en entité
   * GeOxygene. Cette classe permet de faire le lien entre une entité chargée de
   * CityGML et la représentation éventuelle sous jacente (notamment la
   * texturation)
   * 
   * @param geom la géométrie à laquelle sera appliquée le représentation
   * @param obj l'objet CityObject associé qui peut également contenir des
   *          informations de représentation
   * @return une collection de features issus de la conversion avec une
   *         représentation attachée (qui peut ne pas être définie)
   */
  public static IFeatureCollection<IFeature> processAbstractGeometries(
      AbstractGeometry geom, CityObject obj) {
    ConvertyCityGMLGeometry c = new ConvertyCityGMLGeometry();

    IFeatureCollection<IFeature> featColl = new FT_FeatureCollection<IFeature>();

    if (geom != null) {
      IFeature feat = new DefaultFeature(c.convertGMLGeometry(geom));
      List<String> ind = c.getlRingID();

      if (obj.getAppearance() != null && obj.getAppearance().size() != 0) {

        feat.setRepresentation(new ConvertCityGMLAppearance(feat, obj
            .getAppearance(), ind));
      } else {

        feat.setRepresentation(new ConvertCityGMLAppearance(feat, null, ind));
      }
      featColl.add(feat);

    }

    return featColl;
  }

  private static GM_Point p1 = null;
  private static GM_Point p2 = null;

  public static void transformGeomToPoint(
      IFeatureCollection<IFeature> ftFeatColl) {

    if (ftFeatColl == null) {

      return;
    }

    int nbElem = ftFeatColl.size();

    if (nbElem == 0) {

      return;
    }

    if (Util.p1 == null) {
      IDirectPosition dpTemp = ftFeatColl.get(0).getGeom().coord().get(0);
      IDirectPosition dpTemp2 = (DirectPosition) dpTemp.clone();

      dpTemp.move(50, 50, 50);

      Util.p1 = new GM_Point(dpTemp);
      Util.p2 = new GM_Point(dpTemp2);
    }

    for (int i = 0; i < nbElem; i++) {

      if (Math.random() > 0.5) {
        ftFeatColl.get(0).setGeom(Util.p1);
      } else {

        ftFeatColl.get(0).setGeom(Util.p2);
      }

    }
  }

}

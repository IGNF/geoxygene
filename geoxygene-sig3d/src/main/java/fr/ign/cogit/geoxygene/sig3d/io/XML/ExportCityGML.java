package fr.ign.cogit.geoxygene.sig3d.io.XML;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
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
 * Permet de sauvgarder un fichier au format GML TODO Utiliser le bon EPSG
 * introduire PMin et PMAx - A complèter This class enable to save a layer into
 * a citygml file
 * 
 */
@Deprecated
public class ExportCityGML {
  /**
   * Permet de sauvegarder une liste d'objets au format GML
   */

  private static int LevelOfDetail = 0;
  private final static Logger logger = Logger.getLogger(ExportCityGML.class
      .getName());

  /**
   * Export CITY GML depuis une couche vectoriel. Cet export ne conserve que la
   * géométrie mais peut être utile pour être utilisée dans d'autres
   * applications
   * 
   * En attendant mieux ...
   * 
   * @param vectorialLayer
   * @param fileName
   * @param LOD
   */
  public static void export(VectorLayer vectorialLayer, String fileName, int LOD) {

    ExportCityGML.LevelOfDetail = LOD;

    try {
      FileWriter data = new FileWriter(fileName);
      // en tête
      data.write("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n");

      data.write("<CityModel xmlns="
          + '"'
          + "http://www.citygml.org/citygml/1/0/0"
          + '"'
          + " xmlns:gml="
          + '"'
          + "http://www.opengis.net/gml"
          + '"'
          + " xmlns:xlink="
          + '"'
          + "http://www.w3.org/1999/xlink"
          + '"'
          + " xmlns:xsi="
          + '"'
          + "http://www.w3.org/2001/XMLSchema-instance"
          + '"'
          + " xsi:schemaLocation="
          + '"'
          + "http://www.citygml.org/citygml/1/0/0 http://www.citygml.org/citygml/1/0/0/CityGML.xsd"
          + '"' + ">\n");

      String nomCouche = vectorialLayer.getLayerName();

      data.write("<gml:description> This file contains features exported from the Geoxygen plateform </gml:description>\n");
      data.write("<gml:name>" + nomCouche + "</gml:name>\n");
      data.write("<gml:boundedBy>\n");

      data.write("<gml:Envelope srsName=\"EPSG:0\">\n");

      DirectPosition pTemp = new DirectPosition(0.0, 0.0, 0.0);
      data.write("<gml:pos srsDimension=\"3\">" + pTemp.getX() + " "
          + pTemp.getY() + " " + pTemp.getZ() + "</gml:pos>\n");

      // pTemp = lobj.getPMin();
      data.write("<gml:pos srsDimension=\"3\">" + pTemp.getX() + " "
          + pTemp.getY() + " " + pTemp.getZ() + "</gml:pos>\n");

      data.write(" </gml:Envelope>\n");
      data.write(" </gml:boundedBy>\n");

      for (int i = 0; i < vectorialLayer.size(); i++) {

        IFeature obj = vectorialLayer.get(i);

        data.write("<cityObjectMember>\n");

        data.write("<Building gml:" + "id=" + '"' + "Building" + "_" + i + '"'
            + " >\n");

        int dimension = obj.getGeom().dimension();

        String resultat = "";
        switch (dimension) {

          case 0:
            resultat = ExportCityGML.exportPoint(obj.getGeom());
            break;
          case 1:
            resultat = ExportCityGML.exportLine(obj.getGeom());
            break;
          case 2:
            resultat = ExportCityGML.exportSurface(obj.getGeom());
            break;
          case 3:
            resultat = ExportCityGML.exportSolid(obj.getGeom());
            break;
          default:
            break;

        }
        data.write(resultat);
        data.write("</Building>\n");
        data.write("</cityObjectMember>\n");

      }

      data.write("</CityModel>\n");
      data.close();
      ExportCityGML.logger.info(Messages
          .getString("BarrePrincipale.SaveFileCityGM"));

    } catch (IOException e) {
      e.printStackTrace();
      ExportCityGML.logger.warn(Messages.getString("FenetreSauvegarde.Fail"));
    }

  }

  public static String exportSolid(IGeometry geom) {
    GM_Solid solid = (GM_Solid) geom;

    String str = "";

    str += "<lod" + ExportCityGML.LevelOfDetail + "Solid>\n";
    str += "<gml:Solid>\n";
    str += "<gml:exterior>\n";
    str += "<gml:CompositeSurface>\n";
    List<IOrientableSurface> lOS = solid.getFacesList();
    int nbFacette = lOS.size();

    for (int i = 0; i < nbFacette; i++) {

      str += "<gml:surfaceMember>\n";
      str += "<gml:baseSurface>\n";
      str += "<gml:Polygon>\n";
      str += "<gml:exterior>\n";
      str += "<gml:LinearRing>\n";

      IDirectPositionList dpl = lOS.get(i).coord();

      int nbPoints = dpl.size();

      for (int j = 0; j < nbPoints; j++) {

        IDirectPosition dpTemp = dpl.get(j);

        str += "<gml:pos srsDimension=" + '"' + 3 + '"' + ">" + dpTemp.getX()
            + " " + dpTemp.getY() + " " + dpTemp.getZ() + " </gml:pos>\n";
      }

      str += "</gml:LinearRing>\n";
      str += "</gml:exterior>\n";
      str += "</gml:Polygon>\n";
      str += "</gml:baseSurface>\n";
      str += "</gml:surfaceMember>\n";
    }

    str += "</gml:CompositeSurface>\n";
    str += "</gml:exterior>\n";
    str += "</gml:Solid>\n";
    str += "</lod" + ExportCityGML.LevelOfDetail + "Solid>\n";

    return str;
  }

  public static String exportSurface(IGeometry geom) {
    String str = "";

    ArrayList<IGeometry> lFacettes = new ArrayList<IGeometry>();

    if (geom instanceof GM_OrientableSurface) {

      GM_OrientableSurface surface = (GM_OrientableSurface) geom;

      lFacettes.add(surface);

    } else if (geom instanceof GM_MultiSurface<?>) {
      GM_MultiSurface<?> surface = (GM_MultiSurface<?>) geom;

      lFacettes.addAll(surface.getList());
    } else {
      ExportCityGML.logger.warn(Messages.getString("Representation.GeomUnk"));
      return null;
    }

    str += "<lod" + ExportCityGML.LevelOfDetail + "MultiSurface>\n";

    str += "<gml:MultiSurface>\n";

    int nbLFacettes = lFacettes.size();

    for (int i = 0; i < nbLFacettes; i++) {
      str += "<gml:surfaceMember>\n";
      str += "<gml:Polygon>\n";
      str += "<gml:exterior>\n";
      str += "<gml:LinearRing>\n";
      IDirectPositionList dpl = lFacettes.get(i).coord();

      int nbPoints = dpl.size();

      for (int j = 0; j < nbPoints; j++) {

        IDirectPosition dpTemp = dpl.get(j);

        str += "<gml:pos srsDimension=" + '"' + 3 + '"' + ">" + dpTemp.getX()
            + " " + dpTemp.getY() + " " + dpTemp.getZ() + " </gml:pos>\n";
      }

      str += "</gml:LinearRing>\n";
      str += "</gml:exterior>\n";
      str += "</gml:Polygon>\n";
      str += "  </gml:surfaceMember>\n";
    }

    str += "</gml:MultiSurface>\n";
    str += "</lod" + ExportCityGML.LevelOfDetail + "MultiSurface>\n";

    return str;

  }

  public static String exportLine(IGeometry geom) {

    String str = "";

    if (!(geom instanceof GM_Curve)) {

      ExportCityGML.logger.warn(Messages.getString("Representation.GeomUnk"));
      return "";
    }

    IDirectPositionList dPL = geom.coord();
    int nbPoints = dPL.size();
    str += "<lod" + ExportCityGML.LevelOfDetail + "MultiCurve>\n";
    str += "<gml:MultiCurve>\n";
    str += "<gml:curveMember>\n";
    str += "<gml:LineString>\n";

    for (int i = 0; i < nbPoints; i++) {

      IDirectPosition dpTemp = dPL.get(i);

      str += "<gml:pos srsDimension=" + '"' + 3 + '"' + ">" + dpTemp.getX()
          + " " + dpTemp.getY() + " " + dpTemp.getZ() + " </gml:pos>\n";
    }

    str += "    </gml:LineString>\n";
    str += "   </gml:curveMember>\n";
    str += "  </gml:MultiCurve>\n";

    str += "</lod" + ExportCityGML.LevelOfDetail + "MultiCurve>\n";
    return str;

  }

  public static String exportPoint(IGeometry geom) {

    String str = "";

    if (!(geom instanceof GM_Point) && !(geom instanceof GM_MultiPoint)) {
      ExportCityGML.logger.warn(Messages.getString("Representation.GeomUnk"));
      return "";
    }

    IDirectPositionList dPL = geom.coord();
    int nbPoints = dPL.size();

    str += "<gml:MultiPoint>\n";

    for (int i = 0; i < nbPoints; i++) {

      str += "<gml:pointMember\n";

      IDirectPosition dpTemp = dPL.get(i);

      str += "<gml:pos srsDimension=" + '"' + 3 + '"' + ">" + dpTemp.getX()
          + " " + dpTemp.getY() + " " + dpTemp.getZ() + " </gml:pos>\n";
    }

    str += "</gml:MultiPoint>\n";

    return str;
  }

}

package fr.ign.cogit.geoxygene.sig3d.io.xml;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
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
 * Cette classe permet de charger des fichiers au format bati3D actuel
 * 
 * 
 * This class enables to load bati3D files
 * 
 */

public class ParserBATI3D extends DefaultHandler {

  boolean bParcelle = false;
  boolean bCenter = true;

  private final static Logger logger = LogManager.getLogger(ParserBATI3D.class
      .getName());

  // Liste d'objets geographiques, de faces et de noeuds
  private FT_FeatureCollection<IFeature> featCollection = new FT_FeatureCollection<IFeature>();
  private ArrayList<IOrientableSurface> lFaces = new ArrayList<IOrientableSurface>();

  private DirectPositionList lPoints = new DirectPositionList();

  String geometryName = "";

  String val = "";

  private double xmin;
  private double xmax;
  private double ymin;
  private double ymax;

  private double x;
  private double y;
  private double z;

  int dimension = 0;
  boolean ignore = true;

  /**
   * Constructeur par defaut
   */
  public ParserBATI3D() {
    super();
  }

  /**
   * Classe permettant de parser les fichiers XML Bati3D issus du Matis
   */
  public ParserBATI3D(String filePath) throws Exception {

    super();

    // Creation d'un lecteur
    XMLReader xr = XMLReaderFactory.createXMLReader();

    // Modification de la taille du tableau dans lequel est engrange les
    // donnees issues du fichier XML
    String id = "http://apache.org/xml/properties/input-buffer-size";
    Object value = new Integer(16 * 16 * 65536);
    xr.setProperty(id, value);

    // Pour utiliser le setContentHandler et le setContentHandler
    xr.setContentHandler(this);
    xr.setErrorHandler(this);

    // Debut du Parsing
    xr.parse(filePath);

  }

  /**
   * Elements Handlers
   */

  /**
   * Debut de document
   */
  @Override
  public void startDocument() {
    ParserBATI3D.logger.info("debut du parsing");
  }

  /**
   * Fin de document
   */
  @Override
  public void endDocument() {
    ParserBATI3D.logger.info("fin du parsing");
    ParserBATI3D.logger.info("LObj de fin du parsing : "
        + this.featCollection.size());
    // Adjacences.calculeAdjacences(LObj);
  }

  /**
   * Evenement releve a chaque debut de balise
   */
  @Override
  public void startElement(String url, String LName, String QName,
      Attributes atts) {

    if (LName.equalsIgnoreCase("xmin")) {
      this.geometryName = "xmin";
    }

    if (LName.equalsIgnoreCase("ymin")) {
      this.geometryName = "ymin";
    }

    if (LName.equalsIgnoreCase("xmax")) {
      this.geometryName = "xmax";
    }

    if (LName.equalsIgnoreCase("ymax")) {
      this.geometryName = "ymax";
    }

    String typeGeometrie = atts.getValue("name");

    if ("Solide".equals(typeGeometrie)) {
      this.ignore = false;

    }

    if ("Pan de toit".equalsIgnoreCase(typeGeometrie)) {

      this.ignore = true;
      this.featCollection.add(new DefaultFeature(new GM_Solid(this.lFaces)));
      this.lFaces = new ArrayList<IOrientableSurface>();
      this.lPoints = new DirectPositionList();

    }

    if (this.ignore) {
      return;
    }
    if (LName.equalsIgnoreCase("X")) {
      this.val = "X";
    }

    if (LName.equalsIgnoreCase("Y")) {
      this.val = "Y";
    }

    if (LName.equalsIgnoreCase("Z")) {
      this.val = "Z";
    }

  }

  // Evènement a chaque fin de balise
  @Override
  public void endElement(String url, String LName, String QName) {

    if ("polygone".equalsIgnoreCase(LName) && !this.ignore) {

      DirectPositionList dpl = new DirectPositionList();

      int nbPoints = this.lPoints.size();

      for (int i = nbPoints - 1; i > 0; i--) {
        dpl.add(this.lPoints.get(i));

      }

      this.lFaces.add(new GM_Polygon(new GM_LineString(dpl)));
      this.lPoints = new DirectPositionList();

    }

  }

  @Override
  public void ignorableWhitespace(char buff[], int offset, int len) {
    // Pour eliminer les blancs
  }

  // Pour recuperer les donnees suivant un tag
  @Override
  public void characters(char[] ch, int start, int end) {

    this.ignorableWhitespace(ch, start, end);

    if (this.geometryName != null) {

      if (this.geometryName.equalsIgnoreCase("xmin")) {
        this.xmin = Double.parseDouble(new String(ch, start, end));

        this.geometryName = null;

        return;
      }

      if (this.geometryName.equalsIgnoreCase("ymin")) {
        this.ymin = Double.parseDouble(new String(ch, start, end));
        this.geometryName = null;
        return;
      }

      if (this.geometryName.equalsIgnoreCase("xmax")) {
        this.xmax = Double.parseDouble(new String(ch, start, end));
        this.geometryName = null;
        return;
      }

      if (this.geometryName.equalsIgnoreCase("ymax")) {
        this.ymax = Double.parseDouble(new String(ch, start, end));
        this.geometryName = null;
        return;
      }

      return;
    }

    if (this.ignore) {

      return;
    }

    if (this.val.equalsIgnoreCase("X")) {
      this.x = Double.parseDouble(new String(ch, start, end));
      this.val = "";
    }
    if (this.val.equalsIgnoreCase("Y")) {
      this.y = Double.parseDouble(new String(ch, start, end));
      this.val = "";
    }

    if (this.val.equalsIgnoreCase("Z")) {
      this.z = Double.parseDouble(new String(ch, start, end));
      this.val = "";
      this.lPoints.add(new DirectPosition(this.x, this.y, this.z));

    }

  }

  /**
   * @return Returns the lObj.
   */
  public FT_FeatureCollection<IFeature> getFeatureCollection() {
    return this.featCollection;
  }

  public double getXmin() {
    return this.xmin;
  }

  public double getXmax() {
    return this.xmax;
  }

  public double getYmin() {
    return this.ymin;
  }

  public double getYmax() {
    return this.ymax;
  }

}

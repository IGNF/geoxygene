package fr.ign.cogit.geoxygene.sig3d.io.xml;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

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
 *  @author Benoit Poupeau, Eric Grosso, Aurélien Velten, Guillaume Ménégaux,
 *         Guillaume Touya
 * 
 * @version 0.1
 * 
 * Methode pour parser un fichier XML CityGML TODO gèrer les différentes LOD et
 * travailler sur le sens de couleurs TODO Reactiver les textures .... This
 * classe loads CityGML files
 * 
 *
 */
@Deprecated
public class ParserCityGML extends DefaultHandler {

  private final static Logger logger = LogManager.getLogger(ParserCityGML.class
      .getName());

  private double xini = Double.POSITIVE_INFINITY;

  private double yini = Double.POSITIVE_INFINITY;
  // Les différents type de classes que l'on est susceptbile de trouver
  public final static String[] CLASS_NAMES = { "Autre", "PlantCover", "Road",
      "GenericCityObject", "LandUse", "CityFurniture", "Building",
      "ReliefFeature", "WaterBody" };


  // Les couleurs associées aux classe
  public final static Color[] COLORS = { new Color(128, 128, 128),
      new Color(0, 128, 0), new Color(85, 85, 85), new Color(255, 255, 180),
      new Color(255, 0, 0), new Color(255, 255, 0), new Color(230, 100, 80),
      new Color(255, 200, 155), new Color(0, 0, 255) };

  // L'ID de la classe actuelle
  private int IDClass = 0;

  // Creation d'une string permettant de conserver les informations issues du
  // "parsing"
  private String LIST = new String();

  private ArrayList<IFeatureCollection<IFeature>> LObj = new ArrayList<IFeatureCollection<IFeature>>(
      ParserCityGML.CLASS_NAMES.length + 1);

  // La liste des faces formant le corps
  private ArrayList<IOrientableSurface> lPolygones = new ArrayList<IOrientableSurface>();

  // La liste des sommets formant la face
  private DirectPositionList dpl = new DirectPositionList();

  // Le nombre de points parsés
  private int p = 0;

  // Le nombre de faces
  private int f = 0;

  // Le nombre de batiments
  private int b = 0;

  private GM_Polygon face = null;
  private GM_Point point = new GM_Point();

  private boolean ignore = false;
  boolean bCenter = true;

  // Centre
  private DirectPosition dpCenter = new DirectPosition();

  // Objet Geographique cree lors du "parsing"
  private IFeature actualFeature;

  boolean bdimension = false;

  // permette de conserver les coordonnées
  // lors d'un chargement de poslist sur plusieurs lignes
  private double xTemp = Double.NaN;
  private double yTemp = Double.NaN;
  private int coordTemp = 0;

  /** Constructeur par defaut */
  public ParserCityGML() {
    super();
  }

  /** Parser CityModel */
  public ParserCityGML(String filePath) throws Exception {
    super();

    /*
     * Initialisation des tableaux pour les différentes couches
     */

    for (@SuppressWarnings("unused") String element : ParserCityGML.CLASS_NAMES) {

      this.LObj.add(new FT_FeatureCollection<IFeature>());

    }

    // Creation d'un lecteur
    XMLReader xr = XMLReaderFactory.createXMLReader();

    // Taille du tableau dans lequel est engrange les donnees issues du
    // fichier XML
    String id = "http://apache.org/xml/properties/input-buffer-size";
    Object value = new Integer(24 * 24 * 65536);
    xr.setProperty(id, value);

    // Pour utiliser le setContentHandler et le setContentHandler
    xr.setContentHandler(this);
    xr.setErrorHandler(this);

    // Debut du Parsing
    xr.parse(filePath);
  }

  /** Debut de document */
  @Override
  public void startDocument() {
    ParserCityGML.logger.info("Début du parsing...");
  }

  /** Fin de document */
  @Override
  public void endDocument() {
    ParserCityGML.logger.info("Fin du parsing");

    ParserCityGML.logger.info(this.p + " points\t" + this.f + " faces\t"
        + this.b + " bâtiments");
  }

  /** Evenement releve a chaque debut de balise */
  @Override
  public void startElement(String url, String LName, String QName,
      Attributes atts) {

    // On ignore les éléments suivantsz

    if (QName.equalsIgnoreCase("gml:Envelope")) {
      this.ignore = true;
    }

    /*
     * if (QName.equalsIgnoreCase("opening")) { this.ignore = true; return; } if
     * (QName.equalsIgnoreCase("Window")) { this.ignore = true; }
     */

    /*
     * if (QName.equalsIgnoreCase("gml:interior")) { this.ignore = true; }
     */

    if (QName.equalsIgnoreCase("lod4Geometry")) {
      this.ignore = true;
    }

    if (this.ignore) {

      return;
    }

    if (QName.equals("gml:pos")) {
      this.LIST = "point";
    }

    if (QName.equals("gml:posList")) {
      this.LIST = "liste_point";
    }

    // On indique le numéro de classe
    if (QName.equals("PlantCover")) {
      this.IDClass = 1;
    }

    if (QName.equals("Road")) {
      this.IDClass = 2;
    }

    if (QName.equals("GenericCityObject")) {
      this.IDClass = 3;
    }

    if (QName.equals("LandUse")) {
      this.IDClass = 4;
    }

    if (QName.equals("CityFurniture")) {
      this.IDClass = 5;
    }

    if (QName.equals("Building")) {
      this.IDClass = 6;
    }

    if (QName.equals("ReliefFeature")) {
      this.IDClass = 7;
    }

    if (QName.equals("WaterBody")) {
      this.IDClass = 8;
    }

  }

  // Evènement a chaque fin de balise
  @Override
  public void endElement(String url, String name, String QName) {
    if ((QName.equals("gml:surfaceMember") || QName.equals("gml:Triangle"))
        && !this.ignore) {

      if (this.face != null) {
        this.lPolygones.add(this.face);
      }
      return;
    }

    if (QName.equalsIgnoreCase("gml:Envelope")) {
      this.ignore = false;
      return;
    }

    /*
     * if(QName.equalsIgnoreCase("GenericCityObject")){ ignore=false; return; }
     */

    /*
     * if (QName.equalsIgnoreCase("gml:interior")) { this.ignore = false;
     * return; }
     */

    if (QName.equalsIgnoreCase("lod4Geometry")) {
      this.ignore = false;
      return;
    }

    /*
     * if (QName.equalsIgnoreCase("opening")) { this.ignore = false; return; }
     */

    // Recuperer les informations concernant les faces
    if (QName.equals("gml:exterior") && !this.ignore) {

      int nbPoints = this.dpl.size();

      if (nbPoints > 2) {

        // Fermeture si nécessaire
        IDirectPosition p1 = this.dpl.get(0);
        IDirectPosition p2 = this.dpl.get(nbPoints - 1);

        if (!p1.equals(p2, 0.001)) {

          this.dpl.add(p1);
        }

        if (this.dpl.size() > 3) {
          // Instanciation de la facade et ajout dans la liste de
          // faces

          GM_LineString ls = new GM_LineString(this.dpl);
          this.face = new GM_Polygon(ls);

        }
      }
      // Nettoyage de la liste LPoint
      this.dpl = new DirectPositionList();
      this.f++;
    }

    // Recuperer les informations concernant les faces
    if (QName.equals("gml:interior") && !this.ignore) {
      int nbPoints = this.dpl.size();

      if (nbPoints > 2) {

        /*
         * if(this.face.boundary().sizeInterior() == 0) { this.face = new
         * GM_Polygon(new GM_LineString(this.face.boundary
         * ().getExterior().getNegative().coord())); }
         */

        // Fermeture si nécessaire
        IDirectPosition p1 = this.dpl.get(0);
        IDirectPosition p2 = this.dpl.get(nbPoints - 1);

        if (!p1.equals(p2, 0.001)) {

          this.dpl.add(p1);
        }

        if (this.dpl.size() > 3) {
          // Instanciation de la facade et ajout dans la liste de
          // faces

          GM_LineString ls = new GM_LineString(this.dpl);
          this.face.boundary().addInterior(
              new GM_Ring(new GM_LineString(ls.coord())));

          // Ajout de la face
          // this.LFace.add(this.face);
        }
      }
      // Nettoyage de la liste LPoint
      this.dpl = new DirectPositionList();
      // this.f++;
    }

    if ((QName.equals("gml:Solid") || QName.equals("cityObjectMember"))
        && !this.ignore) {

      if (this.lPolygones.size() > 0) {

        GM_MultiSurface<IPolygon> sol = new GM_MultiSurface<IPolygon>(
            this.lPolygones);
        this.actualFeature = new DefaultFeature(sol);
        this.LObj.get(this.IDClass).add(this.actualFeature);
      }

      // On ne connait pas la classe du prochain objet
      this.IDClass = 0;
      this.lPolygones = new ArrayList<IOrientableSurface>();

      this.b++;
    }

    if (QName.equals("gml:LineString") && !this.ignore) {

      if (this.dpl.size() >= 2) {
        GM_LineString ls = new GM_LineString(this.dpl);
        this.LObj.get(this.IDClass).add(new DefaultFeature(ls));

      }
      this.dpl.clear();

    }

    if (QName.equals("gml:pos") && this.ignore == false) {
      if (this.bCenter) {

        this.bCenter = false;
      }
      // Saute suite au changement de modele de données

      IDirectPosition dpTemp = this.point.getPosition();

      if (!this.dpl.getList().contains(dpTemp)) {
        this.dpl.add(dpTemp);
      }

    }

    if (QName.equals("gml:posList") && this.ignore == false) {
      this.LIST = "";
      if (this.bCenter) {
        this.dpCenter = (DirectPosition) this.point.getPosition();
        this.bCenter = false;
      }

      this.coordTemp = 0;
    }

  }

  @Override
  public void ignorableWhitespace(char buff[], int offset, int len) {
    // Pour eliminer les blancs
  }

  // Pour recuperer les donnees suivant un tag
  @Override
  public void characters(char[] ch, int start, int end) {
    if (this.ignore) {
      return;
    }

    this.ignorableWhitespace(ch, start, end);

    if (this.LIST.equals("point")) {
      this.point = new GM_Point();
      String[] s = null;
      Vector<String> t = new Vector<String>();
      String chaine = new String(ch, start, end);
      s = (chaine.split(" "));
      for (String element : s) {
        t.add(element);
      }

      Iterator<String> it = t.iterator();
      double val1 = 0;
      if (it.hasNext()) {
        val1 = Double.valueOf(it.next()).doubleValue();
      }
      double val2 = 0;
      if (it.hasNext()) {
        val2 = Double.valueOf(it.next()).doubleValue();
      }
      double val3 = 0;
      if (it.hasNext()) {
        val3 = Double.valueOf(it.next()).doubleValue();
      }

      if (Double.isInfinite(this.xini)) {
        this.xini = val1;
        this.yini = val2;

      }
      this.point = new GM_Point(new DirectPosition(val1 - this.xini, val2
          - this.yini, val3));

      this.LIST = "";
      this.p++;
    }

    // Cas d'une liste de points
    if (this.LIST.equals("liste_point")) {

      String[] u = null;
      String[] uModifie = null;
      int nb = 0;

      String chaine = new String(ch, start, end);

      u = chaine.split(" ");
      uModifie = new String[u.length + this.coordTemp];

      for (int i = 0; i < uModifie.length; i++) {
        uModifie[i] = "";
      }

      // Si les 3 coordonnées sont sur 2 lignes différents, il faut
      // conserver les
      // coordonnées lues précédemment
      if (this.coordTemp == 1) {

        uModifie[0] = String.valueOf(this.xTemp);
      }

      if (this.coordTemp == 2) {

        uModifie[0] = String.valueOf(this.xTemp);
        uModifie[1] = String.valueOf(this.yTemp);
      }
      nb = this.coordTemp;

      for (String element : u) {
        if (element.equals("")) {
          continue;
        }
        if (element.equalsIgnoreCase("\n")) {
          continue;
        }
        if (element.equalsIgnoreCase("\r")) {
          continue;
        }
        if (element.isEmpty()) {
          continue;
        }
        uModifie[nb] = element;
        nb = nb + 1;
      }

      // il s'agit du nombre de points en sachant qu'il peut y avoir des
      // coordonnées sur plusieurs lignes
      int nbPoints = nb / 3;

      for (int i = 0; i < nbPoints; i++) {
        if (uModifie[3 * i].equals("")) {
          break;
        }

        if (Double.isInfinite(this.xini)) {
          this.xini = Double.valueOf(uModifie[3 * i]).doubleValue();
          this.yini = Double.valueOf(uModifie[3 * i + 1]).doubleValue();

        }

        DirectPosition dp = new DirectPosition(Double.valueOf(uModifie[3 * i])
            .doubleValue() - this.xini, Double.valueOf(uModifie[3 * i + 1])
            .doubleValue() - this.yini, Double.valueOf(uModifie[3 * i + 2])
            .doubleValue());

        this.p++;

        int nbPActu = this.dpl.getList().size();

        // On vérifie qu'il n'y ait pas de doublons
        if (nbPActu > 0) {
          IDirectPosition dpTemp = this.dpl.get(nbPActu - 1);
          if (!(dp.equals(dpTemp))) {

            this.dpl.add(dp);
          }

        } else {
          this.dpl.add(dp);
        }

      }

      this.coordTemp = nb - 3 * nbPoints;

      if (this.coordTemp == 1) {

        this.xTemp = Double.parseDouble(uModifie[nb - 1]);
      }

      if (this.coordTemp == 2) {

        this.xTemp = Double.parseDouble(uModifie[nb - 2]);
        this.yTemp = Double.parseDouble(uModifie[nb - 1]);
      }
    }

  }

  /**
   * @return Returns the dPcentre.
   */
  public DirectPosition getDBCenter() {
    return this.dpCenter;
  }

  /**
   * @param pCenter The dPcentre to set.
   */
  public void setDPcentre(DirectPosition pCenter) {
    this.dpCenter = pCenter;
  }

  /**
   * @return Returns the lObj.
   */
  public ArrayList<IFeatureCollection<IFeature>> getLFeatureCollection() {

    return this.LObj;
  }

}

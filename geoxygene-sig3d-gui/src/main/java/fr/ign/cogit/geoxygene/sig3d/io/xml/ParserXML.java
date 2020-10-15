package fr.ign.cogit.geoxygene.sig3d.io.xml;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
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
 * @author benoit poupeau
 * 
 * @version 0.1
 * 
 * Cette classe permet de charger des objets de la vieille version Bati3D
 * 
 * TODO vérifier le comportement pour un objet surfacique ou linéaire
 * 
 * This class enables to load old bati3D files
 * 
 */
@Deprecated
public class ParserXML extends DefaultHandler {

  boolean bParcelle = false;
  boolean bCenter = true;

  double xmin = Double.POSITIVE_INFINITY;

  double ymin = Double.POSITIVE_INFINITY;

  double zmin = Double.POSITIVE_INFINITY;

  double xmax = Double.NEGATIVE_INFINITY;

  double ymax = Double.NEGATIVE_INFINITY;

  double zmax = Double.NEGATIVE_INFINITY;

  private final static Logger logger = LogManager.getLogger(ParserXML.class
      .getName());

  // Liste d'objets geographiques, de faces et de noeuds
  private IFeatureCollection<IFeature> featureCollection = new FT_FeatureCollection<IFeature>();
  private ArrayList<IOrientableSurface> lFaces = new ArrayList<IOrientableSurface>();
  private ArrayList<ICurve> lLines = new ArrayList<ICurve>();
  private IDirectPositionList lPoint = new DirectPositionList();

  // Centre
  private IDirectPosition dpCenter = new DirectPosition();

  // Elements caracterisant l'objet geographique
  private IOrientableSurface face = new GM_OrientableSurface();
  private ICurve line = new GM_Curve();
  private IPoint vertex = new GM_Point();

  // DirectPosition utilise pour renseigner les noeuds lors du "parsing"
  private IDirectPosition dp = new DirectPosition();

  // Objet Geographique cree lors du "parsing"
  private IFeature objgeo = null;

  // Creation d'une string permettant de conserver les informations issues du
  // "parsing"
  private String list = new String();

  // Booleen pour eviter de generer des doublons lors du "parsing"
  boolean isDouble;

  // Booleen pour recuperer la dimension d'un objet
  boolean bdimension = false;

  // Pour la couleur des objets
  boolean bColor = false;

  /**
   * Constructeur par defaut
   */
  public ParserXML() {
    super();
  }

  /**
   * Classe permettant de parser les fichiers XML Bati3D issus du Matis
   */
  public ParserXML(String filePath) throws Exception {

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
    ParserXML.logger.info("Début du parsing");
  }

  /**
   * Fin de document
   */
  @Override
  public void endDocument() {
    ParserXML.logger.info("Fin du parsing");
    ParserXML.logger.info("LObj de fin du parsing : "
        + this.featureCollection.size());

  }

  /**
   * Evenement releve a chaque debut de balise
   */
  @Override
  public void startElement(String url, String LName, String QName,
      Attributes atts) {

    // Tester le LName pour creer le bon objet
    // Recuperer les informations concernant les points
    if (LName.equals("point")) {
      if (!this.bParcelle) {
        this.vertex = new GM_Point();
      }
    }

    // Eviter de prendre les elements d'une parcelle
    if (LName.equals("parcelle")) {
      this.bParcelle = true;
    }

    // Recuperation des informations concernant les faces et facades
    if (LName.equals("faces") || LName.equals("triangle")) {
      this.bParcelle = false;
    }

    // Initialiser les coordonnees des points : x, y, z
    if (LName.equals("x")) {
      this.list = "x";
    }

    if (LName.equals("y")) {
      this.list = "y";
    }

    if (LName.equals("z")) {
      this.list = "z";
    }

    if (LName.equals("dimension")) {
      this.bdimension = true;
    }

    if (LName.equals("couleur")) {
      this.bColor = true;
    }

  }

  // Evènement a chaque fin de balise
  @Override
  public void endElement(String url, String LName, String QName) {

    if (LName.equals("batiment") || LName.equals("couche")
        || LName.equals("cavite")) {

      if (this.lFaces.size() > 0) {
        // Initialiser l'objet geographique et ajout dans une liste
        // d'objets

        GM_Solid s = new GM_Solid(this.lFaces);
        this.objgeo = new DefaultFeature(s);

        // objgeo = new ObjetGeographique(LFace,color,false);
        // Preciser le nom
        this.featureCollection.add(this.objgeo);

        // Nettoyage de la liste LFace
        this.lFaces = new ArrayList<IOrientableSurface>();

      }
    }

    if (LName.equals("mnt") || LName.equals("route")) {
      if (this.lFaces.size() > 0) {

        GM_Solid s = new GM_Solid(this.lFaces);
        this.objgeo = new DefaultFeature(s);

        // Preciser le nom
        this.featureCollection.add(this.objgeo);

        // Nettoyage de la liste LFace
        this.lFaces = new ArrayList<IOrientableSurface>();
      }
    }

    if (LName.equals("river")) {

      if (this.lLines.size() > 0) {
        // Initialiser l'objet geographique et ajout dans une liste
        // d'objets

        this.objgeo = new DefaultFeature(this.lLines.get(0));

        // Preciser le nom
        this.featureCollection.add(this.objgeo);

        // Nettoyage de la liste LFace
        this.lLines.clear();

      }
    }

    if (LName.equals("forage")) {

      if (this.lPoint.size() > 0) {
        // Initialiser l'objet geographique et ajout dans une liste
        // d'objets

        this.objgeo = new DefaultFeature(new GM_Point(this.lPoint.get(0)));

        this.featureCollection.add(this.objgeo);
        // Nettoyage de la liste LFace
        this.lPoint = new DirectPositionList();
      }
    }

    // Recuperer les informations concernant les courbes et segments
    if (LName.equals("line")) {
      for (int i = 0; i < this.lPoint.size(); i++) {
        IDirectPosition pointN = this.lPoint.get(i);
        for (int j = 0; j < this.lPoint.size(); j++) {
          IDirectPosition pointN2 = this.lPoint.get(j);

          if (!pointN.equals(pointN2)) {
            if (pointN.equals(pointN2, 0.5)) {
              this.lPoint.remove(pointN2);
            }
          }
        }// boucle j
      }// boucle i

      if (this.lPoint.size() >= 2) {
        // Instanciation de la ligne et ajout dans la liste de lignes
        this.line = new GM_Curve();
        DirectPositionList dpL = new DirectPositionList();

        // On rajoute le premier point a la fin de la liste, pour avoir
        // des
        // polygones fermes
        this.lPoint.add(this.lPoint.get(0));

        int nbPoint = this.lPoint.size();

        for (int i = 0; i < nbPoint; i++) {

          IDirectPosition dp1 = this.lPoint.get(i);
          dpL.add(dp1);
        }// Iterator iterLDirectPosition

        this.line = new GM_LineString(dpL);

        // Ajout de la courbe
        this.lLines.add(this.line);
      }
      // Nettoyage de la liste LPoint
      this.lPoint = new DirectPositionList();

    }

    // Recuperer les informations concernant les faces
    if (LName.equals("face") || LName.equals("triangle")) {

      ParserXML.logger.debug("cas faces / triangles");

      // Nettoyage de la faéade
      for (int i = 0; i < this.lPoint.size(); i++) {
        IDirectPosition pointN = this.lPoint.get(i);
        for (int j = 0; j < this.lPoint.size(); j++) {
          IDirectPosition pointN2 = this.lPoint.get(j);

          if (!pointN.equals(pointN2)) {
            if (pointN.equals(pointN2, 0.5)) {
              this.lPoint.remove(pointN2);
            }
          }
        }// boucle j
      }// boucle i

      if (this.lPoint.size() >= 3) {
        // Instanciation de la facade et ajout dans la liste de faces
        this.face = new GM_OrientableSurface();
        DirectPositionList dpL = new DirectPositionList();

        // On rajoute le premier point a la fin de la liste, pour avoir
        // des
        // polygones fermes
        this.lPoint.add(this.lPoint.get(0));

        int nbPoint = this.lPoint.size();

        for (int i = 0; i < nbPoint; i++) {
          IDirectPosition dp1 = this.lPoint.get(i);
          dpL.add(dp1);
        }// Iterator iterLDirectPosition

        // Modification changement modèle de données

        GM_LineString ls = new GM_LineString(this.lPoint);

        this.face = new GM_Polygon(ls);

        // Ajout pour que les points soients orientés dans le bon sens
        // Pour optimiser l'affichage (on ne garde que les faces non
        // cacgées à l'affichage
        PlanEquation eqp = new PlanEquation(this.face);

        Vecteur v = eqp.getNormale();
        v.normalise();
        /*
         * if (Math.abs(v.getZ()) < 0.1) { GM_Ring gmRing = new
         * GM_Ring(ls.getNegative()); face = new GM_Polygon(gmRing);
         * LFace.add(face); } else if ((Math.abs(v.getX()) > 0.1) ||
         * (Math.abs(v.getY()) > 0. GM_Ring gmRing = new GM_Ring(ls); face = new
         * GM_Polygon(gmRing); LFace.add(face);
         * 
         * } else {
         */

        GM_Ring gmRing = new GM_Ring(ls);
        this.face = new GM_Polygon(gmRing);
        this.lFaces.add(this.face);

        // }

        // Ajout de la face

      }
      // Nettoyage de la liste LPoint
      this.lPoint = new DirectPositionList();

    }

    // Recuperer les informations concernant les faces
    if (LName.equals("facade")) {

      ParserXML.logger.debug("facade");
      // Nettoyage de la faéade
      for (int i = 0; i < this.lPoint.size(); i++) {
        IDirectPosition pointN = this.lPoint.get(i);
        for (int j = 0; j < this.lPoint.size(); j++) {
          IDirectPosition pointN2 = this.lPoint.get(j);
          if (!pointN.equals(pointN2)) {
            if (pointN.equals(pointN2, 0.5)) {
              this.lPoint.remove(pointN2);
            }
          }
        }// boucle j
      }// boucle i

      if (this.lPoint.size() >= 3) {
        // Instanciation de la facade et ajout dans la liste de faces
        this.face = new GM_OrientableSurface();
        DirectPositionList dpL = new DirectPositionList();

        // On rajoute le premier point a la fin de la liste, pour avoir
        // des
        // polygones fermes
        this.lPoint.add(this.lPoint.get(0));

        int nbPoint = this.lPoint.size();

        for (int i = 0; i < nbPoint; i++) {

          IDirectPosition dp1 = this.lPoint.get(i);
          dpL.add(dp1);
        }// Iterator iterLDirectPosition
         // face.setCoord(dpL);

        GM_LineString ls = new GM_LineString(this.lPoint);
        this.face = new GM_Polygon(ls);

        // Ajout pour que les points soients orientés dans le bon sens
        // Pour optimiser l'affichage (on ne garde que les faces non
        // cachées à l'affichage
        PlanEquation eqp = new PlanEquation(this.face);

        Vecteur v = eqp.getNormale();
        v.normalise();

        /*
         * if (Math.abs(v.getZ()) < 0.1) { GM_Ring gmRing = new
         * GM_Ring(ls.getNegative()); face = new GM_Polygon(gmRing);
         * LFace.add(face); } else if ((Math.abs(v.getX()) > 0.1) ||
         * (Math.abs(v.getY()) > 0.1)) { GM_Ring gmRing = new
         * GM_Ring(ls.getNegative()); face = new GM_Polygon(gmRing);
         * LFace.add(face);
         * 
         * } else {
         */

        GM_Ring gmRing = new GM_Ring(ls.getNegative());
        this.face = new GM_Polygon(gmRing);
        this.lFaces.add(this.face);

      }

      // Nettoyage de la liste LPoint*/
      this.lPoint = new DirectPositionList();
    }

    // Recuperer les points
    if (LName.equals("point")) {
      // Ajout du point dans la liste
      if (!this.bParcelle) {
        // Test permettant de recuperer, pour le premier objet parse,
        // son centre et permettre
        // ainsi une translation de l'ensemble des objets dans le repère
        // de la scène
        if (this.bCenter) {
          this.dpCenter = this.vertex.getPosition();
          this.bCenter = false;
        }
        // Saute suite au changement de modèle de données
        // if (!noeud.getLface().contains(face))
        // noeud.getLface().add(face);

        // modif
        // if (!LPoint.contains(noeud.getPosition())){
        // LPoint.add(noeud.getPosition())};

        int nbPoints = this.lPoint.size();

        boolean trouve = false;
        for (int i = 0; i < nbPoints; i++) {
          IDirectPosition dpTemp = this.lPoint.get(i);

          if (dpTemp.equals(this.vertex.getPosition(), 0.1)) {
            trouve = true;
            break;

          }

        }

        if (!trouve) {
          this.lPoint.add(this.vertex.getPosition());

        }

      }
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

    // Recuperation de la dimension
    if (this.bdimension) {
      // Enlever les blancs de la string
      if (ch.length > 1) {
        this.bdimension = false;

      } else {

        this.bdimension = false;
      }
    }

    // Recuperation des donnees a rentrer dans les listes de points
    // coordonnees X
    if (this.bParcelle) {
      this.list = "";
    } else {
      if (this.list == "x") {
        this.dp = new DirectPosition();

        double x = Double.parseDouble(new String(ch, start, end));

        this.xmin = Math.min(x, this.xmin);
        this.xmax = Math.max(x, this.xmax);
        this.dp.setX(x);
        this.list = "";
      }

      // coordonnees y
      if (this.list == "y") {
        double y = Double.parseDouble(new String(ch, start, end));

        this.ymin = Math.min(y, this.ymin);
        this.ymax = Math.max(y, this.ymax);

        this.dp.setY(y);
        this.list = "";
      }

      // coordonnees z
      if (this.list == "z") {

        double z = Double.parseDouble(new String(ch, start, end));

        this.zmin = Math.min(z, this.zmin);
        this.zmax = Math.max(z, this.zmax);

        this.dp.setZ(z);
        this.list = "";

        // boolean a faux
        this.isDouble = false;

        // Test sur l'ensemble des faces
        Iterator<IOrientableSurface> iter0 = this.lFaces.iterator();
        while (iter0.hasNext()) {
          // Surface a tester
          IOrientableSurface sf = iter0.next();
          IDirectPositionList lPoints = sf.coord();

          int nbPoints = lPoints.size();
          // Test sur l'ensemble des noeuds
          for (int i = 0; i < nbPoints; i++) {
            IDirectPosition pt = lPoints.get(i);
            if (pt.equals(this.dp, 0.5)) {
              this.isDouble = true;
              this.vertex.setPosition(pt);
            }
          }

        }// Iterator iter0
         // Creation du noeud
        if (!this.isDouble) {
          this.vertex = new GM_Point(this.dp);

        }
      }
    }
  }

  public double getXmin() {
    return this.xmin;
  }

  public double getYmin() {
    return this.ymin;
  }

  public double getZmin() {
    return this.zmin;
  }

  public double getXmax() {
    return this.xmax;
  }

  public double getYmax() {
    return this.ymax;
  }

  public double getZmax() {
    return this.zmax;
  }

  /**
   * @return Returns the dPcentre.
   */
  public IDirectPosition getDPcentre() {
    return this.dpCenter;
  }

  /**
   * @param pcentre The dPcentre to set.
   */
  public void setDPcentre(DirectPosition pcentre) {
    this.dpCenter = pcentre;
  }

  /**
   * @return Returns the lObj.
   */
  public IFeatureCollection<IFeature> getLObj() {
    return this.featureCollection;
  }

  /**
   * @param obj The lObj to set.
   */
  public void setLObj(IFeatureCollection<IFeature> obj) {
    this.featureCollection = obj;
  }

}

package fr.ign.cogit.geoxygene.sig3d.semantic;

import java.awt.Color;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.media.j3d.Appearance;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.Material;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.media.j3d.TriangleStripArray;
import javax.vecmath.Color3f;
import javax.vecmath.Color4f;
import javax.vecmath.Point3d;

import org.apache.log4j.Logger;

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;

import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.Messages;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Triangle;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * 
 * @version 0.1
 * 
 *          Classe pour charger des MNT depuis POSTGIS (données de type raster)
 * 
 *          Class to load DTM from PostGIS raster.
 * 
 * 
 */
public class DTMPostGIS extends AbstractDTM {

  public int echantillonage = 1;

  public static double CONSTANT_OFFSET = 0;

  /*
   * Les paramètres du MNT xIni : il s'agit du X du coin supérieur gauche du MNT
   * yIni : il s'agit du Y du coin supérieur gauche du MNT pasX : il s'agit du
   * pas en X du MNT (echantillonnage inclus) pasY : il s'agit du pas en Y du
   * MNT (echantillonnage inclus) nX : il s'agit du nombre de mailles en X à
   * afficher nY : il s'agit du nombre de mailles en Y à afficher strip : il
   * s'agit de la liste de triangles formant le MNT exageration : il s'agi du
   * coefficient en Z que l'on applique bgeo : il s'agit de la BranchGroup
   * représentant le MNT
   */
  protected double xIni;
  protected double yIni;
  protected double zMax;
  protected double zMin;
  protected double stepX;
  protected double stepY;
  protected int nX;
  protected int nY;
  protected TriangleStripArray strip;
  protected double noDataValue;

  // Echantillonnage
  protected int sampling;

  protected Color4f[] color4fShade = null;

  private final static Logger logger = Logger.getLogger(DTMPostGIS.class
      .getName());

  private String host = "";
  private String port = "";
  private String base = "";
  private String tablename = "";
  private String user = "";
  private String pw = "";

  /**
   * Création d'un MNT chargé depuis PostGIS
   * 
   * @param host hote de la BDD
   * @param port port de la BDD
   * @param base nom de la base de données
   * @param tablename nom de la table contenant le MNT
   * @param user utilisateur
   * @param pw mot de passe
   * @param layerName nom de la couché créée
   * @param fill le MNT est il rempli ?
   * @param exager Quelle exaggération ?
   * @param colorGradation dégradé de couleur appliqué
   */
  public DTMPostGIS(String host, String port, String base, String tablename,
      String user, String pw, String layerName, boolean fill, int exager,
      Color[] colorGradation) {

    super();
    this.exageration = exager;
    this.path = null;
    this.imagePath = null;
    this.colorShade = colorGradation;
    this.imageEnvelope = null;
    this.isFilled = fill;
    this.layerName = layerName;
    this.pw = pw;
    this.user = user;

    this.host = host;
    this.base = base;
    this.tablename = tablename;
    this.port = port;

    try {
      this.bgLayer.addChild(representationProcess(layerName, fill, exager,
          colorGradation));
    } catch (SQLException e) {

      e.printStackTrace();
    }

  }

  /**
   * @throws SQLException
   */
  public void refresh(String layerName, boolean fill, int exager,
      Color[] colorGradation) throws SQLException {

    BranchGroup parent = null;

    if (this.isVisible()) {
      parent = (BranchGroup) this.bgLayer.getParent();
      this.bgLayer.detach();
    }

    this.bgLayer.removeAllChildren();
    this.bgLayer.addChild(this.representationProcess(layerName, fill, exager,
        colorGradation));

    if (parent != null) {
      parent.addChild(this.bgLayer);
    }

  }

  private ResultSet getLine(int indexLine, int nbCol, Connection conn)
      throws SQLException {

    Statement s = conn.createStatement();

    String sql = "SELECT y, ST_Value(rast, 1, y, " + indexLine
        + ",  false) As b1val FROM mnt CROSS JOIN generate_series(1, " + nbCol
        + "," + this.echantillonage + ") As y ;";
    logger.debug(sql);
 
    return s.executeQuery(sql);

  }

  /**
   * Calcul l'objet 3D permettant de représenter un MNT en appliquant un
   * dégradé.
   * 
   * @param file
   * @param layerName
   * @param fill
   * @param exager
   * @param colorGradation
   * @return un objet Java3D représentant la forme du MNT
   * @throws SQLException
   */
  private Shape3D representationProcess(String layerName, boolean fill,
      int exager, Color[] colorGradation) throws SQLException {

    int nbElemCouleur = colorGradation.length;
    this.color4fShade = new Color4f[nbElemCouleur];

    for (int i = 0; i < nbElemCouleur; i++) {
      this.color4fShade[i] = new Color4f(colorGradation[i]);
    }

    // Les informations concernant le coin supérieur gauche du MNT
    double shiftX = 0;
    double shiftY = 0;

    // Le nombre d'éléments présents dans le MNT
    int nbpoints = 0;

    // Valeur indiquant l'absence de données
    this.noDataValue = -9999.0;

    // Lecture du fichier et récupèration des différentes valeurs citées
    // précédemment

    this.zMin = Double.POSITIVE_INFINITY;
    this.zMax = Double.NEGATIVE_INFINITY;

    // Liste des entités que l'on souhaite charger
    java.sql.Connection conn;

    // Création des paramètres de connexion
    String url = "jdbc:postgresql://" + host + ":" + port + "/" + base;
    logger.debug(url);

    DTMPostGIS.logger.info(Messages.getString("PostGIS.Try") + url);
    conn = DriverManager.getConnection(url, user, pw);

    String requestSelect = "SELECT ST_Height(rast), ST_width(rast), ST_UpperLeftX(rast), ST_UpperLeftY(rast), ST_PixelHeight(rast), ST_PixelWidth(rast) from "
        + tablename;

    // System.out.println(requestSelect);
    logger.debug(requestSelect);

    Statement s = conn.createStatement();

    ResultSet rMeta = s.executeQuery(requestSelect);

    // Une seule ligne normalement
    rMeta.next();

    String str_height = rMeta.getString(1);
    int nrows = Integer.parseInt(str_height);

    String str_width = rMeta.getString(2);
    int ncols = Integer.parseInt(str_width);

    String str_PixelH = rMeta.getString(5);
    double cellesizeY = Double.parseDouble(str_PixelH);

    String str_PixelW = rMeta.getString(6);
    double cellesizeX = Double.parseDouble(str_PixelW);

    String str_upLeftX = rMeta.getString(3);

    shiftX = Double.parseDouble(str_upLeftX);

    String str_upLeftY = rMeta.getString(4);
    shiftY = Double.parseDouble(str_upLeftY) - cellesizeY * nrows;

    String sum = "select ST_SummaryStats(rast) from " + tablename;
    Statement s1 = conn.createStatement();

    ResultSet rStats = s1.executeQuery(sum);
    rStats.next();
    String res = rStats.getString(1);

    String[] tabPars = res.split(",");

    String strZmax = tabPars[tabPars.length - 1];
    strZmax = strZmax.substring(0, strZmax.length() - 1);

    zMax = Double.parseDouble(strZmax);
    zMin = Double.parseDouble(tabPars[tabPars.length - 2]);

    logger.debug(requestSelect);

    // On prépare le nombre de lignes
    nrows = nrows / this.echantillonage;
    ncols = ncols / this.echantillonage;

    // Tab contiendra les Z de la ligne précédente
    int[] tab = new int[nrows - 1];

    for (int i = 0; i < nrows - 1; i++) {

      tab[i] = 2 * ncols;
    }

    // Création de la géométrie Java3D qui accueillera le MNT
    TriangleStripArray strp = null;

    // La construction se fait en TriangleStripArray
    // C'est un mode de construction efficace et rapide
    // Toutefois chaque point se retrouve chargé 2 fois en mémoire
    // (un test avec un autre mode de représentation indexé n'a pas été
    // concluant)

    strp = new TriangleStripArray(2 * ncols * (nrows - 1),
        GeometryArray.COORDINATES | GeometryArray.COLOR_4
            | GeometryArray.NORMALS, tab);

    strp.setCapability(GeometryArray.ALLOW_COORDINATE_READ);
    strp.setCapability(Geometry.ALLOW_INTERSECT);
    // On renseigne les différentes valeurs donnant des informations sur
    // le MNT
    this.xIni = shiftX;
    this.yIni = shiftY;
    // on initialize la ligne précédente
    double[] lignePred = new double[ncols];

    double maxy = ((nrows - 1) * cellesizeY + shiftY);

    int currentRow = 1;

    ResultSet rs = getLine(1, ncols, conn);
    // On remplit la premiere ligne : inittialisation
    for (int i = 0; i < ncols; i++) {
      rs.next();
      lignePred[i] = Double.parseDouble(rs.getString(2));

      // lignePred[i] = Double.parseDouble(result[i * this.echantillonage]);

    }

    // On passe des lignes pour sous àchantillonner
    for (int i = 0; i < this.echantillonage; i++) {
      currentRow++;

    }

    Point3d pointAncien = null;
    Point3d nouvePoint = null;

    // On traite le fichier ligne par ligne
    for (int j = 1; j < nrows; j++) {

      // On charge la ligne en mémoire

      rs = getLine(currentRow, ncols, conn);

      // Pour chaque colonne que l'on souhaite récupèrer
      for (int i = 0; i < ncols; i++) {

        rs.next();
        // On récupère le Z du point que l'on traite actuellement
        // Le Z du point en dessous
        double nouvZ = Double.parseDouble(rs.getString(2));
        double oldZ = lignePred[i];

        // On crée le nouveau point

        double oldX = shiftX + cellesizeX * i * this.echantillonage;
        double oldY = maxy - cellesizeY * j * this.echantillonage;

        if (oldZ == this.noDataValue) {

          pointAncien = new Point3d(oldX, oldY, this.zMin * exager);
        } else {

          pointAncien = new Point3d(oldX, oldY, oldZ * exager);
        }

        if (nouvZ == this.noDataValue) {
          nouvePoint = new Point3d(oldX, oldY - cellesizeY
              * this.echantillonage, this.zMin * exager);

        } else {
          nouvePoint = new Point3d(oldX, oldY - cellesizeY
              * this.echantillonage, nouvZ * exager);

        }

        strp.setCoordinate(nbpoints, pointAncien);

        strp.setColor(nbpoints, this.getColor4f(oldZ));

        nbpoints++;

        strp.setCoordinate(nbpoints, nouvePoint);

        strp.setColor(nbpoints, this.getColor4f(nouvZ));

        nbpoints++;
        lignePred[i] = nouvZ;

      }

      // On passe des lignes pour sous àchantillonner
      for (int l = 0; l < this.echantillonage; l++) {
        currentRow++;

      }

    }

    conn.close();

    this.stepX = this.echantillonage * cellesizeX;
    this.stepY = this.echantillonage * cellesizeY;
    this.nX = ncols;
    this.nY = nrows;
    this.sampling = this.echantillonage;
    this.strip = strp;

    Appearance app = new Appearance();
    TransparencyAttributes tra = new TransparencyAttributes();
    tra.setTransparencyMode(TransparencyAttributes.NONE);
    app.setTransparencyAttributes(tra);

    // Style normal
    // Création des attributs du polygone
    PolygonAttributes pa = new PolygonAttributes();

    // a modifier pour changer de mode
    // Le mode permet de représenter le MNT de différentes manières
    // pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
    if (fill) {
      pa.setPolygonMode(PolygonAttributes.POLYGON_FILL);
    } else {
      pa.setPolygonMode(PolygonAttributes.POLYGON_LINE);
    }
    // pa.setPolygonMode(PolygonAttributes.POLYGON_POINT);

    pa.setCullFace(PolygonAttributes.CULL_BACK);
    pa.setBackFaceNormalFlip(true);

    // Création du material (gestion des couleurs et de l'affichage)
    Material material = new Material();

    material.setSpecularColor(new Color3f(1f, 1f, 1f));
    material.setShininess(128f);
    material.setDiffuseColor(new Color3f(0.8f, 0.8f, 0.8f));

    // Autorisations pour le material
    app.setCapability(Appearance.ALLOW_MATERIAL_READ);
    app.setCapability(Appearance.ALLOW_MATERIAL_WRITE);
    /*
     * ColoringAttributes cA = new ColoringAttributes();
     * cA.setShadeModel(ColoringAttributes.SHADE_GOURAUD); cA.setColor(new
     * Color3f(0.0f, 0.8f, 0.0f));
     */

    // Association à l'apparence des attributs de géométrie et de
    // material
    /*
     * RenderingAttributes rendering = new RenderingAttributes();
     * rendering.setAlphaTestFunction(RenderingAttributes.EQUAL);
     * rendering.setAlphaTestValue(1.0f); app.setRenderingAttributes(rendering);
     */

    app.setPolygonAttributes(pa);

    app.setMaterial(material);

    // app.setColoringAttributes(cA);

    Shape3D shapepleine;

    GeometryInfo geomInfo = new GeometryInfo(strp);

    NormalGenerator ng = new NormalGenerator();
    ng.generateNormals(geomInfo);

    shapepleine = new Shape3D(geomInfo.getGeometryArray(), app);

    return shapepleine;
  }

  /**
   * Permet de faire une petite classification sur les altitudes des points Il
   * faut modifier le code pour influer sur cette classification, pour l'instant
   * Cette classification utilise les véritables altitudes et non les altitudes
   * exaggarees On utilise une classification linéaire en fonction du tableau de
   * dégradé utilisé
   * 
   * @param z l'altitude dont on veut obtenir la couleur
   * @return la couleur au format Color3f
   */
  public Color4f getColor4f(double z) {

    // System.out.println("z ="+z+";"+"nodata"+noDataValue);

    if (z == this.noDataValue) {

      return new Color4f(1, 0, 0, 0.95f);
    }
    // Il s'agit du nombre de couleur que l'on va interprêter comme un
    // nombre de classes
    int nbColor = this.color4fShade.length;

    // la largeur d'une classe
    double largeur = (this.zMax - this.zMin) / nbColor;

    int numClass = (int) ((z - this.zMin) / largeur);

    numClass = Math.max(Math.min(nbColor - 1, numClass), 0);

    return this.color4fShade[numClass];

  }

  @Override
  public void refresh() {

    try {
      this.refresh(this.layerName, this.isFilled, this.exageration,
          this.colorShade);
    } catch (SQLException e) {

      e.printStackTrace();
    }
  }

  /**
   * Cette fonction permet d'extraire en géométrie géoxygene les triangles du
   * MNT compris dans le rectangle formé par dpMin et dpMax en 2D
   * 
   * @param dpMin point inférieur gauche du rectangle
   * @param dpMax point supérieur droit du rectangle
   * @return une liste de polygones décrivant les géométries du MNT compris dans
   *         le rectangle formé par les 2 points en 2D
   */
  @Override
  public MultiPolygon processSurfacicGrid(double xmin, double xmax,
      double ymin, double ymax) {

    GeometryFactory fac = new GeometryFactory();

    // On récupère dans quels triangles se trouvent dpMin et dpMax
    int posxMin = (int) ((xmin - this.xIni) / (this.stepX * this.sampling));
    int posyMin = (int) ((ymin - this.yIni) / (this.stepY * this.sampling));

    int posxMax = 1 + (int) ((xmax - this.xIni) / (this.stepX * this.sampling));
    int posyMax = 1 + (int) ((ymax - this.yIni) / (this.stepY * this.sampling));

    // On récupère les sommets extérieurs de ces triangles (ceux qui
    // permettent d'englober totalement le rectangle dpMin, dpMax
    Coordinate dpOrigin = new Coordinate(posxMin * this.stepX + this.xIni,
        posyMin * this.stepY + this.yIni);
    Coordinate dpFin = new Coordinate(posxMax * this.stepX + this.xIni, posyMax
        * this.stepY + this.yIni);

    // On évalue le nombre de mailles à couvrir
    int nbInterX = Math.max(1, (int) ((dpFin.x - dpOrigin.x) / this.stepX));
    int nbInterY = Math.max(1,
        (int) ((int) ((dpFin.y - dpOrigin.y) / this.stepY)));

    Polygon[] lPolys = new Polygon[2 * nbInterX * nbInterY];
    int indPoly = 0;
    // On crée une géométrie géoxygne pour chacune de ces mailles
    // (2 triangles par maille)
    for (int i = 0; i < nbInterX; i++) {
      for (int j = 0; j < nbInterY; j++) {

        Coordinate dp1 = new Coordinate(dpOrigin.x + i * this.stepX, dpOrigin.y
            + j * this.stepY);
        Coordinate dp2 = new Coordinate(dpOrigin.x + (i + 1) * this.stepX,
            dpOrigin.y + j * this.stepY);
        Coordinate dp3 = new Coordinate(dpOrigin.x + i * this.stepX, dpOrigin.y
            + (j + 1) * this.stepY);

        Coordinate dp4 = new Coordinate(dpOrigin.x + (i + 1) * this.stepX,
            dpOrigin.y + (j + 1) * this.stepY);

        Coordinate[] coord = new Coordinate[4];
        coord[0] = dp1;
        coord[1] = dp2;
        coord[2] = dp4;
        coord[3] = dp1;

        LinearRing l1 = fac.createLinearRing(coord);

        Coordinate[] coord2 = new Coordinate[4];
        coord2[0] = dp1;
        coord2[1] = dp4;
        coord2[2] = dp3;
        coord2[3] = dp1;

        LinearRing l2 = fac.createLinearRing(coord2);
        lPolys[indPoly] = fac.createPolygon(l1, null);
        indPoly++;
        lPolys[indPoly] = fac.createPolygon(l2, null);
        indPoly++;

      }

    }
    // On renvoie la liste des triangles
    return fac.createMultiPolygon(lPolys);
  }

  /**
   * Cette fonction renvoie les lignes comprises dans le rectangles ayant dpMin
   * et dpMax comme points extrémités
   * 
   * @param dpMin point inférieur gauche
   * @param dpMax point supérieur droit
   * @return une liste de points correspondant aux lignes des mailles comprises
   *         entre ces 2 points
   */
  @Override
  public MultiLineString processLinearGrid(double xmin, double ymin,
      double xmax, double ymax) {
    GeometryFactory fact = new GeometryFactory();
    // On récupère l'indice des triangles contenant ces points
    int posxMin = (int) ((xmin - this.xIni) / (this.stepX * this.sampling));
    int posyMin = (int) ((ymin - this.yIni) / (this.stepY * this.sampling));

    int posxMax = 1 + (int) ((xmax - this.xIni) / (this.stepX * this.sampling));
    int posyMax = 1 + (int) ((ymax - this.yIni) / (this.stepY * this.sampling));

    // On récupère les points extrêmes appartenant à ces triangles
    Coordinate dpOrigin = new Coordinate(posxMin * this.stepX + this.xIni,
        posyMin * this.stepY + this.yIni);
    Coordinate dpFin = new Coordinate(posxMax * this.stepX + this.xIni, posyMax
        * this.stepY + this.yIni);

    // On calcule le nombre de géométries à générer
    int nbInterX = (int) ((dpFin.x - dpOrigin.x) / this.stepX);
    int nbInterY = (int) ((dpFin.y - dpOrigin.y) / this.stepY);

    nbInterX = Math.max(1, nbInterX);
    nbInterY = Math.max(1, nbInterY);

    LineString[] lineStrings = new LineString[(nbInterX - 1) * (nbInterY - 1)
        * 3 + 2];
    int indL = 0;

    // On crée 3 lignes par maill du MNT
    for (int i = 0; i < nbInterX - 1; i++) {

      for (int j = 0; j < nbInterY - 1; j++) {

        Coordinate dp1 = new Coordinate(dpOrigin.x + i * this.stepX, dpOrigin.y
            + j * this.stepY);
        Coordinate dp2 = new Coordinate(dpOrigin.x + (i + 1) * this.stepX,
            dpOrigin.y + j * this.stepY);
        Coordinate dp3 = new Coordinate(dpOrigin.x + i * this.stepX, dpOrigin.y
            + (j + 1) * this.stepY);
        Coordinate dp4 = new Coordinate(dpOrigin.x + (i + 1) * this.stepX,
            dpOrigin.y + (j + 1) * this.stepY);

        Coordinate[] c1 = new Coordinate[2];
        Coordinate[] c2 = new Coordinate[2];
        Coordinate[] c3 = new Coordinate[2];

        c1[0] = dp1;
        c1[1] = dp2;

        LineString s1 = fact.createLineString(c1);

        c2[0] = dp1;
        c2[1] = dp3;

        LineString s2 = fact.createLineString(c2);

        c3[0] = dp1;
        c3[1] = dp4;

        LineString s3 = fact.createLineString(c3);

        lineStrings[indL] = s1;
        indL++;
        lineStrings[indL] = s2;
        indL++;
        lineStrings[indL] = s3;
        indL++;

      }

    }
    // On complète en ajoutant les bordures supérieures et inférieures (qui
    // ne sont pas parcourus par le boucle précédente
    Coordinate dpInterX = new Coordinate(dpFin.x, dpOrigin.y);
    Coordinate dpInterY = new Coordinate(dpOrigin.x, dpFin.y);

    Coordinate[] c1 = new Coordinate[2];
    c1[0] = dpInterX;
    c1[1] = dpFin;

    lineStrings[indL] = fact.createLineString(c1);
    indL++;

    Coordinate[] c2 = new Coordinate[2];
    c2[0] = dpInterY;
    c2[1] = dpFin;

    lineStrings[indL] = fact.createLineString(c2);

    // On renvoie le tout
    return fact.createMultiLineString(lineStrings);
  }

  /**
   * Renvoie le triangle qui se trouve dans la maille indexX, indexY et dans le
   * bas de la maille si inferior est true et dans le haut de celle-ci sinon
   * Renvoie null si les indices ne correspondent pas à un triangle existant
   * 
   * @param indexX l'indice de la maille en X
   * @param indexY l'indice de la maille en Y
   * @param inferior la position du triangle par rapport à la maille
   * @return une liste de points correspondants au sommet du triangle concerné
   */
  public DirectPositionList getTriangle(int indexX, int indexY, boolean inferior) {

    // Si les coordonnées ne sont pas dans le MNT, on renvoie null
    if (indexX >= (this.nX - 1) || indexX < 0 || indexY >= (this.nY - 1)
        || indexY < 0) {

      return null;

    }
    // On récupère les 4 coordonnées potentielle
    int lignInit2 = 2 * (indexX) + 2 * (this.nY - 1 - (indexY + 1)) * this.nX;

    Point3d pointemp1 = new Point3d();
    this.strip.getCoordinate(lignInit2, pointemp1);

    Point3d pointemp2 = new Point3d();
    this.strip.getCoordinate(lignInit2 + 1, pointemp2);

    Point3d pointemp3 = new Point3d();
    this.strip.getCoordinate(lignInit2 + 2, pointemp3);

    Point3d pointemp4 = new Point3d();
    this.strip.getCoordinate(lignInit2 + 3, pointemp4);

    DirectPosition pt12D = new DirectPosition(pointemp1.x + this.xIni,
        pointemp1.y + this.yIni, pointemp1.getZ());
    DirectPosition pt22D = new DirectPosition(pointemp2.x + this.xIni,
        pointemp2.y + this.yIni, pointemp2.getZ());
    DirectPosition pt32D = new DirectPosition(pointemp3.x + this.xIni,
        pointemp3.y + this.yIni, pointemp3.getZ());
    DirectPosition pt42D = new DirectPosition(pointemp4.x + this.xIni,
        pointemp4.y + this.yIni, pointemp4.getZ());

    DirectPositionList dpl = new DirectPositionList();

    if (inferior) {

      dpl.add(pt12D);
      dpl.add(pt22D);
      dpl.add(pt32D);

    } else {

      dpl.add(pt42D);
      dpl.add(pt22D);
      dpl.add(pt32D);
    }

    return dpl;
  }

  /**
   * Cette fonction permet de projeter un point sur un MNT en lui ajoutant un
   * altitude offsetting
   * 
   * @param x ,y le point à projeter
   * @param offsetting l'altitude que l'on rajoute au point final
   * @return un point 3D ayant comme altitude Z du MNT + offesting
   */
  @Override
  public Coordinate castCoordinate(double x, double y) {

    // Etant donne que l'on a translaté le MNT
    // On fait de meme avec le vecteur

    // On recupere la maille dans laquelle se trouve le point
    int posx = (int) ((x - this.xIni) / (this.stepX * this.sampling));
    int posy = (int) ((y - this.yIni) / (this.stepY * this.sampling));

    if (posx >= (this.nX) || posx < 0 || posy >= (this.nY) || posy < 0) {

      return new Coordinate(x, y, 0);

    } else if (posx == this.nX - 1) {

      return new Coordinate(x, y, 0);

    } else if (posy == this.nY - 1) {

      return new Coordinate(x, y, 0);
    } else {
      int lignInit2 = 2 * (posx) + 2 * (this.nY - 1 - (posy + 1)) * this.nX;

      // int lignInit2 = 2 * (posx) + 2 * (this.nY - 1 - (posy + 1)) * this.nX;

      Point3d pointemp1 = new Point3d();
      this.strip.getCoordinate(lignInit2, pointemp1);

      Point3d pointemp2 = new Point3d();
      this.strip.getCoordinate(lignInit2 + 1, pointemp2);

      Point3d pointemp3 = new Point3d();
      this.strip.getCoordinate(lignInit2 + 2, pointemp3);

      Point3d pointemp4 = new Point3d();
      this.strip.getCoordinate(lignInit2 + 3, pointemp4);

      DirectPosition pt12D = new DirectPosition(pointemp1.x, pointemp1.y);
      DirectPosition pt22D = new DirectPosition(pointemp2.x, pointemp2.y);
      DirectPosition pt32D = new DirectPosition(pointemp3.x, pointemp3.y);
      DirectPosition pt42D = new DirectPosition(pointemp4.x, pointemp4.y);

      DirectPosition ptloc2D = new DirectPosition(x, y);

      double d1 = ptloc2D.distance(pt12D);
      double d2 = ptloc2D.distance(pt22D);

      double d3 = ptloc2D.distance(pt32D);

      double d4 = ptloc2D.distance(pt42D);

      double zMoy = 0;
      if (d1 < 5) {

        zMoy = pointemp1.z;
        return new Coordinate(x, y, zMoy + DTM.CONSTANT_OFFSET);
      } else if (d2 < 5) {
        zMoy = pointemp2.z;
        return new Coordinate(x, y, zMoy + DTM.CONSTANT_OFFSET);
      } else if (d3 < 5) {
        zMoy = pointemp3.z;
        return new Coordinate(x, y, zMoy + DTM.CONSTANT_OFFSET);
      } else if (d4 < 5) {
        zMoy = pointemp4.z;
        return new Coordinate(x, y, zMoy + DTM.CONSTANT_OFFSET);
      }

      Vecteur v1 = new Vecteur(pt22D, pt32D);
      Vecteur v2 = new Vecteur(pt22D, ptloc2D);

      double d = v1.prodVectoriel(v2).prodScalaire(new Vecteur(0, 0, 1));

      if (d > 0) {// Triangle 1 2 3

        double xn = (pointemp2.y - pointemp1.y) * (pointemp3.z - pointemp1.z)
            - (pointemp3.y - pointemp1.y) * (pointemp2.z - pointemp1.z);
        double yn = (pointemp3.x - pointemp1.x) * (pointemp2.z - pointemp1.z)
            - (pointemp2.x - pointemp1.x) * (pointemp3.z - pointemp1.z);
        double zn = (pointemp2.x - pointemp1.x) * (pointemp3.y - pointemp1.y)
            - (pointemp3.x - pointemp1.x) * (pointemp2.y - pointemp1.y);

        zMoy = (pointemp1.x * xn + pointemp1.y * yn + pointemp1.z * zn - x * xn - y
            * yn)
            / zn;

      } else {// Il se trouve dans le triangle 2 3 4

        double xn = (pointemp2.y - pointemp4.y) * (pointemp3.z - pointemp4.z)
            - (pointemp3.y - pointemp4.y) * (pointemp2.z - pointemp4.z);
        double yn = (pointemp3.x - pointemp4.x) * (pointemp2.z - pointemp4.z)
            - (pointemp2.x - pointemp4.x) * (pointemp3.z - pointemp4.z);
        double zn = (pointemp2.x - pointemp4.x) * (pointemp3.y - pointemp4.y)
            - (pointemp3.x - pointemp4.x) * (pointemp2.y - pointemp4.y);

        zMoy = (pointemp4.x * xn + pointemp4.y * yn + pointemp4.z * zn - x * xn - y
            * yn)
            / zn;

      }

      return new Coordinate(x, y, zMoy + DTM.CONSTANT_OFFSET);

    }

  }

  @Override
  public Box3D get3DEnvelope() {
    if (this.emprise == null) {

      return new Box3D(this.xIni, this.yIni, this.zMin, this.xIni + this.nX
          * this.stepX, this.yIni + this.nY * this.stepY, this.zMax);
    }
    return this.emprise;
  }

  @Override
  public GM_Object getGeometryAt(double x, double y) {
    int posx = (int) ((x - this.xIni) / (this.stepX * this.sampling));
    int posy = (int) ((y - this.yIni) / (this.stepY * this.sampling));

    DirectPositionList lTri = this.getTriangle(posx, posy, true);
    GM_Triangle tri = new GM_Triangle(new GM_LineString(lTri));

    if (tri.contains(new GM_Point(new DirectPosition(x, y)))) {
      return tri;
    }
    lTri = this.getTriangle(posx, posy, false);
    return new GM_Triangle(new GM_LineString(lTri));

  }
}

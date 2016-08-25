/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.leastsquares.core;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.collections15.BidiMap;
import org.apache.commons.collections15.bidimap.DualHashBidiMap;

import Jama.Matrix;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Face;
import fr.ign.cogit.geoxygene.contrib.delaunay.ArcDelaunay;
import fr.ign.cogit.geoxygene.contrib.delaunay.NoeudDelaunay;
import fr.ign.cogit.geoxygene.contrib.delaunay.TriangleDelaunay;
import fr.ign.cogit.geoxygene.contrib.delaunay.Triangulation;
import fr.ign.cogit.geoxygene.contrib.geometrie.Angle;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Segment;

/**
 * @author G. Touya
 * 
 *         Classe qui va donner des objets scheduler. Un scheduler gère un
 *         ajustement par moindres carrés avec l'instanciation des contraintes,
 *         la traduction en systèmes d'équations et le lancement des moindres
 *         carrés sur le système. Le scheduler permet aussi de modifier les
 *         géométries selon l'ajustement.
 */
public class LSScheduler {

  static Logger logger = Logger.getLogger(LSScheduler.class.getName());

  public enum MatrixSolver {
    JAMA, EJML, COLT
  }

  public enum GeometryType {
    POINT, LINE, POLYGON, COMPLEX
  }

  public MatrixSolver solver = MatrixSolver.JAMA;

  private Set<IFeature> objsFixes;
  private Set<IFeature> objsRigides;
  private Set<IFeature> objsMalleables;

  private MapspecsLS mapspec;

  private Triangulation triangulation;

  /**
   * les conflits spatiaux par triangulation
   */
  private HashSet<LSSpatialConflict> conflits = new HashSet<LSSpatialConflict>();// les
  // conflits
  // spatiaux
  // par
  // triangulation

  // le système d'équations final
  protected EquationsSystem systemeGlobal;
  protected int tailleVecteurs;

  // une map qui contient les objets traités en clé et le set des LSPoint
  // de sa géométrie en valeur
  private Map<IFeature, ArrayList<LSPoint>> mapObjPts = new HashMap<IFeature, ArrayList<LSPoint>>();

  /**
   * Cette map relie les objets à leur ancienne géométrie (si l'ajustement a été
   * commité) ou à leur nouvelle sinon.
   */
  private Map<IFeature, IGeometry> mapObjGeom = new HashMap<IFeature, IGeometry>();

  // constructeur de base
  public LSScheduler(MapspecsLS ms) {
    this.mapspec = ms;
    this.setObjsFixes(new HashSet<IFeature>());
    this.setObjsRigides(new HashSet<IFeature>());
    this.setObjsMalleables(new HashSet<IFeature>());
  }// MCScheduler(Version vac,MapspecsMC ms)

  /**
   * <p>
   * Récupère les objets à ajuster en fonction de la sélection et de l'option de
   * sélection choisie dans les mapspecs.
   * @throws ClassNotFoundException
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   * 
   */
  public void setObjs() throws IllegalArgumentException, SecurityException,
      IllegalAccessException, NoSuchFieldException, ClassNotFoundException {

    // on parcourt les objets et on les répartit dans les bons sets
    for (IFeature obj : this.mapspec.getSelectedObjects()) {
      Class<?> classe = Class.forName(obj.getClass().getName());

      // on vérifie dans quel type d'objets se situe la classe
      if (this.mapspec.isFixedClass(classe)) {
        this.getObjsFixes().add(obj);
      } else if (this.mapspec.isRigidClass(classe)) {
        this.getObjsRigides().add(obj);
      } else if (this.mapspec.isMalleableClass(classe)) {
        this.getObjsMalleables().add(obj);
      }
    }
  }

  /**
   * <p>
   * Crée un LSPoint par vertex des objets à ajuster et remplit la map des
   * objets avec leurs LSPoints. Crée pour chaque LSPoint les contraintes
   * internes Java qui le concernent. Pour l'initialisation des contraintes
   * externes de chaque LSPoint, lancer la méthode
   * initialiserContraintesExternes().
   * @throws InstantiationException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws NoSuchMethodException
   * @throws ClassNotFoundException
   * @throws IllegalArgumentException
   * @throws SecurityException
   * 
   */
  protected void initialiserLSPoints()
      throws SecurityException, IllegalArgumentException,
      ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
      InvocationTargetException, InstantiationException {
    Set<IFeature> objs = new HashSet<IFeature>();
    objs.addAll(this.getObjsFixes());
    objs.addAll(this.getObjsMalleables());
    objs.addAll(this.getObjsRigides());
    Set<LSPoint> points = new HashSet<LSPoint>();

    Iterator<IFeature> iter = objs.iterator();
    while (iter.hasNext()) {
      IFeature obj = iter.next();

      // on construit le set des LSPoint de obj
      ArrayList<LSPoint> listePoints = new ArrayList<LSPoint>();

      // on récupère la géométrie de obj
      IGeometry geom = obj.getGeom();
      // si l'objet est de type malléable, on le densifie à un pas de 50 m
      if (this.getObjsMalleables().contains(obj)) {
        geom = LineDensification.densification2(geom, 50.0);
      }
      // cas d'un point
      if (geom instanceof IPoint) {
        LSPoint point = this.construirePoint(obj, ((IPoint) geom).getPosition(),
            1, GeometryType.POINT, true, this.getSymbolWidth(obj), points);
        point.setContraintesInternes(this.mapspec, this);
        listePoints.add(point);
        points.add(point);
        this.getMapObjPts().put(obj, listePoints);
        continue;
      } else if (geom instanceof ILineString) {
        // cas d'une ligne
        // on marque la ligne
        int position = 1;
        boolean extreme = true;
        for (IDirectPosition vertex : geom.coord()) {
          // on construit le nouveau point
          LSPoint point = this.construirePoint(obj, vertex, position,
              GeometryType.LINE, extreme, this.getSymbolWidth(obj), points);
          // on lui définit ses contraintes internes
          point.setContraintesInternes(this.mapspec, this);
          points.add(point);
          listePoints.add(point);
          // on passe au suivant
          position += 1;
          if (position < geom.numPoints()) {
            extreme = false;
          } else if (position == geom.numPoints()) {
            extreme = true;
          }
        }
        this.getMapObjPts().put(obj, listePoints);
        continue;
      } else {
        // cas d'une surface, on récupère le outer ring
        ILineString ring = ((IPolygon) geom).exteriorLineString();
        // on marque la ligne
        int position = 1;
        for (IDirectPosition vertex : ring.coord()) {
          // on récupère les coordonnées du vertex marqué
          LSPoint point = this.construirePoint(obj, vertex, position,
              GeometryType.POLYGON, false, this.getSymbolWidth(obj), points);
          point.setContraintesInternes(this.mapspec, this);
          points.add(point);
          listePoints.add(point);
          position += 1.0;
        } // for i
        this.getMapObjPts().put(obj, listePoints);
        continue;
      }

    } // boucle sur les objets à généraliser

  }

  /**
   * <p>
   * Construit et renvoie un LSPoint Java à partir d'un objet géographique et
   * des coordonnées d'un point.
   * 
   */
  protected LSPoint construirePoint(IFeature obj, IDirectPosition pt,
      int position, GeometryType type, boolean pointExtr, double symbolWidth,
      Set<LSPoint> points) {
    for (LSPoint point : points) {
      if (pt.equals(point.getIniPt())) {
        // un LSPoint a déjà été créé à cet endroit
        point.getObjs().add(obj);
        return point;
      }
    }
    boolean fixed = false;
    if (this.getObjsFixes().contains(obj)) {
      fixed = true;
    }
    LSPoint point = new LSPoint(obj, pt, position, type, pointExtr, fixed,
        symbolWidth, this);
    point.setFinalPt(pt);
    return point;
  }

  /**
   * <p>
   * Initialise les contraintes externes choisies dans les mapspecs pour les
   * objets à ajuster. On crée un objet contrainte que l'on lie à chaque objet à
   * ajuster pour chaque contrainte. Crée également les contraintes externes
   * java sur chaque LSPoint des objets concernés. Cette fonction s'appuie sur
   * un calcul de triangulation de Delaunay pour déterminer les voisinages.
   * @throws Exception
   * 
   */
  protected void initialiserContraintesExternes() throws Exception {
    // si la proximité est calculée par triangulation, il faut la faire
    if (this.mapspec.isProxiTinActive()) {
      // on calcule la triangulation
      // p: contrainte
      // c: construit l'enveloppe convexe (utile en cas de segments fermés)
      // z: numerotation de 0 a n-1 et pas de 1 a n
      // e: donne les edges
      // Q, V,VV,VVV: commentaires generes (Q pour quiet, V pour verbose)
      String options = "pczeBQ";
      // on convertit les entrées dans le format pivot
      List<NoeudDelaunay> points = new ArrayList<NoeudDelaunay>();
      IFeatureCollection<ArcDelaunay> segments = new FT_FeatureCollection<ArcDelaunay>();
      int i = 0;
      BidiMap<LSPoint, NoeudDelaunay> pointsMap = new DualHashBidiMap<LSPoint, NoeudDelaunay>();
      BidiMap<LSPoint, Integer> indices = new DualHashBidiMap<LSPoint, Integer>();
      Map<IDirectPosition, Integer> mapIndices = new HashMap<IDirectPosition, Integer>();
      for (IFeature obj : this.getMapObjPts().keySet()) {
        IGeometry geom = obj.getGeom();
        // si l'objet est de type malléable, on le densifie à un pas de 50 m
        if (this.getObjsMalleables().contains(obj)) {
          geom = LineDensification.densification2(geom, 50.0);
        }

        List<Segment> objSegments = new ArrayList<Segment>();
        if (geom instanceof ILineString)
          objSegments.addAll(Segment.getSegmentList((ILineString) geom));
        if (geom instanceof IPolygon)
          objSegments.addAll(
              Segment.getSegmentList((IPolygon) geom, geom.coord().get(0)));
        for (Segment seg : objSegments) {
          IDirectPosition pt1 = seg.getStartPoint();
          LSPoint lsPt1 = getPointFromCoord(pt1, obj);
          IDirectPosition pt2 = seg.getEndPoint();
          LSPoint lsPt2 = getPointFromCoord(pt2, obj);
          NoeudDelaunay triPt1 = null, triPt2 = null;
          if (!pointsMap.containsKey(lsPt1)) {
            triPt1 = new NoeudDelaunay(pt1);
            points.add(triPt1);
            pointsMap.put(lsPt1, triPt1);
            i++;
            indices.put(lsPt1, i);
            mapIndices.put(pt1, i);
          } else
            triPt1 = pointsMap.get(lsPt1);
          if (!pointsMap.containsKey(lsPt2)) {
            triPt2 = new NoeudDelaunay(pt2);
            points.add(triPt2);
            pointsMap.put(lsPt2, triPt2);
            i++;
            indices.put(lsPt2, i);
            mapIndices.put(pt2, i);
          } else
            triPt2 = pointsMap.get(lsPt2);

          segments.add(new ArcDelaunay(triPt1, triPt2));
        }
      }

      // on lance la triangulation
      triangulation = new Triangulation();
      triangulation.importAsNodes(points);
      triangulation.importClasseGeo(segments);
      triangulation.setOptions(options);
      triangulation.create();

      // on détermine les conflits spatiaux à partir de la triangulation
      // pour cela, on fait une boucle sur les triangles
      for (Face face : triangulation.getPopFaces()) {
        TriangleDelaunay triangle = (TriangleDelaunay) face;
        IDirectPosition triPt1 = new DirectPosition(
            triangle.getCoord().get(0).getX(),
            triangle.getCoord().get(0).getY());
        IDirectPosition triPt2 = new DirectPosition(
            triangle.getCoord().get(1).getX(),
            triangle.getCoord().get(1).getY());
        IDirectPosition triPt3 = new DirectPosition(
            triangle.getCoord().get(2).getX(),
            triangle.getCoord().get(2).getY());
        LSPoint point1 = indices.getKey(mapIndices.get(triPt1));
        LSPoint point2 = indices.getKey(mapIndices.get(triPt2));
        LSPoint point3 = indices.getKey(mapIndices.get(triPt3));
        // on gère le cas des "Steiner points" de la triangulation (i.e.
        // nouveaux points)
        if (point1 == null || point2 == null || point3 == null) {
          logger.finest("null point");
          continue;
        }
        // si les trois points sont voisins, on passe
        if (point1.estVoisin(this, point2) && point1.estVoisin(this, point3)) {
          logger.finest("points on same object");
          continue;
        }
        // sinon, on teste si deux sont voisins
        boolean deuxObjs = false;
        LSPoint[] voisins = new LSPoint[2];
        LSPoint autre = null;
        if (point1.estVoisin(this, point2)) {
          autre = point3;
          voisins[0] = point1;
          voisins[1] = point2;
          deuxObjs = true;
        } else if (point1.estVoisin(this, point3)) {
          autre = point2;
          voisins[0] = point1;
          voisins[1] = point3;
          deuxObjs = true;
        } else if (point3.estVoisin(this, point2)) {
          autre = point1;
          voisins[0] = point2;
          voisins[1] = point3;
          deuxObjs = true;
        }
        if (deuxObjs) {
          logger.finest("cas a 2 objets");
          // si les deux angles des voisins sont aigus, c'est un conflit
          // point-segment
          // il faut donc calculer ces angles
          double angle1 = Angle.angleTroisPoints(voisins[0].getIniPt(),
              voisins[1].getIniPt(), autre.getIniPt()).angleAPiPres()
              .getValeur();
          double angle2 = Angle.angleTroisPoints(voisins[1].getIniPt(),
              voisins[0].getIniPt(), autre.getIniPt()).angleAPiPres()
              .getValeur();
          if (angle1 < Math.PI / 2 && angle2 < Math.PI / 2) {
            // on construit un nouveau conflit pToSegment
            double dist = 1.5
                * (100 + autre.getSymbolWidth() + voisins[0].getSymbolWidth());
            // on calcule la distance de autre au segment
            // on calcule l'équation de la droite passant par point2 et point 3
            double a = 0.0, b = 1.0, c = 0.0;
            a = (voisins[1].getIniPt().getY() - voisins[0].getIniPt().getY())
                / (voisins[0].getIniPt().getX() - voisins[1].getIniPt().getX());
            c = voisins[0].getIniPt().getX()
                * (voisins[0].getIniPt().getY() - voisins[1].getIniPt().getY())
                / (voisins[0].getIniPt().getX() - voisins[1].getIniPt().getX())
                - voisins[0].getIniPt().getY();
            double distance = Math.abs(
                a * autre.getIniPt().getX() + b * autre.getIniPt().getY() + c)
                / Math.sqrt(a * a + b * b);
            logger.finest("process triangle (" + point1 + ", " + point2 + ", "
                + point3 + "): distance= " + distance + " for a threshold of "
                + dist);
            if (distance < dist) {
              logger.finer(
                  "conflit Point-to-Segment entre " + autre + " et " + voisins);
              this.getConflits().add(
                  new LSSpatialConflict(this, false, autre, null, voisins));
            }
          } else {
            double dist = 1.5 * this.mapspec.getEchelle() / 1000.0
                * (100 + autre.getSymbolWidth() + voisins[0].getSymbolWidth());
            // on ne construit un conflit pToP qu'avec le point le + proche
            if (angle1 >= Math.PI / 2) {
              if (autre.getIniPt().distance2D(voisins[1].getIniPt()) < dist) {
                logger.finer("conflit Point-to-Point entre " + autre + " et "
                    + voisins[1]);
                this.getConflits().add(
                    new LSSpatialConflict(this, true, autre, voisins[1], null));
              }
            } else {
              if (autre.getIniPt().distance2D(voisins[0].getIniPt()) < dist) {
                logger.finer("conflit Point-to-Point entre " + autre + " et "
                    + voisins[0]);
                this.getConflits().add(
                    new LSSpatialConflict(this, true, autre, voisins[0], null));
              }
            }
          }
        } else {
          logger.finest("cas a 3 objets");
          // on est dans le cas où les trois points ne sont pas voisins
          // on crée donc des conflits pToP entre les points quand la distance
          // est
          // plus grande que 1.5*0.2 pour être sûr de prendre en compte tous les
          // conflits potentiels.
          double dist1 = 1.5 * this.mapspec.getEchelle() / 1000.0
              * (100 + point1.getSymbolWidth() + point2.getSymbolWidth());
          double dist2 = 1.5 * this.mapspec.getEchelle() / 1000.0
              * (100 + point3.getSymbolWidth() + point1.getSymbolWidth());
          double dist3 = 1.5 * this.mapspec.getEchelle() / 1000.0
              * (100 + point2.getSymbolWidth() + point3.getSymbolWidth());
          if (point1.getIniPt().distance2D(point2.getIniPt()) < dist1) {
            this.getConflits()
                .add(new LSSpatialConflict(this, true, point1, point2, null));
          }
          if (point1.getIniPt().distance2D(point3.getIniPt()) < dist2) {
            this.getConflits()
                .add(new LSSpatialConflict(this, true, point1, point3, null));
          }
          if (point3.getIniPt().distance2D(point2.getIniPt()) < dist3) {
            this.getConflits()
                .add(new LSSpatialConflict(this, true, point3, point2, null));
          }
        } // fin du cas avec 3 points non voisins
      } // for j: boucle sur les triangles résultants
    } // fin si la proximité par triangulation est utilisée dans les mapspecs
    logger.finer("we found " + this.getConflits().size() + " TIN conflicts");

    // on parcourt les objets fixes
    int nb = 0;
    Iterator<IFeature> iFixes = this.getObjsFixes().iterator();
    while (iFixes.hasNext()) {
      IFeature obj = iFixes.next();

      ArrayList<LSPoint> points = this.getMapObjPts().get(obj);
      Iterator<LSPoint> i = points.iterator();
      while (i.hasNext()) {
        LSPoint point = i.next();
        point.setContraintesExternes(this.mapspec, this);
        nb = nb + point.getInternalConstraints().size()
            + point.getExternalConstraints().size();
      } // while(i.hasNext())
    } // while(jFixes.hasNext())

    // on parcourt les objets rigides
    Iterator<IFeature> iRigid = this.getObjsRigides().iterator();
    while (iRigid.hasNext()) {
      IFeature obj = iRigid.next();

      ArrayList<LSPoint> points = this.getMapObjPts().get(obj);
      Iterator<LSPoint> i = points.iterator();
      while (i.hasNext()) {
        LSPoint point = i.next();
        point.setContraintesExternes(this.mapspec, this);
        nb = nb + point.getInternalConstraints().size()
            + point.getExternalConstraints().size();
      } // while(i.hasNext())
    } // while(iRigid.hasNext())

    // on parcourt les objets malleables
    Iterator<IFeature> iMall = this.getObjsMalleables().iterator();
    while (iMall.hasNext()) {
      IFeature obj = iMall.next();

      ArrayList<LSPoint> points = this.getMapObjPts().get(obj);
      Iterator<LSPoint> i = points.iterator();
      while (i.hasNext()) {
        LSPoint point = i.next();
        point.setContraintesExternes(this.mapspec, this);
        nb = nb + point.getInternalConstraints().size()
            + point.getExternalConstraints().size();
      } // while(i.hasNext())
    } // while(iMall.hasNext())

    LSScheduler.logger.fine("Nb total de contraintes : " + nb);
    LSScheduler.logger
        .finer("Nb total de conflits : " + this.getConflits().size());
  }// initialiserContraintesExternes()

  /**
   * <p>
   * Assemble les systèmes d'équation calculés pour chaque LSPoint de chaque
   * objet ajusté.
   * 
   */
  protected void assembleSystemesEquation() {
    // on parcourt la map des objets traités
    boolean prems = true;
    for (IFeature obj : this.getMapObjPts().keySet()) {

      // on récupère la valeur de cette objet dans la map
      ArrayList<LSPoint> listePoints = this.getMapObjPts().get(obj);
      // on parcourt ce set des points et on assemble leurs matrices
      Iterator<LSPoint> it = listePoints.iterator();
      while (it.hasNext()) {
        LSPoint point = it.next();

        // on calcule le systeme local du point
        point.calculeSystemeLocal();
        if (point.getSystemeLocal().estVide())
          continue;
        if (prems) {
          this.systemeGlobal = point.getSystemeLocal().copy();
          if (this.systemeGlobal != null)
            prems = false;
        } else {
          // on assemble le systeme global et point.systemeLocal
          EquationsSystem nouveau = this.systemeGlobal
              .assemble(point.getSystemeLocal());
          this.systemeGlobal = nouveau.copy();
        } // else : cas on ce n'est pas le 1er pt traité
      } // while boucle sur setPoints
    } // while boucle sur mapObjPts
  }// assembleSystemesEquation()

  /**
   * <p>
   * Met à jour la géométrie des objets dont les points ont été modifiés. Une
   * nouvelle géométrie est en fait créée et stockée dans le scheduler. Elle est
   * appliquée à l'objet sur le paramètre commit est à true.
   * 
   * @param commit : true si on souhaite appliquer la nouvelle géométrie à
   *          l'objet
   */
  protected void majGeometries(boolean commit) {
    // on "map" les inconnues avec les solutions
    Map<LSPoint, IDirectPosition> mapInconnues = this.setMapInconnues();
    // on commence par parcourir la map des objets traités
    for (IFeature obj : this.getMapObjPts().keySet()) {
      // on récupère la géométrie de obj
      IGeometry geomIni = obj.getGeom();
      // on récupère le type de la géométrie
      IGeometry geomFin;
      // suivant le type de la géométrie, on construit la géométrie
      // finale appropriée (point, ligne ou surface)
      if (geomIni instanceof IPoint) {
        geomFin = this.construitNouveauPoint(obj, (IPoint) geomIni,
            mapInconnues);
      } else if (geomIni instanceof ILineString) {
        geomFin = this.construitNouvelleLigne(obj, mapInconnues);
      } else {
        geomFin = this.construitNouvelleSurface(obj, (IPolygon) geomIni,
            mapInconnues);
      }

      if (commit) {
        // on applique l'ancienne géométrie dans l'attribut correspondant
        obj.setGeom(geomFin);
        this.mapObjGeom.put(obj, geomIni);
      } else {
        // on applique la nouvelle géométrie dans l'attribut correspondant
        this.mapObjGeom.put(obj, geomFin);
      }

    } // while boucle sur les clés de mapObjPts

  }// majGeometries()

  /**
   * <p>
   * Construit la nouvelle géométrie ponctuelle de l'objet obj suite à
   * l'ajustement par moindres carrés.
   * 
   */
  protected IPoint construitNouveauPoint(IFeature obj, IPoint geomIni,
      Map<LSPoint, IDirectPosition> mapInconnues) {
    // on commence par récupèrer les coordonnées initiales du point
    IDirectPosition coord = geomIni.getPosition();
    // on récupère le LSPoint correspondant
    LSPoint pointLS = this.getPointFromCoord(coord, obj);
    IDirectPosition coordFinales = mapInconnues.get(pointLS);

    // on calcule les nouvelles coordonnées
    double newX = pointLS.getFinalPt().getX();
    double newY = pointLS.getFinalPt().getY();
    if (!pointLS.fixed) {
      newX = pointLS.getIniPt().getX() + coordFinales.getX();
      newY = pointLS.getIniPt().getY() + coordFinales.getY();
    }
    pointLS.setFinalPt(new DirectPosition(newX, newY));
    return pointLS.getFinalPt().toGM_Point();
  }

  /**
   * <p>
   * Construit la nouvelle géométrie linéaire de l'objet obj suite à
   * l'ajustement par moindres carrés.
   * 
   */
  protected ILineString construitNouvelleLigne(IFeature obj,
      Map<LSPoint, IDirectPosition> mapInconnues) {
    // on commence par construire la géométrie linéaire vide
    IDirectPositionList newPts = new DirectPositionList();

    for (LSPoint point : this.getMapObjPts().get(obj)) {
      // on récupère les nouvelles coordonnées
      IDirectPosition coordFinales = mapInconnues.get(point);
      // on calcule les nouvelles coordonnées
      double newX = point.getFinalPt().getX();
      double newY = point.getFinalPt().getY();
      if (!point.fixed) {
        newX = point.getIniPt().getX() + coordFinales.getX();
        newY = point.getIniPt().getY() + coordFinales.getY();
      }
      point.setFinalPt(new DirectPosition(newX, newY));
      // on ajoute un vertex à ces coordonnées
      newPts.add(new DirectPosition(newX, newY));
    }

    return new GM_LineString(newPts);
  }// construitNouvelleLigne(GothicObject,Geometry,HashMap)

  /**
   * <p>
   * Construit la nouvelle géométrie surfacique de l'objet obj suite à
   * l'ajustement par moindres carrés.
   * 
   */
  protected IPolygon construitNouvelleSurface(IFeature obj, IPolygon geomIni,
      Map<LSPoint, IDirectPosition> mapInconnues) {
    // initialisation des variables
    IDirectPositionList ring = new DirectPositionList();
    ILineString ringIni = geomIni.exteriorLineString();
    HashSet<IRing> innerRings = new HashSet<IRing>();
    for (IRing inner : geomIni.getInterior()) {
      innerRings.add(inner);
    }
    if (this.getObjsMalleables().contains(obj)
        && ringIni.coord().size() < this.getMapObjPts().get(obj).size()) {
      ringIni = LineDensification.densification2(ringIni,
          this.getMapspec().getDensStep());
    }

    // loop on the vertices of initial geometry
    for (IDirectPosition vertex : ringIni.coord()) {
      // on récupère le LSPoint correspondant
      LSPoint point = this.getPointFromCoord(vertex, obj);
      // on récupère les nouvelles coordonnées
      IDirectPosition coordFinales = mapInconnues.get(point);
      // on teste si le point était bien une inconnue
      if (coordFinales == null) {
        // dans ce cas, on ne bouge pas le point
        coordFinales = new DirectPosition(0.0, 0.0);
      }
      // on calcule les nouvelles coordonnées
      double newX = point.getFinalPt().getX();
      double newY = point.getFinalPt().getY();
      if (!point.fixed) {
        newX = point.getIniPt().getX() + coordFinales.getX();
        newY = point.getIniPt().getY() + coordFinales.getY();
      }
      point.setFinalPt(new DirectPosition(newX, newY));
      // on ajoute un vertex à ces coordonnées
      ring.add(new DirectPosition(newX, newY));
    }
    // on ferme le ring
    if (ring.size() == 0) {
      return geomIni;
    }
    IRing ringGeom = new GM_Ring(new GM_LineString(ring));
    if (!ringGeom.validate(0.0)) {
      ring.add(ring.get(0));
      ringGeom = new GM_Ring(new GM_LineString(ring));
    }
    // enfin on construit la surface à partir du ring
    IPolygon newPolygon = new GM_Polygon(ringGeom);

    // ajout des inners rings
    for (IRing innerRingIni : innerRings) {
      IDirectPositionList innerRingCoord = new DirectPositionList();
      // on marque la géométrie initiale
      for (IDirectPosition vertex : innerRingIni.coord()) {
        // on récupère le LSPoint correspondant
        LSPoint point = this.getPointFromCoord(vertex, obj);
        // on récupère les nouvelles coordonnées
        IDirectPosition coordFinales = mapInconnues.get(point);
        // on teste si le point était bien une inconnue
        if (coordFinales == null) {
          // dans ce cas, on ne bouge pas le point
          coordFinales = new DirectPosition(0.0, 0.0);
        }
        // on calcule les nouvelles coordonn�es
        double newX = point.getIniPt().getX();
        double newY = point.getIniPt().getY();
        if (!point.fixed) {
          newX = point.getIniPt().getX() + coordFinales.getX();
          newY = point.getIniPt().getY() + coordFinales.getY();
        }
        point.setFinalPt(new DirectPosition(newX, newY));
        // on ajoute un vertex � ces coordonn�es
        innerRingCoord.add(new DirectPosition(newX, newY));
      }
      // on ferme le ring
      if (innerRingCoord.size() == 0) {
        newPolygon.addInterior(innerRingIni);
      }
      IRing innerRingGeom = new GM_Ring(new GM_LineString(innerRingCoord));
      if (!innerRingGeom.validate(0.0)) {
        innerRingCoord.add(innerRingCoord.get(0));
        innerRingGeom = new GM_Ring(new GM_LineString(innerRingCoord));
      }
      newPolygon.addInterior(innerRingGeom);
    }
    return newPolygon;
  }

  /**
   * Associate {@link LSPoint} objects with the corresponding solutions for the
   * unknowns of the equations system.
   * @return
   */
  protected Map<LSPoint, IDirectPosition> setMapInconnues() {
    Map<LSPoint, IDirectPosition> map = new HashMap<LSPoint, IDirectPosition>();
    for (int i = 0; i < this.systemeGlobal.getUnknowns().size(); i = i + 2) {
      LSPoint point = this.systemeGlobal.getUnknowns().get(i);
      IDirectPosition vector = new DirectPosition(
          this.systemeGlobal.getSolutions().get(i),
          this.systemeGlobal.getSolutions().get(i + 1));
      map.put(point, vector);
    }
    return map;
  }// setMapInconnues

  /**
   * Get the {@link LSPoint} of a given feature, initially corresponding to a
   * position.
   * @param pt
   * @return
   */
  public LSPoint getPointFromCoord(IDirectPosition pt, IFeature obj) {

    // on parcourt le vecteur des LSPoints
    ArrayList<LSPoint> points = this.getMapObjPts().get(obj);
    for (LSPoint current : points) {
      // on teste si les coordonnées sont les mêmes
      if (!current.getIniPt().equals(pt)) {
        continue;
      }

      // arrivé là, on tient le bon LSPoint
      return current;
    } // for i

    return null;
  }

  /**
   * Get the {@link LSPoint} corresponding initially to a position.
   * @param pt
   * @return
   */
  public LSPoint getPointFromCoord(IDirectPosition pt) {

    // on parcourt le vecteur des LSPoint
    IFeatureCollection<IFeature> points = this.getPoints();
    for (IFeature current : points.select(pt, 0.5)) {
      // on teste si les coordonnées sont les mêmes
      if (!((LSPoint) current).getIniPt().equals(pt)) {
        continue;
      }

      // arrivé là, on tient le bon LSPoint
      return (LSPoint) current;
    } // for i

    return null;
  }

  /**
   * <p>
   * Lance l'ajustement par moindres carrés pour ce scheduler en enchaînant les
   * précédentes méthodes.
   * 
   * @param diffusion: true si on veut utiliser la diffusion sur les éléments de
   *          réseau au bord et false si on veut fixer ces points
   * @param commit: true si on veut appliquer le changement de geom sur les
   *          objets et false si on veut juste mettre la nouvelle géométrie dans
   *          la map attitrée
   */
  public void triggerAdjustment(boolean diffusion, boolean commit) {
    // on commence par sélectionner les objets
    LSScheduler.logger.fine("Moindres carres : on recupere les objets");
    try {
      this.setObjs();
    } catch (IllegalArgumentException e2) {
      e2.printStackTrace();
    } catch (SecurityException e2) {
      e2.printStackTrace();
    } catch (IllegalAccessException e2) {
      e2.printStackTrace();
    } catch (NoSuchFieldException e2) {
      e2.printStackTrace();
    } catch (ClassNotFoundException e2) {
      e2.printStackTrace();
    }
    if (this.countObjs() == 0) {
      LSScheduler.logger.fine("Moindres carres : pas d objet a traiter");
      return;
    }
    LSScheduler.logger
        .fine("Moindres carres : " + this.countObjs() + " objets a traiter");
    // System.out.println(this.objsFixes.size());
    // System.out.println(this.objsRigides.size());
    // System.out.println(this.objsMalleables.size());

    // on crée les LSPoints de chaque objet
    LSScheduler.logger.fine("Moindres carres : on initialise les points");
    try {
      this.initialiserLSPoints();
    } catch (SecurityException e1) {
      e1.printStackTrace();
    } catch (IllegalArgumentException e1) {
      e1.printStackTrace();
    } catch (ClassNotFoundException e1) {
      e1.printStackTrace();
    } catch (NoSuchMethodException e1) {
      e1.printStackTrace();
    } catch (IllegalAccessException e1) {
      e1.printStackTrace();
    } catch (InvocationTargetException e1) {
      e1.printStackTrace();
    } catch (InstantiationException e1) {
      e1.printStackTrace();
    }

    if (diffusion) {
      // on marque les points à diffuser
      try {
        this.marquerAncresDiffusion();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    } else {
      // on fixe les points de réseau liés à des éléments non ajustés
      try {
        this.ancrerObjetsMalleables();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (NoSuchFieldException e) {
        e.printStackTrace();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      }
    }

    // puis on initialise les contraintes internes
    LSScheduler.logger.fine("Moindres carres : on initialise les contraintes");
    try {
      this.initialiserContraintesExternes();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // on assemble alors le système d'équations
    LSScheduler.logger
        .fine("Moindres carres : on assemble les systemes d equation");
    this.assembleSystemesEquation();

    // puis on réalise l'ajustement du système par moindres carrés
    LSScheduler.logger.fine("Moindres carres : on fait l ajustement");
    this.systemeGlobal
        .ajustementMoindresCarres(this.mapspec.getPoidsContraintes());

    LSScheduler.logger
        .finer("solutions : " + this.systemeGlobal.getSolutions().toString());

    // enfin, on met à jour les géométries
    LSScheduler.logger.fine("Moindres carres : on met a jour les geometries");
    this.majGeometries(commit);

    // on diffuse si nécessaire
    if (diffusion) {
      this.diffusionReseaux();
    }
  }

  private void marquerAncresDiffusion()
      throws IllegalArgumentException, SecurityException,
      IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
    // on commence par récupérer les ancres
    Set<LSPoint> ancres = this.recupererPointsAncres();

    for (LSPoint pt : ancres)
      pt.setFixed(true);

  }

  /**
   * Détermine les points des objets malléables qui vont servir d'ancre (ne pas
   * bouger) et marque ces points en mettant le champ "fixed" à true.
   * @throws ClassNotFoundException
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   */
  private void ancrerObjetsMalleables()
      throws IllegalArgumentException, SecurityException,
      IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
    // on commence par récupérer les ancres
    Set<LSPoint> ancres = this.recupererPointsAncres();

    // on parcourt les ancres trouvées pour les fixer
    for (LSPoint ancre : ancres) {
      ancre.fixed = true;
    }
  }

  /**
   * Get the anchored points of the malleable features of {@code this}
   * scheduler.
   * @return
   * @throws ClassNotFoundException
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   */
  private Set<LSPoint> recupererPointsAncres()
      throws IllegalArgumentException, SecurityException,
      IllegalAccessException, NoSuchFieldException, ClassNotFoundException {
    Set<LSPoint> ancres = new HashSet<LSPoint>();
    // on fait une boucle sur les objets malléables
    for (IFeature obj : this.getObjsMalleables()) {
      // on fait une boucle sur ses points
      for (LSPoint point : this.getMapObjPts().get(obj)) {
        // on ne teste que les points extremes du fait du découpage en tronçons
        if (!point.isPointIniFin()) {
          continue;
        }
        // s'il y a un seul objet rattaché à ce point, c'est une ancre.
        if (point.getObjs().size() == 1) {
          ancres.add(point);
        }
      }
    }
    return ancres;
  }

  private void diffusionReseaux() {
    // TODO Auto-generated method stub

  }

  @SuppressWarnings("unused")
  private static double determMatrice(Matrix mat) {
    double determinant = 0.0;

    if (mat.getColumnDimension() == 1) {
      return mat.get(0, 0);
    }

    int n = mat.getRowDimension();
    for (int i = 1; i <= n; i++) {
      int[] lignes = new int[n - 1];
      int[] colonnes = new int[n - 1];
      for (int j = 1; j < n; j++) {
        lignes[j - 1] = j;
      }
      int nb = 0;
      for (int j = 0; j < n; j++) {
        if (j == i - 1) {
          continue;
        }
        lignes[nb] = j;
        nb += 1;
      }
      Matrix sousMat = mat.getMatrix(lignes, colonnes);
      double signe = -1.0;
      for (int j = 1; j <= i; j++) {
        signe = signe * (-1.0);
      }
      double sousDet = sousMat.det();
      determinant = determinant + signe * mat.get(0, i - 1) * sousDet;
    } // for i

    return determinant;
  }

  /**
   * <p>
   * Renvoie la largeur de symbole maximum trouvée parmi les objets du scheduler
   * dont la classe est ou hérite de celle en entrée. Cette méthode permet de
   * déterminer le seuil de recherche des conflits.
   * 
   */
  double getClassSymbolWidth(Class<?> classe) {
    double largeur = 0.0;
    // on boucle sur les objets du scheduler
    Iterator<IFeature> iter = this.getObjsFixes().iterator();
    while (iter.hasNext()) {
      IFeature obj = iter.next();
      Class<?> classeObj = obj.getClass();
      if (!classeObj.equals(classe) && !classe.isAssignableFrom(classeObj)) {
        continue;
      }
      double larg = this.getSymbolWidth(obj);
      if (larg > largeur) {
        largeur = larg;
      }
    }
    iter = this.getObjsRigides().iterator();
    while (iter.hasNext()) {
      IFeature obj = iter.next();
      Class<?> classeObj = obj.getClass();
      if (!classeObj.equals(classe) && !classe.isAssignableFrom(classeObj)) {
        continue;
      }
      double larg = this.getSymbolWidth(obj);
      if (larg > largeur) {
        largeur = larg;
      }
    }
    iter = this.getObjsMalleables().iterator();
    while (iter.hasNext()) {
      IFeature obj = iter.next();
      Class<?> classeObj = obj.getClass();
      if (!classeObj.equals(classe) && !classe.isAssignableFrom(classeObj)) {
        continue;
      }
      double larg = this.getSymbolWidth(obj);
      if (larg > largeur) {
        largeur = larg;
      }
    }
    return largeur;
  }

  /**
   * Get the symbol width according to the object class in m.
   * @param obj
   * @return
   */
  protected double getSymbolWidth(IFeature obj) {
    if (this.getMapspec().getMapSymbolWidth().get(obj) != null)
      return this.getMapspec().getMapSymbolWidth().get(obj)
          * this.mapspec.getEchelle() / 2000.0;
    return 0.0;
  }

  /**
   * Compte le nombre de LSPoints de la zone ajustée par le scheduler.
   * @return
   */
  protected int countPoints() {
    HashSet<LSPoint> set = new HashSet<LSPoint>();
    for (IFeature obj : this.getMapObjPts().keySet()) {
      set.addAll(this.getMapObjPts().get(obj));
    }
    return set.size();
  }

  /**
   * Get all the {@link LSPoint} elements of the scheduler in a feature
   * collection to allow spatial queries.
   * @return
   */
  public IFeatureCollection<IFeature> getPoints() {
    IFeatureCollection<IFeature> fc = new FT_FeatureCollection<IFeature>();
    for (IFeature obj : this.getMapObjPts().keySet()) {
      fc.addAll(this.getMapObjPts().get(obj));
    }
    return fc;
  }

  protected int countObjs() {
    return this.getObjsFixes().size() + this.getObjsMalleables().size()
        + this.getObjsRigides().size();
  }

  /**
   * Crée un système d'équation vide en fonction du solver choisi
   * @return
   */
  public EquationsSystem initSystemeLocal() {
    if (this.solver.equals(MatrixSolver.JAMA)) {
      return new JamaEquationsSystem();
    } else if (this.solver.equals(MatrixSolver.COLT)) {
      // return new ColtSparseEquationsSystem();
    }
    /*
     * if (solver.equals(MatrixSolver.EJML)) return new SystemeEquationsEjml();
     */
    return null;
  }

  public void clearScheduler() {
    this.getObjsFixes().clear();
    this.getObjsMalleables().clear();
    this.getObjsRigides().clear();
    this.getConflits().clear();
    this.getMapObjPts().clear();
    this.systemeGlobal.clear();
  }

  public void setMapObjPts(Map<IFeature, ArrayList<LSPoint>> mapObjPts) {
    this.mapObjPts = mapObjPts;
  }

  public Map<IFeature, ArrayList<LSPoint>> getMapObjPts() {
    return this.mapObjPts;
  }

  public void setConflits(HashSet<LSSpatialConflict> conflits) {
    this.conflits = conflits;
  }

  public HashSet<LSSpatialConflict> getConflits() {
    return this.conflits;
  }

  public MapspecsLS getMapspec() {
    return this.mapspec;
  }

  public void setMapspec(MapspecsLS mapspec) {
    this.mapspec = mapspec;
  }

  public MatrixSolver getSolver() {
    return this.solver;
  }

  public void setSolver(MatrixSolver solver) {
    this.solver = solver;
  }

  public void setObjsFixes(Set<IFeature> objsFixes) {
    this.objsFixes = objsFixes;
  }

  public Set<IFeature> getObjsFixes() {
    return this.objsFixes;
  }

  public void setObjsMalleables(Set<IFeature> objsMalleables) {
    this.objsMalleables = objsMalleables;
  }

  public Set<IFeature> getObjsMalleables() {
    return this.objsMalleables;
  }

  public void setObjsRigides(Set<IFeature> objsRigides) {
    this.objsRigides = objsRigides;
  }

  public Set<IFeature> getObjsRigides() {
    return this.objsRigides;
  }

  public IEnvelope getEnvelope() {
    IPopulation<IFeature> pop = new Population<IFeature>();
    for (IFeature feat : this.objsFixes) {
      pop.add(feat);
    }
    for (IFeature feat : this.objsRigides) {
      pop.add(feat);
    }
    for (IFeature feat : this.objsMalleables) {
      pop.add(feat);
    }
    return pop.envelope();
  }

  public Map<IFeature, IGeometry> getMapObjGeom() {
    return mapObjGeom;
  }

  public void setMapObjGeom(Map<IFeature, IGeometry> mapObjGeom) {
    this.mapObjGeom = mapObjGeom;
  }

  public EquationsSystem getSystemeGlobal() {
    return systemeGlobal;
  }

  public void setSystemeGlobal(EquationsSystem systemeGlobal) {
    this.systemeGlobal = systemeGlobal;
  }

  public Triangulation getTriangulation() {
    return triangulation;
  }

  public void setTriangulation(Triangulation triangulation) {
    this.triangulation = triangulation;
  }
}// class MCScheduler

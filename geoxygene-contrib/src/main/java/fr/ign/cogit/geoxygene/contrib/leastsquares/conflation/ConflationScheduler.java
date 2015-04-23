package fr.ign.cogit.geoxygene.contrib.leastsquares.conflation;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSPoint;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.LSScheduler;
import fr.ign.cogit.geoxygene.contrib.leastsquares.core.MapspecsLS;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.generalisation.Filtering;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class ConflationScheduler extends LSScheduler {

  private static Logger logger = Logger.getLogger(ConflationScheduler.class
      .getName());

  private IFeatureCollection<DefaultFeature> loadedVectors = new FT_FeatureCollection<DefaultFeature>();
  private Class<? extends LSVectorDisplConstraint> conflationConstraint;

  public ConflationScheduler(MapspecsLS ms, Set<DefaultFeature> vectors,
      Class<? extends LSVectorDisplConstraint> conflationConstraint) {
    super(ms);
    this.loadedVectors.addAll(vectors);
    this.conflationConstraint = conflationConstraint;
  }

  @Override
  public void triggerAdjustment(boolean diffusion, boolean commit) {
    // on commence par sélectionner les objets
    logger.fine("Moindres carres : on recupere les objets");
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
      logger.fine("Moindres carres : pas d objet a traiter");
      return;
    }
    logger.fine("Moindres carres : " + this.countObjs() + " objets a traiter");

    // on crée les LSPoints de chaque objet
    logger.fine("Moindres carres : on initialise les points");
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

    // puis on initialise les contraintes internes
    logger.fine("Moindres carres : on initialise les contraintes externes");
    try {
      this.initialiserContraintesExternes();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // puis on initialise les contraintes internes
    logger
        .fine("Moindres carres : on initialise les contraintes de conflation");
    try {
      this.initialiseConflationConstraints();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }

    // on assemble alors le système d'équations
    logger.fine("Moindres carres : on assemble les systemes d equation");
    this.assembleSystemesEquation();

    // puis on réalise l'ajustement du système par moindres carrés
    logger.fine("Moindres carres : on fait l ajustement");
    this.systemeGlobal.ajustementMoindresCarres(this.getMapspec()
        .getPoidsContraintes());

    logger.finer("solutions : " + this.systemeGlobal.getSolutions().toString());

    // enfin, on met à jour les géométries
    logger.fine("Moindres carres : on met a jour les geometries");
    this.majGeometries(commit);

  }

  protected void initialiseConflationConstraints()
      throws ClassNotFoundException, SecurityException, NoSuchMethodException,
      IllegalArgumentException, InstantiationException, IllegalAccessException,
      InvocationTargetException {
    IFeatureCollection<IFeature> conflatedFeats = new FT_FeatureCollection<IFeature>();
    conflatedFeats.addAll(this.getObjsRigides());
    conflatedFeats.addAll(this.getObjsMalleables());
    // loop on the vector features
    for (DefaultFeature vectFeat : loadedVectors) {
      LSPoint iniPt = this.getPointFromCoord(vectFeat.getGeom().coord().get(0));
      // convert the feature into a Vector2D
      Vector2D vect = new Vector2D(vectFeat.getGeom().coord().get(0), vectFeat
          .getGeom().coord().get(1));
      // on cherche les objets les plus proches
      Collection<IFeature> querySet = conflatedFeats.select(vectFeat.getGeom()
          .coord().get(0), 5 * vect.norme());

      // loop on the close features
      for (IFeature feat : querySet) {
        if (feat == null)
          continue;

        // on cherche le LSPoint d'objet le plus proche de pt
        double minDist = 5 * vect.norme();
        for (LSPoint point : this.getMapObjPts().get(feat)) {
          double distance = vectFeat.getGeom().coord().get(0)
              .distance2D(point.getIniPt());
          if (distance < minDist && distance > 0.0) {
            // on construit un objet VecteurDeplacement
            DisplacementVector vector = new DisplacementVector(iniPt, point,
                vect);
            // on construit une nouvelle contrainte à partir de ce vecteur
            Constructor<? extends LSVectorDisplConstraint> constr = conflationConstraint
                .getConstructor(LSPoint.class, IFeature.class, IFeature.class,
                    LSScheduler.class, DisplacementVector.class);
            LSVectorDisplConstraint contrainte = constr.newInstance(point,
                vectFeat, feat, this, vector);
            point.getExternalConstraints().add(contrainte);
          }
        }
      }
    }
  }

  /**
   * En plus de l'initialisation classique, cette méthode ajoute des points au
   * départ des vecteurs de conflation. {@inheritDoc}
   * <p>
   * 
   */
  @Override
  protected void initialiserLSPoints() throws SecurityException,
      IllegalArgumentException, ClassNotFoundException, NoSuchMethodException,
      IllegalAccessException, InvocationTargetException, InstantiationException {
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
        geom = LineDensification.densification2(geom, this.getMapspec()
            .getDensStep());
      }

      // cas d'un point
      if (geom instanceof IPoint) {
        LSPoint point = this.construirePoint(obj,
            ((IPoint) geom).getPosition(), 1, GeometryType.POINT, true,
            this.getSymbolWidth(obj), points);
        point.setContraintesInternes(this.getMapspec(), this);
        listePoints.add(point);
        points.add(point);
        this.getMapObjPts().put(obj, listePoints);
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
          point.setContraintesInternes(this.getMapspec(), this);
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
      } else {
        // cas d'une surface, on récupère le outer ring
        ILineString ring = ((IPolygon) geom).exteriorLineString();
        // on marque la ligne
        int position = 1;
        for (IDirectPosition vertex : ring.coord()) {
          // on récupère les coordonnées du vertex marqué
          LSPoint point = this.construirePoint(obj, vertex, position,
              GeometryType.POLYGON, false, this.getSymbolWidth(obj), points);
          point.setContraintesInternes(this.getMapspec(), this);
          points.add(point);
          listePoints.add(point);
          position += 1.0;
        }// for i
        this.getMapObjPts().put(obj, listePoints);
      }

      // gestion des vecteurs
      Collection<DefaultFeature> vectors = loadedVectors.select(geom
          .buffer(0.01));
      for (DefaultFeature vector : vectors) {
        // on vérifie s'il y a bien un point au niveau du point initial du
        // vecteur
        IDirectPosition ini = vector.getGeom().coord().get(0);
        boolean created = false;
        for (LSPoint lspt : listePoints) {
          if (lspt.getIniPt().equals(ini)) {
            created = true;
            lspt.setFixed(true);
            lspt.setFinalPt(vector.getGeom().coord().get(1));
            break;
          }
        }

        // s'il n'a pas été créé, il faut l'ajouter au bon endroit
        if (!created) {
          if (geom instanceof ILineString) {
            ILineString newLine = CommonAlgorithmsFromCartAGen.insertVertex(
                (ILineString) geom, ini);
            obj.setGeom(newLine);
            int index = newLine.coord().getList().indexOf(ini);
            if (index == -1) {
              // the vector is not on the line but some millimeters aside
              // there is no need to add a LSPoint in the line
              continue;
            }
            // on construit le nouveau point
            LSPoint lsPt = this.construirePoint(obj, ini, index,
                GeometryType.LINE, false, this.getSymbolWidth(obj), points);
            lsPt.setFixed(true);
            lsPt.setFinalPt(vector.getGeom().coord().get(1));

            // on lui définit ses contraintes internes
            lsPt.setContraintesInternes(this.getMapspec(), this);
            points.add(lsPt);
            listePoints.add(index, lsPt);
            this.getMapObjPts().put(obj, listePoints);
          } else if (geom instanceof IPolygon) {
            IPolygon newLine = CommonAlgorithmsFromCartAGen.insertVertex(
                (IPolygon) geom, ini);
            obj.setGeom(newLine);
            int index = newLine.coord().getList().indexOf(ini);
            if (index == -1) {
              // the vector is not on the line but some millimeters aside
              // there is no need to add a LSPoint in the line
              continue;
            }
            // on construit le nouveau point
            LSPoint lsPt = this.construirePoint(obj, ini, index,
                GeometryType.POLYGON, false, this.getSymbolWidth(obj), points);
            lsPt.setFixed(true);
            lsPt.setFinalPt(vector.getGeom().coord().get(1));

            // on lui définit ses contraintes internes
            lsPt.setContraintesInternes(this.getMapspec(), this);
            points.add(lsPt);
            listePoints.add(index, lsPt);
            this.getMapObjPts().put(obj, listePoints);
          }
        }
      }
    }// boucle sur les objets à du scheduler
  }

  /**
   * Par rapport à la version générique, on réalise un filtrage sur les
   * géométries si c'est demandé dans les mapspecs. {@inheritDoc}
   * <p>
   * 
   */
  @Override
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
      if (this.getMapspec().isFilter())
        geomFin = Filtering.DouglasPeucker(geomFin, getMapspec()
            .getFilterThreshold());
      if (commit) {
        // on applique l'ancienne géométrie dans l'attribut correspondant
        obj.setGeom(geomFin);
        this.getMapObjGeom().put(obj, geomIni);
      } else {
        // on applique la nouvelle géométrie dans l'attribut correspondant
        this.getMapObjGeom().put(obj, geomFin);
      }

    }// while boucle sur les clés de mapObjPts
  }
}

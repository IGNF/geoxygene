/*
 * Cr�� le 23 avr. 2008
 * 
 * Pour changer le mod�le de ce fichier g�n�r�, allez � :
 * Fen�tre&gt;Pr�f�rences&gt;Java&gt;G�n�ration de code&gt;Code et commentaires
 */
package fr.ign.cogit.cartagen.leastsquares.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.GeometryType;
import fr.ign.cogit.cartagen.util.ReflectionUtil;
import fr.ign.cogit.cartagen.util.SpatialQuery;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.CommonAlgorithmsFromCartAGen;
import fr.ign.cogit.geoxygene.util.algo.geometricAlgorithms.LineDensification;
import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

/**
 * @author G. Touya
 * 
 */
public class LSPoint extends AbstractFeature {

  private static AtomicInteger counter = new AtomicInteger();

  private Set<IFeature> objs;
  private IDirectPosition finalPt;
  private IDirectPosition iniPt;
  private double symbolWidth;
  private int id;
  private LSScheduler sched;

  // la position sur la géométrie
  private double position;

  // le type de la géométrie dont le point fait partie (0 point, 1 ligne, 2
  // surface)
  private GeometryType typeGeom;

  private EquationsSystem systemeLocal;

  // set contenant un objet java impl�mentant l'interface ContrainteInterneMC
  private Set<LSInternalConstraint> internalConstraints = new HashSet<LSInternalConstraint>();

  // Map contenant en cl� un objet java impl�mentant l'interface
  // ContrainteExterneMC
  private Set<LSExternalConstraint> externalConstraints = new HashSet<LSExternalConstraint>();

  private boolean pointIniFin;
  boolean fixed, diffusion;

  public boolean isFixed() {
    return this.fixed;
  }

  public void setFixed(boolean fixe) {
    this.fixed = fixe;
  }

  public boolean isCrossing() {
    // TODO
    // A REMPLIR SI ON VEUT UTILISER CONTRAINTE DE CROISEMENT
    return false;
  }

  public IDirectPosition getIniPt() {
    return this.iniPt;
  }

  public IDirectPosition getFinalPt() {
    return this.finalPt;
  }

  LSPoint(IFeature object, IDirectPosition pt, double position,
      GeometryType type, boolean pointExtr, boolean fixe, double symbolWidth,
      LSScheduler sched) {
    this.objs = new HashSet<IFeature>();
    this.objs.add(object);
    this.setIniPt(pt);
    this.setPosition(position);
    this.setTypeGeom(type);
    this.setSymbolWidth(symbolWidth);
    this.pointIniFin = pointExtr;
    this.fixed = fixe;
    this.diffusion = false;
    this.systemeLocal = sched.initSystemeLocal();
    this.id = LSPoint.counter.getAndIncrement();
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof LSPoint)) {
      return false;
    }
    LSPoint point = (LSPoint) obj;
    if (this.objs.equals(point.objs) == false) {
      return false;
    }

    if (!this.getIniPt().equals(point.getIniPt())) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  /**
   * <p>
   * On remplit le syst�me local par l'assemblage en syst�me des contraintes
   * internes sur ce point.
   * 
   */
  private void assembleContraintesInternes() {
    boolean prems = true;
    Iterator<LSInternalConstraint> iter = this.getInternalConstraints()
        .iterator();
    while (iter.hasNext()) {
      LSInternalConstraint contr = iter.next();
      for (IFeature obj : this.objs) {
        EquationsSystem systeme = contr.calculeSystemeEquations(obj, this);
        // cas où l'objet ne contribue pas pour ce point en terme de contrainte
        // interne
        if (systeme == null) {
          continue;
        }

        if (prems) {
          this.setSystemeLocal(systeme.copy());
          prems = false;
          continue;
        }// if(prems)

        // on met à jour le nombre de valeurs non nulles dans la matrice A du
        // système linéaire
        this.getSystemeLocal().setNonNullValues(
            this.getSystemeLocal().getNonNullValues()
                + systeme.getNonNullValues());

        // on assemble le systemeLocal et systeme : on ajoute des lignes
        EquationsSystem local = this.getSystemeLocal().copy();
        // et des colonnes
        // on commence par ajouter les observations
        int nb = local.getObsRowNumber();
        this.getSystemeLocal().initObservations(nb + systeme.getObsRowNumber());
        // System.out.println(nb);
        for (int i = 0; i < nb; i++) {
          // System.out.println("i = "+i);
          this.getSystemeLocal().setObs(i, local.getObs(i));
        }// for i, boucle sur les obs initiales
        for (int i = 0; i < systeme.getObsRowNumber(); i++) {
          this.getSystemeLocal().setObs(i + nb, systeme.getObs(i));
        }// for i boucle sur les nouvelles observations

        // on assemble le vecteur des contraintes :
        // on ajoute simplement les nouvelles contraintes apr�s celles d�j�
        // pr�sentes
        this.getSystemeLocal().getConstraints()
            .addAll(systeme.getConstraints());

        // on s'occupe maintenant des inconnues
        // pour cela on parcourt le vecteur des inconnues de systeme
        for (int i = 0; i < systeme.getUnknowns().size(); i = i + 2) {
          LSPoint point = systeme.getUnknowns().get(i);
          if (this.getSystemeLocal().getUnknowns().contains(point)) {
            continue;
          }

          // on ajoute alors 2 fois point aux inconnues
          this.getSystemeLocal().getUnknowns().addElement(point);
          this.getSystemeLocal().getUnknowns().addElement(point);
        }// for i, boucle sur les inconnues de systeme

        // on assemble enfin les matrices A
        // on construit la nouvelle matrice A
        int lignes = this.getSystemeLocal().getObsRowNumber();
        int colonnes = this.getSystemeLocal().getUnknowns().size();
        this.getSystemeLocal().initMatriceA(lignes, colonnes);
        // on fait alors une double boucle pour remplir la matrice
        for (int i = 0; i < lignes; i++) {
          for (int j = 0; j < colonnes; j++) {
            // si i est encore dans les contraintes ini, on ne remplit
            // que les colonnes initiales
            if (i < local.getRowNumber()) {
              if (j >= local.getColumnNumber()) {
                continue;
              }

              this.getSystemeLocal().setA(i, j, local.getA(i, j));
              continue;
            }// if(i<=systemeLocal.matriceA.getRowDimension())

            // dans ce cas, on est dans les nouvelles contraintes
            // il faut trouver � quoi correspondent (i,j) dans systeme
            /*
             * System.out.println(getSystemeLocal().inconnues);
             * System.out.println(getSystemeLocal().inconnues.size());
             * System.out.println(j); System.out.println(this);
             */
            LSPoint inconnue = this.getSystemeLocal().getUnknowns().get(j);
            // on d�termine si c'est la colonne x du point
            boolean estX = false;
            if (!(j + 1 >= this.getSystemeLocal().getUnknowns().size())) {
              if (inconnue.equals(this.getSystemeLocal().getUnknowns()
                  .get(j + 1))) {
                estX = true;
              }
            }

            // si j n'est pas une inconnue de systeme, on laisse 0
            if (systeme.getUnknowns().contains(inconnue) == false) {
              continue;
            }
            // on d�termine la colonne correspondant dans systeme.matriceA
            int colonne = systeme.getUnknowns().indexOf(inconnue);
            if (estX == false) {
              colonne += 1;
            }

            // on assigne la valeur dans la matrice
            this.getSystemeLocal().setA(i, j,
                systeme.getA(i - local.getRowNumber(), colonne));
          }// for j, boucle sur les colonnes
        }// for i, boucle sur les lignes
        local.clear();
      }// for boucle sur les objets r�els contenant ce point
    }// while boucle sur contraintesInternes
  }// assembleContraintesInternes

  /**
   * <p>
   * On remplit le syst�me local par l'assemblage en syst�me des contraintes
   * externes sur ce point.
   * 
   */
  private void assembleContraintesExternes() {
    Iterator<LSExternalConstraint> iter = this.externalConstraints.iterator();
    while (iter.hasNext()) {
      LSExternalConstraint contr = iter.next();
      EquationsSystem systeme = contr.calculeSystemeEquations();

      // si systeme est null, on passe au suivant
      if (systeme == null) {
        continue;
      }

      // cas d'un objet sans contrainte interne
      if (this.getSystemeLocal().estVide()) {
        // copie du syst�me
        this.setSystemeLocal(systeme.copy());
        continue;
      }

      // on met � jour le nombre de valeurs non nulles dans la matrice A du
      // syst�me lin�aire
      this.getSystemeLocal().setNonNullValues(
          this.getSystemeLocal().getNonNullValues()
              + systeme.getNonNullValues());

      // on assemble le systemeLocal et systeme : on ajoute des lignes
      // et des colonnes
      EquationsSystem local = this.getSystemeLocal().copy();
      // on commence par ajouter les observations
      int nb = this.getSystemeLocal().getRowNumber();
      this.getSystemeLocal().initObservations(nb + systeme.getRowNumber());
      for (int i = 0; i < nb; i++) {
        this.getSystemeLocal().setObs(i, local.getObs(i));
      }// for i, boucle sur les obs initiales
      for (int i = 0; i < systeme.getRowNumber(); i++) {
        this.getSystemeLocal().setObs(i + nb, systeme.getObs(i));
      }// for i boucle sur les nouvelles observations

      // on assemble le vecteur des contraintes :
      // on ajoute simplement les nouvelles contraintes apr�s celles d�j�
      // pr�sentes
      this.getSystemeLocal().getConstraints().addAll(systeme.getConstraints());

      // on s'occupe maintenant des inconnues
      // pour cela on parcourt le vecteur des inconnues de systeme
      for (int i = 0; i < systeme.getUnknowns().size(); i = i + 2) {
        LSPoint point = systeme.getUnknowns().get(i);
        if (this.getSystemeLocal().getUnknowns().contains(point)) {
          continue;
        }

        // on ajoute alors 2 fois point aux inconnues
        this.getSystemeLocal().getUnknowns().addElement(point);
        this.getSystemeLocal().getUnknowns().addElement(point);
      }// for i, boucle sur les inconnues de systeme

      // on assemble enfin les matrices A
      // on construit la nouvelle matrice A
      int lignes = this.getSystemeLocal().getObsRowNumber();
      int colonnes = this.getSystemeLocal().getUnknowns().size();
      this.getSystemeLocal().initMatriceA(lignes, colonnes);
      // on fait alors une double boucle pour remplir la matrice
      for (int i = 0; i < lignes; i++) {
        for (int j = 0; j < colonnes; j++) {
          // si i est encore dans les contraintes ini, on ne remplit
          // que les colonnes initiales
          if (i < local.getRowNumber()) {
            if (j >= local.getColumnNumber()) {
              continue;
            }

            this.getSystemeLocal().setA(i, j, local.getA(i, j));
            continue;
          }// if(i<=systemeLocal.matriceA.getRowDimension())

          // dans ce cas, on est dans les nouvelles contraintes
          // il faut trouver � quoi correspondent (i,j) dans systeme
          LSPoint inconnue = this.getSystemeLocal().getUnknowns().get(j);
          // on d�termine si c'est la colonne x du point
          boolean estX = false;
          if (!(j + 1 >= this.getSystemeLocal().getUnknowns().size())) {
            if (inconnue
                .equals(this.getSystemeLocal().getUnknowns().get(j + 1))) {
              estX = true;
            }
          }
          // si j n'est pas une inconnue de systeme, on laisse 0
          if (systeme.getUnknowns().contains(inconnue) == false) {
            continue;
          }
          // on d�termine la colonne correspondant dans systeme.matriceA
          int colonne = systeme.getUnknowns().indexOf(inconnue);
          if (estX == false) {
            colonne += 1;
          }

          // on assigne la valeur dans la matrice
          this.getSystemeLocal().setA(i, j,
              systeme.getA(i - local.getRowNumber(), colonne));
        }// for j, boucle sur les colonnes
      }// for i, boucle sur les lignes
      local.clear();
    }// while boucle sur contraintesExternes
  }// assembleContraintesExternes()

  /**
   * <p>
   * On instancie le set des contraintes internes qui s'appliquent � ce point.
   * Il s'agit de cr�er les objets java correspondant aux mapspecs.
   * @throws ClassNotFoundException
   * @throws NoSuchMethodException
   * @throws SecurityException
   * @throws InvocationTargetException
   * @throws IllegalAccessException
   * @throws IllegalArgumentException
   * @throws InstantiationException
   * 
   */
  void setContraintesInternes(MapspecsLS mapspecs, LSScheduler sched)
      throws ClassNotFoundException, SecurityException, NoSuchMethodException,
      IllegalArgumentException, IllegalAccessException,
      InvocationTargetException, InstantiationException {
    String className = this.objs.iterator().next().getClass().getName();
    Iterator<String> iter = mapspecs.getContraintesFixes().iterator();
    if (ReflectionUtil.containsClassOrSuper(mapspecs.getClassesRigides(),
        className)) {
      iter = mapspecs.getContraintesRigides().iterator();
    } else if (ReflectionUtil.containsClassOrSuper(
        mapspecs.getClassesMalleables(), className)) {
      iter = mapspecs.getContraintesMalleables().iterator();
    }

    // on fait une boucle sur iter
    while (iter.hasNext()) {
      String nom = iter.next();
      // get the class from the name
      Class<?> constraintClass = Class.forName(nom);
      Method applyMethod = constraintClass.getDeclaredMethod("appliesTo",
          LSPoint.class);
      if (!(Boolean) applyMethod.invoke(null, this)) {
        continue;
      }
      Constructor<?> constr = constraintClass.getConstructor(LSPoint.class,
          LSScheduler.class);
      this.internalConstraints.add((LSInternalConstraint) constr.newInstance(
          this, sched));

    }// while iter
  }// setContraintesInternes()

  /**
   * <p>
   * On instancie le set des contraintes externes qui s'appliquent à ce point.
   * Il s'agit de créer les objets java correspondant aux mapspecs.
   * @throws ClassNotFoundException
   * 
   */
  void setContraintesExternes(MapspecsLS mapspecs, LSScheduler sched)
      throws ClassNotFoundException {
    Iterator<String[]> iter;
    iter = mapspecs.getContraintesExternes().keySet().iterator();

    // on fait une boucle sur tous les objets contenant ce point
    for (IFeature obj : this.objs) {
      // on fait une boucle sur iter
      while (iter.hasNext()) {
        String[] contrainte = iter.next();
        String nomContr = contrainte[0];
        String nomClasse1 = contrainte[1];
        Class<?> classe1 = Class.forName(nomClasse1);
        String nomClasse2 = contrainte[2];
        Class<?> classe2 = Class.forName(nomClasse2);

        // on cherche si cette contrainte concerne ce point
        Class<?> classePt = obj.getClass();
        if ((!nomClasse1.equals(classePt))
            && (!classe1.isAssignableFrom(classePt))
            && (!classe2.equals(classePt))
            && (!classe2.isAssignableFrom(classePt))) {
          continue;
        }

        // on détermine l'autre classe
        Class<?> autreClasse = classe2;
        if (classe2.equals(classePt) || classe2.isAssignableFrom(classePt)) {
          autreClasse = classe1;
        }

        // on teste le nom de la contrainte (proximité, coalescence,
        // orientation...)
        Class<?> constrClass = Class.forName(nomContr);
        if (constrClass.equals(LSCoalescenceConstraint.class)) {
          // on construit une contrainte pour ce point
          LSCoalescenceConstraint contr = new LSCoalescenceConstraint(this,
              obj, obj, sched);
          contr.seuilSep = mapspecs.getContraintesExternes().get(contrainte)
              .doubleValue();
          this.externalConstraints.add(contr);
        } else if (constrClass.equals(LSProximityConstraint.class)) {
          // on cherche les conflits
          double distance = mapspecs.getContraintesExternes().get(contrainte)
              .doubleValue();
          // on met la distance à l'échelle
          double dist_req = distance * sched.getMapspec().getEchelle() / 1000.0;
          Map<LSSpatialConflict, Set<IFeature>> conflits = this
              .rechercheConflitsTIN(dist_req, sched, autreClasse);

          for (LSSpatialConflict conflit : conflits.keySet()) {
            // System.out.println(conflit.distance());
            for (IFeature voisin : conflits.get(conflit)) {
              LSProximityConstraint contr = new LSProximityConstraint(this,
                  obj, voisin, sched, conflit);
              contr.setSeuilSep(distance);
              this.externalConstraints.add(contr);
            }
          }// while boucle sur conflits
        }
        // TODO ajout de nouvelles contraintes externes ? (nécessite de rendre
        // la méthode plus générique

      }// boucle sur les contraintes
    }

  }// setContraintesExternes(MapspecsMC mapspecs,MCScheduler sched)

  public void calculeSystemeLocal() {
    // on assemble les contraintes internes
    this.assembleContraintesInternes();

    // on assemble les contraintes externes
    this.assembleContraintesExternes();

  }

  /**
   * <p>
   * Pour une classe d'objets géographique et la distance minimum correspondante
   * la fonction recherche les objets voisins en conflits avec ce point. Dans le
   * cas de lignes connectées, ne génère pas de conflits au niveau du croisement
   * comme expliqué dans (Harrie, 2001).
   * 
   */
  public Map<LSSpatialConflict, Set<IFeature>> rechercheConflitsTIN(
      double distance, LSScheduler sched, Class<?> classeVoisin) {
    // on ajoute la largeur du symbole à la distance
    double dist = distance;
    dist += this.getSymbolWidth();
    dist += sched.getClassSymbolWidth(classeVoisin);
    // on commence par créer le set vide
    Map<LSSpatialConflict, Set<IFeature>> conflits = new HashMap<LSSpatialConflict, Set<IFeature>>();
    // on parcourt les conflits du scheduler et on garde ceux qui concernent
    // point1
    for (LSSpatialConflict conflit : sched.getConflits()) {
      if (conflit.getPoint1().equals(this)) {
        // on vérifie que l'objet voisin hérite bien de classeVoisin
        Set<IFeature> voisins = conflit.getObjsVoisins();
        Set<IFeature> voisinsConf = new HashSet<IFeature>();
        for (IFeature voisin : voisins) {
          if (classeVoisin.isInstance(voisin)) {
            voisinsConf.add(voisin);
          }
        }
        if (voisinsConf.size() != 0) {
          if (conflit.distance() <= dist) {
            conflits.put(conflit, voisinsConf);
          }
        }
      }
    }
    LSScheduler.logger.finer("nb de conflits réels: " + conflits.size());
    return conflits;
  }

  /**
   * <p>
   * Pour une classe d'objets géographique et la distance minimum correspondante
   * la fonction recherche les objets voisins en conflits avec ce point. Dans le
   * cas de lignes connectées, ne génère pas de conflits au niveau du croisement
   * comme expliqué dans (Harrie, 2001).
   * @throws NoSuchFieldException
   * @throws IllegalAccessException
   * @throws SecurityException
   * @throws IllegalArgumentException
   * 
   */
  public Set<IFeature> rechercheConflits(double distance, LSScheduler sched,
      Class<?> classeVoisin) throws IllegalArgumentException,
      SecurityException, IllegalAccessException, NoSuchFieldException {
    // on commence par créer le set vide
    Set<IFeature> voisinsConflit = new HashSet<IFeature>();

    // on ajoute la largeur du symbole à la distance
    double dist = distance;
    dist += this.getSymbolWidth();
    dist += sched.getClassSymbolWidth(classeVoisin);

    // on fait une requête spatiale autour du point
    Collection<IGeneObj> setVoisins = SpatialQuery.selectInRadius(
        this.getIniPt(), 1.25 * dist, classeVoisin);

    // il faut maintenant trier ce set pour ne garder que les vrais conflits
    // on commence par enlever l'objet lui-même s'il y est.
    setVoisins.removeAll(this.objs);

    // on teste si l'objet est malléable
    for (IFeature obj : this.objs) {
      if (sched.getMapspec().getClassesMalleables().contains(obj.getClass())) {
        // dans ce cas il faut filtrer les objets aux croisements
        // on commence par récupérer le set des objets intersectant
        Collection<IGeneObj> objsInter = SpatialQuery.selectCrossing(
            (ILineString) obj.getGeom(), classeVoisin);

        IGeometry geom = obj.getGeom();
        // on parcourt la collection des voisins en conflit
        for (IFeature voisin : setVoisins) {
          // on commence par tester s'il fait partie des objets g�n�ralis�s
          if ((!sched.getObjsFixes().contains(voisin))
              && (!sched.getObjsMalleables().contains(voisin))
              && (!sched.getObjsRigides().contains(voisin))) {
            continue;
          }
          // on teste s'il le voisin croise obj
          if (objsInter.contains(voisin)) {
            // on doit maintenant vérifier si le conflit se situe au
            // niveau du carrefour (on ne le traite pas) ou ailleurs
            // (on le traite dans ce cas)
            // pour cela, on va chercher si le point est à plus de
            // 2*distance du croisement
            // on commence par récupérer les deux géométries
            IGeometry geomVoisin = voisin.getGeom();
            IDirectPosition interPt = CommonAlgorithmsFromCartAGen
                .getCommonVertexBetween2Lines((ILineString) geom,
                    (ILineString) geomVoisin);
            int index1 = CommonAlgorithmsFromCartAGen
                .getNearestVertexPositionFromPoint(geom, interPt);
            int index2 = CommonAlgorithmsFromCartAGen
                .getNearestVertexPositionFromPoint(geom, this.getIniPt());

            // on calcule la distance entre les deux indices
            double dist2 = CommonAlgorithmsFromCartAGen
                .getLineDistanceBetweenIndexes((ILineString) geom, index1,
                    index2);
            if (dist2 >= 2 * dist2) {
              voisinsConflit.add(voisin);
            }
          } else {
            voisinsConflit.add(voisin);
          }
        }// for boucle sur setVoisins

      } else {
        // dans ce cas, on garde tous les objets voisins de la zone à traiter
        // dans les conflits
        for (IFeature voisin : setVoisins) {
          if ((!sched.getObjsRigides().contains(voisin))
              && (!sched.getObjsFixes().contains(voisin))
              && (!sched.getObjsMalleables().contains(voisin))) {
            continue;
          }
          voisinsConflit.add(voisin);
        }// for boucle sur setVoisins
      }// else du test sur les classes Malléables
    }

    return voisinsConflit;
  }// rechercheConflits(distance,classeVoisin)

  @Override
  public String toString() {
    return this.objs.toString() + "\n" + this.getIniPt().toString();
  }

  /**
   * Détermine si deux points sont voisins au sens de (Harrie, 2002) : même
   * objet et à moins de 8 vertices l'un de l'autre.
   * 
   * @param point2
   * @return
   */
  public boolean estVoisin(LSScheduler sched, LSPoint point2) {
    // cas simple : sont-ils portés par le même objet
    Set<IFeature> test = new HashSet<IFeature>(this.objs);
    test.retainAll(point2.objs);
    if (test.size() == 0) {
      return false;
    }
    // on récupère l'objet de this.objs qui contient point2
    IFeature obj = test.iterator().next();
    // on calcule l'écart de vertices entre les deux points
    IGeometry geom = obj.getGeom();
    if (sched.getObjsMalleables().contains(obj)) {
      geom = LineDensification.densification((ILineString) geom, 50.0);
    }
    if (geom instanceof ILineString) {
      int ecart = 0;
      LSPoint premier = null, deuxieme = null;
      for (IDirectPosition coord : geom.coord()) {
        if (premier == null) {
          if (coord.equals(this.getIniPt())) {
            premier = this;
            deuxieme = point2;
          } else if (coord.equals(point2.getIniPt())) {
            premier = point2;
            deuxieme = this;
          }
          continue;
        }
        ecart++;
        // on sort si coord vaut deuxieme
        if (coord.equals(deuxieme.getIniPt())) {
          break;
        }
      }
      if (ecart <= 8) {
        return true;
      }
      return false;
    }
    // cas d'un objet surfacique
    IRing ring = ((IPolygon) geom).getExterior();
    int ecart = 0;
    int nbVertices = ring.numPoints();
    LSPoint premier = null, deuxieme = null;
    for (IDirectPosition coord : geom.coord()) {
      if (premier == null) {
        if (coord.equals(this.getIniPt())) {
          premier = this;
          deuxieme = point2;
        } else if (coord.equals(point2.getIniPt())) {
          premier = point2;
          deuxieme = this;
        }
        continue;
      }
      ecart++;
      // on sort si coord vaut deuxieme
      if (coord.equals(deuxieme.getIniPt())) {
        break;
      }
    }
    if (ecart <= 8) {
      return true;
    } else if (nbVertices - ecart <= 8) {
      return true;
    } else {
      return false;
    }
  }

  public void setContraintesInternes(
      Set<LSInternalConstraint> contraintesInternes) {
    this.internalConstraints = contraintesInternes;
  }

  public Set<LSInternalConstraint> getInternalConstraints() {
    return this.internalConstraints;
  }

  public Set<LSExternalConstraint> getExternalConstraints() {
    return this.externalConstraints;
  }

  public boolean isPointIniFin() {
    return this.pointIniFin;
  }

  public void setPointIniFin(boolean pointIniFin) {
    this.pointIniFin = pointIniFin;
  }

  public void setSystemeLocal(EquationsSystem systemeLocal) {
    this.systemeLocal = systemeLocal;
  }

  public EquationsSystem getSystemeLocal() {
    return this.systemeLocal;
  }

  public void setFinalPt(IDirectPosition finalPt) {
    this.finalPt = finalPt;
  }

  public void setIniPt(IDirectPosition iniPt) {
    this.iniPt = iniPt;
  }

  public void setTypeGeom(GeometryType typeGeom) {
    this.typeGeom = typeGeom;
  }

  public GeometryType getTypeGeom() {
    return this.typeGeom;
  }

  public void setPosition(double position) {
    this.position = position;
  }

  public double getPosition() {
    return this.position;
  }

  public double getSymbolWidth() {
    return this.symbolWidth;
  }

  public void setSymbolWidth(double symbolWidth) {
    this.symbolWidth = symbolWidth;
  }

  public Set<IFeature> getObjs() {
    return this.objs;
  }

  public void setObjs(Set<IFeature> objs) {
    this.objs = objs;
  }

  /**
   * Récupère le vecteur de déplacement calculé sur ce point par les moindres
   * carrés.
   * @return
   */
  public Vector2D getVecteurDepl() {
    // cas o� le point n'a pas encore �t� d�plac�
    if (this.getFinalPt() == null) {
      return new Vector2D(0.0, 0.0);
    }
    // cas g�n�ral
    return new Vector2D(this.getFinalPt().getX() - this.getIniPt().getX(), this
        .getFinalPt().getY() - this.getIniPt().getY());
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    return new LSPoint(this.objs.iterator().next(), new DirectPosition(
        this.iniPt.getX(), this.iniPt.getY()), this.position, this.typeGeom,
        this.pointIniFin, this.fixed, this.symbolWidth, this.sched);
  }

}

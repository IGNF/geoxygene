package fr.ign.cogit.geoxygene.sig3d.analysis.roof;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.equation.LineEquation;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Edge;
import fr.ign.cogit.geoxygene.sig3d.geometry.topology.Triangle;
import fr.ign.cogit.geoxygene.sig3d.topology.CarteTopo3D;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.util.algo.SmallestSurroundingRectangleComputation;




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
 * @version 1.7
 * 
 * Classe pour détecter le type de toit d'un bâtiment
 * 
 * 
 */
public class ClassifyRoof {

  static Logger logger = Logger.getLogger(ClassifyRoof.class.getName());

  /**
   * Les noms associés aux différents types
   */
  public static final String[] NOMS_TYPES = { "Toit plat", "Toits terrasses",
      "Toit 1 pan", "Toit 2 pans transverses", "Toit 2 pans non-transvers",
      "Toit 3 pans", "Toit 3 pans autres", "Toit 4 pans", "Autre",
      "Indécidable" };

  /**
   * Les identifiants rattachés
   */
  public static final int TYPE_TOIT_PLAT = 0;
  public static final int TYPE_TOIT_TERASSE = 1;
  public static final int TYPE_TOIT_APPENTIS = 2;
  public static final int TYPE_2_PANS_TRANSVERSE = 3;
  public static final int TYPE_2_PANS_NON_TRANSVERSE = 4;
  public static final int TYPE_3_PANS = 5;
  public static final int TYPE_3_PANS_AUTRE = 6;
  public static final int TYPE_4_PANS = 7;
  public static final int TYPE_AUTRE = 8;
  public static final int TYPE_UNDECIDED = 9;

  public static final int NB_CLASSES = 10;

  List<Edge> lEdgeRoof = new ArrayList<Edge>();
  List<Edge> lEdgeInteriorRoof = new ArrayList<Edge>();
  
  
  
  List<Edge> lEdgeExteriorRoof = new ArrayList<Edge>();

  List<List<Edge>> lEdgeGroup = new ArrayList<List<Edge>>();

  int type = -1;

  List<List<Triangle>> triangleGroup = null;

  IFeature feat;

  public ClassifyRoof(IFeature feat, double epsilonAngle, double epsilonDistRoof) {
    this.feat = feat;

    this.classify(epsilonAngle, epsilonDistRoof);

    if (this.getType() == TYPE_AUTRE || this.getType() == TYPE_UNDECIDED) {
      // this.classifyComplex(epsilonAngle, epsilonDistRoof);
    }

  }

  /**
   * On classifie un bâtiment
   * @param epsilonAngle
   * @param epsilonDistRoof
   * @return
   */
  private int classify(double epsilonAngle, double epsilonDistRoof) {

    logger.debug("Début de classification du Toit");

    @SuppressWarnings("unchecked")
    IMultiSurface<IOrientableSurface> iMS = (IMultiSurface<IOrientableSurface>) RoofDetection
        .detectRoof(feat, 0.05, true);
    
    
    
    
    if(iMS.size() == 1){
      
      
      ApproximatedPlanEquation aPE = new ApproximatedPlanEquation(iMS.get(0));
      
      
      if(aPE.getNormale().getNormalised().getZ() > 0.9){
        return  TYPE_TOIT_PLAT;
      }else{
        return TYPE_TOIT_APPENTIS;
      }
      
      
      
    }
    
    

    logger.debug("Extraction du toit : " + iMS.size() + "   faces ");

    logger.debug("Création de la topologie pour le toits");

    CarteTopo3D cT = new CarteTopo3D(iMS);

    List<Triangle> lTri = cT.getlTrianglesTopo();

    return this.classifyLTriangle(lTri, epsilonAngle, epsilonDistRoof);

  }

  /**
   * On classifie les corps du bâtiment
   * @param epsilonAngle
   * @param epsilonDistRoof
   * @return
   */
  @SuppressWarnings("unused")
  private int classifyComplex(double epsilonAngle, double epsilonDistRoof) {

    @SuppressWarnings("unchecked")
    IMultiSurface<IOrientableSurface> iMS = (IMultiSurface<IOrientableSurface>) RoofDetection
        .detectRoof(feat, 0.05, true);

    CarteTopo3D cT = new CarteTopo3D(iMS);
    List<List<Triangle>> lTT = cT.getGroupes();

    int type = -1;

    for (List<Triangle> lTri : lTT) {

      type = Math.max(type,
          this.classifyLTriangle(lTri, epsilonAngle, epsilonDistRoof));

    }

    this.type = type;
    return type;
  }

  private int classifyLTriangle(List<Triangle> lTri, double epsilonAngle,
      double epsilonDistRoof) {

    logger.debug("Nombre de triangles : " + lTri.size());

    // La sortie
    List<List<Triangle>> lLTriFinal = new ArrayList<List<Triangle>>();

    // On prépare les equations de plant
    List<PlanEquation> lPlanEq = new ArrayList<PlanEquation>();

    logger.debug("Initialisation des équations de plan");

    List<Edge> uniqueNeighbour = new ArrayList<Edge>();

    for (Triangle t : lTri) {

      List<Edge> lE = t.calculEdge();

      for (Edge e : lE) {

        if (e.getNeighbourTriangles().size() == 1) {

          uniqueNeighbour.add(e);
        }

      }
      lPlanEq.add(new PlanEquation(t));
    }

    logger
        .debug("Classification des triangles en fonctions des équations de plan");

    while (lTri.size() != 0) {

      logger.debug("************ Triangles restants " + lTri.size()
          + "************");

      Triangle triAct = lTri.remove(0);
      PlanEquation pEqAct = lPlanEq.remove(0);
      Vecteur vAct = pEqAct.getNormale();
      vAct.normalise();

      List<Triangle> lTriTemp = new ArrayList<Triangle>();
      lTriTemp.add(triAct);

      int nbTri = lTri.size();

      for (int i = 0; i < nbTri; i++) {

        Triangle triTemp = lTri.get(i);
        PlanEquation pEqTemp = lPlanEq.get(i);
        Vecteur vTemp = pEqTemp.getNormale();
        vTemp.normalise();

        double prodSca = vTemp.prodScalaire(vAct);

        boolean samePlan = Math.abs(prodSca) > 1 - epsilonAngle;

        if (!samePlan) {
          continue;
        }

        boolean toAdd = false;

        IDirectPosition centre = triTemp.getCenter();

        double val = pEqAct.equationValue(centre);

        toAdd = Math.abs(val) < epsilonDistRoof;

        if (!toAdd) {
          continue;
        }

        lTri.remove(i);
        lPlanEq.remove(i);
        i--;
        nbTri--;
        lTriTemp.add(triTemp);

      }

      logger.debug("************ Groupe crée  " + lTriTemp.size()
          + "triangles ************");

      lLTriFinal.add(lTriTemp);

    }

    triangleGroup = lLTriFinal;

    logger.debug("************ Création des groupes d'arrêtes ************");

    for (List<Triangle> lTriTemp : triangleGroup) {

      List<Edge> lETemp = new ArrayList<Edge>();

      for (Triangle triTemp : lTriTemp) {

        List<Edge> eT = triTemp.calculEdge();

        for (Edge e : eT) {

          List<Triangle> lTriN = e.getNeighbourTriangles();

          int count = 0;

          for (Triangle tN : lTriN) {

            if (lTriTemp.contains(tN)) {

              count++;

            }

          }

          if (count == 1) {
            this.lEdgeRoof.add(e);
            lETemp.add(e);
          }

        }

      }

      lEdgeGroup.add(lETemp);

    }

    logger.debug("************ Groupes d'arrêtes crées ************");

    logger.debug("************ Choix du toit de toit************");

    this.type = this.detectType(epsilonAngle, epsilonDistRoof);

//    // logger.info("Type choisi  : " + NOMS_TYPES[this.type]);

   boucle2 :  for (Edge e2 : this.lEdgeRoof) {

      for (Edge e : uniqueNeighbour) {

        if (areSimilar(e, e2, 0.01)) {
          this.lEdgeExteriorRoof.add(e2);
          continue boucle2;
        }

      }
      this.lEdgeInteriorRoof.add(e2);
    }

    return this.type;
  }

  public static Logger getLogger() {
    return logger;
  }

  public static String[] getNomsTypes() {
    return NOMS_TYPES;
  }

  public static int getTypeToitTerasse() {
    return TYPE_TOIT_TERASSE;
  }

  public static int getTypeToitAppentis() {
    return TYPE_TOIT_APPENTIS;
  }

  public static int getType2PansTransverse() {
    return TYPE_2_PANS_TRANSVERSE;
  }

  public static int getType2PansNonTransverse() {
    return TYPE_2_PANS_NON_TRANSVERSE;
  }

  public static int getType3Pans() {
    return TYPE_3_PANS;
  }

  public static int getType3PansAutre() {
    return TYPE_3_PANS_AUTRE;
  }

  public static int getType4Pans() {
    return TYPE_4_PANS;
  }

  public static int getTypeAutre() {
    return TYPE_AUTRE;
  }

  public static int getTypeUndecided() {
    return TYPE_UNDECIDED;
  }

  public static int getNbClasses() {
    return NB_CLASSES;
  }

  public List<Edge> getlEdgeExteriorRoof() {
    return lEdgeExteriorRoof;
  }

  public List<List<Edge>> getlEdgeGroup() {
    return lEdgeGroup;
  }

  public IFeature getFeat() {
    return feat;
  }

  private int detectType(double epsilonAngle, double epsilonDistRoof) {

    if (this.triangleGroup.size() == 1) {

      logger.debug("1 seul pan de toit");

      // Toit 1 pan
      if (isRoofFlat(this.triangleGroup.get(0), epsilonAngle)) {

        return TYPE_TOIT_PLAT;

      } else {

        return TYPE_TOIT_APPENTIS;
      }

    }

    boolean allRoofFlat = true;

    for (List<Triangle> lT : this.triangleGroup) {

      if (!isRoofFlat(lT, epsilonAngle)) {

        allRoofFlat = false;

      }

    }
    // Toutes les contributions sont plates
    if (allRoofFlat) {
      return TYPE_TOIT_TERASSE;
    }

    // TOIT à 2 PANS ?
    if (this.triangleGroup.size() == 2) {
      return this.classify2Pans(epsilonAngle);

    }

    if (this.triangleGroup.size() == 3) {

      return this.classify3Pans(epsilonAngle);
    }

    if (this.triangleGroup.size() == 4) {

      return this.classify4Pans(epsilonAngle);
    }

    return TYPE_AUTRE;

  }

  private int classify2Pans(double angleEpsilon) {

    logger.debug("************ Classification 2 pans de toits ************");

    List<Edge> lECent = getCommonEdge(lEdgeGroup.get(0), lEdgeGroup.get(1));

    if (lECent.size() == 0) {

      logger
          .warn("************ Problème topologique pas d'arrêtes communes entre les pans de toits (application égalité lourde)************");

      lECent = getCommonEdgeHeavy(lEdgeGroup.get(0), lEdgeGroup.get(1),
          angleEpsilon);

      if (lECent.size() == 0) {
        logger
            .error("************ Non décidable :  pas d'arrêtes communes entre les pans de toits  ************");
        return TYPE_UNDECIDED;
      }

    }

    Vecteur orientation = null;

    if (lECent.size() >= 2) {

      int nbCE = lECent.size();

      for (int i = 0; i < nbCE; i++) {

        Edge e1 = lECent.get(i);

        for (int j = i + 1; j < nbCE; j++) {
          Edge e2 = lECent.get(j);
          e1.normalise();
          e2.normalise();

          if (Math.abs(e1.prodScalaire(e2)) < 1 - angleEpsilon) {

            logger
                .error("************ Non décidable : pas d'orientation principale pour les arrêtes du milieu ************");

            return TYPE_UNDECIDED;
          }

        }
      }

    }

    orientation = lECent.get(0).getNormalised();

    IMultiSurface<IOrientableSurface> iMS = new GM_MultiSurface<IOrientableSurface>();
    iMS.addAll( this.triangleGroup.get(0));   
    
    iMS.addAll(this.triangleGroup.get(1));

    IPolygon poly = SmallestSurroundingRectangleComputation.getSSR(iMS);

    IDirectPositionList dpl = poly.coord();

    Vecteur v1 = new Vecteur(dpl.get(0), dpl.get(1));
    v1.normalise();
    Vecteur v2 = new Vecteur(dpl.get(1), dpl.get(2));
    v2.normalise();

    if (Math.abs(v1.prodScalaire(orientation)) > 1 - angleEpsilon * 4) {
      return TYPE_2_PANS_TRANSVERSE;
    }

    if (Math.abs(v2.prodScalaire(orientation)) > 1 - angleEpsilon * 4) {
      return TYPE_2_PANS_TRANSVERSE;
    }

    // System.out.println(Math.max(Math.abs(v1.prodScalaire(orientation)),Math.abs(v2.prodScalaire(orientation)
    // )));

    return TYPE_2_PANS_NON_TRANSVERSE;
  }

  private void removeDouble(List<Edge> lECent) {

    for (int i = 0; i < lECent.size(); i++) {

      for (int j = i + 1; j < lECent.size(); j++) {

        if (lECent.get(i).equals(lECent.get(j))) {

          lECent.remove(i);
          i--;
          break;

        }

      }

    }

  }

  private int classify3Pans(double epsilonAngle) {
    logger.debug("************ Classification 3 pans de toits ************");

    List<Edge> lECent = getCommonEdge(lEdgeGroup.get(0), lEdgeGroup.get(1));
    lECent.addAll(getCommonEdge(lEdgeGroup.get(0), lEdgeGroup.get(2)));
    lECent.addAll(getCommonEdge(lEdgeGroup.get(1), lEdgeGroup.get(2)));

    removeDouble(lECent);

    if (lECent.size() == 3) {
      /*
      if (Double.parseDouble(feat.getAttribute("ID_GEO").toString()) == 31620) {
        logger.debug("DEBUG TIME");
      }*/

      // Toit 3 pans : 76 / 994
      // Toit 3 pans autres : 8 / 994

      // On regarde si les normales formées par les plans principaux forment 2
      // angles droits
      PlanEquation pE1 = new PlanEquation(triangleGroup.get(0).get(0));
      PlanEquation pE2 = new PlanEquation(triangleGroup.get(1).get(0));
      PlanEquation pE3 = new PlanEquation(triangleGroup.get(2).get(0));

      Vecteur v1 = pE1.getNormale();
      Vecteur v2 = pE2.getNormale();
      Vecteur v3 = pE3.getNormale();

      v1.setZ(0);
      v2.setZ(0);
      v3.setZ(0);

      v1.normalise();
      v2.normalise();
      v3.normalise();

      int nbAngleDroit = 0;

      double p1 = Math.abs(v1.prodScalaire(v2));
      double p2 = Math.abs(v1.prodScalaire(v3));
      double p3 = Math.abs(v3.prodScalaire(v2));

      if (p1 < 0.2) {
        nbAngleDroit++;

      }

      if (p2 < 0.2) {
        nbAngleDroit++;

      }

      if (p3 < 0.2) {
        nbAngleDroit++;

      }

      if (nbAngleDroit == 2) {

        return TYPE_3_PANS;
      }

      /*
       * if (nbAngleDroit == 2) {
       * 
       * Edge e1 = lECent.get(0); Edge e2 = lECent.get(1); Edge e3 =
       * lECent.get(2);
       * 
       * if (e1.getLineString().intersects(e2.getLineString().buffer(0.2))) { if
       * (e1.getLineString().intersects(e3.getLineString().buffer(0.2))) {
       * 
       * Vertex vCentre = e1.getVertIni(); Vertex vert1 = e1.getVertFin();
       * 
       * if (!e2.getVertFin().equals2D(vCentre, 0.2) &&
       * e2.getVertIni().equals2D(vCentre, 0.2)) { vCentre = e2.getVertFin();
       * vert1 = e1.getVertIni(); }
       * 
       * Vertex vert2 = e2.getVertIni(); if (vert2.equals2D(vCentre, 0.2)) {
       * vert2 = e2.getVertFin(); }
       * 
       * Vertex vert3 = e3.getVertIni(); if (vert3.equals2D(vCentre, 0.2)) {
       * vert3 = e3.getVertFin(); }
       * 
       * IDirectPosition vNord = (new Vecteur(50, 0, 0)).translate(vCentre);
       * 
       * Angle a1 = Angle.angleTroisPoints(vCentre, vNord, vert1); Angle a2 =
       * Angle.angleTroisPoints(vCentre, vNord, vert2); Angle a3 =
       * Angle.angleTroisPoints(vCentre, vNord, vert3);
       * 
       * System.out.println(a1.getValeur() + "    " + a2.getValeur() + "   " +
       * a3.getValeur());
       * 
       * // On trie les angles
       * 
       * if (a1.getValeur() > a2.getValeur()) { Angle aTemp = a1; a1 = a2; a2 =
       * aTemp; }
       * 
       * if (a3.getValeur() < a2.getValeur()) { Angle aTemp = a2; a2 = a3; a3 =
       * aTemp; }
       * 
       * if (a1.getValeur() > a2.getValeur()) { Angle aTemp = a1; a1 = a2; a2 =
       * aTemp; }
       * 
       * Vecteur v1e = e1.getNormalised(); Vecteur v2e = e2.getNormalised();
       * Vecteur v3e = e3.getNormalised();
       * 
       * v1e.setZ(0); v2e.setZ(0); v3e.setZ(0);
       * 
       * v1e.normalise(); v2e.normalise(); v3e.normalise();
       * 
       * int nbAngleAigu = 0;
       * 
       * double prodV1E = v1e.prodScalaire(v2e); double prodV2E =
       * v1e.prodScalaire(v3e); double prodV3E = v2e.prodScalaire(v3e);
       * 
       * if (prodV1E > 0) { nbAngleAigu++;
       * 
       * }
       * 
       * if (prodV2E > 0) { nbAngleAigu++;
       * 
       * }
       * 
       * if (prodV3E > 0) { nbAngleAigu++;
       * 
       * }
       * 
       * if (nbAngleAigu != 1) { logger.error("Entité : " +
       * this.feat.getAttribute("ID_GEO") + "     " + nbAngleAigu); return
       * TYPE_3_PANS_AUTRE; }
       * 
       * return TYPE_3_PANS; }
       * 
       * }
       * 
       * }
       */

      logger.warn("Entité : " + this.feat.getAttribute("ID_GEO")
          + " 3 pans - non classé");

      return TYPE_3_PANS_AUTRE;
    }

    return TYPE_UNDECIDED;
  }

  private int classify4Pans(double epsilonAngle) {
    // TODO Auto-generated method stub
    return TYPE_4_PANS;
  }

  private boolean isRoofFlat(List<Triangle> lT, double epsilonAngle) {
    boolean isFlat = true;

    for (Triangle t : lT) {

      PlanEquation pE = new PlanEquation(t);
      double z = Math.abs(pE.getNormale().getZ());
      if (z < 1 - epsilonAngle) {

        isFlat = false;

      }

    }

    return isFlat;
  }

  private static List<Edge> getCommonEdge(List<Edge> l1, List<Edge> l2) {

    List<Edge> lE = new ArrayList<Edge>();

    for (Edge e1 : l1) {
      for (Edge e2 : l2) {

        if (e1.equals(e2)) {
          lE.add(e1);
        }

      }

    }
    return lE;
  }

  private static List<Edge> getCommonEdgeHeavy(List<Edge> l1, List<Edge> l2,
      double angleEpsilon) {

    List<Edge> lE = new ArrayList<Edge>();

    for (Edge e1 : l1) {
      for (Edge e2 : l2) {

        if (areSimilar(e1, e2, angleEpsilon)) {
          lE.add(e1);
        }

      }

    }
    return lE;
  }

  private static boolean areSimilar(Edge e1, Edge e2, double angleEpsilon) {

    Vecteur v1 = e1.getNormalised();
    Vecteur v2 = e2.getNormalised();
    // System.out.println(Math.abs(v1.prodScalaire(v2)));
    if (Math.abs(v1.prodScalaire(v2)) < 1 - 4 * angleEpsilon) {
      return false;
    }

    LineEquation lE = new LineEquation(e1.getVertIni(), e1.getVertFin());

    return lE.isPointOnLine(e2.getVertFin());

  }

  public static int getTypeToitPlat() {
    return TYPE_TOIT_PLAT;
  }

  public List<Edge> getlEdgeRoof() {
    return lEdgeRoof;
  }

  public List<Edge> getlEdgeInteriorRoof() {
    return lEdgeInteriorRoof;
  }

  public int getType() {
    return type;
  }

  public List<ILineString> getExteriorLineStrings() {

    List<ILineString> lS = new ArrayList<ILineString>();

    for (Edge e : this.lEdgeExteriorRoof) {
      lS.add(e.getLineString());
    }

    return lS;

  }
  
  
  public List<ILineString> getInteriorLineStrings() {

    List<ILineString> lS = new ArrayList<ILineString>();

    for (Edge e : this.lEdgeInteriorRoof) {
      lS.add(e.getLineString());
    }

    return lS;

  }
  
  
  
  public List<ILineString> getFaitage(double pente){
    List<ILineString> lS = this.getInteriorLineStrings();
    List<ILineString> lSOut = new ArrayList<ILineString>();
    
    
    for(ILineString ls:lS){
      
      
      double zVar = ls.coord().get(0).getZ() - ls.coord().get(1).getZ();
      
      
      if(Math.abs(zVar)/ls.length() < pente)
      {
        lSOut.add(ls);
      }
      
      
      
      
      
      
    }
    
    
    
    
    
    return lSOut;
    
    
  }

  public List<ILineString> getLineStrings() {

    List<ILineString> lS = new ArrayList<ILineString>();

    for (Edge e : this.lEdgeRoof) {
      lS.add(e.getLineString());
    }

    return lS;

  }

  public List<List<Triangle>> getTriangleGroup() {
    return triangleGroup;
  }

}

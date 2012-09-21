package fr.ign.cogit.geoxygene.sig3d.sample.rge;

import java.awt.Color;
import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.geometrie.Vecteur;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.sig3d.conversion.Extrusion2DObject;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object1d;
import fr.ign.cogit.geoxygene.sig3d.representation.toponym.BasicToponym3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.DTM;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiCurve;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiPoint;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Curve;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

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
 * 
 *          Il s'agit d'une extension du MNT de base utilisée ici pour appliquer
 *          des styles et proposer des placages différents en fonction des
 *          shapefiles du RGE Extension from MNT class. Some predefined style
 *          are applied in accordance with the class of the different shapefile
 */
public class MNTRGE extends DTM {

  /*
   * Les différents noms de couches gérés
   */
  private static String[] lCouches = new String[] { "COMMUNE", "CHEMIN",
      "ROUTE_PRIMAIRE", "ROUTE_SECONDAIRE", "GARE", "TRONCON_VOIE_FERREE",
      "CONDUITE", "LIGNE_ELECTRIQUE", "POSTE_TRANSFORMATION", "PYLONE",
      "RESERVOIR_EAU", "SURFACE_EAU", "TRONCON_COURS_EAU",
      "BATI_INDIFFERENCIE", "TERRAIN_SPORT", "ORONYME", "LIEU_DIT_HABITE",
      "LIEU_DIT_NON_HABITE", "CHEF_LIEU", "BATI_INDUSTRIEL", "ZONE_VEGETATION" };
  /*
   * A chaque nom de couche est associé une couleur (relation 1 - 1)
   */
  private static Color[] lCouleur = new Color[] { new Color(61, 43, 31), // Chemin
      new Color(61, 43, 31),// Commune

      new Color(255, 0, 0), // Route Primaire
      new Color(255, 255, 0), // Route Secondaire
      new Color(0, 0, 0), // Gare
      new Color(0, 0, 0),// Voie_ferrée
      new Color(0, 0, 0),// conduite
      new Color(0, 0, 0),// ligne àlectrique
      new Color(0, 0, 0),// poste transformation
      new Color(0, 0, 0),// /pylone
      new Color(0, 0, 255),// reservoir eau
      new Color(0, 0, 255), // surface eau
      new Color(0, 0, 255), // troncon cours eau
      new Color(255, 165, 0), // bati indifferencie
      new Color(0, 165, 0),// Terrain sport
      new Color(61, 43, 31),// oronyme
      new Color(255, 165, 0),// lieu dit habité
      new Color(255, 165, 0), // lieu dit non habité

      new Color(255, 0, 0), // Chef-lieu
      new Color(255, 0, 255),// Bati industriel
      new Color(0, 255, 0) // Zone végétation

  };

  /**
   * Fait appel au constructeur de la classe mère
   * 
   * @param fichier
   * @param nomCouche
   * @param fill
   * @param exager
   * @param degradeRGB
   */
  public MNTRGE(String fichier, String nomCouche, boolean fill, int exager,
      Color[] degradeRGB) {
    super(fichier, nomCouche, fill, exager, degradeRGB);
  }

  /**
   * Fait appel au constructeur de la classe mère
   * 
   * @param fichier
   * @param nomCouche
   * @param fill
   * @param exager
   * @param nomFichierImage
   * @param empriseImage
   */
  public MNTRGE(String fichier, String nomCouche, boolean fill, int exager,
      String nomFichierImage, GM_Envelope empriseImage) {
    super(fichier, nomCouche, fill, exager, nomFichierImage, empriseImage);
  }

  /**
   * Permet de plaquer sur le MNT des shapefile contenant des géométries
   * linéaire ou surfacique Ces géométries ne seront représentés que par des
   * frontières plaquées sur le MNT TODO Cette fonction ne traite pas les trous
   * TODO Cette fonction ne traite pas les multi géométries
   * 
   * @param file fichier à charger
   * @param nomCouche nom de la couche stockant les objets chargés
   * @param echantillonne indique si l'on suréchantillonne la géométrie avec les
   *          mailles du MNT ou non
   * @return une couche avec une géométrie 3D dont l'altitude dépend du MNT
   */
  @SuppressWarnings("unchecked")
  public VectorLayer mapShape(String file, String nomCouche,
      boolean echantillonne) {
    // On récupère la couleur associé et on vérifie qu'elle fait partie des
    // couches traitées
    int nbElem = MNTRGE.lCouches.length;
    int indice = -1;
    for (int i = 0; i < nbElem; i++) {
      if (nomCouche.equalsIgnoreCase(MNTRGE.lCouches[i])) {

        indice = i;
        break;
      }

    }

    if (indice == -1) {

      return null;
    }

    try {
      // On récupère les entités en 2D
      IFeatureCollection<IFeature> ftColl = ShapefileReader.read(file);

      // Nombre d'élements
      int nb = ftColl.size();

      IFeatureCollection<IFeature> featCollFinal = new FT_FeatureCollection<IFeature>();

      // Liste des points d'un vecteur linéaire

      // Pour chaque géométrie, on affecte un Z
      for (int i = 0; i < nb; i++) {// nb

        System.out.println("Feature " + i);
        // On récupère la géométrie de chaque entité et on la transforme
        // en 3D en fonction du type
        IFeature feat = ftColl.get(i);

        IGeometry geom = feat.getGeom();

        if (geom instanceof GM_Curve) {

          if (nomCouche.equalsIgnoreCase("LIGNE_ELECTRIQUE")) {
            // Si c'est une ligne àlectrique, on la met 25 m au
            // dessus du MNT
            feat.setGeom(this.mapCurve((GM_Curve) geom, 25, true, false));
          } else {
            // Sinon on le plaque sur le MNT
            feat.setGeom(this.mapCurve((GM_Curve) geom, 0, true, echantillonne));
          }
          featCollFinal.add(feat);
          continue;

        }
        // Traité comme une liste de GM_Curve
        if ((geom instanceof GM_MultiCurve<?>)) {

          List<?> lCurves = ((GM_MultiCurve<?>) geom).getList();

          int nbLS = lCurves.size();

          IMultiCurve<ICurve> multiCurve = new GM_MultiCurve<ICurve>();

          for (int j = 0; j < nbLS; j++) {

            GM_Curve curve = (GM_Curve) lCurves.get(j);

            if (curve.coord().size() < 2) {

              continue;
            }

            if (nomCouche.equalsIgnoreCase("LIGNE_ELECTRIQUE")) {

              multiCurve.add(this.mapCurve(curve, 25, true, false));
            } else {

              multiCurve.add(this.mapCurve(curve, 0, true, echantillonne));
            }

          }
          feat.setGeom(multiCurve);
          featCollFinal.add(feat);
          continue;

        }

        if ((geom instanceof GM_Polygon)) {
          // Si c'est un polygone on l'extrude sur une altitude
          // hauteur
          // sinon on le plaque tel qu'elle sur le MNT
          GM_Polygon surface = (GM_Polygon) geom;

          GF_AttributeType attribut = (feat.getFeatureType())
              .getFeatureAttributeByName("HAUTEUR");// dataset.getPopulation(0).getFeatureType().getFeatureAttributes();

          double hauteur = 0;

          if (attribut != null) {

            hauteur = Double.parseDouble(feat.getAttribute(
                (AttributeType) attribut).toString());

          }

          feat.setGeom(this.mapSurface(surface, hauteur, true, hauteur == 0
              && echantillonne));
          featCollFinal.add(feat);

          continue;

        }
        // On traite les MultiSurface comme un ensemble de polygones
        if ((geom instanceof GM_MultiSurface<?>)) {

          List<?> lSurfaces = ((GM_MultiSurface<?>) geom).getList();

          int nbLS = lSurfaces.size();

          GF_AttributeType attribut = (feat.getFeatureType())
              .getFeatureAttributeByName("HAUTEUR");// dataset.getPopulation(0).getFeatureType().getFeatureAttributes();

          double hauteur = 0;

          if (attribut != null) {

            int index = feat.getFeatureType().getFeatureAttributes()
                .indexOf(attribut);

            hauteur = Double.parseDouble(((DefaultFeature) feat)
                .getAttributes()[index].toString());

            if (hauteur <= 0) {

              GM_MultiSurface<IOrientableSurface> geomFinal = new GM_MultiSurface<IOrientableSurface>();

              for (int j = 0; j < nbLS; j++) {
                IGeometry objTemp = this
                    .mapSurface((GM_Polygon) lSurfaces.get(j), hauteur, true,
                        echantillonne);

                if (objTemp instanceof GM_MultiSurface<?>) {
                  geomFinal
                      .addAll(((GM_MultiSurface<? extends IOrientableSurface>) objTemp)
                          .getList());
                } else if (objTemp instanceof GM_OrientableSurface) {

                  geomFinal.add((GM_OrientableSurface) objTemp);
                }

              }

              feat.setGeom(geomFinal);
              featCollFinal.add(feat);

              continue;
            } else if (nomCouche.equalsIgnoreCase("POSTE_TRANSFORMATION")) {
              hauteur = 5;

              GM_MultiSurface<GM_OrientableSurface> geomFinal = new GM_MultiSurface<GM_OrientableSurface>();

              for (int j = 0; j < nbLS; j++) {

                geomFinal.add((GM_OrientableSurface) this.mapSurface(
                    (GM_Polygon) lSurfaces.get(j), hauteur, true, hauteur == 0
                        && echantillonne));
              }

              feat.setGeom(geomFinal);
              featCollFinal.add(feat);

            } else {
              IGeometry gmFinal;
              if (nbLS == 1) {
                gmFinal = this.mapSurface((GM_Polygon) lSurfaces.get(0),
                    hauteur, true, hauteur == 0 && echantillonne);

              } else {

                GM_MultiSolid<GM_Solid> gMultiS = new GM_MultiSolid<GM_Solid>();

                for (int j = 0; j < nbLS; j++) {

                  gMultiS.add((GM_Solid) this.mapSurface(
                      (GM_Polygon) lSurfaces.get(j), hauteur, true,
                      hauteur == 0 && echantillonne));

                }
                gmFinal = gMultiS;

              }

              feat.setGeom(gmFinal);
              featCollFinal.add(feat);

              continue;
            }

          } else {

            GM_MultiSurface<IOrientableSurface> geomFinal = new GM_MultiSurface<IOrientableSurface>();

            for (int j = 0; j < nbLS; j++) {

              IGeometry objTemp = this.mapSurface(
                  (GM_Polygon) lSurfaces.get(j), hauteur, true, echantillonne);

              if (objTemp instanceof GM_MultiSurface<?>) {
                geomFinal
                    .addAll(((GM_MultiSurface<? extends IOrientableSurface>) objTemp)
                        .getList());
              } else if (objTemp instanceof GM_OrientableSurface) {

                geomFinal.add((GM_OrientableSurface) objTemp);

              }
            }
            featCollFinal.add(new DefaultFeature(geomFinal));
            continue;
          }

        }

        if ((geom instanceof GM_Point)) {

          feat.setGeom(this.mapGeom(geom, 0, true, false));

          featCollFinal.add(feat);
          continue;
        }

        if ((geom instanceof GM_MultiPoint)) {

          GM_MultiPoint geomfinale = new GM_MultiPoint();
          feat.setGeom(this.mapGeom(geomfinale, 0, true, false));
          featCollFinal.add(feat);
          continue;
        }
      }
      // Si c'est une geometrie ponctuelle
      if (featCollFinal.get(0).getGeom() instanceof GM_Point
          || featCollFinal.get(0).getGeom() instanceof GM_MultiPoint) {
        int nbObjet = featCollFinal.size();

        for (int i = 0; i < nbObjet; i++) {
          // Classe pylone, on extrude le point sur 25 m de haut
          if (nomCouche.equalsIgnoreCase("Pylone")) {

            DefaultFeature feat = (DefaultFeature) featCollFinal.get(i);

            IDirectPositionList dpl = feat.getGeom().coord();

            IDirectPosition dp1 = dpl.get(0);

            dpl.add(new DirectPosition(dp1.getX(), dp1.getY(), dp1.getZ() + 25));

            feat.setGeom(new GM_LineString(dpl));

            feat.setRepresentation(

            new Object1d(feat, true, Color.black, 1, true));

          } else {

            Color c = Color.BLACK;

            if ("LIEU_DIT_NON_HABITE".equals(nomCouche)) {
              c = Color.red;
            }

            DefaultFeature feat = (DefaultFeature) featCollFinal.get(i);
            // Sinon on le gère comme un toponyme
            String toponyme = "X";

            if (feat.getAttributes() != null) {
              if (feat.getAttributes().length > 1) {

                if ((String) feat.getAttribute("NOM") != null) {

                  toponyme = (String) feat.getAttribute("NOM");

                }
              }

            }
            IDirectPosition p = feat.getGeom().coord().get(0);
            p.setZ(p.getZ() + 150);

            feat.setGeom(new GM_Point(p));

            feat.setRepresentation(

            new BasicToponym3D(feat, c, 1, Math.PI / 2, 0, 0,

            toponyme, "Times", 50, true));

          }

        }

        return new VectorLayer(featCollFinal, nomCouche);

      } else {

        if (nomCouche.equalsIgnoreCase("TRONCON_VOIE_FERREE")) {

          return new VectorLayer(featCollFinal, nomCouche, true,
              MNTRGE.lCouleur[indice], 1, false);
        }

        // On affiche en appliquant la couleur toutes les entites
        VectorLayer VectorLayer = new VectorLayer(featCollFinal, nomCouche,
            true, MNTRGE.lCouleur[indice], 1, true);
        return VectorLayer;

      }

    } catch (Exception e) {

      e.printStackTrace();
    }

    return null;
  }

  /**
   * Classe permettant de construire un toit si le batiment à 4 sommets TODO : a
   * remettre
   * 
   * @param poly
   * @param zmin
   * @param zmax
   * @return renvoie un objet issu du polygone paramètre et possédant un toit
   */
  public static IGeometry extrudeToit(IPolygon poly, double zmin, double zmax) {

    IDirectPositionList pList = poly.coord();

    IDirectPosition p1 = pList.get(0);
    IDirectPosition p2 = pList.get(1);
    IDirectPosition p3 = pList.get(2);
    IDirectPosition p4 = pList.get(3);

    Vecteur v1 = new Vecteur(

    (p3.getX() + p4.getX()) / 2 - (p1.getX() + p2.getX()) / 2,
        (p3.getY() + p4.getY()) / 2 - (p1.getY() + p2.getY()) / 2, 0);

    Vecteur v2 = new Vecteur(

    (p3.getX() + p2.getX()) / 2 - (p4.getX() + p1.getX()) / 2,
        (p3.getY() + p2.getY()) / 2 - (p4.getY() + p1.getY()) / 2, 0);

    double n1 = v1.norme();
    double n2 = v2.norme();

    if (n1 > n2) {
      return MNTRGE.addRoof(poly, p1, p2, p3, p4, zmin, zmax);

    } else if (n2 > n1) {
      return MNTRGE.addRoof(poly, p2, p3, p4, p1, zmin, zmax);

    } else {

      return Extrusion2DObject.convertFromPolygon(poly, zmin, zmax);
    }

  }

  private static GM_Solid addRoof(IPolygon poly, IDirectPosition p1,
      IDirectPosition p2, IDirectPosition p3, IDirectPosition p4, double zmin,
      double zmax) {

    Vecteur v1 = new Vecteur(

    ((p3.getX() + p4.getX()) / 2) - ((p1.getX() + p2.getX()) / 2),
        ((p3.getY() + p4.getY()) / 2) - ((p1.getY() + p2.getY()) / 2), 0);

    DirectPosition mil = new DirectPosition(

    (p1.getX() + p2.getX()) / 2, (p1.getY() + p2.getY()) / 2, 0);

    DirectPosition dp1 = new DirectPosition(

    mil.getX() + 0.25 * v1.getX(), mil.getY() + 0.25 * v1.getY(), zmax);

    DirectPosition dp2 = new DirectPosition(

    mil.getX() + 0.75 * v1.getX(), mil.getY() + 0.75 * v1.getY(), zmax);

    double hGouttierre = 0.75 * (zmax - zmin) + zmin;

    List<IOrientableSurface> lOS = ((GM_Solid) Extrusion2DObject
        .convertFromPolygon(poly, zmin, hGouttierre)).getFacesList();

    // On enlève la face supérieure
    lOS.remove(lOS.size() - 2);

    p1.setZ(hGouttierre);
    p2.setZ(hGouttierre);
    p3.setZ(hGouttierre);
    p4.setZ(hGouttierre);

    DirectPositionList pl1 = new DirectPositionList();
    pl1.add(p1);
    pl1.add(dp1);
    pl1.add(p2);
    pl1.add(p1);

    DirectPositionList pl2 = new DirectPositionList();
    pl2.add(p2);
    pl2.add(dp1);
    pl2.add(dp2);
    pl2.add(p3);
    pl2.add(p2);

    DirectPositionList pl3 = new DirectPositionList();
    pl3.add(p3);
    pl3.add(dp2);
    pl3.add(p4);
    pl3.add(p3);

    DirectPositionList pl4 = new DirectPositionList();
    pl4.add(p4);
    pl4.add(dp2);
    pl4.add(dp1);
    pl4.add(p1);
    pl4.add(p4);

    lOS.add(new GM_Polygon(new GM_LineString(pl1)));
    lOS.add(new GM_Polygon(new GM_LineString(pl2)));
    lOS.add(new GM_Polygon(new GM_LineString(pl3)));
    lOS.add(new GM_Polygon(new GM_LineString(pl4)));

    return new GM_Solid(lOS);

  }

}

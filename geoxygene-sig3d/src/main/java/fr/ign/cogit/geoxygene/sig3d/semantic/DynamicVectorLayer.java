package fr.ign.cogit.geoxygene.sig3d.semantic;

import java.util.List;

import javax.media.j3d.Alpha;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.representation.I3DRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.dynamic.Object4d;
import fr.ign.cogit.geoxygene.sig3d.semantic.DefaultLayer;


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
 * DynamicVectorLayer couche permettant de représenter de manière dynamique une
 * trajectoire
 * 
 * 
 */
public class DynamicVectorLayer extends DefaultLayer {

  /**
   * La collection de trajectoires à afficher
   */
  FT_FeatureCollection<IFeature> collection = null;

  /**
   * 
   * Table des temps
   */
  List<List<Double>> time = null;

  /**
   * Emprise 3D de la couche
   */
  Box3D b = null;

  /**
   * Temps initiaux et finaux
   */
  private double tMin, tMax;

  /**
   * Crée une couche de trajectoire standarde ou l'objet est représentée par une
   * petite sphère
   * @param nomCouche nom de la couche créée
   * @param featColl collection de trajectoires à afficher
   * @param time table des temps à afficher
   * @param duree durée en seconde d'un cycle d'animation
   * @param size la taille du rayon de la sphère
   */
  public DynamicVectorLayer(String nomCouche,
      FT_FeatureCollection<IFeature> featColl, List<List<Double>> time,
      int duree, double size) {
    super();
    // On vérifie si les nombres coincident
    if (featColl.size() == time.size()) {
      // On fixe le nom de la couche
      this.setLayerName(nomCouche);
      // Les trajectoires à afficher
      this.collection = featColl;
      // Les temps
      this.time = time;
      // On génère l'envelope 3D
      this.generate3DEnvelope();
      // On génère les représentations
      this.generateStyle(duree, size);
      // On rafraichit
      this.refresh();
    } else {

      System.out
          .println("ERREUR : Le nombre de tableaux de temps et d'entité ne correspondent pas");
    }

  }

  /**
   * Crée une couche de trajectoire standarde ou l'objet est représentée par un
   * cube ou un rectangle de dimension donnée sur lequel on plaque une image
   * 
   * @param nomCouche nom de la couche créée
   * @param featColl collection de trajectoires à afficher
   * @param time table des temps à afficher
   * @param duree durée en seconde d'un cycle d'animation
   * @param path le chemin de l'image à plaquer
   * @param size la taille du cube ou du rectangle
   * @param isCube indique si c'est un cube ou un rectangle
   */
  public DynamicVectorLayer(String nomCouche,
      FT_FeatureCollection<IFeature> featColl, List<List<Double>> time,
      int duree, String path, double size, boolean isCube) {
    super();

    // On vérifie si les nombres coincident
    if (featColl.size() == time.size()) {
      // On fixe le nom de la couche
      this.setLayerName(nomCouche);
      // Les trajectoires à afficher
      this.collection = featColl;
      // Les temps
      this.time = time;
      // On génère l'envelope 3D
      this.generate3DEnvelope();
      // On génère les représentations
      this.generateStyle(duree, path, size, isCube);
      // On rafraichit
      this.refresh();
    } else {

      System.out
          .println("ERREUR : Le nombre de tableaux de temps et d'entité ne correspondent pas");
    }

  }

  /**
   * 
   * @param nomCouche nom de la couche créée
   * @param featColl collection de trajectoires à afficher
   * @param time table des temps à afficher
   * @param duree durée en seconde d'un cycle d'animation
   * @param path le chemin du fichier 3D
   * @param size le facteur de taille à plaquer
   */
  public DynamicVectorLayer(String nomCouche,
      FT_FeatureCollection<IFeature> featColl, List<List<Double>> time,
      int duree, String path, double size) {

    this(nomCouche, featColl, time, duree, path, size, 0, 0, 0);
  }

  /**
   * 
   * @param nomCouche nom de la couche créée
   * @param featColl collection de trajectoires à afficher
   * @param time table des temps à afficher
   * @param duree durée en seconde d'un cycle d'animation
   * @param path le chemin du fichier 3D
   * @param size le facteur de taille à plaquer
   * @param rotX rotation suivant l'axe des X
   * @param rotY rotation suivant l'axe des Y
   * @param rotZ rotation suivant l'axe des Z
   */
  public DynamicVectorLayer(String nomCouche,
      FT_FeatureCollection<IFeature> featColl, List<List<Double>> time,
      int duree, String path, double size, double rotX, double rotY, double rotZ) {
    super();

    // On vérifie si les nombres coincident
    if (featColl.size() == time.size()) {
      // On fixe le nom de la couche
      this.setLayerName(nomCouche);
      // Les trajectoires à afficher
      this.collection = featColl;
      // Les temps
      this.time = time;
      // On génère l'envelope 3D
      this.generate3DEnvelope();
      // On génère les représentations
      this.generateStyle(duree, path, size, rotX, rotY, rotZ);
      // On rafraîchit
      this.refresh();
    } else {

      System.out
          .println("ERREUR : Le nombre de tableaux de temps et d'entité ne correspondent pas");
    }

  }

  @Override
  public Box3D get3DEnvelope() {

    if (this.b == null) {

      return this.generate3DEnvelope();
    }

    return this.b;
  }

  @Override
  public void refresh() {
    this.bgLayer.removeAllChildren();

    int nbElem = this.collection.size();

    for (int i = 0; i < nbElem; i++) {
      Representation rep = this.collection.get(i).getRepresentation();

      if (rep instanceof I3DRepresentation) {
        I3DRepresentation iRep3D = (I3DRepresentation) rep;
        this.bgLayer.addChild(iRep3D.getBGRep());

      }

    }

  }

  /**
   * Génère une boite 3D englobant la temporalité (mais pas le Z des
   * coordonnées)
   * @return
   */
  public Box3D generate3DEnvelope() {

    this.tMin = Double.POSITIVE_INFINITY;
    this.tMax = Double.NEGATIVE_INFINITY;

    int nbFeat = this.collection.size();

    for (int i = 0; i < nbFeat; i++) {
      IFeature feat = this.collection.get(i);

      if (this.b == null) {

        this.b = new Box3D(feat.getGeom());
      } else {
        this.b.union(new Box3D(feat.getGeom()));
      }

      List<Double> dT = this.time.get(i);

      int nTab = dT.size();
      for (int j = 0; j < nTab; j++) {
        double d = dT.get(j);
        if (d < this.tMin) {
          this.tMin = d;

        }

        if (d > this.tMax) {
          this.tMax = d;
        }

      }

    }

    return this.b;

  }

  /**
   * Fonction permettant de faire s'écouler le temps Réfléchir à une utilisation
   * statique pour avoir différentes couches avec le même Alpha
   */
  private Alpha alpha;

  /**
   * Génère les objets Java pour une couche à l'aide de la classe de
   * représentation
   */
  public void generateStyle(int duree, double size) {

    this.generateStyle(duree, null, size, Object4d.DEFAULT_REPRESENTATION, 0,
        0, 0);

  }

  /**
   * Génère les objets Java pour une couche à l'aide de la classe de
   * représentation
   */
  public void generateStyle(int duree, String path, double size, double rotX,
      double rotY, double rotZ) {

    this.generateStyle(duree, path, size, Object4d.MODEL3D_REPRESENTATION,
        rotX, rotY, rotZ);

  }

  /**
   * Génère les objets Java pour une couche à l'aide de la classe de
   * représentation
   */
  public void generateStyle(int duree, String path, double size, boolean isCube) {

    if (isCube) {
      this.generateStyle(duree, path, size, Object4d.CUBE_REPRESENTATION, 0, 0,
          0);
    } else {
      this.generateStyle(duree, path, size, Object4d.SQUARE_REPRESENTATION, 0,
          0, 0);
    }

  }

  /**
   * Génère les objets Java3D
   * @param duree durée en seconde de la simulation
   * @param path chemin d'un fichier (si nécessaire)
   * @param size informations de taille
   * @param representationMode mode de représentation
   */
  private void generateStyle(int duree, String path, double size,
      int representationMode, double rotX, double rotY, double rotZ) {
    int nbElem = this.collection.size();
    // Alpha correspond au temps nécessaire pour faire un parcours
    this.alpha = new Alpha(-1, duree * 1000);
    for (int i = 0; i < nbElem; i++) {
      IFeature feat = this.collection.get(i);
      feat.setRepresentation(new Object4d(feat, this.time.get(i), this.tMin,
          this.tMax, this.alpha, path, size, representationMode, rotX, rotY,
          rotZ));

    }
  }

  /**
   * 
   * @param time le temps en ms où stopper l'animation
   */
  public void stop(double time) {

    this.alpha.pause((long) time);

  }

  /**
   * Permet de reprendre une animation arrêtée
   */
  public void reprendre() {
    this.alpha.resume();
  }
}

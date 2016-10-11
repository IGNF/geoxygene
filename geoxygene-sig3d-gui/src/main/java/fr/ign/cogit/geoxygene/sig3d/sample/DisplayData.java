package fr.ign.cogit.geoxygene.sig3d.sample;

import java.awt.Color;
import java.util.ArrayList;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.semantic.Map3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

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
 *          Classe de test permettant de montrer comment 1/ créer une géométrie
 *          3D 2/ Lui créer un style basique 3/ l'afficher Testclass to learn
 *          how to create and display 3D Geometry with a standard representation
 */
public class DisplayData {
  /**
   * @return un GM_Solid en forme de cube
   */
  public static GM_Solid createCube() {
    // On crée les 6 sommets du cube
    DirectPosition p1 = new DirectPosition(0, 0, 0);
    DirectPosition p2 = new DirectPosition(120, 0, 0);
    DirectPosition p3 = new DirectPosition(120, 0, 120);
    DirectPosition p4 = new DirectPosition(0, 0, 120);

    DirectPosition p5 = new DirectPosition(0.0, 120, 0.0);
    DirectPosition p6 = new DirectPosition(120, 120, 0.0);
    DirectPosition p7 = new DirectPosition(120, 120, 120);
    DirectPosition p8 = new DirectPosition(0.0, 120, 120);

    DirectPositionList LPoint1 = new DirectPositionList();
    DirectPositionList LPoint2 = new DirectPositionList();
    DirectPositionList LPoint3 = new DirectPositionList();
    DirectPositionList LPoint4 = new DirectPositionList();
    DirectPositionList LPoint5 = new DirectPositionList();
    DirectPositionList LPoint6 = new DirectPositionList();

    LPoint1.add(p1);
    LPoint1.add(p2);
    LPoint1.add(p3);
    LPoint1.add(p4);
    LPoint1.add(p1);

    GM_LineString ls = new GM_LineString(LPoint1);
    GM_OrientableSurface surf1 = new GM_Polygon(ls);

    LPoint2.add(p4);
    LPoint2.add(p3);
    LPoint2.add(p7);
    LPoint2.add(p8);
    LPoint2.add(p4);

    ls = new GM_LineString(LPoint2);
    GM_OrientableSurface surf2 = new GM_Polygon(ls);

    LPoint3.add(p3);
    LPoint3.add(p2);
    LPoint3.add(p6);
    LPoint3.add(p7);
    LPoint3.add(p3);

    ls = new GM_LineString(LPoint3);
    GM_OrientableSurface surf3 = new GM_Polygon(ls);
    LPoint4.add(p1);

    LPoint4.add(p5);
    LPoint4.add(p6);
    LPoint4.add(p2);

    LPoint4.add(p1);

    ls = new GM_LineString(LPoint4);
    GM_OrientableSurface surf4 = new GM_Polygon(ls);

    LPoint5.add(p1);
    LPoint5.add(p4);
    LPoint5.add(p8);
    LPoint5.add(p5);
    LPoint5.add(p1);

    ls = new GM_LineString(LPoint5);
    GM_OrientableSurface surf5 = new GM_Polygon(ls);

    LPoint6.add(p6);
    LPoint6.add(p5);
    LPoint6.add(p8);
    LPoint6.add(p7);
    LPoint6.add(p6);

    ls = new GM_LineString(LPoint6);
    GM_OrientableSurface surf6 = new GM_Polygon(ls);

    ArrayList<IOrientableSurface> LFace = new ArrayList<IOrientableSurface>();
    LFace.add(surf1);
    LFace.add(surf2);
    LFace.add(surf3);
    LFace.add(surf4);
    LFace.add(surf5);
    LFace.add(surf6);
    return new GM_Solid(LFace);

  }

  /**
   * Permet d'exécuter le code
   * 
   * @param args
   */
  public static void main(String[] args) {

    // On fabrique les collection d'objets à afficher
    GM_Solid cube = DisplayData.createCube();
    IFeature feat = new DefaultFeature(cube);

    // Collection prête
    FT_FeatureCollection<IFeature> featColl = new FT_FeatureCollection<IFeature>();
    featColl.add(feat);

    // On crée l'interface et on récupère l'objet Carte 3D associé
    MainWindow fenPrincipale = new MainWindow();
    Map3D carte = fenPrincipale.getInterfaceMap3D().getCurrent3DMap();

    // On utilise le constructeur permettant d'utiliser une représentation
    // standard pour chaque entité - Pas besoin d'attacher à la main une
    // représentation par entité
    VectorLayer couche = new VectorLayer(featColl,// la collection qui
        // constituera la
        // couche
        "Cube", // Le nom de la couche
        true, // Indique qu'une couleur déterminée sera appliquée
        Color.orange, // La couleur à appliquer
        1, // Le coefficient d'opacité
        true// Indique que l'on souhaite une représentation solide et
    // non filaire
    );

    // ///Manière compliquée

    /*
     * for(IFeature featTemp:featColl ){
     * 
     * featTemp.setRepresentation(new ObjectCartoon(featTemp, Color.pink)); }
     */

    // VectorLayer couche = new VectorLayer(featColl,"Cube");

    // On ajoute la couche à la carte
    carte.addLayer(couche);

  }

}

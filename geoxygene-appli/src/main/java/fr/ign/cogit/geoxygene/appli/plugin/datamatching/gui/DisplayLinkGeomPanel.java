package fr.ign.cogit.geoxygene.appli.plugin.datamatching.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.ImageIcon;

import fr.ign.cogit.geoxygene.appli.FloatingProjectFrame;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.contrib.appariement.EnsembleDeLiens;
import fr.ign.cogit.geoxygene.contrib.appariement.Lien;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.LienReseaux;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.GroupeApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.Symbolizer;

/**
 * 
 * 
 *
 */
public class DisplayLinkGeomPanel extends FloatingProjectFrame {

  /** Default serial ID. */
  private static final long serialVersionUID = 1L;

  private final EnsembleDeLiens liensReseaux;

  /**
   * Constructor.
   * @param mf
   */
  public DisplayLinkGeomPanel(MainFrame mf, EnsembleDeLiens liens) {

    super(mf, new ImageIcon(
        DisplayLinkPanel.class.getResource("/images/icons/application.png")));

    liensReseaux = liens;

    setSize(800, 400);
    setLocation(500, 400);

    this.getGui().setVisible(true);
  }

  /**
   * 
   * @param id
   */
  public void displayLink(String id) {
    try {

      System.out
          .println("----------------------------------------------------------");

      // On nettoie
      System.out.println("Nb Layer a supprimer = "
          + getLayerLegendPanel().getLayerCount());
      for (int i = 0; i < getLayerLegendPanel().getLayerCount(); i++) {
        Layer layer = getLayerLegendPanel().getLayer(i);
        System.out.println("Layer a supprimer = " + layer.getName());
        List<Layer> listLayer = new ArrayList<Layer>();
        listLayer.add(layer);
        removeLayers(listLayer);
      }

      Iterator<Lien> itLiensReseaux = liensReseaux.iterator();
      while (itLiensReseaux.hasNext()) {

        LienReseaux lienReseau = (LienReseaux) itLiensReseaux.next();
        if (Integer.toString(lienReseau.getId()).equals(id)) {

          Population<DefaultFeature> popArc = new Population<DefaultFeature>(
              false, "Lien", Lien.class, true);
          FeatureType newFeatureType = new FeatureType();
          newFeatureType.setTypeName("Lien");
          newFeatureType.setGeometryType(GM_LineString.class);
          popArc.setFeatureType(newFeatureType);
          popArc.add(lienReseau);
          Layer la = addUserLayer(popArc, "Lien", null);
          la.getSymbolizer().getStroke().setColor(new Color(240, 157, 18));
          la.getSymbolizer().getStroke().setStrokeWidth(2);

          // Arcs du reseau 1
          addLayerArc(lienReseau.getArcs1(), "Arc 1", new Color(11, 73, 157));

          // Noeuds du reseau 1
          addLayerNoeud(lienReseau.getNoeuds1(), "Noeud 1", new Color(11, 73,
              157));

          // Groupes du reseau 1
          Iterator<Groupe> itGroupes1 = lienReseau.getGroupes1().iterator();
          while (itGroupes1.hasNext()) {
            GroupeApp groupe1 = (GroupeApp) itGroupes1.next();
            addLayerArc(groupe1.getListeArcs(), "Arc 1", new Color(11, 73, 157));
            addLayerNoeud(groupe1.getListeNoeuds(), "Noeud 1", new Color(11,
                73, 157));
          }

          // Arcs du reseau 2
          addLayerArc(lienReseau.getArcs2(), "Arc 2", new Color(145, 100, 10));

          // Noeuds du reseau 2
          addLayerNoeud(lienReseau.getNoeuds2(), "Noeud 2", new Color(145, 100,
              10));

          // Groupes du reseau 2
          Iterator<Groupe> itGroupes2 = lienReseau.getGroupes2().iterator();
          while (itGroupes2.hasNext()) {
            GroupeApp groupe2 = (GroupeApp) itGroupes2.next();
            addLayerArc(groupe2.getListeArcs(), "Arc 2",
                new Color(145, 100, 10));
            addLayerNoeud(groupe2.getListeNoeuds(), "Noeud 2", new Color(145,
                100, 10));
          }

        }

      }

      this.getLayerViewPanel().getViewport().zoomToFullExtent();
      this.getInternalFrame().setSelected(true);
      System.out
          .println("----------------------------------------------------------");

    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param listArc2
   */
  private void addLayerArc(List<Arc> listArc, String title, Color color) {

    Population<DefaultFeature> popArcReseau = new Population<DefaultFeature>(
        false, title, Arc.class, true);
    FeatureType newFeatureType = new FeatureType();
    newFeatureType.setTypeName(title);
    newFeatureType.setGeometryType(GM_LineString.class);
    popArcReseau.setFeatureType(newFeatureType);
    Iterator<Arc> itCorrespondant = listArc.iterator();
    while (itCorrespondant.hasNext()) {
      Arc arc = itCorrespondant.next();
      popArcReseau.add(arc);
    }
    Layer l = addUserLayer(popArcReseau, title, null);
    l.getSymbolizer().getStroke().setColor(color);
    l.getSymbolizer().getStroke().setStrokeWidth(2);

  }

  private void addLayerNoeud(List<Noeud> listNoeud, String title, Color color) {

    Population<DefaultFeature> popNoeudReseau = new Population<DefaultFeature>(
        false, title, Noeud.class, true);
    FeatureType newFeatureType = new FeatureType();
    newFeatureType.setTypeName(title);
    newFeatureType.setGeometryType(GM_Point.class);
    popNoeudReseau.setFeatureType(newFeatureType);
    Iterator<Noeud> itNoeud = listNoeud.iterator();
    while (itNoeud.hasNext()) {
      Noeud noeud = itNoeud.next();
      popNoeudReseau.add(noeud);
    }
    Layer l = addUserLayer(popNoeudReseau, title, null);

    PointSymbolizer pointSymbolizer = (PointSymbolizer) l.getStyles().get(0)
        .getSymbolizer();
    Mark mark = pointSymbolizer.getGraphic().getMarks().get(0);
    mark.getFill().setColor(color);
    mark.getFill().setFillOpacity(1.0f);
    mark.getStroke().setColor(color);
    mark.getStroke().setStrokeOpacity(0.8f);
    mark.getStroke().setStrokeWidth(2);
    mark.setWellKnownName("circle");
    pointSymbolizer.setUnitOfMeasure(Symbolizer.METRE);
    pointSymbolizer.getGraphic().setSize(6);

  }

}

/*
 * for (Lien lien : liens) { // System.out.println("Id = " + lien.getId() + ", "
 * + id); if (Integer.toString(lien.getId()).equals(id)) {
 * 
 * Population<DefaultFeature> popArc = new Population<DefaultFeature>(false,
 * "Lien", Lien.class, true); FeatureType newFeatureType = new FeatureType();
 * newFeatureType.setTypeName("Lien");
 * newFeatureType.setGeometryType(GM_LineString.class);
 * popArc.setFeatureType(newFeatureType); popArc.add(lien); Layer la =
 * addUserLayer(popArc, "Lien", null);
 * la.getSymbolizer().getStroke().setColor(new Color(240, 157, 18));
 * la.getSymbolizer().getStroke().setStrokeWidth(2);
 * 
 * // Afficher les objets du reseau 1 Population<DefaultFeature> popReseau1 =
 * new Population<DefaultFeature>(false, "Reseau 1", Arc.class, true);
 * FeatureType newFeatureType2 = new FeatureType();
 * newFeatureType2.setTypeName("Reseau 1");
 * newFeatureType2.setGeometryType(GM_LineString.class);
 * popReseau1.setFeatureType(newFeatureType2); List<IFeature> objRef =
 * lien.getObjetsRef(); System.out.println("Nb liens correspondant = " +
 * objRef.size()); Iterator itCorrespondant = popReseau1.iterator(); while
 * (itCorrespondant.hasNext()) { Arc arc = (Arc) itCorrespondant.next();
 * System.out.println("-" + arc.getGeom()); popReseau1.add(arc); } Layer l1 =
 * addUserLayer(popReseau1, "Correspondant", null);
 * l1.getSymbolizer().getStroke().setColor(new Color(0, 0, 80));
 * l1.getSymbolizer().getStroke().setStrokeWidth(2);
 * 
 * // Afficher l'arc comp
 * 
 * // Afficher la carte topo 1 et 2 (arc + noeud) } }
 */

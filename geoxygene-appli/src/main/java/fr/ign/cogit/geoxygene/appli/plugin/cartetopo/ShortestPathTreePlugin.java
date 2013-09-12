package fr.ign.cogit.geoxygene.appli.plugin.cartetopo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ArcApp;
import fr.ign.cogit.geoxygene.contrib.appariement.reseaux.topologie.ReseauApp;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Groupe;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

public class ShortestPathTreePlugin implements GeOxygeneApplicationPlugin, ActionListener {

    /** GeOxygeneApplication. */
    private GeOxygeneApplication application;

    Noeud root;

    /** On crée une carte topologique. */
    CarteTopo ct2 = new CarteTopo("Carte topologique test avec 8 noeuds");

    /**
     * Initialize the plugin.
     * @param application the application
     */
    @Override
    public final void initialize(final GeOxygeneApplication application) {

        this.application = application;

        // Check if the DataMatching menu exists. If not we create it.
        JMenu menu = null;
        String menuName = I18N.getString("CarteTopoPlugin.CarteTopoPlugin"); //$NON-NLS-1$
        for (Component c : application.getFrame().getJMenuBar().getComponents()) {
            if (c instanceof JMenu) {
                JMenu aMenu = (JMenu) c;
                if (aMenu.getText() != null && aMenu.getText().equalsIgnoreCase(menuName)) {
                    menu = aMenu;
                }
            }
        }
        if (menu == null) {
            menu = new JMenu(menuName);
        }

        // Add network data matching menu item to the menu.
        JMenuItem menuItem = new JMenuItem("Plus court chemin");
        menuItem.addActionListener(this);
        menu.add(menuItem);

        // Refresh menu of the application
        application.getFrame().getJMenuBar().add(menu, application.getFrame().getJMenuBar().getComponentCount() - 2);

    }

    private void initCarteTopo1() {
        
        Noeud nA = new Noeud();
        Noeud nB = new Noeud();
        Noeud nC = new Noeud();
        Noeud nD = new Noeud();
        Noeud nE = new Noeud();
        Noeud nF = new Noeud();
        Noeud nG = new Noeud();
        Noeud nH = new Noeud();

        // On ajoute à la carte ct des noeuds et des arcs

        nA.setCoord(new DirectPosition(0., 30.));
        ct2.addNoeud(nA);
        nB.setCoord(new DirectPosition(20., 30.));
        ct2.addNoeud(nB);
        nC.setCoord(new DirectPosition(40., 30.));
        ct2.addNoeud(nC);
        nD.setCoord(new DirectPosition(20., 20.));
        ct2.addNoeud(nD);
        nE.setCoord(new DirectPosition(10., 10.));
        ct2.addNoeud(nE);
        nF.setCoord(new DirectPosition(30., 10.));
        ct2.addNoeud(nF);
        nG.setCoord(new DirectPosition(0., 0.));
        ct2.addNoeud(nG);
        nH.setCoord(new DirectPosition(40., 0.));
        ct2.addNoeud(nH);

        Arc a1 = new Arc(nA, nB);
        a1.setPoids(4);
        ct2.addArc(a1);

        Arc a2 = new Arc(nB, nC);
        a2.setPoids(3);
        ct2.addArc(a2);

        Arc a3 = new Arc(nC, nH);
        a3.setPoids(4);
        ct2.addArc(a3);

        Arc a4 = new Arc(nA, nG);
        a4.setPoids(5);
        ct2.addArc(a4);

        Arc a5 = new Arc(nG, nH);
        a5.setPoids(1);
        ct2.addArc(a5);

        Arc a6 = new Arc(nB, nD);
        a6.setPoids(2);
        ct2.addArc(a6);

        Arc a7 = new Arc(nG, nE);
        a7.setPoids(2);
        ct2.addArc(a7);

        Arc a8 = new Arc(nH, nF);
        a8.setPoids(2);
        ct2.addArc(a8);

        Arc a9 = new Arc(nE, nF);
        a9.setPoids(1);
        ct2.addArc(a9);

        Arc a10 = new Arc(nD, nE);
        a10.setPoids(1);
        ct2.addArc(a10);

        Arc a11 = new Arc(nD, nF);
        a11.setPoids(1);
        ct2.addArc(a11);

        // Calcul de la topologie arc/noeuds (relations noeud initial/noeud
        // final
        // pour chaque arete) a l'aide de la géometrie.
        ct2.creeTopologieArcsNoeuds(0.1);
        
        root = nA;

    }
    
    private void initCarteTopo2() {
        
        // Réseau final muni d'une topologie réseau
        ReseauApp reseau = new ReseauApp("Réseau muni d'une topologie réseau");
        IPopulation<? extends IFeature> popArc = reseau.getPopArcs();
        
        IFeatureCollection<? extends IFeature> populationsArcs = ShapefileReader.read("D:\\Data\\Appariement\\ESPON_DB\\Test02\\Reseau2.shp");
        for (IFeature element : populationsArcs) {
            ArcApp arc = (ArcApp) popArc.nouvelElement();
            ILineString ligne = new GM_LineString((IDirectPositionList) element.getGeom().coord().clone());
            arc.setGeometrie(ligne);
            arc.setOrientation(2);
            arc.addCorrespondant(element);
        }
        
        double tolerance = 0.1;
        double seuilFusion = 0.1;
        reseau.creeNoeudsManquants(tolerance);
        reseau.filtreDoublons(tolerance);
        reseau.creeTopologieArcsNoeuds(tolerance);
        reseau.filtreArcsDoublons();
        
        reseau.rendPlanaire(tolerance);
        reseau.filtreDoublons(tolerance);
        
        reseau.fusionNoeuds(seuilFusion);
        
        reseau.filtreNoeudsIsoles();
        
        reseau.filtreNoeudsSimples();
        
        reseau.filtreArcsDoublons();
        
        ct2 = reseau;
        root = ct2.getListeNoeuds().get(50);
    }

    /**
     * 
     */
    @Override
    public void actionPerformed(final ActionEvent e) {

        initCarteTopo2();

        try {

            Dimension desktopSize = this.application.getFrame().getSize();
            int widthProjectFrame = desktopSize.width / 2;
            int heightProjectFrame = desktopSize.height / 2;

            // Graph Frame
            ProjectFrame p1 = this.application.getFrame().newProjectFrame();
            // p1.setMaximum(true);
            p1.setLocation(0, 0);
            p1.setSize(widthProjectFrame, heightProjectFrame);
            p1.setTitle("A weighted graph");

            Layer lp = p1.addUserLayer(ct2.getPopNoeuds(), "Noeuds", null);
            // lp.getSymbolizer().getStroke().setColor(new Color(255, 216, 0));
            // lp.getSymbolizer().getStroke().setStrokeWidth(1);

            Layer la = p1.addUserLayer(ct2.getPopArcs(), "Arcs", null);
            la.getSymbolizer().getStroke().setColor(new Color(240, 157, 18));
            la.getSymbolizer().getStroke().setStrokeWidth(2);

            Viewport viewport = p1.getLayerViewPanel().getViewport();

            // Shortest path tree Frame
            ProjectFrame p2 = this.application.getFrame().newProjectFrame();
            p2.setSize(widthProjectFrame, heightProjectFrame);
            p2.setLocation(widthProjectFrame, 0);
            p2.getLayerViewPanel().setViewport(viewport);
            p2.setTitle("Shortest Path Tree");
            viewport.getLayerViewPanels().add(p2.getLayerViewPanel());

            p2.addUserLayer(ct2.getPopNoeuds(), "Noeuds", null);

            Layer lppc = p2.addUserLayer(getShortestPathTree(root, 10), "Arcs", null);
            lppc.getSymbolizer().getStroke().setColor(new Color(239, 59, 44));
            lppc.getSymbolizer().getStroke().setStrokeWidth(2);

        } catch (Exception ex) {
        }

    }

    /**
     * 
     * @param root
     * @return
     */
    private Population<Arc> getShortestPathTree(Noeud root, double maxlongueur) {

        // On prépare le retour
        Population<Arc> popSPTree = new Population<Arc>(false, "popSPTree", Arc.class, true);
        FeatureType newFeatureType = new FeatureType();
        newFeatureType.setTypeName("Shortest path tree");
        newFeatureType.setGeometryType(GM_LineString.class);
        SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
        schemaDefaultFeature.setNom("spTree");
        schemaDefaultFeature.setNomSchema("spTree");
        schemaDefaultFeature.setFeatureType(newFeatureType);
        popSPTree.setFeatureType(newFeatureType);

        System.out.println("Nb noeuds = " + ct2.getListeNoeuds().size());
        
        // On parcourt tous les noeuds
        // int cpt = 0;
        for (Noeud noeud : ct2.getListeNoeuds()) {
            if (!noeud.equals(root)) {
                // System.out.print("*");cpt++;
                // if ((cpt%100) == 0) {System.out.println("");}
                Groupe gpResult = root.plusCourtChemin(noeud, maxlongueur);
                if (gpResult != null) {
                    List<Arc> listArc = gpResult.getListeArcs();
                    for (int i = 0; i < listArc.size(); i++) {
                        Arc arc = listArc.get(i);
                        popSPTree.add(arc);
                    }
                }
            }
        }

        // return shortest path tree population
        return popSPTree;
    }

}

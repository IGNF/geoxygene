package fr.ign.cogit.geoxygene.appli.plugin.cartetopo;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.LineSymbolizer;
import fr.ign.cogit.geoxygene.style.Mark;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
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
        JMenuItem menuItem = new JMenuItem("Shortest path tree");
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
        
        IFeatureCollection<? extends IFeature> populationsArcs = ShapefileReader.read("D:\\DATA\\Appariement\\MesTests\\T3\\bdtopo_route.shp");
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
            int heightProjectFrame = desktopSize.height;

            // Graph Frame
            ProjectFrame p1 = this.application.getFrame().newProjectFrame();
            // p1.setMaximum(true);
            p1.setLocation(0, 0);
            p1.setSize(widthProjectFrame, heightProjectFrame - 200);
            p1.setTitle("A weighted graph");
            
            Layer la = p1.addUserLayer(ct2.getPopArcs(), "Arcs", null);
            // la.getSymbolizer().getStroke().setColor(new Color(240, 157, 18));
            // la.getSymbolizer().getStroke().setStrokeWidth(2);
            
            LineSymbolizer lineSymbolizer = (LineSymbolizer) la.getStyles().get(0).getSymbolizer();
            lineSymbolizer.getStroke().setColor(new Color(106, 81, 163));
            lineSymbolizer.getStroke().setStrokeOpacity(1.0f);
            lineSymbolizer.getStroke().setStrokeWidth((float) 2);
            lineSymbolizer.setUnitOfMeasure(Symbolizer.METRE);

            StyledLayerDescriptor sld = StyledLayerDescriptor
                    .unmarshall(StyledLayerDescriptor.class.getClassLoader().getResourceAsStream("sld/BasicStyles.xml"));
            la.getStyles().add(sld.getLayer("Basic Line").getStyles().get(0));
            LineSymbolizer lineSymbolizer2 = (LineSymbolizer) la.getStyles().get(1).getSymbolizer();
            
            lineSymbolizer2.getStroke().setColor(new Color(255, 255, 255));
            lineSymbolizer2.getStroke().setStrokeOpacity(1.0f);
            lineSymbolizer2.getStroke().setStrokeWidth((float)0.5);
            lineSymbolizer2.setUnitOfMeasure(Symbolizer.METRE);
            
            Layer lp = p1.addUserLayer(ct2.getPopNoeuds(), "Point", null);
            PointSymbolizer pointSymbolizer = (PointSymbolizer) lp.getStyles().get(0).getSymbolizer();
            Mark mark = pointSymbolizer.getGraphic().getMarks().get(0);
            mark.getFill().setColor(new Color(67, 144, 193));
            mark.getFill().setFillOpacity(1.0f);
            mark.getStroke().setColor(new Color(35, 140, 69));
            mark.getStroke().setStrokeOpacity(0.8f);
            mark.getStroke().setStrokeWidth((float) 2);
            mark.setWellKnownName("circle");
            pointSymbolizer.setUnitOfMeasure(Symbolizer.METRE);
            pointSymbolizer.getGraphic().setSize(6);
            
            Viewport viewport = p1.getLayerViewPanel().getViewport();
            
            // ----------------------------------------------------------------------------------------
            
            // Shortest path tree Frame
            ProjectFrame p2 = this.application.getFrame().newProjectFrame();
            p2.setSize(widthProjectFrame - 50, heightProjectFrame - 200);
            p2.setLocation(widthProjectFrame, 0);
            p2.getLayerViewPanel().setViewport(viewport);
            p2.setTitle("Shortest Path Tree");
            viewport.getLayerViewPanels().add(p2.getLayerViewPanel());

            Population<DefaultFeature> popArc = getShortestPathTree(root, 10);
            Layer lppc = p2.addUserLayer(popArc, "Arcs", null);
            lppc.getSymbolizer().getStroke().setColor(new Color(0, 0, 0));
            lppc.getSymbolizer().getStroke().setStrokeWidth(1);
            
            StyledLayerDescriptor sldSPT = StyledLayerDescriptor
                    .unmarshall(StyledLayerDescriptor.class.getClassLoader().getResourceAsStream("sld/shortestpathtree.xml"));
            lppc.setStyles(sldSPT.getLayer("troncon").getStyles());
            application.getFrame().getSelectedProjectFrame().getLayerViewPanel().repaint();
            
            Population<Noeud> popRoot = new Population<Noeud>("root");
            popRoot.add(root);
            System.out.println("-------------------------------------------------------------------");
            Layer lr = p2.addUserLayer(popRoot, "Root", null);
            System.out.println("-------------------------------------------------------------------");
            /*pointSymbolizer = (PointSymbolizer) lr.getStyles().get(0).getSymbolizer();
            mark = pointSymbolizer.getGraphic().getMarks().get(0);
            mark.getFill().setColor(new Color(67, 144, 193));
            mark.getFill().setFillOpacity(1.0f);
            mark.getStroke().setColor(new Color(35, 140, 69));
            mark.getStroke().setStrokeOpacity(0.8f);
            mark.getStroke().setStrokeWidth((float) 2);
            mark.setWellKnownName("star");
            pointSymbolizer.setUnitOfMeasure(Symbolizer.METRE);
            pointSymbolizer.getGraphic().setSize(6);*/
            

        } catch (Exception ex) {
        }

    }

    /**
     * 
     * @param root
     * @return
     */
    private Population<DefaultFeature> getShortestPathTree(Noeud root, double maxlongueur) {

        // On prépare le retour
        Population<DefaultFeature> popSPTree = new Population<DefaultFeature>(false, "popSPTree", Arc.class, true);
        FeatureType newFeatureType = new FeatureType();
        newFeatureType.setTypeName("Shortest path tree");
        newFeatureType.setGeometryType(GM_LineString.class);
        
        AttributeType nbPassage = new AttributeType("nb", "integer");
        newFeatureType.addFeatureAttribute(nbPassage);
        
        SchemaDefaultFeature schemaDefaultFeature = new SchemaDefaultFeature();
        schemaDefaultFeature.setNom("spTree");
        schemaDefaultFeature.setNomSchema("spTree");
        schemaDefaultFeature.setFeatureType(newFeatureType);
        popSPTree.setFeatureType(newFeatureType);
        
        Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
        attLookup.put(new Integer(0), new String[] { nbPassage.getNomField(), nbPassage.getMemberName() });
        schemaDefaultFeature.setAttLookup(attLookup);

        System.out.println("Nb noeuds = " + ct2.getListeNoeuds().size());
        
        // On parcourt tous les noeuds
        // int cpt = 0;
        for (Noeud noeud : ct2.getListeNoeuds()) {
            if (!noeud.equals(root)) {
                Groupe gpResult = root.plusCourtChemin(noeud, maxlongueur);
                if (gpResult != null) {
                    List<Arc> listArc = gpResult.getListeArcs();
                    for (int i = 0; i < listArc.size(); i++) {
                    	DefaultFeature arc = listArc.get(i);
                        arc.setSchema(schemaDefaultFeature);
                        
                        int nb = 0;
                        
                        // on cherche l'arc 
                        boolean trouve = false;
                        for (DefaultFeature feat : popSPTree.getElements()) {
                        	if (feat.equals(arc)) {
                        		trouve = true;
                        		nb = (Integer) feat.getAttribute("nb");
                        	}
                        }
                        nb++;
                        
                        Object[] attributes = new Object[] { nb };
                        arc.setAttributes(attributes);
                        popSPTree.add(arc);
                    }
                }
            }
        }

        // return shortest path tree population
        return popSPTree;
    }

}

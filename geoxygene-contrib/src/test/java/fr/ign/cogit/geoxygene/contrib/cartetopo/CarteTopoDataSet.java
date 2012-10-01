package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * carte topo dataset. <br/>
 * 
 * 
 */
public class CarteTopoDataSet {

    // Logger
    private static Logger logger = Logger.getLogger(CarteTopoDataSet.class);

    // La carte topologique 1
    protected CarteTopo ct1 = null;

    /**
     * Carte : 4 noeuds + 5 arcs
     */
    protected void initCarteTopo1() {
        logger.info("Set carte topo");

        // On crée une carte topologique
        ct1 = new CarteTopo("Carte topologique test avec 4 noeuds");

        // On ajoute à la carte ct des noeuds et des arcs
        Noeud n1 = new Noeud();
        n1.setCoord(new DirectPosition(0., 0., 0.));
        ct1.addNoeud(n1);

        Noeud n2 = new Noeud();
        n2.setCoord(new DirectPosition(3., 1., 0.));
        ct1.addNoeud(n2);

        Noeud n3 = new Noeud();
        n3.setCoord(new DirectPosition(1., 1., 0.));
        ct1.addNoeud(n3);

        Noeud n4 = new Noeud();
        n4.setCoord(new DirectPosition(1., -1., 0.));
        ct1.addNoeud(n4);

        Arc a1 = new Arc();
        DirectPositionList dpl1 = new DirectPositionList();
        dpl1.add(new DirectPosition(1., -1., 0.));
        dpl1.add(new DirectPosition(1., 1., 0.));
        a1.setCoord(dpl1);
        ct1.addArc(a1);

        Arc a2 = new Arc();
        DirectPositionList dpl2 = new DirectPositionList();
        dpl2.add(new DirectPosition(0., 0., 0.));
        dpl2.add(new DirectPosition(1., 1., 0.));
        a2.setCoord(dpl2);
        ct1.addArc(a2);

        Arc a3 = new Arc();
        DirectPositionList dpl3 = new DirectPositionList();
        dpl3.add(new DirectPosition(1., 1., 0.));
        dpl3.add(new DirectPosition(3., 1., 0.));
        a3.setCoord(dpl3);
        ct1.addArc(a3);

        Arc a4 = new Arc();
        DirectPositionList dpl4 = new DirectPositionList();
        dpl4.add(new DirectPosition(3., 1., 0.));
        dpl4.add(new DirectPosition(1., -1., 0.));
        a4.setCoord(dpl4);
        ct1.addArc(a4);

        Arc a5 = new Arc();
        DirectPositionList dpl5 = new DirectPositionList();
        dpl5.add(new DirectPosition(1., -1., 0.));
        dpl5.add(new DirectPosition(0., 0., 0.));
        a5.setCoord(dpl5);
        ct1.addArc(a5);

        // Calcul de la topologie arc/noeuds (relations noeud initial/noeud
        // final
        // pour chaque arete) a l'aide de la géometrie.
        ct1.creeTopologieArcsNoeuds(0.1);

        // Calcul de la topologie de carte topologique (relations face gauche /
        // face droite pour chaque arete) avec les faces définies comme des
        // cycles du graphe.
        ct1.creeTopologieFaces();

        // Affichage du nombre de faces
        logger.info("Nombre de faces de la carte : "
                + ct1.getListeFaces().size());

        // Affichage des coordonnees du noeud initial et du noeud final du
        // premier
        // arc
        Arc arc = (Arc) ct1.getListeArcs().get(0);
        logger.info("Noeud initial de a0 : " + arc.getNoeudIni().getCoord());
        logger.info("Noeud final de a0   : " + arc.getNoeudFin().getCoord());

        // Calcul de la superficie des deux faces
        Face face = (Face) ct1.getListeFaces().get(0);
        logger.info("Superficie de f0 : " + face.getGeom().area());
        face = (Face) ct1.getListeFaces().get(1);
        logger.info("Superficie de f1 : " + face.getGeom().area());
    }

}

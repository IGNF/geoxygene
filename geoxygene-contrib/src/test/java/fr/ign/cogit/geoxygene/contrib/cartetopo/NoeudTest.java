package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

/**
 * Tests unitaires de la classe Noeud. <br/>
 * 
 * 
 */
public class NoeudTest {

    // Logger
    private static Logger logger = LogManager.getLogger(NoeudTest.class);
    
 // La carte topologique 1
    protected CarteTopo ct1 = null;

    @Before
    public void setUp() throws Exception {
    }

    /**
     * Carte construite à la main : 4 noeuds + 5 arcs.
     * 
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
      logger.info("Nombre de faces de la carte : " + ct1.getListeFaces().size());

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
    
    /**
     * Test du plus court chemin dans une carte topologique. <br/>
     * Jeux de test utilisés :
     * <ul>
     * <li>Carte topologique test n°1 : 4 noeuds + 5 arcs.</li>
     * </ul>
     */
    @Test
    public void testPlusCourtChemin() {

        logger.info("Début du test plus court chemin");

        // Initialisation de la carte topo 1 pour les tests.
        initCarteTopo1();

        // On récupère les objets
        Noeud n1 = ct1.getListeNoeuds().get(0);
        Noeud n2 = ct1.getListeNoeuds().get(1);
        Noeud n3 = ct1.getListeNoeuds().get(2);
        Noeud n4 = ct1.getListeNoeuds().get(3);
        // Arc a1 = ct1.getListeArcs().get(0);
        Arc a2 = ct1.getListeArcs().get(1);
        Arc a3 = ct1.getListeArcs().get(2);
        Arc a4 = ct1.getListeArcs().get(3);
        Arc a5 = ct1.getListeArcs().get(4);

        // Groupe final
        Groupe gpFinal = null;
        // Groupe resultat
        Groupe gpResult = null;

        // ====================================================================
        logger.info("Cas n°1 : chemin n1-n2");

        // on calcule le plus court chenmin
        gpResult = n1.plusCourtChemin(n2, 10);

        // On construit le résultat pour comparer avec le calcul
        gpFinal = new Groupe();
        gpFinal.addNoeud(n1);
        gpFinal.addNoeud(n4);
        gpFinal.addNoeud(n2);
        gpFinal.addArc(a5);
        gpFinal.addArc(a4);

        // On compare le résultat
        Assert.assertEquals("Comparaison de la liste des noeuds cas 1 : ",
                gpFinal.getListeNoeuds(), gpResult.getListeNoeuds());
        Assert.assertEquals("Comparaison de la liste des arcs cas 1 : ",
                gpFinal.getListeArcs(), gpResult.getListeArcs());

        // ================================================================
        logger.info("Cas n°2 : chemin n2-n1");

        // On vide les groupes
        gpResult.vide();
        gpFinal.vide();

        // on calcule le plus court chenmin
        gpResult = n2.plusCourtChemin(n1, 10);

        // On construit le résultat pour comparer avec le calcul
        gpFinal = new Groupe();
        gpFinal.addNoeud(n2);
        gpFinal.addNoeud(n3);
        gpFinal.addNoeud(n1);
        gpFinal.addArc(a3);
        gpFinal.addArc(a2);

        // On compare le résultat
        Assert.assertEquals("Comparaison de la liste des noeuds cas 2 : ",
                gpFinal.getListeNoeuds(), gpResult.getListeNoeuds());
        Assert.assertEquals("Comparaison de la liste des arcs cas 2 : ",
                gpFinal.getListeArcs(), gpResult.getListeArcs());

        // ================================================================
        logger.info("Cas n°3 : chemin n1-n1");

        // On vide les groupes
        gpResult.vide();
        gpFinal.vide();

        // on calcule le plus court chenmin
        gpResult = n1.plusCourtChemin(n1, 10);

        // On construit le résultat pour comparer avec le calcul
        gpFinal = new Groupe();
        gpFinal.addNoeud(n1);

        // On compare le résultat
        Assert.assertEquals("Comparaison de la liste des noeuds cas 3 : ",
                gpFinal.getListeNoeuds(), gpResult.getListeNoeuds());
        Assert.assertEquals("Comparaison de la liste des arcs cas 3 : ",
                gpFinal.getListeArcs(), gpResult.getListeArcs());

        // ================================================================
        logger.info("Cas n°4.1 : chemin n4-n1 (bonne comparaison)");

        // On vide les groupes
        gpResult.vide();
        gpFinal.vide();

        // on calcule le plus court chenmin
        gpResult = n4.plusCourtChemin(n1, 10);

        // On construit le bon résultat pour comparer avec le calcul
        gpFinal = new Groupe();
        gpFinal.addNoeud(n4);
        gpFinal.addNoeud(n1);
        gpFinal.addArc(a5);

        // On compare le résultat
        Assert.assertEquals("Comparaison de la liste des noeuds cas 4.1 : ",
                gpFinal.getListeNoeuds(), gpResult.getListeNoeuds());
        Assert.assertEquals("Comparaison de la liste des arcs cas 4.1 : ",
                gpFinal.getListeArcs(), gpResult.getListeArcs());

        Assert.assertTrue("",
                gpFinal.getListeNoeuds().equals(gpResult.getListeNoeuds()));
        Assert.assertTrue("",
                gpFinal.getListeArcs().equals(gpResult.getListeArcs()));

        // On inverse les noeuds pour vérifier que ce n'est pas le bon résultat
        logger.info("Cas n°4.1 : chemin n4-n1 (mauvaise comparaison : inversion des noeuds)");

        // On vide le groupe construit avec le bon resultat
        gpFinal.vide();

        // On construit le mauvais résultat pour comparer avec le calcul
        gpFinal = new Groupe();
        gpFinal.addNoeud(n1);
        gpFinal.addNoeud(n4);

        // On compare le résultat
        Assert.assertFalse(
                "Comparaison de la liste des noeuds en sens inverse cas 4.2 : ",
                gpFinal.getListeNoeuds().equals(gpResult.getListeNoeuds()));
        
    }

}

package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitaires de la classe Noeud. <br/>
 * 
 * 
 */
public class NoeudTest extends CarteTopoDataSet {

    // Logger
    private static Logger logger = Logger.getLogger(NoeudTest.class);

    @Before
    public void setUp() throws Exception {
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

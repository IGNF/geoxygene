package fr.ign.cogit.geoxygene.contrib.cartetopo;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 * 
 * @author MDVan-Damme
 * 
 */
public class NoeudTest {

    // Logger
    private static Logger logger = Logger.getLogger(NoeudTest.class);

    @Test
    public void testPlusCourtChemin() {

        int compare = 1;

        // Reseau initial

        // Groupe resultat
        Groupe gpResult = null;

        // Groupe final
        Groupe gpFinal = null;

        // On compare les 2 groupes obtenus
        // La longueur
        BeanComparator beanComparator = new BeanComparator("length");
        compare = beanComparator.compare(gpFinal, gpResult);
        Assert.assertEquals("Comparaison de la longueur : ", compare, 0);
        // La liste des noeuds
        // beanComparator = new BeanComparator("listeNoeuds");
        // compare = beanComparator.compare(gpFinal, gpResult);
        // Assert.assertEquals("Comparaison de la liste des noeuds : ", compare,
        // 0);
        // La liste des arcs

        // La liste des faces

    }

}

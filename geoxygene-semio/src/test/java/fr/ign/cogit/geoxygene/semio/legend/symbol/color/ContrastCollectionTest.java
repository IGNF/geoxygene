package fr.ign.cogit.geoxygene.semio.legend.symbol.color;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * ContrastCollection unit tests.
 * 
 */
public class ContrastCollectionTest {
	
  private Logger logger = Logger.getLogger(ContrastCollectionTest.class);
  
  @Before
  public void setUp() throws Exception {
  }
    
  /**
   * Compare quelques indices de la matrice.
   */
  @Test
  public final void testMatrixValues() {
	
    // On charge la matrice de contraste
    ContrastCollection contrastCollection = ContrastCollection
					.unmarshall(ContrastCollectionTest.class.getResource("/symbol/color/Contrast.xml").getPath());
	
    Assert.assertEquals("Le nom de la matrice ne correspond pas.", contrastCollection.getName(), "Matrice de contraste");
    Assert.assertEquals("Nombre de contraste ne correspond pas à 24336.", contrastCollection.getNbContrasts(), 24336);

    // -------------------------------------------------------------------------------------------------------------------------------------------------
    // Set n°3 
    logger.info("Test des indices du set n°3.");
    Contrast contrast2 = contrastCollection.getContrasts().get(2);
    Assert.assertEquals("C1 de l'indice de clarté n°3 est différent de 1.", contrast2.getIdC1(), 1);
    Assert.assertEquals("C2 de l'indice de clarté n°3 est différent de 3.", contrast2.getIdC2(), 3);
    Assert.assertEquals("L'indice de clarté n°3 (c1 = 1, c2 = 3) est différent de 1.16.", contrast2.getContrasteClarte(), 1.16, 0);
    Assert.assertEquals("L'indice de teinte n°3 (c1 = 1, c2 = 3) est différent de 0.", contrast2.getContrasteTeinte(), 0, 0);
    Assert.assertEquals("L'indice de qualité de clarté n°3 (c1 = 1, c2 = 3) est différent de -1.", contrast2.getQualiteContrasteClarte(), -1.0, 0);
    Assert.assertEquals("L'indice de qualité de teinte n°3 (c1 = 1, c2 = 3) est différent de -1.", contrast2.getQualiteContrasteTeinte(), -1.0, 0);

	// -------------------------------------------------------------------------------------------------------------------------------------------------
    // 34-125
    logger.info("Test des indices entre la couleur 34 et la couleur 125");
    ColorimetricColor c1 = new ColorimetricColor(34);
	ColorimetricColor c2 = new ColorimetricColor(125);
	Assert.assertEquals("La clé pour la couleur 34 ne correspond pas à B6.", c1.getCleCoul(), "B6");
	Assert.assertEquals("La clé pour la couleur 125 ne correspond pas à RG3.", c2.getCleCoul(), "RG3");
	
	ContrastCollection.getCOGITContrast(c1,c2);
		
	Assert.assertEquals("L'indice de clarté entre (c1 = B6, c2 = RG3) est différent de 1.88.", ContrastCollection.getCOGITContrast(c1,c2).getContrasteClarte(), 1.88, 0);
	Assert.assertEquals("L'indice de teinte entre (c1 = B6, c2 = RG3) est différent de 3.12.", ContrastCollection.getCOGITContrast(c1,c2).getContrasteTeinte(), 3.12, 0);
	
  }
    
    
}

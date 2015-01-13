package fr.ign.cogit.geoxygene.util.string;

import org.junit.Test;

public class MesureRessemblanceToponymeSamalTest {
  
  @Test
  public void testToponyme() {
    System.out.println("=============== 1 ===================");
    System.out.println("('Borde', 'la borde') : " + MesureRessemblanceToponymeSamal.mesureRessemblanceToponymeSamal("Borde", "la borde"));
    System.out.println("('Brosses', 'croix des brosses') : " + MesureRessemblanceToponymeSamal.mesureRessemblanceToponymeSamal("Brosses", "croix des brosses"));
    System.out.println("('Chaume-du-Roi', 'la chaume du roi') : " + MesureRessemblanceToponymeSamal.mesureRessemblanceToponymeSamal("Chaume-du-Roi", "la chaume du roi"));
    System.out.println("('Maison-Rouge', 'maison rouge') : " + MesureRessemblanceToponymeSamal.mesureRessemblanceToponymeSamal("Maison-Rouge", "maison rouge"));
    System.out.println("('Saint-Roch', 'croix saint-roch') : " + MesureRessemblanceToponymeSamal.mesureRessemblanceToponymeSamal("Saint-Roch", "croix saint-roch"));
    System.out.println("('Ramisse', 'pont de la ramisse') : " + MesureRessemblanceToponymeSamal.mesureRessemblanceToponymeSamal("Ramisse", "pont de la ramisse"));
    System.out.println("('Pont-de-Colonne', 'pont de colomne') : " + MesureRessemblanceToponymeSamal.mesureRessemblanceToponymeSamal("Pont-de-Colonne", "pont de colomne"));
  }



}

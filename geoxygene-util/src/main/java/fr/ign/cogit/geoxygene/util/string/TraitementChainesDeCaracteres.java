package fr.ign.cogit.geoxygene.util.string;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;



/**
 * Cette classe contient d'une part une Méthode qui permet d'éliminer les
 * ponctuations d'une chaîne de caractère et les mots contenant maximum 2
 * caractères. Afin d'éliminer les ponctuations on cherche la position du
 * premier signe de ponctuation, on extraie une sous-chaîne à partir de 0
 * jusqu'à la position donnée et une deuxième sous-chaîne à partir de la
 * position donnée jusqu'a la fin de la chaîne de caractère. Et d'autre part,
 * elle contient une Méthode qui permet de compter les tokens dans une chaîne de
 * caractère.
 * @author Ana-Maria Raimond
 */
public class TraitementChainesDeCaracteres {
  /**
   * la Méthode ignorePunctuation elimine les ponctuations d'une chaîne de
   * caractères
   * @param chaine
   * @return
   */
  public static String ignorePunctuation(String chaine) {
    String punctuations = ".,;!#$/:?'()[]_-&{}", chaineStrippee = chaine, chaineStrippee1 = "", chaine1, chaine2;
    StringTokenizer st;
    char caracter;
    int indice;

    for (int i = 0; i < punctuations.length(); i++) {
      caracter = punctuations.charAt(i);
      // eliminer les ponctuations
      while (chaineStrippee.indexOf(caracter) != -1) {
        indice = chaineStrippee.indexOf(caracter);
        chaine1 = chaineStrippee.substring(0, indice);
        chaine2 = chaineStrippee.substring(indice + 1, chaineStrippee.length());
        chaineStrippee = chaine1 + chaine2;
      }
    }
    st = new StringTokenizer(chaineStrippee);
    // Méthode qui va éliminer les mots contenant maximum 2 caractères
    while (st.hasMoreTokens()) {
      String mot = st.nextToken();
      if (mot.length() > 2) {
        chaineStrippee1 = chaineStrippee1.concat(mot + " ");
      }
    }
    return chaineStrippee1;
  }

  /**
   * @param chaineCaractere
   * @return
   */
  public static int compteToken(String chaineCaractere) {
    int nbToken = 0;
    StringTokenizer st = new StringTokenizer(chaineCaractere);
    while (st.hasMoreTokens()) {
      st.nextToken();
      nbToken++;
    }
    return nbToken;
  }

  
}

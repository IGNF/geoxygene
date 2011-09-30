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

  @SuppressWarnings("unused")
  public static double mesureRessemblanceToponymeSamal(String string1,
      String string2) {
    List<String> tokenLigne = new ArrayList<String>();
    List<String> tokenColonne = new ArrayList<String>();
    ApproximateMatcher matcher = new ApproximateMatcher();
    String toponyme, oronyme;
    MatriceConfiance matriceToken;
    StringTokenizer st1, st2;
    matcher.setIgnoreCase(true);
    matcher.setIgnoreAccent(true);
    double ecart = 0, ecartRelatif = 0, confiance = 0;
    string1 = matcher.process(string1);
    string1 = TraitementChainesDeCaracteres.ignorePunctuation(string1);
    string2 = matcher.process(string2);
    string2 = TraitementChainesDeCaracteres.ignorePunctuation(string2);
    st1 = new StringTokenizer(string1);
    st2 = new StringTokenizer(string2);
    matriceToken = new MatriceConfiance(string1, string2);
    // initialisation matrice
    for (int i = 0; i < matriceToken.nbRows; i++) {
      for (int j = 0; j < matriceToken.nbColumns; j++) {
        matriceToken.values[i][j] = 0;
      }
    }
    while (st1.hasMoreElements()) {
      tokenLigne.add(st1.nextToken());
    }
    while (st2.hasMoreElements()) {
      tokenColonne.add(st2.nextToken());
    }
    for (int i = 0; i < tokenLigne.size(); i++) {
      for (int j = 0; j < tokenColonne.size(); j++) {
        ecart = matcher.distance(tokenLigne.get(i).toString(), tokenColonne
            .get(j).toString());
        ecartRelatif = 1 - (ecart / Math.max(tokenLigne.get(i).toString()
            .length(), tokenColonne.get(j).toString().length()));
        matriceToken.values[i][j] = ecartRelatif;
      }
    }
    confiance = matriceToken.confidenceMaxsRows();
    return confiance;
  }
}

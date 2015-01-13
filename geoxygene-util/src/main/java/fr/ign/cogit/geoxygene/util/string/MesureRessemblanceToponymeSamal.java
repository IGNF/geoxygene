/**
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 * 
 */
package fr.ign.cogit.geoxygene.util.string;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MesureRessemblanceToponymeSamal {
  
  
  @SuppressWarnings("unused")
  public static double mesureRessemblanceToponymeSamal(String string1, String string2) {
  
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

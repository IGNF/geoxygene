/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.util;

public class StringUtil {

  public static String removeSpecialCharacters(String text) {
    String newText = text.replaceAll("é", "e");
    newText = newText.replaceAll("è", "e");
    newText = newText.replaceAll("à", "a");
    newText = newText.replaceAll("ù", "u");
    newText = newText.replaceAll("ç", "c");
    newText = newText.replaceAll("â", "a");
    newText = newText.replaceAll("î", "i");
    newText = newText.replaceAll("ô", "o");
    return newText;
  }
}

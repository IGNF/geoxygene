/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.panel;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Limits the characters inserted in a text field to real numbers, i.e. numbers
 * plus the '.' character.
 * @author GTouya
 * 
 */
public class RealLimitator extends PlainDocument {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  @Override
  public void insertString(int offs, String str, AttributeSet a)
      throws BadLocationException {
    for (int i = 0; i < str.length(); i++) {
      if ((!Character.isDigit(str.charAt(i))) && (str.charAt(i) != '.')) {
        return;
      }
    }
    super.insertString(offs, str, a);
  }
}

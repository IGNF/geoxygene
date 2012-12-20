/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen;

import org.apache.log4j.Logger;

/**
 * @author julien Gaffuri 6 mars 2009
 */
public class GeneralisationBottomPanelComplement {
  static Logger logger = Logger
      .getLogger(GeneralisationBottomPanelComplement.class.getName());

  /**
	 */
  private static GeneralisationBottomPanelComplement content = null;

  public static GeneralisationBottomPanelComplement getInstance() {
    if (GeneralisationBottomPanelComplement.content == null) {
      GeneralisationBottomPanelComplement.content = new GeneralisationBottomPanelComplement();
    }
    return GeneralisationBottomPanelComplement.content;
  }

  private GeneralisationBottomPanelComplement() {
  }

  /**
	 * 
	 */
  public void add() {

  }

}

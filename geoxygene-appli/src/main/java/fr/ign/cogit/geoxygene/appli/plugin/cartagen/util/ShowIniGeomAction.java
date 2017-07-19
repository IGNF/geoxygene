/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;

public class ShowIniGeomAction extends AbstractAction {

  /****/
  private static final long serialVersionUID = 1L;
  private GeOxygeneApplication appli;

  public ShowIniGeomAction(GeOxygeneApplication appli) {
    this.appli = appli;
    putValue(Action.NAME,
        I18N.getString("DisplayInitialGeomsFrame.shortTitle"));
    putValue(Action.SHORT_DESCRIPTION,
        I18N.getString("DisplayInitialGeomsFrame.title"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    DisplayInitialGeomsFrame frame = new DisplayInitialGeomsFrame(
        appli.getMainFrame().getSelectedProjectFrame());
    frame.setVisible(true);
  }

}

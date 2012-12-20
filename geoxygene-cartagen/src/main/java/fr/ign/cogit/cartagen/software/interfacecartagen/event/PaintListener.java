/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.event;

import java.awt.Graphics;
import java.util.EventListener;

import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;

public interface PaintListener extends EventListener {
  public void paint(final VisuPanel layerViewPanel, final Graphics graphics);
}

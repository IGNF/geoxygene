package fr.ign.cogit.geoxygene.appli.plugin;

import java.awt.Component;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;




public abstract class AbstractGeOxygeneApplicationPlugin implements GeOxygeneApplicationPlugin, ActionListener {
  
  /** GeOxygene application. */
  protected GeOxygeneApplication application = null;

  /**
   * Add menuItem in a menu
   * @param menuName
   * @param menuItemName
   * @return 
   */
  protected JMenu addMenu(String menuName, String menuItemName) {
    JMenu menu = null;
    for (Component c : application.getMainFrame().getMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null && aMenu.getText().equalsIgnoreCase(menuName)) {
          menu = aMenu;
        }
      }
    }
    if (menu == null) {
      menu = new JMenu(menuName);
    }
    JMenuItem menuItem = new JMenuItem(menuItemName);
    menuItem.addActionListener(this);
    menu.add(menuItem);
    
    return menu;
  }
  
  /**
   * Add menuItem in a submenu of a menu
   * @param menuName
   * @param subMenuName
   * @param subMenuItemName
   * @return 
   */
  protected JMenu addSubMenu(String menuName, String subMenuName, String subMenuItemName) {
    JMenu menu = null;
    JMenu submenu = null;
    
    for (Component c : application.getMainFrame().getMenuBar().getComponents()) {
      if (c instanceof JMenu) {
        JMenu aMenu = (JMenu) c;
        if (aMenu.getText() != null && aMenu.getText().equalsIgnoreCase(menuName)) {
          menu = aMenu;
        }
      }
    }
    
    if (menu == null) {
      // create parent menu
      menu = new JMenu(menuName);
      // create children menu
      submenu = new JMenu(subMenuName);
      
    } else {
      
      for (int i = 0; i < menu.getItemCount(); i++) {
        Component c = menu.getItem(i);
        if (c instanceof JMenu) {
          JMenu aMenu = (JMenu) c;
          if (aMenu.getText() != null && aMenu.getText().equalsIgnoreCase(subMenuName)) {
            submenu = aMenu;
          }
        } 
      }
      if (submenu == null) {
        submenu = new JMenu(subMenuName);
      } 
    }
    
    // create item
    JMenuItem menuItem = new JMenuItem(subMenuItemName);
    menuItem.addActionListener(this);
    // add item to submenu
    submenu.add(menuItem);
    // add submenu to menu
    menu.add(submenu);
    
    return menu;
  }

}

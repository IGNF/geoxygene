/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.GeneralisationLaunchingFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.GeoxygeneMenu;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset.DatasetGeoxGUIComponent;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.themes.DataThemesGUIComponent;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.util.GeneralisationConfigurationFrame;

/**
 * @author julien Gaffuri 6 mars 2009
 */
public class GeneralisationMenus {
  static Logger logger = Logger.getLogger(GeneralisationMenus.class.getName());

  /**
   * @return
   */
  static Logger getLogger() {
    return GeneralisationMenus.logger;
  }

  // generalisation
  private JMenu menuGene = new JMenu("Generalisation");

  public JMenu getMenuGene() {
    return this.menuGene;
  }

  private JMenuItem mLancerGeneralisation = new JMenuItem(
      "Generalisation launching");
  private JMenuItem mConfigGeneralisation = new JMenuItem(
      "Generalisation configuration");

  // data themes
  private DataThemesGUIComponent dataThemesMenu = new DataThemesGUIComponent(
      "Themes");

  // dataset management
  private DatasetGeoxGUIComponent menuDataset = new DatasetGeoxGUIComponent(
      "Dataset");

  /**
     */
  private static GeneralisationMenus content = null;

  public static GeneralisationMenus getInstance() {
    if (GeneralisationMenus.content == null) {
      GeneralisationMenus.content = new GeneralisationMenus();
    }
    return GeneralisationMenus.content;
  }

  private GeneralisationMenus() {
  }

  public JMenu getMenuComplement(Class<? extends JMenu> menuClass) {
    GeoxygeneMenu menu = CartagenApplication.getInstance().getFrame().getMenu();
    for (Component comp : menu.getComponents()) {
      if (menuClass.isInstance(comp)) {
        return menuClass.cast(comp);
      }
    }
    return null;
  }

  /**
   * Creation of the toolbar with all menus
   * @throws NoSuchMethodException
   */
  public void add(GeOxygeneApplication appli, JMenuBar menuBar, JMenu menu)
      throws NoSuchMethodException {

    JComponent parent = menuBar;
    boolean isBar = true;
    if (menu != null) {
      parent = menu;
      isBar = false;
    }
    // menu generalisation
    this.mLancerGeneralisation.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeneralisationLaunchingFrame.get().setVisible(true);
      }
    });
    this.menuGene.add(this.mLancerGeneralisation);

    this.mConfigGeneralisation.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeneralisationConfigurationFrame.getInstance().setVisible(true);
      }
    });
    this.menuGene.add(this.mConfigGeneralisation);

    // ajout aux menus existants

    Font font = menuBar.getFont();

    parent.add(this.menuGene, getMenuLocation(isBar, parent));
    parent.add(menuDataset, getMenuLocation(isBar, parent));
    parent.add(this.dataThemesMenu, getMenuLocation(isBar, parent));

    String configFilePath = "/ConfigGUIComponents.xml";
    // parse the Components config file
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = null;
    try {
      db = dbf.newDocumentBuilder();
    } catch (ParserConfigurationException e1) {
      e1.printStackTrace();
    }
    Document doc = null;
    try {
      if (db != null) {
        doc = db.parse(GeneralisationMenus.class
            .getResourceAsStream(configFilePath));
      }
    } catch (SAXException e1) {
      e1.printStackTrace();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    if (doc != null) {
      doc.getDocumentElement().normalize();
      Element root = (Element) doc.getElementsByTagName(
          "Config-CartAGen-GUI-Components").item(0);
      // loop on the GUI components to add to CartAGen GUI
      for (int i = 0; i < root.getElementsByTagName("Component").getLength(); i++) {
        Element compElem = (Element) root.getElementsByTagName("Component")
            .item(i);
        Element pathElem = (Element) compElem.getElementsByTagName("path")
            .item(0);
        Element nameElem = (Element) compElem.getElementsByTagName("name")
            .item(0);
        String className = pathElem.getChildNodes().item(0).getNodeValue();
        String name = nameElem.getChildNodes().item(0).getNodeValue();
        try {
          @SuppressWarnings("unchecked")
          Class<? extends JMenu> classObj = (Class<? extends JMenu>) Class
              .forName(className);
          JMenu componentMenu = null;
          try {
            Constructor<? extends JMenu> constructor = classObj
                .getConstructor(String.class);
            componentMenu = constructor.newInstance(name);
          } catch (NoSuchMethodException e) {
            Constructor<? extends JMenu> constructor = classObj.getConstructor(
                GeOxygeneApplication.class, String.class);
            componentMenu = constructor.newInstance(appli, name);
          }

          parent.add(componentMenu, getMenuLocation(isBar, parent));
        } catch (ClassNotFoundException e1) {
          e1.printStackTrace();
        } catch (SecurityException e) {
          e.printStackTrace();
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (InstantiationException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
    // met la bonne police a tous les trucs du menu
    for (int i = 0; i < parent.getComponentCount(); i++) {
      if (!(parent.getComponent(i) instanceof JMenu)) {
        continue;
      }
      JMenu comp = (JMenu) parent.getComponent(i);
      comp.setFont(font);
      for (int j = 0; j < comp.getItemCount(); j++) {
        if (comp.getItem(j) == null) {
          continue;
        }
        comp.getItem(j).setFont(font);
      }
    }

  }

  private int getMenuLocation(boolean isBar, JComponent parent) {
    if (isBar)
      return ((JMenuBar) parent).getMenuCount() - 1;
    else
      return ((JMenu) parent).getMenuComponentCount();
  }
}

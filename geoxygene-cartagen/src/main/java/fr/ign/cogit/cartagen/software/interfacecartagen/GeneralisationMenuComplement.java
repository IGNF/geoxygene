/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

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
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.GeneralisationConfigurationFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.annexes.GeneralisationLaunchingFrame;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.GeoxygeneMenu;
import fr.ign.cogit.cartagen.software.interfacecartagen.menus.DataThemesGUIComponent;
import fr.ign.cogit.cartagen.software.interfacecartagen.menus.DatasetGUIComponent;

/**
 * @author julien Gaffuri 6 mars 2009
 */
public class GeneralisationMenuComplement {
  static Logger logger = Logger.getLogger(GeneralisationMenuComplement.class
      .getName());

  /**
   * @return
   */
  static Logger getLogger() {
    return GeneralisationMenuComplement.logger;
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
  private DatasetGUIComponent menuDataset = new DatasetGUIComponent("Dataset");

  /**
     */
  private static GeneralisationMenuComplement content = null;

  public static GeneralisationMenuComplement getInstance() {
    if (GeneralisationMenuComplement.content == null) {
      GeneralisationMenuComplement.content = new GeneralisationMenuComplement();
    }
    return GeneralisationMenuComplement.content;
  }

  private GeneralisationMenuComplement() {
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
   */
  public void add() {

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

    GeoxygeneMenu menu = CartagenApplication.getInstance().getFrame().getMenu();
    Font font = menu.getFont();

    menu.add(this.menuGene, menu.getComponentCount() - 1);
    menu.add(this.menuDataset, menu.getComponentCount() - 1);
    menu.add(this.dataThemesMenu, menu.getComponentCount() - 1);

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
        doc = db.parse(GeneralisationMenuComplement.class
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
          Constructor<? extends JMenu> constructor = classObj
              .getConstructor(String.class);
          JMenu componentMenu = constructor.newInstance(name);
          menu.add(componentMenu, menu.getComponentCount() - 1);
        } catch (ClassNotFoundException e1) {
          e1.printStackTrace();
        } catch (SecurityException e) {
          e.printStackTrace();
        } catch (NoSuchMethodException e) {
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
    for (int i = 0; i < menu.getComponentCount(); i++) {
      if (!(menu.getComponent(i) instanceof JMenu)) {
        continue;
      }
      JMenu comp = (JMenu) menu.getComponent(i);
      comp.setFont(font);
      for (int j = 0; j < comp.getItemCount(); j++) {
        if (comp.getItem(j) == null) {
          continue;
        }
        comp.getItem(j).setFont(font);
      }
    }

  }

  /**
   * Creation of the toolbar with all menus
   */
  public void add(JMenuBar menu) {

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

    Font font = menu.getFont();

    menu.add(this.menuGene, menu.getComponentCount() - 1);
    menu.add(new DatasetGUIComponent("Dataset"), menu.getComponentCount() - 1);
    menu.add(this.dataThemesMenu, menu.getComponentCount() - 1);

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
        doc = db.parse(GeneralisationMenuComplement.class
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
          Constructor<? extends JMenu> constructor = classObj
              .getConstructor(String.class);
          JMenu componentMenu = constructor.newInstance(name);
          menu.add(componentMenu, menu.getComponentCount() - 1);
        } catch (ClassNotFoundException e1) {
          e1.printStackTrace();
        } catch (SecurityException e) {
          e.printStackTrace();
        } catch (NoSuchMethodException e) {
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
    for (int i = 0; i < menu.getComponentCount(); i++) {
      if (!(menu.getComponent(i) instanceof JMenu)) {
        continue;
      }
      JMenu comp = (JMenu) menu.getComponent(i);
      comp.setFont(font);
      for (int j = 0; j < comp.getItemCount(); j++) {
        if (comp.getItem(j) == null) {
          continue;
        }
        comp.getItem(j).setFont(font);
      }
    }

  }
}

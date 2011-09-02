package fr.ign.cogit.geoxygene.gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterJob;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * Le menu de l'interface
 * @author julien Gaffuri
 * 
 */
public class MenuGeoxygene extends JMenuBar {
  private static final long serialVersionUID = 5852864862627549895L;

  static Logger logger = Logger.getLogger(MenuGeoxygene.class.getName());

  private InterfaceGeoxygene frameMirage = null;

  /**
   * @return la fenètre à laquelle le menu est eventuellement lié
   */
  InterfaceGeoxygene getFrameMirage() {
    return this.frameMirage;
  }

  // vue
  private JMenu menuVue = new JMenu("Affichage");
  private JMenuItem mVueEch = new JMenuItem("1:"
      + Legende.getECHELLE_SYMBOLISATION());
  private JMenuItem mVueEch6250 = new JMenuItem("1:6250");
  private JMenuItem mVueEch12500 = new JMenuItem("1:12500");
  private JMenuItem mVueEch25k = new JMenuItem("1:25k");
  private JMenuItem mVueEch50k = new JMenuItem("1:50k");
  private JMenuItem mVueEch100k = new JMenuItem("1:100k");
  private JMenuItem mImprimer = new JMenuItem("Imprimer");

  // config
  private JMenu menuConfig = new JMenu("Configuration");
  public JCheckBoxMenuItem mAntiAliasing = new JCheckBoxMenuItem("AntiAliasing");
  private JMenuItem mRechargerConfigurationLogger = new JMenuItem(
      "Recharger la configuration du logger");

  // infos
  private JMenu menuInfos = new JMenu("?");
  public JMenuItem mAPropos = new JMenuItem("A propos...");

  public MenuGeoxygene(final InterfaceGeoxygene frameMirage) {
    this.frameMirage = frameMirage;

    this.setFont(new Font("Arial", Font.PLAIN, 9));

    // menu vue

    this.mVueEch.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        MenuGeoxygene.this
            .getFrameMirage()
            .getPanelVisu()
            .setTaillePixel(
                Legende.getECHELLE_SYMBOLISATION()
                    * PanelVisu.getMETERS_PER_PIXEL());
        MenuGeoxygene.this.getFrameMirage().getPanelVisu().repaint();
      }
    });
    this.mVueEch.setFont(this.getFont());
    this.menuVue.add(this.mVueEch);

    this.menuVue.addSeparator();

    this.mVueEch6250.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        MenuGeoxygene.this.getFrameMirage().getPanelVisu()
            .setTaillePixel(6250 * PanelVisu.getMETERS_PER_PIXEL());
        MenuGeoxygene.this.getFrameMirage().getPanelVisu().repaint();
      }
    });
    this.mVueEch6250.setFont(this.getFont());
    this.menuVue.add(this.mVueEch6250);

    this.mVueEch12500.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        MenuGeoxygene.this.getFrameMirage().getPanelVisu()
            .setTaillePixel(12500 * PanelVisu.getMETERS_PER_PIXEL());
        MenuGeoxygene.this.getFrameMirage().getPanelVisu().repaint();
      }
    });
    this.mVueEch12500.setFont(this.getFont());
    this.menuVue.add(this.mVueEch12500);

    this.mVueEch25k.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        MenuGeoxygene.this.getFrameMirage().getPanelVisu()
            .setTaillePixel(25000 * PanelVisu.getMETERS_PER_PIXEL());
        MenuGeoxygene.this.getFrameMirage().getPanelVisu().repaint();
      }
    });
    this.mVueEch25k.setFont(this.getFont());
    this.menuVue.add(this.mVueEch25k);

    this.mVueEch50k.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        MenuGeoxygene.this.getFrameMirage().getPanelVisu()
            .setTaillePixel(50000 * PanelVisu.getMETERS_PER_PIXEL());
        MenuGeoxygene.this.getFrameMirage().getPanelVisu().repaint();
      }
    });
    this.mVueEch50k.setFont(this.getFont());
    this.menuVue.add(this.mVueEch50k);

    this.mVueEch100k.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        MenuGeoxygene.this.getFrameMirage().getPanelVisu()
            .setTaillePixel(100000 * PanelVisu.getMETERS_PER_PIXEL());
        MenuGeoxygene.this.getFrameMirage().getPanelVisu().repaint();
      }
    });
    this.mVueEch100k.setFont(this.getFont());
    this.menuVue.add(this.mVueEch100k);

    this.menuVue.addSeparator();

    this.mImprimer.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        Thread th = new Thread(new Runnable() {
          public void run() {
            try {
              PrinterJob printJob = PrinterJob.getPrinterJob();
              printJob.setPrintable(MenuGeoxygene.this.getFrameMirage()
                  .getPanelVisu());
              PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
              if (printJob.printDialog(aset)) {
                printJob.print(aset);
              }
            } catch (java.security.AccessControlException ace) {
              JOptionPane.showMessageDialog(
                  MenuGeoxygene.this.getFrameMirage().getPanelVisu(),
                  "Impossible d'imprimer; probleme de controle d'acces: "
                      + ace.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
              ex.printStackTrace();
            }
          }
        });
        th.start();
      }
    });
    this.mImprimer.setFont(this.getFont());
    this.menuVue.add(this.mImprimer);

    JMenuItem menuItemCentrer = new JMenuItem("Centrer la vue");
    menuItemCentrer.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        MenuGeoxygene.this.getFrameMirage().getPanelVisu().centrer();
      }
    });
    menuItemCentrer.setFont(this.getFont());
    this.menuVue.add(menuItemCentrer);

    // menu config

    this.mAntiAliasing.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MenuGeoxygene.this.getFrameMirage().getPanelVisu()
            .setAntiAliasing(MenuGeoxygene.this.mAntiAliasing.isSelected());
        MenuGeoxygene.this.getFrameMirage().getPanelVisu().repaint();
      }
    });
    this.mAntiAliasing.setSelected(this.getFrameMirage().getPanelVisu()
        .isAntiAliasing());
    this.mAntiAliasing.setFont(this.getFont());
    this.menuConfig.add(this.mAntiAliasing);

    this.mRechargerConfigurationLogger.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        MenuGeoxygene.logger.info("Rechargement configuration log");
        PropertyConfigurator.configure("log4j.properties");
      }
    });
    this.mRechargerConfigurationLogger.setFont(this.getFont());
    this.menuConfig.add(this.mRechargerConfigurationLogger);

    // menu infos
    this.mAPropos.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        String message = "<html><b>GéOxygène</b><br />http://oxygene-project.sourceforge.net/<br /><br />Laboratoire COGIT<br />http://recherche.ign.fr/labos/cogit<br /><br />Institut Géographique National<br />http://www.ign.fr</html>";
        String titre = "A propos de MiraGe";
        JOptionPane
            .showMessageDialog(null, message, titre,
                JOptionPane.INFORMATION_MESSAGE, new ImageIcon(
                    "images/splash.png"));
      }
    });
    this.mAPropos.setFont(this.getFont());
    this.menuInfos.add(this.mAPropos);

    this.menuVue.setFont(this.getFont());
    this.add(this.menuVue);
    this.menuConfig.setFont(this.getFont());
    this.add(this.menuConfig);
    this.menuInfos.setFont(this.getFont());
    this.add(this.menuInfos);
  }

  /**
   * Renvoie le Menu "Affichage"
   * @return le Menu "Affichage"
   */
  public JMenu getMenuAffichage() {
    return this.menuVue;
  }
}

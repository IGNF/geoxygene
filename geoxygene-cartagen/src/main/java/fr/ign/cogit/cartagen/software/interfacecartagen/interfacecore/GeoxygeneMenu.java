/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/*
 * Créé le 2 août 2005
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import java.awt.Font;
import java.awt.RenderingHints;
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

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool.GeomPoolFrame;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;

public class GeoxygeneMenu extends JMenuBar {
  private static final long serialVersionUID = 5852864862627549895L;

  static Logger logger = Logger.getLogger(GeoxygeneMenu.class.getName());

  // la fenetre a laquelle le panel est eventuellement lie
  /**
     */
  private GeoxygeneFrame frame = null;

  /**
   * @return
   */
  GeoxygeneFrame getFrame() {
    return this.frame;
  }

  // vue
  /**
     */
  private JMenu menuView = new JMenu("View");
  /**
     */
  private JMenuItem mVueEch = new JMenuItem("1:"
      + Legend.getSYMBOLISATI0N_SCALE());
  /**
     */
  private JMenuItem mVueEch6250 = new JMenuItem("1:6250");
  /**
     */
  private JMenuItem mVueEch12500 = new JMenuItem("1:12500");
  /**
     */
  private JMenuItem mVueEch25k = new JMenuItem("1:25k");
  /**
     */
  private JMenuItem mVueEch50k = new JMenuItem("1:50k");
  /**
     */
  private JMenuItem mVueEch100k = new JMenuItem("1:100k");
  /**
     */
  private JMenuItem mImprimer = new JMenuItem("Print");

  // config
  /**
     */
  private JMenu menuConfig = new JMenu("Config");
  /**
     */
  public JCheckBoxMenuItem mAntiAliasing = new JCheckBoxMenuItem("AntiAliasing");
  /**
     */
  private JMenuItem mRechargerConfigurationLogger = new JMenuItem(
      "Reload logger configuration");

  // Geometries pool
  /**
     */
  private JMenu menuGeomPool = new JMenu("Geometries pool");
  /**
     */
  public JCheckBoxMenuItem mGeomPoolVisible = new JCheckBoxMenuItem("Visible");
  /**
   */
  public JMenuItem mGeomPoolManagement = new JMenuItem("Management");
  /**
     */
  private JMenuItem mGeomPoolEmpty = new JMenuItem("Empty");
  /**
     */
  private JMenuItem mGeomPoolAddObjects = new JMenuItem(
      "Add selected objects geometry");
  /**
     */
  public JCheckBoxMenuItem mGeomPoolDrawSegments = new JCheckBoxMenuItem(
      "Draw segments");
  public IDirectPositionList mGeomPoolDrawSegmentsCoords = new DirectPositionList();

  // infos
  /**
     */
  private JMenu menuInfos = new JMenu("?");
  /**
     */
  public JMenuItem mAPropos = new JMenuItem("About...");

  public GeoxygeneMenu(final GeoxygeneFrame frameMirage) {
    this.frame = frameMirage;

    this.setFont(new Font("Arial", Font.PLAIN, 9));

    // menu vue

    this.mVueEch.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeoxygeneMenu.this.getFrame().getVisuPanel().setPixelSize(
            Legend.getSYMBOLISATI0N_SCALE() * VisuPanel.getMETERS_PER_PIXEL());
        GeoxygeneMenu.this.getFrame().getVisuPanel().repaint();
      }
    });
    this.mVueEch.setFont(this.getFont());
    this.menuView.add(this.mVueEch);

    this.menuView.addSeparator();

    this.mVueEch6250.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeoxygeneMenu.this.getFrame().getVisuPanel().setPixelSize(
            6250 * VisuPanel.getMETERS_PER_PIXEL());
        GeoxygeneMenu.this.getFrame().getVisuPanel().repaint();
      }
    });
    this.mVueEch6250.setFont(this.getFont());
    this.menuView.add(this.mVueEch6250);

    this.mVueEch12500.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeoxygeneMenu.this.getFrame().getVisuPanel().setPixelSize(
            12500 * VisuPanel.getMETERS_PER_PIXEL());
        GeoxygeneMenu.this.getFrame().getVisuPanel().repaint();
      }
    });
    this.mVueEch12500.setFont(this.getFont());
    this.menuView.add(this.mVueEch12500);

    this.mVueEch25k.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeoxygeneMenu.this.getFrame().getVisuPanel().setPixelSize(
            25000 * VisuPanel.getMETERS_PER_PIXEL());
        GeoxygeneMenu.this.getFrame().getVisuPanel().repaint();
      }
    });
    this.mVueEch25k.setFont(this.getFont());
    this.menuView.add(this.mVueEch25k);

    this.mVueEch50k.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeoxygeneMenu.this.getFrame().getVisuPanel().setPixelSize(
            50000 * VisuPanel.getMETERS_PER_PIXEL());
        GeoxygeneMenu.this.getFrame().getVisuPanel().repaint();
      }
    });
    this.mVueEch50k.setFont(this.getFont());
    this.menuView.add(this.mVueEch50k);

    this.mVueEch100k.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        GeoxygeneMenu.this.getFrame().getVisuPanel().setPixelSize(
            100000 * VisuPanel.getMETERS_PER_PIXEL());
        GeoxygeneMenu.this.getFrame().getVisuPanel().repaint();
      }
    });
    this.mVueEch100k.setFont(this.getFont());
    this.menuView.add(this.mVueEch100k);

    this.menuView.addSeparator();

    this.mImprimer.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        Thread th = new Thread(new Runnable() {
          @Override
          public void run() {
            try {
              PrinterJob printJob = PrinterJob.getPrinterJob();
              printJob.setPrintable(GeoxygeneMenu.this.getFrame()
                  .getVisuPanel());
              PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
              if (printJob.printDialog(aset)) {
                printJob.print(aset);
              }
            } catch (java.security.AccessControlException ace) {
              JOptionPane.showMessageDialog(GeoxygeneMenu.this.getFrame()
                  .getVisuPanel(),
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
    this.menuView.add(this.mImprimer);

    // menu config

    this.mAntiAliasing.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (GeoxygeneMenu.this.mAntiAliasing.isSelected()) {
          // activer l'antialiasing
          GeoxygeneMenu.this.getFrame().getVisuPanel().antiAliasing = true;
          GeoxygeneMenu.this.getFrame().getVisuPanel().getG2D()
              .setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                  RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
          // desactiver l'antialiasing
          GeoxygeneMenu.this.getFrame().getVisuPanel().antiAliasing = false;
          GeoxygeneMenu.this.getFrame().getVisuPanel().getG2D()
              .setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                  RenderingHints.VALUE_ANTIALIAS_OFF);
        }
      }
    });
    this.mAntiAliasing.setSelected(this.getFrame().getVisuPanel().antiAliasing);
    this.mAntiAliasing.setFont(this.getFont());
    this.menuConfig.add(this.mAntiAliasing);

    this.mRechargerConfigurationLogger.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GeoxygeneMenu.logger.info("Rechargement configuration log");
        PropertyConfigurator.configure("log4j.properties");
      }
    });
    this.mRechargerConfigurationLogger.setFont(this.getFont());
    this.menuConfig.add(this.mRechargerConfigurationLogger);

    // geometries pool menu
    this.menuGeomPool
        .setToolTipText("geometries pool: a set of geometries usefull to display");

    this.mGeomPoolVisible.setSelected(true);
    this.menuGeomPool.add(this.mGeomPoolVisible);

    this.mGeomPoolManagement.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        GeomPoolFrame.getInstance().setVisible(true);
      }
    });
    this.menuGeomPool.add(this.mGeomPoolManagement);

    this.mGeomPoolEmpty.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // empty the geometries pool layer
        GeoxygeneMenu.this.getFrame().getLayerManager().emptyGeometriesPool();
      }
    });
    this.menuGeomPool.add(this.mGeomPoolEmpty);

    this.mGeomPoolAddObjects.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (IFeature feat : CartagenApplication.getInstance().getFrame()
            .getVisuPanel().selectedObjects) {
          if (!(feat instanceof IGeneObj)) {
            continue;
          }
          CartagenApplication.getInstance().getFrame().getLayerManager()
              .addToGeometriesPool(((IGeneObj) feat).getGeom());
        }
      }
    });
    this.menuGeomPool.add(this.mGeomPoolAddObjects);

    this.mGeomPoolDrawSegments.setSelected(false);
    this.menuGeomPool.add(this.mGeomPoolDrawSegments);

    // menu infos
    this.mAPropos.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        String message = "<html><b>GéOxygène</b><br />http://oxygene-project.sourceforge.net/<br /><br />Laboratoire COGIT<br />http://recherche.ign.fr/labos/cogit<br /><br />Institut Géographique National<br />http://www.ign.fr</html>";
        String titre = "A propos de MiraGe";
        JOptionPane.showMessageDialog(null, message, titre,
            JOptionPane.INFORMATION_MESSAGE, new ImageIcon(GeoxygeneMenu.class
                .getResource("images/splash.png").getPath().replaceAll("%20",
                    " ")));
      }
    });
    this.mAPropos.setFont(this.getFont());
    this.menuInfos.add(this.mAPropos);

    this.menuView.setFont(this.getFont());
    this.add(this.menuView);
    this.menuConfig.setFont(this.getFont());
    this.add(this.menuConfig);
    this.menuGeomPool.setFont(this.getFont());
    this.add(this.menuGeomPool);
    this.menuInfos.setFont(this.getFont());
    this.add(this.menuInfos);
  }
}

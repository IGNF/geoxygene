/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.menus;

import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.VisuPanel;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;

/**
 * Extra menu that contains utility functions of CartAGen.
 * @author GTouya
 * 
 */
public class ConfigMenuComponent extends JMenu {

  static Logger logger = Logger.getLogger(ConfigMenuComponent.class.getName());

  private VisuPanel view;
  /**
   */
  public JCheckBoxMenuItem mAntiAliasing;
  /**
   */
  private JMenuItem mRechargerConfigurationLogger;

  private JCheckBoxMenuItem mTransparentSelection;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public ConfigMenuComponent(String title) {
    super(title);
    this.mAntiAliasing = new JCheckBoxMenuItem(new AntiAliasingAction());
    this.mAntiAliasing.setSelected(true);
    this.mAntiAliasing.setFont(this.getFont());
    this.mRechargerConfigurationLogger = new JMenuItem(new ReloadLoggerAction());
    this.mRechargerConfigurationLogger.setFont(this.getFont());
    this.add(mAntiAliasing);
    this.add(mRechargerConfigurationLogger);
    this.addSeparator();
    this.mTransparentSelection = new JCheckBoxMenuItem(
        new TransparentSelectionAction());
    this.mTransparentSelection.setSelected(true);
    this.add(mTransparentSelection);
    this.add(new JMenuItem(new SelectionColorAction()));
  }

  /**
   * Action that enables the anti-aliasing in the CartAGen display.
   * @author GTouya
   * 
   */
  class AntiAliasingAction extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      view = CartagenApplication.getInstance().getFrame().getVisuPanel();
      if (mAntiAliasing.isSelected()) {
        // activer l'antialiasing
        view.antiAliasing = true;
        view.getG2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
      } else {
        // desactiver l'antialiasing
        view.antiAliasing = false;
        view.getG2D().setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_OFF);
      }
    }

    public AntiAliasingAction() {
      putValue(Action.SHORT_DESCRIPTION,
          "Enable the Anti-Aliasing in the display frame");
      putValue(Action.NAME, "AntiAliasing");
    }
  }

  /**
   * @author GTouya
   * 
   */
  class ReloadLoggerAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      logger.info("Rechargement configuration log");
      PropertyConfigurator.configure("log4j.properties");
    }

    public ReloadLoggerAction() {
      putValue(Action.NAME, "Reload logger configuration");
    }
  }

  /**
   * Make the selection display transparent or opaque.
   * @author GTouya
   * 
   */
  class TransparentSelectionAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      view = CartagenApplication.getInstance().getFrame().getVisuPanel();
      if (mTransparentSelection.isSelected())
        view.setTransparentSelection(true);
      else
        view.setTransparentSelection(false);
    }

    public TransparentSelectionAction() {
      putValue(Action.SHORT_DESCRIPTION,
          "Make the display of selected objects transparent or opaque");
      putValue(Action.NAME, "Transparent Selection Display");
    }
  }

  /**
   * Modify the selection color.
   * @author GTouya
   * 
   */
  class SelectionColorAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      view = CartagenApplication.getInstance().getFrame().getVisuPanel();
      ColorChooserFrame frame = new ColorChooserFrame();
      frame.setVisible(true);
    }

    public SelectionColorAction() {
      putValue(Action.NAME, "Change selection color");
    }

    class ColorChooserFrame extends JFrame implements ActionListener,
        MouseListener {

      /****/
      private static final long serialVersionUID = 1L;
      private JColorChooser colorChooser;

      ColorChooserFrame() {
        super(I18N.getString("MainLabels.color"));
        JPanel btnPanel = new JPanel();
        JButton okBtn = new JButton("OK");
        okBtn.addActionListener(this);
        okBtn.setActionCommand("ok");
        btnPanel.add(okBtn);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.X_AXIS));
        colorChooser = new JColorChooser();
        colorChooser.setColor(view.getSelectionColor());
        this.getContentPane().add(colorChooser);
        this.getContentPane().add(btnPanel);
        this.getContentPane().setLayout(
            new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
        this.pack();
      }

      @Override
      public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
          colorSelected();
          this.setVisible(false);
        }
      }

      @Override
      public void mousePressed(MouseEvent e) {
      }

      @Override
      public void mouseReleased(MouseEvent e) {
      }

      @Override
      public void mouseEntered(MouseEvent e) {
      }

      @Override
      public void mouseExited(MouseEvent e) {
      }

      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("ok")) {
          colorSelected();
          this.setVisible(false);
        } else if (e.getActionCommand().equals("cancel")) {
          this.setVisible(false);
        }
      }

      private void colorSelected() {
        view.setSelectionColor(colorChooser.getColor());
      }
    }

  }

}

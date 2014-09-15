package fr.ign.cogit.geoxygene.appli.plugin.lenses;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.event.PaintListener;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.ProjectFramePlugin;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.CartAGenPlugin;

public class LensesPlugin implements ProjectFramePlugin,
    GeOxygeneApplicationPlugin {

  private GeOxygeneApplication application = null;
  @SuppressWarnings("unused")
  private JCheckBoxMenuItem mFishEye, mJelly;

  @Override
  public void initialize(GeOxygeneApplication application) {
    this.application = application;
    JMenu menu = new JMenu("Lenses");
    mFishEye = new JCheckBoxMenuItem(new FishEyeAction());
    mJelly = new JCheckBoxMenuItem(new JellyLensAction());
    menu.add(mFishEye);
    // menu.add(mJelly);
    menu.add(new JMenuItem(new LensParamsAction()));
    application.getMainFrame().getMenuBar()
        .add(menu, application.getMainFrame().getMenuBar().getMenuCount() - 2);

  }

  @Override
  public void initialize(ProjectFrame projectFrame) {
    // TODO Auto-generated method stub

  }

  /**
   * Creates a fisheye view around the cursor and disables any previously
   * activated lens.
   * 
   * @author GTouya
   * 
   */
  class FishEyeAction extends AbstractAction {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      LayerViewPanel layerViewPanel = application.getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel();
      if (!mFishEye.isSelected()) {

        for (PaintListener listener : layerViewPanel.getOverlayListeners()) {
          if (listener instanceof LensPaintListener) {
            layerViewPanel.getOverlayListeners().remove(listener);
            break;
          }
        }
      } else {
        LensPaintListener listener = new LensPaintListener(CartAGenPlugin
            .getInstance().getApplication().getMainFrame(), CartAGenPlugin
            .getInstance().getApplication().getMainFrame().getMode(), mFishEye);
        layerViewPanel.getOverlayListeners().add(listener);
        CartAGenPlugin.getInstance().getApplication().getMainFrame().getMode()
            .setCurrentMode(listener);
      }
    }

    public FishEyeAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Adds a FishEye lens around the cursor in the map panel");
      this.putValue(Action.NAME, "FishEye Lens");
    }
  }

  /**
   * Creates a JellyLens view around the cursor and disables any previously
   * activated lens. See (Pindat et al. 2012) for details on the JellyLens.
   * 
   * @author GTouya
   * 
   */
  class JellyLensAction extends AbstractAction {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      // TODO
    }

    public JellyLensAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Adds a JellyLens type lens around the cursor in the map panel");
      this.putValue(Action.NAME, "JellyLens");
    }
  }

  /**
   * Create a new empty dataset, with its zone details, in which data can be
   * added later.
   * 
   * @author GTouya
   * 
   */
  class LensParamsAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      LensParamsFrame frame = new LensParamsFrame();
      frame.setVisible(true);
    }

    public LensParamsAction() {
      this.putValue(Action.NAME, "Set the parameters of the lenses");
    }
  }

  class LensParamsFrame extends JFrame implements ActionListener {

    private JTabbedPane tabs;
    private JSpinner spinFishFocus, spinFishTrans;

    /****/
    private static final long serialVersionUID = 1L;

    public LensParamsFrame() throws HeadlessException {
      super("Lens Parameters");
      this.setSize(500, 200);
      this.setAlwaysOnTop(true);

      // the tabbed pane
      tabs = new JTabbedPane();
      // a panel for the Fisheye Lens
      JPanel panelFish = new JPanel();
      SpinnerModel fishFocusModel = new SpinnerNumberModel(50.0, 10.0, 500.0,
          5.0);
      spinFishFocus = new JSpinner(fishFocusModel);
      SpinnerModel fishTransModel = new SpinnerNumberModel(100.0, 0.0, 500.0,
          5.0);
      spinFishTrans = new JSpinner(fishTransModel);
      panelFish.add(new JLabel("Focus radius (px) "));
      panelFish.add(spinFishFocus);
      panelFish.add(new JLabel("Transition radius (px) "));
      panelFish.add(spinFishTrans);
      panelFish.setLayout(new BoxLayout(panelFish, BoxLayout.X_AXIS));
      tabs.add(panelFish, "FishEye");
      tabs.setSelectedIndex(0);

      // OK button frame
      JPanel panelBtn = new JPanel();
      panelBtn.setLayout(new BoxLayout(panelBtn, BoxLayout.X_AXIS));
      JButton bouton0 = new JButton("OK");
      bouton0.addActionListener(this);
      bouton0.setActionCommand("OK");
      JButton bouton1 = new JButton("Cancel");
      bouton1.addActionListener(this);
      bouton1.setActionCommand("cancel");
      panelBtn.add(bouton0);
      panelBtn.add(bouton1);
      panelBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

      // global layout of the frame
      this.getContentPane().add(tabs);
      this.getContentPane().add(panelBtn);
      this.getContentPane().setLayout(
          new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("cancel")) {
        this.dispose();
      } else if (e.getActionCommand().equals("OK")) {
        FishEyeLens.FOCUS_RADIUS = (Double) spinFishFocus.getValue();
        FishEyeLens.TRANSITION_RADIUS = (Double) spinFishTrans.getValue();
        if (mFishEye.isSelected()) {
          LayerViewPanel layerViewPanel = application.getMainFrame()
              .getSelectedProjectFrame().getLayerViewPanel();
          for (PaintListener listener : layerViewPanel.getOverlayListeners()) {
            if (listener instanceof LensPaintListener) {
              layerViewPanel.getOverlayListeners().remove(listener);
              break;
            }
          }
          LensPaintListener listener = new LensPaintListener(CartAGenPlugin
              .getInstance().getApplication().getMainFrame(), CartAGenPlugin
              .getInstance().getApplication().getMainFrame().getMode(),
              mFishEye);
          layerViewPanel.getOverlayListeners().add(listener);
          CartAGenPlugin.getInstance().getApplication().getMainFrame()
              .getMode().setCurrentMode(listener);
        }
        this.dispose();
      }
    }

  }

}

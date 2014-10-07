package fr.ign.cogit.geoxygene.appli.plugin.lenses;

import java.awt.Dimension;
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
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.event.PaintListener;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.ProjectFramePlugin;

public class LensesPlugin implements ProjectFramePlugin,
    GeOxygeneApplicationPlugin {

  private GeOxygeneApplication application = null;
  @SuppressWarnings("unused")
  private JCheckBoxMenuItem mFishEye, mJelly, mPierce, mFuzzyPierce,
      mDoublePierce;
  private JCheckBoxMenuItem current;
  private PaintListener listener;

  @Override
  public void initialize(GeOxygeneApplication application) {
    this.application = application;
    JMenu menu = new JMenu("Lenses");
    mFishEye = new JCheckBoxMenuItem(new FishEyeAction());
    mJelly = new JCheckBoxMenuItem(new JellyLensAction());
    mPierce = new JCheckBoxMenuItem(new PierceLensAction());
    mFuzzyPierce = new JCheckBoxMenuItem(new FuzzyPierceLensAction());
    mDoublePierce = new JCheckBoxMenuItem(new DoublePierceLensAction());
    menu.add(mFishEye);
    menu.add(mPierce);
    menu.add(mFuzzyPierce);
    menu.add(mDoublePierce);
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
            if (((LensPaintListener) listener).getLens() instanceof FishEyeLens) {
              layerViewPanel.getOverlayListeners().remove(listener);
            }
            break;
          }
        }
      } else {
        LensPaintListener listener = new LensPaintListener(
            LensesPlugin.this.application.getMainFrame(),
            LensesPlugin.this.application.getMainFrame().getMode(), mFishEye);
        listener.setLens(LensPaintListener.FISHEYE_LENS);
        layerViewPanel.getOverlayListeners().add(listener);
        LensesPlugin.this.application.getMainFrame().getMode()
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
   * Adds a lens that pierces the map around the cursor to display another
   * layer, such as a photograph.
   * 
   * @author GTouya
   * 
   */
  class PierceLensAction extends AbstractAction {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      LayerViewPanel layerViewPanel = application.getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel();
      if (!mPierce.isSelected()) {
        for (PaintListener listener : layerViewPanel.getOverlayListeners()) {
          if (listener instanceof LensPaintListener) {
            if (((LensPaintListener) listener).getLens() instanceof PierceLens) {
              layerViewPanel.getOverlayListeners().remove(listener);
            }
            break;
          }
        }
      } else {
        LensPaintListener listener = new LensPaintListener(
            LensesPlugin.this.application.getMainFrame(),
            LensesPlugin.this.application.getMainFrame().getMode(), mPierce);
        listener.setLens(LensPaintListener.PIERCE_LENS);
        layerViewPanel.getOverlayListeners().add(listener);
        LensesPlugin.this.application.getMainFrame().getMode()
            .setCurrentMode(listener);
      }
    }

    public PierceLensAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Adds a lens that pierces the map around the cursor to display another layer");
      this.putValue(Action.NAME, "Pierce Lens");
    }
  }

  /**
   * Adds a fuzzy lens that pierces the map around the cursor to display another
   * layer, such as a photograph.
   * 
   * @author GTouya
   * 
   */
  class FuzzyPierceLensAction extends AbstractAction {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      LayerViewPanel layerViewPanel = application.getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel();
      if (!mFuzzyPierce.isSelected()) {
        for (PaintListener listener : layerViewPanel.getOverlayListeners()) {
          if (listener instanceof LensPaintListener) {
            if (((LensPaintListener) listener).getLens() instanceof FuzzyPierceLens) {
              layerViewPanel.getOverlayListeners().remove(listener);
            }
            break;
          }
        }
      } else {
        LensPaintListener listener = new LensPaintListener(
            LensesPlugin.this.application.getMainFrame(),
            LensesPlugin.this.application.getMainFrame().getMode(),
            mFuzzyPierce);
        listener.setLens(LensPaintListener.FUZZY_PIERCE_LENS);
        layerViewPanel.getOverlayListeners().add(listener);
        LensesPlugin.this.application.getMainFrame().getMode()
            .setCurrentMode(listener);
      }
    }

    public FuzzyPierceLensAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Adds a lens that pierces the map around the cursor to display another layer");
      this.putValue(Action.NAME, "Fuzzy Pierce Lens");
    }
  }

  /**
   * Adds a double lens that pierces the map around the cursor to display other
   * layers, such as a photograph.
   * 
   * @author GTouya
   * 
   */
  class DoublePierceLensAction extends AbstractAction {

    /**
   * 
   */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      LayerViewPanel layerViewPanel = application.getMainFrame()
          .getSelectedProjectFrame().getLayerViewPanel();
      if (!mDoublePierce.isSelected()) {
        for (PaintListener listener : layerViewPanel.getOverlayListeners()) {
          if (listener instanceof LensPaintListener) {
            if (((LensPaintListener) listener).getLens() instanceof DoublePierceLens) {
              layerViewPanel.getOverlayListeners().remove(listener);
            }
            break;
          }
        }
      } else {
        LensPaintListener listener = new LensPaintListener(
            LensesPlugin.this.application.getMainFrame(),
            LensesPlugin.this.application.getMainFrame().getMode(),
            mDoublePierce);
        listener.setLens(LensPaintListener.DOUBLE_PIERCE_LENS);
        layerViewPanel.getOverlayListeners().add(listener);
        LensesPlugin.this.application.getMainFrame().getMode()
            .setCurrentMode(listener);
      }
    }

    public DoublePierceLensAction() {
      this.putValue(Action.SHORT_DESCRIPTION,
          "Adds a lens that pierces the map around the cursor to display another layer");
      this.putValue(Action.NAME, "Double Pierce Lens");
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
    private JSpinner spinFishFocus, spinFishTrans, spinPierceFocus,
        spinFuzzyPierceFocus, spinFuzzyPierceTrans, spinDoublePierceFocus,
        spinDoublePierceTrans;
    private JTextField txtPierceLayer, txtFuzzyPierceLayer,
        txtDoublePierceFocusLayer, txtDoublePierceTransLayer;

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
      SpinnerModel fishFocusModel = new SpinnerNumberModel(
          FishEyeLens.FOCUS_RADIUS, 10.0, 500.0, 5.0);
      spinFishFocus = new JSpinner(fishFocusModel);
      SpinnerModel fishTransModel = new SpinnerNumberModel(
          FishEyeLens.TRANSITION_RADIUS, 0.0, 500.0, 5.0);
      spinFishTrans = new JSpinner(fishTransModel);
      panelFish.add(new JLabel("Focus radius (px) "));
      panelFish.add(spinFishFocus);
      panelFish.add(new JLabel("Transition radius (px) "));
      panelFish.add(spinFishTrans);
      panelFish.setLayout(new BoxLayout(panelFish, BoxLayout.X_AXIS));

      // a panel for the Pierce Lens
      JPanel panelPierce = new JPanel();
      txtPierceLayer = new JTextField();
      txtPierceLayer.setMaximumSize(new Dimension(140, 20));
      txtPierceLayer.setMinimumSize(new Dimension(140, 20));
      txtPierceLayer.setPreferredSize(new Dimension(140, 20));
      SpinnerModel pierceRadiusModel = new SpinnerNumberModel(
          PierceLens.FOCUS_RADIUS, 10.0, 500.0, 5.0);
      spinPierceFocus = new JSpinner(pierceRadiusModel);

      panelPierce.add(new JLabel("Layer to display in the Lens: "));
      panelPierce.add(txtPierceLayer);
      panelPierce.add(new JLabel("Pocus radius (px) "));
      panelPierce.add(spinPierceFocus);

      panelPierce.setLayout(new BoxLayout(panelPierce, BoxLayout.X_AXIS));

      // a panel for the FuzzyPierce Lens
      JPanel panelFuzzyPierce = new JPanel();
      txtFuzzyPierceLayer = new JTextField();
      txtFuzzyPierceLayer.setMaximumSize(new Dimension(140, 20));
      txtFuzzyPierceLayer.setMinimumSize(new Dimension(140, 20));
      txtFuzzyPierceLayer.setPreferredSize(new Dimension(140, 20));
      SpinnerModel pierceFuzzyRadiusModel = new SpinnerNumberModel(
          FuzzyPierceLens.FOCUS_RADIUS, 10.0, 500.0, 5.0);
      spinFuzzyPierceFocus = new JSpinner(pierceFuzzyRadiusModel);
      SpinnerModel pierceFuzzyTransModel = new SpinnerNumberModel(
          FuzzyPierceLens.TRANSITION_RADIUS, 10.0, 500.0, 10.0);
      spinFuzzyPierceTrans = new JSpinner(pierceFuzzyTransModel);
      panelFuzzyPierce.add(new JLabel("Layer to display in the Lens: "));
      panelFuzzyPierce.add(txtFuzzyPierceLayer);
      panelFuzzyPierce.add(new JLabel("Pocus radius (px) "));
      panelFuzzyPierce.add(spinFuzzyPierceFocus);
      panelFuzzyPierce.add(new JLabel("Transition radius (px) "));
      panelFuzzyPierce.add(spinFuzzyPierceTrans);
      panelFuzzyPierce.setLayout(new BoxLayout(panelFuzzyPierce,
          BoxLayout.X_AXIS));

      // a panel for the DoublePierce Lens
      JPanel panelDoublePierce = new JPanel();
      txtDoublePierceFocusLayer = new JTextField();
      txtDoublePierceFocusLayer.setMaximumSize(new Dimension(140, 20));
      txtDoublePierceFocusLayer.setMinimumSize(new Dimension(140, 20));
      txtDoublePierceFocusLayer.setPreferredSize(new Dimension(140, 20));
      txtDoublePierceTransLayer = new JTextField();
      txtDoublePierceTransLayer.setMaximumSize(new Dimension(140, 20));
      txtDoublePierceTransLayer.setMinimumSize(new Dimension(140, 20));
      txtDoublePierceTransLayer.setPreferredSize(new Dimension(140, 20));
      SpinnerModel doublePierceFocusModel = new SpinnerNumberModel(
          DoublePierceLens.FOCUS_RADIUS, 10.0, 500.0, 5.0);
      spinDoublePierceFocus = new JSpinner(doublePierceFocusModel);
      SpinnerModel doublePierceTransModel = new SpinnerNumberModel(
          DoublePierceLens.TRANSITION_RADIUS, 10.0, 500.0, 10.0);
      spinDoublePierceTrans = new JSpinner(doublePierceTransModel);
      panelDoublePierce.add(new JLabel("Layer to display in the Focus: "));
      panelDoublePierce.add(txtDoublePierceFocusLayer);
      panelDoublePierce.add(new JLabel("Focus radius (px) "));
      panelDoublePierce.add(spinDoublePierceFocus);
      panelDoublePierce.add(new JLabel("Layer to display in the Transition: "));
      panelDoublePierce.add(txtDoublePierceTransLayer);
      panelDoublePierce.add(new JLabel("Transition radius (px) "));
      panelDoublePierce.add(spinDoublePierceTrans);
      panelDoublePierce.setLayout(new BoxLayout(panelDoublePierce,
          BoxLayout.X_AXIS));

      tabs.add(panelFish, "FishEye");
      tabs.add(panelPierce, "Pierce");
      tabs.add(panelFuzzyPierce, "Fuzzy Pierce");
      tabs.add(panelDoublePierce, "Double Pierce");
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
        if (tabs.getSelectedIndex() == 0) {
          FishEyeLens.FOCUS_RADIUS = (Double) spinFishFocus.getValue();
          FishEyeLens.TRANSITION_RADIUS = (Double) spinFishTrans.getValue();
          if (mFishEye.isSelected()) {
            LayerViewPanel layerViewPanel = application.getMainFrame()
                .getSelectedProjectFrame().getLayerViewPanel();
            for (PaintListener listener : layerViewPanel.getOverlayListeners()) {
              if (listener instanceof LensPaintListener) {
                if (((LensPaintListener) listener).getLens() instanceof FishEyeLens) {
                  layerViewPanel.getOverlayListeners().remove(listener);
                }
                break;
              }
            }
            LensPaintListener listener = new LensPaintListener(
                LensesPlugin.this.application.getMainFrame(),
                LensesPlugin.this.application.getMainFrame().getMode(),
                mFishEye);
            listener.setLens(LensPaintListener.FISHEYE_LENS);
            layerViewPanel.getOverlayListeners().add(listener);
            LensesPlugin.this.application.getMainFrame().getMode()
                .setCurrentMode(listener);
          }
        } else if (tabs.getSelectedIndex() == 1) {
          PierceLens.FOCUS_RADIUS = (Double) spinPierceFocus.getValue();
          PierceLens.FOCUS_LAYER_NAME = (String) txtPierceLayer.getText();
          LayerViewPanel layerViewPanel = application.getMainFrame()
              .getSelectedProjectFrame().getLayerViewPanel();
          if (mPierce.isSelected()) {
            for (PaintListener listener : layerViewPanel.getOverlayListeners()) {
              if (listener instanceof LensPaintListener) {
                if (((LensPaintListener) listener).getLens() instanceof PierceLens) {
                  layerViewPanel.getOverlayListeners().remove(listener);
                }
                break;
              }
            }
            LensPaintListener listener = new LensPaintListener(
                LensesPlugin.this.application.getMainFrame(),
                LensesPlugin.this.application.getMainFrame().getMode(), mPierce);
            listener.setLens(LensPaintListener.PIERCE_LENS);
            layerViewPanel.getOverlayListeners().add(listener);
            LensesPlugin.this.application.getMainFrame().getMode()
                .setCurrentMode(listener);
          }
        } else if (tabs.getSelectedIndex() == 2) {
          FuzzyPierceLens.FOCUS_RADIUS = (Double) spinFuzzyPierceFocus
              .getValue();
          FuzzyPierceLens.FOCUS_LAYER_NAME = (String) txtFuzzyPierceLayer
              .getText();
          FuzzyPierceLens.TRANSITION_RADIUS = (Double) spinFuzzyPierceTrans
              .getValue();
          LayerViewPanel layerViewPanel = application.getMainFrame()
              .getSelectedProjectFrame().getLayerViewPanel();
          if (mFuzzyPierce.isSelected()) {
            for (PaintListener listener : layerViewPanel.getOverlayListeners()) {
              if (listener instanceof LensPaintListener) {
                if (((LensPaintListener) listener).getLens() instanceof FuzzyPierceLens) {
                  layerViewPanel.getOverlayListeners().remove(listener);
                }
                break;
              }
            }
            LensPaintListener listener = new LensPaintListener(
                LensesPlugin.this.application.getMainFrame(),
                LensesPlugin.this.application.getMainFrame().getMode(),
                mFuzzyPierce);
            listener.setLens(LensPaintListener.FUZZY_PIERCE_LENS);
            layerViewPanel.getOverlayListeners().add(listener);
            LensesPlugin.this.application.getMainFrame().getMode()
                .setCurrentMode(listener);
          }
        } else if (tabs.getSelectedIndex() == 3) {
          DoublePierceLens.FOCUS_RADIUS = (Double) spinDoublePierceFocus
              .getValue();
          DoublePierceLens.FOCUS_LAYER_NAME = (String) txtDoublePierceFocusLayer
              .getText();
          DoublePierceLens.TRANSITION_RADIUS = (Double) spinDoublePierceTrans
              .getValue();
          DoublePierceLens.TRANSITION_LAYER_NAME = (String) txtDoublePierceTransLayer
              .getText();
          LayerViewPanel layerViewPanel = application.getMainFrame()
              .getSelectedProjectFrame().getLayerViewPanel();
          if (mDoublePierce.isSelected()) {
            for (PaintListener listener : layerViewPanel.getOverlayListeners()) {
              if (listener instanceof LensPaintListener) {
                if (((LensPaintListener) listener).getLens() instanceof DoublePierceLens)
                  layerViewPanel.getOverlayListeners().remove(listener);
                break;
              }
            }
            LensPaintListener listener = new LensPaintListener(
                LensesPlugin.this.application.getMainFrame(),
                LensesPlugin.this.application.getMainFrame().getMode(),
                mDoublePierce);
            listener.setLens(LensPaintListener.DOUBLE_PIERCE_LENS);
            layerViewPanel.getOverlayListeners().add(listener);
            LensesPlugin.this.application.getMainFrame().getMode()
                .setCurrentMode(listener);
          }
        }
        this.dispose();
      }
    }

  }

}

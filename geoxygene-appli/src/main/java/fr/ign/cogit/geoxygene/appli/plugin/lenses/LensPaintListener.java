package fr.ign.cogit.geoxygene.appli.plugin.lenses;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.JButton;

import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.event.PaintListener;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewAwtPanel;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanel;
import fr.ign.cogit.geoxygene.appli.mode.AbstractMode;
import fr.ign.cogit.geoxygene.appli.mode.MainFrameToolBar;

public class LensPaintListener extends AbstractMode implements PaintListener {

  private AbstractButton button = new JButton();

  public final static int FISHEYE_LENS = 0;
  public final static int JELLY_LENS = 1;
  public final static int PIERCE_LENS = 2;
  public final static int DOUBLE_PIERCE_LENS = 3;
  public final static int FUZZY_PIERCE_LENS = 4;

  public LensPaintListener(MainFrame theMainFrame,
      MainFrameToolBar theModeSelector, AbstractButton button) {
    super(theMainFrame, theModeSelector);
    this.button = button;
    this.button.setToolTipText(this.getToolTipText());
    @SuppressWarnings("unused")
    final LensPaintListener currentMode = this;
    // this.button.addActionListener(new ActionListener() {
    // @Override
    // public void actionPerformed(final ActionEvent e) {
    // // LensPaintListener.this.getModeSelector().setCurrentMode(currentMode);
    // }
    // });
  }

  private ILens lens;

  public ILens getLens() {
    return lens;
  }

  public void setLens(int type_lens) {
    switch (type_lens) {
      case FISHEYE_LENS:
        this.lens = new FishEyeLens();
        break;
      case PIERCE_LENS:
        this.lens = new PierceLens(this.mainFrame.getApplication());
        break;
      case FUZZY_PIERCE_LENS:
        this.lens = new FuzzyPierceLens(this.mainFrame.getApplication());
        break;
      case DOUBLE_PIERCE_LENS:
        this.lens = new DoublePierceLens(this.mainFrame.getApplication());
        break;
      default:
        this.lens = new FishEyeLens();
        break;
    }
  }

  @Override
  public void paint(LayerViewPanel layerViewPanel, Graphics graphics) {

    LayerViewAwtPanel newLayer = new LayerViewAwtPanel();
    newLayer.setEnabled(true);
    newLayer.setOpaque(true);
    newLayer.setVisible(true);
    newLayer.setProjectFrame(layerViewPanel.getProjectFrame());
    Viewport viewport = new Viewport(newLayer);
    viewport.setScale(layerViewPanel.getViewport().getScale() * 10);
    // newLayer.setViewport(layerViewPanel.getViewport());

    // g2.fillRect(100, 100, 500, 500);
    lens.setVisuPanel(layerViewPanel);
    newLayer.setLayout(null);

    newLayer.setBounds(layerViewPanel.getX(), layerViewPanel.getY(),
        layerViewPanel.getWidth(), layerViewPanel.getHeight());

    // newLayer.setSize(new Dimension());

    Color bg = layerViewPanel.getBackground();
    BufferedImage image = new BufferedImage(newLayer.getWidth(),
        newLayer.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = image.createGraphics();
    g2.setColor(bg);
    g2.fillRect(0, 0, newLayer.getWidth(), newLayer.getHeight());
    ((LayerViewAwtPanel) layerViewPanel).getRenderingManager().copyTo(g2);
    // newLayer.
    // newLayer.paintComponent(g2);
    // newLayer.getRenderingManager().copyTo(g2);

    lens.apply((Graphics2D) graphics, image);

    // JFrame frame = new JFrame();
    // frame.getContentPane().setLayout(new FlowLayout());
    // frame.getContentPane().add(new JLabel(new ImageIcon(image)));
    // frame.pack();
    // frame.setVisible(true);

  }

  @Override
  public void mouseMoved(final MouseEvent e) {
    ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
    frame.getLayerViewPanel().superRepaint();
  }

  @Override
  protected String getToolTipText() {
    return "";
  }

  @Override
  protected JButton createButton() {
    return new JButton();
  }

}

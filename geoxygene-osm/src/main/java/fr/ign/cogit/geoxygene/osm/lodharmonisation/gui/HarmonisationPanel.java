package fr.ign.cogit.geoxygene.osm.lodharmonisation.gui;

import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.geoxygene.osm.util.I18N;

public abstract class HarmonisationPanel extends JPanel {

  /****/
  private static final long serialVersionUID = 1L;
  protected JPanel lodPanel, detectionPanel, harmPanel;
  protected JSlider lodSlider;

  public HarmonisationPanel() {
    super();
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    // a panel to choose LoD difference parameter
    lodPanel = new JPanel();
    lodPanel.setLayout(new BoxLayout(lodPanel, BoxLayout.X_AXIS));
    lodPanel
        .add(new JLabel(I18N.getString("HarmonisationFrame.lodDifference")));
    lodSlider = new JSlider(0, 4, 2);
    lodSlider.setPaintTicks(true);
    lodSlider.setMajorTickSpacing(1);
    lodPanel.add(lodSlider);
    this.add(lodPanel);
    // a panel for detection parameters
    detectionPanel = new JPanel();
    detectionPanel.setBorder(BorderFactory.createTitledBorder(I18N
        .getString("HarmonisationFrame.detectionPanel")));
    detectionPanel.setLayout(new BoxLayout(detectionPanel, BoxLayout.X_AXIS));
    this.add(detectionPanel);
    this.add(Box.createVerticalGlue());
    // a panel for harmonisation parameters
    harmPanel = new JPanel();
    harmPanel.setBorder(BorderFactory.createTitledBorder(I18N
        .getString("HarmonisationFrame.harmPanel")));
    harmPanel.setLayout(new BoxLayout(harmPanel, BoxLayout.X_AXIS));
    this.add(harmPanel);
  }

  public abstract String getTabName();

  public abstract Set<IGeneObj> triggerHarmonisation(boolean window,
      Set<IGeneObj> windowObjs);
}

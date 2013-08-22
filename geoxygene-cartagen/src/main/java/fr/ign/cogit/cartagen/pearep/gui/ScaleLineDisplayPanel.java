package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.ScaleRulerPanel;
import fr.ign.cogit.cartagen.util.Interval;

public class ScaleLineDisplayPanel extends JPanel implements ActionListener {

  /****/
  private static final long serialVersionUID = 5869955598973293476L;

  /**
   * The Scale Line to be displayed in {@code this}.
   */
  private ScaleLine scaleLine;
  /**
   * The ruler panel on which the elements display is set.
   */
  private ScaleRulerPanel ruler;

  /**
   * The panels used to display the elements of the scale line.
   */
  private Map<Interval<Integer>, ScaleMasterElementPanel> eltPanels;
  private List<Interval<Integer>> intervals = new ArrayList<Interval<Integer>>();

  private EditScaleMasterFrame frame;

  private JToggleButton toggle;

  private String name;

  /**
   * Default constructor with a scale line, a color to display its elements and
   * the ruler to set the elements at the correct alignment.
   * @param color
   * @param scaleLine
   * @param ruler
   */
  public ScaleLineDisplayPanel(ScaleLine scaleLine, ScaleRulerPanel ruler,
      EditScaleMasterFrame frame) {
    super();
    this.scaleLine = scaleLine;
    this.ruler = ruler;
    this.frame = frame;
    this.setBackground(Color.YELLOW);
    this.setOpaque(false);
    this.eltPanels = new HashMap<Interval<Integer>, ScaleMasterElementPanel>();
    this.name = scaleLine.getTheme().getName();

    this.toggle = new JToggleButton(this.name);
    this.toggle.setActionCommand("toggle");
    this.toggle.addActionListener(this);
    this.setAlignmentX(Component.LEFT_ALIGNMENT);
    this.intervals.clear();
    for (Interval<Integer> interval : this.scaleLine.getLine().keySet()) {
      ScaleMasterElementPanel panel = new ScaleMasterElementPanel(
          this.scaleLine.getLine().get(interval), frame);
      this.eltPanels.put(interval, panel);
      this.intervals.add(interval);
    }
    this.adjustElementsToRuler();
    this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
  }

  /**
   * Adjust the panels corresponding to scale line elements to the ruler defined
   * for {@code this}.
   */
  void adjustElementsToRuler() {
    // first clear the components
    this.removeAll();
    this.add(this.toggle);
    int lastEltPix = this.toggle.getWidth();
    // then sort the list of intervals
    Collections.sort(this.intervals, new IntervalComparator());
    // loop on the elements to display them properly
    for (int i = 0; i < this.eltPanels.size(); i++) {
      Interval<Integer> interval = this.intervals.get(i);
      ScaleMasterElementPanel panel = this.eltPanels.get(interval);
      // get the pixels corresponding to the interval bounds
      int pixIni = this.ruler.getPixelAlign(interval.getMinimum());
      int pixFin = this.ruler.getPixelAlign(interval.getMaximum());

      // create an horizontal strut from last elt to pixIni
      this.add(Box.createHorizontalStrut(pixIni - lastEltPix));
      // add the panel
      panel.setPreferredSize(new Dimension(pixFin - pixIni,
          this.getHeight() - 2));
      panel
          .setMinimumSize(new Dimension(pixFin - pixIni, this.getHeight() - 2));
      panel
          .setMaximumSize(new Dimension(pixFin - pixIni, this.getHeight() - 2));
      Dimension labelSize = panel.getLabel().getPreferredSize();
      panel.getLabel().setPreferredSize(
          new Dimension((pixFin - pixIni) / 2, labelSize.height));
      this.add(panel);
      lastEltPix = pixFin;
    }
    this.add(Box.createHorizontalGlue());
  }

  public void setToggle(JToggleButton toggle) {
    this.toggle = toggle;
  }

  public JToggleButton getToggle() {
    return this.toggle;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  public Map<Interval<Integer>, ScaleMasterElementPanel> getEltPanels() {
    return eltPanels;
  }

  public void setEltPanels(
      Map<Interval<Integer>, ScaleMasterElementPanel> eltPanels) {
    this.eltPanels = eltPanels;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("toggle")) {
      if (this.toggle.isSelected()) {
        this.setOpaque(true);
        this.repaint();
        this.frame.resetAllToggles(this);
        this.frame.setSelectedLine(this.scaleLine);
        this.frame.getBtnUp().setEnabled(true);
        this.frame.getBtnDown().setEnabled(true);
        this.frame.getBtnTop().setEnabled(true);
        this.frame.getBtnBottom().setEnabled(true);
      } else {
        this.setOpaque(false);
        this.repaint();
        this.frame.setSelectedLine(null);
        this.frame.getBtnUp().setEnabled(false);
        this.frame.getBtnDown().setEnabled(false);
        this.frame.getBtnTop().setEnabled(false);
        this.frame.getBtnBottom().setEnabled(false);
      }
      this.frame.validate();
    }
  }

  public void updateElements() {
    this.eltPanels.clear();
    this.intervals.clear();
    for (Interval<Integer> interval : this.scaleLine.getLine().keySet()) {
      ScaleMasterElementPanel panel = new ScaleMasterElementPanel(
          this.scaleLine.getLine().get(interval), this.frame);
      this.eltPanels.put(interval, panel);
      this.intervals.add(interval);
    }
    this.adjustElementsToRuler();
  }
}

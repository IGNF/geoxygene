package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMaster;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement.ProcessPriority;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.RangeSlider;
import fr.ign.cogit.cartagen.util.Interval;
import fr.ign.cogit.geoxygene.filter.Filter;

public class AddScaleMasterEltFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 4360672319562183279L;
  private EditScaleMasterFrame parent;
  private ScaleLine line;
  private RangeSlider rangeSlider;
  private JButton btnOk, btnCancel;
  private boolean largeScales = true;
  private JButton switchScales, btnPlus, btnMinus;
  private JComboBox comboDbs, comboClasses;
  private JList jlistClasses;
  private Set<Class<? extends IGeneObj>> classes;
  private Filter filter;
  private JSlider slideFilter;

  // internationalisation labels
  private String frameTitle, lblOk, lblCancel, lblInterval, lblLargeScales,
      lblSmallScales;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("ok")) {
      Interval<Integer> interval = new Interval<Integer>(this.rangeSlider
          .getValue(), this.rangeSlider.getUpperValue());
      ScaleMasterElement element = new ScaleMasterElement(this.line, interval,
          (String) this.comboDbs.getSelectedItem());
      // add the filter to the element
      if (this.filter != null) {
        element.setOgcFilter(this.filter);
        element.setFilterPriority(ProcessPriority.values()[this.slideFilter
            .getValue()]);
      }
      // TODO build element from swing components (add the enrichments and the
      // processes)
      this.line.addElement(element);
      ScaleLineDisplayPanel panel = this.parent.getLinePanels().get(
          this.line.getTheme().getName());
      panel.updateElements();
      this.parent.pack();
      this.setVisible(false);
    } else if (e.getActionCommand().equals("cancel")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("switch")) {
      String label = this.lblSmallScales;
      if (this.largeScales) {
        label = this.lblLargeScales;
        this.largeScales = false;
      } else {
        this.largeScales = true;
      }
      this.switchScales.setText(label);
      this.defineRangeSliderScales(this.line.getScaleMaster());
      this.pack();
    } else if (e.getActionCommand().equals("plus")) {
      // TODO
    } else if (e.getActionCommand().equals("minus")) {
      this.classes.remove(this.jlistClasses.getSelectedValue());
      this.updateJList();
    }
  }

  public AddScaleMasterEltFrame(EditScaleMasterFrame frame, ScaleLine line) {
    super();
    this.internationalisation();
    this.setTitle(this.frameTitle + line.getTheme().getName());
    this.parent = frame;
    this.line = line;

    // a panel for the definition of the interval
    JPanel pInterval = new JPanel();
    this.rangeSlider = new RangeSlider();
    this.rangeSlider.setPaintTicks(true);
    this.rangeSlider.setPaintLabels(true);
    this.defineRangeSliderScales(line.getScaleMaster());
    this.switchScales = new JButton(this.lblLargeScales);
    this.switchScales.addActionListener(this);
    this.switchScales.setActionCommand("switch");
    pInterval.add(new JLabel(this.lblInterval));
    pInterval.add(this.rangeSlider);
    pInterval.add(this.switchScales);
    pInterval.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pInterval.setLayout(new BoxLayout(pInterval, BoxLayout.X_AXIS));

    // a panel for the definition of the used DB and the classes
    JPanel pData = new JPanel();
    this.comboDbs = new JComboBox(CartAGenDoc.getInstance().getDatabases()
        .keySet().toArray());
    this.comboClasses = new JComboBox(this.parent.getGeoClassesComboModel());
    // TODO the JList
    this.btnPlus = new JButton("+");
    this.btnPlus.addActionListener(this);
    this.btnPlus.setActionCommand("plus");
    this.btnMinus = new JButton("-");
    this.btnMinus.addActionListener(this);
    this.btnMinus.setActionCommand("minus");
    pData.add(new JLabel("Dataset: "));
    pData.add(this.comboDbs);
    pData.add(this.comboClasses);
    pData.add(this.jlistClasses);
    pData.add(this.btnPlus);
    pData.add(this.btnMinus);
    pData.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pData.setLayout(new BoxLayout(pData, BoxLayout.X_AXIS));

    // TODO the panels for enrichments, the filter and the processes

    // a panel for the buttons
    JPanel pButtons = new JPanel();
    this.btnOk = new JButton(this.lblOk);
    this.btnOk.addActionListener(this);
    this.btnOk.setActionCommand("ok");
    this.btnCancel = new JButton(this.lblCancel);
    this.btnCancel.addActionListener(this);
    this.btnCancel.setActionCommand("cancel");
    pButtons.add(this.btnOk);
    pButtons.add(this.btnCancel);
    pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

    this.getContentPane().add(pInterval);
    this.getContentPane().add(pData);
    this.getContentPane().add(pButtons);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  private void defineRangeSliderScales(ScaleMaster scaleMaster) {
    this.rangeSlider.setPreferredSize(new Dimension(240, this.rangeSlider
        .getPreferredSize().height));
    if (this.largeScales) {
      this.rangeSlider.setMinimum(0);
      this.rangeSlider.setMaximum(250000);
      this.rangeSlider.setMinorTickSpacing(5000);
      this.rangeSlider.setMajorTickSpacing(50000);
      this.rangeSlider.setValue(25000);
      this.rangeSlider.setUpperValue(50000);
      Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
      for (int i = 0; i <= 500000; i += 50000) {
        if (i < scaleMaster.getGlobalRange().minimum()) {
          continue;
        }
        if (i > scaleMaster.getGlobalRange().maximum()) {
          continue;
        }
        labelTable.put(i, new JLabel(String.valueOf(i).substring(0,
            String.valueOf(i).length() - 3)
            + "k"));
      }
      this.rangeSlider.setLabelTable(labelTable);
    } else {

    }
  }

  /**
   * Update the {@link JList} jlistClasses content with the current content of
   * the classes collection.
   */
  private void updateJList() {
    DefaultListModel model = new DefaultListModel();
    for (Class<? extends IGeneObj> classObj : this.classes) {
      model.addElement(classObj);
    }
    this.jlistClasses.setModel(model);
    this.parent.pack();
  }

  /**
   * Internationalise the labels of the frame, reading the values stored in the
   * properties files.
   */
  private void internationalisation() {
    this.frameTitle = I18N.getString("AddScaleMasterEltFrame.frameTitle");
    this.lblInterval = I18N.getString("AddScaleMasterEltFrame.lblInterval");
    this.lblCancel = I18N.getString("MainLabels.lblCancel");
    this.lblOk = I18N.getString("MainLabels.lblOk");
    // TODO
  }
}

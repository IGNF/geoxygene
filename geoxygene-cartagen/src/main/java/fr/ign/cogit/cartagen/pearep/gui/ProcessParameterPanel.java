package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.IntegerLimitator;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.RealLimitator;

public class ProcessParameterPanel extends JPanel {

  /**
   * 
   */
  private static final long serialVersionUID = 7205215860759774117L;

  private Map<String, JPanel> parameterPanels;

  public ProcessParameterPanel() {
    super();
    this.parameterPanels = new HashMap<String, JPanel>();
  }

  public void clear() {
    this.removeAll();
    this.parameterPanels.clear();
  }

  /**
   * Get the value filled in the panel for a given parameter.
   * @param param
   * @return
   */
  public Object getValue(ProcessParameter param) {
    JPanel panel = this.parameterPanels.get(param.getName());
    if (param.getType().equals(Boolean.class)) {
      return ((JCheckBox) panel.getComponent(0)).isSelected();
    } else if (param.getType().equals(Double.class)) {
      // get the text field
      JTextField txtField = (JTextField) panel.getComponent(1);
      return new Double(txtField.getText());
    } else if (param.getType().equals(Integer.class)) {
      // get the text field
      JTextField txtField = (JTextField) panel.getComponent(1);
      return new Integer(txtField.getText());
    } else if (param.getType().equals(String.class)) {
      // get the text field
      JTextField txtField = (JTextField) panel.getComponent(1);
      return txtField.getText();
    }
    return null;
  }

  public void update(Set<ProcessParameter> params) {
    for (ProcessParameter param : params) {
      if (param.getType().equals(Integer.class))
        this.addIntegerPanel(param);
      else if (param.getType().equals(Double.class))
        this.addDoublePanel(param);
      else if (param.getType().equals(String.class))
        this.addStringPanel(param);
      else if (param.getType().equals(Boolean.class))
        this.addBooleanPanel(param);
    }
  }

  private void addBooleanPanel(ProcessParameter param) {
    JPanel panel = new JPanel();
    JCheckBox chkBox = new JCheckBox(param.getName() + " = ");
    chkBox.setSelected((Boolean) param.getValue());
    panel.add(chkBox);
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    this.parameterPanels.put(param.getName(), panel);
    this.add(panel);
  }

  private void addStringPanel(ProcessParameter param) {
    JPanel panel = new JPanel();
    JTextField txtField = new JTextField();
    txtField.setPreferredSize(new Dimension(100, 20));
    txtField.setMaximumSize(new Dimension(100, 20));
    txtField.setMinimumSize(new Dimension(100, 20));
    txtField.setText((String) param.getValue());
    panel.add(new JLabel(param.getName() + " = "));
    panel.add(txtField);
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    this.parameterPanels.put(param.getName(), panel);
    this.add(panel);
  }

  private void addDoublePanel(ProcessParameter param) {
    JPanel panel = new JPanel();
    JTextField txtField = new JTextField();
    txtField.setPreferredSize(new Dimension(100, 20));
    txtField.setMaximumSize(new Dimension(100, 20));
    txtField.setMinimumSize(new Dimension(100, 20));
    txtField.setDocument(new RealLimitator());
    txtField.setText(String.valueOf(((Double) param.getValue()).doubleValue()));
    panel.add(new JLabel(param.getName() + " = "));
    panel.add(txtField);
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    this.parameterPanels.put(param.getName(), panel);
    this.add(panel);
  }

  private void addIntegerPanel(ProcessParameter param) {
    JPanel panel = new JPanel();
    JTextField txtField = new JTextField();
    txtField.setPreferredSize(new Dimension(100, 20));
    txtField.setMaximumSize(new Dimension(100, 20));
    txtField.setMinimumSize(new Dimension(100, 20));
    txtField.setDocument(new IntegerLimitator());
    txtField.setText(String.valueOf(((Integer) param.getValue()).intValue()));
    panel.add(new JLabel(param.getName() + " = "));
    panel.add(txtField);
    panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
    this.parameterPanels.put(param.getName(), panel);
    this.add(panel);
  }
}

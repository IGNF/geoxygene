package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.batik.ext.swing.GridBagConstants;

import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;

public class AddScaleLineFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 5062507282487229799L;
  private JButton btnOk, btnCancel;
  private JComboBox cbThemes;
  private JTextField txtTheme;
  private JList listClasses;
  private EditScaleMasterFrame frame;

  // internationalisation labels
  private String frameTitle, lblOk, lblCancel, lblThemes;

  public AddScaleLineFrame(EditScaleMasterFrame frame) {
    super();
    internationalisation();
    this.setTitle(frameTitle);
    this.frame = frame;

    // a panel to define a new line
    JPanel pLine = new JPanel();
    List<ScaleMasterTheme> listThemes = new ArrayList<ScaleMasterTheme>(
        frame.getExistingThemes());
    Collections.sort(listThemes);
    cbThemes = new JComboBox(listThemes.toArray());
    cbThemes.setPreferredSize(new Dimension(120, 20));
    cbThemes.setMaximumSize(new Dimension(120, 20));
    cbThemes.setMinimumSize(new Dimension(120, 20));
    pLine.setLayout(new GridBagLayout());
    pLine.add(new JLabel(this.lblThemes + " : "), new GridBagConstraints(0, 0,
        1, 1, 0.0, 0.0, GridBagConstants.WEST, GridBagConstants.NONE,
        new Insets(2, 2, 2, 2), 1, 1));
    pLine.add(cbThemes, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
        GridBagConstants.CENTER, GridBagConstants.HORIZONTAL, new Insets(2, 2,
            2, 2), 1, 1));

    // a panel for the buttons
    JPanel pButtons = new JPanel();
    btnOk = new JButton(lblOk);
    btnOk.addActionListener(this);
    btnOk.setActionCommand("ok");
    btnCancel = new JButton(lblCancel);
    btnCancel.addActionListener(this);
    btnCancel.setActionCommand("cancel");
    pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));
    pButtons.add(btnOk);
    pButtons.add(Box.createRigidArea(new Dimension(10, 0)));
    pButtons.add(btnCancel);

    this.getContentPane().add(pLine);
    this.getContentPane().add(pButtons);
    this.getContentPane().setLayout(
        new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    this.setMinimumSize(new Dimension(500, 120));
    this.setSize(500, 120);
    this.setResizable(false);
    this.setLocationRelativeTo(this.frame);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("ok")) {
      ScaleLine scaleLine = new ScaleLine(frame.getCurrent(),
          (ScaleMasterTheme) cbThemes.getSelectedItem());
      ScaleLineDisplayPanel linePanel = new ScaleLineDisplayPanel(scaleLine,
          frame.getRuler(), frame);
      frame.getLinePanels().put(scaleLine.getTheme().getName(), linePanel);
      JPanel jp = new JPanel(new FlowLayout(FlowLayout.LEFT));
      jp.add(linePanel);
      frame.getDisplayPanel().add(jp);
      frame.getDisplayPanel().validate();
      this.setVisible(false);
    } else if (e.getActionCommand().equals("cancel")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("add")) {
      frame.getExistingThemes().add(
          new ScaleMasterTheme(txtTheme.getText(), listClasses
              .getSelectedValues(), null));
      this.updateCombo();
      this.validate();
    }
  }

  /**
   * Update the combo box model according to the current existing themes of the
   * parent frame.
   */
  private void updateCombo() {
    DefaultComboBoxModel model = new DefaultComboBoxModel();
    for (ScaleMasterTheme theme : frame.getExistingThemes())
      model.addElement(theme);
    this.cbThemes.setModel(model);
  }

  private void internationalisation() {
    this.frameTitle = I18N.getString("AddScaleLineFrame.frameTitle");
    this.lblThemes = I18N.getString("AddScaleLineFrame.lblThemes");
    this.lblCancel = I18N.getString("MainLabels.lblCancel");
    this.lblOk = I18N.getString("MainLabels.lblOk");
    I18N.getString("MainLabels.lblAdd");
    I18N.getString("AddScaleLineFrame.lblThemeName");
    I18N.getString("AddScaleLineFrame.lblAddTheme");
  }
}

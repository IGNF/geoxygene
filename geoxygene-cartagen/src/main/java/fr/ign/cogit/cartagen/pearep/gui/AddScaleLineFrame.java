package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterTheme;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.renderer.ClassSimpleNameListRenderer;

public class AddScaleLineFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 5062507282487229799L;
  private JButton btnOk, btnCancel, btnAdd;
  private JComboBox cbThemes;
  private JTextField txtTheme;
  private JList listClasses;
  private EditScaleMasterFrame frame;

  // internationalisation labels
  private String frameTitle, lblOk, lblCancel, lblThemes, lblAdd, lblThemeName,
      lblAddTheme;

  public AddScaleLineFrame(EditScaleMasterFrame frame) {
    super();
    internationalisation();
    this.setTitle(frameTitle);
    this.frame = frame;

    // a panel to define a new line
    JPanel pLine = new JPanel();
    cbThemes = new JComboBox(frame.getExistingThemes().toArray());
    cbThemes.setPreferredSize(new Dimension(120, 20));
    cbThemes.setMaximumSize(new Dimension(120, 20));
    cbThemes.setMinimumSize(new Dimension(120, 20));
    pLine.add(Box.createHorizontalGlue());
    pLine.add(new JLabel(this.lblThemes + " : "));
    pLine.add(cbThemes);
    pLine.add(Box.createHorizontalGlue());
    pLine.setLayout(new BoxLayout(pLine, BoxLayout.X_AXIS));

    // a panel to add a theme to existing themes
    JPanel pThemes = new JPanel();
    txtTheme = new JTextField();
    txtTheme.setPreferredSize(new Dimension(100, 20));
    txtTheme.setMaximumSize(new Dimension(100, 20));
    txtTheme.setMinimumSize(new Dimension(100, 20));
    listClasses = new JList(frame.getGeoClassesModel());
    listClasses
        .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    listClasses.setPreferredSize(new Dimension(120, 1000));
    listClasses.setMaximumSize(new Dimension(120, 1000));
    listClasses.setMinimumSize(new Dimension(120, 1000));
    listClasses.setCellRenderer(new ClassSimpleNameListRenderer());
    JScrollPane scroll = new JScrollPane(listClasses);
    scroll.setPreferredSize(new Dimension(120, 120));
    scroll.setMaximumSize(new Dimension(120, 120));
    scroll.setMinimumSize(new Dimension(120, 120));
    btnAdd = new JButton(lblAdd);
    btnAdd.addActionListener(this);
    btnAdd.setActionCommand("add");
    pThemes.add(Box.createHorizontalGlue());
    pThemes.add(new JLabel(this.lblThemeName + " : "));
    pThemes.add(txtTheme);
    pThemes.add(Box.createHorizontalGlue());
    pThemes.add(scroll);
    pThemes.add(Box.createHorizontalGlue());
    pThemes.add(btnAdd);
    pThemes.add(Box.createHorizontalGlue());
    pThemes.setBorder(BorderFactory.createTitledBorder(lblAddTheme));
    pThemes.setLayout(new BoxLayout(pThemes, BoxLayout.X_AXIS));

    // a panel for the buttons
    JPanel pButtons = new JPanel();
    btnOk = new JButton(lblOk);
    btnOk.addActionListener(this);
    btnOk.setActionCommand("ok");
    btnCancel = new JButton(lblCancel);
    btnCancel.addActionListener(this);
    btnCancel.setActionCommand("cancel");
    pButtons.add(btnOk);
    pButtons.add(btnCancel);
    pButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

    this.getContentPane().add(pLine);
    this.getContentPane().add(pThemes);
    this.getContentPane().add(pButtons);
    this.getContentPane().setLayout(
        new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
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
      frame.pack();
      this.setVisible(false);
    } else if (e.getActionCommand().equals("cancel")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("add")) {
      frame.getExistingThemes().add(
          new ScaleMasterTheme(txtTheme.getText(), listClasses
              .getSelectedValues(), null));
      this.updateCombo();
      this.pack();
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
    this.lblAdd = I18N.getString("MainLabels.lblAdd");
    this.lblThemeName = I18N.getString("AddScaleLineFrame.lblThemeName");
    this.lblAddTheme = I18N.getString("AddScaleLineFrame.lblAddTheme");
  }
}

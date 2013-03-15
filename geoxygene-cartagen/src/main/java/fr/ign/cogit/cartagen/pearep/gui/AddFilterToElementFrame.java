package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.OGCFilterPanel;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.renderer.ClassSimpleNameListRenderer;

public class AddFilterToElementFrame extends JFrame implements ActionListener,
    ListSelectionListener {

  /****/
  private static final long serialVersionUID = 1L;
  private OGCFilterPanel filterPanel;
  private AddScaleMasterEltFrame parentFrame;
  private HashMap<Integer, JPanel> mapLevelPanel = new HashMap<Integer, JPanel>();
  private JPanel displayedPanel;
  private JList classJList;
  private JPanel pClass;
  private JButton btnBack, btnNext;
  private int level = 0;
  private final static int LEVELS = 2;

  public AddFilterToElementFrame(AddScaleMasterEltFrame frame) {
    super();
    this.setTitle(I18N.getString("AddScaleMasterEltFrame.filterFrameTitle"));
    this.parentFrame = frame;

    // ***********************************
    // a panel to define the queried class
    this.pClass = new JPanel();
    DefaultListModel model = new DefaultListModel();
    for (Class<?> classObj : this.parentFrame.getClasses()) {
      model.addElement(classObj);
    }
    this.classJList = new JList(model);
    this.classJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    this.classJList.setCellRenderer(new ClassSimpleNameListRenderer());
    this.classJList.setPreferredSize(new Dimension(150, 800));
    this.classJList.setMaximumSize(new Dimension(150, 800));
    this.classJList.setMinimumSize(new Dimension(150, 800));
    this.classJList.setSelectedIndex(0);
    this.classJList.addListSelectionListener(this);
    this.pClass.add(new JLabel(I18N.getString("AttributeQueryFrame.lblClasses")
        + " : "));
    this.pClass.add(new JScrollPane(this.classJList));
    Border titled = BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
        I18N.getString("AttributeQueryFrame.classTitle"));
    this.pClass.setBorder(BorderFactory.createCompoundBorder(titled,
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    this.pClass.setLayout(new BoxLayout(this.pClass, BoxLayout.X_AXIS));

    // ***********************************
    // a panel to define the OGC Filter
    this.filterPanel = new OGCFilterPanel(this,
        (Class<?>) this.classJList.getSelectedValue());

    // ***********************************
    // a panel for the buttons
    JPanel pButtons = new JPanel();
    this.btnBack = new JButton(I18N.getString("MainLabels.lblBack"));
    this.btnBack.addActionListener(this);
    this.btnBack.setActionCommand("Back");
    this.btnBack.setPreferredSize(new Dimension(100, 40));
    this.btnBack.setEnabled(false);
    this.btnNext = new JButton(I18N.getString("MainLabels.lblNext"));
    this.btnNext.addActionListener(this);
    this.btnNext.setActionCommand("Next");
    this.btnNext.setPreferredSize(new Dimension(100, 40));
    JButton btnCancel = new JButton(I18N.getString("MainLabels.lblCancel"));
    btnCancel.addActionListener(this);
    btnCancel.setActionCommand("Cancel");
    btnCancel.setPreferredSize(new Dimension(100, 40));
    JButton btnQuery = new JButton("OK");
    btnQuery.addActionListener(this);
    btnQuery.setActionCommand("Ok");
    btnQuery.setPreferredSize(new Dimension(100, 40));
    pButtons.add(this.btnBack);
    pButtons.add(this.btnNext);
    pButtons.add(btnQuery);
    pButtons.add(btnCancel);
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

    // ***********************************
    // final layout of the frame
    this.mapLevelPanel.put(0, this.pClass);
    this.mapLevelPanel.put(1, this.filterPanel);
    this.displayedPanel = this.pClass;
    this.getContentPane().add(this.pClass);
    this.getContentPane().add(pButtons);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("Cancel")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("Ok")) {
      this.parentFrame.setFilter(filterPanel.getFilter());
      this.parentFrame.getFilterTxt().setEditable(true);
      this.parentFrame.getFilterTxt().setText(
          filterPanel.getFilter().toString());
      this.parentFrame.getFilterTxt().setEditable(false);
      this.setVisible(false);
    } else if (e.getActionCommand().equals("Back")) {
      this.level--;
      // first remove the displayed panel
      this.getContentPane().remove(this.displayedPanel);
      // change the displayed panel
      this.displayedPanel = this.mapLevelPanel.get(this.level);
      this.getContentPane().add(this.displayedPanel, 0);
      if (this.level == 0) {
        this.btnBack.setEnabled(false);
      }
      if (!this.btnNext.isEnabled()) {
        this.btnNext.setEnabled(true);
      }
      this.pack();
    } else if (e.getActionCommand().equals("Next")) {
      this.level++;
      // first remove the displayed panel
      this.getContentPane().remove(this.displayedPanel);
      // change the displayed panel
      this.displayedPanel = this.mapLevelPanel.get(this.level);
      this.getContentPane().add(this.displayedPanel, 0);
      if (this.level == LEVELS - 1) {
        this.btnNext.setEnabled(false);
      }
      if (!this.btnBack.isEnabled()) {
        this.btnBack.setEnabled(true);
      }
      this.pack();
    }
  }

  public OGCFilterPanel getFilterPanel() {
    return filterPanel;
  }

  public void setFilterPanel(OGCFilterPanel filterPanel) {
    this.filterPanel = filterPanel;
  }

  public AddScaleMasterEltFrame getParentFrame() {
    return parentFrame;
  }

  public void setParentFrame(AddScaleMasterEltFrame parentFrame) {
    this.parentFrame = parentFrame;
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getSource().equals(this.classJList)) {
      if (!e.getValueIsAdjusting()) {
        this.filterPanel.changeSelectedClass((Class<?>) this.classJList
            .getSelectedValue());
      }
    }
  }

}

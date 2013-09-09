package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import fr.ign.cogit.cartagen.software.dataset.GeneObjImplementation;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.OGCFilterPanel;

public class AddFilterToElementFrame extends JFrame implements ActionListener {

  /****/
  private static final long serialVersionUID = 1L;
  private OGCFilterPanel filterPanel;
  private AddScaleMasterEltFrame parentFrame;
  private HashMap<Integer, JPanel> mapLevelPanel = new HashMap<Integer, JPanel>();

  public AddFilterToElementFrame(AddScaleMasterEltFrame frame) {
    super();
    this.setTitle(I18N.getString("AddScaleMasterEltFrame.filterFrameTitle"));
    this.parentFrame = frame;

    // ***********************************
    // a panel to define the OGC Filter
    Class<?> selectedClass = null;
    for (Class<?> classObj : this.parentFrame.getLine().getTheme()
        .getRelatedClasses()) {
      SourceDLM source = SourceDLM.valueOf((String) parentFrame.getComboDbs()
          .getSelectedItem());
      GeneObjImplementation impl = parentFrame.getParent().getImplFromName(
          source.name());
      if (impl.containsClass(classObj)) {
        selectedClass = classObj;
        break;
      }
    }

    this.filterPanel = new OGCFilterPanel(this, selectedClass);
    ItemListener listener = new ItemListener() {
      @Override
      public void itemStateChanged(ItemEvent e) {
        if (((JCheckBox) e.getSource()).isSelected()) {
          parentFrame.setFilter(filterPanel.getFilter());
          parentFrame.getFilterTxt().setEditable(true);
          parentFrame.getFilterTxt()
              .setText(filterPanel.getFilter().toString());
          parentFrame.getFilterTxt().setEditable(false);
          setVisible(false);
        }
      }
    };
    this.filterPanel.getChkCreated().addItemListener(listener);
    // ***********************************
    // a panel for the buttons
    JPanel pButtons = new JPanel();
    JButton btnCancel = new JButton(I18N.getString("MainLabels.lblCancel"));
    btnCancel.addActionListener(this);
    btnCancel.setActionCommand("Cancel");
    btnCancel.setPreferredSize(new Dimension(100, 40));
    JButton btnQuery = new JButton("OK");
    btnQuery.addActionListener(this);
    btnQuery.setActionCommand("Ok");
    btnQuery.setPreferredSize(new Dimension(100, 40));
    pButtons.add(btnQuery);
    pButtons.add(btnCancel);
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

    // ***********************************
    // final layout of the frame
    this.mapLevelPanel.put(0, this.filterPanel);
    this.getContentPane().add(this.filterPanel);
    this.getContentPane().add(pButtons);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.setSize(700, 230);
    this.setLocationRelativeTo(this.parentFrame);
    this.validate();
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

}

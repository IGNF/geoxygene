/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.MutableComboBoxModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.geoxygene.filter.And;
import fr.ign.cogit.geoxygene.filter.BinaryComparisonOpsType;
import fr.ign.cogit.geoxygene.filter.BinaryLogicOpsType;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.Not;
import fr.ign.cogit.geoxygene.filter.Or;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThan;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThan;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsLike;
import fr.ign.cogit.geoxygene.filter.PropertyIsNotEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsNull;
import fr.ign.cogit.geoxygene.filter.UnaryLogicOpsType;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;
import fr.ign.cogit.geoxygene.util.ReflectionUtil;

/**
 * A GUI to manually create OGC Filters.
 * @author GTouya, Florence Curie
 * 
 */
public class OGCFilterPanel extends JPanel implements ActionListener,
    ListSelectionListener, ItemListener {

  /**
   * 
   */
  private static final long serialVersionUID = 1811757108743258915L;

  private Filter filter;
  private Class<?> filteredClass;
  private List<Method> methodsList;
  private List<Filter> unitaryList;
  private Filter operator = null;
  private List<Filter> filterOpList = new ArrayList<Filter>();
  private boolean show = false;

  // Swing components
  private Window parent;
  private JButton btnCreateUnitary, btnDelete, btnAddToFilter, btnNew;
  private JButton btnCreateCompound, btnAnd, btnOr, btnNot;
  private JButton btnAddUnit, btnDeleteUnit, btnModify, btnShow;
  private JList jlist;
  private JComboBox cbMethods, cbOps;
  private JTextField txtValue;
  private JTextArea txtArea;
  private JPanel pCompound;
  private String[] typeOp = { "==", ">", ">=", "<", "<=", "<>", "Null", "Like" };
  private JCheckBox chkCreated = new JCheckBox();

  // internationalisation
  private String borderTitle, simpleTitle, compoundTitle;
  private String lblOps, lblValue, lblMethods, lblDelete, lblCompoundFilter;
  private String lblCreateUnitary, lblCreateCompound, lblAddToFilter, lblNew;
  private String lblUnitList, lblAddUnit, lblModify;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("createU")) {
      this.createFilter(true);
      chkCreated.setSelected(true);
    } else if (e.getActionCommand().equals("createC")) {
      this.createFilter(false);
      chkCreated.setSelected(true);
    } else if (e.getActionCommand().equals("add")) {
      if (this.operator instanceof BinaryLogicOpsType) {
        if (this.filterOpList.size() < 2) {
          this.filterOpList.add(this.unitaryList.get(this.jlist
              .getSelectedIndex()));
        }
        if (this.filterOpList.size() == 2) {
          this.btnAddToFilter.setEnabled(false);
          this.btnNew.setEnabled(true);
        }
      } else if (this.operator instanceof UnaryLogicOpsType) {
        if (this.filterOpList.size() < 1) {
          this.filterOpList.add(this.unitaryList.get(this.jlist
              .getSelectedIndex()));
        }
        if (this.filterOpList.size() == 1) {
          this.btnAddToFilter.setEnabled(false);
          this.btnNew.setEnabled(true);
        }
      } else {
        if (!this.jlist.isSelectionEmpty()) {
          this.filterOpList.add(this.unitaryList.get(this.jlist
              .getSelectedIndex()));
          this.btnAddToFilter.setEnabled(false);
          this.btnAnd.setEnabled(false);
          this.btnNot.setEnabled(false);
          this.btnOr.setEnabled(false);
        }
      }
      String strAff = this.makeString();
      this.txtArea.setText(strAff);
    } else if (e.getActionCommand().equals("and")) {
      this.operator = new And();
      String strAff = this.makeString();
      this.txtArea.setText(strAff);
      // Gestion des boutons
      this.btnAnd.setEnabled(false);
      this.btnOr.setEnabled(false);
      this.btnNot.setEnabled(false);
      if (this.unitaryList.size() > 0) {
        this.btnAddToFilter.setEnabled(true);
      }
    } else if (e.getActionCommand().equals("or")) {
      this.operator = new Or();
      String strAff = this.makeString();
      this.txtArea.setText(strAff);
      // Gestion des boutons
      this.btnAnd.setEnabled(false);
      this.btnOr.setEnabled(false);
      this.btnNot.setEnabled(false);
      if (this.unitaryList.size() > 0) {
        this.btnAddToFilter.setEnabled(true);
      }
    } else if (e.getActionCommand().equals("not")) {
      this.operator = new Not();
      String strAff = this.makeString();
      this.txtArea.setText(strAff);
      // Gestion des boutons
      this.btnAnd.setEnabled(false);
      this.btnOr.setEnabled(false);
      this.btnNot.setEnabled(false);
      if (this.unitaryList.size() > 0) {
        this.btnAddToFilter.setEnabled(true);
      }
    } else if (e.getActionCommand().equals("new")) {
      if (this.operator instanceof BinaryLogicOpsType) {
        ((BinaryLogicOpsType) this.operator).getOps().addAll(this.filterOpList);
      } else if (this.operator instanceof UnaryLogicOpsType) {
        ((UnaryLogicOpsType) this.operator).setOp(this.filterOpList.get(0));
      }
      // Ajout de la précondition unitaire à la liste
      int index = this.unitaryList.size();
      this.unitaryList.add(this.operator);
      this.updateJList();
      // Selection du nouvel item et visibilité
      this.jlist.setSelectedIndex(index);
      this.jlist.ensureIndexIsVisible(index);
      if (this.unitaryList.size() > 0) {
        this.btnDeleteUnit.setEnabled(true);
        this.btnModify.setEnabled(true);
      }
      // On efface la précondition
      this.operator = null;
      this.filterOpList = new ArrayList<Filter>();
      this.txtArea.setText("");
      // Gestion des boutons
      this.btnNew.setEnabled(false);
      if ((this.unitaryList.size() == 1)) {
        this.btnAddToFilter.setEnabled(true);
        this.btnNot.setEnabled(true);
        this.btnAnd.setEnabled(false);
        this.btnOr.setEnabled(false);
      } else if (this.unitaryList.size() > 1) {
        this.btnAddToFilter.setEnabled(true);
        this.btnNot.setEnabled(true);
        this.btnAnd.setEnabled(true);
        this.btnOr.setEnabled(true);
      }
      this.parent.pack();
    } else if (e.getActionCommand().equals("delete")) {
      this.operator = null;
      this.filterOpList = new ArrayList<Filter>();
      // Gestion des boutons
      this.btnNew.setEnabled(false);
      this.txtArea.setText("");
      // Boutons opérateurs
      if ((this.unitaryList.size() == 1)) {
        this.btnAddToFilter.setEnabled(true);
        this.btnNot.setEnabled(true);
      } else if (this.unitaryList.size() > 1) {
        this.btnAddToFilter.setEnabled(true);
        this.btnNot.setEnabled(true);
        this.btnAnd.setEnabled(true);
        this.btnOr.setEnabled(true);
      }
    } else if (e.getActionCommand().equals("add_unit")) {
      Filter filter1 = this.getSimpleFilter();
      this.unitaryList.add(filter1);
      this.updateJList();
      // update the panel components
      if (this.unitaryList.size() > 0) {
        this.btnDeleteUnit.setEnabled(true);
        this.btnModify.setEnabled(true);
      }
      if ((this.unitaryList.size() == 1)
          && ((this.txtArea.getText() == "") || (this.txtArea.getText()
              .isEmpty()))) {
        this.btnAddToFilter.setEnabled(true);
        this.btnNot.setEnabled(true);
      } else if (this.unitaryList.size() > 1) {
        if ((this.txtArea.getText() == "")
            || (this.txtArea.getText().isEmpty())) {
          this.btnAddToFilter.setEnabled(true);
          this.btnNot.setEnabled(true);
          this.btnAnd.setEnabled(true);
          this.btnOr.setEnabled(true);
        } else if (this.operator != null) {
          if (this.operator instanceof BinaryLogicOpsType) {
            if (((BinaryLogicOpsType) this.operator).getOps().size() < 2) {
              this.btnAddToFilter.setEnabled(true);
            }
          } else if (this.operator instanceof UnaryLogicOpsType) {
            if (((UnaryLogicOpsType) this.operator).getOp() == null) {
              this.btnAddToFilter.setEnabled(true);
            }
          }
        }
      }
      this.parent.pack();
    } else if (e.getActionCommand().equals("delete_unit")) {
      int index = this.jlist.getSelectedIndex();
      // Suppresion de l'élément
      this.unitaryList.remove(index);
      this.updateJList();
      // Si la liste est vide on désactive le bouton supprimer
      if (this.unitaryList.size() == 0) {
        this.btnDeleteUnit.setEnabled(false);
        this.btnModify.setEnabled(false);
      } else {// select an element of the list
        if (index == this.unitaryList.size()) {
          index--;
        }
        this.jlist.setSelectedIndex(index);
        this.jlist.ensureIndexIsVisible(index);
      }
      this.parent.pack();
    } else if (e.getActionCommand().equals("modify")) {
      int index = this.jlist.getSelectedIndex();
      this.unitaryList.remove(index);
      this.updateJList();
      this.parent.pack();
    } else if (e.getActionCommand().equals("show")) {
      if (this.show) {
        this.show = false;
        this.btnShow.setText("+");
        this.remove(this.pCompound);
      } else {
        this.show = true;
        this.btnShow.setText("-");
        this.add(this.pCompound);
      }
      this.parent.pack();
    }
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  public Filter getFilter() {
    return this.filter;
  }

  public void setFilteredClass(Class<?> filteredClass) {
    this.filteredClass = filteredClass;
  }

  public Class<?> getFilteredClass() {
    return this.filteredClass;
  }

  /**
   * The method fills the filter field of the panel with the swing components.
   * @param unitary true if the filter is unitary
   */
  private void createFilter(boolean unitary) {
    if (unitary) {
      this.filter = this.getSimpleFilter();
    } else {
      ((BinaryLogicOpsType) this.operator).getOps().addAll(filterOpList);
      this.filter = this.operator;
    }
  }

  /**
   * Update the JList according to unitaryList.
   */
  private void updateJList() {
    DefaultListModel model = new DefaultListModel();
    for (Filter filter1 : this.unitaryList) {
      model.addElement(filter1);
    }
    this.jlist.setModel(model);
  }

  public OGCFilterPanel(Window parent, Class<?> filteredClass) {
    super();
    this.internationalisation();
    this.parent = parent;
    this.filteredClass = filteredClass;
    this.methodsList = new ArrayList<Method>();

    // *************************************
    // define the panel components
    // *************************************
    // a sub panel to define simple filters
    JPanel pSimple = new JPanel();
    // a combo box that contains the methods of the selected class
    Collection<Class<?>> types = new HashSet<Class<?>>();
    types.add(long.class);
    types.add(String.class);
    types.add(double.class);
    types.add(int.class);
    types.add(boolean.class);
    types.add(float.class);
    this.methodsList.addAll(ReflectionUtil.getAllGetters(filteredClass, types));
    List<String> listMethods = new ArrayList<String>();
    for (int i = 0; i < this.methodsList.size(); i++) {
      listMethods
          .add(this.methodsList.get(i).getName().replaceFirst("get", ""));
    }
    Collections.sort(listMethods);
    MutableComboBoxModel cbModel = new DefaultComboBoxModel(
        listMethods.toArray());
    this.cbMethods = new JComboBox(cbModel);
    this.cbMethods.setPreferredSize(new Dimension(90, 20));
    this.cbMethods.setMaximumSize(new Dimension(90, 20));
    this.cbMethods.setMinimumSize(new Dimension(90, 20));
    this.changeSelectedClass(filteredClass);
    // a combo box for possible filter operators
    this.cbOps = new JComboBox(this.typeOp);
    this.cbOps.setPreferredSize(new Dimension(60, 20));
    this.cbOps.setMaximumSize(new Dimension(60, 20));
    this.cbOps.setMinimumSize(new Dimension(60, 20));
    this.cbOps.addItemListener(this);
    // a text field
    this.txtValue = new JTextField();
    this.txtValue.setPreferredSize(new Dimension(90, 20));
    this.txtValue.setMaximumSize(new Dimension(90, 20));
    this.txtValue.setMinimumSize(new Dimension(90, 20));

    // a button to create a unitary filter
    this.btnCreateUnitary = new JButton(this.lblCreateUnitary);
    this.btnCreateUnitary.setActionCommand("createU");
    this.btnCreateUnitary.addActionListener(this);

    pSimple.add(Box.createHorizontalGlue());
    pSimple.add(new JLabel(this.lblMethods));
    pSimple.add(this.cbMethods);
    pSimple.add(Box.createHorizontalGlue());
    pSimple.add(new JLabel(this.lblOps));
    pSimple.add(this.cbOps);
    pSimple.add(Box.createHorizontalGlue());
    pSimple.add(new JLabel(this.lblValue));
    pSimple.add(this.txtValue);
    pSimple.add(Box.createHorizontalGlue());
    pSimple.add(this.btnCreateUnitary);

    pSimple.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.BLACK), this.simpleTitle));
    pSimple.setLayout(new BoxLayout(pSimple, BoxLayout.X_AXIS));

    // *************************************
    // a sub panel to define compound filters
    this.pCompound = new JPanel();
    // a left panel containing the stored unitary filters
    JPanel pLeft = new JPanel();
    this.unitaryList = new ArrayList<Filter>();
    this.jlist = new JList();
    this.jlist.addListSelectionListener(this);
    JScrollPane scroll = new JScrollPane(this.jlist);
    scroll.setPreferredSize(new Dimension(260, 220));
    // a horizontal panel with three buttons
    JPanel pButtons = new JPanel();
    this.btnAddUnit = new JButton(this.lblAddUnit);
    this.btnAddUnit.addActionListener(this);
    this.btnAddUnit.setActionCommand("add_unit");
    this.btnDeleteUnit = new JButton(this.lblDelete);
    this.btnDeleteUnit.addActionListener(this);
    this.btnDeleteUnit.setActionCommand("delete_unit");
    this.btnModify = new JButton(this.lblModify);
    this.btnModify.addActionListener(this);
    this.btnModify.setActionCommand("modify");
    pButtons.add(this.btnAddUnit);
    pButtons.add(this.btnDeleteUnit);
    pButtons.add(this.btnModify);
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));
    pLeft.add(new JLabel(this.lblUnitList));
    pLeft.add(scroll);
    pLeft.add(pButtons);
    pLeft.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pLeft.setLayout(new BoxLayout(pLeft, BoxLayout.Y_AXIS));

    // a center panel with logical buttons
    JPanel pCentre = new JPanel();
    JPanel pLogical = new JPanel();
    this.btnAnd = new JButton("And");
    this.btnAnd.addActionListener(this);
    this.btnAnd.setActionCommand("and");
    this.btnOr = new JButton("Or");
    this.btnOr.addActionListener(this);
    this.btnOr.setActionCommand("or");
    this.btnNot = new JButton("Not");
    this.btnNot.addActionListener(this);
    this.btnNot.setActionCommand("not");
    this.btnAnd.setEnabled(false);
    this.btnOr.setEnabled(false);
    pLogical.add(this.btnAnd);
    pLogical.add(Box.createRigidArea(new Dimension(5, 0)));
    pLogical.add(this.btnOr);
    pLogical.add(Box.createRigidArea(new Dimension(5, 0)));
    pLogical.add(this.btnNot);
    pLogical.setLayout(new BoxLayout(pLogical, BoxLayout.X_AXIS));
    this.btnAddToFilter = new JButton(this.lblAddToFilter);
    this.btnAddToFilter.addActionListener(this);
    this.btnAddToFilter.setActionCommand("add");
    this.btnAddToFilter.setAlignmentX(pCentre.getAlignmentX());
    this.btnNew = new JButton(this.lblNew);
    this.btnNew.addActionListener(this);
    this.btnNew.setActionCommand("new");
    this.btnNew.setAlignmentX(pCentre.getAlignmentX());
    this.btnCreateCompound = new JButton(this.lblCreateCompound);
    this.btnCreateCompound.setActionCommand("createC");
    this.btnCreateCompound.addActionListener(this);
    this.btnCreateCompound.setAlignmentX(pCentre.getAlignmentX());
    pCentre.add(pLogical);
    pCentre.add(Box.createVerticalStrut(10));
    pCentre.add(this.btnAddToFilter);
    pCentre.add(Box.createVerticalStrut(25));
    pCentre.add(this.btnNew);
    pCentre.add(Box.createVerticalStrut(25));
    pCentre.add(this.btnCreateCompound);
    pCentre.setLayout(new BoxLayout(pCentre, BoxLayout.Y_AXIS));

    // a right panel containing the built compound filter
    JPanel pRight = new JPanel();
    JPanel upRightPanel = new JPanel();
    upRightPanel.add(new JLabel(this.lblCompoundFilter));
    upRightPanel.add(Box.createHorizontalGlue());
    this.btnDelete = new JButton(this.lblDelete);
    this.btnDelete.addActionListener(this);
    this.btnDelete.setActionCommand("delete");
    upRightPanel.add(this.btnDelete);
    upRightPanel.setLayout(new BoxLayout(upRightPanel, BoxLayout.X_AXIS));
    // a text area to display the compound filter
    this.txtArea = new JTextArea();
    this.txtArea.setLineWrap(true);
    this.txtArea.setEditable(false);
    this.txtArea.setText("");
    JScrollPane scroll2 = new JScrollPane(this.txtArea);
    scroll2.setPreferredSize(new Dimension(260, 220));
    pRight.add(upRightPanel);
    pRight.add(Box.createVerticalStrut(10));
    pRight.add(scroll2);
    pRight.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pRight.setLayout(new BoxLayout(pRight, BoxLayout.Y_AXIS));
    // display the three panels
    this.pCompound.add(pLeft);
    this.pCompound.add(Box.createHorizontalGlue());
    this.pCompound.add(pCentre);
    this.pCompound.add(Box.createHorizontalGlue());
    this.pCompound.add(pRight);
    this.pCompound.setBorder(BorderFactory.createTitledBorder(
        BorderFactory.createLineBorder(Color.BLACK), this.compoundTitle));
    this.pCompound.setLayout(new BoxLayout(this.pCompound, BoxLayout.X_AXIS));

    // *************************************
    // a sub panel to allow to display/hide the compound filters
    JPanel pExtend = new JPanel();
    this.btnShow = new JButton("+");
    this.btnShow.addActionListener(this);
    this.btnShow.setActionCommand("show");
    pExtend.add(new JLabel(this.compoundTitle + " : "));
    pExtend.add(this.btnShow);
    pExtend.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
    pExtend.setLayout(new BoxLayout(pExtend, BoxLayout.X_AXIS));

    // define a specific border to the panel
    Border titled = BorderFactory.createTitledBorder(
        BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
        this.borderTitle);
    this.setBorder(BorderFactory.createCompoundBorder(titled,
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));

    // define the panel layout
    this.add(pSimple);
    this.add(Box.createVerticalGlue());
    this.add(pExtend);
    this.add(Box.createVerticalGlue());
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.parent.pack();
  }

  /**
   * Update the methods list stored in {@code this} according to the current
   * selected class in the cbClasses combo box.
   */
  public void changeSelectedClass(Class<?> selectedClass) {
    this.filteredClass = selectedClass;
    this.methodsList.clear();
    Collection<Class<?>> types = new HashSet<Class<?>>();
    types.add(long.class);
    types.add(String.class);
    types.add(double.class);
    types.add(int.class);
    types.add(boolean.class);
    types.add(float.class);
    this.methodsList.addAll(ReflectionUtil.getAllGetters(filteredClass, types));
    MutableComboBoxModel cbModel = new DefaultComboBoxModel();
    for (int i = 0; i < this.methodsList.size(); i++) {
      cbModel.addElement(this.methodsList.get(i).getName()
          .replaceFirst("get", ""));
    }
    System.out.println(methodsList);
    this.cbMethods.setModel(cbModel);
    this.parent.pack();
  }

  /**
   * Computes a simple filter from the swing components of the 'simple filter'
   * sub-panel.
   * @return
   */
  private BinaryComparisonOpsType getSimpleFilter() {
    BinaryComparisonOpsType comp = null;
    if (this.cbOps.getSelectedItem().equals("==")) {
      comp = new PropertyIsEqualTo();
    } else if (this.cbOps.getSelectedItem().equals(">")) {
      comp = new PropertyIsGreaterThan();
    } else if (this.cbOps.getSelectedItem().equals(">=")) {
      comp = new PropertyIsGreaterThanOrEqualTo();
    } else if (this.cbOps.getSelectedItem().equals("<")) {
      comp = new PropertyIsLessThan();
    } else if (this.cbOps.getSelectedItem().equals("<=")) {
      comp = new PropertyIsLessThanOrEqualTo();
    } else if (this.cbOps.getSelectedItem().equals("<>")) {
      comp = new PropertyIsNotEqualTo();
    } else if (this.cbOps.getSelectedItem().equals("Null")) {
      comp = new PropertyIsNull();
    } else if (this.cbOps.getSelectedItem().equals("Like")) {
      comp = new PropertyIsLike();
    }

    if (comp != null) {
      comp.setPropertyName(new PropertyName(this.cbMethods.getSelectedItem()
          .toString()));
      comp.setLiteral(new Literal(this.txtValue.getText()));
    }

    return comp;
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (!e.getValueIsAdjusting()) {
      int index = this.jlist.getSelectedIndex();
      if (index != -1) {
        Filter prec = this.unitaryList.get(index);
        if (prec instanceof BinaryComparisonOpsType) {
          this.btnModify.setEnabled(true);
        } else {
          this.btnModify.setEnabled(false);
        }
      }
    }
  }

  /**
   * Builds the String to display in the right panel text area.
   * @return
   */
  private String makeString() {
    String strAff = "";
    if (this.operator != null) {
      if (this.operator instanceof UnaryLogicOpsType) {
        strAff = strAff + " " + this.operator.getClass().getSimpleName() + " ";
        if (this.filterOpList.size() > 0) {
          strAff += "(" + this.filterOpList.get(0) + ")";
        }
      }
      if (this.operator instanceof BinaryLogicOpsType) {
        if (this.filterOpList.size() > 0) {
          strAff += "(" + this.filterOpList.get(0) + ")";
        }
        strAff += " " + this.operator.getClass().getSimpleName() + " ";
        if (this.filterOpList.size() > 1) {
          strAff += "(" + this.filterOpList.get(1) + ")";
        }
      }
    } else if ((this.unitaryList != null) && (this.unitaryList.size() > 0)) {
      int ind = this.jlist.getSelectedIndex();
      strAff += this.unitaryList.get(ind);
    }
    return strAff;
  }

  public JButton getBtnCreateUnitary() {
    return btnCreateUnitary;
  }

  public JButton getBtnCreateCompound() {
    return btnCreateCompound;
  }

  public JCheckBox getChkCreated() {
    return chkCreated;
  }

  public void setChkCreated(JCheckBox chkCreated) {
    this.chkCreated = chkCreated;
  }

  /**
   * This methods internationalise all labels of the panel.
   */
  private void internationalisation() {
    this.borderTitle = I18N.getString("OGCFilterPanel.borderTitle");
    this.simpleTitle = I18N.getString("OGCFilterPanel.simpleTitle");
    this.compoundTitle = I18N.getString("OGCFilterPanel.compoundTitle");
    this.lblMethods = I18N.getString("OGCFilterPanel.lblMethods");
    this.lblOps = I18N.getString("OGCFilterPanel.lblOps");
    this.lblValue = I18N.getString("OGCFilterPanel.lblValue");
    this.lblDelete = I18N.getString("OGCFilterPanel.lblDelete");
    this.lblCompoundFilter = I18N.getString("OGCFilterPanel.lblCompoundFilter");
    this.lblCreateCompound = I18N.getString("OGCFilterPanel.lblCreateCompound");
    this.lblCreateUnitary = I18N.getString("OGCFilterPanel.lblCreateUnitary");
    this.lblAddToFilter = I18N.getString("OGCFilterPanel.lblAddToFilter");
    this.lblNew = I18N.getString("OGCFilterPanel.lblNew");
    this.lblUnitList = I18N.getString("OGCFilterPanel.lblUnitList");
    this.lblAddUnit = I18N.getString("OGCFilterPanel.lblAddUnit");
    this.lblModify = I18N.getString("OGCFilterPanel.lblModify");
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (cbOps.getSelectedItem().equals("Null"))
      this.txtValue.setEnabled(false);
    else
      this.txtValue.setEnabled(true);
  }

}

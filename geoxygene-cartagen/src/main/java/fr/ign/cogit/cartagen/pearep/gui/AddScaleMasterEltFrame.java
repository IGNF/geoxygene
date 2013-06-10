package fr.ign.cogit.cartagen.pearep.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;

import fr.ign.cogit.cartagen.mrdb.scalemaster.ProcessParameter;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleLine;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterElement.ProcessPriority;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterEnrichment;
import fr.ign.cogit.cartagen.mrdb.scalemaster.ScaleMasterGeneProcess;
import fr.ign.cogit.cartagen.software.dataset.SourceDLM;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.util.Interval;
import fr.ign.cogit.geoxygene.filter.Filter;

public class AddScaleMasterEltFrame extends JFrame implements ActionListener,
    ItemListener {

  /****/
  private static final long serialVersionUID = 4360672319562183279L;
  private EditScaleMasterFrame parent;
  private ScaleLine line;
  private JSpinner spinMin, spinMax;
  private JButton btnOk, btnCancel;
  private JButton btnAddEnrich, btnRemoveEnrich, btnAddFilter, btnRemoveFilter,
      btnAddProcess, btnRemoveProcess;
  private JComboBox comboDbs, comboEnrich, comboProc;
  private JList jlistEnrichs, jlistProcess;
  private List<ScaleMasterEnrichment> enrichments;
  private List<ScaleMasterGeneProcess> processes;
  private Filter filter;
  private JTextField filterTxt;
  private JSlider slideFilter, slideProcess;
  private ProcessParameterPanel pParameters;
  private Set<ProcessParameter> parameters;
  private List<ProcessPriority> processPriorities;

  // internationalisation labels
  private String frameTitle, lblOk, lblCancel, lblInterval, lblMinScale,
      lblMaxScale, lblDb, lblEnrichments, lblFilter, lblProcesses, lblPriority,
      lblAdd, lblRemove;

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("ok")) {
      Interval<Integer> interval = new Interval<Integer>(
          (Integer) this.spinMin.getValue(), (Integer) this.spinMax.getValue());
      ScaleMasterElement element = new ScaleMasterElement(this.line, interval,
          (String) this.comboDbs.getSelectedItem());
      // add the filter to the element
      if (this.filter != null) {
        element.setOgcFilter(this.filter);
        element.setFilterPriority(ProcessPriority.values()[this.slideFilter
            .getValue() - 1]);
      }
      // build element from swing components (add the enrichments and the
      // processes)
      element.getEnrichments().addAll(this.enrichments);
      for (int i = 0; i < this.processes.size(); i++) {
        ScaleMasterGeneProcess proc = this.processes.get(i);
        element.addProcess(proc.getProcessName(), proc.getParametersMap(),
            this.processPriorities.get(i));
      }

      this.line.addElement(element);
      ScaleLineDisplayPanel panel = this.parent.getLinePanels().get(
          this.line.getTheme().getName());
      panel.updateElements();
      this.parent.pack();
      this.setVisible(false);
    } else if (e.getActionCommand().equals("cancel")) {
      this.setVisible(false);
    } else if (e.getActionCommand().equals("addEnrich")) {
      this.enrichments.add((ScaleMasterEnrichment) comboEnrich
          .getSelectedItem());
      updateJList(jlistEnrichs);
    } else if (e.getActionCommand().equals("addProc")) {
      fillParametersValues();
      ScaleMasterGeneProcess processToAdd = (ScaleMasterGeneProcess) comboProc
          .getSelectedItem();
      for (ProcessParameter param : parameters)
        processToAdd.addParameter(param);
      this.processes.add(processToAdd);
      processPriorities
          .add(ProcessPriority.values()[slideProcess.getValue() - 1]);
      comboProc.setSelectedItem(null);
      updateJList(jlistProcess);
    } else if (e.getActionCommand().equals("addFilter")) {
      // open a frame to define the filter
      AddFilterToElementFrame frame = new AddFilterToElementFrame(this);
      frame.setVisible(true);
    } else if (e.getActionCommand().equals("removeFilter")) {
      this.filter = null;
      this.filterTxt.setEditable(true);
      this.filterTxt.setText("");
      this.filterTxt.setEditable(false);
    } else if (e.getActionCommand().equals("removeProc")) {
      this.processes.remove(jlistProcess.getSelectedValue());
      this.processPriorities.remove(jlistProcess.getSelectedIndex());
      this.updateJList(jlistProcess);
    } else if (e.getActionCommand().equals("removeEnrich")) {
      this.enrichments.remove(jlistEnrichs.getSelectedValue());
      this.updateJList(jlistEnrichs);
    }
  }

  public AddScaleMasterEltFrame(EditScaleMasterFrame frame, ScaleLine line) {
    super();
    this.internationalisation();
    this.setTitle(this.frameTitle + line.getTheme().getName());
    this.parent = frame;
    this.line = line;
    this.parameters = new HashSet<ProcessParameter>();
    this.processes = new ArrayList<ScaleMasterGeneProcess>();
    this.processPriorities = new ArrayList<ScaleMasterElement.ProcessPriority>();
    this.enrichments = new ArrayList<ScaleMasterEnrichment>();

    // a panel for the definition of the interval
    JPanel pInterval = new JPanel();
    this.spinMin = new JSpinner(new SpinnerNumberModel(25000,
        ((Integer) this.parent.getSpMin().getValue()).intValue(),
        ((Integer) this.parent.getSpMax().getValue()).intValue(), 1000));
    spinMin.setPreferredSize(new Dimension(80, 20));
    spinMin.setMaximumSize(new Dimension(80, 20));
    spinMin.setMinimumSize(new Dimension(80, 20));
    this.spinMax = new JSpinner(new SpinnerNumberModel(250000,
        ((Integer) this.parent.getSpMin().getValue()).intValue(),
        ((Integer) this.parent.getSpMax().getValue()).intValue(), 1000));
    spinMax.setPreferredSize(new Dimension(80, 20));
    spinMax.setMaximumSize(new Dimension(80, 20));
    spinMax.setMinimumSize(new Dimension(80, 20));
    pInterval.add(new JLabel(this.lblInterval));
    pInterval.add(Box.createHorizontalGlue());
    pInterval.add(new JLabel(this.lblMinScale));
    pInterval.add(this.spinMin);
    pInterval.add(Box.createHorizontalGlue());
    pInterval.add(new JLabel(this.lblMaxScale));
    pInterval.add(this.spinMax);
    pInterval.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pInterval.setLayout(new BoxLayout(pInterval, BoxLayout.X_AXIS));

    // a panel for the definition of the used DB and the classes
    JPanel pData = new JPanel();
    DefaultComboBoxModel cbModel = new DefaultComboBoxModel();
    cbModel.addElement(SourceDLM.MGCPPlusPlus.name());
    cbModel.addElement(SourceDLM.VMAP1PlusPlus.name());
    this.comboDbs = new JComboBox(cbModel);
    comboDbs.setPreferredSize(new Dimension(80, 20));
    comboDbs.setMaximumSize(new Dimension(80, 20));
    comboDbs.setMinimumSize(new Dimension(80, 20));
    pData.add(new JLabel(lblDb));
    pData.add(this.comboDbs);
    pData.add(Box.createHorizontalGlue());
    pData.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    pData.setLayout(new BoxLayout(pData, BoxLayout.X_AXIS));

    // the panels for enrichments, the filter and the processes
    // First, the enrichment panel
    JPanel pEnrich = new JPanel();
    this.btnAddEnrich = new JButton(this.lblAdd);
    this.btnAddEnrich.addActionListener(this);
    this.btnAddEnrich.setActionCommand("addEnrich");
    this.btnRemoveEnrich = new JButton(this.lblRemove);
    this.btnRemoveEnrich.addActionListener(this);
    this.btnRemoveEnrich.setActionCommand("removeEnrich");
    this.comboEnrich = new JComboBox(this.parent.getEnrichComboModel());
    comboEnrich.setPreferredSize(new Dimension(100, 20));
    comboEnrich.setMaximumSize(new Dimension(100, 20));
    comboEnrich.setMinimumSize(new Dimension(100, 20));
    jlistEnrichs = new JList();
    jlistEnrichs
        .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    jlistEnrichs.setPreferredSize(new Dimension(120, 1000));
    jlistEnrichs.setMaximumSize(new Dimension(120, 1000));
    jlistEnrichs.setMinimumSize(new Dimension(120, 1000));
    JScrollPane scroll = new JScrollPane(jlistEnrichs);
    scroll.setPreferredSize(new Dimension(120, 120));
    scroll.setMaximumSize(new Dimension(120, 120));
    scroll.setMinimumSize(new Dimension(120, 120));
    pEnrich.add(scroll);
    pInterval.add(Box.createHorizontalGlue());
    pEnrich.add(comboEnrich);
    pInterval.add(Box.createHorizontalGlue());
    pEnrich.add(btnAddEnrich);
    pInterval.add(Box.createHorizontalStrut(4));
    pEnrich.add(btnRemoveEnrich);
    pEnrich.setBorder(BorderFactory.createTitledBorder(lblEnrichments));
    pEnrich.setLayout(new BoxLayout(pEnrich, BoxLayout.X_AXIS));

    // Then, the filter panel
    JPanel pFilter = new JPanel();
    this.btnAddFilter = new JButton(this.lblAdd);
    this.btnAddFilter.addActionListener(this);
    this.btnAddFilter.setActionCommand("addFilter");
    this.btnRemoveFilter = new JButton(this.lblRemove);
    this.btnRemoveFilter.addActionListener(this);
    this.btnRemoveFilter.setActionCommand("removeFilter");
    filterTxt = new JTextField();
    filterTxt.setPreferredSize(new Dimension(120, 20));
    filterTxt.setMaximumSize(new Dimension(120, 20));
    filterTxt.setMinimumSize(new Dimension(120, 20));
    filterTxt.setEditable(false);
    this.slideFilter = new JSlider(1, 5, 3);
    this.slideFilter.setPaintTicks(true);
    this.slideFilter.setMajorTickSpacing(1);
    this.slideFilter.setPreferredSize(new Dimension(120, 50));
    this.slideFilter.setMaximumSize(new Dimension(120, 50));
    this.slideFilter.setMinimumSize(new Dimension(120, 50));
    pFilter.add(filterTxt);
    pInterval.add(Box.createHorizontalGlue());
    pFilter.add(new JLabel(this.lblPriority));
    pFilter.add(slideFilter);
    pInterval.add(Box.createHorizontalGlue());
    pFilter.add(btnAddFilter);
    pInterval.add(Box.createHorizontalStrut(4));
    pFilter.add(btnRemoveFilter);
    pFilter.setBorder(BorderFactory.createTitledBorder(lblFilter));
    pFilter.setLayout(new BoxLayout(pFilter, BoxLayout.X_AXIS));

    // Finally, the processes panel
    JPanel pProc = new JPanel();
    JPanel pBtns = new JPanel();
    this.btnAddProcess = new JButton(this.lblAdd);
    this.btnAddProcess.addActionListener(this);
    this.btnAddProcess.setActionCommand("addProc");
    this.btnRemoveProcess = new JButton(this.lblRemove);
    this.btnRemoveProcess.addActionListener(this);
    this.btnRemoveProcess.setActionCommand("removeProc");
    pBtns.add(btnAddProcess);
    pBtns.add(btnRemoveProcess);
    pBtns.setLayout(new BoxLayout(pBtns, BoxLayout.Y_AXIS));
    jlistProcess = new JList();
    jlistProcess
        .setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    jlistProcess.setPreferredSize(new Dimension(140, 1000));
    jlistProcess.setMaximumSize(new Dimension(140, 1000));
    jlistProcess.setMinimumSize(new Dimension(140, 1000));
    JScrollPane scroll2 = new JScrollPane(jlistProcess);
    scroll2.setPreferredSize(new Dimension(140, 120));
    scroll2.setMaximumSize(new Dimension(140, 120));
    scroll2.setMinimumSize(new Dimension(140, 120));
    JPanel pCurrentProc = new JPanel();
    // comboProc et slideProcess
    JPanel pDescProc = new JPanel();
    this.comboProc = new JComboBox(this.parent.getProcComboModel());
    this.comboProc.setPreferredSize(new Dimension(140, 20));
    this.comboProc.setMaximumSize(new Dimension(140, 20));
    this.comboProc.setMinimumSize(new Dimension(140, 20));
    this.comboProc.addItemListener(this);
    this.slideProcess = new JSlider(1, 5, 3);
    this.slideProcess.setPaintTicks(true);
    this.slideProcess.setMajorTickSpacing(1);
    this.slideProcess.setPreferredSize(new Dimension(120, 50));
    this.slideProcess.setMaximumSize(new Dimension(120, 50));
    this.slideProcess.setMinimumSize(new Dimension(120, 50));
    pDescProc.add(comboProc);
    pDescProc.add(slideProcess);
    pDescProc.setLayout(new BoxLayout(pDescProc, BoxLayout.Y_AXIS));
    // pParameters panel
    this.pParameters = new ProcessParameterPanel();
    this.comboProc.setSelectedIndex(0);
    this.updateParametersPanel();
    this.pParameters.setLayout(new BoxLayout(pParameters, BoxLayout.Y_AXIS));
    pCurrentProc.add(pDescProc);
    pCurrentProc.add(pParameters);
    pCurrentProc.setLayout(new BoxLayout(pCurrentProc, BoxLayout.X_AXIS));
    pProc.add(scroll2);
    pInterval.add(Box.createHorizontalGlue());
    pProc.add(pBtns);
    pInterval.add(Box.createHorizontalGlue());
    pProc.add(pCurrentProc);
    pProc.setBorder(BorderFactory.createTitledBorder(lblProcesses));
    pProc.setLayout(new BoxLayout(pProc, BoxLayout.X_AXIS));

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

    // final layout of the frame
    this.getContentPane().add(pInterval);
    this.getContentPane().add(pData);
    this.getContentPane().add(pEnrich);
    this.getContentPane().add(pFilter);
    this.getContentPane().add(pProc);
    this.getContentPane().add(pButtons);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.pack();
  }

  public AddScaleMasterEltFrame(EditScaleMasterFrame frame, ScaleLine line,
      ScaleMasterElement elem) {
    this(frame, line);
    this.comboDbs.setSelectedItem(elem.getDbName());
    this.enrichments = elem.getEnrichments();
    this.filter = elem.getOgcFilter();
    this.filterTxt.setEditable(true);
    this.filterTxt.setText(filter.toString());
    this.filterTxt.setEditable(false);
    this.spinMin.setValue(elem.getInterval().getMinimum());
    this.spinMax.setValue(elem.getInterval().getMaximum());
    for (String procName : elem.getProcessesToApply()) {
      ScaleMasterGeneProcess proc = null;
      for (int i = 0; i < comboProc.getModel().getSize(); i++) {
        ScaleMasterGeneProcess procI = (ScaleMasterGeneProcess) comboProc
            .getModel().getElementAt(i);
        if (procName.equals(procI.getProcessName())) {
          proc = procI;
          break;
        }
      }
      this.processes.add(proc);
    }
    this.processPriorities = elem.getProcessPriorities();
    this.updateJList(jlistEnrichs);
    this.updateJList(jlistProcess);
    this.pack();
  }

  /**
   * Fill the parameter values from the frame for the parameters of the current
   * process.
   */
  private void fillParametersValues() {
    for (ProcessParameter param : this.parameters)
      param.setValue(this.pParameters.getValue(param));
  }

  /**
   * Update the parameters that are displayed in the parameters panel with the
   * parameters of the current generalisation process in the combo box.
   */
  private void updateParametersPanel() {
    this.pParameters.clear();
    this.pParameters.update(this.parameters);
    this.pack();
  }

  /**
   * Update the {@link JList} parameter content with the current content of the
   * related collection.
   */
  private void updateJList(JList list) {
    Collection<?> coln = null;
    if (list.equals(jlistEnrichs))
      coln = enrichments;
    else
      coln = processes;
    DefaultListModel model = new DefaultListModel();
    for (Object classObj : coln) {
      model.addElement(classObj);
    }
    list.setModel(model);
    this.parent.pack();
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource().equals(this.comboProc)) {
      this.parameters.clear();
      if (this.comboProc.getSelectedItem() != null) {
        this.parameters.addAll(((ScaleMasterGeneProcess) this.comboProc
            .getSelectedItem()).getDefaultParameters());
      }
      updateParametersPanel();
    }
  }

  public Filter getFilter() {
    return filter;
  }

  public void setFilter(Filter filter) {
    this.filter = filter;
  }

  public JTextField getFilterTxt() {
    return filterTxt;
  }

  public void setFilterTxt(JTextField filterTxt) {
    this.filterTxt = filterTxt;
  }

  public ScaleLine getLine() {
    return line;
  }

  public void setLine(ScaleLine line) {
    this.line = line;
  }

  public JComboBox getComboDbs() {
    return comboDbs;
  }

  public void setComboDbs(JComboBox comboDbs) {
    this.comboDbs = comboDbs;
  }

  public EditScaleMasterFrame getParent() {
    return parent;
  }

  public void setParent(EditScaleMasterFrame parent) {
    this.parent = parent;
  }

  /**
   * Internationalise the labels of the frame, reading the values stored in the
   * properties files.
   */
  private void internationalisation() {
    this.frameTitle = I18N.getString("AddScaleMasterEltFrame.frameTitle");
    this.lblInterval = I18N.getString("AddScaleMasterEltFrame.lblInterval");
    this.lblMinScale = I18N.getString("AddScaleMasterEltFrame.lblMinScale");
    this.lblMaxScale = I18N.getString("AddScaleMasterEltFrame.lblMaxScale");
    this.lblDb = I18N.getString("AddScaleMasterEltFrame.lblDb");
    this.lblEnrichments = I18N
        .getString("AddScaleMasterEltFrame.lblEnrichments");
    this.lblFilter = I18N.getString("AddScaleMasterEltFrame.lblFilter");
    this.lblProcesses = I18N.getString("AddScaleMasterEltFrame.lblProcesses");
    this.lblPriority = I18N.getString("AddScaleMasterEltFrame.lblPriority");
    this.lblCancel = I18N.getString("MainLabels.lblCancel");
    this.lblOk = I18N.getString("MainLabels.lblOk");
    this.lblAdd = I18N.getString("MainLabels.lblAdd");
    this.lblRemove = I18N.getString("MainLabels.lblRemove");
  }
}

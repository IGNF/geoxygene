/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.utilities.selection;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jfree.util.ClassComparator;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.CartAGenDataSet;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.component.OGCFilterPanel;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.filter.RealLimitator;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.renderer.ClassSimpleNameListRenderer;
import fr.ign.cogit.cartagen.util.FileUtil;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;

public class AttributeQueryFrame extends JFrame implements ActionListener,
    ItemListener, ChangeListener, ListSelectionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private enum ExtentType {
    NONE, WINDOW, GEOMETRY, ENVELOPE
  }

  private List<Class<?>> geoClasses;
  private ExtentType extentType = ExtentType.NONE;
  private String dbName;

  // swing components of the frame
  private JButton btnBack, btnNext;
  private int level = 0;
  private final static int LEVELS = 3;
  private OGCFilterPanel pFilter;
  private JPanel pExtent, pClass;
  private JComboBox cbDbs;
  private JRadioButton rbNone, rbWindow, rbGeometry, rbEnvelope;
  private JTextField txtXMin, txtXMax, txtYMin, txtYMax;
  private HashMap<Integer, JPanel> mapLevelPanel = new HashMap<Integer, JPanel>();
  private JPanel displayedPanel;
  private JList classJList;

  // internationalisation labels
  private String lblQuery, lblCancel, lblNext, lblBack, extentTitle, lblDbs;
  private String lblNone, lblWindow, lblGeometry, lblEnvelope, classTitle,
      lblClasses, lblMessage;

  public AttributeQueryFrame() {
    super(I18N.getString("AttributeQueryFrame.menuName"));
    this.setSize(800, 600);
    this.internationalisation();
    this.setGeoClasses();
    this.dbName = CartAGenDoc.getInstance().getCurrentDataset().getCartAGenDB()
        .getName();

    // ***********************************
    // a panel to define the queried class
    this.pClass = new JPanel();
    DefaultListModel model = new DefaultListModel();
    for (Class<?> classObj : this.geoClasses) {
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
    this.pClass.add(new JLabel(this.lblClasses + " : "));
    this.pClass.add(new JScrollPane(this.classJList));
    Border titled = BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(EtchedBorder.LOWERED), this.classTitle);
    this.pClass.setBorder(BorderFactory.createCompoundBorder(titled,
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    this.pClass.setLayout(new BoxLayout(this.pClass, BoxLayout.X_AXIS));

    // ***********************************
    // a panel to define the OGC Filter
    this.pFilter = new OGCFilterPanel(this, (Class<?>) this.classJList
        .getSelectedValue());

    // ***********************************
    // a panel to define the extent of the query
    this.pExtent = new JPanel();
    JPanel pDb = new JPanel();
    this.cbDbs = new JComboBox(CartAGenDoc.getInstance().getDatabases()
        .keySet().toArray());
    this.cbDbs.setSelectedItem(CartAGenDoc.getInstance().getCurrentDataset()
        .getCartAGenDB());
    this.cbDbs.setPreferredSize(new Dimension(110, 20));
    this.cbDbs.setMaximumSize(new Dimension(110, 20));
    this.cbDbs.setMinimumSize(new Dimension(110, 20));
    this.cbDbs.addItemListener(this);
    pDb.add(new JLabel(this.lblDbs + " : "));
    pDb.add(this.cbDbs);
    pDb.setLayout(new BoxLayout(pDb, BoxLayout.X_AXIS));
    // a sub panel to display the radio buttons
    JPanel pRadios = new JPanel();
    this.rbNone = new JRadioButton(this.lblNone);
    this.rbNone.addChangeListener(this);
    this.rbNone.setSelected(true);
    this.rbWindow = new JRadioButton(this.lblWindow);
    this.rbWindow.addChangeListener(this);
    this.rbGeometry = new JRadioButton(this.lblGeometry);
    this.rbGeometry.addChangeListener(this);
    this.rbEnvelope = new JRadioButton(this.lblEnvelope);
    this.rbEnvelope.addChangeListener(this);
    ButtonGroup bg = new ButtonGroup();
    bg.add(this.rbNone);
    bg.add(this.rbWindow);
    bg.add(this.rbEnvelope);
    bg.add(this.rbGeometry);
    pRadios.add(this.rbNone);
    pRadios.add(this.rbWindow);
    pRadios.add(this.rbEnvelope);
    pRadios.add(this.rbGeometry);
    pRadios.setLayout(new BoxLayout(pRadios, BoxLayout.X_AXIS));
    // a panel to define a custom envelope
    JPanel pEnvelope = new JPanel();
    this.txtXMin = new JTextField();
    this.txtXMin.setPreferredSize(new Dimension(90, 20));
    this.txtXMin.setMaximumSize(new Dimension(90, 20));
    this.txtXMin.setMinimumSize(new Dimension(90, 20));
    this.txtXMin.setDocument(new RealLimitator());
    this.txtXMax = new JTextField();
    this.txtXMax.setPreferredSize(new Dimension(90, 20));
    this.txtXMax.setMaximumSize(new Dimension(90, 20));
    this.txtXMax.setMinimumSize(new Dimension(90, 20));
    this.txtXMax.setDocument(new RealLimitator());
    this.txtYMin = new JTextField();
    this.txtYMin.setPreferredSize(new Dimension(90, 20));
    this.txtYMin.setMaximumSize(new Dimension(90, 20));
    this.txtYMin.setMinimumSize(new Dimension(90, 20));
    this.txtYMin.setDocument(new RealLimitator());
    this.txtYMax = new JTextField();
    this.txtYMax.setPreferredSize(new Dimension(90, 20));
    this.txtYMax.setMaximumSize(new Dimension(90, 20));
    this.txtYMax.setMinimumSize(new Dimension(90, 20));
    this.txtYMax.setDocument(new RealLimitator());
    pEnvelope.add(new JLabel("X Min "));
    pEnvelope.add(this.txtXMin);
    pEnvelope.add(new JLabel("X Max "));
    pEnvelope.add(this.txtXMax);
    pEnvelope.add(new JLabel("Y Min "));
    pEnvelope.add(this.txtYMin);
    pEnvelope.add(new JLabel("Y Max "));
    pEnvelope.add(this.txtYMax);
    pEnvelope.setLayout(new BoxLayout(pEnvelope, BoxLayout.X_AXIS));
    this.pExtent.add(pDb);
    this.pExtent.add(Box.createVerticalGlue());
    this.pExtent.add(pRadios);
    this.pExtent.add(Box.createVerticalGlue());
    this.pExtent.add(pEnvelope);
    this.pExtent.add(Box.createVerticalGlue());
    titled = BorderFactory.createTitledBorder(BorderFactory
        .createEtchedBorder(EtchedBorder.LOWERED), this.extentTitle);
    this.pExtent.setBorder(BorderFactory.createCompoundBorder(titled,
        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    this.pExtent.setLayout(new BoxLayout(this.pExtent, BoxLayout.Y_AXIS));

    // ***********************************
    // a panel for the buttons
    JPanel pButtons = new JPanel();
    this.btnBack = new JButton(this.lblBack);
    this.btnBack.addActionListener(this);
    this.btnBack.setActionCommand("Back");
    this.btnBack.setPreferredSize(new Dimension(100, 40));
    this.btnBack.setEnabled(false);
    this.btnNext = new JButton(this.lblNext);
    this.btnNext.addActionListener(this);
    this.btnNext.setActionCommand("Next");
    this.btnNext.setPreferredSize(new Dimension(100, 40));
    JButton btnCancel = new JButton(this.lblCancel);
    btnCancel.addActionListener(this);
    btnCancel.setActionCommand("Cancel");
    btnCancel.setPreferredSize(new Dimension(100, 40));
    JButton btnQuery = new JButton(this.lblQuery);
    btnQuery.addActionListener(this);
    btnQuery.setActionCommand("Query");
    btnQuery.setPreferredSize(new Dimension(100, 40));
    pButtons.add(this.btnBack);
    pButtons.add(this.btnNext);
    pButtons.add(btnQuery);
    pButtons.add(btnCancel);
    pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));

    // ***********************************
    // final layout of the frame
    this.mapLevelPanel.put(0, this.pClass);
    this.mapLevelPanel.put(1, this.pFilter);
    this.mapLevelPanel.put(2, this.pExtent);
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
    } else if (e.getActionCommand().equals("Query")) {
      this.triggerQuery();
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
      if (this.level == AttributeQueryFrame.LEVELS - 1) {
        this.btnNext.setEnabled(false);
      }
      if (!this.btnBack.isEnabled()) {
        this.btnBack.setEnabled(true);
      }
      this.pack();
    }
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    this.dbName = this.cbDbs.getSelectedItem().toString();
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    if (this.rbNone.isSelected()) {
      this.extentType = ExtentType.NONE;
    } else if (this.rbWindow.isSelected()) {
      this.extentType = ExtentType.WINDOW;
    } else if (this.rbEnvelope.isSelected()) {
      this.extentType = ExtentType.ENVELOPE;
    } else if (this.rbGeometry.isSelected()) {
      this.extentType = ExtentType.GEOMETRY;
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getSource().equals(this.classJList)) {
      if (!e.getValueIsAdjusting()) {
        this.pFilter.changeSelectedClass((Class<?>) this.classJList
            .getSelectedValue());
      }
    }
  }

  /**
   * Trigger the query from the filter and the extent defined in the frame.
   */
  private void triggerQuery() {
    // first get the correct dataset to work with
    CartAGenDoc doc = CartAGenDoc.getInstance();
    CartAGenDataSet dataset = doc.getDataset(this.dbName);
    // now get the population from the class
    String popName = dataset.getPopNameFromClass(this.pFilter
        .getFilteredClass());
    IPopulation<IGeneObj> pop = dataset.getCartagenPop(popName);
    Collection<IGeneObj> queryColn = pop;
    // reduce the query collection if there is an extent specified
    if (this.extentType != ExtentType.NONE) {
      IGeometry geom = null;
      if (this.extentType == ExtentType.WINDOW) {
        geom = CartagenApplication.getInstance().getFrame().getVisuPanel()
            .getDisplayEnvelope().getGeom();
      }
      if (this.extentType == ExtentType.ENVELOPE) {
        geom = new GM_Envelope(Double.valueOf(this.txtXMin.getText()), Double
            .valueOf(this.txtXMax.getText()), Double.valueOf(this.txtYMin
            .getText()), Double.valueOf(this.txtYMax.getText())).getGeom();
      }
      if (this.extentType == ExtentType.GEOMETRY) {
        geom = CartagenApplication.getInstance().getFrame().getVisuPanel().selectedObjects
            .get(0).getGeom();
      }
      queryColn = pop.select(geom);
    }
    int nbSelectedObjs = 0;
    for (IGeneObj obj : queryColn) {
      // if not filter has been defined, all features are selected
      if (this.pFilter.getFilter() == null) {
        CartagenApplication.getInstance().getFrame().getVisuPanel().selectedObjects
            .add(obj);
        nbSelectedObjs++;
        continue;
      }
      // else, select according to the OGC Filter
      if (this.pFilter.getFilter().evaluate(obj)) {
        CartagenApplication.getInstance().getFrame().getVisuPanel().selectedObjects
            .add(obj);
        nbSelectedObjs++;
      }
    }
    JOptionPane.showMessageDialog(this, nbSelectedObjs + " " + this.lblMessage);
  }

  /**
   * Get all the classes inheriting from IGeneObj in the project and set them as
   * the geo classes to be queried.
   */
  private void setGeoClasses() {
    this.geoClasses = new ArrayList<Class<?>>();
    // get the directory of the package of this class
    Package pack = this.getClass().getPackage();
    String name = pack.getName();
    name = name.replace('.', '/');
    name.replaceAll("%20", " ");
    if (!name.startsWith("/")) {
      name = "/" + name;
    }
    URL pathName = this.getClass().getResource(name);
    File directory = new File(pathName.getFile());
    // get the parent directories to get fr.ign.cogit.cartagen package
    while (!directory.getName().equals("cartagen")) {
      directory = directory.getParentFile();
    }
    Collection<String> excluded = new HashSet<String>();
    excluded.add("gestionK");
    excluded.add("software");
    excluded.add("agentGeneralisation");
    excluded.add("internships");
    List<File> files = FileUtil.getAllFilesInDir(directory, excluded);
    for (File file : files) {
      if (!file.getName().endsWith(".class")) {
        continue;
      }
      if (file.getName().substring(0, file.getName().length() - 6).equals(
          "GothicObjectDiffusion")) {
        continue;
      }
      String path = file.getPath().substring(file.getPath().indexOf("fr"));
      String classname = FileUtil.changeFileNameToClassName(path);
      try {
        // Try to create an instance of the object
        Class<?> classObj = Class.forName(classname);
        if (classObj.isInterface()) {
          continue;
        }
        if (classObj.isLocalClass()) {
          continue;
        }
        if (classObj.isMemberClass()) {
          continue;
        }
        if (classObj.isEnum()) {
          continue;
        }
        if (Modifier.isAbstract(classObj.getModifiers())) {
          continue;
        }
        // test if the class inherits from IGeneObj
        if (IGeneObj.class.isAssignableFrom(classObj)) {
          this.geoClasses.add(classObj);
        }
      } catch (ClassNotFoundException cnfex) {
        cnfex.printStackTrace();
      }
    }
    Collections.sort(this.geoClasses, new ClassComparator());
  }

  /**
   * This methods internationalise all labels of the panel.
   */
  private void internationalisation() {
    this.lblQuery = I18N.getString("AttributeQueryFrame.lblQuery");
    this.lblCancel = I18N.getString("MainLabels.lblCancel");
    this.lblNext = I18N.getString("MainLabels.lblNext");
    this.lblBack = I18N.getString("MainLabels.lblBack");
    this.extentTitle = I18N.getString("AttributeQueryFrame.extentTitle");
    this.lblDbs = I18N.getString("AttributeQueryFrame.lblDbs");
    this.lblNone = I18N.getString("AttributeQueryFrame.lblNone");
    this.lblWindow = I18N.getString("AttributeQueryFrame.lblWindow");
    this.lblGeometry = I18N.getString("AttributeQueryFrame.lblGeometry");
    this.lblEnvelope = I18N.getString("AttributeQueryFrame.lblEnvelope");
    this.classTitle = I18N.getString("AttributeQueryFrame.classTitle");
    this.lblClasses = I18N.getString("AttributeQueryFrame.lblClasses");
    this.lblMessage = I18N.getString("AttributeQueryFrame.lblMessage");
  }

}

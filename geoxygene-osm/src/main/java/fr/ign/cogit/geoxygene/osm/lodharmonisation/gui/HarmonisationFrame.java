package fr.ign.cogit.geoxygene.osm.lodharmonisation.gui;

import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.cartagen.core.genericschema.IGeneObj;
import fr.ign.cogit.cartagen.software.dataset.GeometryPool;
import fr.ign.cogit.geoxygene.osm.util.I18N;

public class HarmonisationFrame extends JFrame implements ActionListener,
    ChangeListener {

  /****/
  private static final long serialVersionUID = 1L;
  private Set<IGeneObj> windowObjects;
  private JTabbedPane tabs;
  private HarmonisationPanel currentTab;
  private JRadioButton rdDataset, rdWindow;
  private GeometryPool pool;
  private Color defColor = Color.RED;
  private int defWidth = 2;

  public HarmonisationFrame(Set<IGeneObj> windowObjects, GeometryPool pool)
      throws HeadlessException {
    super(I18N.getString("HarmonisationFrame.frameTitle"));
    this.windowObjects = windowObjects;
    this.pool = pool;
    this.setSize(500, 600);
    this.setAlwaysOnTop(true);

    tabs = new JTabbedPane();
    tabs.addChangeListener(this);
    HarmonisationPanel extendBuiltUp = new ExtendBuiltUpPanel();
    tabs.addTab(extendBuiltUp.getTabName(), extendBuiltUp);
    AddClearingPanel addClearing = new AddClearingPanel();
    tabs.addTab(addClearing.getTabName(), addClearing);
    AdjustLakePanel adjustLake = new AdjustLakePanel();
    tabs.addTab(adjustLake.getTabName(), adjustLake);
    AlignTreeAlongRoadPanel alignTrees = new AlignTreeAlongRoadPanel();
    tabs.addTab(alignTrees.getTabName(), alignTrees);
    CoastlineLandusePanel coastlines = new CoastlineLandusePanel();
    tabs.addTab(coastlines.getTabName(), coastlines);
    AdjustFunctionalSitePanel functionalSites = new AdjustFunctionalSitePanel();
    tabs.addTab(functionalSites.getTabName(), functionalSites);
    tabs.setSelectedIndex(0);
    currentTab = extendBuiltUp;
    // TODO add the other tabs

    // a panel for the feature selection mode
    JPanel selectionPanel = new JPanel();
    rdDataset = new JRadioButton(
        I18N.getString("HarmonisationFrame.radioDataset"));
    rdWindow = new JRadioButton(
        I18N.getString("HarmonisationFrame.radioWindow"));
    ButtonGroup bg = new ButtonGroup();
    bg.add(rdDataset);
    bg.add(rdWindow);
    rdDataset.setSelected(true);
    selectionPanel.add(rdDataset);
    selectionPanel.add(rdWindow);
    selectionPanel.setBorder(BorderFactory.createTitledBorder(I18N
        .getString("HarmonisationFrame.selection")));
    selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.X_AXIS));

    // OK button frame
    JPanel panelBtn = new JPanel();
    panelBtn.setLayout(new BoxLayout(panelBtn, BoxLayout.X_AXIS));
    JButton bouton0 = new JButton("OK");
    bouton0.addActionListener(this);
    bouton0.setActionCommand("OK");
    JButton bouton1 = new JButton(I18N.getString("MainLabels.lblCancel"));
    bouton1.addActionListener(this);
    bouton1.setActionCommand("cancel");
    panelBtn.add(bouton0);
    panelBtn.add(bouton1);
    panelBtn.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // global layout of the frame
    this.getContentPane().add(tabs);
    this.getContentPane().add(selectionPanel);
    this.getContentPane().add(panelBtn);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    pack();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("OK")) {
      Set<IGeneObj> modifiedObjects = null;
      if (rdWindow.isSelected()) {
        modifiedObjects = currentTab.triggerHarmonisation(true, windowObjects);
      } else {
        modifiedObjects = currentTab.triggerHarmonisation(false, null);
      }
      // display the initial geometry in the geometry pool
      for (IGeneObj obj : modifiedObjects) {
        pool.addFeatureToGeometryPool(obj.getInitialGeom(), defColor, defWidth);
      }
      this.dispose();
    } else if (e.getActionCommand().equals("cancel")) {
      this.dispose();
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    // change the current tab
    currentTab = (HarmonisationPanel) tabs.getComponentAt(tabs
        .getSelectedIndex());
  }

}

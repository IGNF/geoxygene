package fr.ign.cogit.osm.lodharmonisation.gui;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.osm.util.I18N;

public class HarmonisationFrame extends JFrame implements ActionListener,
    ChangeListener {

  /****/
  private static final long serialVersionUID = 1L;
  private Set<IFeature> windowObjects;
  private JTabbedPane tabs;
  private HarmonisationPanel currentTab;
  private JRadioButton rdDataset, rdWindow;

  public HarmonisationFrame(Set<IFeature> windowObjects)
      throws HeadlessException {
    super(I18N.getString("HarmonisationFrame.frameTitle"));
    this.windowObjects = windowObjects;
    this.setSize(500, 600);
    this.setAlwaysOnTop(true);

    tabs = new JTabbedPane();
    tabs.addChangeListener(this);
    HarmonisationPanel extendBuiltUp = new ExtendBuiltUpPanel();
    tabs.addTab(extendBuiltUp.getTabName(), extendBuiltUp);
    // TODO add the other tabs

    // a panel for the feature selection mode
    JPanel selectionPanel = new JPanel();
    // TODO
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
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getActionCommand().equals("OK")) {
      // TODO
      this.dispose();
    } else if (e.getActionCommand().equals("cancel")) {
      this.dispose();
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    // change the current tab
    currentTab = (HarmonisationPanel) tabs.getTabComponentAt(tabs
        .getSelectedIndex());
  }

}

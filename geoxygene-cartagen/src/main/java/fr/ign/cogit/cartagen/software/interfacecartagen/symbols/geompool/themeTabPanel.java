/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.symbols.geompool;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

public class themeTabPanel extends JPanel {
  private static final long serialVersionUID = 1L;

  // //////////////
  // COMPONENTS
  // /////////////

  public String popName;

  private ColourTableModel colourModel;

  private JLabel lblCurrent;
  public JTable tabCurrent;
  private JLabel lblInitial;
  public JTable tabInitial;
  private JLabel lblUnchanged;
  public JTable tabUnchanged;
  private JLabel lblModified;
  public JTable tabModified;
  private JLabel lblDeleted;
  public JTable tabDeleted;
  private JLabel lblCreated;
  public JTable tabCreated;

  /**
   * Constructor
   */
  public themeTabPanel(String popName) {
    this.popName = popName;
    this.initComponents();
    this.placeComponents();
  }

  /**
   * Creation of colour table
   */
  private JTable createColourTable() {
    JTable tab = new JTable(this.colourModel);
    tab.setMinimumSize(new Dimension(260, 20));
    tab.setDefaultRenderer(Object.class, new ColourTableCellRenderer());
    tab.setColumnSelectionAllowed(true);
    tab.setRowSelectionAllowed(false);
    tab.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tab.changeSelection(0, 0, false, false);
    return tab;
  }

  /**
   * Initialisation of the components
   */
  private void initComponents() {

    // Colour table model
    this.colourModel = new ColourTableModel();

    // current dataset
    this.lblCurrent = new JLabel("Current dataset features :");
    this.tabCurrent = this.createColourTable();
    // initial dataset
    this.lblInitial = new JLabel("Initial dataset features :");
    this.tabInitial = this.createColourTable();
    // unchanged features
    this.lblUnchanged = new JLabel("Unchanged features :");
    this.tabUnchanged = this.createColourTable();
    // modified features
    this.lblModified = new JLabel("Modified features :");
    this.tabModified = this.createColourTable();
    // deleted features
    this.lblDeleted = new JLabel("Deleted features :");
    this.tabDeleted = this.createColourTable();
    // created features
    this.lblCreated = new JLabel("Created features :");
    this.tabCreated = this.createColourTable();

  }

  /**
   * Placement of the components in the frame
   */
  private void placeComponents() {

    GridBagConstraints gbc = new GridBagConstraints();
    this.setLayout(new GridBagLayout());

    // Current dataset
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.insets = new Insets(50, 20, 20, 20);
    gbc.anchor = GridBagConstraints.WEST;
    this.add(this.lblCurrent, gbc);
    gbc.gridx++;
    this.add(this.tabCurrent, gbc);

    // Initial dataset
    gbc.gridx = 1;
    gbc.gridy++;
    gbc.insets = new Insets(20, 20, 50, 20);
    gbc.anchor = GridBagConstraints.WEST;
    this.add(this.lblInitial, gbc);
    gbc.gridx++;
    this.add(this.tabInitial, gbc);

    // Unchanged features
    gbc.gridx = 1;
    gbc.gridy++;
    gbc.insets = new Insets(20, 20, 20, 20);
    gbc.anchor = GridBagConstraints.WEST;
    this.add(this.lblUnchanged, gbc);
    gbc.gridx++;
    this.add(this.tabUnchanged, gbc);

    // Modified features
    gbc.gridx = 1;
    gbc.gridy++;
    gbc.anchor = GridBagConstraints.WEST;
    this.add(this.lblModified, gbc);
    gbc.gridx++;
    this.add(this.tabModified, gbc);

    // Deleted features
    gbc.gridx = 1;
    gbc.gridy++;
    gbc.anchor = GridBagConstraints.WEST;
    this.add(this.lblDeleted, gbc);
    gbc.gridx++;
    this.add(this.tabDeleted, gbc);

    // Created features
    gbc.gridx = 1;
    gbc.gridy++;
    gbc.insets = new Insets(20, 20, 50, 20);
    gbc.anchor = GridBagConstraints.WEST;
    this.add(this.lblCreated, gbc);
    gbc.gridx++;
    this.add(this.tabCreated, gbc);

  }

}

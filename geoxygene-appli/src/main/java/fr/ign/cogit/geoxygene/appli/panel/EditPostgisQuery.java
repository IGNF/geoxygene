package fr.ign.cogit.geoxygene.appli.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;

/**
 * 
 * @author Marie-Dominique
 */
public class EditPostgisQuery extends JDialog implements ActionListener {
  
  /** Serial version id. */
  private static final long serialVersionUID = 1L;
  
  /** LOGGER. */
  private Logger LOGGER = Logger.getLogger(EditPostgisQuery.class.getName());
  
  private AddPostgisLayer addPostgisLayer;
  private String schemaName;
  private String tableName;
  private String geomColumnName;
  
  private JPanel operatorPanel;
  private JPanel filterExpressionPanel;
  private JPanel buttonPanel;
  
  private JButton okay;
  private JButton cancel;
  private JTextArea queryArea;
  
  private JButton gtButt;
  private JButton gteButt;
  private JButton ltButt;
  private JButton lteButt;
  private JButton eqButt;
  private JButton neqButt;
  private JButton inButt;
  private JButton ninButt;
  
  public EditPostgisQuery(AddPostgisLayer addPostgisLayer) {
    
    String chaineConn = addPostgisLayer.tableQueryList.getSelectedItem().toString();
    String[] tabConn = chaineConn.split(" - ");
    if (tabConn.length < 3) {
      LOGGER.error("Cannot get schema, table and geometry colum names.");
      return;
    }
    schemaName = tabConn[0];
    tableName = tabConn[1];
    geomColumnName = tabConn[2];
    
    this.addPostgisLayer = addPostgisLayer;
    
    setModal(true);
    setTitle(I18N.getString("AddPostgisLayer.title"));
    setIconImage(new ImageIcon(
            GeOxygeneApplication.class.getResource("/images/toolbar/database_add.png")).getImage());
    setLocation(300, 150);
    
    initFilterExpressionPanel();
    initOperatorPanel();
    initButtonPanel();
    
    // dispaly panel
    FormLayout layout = new FormLayout(
            "20dlu, pref, 20dlu",  // colonnes
            "10dlu, pref, pref, pref, 20dlu");  // lignes
    getContentPane().setLayout(layout);
    CellConstraints cc = new CellConstraints();
    add(operatorPanel, cc.xy(2, 2));
    add(filterExpressionPanel, cc.xy(2, 3));
    add(buttonPanel, cc.xy(2, 4));
    
    pack();
    setVisible(true);
    // setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

  }
  
  private void initButtonPanel() {
    
    buttonPanel = new JPanel();
    buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(""),
            BorderFactory.createEmptyBorder(10, 0, 0, 0)));
    
    FormLayout layout = new FormLayout(
            "20dlu, pref, 5dlu, pref, 20dlu",  // colonnes
            "10dlu, pref, 20dlu");  // lignes
    buttonPanel.setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    okay = new JButton("Add query with filter");
    cancel = new JButton("Close");
    
    okay.addActionListener( this );
    cancel.addActionListener( this );
    
    buttonPanel.add(okay, cc.xy(2, 2));
    buttonPanel.add(cancel, cc.xy(4, 2));

  }
  
  private void initOperatorPanel() {
    
    operatorPanel = new JPanel();
    operatorPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder("Opérateur de filtre"),
            BorderFactory.createEmptyBorder(10, 0, 0, 0)));
    
    FormLayout layout = new FormLayout(
        "20dlu, pref, pref, pref, pref, 20dlu",  // colonnes
        "10dlu, pref, pref, 20dlu");  // lignes
    operatorPanel.setLayout(layout);
    CellConstraints cc = new CellConstraints();
    
    ltButt = new JButton("<");
    ltButt.addActionListener(this);
    operatorPanel.add(ltButt, cc.xy(2, 2));
    lteButt = new JButton("<=");
    lteButt.addActionListener(this);
    operatorPanel.add(lteButt, cc.xy(2, 3));
    
    gtButt = new JButton(">");
    gtButt.addActionListener(this);
    operatorPanel.add(gtButt, cc.xy(3, 2));
    gteButt = new JButton(">=");
    gteButt.addActionListener(this);
    operatorPanel.add(gteButt, cc.xy(3, 3));
    
    eqButt = new JButton("=");
    eqButt.addActionListener(this);
    operatorPanel.add(eqButt, cc.xy(4, 2));
    neqButt = new JButton("!=");
    neqButt.addActionListener(this);
    operatorPanel.add(neqButt, cc.xy(4, 3));
    
    inButt = new JButton("IN");
    inButt.addActionListener(this);
    operatorPanel.add(inButt, cc.xy(5, 2));
    ninButt = new JButton("NOT IN");
    ninButt.addActionListener(this);
    operatorPanel.add(ninButt, cc.xy(5, 3));
  }
  
  private void initFilterExpressionPanel() {
    
    filterExpressionPanel = new JPanel();
    filterExpressionPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createTitledBorder("Expression de filtre"),
        BorderFactory.createEmptyBorder(10, 0, 0, 0)));
    
    queryArea = new JTextArea(20, 80);
    queryArea.setText(""); 
    JScrollPane scrollPane = new JScrollPane(queryArea); 
    filterExpressionPanel.add(scrollPane);
    
  }
  
  @Override
  public void actionPerformed(ActionEvent evt) {
    Object source = evt.getSource();
    if (source == okay) {
        this.addPostgisLayer.addQuery(this.schemaName, this.tableName, this.geomColumnName, queryArea.getText());
        dispose();
    } else if (source == cancel) {
        dispose();
    }
    this.setVisible(false);
  }

}

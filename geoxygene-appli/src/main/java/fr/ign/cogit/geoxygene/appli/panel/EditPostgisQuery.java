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
    
    /*
     * "iris in (5659, 5788, 5832, 5861, 5871, 5881, \n"
        + " 5240, 5242, 5243, 5246, 5249, 5287, 5288, 5289, 5290, 5291, 5292, 5293, 5294, 5295, 5315, \n"
        + " 5316, 5317, 5323, 5324, 5325, 5348, 5360, 5361, 5362, 5363, 5364, 5365, 5366, 5376, 5377, \n"
        + " 5381, 5383, 5384, 5385, 5386, 5387, 5388, 5389, 5391, 5575, 5576, 5577, 5581, 5606, 5609, \n"
        + " 5622, 5630, 5631, 5649, 5661, 5675, 5676, 5677, 5789, 5790, 5791, 5792, 5809, 5810, 5812, \n"
        + " 5815, 5816, 5817, 5818, 5833, 5834, 5835, 5859, 5860, 5862, 5868, 5869, 5870, 5872, 5873, \n"
        + " 5874, 5875, 5876, 5877, 5878, 5879, 5880) \n"
     */
    
    queryArea.setText("iris = '330630305' or iris = '330631101' or iris = '330630608' "
        + " or iris = '330630704' or iris = '330630313' or iris = '330630201' or iris = '330631005' "
        + " or iris = '330631006' or iris = '330631003' or iris = '330631102' or iris = '330631106' "
        + " or iris = '330631305' or iris = '330631304' or iris = '330631209' or iris = '330631203' "
        + " or iris = '330631201' or iris = '330631204' or iris = '330631207' or iris = '330631202' "
        + " or iris = '330631205' or iris = '330630504' or iris = '330630503' or iris = '330630501' "
        + " or iris = '330630601' or iris = '330630606' or iris = '330630604' or iris = '330630703' "
        + " or iris = '330630310' or iris = '330630306' or iris = '330630307' or iris = '330630311' "
        + " or iris = '330630312' or iris = '330630308' or iris = '330630303' or iris = '330630402' "
        + " or iris = '330630404' or iris = '330630905' or iris = '330630806' or iris = '330630804' "
        + " or iris = '330630801' or iris = '330630802' or iris = '330630805' or iris = '330630803' "
        + " or iris = '330630807' or iris = '330630904' or iris = '330631002' or iris = '330631004' "
        + " or iris = '330631001' or iris = '330631103' or iris = '330631301' or iris = '330631206' "
        + " or iris = '330630505' or iris = '330630609' or iris = '330630607' or iris = '330630705' "
        + " or iris = '330630808' or iris = '330630103' or iris = '330630203' or iris = '330630202' "
        + " or iris = '330631108' or iris = '330631107' or iris = '330631105' or iris = '330631104' "
        + " or iris = '330631302' or iris = '330631303' or iris = '330631208' or iris = '330630508' "
        + " or iris = '330630506' or iris = '330630507' or iris = '330630502' or iris = '330630605' "
        + " or iris = '330630603' or iris = '330630602' or iris = '330630701' or iris = '330630702' "
        + " or iris = '330630706' or iris = '330630309' or iris = '330630301' or iris = '330630302' "
        + " or iris = '330630405' or iris = '330630403' or iris = '330630401' or iris = '330630809' "
        + " or iris = '330630901' or iris = '330630902' or iris = '330630903' or iris = '330630101' "
        + " or iris = '330630204' "); 
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

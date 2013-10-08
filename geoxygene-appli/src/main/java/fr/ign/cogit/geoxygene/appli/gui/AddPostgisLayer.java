package fr.ign.cogit.geoxygene.appli.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;
import org.apache.log4j.Level;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.LayerLegendPanel;
import fr.ign.cogit.geoxygene.datatools.postgis.PostgisSpatialQuery;
import fr.ign.cogit.geoxygene.jdbc.postgis.ConnectionParam;
import fr.ign.cogit.geoxygene.jdbc.postgis.PostgisReader;


/**
 * 
 * 
 * @author Marie-Dominique Van Damme
 */
public class AddPostgisLayer extends JDialog implements ActionListener {
    
    /** Serial version UID. */
    private static final long serialVersionUID = 1L;

    /** A classic logger. */
    private Logger LOGGER = Logger.getLogger(AddPostgisLayer.class.getName());
    
    private LayerLegendPanel layerLegendPanel;
    private ConnectionParam connectionParam;
    
    private JPanel loadPanel = null;
    private JPanel layerPanel = null;
    
    /** List of connections. */
    protected JComboBox connectionList;
    
    private PgLayerTableModel modele = new PgLayerTableModel();
    private JTable tableau;
    
    /** Different buttons. */
    private JButton connectBt;
    private JButton editConnectionBt;
    private JButton newConnectionBt;
    private JButton delConnectionBt;
    
    private JButton closeBt;
    private JButton queryBt;
    private JButton addLayerBt;

    /**
     * Constructor.
     */
    public AddPostgisLayer(LayerLegendPanel layerLegendPanel) {
        
        this.layerLegendPanel = layerLegendPanel;
        this.connectionParam = layerLegendPanel.getLayerViewPanel().getProjectFrame().getMainFrame()
                .getApplication().getProperties().getConnectionParam();
        
        setModal(true);
        setTitle(I18N.getString("AddPostgisLayer.title"));
        setIconImage(new ImageIcon(
                GeOxygeneApplication.class.getResource("/images/icons/16x16/database_add.png")).getImage());
        setLocation(300, 150);
        
        initLoadPanel();
        initLayerPanel();
        
        // dispaly panel
        FormLayout layout = new FormLayout(
                "20dlu, pref, 20dlu",  // colonnes
                "10dlu, pref, pref, 20dlu");  // lignes
        getContentPane().setLayout(layout);
        // add(loadPanel, BorderLayout.NORTH);
        // add(layerPanel, BorderLayout.SOUTH);
        CellConstraints cc = new CellConstraints();
        add(loadPanel, cc.xy(2, 2));
        add(layerPanel, cc.xy(2, 3));
        
        pack();
        setVisible(true);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == connectBt) {
            // load table with all table which contains a geom column 
            connectDB();
        } else if (source == addLayerBt) {
            // 
            loadShape();
        } else if (source == editConnectionBt) {
            // 
            EditPostgisConnection editPostgisConnection = new EditPostgisConnection(this, connectionParam);
            editPostgisConnection.setSize(600, 500);
        } else if (source == closeBt) {
            dispose();
        }
    }
    
    /**
     * Display all connection.
     * Add, delete, edit and load for one connection.
     */
    private void initLoadPanel() {
        
        loadPanel = new JPanel();
        loadPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Connections"),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)));
        
        FormLayout layout = new FormLayout(
                "20dlu, pref, 5dlu, pref, 5dlu, pref, 5dlu, pref, 20dlu",  // colonnes
                "10dlu, pref, 10dlu, pref, 20dlu");  // lignes
        loadPanel.setLayout(layout);
        CellConstraints cc = new CellConstraints();
        
        String name = "jdbc:postgresql://" + connectionParam.getHost() + ":" + connectionParam.getPort() 
                + "/" + connectionParam.getDatabase();
        Object[] elements = new Object[]{name};
        connectionList = new JComboBox(elements);
        
        connectBt = new JButton(I18N.getString("AddPostgisLayer.Connect"));
        connectBt.addActionListener(this);
        editConnectionBt = new JButton(I18N.getString("AddPostgisLayer.Edit"));
        editConnectionBt.addActionListener(this);
        newConnectionBt = new JButton(I18N.getString("AddPostgisLayer.New"));
        newConnectionBt.addActionListener(this);
        newConnectionBt.setEnabled(false);
        delConnectionBt = new JButton(I18N.getString("AddPostgisLayer.Delete"));
        delConnectionBt.addActionListener(this);
        delConnectionBt.setEnabled(false);
        
        // Ligne 1
        // loadPanel.add(new JLabel(I18N.getString("AddPostgisLayer.Connection") + " : "), cc.xy(2, 2));
        
        // Ligne 2
        loadPanel.add(connectionList, cc.xyw(2, 2, 7));
        
        // Ligne 3
        loadPanel.add(connectBt, cc.xy(2, 4));
        loadPanel.add(editConnectionBt, cc.xy(4, 4));
        loadPanel.add(newConnectionBt, cc.xy(6, 4));
        loadPanel.add(delConnectionBt, cc.xy(8, 4));
    }
    
    /**
     * Display all tables for one connection
     */
    private void initLayerPanel() {
        
        layerPanel = new JPanel();
        layerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(""),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)));
        FormLayout layout = new FormLayout(
                "20dlu, 200dlu, pref, 5dlu, pref, 5dlu, pref, 20dlu",  // colonnes
                "20dlu, pref, pref, 20dlu, pref, 30dlu");  // lignes
        layerPanel.setLayout(layout);
        CellConstraints cc = new CellConstraints();
        
        // Tableau
        // tableau = new JTable(data, rowHeader);
        tableau = new JTable(modele);
        // JScrollPane scrollpane = new JScrollPane(tableau);
        
        layerPanel.add(tableau.getTableHeader(), cc.xyw(2, 2, 6));
        layerPanel.add(new JScrollPane(tableau), cc.xyw(2, 3, 6));
        
        // Buttons
        closeBt = new JButton(I18N.getString("AddPostgisLayer.Close"));
        closeBt.addActionListener(this);
        
        queryBt = new JButton(I18N.getString("AddPostgisLayer.Query"));
        queryBt.addActionListener(this);
        queryBt.setEnabled(false);
        
        addLayerBt = new JButton(I18N.getString("AddPostgisLayer.AddLayer"));
        addLayerBt.addActionListener(this);
        
        layerPanel.add(new JLabel(""), cc.xy(2, 5));
        layerPanel.add(addLayerBt, cc.xy(3, 5));
        layerPanel.add(queryBt, cc.xy(5, 5));
        layerPanel.add(closeBt, cc.xy(7, 5));
    }
    
    /**
     * Connect to the database, then load all tables wich contain geom column.
     */
    private void connectDB() {
        
        try {
            
            // On vide le tableau
            for (int i = 0; i < modele.getRowCount(); i++) {
                modele.removePgLayer(i);
            }
            
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + connectionParam.getHost() + ":" + connectionParam.getPort() 
                    + "/" + connectionParam.getDatabase(), connectionParam.getUser(), connectionParam.getPasswd());
            Map<Integer, Map<String, String>> resultat = PostgisSpatialQuery.getTables(connection);
            connection.close();
            
            if (resultat != null && resultat.size() > 0) {
                for (int i = 0; i < resultat.size(); i++) {
                    Map<String, String> row = resultat.get(i);
                    modele.addPgLayer(new PgLayer(row.get("schema"), 
                            row.get("table"), 
                            row.get("type"), 
                            row.get("geometry_colum"), 
                            row.get("srid")));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 
     */
    private void loadShape() {
        
        // Get the layer
        int[] selection = tableau.getSelectedRows();
        if (selection.length <= 0) {
            javax.swing.JOptionPane.showMessageDialog(null, I18N.getString("AddPostgisLayer.OneTable"));
        } else {
        
            Map<String,String> params = new HashMap<String,String>();
            params.put("dbtype", connectionParam.getDbtype());
            params.put("host", connectionParam.getHost());
            params.put("port", connectionParam.getPort());
            params.put("database", connectionParam.getDatabase());
            params.put("user", connectionParam.getUser());
            params.put("passwd", connectionParam.getPasswd());
            
            try {
                
                for (int i = 0; i < selection.length; i++) {
                
                    String schemaValue = modele.getValueAt(selection[i], 0).toString();
                    params.put("schema", schemaValue);
                    LOGGER.log(Level.DEBUG, "Chargement dans le schema : " + schemaValue);
                    String tableValue = modele.getValueAt(selection[i], 1).toString();
                    LOGGER.log(Level.DEBUG, "Chargement de la table : " + tableValue);
                    
                    IPopulation<IFeature> pop = PostgisReader.read(params, tableValue, tableValue, null, false);
                    LOGGER.log(Level.DEBUG, "Nb features = " + pop.size());
                    
                    layerLegendPanel.getLayerViewPanel().getProjectFrame().addUserLayer(pop, tableValue, null);
                }
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // Then close the window
            dispose();
            
        }
    }
    
    public void setConnectionParam(ConnectionParam cp) {
        connectionParam = cp;
    }
    
    public LayerLegendPanel getLayerLegendPanel() {
        return layerLegendPanel;
    }

}


class PgLayer {
    String schema;
    String table;
    String type;
    String geomCol;
    String srid;
    
    public PgLayer() {
        schema = "";
        table = "";
        type = "";
        geomCol = "";
        srid = "";
    }
    
    public PgLayer(String s, String ta, String ty, String g, String p) {
        schema = s;
        table = ta;
        type = ty;
        geomCol = g;
        srid = p;
    }
    
    public String getSchema() {
        return schema;
    }
    public String getTable() {
        return table;
    }
    public String getType() {
        return type;
    }
    public String getGeomCol() {
        return geomCol;
    }
    public String getSrid() {
        return srid;
    }
}

class PgLayerTableModel extends AbstractTableModel {
    
    /** Serial ID. */
    private static final long serialVersionUID = 1L;

    /** Data. */
    private final List<PgLayer> data = new ArrayList<PgLayer>();;
    
    private final String[] rowHeader = {I18N.getString("AddPostgisLayer.schema"), 
            I18N.getString("AddPostgisLayer.table"), 
            I18N.getString("AddPostgisLayer.type"), 
            I18N.getString("AddPostgisLayer.geometryColumn"), 
            I18N.getString("AddPostgisLayer.srid")};
 
    public PgLayerTableModel() {
        super();
        data.add(new PgLayer());
    }
 
    @Override
    public int getRowCount() {
        return data.size();
    }
 
    @Override
    public int getColumnCount() {
        return rowHeader.length;
    }
 
    @Override
    public String getColumnName(int columnIndex) {
        return rowHeader[columnIndex];
    }
 
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch(columnIndex){
            case 0:
                return data.get(rowIndex).getSchema();
            case 1:
                return data.get(rowIndex).getTable();
            case 2:
                return data.get(rowIndex).getType();
            case 3:
                return data.get(rowIndex).getGeomCol();
            case 4:
                return data.get(rowIndex).getSrid();
            default:
                return null; // Ne devrait jamais arriver
        }
    }
    
    public void addPgLayer(PgLayer pgLayer) {
        data.add(pgLayer);
        fireTableRowsInserted(data.size() -1, data.size() -1);
    }
 
    public void removePgLayer(int rowIndex) {
        data.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
}

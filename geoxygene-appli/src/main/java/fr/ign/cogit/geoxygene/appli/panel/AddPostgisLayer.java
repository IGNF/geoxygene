package fr.ign.cogit.geoxygene.appli.panel;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

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
    private JPanel queryPanel = null;
    private JPanel tablePanel = null;
    private JPanel buttonPanel = null;
    
    /** List of connections. */
    @SuppressWarnings("rawtypes")
    protected JComboBox connectionList;
    
    private PgLayerTableModel modeleTable = new PgLayerTableModel();
    private PgQueryTableModel modeleQuery = new PgQueryTableModel();
    private JTable tableauTable;
    private JTable tableauQuery;
    
    /** Different buttons. */
    private JButton connectBt;
    private JButton editConnectionBt;
    private JButton newConnectionBt;
    private JButton delConnectionBt;
    
    private JButton closeBt;
    private JButton addLayerBt;
    
    protected JComboBox<String> tableQueryList;
    private JButton addQueryBt;
    

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
                GeOxygeneApplication.class.getResource("/images/toolbar/database_add.png")).getImage());
        setLocation(300, 50);
        
        initLoadPanel();
        initTablePanel();
        initQueryPanel();
        initButtonPanel();
        
        // dispaly panel
        FormLayout layout = new FormLayout(
                "20dlu, pref, 20dlu",  // colonnes
                "10dlu, pref, pref, pref, pref, 20dlu");  // lignes
        getContentPane().setLayout(layout);
        CellConstraints cc = new CellConstraints();
        add(loadPanel, cc.xy(2, 2));
        add(tablePanel, cc.xy(2, 3));
        add(queryPanel, cc.xy(2, 4));
        add(buttonPanel, cc.xy(2, 5));
        
        pack();
        setVisible(true);
        
    }
    
    @Override
    public void actionPerformed(ActionEvent evt) {
        Object source = evt.getSource();
        if (source == connectBt) {
            // load table with all table which contains a geom column 
            connectDB();
        } else if (source == addQueryBt) {
          @SuppressWarnings("unused")
          EditPostgisQuery query = new EditPostgisQuery(this);
        } else if (source == addLayerBt) {
            loadShape();
        } else if (source == editConnectionBt) {
            @SuppressWarnings("unused")
            EditPostgisConnection editPostgisConnection = new EditPostgisConnection(this, connectionParam);
        } else if (source == closeBt) {
            dispose();
        } 
    }
    
    /**
     * Display all connection.
     * Add, delete, edit and load for one connection.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
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
    
    private void initButtonPanel() {
      
      buttonPanel = new JPanel();
      buttonPanel.setBorder(BorderFactory.createCompoundBorder(
              BorderFactory.createTitledBorder(""),
              BorderFactory.createEmptyBorder(10, 0, 0, 0)));
      
      FormLayout layout = new FormLayout(
              "20dlu, pref, 5dlu, pref, 5dlu, pref, 20dlu",  // colonnes
              "10dlu, pref, 20dlu");  // lignes
      buttonPanel.setLayout(layout);
      CellConstraints cc = new CellConstraints();
      
      // Buttons
      closeBt = new JButton(I18N.getString("AddPostgisLayer.Close"));
      closeBt.addActionListener(this);
      
      addLayerBt = new JButton(I18N.getString("AddPostgisLayer.AddLayer"));
      addLayerBt.addActionListener(this);
      
      // buttonPanel.add(addQueryBt, cc.xy(2, 2));
      buttonPanel.add(addLayerBt, cc.xy(4, 2));
      buttonPanel.add(closeBt, cc.xy(6, 2));
    }
    
    /**
     * Display all tables for one connection
     */
    private void initTablePanel() {
        
        tablePanel = new JPanel();
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Table"),
                BorderFactory.createEmptyBorder(10, 0, 0, 0)));
        FormLayout layout = new FormLayout(
                "20dlu, pref, 20dlu",  // colonnes  200dlu, pref, 5dlu, 
                "20dlu, pref, 120dlu, 30dlu");  // lignes
        tablePanel.setLayout(layout);
        CellConstraints cc = new CellConstraints();
        
        // Tableau
        tableauTable = new JTable(modeleTable);
       
        tablePanel.add(tableauTable.getTableHeader(), cc.xy(2, 2));
        tablePanel.add(new JScrollPane(tableauTable), cc.xy(2, 3));
        
    }
    
    private void initQueryPanel() {
      queryPanel = new JPanel();
      queryPanel.setBorder(BorderFactory.createCompoundBorder(
          BorderFactory.createTitledBorder("Query"),
          BorderFactory.createEmptyBorder(10, 0, 0, 0)));
  
      FormLayout layout = new FormLayout(
          "20dlu, pref, 5dlu, pref, 20dlu",  // colonnes
          "10dlu, pref, 5dlu, pref, 80dlu, 10dlu, pref, 20dlu");  // lignes
      queryPanel.setLayout(layout);
      CellConstraints cc = new CellConstraints();
      
      String[] elements = new String[]{"---"};
      tableQueryList = new JComboBox<String>(elements);
      queryPanel.add(tableQueryList, cc.xy(2, 2));
      
      addQueryBt = new JButton("Add query");
      addQueryBt.addActionListener(this);
      queryPanel.add(addQueryBt, cc.xy(4, 2));
      
      tableauQuery = new JTable(modeleQuery);
      queryPanel.add(tableauQuery.getTableHeader(), cc.xyw(2, 4, 3));
      queryPanel.add(new JScrollPane(tableauQuery), cc.xyw(2, 5, 3));
      
      // addQueryBt = new JButton("Add query");
      // addQueryBt.addActionListener(this);
      // queryPanel.add(addQueryBt, cc.xy(2, 5));
      
    }
    
    /**
     * Connect to the database, then load all tables wich contain geom column.
     */
    private void connectDB() {
        
        try {
            
            // On vide le tableau
            for (int i = 0; i < modeleTable.getRowCount(); i++) {
              modeleTable.removePgLayer(i);
            }
            // On vide la liste déroulante
            tableQueryList.removeAllItems();
            
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + connectionParam.getHost() + ":" + connectionParam.getPort() 
                    + "/" + connectionParam.getDatabase(), connectionParam.getUser(), connectionParam.getPasswd());
            Map<Integer, Map<String, String>> resultat = PostgisSpatialQuery.getTables(connection);
            connection.close();
            
            if (resultat != null && resultat.size() > 0) {
                for (int i = 0; i < resultat.size(); i++) {
                    Map<String, String> row = resultat.get(i);
                    modeleTable.addPgLayer(new PgLayer(row.get("schema"), 
                            row.get("table"), 
                            row.get("type"), 
                            row.get("geometry_colum"), 
                            row.get("srid")
                        ));
                    
                    tableQueryList.addItem(row.get("schema") + " - " + row.get("table") + " - " + row.get("geometry_colum"));
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    protected void addQuery(String schema, String table, String geomColumn, String txtQuery) {
      this.modeleQuery.addPgLayer(new QueryLayer(schema, table, geomColumn, txtQuery));
    }
    
    /**
     * 
     */
    private void loadShape() {
        
        // Get the layer
        int[] selectionTable = tableauTable.getSelectedRows();
        int[] selectionQuery = tableauQuery.getSelectedRows();
        if ((selectionTable.length + selectionQuery.length) <= 0) {
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
                
                for (int i = 0; i < selectionTable.length; i++) {
                    String schemaValue = modeleTable.getValueAt(selectionTable[i], 0).toString();
                    params.put("schema", schemaValue);
                    LOGGER.log(Level.DEBUG, "Chargement dans le schema : " + schemaValue);
                    String tableValue = modeleTable.getValueAt(selectionTable[i], 1).toString();
                    LOGGER.log(Level.DEBUG, "Chargement de la table : " + tableValue);
                    String geomColumnValue = modeleTable.getValueAt(selectionTable[i], 3).toString();
                    LOGGER.log(Level.DEBUG, "Geometry column : " + geomColumnValue);
                    
                    IPopulation<IFeature> pop = PostgisReader.read(params, tableValue, tableValue, null, false, geomColumnValue);
                    layerLegendPanel.getLayerViewPanel().getProjectFrame().addUserLayer(pop, tableValue + " " + i, null);
                }
                
                for (int i = 0; i < selectionQuery.length; i++) {
                  
                  String schemaValue = modeleQuery.getValueAt(selectionQuery[i], 0).toString();
                  params.put("schema", schemaValue);
                  LOGGER.log(Level.DEBUG, "Chargement dans le schema : " + schemaValue);
                  String tableValue = modeleQuery.getValueAt(selectionQuery[i], 1).toString();
                  LOGGER.log(Level.DEBUG, "Chargement de la table : " + tableValue);
                  String geomColumnValue = modeleQuery.getValueAt(selectionQuery[i], 2).toString();
                  LOGGER.log(Level.DEBUG, "Geometry column : " + geomColumnValue);
                  String filter = modeleQuery.getValueAt(selectionQuery[i], 3).toString();
                  LOGGER.log(Level.DEBUG, "Filter : " + filter);
                  
                  IPopulation<IFeature> pop = PostgisReader.read(params, tableValue, tableValue, null, false, geomColumnValue, filter);
                  LOGGER.log(Level.DEBUG, "Nb features = " + pop.size());
                  
                  layerLegendPanel.getLayerViewPanel().getProjectFrame().addUserLayer(pop, tableValue + " " + i, null);
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
            I18N.getString("AddPostgisLayer.srid")
    };
 
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

class QueryLayer {
  String schema;
  String table;
  String geomCol;
  String query;
  
  public QueryLayer() {
    schema = "";
    table = "";
    geomCol = "";
    query = "";
  }
  
  public QueryLayer(String s, String ta, String g, String q) {
    schema = s;
    table = ta;
    geomCol = g;
    query = q;
  }
  
  public String getSchema() {
    return schema;
  }
  public String getTable() {
    return table;
  }
  public String getGeomCol() {
    return geomCol;
  }
  public String getQuery() {
      return query;
  }
}

class PgQueryTableModel extends AbstractTableModel {
  
  /** Serial ID. */
  private static final long serialVersionUID = 1L;

  /** Data. */
  private final List<QueryLayer> data = new ArrayList<QueryLayer>();;
  
  private final String[] rowHeader = {I18N.getString("AddPostgisLayer.schema"), 
      I18N.getString("AddPostgisLayer.table"), 
      I18N.getString("AddPostgisLayer.geometryColumn"), "Filter"};

  public PgQueryTableModel() {
      super();
      // data.add(new QueryLayer());
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
  public String getValueAt(int rowIndex, int columnIndex) {
      switch(columnIndex){
          case 0:
              return data.get(rowIndex).getSchema();
          case 1:
            return data.get(rowIndex).getTable();
          case 2:
            return data.get(rowIndex).getGeomCol();
          case 3:
              return data.get(rowIndex).getQuery();
          default:
              return null; // Ne devrait jamais arriver
      }
  }
  
  public void addPgLayer(QueryLayer query) {
      data.add(query);
      fireTableRowsInserted(data.size() -1, data.size() -1);
  }

  public void removePgLayer(int rowIndex) {
      data.remove(rowIndex);
      fireTableRowsDeleted(rowIndex, rowIndex);
  }
}

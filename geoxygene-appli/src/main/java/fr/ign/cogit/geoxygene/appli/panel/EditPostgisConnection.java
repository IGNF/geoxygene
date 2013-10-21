package fr.ign.cogit.geoxygene.appli.panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.jdbc.postgis.ConnectionParam;

/**
 * 
 * 
 *
 */
public class EditPostgisConnection extends JDialog implements ActionListener {
    
    /** Serial version id. */
    private static final long serialVersionUID = 1L;
    
    private AddPostgisLayer addPostgisLayer;
    private ConnectionParam connectionParam;
    
    private JParamField databaseField;
    private JParamField dbtype;
    private JParamField host;
    private JParamField port;
    private JParamField schema;
    private JParamField user;
    private JParamField passwd;
    private JButton okay;
    private JButton cancel;
    
    public enum ParamType {
        DBTYPE, HOST, PORT, DATABASE, SCHEMA, USER, PASSWD
    };
    
    public EditPostgisConnection(AddPostgisLayer addPostgisLayer, ConnectionParam param) {
        
        this.addPostgisLayer = addPostgisLayer;
        connectionParam = param;
        
        setModal(true);
        setTitle(I18N.getString("AddPostgisLayer.title"));
        setIconImage(new ImageIcon(
                GeOxygeneApplication.class.getResource("/images/toolbar/database_add.png")).getImage());
        setLocation(300, 150);
        
        initConnectionPanel(connectionParam);
        
        pack();
        setVisible(true);
    }
    
    
    private void initConnectionPanel(ConnectionParam config) {
        
        dbtype = new JParamField(ParamType.DBTYPE, config);
        host = new JParamField(ParamType.HOST, config);
        port = new JParamField(ParamType.PORT, config);
        
        schema = new JParamField(ParamType.SCHEMA, config);
        databaseField = new JParamField(ParamType.DATABASE, config);
        user = new JParamField(ParamType.USER, config);
        passwd = new JParamField(ParamType.PASSWD, config);            

        okay = new JButton("OK");
        cancel = new JButton("Cancel");
        
        okay.addActionListener( this );
        cancel.addActionListener( this );
        
        // layout dialog
        setLayout( new GridLayout(0,2));
        
        add(new JLabel("DBType") );
        add(dbtype);
        add(new JLabel("Host"));
        add(host);
        add(new JLabel("Port"));
        add(port);
        add(new JLabel("Schema"));
        add(schema);
        add(new JLabel("Database"));
        add(databaseField);
        add(new JLabel("user"));
        add(user);
        add(new JLabel("password"));
        add(passwd);
        
        add(new JLabel(""));
        JPanel buttons = new JPanel();
        add(buttons);         
        buttons.add(okay);
        buttons.add(cancel);
        
        setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
        Dimension preferredSize = getPreferredSize();
        preferredSize.height += 30;
        setSize(preferredSize);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("OK")) {
            
            //
            ConnectionParam newConnectionParam = new ConnectionParam();
            newConnectionParam.dbtype = dbtype.getText();
            newConnectionParam.host = host.getText();
            newConnectionParam.port = port.getText();
            newConnectionParam.database = databaseField.getText();
            newConnectionParam.schema = schema.getText();
            newConnectionParam.user = user.getText();
            newConnectionParam.passwd = passwd.getText();
            // Set
            connectionParam = newConnectionParam;
            // Set new connection param to postgis layer
            addPostgisLayer.setConnectionParam(newConnectionParam);
            addPostgisLayer.getLayerLegendPanel().getLayerViewPanel().getProjectFrame().getMainFrame()
                .getApplication().getProperties().setConnectionParam(newConnectionParam);
            // Refreh connection list.
            addPostgisLayer.connectionList.removeAllItems();
            String name = "jdbc:postgresql://" + connectionParam.getHost() + ":" + connectionParam.getPort() 
                    + "/" + connectionParam.getDatabase();
            addPostgisLayer.connectionList.addItem(name);
            System.out.println(name + " ajouté");
        }
        setVisible(false);
    }
    
    
    class JParamField extends JTextField {
        
        ConnectionParam param;
        ParamType paramType;
        String value;
        /** Serial version id. */
        private static final long serialVersionUID = 1L;
        
        JParamField(ParamType key, ConnectionParam param) {
            super(20);
            this.paramType = key;
            this.param = param;
            switch(paramType) {
                case DBTYPE : setValue(param.getDbtype());break;
                case DATABASE : setValue(param.getDatabase());break;
                case HOST : setValue(param.getHost());break;
                case PORT : setValue(param.getPort());break;
                case SCHEMA : setValue(param.getSchema());break;
                case USER : setValue(param.getUser());break;
                case PASSWD : setValue(param.getPasswd());break;
                default:;
            }
            
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    refresh();
                }
            });
            // setToolTipText(param.description.toString());
        }
        
        public void refresh() {
            try {
                JParamField.this.value = value;
                // setToolTipText(param.toString());
                setForeground(Color.BLACK);
            } catch (Throwable e) {
                setToolTipText(e.getLocalizedMessage());
                setForeground(Color.RED);
                JParamField.this.value = null;
            }
        }
        
        public void setValue(String value) {
            if (value == null) {
                value = "";
            }
            this.value = value;
            setText(value);         
        }
        
        public String getValue() {
            return value;
        }
    }

}

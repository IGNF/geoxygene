/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.ojb.broker.PBKey;
import org.apache.ojb.broker.metadata.ConnectionRepository;
import org.apache.ojb.broker.metadata.JdbcConnectionDescriptor;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.apache.ojb.broker.metadata.SequenceDescriptor;

/**
 * @author Julien Perret
 * 
 */
public class MetadataLoader extends JFrame implements ListDataListener {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private boolean validated = false;
  private JList combo = new JList();
  private DescriptorListModel listModel = null;//new DefaultListModel();

  public JdbcConnectionDescriptor getSelectedItem() {
    return (JdbcConnectionDescriptor) this.combo.getSelectedValue();
  }

  public MetadataLoader() {
  }

  /**
   * Création du JDialog.
   * @param parent Frame parent du JDialog à créer.
   * @return JDialog
   */
  private JDialog createDialog(Frame parent) {
    final JDialog dialog = new JDialog(parent, "Choose database", true);
    Container contentPane = dialog.getContentPane();
    contentPane.setLayout(new BorderLayout());

    JPanel controlPanel = new JPanel();
    controlPanel.setLayout(new GridLayout(4, 1));
    JButton addButton = new JButton("Add");
    addButton.setActionCommand("Add");
    addButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DataBaseFrame newDBFrame = new DataBaseFrame();
        boolean valid = newDBFrame.showDialog();
        System.out.println(valid + " - " + newDBFrame.getDescriptor());
        if (valid) {
          listModel.add(newDBFrame.getDescriptor());
        }
        newDBFrame.dispose();
      }
    });
    JButton editButton = new JButton("Edit");
    editButton.setActionCommand("Edit");
    editButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String jcdAlias = getSelectedItem().getJcdAlias();
        DataBaseFrame newDBFrame = new DataBaseFrame(getSelectedItem(), true);
        boolean valid = newDBFrame.showDialog();
        System.out.println(valid + " - " + newDBFrame.getDescriptor());
        if (valid) {
          PBKey key = listModel.repository.getStandardPBKeyForJcdAlias(jcdAlias);
          // we have to remove it using the previous standard pbkey
          listModel.repository.removeDescriptor(key);
          // and add it again with its new pbkey
          listModel.add(newDBFrame.getDescriptor());
          contentsChanged(null);
        }
        newDBFrame.dispose();
      }
    });
    JButton copyButton = new JButton("Copy");
    copyButton.setActionCommand("Copy");
    copyButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        DataBaseFrame newDBFrame = new DataBaseFrame(getSelectedItem(), false);
        boolean valid = newDBFrame.showDialog();
        System.out.println(valid + " - " + newDBFrame.getDescriptor());
        if (valid) {
          listModel.add(newDBFrame.getDescriptor());
        }
        newDBFrame.dispose();
      }
    });
    JButton deleteButton = new JButton("Delete");
    deleteButton.setActionCommand("Delete");
    deleteButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        listModel.remove(getSelectedItem());
      }
    });
    controlPanel.add(addButton);
    controlPanel.add(editButton);
    controlPanel.add(copyButton);
    controlPanel.add(deleteButton);
    // controlPanel.add(new V);
    contentPane.add(controlPanel, BorderLayout.EAST);

    MetadataManager mm = MetadataManager.getInstance();
    ConnectionRepository cr = mm.connectionRepository();
    this.listModel = new DescriptorListModel(cr);
    this.combo.setModel(this.listModel);
    this.combo.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    ListRenderer renderer = new ListRenderer();
    this.combo.setCellRenderer(renderer);

    this.listModel.addListDataListener(this);
//    for (Object c : cr.getAllDescriptor()) {
//      JdbcConnectionDescriptor descriptor = (JdbcConnectionDescriptor) c;
//      // System.out.println(descriptor);
//      listModel.add(descriptor);
//    }
    contentPane.add(this.combo, BorderLayout.CENTER);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
    JButton okButton = new JButton("Ok");
    okButton.setActionCommand("Ok");
    okButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        validated = true;
        dialog.dispose();
      }
    });
    JButton cancelButton = new JButton("Annuler");
    cancelButton.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        dialog.dispose();
      }
    });
    buttonPanel.add(okButton);
    buttonPanel.add(cancelButton);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);
    dialog.pack();
    dialog.setLocationRelativeTo(parent);
    return dialog;
  }

  /**
   * Création du JDialog de sélection des fichiers.
   * @return vrai si le JDialog a été validé (grâce au bouton "Ok"), faux sinon.
   */
  public boolean showDialog() {
    final JDialog dialog = createDialog(this);
    dialog.setVisible(true);
    dialog.dispose();
    return validated;
  }

  public static String getConnection() {
    MetadataLoader loader = new MetadataLoader();
    boolean valid = loader.showDialog();
    String connection = null;
    if (valid) {
//      System.out.println(valid + " - " + loader.getSelectedItem());
      connection = loader.getSelectedItem().getJcdAlias();
    }
    loader.dispose();
    return connection;
  }
  /**
   * @param args
   */
  public static void main(String[] args) {
    MetadataLoader loader = new MetadataLoader();
    boolean valid = loader.showDialog();
    System.out.println(valid + " - " + loader.getSelectedItem());
    loader.dispose();
    
    MetadataManager mm = MetadataManager.getInstance();
    ConnectionRepository cr = mm.connectionRepository();
    System.out.println(cr.toXML());
    // for (Annotation annotation : Batiment.class.getAnnotations()) {
    // System.out.println(annotation + " " + annotation.annotationType());
    // }
    // for (Field field : Batiment.class.getDeclaredFields()) {
    // System.out.println(field);
    // }
  }

  class ListRenderer extends JLabel implements ListCellRenderer {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public ListRenderer() {
      setOpaque(true);
      setHorizontalAlignment(LEFT);
      setVerticalAlignment(CENTER);
    }
    @Override
    public Component getListCellRendererComponent(JList list, Object value,
        int index, boolean isSelected, boolean cellHasFocus) {
      // Get the selected index. (The index param isn't
      // always valid, so just use the value.)
      JdbcConnectionDescriptor selected = (JdbcConnectionDescriptor) value;
      if (isSelected) {
        setBackground(list.getSelectionBackground());
        setForeground(list.getSelectionForeground());
      } else {
        setBackground(list.getBackground());
        setForeground(list.getForeground());
      }
      setText(selected.getJcdAlias().trim());
      return this;
    }
  }

  class DataBaseFrame extends JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private boolean validatedDB = false;
    private JdbcConnectionDescriptor descriptor = null;

    public JdbcConnectionDescriptor getDescriptor() {
      return this.descriptor;
    }

    private JTextField alias = new JTextField("alias");
    private JTextField server = new JTextField("localhost");
    private JTextField port = new JTextField("5432");
    private JTextField database = new JTextField("database");
    private JTextField user = new JTextField("postgres");
    private JPasswordField password = new JPasswordField("postgres");

    public DataBaseFrame() {
      this.descriptor = new JdbcConnectionDescriptor();
      this.descriptor.setDbms("PostgreSQL");
      this.descriptor.setProtocol("jdbc");
      this.descriptor.setSubProtocol("postgresql");
      this.descriptor.setDriver("org.postgresql.Driver");
      this.descriptor.setBatchMode(true);
      this.descriptor.setUseAutoCommit(2);
      SequenceDescriptor sd = new SequenceDescriptor(this.descriptor,
          org.apache.ojb.broker.util.sequence.SequenceManagerInMemoryImpl.class);
      this.descriptor.setSequenceDescriptor(sd);
    }

    /**
     * @param selectedDescriptor
     */
    public DataBaseFrame(JdbcConnectionDescriptor selectedDescriptor,
        boolean edit) {
      if (edit) {
        this.descriptor = selectedDescriptor;
      } else {
        this.descriptor = new JdbcConnectionDescriptor();
        this.descriptor.setJcdAlias(selectedDescriptor.getJcdAlias());
        this.descriptor.setDbms(selectedDescriptor.getDbms());
        this.descriptor.setProtocol(selectedDescriptor.getProtocol());
        this.descriptor.setSubProtocol(selectedDescriptor.getSubProtocol());
        this.descriptor.setDriver(selectedDescriptor.getDriver());
        this.descriptor.setBatchMode(selectedDescriptor.getBatchMode());
        this.descriptor.setUseAutoCommit(selectedDescriptor.getUseAutoCommit());
        this.descriptor.setDbAlias(selectedDescriptor.getDbAlias());
        this.descriptor.setUserName(selectedDescriptor.getUserName());
        this.descriptor.setPassWord(selectedDescriptor.getPassWord());
        this.descriptor.setSequenceDescriptor(selectedDescriptor
            .getSequenceDescriptor());
      }
      alias.setText(this.descriptor.getJcdAlias());
      int i1 = this.descriptor.getDbAlias().indexOf("//");
      int i2 = this.descriptor.getDbAlias().substring(i1).indexOf(":");
      int i3 = this.descriptor.getDbAlias().substring(i1).substring(i2)
          .indexOf("/");
      server.setText(this.descriptor.getDbAlias().substring(i1 + 2, i1 + i2));
      port.setText(this.descriptor.getDbAlias().substring(i1 + i2 + 1,
          i1 + i2 + i3));
      database
          .setText(this.descriptor.getDbAlias().substring(i1 + i2 + i3 + 1));
      user.setText(this.descriptor.getUserName());
      password.setText(this.descriptor.getPassWord());
    }

    /**
     * Création du JDialog.
     * @param parent Frame parent du JDialog à créer.
     * @return JDialog
     */
    private JDialog createDialog(Frame parent) {
      final JDialog dialog = new JDialog(parent, "New database", true);
      Container contentPane = dialog.getContentPane();
      contentPane.setLayout(new BorderLayout());

      GridLayout l = new GridLayout(6, 2);
      JPanel configurationPanel = new JPanel(l);
      configurationPanel.add(new JLabel("Name"));
      configurationPanel.add(this.alias);
      configurationPanel.add(new JLabel("Server"));
      configurationPanel.add(this.server);
      configurationPanel.add(new JLabel("Port"));
      configurationPanel.add(this.port);
      configurationPanel.add(new JLabel("Database"));
      configurationPanel.add(this.database);
      configurationPanel.add(new JLabel("User"));
      configurationPanel.add(this.user);
      configurationPanel.add(new JLabel("Password"));
      configurationPanel.add(this.password);

      contentPane.add(configurationPanel, BorderLayout.CENTER);
      JPanel buttonPanel = new JPanel();
      buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
      JButton okButton = new JButton("Ok");
      okButton.setActionCommand("Ok");
      okButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          validatedDB = true;
          // if (edit) {
          // } else {
          // descriptor = new JdbcConnectionDescriptor();
          // descriptor.setDbms("PostgreSQL");
          // descriptor.setProtocol("jdbc");
          // descriptor.setSubProtocol("postgresql");
          // descriptor.setDriver("org.postgresql.Driver");
          // descriptor.setBatchMode(true);
          // descriptor.setUseAutoCommit(2);
          // SequenceDescriptor sd = new SequenceDescriptor(descriptor,
          // org.apache.ojb.broker.util.sequence.SequenceManagerInMemoryImpl.class);
          // descriptor.setSequenceDescriptor(sd);
          // }
//          System.out.println(alias.getText());
          descriptor.setJcdAlias(alias.getText());
//          System.out.println(descriptor.getPBKey().getAlias());
//          System.out.println("//" + server.getText() + ":" + port.getText()
//              + "/" + database.getText());
          descriptor.setDbAlias("//" + server.getText() + ":" + port.getText()
              + "/" + database.getText());
          descriptor.setUserName(user.getText());
          descriptor.setPassWord(new String(password.getPassword()));
          dialog.dispose();
        }
      });
      JButton cancelButton = new JButton("Annuler");
      cancelButton.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
          dialog.dispose();
        }
      });
      buttonPanel.add(okButton);
      buttonPanel.add(cancelButton);
      contentPane.add(buttonPanel, BorderLayout.SOUTH);
      dialog.pack();
      dialog.setLocationRelativeTo(parent);
      return dialog;
    }

    /**
     * Création du JDialog de sélection des fichiers.
     * @return vrai si le JDialog a été validé (grâce au bouton "Ok"), faux
     *         sinon.
     */
    public boolean showDialog() {
      final JDialog dialog = createDialog(this);
      dialog.setVisible(true);
      dialog.dispose();
      return this.validatedDB;
    }
  }
  class DescriptorListModel implements ListModel {
    private ConnectionRepository repository = null;
    public DescriptorListModel(ConnectionRepository repository) {
      this.repository = repository;
    }
    @Override
    public Object getElementAt(int index) {
      JdbcConnectionDescriptor d = (JdbcConnectionDescriptor) this.repository
          .getAllDescriptor().get(index);
//      System.out.println("jcd alias = " + d.getJcdAlias());
      PBKey key = this.repository.getStandardPBKeyForJcdAlias(d.getJcdAlias());
//      System.out.println("std key = " + key);
      if (key == null) {
//        this.repository.removeDescriptor(d);
//        System.out.println("key = " + d.getPBKey());
//        System.out.println("desc = " + this.repository.getDescriptor(d.getPBKey()));
        return d;
      }
      JdbcConnectionDescriptor descriptor = this.repository.getDescriptor(key);
      return descriptor;
    }
    @Override
    public int getSize() {
      return this.repository.getAllDescriptor().size();
    }
    public void add(JdbcConnectionDescriptor d) {
      int index = this.getSize();
      this.repository.addDescriptor(d);
      this.fireIntervalAdded(this, index, index);
    }
    public void remove(JdbcConnectionDescriptor d) {
      this.repository.removeDescriptor(d);
      fireContentsChanged(this, 0, this.getSize());
    }
    protected EventListenerList listenerList = new EventListenerList();
    /**
     * Adds a listener to the list that's notified each time a change
     * to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be added
     */  
    public void addListDataListener(ListDataListener l) {
      this.listenerList.add(ListDataListener.class, l);
    }
    /**
     * Removes a listener from the list that's notified each time a 
     * change to the data model occurs.
     *
     * @param l the <code>ListDataListener</code> to be removed
     */  
    public void removeListDataListener(ListDataListener l) {
      this.listenerList.remove(ListDataListener.class, l);
    }
    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b>
     * one or more elements of the list change.  The changed elements
     * are specified by the closed interval index0, index1 -- the endpoints
     * are included.  Note that
     * index0 need not be less than or equal to index1.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireContentsChanged(Object source, int index0, int index1)
    {
      Object[] listeners = listenerList.getListenerList();
      ListDataEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (listeners[i] == ListDataListener.class) {
          if (e == null) {
            e = new ListDataEvent(source, ListDataEvent.CONTENTS_CHANGED, index0, index1);
          }
          ((ListDataListener)listeners[i+1]).contentsChanged(e);
        }          
      }
    }
    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b>
     * one or more elements are added to the model.  The new elements
     * are specified by a closed interval index0, index1 -- the enpoints
     * are included.  Note that
     * index0 need not be less than or equal to index1.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the new interval
     * @param index1 the other end of the new interval
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalAdded(Object source, int index0, int index1)
    {
      Object[] listeners = listenerList.getListenerList();
      ListDataEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (listeners[i] == ListDataListener.class) {
          if (e == null) {
            e = new ListDataEvent(source, ListDataEvent.INTERVAL_ADDED, index0, index1);
          }
          ((ListDataListener)listeners[i+1]).intervalAdded(e);
        }          
      }
    }
    /**
     * <code>AbstractListModel</code> subclasses must call this method
     * <b>after</b> one or more elements are removed from the model. 
     * <code>index0</code> and <code>index1</code> are the end points
     * of the interval that's been removed.  Note that <code>index0</code>
     * need not be less than or equal to <code>index1</code>.
     * 
     * @param source the <code>ListModel</code> that changed, typically "this"
     * @param index0 one end of the removed interval,
     *               including <code>index0</code>
     * @param index1 the other end of the removed interval,
     *               including <code>index1</code>
     * @see EventListenerList
     * @see DefaultListModel
     */
    protected void fireIntervalRemoved(Object source, int index0, int index1)
    {
      Object[] listeners = listenerList.getListenerList();
      ListDataEvent e = null;

      for (int i = listeners.length - 2; i >= 0; i -= 2) {
        if (listeners[i] == ListDataListener.class) {
          if (e == null) {
            e = new ListDataEvent(source, ListDataEvent.INTERVAL_REMOVED, index0, index1);
          }
          ((ListDataListener)listeners[i+1]).intervalRemoved(e);
        }          
      }
    }
  }
  @Override
  public void contentsChanged(ListDataEvent e) {
    System.out.println("contentsChanged");
    //this.doLayout();
    //this.repaint();
    this.combo.repaint();
  }

  @Override
  public void intervalAdded(ListDataEvent e) {
    System.out.println("intervalAdded");
    this.combo.repaint();
  }

  @Override
  public void intervalRemoved(ListDataEvent e) {
    System.out.println("intervalRemoved");
    this.combo.repaint();
  }
}

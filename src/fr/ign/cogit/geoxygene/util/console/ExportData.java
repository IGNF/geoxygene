/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.util.console;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import org.apache.ojb.broker.PersistenceBroker;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.apache.ojb.broker.metadata.DescriptorRepository;
import org.apache.ojb.broker.metadata.FieldDescriptor;
import org.apache.ojb.broker.util.configuration.impl.OjbConfiguration;
import org.apache.ojb.odmg.HasBroker;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.datatools.Geodatabase;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjb;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileWriter;
import fr.ign.cogit.geoxygene.util.loader.gui.GUIExportData;
import fr.ign.cogit.geoxygene.util.viewer.ObjectViewer;

/**
 * @author Julien Perret - IGN / Laboratoire COGIT
 * @version 1.0
 */
public class ExportData extends JPanel {

  /**
     * 
     */
  private static final long serialVersionUID = 2282858491554958977L;

  protected EventListenerList listenerList = new EventListenerList();

  /**
   * Adds an <code>ActionListener</code> to the button.
   * @param l the <code>ActionListener</code> to be added
   */
  public void addActionListener(ActionListener l) {
    this.listenerList.add(ActionListener.class, l);
  }

  /**
   * Notifies all listeners that have registered interest for notification on
   * this event type. The event instance is lazily created.
   * @see EventListenerList
   */
  protected void fireActionPerformed(ActionEvent event) {
    // Guaranteed to return a non-null array
    Object[] listeners = this.listenerList.getListenerList();
    // Process the listeners last to first, notifying
    // those that are interested in this event
    for (int i = listeners.length - 2; i >= 0; i -= 2) {
      if (listeners[i] == ActionListener.class) {
        // Lazily create the event:
        ((ActionListener) listeners[i + 1]).actionPerformed(event);
      }
    }
  }

  /**
     * 
     */
  protected void action() {

    System.out.println(I18N.getString("ExportData.ESRIShapefileExport")); //$NON-NLS-1$

    try {

      // initialisation
      Geodatabase data = GeodatabaseOjbFactory.newInstance();

      // determine valeur par defaut de la racine du projet geoxygene
      OjbConfiguration config = new OjbConfiguration();
      File fileMapping = new File(config.getRepositoryFilename());
      /*
       * try { File tryFileData = new File(fileMapping.getParentFile()
       * .getParentFile().getParentFile(), "data"); if (tryFileData.exists()) {
       * geoxygeneDirectory = tryFileData.getParentFile().getPath(); } else {
       * tryFileData = new File(fileMapping.getParentFile()
       * .getParentFile().getParentFile().getParentFile(), "data"); if
       * (tryFileData.exists()) { geoxygeneDirectory =
       * tryFileData.getParentFile() .getPath(); } } if (!tryFileData.exists())
       * { tryFileData = new File(fileMapping.getParentFile()
       * .getParentFile().getParentFile().getParentFile() .getParentFile(),
       * "data"); if (tryFileData.exists()) { geoxygeneDirectory =
       * tryFileData.getParentFile() .getPath(); } } } catch (Exception e) { }
       */

      // determine valeur par defaut de geoxygeneMapping
      // geOxygeneMapping = fileMapping.getParentFile().getPath();

      GUIExportData configuration = new GUIExportData();
      if (!configuration.showDialog()) {
        return;
      }

      String tableName = configuration.getTableName();
      String shapefileName = configuration.getShapefilePath();

      if (tableName == null || shapefileName == null) {
        return;
      }

      // rafraichissement du repository d'OJB
      data.refreshRepository(fileMapping);
      // Metadata metadonnees = data.getMetadata(tableName);
      ResultSet rs = data.getConnection().getMetaData().getColumns(null, null,
          tableName, "*"); //$NON-NLS-1$

      int columnNameIndex = -1;
      int typeNameIndex = -1;
      int nbCol = rs.getMetaData().getColumnCount();
      System.out.println(nbCol + " " + I18N.getString("ExportData.Columns")); //$NON-NLS-1$ //$NON-NLS-2$
      for (int i = 1; i < nbCol + 1; i++) {
        System.out.println(i + " " //$NON-NLS-1$
            + "columnName = " + rs.getMetaData().getColumnName(i)); //$NON-NLS-1$
        System.out.println("   type = " + rs.getMetaData().getColumnType(i)); //$NON-NLS-1$
        if (rs.getMetaData().getColumnName(i).equalsIgnoreCase("COLUMN_NAME")) {
          columnNameIndex = i;
        }
        if (rs.getMetaData().getColumnTypeName(i).equalsIgnoreCase("TYPE_NAME")) {
          typeNameIndex = i;
        }
      }
      rs.absolute(1);
      while (!rs.isAfterLast()) {
        rs.next();
        System.out.println("column name = " + rs.getString(columnNameIndex)); //$NON-NLS-1$
        System.out.println(" ----- type = " + rs.getString(typeNameIndex)); //$NON-NLS-1$
      }

      System.exit(0);

      Class<?> javaClass = data.getMetadata(tableName).getJavaClass();
      String javaClassName = javaClass.getName();

      System.out.println("table " + tableName //$NON-NLS-1$
          + " - " + I18N.getString("ExportData.Class") + " " + javaClassName); //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
      // lecture du fichier choisi

      System.out
          .println(I18N.getString("ExportData.ExportInFile") + shapefileName); //$NON-NLS-1$

      System.out.println(I18N.getString("ExportData.ReadingMetaData")); //$NON-NLS-1$
      data.begin();
      PersistenceBroker broker = ((HasBroker) ((GeodatabaseOjb) data)
          .getODMGTransaction()).getBroker();
      DescriptorRepository desc = broker.getDescriptorRepository();
      ClassDescriptor cd = desc.getDescriptorFor(javaClassName);
      System.out.println(javaClassName);
      System.out.println("\t table " + cd.getFullTableName()); //$NON-NLS-1$
      FieldDescriptor[] fields = cd.getFieldDescriptions();
      Map<String, String> columnNames = new HashMap<String, String>();
      for (FieldDescriptor field : fields) {
        if (field != null) {
          System.out
              .println("\t\t " + field.getAttributeName() + " -- " + field.getColumnName()); //$NON-NLS-1$//$NON-NLS-2$
          columnNames.put(field.getAttributeName(), field.getColumnName());
        }
      }

      FT_FeatureCollection<?> ftfc = data.loadAllFeatures(javaClass);

      ShapefileWriter.write(ftfc, shapefileName,null);

      ObjectViewer.flagWindowClosing = false;
      ObjectViewer viewer = new ObjectViewer(data);
      viewer.addFeatureCollection(ftfc, I18N
          .getString("ExportData.ExportedData")); //$NON-NLS-1$

      System.out.println(I18N.getString("ExportData.Finished")); //$NON-NLS-1$

    } catch (Exception e) {
      System.out.println(I18N.getString("ExportData.ExportDataFailed")); //$NON-NLS-1$
      e.printStackTrace();
    }
  }
}

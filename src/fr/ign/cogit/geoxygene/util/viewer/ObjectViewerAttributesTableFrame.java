/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework compliant with OGC/ISO specifications for 
 * the development and deployment of geographic (GIS) applications. It is a open source 
 * contribution of the COGIT laboratory at the Institut Géographique National (the French 
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net 
 *  
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation; 
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with 
 * this library (see file LICENSE if present); if not, write to the Free Software 
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *  
 */

package fr.ign.cogit.geoxygene.util.viewer;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import uk.ac.leeds.ccg.dbffile.Dbf;
import uk.ac.leeds.ccg.geotools.DataSource;
import uk.ac.leeds.ccg.geotools.ShapefileReader;
import uk.ac.leeds.ccg.geotools.Theme;

/**
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class ObjectViewerAttributesTableFrame extends JFrame {
	
	private static final String FRAME_TITLE = "GeOxygene Theme Values - ";
	
	private Theme theme;
	private int nbFields ;
	private int nbRecords ;
	private Vector columnNames ;
	private Vector rowData ;
	private String title;	
	private DataSource dataSource;
		

	public ObjectViewerAttributesTableFrame(Theme t, String dataSourceType, DataSource source) {
		super();
		
		theme = t;
		dataSource = source;
				
		if (dataSourceType.equals(Utils.SHAPEFILE))  {
			shapefile();
		}	
				
		else if (dataSourceType.equals(Utils.GEOXYGENE)) {
			geoxygene();
		}
		
		else {
			System.out.println(" ## SHOW ATTRIBUTES : NOT DEFINED FOR THIS KIND OF DATASOURCE ");
		}
		
		// Title
		this.setTitle(FRAME_TITLE + title);
		
		// Layout
		this.getContentPane().setLayout(new BorderLayout());
		
		
		// Create/setup table
		JTable table = new JTable(rowData, columnNames);

		// Place table in JScrollPane
		JScrollPane scrollPane = new JScrollPane(table);

		// Add to Screen
		this.getContentPane().add(scrollPane, BorderLayout.CENTER);

	}
	

	/** Fill vectors of attributes for a shapefile by reading directly dbf file (we do not load it into memory) */
	public void shapefile() {

		String themeName = theme.getName();
		title = themeName.substring(themeName.lastIndexOf("/") + 1);

		try {
			// Transformer URL en File pour eviter le chargement en memoire du fichier Dbf
			final Dbf themeDbf = ((ShapefileReader)dataSource).dbf;
			
			nbFields = themeDbf.getNumFields();
			nbRecords = themeDbf.getLastRec();
			StringBuffer fieldName;
			Vector row;
			columnNames = new Vector();
			rowData = new Vector();
			
			for (int i = 0; i < nbFields; i++) {
				fieldName = themeDbf.getFieldName(i);
				columnNames.add(fieldName);
			}

			for (int j = 0; j < nbRecords; j++) {
				row = new Vector();
				for (int i = 0; i < nbFields; i++) {
					if (themeDbf.getFieldType(i) == 'N') {
						row.add(
							(themeDbf.getFloatCol(i, j, j + 1))[0].toString());
					} else {
						row.add(
							(themeDbf.getStringCol(i, j, j + 1))[0].toString());
					}
				}
				rowData.add(row);
			}

		} catch (Exception e) {
			System.err.println("Can not open the file: " + e);
		}

	}


	/** Fill vectors of attributes for GeOxygene data */
	public void geoxygene() {
		title = theme.getName();
		GeOxygeneReader geOxyRd = (GeOxygeneReader) dataSource;
		Object[] result = geOxyRd.readData();
		columnNames = (Vector) result[0];
		rowData = (Vector) result[1];
		
	}
		


}

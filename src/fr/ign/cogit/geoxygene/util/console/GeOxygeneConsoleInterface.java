/*
 * This file is part of the GeOxygene project source files. 
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for 
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

package fr.ign.cogit.geoxygene.util.console;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
  *
  * @author Thierry Badard & Arnaud Braun
  * @version 1.0
  * 
  */

class GeOxygeneConsoleInterface extends JFrame {
	

	private static String ojb = "OJB";
	private static String castor= "Castor";
	
	private static String sqlToJavaText = "SQL to Java";
	private static String javaToSqlText = "Java to SQL";
	private static String manageDataText = "Manage Data";
	private static String datasetText = "Create DataSet";
	private static String viewDataText = "View data";
	private static String importDatatext = "Import data";
	private static String quitText = "QUIT";

	protected GeOxygeneConsoleInterface(String titre) {
		super(titre);
		InterfaceInit();
	}


	private void InterfaceInit() {
		
		System.out.println("Bonjour");
		
		// Init GUI
		this.getContentPane().setLayout( new GridLayout(8,1) );
		
		//A COMMENTER
		final JPanel mappingPanel =	new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		final JComboBox mappingComboBox = new JComboBox( new String[] {ojb,castor} );
		mappingPanel.add(mappingComboBox);
		
		this.getContentPane().add(mappingPanel);	

		
		JButton sqlToJavaButton = new JButton (sqlToJavaText);
		this.getContentPane().add(sqlToJavaButton);
		sqlToJavaButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String mappingTool = (String) mappingComboBox.getSelectedItem();
				SqlToJava.action(getMappingTool(mappingTool));
			}
		});
		
		JButton javaToSqlButton = new JButton (javaToSqlText);
		this.getContentPane().add(javaToSqlButton);
		javaToSqlButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("marche pas !!");
			}
		});			
		
		JButton manageDataButton = new JButton (manageDataText);
		this.getContentPane().add(manageDataButton);
		manageDataButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ManageData.action();
			}
		});			
		
		JButton datasetButton = new JButton (datasetText);
		this.getContentPane().add(datasetButton);
		datasetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("marche pas !!");
			}
		});			
				
		JButton importDataButton = new JButton (importDatatext);
		this.getContentPane().add(importDataButton);
		importDataButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("marche pas !!");
			}
		});			
		
		JButton viewDataButton = new JButton (viewDataText);
		this.getContentPane().add(viewDataButton);	
		viewDataButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ViewData.action();
			}
		});			
				
		JButton quitButton = new JButton (quitText);
		this.getContentPane().add(quitButton);
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});
		
	}
	
	
	private int getMappingTool (String string) {
		if (string.equals(ojb)) return GeOxygeneConsole.OJB;
		else if (string.equals(castor)) return GeOxygeneConsole.CASTOR;
		else return 0;
	}
	
}

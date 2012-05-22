/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.appli.rules;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.apache.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import fr.ign.cogit.appli.geopensim.agent.AgentGeographiqueCollection;
import fr.ign.cogit.appli.geopensim.feature.meso.UniteUrbaine;
import fr.ign.cogit.appli.geopensim.feature.meso.ZoneElementaireUrbaine;
import fr.ign.cogit.geoxygene.appli.MainFrame;

/**
 * @author Julien Perret
 *
 */
public class EditRulesFrame extends JFrame {
	private static final long serialVersionUID = 8404418768574265805L;
	static final Logger logger=Logger.getLogger(EditRulesFrame.class.getName());
	private MainFrame mainFrame = null;
	private AgentGeographiqueCollection collection = null;
	private JLabel label = new JLabel("f(t,d,v) avec t le temps, d la Durée et v la valeur de la mesure");

	public EditRulesFrame(MainFrame mainFrame, AgentGeographiqueCollection collection){
		this.mainFrame = mainFrame;
		this.collection = collection;
		
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		setResizable(false);

		setSize(new Dimension(600,300));

		setLocation(100, 100);
		setTitle("Edit Evolution Rules");
		setIconImage(this.mainFrame.getIconImage());

		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
				dispose();
			}
			@Override
			public void windowActivated(WindowEvent e) {}
		});

		setLayout(new GridBagLayout());

		JTabbedPane tabbedPane = new JTabbedPane();
		JComponent unitePanel = makeAgentTypePanel(collection,UniteUrbaine.class);
		tabbedPane.addTab("Unite Urbaine", unitePanel);
		JComponent zonePanel = makeAgentTypePanel(collection,ZoneElementaireUrbaine.class);
		tabbedPane.addTab("Zone Elementaire Urbaine", zonePanel);
		JComponent globalPanel = makeGlobalPanel(collection);
		tabbedPane.addTab("Connaissances globales", globalPanel);

		GridBagConstraints c;
		c=new GridBagConstraints();
		c.gridx=0; c.gridy=GridBagConstraints.RELATIVE; c.insets=new Insets(5,5,5,5);

		add(tabbedPane, c);
		add(new JButton("Appliquer"), c);
		this.repaint();
		pack();
	}

	/**
	 * @param agentCollection
	 * @return
	 */
	private JComponent makeGlobalPanel(AgentGeographiqueCollection agentCollection) {
        JPanel panel = new JPanel(false);
        DefaultCategoryDataset categoryDataset = new DefaultCategoryDataset();
        String serie = "";
        categoryDataset.addValue(10, serie, "Industriel/Commercial");
        categoryDataset.addValue(20, serie, "Public");
        categoryDataset.addValue(30, serie, "Habitat Collectif");
        categoryDataset.addValue(40, serie, "Habitat Individuel");
        JFreeChart chart = ChartFactory.createStackedBarChart(
        		"Distribution des types de bâtiment", 
        		"Type", 
        		"Distribution", 
        		categoryDataset, 
        		PlotOrientation.HORIZONTAL,
        		true,
        		true,
        		false);
        BufferedImage image = chart.createBufferedImage(400,300);
        JLabel lblChart = new JLabel();
        lblChart.setIcon(new ImageIcon(image));
        panel.add(lblChart);
		return panel;
	}

	/**
	 * @param agentCollection
	 * @param classe
	 * @return
	 */
	private JComponent makeAgentTypePanel(AgentGeographiqueCollection agentCollection, Class<?> classe) {
        JPanel panel = new JPanel(false);
		JTabbedPane tabbedPane = new JTabbedPane();
        for(int dateIndex = 0 ; dateIndex < agentCollection.getDates().size()-1 ; dateIndex++) {
        	int startDate = agentCollection.getDates().get(dateIndex);
        	int endDate = agentCollection.getDates().get(dateIndex+1);
        	String name = startDate+"-"+endDate;
    		JComponent periodPanel = makeTimePeriodPanel(collection,startDate,endDate);
    		tabbedPane.addTab(name, periodPanel);
        }
        panel.add(tabbedPane);
		return panel;
	}

	/**
	 * @param agentCollection
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	private JComponent makeTimePeriodPanel(AgentGeographiqueCollection agentCollection, int startDate, int endDate) {
        JPanel panel = new JPanel(false);
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        panel.add(label);
        JButton addRuleButton = new JButton(new ImageIcon(this.getClass().getResource("/icons/16x16/plus.png")));
        addRuleButton.setToolTipText("Ajouter une règle");
        panel.add(addRuleButton);
        JPanel rulePanel = new JPanel();
        rulePanel.setLayout(new BoxLayout(rulePanel,BoxLayout.X_AXIS));
        JTextField condition = new JTextField();
        JTextField expression = new JTextField("densite = densite + d*0.01");
        JSpinner probabilite = new JSpinner(new SpinnerNumberModel(1.0,0.0,1.0,0.05));
        rulePanel.add(condition);
        rulePanel.add(probabilite);
        rulePanel.add(expression);
        panel.add(rulePanel);
		return panel;
	}
}

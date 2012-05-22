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

package fr.ign.cogit.appli.geopensim.util;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.feature.basic.BasicBatiment;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconChemin;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconCoursEau;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconRoute;
import fr.ign.cogit.appli.geopensim.feature.basic.BasicTronconVoieFerree;
import fr.ign.cogit.appli.geopensim.feature.macro.PopulationUnites;
import fr.ign.cogit.geoxygene.datatools.ojb.GeodatabaseOjbFactory;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.util.algo.JtsUtil;

/**
 * @author Julien Perret
 *
 */
public class ConstruireZonesElementaires {
	static Logger logger=Logger.getLogger(ConstruireZonesElementaires.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("Construction des villes");
		Container p = frame.getContentPane();
		p.setLayout(new BoxLayout(p,BoxLayout.Y_AXIS));

		final JProgressBar progressBar = new JProgressBar();
		final JProgressBar progressBarIlots = new JProgressBar();
		progressBar.setStringPainted(true);
		progressBarIlots.setStringPainted(true);
		progressBar.setString("...");
		progressBarIlots.setString("...");
		final JLabel labelGeneral = new JLabel("Processus général");
		final JLabel labelDetail = new JLabel("Processus détaillé");
		p.add(labelGeneral);
		p.add(progressBar);
		p.add(labelDetail);
		p.add(progressBarIlots);
		ActionListener progressBarActionListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(e.getID()) {
				case 0:
					progressBar.setMaximum(e.getModifiers());
					progressBar.setString("0 / "+e.getModifiers());
					labelGeneral.setText(e.getActionCommand());
					break;
				case 1:
					progressBar.setValue(e.getModifiers());
					progressBar.setString(e.getModifiers()+" / "+progressBar.getMaximum());
					break;
				case 2:
					progressBarIlots.setMaximum(e.getModifiers());
					progressBarIlots.setString("0 / "+e.getModifiers());
					labelDetail.setText(e.getActionCommand());
					break;
				case 3:
					progressBarIlots.setValue(e.getModifiers());
					progressBarIlots.setString(e.getModifiers()+" / "+progressBarIlots.getMaximum());
					break;
				default:
					progressBar.setMaximum(0);
					progressBar.setValue(0);
					progressBar.setString("");
					progressBarIlots.setMaximum(0);
					progressBarIlots.setValue(0);
					progressBarIlots.setString("");
					labelGeneral.setText("Processus général");
					labelDetail.setText("Processus détaillé");
				}
			}
		};
		JtsUtil.addActionListener(progressBarActionListener);
		frame.pack();
		frame.setVisible(true);
		DataSet.db = GeodatabaseOjbFactory.newInstance();
		PopulationUnites popVilles = new PopulationUnites(BasicBatiment.class,BasicTronconRoute.class, BasicTronconChemin.class, BasicTronconVoieFerree.class, BasicTronconCoursEau.class, 1989);
		popVilles.addActionListener(progressBarActionListener);
		if (logger.isDebugEnabled()) logger.debug("Chargement des villes");
		popVilles.chargerElements();
		if (popVilles.getUnitePeriUrbaine().getGeom()==null) popVilles.construireUnitePeriUrbaine();
		if (logger.isDebugEnabled()) logger.debug("ConstruireZonesElementaires");
		popVilles.construireZonesElementaires();
//		if (logger.isDebugEnabled()) logger.debug("ConstruireGroupesBatiments");
//		popVilles.construireGroupesBatiments();
		if (logger.isDebugEnabled()) logger.debug("ConstruireCarrefours");
		popVilles.construireCarrefours();
		if (logger.isDebugEnabled()) logger.debug("Qualification des villes");
		popVilles.qualifier();
		if (logger.isDebugEnabled()) logger.debug("Sauvegarde des villes");
		popVilles.sauverPopulations();
		if (logger.isDebugEnabled()) logger.debug(popVilles);
		frame.dispose();
	}

}

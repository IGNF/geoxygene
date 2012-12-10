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

package fr.ign.cogit.geoxygene.appli.plugin.semio;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyVetoException;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.cogit.geoxygene.appli.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;
import fr.ign.cogit.geoxygene.appli.plugin.semio.contrast.ContrastAnalysisToolBar;
import fr.ign.cogit.geoxygene.appli.plugin.semio.contrast.ContrastFrame;
import fr.ign.cogit.geoxygene.appli.plugin.semio.toolbar.SemioToolBar;

/**
 * @author Charlotte Hoarau
 * 
 * Plugin for semiology tools.
 */
public class SemioPlugin implements GeOxygeneApplicationPlugin, ActionListener {
	/**
     * Logger.
     */
    static Logger logger = Logger.getLogger(SemioPlugin.class.getName());

	private GeOxygeneApplication application = null;
	private JCheckBoxMenuItem semioMenuItem;
	private JCheckBoxMenuItem contrastMenuItem;
	
	private SemioToolBar semioToolBar;
	private ContrastAnalysisToolBar contrastAnalysisToolBar;
	
	@Override
	public void initialize(final GeOxygeneApplication application) {
		this.application = application;
        JMenu menuSemio = new JMenu(I18N.getString("SemioPlugin.Semiology")); //$NON-NLS-1$
        
        this.semioMenuItem = new JCheckBoxMenuItem(
        		I18N.getString("SemioPlugin.SemiologyToolbar") //$NON-NLS-1$
        );
        this.semioMenuItem.addActionListener(this);

        this.contrastMenuItem = new JCheckBoxMenuItem(
                I18N.getString("SemioPlugin.ContrastImprovement") //$NON-NLS-1$
        );
        this.contrastMenuItem.addActionListener(this);
        
//      JMenuItem menuColleg = new JMenuItem("COLLEG");
//		JMenuItem menuConception = new JMenuItem("Web Services"); //$NON-NLS-1$
		
		JMenuItem menuCercleChrom = new JMenuItem(I18N.getString("SemioPlugin.VisuWheels")); //$NON-NLS-1$
		menuCercleChrom.addActionListener(new java.awt.event.ActionListener(){
			@Override
      public void actionPerformed(ActionEvent e){
				afficherCercles();
			}
		});
		
		JMenuItem menuContrastOnChromWheels = new JMenuItem(I18N.getString("SemioPlugin.VisuContrast")); //$NON-NLS-1$
		menuContrastOnChromWheels.addActionListener(new java.awt.event.ActionListener(){
			@Override
      public void actionPerformed(ActionEvent e){
				ContrastFrame frame = new ContrastFrame();
				frame.setVisible(true);
			}
		});
		
        menuSemio.add(semioMenuItem);
        menuSemio.add(menuCercleChrom);
        menuSemio.add(menuContrastOnChromWheels);
        menuSemio.addSeparator();
        menuSemio.add(contrastMenuItem);
        
        application.getFrame().getJMenuBar().add(
        		menuSemio, application.getFrame().getJMenuBar().getComponentCount() - 1);
	}

	public void afficherCercles(){
		JColorChooser colorChooser = new JColorChooser();
		
		colorChooser.addChooserPanel(new COGITColorChooserPanel());
		for (int i = 0; i < colorChooser.getChooserPanels().length; i++) {
			colorChooser.removeChooserPanel(colorChooser.getChooserPanels()[i]);
		}
		colorChooser.removeChooserPanel(colorChooser.getChooserPanels()[0]);
		
		JDialog dialog = JColorChooser.createDialog(
				SemioPlugin.this.application.getFrame(),
				I18N.getString("SemioPlugin.ReferenceColors"), //$NON-NLS-1$
				true, colorChooser,
				null,
				null);
		dialog.setVisible(true);
		dialog.remove(0);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		ProjectFrame projectFrame = 
				SemioPlugin.this.application.getFrame().getSelectedProjectFrame();
		
		if (e.getSource() == SemioPlugin.this.contrastMenuItem) {
			if (contrastMenuItem.isSelected()) {
				contrastAnalysisToolBar = new ContrastAnalysisToolBar(projectFrame);
				if (projectFrame.getJMenuBar() == null) {
				  JMenuBar menuBar = new JMenuBar();
                  projectFrame.setJMenuBar(menuBar);
                }
                projectFrame.getJMenuBar().add(contrastAnalysisToolBar);
			} else {
				projectFrame.getJMenuBar().remove(contrastAnalysisToolBar);
			}
		}
		
		if (e.getSource() == semioMenuItem) {
			if (semioMenuItem.isSelected()) {
			  semioToolBar = new SemioToolBar(projectFrame);
			  if (projectFrame.getJMenuBar() == null) {
                JMenuBar menuBar = new JMenuBar();
                projectFrame.setJMenuBar(menuBar);
              }
                projectFrame.getJMenuBar().add(semioToolBar, 0);		
			} else {
			  projectFrame.getJMenuBar().remove(semioToolBar);
			}
		}
		
		projectFrame.pack();
		try {
			projectFrame.setMaximum(true);
		} catch (PropertyVetoException e1) {
			e1.printStackTrace();
		}
		projectFrame.repaint();
	}
}

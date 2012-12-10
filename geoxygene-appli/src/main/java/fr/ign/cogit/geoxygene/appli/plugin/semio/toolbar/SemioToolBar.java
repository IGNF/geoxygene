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

package fr.ign.cogit.geoxygene.appli.plugin.semio.toolbar;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.COGITColorChooserPanel;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * @author Charlotte Hoarau
 * 
 * Menu Bar dedicated to semiotics tools.
 * It is available by an item menu of the Menu of the Semiotics Plugin.
 *
 */
public class SemioToolBar extends JToolBar implements ActionListener {

	private static final long serialVersionUID = 4791806011051504347L;
	
	/**
     * Logger.
     */
    static Logger logger = Logger.getLogger(SemioToolBar.class.getName());
    
	private ProjectFrame projectFrame;
	private JToggleButton btnSpecifications;
	private JToggleButton btnPalettes;
	public JButton btnCercle;
	
	private SpecificationToolBar specificationToolBar = null;
	private PaletteToolBar paletteToolBar = null;
	
	/**
	 * Constructor.
	 * 
	 * @param projectFrame The ProjectFrame object which contains the Menu Bar.
	 */
	public SemioToolBar(ProjectFrame projectFrame){
		this.projectFrame = projectFrame;
		
		btnSpecifications = new JToggleButton(new ImageIcon(
		        GeOxygeneApplication.class.getResource(
                "/images/icons/16x16/tree.png")));
		btnSpecifications.addActionListener(this);
		btnSpecifications.setToolTipText("Open the Specifications Toolbar");
		
		btnPalettes = new JToggleButton(new ImageIcon(
		        GeOxygeneApplication.class.getResource(
                "/images/icons/palette.png")));
		btnPalettes.addActionListener(this);
		btnPalettes.setToolTipText("Open the Palette Toolbar");
		
		btnCercle = new JButton(new ImageIcon(
				GeOxygeneApplication.class.getResource(
				"/images/icons/cercle.png")));
		btnCercle.addActionListener(this);
		btnCercle.setToolTipText("See the legend on the color wheels");
		
		add(btnSpecifications);
		add(btnPalettes);
		add(btnCercle);
	}
	
	public SpecificationToolBar getSpecificationToolBar() {
		return this.specificationToolBar;
	}

	/**
	 * Hide or Display the Specification Tool Bar.
	 * @param b <code>true</code> to display the tool bar; <code>false</code> otherwise.
	 */
	public void setSpecificationToolsVisible(boolean b) {
        if (b) {
            if (this.specificationToolBar == null) {
                this.specificationToolBar =
                	new SpecificationToolBar(this.projectFrame);
                this.projectFrame.getContentPane().add(
                		this.specificationToolBar,
                        BorderLayout.EAST);
            }
            this.specificationToolBar.setVisible(true);
            this.projectFrame.validate();
        } else {
            this.specificationToolBar.setVisible(false);
            this.projectFrame.validate();
        }
	}
	
	public void setPaletteToolBar(PaletteToolBar paletteToolBar) {
		this.paletteToolBar = paletteToolBar;
	}
	
	public PaletteToolBar getPaletteToolBar() {
		return this.paletteToolBar;
	}

	/**
	 * Hide or Display the {@link}PaletteToolBar.
	 * @param b <code>true</code> to display the tool bar; <code>false</code> otherwise.
	 */
	public void setPaletteToolsVisible(boolean b) {
	        if (b) {
	            if (this.paletteToolBar == null) {
	                this.paletteToolBar =
	                	new PaletteToolBar(this.projectFrame);
	                this.projectFrame.getContentPane().add(
	                		this.paletteToolBar,
	                        BorderLayout.SOUTH);
	            }
	            this.paletteToolBar.setVisible(true);
	            this.projectFrame.validate();
	        } else {
	            this.paletteToolBar.setVisible(false);
	            this.projectFrame.validate();
	        }
	    }

	@Override
	public void actionPerformed(ActionEvent e) {
		//TODO A améliorer : Ne pas rendre l'analyse des contrastes possible tant que les spécifications ne sont pas remplies
		if (e.getSource() == this.btnSpecifications) {
            if (!btnSpecifications.isSelected()) {
            	//Hide the specificationToolBar
                SemioToolBar.this.setSpecificationToolsVisible(false);
            } else {
            	//Display the specificationToolBar
            	SemioToolBar.this.setSpecificationToolsVisible(true);
            }
    	} else if (e.getSource() == btnPalettes) {
    		if (!btnPalettes.isSelected()) {
            	//Hide the paletteToolBar
    			SemioToolBar.this.setPaletteToolsVisible(false);
            } else {
            	//Display the paletteToolBar
            	SemioToolBar.this.setPaletteToolsVisible(true);
            }
    	} else if (e.getSource() == btnCercle) {
    		List<ColorimetricColor> colors = SemioToolBar.this.projectFrame.getSld().getColors();
    		
    		//Conversion to chromatic wheels colors
    		List<ColorimetricColor> colorimetricColors = new ArrayList<ColorimetricColor>();
    		for (ColorimetricColor c : colors) {
				ColorimetricColor cCOGIT = new ColorimetricColor(c.toColor(),true);
				colorimetricColors.add(cCOGIT);
			}
    		
    		COGITColorChooserPanel.show(SemioToolBar.this.projectFrame, "Colors of the Current Legend", colorimetricColors);
    	}
	}
	
}

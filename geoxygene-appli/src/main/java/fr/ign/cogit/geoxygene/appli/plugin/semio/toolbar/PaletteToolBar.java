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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JToolBar;

import fr.ign.cogit.geoxygene.semio.legend.legendContent.Legend;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.Map;

import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * @author Charlotte Hoarau
 * 
 * Palette ToolBar.
 * Tool bar for the management of color palettes extracted from the legend.
 *
 */
public class PaletteToolBar extends JToolBar implements ActionListener{

	private static final long serialVersionUID = 1L;
	
	List<Palette> palettes = new ArrayList<Palette>();
	private ProjectFrame projectFrame;
	private Map currentMap;
	
	public PaletteToolBar(ProjectFrame frame){
		this.projectFrame = frame;
		
		setOrientation(1);
		setPreferredSize(new Dimension(1800, 300));
		setMaximumSize(getPreferredSize());
		setLayout(new FlowLayout());
		
		Palette p = new Palette();
		p.setCouleursPalette(frame.getSld().getColors());
		this.addImagePalette(p);
	}
	
	public void addImagePalette(Palette p){
		
		this.palettes.add(p);
		
		BufferedImage image = p.buildPaletteImage(30);
		JButton imageButton = new JButton(new ImageIcon(image));
		imageButton.addActionListener(this);
		this.add(imageButton);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		for (int i = 0; i < PaletteToolBar.this.getComponents().length; i++) {
			if (PaletteToolBar.this.getComponents()[i] == arg0.getSource()) {
			  Palette pClick = PaletteToolBar.this.palettes.get(i);
				JOptionPane.showMessageDialog(
					((Component)arg0.getSource()).getParent().getParent(),
					pClick.getNbCouleurs() + " couleurs dans la palette\n\n"
					+ "Energie d'affichage : " + pClick.energyDisplay(false));
				for (ColorimetricColor color : pClick.getCouleursPalette()) {
					System.out.println(color.toString() + "   -   " + color.maxRGB());
				}
			}
		}
	}
	
	public Palette makePalette(){
		
		Legend currentLegend = null;
		for (Component component : PaletteToolBar.this.projectFrame
				.getContentPane().getComponents()) {
			if (component.getClass().isAssignableFrom(SemioToolBar.class)) {
				currentLegend = ((SemioToolBar)component).getSpecificationToolBar().getCurrentLegend();
			}
		}
		
		List<Layer> layers = PaletteToolBar.this.projectFrame.getLayers();
		currentMap = new Map(layers, currentLegend, Viewport.getMETERS_PER_PIXEL());
		currentMap.setName(PaletteToolBar.this.projectFrame.getName());
		
		List<ColorimetricColor> colors = PaletteToolBar.this.projectFrame.getSld().getColors();
		List<Float> areas = currentMap.getAreas();
		System.out.println("Aires  " + areas);
		float sumThemes = 0;
		for (int i=1; i<areas.size(); i++) {
			sumThemes += areas.get(i);
		}
		System.out.println("Total : " + sumThemes);
		float areaBackground = areas.get(0) - areas.get(1);
		float totalArea = areaBackground + sumThemes;
		
		List<Float> proportions = new ArrayList<Float>();
		proportions.add(areaBackground / totalArea);
		for (int i=1; i<areas.size(); i++) {
			proportions.add( areas.get(i) / totalArea );
		}
		System.out.println("Proportions " + proportions);
		return new Palette(colors, proportions);
	}
}

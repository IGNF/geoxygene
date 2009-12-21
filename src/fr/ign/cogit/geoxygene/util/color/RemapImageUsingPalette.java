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

package fr.ign.cogit.geoxygene.util.color;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.log4j.Logger;

/**
 * @author Julien Perret
 *
 */
public class RemapImageUsingPalette {
    static Logger logger=Logger.getLogger(RemapImageUsingPalette.class.getName());
    
    /**
     * @param args
     */
    public static void main(String[] args) {
	JFrame frame = new JFrame("Remapping d'une image à partir d'une palette");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	frame.setLayout(new FlowLayout());

	JFileChooser chooser = new JFileChooser();
	FileNameExtensionFilter filter = new FileNameExtensionFilter("JPG & GIF & PNG & BMP Images", "jpg", "gif","png", "bmp");
	chooser.setFileFilter(filter);
	int returnVal = chooser.showOpenDialog(frame);
	if(returnVal != JFileChooser.APPROVE_OPTION) return;
	File imageFile = chooser.getSelectedFile();
	returnVal = chooser.showOpenDialog(frame);
	if(returnVal != JFileChooser.APPROVE_OPTION) return;
	File paletteFile = chooser.getSelectedFile();
	frame.dispose();
	// Get RGB.
	try {
	    	BufferedImage paletteImage = ImageIO.read(paletteFile);
	    	Set<Color> paletteColors = new HashSet<Color>();
		for(int y = 0 ; y < paletteImage.getHeight() ; y++) {
			for(int x = 0 ; x < paletteImage.getWidth() ; x++) {
				Color color = new Color(paletteImage.getRGB(x, y));
				int alpha =  paletteImage.getColorModel().getAlpha(paletteImage.getRaster().getDataElements(x, y, null));
				if (alpha==255) paletteColors.add(color);
			}
		}
		int numberOfColors = paletteColors.size();

		BufferedImage image = ImageIO.read(imageFile);
		OctreeQuantizer quantization = new OctreeQuantizer(image);
		quantization.setColors(paletteColors.toArray(new Color[0]));
		quantization.reMap(image);

		Map<Color, Integer> occurrenceMap = ColorUtil.occurrenceMap(image);

		// check if there is no useless color
		List<Color> colorsToRemove = new ArrayList<Color>();
		for (int i = 0 ; i < quantization.getColorLookUpTable().length ; i++) {
		    if(occurrenceMap.get(quantization.getColorLookUpTable()[i])==0) colorsToRemove.add(quantization.getColorLookUpTable()[i]);
		}
		if (!colorsToRemove.isEmpty()) {
		    paletteColors.removeAll(colorsToRemove);
		    numberOfColors = paletteColors.size();
		    quantization.setColors(paletteColors.toArray(new Color[0]));
		    quantization.reMap(image);
		}
		logger.info(numberOfColors+" colors found in the palette image");	    	

		int nbPixels = image.getWidth()*image.getHeight();
		List<Integer> colors = new ArrayList<Integer>();
		for (int i = 0 ; i < quantization.getColorLookUpTable().length ; i++) {
			Color rgb = quantization.getColorLookUpTable()[i];
			if (rgb==null) continue;
			Color color = new Color(rgb.getRGB());
			colors.add(color.getRGB());
			if (logger.isDebugEnabled()) logger.debug(Integer.toHexString(color.getRGB())+"    ---   "+occurrenceMap.get(quantization.getColorLookUpTable()[i])+" pixels   --- "+100*occurrenceMap.get(quantization.getColorLookUpTable()[i])/(double)nbPixels+" %");
		}
		File directory = new File(imageFile.getAbsolutePath()+"_"+numberOfColors);
		directory.mkdir();
		logger.info("Writing palette image "+directory.getAbsolutePath()+"/palette_"+numberOfColors+".png");
		BufferedImage image2 = ColorUtil.buildPaletteImage(quantization.getColorLookUpTable(),50);
		ImageIO.write(image2, "PNG", new File(directory.getAbsolutePath()+"/palette_"+numberOfColors+".png"));
		logger.info("Writing proportional palette image "+directory.getAbsolutePath()+"/prop_palette_"+numberOfColors+".png");
		BufferedImage propPaletteImage = ColorUtil.buildProportionalPaletteImage(quantization.getRemappedImage());
		ImageIO.write(propPaletteImage, "PNG", new File(directory.getAbsolutePath()+"/prop_palette_"+numberOfColors+".png"));
		logger.info("Writing remapped image "+directory.getAbsolutePath()+"/remapped_"+numberOfColors+".png");
		ImageIO.write(image, "PNG", new File(directory.getAbsolutePath()+"/remapped_"+numberOfColors+".png"));
		BufferedImage image3 = quantization.buildOctreeImage(100);
		logger.info("Writing octree image "+directory.getAbsolutePath()+"/octree_"+numberOfColors+".png");
		ImageIO.write(image3, "PNG", new File(directory.getAbsolutePath()+"/octree_"+numberOfColors+".png"));
		logger.info("Building graph");
		ImageColorClusterGraph graph = new ImageColorClusterGraph(image);
		logger.info("Writing graph cluster image"+directory.getAbsolutePath()+"/graph_cluster_"+numberOfColors+".png");
		BufferedImage graphClusterImage = graph.buildGraphClusterImage();
		ImageIO.write(graphClusterImage, "PNG", new File(directory.getAbsolutePath()+"/graph_cluster"+numberOfColors+".png"));
		logger.info("Writing graph image"+directory.getAbsolutePath()+"/graph_"+numberOfColors+".png");
		BufferedImage graphImage = graph.buildGraphImage();
		ImageIO.write(graphImage, "PNG", new File(directory.getAbsolutePath()+"/graph_"+numberOfColors+".png"));

		/*
		Graph graph = quantization.buildGraph(image);
		logger.info("Writing graph cluster image"+directory.getAbsolutePath()+"/graph_cluster_"+numberOfColors+".png");
		BufferedImage graphClusterImage = quantization.buildGraphClusterImage(graph);
		ImageIO.write(graphClusterImage, "PNG", new File(directory.getAbsolutePath()+"/graph_cluster"+numberOfColors+".png"));
		logger.info("Writing graph image"+directory.getAbsolutePath()+"/graph_"+numberOfColors+".png");
		BufferedImage graphImage = quantization.buildGraphImage(graph);
		ImageIO.write(graphImage, "PNG", new File(directory.getAbsolutePath()+"/graph_"+numberOfColors+".png"));
		*/
		logger.info("Finished");
	} catch (IOException e) {
	}
    }

}

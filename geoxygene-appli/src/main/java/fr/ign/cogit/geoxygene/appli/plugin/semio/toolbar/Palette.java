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

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import fr.ign.cogit.geoxygene.style.colorimetry.ColorimetricColor;

/**
 * @author Charlotte Hoarau
 * @author Jérémy Guilbaud
 * 
 * Color Palette
 * 
 */
public class Palette {
	/**
	 * Nombre de couleurs de la palette.
	 */
	int nbCouleurs;
	
	public int getNbCouleurs() {
		this.nbCouleurs = this.couleursPalette.size();
		return nbCouleurs;
	}

	public void setNbCouleurs(int nbCouleurs) {
		this.nbCouleurs = nbCouleurs;
	}

	/**
	 * Récupère les couleurs de la palette
	 */
	List<ColorimetricColor> couleursPalette;
	
	public List<ColorimetricColor> getCouleursPalette() {	
		return couleursPalette;
	}

	public void setCouleursPalette(List<ColorimetricColor> couleursPalette) {		
		this.couleursPalette = couleursPalette;
		this.setNbCouleurs(couleursPalette.size());
	}

	List<Float> proportions;
	
	public List<Float> getProportions() {
		return proportions;
	}

	public void setProportions(List<Float> proportions) {
		this.proportions = proportions;
	}
	
	/**
	 * Color distance matrix
	 */
	float[][] distances;
	
	public float[][] getDistances() {
		return distances;
	}

	private void setDistances(float[][] distances) {
		this.distances = distances;
	}
	/**
	 * Computes the color distance matrix of the Palette.
	 */
	public float[][] computeDistances(){
		float[][] distMatrix = new float[this.getNbCouleurs()][this.getNbCouleurs()];
		
		for (int i = 0; i < this.getNbCouleurs(); i++) {
			for (int j = 0; j < this.getNbCouleurs(); j++) {
				distMatrix[i][j] = ColorimetricColor.distanceCIElab(
												this.getCouleursPalette().get(i),
												this.getCouleursPalette().get(j));
			}
		}
		this.setDistances(distMatrix);
		return distMatrix;
	}
	/**
	 * Computes the color distance matrix of the Palette.
	 */
	public float[][] computeBinaryDistances(){
		float[][] distBinaryMatrix = new float[this.getNbCouleurs()][this.getNbCouleurs()];
		for (int i = 0; i < this.getNbCouleurs(); i++) {
			for (int j = 0; j < this.getNbCouleurs(); j++) {
				if(ColorimetricColor.distanceCIElab(
						this.getCouleursPalette().get(i),
						this.getCouleursPalette().get(j)) < 50){
					distBinaryMatrix[i][j] = 0;
				} else {
					distBinaryMatrix[i][j] = 1;
				}
			}
		}
		this.setDistances(distBinaryMatrix);
		return distBinaryMatrix;
	}

	/**
	 * Default Constructor.
	 */
	public Palette() {
		
	}
	
	public Palette(List<ColorimetricColor> colors, List<Float> proportions) {
		this.setCouleursPalette(colors);
		this.setProportions(proportions);
		this.setNbCouleurs(colors.size());
	}

	//List of the energies used to display the palette :
	//maximum of the RVG color composants
	//TODO essayer différents types d'énergie d'affichage selon les différents types d'écran
	public List<Float> getListDisplayEnergy() {
		List<Float> listDisplayEnergy = new ArrayList<Float>();
		for (int i = 0; i < this.getCouleursPalette().size(); i++){
		listDisplayEnergy.add(this.getCouleursPalette().get(i).maxRGB()/255f);
		}
		return listDisplayEnergy;
	}
	
	public float energyDisplay(boolean ponderation){
		
		List<Float> eDisplay = this.getListDisplayEnergy();
		
		if (ponderation) {
			float eDisplayPond = 0f;
			for (int i = 0; i < eDisplay.size(); i++) {
			eDisplayPond = eDisplayPond + eDisplay.get(i) * this.proportions.get(i);
			}
			return eDisplayPond;
		} else {
			float eDisplaySom = 0f;
			for (int i = 0; i < eDisplay.size(); i++) {
			eDisplaySom = eDisplaySom + eDisplay.get(i);
			}
			return eDisplaySom;
		}
	}
	
	/**
	 * Calcul de la distance entre deux palettes
	 * TODO Overwrite this method
	 * @param p1 point 1
	 * @param p2 point 2
	 * @return distance
	 */
	public static double distancePalettes(Palette p1, Palette p2){

		double dist = 0;

		for(int i = 0; i < p1.getNbCouleurs(); i++){
			dist = dist + ColorimetricColor.distanceCIElab(
					p1.getCouleursPalette().get(i),
					p2.getCouleursPalette().get(i));
		}	
		dist = Math.sqrt(dist);
		return dist;
	}
	
	/**
	 * Write an image representing the palette.
	 * @param imageName the name of the image representing the palette
	 */
	public void writePaletteImage(String imageName) {
		int sizeElement = 40;
		BufferedImage image = buildPaletteImage(sizeElement);
		try {
		  ImageIO.write(image, "PNG", new File(imageName));
	    } catch (IOException e) {
		   e.printStackTrace();
		}
    }
	
	/**
     * Build an image representing the palette.
     * @param colors the palette's list of colors
     * @param sizeElement size of the palette elements
     * @return an image representing the palette
     */
    public BufferedImage buildPaletteImage(int sizeElement){
		//Compute the columns and lines numbers
    	//to get as close to a square as possible
		int numberOfColors = this.getCouleursPalette().size();
		int numberOfColumns = (int) Math.sqrt(numberOfColors);
		int numberOfLines = numberOfColors/numberOfColumns;
		if(numberOfColors>numberOfColumns*numberOfLines){
			numberOfLines++;
		}
		//Create the image with the calculated size
		BufferedImage image = new BufferedImage(
				numberOfColumns*sizeElement,
				numberOfLines*sizeElement,
				BufferedImage.TYPE_INT_ARGB);
		//Fill the image with the interface background color
		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				image.setRGB(i,j, new Color(238,238,238).getRGB());
			}
		}
		
		//Fill the image with the colors of the palette
		for(int index = 0 ; index < this.getCouleursPalette().size() ; index++) {
		    int w = (index%numberOfColumns)*sizeElement;
		    int h = (index/numberOfColumns)*sizeElement;
		    for(int i =0 ; i < sizeElement ; i++){
		    	for(int j =0 ; j < sizeElement ; j++){
		    		if (this.getCouleursPalette().get(index)!=null){
		    			image.setRGB(w+i,h+j,this.getCouleursPalette().get(index).toColor().getRGB());
		    		}
		    	}
		    }
		}
		return image;
    }

	@Override
  public String toString(){
		return "Palette : NbCouleurs=" + this.nbCouleurs + "Couleurs" + this.getCouleursPalette();
	}
}


	
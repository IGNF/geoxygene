/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.appli.gl;

import java.util.Random;

import javax.vecmath.Point2d;

/**
 * @author JeT
 * Jittered texture is a texture that represents n time the same texture with small differences
 * each row has start and an end, the middle part (the tile) is repeated.
 * setTextureSizeInWorldCoordinates() method is very important, it fixes the perceptual size of the texture
 */
public class TiledTexture extends BasicTexture {

  private int nbRows = 0;
  private int rowHeight = 0;
  private double textureWidthInWorldCoordinates = 1.; // = how many world units are equivalent to 1 texture unit
  private int beginWidth = 0; // size of the stroke start (in pixels)
  private int endWidth = 0; // size of the stroke start (in pixels)
  private int tileWidth = 0; // size of the tile width (in pixels) = texture width - beginWidth - endWidth
  private double beginWidthInWorldCoordinates = 0; // size of the stroke start (in world coordinates)
  private double endWidthInWorldCoordinates = 0; // size of the stroke start (in world coordinates)
  private double tileWidthInWorldCoordinates = 0; // size of the tile width (in world coordinates)
  private Random rand = null; // row randomizer

  /**
   * Constructor
   */
  public TiledTexture() {
    super();
  }

  /**
   * Constructor
   * @param textureFilename texture filename
   * @param nbRows number of rows in the texture
   * @param rowHeight height of each rows in the texture (in pixels)
   * @param beginWidth size of the stroke start (in pixels)
   * @param endWidth size of the stroke end (in pixels)
   */
  public TiledTexture(final String textureFilename, final int nbRows, final int rowHeight, final int beginWidth, final int endWidth) {
    super(textureFilename);
    this.nbRows = nbRows;
    this.rowHeight = rowHeight;
    this.beginWidth = beginWidth;
    this.endWidth = endWidth;
  }

  /**
   * @return the texture size in World Coordinates
   */
  public double getTextureWidthInWorldCoordinates() {
    return this.textureWidthInWorldCoordinates;
  }

  /**
   * set the texture size in World Coordinates
   * @param textureScale the size of the texture in World Coordinates
   */
  public void setTextureSizeInWorldCoordinates(final double textureScale) {
    this.textureWidthInWorldCoordinates = textureScale;
  }

  /**
   * initialize the texture rendering
   */
  @Override
  public boolean initializeRendering() {
    this.rand = new Random();
    this.tileWidth = this.getTextureWidth() - this.beginWidth - this.endWidth;
    this.beginWidthInWorldCoordinates = (double) this.beginWidth / (double) this.getTextureWidth() * this.getTextureWidthInWorldCoordinates();
    this.endWidthInWorldCoordinates = (double) this.endWidth / (double) this.getTextureWidth() * this.getTextureWidthInWorldCoordinates();
    this.tileWidthInWorldCoordinates = (double) this.tileWidth / (double) this.getTextureWidth() * this.getTextureWidthInWorldCoordinates();
//    System.err.println((double) this.beginWidth / this.getTextureWidth() + " = " + this.beginWidthInWorldCoordinates
//        / this.textureWidthInWorldCoordinates);
//    System.err.println((double) this.endWidth / this.getTextureWidth() + " = " + this.endWidthInWorldCoordinates
//        / this.textureWidthInWorldCoordinates);
    return super.initializeRendering();
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.gl.Texture#vertexCoordinates(double, double)
   */
  @Override
  public Point2d vertexCoordinates(final double x, final double y) {
    int row = 0;
    double xTexture = 0;
    double yTexture = 0;
    if (x <= this.getMinX() + this.beginWidthInWorldCoordinates) {
      // start of the stroke
      xTexture = (x - this.getMinX()) / this.textureWidthInWorldCoordinates;
      //      System.err.println("begin x = " + x + " xTexture = " + xTexture);
    } else if (x >= this.getMaxX() - this.endWidthInWorldCoordinates) {
      // end of the stroke
      xTexture = (x - (this.getMaxX() - this.endWidthInWorldCoordinates)) / this.textureWidthInWorldCoordinates + 1 - this.endWidthInWorldCoordinates
          / this.textureWidthInWorldCoordinates;
      //      System.err.println("end x = " + x + " xTexture = " + xTexture);
    } else {
      // stroke tiling
      xTexture = (x - (this.getMinX() + this.beginWidthInWorldCoordinates)) / this.textureWidthInWorldCoordinates
          % ((double) this.tileWidth / (double) this.getTextureWidth()) + this.beginWidth / this.getTextureWidth();
      //      System.err.println("tile x = " + x + " xTexture = " + xTexture);
    }
    yTexture = (y - this.getMinY()) / (this.getMaxY() - this.getMinY()) / this.nbRows + (double) this.rowHeight / (double) this.getTextureHeight()
        * row;
    return new Point2d(xTexture, yTexture);
  }

  /* (non-Javadoc)
   * @see fr.ign.cogit.geoxygene.appli.gl.Texture#finalizeRendering()
   */
  @Override
  public void finalizeRendering() {
    super.finalizeRendering();
  }

}

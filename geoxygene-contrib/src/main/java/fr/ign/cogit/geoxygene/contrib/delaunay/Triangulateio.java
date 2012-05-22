/*
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
 */

package fr.ign.cogit.geoxygene.contrib.delaunay;

/**
 * Classe interne - NE PAS UTILISER.
 * @author Bonin
 * @version 1.0
 */

public class Triangulateio {

  // Constructeur
  protected Triangulateio() {
  }

  // /////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////Attributs //////////////////////////
  // //////////////////////////////////////////////////////////////////////////
  protected double[] pointlist = null;
  protected double[] pointattributelist = null;
  protected int[] pointmarkerlist = null;
  protected int numberofpoints = 0;
  protected int numberofpointattributes = 0;

  protected int[] trianglelist = null;
  protected double[] triangleattributelist = null;
  protected double[] trianglearealist = null;
  protected int[] neighborlist = null;
  protected int numberoftriangles = 0;
  protected int numberofcorners = 0;
  protected int numberoftriangleattributes = 0;

  protected int[] segmentlist = null;
  protected int[] segmentmarkerlist = null;
  protected int numberofsegments = 0;

  protected double[] holelist = null;
  protected int numberofholes = 0;

  protected double[] regionlist = null;
  protected int numberofregions = 0;

  protected int[] edgelist = null;
  protected int[] edgemarkerlist = null;
  protected double[] normlist = null;
  protected int numberofedges = 0;

  protected void joutInit() {
    this.pointlist = new double[2 * this.numberofpoints];
    this.edgelist = new int[2 * this.numberofedges];
    this.segmentlist = new int[2 * this.numberofsegments];
    this.trianglelist = new int[this.numberofcorners * this.numberoftriangles];
  }

  protected void jvoroutInit() {
    this.pointlist = new double[2 * this.numberofpoints];
    this.edgelist = new int[2 * this.numberofedges];
    this.normlist = new double[2 * this.numberofedges];
  }
}

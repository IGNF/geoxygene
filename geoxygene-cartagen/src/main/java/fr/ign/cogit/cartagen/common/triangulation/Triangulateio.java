/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.common.triangulation;

/**
 * Class used for Java/C comminucation
 * @author Bonin
 */

public class Triangulateio {
  /**
   * Constructor
   */
  protected Triangulateio() {
  }

  // the points
  // points number
  /**
	 */
  protected int numberofpoints = 0;
  // points coordinates list: x,y,x,y,x,y...
  /**
	 */
  protected double[] pointlist = null;
  // attributs
  /**
	 */
  protected double[] pointattributelist = null;
  // marker for the region triangulation
  /**
	 */
  protected int[] pointmarkerlist = null;
  // ?
  /**
	 */
  protected int numberofpointattributes = 0;

  // triangles
  /**
	 */
  protected int[] trianglelist = null;
  /**
	 */
  protected double[] triangleattributelist = null;
  /**
	 */
  protected double[] trianglearealist = null;
  /**
	 */
  protected int[] neighborlist = null;
  /**
	 */
  protected int numberoftriangles = 0;
  /**
	 */
  protected int numberofcorners = 0;
  /**
	 */
  protected int numberoftriangleattributes = 0;

  // segments for the constrained triangulation
  /**
	 */
  protected int[] segmentlist = null;
  /**
	 */
  protected int[] segmentmarkerlist = null;
  /**
	 */
  protected int numberofsegments = 0;

  // input holes
  /**
	 */
  protected double[] holelist = null;
  /**
	 */
  protected int numberofholes = 0;

  // regions
  /**
	 */
  protected double[] regionlist = null;
  /**
	 */
  protected int numberofregions = 0;

  // segments created by th triangulation
  /**
	 */
  protected int[] edgelist = null;
  /**
	 */
  protected int[] edgemarkerlist = null;
  /**
	 */
  protected double[] normelist = null;
  /**
	 */
  protected int numberofedges = 0;

  protected void joutInit() {
    this.pointlist = new double[2 * this.numberofpoints];
    this.edgelist = new int[2 * this.numberofedges];
    this.segmentlist = new int[2 * this.numberofsegments];
    this.trianglelist = new int[this.numberofcorners * this.numberoftriangles];
  }

}

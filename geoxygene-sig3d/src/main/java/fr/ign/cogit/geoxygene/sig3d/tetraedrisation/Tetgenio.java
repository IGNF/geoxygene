package fr.ign.cogit.geoxygene.sig3d.tetraedrisation;


/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see LICENSE.TXT
 * 
 *        see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * @author Bonin
 * @version 0.1
 * 
 * 
 * Classe interne - NE PAS UTILISER. Cette classe permet de communiquer avec la
 * DLL de tetraedrisation
 * 
 * Internal class don't use it. This class serves to communicate with the DLL of
 * tetrahedrization
 * 
 */

public class Tetgenio {

  // Constructeur
  protected Tetgenio() {

    // Constructeur par défaut.
  }

  // /////////////////////////////////////////////////////////////////////////
  // /////////////////////////////////////Attributs //////////////////////////
  // //////////////////////////////////////////////////////////////////////////
  protected double[] pointlist = new double[1000000];
  protected double[] pointattributelist = null;
  protected double[] addpointlist = new double[1000000];
  protected int[] pointmarkerlist = null;
  protected int numberofpoints = 0;
  protected int numberofpointattributes = 0;
  protected int numberofaddpoints = 0;

  protected int[] tetrahedronlist = null;
  protected double[] tetrahedronattributelist = null;
  protected double[] tetrahedronvolumelist = null;
  protected int[] neighborlist = null;
  protected int numberoftetrahedra = 0;
  protected int numberofcorners = 0;
  protected int numberoftetrahedronattributes = 0;

  protected int[] facetmarkerlist = new int[1000000];
  protected int numberoffacets = 0;
  protected int[] facetvertexlist = new int[1000000];

  protected double[] holelist = null;
  protected int numberofholes = 0;

  protected double[] regionlist = null;
  protected int numberofregions = 0;

  protected double[] facetconstraintlist = null;
  protected int numberofacetconstraints = 0;

  protected double[] segmentconstraintlist = null;
  protected int numberofsegmentconstraints = 0;

  protected int[] trifacelist = null;
  protected int[] trifacemarkerlist = null;
  protected int numberoftrifaces = 0;

  protected int[] edgelist = null;
  protected int[] edgemarkerlist = null;
  protected int numberofedges = 0;

  // Initialisation
  protected void joutInit() {
    this.pointlist = new double[3 * this.numberofpoints];
    this.tetrahedronlist = new int[4 * this.numberoftetrahedra];
    this.trifacelist = new int[3 * this.numberoftrifaces];
    this.neighborlist = new int[4 * this.numberoftetrahedra];
  }

}

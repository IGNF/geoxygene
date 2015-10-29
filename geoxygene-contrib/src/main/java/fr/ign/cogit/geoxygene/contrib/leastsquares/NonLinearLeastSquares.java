/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.contrib.leastsquares;

import Jama.Matrix;

/**
 * Abstract class for processes encapsulating non linear (iterative) least
 * squares processes. Matrices are from Jama.
 * @author gtouya
 * 
 */
public abstract class NonLinearLeastSquares {

  /**
   * The maximum number of least squares iterations.
   */
  private int maxIterations = 500;

  public int getMaxIterations() {
    return maxIterations;
  }

  public void setMaxIterations(int maxIterations) {
    this.maxIterations = maxIterations;
  }

  /**
   * A method to get the Jacobean matrix A.
   * @return
   */
  public abstract Matrix getA();

  /**
   * A method to get the observations matrix Y.
   * @return
   */
  public abstract Matrix getY();

  /**
   * A method to get the weight matrix P.
   * @return
   */
  public abstract Matrix getP();

  /**
   * A method to get the matrix B, i.e. the observations Y minus S the current
   * solution dX.
   * @return
   */
  public abstract Matrix getB();

  /**
   * compute Dx = (A'PA)^1.(A'PB), i.e. the solution of an iteration.
   * @return
   */
  protected Matrix computeDx() {
    Matrix a = this.getA();
    Matrix at = a.transpose();
    Matrix p = this.getP();
    Matrix b = this.getB();
    System.out.println(at.times(p).times(a).det());
    if (at.times(p).times(a).det() == 0.0)
      return new Matrix(a.getColumnDimension(), 1);
    else {
      Matrix dx = (at.times(p).times(a)).inverse().times(at.times(p).times(b));
      return dx;
    }
  }
}

/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.spatialanalysis.measures.congestion;

import java.util.HashSet;

import fr.ign.cogit.geoxygene.util.algo.geomstructure.Vector2D;

public class ExhaustDirections {
  // //////////////////////////////////////////
  // Fields //
  // //////////////////////////////////////////

  // Private fields //
  private HashSet<Vector2D> displVectors;
  private Vector2D previousDispl;
  private int nbOrientations;
  private double authorizationThreshold;
  private HashSet<Vector2D> authorizedDirections;
  private Vector2D microDispl;

  // //////////////////////////////////////////
  // Public methods //
  // //////////////////////////////////////////

  // Public constructors //
  public ExhaustDirections(HashSet<Vector2D> displVectors,
      Vector2D previousDispl) {
    super();
    this.displVectors = displVectors;
    this.previousDispl = previousDispl;
    this.setNbOrientations(16);
    this.setAuthorizationThreshold(100.0 * Math.PI / 180.0);
    this.authorizedDirections = new HashSet<Vector2D>();
    this.computeExhaustDirections();
  }

  // Getters and setters //
  public Vector2D getMicroDispl() {
    return this.microDispl;
  }

  private void setMicroDispl(Vector2D microDispl) {
    this.microDispl = microDispl;
  }

  public void setNbOrientations(int nbOrientations) {
    this.nbOrientations = nbOrientations;
  }

  private int getNbOrientations() {
    return this.nbOrientations;
  }

  public void setAuthorizationThreshold(double authorizationThreshold) {
    this.authorizationThreshold = authorizationThreshold;
  }

  private double getAuthorizationThreshold() {
    return this.authorizationThreshold;
  }

  // ////////////////////////////////////////
  // Private methods //
  // ////////////////////////////////////////
  private void computeExhaustDirections() {
    // first compute the authorized directions according to the
    // displacement vectors
    this.computeAuthorizedDirections();
    // test if an exhaust direction exists
    if (this.authorizedDirections.size() == 0) {
      this.microDispl = new Vector2D(0.0, 0.0);
      return;
    }
    // then projects the previous displacement on the authorized directions
    this.projectDisplOnAuthDirections();
  }

  private void projectDisplOnAuthDirections() {
    double min = 2 * Math.PI;
    Vector2D nearest = new Vector2D();
    for (Vector2D v : this.authorizedDirections) {
      double diff = v.vectorAngle(this.previousDispl);
      if (Math.abs(diff) < min) {
        min = Math.abs(diff);
        double angle = v.direction().getValeur();
        if (diff < 0) {
          nearest = new Vector2D(Math
              .cos(angle - Math.PI / this.nbOrientations), Math.sin(angle
              - Math.PI / this.nbOrientations));
        } else {
          nearest = new Vector2D(Math
              .cos(angle + Math.PI / this.nbOrientations), Math.sin(angle
              + Math.PI / this.nbOrientations));
        }
      }
    }
    // now computes the norm of the projected vector
    double diff = nearest.vectorAngle(this.previousDispl);
    nearest.normalise();
    nearest.scalarMultiplication(this.previousDispl.norme()
        * Math.abs(Math.cos(diff)));
    this.setMicroDispl(nearest);
  }

  /**
   * Computes the authorized exhaust directions using each displacement vector
   * and aggregating their contribution. The method fills the
   * authorizedDirections field.
   * 
   * @author GTouya
   */
  private void computeAuthorizedDirections() {
    HashSet<HashSet<Vector2D>> authorizedSets = new HashSet<HashSet<Vector2D>>();
    // loop on the displacement vectors to add each vector contribution
    for (Vector2D v : this.displVectors) {
      authorizedSets.add(this.getAuthorizedDirections(v));
    }
    // now aggregates the directions
    this.aggregateExhaustDirections(authorizedSets);
  }

  /**
   * Computes the authorized directions from a single displacement vector. The
   * directions are provided in a vector form.
   * 
   * @param vector the displacement vector that authorizes exhaust directions
   * @return a set of vectors representing the authorized directions
   * @author GTouya
   */
  private HashSet<Vector2D> getAuthorizedDirections(Vector2D vector) {
    HashSet<Vector2D> authorizedDirs = new HashSet<Vector2D>();
    // get the direction of the vector
    double dir = vector.direction().getValeur();
    double step = 2 * Math.PI / this.getNbOrientations();
    // loop on the angles using the step
    for (double i = 0.0; i < this.getNbOrientations(); i++) {
      // if the direction is not authorized by vector, continue
      double angleDiff = Math.abs(dir - i * step);
      angleDiff = angleDiff % Math.PI;
      if (angleDiff > this.getAuthorizationThreshold()) {
        continue;
      }
      // the vector norm is softened as it far from dir
      double norm = 1 - angleDiff / Math.PI;
      // build the vector
      Vector2D vect = new Vector2D(Math.cos(i * step), Math.sin(i * step));
      vect.scalarMultiplication(norm);
      authorizedDirs.add(vect);
    }
    return authorizedDirs;
  }

  private void aggregateExhaustDirections(
      HashSet<HashSet<Vector2D>> authorizedSets) {
    // loop on the angles using the step
    double step = 2 * Math.PI / this.getNbOrientations();
    for (double i = 0.0; i < this.getNbOrientations(); i++) {
      // test if the current direction is authorized
      boolean authorized = true;
      for (HashSet<Vector2D> set : authorizedSets) {
        if (!this.isDirectionAuthorized(set, i * step)) {
          authorized = false;
          break;
        }
      }
      if (!authorized) {
        continue;
      }

      // now computes the norm of the exhaust direction
      double norm = 0.0;
      for (HashSet<Vector2D> set : authorizedSets) {
        norm += this.getDirectionNorm(set, i * step);
      }

      // builds the vector
      Vector2D vect = new Vector2D(Math.cos(i * step), Math.sin(i * step));
      vect.scalarMultiplication(norm);
      this.authorizedDirections.add(vect);
    }
  }

  private boolean isDirectionAuthorized(HashSet<Vector2D> authorizedDirs,
      double direction) {
    boolean authorized = false;
    for (Vector2D v : authorizedDirs) {
      if (v.direction().getValeur() == direction) {
        authorized = true;
        break;
      }
    }
    return authorized;
  }

  private double getDirectionNorm(HashSet<Vector2D> authorizedDirs,
      double direction) {
    for (Vector2D v : authorizedDirs) {
      if (v.direction().getValeur() == direction) {
        return v.norme();
      }
    }
    return 0.0;
  }
}

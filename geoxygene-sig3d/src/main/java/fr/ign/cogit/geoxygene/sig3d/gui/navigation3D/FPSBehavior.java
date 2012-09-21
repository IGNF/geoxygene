package fr.ign.cogit.geoxygene.sig3d.gui.navigation3D;

import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.WakeupCondition;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnElapsedFrames;

import org.apache.log4j.Logger;


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
 * 
 * @version 0.1
 * 
 * Cette classe permet d'afficher dans la console le nombre d'images par
 * secondes qui "défilent" dans la carte 3d
 * 
 * 
 * This class is used to display the number of frame per seconde displayed
 * 
 * 
 */
public class FPSBehavior extends Behavior {
  protected WakeupCondition wakeupCondition = null;
  protected long startTime = 0;
  protected final int nbImageEcoulees = 10;

  private final static Logger logger = Logger.getLogger(FPSBehavior.class
      .getName());

  int count = 0;
  long tot = 0;

  /**
   * Constructeur de notre comportement
   */
  public FPSBehavior() {
    this.wakeupCondition = new WakeupOnElapsedFrames(this.nbImageEcoulees);
  }

  /**
   * Initialisation du comportement : La methode processStimulus() sera appelee
   * a chaque fois que "nbImageEcoulees" auront ete affichees
   */
  @Override
  public void initialize() {
    this.wakeupOn(this.wakeupCondition);
  }

  /**
   * Methode appelee a chaque fois que "nbImageEcoulees" auront ete affichees.
   * 
   * @param criteria critere ayant declenche le stimulus
   */
  @SuppressWarnings("unchecked")
  @Override
  public void processStimulus(Enumeration criteria) {
    WakeupCriterion critere;
    long interval;

    // On boucle sur les criteres ayant declenche le comportement
    while (criteria.hasMoreElements()) {
      critere = (WakeupCriterion) criteria.nextElement();

      if (critere instanceof WakeupOnElapsedFrames) {
        // Calcul de la duree (en ms) necessaire pour l'affichage des
        // "nbImagesEcoulees" images
        interval = System.currentTimeMillis() - this.startTime;

        // Calcul du nombre d'images par secondes affichees
        if ((this.startTime > 0) && (interval != 0)) {

          this.count++;

          long FPS = (this.nbImageEcoulees * 1000) / interval;
          this.tot = this.tot + FPS;
          long moy = this.tot / this.count;

          FPSBehavior.logger.info("FPS : " + FPS + "moy" + moy);
        }

        this.startTime = System.currentTimeMillis();
      } // fin if (critere instanceof WakeupOnElapsedFrames)
    } // fin while (criteria.hasMoreElements())

    // Une fois le stimulus traite, on reinitialise le comportement
    this.wakeupOn(this.wakeupCondition);
  }
}

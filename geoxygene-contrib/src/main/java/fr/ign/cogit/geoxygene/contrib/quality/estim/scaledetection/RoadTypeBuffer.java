package fr.ign.cogit.geoxygene.contrib.quality.estim.scaledetection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author JFGirres
 */
public class RoadTypeBuffer {

  static Logger logger = LogManager.getLogger(RoadTypeBuffer.class.getName());

  /**
   * A class to compute the buffer size used for the symbology of a road network
   * on two categories of cartographic data (TOP100 and Regional Map)
   * @param arc
   * @return
   */
  public static double computeSizeTop100(Arc arc, double scale) {

    double tailleBuffer;

    String classeVoc = (String) arc.getCorrespondant(0)
        .getAttribute("VOCATION");
    String classeNvVoies = (String) arc.getCorrespondant(0).getAttribute(
        "NBVOIES");

    if (classeVoc.equalsIgnoreCase("Type Autoroutier")) {
      tailleBuffer = 1.5 * scale / 2000;

      // on divise par 2000 au lieu de 1000 car on se base sur la demi-largeur
      // du symbole
    } else {
      if (classeVoc.endsWith("gionale")) {
        if (classeNvVoies.startsWith("4")) {
          tailleBuffer = 1.5 * scale / 2000;
        }
        if (classeNvVoies.startsWith("3")) {
          tailleBuffer = 0.92 * scale / 2000;
        }
        if (classeNvVoies.startsWith("2")) {
          tailleBuffer = 0.92 * scale / 2000;
        } else {
          tailleBuffer = 0.69 * scale / 2000;
        }
      }
      if (classeVoc.endsWith("principale")) {
        if (classeNvVoies.startsWith("4")) {
          tailleBuffer = 1.5 * scale / 2000;
        }
        if (classeNvVoies.startsWith("3")) {
          tailleBuffer = 0.92 * scale / 2000;
        }
        if (classeNvVoies.startsWith("2")) {
          tailleBuffer = 0.92 * scale / 2000;
        } else {
          tailleBuffer = 0.69 * scale / 2000;
        }
      }

      else {
        tailleBuffer = 0.69 * scale / 2000;
      }
    }

    return tailleBuffer;
  }

  /**
   * Compute Buffer size according to the road category from Top 100 maps
   * @param arc
   * @return
   */
  public static double computeSizeCR250(Arc arc, double scale) {

    double tailleBuffer = 0;

    String classeCat = (String) arc.getCorrespondant(0)
        .getAttribute("SYMBOLIS");

    if (classeCat.startsWith("autoroute")) {
      tailleBuffer = 1.5 * scale / 2000;
    }

    if (classeCat.startsWith("type auto")) {
      tailleBuffer = 1.5 * scale / 2000;
    }

    if (classeCat.endsWith("4 voies")) {
      tailleBuffer = 1.5 * scale / 2000;
    }

    if (classeCat.equals("principale")) {
      tailleBuffer = 1 * scale / 2000;
    }

    if (classeCat.equals("principale en construction")) {
      tailleBuffer = 1 * scale / 2000;
    }

    if (classeCat.endsWith("gionale")) {
      tailleBuffer = 1 * scale / 2000;
    }

    if (classeCat.endsWith("gionale en construction")) {
      tailleBuffer = 1 * scale / 2000;
    }

    if (classeCat.endsWith("verte")) {
      tailleBuffer = 1 * scale / 2000;
    }

    if (classeCat.startsWith("principale") && classeCat.endsWith("troite")) {
      tailleBuffer = 0.75 * scale / 2000;
    }

    if (classeCat.startsWith("r") && classeCat.endsWith("troite")) {
      tailleBuffer = 0.75 * scale / 2000;
    }

    if (classeCat.startsWith("verte") && classeCat.endsWith("troite")) {
      tailleBuffer = 0.75 * scale / 2000;
    }

    if (classeCat.equals("locale")) {
      tailleBuffer = 0.75 * scale / 2000;
    }

    if (classeCat.startsWith("locale") && classeCat.endsWith("troite")) {
      tailleBuffer = 0.5 * scale / 2000;
    }

    if (classeCat.equals("interdite")) {
      tailleBuffer = 0.5 * scale / 2000;
    }

    if (classeCat.equals("chemin")) {
      tailleBuffer = 0.25 * scale / 2000;
    }

    if (classeCat.equals("autre route")) {
      tailleBuffer = 0.25 * scale / 2000;
    }

    return tailleBuffer;

  }

}

package fr.ign.cogit.cartagen.software.viewer;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import fr.ign.cogit.cartagen.core.defaultschema.DefaultCreationFactory;
import fr.ign.cogit.cartagen.software.CartagenApplication;
import fr.ign.cogit.cartagen.software.GeneralisationSpecifications;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore.Legend;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplicationProperties;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.SplashScreen;
import fr.ign.cogit.geoxygene.appli.plugin.GeOxygeneApplicationPlugin;

public class CartAGenApplicationTest extends GeOxygeneApplication {

  private static Logger logger = Logger.getLogger(CartAGenApplicationTest.class
      .getName());
  /**
   * Singleton
   */
  private static CartAGenApplicationTest cartagenApplication = null;

  /**
   * private main frame of the application.
   */
  private CartAGenFrame frame;

  /**
   * Recuperation de l'instance unique (singleton)
   * @return instance unique (singleton) de CartagenApplication.
   */
  public static CartAGenApplicationTest getInstance() {
    if (CartAGenApplicationTest.cartagenApplication == null) {
      synchronized (CartAGenApplicationTest.class) {
        if (CartAGenApplicationTest.cartagenApplication == null) {
          CartAGenApplicationTest.cartagenApplication = new CartAGenApplicationTest();
        }
      }
    }
    return CartAGenApplicationTest.cartagenApplication;
  }

  /**
   * Default constructor
   */
  private CartAGenApplicationTest() {
    super(true);
    this.frame = new CartAGenFrame("CartAGen - GeOxygene", this);
    this.frame.setVisible(true);
    this.initializeProperties();
    // pour permettre le fonctionnement du code
    CartagenApplication.getInstance().setCreationFactory(
        new DefaultCreationFactory());
  }

  /**
   * The {@link CartAGenDoc} related to the main application.
   */
  private CartAGenDoc document = null;

  public CartAGenDoc getDocument() {
    if (this.document == null) {
      synchronized (CartAGenApplicationTest.class) {
        if (this.document == null) {
          this.document = CartAGenDoc.getInstance();
        }
      }
    }
    return this.document;
  }

  @Override
  public CartAGenFrame getFrame() {
    return frame;
  }

  private String cheminDonnees = null;
  private String cheminDonneesInitial = null;

  private final String cheminFichierConfigurationDonnees = "/configurationDonnees.xml";

  private final String cheminFichierConfigurationGeneralisation = "/configurationGeneralisation.xml";

  public String getCheminFichierConfigurationGene() {
    return this.cheminFichierConfigurationGeneralisation;
  }

  public String getCheminFichierConfigurationChargementDonnees() {
    return this.cheminFichierConfigurationDonnees;
  }

  public String getCheminDonnees() {
    return this.cheminDonnees;
  }

  public void setCheminDonnees(String chemin) {
    this.cheminDonnees = chemin;
  }

  public String getCheminDonneesInitial() {
    return this.cheminDonneesInitial;
  }

  public void setCheminDonneesInitial(String chemin) {
    this.cheminDonneesInitial = chemin;
  }

  /**
   * Initialize the application plugins.
   */
  private void initializeProperties() {
    try {
      this.setPropertiesFile(new URL("file", "", "./plugins.xml")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
    this.setProperties(GeOxygeneApplicationProperties.unmarshall(this
        .getPropertiesFile().getFile()));
    if (this.getProperties().getLastOpenedFile() != null) {
      MainFrame.getFilechooser().setPreviousDirectory(
          new File(this.getProperties().getLastOpenedFile()));
    }
    for (String pluginName : this.getProperties().getPlugins()) {
      try {
        Class<?> pluginClass = Class.forName(pluginName);
        GeOxygeneApplicationPlugin plugin = (GeOxygeneApplicationPlugin) pluginClass
            .newInstance();
        plugin.initialize(this);
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Main GeOxygene Application.
   * @param args arguments of the application
   */
  public static void main(final String[] args) {
    SplashScreen splashScreen = new SplashScreen(
        GeOxygeneApplication.splashImage(), "CartAGen - GeOxygene"); //$NON-NLS-1$
    splashScreen.setVisible(true);
    CartAGenApplicationTest application = CartAGenApplicationTest.getInstance();
    application.getFrame().newCartProjectFrame();
    application.getFrame().setVisible(true);
    splashScreen.setVisible(false);
    splashScreen.dispose();
  }

  /**
   * Generalisation configuration file
   */

  public void lectureFichierConfigurationGeneralisation() {

    // le document XML
    Document docXML = null;
    try {
      docXML = DocumentBuilderFactory
          .newInstance()
          .newDocumentBuilder()
          .parse(
              CartAGenApplicationTest.class
                  .getResourceAsStream(this.cheminFichierConfigurationGeneralisation));
    } catch (FileNotFoundException e) {
      CartAGenApplicationTest.logger.error("Fichier non trouvé: "
          + this.cheminFichierConfigurationGeneralisation);
      e.printStackTrace();
      return;
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (docXML == null) {
      CartAGenApplicationTest.logger.error("Erreur lors de la lecture de: "
          + this.cheminFichierConfigurationDonnees);
      return;
    }

    Element configurationGeneralisationMirageXML = (Element) docXML
        .getElementsByTagName("configurationGeneralisationCartagen").item(0);
    if (configurationGeneralisationMirageXML == null) {
      return;
    }

    // general
    Element generalXML = (Element) configurationGeneralisationMirageXML
        .getElementsByTagName("general").item(0);
    if (generalXML != null) {
      Element elXML = null;
      // description
      elXML = (Element) generalXML.getElementsByTagName("description").item(0);
      if (elXML != null) {
        GeneralisationSpecifications.setDESCRIPTION(elXML.getFirstChild()
            .getNodeValue());
      }

      // echelle cible
      elXML = (Element) generalXML.getElementsByTagName("echelleCible").item(0);
      if (elXML != null) {
        Legend.setSYMBOLISATI0N_SCALE(Double.parseDouble(elXML.getFirstChild()
            .getNodeValue()));
      }

      // resolution
      elXML = (Element) generalXML.getElementsByTagName("resolution").item(0);
      if (elXML != null) {
        GeneralisationSpecifications.setRESOLUTION(Double.parseDouble(elXML
            .getFirstChild().getNodeValue()));
      }

    }

    // themes
    Element contrainteXML = (Element) configurationGeneralisationMirageXML
        .getElementsByTagName("themes").item(0);
    if (contrainteXML != null) {

      Element contXML = null;
      Element elXML = null;

      // bati
      Element batiXML = (Element) contrainteXML.getElementsByTagName("bati")
          .item(0);
      if (batiXML != null) {

        // taille
        contXML = (Element) batiXML.getElementsByTagName("taille").item(0);
        if (contXML != null) {

          elXML = (Element) contXML.getElementsByTagName("aireMinimale")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.AIRE_MINIMALE_BATIMENT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML
              .getElementsByTagName("aireSeuilSuppression").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.AIRE_SEUIL_SUPPRESSION_BATIMENT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // granularite
        contXML = (Element) batiXML.getElementsByTagName("granularite").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("longueurMinimale")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.LONGUEUR_MINI_GRANULARITE = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // equarrite
        contXML = (Element) batiXML.getElementsByTagName("equarrite").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("toleranceAngle")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.TOLERANCE_ANGLE = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // convexite
        contXML = (Element) batiXML.getElementsByTagName("convexite").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("convexiteMini").item(
              0);
          if (elXML != null) {
            GeneralisationSpecifications.BUILDING_CONVEXITE_MINI = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // elongation
        contXML = (Element) batiXML.getElementsByTagName("elongation").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("elongationMini")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.BUILDING_ELONGATION_MINI = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // altitude
        contXML = (Element) batiXML.getElementsByTagName("altitude").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("denivelleeMini")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DENIVELLEE_MINI = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // proximite
        contXML = (Element) batiXML.getElementsByTagName(
            "proximiteBatimentsIlot").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName(
              "distanceSeparationBatiments").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_BATIMENT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML.getElementsByTagName(
              "distancMaxDeplacementBatiment").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_MAX_DEPLACEMENT_BATIMENT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML.getElementsByTagName(
              "distanceSeparationBatimentsRoutes").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_SEPARATION_BATIMENT_ROUTE = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML.getElementsByTagName(
              "seuilTauxSuperpositionSuppression").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.SEUIL_TAUX_SUPERPOSITION_SUPPRESSION = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML
              .getElementsByTagName("distanceMaxProximite").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_MAX_PROXIMITE = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // densite ilots
        contXML = (Element) batiXML.getElementsByTagName("densiteIlot").item(0);
        if (contXML != null) {

          elXML = (Element) contXML.getElementsByTagName("ratioDensiteIlot")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.RATIO_BLOCK_DENSITY = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML
              .getElementsByTagName("densiteLimiteGrisage").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DENSITE_LIMITE_GRISAGE_ILOT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML.getElementsByTagName(
              "ratioDensiteReduction").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DENSITE_RATIO_REDUCTION_MAX = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // grands batiments
        contXML = (Element) batiXML.getElementsByTagName("grandsBatimentse")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("aireMin").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.GRANDS_BATIMENTS_AIRE = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

      }

      // routier
      Element routierXML = (Element) contrainteXML.getElementsByTagName(
          "routier").item(0);
      if (routierXML != null) {

        // enpatement
        contXML = (Element) routierXML.getElementsByTagName("empatement").item(
            0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("coeffPropagation")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.ROUTIER_COEFF_PROPAGATION_EMPATEMENT = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // impasses
        contXML = (Element) routierXML.getElementsByTagName("impasses").item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("longueurMin").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.ROADS_DEADEND_MIN_LENGTH = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

        // densite Ville
        contXML = (Element) routierXML.getElementsByTagName("densiteVille")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("densiteRouteVille")
              .item(0);
          if (elXML != null) {
            GeneralisationSpecifications.ROAD_TOWN_DENSITY = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

      }

      // hydro
      Element hydroXML = (Element) contrainteXML.getElementsByTagName("hydro")
          .item(0);
      if (hydroXML != null) {

        // proximite routier
        contXML = (Element) hydroXML.getElementsByTagName("proximiteRoutier")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName(
              "distanceSeparationHydroRoute").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_SEPARATION_HYDRO_ROUTIER = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }

          elXML = (Element) contXML.getElementsByTagName(
              "tauxSuperpositionHydroRoute").item(0);
          if (elXML != null) {
            GeneralisationSpecifications.TAUX_SUPERPOSITION_HYDRO_ROUTIER = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

      }

      // relief
      Element reliefXML = (Element) contrainteXML
          .getElementsByTagName("relief").item(0);
      if (reliefXML != null) {

        // courbes de niveau
        contXML = (Element) reliefXML.getElementsByTagName("courbesNiveau")
            .item(0);
        if (contXML != null) {
          elXML = (Element) contXML.getElementsByTagName("interDistance").item(
              0);
          if (elXML != null) {
            GeneralisationSpecifications.DISTANCE_SEPARATION_INTER_CN = Double
                .parseDouble(elXML.getFirstChild().getNodeValue());
          }
        }

      }

      // occupation du sol
      Element occSolXML = (Element) contrainteXML
          .getElementsByTagName("occSol").item(0);
      if (occSolXML != null) {
      }

      if (logger.isDebugEnabled()) {
        logger.debug("fin chargement de la configuration de généralisation");
      }
    }

  }

}

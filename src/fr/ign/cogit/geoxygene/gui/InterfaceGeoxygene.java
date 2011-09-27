package fr.ign.cogit.geoxygene.gui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.LayerFactory;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;
import fr.ign.cogit.geoxygene.util.index.Tiling;
import fr.ign.cogit.geoxygene.util.loader.Chargement;

/**
 * La fenetre principale de l'interface
 * @author julien Gaffuri
 * 
 */
public class InterfaceGeoxygene extends JFrame {
  private static final long serialVersionUID = -3566099212243269188L;
  static Logger logger = Logger.getLogger(InterfaceGeoxygene.class.getName());

  private MenuGeoxygene menu = null;

  /**
   * Renvoie le menu
   * @return le menu
   */
  public MenuGeoxygene getMenu() {
    if (this.menu == null) {
      synchronized (InterfaceGeoxygene.class) {
        if (this.menu == null) {
          this.menu = new MenuGeoxygene(this);
        }
      }
    }
    return this.menu;
  }

  private PanelVisu panelVisu = null;

  /**
   * Renvoie le panneau de visualisation
   * @return le panneau de visualisation
   */
  public PanelVisu getPanelVisu() {
    if (this.panelVisu == null) {
      synchronized (InterfaceGeoxygene.class) {
        if (this.panelVisu == null) {
          this.panelVisu = new PanelVisu(this);
        }
      }
    }
    return this.panelVisu;
  }

  private PanelHaut panelhaut = null;

  /**
   * Renvoie le panneau en haut, sous le menu (barre d'outils)
   * @return le panneau en haut, sous le menu (barre d'outils)
   */
  public PanelHaut getPanelHaut() {
    if (this.panelhaut == null) {
      synchronized (InterfaceGeoxygene.class) {
        if (this.panelhaut == null) {
          this.panelhaut = new PanelHaut(this);
        }
      }
    }
    return this.panelhaut;
  }

  private PanelBas panelBas = null;

  /**
   * Renvoie le panneau en bas
   * @return le panneau en bas
   */
  public PanelBas getPanelBas() {
    if (this.panelBas == null) {
      synchronized (InterfaceGeoxygene.class) {
        if (this.panelBas == null) {
          this.panelBas = new PanelBas(this);
        }
      }
    }
    return this.panelBas;
  }

  private PanelDroit panelDroit = null;

  /**
   * Renvoie le panneau de droite
   * @return le panneau de droite
   */
  public PanelDroit getPanelDroit() {
    if (this.panelDroit == null) {
      synchronized (InterfaceGeoxygene.class) {
        if (this.panelDroit == null) {
          this.panelDroit = new PanelDroit(this);
        }
      }
    }
    return this.panelDroit;
  }

  private PanelGauche panelGauche = null;

  /**
   * Renvoie le panneau de gauche
   * @return le panneau de gauche
   */
  public PanelGauche getPanelGauche() {
    if (this.panelGauche == null) {
      synchronized (InterfaceGeoxygene.class) {
        if (this.panelGauche == null) {
          this.panelGauche = new PanelGauche(this);
        }
      }
    }
    return this.panelGauche;
  }

  private static Image icone;

  /**
   * Renvoie l'icone
   * @return l'icone
   */
  public static Image getIcone() {
    if (InterfaceGeoxygene.icone == null) {
      InterfaceGeoxygene.icone = (new ImageIcon("images/icone.gif")).getImage();
    }
    return InterfaceGeoxygene.icone;
  }

  Chargement chargement = new Chargement();

  /**
   * Constructeur de l'interface GeOxygene
   */
  public InterfaceGeoxygene() {
    this.enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    this.setLayout(new BorderLayout());
    this.setResizable(true);
    // setLocation(100,100);
    this.setSize(new Dimension(830, 530));
    this.setExtendedState(Frame.MAXIMIZED_BOTH);
    this.setTitle("GéOxygène");
    this.setIconImage(InterfaceGeoxygene.getIcone());

    JSplitPane splitPaneGauche = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        this.getPanelVisu(), this.getPanelDroit());
    splitPaneGauche.setContinuousLayout(false);
    splitPaneGauche.setOneTouchExpandable(true);
    splitPaneGauche.setDividerLocation(10000);
    splitPaneGauche.addPropertyChangeListener("dividerLocation",
        new PropertyChangeListener() {
          @Override
          public void propertyChange(PropertyChangeEvent arg0) {
            InterfaceGeoxygene.this.getPanelVisu().repaint();
          }
        });

    this.add(splitPaneGauche, BorderLayout.CENTER);
    this.add(this.getPanelGauche(), BorderLayout.LINE_START);
    this.add(this.getPanelBas(), BorderLayout.PAGE_END);
    this.add(this.getPanelHaut(), BorderLayout.NORTH);

    this.setJMenuBar(this.getMenu());

    this.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent e) {
        /**
         * Sauve le sld courant et la liste des fichiers ouverts...
         */
        /*
         * logger.info("Sauvegarde d'un sld portant "+getPanelVisu().getSld().
         * getLayers ().size()+" layers");
         * getPanelVisu().getSld().toXml("sld.xml");
         * chargement.setDataSet(getPanelVisu().getDataset());
         * chargement.toXml("chargement.xml");
         */
        System.exit(0);
      }

      @Override
      public void windowActivated(WindowEvent e) {
        InterfaceGeoxygene.this.getPanelVisu().requestFocusInWindow();
      }
    });

    this.addComponentListener(new ComponentListener() {
      @Override
      public void componentHidden(ComponentEvent e) {
      }

      @Override
      public void componentMoved(ComponentEvent e) {
      }

      @Override
      public void componentResized(ComponentEvent e) {
        InterfaceGeoxygene.this.getPanelVisu().repaint();
      }

      @Override
      public void componentShown(ComponentEvent e) {
        InterfaceGeoxygene.this.getPanelVisu().repaint();
      }
    });
  }

  /**
   * Ouvre l'interface de chargement de fichiers shape.
   * @see FrameChargement
   * @see ShapefileReader
   * @see StyledLayerDescriptor
   * @see Layer
   */
  public void chargeShapefiles() {
    FrameChargement chargeur = new FrameChargement(this.getPanelVisu()
        .getDefaultSld());
    boolean validated = chargeur.showDialog();
    if (validated) {
      List<File> shapeFiles = chargeur.shapeFiles;
      List<Integer> shapeFileLayers = chargeur.shapeFileLayers;
      List<String> layerNames = chargeur.layerNames;
      for (int i = 0; i < shapeFiles.size(); i++) {
        String populationName = layerNames.get(shapeFileLayers.get(i));
        String shapefileName = shapeFiles.get(i).getAbsolutePath();
        if (shapeFileLayers.get(i) == 0) {
          populationName = shapeFiles.get(i).getName();
          if (this.getPanelVisu().getDataset().getPopulation(populationName) != null) {
            /** Il existe déjà une population avec ce nom */
            int n = 2;
            while (this.getPanelVisu().getDataset()
                .getPopulation(populationName + " (" + n + ")") != null) {
              n++;
            }
            populationName = populationName + " (" + n + ")";
          }
        } else {
          if (this.getPanelVisu().getDataset().getPopulation(populationName) != null) {
            /** Il existe déjà une population avec ce nom */
            int n = 2;
            while (this.getPanelVisu().getDataset()
                .getPopulation(populationName + " (" + n + ")") != null) {
              n++;
            }
            populationName = populationName + " (" + n + ")";
          }
        }
        this.chargeShapefile(shapefileName, populationName);
      }
    }
  }

  public void chargeShapefile(String shapefileName, String populationName) {
    /** Création du ShapefilReader pour le chargement asynchrone des fichiers */
    ShapefileReader reader = new ShapefileReader(shapefileName, populationName,
        this.getPanelVisu().getDataset(), true);
    /**
     * Ajoute le panel de visualisation au listeners de la population. Il sera
     * informé de l'ajout des nouveaux objets au fur et à mesure.
     */
    reader.getPopulation().addFeatureCollectionListener(this.getPanelVisu());
    /**
     * Ajoute un layer au SLD du panel de visualisation. On essaye de récupérer
     * les styles existants depuis le sld courant ou depuis le sld par défaut
     */
    Layer layer = this.getPanelVisu().getSld().getLayer(populationName);
    /**
     * On n'a pas trouvé la couche dans le SLD courant, on cherche dans le sld
     * par défaut
     */
    if (layer == null) {
      if (InterfaceGeoxygene.logger.isDebugEnabled()) {
        InterfaceGeoxygene.logger.debug("Layer " + populationName
            + " non trouvé dans le sld courant");
      }
      layer = this.getPanelVisu().getDefaultSld().getLayer(populationName);
      /** On n'a pas trouvé la couche dans le SLD, on en crée une nouvelle */
      if (layer == null) {
        if (InterfaceGeoxygene.logger.isDebugEnabled()) {
          InterfaceGeoxygene.logger.debug("Layer " + populationName
              + " non trouvé dans le sld par défaut");
        }
        LayerFactory factory = new LayerFactory(this.getPanelVisu().getSld());
        layer = factory.createLayer(populationName,
                reader.getPopulation().getFeatureType().getGeometryType());
      }
      this.getPanelVisu().getSld().getLayers().add(layer);
    }
    this.getPanelVisu().centrer();
    this.chargement.getFichiers().put(populationName, shapefileName);
    /** On lance le chargement asynchrone */
    reader.read();
  }

  /**
   * @param population
   * @param populationName
   * @param color
   */
  public void addPopulation(Population<? extends FT_Feature> population,
      String populationName, Color color) {
    if (population.isEmpty()) {
      InterfaceGeoxygene.logger
          .error("Aucun élément à afficher : collection vide");
      return;
    }
    this.getPanelVisu().getDataset().addPopulation(population);
    /**
     * Ajoute un layer au SLD du panel de visualisation. On essaye de récupérer
     * les styles existants depuis le sld courant ou depuis le sld par défaut
     */
    Layer layer = this.getPanelVisu().getSld().getLayer(populationName);
    /**
     * On n'a pas trouvé la couche dans le SLD courant, on cherche dans le sld
     * par défaut
     */
    if (layer == null) {
      if (InterfaceGeoxygene.logger.isDebugEnabled()) {
        InterfaceGeoxygene.logger.debug("Layer " + populationName
            + " non trouvé dans le sld courant");
      }
      layer = this.getPanelVisu().getDefaultSld().getLayer(populationName);
      /** On n'a pas trouvé la couche dans le SLD, on en crée une nouvelle */
      if (layer == null) {
        if (InterfaceGeoxygene.logger.isDebugEnabled()) {
          InterfaceGeoxygene.logger.debug("Layer " + populationName
              + " non trouvé dans le sld par défaut");
        }
        if (population.getFeatureType() == null) {
          layer = this
              .getPanelVisu()
              .getSld()
              .createLayer(populationName,
                  population.get(0).getGeom().getClass(), color);
        } else {
          layer = this
              .getPanelVisu()
              .getSld()
              .createLayer(populationName,
                  population.getFeatureType().getGeometryType(), color);
        }
      }
      this.getPanelVisu().getSld().getLayers().add(layer);
    }
    if (!population.hasSpatialIndex()) {
      population.initSpatialIndex(Tiling.class, true);
    }
    this.getPanelVisu().centrer();
  }

  /**
   * @param population
   * @param populationName
   */
  public void addPopulation(Population<? extends FT_Feature> population,
      String populationName) {
    this.addPopulation(
        population,
        populationName,
        new Color((float) Math.random(), (float) Math.random(), (float) Math
            .random(), 0.5f));
  }

  /**
   * @param collection
   * @param populationName
   */
  public void addFeatureCollection(
      FT_FeatureCollection<? extends FT_Feature> collection,
      String populationName) {
    this.addFeatureCollection(collection, populationName, new Color(
        (float) Math.random(), (float) Math.random(), (float) Math.random(),
        0.5f));
  }

  /**
   * @param collection
   * @param populationName
   * @param color
   */
  @SuppressWarnings("unchecked")
  public void addFeatureCollection(
      FT_FeatureCollection<? extends FT_Feature> collection,
      String populationName, Color color) {
    if (collection.isEmpty()) {
      InterfaceGeoxygene.logger
          .error("Aucun élément à afficher : collection vide");
      return;
    }
    Population<FT_Feature> population = new Population<FT_Feature>(
        populationName);
    population.setElements(collection.getElements());
    this.getPanelVisu().getDataset().addPopulation(population);
    /**
     * Ajoute un layer au SLD du panel de visualisation. On essaye de récupérer
     * les styles existants depuis le sld courant ou depuis le sld par défaut
     */
    Layer layer = this.getPanelVisu().getSld().getLayer(populationName);
    /**
     * On n'a pas trouvé la couche dans le SLD courant, on cherche dans le sld
     * par défaut
     */
    if (layer == null) {
      if (InterfaceGeoxygene.logger.isDebugEnabled()) {
        InterfaceGeoxygene.logger.debug("Layer " + populationName
            + " non trouvé dans le sld courant");
      }
      layer = this.getPanelVisu().getDefaultSld().getLayer(populationName);
      /** On n'a pas trouvé la couche dans le SLD, on en crée une nouvelle */
      if (layer == null) {
        if (InterfaceGeoxygene.logger.isDebugEnabled()) {
          InterfaceGeoxygene.logger.debug("Layer " + populationName
              + " non trouvé dans le sld par défaut");
        }
        if (population.getFeatureType() == null) {
          layer = this
              .getPanelVisu()
              .getSld()
              .createLayer(populationName,
                  population.get(0).getGeom().getClass(), color);
        } else {
          layer = this
              .getPanelVisu()
              .getSld()
              .createLayer(populationName,
                  population.getFeatureType().getGeometryType(), color);
        }
      }
      this.getPanelVisu().getSld().getLayers().add(layer);
    }
    if (!population.hasSpatialIndex()) {
      population.initSpatialIndex(Tiling.class, true);
    }
    this.getPanelVisu().centrer();
  }
}

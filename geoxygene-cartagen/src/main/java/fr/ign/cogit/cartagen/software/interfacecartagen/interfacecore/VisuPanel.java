/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
/*
 * Cree le 26 juil. 2005
 */
package fr.ign.cogit.cartagen.software.interfacecartagen.interfacecore;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;

import org.apache.log4j.Logger;

import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.event.PaintListener;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IArc;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IArc2;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineSegment;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IAggregate;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiCurve;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.ICurve;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * @author julien Gaffuri
 */
public class VisuPanel extends JPanel implements Runnable, Printable {
  private static final long serialVersionUID = 6867389438475472471L;
  private final static Logger logger = Logger.getLogger(VisuPanel.class
      .getName());

  private GeoxygeneFrame frame = null;

  public GeoxygeneFrame getFrame() {
    return this.frame;
  }

  private LayerManager layerManager = null;

  public LayerManager getLayerManager() {
    return this.layerManager;
  }

  /**
   * coordonnees geographiques du centre du panneau. ce champ determine la
   * localisation de la zone affichee..
   */
  private IDirectPosition geoCenter = new DirectPosition(0.0, 0.0);

  public IDirectPosition getGeoCenter() {
    return this.geoCenter;
  }

  public void setGeoCenter(IDirectPosition centreGeo) {
    this.geoCenter = centreGeo;
  }

  /**
   * geometrie en coordonnees geographiques de la fenetre d'affichage, du cadre:
   * tous les objets intersectant ce cadre seront affiches
   */
  private IEnvelope displayEnvelope = null;

  public IEnvelope getDisplayEnvelope() {
    return this.displayEnvelope;
  }

  private int marginPixelsNb = 20;

  public double getXMax() {
    return this.displayEnvelope.maxX();
  }

  public double getXMin() {
    return this.displayEnvelope.minX();
  }

  public double getYMax() {
    return this.displayEnvelope.maxY();
  }

  public double getYMin() {
    return this.displayEnvelope.minY();
  }

  /**
   * taille d'un pixel (la longueur d'un cote de pixel represente une longueur
   * de taillePixel dans la realite) ce champ est celui qui permet de changer le
   * zoom de la vue
   */
  private double pixelSize = 2.0;

  public double getPixelSize() {
    return this.pixelSize;
  }

  public void setPixelSize(double tp) {
    this.pixelSize = tp;
  }

  /**
   * taille d'un pixel en m (la longueur d'un cote de pixel de l'ecran) utilise
   * pour le calcul de l'echelle courante de la vue, avec 'taillePixel'
   */
  private final static double METERS_PER_PIXEL;

  public static double getMETERS_PER_PIXEL() {
    return VisuPanel.METERS_PER_PIXEL;
  }

  static {
    // elle est calculee a partie de la resolution de l'ecran en DPI.
    // par exemple si la resolution est 90DPI, c'est: 90 pix/inch = 1/90
    // inch/pix = 0.0254/90 meter/pix
    METERS_PER_PIXEL = 0.02540005 / Toolkit.getDefaultToolkit()
        .getScreenResolution();
    // System.out.print(METERS_PER_PIXEL*1280);
  }

  /**
   * facteur multiplicatif utilise a chaque changement de zoom
   */
  private final double zoomFactor = 1.3;

  double getZoomFactor() {
    return this.zoomFactor;
  }

  /**
   * facteur multiplicatif utilise a chaque deplacement avec les fleches du pave
   * directionnel
   */
  private final double panFactor = 0.2;

  double getPanFactor() {
    return this.panFactor;
  }

  /**
   * les objets selectionnes
   */
  public IFeatureCollection<IFeature> selectedObjects = new FT_FeatureCollection<IFeature>();

  public IFeatureCollection<IFeature> getSelectedFeatures() {
    return this.selectedObjects;
  }

  /**
   * la couleur des objets selectionnes
   */
  private Color SELECTION_COLOR = new Color(255, 0, 0, 100);
  private boolean transparentSelection = true;

  public Color getSelectionColor() {
    return SELECTION_COLOR;
  }

  public void setSelectionColor(Color color) {
    SELECTION_COLOR = color;
  }

  public boolean isTransparentSelection() {
    return transparentSelection;
  }

  public void setTransparentSelection(boolean transparentSelection) {
    this.transparentSelection = transparentSelection;
  }

  // distance en m pour la selection
  private double selectionDistance = 5;

  public double getSelectionDistance() {
    return this.selectionDistance;
  }

  public void setDistanceSelection(double d) {
    this.selectionDistance = d;
  }

  private MouseListenerGeox mouseListenerGeox = null;

  public MouseListenerGeox getMouseListenerGeox() {
    if (this.mouseListenerGeox == null) {
      this.mouseListenerGeox = new MouseListenerGeox();
    }
    return this.mouseListenerGeox;
  }

  private MouseWheelListenerGeox mouseWheelListenerGeox = null;

  public MouseWheelListenerGeox getMouseWheelListenerGeox() {
    if (this.mouseWheelListenerGeox == null) {
      this.mouseWheelListenerGeox = new MouseWheelListenerGeox();
    }
    return this.mouseWheelListenerGeox;
  }

  private KeyListenerGeox keyListenerGeox = null;

  public KeyListenerGeox getKeyListenerGeox() {
    if (this.keyListenerGeox == null) {
      this.keyListenerGeox = new KeyListenerGeox();
    }
    return this.keyListenerGeox;
  }

  private MouseMotionListenerGeox mouseMotionListenerGeox = null;

  public MouseMotionListenerGeox getMouseMotionListenerGeox() {
    if (this.mouseMotionListenerGeox == null) {
      this.mouseMotionListenerGeox = new MouseMotionListenerGeox();
    }
    return this.mouseMotionListenerGeox;
  }

  public VisuPanel(GeoxygeneFrame frame, LayerManager layerManager) {
    this.frame = frame;
    this.layerManager = layerManager;

    // this.addMouseListener(this.getMouseListenerGeox());
    this.addMouseWheelListener(this.getMouseWheelListenerGeox());
    // this.addKeyListener(this.getKeyListenerGeox());
    this.addMouseMotionListener(this.getMouseMotionListenerGeox());

    this.setFocusable(true);
    this.setIgnoreRepaint(true);
  }

  /**
   * indique si on affiche les objet sous forme symbolisee ou non
   */
  // public boolean symbolisationDisplay = false;

  /**
   * indique si on affiche les objets initiaux sous forme symbolisee ou non
   */
  // public boolean initialSymbolisationDisplay = false;

  /**
   * indique si la legende est a afficher ou non sur la vue
   */
  public boolean scaleBarDisplay = false;

  /**
   * indique si la position du curseur doit etre affichee dans la barre du bas
   */
  public boolean CursorPositionDispaly = false;

  /**
   * indique si la fenetre se rafraichit automatiquement
   */
  public boolean automaticRefresh = true;

  // thread de rafraichissement automatique
  protected Thread automaticRefresher = null;
  protected long TIME = 100; // en ms

  public synchronized void activateAutomaticRefresh() {
    this.automaticRefresher = new Thread(new Runnable() {
      @Override
      public synchronized void run() {
        Thread cth = Thread.currentThread();
        while (VisuPanel.this.automaticRefresher == cth) {
          try {
            VisuPanel.this.imageUpdate();
          } catch (InterruptedException e1) {
          } catch (ConcurrentModificationException e2) {
            VisuPanel.this.repaint();
          }

          VisuPanel.this.repaint();
          try {
            Thread.sleep(VisuPanel.this.TIME);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        }
      }
    });
    this.automaticRefresher.start();
  }

  public synchronized void desactivateAutomaticRefresh() {
    this.automaticRefresher = null;
  }

  // thread d'affichage
  private Thread displayThread = null;
  private boolean displayStop = false;

  public synchronized void stopDisplayTest() throws InterruptedException {
    if (this.displayStop) {
      throw new InterruptedException();
    }
  }

  public synchronized void activate() {
    if (VisuPanel.logger.isDebugEnabled()) {
      VisuPanel.logger.debug("activer()");
    }

    // stoppe l'affichage precedent eventuel
    this.displayStop = true;

    // cree le nouveau thread
    this.displayThread = new Thread(this);

    // lancement du thread d'affichage
    this.displayThread.start();
  }

  @Override
  public synchronized void run() {
    if (VisuPanel.logger.isDebugEnabled()) {
      VisuPanel.logger.debug("run()");
    }
    try {
      this.displayStop = false;

      // maj limites
      this.displayLimitsUpdate();

      // maj cache
      this.getLayerManager().updateCache();

      // si le rafraicissement auto est deja active, sortir
      if (this.automaticRefresh) {
        return;
      }

      // maj image
      this.imageUpdate();

      // rafraichissement
      this.repaint();
    } catch (InterruptedException e) {
      if (VisuPanel.logger.isDebugEnabled()) {
        VisuPanel.logger.debug("interruption affichage");
      }
    }
  }

  /**
   * Move up.
   */
  public final void moveUp() {
    this.panVector(0, (int) Math.round(this.getPanFactor() * this.getHeight()));
  }

  /**
   * Move down.
   */
  public final void moveDown() {
    this.panVector(0, (int) Math.round(-this.getPanFactor() * this.getHeight()));
  }

  /**
   * Move right.
   */
  public final void moveRight() {
    this.panVector((int) Math.round(this.getPanFactor() * this.getWidth()), 0);
  }

  /**
   * Move left.
   */
  public final void moveLeft() {
    this.panVector((int) Math.round(-this.getPanFactor() * this.getWidth()), 0);
  }

  /**
   * Effectue un zoom d'un facteur donne
   * @param facteur
   */
  public void zoom(double facteur) {
    if (VisuPanel.logger.isDebugEnabled()) {
      VisuPanel.logger.debug("zoom");
    }

    /*
     * BufferedImage imageAux = (BufferedImage)createImage(getWidth(),
     * getHeight()); (imageAux.getGraphics()).drawImage(getImage(),
     * (int)(0.5*getWidth()*(1-1/facteur)),
     * (int)(0.5*getHeight()*(1-1/facteur)), (int)(getWidth()/facteur),
     * (int)(getHeight()/facteur), null); getG2D().drawImage(imageAux, 0, 0,
     * null); repaint();
     */

    // changement zoom
    this.pixelSize = this.pixelSize * facteur;

    // rafraichissement
    this.activate();
  }

  /**
   * Zoom with the default zoom factor.
   */
  public void zoomIn() {
    this.zoom(1 / this.zoomFactor);
  }

  /**
   * Zoom with the inverse zoomFactor as the factor
   */
  public void zoomOut() {
    this.zoom(this.zoomFactor);
  }

  /**
   * Deplace le centre de la vue au niveau d'une position geographique donnee.
   * @param xGeoCentre
   * @param yGeoCentre
   */
  public void pan(int xGeoCentre, int yGeoCentre) {
    if (VisuPanel.logger.isDebugEnabled()) {
      VisuPanel.logger.debug("deplacement");
    }

    /*
     * BufferedImage imageAux = (BufferedImage)createImage(getWidth(),
     * getHeight()); (imageAux.getGraphics()).drawImage(getImage(),
     * (int)(getWidth()*0.5-xCentre), (int)(getHeight()*0.5-yCentre), null);
     * getG2D().drawImage(imageAux, 0, 0, null); repaint();
     */

    // changement position centre
    this.geoCenter.setX(this.pixToCoordX(xGeoCentre));
    this.geoCenter.setY(this.pixToCoordY(yGeoCentre));

    // rafraichissement
    this.activate();

  }

  /**
   * Displace the wiew centre according to a vector.
   * @param xGeoCentre
   * @param yGeoCentre
   */
  public void panVector(int dx, int dy) {
    if (VisuPanel.logger.isDebugEnabled()) {
      VisuPanel.logger.debug("deplacement");
    }

    /*
     * BufferedImage imageAux = (BufferedImage)createImage(getWidth(),
     * getHeight()); (imageAux.getGraphics()).drawImage(getImage(),
     * (int)(getWidth()*0.5-xCentre), (int)(getHeight()*0.5-yCentre), null);
     * getG2D().drawImage(imageAux, 0, 0, null); repaint();
     */

    // changement position centre
    double dxCoord = dx * this.pixelSize;
    double dyCoord = dy * this.pixelSize;
    this.geoCenter.move(dxCoord, dyCoord);

    // rafraichissement
    this.activate();

  }

  public boolean hasToBeDisplayed(IFeature obj) {
    return obj.intersecte(this.displayEnvelope);
  }

  /**
   * met a jour le champ 'enveloppeAffichage' qui le rectangle de la fenetre de
   * viualisation en coordonnees geographiques, en fonction des coordonnees du
   * centre de la vue 'centreGeo' et du facteur de zoom 'taillePixel'
   * 'enveloppeAffichage' est utilise pour determiner les objets a tracer dans
   * la vue (ceux qui l'intersectent)
   * @throws InterruptedException
   */
  private synchronized void displayLimitsUpdate() {
    if (VisuPanel.logger.isDebugEnabled()) {
      VisuPanel.logger.debug("majLimitesAffichage()");
    }

    double XMin = this.geoCenter.getX() - this.getWidth() * this.pixelSize
        * 0.5 - this.marginPixelsNb * this.pixelSize;
    double XMax = this.geoCenter.getX() + this.getWidth() * this.pixelSize
        * 0.5 + this.marginPixelsNb * this.pixelSize;
    double YMin = this.geoCenter.getY() - this.getHeight() * this.pixelSize
        * 0.5 - this.marginPixelsNb * this.pixelSize;
    double YMax = this.geoCenter.getY() + this.getHeight() * this.pixelSize
        * 0.5 + this.marginPixelsNb * this.pixelSize;

    this.displayEnvelope = new GM_Envelope(XMin, XMax, YMin, YMax);
  }

  protected Set<PaintListener> overlayListeners = new HashSet<PaintListener>(0);

  public void addPaintListener(PaintListener listener) {
    this.overlayListeners.add(listener);
  }

  public void clearPaintListeners() {
    this.overlayListeners.clear();
  }

  public void paintOverlays(final Graphics graphics) {
    System.out.println("on passe");
    for (PaintListener listener : this.overlayListeners) {
      listener.paint(this, graphics);
    }
  }

  // image
  private BufferedImage image = null;
  private Graphics2D g2D = null;

  /**
   * indique si l'affichage utilise l'antialiasing
   */
  public boolean antiAliasing = true;

  protected synchronized BufferedImage getImage() {
    if (this.image == null) {
      this.image = (BufferedImage) this.createImage(this.getWidth(),
          this.getHeight());
      this.g2D = (Graphics2D) this.getImage().getGraphics();
      if (this.antiAliasing) {
        this.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
      } else {
        this.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_OFF);
      }
    } else if (this.getWidth() != this.image.getWidth()
        || this.getHeight() != this.image.getHeight()) {
      if (this.g2D != null) {
        this.g2D.dispose();
      }
      this.image = (BufferedImage) this.createImage(this.getWidth(),
          this.getHeight());
      this.g2D = (Graphics2D) this.getImage().getGraphics();
      if (this.antiAliasing) {
        this.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON);
      } else {
        this.g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_OFF);
      }
    }
    return this.image;
  }

  public synchronized Graphics2D getG2D() {
    if (this.g2D == null) {
      this.getImage();
    }
    return this.g2D;
  }

  @Override
  public synchronized void paint(Graphics g) {
    Graphics2D g2 = (Graphics2D) g;

    // l'image
    g2.drawImage(this.getImage(), 0, 0, null);
    try {
      this.stopDisplayTest();
    } catch (InterruptedException e) {
      return;
    }

    // la selection
    if (this.getFrame().getRightPanel().cDisplaySelection.isSelected()) {
      for (IFeature obj : this.selectedObjects) {
        try {
          this.stopDisplayTest();
        } catch (InterruptedException e) {
          return;
        }
        if (obj.isDeleted()) {
          continue;
        }
        if (!this.transparentSelection) {
          Composite defaultComp = g2.getComposite();
          g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
          this.draw(g2, this.SELECTION_COLOR, obj.getGeom());
          g2.setComposite(defaultComp);
        } else
          this.draw(g2, this.SELECTION_COLOR, obj.getGeom());
      }
    }

    // la barre d'echelle
    if (this.scaleBarDisplay) {
      try {
        this.displayScale(g2);
      } catch (InterruptedException e) {
        return;
      }
    }
  }

  public synchronized void imageUpdate() throws InterruptedException {

    // effacement
    this.getG2D().setColor(Color.WHITE);
    this.getG2D().fillRect(0, 0, this.getWidth(), this.getHeight());
    this.stopDisplayTest();

    // parcours des couches symbolisees pour les dessiner dans l'ordre
    for (SymbolisedLayer c : this.getLayerManager().getSymbolisedLayers()) {
      // dessin
      c.draw(this);
      // test d'interruption d'affichage
      this.stopDisplayTest();
    }

    // geometries pool
    this.getLayerManager().getGeometriesPoolSymbolisedLayer().draw(this);
    this.stopDisplayTest();
  }

  // les methodes de conversion entre coordonnees ecran (pixel) et coordonnees
  // geographiques
  public int coordToPixX(double x) {
    return (int) ((x - (this.geoCenter.getX() - this.getWidth() * 0.5
        * this.pixelSize)) / this.pixelSize);
  }

  public int coordToPixY(double y) {
    return (int) (this.getHeight() + (this.geoCenter.getY() - this.getHeight()
        * 0.5 * this.pixelSize - y)
        / this.pixelSize);
  }

  public double pixToCoordX(int x) {
    return this.geoCenter.getX() - this.getWidth() * 0.5 * this.pixelSize + x
        * this.pixelSize;
  }

  public double pixToCoordY(int y) {
    return this.geoCenter.getY() - this.getHeight() * 0.5 * this.pixelSize
        + (this.getHeight() - y) * this.pixelSize;
  }

  /**
   * Convert a screen point (in pixels) to a direct position.
   * @param point
   * @return
   */
  public IDirectPosition toModelDirectPosition(Point point) {
    return new DirectPosition(this.pixToCoordX(point.x),
        this.pixToCoordY(point.y));
  }

  /**
   * ajuste la vue sur l'objet (position et zoom)
   * @param obj
   */
  public void geographicPositionInitialisation(IFeature obj) {
    // recupere une enveloppe
    IGeometry geom = obj.getGeom().envelope().getGeom();

    // recupere les coordonnees extremes de l'enveloppe
    double xMin = geom.envelope().minX();
    double xMax = geom.envelope().maxX();
    double yMin = geom.envelope().minY();
    double yMax = geom.envelope().maxY();

    // assure que les coordonnees extreme ne sont pas confondues
    if (xMin == xMax) {
      xMin = xMin - 10;
      xMax = xMax + 10;
    }
    if (yMin == yMax) {
      yMin = yMin - 10;
      yMax = yMax + 10;
    }

    IDirectPosition pt = obj.getGeom().centroid();

    // ajuste position au centre
    this.geoCenter.setX(pt.getX());
    this.geoCenter.setY(pt.getY());

    // ajuste zoom
    this.pixelSize = 1.2 * Math.max((xMax - xMin) / this.getWidth(),
        (yMax - yMin) / this.getHeight());
  }

  /**
   * methode qui permet de centrer la vue sur un objet au pif contenu dans une
   * liste de featurecollections
   * @param fcl
   */
  public void geographicPositionInitialisation(ArrayList<IPopulation<?>> popl) {
    for (IPopulation<?> pop : popl) {
      if (pop == null || pop.size() == 0) {
        continue;
      }
      for (IFeature obj : pop) {
        if (obj.getGeom() == null || obj.getGeom().isEmpty()) {
          continue;
        }
        IDirectPosition dr = obj.getGeom().centroid();
        this.geoCenter.setX(dr.getX());
        this.geoCenter.setY(dr.getY());
        return;
      }
    }
  }

  /**
   * centre la vue sur un objet (sans modifier le niveau de zoom)
   * @param obj
   */
  public void center(IFeature obj) {
    this.center(obj.getGeom());
  }

  /**
   * centre la vue sur une geometrie (sans modifier le niveau de zoom)
   * @param obj
   */
  public void center(IGeometry geom) {
    if (geom == null || geom.isEmpty()) {
      return;
    }
    IDirectPosition dr = geom.envelope().center();
    this.pan(this.coordToPixX(dr.getX()), this.coordToPixY(dr.getY()));
  }

  /**
   * centre la vue sur un objet en adaptant le niveau de zoom
   * @param obj
   */
  public void centerAndZoom(IFeature obj) {
    this.centerAndZoom(obj.getGeom());
  }

  /**
   * centre la vue sur plusieurs objets en adaptant le niveau de zoom
   * @param col
   */
  public void centerAndZoom(IFeatureCollection<IFeature> col) {
    if (col == null || col.size() == 0) {
      return;
    }
    this.centerAndZoom(col.envelope());
  }

  /**
   * centre la vue sur une geometrie en adaptant le niveau de zoom
   * @param geom
   */
  public void centerAndZoom(IGeometry geom) {
    if (geom == null || geom.isEmpty()) {
      return;
    }
    this.centerAndZoom(geom.envelope());
  }

  /**
   * centre la vue sur une enveloppe en adaptant le niveau de zoom
   * @param env
   */
  public void centerAndZoom(IEnvelope env) {
    if (env == null || env.isEmpty()) {
      return;
    }

    // centre
    IDirectPosition dr = env.center();
    this.pan(this.coordToPixX(dr.getX()), this.coordToPixY(dr.getY()));

    // zoome
    this.setPixelSize(Math.max(env.width() / this.getWidth(), env.length()
        / this.getHeight()));
  }

  /**
   * center to a point of the panel.
   * @param env
   */
  public void moveTo(Point point) {
    this.pan(point.x, point.y);
  }

  /**
   * center to a point of the panel and zoom with the default zoom factor.
   * @param env
   */
  public void zoomInTo(Point point) {
    this.pan(point.x, point.y);
    this.zoomIn();
  }

  /**
   * center to a point of the panel and zoom out with the default zoom factor.
   * @param env
   */
  public void zoomOutTo(Point point) {
    this.pan(point.x, point.y);
    this.zoomOut();
  }

  /**
   * Zoom to see the full extent of data.
   */
  public void zoomToFullExtent() {
    this.centerAndZoom(this.getEnvelope());
  }

  /**
   * Get the envelope of all layers (not only the displayed window).
   * 
   * @return The envelope of all layers of the panel in model coordinates
   */
  public final IEnvelope getEnvelope() {
    IEnvelope envelope = null;
    for (IPopulation<? extends IFeature> pop : CartAGenDoc.getInstance()
        .getCurrentDataset().getPopulations()) {
      if (pop == null) {
        continue;
      }
      if (pop.size() == 0) {
        continue;
      }
      if (envelope == null) {
        envelope = pop.envelope();
      } else {
        envelope.expand(pop.envelope());
      }
    }
    return envelope;
  }

  /**
   * ajoute a la selection les objets d'une liste proches d'un point
   * @param liste
   * @param pointClic
   */
  public void addToSelection(IFeatureCollection<?> liste, IPoint pointClic) {
    // parcours des objets de la liste
    for (IFeature obj : liste) {
      if (obj.getGeom() == null || obj.getGeom().isEmpty()) {
        continue;
      }

      // si l'objet est supprime, sortir
      if (obj.isDeleted()) {
        continue;
      }

      // distance de la geometrie de l'objet au point du clic
      double d = obj.getGeom().distance(pointClic);

      if (d <= this.selectionDistance) {
        // ajout/suppression de l'objet a la selection
        if (this.selectedObjects.contains(obj)) {
          this.selectedObjects.remove(obj);
        } else {
          this.selectedObjects.add(obj);
        }
      }
    }
  }

  /**
   * ajoute a la selection les objets d'une liste dans une enveloppe.
   * @param liste
   * @param pointClic
   */
  public void addToSelection(IFeatureCollection<?> liste, IEnvelope env) {
    // parcours des objets de la liste
    for (IFeature obj : liste) {
      if (obj.getGeom() == null || obj.getGeom().isEmpty()) {
        continue;
      }

      // si l'objet est supprime, sortir
      if (obj.isDeleted()) {
        continue;
      }

      if (env.getGeom().intersects(obj.getGeom())) {
        // ajout/suppression de l'objet a la selection
        if (this.selectedObjects.contains(obj)) {
          this.selectedObjects.remove(obj);
        } else {
          this.selectedObjects.add(obj);
        }
      }
    }
  }

  public void displayScale(Graphics2D g) throws InterruptedException {
    this.stopDisplayTest();

    int nbDecalage = 10;
    int nbLargeurBarre = 5;
    double dist = this.pixToCoordX(this.getWidth() / 3) - this.pixToCoordX(0);
    int log = (int) Math.log10(dist);
    dist = Math.pow(10, log);
    int nbLongeurBarre = (int) (dist / this.pixelSize);
    this.stopDisplayTest();

    g.setColor(Color.BLACK);
    g.drawString(Double.toString(dist), nbDecalage + 1, this.getHeight()
        - nbDecalage - nbLargeurBarre - 1);
    g.fillRect(nbDecalage, this.getHeight() - nbDecalage - nbLargeurBarre,
        nbLongeurBarre, nbLargeurBarre);
    this.stopDisplayTest();

    int ech = (int) (this.pixelSize / VisuPanel.METERS_PER_PIXEL);
    g.drawString("1:" + Integer.toString(ech), 1, 10);
  }

  @Override
  public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
    if (pi >= 1) {
      return Printable.NO_SUCH_PAGE;
    }

    Graphics2D g2d = (Graphics2D) g;
    g2d.translate(pf.getImageableX(), pf.getImageableY());
    g2d.translate(pf.getImageableWidth() / 2, pf.getImageableHeight() / 2);
    Dimension d = this.getSize();

    double scale = Math.min(pf.getImageableWidth() / d.width,
        pf.getImageableHeight() / d.height);
    if (scale < 1.0) {
      g2d.scale(scale, scale);
    }

    g2d.translate(-d.width / 2.0, -d.height / 2.0);
    g2d.drawImage(this.getImage(), 0, 0, this);

    return Printable.PAGE_EXISTS;
  }

  @Override
  protected void paintComponent(Graphics g) {
    System.out.println("on passe dans paint");
    // super.paintComponent(g);
    // clear the graphics
    g.setColor(this.getBackground());
    g.fillRect(0, 0, this.getWidth(), this.getHeight());
    // paint overlays
    this.paintOverlays(g);
  }

  // methodes de dessin geometries Geoxygene

  // point

  // dessine un point avec symbole circulaire plein
  public synchronized void drawCircle(Color col, double x, double y, int largeur) {
    this.getG2D().setColor(col);
    this.getG2D().fillOval(this.coordToPixX(x) - largeur,
        this.coordToPixY(y) - largeur, 2 * largeur, 2 * largeur);
  }

  // dessine un point avec symbole circulaire plein
  public synchronized void drawCircle(Color col, double x, double y,
      double largeur) {
    this.getG2D().setColor(col);
    this.getG2D().fillOval(this.coordToPixX(x - largeur * 0.5),
        this.coordToPixY(y + largeur * 0.5),
        (int) Math.round(largeur / this.pixelSize),
        (int) Math.round(largeur / this.pixelSize));
  }

  // dessine un pixel
  public synchronized void drawRectangle(Color col, double x, double y,
      double largeur) {
    this.getG2D().setColor(col);
    this.getG2D().fillRect(this.coordToPixX(x - largeur / 2),
        this.coordToPixY(y + largeur / 2),
        (int) Math.round(largeur / this.pixelSize + 0.5),
        (int) Math.round(largeur / this.pixelSize + 0.5));
  }

  public synchronized void drawCircle(Color col, IPoint point, int largeur) {
    this.drawCircle(col, point.getPosition().getX(),
        point.getPosition().getY(), largeur);
  }

  // dessine un point avec symbole carre
  public synchronized void draw(Color col, IPoint point, int largeur) {
    this.getG2D().setColor(col);
    this.getG2D().fillRect(
        this.coordToPixX(point.getPosition().getX()) - largeur,
        this.coordToPixY(point.getPosition().getY()) - largeur, 2 * largeur,
        2 * largeur);
  }

  // dessine limite de point avec symbole circulaire
  public synchronized void drawLimitCircle(Color col, IPoint point, int largeur) {
    this.drawLimitCircle(this.getG2D(), col, point, largeur);
  }

  public synchronized void drawLimitCircle(Graphics2D g2, Color col,
      IPoint point, int largeur) {
    g2.setColor(col);
    g2.drawOval(this.coordToPixX(point.getPosition().getX()) - largeur,
        this.coordToPixY(point.getPosition().getY()) - largeur, 2 * largeur,
        2 * largeur);
  }

  // dessine limite de point avec symbole carre
  public synchronized void drawLimitSquare(Color col, IPoint point, int largeur) {
    this.getG2D().setColor(col);
    this.getG2D().drawRect(
        this.coordToPixX(point.getPosition().getX()) - largeur,
        this.coordToPixY(point.getPosition().getY()) - largeur, 2 * largeur,
        2 * largeur);
  }

  public synchronized void drawCircle(Color col, IPoint point, double largeur) {
    this.drawCircle(col, point.getPosition().getX(),
        point.getPosition().getY(), largeur);
  }

  // dessine un point avec symbole carre
  public synchronized void drawRectangle(Color col, IPoint point, double largeur) {
    this.getG2D().setColor(col);
    this.getG2D().fillRect(
        this.coordToPixX(point.getPosition().getX() - largeur / 2),
        this.coordToPixY(point.getPosition().getY() + largeur / 2),
        (int) Math.round(largeur / this.pixelSize + 0.5),
        (int) Math.round(largeur / this.pixelSize + 0.5));
  }

  // dessine limite de point avec symbole circulaire
  public synchronized void drawLimit(Color col, IPoint point, double largeur) {
    this.getG2D().setColor(col);
    this.getG2D().drawOval(
        this.coordToPixX(point.getPosition().getX() - largeur * 0.5),
        this.coordToPixY(point.getPosition().getY() + largeur * 0.5),
        (int) (largeur / this.pixelSize), (int) (largeur / this.pixelSize));
  }

  // dessine limite de point avec symbole carre
  public synchronized void drawLimitRectangle(Color col, IPoint point,
      double largeur) {
    this.getG2D().setColor(col);
    this.getG2D().drawRect(
        this.coordToPixX(point.getPosition().getX() - largeur * 0.5),
        this.coordToPixY(point.getPosition().getY() + largeur * 0.5),
        (int) (largeur / this.pixelSize), (int) (largeur / this.pixelSize));
  }

  // coordonnees

  // dessine coordonnees avec symbole circulaire
  public synchronized void draw(Color col, IDirectPosition coord, int taille) {
    this.drawCircle(col, coord.getX(), coord.getY(), taille);
  }

  // dessine coordonnees avec symbole carre
  public synchronized void draw(Color col, IDirectPosition coord, double largeur) {
    this.drawRectangle(col, coord.getX(), coord.getY(), largeur);
  }

  // multipoint
  public synchronized void drawCircle(Color col, IMultiPoint mp, double largeur) {
    for (int i = 0; i < mp.size(); i++) {
      this.drawCircle(col, mp.get(i), largeur);
    }
  }

  /**
   * Draws a plus-shaped cross of required colour on the point. The width of
   * each stroke of the cross is the width passed in parameter (in pixels). The
   * length of the storkes is 6 times the width
   * @param colour the colour to draw with
   * @param point the point to draw (ground coords)
   * @param widthPixels the wisth of the strokes of the cross, in Pixels
   */
  public synchronized void drawPlusCross(Color colour, IPoint point,
      int widthPixels) {
    double xPoint = point.getPosition().getX();
    double xMin = xPoint - 3 * widthPixels * this.getPixelSize();
    double xMax = xPoint + 3 * widthPixels * this.getPixelSize();
    double yPoint = point.getPosition().getY();
    double yMin = yPoint - 3 * widthPixels * this.getPixelSize();
    double yMax = yPoint + 3 * widthPixels * this.getPixelSize();
    this.drawSegment(colour, new BasicStroke(widthPixels, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_ROUND), xMin, yPoint, xMax, yPoint);
    this.drawSegment(colour, new BasicStroke(widthPixels, BasicStroke.CAP_BUTT,
        BasicStroke.JOIN_ROUND), xPoint, yMin, xPoint, yMax);
  }

  // ligne

  // segment
  public synchronized void drawSegment(Color col, BasicStroke bs, double x1,
      double y1, double x2, double y2) {
    this.getG2D().setColor(col);
    this.getG2D().setStroke(bs);
    this.getG2D().drawLine(this.coordToPixX(x1), this.coordToPixY(y1),
        this.coordToPixX(x2), this.coordToPixY(y2));

    /*
     * double lg=Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2)); int[] x=new int[]
     * {PanelVisu.get().coordToPixX(x1+0.5*largeur/lg*(y2-y1)),
     * PanelVisu.get().coordToPixX(x2+0.5*largeur/lg*(y2-y1)),
     * PanelVisu.get().coordToPixX(x2-0.5*largeur/lg*(y2-y1)),
     * PanelVisu.get().coordToPixX(x1-0.5*largeur/lg*(y2-y1))}; int[] y=new
     * int[] {PanelVisu.get().coordToPixY(y1+0.5*largeur/lg*(x1-x2)),
     * PanelVisu.get().coordToPixY(y2+0.5*largeur/lg*(x1-x2)),
     * PanelVisu.get().coordToPixY(y2-0.5*largeur/lg*(x1-x2)),
     * PanelVisu.get().coordToPixY(y1-0.5*largeur/lg*(x1-x2))};
     * PanelVisu.get().gr.fillPolygon(x,y,4);
     */
  }

  public synchronized void drawSegment(Color col, double x1, double y1,
      double x2, double y2) {
    this.drawSegment(col, x1, y1, x2, y2, 2);
  }

  public synchronized void drawSegment(Color col, double x1, double y1,
      double x2, double y2, int taille) {
    this.drawSegment(col, new BasicStroke(taille, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND), x1, y1, x2, y2);
  }

  public synchronized void drawSegment(Color col, IDirectPosition coord1,
      IDirectPosition coord2, int taille) {
    this.drawSegment(col, new BasicStroke(taille, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND), coord1.getX(), coord1.getY(), coord2.getX(),
        coord2.getY());
  }

  public synchronized void drawSegment(double x1, double y1, double x2,
      double y2, int taille) {
    this.drawSegment(Color.RED, x1, y1, x2, y2, taille);
  }

  public synchronized void drawSegment(IDirectPosition c1, IDirectPosition c2,
      int taille) {
    this.drawSegment(c1.getX(), c1.getY(), c2.getX(), c2.getY(), taille);
  }

  public synchronized void draw(Color col, ILineSegment line) {
    this.getG2D().setColor(col);
    this.getG2D().drawLine(this.coordToPixX(line.getControlPoint(0).getX()),
        this.coordToPixY(line.getControlPoint(0).getY()),
        this.coordToPixX(line.getControlPoint(1).getX()),
        this.coordToPixY(line.getControlPoint(1).getY()));
  }

  // LineString
  public synchronized void draw(Color col, ILineString line) {
    this.draw(this.getG2D(), col, line);
  }

  public synchronized void draw(Graphics2D g2, Color col, ILineString line,
      int largeur) {
    g2.setColor(col);
    BasicStroke bs = new BasicStroke(largeur, BasicStroke.CAP_ROUND,
        BasicStroke.JOIN_ROUND);
    g2.setStroke(bs);

    GeneralPath p = new GeneralPath();
    IDirectPositionList coords = line.coord();
    int x = this.coordToPixX(coords.get(0).getX());
    int y = this.coordToPixY(coords.get(0).getY());
    p.moveTo(x, y);
    for (int j = 1; j < coords.size(); j++) {
      x = this.coordToPixX(coords.get(j).getX());
      y = this.coordToPixY(coords.get(j).getY());
      p.lineTo(x, y);
    }
    g2.draw(p);
    g2.setStroke(new BasicStroke());
  }

  public synchronized void draw(Color col, ILineString ls, double largeurm,
      int cap, int join, float[] pointilles) {
    this.draw(col, ls, new BasicStroke((int) (largeurm / this.pixelSize), cap,
        join, 1, pointilles, 0));
  }

  /**
   * Draws a line with the specified colour, width in ground units, cap and join
   * types
   * @param col Colour the line should be drawn with
   * @param ls The line to draw
   * @param largeurm Width, in ground units
   * @param cap Type of end of the line: {@link BasicStroke#CAP_BUTT},
   *          {@link BasicStroke#CAP_ROUND} or {@link BasicStroke#CAP_SQUARE}
   * @param join Type of the junctions between the segments of the line:
   *          {@link BasicStroke#JOIN_BEVEL}, {@link BasicStroke#JOIN_MITER} or
   *          {@link BasicStroke#JOIN_ROUND}
   */
  public synchronized void draw(Color col, ILineString ls, double largeurm,
      int cap, int join) {
    this.draw(col, ls, new BasicStroke((int) (largeurm / this.pixelSize), cap,
        join));
  }

  /**
   * Draws a line with the specified colour, width in pixels, cap and join types
   * @param col Colour the line should be drawn with
   * @param ls the line to draw
   * @param largeur Width, in pixels
   * @param cap type of end of the line: {@link BasicStroke#CAP_BUTT},
   *          {@link BasicStroke#CAP_ROUND} or {@link BasicStroke#CAP_SQUARE}
   * @param join type of the junctions between the segments of the line:
   *          {@link BasicStroke#JOIN_BEVEL}, {@link BasicStroke#JOIN_MITER} or
   *          {@link BasicStroke#JOIN_ROUND}
   */
  public synchronized void draw(Color col, ILineString ls, int largeur,
      int cap, int join) {
    this.draw(col, ls, new BasicStroke(largeur, cap, join));
  }

  public synchronized void draw(Color col, ILineString ls, BasicStroke bs) {

    this.getG2D().setColor(col);
    this.getG2D().setStroke(bs);

    GeneralPath p = new GeneralPath();
    IDirectPositionList coords = ls.coord();
    int x = this.coordToPixX(coords.get(0).getX());
    int y = this.coordToPixY(coords.get(0).getY());
    p.moveTo(x, y);
    for (int j = 1; j < coords.size(); j++) {
      x = this.coordToPixX(coords.get(j).getX());
      y = this.coordToPixY(coords.get(j).getY());
      p.lineTo(x, y);
    }
    // gr.draw(bs.createStrokedShape(p));
    this.getG2D().draw(p);

    this.getG2D().setStroke(new BasicStroke());
  }

  // Ring
  public synchronized void draw(Color col, IRing ring) {
    this.draw(col, new GM_LineString(ring.coord()));
  }

  public synchronized void draw(Graphics2D g2, Color col, IRing ring,
      int largeur) {
    this.draw(g2, col, new GM_LineString(ring.coord()), largeur);
  }

  public synchronized void draw(Color col, IRing ring, double largeurmm,
      int cap, int join, float[] pointilles) {
    this.draw(col, new GM_LineString(ring.coord()), largeurmm, cap, join,
        pointilles);
  }

  public synchronized void draw(Color col, IRing ring, double largeur, int cap,
      int join) {
    this.draw(col, new GM_LineString(ring.coord()), largeur, cap, join);
  }

  public synchronized void draw(Color col, IRing ring, int largeur, int cap,
      int join) {
    this.draw(col, ring, new BasicStroke(largeur, cap, join));
  }

  public synchronized void draw(Color col, IRing ring, BasicStroke bs) {
    this.draw(col, new GM_LineString(ring.coord()), bs);
  }

  // Arc
  public synchronized void draw(Color col, IArc arc) {
    this.draw(this.getG2D(), col, arc);
  }

  public synchronized void draw(Graphics2D g2, Color col, IArc arc) {
    g2.setColor(col);
    IDirectPosition center = arc.getCenter();
    double radius = arc.getRadius();
    double theta = arc.startOfArc() * 180 / Math.PI;
    double delta = arc.delta() * 180 / Math.PI;
    g2.draw(new Arc2D.Double(this.coordToPixX(center.getX() - radius), this
        .coordToPixY(center.getY() + radius),
        (int) (2 * radius / this.pixelSize),
        (int) (2 * radius / this.pixelSize), (int) theta, (int) delta,
        Arc2D.OPEN));
  }

  public synchronized void draw(Color col, IArc arc, double largeurmm, int cap,
      int join, float[] pointilles) {
    this.draw(col, arc, new BasicStroke((int) (largeurmm / this.pixelSize),
        cap, join, 1, pointilles, 0));
  }

  public synchronized void draw(Color col, IArc arc, double largeurm, int cap,
      int join) {
    this.draw(col, arc, new BasicStroke((int) (largeurm / this.pixelSize), cap,
        join));
  }

  public synchronized void draw(Color col, IArc arc, int largeur, int cap,
      int join) {
    this.draw(col, arc, new BasicStroke(largeur, cap, join));
  }

  public synchronized void draw(Color col, IArc arc, BasicStroke bs) {
    this.getG2D().setStroke(bs);
    this.draw(this.getG2D(), col, arc);
  }

  // Arc2
  public synchronized void draw(Color col, IArc2 arc) {
    this.draw(this.getG2D(), col, arc);
  }

  public synchronized void draw(Graphics2D g2, Color col, IArc2 arc) {
    g2.setColor(col);
    IDirectPosition center = arc.getCenter();
    double radius = arc.getRadius();
    double theta = arc.getStartOfArc() * 180 / Math.PI;
    double delta = arc.getDelta() * 180 / Math.PI;
    // System.out.println("delta="+delta);
    g2.draw(new Arc2D.Double(this.coordToPixX(center.getX() - radius), this
        .coordToPixY(center.getY() + radius),
        (int) (2 * radius / this.pixelSize),
        (int) (2 * radius / this.pixelSize), (int) theta, (int) delta,
        Arc2D.OPEN));
  }

  public synchronized void draw(Color col, IArc2 arc, double largeurmm,
      int cap, int join, float[] pointilles) {
    this.draw(col, arc, new BasicStroke((int) (largeurmm / this.pixelSize),
        cap, join, 1, pointilles, 0));
  }

  public synchronized void draw(Color col, IArc2 arc, double largeurm, int cap,
      int join) {
    this.draw(col, arc, new BasicStroke((int) (largeurm / this.pixelSize), cap,
        join));
  }

  public synchronized void draw(Color col, IArc2 arc, int largeur, int cap,
      int join) {
    this.draw(col, arc, new BasicStroke(largeur, cap, join));
  }

  public synchronized void draw(Color col, IArc2 arc, BasicStroke bs) {
    this.getG2D().setStroke(bs);
    this.draw(this.getG2D(), col, arc);
  }

  // MultiCurve
  public synchronized void draw(Color col, IMultiCurve<ILineString> mls) {
    this.draw(this.getG2D(), col, mls);
  }

  public synchronized void draw(Graphics2D g2, Color col,
      IMultiCurve<ILineString> mls) {
    for (int i = 0; i < mls.size(); i++) {
      this.draw(g2, col, mls.get(i));
    }
  }

  public synchronized void draw(Color col, IMultiCurve<ILineString> mls,
      BasicStroke bs) {
    for (int i = 0; i < mls.size(); i++) {
      this.draw(col, mls.get(i), bs);
    }
  }

  /**
   * Draws a multiline with the specified colour, width in ground units, cap and
   * join types
   * @param col Colour the line should be drawn with
   * @param mls The line to draw
   * @param largeurm Width, in ground units
   * @param cap Type of end of the line: {@link BasicStroke#CAP_BUTT},
   *          {@link BasicStroke#CAP_ROUND} or {@link BasicStroke#CAP_SQUARE}
   * @param join Type of the junctions between the segments of the line:
   *          {@link BasicStroke#JOIN_BEVEL}, {@link BasicStroke#JOIN_MITER} or
   *          {@link BasicStroke#JOIN_ROUND}
   */
  public synchronized void draw(Color col, IMultiCurve<ILineString> mls,
      double largeur, int cap, int join) {
    for (int i = 0; i < mls.size(); i++) {
      this.draw(col, mls.get(i), largeur, cap, join);
    }
  }

  /**
   * Draws a multiline with the specified colour, width in pixels, cap and join
   * types
   * @param col Colour the line should be drawn with
   * @param mls The line to draw
   * @param largeurp Width, in pixels
   * @param cap Type of end of the line: {@link BasicStroke#CAP_BUTT},
   *          {@link BasicStroke#CAP_ROUND} or {@link BasicStroke#CAP_SQUARE}
   * @param join Type of the junctions between the segments of the line:
   *          {@link BasicStroke#JOIN_BEVEL}, {@link BasicStroke#JOIN_MITER} or
   *          {@link BasicStroke#JOIN_ROUND}
   */
  public synchronized void draw(Color col, IMultiCurve<ILineString> mls,
      int largeurp, int cap, int join) {
    for (int i = 0; i < mls.size(); i++) {
      this.draw(col, mls.get(i), largeurp, cap, join);
    }
  }

  // surface

  // GM_Polygon
  public synchronized void draw(Color col, IPolygon poly) {
    this.draw(this.getG2D(), col, poly);
  }

  public synchronized void draw(Graphics2D g2, Color col, IPolygon poly) {
    int nb = poly.coord().size() + poly.getInterior().size();
    int[] x = new int[nb], y = new int[nb];
    // enveloppe exterieure
    IRing ls = poly.getExterior();
    int x0 = this.coordToPixX(ls.coord().get(0).getX());
    int y0 = this.coordToPixY(ls.coord().get(0).getY());
    for (int i = 0; i < ls.coord().size(); i++) {
      x[i] = this.coordToPixX(ls.coord().get(i).getX());
      y[i] = this.coordToPixY(ls.coord().get(i).getY());
    }
    // trous
    int index = ls.coord().size();
    for (int j = 0; j < poly.getInterior().size(); j++) {
      ls = poly.getInterior(j);
      for (int i = index; i < index + ls.coord().size(); i++) {
        x[i] = this.coordToPixX(ls.coord().get(i - index).getX());
        y[i] = this.coordToPixY(ls.coord().get(i - index).getY());
      }// i
      x[index + ls.coord().size()] = x0;
      y[index + ls.coord().size()] = y0;
      index += ls.coord().size() + 1;
    }// j
    g2.setColor(col);
    g2.fillPolygon(x, y, nb);
  }

  public synchronized void drawLimit(Color col, IPolygon poly) {
    this.draw(col, poly.getExterior());
    for (int i = 0; i < poly.getInterior().size(); i++) {
      this.draw(col, poly.getInterior(i));
    }
  }

  public synchronized void drawLimit(Color col, IPolygon poly, double largeur,
      int cap, int join) {
    this.draw(col, poly.getExterior(), largeur, cap, join);
    for (int i = 0; i < poly.getInterior().size(); i++) {
      this.draw(col, poly.getInterior(i), largeur, cap, join);
    }
  }

  public synchronized void drawLimit(Color col, IPolygon poly, BasicStroke bs) {
    this.draw(col, poly.getExterior(), bs);
    for (int i = 0; i < poly.getInterior().size(); i++) {
      this.draw(col, poly.getInterior(i), bs);
    }
  }

  public synchronized void drawLimit(Color col, IPolygon poly,
      double largeurmm, int cap, int join, float[] pointilles) {
    this.draw(col, poly.getExterior(), largeurmm, cap, join, pointilles);
    for (int i = 0; i < poly.getInterior().size(); i++) {
      this.draw(col, poly.getInterior(i), largeurmm, cap, join, pointilles);
    }
  }

  // MultiPolygon

  public synchronized void draw(Graphics2D g2, Color col,
      IMultiSurface<?> multipoly) {
    for (int i = 0; i < multipoly.size(); i++) {
      this.draw(g2, col, (IPolygon) multipoly.get(i));
    }
  }

  public synchronized void drawLimit(Color col, IMultiSurface<?> multipoly) {
    for (int i = 0; i < multipoly.size(); i++) {
      this.drawLimit(col, (IPolygon) multipoly.get(i));
    }
  }

  public synchronized void drawLimit(Color col, IMultiSurface<?> multipoly,
      double largeur, int cap, int join) {
    for (int i = 0; i < multipoly.size(); i++) {
      this.drawLimit(col, (IPolygon) multipoly.get(i), largeur, cap, join);
    }
  }

  public synchronized void drawLimit(Color col, IMultiSurface<?> multipoly,
      BasicStroke bs) {
    for (int i = 0; i < multipoly.size(); i++) {
      this.drawLimit(col, (IPolygon) multipoly.get(i), bs);
    }
  }

  // Geometry
  public synchronized void draw(Color col, IGeometry geom) {
    this.draw(this.getG2D(), col, geom);
  }

  @SuppressWarnings("unchecked")
  public synchronized void draw(Graphics2D g2, Color col, IGeometry geom) {
    if (geom instanceof IPolygon) {
      this.draw(g2, col, (IPolygon) geom);
    } else if (geom instanceof IMultiSurface) {
      this.draw(g2, col, (IMultiSurface<?>) geom);
    } else if (geom instanceof ILineString) {
      this.draw(g2, col, (ILineString) geom, 2);
    } else if (geom instanceof IArc) {
      this.draw(g2, col, (IArc) geom);
    } else if (geom instanceof IArc2) {
      this.draw(g2, col, (IArc2) geom);
    } else if (geom instanceof IMultiCurve<?>) {
      this.draw(g2, col, (IMultiCurve<ILineString>) geom);
    } else if (geom instanceof IPoint) {
      this.drawLimitCircle(g2, col, (IPoint) geom, 4);
    } else {
      VisuPanel.logger.warn("impossible d'afficher " + geom
          + ": type de geometrie non gere.");
    }
  }

  public synchronized void drawLimit(Color col, IGeometry geom) {
    if (geom instanceof IPolygon) {
      this.drawLimit(col, (IPolygon) geom);
    } else if (geom instanceof IMultiSurface<?>) {
      this.drawLimit(col, (IMultiSurface<?>) geom);
    } else {
      VisuPanel.logger.warn("impossible d'afficher " + geom
          + ": type de geometrie non gere.");
    }
  }

  // textes
  public synchronized void drawText(Color col, double x, double y, String texte) {
    this.getG2D().setColor(col);
    this.getG2D().drawString(texte, this.coordToPixX(x), this.coordToPixY(y));
  }

  public synchronized void drawText(Color col, double x, double y, double val) {
    this.drawText(col, x, y, Double.toString(val));
  }

  public synchronized void drawText(Color col, IDirectPosition coord,
      String texte) {
    this.drawText(col, coord.getX(), coord.getY(), texte);
  }

  public synchronized void drawText(Color col, Font font,
      IDirectPosition coord, String texte) {
    this.getG2D().setFont(font);
    this.drawText(col, coord.getX(), coord.getY(), texte);
  }

  public synchronized void drawText(Color col, IGeometry geom, String texte) {
    this.drawText(col, geom.centroid(), texte);
  }

  public synchronized void drawText(Color col, Font font, IGeometry geom,
      String texte) {
    this.drawText(col, font, geom.centroid(), texte);
  }

  /**
   * Builds a Shape awt object from IGemetry.
   * @param geometry a geometry
   * @return A shape representing the geometry in view coordinates
   */
  public final Shape toShape(final IGeometry geometry) {
    if (geometry == null) {
      return null;
    }
    IEnvelope envelope = this.getDisplayEnvelope();
    try {
      IEnvelope geometryEnvelope = geometry.getEnvelope();
      // if the geometry does not intersect the envelope of
      // the view, return a null shape
      if (!envelope.intersects(geometryEnvelope)) {
        return null;
      }
      if (geometry.isEmpty()) {
        return null;
      }
      if (geometry.isPolygon()) {
        return this.toShape((IPolygon) geometry);
      }
      if (geometry.isMultiSurface()) {
        return null;
      }
      if (geometry.isLineString()) {
        return this.toShape((ILineString) geometry);
      }
      if (ICurve.class.isAssignableFrom(geometry.getClass())) {
        // Curve other than linestring
        return null;
      }
      if (geometry instanceof IRing) {
        return this.toShape(new GM_Polygon((IRing) geometry));
      }
      if (geometry.isMultiCurve()) {
        return null;
      }
      if (geometry.isPoint()) {
        return this.toShape((IPoint) geometry);
      }
      if (geometry instanceof IAggregate<?>) {
        return null;
      }
      throw new IllegalArgumentException();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  /**
   * Transform a linestring to an awt general path.
   * @param lineString a linestring
   * @return a GeneralPath representing the given linestring as an AWT shape
   */
  private GeneralPath toShape(final ILineString lineString) {
    return this.toShape(lineString.coord());
  }

  /**
   * Transform a DirectPosition list to an awt general path.
   * @param list a DirectPosition list
   * @return a GeneralPath representing the given linestring as an AWT shape
   */
  public GeneralPath toShape(IDirectPositionList list) {
    IDirectPositionList viewPositionList = this.toViewDirectPositionList(list);
    GeneralPath shape = new GeneralPath();
    IDirectPosition p = viewPositionList.get(0);
    shape.moveTo(p.getX(), p.getY());
    for (int i = 1; i < viewPositionList.size(); i++) {
      p = viewPositionList.get(i);
      shape.lineTo(p.getX(), p.getY());
    }
    return shape;
  }

  /**
   * Transform a polygon to an awt shape.
   * @param p a polygon
   * @return A shape representing the polygon in view coordinates
   * @see #toViewDirectPositionList(IPolygon p)
   */
  private Shape toShape(final IPolygon p) {
    return this.toPolygonShape(this.toViewDirectPositionList(p.coord()));
  }

  /**
   * Transform a direct position list in view coordinates to an awt shape.
   * @param viewDirectPositionList a direct position list in view coordinates
   * @return A shape representing the polygon in view coordinates
   */
  private Shape toPolygonShape(final IDirectPositionList viewDirectPositionList) {
    int numPoints = viewDirectPositionList.size();
    int[] xpoints = new int[numPoints];
    int[] ypoints = new int[numPoints];
    for (int i = 0; i < viewDirectPositionList.size(); i++) {
      IDirectPosition p = viewDirectPositionList.get(i);
      xpoints[i] = (int) p.getX();
      ypoints[i] = (int) p.getY();
    }
    return new Polygon(xpoints, ypoints, numPoints);
  }

  /**
   * Transform a direct position list in model coordinates to view coordinates.
   * @param modelDirectPositionList a direct position list in model coordinates
   * @return a DirectPositionList of DirectPosition in the screen coordinate
   *         system corresponding to the given DirectPositionList in model
   *         coordinate system
   */
  public final IDirectPositionList toViewDirectPositionList(
      final IDirectPositionList modelDirectPositionList) {
    IDirectPositionList viewDirectPositionList = new DirectPositionList();
    if (modelDirectPositionList.isEmpty()) {
      return viewDirectPositionList;
    }
    for (IDirectPosition pt : modelDirectPositionList) {
      viewDirectPositionList.add(new DirectPosition(
          this.coordToPixX(pt.getX()), this.coordToPixY(pt.getY())));
    }

    return viewDirectPositionList;
  }

  /**
   * Transform a point to an awt general path.
   * @param point a point
   * @return a GeneralPath representing the given point as an AWT shape
   */
  private GeneralPath toShape(final IPoint point) {
    Point2D p = new Point2D.Double(
        this.coordToPixX(point.getPosition().getX()), this.coordToPixY(point
            .getPosition().getY()));
    GeneralPath shape = new GeneralPath();
    shape.moveTo(p.getX(), p.getY());
    return shape;
  }

}

/**
 * @author julien gaffuri
 * 
 */
class MouseListenerGeox implements MouseListener {
  private final static Logger logger = Logger.getLogger(MouseListenerGeox.class
      .getName());

  @Override
  public void mousePressed(MouseEvent e) {
    if (e.getButton() == 0) {
      return;
    }
    if (e.getButton() == MouseEvent.BUTTON1) {
      VisuPanel pv = (VisuPanel) e.getSource();

      // selection d'objets

      // position du clic
      double x = pv.pixToCoordX(e.getX()), y = pv.pixToCoordY(e.getY());
      GM_Point p = new GM_Point(new DirectPosition(x, y));

      try {
        // ajout des objets des couches selectionnables a la selection
        for (Layer c : pv.getLayerManager().getLayers()) {
          if (c == null) {
            continue;
          }
          if (c.isSelectable()) {
            pv.addToSelection(c.getDisplayCache(pv), p);
          }
        }
      } catch (InterruptedException e1) {
      }

      pv.getFrame().getRightPanel().lNbSelection.setText("Nb="
          + pv.selectedObjects.size());

      if (!pv.automaticRefresh) {
        pv.repaint();
      }
    } else if (e.getButton() == MouseEvent.BUTTON2) {
    } else if (e.getButton() == MouseEvent.BUTTON3) {
      // selection de point
      // position du curseur
      /*
       * double x=pixToCoordX(e.getX()), y=pixToCoordY(e.getY()); double
       * bestdistCPoint = Double.MAX_VALUE; double
       * distanceSelectionC=distanceSelection*distanceSelection; double distC;
       * 
       * //on cherche le point le plus proche du clic et a moins de
       * DISTANCE_SELECTION pixels du clic for(AgentPoint p:AgentPoint.LISTE) {
       * distC = (p.getX()-x)*(p.getX()-x) + (p.getY()-y)*(p.getY()-y); if
       * ((distC<bestdistCPoint) && (distC<=distanceSelectionC)) {
       * pointSelectionne=p; bestdistCPoint=distC; } }
       * 
       * //si on n'a pas trouve de point (ils sont tous trop loin du clic), on
       * sort if (pointSelectionne==null) return;
       * 
       * //si le point est plus proche
       * 
       * //on marque le point comme selectionne
       * pointSelectionne.setSelectionne(true);
       * 
       * //on met le point a la position du curseur pointSelectionne.setX(x);
       * pointSelectionne.setY(y);
       * 
       * if (! rafraichissementAutomatique) repaint();
       */
    } else {
      MouseListenerGeox.logger
          .error("clic de souris avec bouton inconnu (mousePressed): "
              + e.getButton());
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    if (e.getButton() == 0) {
      return;
    }
    if (e.getButton() == MouseEvent.BUTTON1) {
    } else if (e.getButton() == MouseEvent.BUTTON2) {
    } else if (e.getButton() == MouseEvent.BUTTON3) {
      /*
       * if (pointSelectionne!=null) {
       * pointSelectionne.setX(pixToCoordX(e.getX()));
       * pointSelectionne.setY(pixToCoordY(e.getY()));
       * pointSelectionne.setSelectionne(false); pointSelectionne=null; if (!
       * rafraichissementAutomatique) { try {majImage();} catch
       * (InterruptedException e1) {} repaint(); } }
       */
    } else {
      MouseListenerGeox.logger
          .error("clic de souris avec bouton inconnu (mouseReleased): "
              + e.getButton());
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
    VisuPanel pv = (VisuPanel) e.getSource();

    pv.requestFocus(true);

    if (e.getButton() == 0) {
      return;
    }
    if (e.getButton() == MouseEvent.BUTTON1) {
    } else if (e.getButton() == MouseEvent.BUTTON2) {
      pv.pan(e.getX(), e.getY());
    } else if (e.getButton() == MouseEvent.BUTTON3) {
    } else {
      MouseListenerGeox.logger
          .error("clic de souris avec bouton inconnu (mouseClicked): "
              + e.getButton());
    }
  }

  @Override
  public void mouseEntered(MouseEvent e) {
  }

  @Override
  public void mouseExited(MouseEvent e) {
    this.mouseReleased(e);
  }

}

/**
 * @author julien gaffuri
 * 
 */
class MouseWheelListenerGeox implements MouseWheelListener {

  @Override
  public void mouseWheelMoved(MouseWheelEvent e) {
    VisuPanel pv = (VisuPanel) e.getSource();
    if (e.getWheelRotation() > 0) {
      pv.zoom(pv.getZoomFactor());
    } else {
      pv.zoom(1 / pv.getZoomFactor());
    }
  }
}

/**
 * @author julien gaffuri
 * 
 */
class MouseMotionListenerGeox implements MouseMotionListener {

  @Override
  public void mouseMoved(MouseEvent e) {
    VisuPanel pv = (VisuPanel) e.getSource();
    if (pv.CursorPositionDispaly) {
      double x = pv.pixToCoordX(e.getX()), y = pv.pixToCoordY(e.getY());
      pv.getFrame().getBottomPanel().lX.setText("X="
          + Double.toString(Math.round((float) x * 100) * 0.01));
      pv.getFrame().getBottomPanel().lY.setText("Y="
          + Double.toString(Math.round((float) y * 100) * 0.01));
    }
  }

  @Override
  public void mouseDragged(MouseEvent e) {
  }
}

/**
 * @author julien gaffuri
 * 
 */
class KeyListenerGeox implements KeyListener {
  private final static Logger logger = Logger.getLogger(KeyListenerGeox.class
      .getName());

  @Override
  public void keyPressed(KeyEvent e) {
    if (KeyListenerGeox.logger.isInfoEnabled()) {
      KeyListenerGeox.logger.info("appui sur touche du clavier "
          + e.getKeyCode());
      // System.out.println(e.getKeyCode());
    }

    VisuPanel pv = (VisuPanel) e.getSource();

    switch (e.getKeyCode()) {
      case KeyEvent.VK_SPACE: // espace: rafraichissement
        if (!pv.automaticRefresh) {
          try {
            pv.imageUpdate();
          } catch (InterruptedException e1) {
            return;
          }
          pv.repaint();
        }
        break;
      case KeyEvent.VK_ESCAPE: // echap: vide la selection
        pv.selectedObjects.clear();
        if (!pv.automaticRefresh) {
          try {
            pv.imageUpdate();
          } catch (InterruptedException e1) {
            return;
          }
          pv.repaint();
        }
        break;
      case 33: // zoomin
        pv.zoom(1 / pv.getZoomFactor());
        break;
      case 34: // zoomout
        pv.zoom(pv.getZoomFactor());
        break;
      case 37: // gauche
        pv.pan((int) (pv.getWidth() * (0.5 - pv.getPanFactor())),
            (int) (pv.getHeight() * 0.5));
        break;
      case 38: // haut
        pv.pan((int) (pv.getWidth() * 0.5),
            (int) (pv.getHeight() * (0.5 - pv.getPanFactor())));
        break;
      case 39: // droite
        pv.pan((int) (pv.getWidth() * (0.5 + pv.getPanFactor())),
            (int) (pv.getHeight() * 0.5));
        break;
      case 40: // bas
        pv.pan((int) (pv.getWidth() * 0.5),
            (int) (pv.getHeight() * (0.5 + pv.getPanFactor())));
        break;
      default:
    }
  }

  @Override
  public void keyReleased(KeyEvent e) {
  }

  @Override
  public void keyTyped(KeyEvent e) {
  }

}

package fr.ign.cogit.geoxygene.sig3d.gui.navigation3D;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.KeyEventPostProcessor;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.media.j3d.Behavior;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.SceneGraphPath;
import javax.media.j3d.WakeupCriterion;
import javax.media.j3d.WakeupOnAWTEvent;
import javax.swing.JComponent;
import javax.vecmath.Point3d;

import org.apache.log4j.Logger;

import com.sun.j3d.utils.picking.PickCanvas;
import com.sun.j3d.utils.picking.PickResult;
import com.sun.j3d.utils.picking.PickTool;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.sig3d.gui.InterfaceMap3D;
import fr.ign.cogit.geoxygene.sig3d.gui.MainWindow;
import fr.ign.cogit.geoxygene.sig3d.gui.table.featurestable.FeaturesListTable;
import fr.ign.cogit.geoxygene.sig3d.gui.table.featurestable.FeaturesListTableModel;
import fr.ign.cogit.geoxygene.sig3d.representation.I3DRepresentation;
import fr.ign.cogit.geoxygene.sig3d.representation.basic.Object0d;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * 
 * This software is released under the licence CeCILL
 * 
 * see LICENSE.TXT
 * 
 * see <http://www.cecill.info/ http://www.cecill.info/
 * 
 * 
 * 
 * @copyright IGN
 * 
 * @author Brasebin Mickaël
 * @author benoitpoupeau
 * 
 * @version 0.1
 * 
 *          Classe permettant la sélection d'un objet dans un espace 3D et son
 *          affichage dans l'espace local Elle gère àgalement la création de
 *          points lorsque l'on clique sur un objet (clic droit)
 * 
 */
public class Picking extends Behavior {

	private final static Logger logger = Logger.getLogger(Picking.class.getName());

	/**
	 * Nom de la couche dans laquelle seront ajouté les points issus du clic
	 * droit
	 */
	public static final String NOM_COUCHE_POINTS = "PointsCliques";

	// Variables lié au picking (zone à "picker" et le résultat du "picking")
	private PickCanvas pickCanvas;

	// Canvas rafraîchit permettant le redimensionnement de l'objet
	private Canvas3D canvas3D;

	// variable pour récupèrer les coordonnées àcran
	int x, y;

	// Boolean permettant d'afficher ou non les stats de l'objet
	public static boolean info = false;

	// Critère de réveil
	private WakeupOnAWTEvent wakeupCriterion;

	private static boolean PICKING_IS_CTRL_PRESSED = false;

	/**
	 * Crée un comportement de picking
	 * 
	 * @param canvas
	 *            le canvas dans lequel est créé le picking
	 * @param scene
	 *            le BranchGroup contenant les éléments à sélectionner
	 */
	public Picking(Canvas3D canvas, BranchGroup scene) {

		this.canvas3D = canvas;

		this.pickCanvas = new PickCanvas(this.canvas3D, scene);
		// Tolérance à partir de laquelle les objets en fonction de la distance
		// avec la souris
		// sera sélectionnée
		this.pickCanvas.setTolerance(5f);
		// Pour avoir des infos à partir des frontières de l'objet intersecté
		this.pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);

		// Le bouton controle est il enfoncé ?
		KeyboardFocusManager kbm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
		kbm.addKeyEventPostProcessor(new KeyEventPostProcessor() {
			@Override
			public boolean postProcessKeyEvent(KeyEvent e) {
				if (e.getModifiers() == InputEvent.CTRL_MASK) {
					if (e.getID() == KeyEvent.KEY_PRESSED) {
						Picking.PICKING_IS_CTRL_PRESSED = true;
					}
				}
				if (e.getID() == KeyEvent.KEY_RELEASED) {
					Picking.PICKING_IS_CTRL_PRESSED = false;
				}
				return false;
			}
		});

	}

	@Override
	public void initialize() {
		// Réveil si un des boutton de la souris est pressé
		this.wakeupCriterion = new WakeupOnAWTEvent(MouseEvent.MOUSE_CLICKED);
		this.wakeupOn(this.wakeupCriterion);
	}

	@Override
	public void processStimulus(Enumeration criteria) {
		while (criteria.hasMoreElements()) {
			WakeupCriterion wakeup = (WakeupCriterion) criteria.nextElement();
			if (wakeup instanceof WakeupOnAWTEvent) {
				// Les évènements souris (ici que les clicks)
				AWTEvent[] events = ((WakeupOnAWTEvent) wakeup).getAWTEvent();

				// donc un seul élément
				for (AWTEvent e : events) {
					try {
						if (e instanceof MouseEvent) {
							MouseEvent evt = (MouseEvent) e;

							// Si cet évènement est bien un évènement souris
							if (evt.getID() == MouseEvent.MOUSE_CLICKED) {

								// On procède à la sélection
								this.pickCanvas.setShapeLocation(evt);

								// Objet récupèré
								PickResult pickResult = this.pickCanvas.pickClosest();

								InterfaceMap3D carte = (InterfaceMap3D) this.canvas3D.getParent();

								// Cas du clic droit ajoute un objet ponctuel
								// dans la couche
								// NOM_COUCHE_POINTS
								if (evt.getButton() == MouseEvent.BUTTON3) {

									if (pickResult == null) {

										continue;
									}

									Point3d p = pickResult.getIntersection(0).getPointCoordinates();// pickResult.getIntersection(0).getClosestVertexCoordinates();

									// On récupère le point
									GM_Point gmPoints = new GM_Point(new DirectPosition(p.x, p.y, p.z));

									VectorLayer c = (VectorLayer) carte.getCurrent3DMap()
											.getLayer(Picking.NOM_COUCHE_POINTS);

									if (c == null) {// Si la couche
										// NOM_COUCHE_POINTS
										// n'existe pas on l'ajoute

										FT_FeatureCollection<IFeature> ftColl = new FT_FeatureCollection<IFeature>();

										IFeature feat = new DefaultFeature(gmPoints);
										feat.setRepresentation(new Object0d(feat, true, Color.RED, 0.5, true));

										ftColl.add(feat);

										c = new VectorLayer(ftColl, Picking.NOM_COUCHE_POINTS);

										carte.getCurrent3DMap().addLayer(c);

									} else {
										// Sinon on la modifie
										VectorLayer coucheVecteur = (VectorLayer) carte.getCurrent3DMap()
												.getLayer(Picking.NOM_COUCHE_POINTS);

										coucheVecteur.get(0).setGeom(gmPoints);
										coucheVecteur.updateStyle(true, Color.red, 0.5, true);
									}

								} else {

									// récupèration de la fenetre qui contient
									// l'objet sélectionné

									MainWindow fenetre = carte.getMainWindow();

									JComponent jcomp = fenetre.getActionPanel().getActionComponent();
									if (jcomp instanceof FeaturesListTable) {
										((FeaturesListTable) jcomp).getSelectionModel().clearSelection();

									}
									// Qui je récupère?
									if (pickResult == null) {

										Picking.logger.debug("aucun objet sélectionné");

										if (!PICKING_IS_CTRL_PRESSED) {
											fenetre.getInterfaceMap3D()
													.setSelection(new FT_FeatureCollection<IFeature>());
										}

									} else {

										// récupèrer le BranchGroup du
										// BranchGroup
										// de la Shape3D
										// A l'aide d'un scenegraphpath
										SceneGraphPath graph = new SceneGraphPath();
										graph = pickResult.getSceneGraphPath();

										// C'est le noeud du graphe de scene
										// contenant l'objet
										Object userData = graph.getNode(graph.nodeCount() - 1).getUserData();

										IFeature feat = null;
										I3DRepresentation objRep = null;

										if (userData == null) {

											if (!PICKING_IS_CTRL_PRESSED) {
												fenetre.getInterfaceMap3D()
														.setSelection(new FT_FeatureCollection<IFeature>());
											}
											// On ne trouve rien on vide la
											// sélection
											continue;
										}

										// La sélection est de type
										// I3DRepresentation
										// Il doit y avoir un feature attaché
										if (userData instanceof I3DRepresentation) {

											objRep = (I3DRepresentation) userData;
											feat = objRep.getFeature();

											// Si il y a une table dans le
											// panneau droit, on met à jour la
											// sélection
											if (jcomp instanceof FeaturesListTable) {

												FeaturesListTableModel model = (FeaturesListTableModel) ((FeaturesListTable) jcomp)
														.getModel();

												int ind = model.getIndex(feat);

												if (ind != -1) {

													((FeaturesListTable) jcomp).getSelectionModel()
															.setSelectionInterval(ind, ind);
												}

											}
											// On indique l'objet comme
											// sélectionné

											if (PICKING_IS_CTRL_PRESSED) {
												fenetre.getInterfaceMap3D().addToSelection(feat);
											} else {
												fenetre.getInterfaceMap3D().setSelection(feat);

											}

										}

										if ((feat == null) || (objRep == null)) {

											continue;
										}

										// On affiche l'ObjectBrowser si la
										// variable d'info est vraie.
										if (Picking.info) {

											// ObjectBrowser.browse(feat);

										}
									}

								}
							}
						}
					} catch (Exception ex) {

						ex.printStackTrace();
					}
				} // Boucle i
			}
		}
		this.wakeupOn(this.wakeupCriterion);
	}

}

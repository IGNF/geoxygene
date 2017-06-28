package fr.ign.cogit.geoxygene.sig3d.calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;

import org.apache.log4j.Logger;
import org.twak.camp.Corner;
import org.twak.camp.Edge;
import org.twak.camp.Machine;
import org.twak.camp.Output;
import org.twak.camp.Output.Face;
import org.twak.camp.Output.SharedEdge;
import org.twak.camp.Skeleton;
import org.twak.utils.collections.Loop;
import org.twak.utils.collections.LoopL;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IPolygon;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IRing;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Arc;
import fr.ign.cogit.geoxygene.contrib.cartetopo.CarteTopo;
import fr.ign.cogit.geoxygene.contrib.cartetopo.Noeud;
import fr.ign.cogit.geoxygene.sig3d.equation.ApproximatedPlanEquation;
import fr.ign.cogit.geoxygene.sig3d.equation.PlanEquation;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Ring;

/**
 * Squelette droit pondéré calculé d'après la librairie campskeleton.
 * Possibilité de pondérer ce squelette. Le résultat est présenté sous la forme
 * d'une carte topo. Les faces les arcs et les noeuds sont renseignés ainsi que
 * la position des arcs par rapport aux différentes faces. Possibilité d'obtenir
 * seulement les arcs intérieurs
 * 
 * @author MBrasebin
 * 
 */
public class CampSkeleton {

	private static Logger logger = Logger.getLogger(CampSkeleton.class);

	private IPolygon p;

	/**
	 * Calcul du squelette droit, le résultat est obtenu par le getCarteTopo()
	 * Le même poids est appliqué à tous les arcs
	 * 
	 * @param p
	 */
	public CampSkeleton(IPolygon p) {

		this(p, null, 0);
	}

	/**
	 * Straight skelton calculation with cap parameters that defines a
	 * perpendicular distance from the block contours
	 * 
	 * @param p
	 * @param cap
	 */
	public CampSkeleton(IPolygon p, double cap) {
		this(p, null, cap);
	}

	public CampSkeleton(IPolygon p, double[] angles) {
		this(p, angles, 0);
	}

	/**
	 * Calcul du squelette droit, le résultat est obtenu par le getCarteTopo()
	 * Une pondération est appliquée
	 * 
	 * @param p
	 * @param angles
	 *            : la pondération appliquée pour le calcul de squelette droit.
	 *            Le nombre d'élément du tableaux doit être au moins égal au
	 *            nombre de côté (intérieurs inclus du polygone)
	 */
	public CampSkeleton(IPolygon p, double[] angles, double cap) {

		this.p = p;

		int countAngle = 0;

		IDirectPositionList dpl = p.coord();

		for (IDirectPosition dp : dpl) {
			dp.setZ(0);
		}

		PlanEquation pe = new ApproximatedPlanEquation(p);

		if (pe.getNormale().getZ() < 0) {

			p = (IPolygon) p.reverse();

		}

		Machine directionMachine = new Machine();

		LoopL<Edge> input = new LoopL<Edge>();

		IRing rExt = p.getExterior();

		Loop<Edge> loop = new Loop<Edge>();

		List<Edge> lEExt = fromDPLToEdges(rExt.coord());

		for (Edge e : lEExt) {

			loop.append(e);
		}

		for (Edge e : lEExt) {
			if (angles == null) {
				e.machine = directionMachine;
			} else {
				e.machine = new Machine(angles[countAngle++]);
			}

		}

		input.add(loop);

		for (IRing rInt : p.getInterior()) {

			Loop<Edge> loopIn = new Loop<Edge>();
			input.add(loopIn);

			List<Edge> lInt = fromDPLToEdges(rInt.coord());

			for (Edge e : lInt) {
				loop.append(e);
			}

			for (Edge e : lInt) {
				if (angles == null) {
					e.machine = directionMachine;
				} else {
					e.machine = new Machine(angles[countAngle++]);
				}
			}
		}

		Skeleton s = new Skeleton(input, cap);

		s.skeleton();
		Output out = s.output;
		this.ct = convertOutPut(out);

	}

	/**
	 * Convertit la sortie de l'algorithme de squelette droit
	 * 
	 * @TODO : il subsite un problème, parfois, 2 arrêtes de 2 faces sont
	 *       équivalentes à 1 arrête d'une autre face.
	 * @param out
	 * @return
	 */
	private static CarteTopo convertOutPut(Output out) {
		// On créer la carte Toppo
		CarteTopo cT = new CarteTopo("squelette");

		// On récupère les faces
		Map<Corner, Face> faces = out.faces;

		List<Face> collFaces = new ArrayList<>();
		collFaces.addAll(faces.values());

		/*
		 * bouclei: for(int i=0;i < collFaces.size(); i++){ for(int j=i+1;j <
		 * collFaces.size(); j++){
		 * if(collFaces.get(i).equals(collFaces.get(j))){ collFaces.remove(i);
		 * i--; logger.warn("Duplicate faces found : auto-remove applied : " +
		 * CampSkeleton.class); continue bouclei; }
		 * 
		 * } }
		 */

		// Liste des arrêtes rencontrées
		List<SharedEdge> lSharedEdges = new ArrayList<SharedEdge>();
		List<Arc> lArcs = new ArrayList<Arc>();

		// Liste des noeuds rencontres

		List<Noeud> lNoeuds = new ArrayList<Noeud>();
		List<Point3d> lPoints = new ArrayList<Point3d>();

		// Pour chaque face du squelette
		for (Face f : collFaces) {

			// On créer une face de la carte topo
			fr.ign.cogit.geoxygene.contrib.cartetopo.Face fTopo = new fr.ign.cogit.geoxygene.contrib.cartetopo.Face();

			// On génère la géométrie de la face
			LoopL<Point3d> loopLPoint = f.points;

			// On récupère la géométrie du polygone
			IPolygon poly = new GM_Polygon();

			for (Loop<Point3d> lP : loopLPoint) {

				IDirectPositionList dpl = convertLoopCorner(lP);

				// Il ne ferme pas ses faces
				dpl.add(dpl.get(0));

				if (poly.getExterior() == null) {

					poly.setExterior(new GM_Ring(new GM_LineString(dpl)));

				} else {
					poly.addInterior(new GM_Ring(new GM_LineString(dpl)));
				}

			}

			// On affecte la géomégtrie
			fTopo.setGeometrie(poly);

			// On récupère les arrête de la face

			LoopL<SharedEdge> lSE = f.edges;

			// On parcourt les arrêtes
			int nbSE = lSE.size();

			for (int i = 0; i < nbSE; i++) {

				for (Loop<SharedEdge> loopSE : lSE) {

					for (SharedEdge se : loopSE) {

						// Est ce une arrête déjà rencontrée ?
						int indexArc = lSharedEdges.indexOf(se);

						if (indexArc == -1) {
							// Non : on doit générer les informations add hoc

							// On récupère les sommets initiaux et finaux
							Point3d p = getStart(se, f);
							Point3d p2 = getEnd(se, f);

							if (p == null || p2 == null) {
								continue;
							}
							// On la rajoute à la liste des arrêtes existants
							lSharedEdges.add(se);

							// S'agit il de sommets déjà rencontrés ?
							int indexP1 = lPoints.indexOf(p);
							int indexP2 = lPoints.indexOf(p2);

							// Non ! on génère la sommet
							if (indexP1 == -1) {
								// On l'ajoute à la liste des sommets rencontrés
								lPoints.add(p);
								// On met à jour le sommet considéré
								indexP1 = lPoints.size() - 1;
								// On génère un noeud
								Noeud n = new Noeud(fromCornerToPosition(p));
								// On l'ajoute à la liste des noeuds et à la
								// carte topo
								lNoeuds.add(n);
								cT.addNoeud(n);

							}

							// idem avec le second sommet
							if (indexP2 == -1) {

								lPoints.add(p2);
								indexP2 = lPoints.size() - 1;
								Noeud n = new Noeud(fromCornerToPosition(p2));
								lNoeuds.add(n);
								cT.addNoeud(n);

							}

							// On génère l'arc

							Arc a = new Arc();
							a.setNoeudIni(lNoeuds.get(indexP1));
							a.setNoeudFin(lNoeuds.get(indexP2));

							// On génère sa géométrie
							IDirectPositionList dpl = new DirectPositionList();
							dpl.add(lNoeuds.get(indexP1).getCoord());
							dpl.add(lNoeuds.get(indexP2).getCoord());

							a.setGeometrie(new GM_LineString(dpl));

							// On ajoute l'arc
							lArcs.add(a);
							cT.addArc(a);
							indexArc = lSharedEdges.size() - 1;
						}
						// On affecte le côté d'où se trouve la face
						// Normalement la carte topo met ça à jour du côté de la
						// face
						Arc a = lArcs.get(indexArc);

						boolean isOnRight = (f.equals(se.right));
						boolean isOnLeft = (f.equals(se.left));

						if (isOnRight) {
							a.setFaceDroite(fTopo);
						} else if (isOnLeft) {

							a.setFaceGauche(fTopo);
						} else {
							logger.warn("QUICK FIX APLIED : face is neither at the right or the left of a polygon");

							if (se.right == null) {
								a.setFaceDroite(fTopo);
							} else if (se.left == null) {
								a.setFaceGauche(fTopo);
							} else {
								logger.error("Null both side");
							}

						}

					}
				}

			}
			// On ajoute la faces à la carte topo
			cT.addFace(fTopo);

		}

		cT.fusionNoeuds(0.2);

		cT.rendPlanaire(0.5);

		return cT;

	}

	public static Point3d getStart(SharedEdge se, Face ref) {

		return se.start;
	}

	public static Point3d getEnd(SharedEdge se, Face ref) {

		return se.end;
	}
	/*
	 * Conversion Geoxygene => format de la lib
	 */

	/**
	 * Convertit une liste de sommets formant un cycle en arrêtes
	 * 
	 * @param dpl
	 * @return
	 */
	public static List<Edge> fromDPLToEdges(IDirectPositionList dpl) {

		int nbPoints = dpl.size();
		List<Edge> lEOut = new ArrayList<Edge>();
		List<Corner> lC = new ArrayList<Corner>();

		for (int i = 0; i < nbPoints - 1; i++) {

			lC.add(fromPositionToCorner(dpl.get(i)));

		}

		// lC.add(lC.get(0));

		for (int i = 0; i < nbPoints - 2; i++) {

			lEOut.add(new Edge(lC.get(i), lC.get(i + 1)));

		}

		lEOut.add(new Edge(lC.get(nbPoints - 2), lC.get(0)));

		return lEOut;
	}

	/**
	 * Convertit un positon en corner
	 * 
	 * @param dp
	 * @return
	 */
	private static Corner fromPositionToCorner(IDirectPosition dp) {

		if (dp.getDimension() == 2) {
			return new Corner(dp.getX(), dp.getY(), 0);
		}

		return new Corner(dp.getX(), dp.getY(), dp.getZ());
	}

	/*
	 * Conversion format de la lib => Geoxygene
	 */

	/**
	 * 
	 */
	private static IDirectPositionList convertLoopCorner(Loop<Point3d> lC) {

		IDirectPositionList dpl = new DirectPositionList();

		for (Point3d c : lC) {
			dpl.add(fromCornerToPosition(c));
		}

		return dpl;
	}

	private static IDirectPosition fromCornerToPosition(Point3d c) {

		return new DirectPosition(c.x, c.y, c.z);

	}

	private CarteTopo ct = null;

	/**
	 * 
	 * @return perment d'obtenir la carte topo générée
	 */
	public CarteTopo getCarteTopo() {
		return ct;
	}

	/**
	 * 
	 * @return extrait les arcs extérieurs du polygone
	 */
	public List<Arc> getExteriorArcs() {

		List<Arc> lArcsOut = new ArrayList<Arc>();

		for (Arc a : ct.getPopArcs()) {
			if (a.getFaceDroite() == null && a.getFaceGauche() != null) {

				lArcsOut.add(a);
				continue;
			}

			if (a.getFaceDroite() != null && a.getFaceGauche() == null) {

				lArcsOut.add(a);
				continue;
			}

		}

		return lArcsOut;

	}

	/**
	 * 
	 * @return extrait les arcs générés lors du calcul du squelette droit
	 */
	public List<Arc> getInteriorArcs() {

		List<Arc> lArcsOut = new ArrayList<Arc>();

		for (Arc a : ct.getPopArcs()) {

			if (a.getFaceDroite() != null && a.getFaceGauche() != null) {

				lArcsOut.add(a);
			}

		}

		return lArcsOut;

	}

	/**
	 * 
	 * @return extrait les arcs générés ne touchant pas la frontière du polygone
	 */
	public List<Arc> getIncludedArcs() {

		List<Arc> lArcsOut = new ArrayList<Arc>();

		IGeometry geom = p.buffer(-0.5);
		for (Arc a : ct.getPopArcs()) {

			if (geom.contains(a.getGeometrie())) {

				lArcsOut.add(a);

			}

		}

		//

		return lArcsOut;

	}

}

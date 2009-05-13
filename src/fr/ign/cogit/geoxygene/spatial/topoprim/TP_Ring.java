/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.spatial.topoprim;

import java.util.ArrayList;
import java.util.List;


/**
 * Représente des TP_DirectedEdge connectés en un cycle.
 * L'anneau doit être orienté pour que la face soit à sa gauche.
 *
 * @author Thierry Badard & Arnaud Braun
 * @version 1.0
 * 
 */

public class TP_Ring extends TP_Expression {


	/** Constructeur par défaut. */
	public TP_Ring() {
		term = new ArrayList<TP_DirectedTopo>();
	}


	/** Constructeur à partir de plusieurs TP_DirectedEdge. La liste doit contenir au moins 1 element.
	 * Le constructeur réorganise la liste pour que les brins orientés soient chaînés. Il renvoie une exception si ça ne boucle pas. */
	public TP_Ring(List<TP_DirectedEdge> sdt) throws Exception {
		term = new ArrayList<TP_DirectedTopo>();
		int compteur; // nombre d'arc en contact avec l'arc courant

		TP_DirectedEdge dt0 = sdt.get(0);
		TP_DirectedEdge dtref = null;

		// probleme si on commence par un arc pendant, dans ce cas on en prend un autre
		if (!dt0.topo().getLeftface().equals(dt0.topo().getRightface())) {
			term.add(dt0);
			sdt.remove(0);
		} else {
			if (sdt.size() > 1) {
				for (int i=1; i<sdt.size(); i++) {
					dt0 = sdt.get(i);
					if (!dt0.topo().getLeftface().equals(dt0.topo().getRightface())) {
						term.add(dt0);
						sdt.remove(i);
						break;
					}
				}
			}
		}

		int IDEndNode = dt0.endNode().topo().getId();
		int theIDStartNode = dt0.startNode().topo().getId();

		while (sdt.size() > 0) {
			compteur = 0;
			// on cherche le nombre d'arc en contact avec l'arc courant
			for (int j=0; j<sdt.size(); j++) {
				TP_DirectedEdge dt = sdt.get(j);
				int IDStartNode = dt.startNode().topo().getId();
				if (IDEndNode == IDStartNode) {
					compteur++;
					dtref = dt;
				}
			}

			// probleme ! ca ne chaine pas
			if (compteur == 0) {
				throw new Exception("Les brins ne sont pas chainés.");
			}

			// pas de probleme !
			else if (compteur == 1) {
				term.add(dtref);
				if (dtref!=null) {
					IDEndNode = dtref.endNode().topo().getId();
					dt0=dtref;
					sdt.remove(dtref);
				}

				// if compteur > 1 : presence d'arc pendant (caracterise par left face = right face)
			} else {
				// c'est ceux la qu'on met en premier dans la liste, pour revenir au point initial
				for (int j=0; j<sdt.size(); j++) {
					TP_DirectedEdge dt = sdt.get(j);
					int IDStartNode = dt.startNode().topo().getId();
					TP_Edge dttopo = dt.topo();
					if ((IDEndNode == IDStartNode) &&
							(dttopo.getLeftface().equals(dttopo.getRightface()))) {
						boolean flag = true;
						if (dt.topo().equals(dt0.topo()))  { // cas particulier : il faut verifier qu'il n'y a pas d'autre candidat
							for (int k=j+1; k<sdt.size(); k++) {
								TP_DirectedEdge dt1 = sdt.get(k);
								IDStartNode = dt1.startNode().topo().getId();
								TP_Edge dt1topo = dt1.topo();
								if ((IDEndNode == IDStartNode) &&
										(dt1topo.getLeftface().equals(dt1topo.getRightface()))) {
									term.add(dt1);
									IDEndNode = dt1.endNode().topo().getId();
									dt0 = dt1;
									sdt.remove(dt1);
									flag = false;
									break;
								}
							}
						}
						if (flag) {    // pas d'autre candidat trouve : on rebrousse chemin
							term.add(dt);
							IDEndNode = dt.endNode().topo().getId();
							dt0 = dt;
							sdt.remove(dt);
						}
						break;
					}
				}
			}
		}

		// ultime verification du bouclage
		if (theIDStartNode != IDEndNode)
			throw new Exception("Les brins ne sont pas chainés.");

	}

}

/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * See: http://oxygene-project.sourceforge.net
 * Copyright (C) 2005 Institut Géographique National
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.matching.dst.evidence;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.matching.dst.evidence.codec.EvidenceCodec;
import fr.ign.cogit.geoxygene.matching.dst.operators.CombinationAlgos;
import fr.ign.cogit.geoxygene.matching.dst.operators.DempsterOp;
import fr.ign.cogit.geoxygene.matching.dst.operators.SmetsOp;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;
import fr.ign.cogit.geoxygene.matching.dst.util.Utils;

/**
 * Matching process.
 * <p>
 * Processus d'appariement de données géographiques utilisant la théorie des fonctions de croyance
 * (voir Dempster67, Smets89). Si on fait l'hypothèse d'un monde clos ( = les hypothèses sont
 * exclusives ET exhaustives), la combinaison des croyances est effectuée à l'aide de l'opérateur
 * conjonctif de Dempster. Dans ce cas le conflit entre sources d'information est réparti entre les
 * différentes masses de croyances et sa valeur est stockée dans le conflict. Dans le cas d'un monde
 * ouvert on utilisera l'opérateur de Smets qui répartira le conflit entièrement sur l'hypothèse
 * vide.
 * <p>
 * @TODO : Ajouter la règle de Yager afin de permettre la répartition du conflit sur l'ensemble
 *       total (ignorance complète) plutôt que sur toutes les hypothèses.
 * @author Bertrand Dumenieu
 */
public class MatchingProcess<F, Hyp extends Hypothesis> {

  /**
   * 
   */
  private final static Logger LOGGER = Logger.getLogger(MatchingProcess.class);

  private Collection<Source<F, Hyp>> criteria;
  
  // Cadre de discernement : stocke les candidats
  private List<Hyp> frame;
  private List<List<Pair<byte[], Float>>> beliefs;
  private EvidenceCodec<Hyp> codec;
  private boolean isworldclosed = true;

  /**
   * @param criteria
   * @param candidates
   * @param codec
   * @param isworldclosed
   */
  public MatchingProcess(Collection<Source<F, Hyp>> criteria, List<Hyp> candidates,
      EvidenceCodec<Hyp> codec, boolean isworldclosed) {
    this.codec = codec;
    this.frame = Collections.unmodifiableList(candidates);
    this.criteria = criteria;
    this.beliefs = new ArrayList<List<Pair<byte[], Float>>>();
    this.isworldclosed = isworldclosed;
  }

  public void closedworld(boolean b) {
    this.isworldclosed = b;
  }

  /**
   * Does the actual fusion of the information and returns the final mass potentials.
   * <p>
   * Effectue l'ensemble des opérations de fusion d'information et renvoie les potentiels de masse
   * finaux.
   * <p>
   * @TODO : Placer le processus dans un thread séparé?
   * @throws Exception
   */
  public List<Pair<byte[], Float>> combinationProcess(F reference) throws Exception {
    
	  LOGGER.info("RUNNING MATCHING PROCESS UNDER " + (this.closedworld() ? "CLOSED" : "OPEN")
        + " WORLD ASSUMPTION...");
	  double start = System.currentTimeMillis();
	  this.beliefs.clear();
	  
	  // ---------------- EVALUATION ------------------------
	  // On récupère les éléments focaux de la masse de croyance et on ordonne la
	  // liste afin de permettre la combinaison de critères.
	  for (Source<F, Hyp> src : this.criteria) {
		  
	    List<Pair<byte[], Float>> kernel = src.evaluate(reference, this.frame, this.codec);

		// On s'assure que sum(m(j)) = 1 sinon erreur.
		/*float sum = 0;
		for (Pair<byte[], Float> pair : kernel) {
		  // System.out.print(pair.getSecond() + " + ");
		  sum += pair.getSecond();
		}
		// System.out.println (" , somme = " + sum);
		if ((1 - sum > 0.001) || (1 - sum) < -0.001) {
		  LOGGER.error("mass potential != 1(" + sum
                + "), the process can not continue. Please check if belief functions ensure that sum(m(A))=1");
		  throw new Exception();
		}
		LOGGER.debug("Fin de la vérification somme des masses = 1"); */

	    // On vérifie qu'il n'y a pas plusieurs fois une hypothèse A, sinon on les fusionne
	    CombinationAlgos.deleteDoubles(kernel);
	    // Finalement on trie la liste des ensemble focaux
	    CombinationAlgos.sortKernel(kernel);
      
	    LOGGER.debug("----HYPOTHESIS FOR FUNCTION " + src.getName() + "----");
	    for (Pair<byte[], Float> focal : kernel) {
	      LOGGER.debug(focal.getSecond() + " " + Arrays.toString(focal.getFirst()));
	    }
	    this.beliefs.add(kernel);
	  
	  }
	  
	  // ----------------FUSION------------------------
	  // Phase de combinaison des masses de croyance.
	  // Si on se trouve dans l'hypothèse d'un monde clos, alors on utilise
	  // l'opérateur de Dempster, sinon on utilise la règle de Smets.
	  List<Pair<byte[], Float>> massresult = null;
	  if (this.closedworld()) {
	    massresult = new DempsterOp(this.isworldclosed).combine(this.beliefs);
	  } else {
		massresult = new SmetsOp(this.isworldclosed).combine(this.beliefs);
	  }
	  if (LOGGER.isDebugEnabled()) {
		  if (!massresult.isEmpty() && Utils.isEmpty(massresult.get(0).getFirst())) {
			  if (this.closedworld()) {
				  LOGGER
              .debug("Warning : Non null empty hypothesis under closed world hypothesis! value : "
                  + massresult.get(0).getSecond());
			  } else {
        	    LOGGER.debug("Empty hypothesis value : " + massresult.get(0).getSecond());
			  }
		  }
	  }
      if (massresult == null || massresult.isEmpty()) {
    	LOGGER.error("Error : The combination result is null or empty");
      }
      double elapsed = System.currentTimeMillis() - start;
      LOGGER.info("The combination process took " + elapsed / 1000 + " seconds");
      return massresult;
  }

  /**
   * @return true if the world is closed, false if it is open.
   */
  private boolean closedworld() {
    return this.isworldclosed;
  }

}

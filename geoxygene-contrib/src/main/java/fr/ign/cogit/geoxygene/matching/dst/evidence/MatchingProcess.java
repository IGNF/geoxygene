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

import fr.ign.cogit.geoxygene.matching.dst.operators.CombinationAlgos;
import fr.ign.cogit.geoxygene.matching.dst.operators.DempsterOp;
import fr.ign.cogit.geoxygene.matching.dst.operators.SmetsOp;
import fr.ign.cogit.geoxygene.matching.dst.util.Pair;
import fr.ign.cogit.geoxygene.matching.dst.util.Utils;

/**
 * Matching process.
 * <p> 
 * Processus d'appariement de données géographiques utilisant la théorie des
 * fonctions de croyance (voir Dempster67, Smets89). Si on fait l'hypothèse d'un
 * monde clos ( = les hypothèses sont exclusives ET exhaustives), la
 * combinaison des croyances est effectuée à l'aide de l'opérateur conjonctif de
 * Dempster. Dans ce cas le conflit entre sources d'information est réparti
 * entre les différentes masses de croyances et sa valeur est stockée dans le
 * conflict. Dans le cas d'un monde ouvert on utilisera l'opérateur de Smets qui
 * répartira le conflit entièrement sur l'hypothèse vide.
 * <p>
 * @TODO : Ajouter la règle de Yager afin de permettre la répartition du conflit sur l'ensemble
 *       total (ignorance complète) plutôt que sur toutes les hypothèses.
 * @author Bertrand Dumenieu
 */
public class MatchingProcess {

  /**
   * 
   */
  Logger logger = Logger.getLogger(MatchingProcess.class);

  Collection<Source<Hypothesis>> criteria;
  // Cadre de discernement : stocke les candidats
  private List<Hypothesis> frame;

  // Map<Source<Hypothesis>, List<Pair<byte[], Float>>> beliefs;
  List<List<Pair<byte[], Float>>> beliefs;
  EvidenceCodec codec;
  private boolean isworldclosed = true;

  /**
   * @param criteria
   * @param candidates
   * @param codec
   * @param isworldclosed
   */
  public MatchingProcess(Collection<Source<Hypothesis>> criteria, List<Hypothesis> candidates,
      EvidenceCodec codec, boolean isworldclosed) {
    this.logger.debug(candidates.size() + " candidates");
    this.codec = codec;
    this.frame = Collections.unmodifiableList(candidates);
    this.criteria = criteria;
    this.beliefs = new ArrayList<List<Pair<byte[], Float>>>();
    this.isworldclosed = isworldclosed;
  }

  /**
   * 
   */
  public MatchingProcess() {
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
  public List<Pair<byte[], Float>> combinationProcess() throws Exception {
    logger.info("RUNNING MATCHING PROCESS UNDER " + (this.closedworld() ? "CLOSED" : "OPEN")
        + " WORLD ASSUMPTION...");
    double start = System.currentTimeMillis();
    // ----------------EVALUATION------------------------
    // On récupère les éléments focaux de la masse de croyance et on ordonne la
    // liste afin de permettre la combinaison de critères.
    for (Source<Hypothesis> src : this.criteria) {
      List<Pair<byte[], Float>> kernel = src.evaluate(frame, this.codec);
      // On s'assure que sum(m(j)) = 1 sinon erreur.
      float sum = 0;
      for (Pair<byte[], Float> pair : kernel) {
        sum += pair.getSecond();
      }
      if (1 - sum > 0.001) {
        logger
            .error("mass potential != 1("
                + sum
                + "), the process can not continue. Please check if belief functions ensure that sum(m(A))=1");
        throw new Exception();
      }

      // On vérifie qu'il n'y a pas plusieurs fois une hypothèse A, sinon on les
      // fusionne
      CombinationAlgos.deleteDoubles(kernel);
      // Finalement on trie la liste des ensemble focaux
      CombinationAlgos.sortKernel(kernel);
      logger.debug("----HYPOTHESIS FOR FUNCTION " + src.getName() + "----");
      for (Pair<byte[], Float> focal : kernel) {
        logger.debug(focal.getSecond() + " " + Arrays.toString(focal.getFirst()));
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
      massresult = new SmetsOp(isworldclosed).combine(this.beliefs);
    }
    if (logger.isDebugEnabled()) {
      if (!massresult.isEmpty() && Utils.isEmpty(massresult.get(0).getFirst())) {
        if (this.closedworld()) {
          logger
              .debug("Warning : Non null empty hypothesis under closed world hypothesis! value : "
                  + massresult.get(0).getSecond());
        } else {
          logger.debug("Empty hypothesis value : " + massresult.get(0).getSecond());
        }
      }
    }
    if (massresult == null || massresult.isEmpty()) {
      logger.error("Error : The combination result is null or empty");
    }
    double elapsed = System.currentTimeMillis() - start;
    logger.info("The combination process took " + elapsed / 1000 + " seconds");
    return massresult;
  }

  /**
   * @return true if the world is closed, false if it is open.
   */
  private boolean closedworld() {
    return this.isworldclosed;
  }

}

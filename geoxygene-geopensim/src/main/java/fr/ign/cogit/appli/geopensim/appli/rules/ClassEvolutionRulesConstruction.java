/*******************************************************************************
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
 *******************************************************************************/

package fr.ign.cogit.appli.geopensim.appli.rules;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVParser;
import org.apache.log4j.Logger;

import fr.ign.cogit.appli.geopensim.evolution.EvolutionRule;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleBase;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleUnitaire;
import fr.ign.cogit.appli.geopensim.evolution.EvolutionRuleUnitaire.Possibilites;
import fr.ign.cogit.appli.geopensim.feature.meso.ClasseUrbaine;
import fr.ign.cogit.geoxygene.filter.And;
import fr.ign.cogit.geoxygene.filter.Filter;
import fr.ign.cogit.geoxygene.filter.PropertyIsEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsGreaterThanOrEqualTo;
import fr.ign.cogit.geoxygene.filter.PropertyIsLessThan;
import fr.ign.cogit.geoxygene.filter.expression.Literal;
import fr.ign.cogit.geoxygene.filter.expression.PropertyName;

/**
 * @author Julien Perret
 *
 */
public class ClassEvolutionRulesConstruction {
  private static Logger logger=Logger.getLogger(ClassEvolutionRulesConstruction.class.getName());

  public static EvolutionRule create(String input, int start, int end) throws Exception {
    EvolutionRule evolutionRule = new EvolutionRule();
    evolutionRule.setType("ZoneElementaireUrbaine");
    String property = "classificationFonctionnelle";
    evolutionRule.setPropertyName(property + "But");
    evolutionRule.setNom(property + "-distance-2.5-5-" + start + "-" + end + "");
    evolutionRule.setStart(start);
    evolutionRule.setEnd(end);

    PropertyName propertyName = new PropertyName(property);

    CSVParser p = new CSVParser(new FileReader(input + start + "-" + end + ".csv"),',');
    String[][]values = p.getAllValues();
    
    List<EvolutionRuleUnitaire> listesub = new ArrayList<EvolutionRuleUnitaire>(0);
    for (int i = 0 ; i<values.length;i++) {
        String valInit=values[i][0];
        logger.trace(valInit);
        EvolutionRuleUnitaire ERU = new EvolutionRuleUnitaire();
        // Création d'un filtre
        Filter[] filter = new Filter[1];
        PropertyIsEqualTo equalTo = new PropertyIsEqualTo();
        equalTo.setPropertyName(propertyName);
        equalTo.setLiteral(new Literal(""+ClasseUrbaine.getValFromSimpleName(valInit)));
        filter[0] = equalTo;
        logger.trace(filter[0]);
        ERU.setFilter(filter);
        List<Possibilites> listePoss = new ArrayList<Possibilites>(0);
        int sum = 0;
        for (int j = 1 ; j<values[i].length;j++) {
            int proba = Integer.parseInt(values[i][j]);
            sum += proba;
        }
        logger.trace("sum = " + sum);
        for (int j = 1 ; j<values[i].length;j++) {
            String valFin=values[j - 1][0];
            valFin = "" + ClasseUrbaine.getValFromSimpleName(valFin);
            logger.trace("valFin = " + valFin);
            int proba = Integer.parseInt(values[i][j]);
            if(proba >0){
                Possibilites possib= new Possibilites();
                possib.setPropertyName(property + "But");
                possib.setProbability((float) proba / (float) sum);
                Literal literal = new Literal();
                literal.setValue(valFin);
                possib.setExpression(literal);  
                listePoss.add(possib);
                logger.trace("new possibility = " + possib.getPropertyName() + " " + possib.getProbability() + " " + possib.getExpression());
            }
        }
        ERU.setPossib(listePoss);
        listesub.add(ERU);
    }
    evolutionRule.setListeEvolutionRuleUnitaire(listesub);
    logger.trace(evolutionRule);
    return evolutionRule;
  }
  /**
   * @param args
   */
  public static void main(String[] args) {
    EvolutionRuleBase configuration = EvolutionRuleBase.getInstance();
    String baseRep = "D:\\Users\\JulienPerret\\workspace\\GeOpenSimSVN\\rules\\stats\\distance\\classe\\2.5-5\\classe-";
    int[] dates = {1956,1976,1989,2002,2008};
    for (int i = 0; i < dates.length -1; i++) {
      int start = dates[i];
      int end = dates[i + 1];
      logger.info("file " + start + " - " +end);
      try {
        EvolutionRule evolutionRule = create(baseRep, start, end);
        configuration.getRules().add(evolutionRule);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    configuration.marshall();
  }
}

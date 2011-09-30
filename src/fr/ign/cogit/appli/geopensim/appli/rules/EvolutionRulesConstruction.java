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
public class EvolutionRulesConstruction {
  private static Logger logger=Logger.getLogger(EvolutionRulesConstruction.class.getName());

  public static void add(EvolutionRule evolutionRule, String input, int classe) {
    CSVParser p;
    try {
        p = new CSVParser(new FileReader(input),',');
        String[][]values = p.getAllValues();
        // Création d'une liste de règles unitaires
        PropertyName propertyName = new PropertyName("densite");
        logger.trace(propertyName);

        PropertyIsEqualTo classProperty = new PropertyIsEqualTo();
        classProperty.setPropertyName(new PropertyName("classificationFonctionnelle"));
        classProperty.setLiteral(new Literal(""+classe));

        List<EvolutionRuleUnitaire> listesub = new ArrayList<EvolutionRuleUnitaire>(0);
        for (int i = 0 ; i<values.length;i++) {
            String valInit=values[i][0];
            logger.trace(valInit);
            EvolutionRuleUnitaire ERU = new EvolutionRuleUnitaire();
            // Création d'un filtre
            Filter[] filter = new Filter[1];
            PropertyIsGreaterThanOrEqualTo greater = new PropertyIsGreaterThanOrEqualTo();
            greater.setPropertyName(propertyName);
            greater.setLiteral(new Literal(valInit));
            PropertyIsLessThan less = new PropertyIsLessThan();
            less.setPropertyName(propertyName);
            less.setLiteral(new Literal("" + (Double.parseDouble(valInit) + 0.05)));
            And andQuantile = new And();
            andQuantile.setOps(new ArrayList<Filter>(2));
            andQuantile.getOps().add(greater);
            andQuantile.getOps().add(less);
            And and = new And();
            and.setOps(new ArrayList<Filter>(2));
            and.getOps().add(classProperty);
            and.getOps().add(andQuantile);
            filter[0] = and;
            logger.trace(filter[0]);
            ERU.setFilter(filter);
            List<Possibilites> listePoss = new ArrayList<Possibilites>();
            int sum = 0;
            for (int j = 1 ; j<values.length;j++) {
                int proba = Integer.parseInt(values[i][j]);
                sum += proba;
            }
            logger.trace("sum = " + sum);
            for (int j = 1 ; j<values.length;j++) {
                String valFin=values[j - 1][0];
                if (!propertyName.getPropertyName().equals("densite")) {
                    valFin = "" + ClasseUrbaine.getValFromSimpleName(valFin);
                }
                logger.trace("valFin = " + valFin);
                int proba = Integer.parseInt(values[i][j]);
                if(proba >0){
                    Possibilites possib= new Possibilites();
                    possib.setPropertyName("densiteBut");
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
        evolutionRule.getListeEvolutionRuleUnitaire().addAll(listesub);
        logger.trace(evolutionRule);
    } catch (FileNotFoundException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    } catch (IOException e1) {
        // TODO Auto-generated catch block
        e1.printStackTrace();
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    EvolutionRuleBase configuration = EvolutionRuleBase.getInstance();
    String baseRep = "D:\\Users\\JulienPerret\\workspace\\GeOpenSimSVN\\rules\\stats\\densite\\";
    int start = 2002;
    int end = 2008;
    String[] fileNames = {
        "densite-em_pas_b-" + start + "-" + end + ".csv",
        "densite-em_spec-" + start + "-" + end + ".csv",
        "densite-h_coll-" + start + "-" + end + ".csv",
        "densite-h_cont-" + start + "-" + end + ".csv",
        "densite-h_indiv-" + start + "-" + end + ".csv",
        "densite-h_mixte-" + start + "-" + end + ".csv",
        "densite-tissu-" + start + "-" + end + ".csv"
    };
    EvolutionRule evolutionRule = new EvolutionRule();
    evolutionRule.setType("ZoneElementaireUrbaine");
    String property = "densite";
    evolutionRule.setPropertyName(property + "But");
    evolutionRule.setNom(property + "-" + start + "-" + end);
    evolutionRule.setStart(start);
    evolutionRule.setEnd(end);
    for (String fileName : fileNames) {
      String[]strings = fileName.split("-");
      String className = strings[1];
      int classe = ClasseUrbaine.getValFromSimpleName(className);
      logger.info("file " + fileName + " classe " + classe  + " - " +className);
      add(evolutionRule, baseRep + fileName, classe);
    }
    configuration.getRules().add(evolutionRule);
    configuration.marshall();
  }
}

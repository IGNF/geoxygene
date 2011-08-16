/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.util.loader;

import java.util.Map.Entry;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.feature.DataSet;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.feature.Population;

/**
 * Classe servant à convertir un chargement en XML et un fichier XML en
 * chargement.
 * @see Chargement
 * 
 * @author Julien Perret
 */
public class ChargementConverter implements Converter {
  @Override
  public void marshal(Object source, HierarchicalStreamWriter writer,
      MarshallingContext context) {
    Chargement chargement = (Chargement) source;
    writer.startNode("DataSet");
    writer.startNode("Nom");
    writer.setValue(chargement.getDataSet().getNom());
    writer.endNode();
    writer.startNode("TypeBD");
    writer.setValue(chargement.getDataSet().getTypeBD());
    writer.endNode();
    writer.startNode("Modele");
    writer.setValue(chargement.getDataSet().getModele());
    writer.endNode();
    writer.startNode("Zone");
    writer.setValue(chargement.getDataSet().getZone());
    writer.endNode();
    writer.startNode("Date");
    writer.setValue(chargement.getDataSet().getDate());
    writer.endNode();
    writer.startNode("Commentaire");
    writer.setValue(chargement.getDataSet().getCommentaire());
    writer.endNode();
    for (IPopulation<?> population : chargement.getDataSet().getPopulations()) {
      writer.startNode("Population");
      writer.startNode("Nom");
      writer.setValue(population.getNom());
      writer.endNode();
      writer.startNode("NomClasse");
      writer.setValue(population.getNomClasse());
      writer.endNode();
      writer.endNode();
    }
    writer.endNode();
    for (Entry<String, String> entry : chargement.getFichiers().entrySet()) {
      writer.startNode("Fichier");
      writer.startNode("NomPopulation");
      writer.setValue(entry.getKey());
      writer.endNode();
      writer.startNode("NomFichier");
      writer.setValue(entry.getValue());
      writer.endNode();
      writer.endNode();
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Object unmarshal(HierarchicalStreamReader reader,
      UnmarshallingContext context) {
    Chargement chargement = new Chargement();
    while (reader.hasMoreChildren()) {
      reader.moveDown();
      if (reader.getNodeName().equalsIgnoreCase("DataSet")) {
        chargement.setDataSet(new DataSet());
        while (reader.hasMoreChildren()) {
          reader.moveDown();
          if (reader.getNodeName().equalsIgnoreCase("Nom")) {
            chargement.getDataSet().setNom(reader.getValue());
          } else if (reader.getNodeName().equalsIgnoreCase("TypeBD")) {
            chargement.getDataSet().setTypeBD(reader.getValue());
          } else if (reader.getNodeName().equalsIgnoreCase("Modele")) {
            chargement.getDataSet().setModele(reader.getValue());
          } else if (reader.getNodeName().equalsIgnoreCase("Zone")) {
            chargement.getDataSet().setZone(reader.getValue());
          } else if (reader.getNodeName().equalsIgnoreCase("Date")) {
            chargement.getDataSet().setDate(reader.getValue());
          } else if (reader.getNodeName().equalsIgnoreCase("Commentaire")) {
            chargement.getDataSet().setCommentaire(reader.getValue());
          } else if (reader.getNodeName().equalsIgnoreCase("Population")) {
            Population<FT_Feature> population = new Population<FT_Feature>();
            while (reader.hasMoreChildren()) {
              reader.moveDown();
              if (reader.getNodeName().equalsIgnoreCase("Nom")) {
                population.setNom(reader.getValue());
              } else if (reader.getNodeName().equalsIgnoreCase("NomClasse")) {
                population.setNomClasse(reader.getValue());
                if (!population.getNomClasse().isEmpty()) {
                  try {
                    population.setClasse((Class<FT_Feature>) Class
                        .forName(population.getNomClasse()));
                  } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                  }
                }
              }
              reader.moveUp();
            }
            chargement.getDataSet().addPopulation(population);
          }
          reader.moveUp();
        }
      } else if (reader.getNodeName().equalsIgnoreCase("Fichier")) {
        String nomPopulation = "";
        String nomFichier = "";
        while (reader.hasMoreChildren()) {
          reader.moveDown();
          if (reader.getNodeName().equalsIgnoreCase("NomPopulation")) {
            nomPopulation = reader.getValue();
          } else if (reader.getNodeName().equalsIgnoreCase("NomFichier")) {
            nomFichier = reader.getValue();
          }
          reader.moveUp();
        }
        if (!nomFichier.isEmpty()) {
          chargement.getFichiers().put(nomPopulation, nomFichier);
        }
      }
      reader.moveUp();
    }
    return chargement;
  }

  @SuppressWarnings("unchecked")
  @Override
  public boolean canConvert(Class classe) {
    return classe.equals(Chargement.class);
  }
}

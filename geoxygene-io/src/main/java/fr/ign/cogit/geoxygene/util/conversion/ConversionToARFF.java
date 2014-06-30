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
package fr.ign.cogit.geoxygene.util.conversion;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.feature.FT_FeatureCollection;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.util.conversion.ShapefileReader;

/**
 * Cette classe permet d'exporter un shapeFile ou une collection de GeOxygene en
 * ARFF en conservant tous les attributs et en assignant un poids 1 à chaque
 * objet
 * 
 * 
 * @author Mickaël Brasebin
 * 
 */
public class ConversionToARFF {


  
  
  
  public static void export(String directory) throws ParseException, IOException{
    
    File f = new File(directory);
    

    File[] lFiles = f.listFiles();
    int nbFiles = lFiles.length;
    
    
    for (int i = 0; i < nbFiles; i++) {

      File fTemp = lFiles[i];

      if (!fTemp.getName().contains(".shp")) {
        continue;
      }

        
      String arFF = fTemp.getName().replace(".shp", "");
      
      
      export(directory +  fTemp.getName(), directory + arFF+ ".arff");
    }
    
    
  }
  /**
   * Cette classe permet d'exporter un shapefile en ARFF
   * 
   * @param pathShapeFile le shapefile en entrée
   * @param outFilePath le fichier en sortie
   * @throws ParseException
   * @throws IOException
   */
  public static void export(String pathShapeFile, String outFilePath)
      throws ParseException, IOException {
    // On convertit tout en FeatureCollection
    IPopulation<IFeature> featCollIni = ShapefileReader.read(pathShapeFile);

    IFeatureCollection<IFeature> featCollFin = new FT_FeatureCollection<IFeature>();

    int nbElemBati = featCollIni.size();

    for (int i = 0; i < nbElemBati; i++) {
      featCollFin.add(featCollIni.get(i));

    }
    // On appelle la fonction prenant des collections en sortie
    export(featCollFin, outFilePath);

  }

  /**
   * Permet de convertir des FeatureCollections en fichier ARFF
   * 
   * @param featColl la collection en entrée
   * @param outFilePath le fichier en sortie
   * @throws ParseException
   * @throws IOException
   */
  public static void export(IFeatureCollection<IFeature> featColl,
      String outFilePath) throws ParseException, IOException {

    ArrayList<Attribute> atts = new ArrayList<Attribute>();
    Instances data;
    double[] vals;
    int i;

    // 1. Préparation des attributs

    IFeature feat = featColl.get(0);

    FeatureType ft = (FeatureType)feat.getFeatureType();

    List<GF_AttributeType> lAttributeTypes = ft.getFeatureAttributes();

    int nbAttributes = lAttributeTypes.size();

    for (i = 0; i < nbAttributes; i++) {

      GF_AttributeType attT = lAttributeTypes.get(i);

      if (attT.getValueType().equalsIgnoreCase("String")) {

        atts.add(new Attribute(attT.getMemberName(), (List<String>)null));

      } else {

        atts.add(new Attribute(attT.getMemberName()));
      }

    }

    // 2 on crée l'instance
    data = new Instances("MyRelation", atts, 0);

    // 3 on ajoute les données

    int nbElem = featColl.size();

    for (i = 0; i < nbElem; i++) {

      feat = featColl.get(i);

      vals = new double[nbAttributes];

      for (int j = 0; j < nbAttributes; j++) {

        GF_AttributeType attT = lAttributeTypes.get(j);

        if (attT.getValueType().equalsIgnoreCase("String")) {
         
          vals[j] = data.attribute(j).addStringValue(feat.getAttribute(attT.getMemberName()).toString());

        } else {
          vals[j] = Double.parseDouble(feat.getAttribute(attT.getMemberName())
              .toString());
        }

      }

      DenseInstance densInstance = new DenseInstance(1.0, vals);

      data.add(densInstance);
    }

    ArffSaver arffSaver = new ArffSaver();
    arffSaver.setInstances(data);
    arffSaver.setFile(new File(outFilePath));
    arffSaver.writeBatch();

  }
  
  
  private static List<String> determinateUniqueValues( GF_AttributeType attT, IFeatureCollection<IFeature> fColl){
    
    
    int nbELem = fColl.size();
    List<String> lOut = new ArrayList<String>();
    
    
    for(IFeature feat: fColl){
        
        String toADdd = feat.getAttribute(attT).toString();
        
        if(! lOut.contains(toADdd)){
            lOut.add(toADdd);
        }
      
      
      
    }
    System.out.println(lOut.size());
    return lOut;
    
  }

}


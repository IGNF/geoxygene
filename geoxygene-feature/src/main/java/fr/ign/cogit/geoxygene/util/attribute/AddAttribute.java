/*
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
 */
package fr.ign.cogit.geoxygene.util.attribute;

import java.util.ArrayList;
import java.util.Arrays;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.SchemaDefaultFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;

public class AddAttribute {
  /**
   * 
   * @author mickaelbrasebin
   * 
   *         Classe permettant d'ajouter un attribut à un entité. Fonctionne
   *         seulement avec DefaultFeature
   * 
   * 
   *         Class to add an attribute to an entity. Only works on
   *         DefaultFeature.
   * 
   * @param feature l'entité dont on souhaite ajouter l'attribut
   * @param attName le nom de l'attribut
   * @param value la valeur
   * @param type le type de l'attribut
   */
  public static void addAttribute(IFeature feature, String attName,
      Object value, String type) {

    if (!(feature instanceof DefaultFeature)) {
      return;

    }

    DefaultFeature feat = (DefaultFeature) feature;
    FeatureType ft = (FeatureType) feat.getFeatureType();

    if (ft == null) {

      feat.setFeatureType(new FeatureType());

      ft = (FeatureType) feat.getFeatureType();
    }

    GF_AttributeType attType = (feat.getFeatureType())
        .getFeatureAttributeByName(attName);

    if (attType == null) {

      AttributeType aT = new AttributeType();

      aT.setMemberName(attName);
      aT.setNomField(attName);
      aT.setValueType(type);

      ft.addFeatureAttribute(aT);

      SchemaDefaultFeature sft = (SchemaDefaultFeature) ft.getSchema();

      if (sft == null) {
        sft = new SchemaDefaultFeature();
        sft.setFeatureType(ft);
        ft.setSchema(sft);
      }

      if (feat.getSchema() == null) {
        feat.setSchema(sft);
      }

      if (!sft.getColonnes().contains(attName)) {

        sft.getColonnes().add(attName);
        sft.getAttLookup().put(ft.getFeatureAttributes().size() - 1,
            new String[] { aT.getNomField(), aT.getMemberName() });

        // sft.getColonnes().add(attName);

      }

      attType = (feat.getFeatureType()).getFeatureAttributeByName(attName);
    }

    if (feat.getFeatureType().getFeatureAttributes() == null) {
      feat.getFeatureType().setFeatureAttributes(
          new ArrayList<GF_AttributeType>());
    }

    if (feat.getAttributes() == null) {
      feat.setAttributes(new Object[0]);
    }

    if (feat.getFeatureType().getFeatureAttributes().size() != feat
        .getAttributes().length) {
      Object[] attributes = new Object[feat.getAttributes().length + 1];

      attributes = Arrays.copyOf(feat.getAttributes(),
          feat.getAttributes().length + 1);

      feat.setAttributes(attributes);

    }

    feat.setAttribute(attType, value);

  }
}

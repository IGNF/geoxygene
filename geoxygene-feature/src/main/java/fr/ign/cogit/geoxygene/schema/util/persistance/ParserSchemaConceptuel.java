/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.schema.util.persistance;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.ign.cogit.geoxygene.api.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationRole;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.SchemaConceptuelProduit;

public class ParserSchemaConceptuel {

  DocumentBuilder db;

  // constructeur
  public ParserSchemaConceptuel() {

    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      this.db = dbf.newDocumentBuilder();
    } catch (FactoryConfigurationError e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    }
  }

  /**
   * parseur de schema conceptuelProduit : crée des listes de FeatureType,
   * AttributeType et InheritanceRelation à partir d'un InputStream
   * @param is
   **/
  public SchemaConceptuelProduit litSchemaConceptuelProduitXML(InputStream is) {

    Element fType;
    Element aType;
    Element asType;
    Element eRole;
    NodeList listAttrib;
    NodeList listRoles;

    SchemaConceptuelProduit schemaCible = new SchemaConceptuelProduit();

    try {
      Document doc;
      doc = this.db.parse(is);
      System.out.println("parsing inputStream");
      System.out.println(doc.toString());
      System.out.println(doc.getDocumentElement().getTagName());

      // ici ajouter la validation par xmlSchema
      // System.out.println("parsing ok");
      Node elementRacine = doc.getDocumentElement();
      Node currentNode;
      NodeList listFeatureTypeNodes = elementRacine.getChildNodes();

      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType featureType;
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationType featureAssociation;
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType featureAttribute;
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationRole associationRole;

      // je Récupère les feature types
      for (int i = 0; i < listFeatureTypeNodes.getLength(); i++) {
        currentNode = listFeatureTypeNodes.item(i);
        // System.out.println(currentNode.getLocalName());
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
          // System.out.println("nodeName = "+listFeatureTypeNodes.item(i).getNodeName());

          if (currentNode.getNodeName().equals("FeatureType")) {
            fType = (Element) currentNode;
            featureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType();
            // featureType.setTypeName(((Element)currentElement.getElementsByTagName("name").item(0)).get);

            featureType.setId(Integer.parseInt(fType.getAttribute("id")));

            featureType.setTypeName(fType.getElementsByTagName("typeName")
                .item(0).getFirstChild().getNodeValue());
            // featureType.setNomClasse(fType.getElementsByTagName("nomClasse").item(0).getFirstChild().getNodeValue());
            // System.out.println("nomClasse = "+featureType.getNomClasse());
            // featureType.setIsExplicite(new
            // Boolean(fType.getElementsByTagName("isExplicite").item(0).getFirstChild().getNodeValue()));

            schemaCible.getFeatureTypes().add(featureType);

            // je Récupère les attributs
            listAttrib = fType.getElementsByTagName("AttributeType");
            for (int j = 0; j < listAttrib.getLength(); j++) {
              aType = (Element) listAttrib.item(j);
              featureAttribute = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType();
              featureAttribute.setId(Integer.parseInt(aType.getAttribute("id")));
              featureAttribute.setMemberName(aType
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setValueType(aType
                  .getElementsByTagName("valueType").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setFeatureType(featureType);
              featureType.addFeatureAttribute(featureAttribute);

            }
          }
        }
      }
      // je recommence pour les associations
      for (int i = 0; i < listFeatureTypeNodes.getLength(); i++) {
        currentNode = listFeatureTypeNodes.item(i);
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

          if (currentNode.getNodeName().equals("AssociationType")) {
            asType = (Element) currentNode;
            featureAssociation = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationType();

            featureAssociation.setId(Integer.parseInt(asType.getAttribute("id")));
            featureAssociation.setTypeName(asType
                .getElementsByTagName("typeName").item(0).getFirstChild()
                .getNodeValue());
            // System.out.println("nom at : "+featureAssociation.getTypeName());
            schemaCible.getFeatureAssociations().add(featureAssociation);

            // je Récupère les attributs
            listAttrib = asType.getElementsByTagName("AttributeType");
            for (int j = 0; j < listAttrib.getLength(); j++) {
              aType = (Element) listAttrib.item(j);
              featureAttribute = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType();
              featureAttribute.setId(Integer.parseInt(aType.getAttribute("id")));
              featureAttribute.setMemberName(aType
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              // System.out.println("nom = "+featureAttribute.getMemberName());
              featureAttribute.setValueType(aType
                  .getElementsByTagName("valueType").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setFeatureType(featureAssociation);
              featureAssociation.addFeatureAttribute(featureAttribute);
            }

            // je récupère les roles
            listRoles = asType.getElementsByTagName("AssociationRole");
            int idFt;
            fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType ft;
            for (int j = 0; j < listRoles.getLength(); j++) {

              // System.out.println("il y a "+listRoles.getLength()+" roles pour cette asso");
              eRole = (Element) listRoles.item(j);
              associationRole = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationRole();
              // System.out.println("id role = "+eRole.getAttribute("id"));
              associationRole.setId(Integer.parseInt(eRole.getAttribute("id")));
              associationRole.setMemberName(eRole
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              // System.out.println("nom role = "+associationRole.getMemberName());
              associationRole.setCardMin(eRole.getElementsByTagName("cardMin")
                  .item(0).getFirstChild().getNodeValue());
              associationRole.setCardMax(eRole.getElementsByTagName("cardMax")
                  .item(0).getFirstChild().getNodeValue());
              associationRole.setAssociationType(featureAssociation);
              featureAssociation.addRole(associationRole);

              // je cherche le feature type associé
              // idFt =
              // eRole.getElementsByTagName("idFeatureType").item(0).getNodeValue()).intValue();
              idFt = Integer.parseInt(eRole.getElementsByTagName("featureTypeId")
                      .item(0).getFirstChild().getNodeValue());
              for (int k = 0; k < schemaCible.getFeatureTypes().size(); k++) {
                ft = (fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType) schemaCible
                    .getFeatureTypes().get(k);
                if (ft.getId() == idFt) {
                  associationRole.setFeatureType(ft);
                  featureAssociation.getLinkBetween().add(ft);
                  ft.addMemberOf(featureAssociation);

                }
              }
            }
          }
        }
      }
    } catch (FactoryConfigurationError e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return schemaCible;
  }

  /**
   * parseur de schema conceptuelProduit : crée des listes de FeatureType,
   * AttributeType et InheritanceRelation à partir d'un fichier
   * @param file fichier XML du schéma conceptuel lu
   * @return objet SchemaConceptuelJeu qui va être créé par le parseur.
   */
  public SchemaConceptuelProduit litSchemaConceptuelProduitXML(File file) {

    Element fType;
    Element aType;
    Element asType;
    Element eRole;
    NodeList listAttrib;
    NodeList listRoles;

    SchemaConceptuelProduit schemaCible = new SchemaConceptuelProduit();

    try {
      Document doc;

      doc = this.db.parse(file);
      System.out.println("parsing file");
      System.out.println(doc.toString());
      System.out.println(doc.getDocumentElement().getTagName());

      // ici ajouter la validation par xmlSchema
      // System.out.println("parsing ok");
      Node elementRacine = doc.getDocumentElement();
      Node currentNode;
      NodeList listFeatureTypeNodes = elementRacine.getChildNodes();

      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType featureType;
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationType featureAssociation;
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType featureAttribute;
      fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationRole associationRole;

      // je Récupère les feature types
      for (int i = 0; i < listFeatureTypeNodes.getLength(); i++) {
        currentNode = listFeatureTypeNodes.item(i);
        // System.out.println(currentNode.getLocalName());
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
          // System.out.println("nodeName = "+listFeatureTypeNodes.item(i).getNodeName());

          if (currentNode.getNodeName().equals("featureType")) {
            fType = (Element) currentNode;
            featureType = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType();
            // featureType.setTypeName(((Element)currentElement.getElementsByTagName("name").item(0)).get);

            featureType.setId(Integer.parseInt(fType.getAttribute("id")));

            featureType.setTypeName(fType.getElementsByTagName("typeName")
                .item(0).getFirstChild().getNodeValue());
            // featureType.setNomClasse(fType.getElementsByTagName("nomClasse").item(0).getFirstChild().getNodeValue());
            // System.out.println("nomClasse = "+featureType.getNomClasse());
            // featureType.setIsExplicite(new
            // Boolean(fType.getElementsByTagName("isExplicite").item(0).getFirstChild().getNodeValue()));

            schemaCible.getFeatureTypes().add(featureType);

            // je Récupère les attributs
            listAttrib = fType.getElementsByTagName("AttributeType");
            for (int j = 0; j < listAttrib.getLength(); j++) {
              aType = (Element) listAttrib.item(j);
              featureAttribute = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType();
              featureAttribute.setId(Integer.parseInt(aType.getAttribute("id")));
              featureAttribute.setMemberName(aType
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setValueType(aType
                  .getElementsByTagName("valueType").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setFeatureType(featureType);
              featureType.addFeatureAttribute(featureAttribute);

            }
          }
        }
      }
      // je recommence pour les associations
      for (int i = 0; i < listFeatureTypeNodes.getLength(); i++) {
        currentNode = listFeatureTypeNodes.item(i);
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

          if (currentNode.getNodeName().equals("associationType")) {
            asType = (Element) currentNode;
            featureAssociation = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationType();

            featureAssociation.setId(Integer.parseInt(asType.getAttribute("id")));
            featureAssociation.setTypeName(asType
                .getElementsByTagName("typeName").item(0).getFirstChild()
                .getNodeValue());
            // System.out.println("nom at : "+featureAssociation.getTypeName());
            schemaCible.getFeatureAssociations().add(featureAssociation);

            // je Récupère les attributs
            listAttrib = asType.getElementsByTagName("AttributeType");
            for (int j = 0; j < listAttrib.getLength(); j++) {
              aType = (Element) listAttrib.item(j);
              featureAttribute = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AttributeType();
              featureAttribute.setId(Integer.parseInt(aType.getAttribute("id")));
              featureAttribute.setMemberName(aType
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              // System.out.println("nom = "+featureAttribute.getMemberName());
              featureAttribute.setValueType(aType
                  .getElementsByTagName("valueType").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setFeatureType(featureAssociation);
              featureAssociation.addFeatureAttribute(featureAttribute);
            }

            // je récupère les roles
            listRoles = asType.getElementsByTagName("AssociationRole");
            int idFt;
            fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType ft;
            for (int j = 0; j < listRoles.getLength(); j++) {

              // System.out.println("il y a "+listRoles.getLength()+" roles pour cette asso");
              eRole = (Element) listRoles.item(j);
              associationRole = new fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationRole();
              // System.out.println("id role = "+eRole.getAttribute("id"));
              associationRole.setId(Integer.parseInt(eRole.getAttribute("id")));
              associationRole.setMemberName(eRole
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              // System.out.println("nom role = "+associationRole.getMemberName());
              associationRole.setCardMin(eRole.getElementsByTagName("cardMin")
                  .item(0).getFirstChild().getNodeValue());
              associationRole.setCardMax(eRole.getElementsByTagName("cardMax")
                  .item(0).getFirstChild().getNodeValue());
              associationRole.setAssociationType(featureAssociation);
              featureAssociation.addRole(associationRole);

              // je cherche le feature type associé
              // idFt =
              // eRole.getElementsByTagName("idFeatureType").item(0).getNodeValue()).intValue();
              idFt = Integer.parseInt(eRole.getElementsByTagName("featureTypeId")
                  .item(0).getFirstChild().getNodeValue());
              for (int k = 0; k < schemaCible.getFeatureTypes().size(); k++) {
                ft = (fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType) schemaCible
                    .getFeatureTypeI(k);
                if (ft.getId() == idFt) {
                  associationRole.setFeatureType(ft);
                  featureAssociation.getLinkBetween().add(ft);
                  ft.addMemberOf(featureAssociation);

                }
              }
            }
          }
        }
      }
    } catch (FactoryConfigurationError e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return schemaCible;
  }

  /**
   * parseur de schema conceptuelJeu : crée des listes de FeatureType,
   * AttributeType et InheritanceRelation à partir d'un fichier
   * @param file fichier XML du schéma conceptuel lu
   * @return objet SchemaConceptuelJeu qui va être créé par le parseur.
   */
  public SchemaConceptuelJeu litSchemaConceptuelJeuXML(File file) {

    Element fType;
    Element aType;
    Element asType;
    Element eRole;
    NodeList listAttrib;
    NodeList listRoles;

    SchemaConceptuelJeu schemaCible = new SchemaConceptuelJeu();

    try {
      Document doc;

      doc = this.db.parse(file);
      System.out.println("parsing file");
      System.out.println(doc.toString());
      System.out.println(doc.getDocumentElement().getTagName());

      // ici ajouter la validation par xmlSchema
      // System.out.println("parsing ok");
      Node elementRacine = doc.getDocumentElement();
      Node currentNode;
      NodeList listFeatureTypeNodes = elementRacine.getChildNodes();

      FeatureType featureType;
      AssociationType featureAssociation;
      AttributeType featureAttribute;
      AssociationRole associationRole;

      // je Récupère les feature types
      for (int i = 0; i < listFeatureTypeNodes.getLength(); i++) {
        currentNode = listFeatureTypeNodes.item(i);
        // System.out.println(currentNode.getLocalName());
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
          // System.out.println("nodeName = "+listFeatureTypeNodes.item(i).getNodeName());

          if (currentNode.getNodeName().equals("FeatureType")) {
            fType = (Element) currentNode;
            featureType = new FeatureType();
            // featureType.setTypeName(((Element)currentElement.getElementsByTagName("name").item(0)).get);

            featureType.setId(Integer.parseInt(fType.getAttribute("id")));

            featureType.setTypeName(fType.getElementsByTagName("typeName")
                .item(0).getFirstChild().getNodeValue());
            // featureType.setNomClasse(fType.getElementsByTagName("nomClasse").item(0).getFirstChild().getNodeValue());
            // System.out.println("nomClasse = "+featureType.getNomClasse());
            // featureType.setIsExplicite(new
            // Boolean(fType.getElementsByTagName("isExplicite").item(0).getFirstChild().getNodeValue()));

            schemaCible.getFeatureTypes().add(featureType);

            // je Récupère les attributs
            listAttrib = fType.getElementsByTagName("AttributeType");
            for (int j = 0; j < listAttrib.getLength(); j++) {
              aType = (Element) listAttrib.item(j);
              featureAttribute = new AttributeType();
              featureAttribute.setId(Integer.parseInt(aType.getAttribute("id")));
              featureAttribute.setMemberName(aType
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setValueType(aType
                  .getElementsByTagName("valueType").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setFeatureType(featureType);
              featureType.addFeatureAttribute(featureAttribute);

            }
          }
        }
      }
      // je recommence pour les associations
      for (int i = 0; i < listFeatureTypeNodes.getLength(); i++) {
        currentNode = listFeatureTypeNodes.item(i);
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

          if (currentNode.getNodeName().equals("AssociationType")) {
            asType = (Element) currentNode;
            featureAssociation = new AssociationType();

            featureAssociation.setId(Integer.parseInt(asType.getAttribute("id")));
            featureAssociation.setTypeName(asType
                .getElementsByTagName("typeName").item(0).getFirstChild()
                .getNodeValue());
            // System.out.println("nom at : "+featureAssociation.getTypeName());
            (schemaCible).getFeatureAssociations().add(featureAssociation);

            // je Récupère les attributs
            listAttrib = asType.getElementsByTagName("AttributeType");
            for (int j = 0; j < listAttrib.getLength(); j++) {
              aType = (Element) listAttrib.item(j);
              featureAttribute = new AttributeType();
              featureAttribute.setId(Integer.parseInt(aType.getAttribute("id")));
              featureAttribute.setMemberName(aType
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              // System.out.println("nom = "+featureAttribute.getMemberName());
              featureAttribute.setValueType(aType
                  .getElementsByTagName("valueType").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setFeatureType(featureAssociation);
              featureAssociation.addFeatureAttribute(featureAttribute);
            }

            // je récupère les roles
            listRoles = asType.getElementsByTagName("AssociationRole");
            int idFt;
            FeatureType ft;
            for (int j = 0; j < listRoles.getLength(); j++) {

              // System.out.println("il y a "+listRoles.getLength()+" roles pour cette asso");
              eRole = (Element) listRoles.item(j);
              associationRole = new AssociationRole();
              // System.out.println("id role = "+eRole.getAttribute("id"));
              associationRole.setId(Integer.parseInt(eRole.getAttribute("id")));
              associationRole.setMemberName(eRole
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              // System.out.println("nom role = "+associationRole.getMemberName());
              associationRole.setCardMin(eRole.getElementsByTagName("cardMin")
                  .item(0).getFirstChild().getNodeValue());
              associationRole.setCardMax(eRole.getElementsByTagName("cardMax")
                  .item(0).getFirstChild().getNodeValue());
              associationRole.setAssociationType(featureAssociation);
              featureAssociation.addRole(associationRole);

              // je cherche le feature type associé
              // idFt =
              // eRole.getElementsByTagName("idFeatureType").item(0).getNodeValue()).intValue();
              idFt = Integer.parseInt(eRole.getElementsByTagName("featureTypeId")
                  .item(0).getFirstChild().getNodeValue());
              for (int k = 0; k < schemaCible.getFeatureTypes().size(); k++) {
                ft = (FeatureType) schemaCible.getFeatureTypes().get(k);
                if (ft.getId() == idFt) {
                  associationRole.setFeatureType(ft);
                  featureAssociation.getLinkBetween().add(ft);
                  ft.addMemberOf(featureAssociation);

                }
              }
            }
          }
        }
      }
    } catch (FactoryConfigurationError e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return schemaCible;
  }

  /**
   * parseur de schema conceptuelJeu : crée des listes de FeatureType,
   * AttributeType et InheritanceRelation à partir d'un fichier
   * @param is flux XML correspondant au schéma conceptuel lu
   * @return objet SchemaConceptuelJeu qui va être créé par le parseur.
   */
  public SchemaConceptuelJeu litSchemaConceptuelJeuXML(InputStream is) {

    Element fType;
    Element aType;
    Element asType;
    Element eRole;
    NodeList listAttrib;
    NodeList listRoles;

    SchemaConceptuelJeu schemaCible = new SchemaConceptuelJeu();

    try {
      Document doc;
      doc = this.db.parse(is);
      System.out.println("parsing inputStream");

      // ici ajouter la validation par xmlSchema
      // System.out.println("parsing ok");
      Node elementRacine = doc.getDocumentElement();
      Node currentNode;
      NodeList listFeatureTypeNodes = elementRacine.getChildNodes();

      FeatureType featureType;
      AssociationType featureAssociation;
      AttributeType featureAttribute;
      AssociationRole associationRole;
      // je Récupère les feature types
      for (int i = 0; i < listFeatureTypeNodes.getLength(); i++) {
        currentNode = listFeatureTypeNodes.item(i);
        // System.out.println(currentNode.getLocalName());
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
          // System.out.println("nodeName = "+listFeatureTypeNodes.item(i).getNodeName());

          if (currentNode.getNodeName().equals("FeatureType")) {
            fType = (Element) currentNode;
            featureType = new FeatureType();
            // featureType.setTypeName(((Element)currentElement.getElementsByTagName("name").item(0)).get);

            featureType.setId(Integer.parseInt(fType.getAttribute("id")));

            featureType.setTypeName(fType.getElementsByTagName("typeName")
                .item(0).getFirstChild().getNodeValue());
            // featureType.setNomClasse(fType.getElementsByTagName("nomClasse").item(0).getFirstChild().getNodeValue());
            // System.out.println("nomClasse = "+featureType.getNomClasse());
            // featureType.setIsExplicite(new
            // Boolean(fType.getElementsByTagName("isExplicite").item(0).getFirstChild().getNodeValue()));

            schemaCible.getFeatureTypes().add(featureType);

            // je Récupère les attributs
            listAttrib = fType.getElementsByTagName("AttributeType");
            for (int j = 0; j < listAttrib.getLength(); j++) {
              aType = (Element) listAttrib.item(j);
              featureAttribute = new AttributeType();
              featureAttribute.setId(Integer.parseInt(aType.getAttribute("id")));
              featureAttribute.setMemberName(aType
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setValueType(aType
                  .getElementsByTagName("valueType").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setFeatureType(featureType);
              featureType.addFeatureAttribute(featureAttribute);

            }
          }
        }
      }
      // je recommence pour les associations
      for (int i = 0; i < listFeatureTypeNodes.getLength(); i++) {
        currentNode = listFeatureTypeNodes.item(i);
        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {

          if (currentNode.getNodeName().equals("AssociationType")) {
            asType = (Element) currentNode;
            featureAssociation = new AssociationType();

            featureAssociation.setId(Integer.parseInt(asType.getAttribute("id")));
            featureAssociation.setTypeName(asType
                .getElementsByTagName("typeName").item(0).getFirstChild()
                .getNodeValue());
            // System.out.println("nom at : "+featureAssociation.getTypeName());
            (schemaCible).getFeatureAssociations().add(featureAssociation);

            // je Récupère les attributs
            listAttrib = asType.getElementsByTagName("AttributeType");
            for (int j = 0; j < listAttrib.getLength(); j++) {
              aType = (Element) listAttrib.item(j);
              featureAttribute = new AttributeType();
              featureAttribute.setId(Integer.parseInt(aType.getAttribute("id")));
              featureAttribute.setMemberName(aType
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              // System.out.println("nom = "+featureAttribute.getMemberName());
              featureAttribute.setValueType(aType
                  .getElementsByTagName("valueType").item(0).getFirstChild()
                  .getNodeValue());
              featureAttribute.setFeatureType(featureAssociation);
              featureAssociation.addFeatureAttribute(featureAttribute);
            }

            // je récupère les roles
            listRoles = asType.getElementsByTagName("AssociationRole");
            int idFt;
            FeatureType ft;
            for (int j = 0; j < listRoles.getLength(); j++) {

              // System.out.println("il y a "+listRoles.getLength()+" roles pour cette asso");
              eRole = (Element) listRoles.item(j);
              associationRole = new AssociationRole();
              // System.out.println("id role = "+eRole.getAttribute("id"));
              associationRole.setId(Integer.parseInt(eRole.getAttribute("id")));
              associationRole.setMemberName(eRole
                  .getElementsByTagName("memberName").item(0).getFirstChild()
                  .getNodeValue());
              // System.out.println("nom role = "+associationRole.getMemberName());
              associationRole.setCardMin(eRole.getElementsByTagName("cardMin")
                  .item(0).getFirstChild().getNodeValue());
              associationRole.setCardMax(eRole.getElementsByTagName("cardMax")
                  .item(0).getFirstChild().getNodeValue());
              associationRole.setAssociationType(featureAssociation);
              featureAssociation.addRole(associationRole);

              // je cherche le feature type associé
              // idFt =
              // eRole.getElementsByTagName("idFeatureType").item(0).getNodeValue()).intValue();
              idFt = Integer.parseInt(eRole.getElementsByTagName("featureTypeId")
                  .item(0).getFirstChild().getNodeValue());
              for (int k = 0; k < schemaCible.getFeatureTypes().size(); k++) {
                ft = (FeatureType) schemaCible.getFeatureTypes().get(k);
                if (ft.getId() == idFt) {
                  associationRole.setFeatureType(ft);
                  featureAssociation.getLinkBetween().add(ft);
                  ft.addMemberOf(featureAssociation);

                }
              }
            }
          }
        }
      }

    } catch (FactoryConfigurationError e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return schemaCible;
  }

  /**
   * sûrialise dans un fichier XML un objet SchemaConceptuelJeu
   */
  public void ecritSchemaConceptuelJeu(SchemaConceptuelJeu sc, File file) {

    Element featureType;
    Element attributeType;
    Element attributeValue;
    Element associationType;
    Element associationRole;
    Element schema;
    Element elementAttributeNode;

    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

      Document doc = documentBuilder.newDocument();
      schema = doc.createElement("schema");

      elementAttributeNode = doc.createElement("name");
      elementAttributeNode.setTextContent(sc.getNomSchema());
      schema.appendChild(elementAttributeNode);

      elementAttributeNode = doc.createElement("definition");
      elementAttributeNode.setTextContent(sc.getDefinition());
      schema.appendChild(elementAttributeNode);

      if (sc.getDataset() != null) {
        schema.setAttribute("dataset",
            String.valueOf((sc).getDataset().getId()));
      }
      if ((sc).getSchemaProduitOrigine() != null) {
        schema.setAttribute("schemaProduitOrigine",
            String.valueOf((sc).getSchemaProduitOrigine().getId()));
      }

      // ecriture des featureTypes
      for (int i = 0; i < sc.getFeatureTypes().size(); i++) {
        FeatureType ft = (FeatureType) sc.getFeatureTypes().get(i);
        featureType = doc.createElement("FeatureType");
        featureType.setAttribute("id", String.valueOf(ft.getId()));

        elementAttributeNode = doc.createElement("typeName");
        elementAttributeNode.setTextContent(ft.getTypeName());
        featureType.appendChild(elementAttributeNode);

        // remplissage des attributs
        for (int j = 0; j < ft.getFeatureAttributes().size(); j++) {
          GF_AttributeType att = ft.getFeatureAttributes().get(j);
          attributeType = doc.createElement("AttributeType");
          attributeType.setAttribute("id", String.valueOf(att.getId()));

          elementAttributeNode = doc.createElement("memberName");
          elementAttributeNode.setTextContent(att.getMemberName());
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("definition");
          elementAttributeNode.setTextContent(att.getDefinition());
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("annotation");
          elementAttributeNode.setTextContent(String.valueOf(att
              .getDefinitionReference()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMin");
          elementAttributeNode.setTextContent(String.valueOf(att.getCardMin()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMax");
          elementAttributeNode.setTextContent(String.valueOf(att.getCardMax()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("valueType");
          elementAttributeNode
              .setTextContent(String.valueOf(att.getValueType()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("valueDomainType");
          elementAttributeNode.setTextContent(String.valueOf(att
              .getValueDomainType()));
          attributeType.appendChild(elementAttributeNode);

          // cas du domaine de valeur enumere
          if (att.getValueDomainType()) {
            for (int k = 0; k < att.getValuesDomain().size(); k++) {
              FC_FeatureAttributeValue av = att.getValuesDomain().get(k);
              attributeValue = doc.createElement("AttributeValue");

              elementAttributeNode = doc.createElement("label");
              elementAttributeNode.setTextContent(av.getLabel());
              attributeValue.appendChild(elementAttributeNode);

              elementAttributeNode = doc.createElement("definition");
              elementAttributeNode.setTextContent(av.getDefinition());
              attributeValue.appendChild(elementAttributeNode);

              elementAttributeNode = doc.createElement("code");
              elementAttributeNode.setTextContent(String.valueOf(av.getcode()));
              attributeValue.appendChild(elementAttributeNode);

              attributeType.appendChild(attributeValue);
            }
          }

          // cas des valeurs non enumerees
          else {
            elementAttributeNode = doc.createElement("valueDomain");
            elementAttributeNode.setTextContent(att.getDomainOfValues());
            attributeType.appendChild(elementAttributeNode);

          }
          featureType.appendChild(attributeType);
        }
        schema.appendChild(featureType);
      }

      // ecriture des associationTypes

      for (int i = 0; i < (sc).getFeatureAssociations().size(); i++) {
        AssociationType at = (sc).getFeatureAssociations().get(i);
        associationType = doc.createElement("AssociationType");
        associationType.setAttribute("id", String.valueOf(at.getId()));

        elementAttributeNode = doc.createElement("typeName");
        elementAttributeNode.setTextContent(at.getTypeName());
        associationType.appendChild(elementAttributeNode);

        // remplissage des attributs
        for (int j = 0; j < at.getFeatureAttributes().size(); j++) {
          GF_AttributeType att = at.getFeatureAttributes().get(j);

          attributeType = doc.createElement("AttributeType");
          attributeType.setAttribute("id", String.valueOf(att.getId()));

          elementAttributeNode = doc.createElement("memberName");
          elementAttributeNode.setTextContent(att.getMemberName());
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("definition");
          elementAttributeNode.setTextContent(att.getDefinition());
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("annotation");
          elementAttributeNode.setTextContent(String.valueOf(att
              .getDefinitionReference()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMin");
          elementAttributeNode.setTextContent(String.valueOf(att.getCardMin()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMax");
          elementAttributeNode.setTextContent(String.valueOf(att.getCardMax()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("valueType");
          elementAttributeNode
              .setTextContent(String.valueOf(att.getValueType()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("valueDomainType");
          elementAttributeNode.setTextContent(String.valueOf(att
              .getValueDomainType()));
          attributeType.appendChild(elementAttributeNode);

          // cas du domaine de valeur enumere
          if (att.getValueDomainType()) {
            for (int k = 0; k < att.getValuesDomain().size(); k++) {
              FC_FeatureAttributeValue av = att.getValuesDomain().get(k);
              attributeValue = doc.createElement("AttributeValue");

              elementAttributeNode = doc.createElement("label");
              elementAttributeNode.setTextContent(av.getLabel());
              attributeValue.appendChild(elementAttributeNode);

              elementAttributeNode = doc.createElement("definition");
              elementAttributeNode.setTextContent(av.getDefinition());
              attributeValue.appendChild(elementAttributeNode);

              elementAttributeNode = doc.createElement("code");
              elementAttributeNode.setTextContent(String.valueOf(av.getcode()));
              attributeValue.appendChild(elementAttributeNode);

              attributeType.appendChild(attributeValue);
            }
          }

          // cas des valeurs non enumerees
          else {
            attributeType.setAttribute("ValueDomain", att.getDomainOfValues());
          }
          associationType.appendChild(attributeType);
        }

        // remplissage des roles
        for (int l = 0; l < at.getRoles().size(); l++) {
          AssociationRole ar = at.getRoleI(l);
          associationRole = doc.createElement("AssociationRole");
          associationRole.setAttribute("id", String.valueOf(ar.getId()));

          elementAttributeNode = doc.createElement("memberName");
          elementAttributeNode
              .setTextContent(String.valueOf(ar.getMemberName()));
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("definition");
          elementAttributeNode.setTextContent(ar.getDefinition());
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("featureTypeId");
          elementAttributeNode.setTextContent(String.valueOf(((FeatureType) ar
              .getFeatureType()).getId()));
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMax");
          elementAttributeNode.setTextContent(ar.getCardMax());
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMin");
          elementAttributeNode.setTextContent(ar.getCardMin());
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("isComponent");
          elementAttributeNode.setTextContent(String.valueOf(ar
              .getIsComponent()));
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("isComposite");
          elementAttributeNode.setTextContent(String.valueOf(ar
              .getIsComposite()));
          associationRole.appendChild(elementAttributeNode);

          associationType.appendChild(associationRole);

        }
        schema.appendChild(associationType);
      }

      // ecriture des heritages : à faire !!!

      doc.appendChild(schema);

      // fichier par defaut si variable d'entree nulle
      if (file == null) {
        file = new File("schemaConceptuel.xml");
      }

      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer();
      transformer.setOutputProperty("indent", "yes");
      // transformer.transform(new DOMSource(doc), new
      // StreamResult(System.out));
      transformer.transform(new DOMSource(doc), new StreamResult(file));
      System.out.println("schema ecrit dans " + file.getAbsolutePath());

    } catch (FactoryConfigurationError e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (TransformerFactoryConfigurationError e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }

  }

  /**
   * sûrialise dans un fichier XML un objet SchemaConceptuelProduit
   * @param sc
   * @param file
   */
  public void ecritSchemaConceptuelProduit(SchemaConceptuelProduit sc, File file) {
    Element featureType;
    Element attributeType;
    Element attributeValue;
    Element associationType;
    Element associationRole;
    Element schema;
    Element elementAttributeNode;

    try {
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder documentBuilder = dbf.newDocumentBuilder();

      Document doc = documentBuilder.newDocument();
      schema = doc.createElement("schema");

      elementAttributeNode = doc.createElement("name");
      elementAttributeNode.setTextContent(sc.getNomSchema());
      schema.appendChild(elementAttributeNode);

      elementAttributeNode = doc.createElement("definition");
      elementAttributeNode.setTextContent(sc.getDefinition());
      schema.appendChild(elementAttributeNode);

      elementAttributeNode = doc.createElement("produit");
      elementAttributeNode.setTextContent(sc.getBD());
      schema.appendChild(elementAttributeNode);

      // ecriture des featureTypes
      for (int i = 0; i < sc.getFeatureTypes().size(); i++) {
        fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType ft = (fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.FeatureType) sc
            .getFeatureTypes().get(i);
        featureType = doc.createElement("FeatureType");
        featureType.setAttribute("id", String.valueOf(ft.getId()));

        elementAttributeNode = doc.createElement("typeName");
        elementAttributeNode.setTextContent(ft.getTypeName());
        featureType.appendChild(elementAttributeNode);

        // remplissage des attributs
        for (int j = 0; j < ft.getFeatureAttributes().size(); j++) {
          GF_AttributeType att = ft.getFeatureAttributes().get(j);
          attributeType = doc.createElement("AttributeType");
          attributeType.setAttribute("id", String.valueOf(att.getId()));

          elementAttributeNode = doc.createElement("memberName");
          elementAttributeNode.setTextContent(att.getMemberName());
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("definition");
          elementAttributeNode.setTextContent(att.getDefinition());
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("annotation");
          elementAttributeNode.setTextContent(String.valueOf(att
              .getDefinitionReference()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMin");
          elementAttributeNode.setTextContent(String.valueOf(att.getCardMin()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMax");
          elementAttributeNode.setTextContent(String.valueOf(att.getCardMax()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("valueType");
          elementAttributeNode
              .setTextContent(String.valueOf(att.getValueType()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("valueDomainType");
          elementAttributeNode.setTextContent(String.valueOf(att
              .getValueDomainType()));
          attributeType.appendChild(elementAttributeNode);

          // cas du domaine de valeur enumere
          if (att.getValueDomainType()) {
            for (int k = 0; k < att.getValuesDomain().size(); k++) {
              FC_FeatureAttributeValue av = att.getValuesDomain().get(k);
              attributeValue = doc.createElement("AttributeValue");

              elementAttributeNode = doc.createElement("label");
              elementAttributeNode.setTextContent(av.getLabel());
              attributeValue.appendChild(elementAttributeNode);

              elementAttributeNode = doc.createElement("definition");
              elementAttributeNode.setTextContent(av.getDefinition());
              attributeValue.appendChild(elementAttributeNode);

              elementAttributeNode = doc.createElement("code");
              elementAttributeNode.setTextContent(String.valueOf(av.getcode()));
              attributeValue.appendChild(elementAttributeNode);

              attributeType.appendChild(attributeValue);
            }
          }

          // cas des valeurs non enumerees
          else {
            elementAttributeNode = doc.createElement("valueDomain");
            elementAttributeNode.setTextContent(att.getDomainOfValues());
            attributeType.appendChild(elementAttributeNode);

          }
          featureType.appendChild(attributeType);
        }
        schema.appendChild(featureType);
      }

      // ecriture des associationTypes

      for (int i = 0; i < sc.getFeatureAssociations().size(); i++) {
        fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationType at = sc
            .getFeatureAssociations().get(i);
        associationType = doc.createElement("AssociationType");
        associationType.setAttribute("id", String.valueOf(at.getId()));

        // remplissage des attributs
        for (int j = 0; j < at.getFeatureAttributes().size(); j++) {
          GF_AttributeType att = at.getFeatureAttributes().get(j);

          attributeType = doc.createElement("AttributeType");
          attributeType.setAttribute("id", String.valueOf(att.getId()));

          elementAttributeNode = doc.createElement("memberName");
          elementAttributeNode.setTextContent(att.getMemberName());
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("definition");
          elementAttributeNode.setTextContent(att.getDefinition());
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("annotation");
          elementAttributeNode.setTextContent(String.valueOf(att
              .getDefinitionReference()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMin");
          elementAttributeNode.setTextContent(String.valueOf(att.getCardMin()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMax");
          elementAttributeNode.setTextContent(String.valueOf(att.getCardMax()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("valueType");
          elementAttributeNode
              .setTextContent(String.valueOf(att.getValueType()));
          attributeType.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("valueDomainType");
          elementAttributeNode.setTextContent(String.valueOf(att
              .getValueDomainType()));
          attributeType.appendChild(elementAttributeNode);

          // cas du domaine de valeur enumere
          if (att.getValueDomainType()) {
            for (int k = 0; k < att.getValuesDomain().size(); k++) {
              FC_FeatureAttributeValue av = att.getValuesDomain().get(k);
              attributeValue = doc.createElement("AttributeValue");

              elementAttributeNode = doc.createElement("label");
              elementAttributeNode.setTextContent(av.getLabel());
              attributeValue.appendChild(elementAttributeNode);

              elementAttributeNode = doc.createElement("definition");
              elementAttributeNode.setTextContent(av.getDefinition());
              attributeValue.appendChild(elementAttributeNode);

              elementAttributeNode = doc.createElement("code");
              elementAttributeNode.setTextContent(String.valueOf(av.getcode()));
              attributeValue.appendChild(elementAttributeNode);

              attributeType.appendChild(attributeValue);
            }
          }

          // cas des valeurs non enumerees
          else {
            attributeType.setAttribute("ValueDomain", att.getDomainOfValues());
          }
          associationType.appendChild(attributeType);
        }

        // remplissage des roles
        for (int l = 0; l < at.getRoles().size(); l++) {
          fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.AssociationRole ar = at
              .getRoleI(l);
          associationRole = doc.createElement("AssociationRole");
          associationRole.setAttribute("id", String.valueOf(ar.getId()));

          elementAttributeNode = doc.createElement("memberName");
          elementAttributeNode
              .setTextContent(String.valueOf(ar.getMemberName()));
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("definition");
          elementAttributeNode.setTextContent(ar.getDefinition());
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("featureTypeId");
          elementAttributeNode.setTextContent(String.valueOf(((FeatureType) ar
              .getFeatureType()).getId()));
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMax");
          elementAttributeNode.setTextContent(ar.getCardMax());
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("cardMin");
          elementAttributeNode.setTextContent(ar.getCardMin());
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("isComponent");
          elementAttributeNode.setTextContent(String.valueOf(ar
              .getIsComponent()));
          associationRole.appendChild(elementAttributeNode);

          elementAttributeNode = doc.createElement("isComposite");
          elementAttributeNode.setTextContent(String.valueOf(ar
              .getIsComposite()));
          associationRole.appendChild(elementAttributeNode);

        }
        schema.appendChild(associationType);
      }

      // ecriture des heritages : à faire !!!

      doc.appendChild(schema);

      // fichier par defaut si variable d'entree nulle
      if (file == null) {
        file = new File("");
      }

      TransformerFactory tFactory = TransformerFactory.newInstance();
      Transformer transformer = tFactory.newTransformer();
      transformer.setOutputProperty("indent", "yes");
      // transformer.transform(new DOMSource(doc), new
      // StreamResult(System.out));
      transformer.transform(new DOMSource(doc), new StreamResult(file));
      System.out.println("schema ecrit dans " + file.getAbsolutePath());

    } catch (FactoryConfigurationError e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (TransformerConfigurationException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (TransformerFactoryConfigurationError e) {
      e.printStackTrace();
    } catch (TransformerException e) {
      e.printStackTrace();
    }
  }
}

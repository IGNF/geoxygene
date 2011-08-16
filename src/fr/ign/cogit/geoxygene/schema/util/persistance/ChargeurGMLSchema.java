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
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.SchemaConceptuelJeu;

/**
 * @author Balley transforme un GMLschema, c'est à dire un fichier x.xsd
 *         conforme à http://schemas.opengis.net/gml/2.1.2/feature.xsd, en un
 *         SchemaISOJeu
 * 
 *         TODO gérer le GML3 et les xlink, s'occuper plus en détail des types
 *         géométriques
 */
public class ChargeurGMLSchema {

  Document docXSD;

  public ChargeurGMLSchema() {
  }

  public ChargeurGMLSchema(Document doc) {
    this.docXSD = doc;
  }

  /**
   * @param args FIXME c'est très moche : il y a des noms de fichiers locaux...
   */
  public static void main(String[] args) {
    // je ne crée que l'objet sc et je ne le rends pas persistant.

    try {
      // File fichierXSD = new
      // File("D:/Users/Balley/données/terranumerica/rge/bdtopo/TRONCON_ROUTE.xsd");
      File fichierXSD = new File("D:/Users/Balley/données/gml/commune.xsd");
      // File fichierXSD = new
      // File("D:/Users/Balley/données/gml/route.xsd");
      URL urlFichierXSD = fichierXSD.toURI().toURL();
      // System.out.println(urlFichierXSD);

      InputStream isXSD = urlFichierXSD.openStream();
      DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = dbFactory.newDocumentBuilder();
      Document documentXSD = (builder.parse(isXSD));
      ChargeurGMLSchema chargeur = new ChargeurGMLSchema(documentXSD);

      SchemaConceptuelJeu sc = chargeur.gmlSchema2schemaConceptuel(documentXSD);

      // ecriture du schéma généré
      System.out.println(sc.getFeatureTypes().size());
      for (int i = 0; i < sc.getFeatureTypes().size(); i++) {
        System.out.println(sc.getFeatureTypes().get(i).getTypeName());
        for (int j = 0; j < sc.getFeatureTypes().get(i).getFeatureAttributes()
            .size(); j++) {
          System.out.println("    "
              + sc.getFeatureTypes().get(i).getFeatureAttributes().get(j)
                  .getMemberName()
              + " : "
              + sc.getFeatureTypes().get(i).getFeatureAttributes().get(j)
                  .getValueType());
        }
      }

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
  }

  /**
   * transforme un GMLSchema déjà parsé en un objet SchemaISOJeu contenant une
   * liste de FeatureType.
   * @param newDocXSD
   * @return SchemaConceptuelJeu
   */
  public SchemaConceptuelJeu gmlSchema2schemaConceptuel(Document newDocXSD) {
    SchemaConceptuelJeu schemaConceptuel = new SchemaConceptuelJeu();

    // recherche des FeatureTypes
    // recherche de tout ce qui etend AbstractFeatureType
    // System.out.println("nom element racine = "+docXSD.getElementsByTagName("element").item(0).getAttributes().getNamedItem("name").getNodeValue());

    NodeList listeNoeuds = newDocXSD.getElementsByTagName("extension");
    // System.out.println("nb extension = "+listeNoeuds.getLength());
    Node noeudAbstractFeatureType = null;
    String nomType = null;
    String nomElementsDeCeType = null;
    String typeSimple = null;
    for (int i = 0; i < listeNoeuds.getLength(); i++) {
      if (listeNoeuds.item(i).getAttributes().getNamedItem("base")
          .getNodeValue().equals("gml:AbstractFeatureType")) {
        System.out
            .println("type = "
                + listeNoeuds.item(i).getParentNode().getParentNode()
                    .getNodeName());
        noeudAbstractFeatureType = listeNoeuds.item(i).getParentNode()
            .getParentNode();
        nomType = noeudAbstractFeatureType.getAttributes().getNamedItem("name")
            .getNodeValue();
        System.out.println("le type : " + nomType
            + " etend AbstractFeatureType");

        // je cherche le nom des élements de ce type : ca me donnera le
        // nom du
        // FeatureType à mettre dans mon schemaConceptuel
        System.out.println("\nLecture des attributs...");
        NodeList listElements = newDocXSD.getElementsByTagName("element");
        for (int j = 0; j < listElements.getLength(); j++) {

          // System.out.println(listElements.item(j));
          // System.out.println(listElements.item(j).getAttributes());
          // System.out.println(listElements.item(j).getAttributes().getNamedItem("type"));

          if (listElements.item(j).getAttributes().getNamedItem("type") != null) {
            if (listElements.item(j).getAttributes().getNamedItem("type")
                .getNodeValue().equals("gml2:" + nomType)) {
              nomElementsDeCeType = listElements.item(j).getAttributes()
                  .getNamedItem("name").getNodeValue();
              // System.out.println("nom des elements de ce type : "+nomElementsDeCeType);

              // ce type est effectivement utilisé dans le jeu, je
              // crée un featureType
              FeatureType ft = new FeatureType();
              ft.setTypeName(nomElementsDeCeType);

              // je remplis les attributs

              // System.out.println(noeudAbstractFeatureType.getChildNodes().item(1).getNodeName());
              Node noeudSequence = noeudAbstractFeatureType.getChildNodes()
                  .item(1).getChildNodes().item(1).getChildNodes().item(1);
              NodeList noeudsAttributs = noeudSequence.getChildNodes();
              // System.out.println("nb attributs = "+noeudsAttributs.getLength());
              Node noeudAttribut = null;
              AttributeType fa = null;
              // System.out.println(listNoeudsAttributs.getLength());
              for (int k = 1; k < noeudsAttributs.getLength() - 1; k = k + 2) {
                noeudAttribut = noeudsAttributs.item(k);

                if (noeudAttribut.getNodeName().equals("choice")) {
                  // System.out.println("cas choice");
                  // à voir, pour la géométrie par exemple
                } else if (noeudAttribut.getNodeName().equals("element")) {
                  // System.out.println("cas element");
                  // System.out.println("\nattribut "+k+" noeud "+noeudAttribut.getNodeName());
                  System.out.println("name = "
                      + noeudAttribut.getAttributes().getNamedItem("name")
                          .getNodeValue());
                  // System.out.println("nodeValue = "+noeudAttribut.getNodeValue());
                  fa = new AttributeType();
                  fa.setMemberName(noeudAttribut.getAttributes().getNamedItem(
                      "name").getNodeValue());

                  // je cherche le type de l'attribut

                  // soit c'est un type simple mis directement
                  // en attribut
                  if (noeudAttribut.getAttributes().getNamedItem("type") != null) {
                    fa.setValueType(noeudAttribut.getAttributes().getNamedItem(
                        "type").getNodeValue());
                    // System.out.println("type direct : "+noeudAttribut.getAttributes().getNamedItem("type").getNodeValue());
                  }

                  // soit c'est un type simple en étendant un
                  // autre
                  else if (noeudAttribut.hasChildNodes()) {
                    // System.out.println("le type a "+noeudAttribut.getChildNodes().getLength()+" childNodes : il doit etendre un type");
                    for (int l = 0; l < noeudAttribut.getChildNodes()
                        .getLength(); l++) {
                      if (noeudAttribut.getChildNodes().item(l).getNodeName()
                          .equals("simpleType")) {
                        // System.out.println("simpleType");
                        // System.out.println("nodeType : "+noeudAttribut.getNodeType());
                        // System.out.println(noeudAttribut.getChildNodes().item(l).getChildNodes().item(1));
                        typeSimple = noeudAttribut.getChildNodes().item(l)
                            .getChildNodes().item(1).getAttributes()
                            .getNamedItem("base").getNodeValue();

                        // System.out.println("typeSimple = "+typeSimple);
                        fa.setValueType(ChargeurGMLSchema
                            .GMLType2schemaType(typeSimple));
                        // System.out.println("type interne = "+GMLType2schemaType(typeSimple));
                        // System.out.println(noeudAttribut.getChildNodes().item(1));
                      }
                    }
                  }
                  ft.addFeatureAttribute(fa);

                  // soit c'est un type complexe... à faire
                  // soit c'est un xlink... à faire
                }
              }

              // rechercher le type spatial
              this.trouveTypeGeom(noeudAbstractFeatureType, ft);

              ft.setIsExplicite(true);
              schemaConceptuel.getFeatureTypes().add(ft);
            }
          }

        }

      }
      System.out.println();
    }
    return schemaConceptuel;
  }

  public void trouveTypeGeom(Node noeudFT, FeatureType ft) {

    AttributeType attribGeom = null;
    System.out.println("\nrecherche spatialité...");
    // System.out.println(noeudFT.getNodeType());
    NodeList list = noeudFT.getChildNodes();
    for (int i = 0; i < list.getLength(); i++) {
      // System.out.println(list.item(i).getNodeName());
    }
    NodeList list2 = this.docXSD.getElementsByTagName("element");
    for (int i = 0; i < list2.getLength(); i++) {
      // System.out.println(list2.item(i).getNodeName());
      if (list2.item(i).getAttributes().getNamedItem("ref") != null) {
        // System.out.println("c'est une référence à "+list2.item(i).getAttributes().getNamedItem("ref").getNodeValue());
        // ce noeud m'interesse, je regarde s'il appartient bien à mon
        // abstractFeatureType
        Node noeudParent = list2.item(i);
        while (noeudParent != null) {
          // System.out.println("iter");
          if (noeudParent.equals(noeudFT)) {
            // System.out.println("je suis bien dans le bon ft");
            break;
          }
          noeudParent = noeudParent.getParentNode();
        }
        if (noeudParent == null) {
          // System.out.println("c'était ailleurs");
        }

        if (list2.item(i).getAttributes().getNamedItem("ref").getNodeValue()
            .equals("gml:polygonProperty")) {
          attribGeom = new AttributeType();
          attribGeom.setMemberName("geom");
          attribGeom.setValueType("surfacique");
        } else if (list2.item(i).getAttributes().getNamedItem("ref")
            .getNodeValue().equals("gml:multiPolygonProperty")) {
          attribGeom = new AttributeType();
          attribGeom.setMemberName("geom2");
          attribGeom.setValueType("surfacique multiple");
        }
        ft.addFeatureAttribute(attribGeom);
      }
    }

  }

  /**
   * transforme un GMLSchema déjà parsé en un objet SchemaConceptuelIni
   * contenant une liste de SC_Ini_FeatureType. Utile seulement pour
   * l'application transfoschema qui doit garder le schéma initial du
   * producteur, non modifiable, en face d'un schéma d'utilisateur modifiable.
   * @param docXSD
   * @return
   */

  /**
   * Génère une structure goexygene complete : schéma conceptuel persistant,
   * bibliothèques de classes, tables et mapping. Utilise la méthode
   * gmlSchema2schemaConceptuel(docXSD) et les utilitaires du package
   * fr.ign.cogit.appli.sissi.outils.transfoschema.donnees.classesGenerees.
   * outils
   * @param GMLType
   */
  /*
   * public void gmlSchema2structureGeoxygene(Document docXSD){ DataSetCommun ds
   * = new DataSetCommun(); DataSetCommun.db = new GeodatabaseCommun();
   * ds.setSchemaConceptuel(gmlSchema2schemaConceptuel(docXSD));
   * System.out.println("objet SchemaConceptuel créé");
   * GenerationSL.sc2schemaLogiqueGeoxygene(ds.getSchemaConceptuel(), false);
   * System.out.println("classes et tables du schéma logique créées");
   * ds.getSchemaConceptuel().ecritSchemaConceptuel();
   * ds.getSchemaConceptuelIni().ecritSchemaConceptuel();
   * System.out.println("schéma conceptuel persistant"); }
   */

  protected static String GMLType2schemaType(String GMLType) {
    if (GMLType.compareToIgnoreCase("string") == 0) {
      return "text";
    } else if (GMLType.compareToIgnoreCase("") == 0) {
      return "float";
    } else if (GMLType.compareToIgnoreCase("integer") == 0) {
      return "entier";
    } else if (GMLType.compareToIgnoreCase("") == 0) {
      return "bool";
    } else if (GMLType.compareToIgnoreCase("gml:lineStringProperty") == 0) {
      return "linéaire";
    } else if (GMLType.compareToIgnoreCase("gml:multiLineStringProperty") == 0) {
      return "linéaire";
    } else if (GMLType.compareToIgnoreCase("gml:pointProperty") == 0) {
      return "ponctuel";
    } else if (GMLType.compareToIgnoreCase("gml:multiPolygonProperty") == 0) {
      return "surfacique";
    } else if (GMLType.compareToIgnoreCase("gml:polygonProperty") == 0) {
      return "surfacique";
    } else if (GMLType.compareToIgnoreCase("") == 0) {
      return "arc";
    } else if (GMLType.compareToIgnoreCase("") == 0) {
      return "noeud";
    } else if (GMLType.compareToIgnoreCase("") == 0) {
      return "face";
    } else {
      return "text";
    }
  }
}

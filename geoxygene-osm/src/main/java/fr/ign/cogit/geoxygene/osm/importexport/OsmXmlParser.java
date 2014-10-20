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
package fr.ign.cogit.geoxygene.osm.importexport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.util.CRSConversion;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPositionList;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.Population;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.RoleMembre;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.TypeRelation;
import fr.ign.cogit.geoxygene.osm.schema.OsmCaptureTool;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPositionList;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_LineString;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Point;

/**
 * 
 * @author MDVan-Damme
 */
public class OsmXmlParser {
  
  private static final Logger LOGGER = Logger.getLogger(OsmXmlParser.class);
  
  private Set<OSMResource> nodes;
  
  private Population<DefaultFeature> popParking;
  private Population<DefaultFeature> popPolice;
  private Population<DefaultFeature> popSchool;
  private Population<DefaultFeature> popPointOSM;
  private Population<DefaultFeature> popLigneOSM;
  
  public Population<DefaultFeature> getPopParking() {
    return this.popParking;
  }
  
  public Population<DefaultFeature> getPopPolice() {
    return this.popPolice;
  }
  
  public Population<DefaultFeature> getPopSchool() {
    return this.popSchool;
  }
  
  public Population<DefaultFeature> getPopPoint() {
    return this.popPointOSM;
  }
  
  public Population<DefaultFeature> getPopLigne() {
    return this.popLigneOSM;
  }
  
  /**
   * 
   * @param doc
   * @throws SAXException
   * @throws IOException
   * @throws ParserConfigurationException
   */
  public void loadOsm(Document doc) throws SAXException, IOException,
    ParserConfigurationException {
    
    this.nodes = new HashSet<OSMResource>();
    
    // popParking  popPolice   popSchool
    popParking = new Population<DefaultFeature>(false, "parkings", DefaultFeature.class, true);
    FeatureType parkingFeatureType = new FeatureType();
    parkingFeatureType.setTypeName("parkings");
    parkingFeatureType.setGeometryType(IPoint.class);
    popParking.setFeatureType(parkingFeatureType);
    
    popPolice = new Population<DefaultFeature>(false, "police", DefaultFeature.class, true);
    FeatureType policeFeatureType = new FeatureType();
    policeFeatureType.setTypeName("police");
    policeFeatureType.setGeometryType(IPoint.class);
    popPolice.setFeatureType(policeFeatureType);
    
    popSchool = new Population<DefaultFeature>(false, "school", DefaultFeature.class, true);
    FeatureType schoolFeatureType = new FeatureType();
    schoolFeatureType.setTypeName("school");
    schoolFeatureType.setGeometryType(IPoint.class);
    popSchool.setFeatureType(schoolFeatureType);
    
    popPointOSM = new Population<DefaultFeature>(false, "points", DefaultFeature.class, true);
    FeatureType pointFeatureType = new FeatureType();
    pointFeatureType.setTypeName("point");
    pointFeatureType.setGeometryType(IPoint.class);
    popPointOSM.setFeatureType(pointFeatureType);
    
    popLigneOSM = new Population<DefaultFeature>(false, "lignes", DefaultFeature.class, true);
    FeatureType ligneFeatureType = new FeatureType();
    ligneFeatureType.setTypeName("point");
    ligneFeatureType.setGeometryType(ILineString.class);
    popLigneOSM.setFeatureType(ligneFeatureType);
    
    doc.getDocumentElement().normalize();
    Element root = (Element) doc.getElementsByTagName("osm").item(0);
    
    // On charge les objets ponctuels
    int nbNoeuds = root.getElementsByTagName(OsmGeneObj.TAG_NODE).getLength();
    for (int i = 0; i < nbNoeuds; i++) {
      
      Element elem = (Element) root.getElementsByTagName(OsmGeneObj.TAG_NODE)
          .item(i);
      
      // On récupère son ID
      long id = Long.valueOf(elem.getAttribute(OsmGeneObj.ATTR_ID));
      // String versionAttr = elem.getAttribute(OsmGeneObj.ATTR_VERSION);
      
      // on récupère sa géométrie
      double lat = Double.valueOf(elem.getAttribute(OsmGeneObj.ATTR_LAT));
      double lon = Double.valueOf(elem.getAttribute(OsmGeneObj.ATTR_LON));
      OSMNode geom = new OSMNode(lat, lon);
      
      if (elem.getElementsByTagName("tag").getLength() > 0) {
        
        DefaultFeature n = null;
        String type = this.instancierTagsObjet(elem);
        if (type.equals("parking")) {
          n = popParking.nouvelElement(new GM_Point(CRSConversion.wgs84ToLambert93(geom.getLatitude(), geom.getLongitude())));
        } else if (type.equals("school")) {
          n = popSchool.nouvelElement(new GM_Point(CRSConversion.wgs84ToLambert93(geom.getLatitude(), geom.getLongitude())));
        } else if (type.equals("police")) {
          n = popPolice.nouvelElement(new GM_Point(CRSConversion.wgs84ToLambert93(geom.getLatitude(), geom.getLongitude())));
        } else {
          n = popPointOSM.nouvelElement(new GM_Point(CRSConversion.wgs84ToLambert93(geom.getLatitude(), geom.getLongitude())));
        }
        n.setFeatureType(pointFeatureType);
        
        
      }
      
      // On construit le nouvel objet ponctuel
      OSMResource obj = new OSMResource("", geom, id, 0, 0, 1, null);
      geom.setObjet(obj);
      // On ajoute obj aux objets chargés
      this.nodes.add(obj);

    }
    LOGGER.info(nbNoeuds + " points chargés");

    
    // On charge les objets linéaires
    int nbWays = root.getElementsByTagName(OsmGeneObj.TAG_WAY).getLength();
    for (int i = 0; i < nbWays; i++) {
      
      Element elem = (Element) root.getElementsByTagName(OsmGeneObj.TAG_WAY)
          .item(i);
      
      // On récupère sa géométrie
      ArrayList<Long> vertices = new ArrayList<Long>();
      for (int j = 0; j < elem.getElementsByTagName("nd").getLength(); j++) {
        Element ndElem = (Element) elem.getElementsByTagName("nd").item(j);
        long ref = Long.valueOf(ndElem.getAttribute("ref"));
        vertices.add(ref);
      }
      OSMWay way = new OSMWay(vertices);
      
      // on récupère sa géométrie
      IDirectPositionList coord = new DirectPositionList();
      for (long index : way.getVertices()) {
        OSMNode vertex = null;
        for (OSMResource node : nodes) {
          if (node.getId() == index) {
            vertex = (OSMNode) node.getGeom();
            break;
          }
        }
        if (vertex != null) {
          IDirectPosition pt = CRSConversion.wgs84ToLambert93(
              vertex.getLatitude(), vertex.getLongitude());
          coord.add(pt);
        }
      }
      if (coord.size() > 1) {
        DefaultFeature n = popLigneOSM.nouvelElement(new GM_LineString(coord));
        n.setFeatureType(ligneFeatureType);
        this.instancierTagsObjet(elem);
      }
    }
    LOGGER.info(nbWays + " lignes chargées");

    // On charge les relations
    int nbRels = root.getElementsByTagName(OsmGeneObj.TAG_REL).getLength();
    for (int i = 0; i < nbRels; i++) {
      
      Element elem = (Element) root.getElementsByTagName(OsmGeneObj.TAG_REL)
          .item(i);
      
      // On récupère sa primitive
      TypeRelation type = TypeRelation.NON_DEF;
      for (int j = 0; j < elem.getElementsByTagName("tag").getLength(); j++) {
        Element tagElem = (Element) elem.getElementsByTagName("tag").item(j);
        String cle = tagElem.getAttribute("k");
        if (cle.equals("type")) {
          type = TypeRelation.valueOfTexte(tagElem.getAttribute("v"));
        }
      }
      List<OsmRelationMember> membres = new ArrayList<OsmRelationMember>();
      for (int j = 0; j < elem.getElementsByTagName("member").getLength(); j++) {
        Element memElem = (Element) elem.getElementsByTagName("member").item(j);
        long ref = Long.valueOf(memElem.getAttribute("ref"));
        String role = memElem.getAttribute("role");
        membres.add(new OsmRelationMember(RoleMembre.valueOfTexte(role), true,
            ref));
      }
      //OSMRelation geom = new OSMRelation(type, membres);
      
    }
    LOGGER.info(nbRels + " relations chargées");
    
    int nbResources = nbNoeuds + nbWays + nbRels;
    LOGGER.info(nbResources + " ressources chargées");
  }
  
  
  private String instancierTagsObjet(Element elem) {
    for (int j = 0; j < elem.getElementsByTagName("tag").getLength(); j++) {
      Element tagElem = (Element) elem.getElementsByTagName("tag").item(j);
      String cle = tagElem.getAttribute("k");
      // cas du tag outil
      if (cle.equals(OsmGeneObj.TAG_OUTIL)) {
        /*String txt = tagElem.getAttribute("v");
        OsmCaptureTool outil = OsmCaptureTool.valueOfTexte(txt);
        //obj.setCaptureTool(outil);*/
        continue;
      }
      // cas du tag source
      if (cle.equals(OsmGeneObj.TAG_SOURCE)) {
        /*String txt = tagElem.getAttribute("v");
        //obj.setSource(txt);*/
        continue;
      }
      // autres tags
      // System.out.print(cle + ":" + tagElem.getAttribute("v") + ", ");
      if (cle.equals("amenity")) {
        if (tagElem.getAttribute("v") != null) {
          return tagElem.getAttribute("v");
        }
      }
      //obj.addTag(cle, tagElem.getAttribute("v"));*/
    }
    // System.out.println("");
    return "";
  }
  
}

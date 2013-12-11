package fr.ign.cogit.geoxygene.osm.importexport;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.RoleMembre;
import fr.ign.cogit.geoxygene.osm.importexport.OSMRelation.TypeRelation;
import fr.ign.cogit.geoxygene.osm.schema.OsmCaptureTool;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObj;

public class OsmResourceHandler extends DefaultHandler {

  private OSMLoader loader;
  private StringBuffer buffer;
  private Map<String, Boolean> elements = new HashMap<String, Boolean>();
  private DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private int i = 0;
  private OSMResource resource;
  private long nbResources;
  private ArrayList<Long> vertices;
  private List<OsmRelationMember> members;
  private TypeRelation type = TypeRelation.NON_DEF;;

  public OsmResourceHandler(OSMLoader loader, long nbResources) {
    super();
    this.loader = loader;
    this.nbResources = nbResources;
  }

  @Override
  public void startElement(String uri, String localName, String qName,
      Attributes attributes) throws SAXException {
    elements.put(qName, true);
    if (qName.equals("bounds")) {
      loader.xMin = Double.valueOf(attributes.getValue(OSMLoader.TAG_MIN_LAT));
      loader.xMax = Double.valueOf(attributes.getValue(OSMLoader.TAG_MAX_LAT));
      loader.yMin = Double.valueOf(attributes.getValue(OSMLoader.TAG_MIN_LON));
      loader.yMax = Double.valueOf(attributes.getValue(OSMLoader.TAG_MAX_LON));
    } else if (qName.equals("osm")) {
      // Do nothing, unused metadata
    } else if (qName.equals("node")) {
      // Sleep for up to one second.
      try {
        Thread.sleep(1);
      } catch (InterruptedException ignore) {
      }
      i++;
      resource = createResource(attributes);
      // on récupère sa géométrie
      double lat = Double.valueOf(attributes.getValue(OsmGeneObj.ATTR_LAT));
      double lon = Double.valueOf(attributes.getValue(OsmGeneObj.ATTR_LON));
      OSMNode geom = new OSMNode(lat, lon);
      resource.setGeom(geom);
      geom.setObjet(resource);
    } else if (qName.equals("way")) {
      // Sleep for up to one second.
      try {
        Thread.sleep(1);
      } catch (InterruptedException ignore) {
      }
      i++;
      resource = createResource(attributes);
      vertices = new ArrayList<Long>();
    } else if (qName.equals("relation")) {
      // Sleep for up to one second.
      try {
        Thread.sleep(1);
      } catch (InterruptedException ignore) {
      }
      i++;
      resource = createResource(attributes);
      members = new ArrayList<OsmRelationMember>();
    } else if (qName.equals("tag")) {
      // case of the relation type tag
      if (attributes.getValue("k").equals("type")) {
        type = TypeRelation.valueOfTexte(attributes.getValue("v"));
        if (resource.getGeom() instanceof OSMRelation)
          ((OSMRelation) resource.getGeom()).setType(type);
      }
      // special case: the tag created_by
      else if (attributes.getValue("k").equals("created_by")) {
        OsmCaptureTool outil = OsmCaptureTool.valueOfTexte(attributes
            .getValue("v"));
        resource.setCaptureTool(outil);
      }
      // special case: the tag source
      else if (attributes.getValue("k").equals("source")) {
        resource.setSource(attributes.getValue("v"));
      }
      resource.addTag(attributes.getValue("k"), attributes.getValue("v"));
    } else if (qName.equals("nd")) {
      long ref = Long.valueOf(attributes.getValue("ref"));
      vertices.add(ref);
    } else if (qName.equals("member")) {
      long ref = Long.valueOf(attributes.getValue("ref"));
      String role = attributes.getValue("role");
      boolean node = false;
      String type = attributes.getValue("type");
      if ("node".equals(type))
        node = true;
      members.add(new OsmRelationMember(RoleMembre.valueOfTexte(role), node,
          ref));
    } else {
      // do nothing for unknowns tags
    }
  }

  private OSMResource createResource(Attributes attributes) {
    // on récupère les attributs de l'élément
    long id = Long.valueOf(attributes.getValue(OsmGeneObj.ATTR_ID));
    String versionAttr = attributes.getValue(OsmGeneObj.ATTR_VERSION);
    int version = 1;
    if (versionAttr != null) {
      version = Integer.valueOf(versionAttr);
    }
    int changeSet = Integer.valueOf(attributes.getValue(OsmGeneObj.ATTR_SET));
    String contributeur = attributes.getValue(OsmGeneObj.ATTR_USER);
    int uid = 0;
    if (!contributeur.equals("")) {
      uid = Integer.valueOf(attributes.getValue(OsmGeneObj.ATTR_UID));
    }
    String timeStamp = attributes.getValue(OsmGeneObj.ATTR_DATE);
    Date date = null;
    try {
      date = formatDate.parse(timeStamp);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    // on construit le nouvel objet ponctuel
    OSMResource obj = new OSMResource(contributeur, null, id, changeSet,
        version, uid, date);

    return obj;
  }

  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    String lecture = new String(ch, start, length);
    if (buffer != null)
      buffer.append(lecture);
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException {

    if (qName.equals("node")) {
      loader.getNodes().add(resource);
      resource = null;
      loader.setProgressForBar(Math
          .min(100, (int) (i * 100 / this.nbResources)));
    } else if (qName.equals("way")) {
      loader.getWays().add(resource);
      resource = null;
      loader.setProgressForBar(Math
          .min(100, (int) (i * 100 / this.nbResources)));
    } else if (qName.equals("relation")) {
      loader.getRelations().add(resource);
      resource = null;
      loader.setProgressForBar(Math
          .min(100, (int) (i * 100 / this.nbResources)));
    } else if (qName.equals("nd")) {
      OSMWay geom = new OSMWay(vertices);
      resource.setGeom(geom);
      geom.setObjet(resource);
    } else if (qName.equals("tag")) {
      // do nothing
    } else if (qName.equals("member")) {
      OSMRelation rel = new OSMRelation(type, members);
      resource.setGeom(rel);
      rel.setObjet(resource);
    } else {
      // do nothing
    }
    elements.put(qName, false);
  }

}

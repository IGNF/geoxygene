/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.osm.schema;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import fr.ign.cogit.cartagen.core.defaultschema.GeneObjDefault;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.feature.AbstractFeature;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;

/**
 * OpenStreetMap implementation of the GeneObj schema of CartAGen.
 * @author GTouya
 * 
 */
public class OsmGeneObj extends GeneObjDefault {

  /**
   * The name of the last contributor for {@code this} feature.
   */
  private String contributor;
  private OsmSource source = OsmSource.UNKNOWN;
  private OsmCaptureTool captureTool = OsmCaptureTool.UNKNOWN;
  private long osmId;
  private int changeSet;
  /**
   * The version of the feature, i.e. 1 if it's just been created, 2 if someone
   * modified it once, etc.
   */
  private int version;
  /**
   * The user (i.e. contributor) id for this version of the feature.
   */
  private int uid;
  private Map<String, String> tags;
  /**
   * The date the contribution was added in OSM.
   */
  private Date date;

  // String values for the general tags of OSM data
  public static final String TAG_SOURCE = "source";
  public static final String TAG_OUTIL = "created_by";
  public static final String TAG_NODE = "node";
  public static final String TAG_WAY = "way";
  public static final String TAG_REL = "relation";
  public static final String ATTR_USER = "user";
  public static final String ATTR_DATE = "timestamp";
  public static final String ATTR_SET = "changeset";
  public static final String ATTR_ID = "id";
  public static final String ATTR_UID = "uid";
  public static final String ATTR_VERSION = "version";
  public static final String ATTR_LAT = "lat";
  public static final String ATTR_LON = "lon";

  public OsmGeneObj(String contributor, IGeometry geom, int id, int changeSet,
      int version, int uid, Date date) {
    super();
    this.contributor = contributor;
    this.geom = geom;
    this.setInitialGeom(geom);
    this.osmId = id;
    this.uid = uid;
    this.changeSet = changeSet;
    this.version = version;
    this.date = date;
    this.tags = new HashMap<String, String>();
  }

  public OsmGeneObj() {
    this.tags = new HashMap<String, String>();
  }

  public String getContributor() {
    return this.contributor;
  }

  public void setContributor(String contributor) {
    this.contributor = contributor;
  }

  public OsmSource getSource() {
    return this.source;
  }

  public void setSource(OsmSource source) {
    this.source = source;
  }

  public OsmCaptureTool getCaptureTool() {
    return this.captureTool;
  }

  public void setCaptureTool(OsmCaptureTool captureTool) {
    this.captureTool = captureTool;
  }

  public long getOsmId() {
    return this.osmId;
  }

  public void setOsmId(long id) {
    this.osmId = id;
  }

  public int getChangeSet() {
    return this.changeSet;
  }

  public void setChangeSet(int changeSet) {
    this.changeSet = changeSet;
  }

  public int getVersion() {
    return this.version;
  }

  public void setVersion(int version) {
    this.version = version;
  }

  public int getUid() {
    return this.uid;
  }

  public void setUid(int uid) {
    this.uid = uid;
  }

  public Map<String, String> getTags() {
    return this.tags;
  }

  public void setTags(Map<String, String> tags) {
    this.tags = tags;
  }

  public Date getDate() {
    return this.date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  @Override
  public int hashCode() {
    return this.id;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    OsmGeneObj other = (OsmGeneObj) obj;
    if (this.osmId != other.osmId) {
      return false;
    }
    if (this.id != other.id) {
      return false;
    }
    if (this.version != other.version) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "id: " + this.osmId + " v." + this.version;
  }

  /**
   * Add a tag and its value ot the tags of {@code this}.
   * @param cle
   * @param valeur
   */
  public void addTag(String cle, String valeur) {
    this.tags.put(cle, valeur);
  }

  @Override
  public Object getAttribute(String nomAttribut) {
    Object value = getTags().get(nomAttribut);
    if (value != null)
      return value;

    AttributeType attribute = new AttributeType();
    attribute.setNomField(nomAttribut);
    attribute.setMemberName(nomAttribut);
    if (attribute.getMemberName() != null
        && attribute.getMemberName().equals("geom")) { //$NON-NLS-1$
      return this.getGeom();
    }
    if (attribute.getMemberName() != null
        && attribute.getMemberName().equals("topo")) { //$NON-NLS-1$
      return this.getTopo();
    }
    if (attribute.getMemberName() != null
        && attribute.getMemberName().equals("id")) { //$NON-NLS-1$
      return this.getId();
    }
    Object valeur = null;
    String nomFieldMaj = null;
    if (((AttributeType) attribute).getNomField().length() == 0) {
      nomFieldMaj = ((AttributeType) attribute).getNomField();
    } else {
      nomFieldMaj = Character.toUpperCase(((AttributeType) attribute)
          .getNomField().charAt(0))
          + ((AttributeType) attribute).getNomField().substring(1);
    }
    String nomGetFieldMethod = "get" + nomFieldMaj; //$NON-NLS-1$
    Class<?> classe = this.getClass();
    while (!classe.equals(Object.class)) {
      try {
        Method methodGetter = classe.getDeclaredMethod(nomGetFieldMethod,
            (Class[]) null);
        valeur = methodGetter.invoke(this, (Object[]) null);
        return valeur;
      } catch (SecurityException e) {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger
              .debug("SecurityException pendant l'appel de la méthode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (IllegalArgumentException e) {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger
              .debug("IllegalArgumentException pendant l'appel de la méthode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (NoSuchMethodException e) {
        // if (logger.isDebugEnabled())
        // logger.debug("La m�thode "+nomGetFieldMethod+" n'existe pas dans la classe "+classe);
      } catch (IllegalAccessException e) {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger
              .debug("IllegalAccessException pendant l'appel de la méthode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (InvocationTargetException e) {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger
              .debug("InvocationTargetException pendant l'appel de la méthode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      }
      classe = classe.getSuperclass();
    }
    // réessayer si le getter est du genre isAttribute, ie pour un booléen
    nomGetFieldMethod = "is" + nomFieldMaj; //$NON-NLS-1$
    classe = this.getClass();
    while (!classe.equals(Object.class)) {
      try {
        Method methodGetter = classe.getDeclaredMethod(nomGetFieldMethod,
            (Class[]) null);
        valeur = methodGetter.invoke(this, (Object[]) null);
        return valeur;
      } catch (SecurityException e) {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger
              .debug("SecurityException pendant l'appel de la méthode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (IllegalArgumentException e) {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger
              .debug("IllegalArgumentException pendant l'appel de la méthode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (NoSuchMethodException e) {
        // if (logger.isDebugEnabled())
        // logger.debug("La méthode "+nomGetFieldMethod+" n'existe pas dans la classe "+classe);
      } catch (IllegalAccessException e) {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger
              .debug("IllegalAccessException pendant l'appel de la méthode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      } catch (InvocationTargetException e) {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger
              .debug("InvocationTargetException pendant l'appel de la méthode "
                  + nomGetFieldMethod + " sur la classe " + classe);
        }
      }
      classe = classe.getSuperclass();
    }

    return null;
  }

}

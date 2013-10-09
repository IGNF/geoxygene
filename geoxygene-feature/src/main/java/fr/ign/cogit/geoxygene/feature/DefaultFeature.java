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

package fr.ign.cogit.geoxygene.feature;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.api.spatial.toporoot.ITopology;

/**
 * 
 * Feature générique. Les attributs sont représentés dans une table et ne
 * peuvent pas être accèdés autrement -(pas de getter ou setter spécifique à un
 * attribut comme getNbVoies() pour les TronconRoute.java par exemple).
 * 
 * Un defaultFeature est cependant associé à un FeatureType avec toutes les
 * descriptions des ses attributs, types de valeurs etc. C'est au développeur de
 * s'assurer que le defaultFeature reste conforme à la définition de son
 * featureType. Au premier chargement, s'il n'y a pas de featuretype renseigné,
 * un nouveau featureType est généré automatiquement grâce aux colonnes de la
 * table. Mais cela ne constitue pas un schéma conceptuel, il doit donc être
 * précisé manuellement dès que possible pour les utilisations ultérieures
 * (notamment pour identifier les relatios entre objets etc.)
 * 
 * Stockage et chargement par OJB : un seul mapping pour toutes les tables
 * 
 * Ou plus simple : chargement par JDBC en utilisant soit une connexion neuve
 * soit la java.sql.Connexion de Geodatabase (Geodatabase.getConnexion())
 * 
 * @author Sandrine Balley
 * @author Nathalie Abadie
 * @author Julien Perret
 */

public class DefaultFeature extends AbstractFeature {
    
    /** Logger. */
    protected static final Logger LOGGER = Logger.getLogger(DefaultFeature.class.getName());
    
  /**
   * Constructeur vide
   */
  public DefaultFeature() {
    super();
  }

  /**
   * Constructeur à partir d'une géométrie
   * @param geometry géométrie de l'objet
   */
  public DefaultFeature(IGeometry geometry) {
    super();
    this.setGeom(geometry);
  }

  /**
   * The Schema is not copied (so that when you copy a feature collection, each
   * feature does not have a different schema.
   * <p>
   * Only a few attribute type are handled for now.
   * <ul>
   * <li>Double
   * <li>Integer
   * <li>String
   * </ul>
   * @param original the default feature to copy
   */
  public DefaultFeature(final DefaultFeature original) {
    super();
    this.setId(original.getId());
    this.setGeom((IGeometry) original.getGeom().clone());
    this.setAttributes(new Object[original.getAttributes().length]);
    for (int i = 0; i < original.getAttributes().length; i++) {
      Object attribute = original.getAttribute(i);
      if (Double.class.isAssignableFrom(attribute.getClass())) {
        Double n = (Double) attribute;
        this.attributes[i] = new Double(n);
      }
      if (Integer.class.isAssignableFrom(attribute.getClass())) {
        Integer n = (Integer) attribute;
        this.attributes[i] = new Integer(n);
      }
      if (String.class.isAssignableFrom(attribute.getClass())) {
        String n = (String) attribute;
        this.attributes[i] = new String(n);
      }
    }
  }

  // private FeatureType featureType;
  /**
   * nom table et colonnes. contient une "lookup table" reliant le numéro de
   * l'attribut dans la table attributes[] du defaultFeature, son nom de colonne
   * et son nom d'attributeType.
   */
  private SchemaDefaultFeature schema;
  private Object[] attributes;

  /**
   * Renvoie un tableau contenant les valeurs des attributs de l'objet
   * @return un tableau contenant les valeurs des attributs de l'objet
   */
  public Object[] getAttributes() {
    return this.attributes;
  }

  /**
   * Renvoie l'attribut de position <code>n</code> dans le tableau d'attributs
   * @param rang le rang de l'attribut
   * @return l'attribut de position <code>n</code> dans le tableau d'attributs
   */
  public Object getAttribute(int rang) {
    return this.attributes[rang];
  }

  @Override
  public Object getAttribute(String nom) {
    if (nom.equals("geom")) { //$NON-NLS-1$
      return this.getGeom();
    }
    if (nom.equals("topo")) { //$NON-NLS-1$
      return this.getTopo();
    }
    if (nom.equals("id")) { //$NON-NLS-1$
      return this.getId();
    }
    
    /**
     * on regarde en priorité si le nom correspond à un nom d'attributeType
     * (métadonnées de niveau conceptuel)
     */
    String[] tabNoms;
    if (this.getSchema() != null) {
      for (Integer key : this.getSchema().getAttLookup().keySet()) {
        tabNoms = this.getSchema().getAttLookup().get(key);
        if ((tabNoms != null) && (tabNoms[1] != null)
            && (tabNoms[1].equals(nom))) {
          return this.getAttribute(key.intValue());
        }
      }
      /**
       * si on n'a pas trouvé au niveau conceptuel, on regarde s'il correspond à
       * un nom de colonne (métadonnées de niveau logique)
       */
      for (Integer key : this.getSchema().getAttLookup().keySet()) {
        tabNoms = this.getSchema().getAttLookup().get(key);
        if ((tabNoms != null) && (tabNoms[0] != null)
            && (tabNoms[0].equals(nom))) {
          return this.getAttribute(key.intValue());
        }
      }
    }
    if (LOGGER.isDebugEnabled()) {
        LOGGER.warn("!!! le nom '" + nom + "' ne correspond pas à un attribut de ce feature !!!"); //$NON-NLS-1$//$NON-NLS-2$
    }
    return null;
  }

  @Override
  public Object getAttribute(GF_AttributeType attribute) {
    return this.getAttribute(attribute.getMemberName());
  }

  /**
   * @param attributes the attributes to set
   */
  public void setAttributes(Object[] attributes) {
    this.attributes = attributes;
  }

  /**
   * met la valeur value dans la case rang de la table d'attributs. Pour éviter
   * toute erreur, mieux vaut utiliser setAttribute(String nom, Object value)
   * qui va chercher dans le schema l'emplacement correct de l'attribut.
   * @param rang
   * @param value
   */
  public void setAttribute(int rang, Object value) {
    this.attributes[rang] = value;
  }

  /**
   * Va voir dans la lookup table de feature.schéma dans quelle case se place
   * l'attribut puis le met dans la table d'attributs.
   * @param nom nom de l'attribut
   * @param value valeur à affecter à l'attribut
   */
  public void setAttribute(String nom, Object value) {
    /*
     * on regarde en priorité si le nom correspond à un nom d'attributeType
     * (métadonnées de niveau conceptuel)
     */
    String[] tabNoms;
    for (Integer key : this.getSchema().getAttLookup().keySet()) {
      tabNoms = this.getSchema().getAttLookup().get(key);
      if ((tabNoms != null) && (tabNoms[1] != null)) {
        if (tabNoms[1].equals(nom)) {
          this.setAttribute(key.intValue(), value);
          return;
        }
      }
    }
    /*
     * si on n'a pas trouvé au niveau conceptuel, on regarde s'il correspond à
     * un nom de colonne (métadonnées de niveau logique)
     */
    for (Integer key : this.getSchema().getAttLookup().keySet()) {
      tabNoms = this.getSchema().getAttLookup().get(key);
      if ((tabNoms != null) && (tabNoms[0] != null)) {
        if (tabNoms[0].equals(nom)) {
          if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("setAttribute " + nom + " =?= " + tabNoms[0]); //$NON-NLS-1$//$NON-NLS-2$
          }
          this.setAttribute(key.intValue(), value);
          return;
        }
      }
    }
    if (LOGGER.isDebugEnabled()) {
        LOGGER.warn("!!! le nom '" + nom + "' ne correspond pas à un attribut de ce feature !!!"); //$NON-NLS-1$ //$NON-NLS-2$
      for (Integer key : this.getSchema().getAttLookup().keySet()) {
        tabNoms = this.getSchema().getAttLookup().get(key);
        if (tabNoms == null) {
          LOGGER.debug("Attribut " + key + " nul"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
          LOGGER.debug("Attribut " + key + " = " + tabNoms[0] + " - " + tabNoms[1]); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
        }
      }
    }
    return;
  }

  /**
   * @return the table
   */
  public SchemaDefaultFeature getSchema() {
    return this.schema;
  }

  /**
   * @param schema
   */
  public void setSchema(SchemaDefaultFeature schema) {
    this.schema = schema;
  }

  @Override
  public void setAttribute(GF_AttributeType attribute, Object valeur) {
    // FIXME changer le comportement !!!!
    if (attribute.getMemberName().equals("geom")) { //$NON-NLS-1$
      AbstractFeature
          .getLogger()
          .warn(
              "WARNING : Pour affecter la primitive géométrique par défaut, veuillez utiliser " //$NON-NLS-1$
                  + "la méthode FT_Feature.getGeom() et non pas MdFeature.getAttribute(AttributeType attribute)"); //$NON-NLS-1$
      this.setGeom((IGeometry) valeur);
    } else if (attribute.getMemberName().equals("topo")) { //$NON-NLS-1$
      AbstractFeature
          .getLogger()
          .warn(
              "WARNING : Pour affecter la primitive topologique par défaut, veuillez utiliser " //$NON-NLS-1$
                  + "la méthode FT_Feature.getTopo() et non pas MdFeature.getAttribute(AttributeType attribute)"); //$NON-NLS-1$
      this.setTopo((ITopology) valeur);
    } else {
      this.setAttribute(attribute.getMemberName(), valeur);
      /*
       * try { String nomFieldMaj2; if (attribute.getNomField().length() == 0)
       * {nomFieldMaj2 = attribute.getNomField();} else {nomFieldMaj2 =
       * Character
       * .toUpperCase(attribute.getNomField().charAt(0))+attribute.getNomField
       * ().substring(1);} String nomSetFieldMethod = "set" + nomFieldMaj2;
       * Method methodSetter =
       * this.getClass().getDeclaredMethod(nomSetFieldMethod,
       * valeur.getClass()); // Method methodGetter = //
       * this.getClass().getSuperclass().getDeclaredMethod( //
       * nomGetFieldMethod, // null); valeur = methodSetter.invoke(this,
       * valeur); } catch (SecurityException e) { e.printStackTrace(); } catch
       * (IllegalArgumentException e) { e.printStackTrace(); } catch
       * (NoSuchMethodException e) { e.printStackTrace(); } catch
       * (IllegalAccessException e) { e.printStackTrace(); } catch
       * (InvocationTargetException e) { e.printStackTrace(); }
       */
    }
  }

  @Override
  public IFeature cloneGeom() throws CloneNotSupportedException {
    DefaultFeature clone = new DefaultFeature((IGeometry) this.getGeom()
        .clone());
    clone.setSchema(this.getSchema());
    clone.setAttributes(this.getAttributes());
    clone.setFeatureType(this.getFeatureType());

    return clone;
  }
@Override
public String toString() {
  String result = "" + this.getId();// + " - " + this.getGeom();
  return result;
}
}

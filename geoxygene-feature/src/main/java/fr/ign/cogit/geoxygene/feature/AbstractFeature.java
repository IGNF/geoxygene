/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut G�ographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut G�ographique National
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.api.feature.IFeature;
import fr.ign.cogit.geoxygene.api.feature.IFeatureCollection;
import fr.ign.cogit.geoxygene.api.feature.IPopulation;
import fr.ign.cogit.geoxygene.api.feature.Representation;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IEnvelope;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.api.spatial.toporoot.ITopology;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationRole;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;

/**
 * @author julien Gaffuri 6 juil. 2009
 * 
 */
public abstract class AbstractFeature implements IFeature {
  /**
   * Logger.
   */
  protected static final Logger logger = Logger.getLogger(AbstractFeature.class
      .getName());

  /**
   * @return the logger
   */
  public static final Logger getLogger() {
    return AbstractFeature.logger;
  }

  public AbstractFeature() {
  }

  public AbstractFeature(IGeometry geometry) {
    this.geom = geometry;
  }

  /**
   * Constructeur par défaut d'AbstractFeature Le nouveau feature n'appartient
   * pas a la création aux mêmes featureCollections que son parent ni a la même
   * population.
   * 
   * nb : La copie de la resprésentation et topologie est à faire.
   * 
   * @param feature
   */
  public AbstractFeature(IFeature feature) {
    this.id = Integer.MAX_VALUE;
    this.setCorrespondants(feature.getCorrespondants());
    this.estSupprime = feature.isDeleted();
    this.featurecollections = null;
    this.representation = null;
    this.featureType = feature.getFeatureType();
    this.geom = (IGeometry) feature.getGeom().clone();
    this.population = null;
    this.topo = null;
  }

  protected int id;

  @Override
  @Id
  @GeneratedValue
  public int getId() {
    return this.id;
  }

  @Override
  public void setId(int Id) {
    this.id = Id;
  }

  protected IGeometry geom = null;

  @Override
  public IGeometry getGeom() {
    return this.geom;
  }

  @Override
  @SuppressWarnings("unchecked")
  public void setGeom(IGeometry g) {
    if (g == null) {
      this.geom = null;
      return;
    }
    IGeometry previousGeometry = this.geom;
    boolean hasPreviousGeometry = (previousGeometry != null);
    this.geom = g;
    synchronized (this.featurecollections) {
      IFeatureCollection<IFeature>[] collections = this.featurecollections
          .toArray(new IFeatureCollection[0]);
      for (IFeatureCollection<IFeature> fc : collections) {
        if (fc.hasSpatialIndex()) {
          if (fc.getSpatialIndex().hasAutomaticUpdate()) {
            if (hasPreviousGeometry) {
              fc.getSpatialIndex().update(this, (g != null) ? 0 : -1);
            } else {
              fc.getSpatialIndex().update(this, 1);
            }
          }
        }
        /*
         * if ((previousGeometry != null) && ((this.geom == null) ||
         * !previousGeometry.equals(this.geom))) { fc.fireActionPerformed(new
         * FeatureCollectionEvent(fc, this, FeatureCollectionEvent.Type.CHANGED,
         * previousGeometry)); }
         */
      }
    }
  }

  @Override
  public boolean hasGeom() {
    return (this.geom != null);
  }

  protected ITopology topo = null;

  @Override
  public ITopology getTopo() {
    return this.topo;
  }

  @Override
  public void setTopo(ITopology t) {
    this.topo = t;
  }

  @Override
  public boolean hasTopo() {
    return (this.topo != null);
  }

  /** Lien n-m bidirectionnel vers IFeatureCollection. */
  private List<IFeatureCollection<IFeature>> featurecollections = new ArrayList<IFeatureCollection<IFeature>>(
      0);

  @Override
  public List<IFeatureCollection<IFeature>> getFeatureCollections() {
    return this.featurecollections;
  }

  @Override
  public IFeatureCollection<IFeature> getFeatureCollection(int i) {
    return this.featurecollections.get(i);
  }

  /**
   * Lien bidirectionnel n-m des éléments vers eux-mêmes. Les méthodes get (sans
   * indice) et set sont nécessaires au mapping. Les autres méthodes sont là
   * seulement pour faciliter l'utilisation de la relation. ATTENTION: Pour
   * assurer la bidirection, il faut modifier les listes uniquement avec ces
   * methodes. NB: si il n'y a pas d'objet en relation, la liste est vide mais
   * n'est pas "null". Pour casser toutes les relations, faire setListe(new
   * ArrayList()) ou emptyListe().
   */
  private final List<IFeature> correspondants = new ArrayList<IFeature>(0);

  @Override
  public List<IFeature> getCorrespondants() {
    return this.correspondants;
  }

  public String getCorrespondantsAsString() {
    String result = "";
    for (IFeature f : this.correspondants) {
      if (!result.isEmpty()) {
        result += ",";
      }
      result += f.getId();
    }
    return result;
  }

  public int getSizeCorrespondants() {
    return this.correspondants.size();
  }

  @Override
  public void setCorrespondants(List<IFeature> L) {
    List<IFeature> old = new ArrayList<IFeature>(this.correspondants);
    for (IFeature O : old) {
      this.correspondants.remove(O);
      O.getCorrespondants().remove(this);
    }
    for (IFeature O : L) {
      this.correspondants.add(O);
      O.getCorrespondants().add(this);
    }
  }

  @Override
  public IFeature getCorrespondant(int i) {
    if (this.correspondants.size() == 0) {
      return null;
    }
    return this.correspondants.get(i);
  }

  @Override
  public void addCorrespondant(IFeature O) {
    if (O == null) {
      return;
    }
    this.correspondants.add(O);
    O.getCorrespondants().add(this);
  }

  @Override
  public void removeCorrespondant(IFeature O) {
    if (O == null) {
      return;
    }
    this.correspondants.remove(O);
    O.getCorrespondants().remove(this);
  }

  @Override
  public void clearCorrespondants() {
    for (IFeature O : this.correspondants) {
      O.getCorrespondants().remove(this);
    }
    this.correspondants.clear();
  }

  @Override
  public void addAllCorrespondants(Collection<IFeature> c) {
    for (IFeature feature : c) {
      this.addCorrespondant(feature);
    }
  }

  @Override
  public List<IFeature> getCorrespondants(
      IFeatureCollection<? extends IFeature> pop) {
    List<? extends IFeature> elementsPop = pop.getElements();
    List<IFeature> resultats = new ArrayList<IFeature>(this.getCorrespondants());
    resultats.retainAll(elementsPop);
    return resultats;
  }

  // //////////////////////////////////////////////////////////////////////////
  // /////
  /**
   * méthodes issues de MdFeature : Permettent de créer un Feature dont les
   * propriétés (valeurs d'attributs, opérations et objets en relation) peuvent
   * être accedés de façon générique en mentionnant le nom de la propriété. Pour
   * permettre cela chaque feature est rattaché à son featureType.
   */
  // //////////////////////////////////////////////////////////////////////////
  // /////
  /**
   * Creation d'un feature du type donné en paramètre, par exemple Route.
   * L'objet crée sera alors une instance de bdcarto.TronconRoute (l'element de
   * schéma logique correspondant au featureType Route) qui étend FeatureCommun.
   * Les valeurs d'attributs ne sont pas initialisées.
   * 
   * @param featureType le feature type de l'objet à créer
   * @return l'objet créé
   */
  public static IFeature createTypedFeature(FeatureType featureType) {
    IFeature feature = null;
    try {
      Class<?> theClass = Class.forName(featureType.getNomClasse());
      feature = (IFeature) theClass.getConstructor().newInstance();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return feature;
  }

  /**
   * L'unique population à laquelle appartient cet objet.
   */
  protected IPopulation<? extends IFeature> population;

  @Override
  @SuppressWarnings("unchecked")
  public IPopulation<? extends IFeature> getPopulation() {
    if (this.population != null) {
      return this.population;
    }
    synchronized (this.featurecollections) {
      IFeatureCollection<IFeature>[] collections = this.featurecollections
          .toArray(new IFeatureCollection[0]);
      for (IFeatureCollection<IFeature> f : collections) {
        if (f instanceof Population<?>) {
          return (Population<IFeature>) f;
        }
      }
    }
    return null;
  }

  @Override
  public void setPopulation(IPopulation<? extends IFeature> population) {
    this.population = population;
    // Refuse d'écrire dans ma population car ne peut pas pas vérifier si
    // this hérite bien de IFeature...
    // this.population.addUnique(this);
  }

  /**
   * L'unique featureType auquel appartient cet objet.
   */
  protected GF_FeatureType featureType = null;

  @Override
  public void setFeatureType(GF_FeatureType featureType) {
    this.featureType = featureType;
  }

  @Override
  public GF_FeatureType getFeatureType() {
    if ((this.featureType == null) && (this.getPopulation() != null)) {
      return this.getPopulation().getFeatureType();
    }
    return this.featureType;
  }

  @Override
  public Object getAttribute(GF_AttributeType attribute) {
    if (attribute.getMemberName() != null
        && attribute.getMemberName().equals("geom")) { //$NON-NLS-1$
      return this.getGeom();
    }
    if (attribute.getMemberName() != null
        && attribute.getMemberName().equals("topo")) { //$NON-NLS-1$
      AbstractFeature.logger
          .warn("WARNING : Pour Récupèrer la primitive topologique par défaut, veuillez utiliser " //$NON-NLS-1$
              + "la méthode IFeature.getTopo() et non pas MdFeature.getAttribute(AttributeType attribute)"); //$NON-NLS-1$
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
    AbstractFeature.logger.error("Echec de l'appel à la méthode "
        + nomGetFieldMethod + " sur la classe " + this.getClass());
    return null;
  }

  @Override
  public void setAttribute(GF_AttributeType attribute, Object valeur) {
    if (attribute.getMemberName().equals("geom")) { //$NON-NLS-1$
      AbstractFeature.logger
          .warn("WARNING : Pour affecter la primitive géométrique par défaut, veuillez utiliser " //$NON-NLS-1$
              + "la méthode IFeature.getGeom() et non pas MdFeature.getAttribute(AttributeType attribute)"); //$NON-NLS-1$
      this.setGeom((IGeometry) valeur);
    } else if (attribute.getMemberName().equals("topo")) { //$NON-NLS-1$
      AbstractFeature.logger
          .warn("WARNING : Pour affecter la primitive topologique par défaut, veuillez utiliser " //$NON-NLS-1$
              + "la méthode IFeature.getTopo() et non pas MdFeature.getAttribute(AttributeType attribute)"); //$NON-NLS-1$
      this.setTopo((ITopology) valeur);
    } else {
      String nomFieldMaj2;
      if (((AttributeType) attribute).getNomField().isEmpty()) {
        AbstractFeature.logger.warn("Empty Attribute Name");
        return;
      } else {
        nomFieldMaj2 = Character.toUpperCase(((AttributeType) attribute)
            .getNomField().charAt(0))
            + ((AttributeType) attribute).getNomField().substring(1);
      }
      String nomSetFieldMethod = "set" + nomFieldMaj2; //$NON-NLS-1$
      try {
        Method methodSetter = this.getClass().getDeclaredMethod(
            nomSetFieldMethod, valeur.getClass());
        valeur = methodSetter.invoke(this, valeur);
      } catch (SecurityException e) {
        e.printStackTrace();
      } catch (IllegalArgumentException e) {
        e.printStackTrace();
      } catch (NoSuchMethodException e) {
        try {
          Method methodSetter = this.getClass().getDeclaredMethod(
              nomSetFieldMethod,
              AbstractFeature.class2PrimitiveClass(valeur.getClass()));
          methodSetter.invoke(this, valeur);
        } catch (Exception e1) {
          e1.printStackTrace();
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<? extends IFeature> getRelatedFeatures(GF_FeatureType ftt,
      GF_AssociationRole role) {
    List<IFeature> listResult = new ArrayList<IFeature>();
    if (AbstractFeature.logger.isDebugEnabled()) {
      AbstractFeature.logger.debug("\n**recherche des features en relation**");
    }
    try {
      // cas 1-1 ou 1-N ou N-1 où il n'y a pas de classe association
      if (((AssociationRole) role).getNomFieldAsso() == null) {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger.debug("pas de classe association");
        }
        String nomFieldClasseMaj;
        if (((AssociationRole) role).getNomFieldClasse().length() == 0) {
          nomFieldClasseMaj = ((AssociationRole) role).getNomFieldClasse();
        } else {
          nomFieldClasseMaj = Character.toUpperCase(((AssociationRole) role)
              .getNomFieldClasse().charAt(0))
              + ((AssociationRole) role).getNomFieldClasse().substring(1);
        }
        String nomGetMethod = "get" + nomFieldClasseMaj; //$NON-NLS-1$
        Method methodGetter = this.getClass().getDeclaredMethod(nomGetMethod,
            (Class[]) null);
        // Method methodGetter =
        // this.getClass().getSuperclass().getDeclaredMethod(
        // nomGetFieldMethod,
        // null);
        Object objResult = methodGetter.invoke(this, (Object[]) null);
        if (objResult instanceof IFeature) {
          listResult.add((IFeature) objResult);
        } else if (objResult instanceof List) {
          listResult.addAll((List<IFeature>) objResult);
        }
      }
      // cas ou il y a une classe association
      else {
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger.debug("classe association : "
              + role.getAssociationType().getTypeName());
        }
        List<IFeature> listInstancesAsso = new ArrayList<IFeature>();
        // je vais chercher les instances de classe-association
        String nomFieldClasseMaj;
        if (((AssociationRole) role).getNomFieldClasse().length() == 0) {
          nomFieldClasseMaj = ((AssociationRole) role).getNomFieldClasse();
        } else {
          nomFieldClasseMaj = Character.toUpperCase(((AssociationRole) role)
              .getNomFieldClasse().charAt(0))
              + ((AssociationRole) role).getNomFieldClasse().substring(1);
        }
        String nomGetMethod = "get" + nomFieldClasseMaj; //$NON-NLS-1$
        Method methodGetter = this.getClass().getDeclaredMethod(nomGetMethod,
            (Class[]) null);
        String nomClasseAsso = ((AssociationType) role.getAssociationType())
            .getNomClasseAsso();
        Class<?> classeAsso = Class.forName(nomClasseAsso);
        if (AbstractFeature.logger.isDebugEnabled()) {
          AbstractFeature.logger.debug("cardMax de " + role.getMemberName()
              + " = " + role.getCardMax());
        }
        if (!role.getCardMax().equals("1")) {
          if (AbstractFeature.logger.isDebugEnabled()) {
            AbstractFeature.logger.debug("invocation de "
                + methodGetter.getName() + " sur le feature " + this.getId());
          }
          listInstancesAsso.addAll((List<IFeature>) methodGetter.invoke(this,
              (Object[]) null));
          if (AbstractFeature.logger.isDebugEnabled()) {
            AbstractFeature.logger.debug("nb instances d'association = "
                + listInstancesAsso.size());
          }
        } else {
          listInstancesAsso.add((IFeature) methodGetter.invoke(this,
              (Object[]) null));
          if (AbstractFeature.logger.isDebugEnabled()) {
            AbstractFeature.logger.debug("nb instances d'association = "
                + listInstancesAsso.size());
          }
        }
        // je cherche le (ou les) role(s) allant de l'association à
        // l'autre featureType
        List<GF_AssociationRole> listRoles = role.getAssociationType().getRoles();
        listRoles.remove(role);
        List<GF_AssociationRole> listRolesAGarder = role.getAssociationType().getRoles();
        listRolesAGarder.remove(role);
        for (int i = 0; i < listRoles.size(); i++) {
          if (!((AssociationRole) listRoles.get(i)).getFeatureType()
              .equals(ftt)) {
            listRolesAGarder.remove(listRoles.get(i));
          }
        }
        /**
         * pour chaque role concerné (il peut y en avoir plus d'un) je vais
         * chercher les instances en relation
         */
        AssociationRole roleExplore;
        for (int i = 0; i < listRolesAGarder.size(); i++) {
          roleExplore = (AssociationRole) listRolesAGarder.get(i);
          if (AbstractFeature.logger.isDebugEnabled()) {
            AbstractFeature.logger.debug("role exploré = "
                + roleExplore.getMemberName());
          }
          String nomFieldAssoMaj;
          if (roleExplore.getNomFieldAsso().length() == 0) {
            nomFieldAssoMaj = roleExplore.getNomFieldAsso();
          } else {
            nomFieldAssoMaj = Character.toUpperCase(roleExplore
                .getNomFieldAsso().charAt(0))
                + roleExplore.getNomFieldAsso().substring(1);
          }
          nomGetMethod = "get" + nomFieldAssoMaj;
          methodGetter = classeAsso.getDeclaredMethod(nomGetMethod,
              (Class[]) null);
          if (AbstractFeature.logger.isDebugEnabled()) {
            AbstractFeature.logger
                .debug("methode de la classe-asso pour recuperer les instances en relation = "
                    + methodGetter.getName());
          }
          /**
           * je vais chercher les objets en relation via chaque instance de
           * classe-association
           */
          for (int j = 0; j < listInstancesAsso.size(); j++) {
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger.debug("j = " + j);
            }
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger.debug("instance = "
                  + listInstancesAsso.get(j).getId());
            }
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger.debug("class  "
                  + listInstancesAsso.get(j).getClass());
            }
            if (AbstractFeature.logger.isDebugEnabled()) {
              AbstractFeature.logger.debug(methodGetter.invoke(
                  listInstancesAsso.get(j), (Object[]) null));
            }
            Object objResult = methodGetter.invoke(listInstancesAsso.get(j),
                (Object[]) null);
            if (objResult instanceof IFeature) {
              listResult.add((IFeature) objResult);
            } else if (objResult instanceof List) {
              listResult.addAll((List<IFeature>) objResult);
            }
          }
        }
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    if (AbstractFeature.logger.isDebugEnabled()) {
      AbstractFeature.logger
          .debug("\n**fin de la recherche des features en relation**");
    }
    return listResult;
  }

  // //////////////////////////////////////////////////////////////////////////
  // /
  // ///////////////// Ajout Nathalie
  // //////////////////////////////////////////////////////////////////////////
  // /

  @Override
  public Object getAttribute(String nomAttribut) {
    GF_FeatureType ft = this.getFeatureType();
    if (ft == null) {
      AttributeType type = new AttributeType();
      type.setNomField(nomAttribut);
      type.setMemberName(nomAttribut);
      // FIXME c'est un peu une bidouille
      return this.getAttribute(type);
    }
    GF_AttributeType attribute = ft.getFeatureAttributeByName(nomAttribut);
    if (attribute == null) {
      AttributeType type = new AttributeType();
      type.setNomField(nomAttribut);
      type.setMemberName(nomAttribut);
      // FIXME c'est un peu une bidouille
      return this.getAttribute(type);
      // logger.error("Le FeatureType correspondant à ce IFeature est
      // introuvable: Impossible de remonter au AttributeType à partir de
      // son nom.");
      // return null;
    }
    return (this.getAttribute(attribute));
  }

  @Override
  public List<? extends IFeature> getRelatedFeatures(String nomFeatureType,
      String nomRole) {
    // Initialisation de la liste des résultats
    List<? extends IFeature> listResultats = null;
    // On récupère le featuretype nommé nomFeatureType
    GF_FeatureType ftt = ((FeatureType) this.getFeatureType()).getSchema()
        .getFeatureTypeByName(nomFeatureType);
    // On récupère l'AssociationRole nommé nomRole
    AssociationRole role = null;
    List<GF_AssociationRole> listeRoles = ftt.getRoles();
    for (GF_AssociationRole r : listeRoles) {
      if (r.getMemberName().equalsIgnoreCase(nomRole)) {
        role = (AssociationRole) r;
      } else {
        continue;
      }
    }
    if ((ftt == null) || (role == null)) {
      AbstractFeature.logger
          .error("Le FeatureType "
              + nomFeatureType
              + " ou l'AssociationRole "
              + nomRole
              + " est introuvable. Impossible de calculer les IFeature en relation!");
      return null;
    }
    listResultats = this.getRelatedFeatures(ftt, role);
    return listResultats;
  }

  /**
   * La sémiologie de l'objet géographique
   */
  private Representation representation = null;

  @Override
  public Representation getRepresentation() {
    return this.representation;
  }

  @Override
  public void setRepresentation(Representation rep) {
    this.representation = rep;
  }

  /**
   * marqueur de suppression d'un objet (utilisé par exemple en généralisation)
   */
  private boolean estSupprime = false;

  @Override
  public boolean isDeleted() {
    return this.estSupprime;
  }

  @Override
  public void setDeleted(boolean estSupprime) {
    this.estSupprime = estSupprime;
  }

  @Override
  public boolean intersecte(IEnvelope env) {
    if (this.getGeom() == null || env == null) {
      return false;
    }
    return this.getGeom().envelope().intersects(env);
  }

  @Override
  public String toString() {
    return this.getClass().getName() + " " + this.getGeom(); //$NON-NLS-1$
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || this.getClass() != obj.getClass()) {
      return false;
    }
    AbstractFeature other = (AbstractFeature) obj;
    if (this.getId() != other.getId()) {
      return false;
    }
    if (this.getGeom() != null && other.getGeom() != null) {
      if (this.getGeom().coord().size() != other.getGeom().coord().size()) {
        return false;
      }
      for (int i = 0; i < this.getGeom().coord().size(); i++) {
        if (this.getGeom().coord().get(i) != other.getGeom().coord().get(i)) {
          return false;
        }
      }

      // les tests ont été passés avec succès
      return true;
    }

    // Une des 2 géométries est nulle a priori faux ....
    return false;
  }

  /**
   * XXX HashCode might not be the feature id.
   */
  // @Override
  // public int hashCode() {
  // return this.getId();
  // }

  /**
   * Renvoie la classe primitive correspondant à la classe donnée, null si le
   * paramètre ne correspond pas à un type primitif ou s'il n'est pas géré.
   * 
   * @param classe classe
   * @return la classe correspondant à un type primitif ou null si le paramètre
   *         ne correspond pas à un type primitif ou s'il n'est pas géré.
   */
  public static Class<?> class2PrimitiveClass(Class<?> classe) {
    String simpleName = classe.getSimpleName();
    if (simpleName.equalsIgnoreCase("integer")) { //$NON-NLS-1$
      return int.class;
    }
    if (simpleName.equalsIgnoreCase("double")) { //$NON-NLS-1$
      return double.class;
    }
    if (simpleName.equalsIgnoreCase("long")) { //$NON-NLS-1$
      return long.class;
    }
    if (simpleName.equalsIgnoreCase("boolean")) { //$NON-NLS-1$
      return boolean.class;
    }
    return null;
  }

}

/*
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO specifications for
 * the development and deployment of geographic (GIS) applications. It is a open source
 * contribution of the COGIT laboratory at the Institut Géographique National (the French
 * National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this library (see file LICENSE if present); if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 */

package fr.ign.cogit.geoxygene.feature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.feature.event.FeatureCollectionEvent;
import fr.ign.cogit.geoxygene.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationRole;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AssociationType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.AttributeType;
import fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu.FeatureType;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.geomroot.GM_Object;
import fr.ign.cogit.geoxygene.spatial.toporoot.TP_Object;

/**
 * Classe mère pour toute classe d'éléments ayant une r�alit� Géographique. Par
 * défaut, porte une géométrie et une topologie, qui peuvent être nulles.
 * 
 * <P>
 * TODO : ne plus porter de geometrie ni de topologie par defaut,
 * et permettre le choix du nom de l'attribut portant geometrie et topologie.
 * 
 * @author Thierry Badard
 * @author Arnaud Braun
 * @author Sandrine Balley
 * @author Nathalie Abadie
 * @author Julien Perret
 */
public abstract class FT_Feature implements Cloneable {
	static Logger logger=Logger.getLogger(FT_Feature.class.getName());

	/**
	 * Constructeur par défaut
	 */
	public FT_Feature() {super();}
	/**
	 * Contructeur à partir d'une géométrie
	 * @param geom géométrie du feature
	 */
	public FT_Feature(GM_Object geom) {
		super();
		this.geom = geom;
	}

	protected int id;
	/**
	 * Renvoie l'identifiant. NB: l'identifiant n'est rempli automatiquement que
	 * pour les objets persistants
	 * @return l'identifiant
	 */
	public int getId() {return this.id;}
	/**
	 * Affecte un identifiant (ne pas utiliser si l'objet est persistant car
	 * cela est automatique)
	 * @param Id l'identifiant
	 */
	public void setId(int Id) {this.id = Id;}

	protected GM_Object geom = null;
	/** 
	 * Renvoie une geometrie. 
	 * @return la géométrie de l'objet
	 */
	public GM_Object getGeom() {return this.geom;}
	/** 
	 * Affecte une geometrie et met à jour les éventuels index concernés.
	 * @param g nouvelle géométrie de l'objet 
	 */
	@SuppressWarnings("unchecked")
	public void setGeom(GM_Object g) {
		GM_Object previousGeometry = this.geom;
		boolean geomAvant = (previousGeometry != null);
		this.geom = g;
		synchronized(this.featurecollections) {
			FT_FeatureCollection<FT_Feature>[] collections = this.featurecollections.toArray(new FT_FeatureCollection[0]);
			for (FT_FeatureCollection<FT_Feature>fc:collections) {
				if (fc.hasSpatialIndex()) {
					if (fc.getSpatialIndex().hasAutomaticUpdate()) {
						if (geomAvant) fc.getSpatialIndex().update(this, (g!=null)?0:-1);
						else fc.getSpatialIndex().update(this, 1);
					}
				}
				if ((previousGeometry != null)&&((this.geom==null)||!previousGeometry.equals(this.geom))) fc.fireActionPerformed(new FeatureCollectionEvent(fc, this, FeatureCollectionEvent.Type.CHANGED,previousGeometry));
			}
		}
	}

	/** 
	 * Renvoie true si une geometrie existe, false sinon.
	 * @return vrai si une geometrie existe, faux sinon.
	 */
	public boolean hasGeom() {return (this.geom!=null);}

	protected TP_Object topo = null;
	/**
	 * Renvoie la topologie de l'objet. 
	 * @return la topologie de l'objet
	 */
	public TP_Object getTopo() {return this.topo;}
	/**
	 * Affecte la topologie de l'objet.
	 * @param t la topologie de l'objet
	 */
	public void setTopo(TP_Object t) {this.topo = t;}
	/**
	 * Renvoie true si une topologie existe, false sinon.
	 * @return vrai si l'objet possède une topologie, faux sinon
	 */
	public boolean hasTopo() {return (this.topo != null);}
	/** Clonage avec clonage de la geometrie. 
	 * @throws CloneNotSupportedException */
	public FT_Feature cloneGeom() throws CloneNotSupportedException {
		FT_Feature result = (FT_Feature) this.clone();
		result.setGeom((GM_Object) this.getGeom().clone());
		return result;
	}
	/** Clonage sans clonage de la geometrie. */
	@Override
	public Object clone() /*throws CloneNotSupportedException*/ {
	    try {
		return super.clone();
	    } catch (CloneNotSupportedException e) {
		return null;
	    }
	}
	/** Lien n-m bidirectionnel vers FT_FeatureCollection. */
	private List<FT_FeatureCollection<FT_Feature>> featurecollections = new ArrayList<FT_FeatureCollection<FT_Feature>>();

	/** Renvoie toutes les FT_FeatureCollection auquelles appartient this. */
	public List<FT_FeatureCollection<FT_Feature>> getFeatureCollections() {
		return this.featurecollections;
	}

	/** Renvoie la i-eme FT_FeatureCollection a laquelle appartient this. */
	public FT_FeatureCollection<FT_Feature> getFeatureCollection(int i) {
		return this.featurecollections.get(i);
	}

	/**
	 * Population a laquelle appartient this. Renvoie null si this n'appartient
	 * a aucune population. NB : normalement, this appartient à une seule
	 * collection. Si ce n'est pas le cas, une seule des collections est
	 * renvoyée au hasard (la Première de la liste).
	 */
	// @SuppressWarnings("unchecked")
	// public Population<? extends FT_Feature> getPopulation() {
	// Iterator<FT_FeatureCollection<? extends FT_Feature>> it =
	// featurecollections
	// .iterator();
	// while (it.hasNext()) {
	// Object o = it.next();
	// if (o instanceof Population)
	// return (Population<FT_Feature>) o;
	// }
	// return null;
	// }
	/**
	 * définit la population en relation, et met à jour la relation inverse.
	 * ATTENTION : persistance du FT_Feature non gérée dans cette méthode.
	 */

	// public void setPopulation(Population O) { Population old =
	// this.getPopulation(); if ( old != null ) old.remove(this); if ( O != null
	// )
	// O.add(this); }
	/**
	 * Lien bidirectionnel n-m des éléments vers eux-mêmes. Les méthodes get
	 * (sans indice) et set sont nécessaires au mapping. Les autres méthodes
	 * sont là seulement pour faciliter l'utilisation de la relation. ATTENTION:
	 * Pour assurer la bidirection, il faut modifier les listes uniquement avec
	 * ces methodes. NB: si il n'y a pas d'objet en relation, la liste est vide
	 * mais n'est pas "null". Pour casser toutes les relations, faire
	 * setListe(new ArrayList()) ou emptyListe().
	 */
	private List<FT_Feature> correspondants = new ArrayList<FT_Feature>();

	/** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
	public List<FT_Feature> getCorrespondants() {return this.correspondants;}

	/** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
	public void setCorrespondants(List<FT_Feature> L) {
		List<FT_Feature> old = new ArrayList<FT_Feature>(this.correspondants);
		for(FT_Feature O:old) {
			this.correspondants.remove(O);
			O.getCorrespondants().remove(this);
		}
		for(FT_Feature O:L) {
			this.correspondants.add(O);
			O.getCorrespondants().add(this);
		}
	}

	/** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
	public FT_Feature getCorrespondant(int i) {
		if (this.correspondants.size() == 0) return null;
		return this.correspondants.get(i);
	}

	/** Lien bidirectionnel n-m des éléments vers eux même. */
	public void addCorrespondant(FT_Feature O) {
		if (O == null) return;
		this.correspondants.add(O);
		O.getCorrespondants().add(this);
	}

	/** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
	public void removeCorrespondant(FT_Feature O) {
		if (O == null) return;
		this.correspondants.remove(O);
		O.getCorrespondants().remove(this);
	}

	/** Lien bidirectionnel n-m des éléments vers eux-mêmes. */
	public void clearCorrespondants() {
		for(FT_Feature O:this.correspondants) {O.getCorrespondants().remove(this);}
		this.correspondants.clear();
	}

	/** Lien bidirectionnel n-m des éléments vers eux même. */
	public void addAllCorrespondants(Collection<FT_Feature> c) {
		for(FT_Feature feature:c) addCorrespondant(feature);
	}

	/**
	 * Renvoie les correspondants appartenant a la FT_FeatureCollection passee
	 * en parametre.
	 */
	public List<FT_Feature> getCorrespondants(FT_FeatureCollection<? extends FT_Feature> pop) {
		List<? extends FT_Feature> elementsPop = pop.getElements();
		List<FT_Feature> resultats = new ArrayList<FT_Feature>(this.getCorrespondants());
		resultats.retainAll(elementsPop);
		return resultats;
	}

	////////////////////////////////////////////////////////////////////////////
	// /////
	/**
	 * méthodes issues de MdFeature : Permettent de créer un
	 * Feature dont les propriétés (valeurs d'attributs, opérations et objets en
	 * relation) peuvent être acced�s de fa�on générique en mentionnant le nom
	 * de la propriété. Pour permettre cela chaque feature est rattach� à son
	 * featureType.
	 */
	////////////////////////////////////////////////////////////////////////////
	// /////
	/**
	 * Creation d'un feature du type donné en paramètre, par exemple Route.
	 * L'objet crée sera alors une instance de bdcarto.TronconRoute (l'element
	 * de schéma logique correspondant au featureType Route) qui �tend
	 * FeatureCommun. Les valeurs d'attributs ne sont pas initialisées.
	 * @param featureType le feature type de l'objet à créer
	 * @return l'objet créé
	 */
	public static FT_Feature createTypedFeature(FeatureType featureType) {
		FT_Feature feature = null;
		try {
			Class<?> theClass = Class.forName(featureType.getNomClasse());
			feature = (FT_Feature) theClass.newInstance();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return feature;
	}

	/**
	 * L'unique population à laquelle appartient cet objet.
	 */
	protected Population<FT_Feature> population;
	/**
	 * @return the population
	 */
	@SuppressWarnings("unchecked")
	public Population<FT_Feature> getPopulation() {
		if (this.population != null) {return this.population;}
		synchronized(this.featurecollections) {
		    FT_FeatureCollection<FT_Feature>[] collections = this.featurecollections.toArray(new FT_FeatureCollection[0]);
		    for(FT_FeatureCollection<FT_Feature> f:collections) if (f instanceof Population<?>) return (Population<FT_Feature>) f;
		}
		return null;
	}
	/**
	 * @param population the population to set
	 */
	public void setPopulation(Population<FT_Feature> population) {
		this.population = population;
		// Refuse d'�crire dans ma population car ne peut pas pas vérifier si
		// this hérite bien de FT_Feature...
		// this.population.addUnique(this);
	}
	/**
	 * L'unique featureType auquel appartient cet objet.
	 */
	protected FeatureType featureType;
	/**
	 * Affecte le feature type de l'objet
	 * @param featureType le feature type de l'objet
	 */
	public void setFeatureType(FeatureType featureType) {this.featureType = featureType;}
	/**
	 * Utilitaire pour retrouver le type d'un objet (passe par la population)
	 * 
	 * @return le featureType de ce feature
	 */
	public FeatureType getFeatureType() {
		if ( (this.featureType == null) && (this.getPopulation() != null) )
			return this.getPopulation().getFeatureType();
		return this.featureType;
	}
	/**
	 * Methode reflexive pour Récupèrer la valeur d'un attribut donné en
	 * paramètre
	 * 
	 * @param attribute
	 * @return la valeur de l'attribut sous forme d'Object
	 */
	public Object getAttribute(AttributeType attribute) {
		if (attribute.getMemberName().equals("geom")) { //$NON-NLS-1$
			logger.warn("WARNING : Pour Récupèrer la primitive géométrique par défaut, veuillez utiliser " //$NON-NLS-1$
					+ "la méthode FT_Feature.getGeom() et non pas MdFeature.getAttribute(AttributeType attribute)"); //$NON-NLS-1$
			return this.getGeom();
		}
		if (attribute.getMemberName().equals("topo")) { //$NON-NLS-1$
			logger.warn("WARNING : Pour Récupèrer la primitive topologique par défaut, veuillez utiliser " //$NON-NLS-1$
					+ "la méthode FT_Feature.getTopo() et non pas MdFeature.getAttribute(AttributeType attribute)"); //$NON-NLS-1$
			return this.getTopo();
		}
		Object valeur = null;
		String nomFieldMaj = null;
		if (attribute.getNomField().length() == 0) nomFieldMaj = attribute.getNomField();
		else nomFieldMaj = Character.toUpperCase(attribute.getNomField().charAt(0)) + attribute.getNomField().substring(1);
		String nomGetFieldMethod = "get" + nomFieldMaj; //$NON-NLS-1$
		Class<?> classe = this.getClass();
		while (!classe.equals(Object.class)) {
			try {
				Method methodGetter = classe.getDeclaredMethod(nomGetFieldMethod, (Class[]) null);
				valeur = methodGetter.invoke(this, (Object[]) null);
				return valeur;
			} catch (NoSuchMethodException e) {
				//if (logger.isTraceEnabled()) logger.trace("La méthode "+nomGetFieldMethod+" n'existe pas dans la classe "+classe);
			} catch (SecurityException e) {
				if (logger.isDebugEnabled()) logger.debug("SecurityException pendant l'appel de la méthode "+nomGetFieldMethod+" sur la classe "+classe); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IllegalArgumentException e) {
				if (logger.isDebugEnabled()) logger.debug("IllegalArgumentException pendant l'appel de la méthode "+nomGetFieldMethod+" sur la classe "+classe); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IllegalAccessException e) {
				if (logger.isDebugEnabled()) logger.debug("IllegalAccessException pendant l'appel de la méthode "+nomGetFieldMethod+" sur la classe "+classe); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (InvocationTargetException e) {
				if (logger.isDebugEnabled()) {
				    logger.debug("InvocationTargetException pendant l'appel de la méthode "+nomGetFieldMethod+" sur la classe "+classe); //$NON-NLS-1$ //$NON-NLS-2$
				    logger.debug(e.getCause());
				}
			}
			classe = classe.getSuperclass();
		}
		// on r�essayer si le getter est du genre isAttribute, ie pour un Booléen
		nomGetFieldMethod = "is" + nomFieldMaj; //$NON-NLS-1$
		classe = this.getClass();
		while (!classe.equals(Object.class)) {
			try {
				Method methodGetter = classe.getDeclaredMethod(nomGetFieldMethod, (Class[]) null);
				valeur = methodGetter.invoke(this, (Object[]) null);
				return valeur;
			} catch (NoSuchMethodException e) {
				//if (logger.isTraceEnabled()) logger.trace("La méthode "+nomGetFieldMethod+" n'existe pas dans la classe "+classe);
			} catch (SecurityException e) {
				if (logger.isTraceEnabled()) logger.trace("SecurityException pendant l'appel de la méthode "+nomGetFieldMethod+" sur la classe "+classe); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IllegalArgumentException e) {
				if (logger.isTraceEnabled()) logger.trace("IllegalArgumentException pendant l'appel de la méthode "+nomGetFieldMethod+" sur la classe "+classe); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (IllegalAccessException e) {
				if (logger.isTraceEnabled()) logger.trace("IllegalAccessException pendant l'appel de la méthode "+nomGetFieldMethod+" sur la classe "+classe); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (InvocationTargetException e) {
				if (logger.isTraceEnabled()) logger.trace("InvocationTargetException pendant l'appel de la méthode "+nomGetFieldMethod+" sur la classe "+classe); //$NON-NLS-1$ //$NON-NLS-2$
			}
			classe = classe.getSuperclass();
		}
		logger.error("Echec de l'appel au getter de l'attribut "+attribute.getNomField()+" sur l'objet "+this); //$NON-NLS-1$ //$NON-NLS-2$
		return null;
	}
	/**
	 * méthode reflexive pour affecter à un feature une valeur d'attribut pour
	 * l'attributeType donné en paramètre. Il est inutile de connaêtre la classe
	 * d'implémentation du feature ni le nom de la méthode setter à invoquer.
	 * 
	 * @param attribute
	 * @param valeur
	 */
	public void setAttribute(AttributeType attribute, Object valeur) {
		if (attribute.getMemberName().equals("geom")) { //$NON-NLS-1$
			logger.warn("WARNING : Pour affecter la primitive géométrique par défaut, veuillez utiliser " //$NON-NLS-1$
					+ "la méthode FT_Feature.getGeom() et non pas MdFeature.getAttribute(AttributeType attribute)"); //$NON-NLS-1$
			this.setGeom((GM_Object) valeur);
		} else if (attribute.getMemberName().equals("topo")) { //$NON-NLS-1$
			logger.warn("WARNING : Pour affecter la primitive topologique par défaut, veuillez utiliser " //$NON-NLS-1$
					+ "la méthode FT_Feature.getTopo() et non pas MdFeature.getAttribute(AttributeType attribute)"); //$NON-NLS-1$
			this.setTopo((TP_Object) valeur);
		}
		else {
			try {
				String nomFieldMaj2;
				if (attribute.getNomField().length() == 0) {nomFieldMaj2 = attribute.getNomField();}
				else {
					nomFieldMaj2 = Character.toUpperCase(attribute.getNomField().charAt(0))
							+ attribute.getNomField().substring(1);
				}
				String nomSetFieldMethod = "set" + nomFieldMaj2; //$NON-NLS-1$
				Method methodSetter = this.getClass().getDeclaredMethod(nomSetFieldMethod, valeur.getClass());
				// Method methodGetter =
				// this.getClass().getSuperclass().getDeclaredMethod(
				// nomGetFieldMethod,
				// null);
				methodSetter.invoke(this, valeur);
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
			}
		}
	}
	/**
	 * Methode reflexive pour Récupèrer les features en relation par
	 * l'intermédiaire du role donné en paramètre. Attention, cette méthode
	 * suppose que tous les éléments en relation ont été chargés en m�moire. Ce
	 * n'est pas toujours le cas avec OJB : pour des raisons de performances, le
	 * concepteur du fichier de mapping y représente parfois les relations de
	 * fa�on unidirectionnelle. Si la méthode renvoie une liste vide, vérifiez
	 * votre fichier de mapping. Si vous ne souhaitez pas le modifier, explorez
	 * la relation dans l'autre sens (par exemple, avec un fichier de mapping
	 * donné, le role "troncon route a pour noeud initial" sera explorable mais
	 * pas le role "noeud routier à pour arcs sortants".
	 * 
	 * @param ftt le type d'objets dont on veut la liste
	 * @param role le r�le que l'on souhaite explorer
	 * @return la liste des features en relation
	 */
	@SuppressWarnings("unchecked")
	public List<? extends FT_Feature> getRelatedFeatures(FeatureType ftt,AssociationRole role) {
		List<FT_Feature> listResult = new ArrayList();
		if (logger.isDebugEnabled()) logger.debug("\n**recherche des features en relation**"); //$NON-NLS-1$
		try {
			// cas 1-1 ou 1-N ou N-1 où il n'y a pas de classe association
			if (role.getNomFieldAsso() == null) {
				if (logger.isDebugEnabled()) logger.debug("pas de classe association"); //$NON-NLS-1$
				String nomFieldClasseMaj;
				if (role.getNomFieldClasse().length() == 0) {nomFieldClasseMaj = role.getNomFieldClasse();}
				else {nomFieldClasseMaj = Character.toUpperCase(role.getNomFieldClasse().charAt(0))+role.getNomFieldClasse().substring(1);}
				String nomGetMethod = "get" + nomFieldClasseMaj; //$NON-NLS-1$
				Method methodGetter = this.getClass().getDeclaredMethod(nomGetMethod, (Class[]) null);
				// Method methodGetter =
				// this.getClass().getSuperclass().getDeclaredMethod(
				// nomGetFieldMethod,
				// null);
				Object objResult = methodGetter.invoke(this,(Object[]) null);
				if (objResult instanceof FT_Feature) {listResult.add((FT_Feature) objResult);}
				else if (objResult instanceof List) {listResult.addAll((List<FT_Feature>) objResult);}
			}
			// cas ou il y a une classe association
			else {
				if (logger.isDebugEnabled()) logger.debug("classe association : " //$NON-NLS-1$
						+ role.getAssociationType().getTypeName());
				List<FT_Feature> listInstancesAsso = new ArrayList<FT_Feature>();
				// je vais chercher les instances de classe-association
				String nomFieldClasseMaj;
				if (role.getNomFieldClasse().length() == 0) {nomFieldClasseMaj = role.getNomFieldClasse();}
				else {nomFieldClasseMaj = Character.toUpperCase(role.getNomFieldClasse().charAt(0))+role.getNomFieldClasse().substring(1);}
				String nomGetMethod = "get" + nomFieldClasseMaj; //$NON-NLS-1$
				Method methodGetter = this.getClass().getDeclaredMethod(nomGetMethod, (Class[]) null);
				String nomClasseAsso = ((AssociationType) role.getAssociationType()).getNomClasseAsso();
				Class classeAsso = Class.forName(nomClasseAsso);
				if (logger.isDebugEnabled()) logger.debug("cardMax de " + role.getMemberName() + " = "+ role.getCardMax()); //$NON-NLS-1$ //$NON-NLS-2$
				if (!role.getCardMax().equals("1")) { //$NON-NLS-1$
					if (logger.isDebugEnabled()) logger.debug("invocation de "+ methodGetter.getName() + " sur le feature "+ this.getId()); //$NON-NLS-1$ //$NON-NLS-2$
					listInstancesAsso.addAll((List<FT_Feature>) methodGetter.invoke(this, (Object[]) null));
					if (logger.isDebugEnabled()) logger.debug("nb instances d'association = "+ listInstancesAsso.size()); //$NON-NLS-1$
				} else {
					listInstancesAsso.add((FT_Feature) methodGetter.invoke(this, (Object[]) null));
					if (logger.isDebugEnabled()) logger.debug("nb instances d'association = "+listInstancesAsso.size()); //$NON-NLS-1$
				}
				// je cherche le (ou les) role(s) allant de l'association �
				// l'autre featureType
				List listRoles = role.getAssociationType().getRoles();
				listRoles.remove(role);
				List listRolesAGarder = role.getAssociationType().getRoles();
				listRolesAGarder.remove(role);
				for (int i = 0; i < listRoles.size(); i++) {
					if (!((AssociationRole) listRoles.get(i)).getFeatureType().equals(ftt)) {
						listRolesAGarder.remove(listRoles.get(i));
					}
				}
				/**
				 * pour chaque role concerné (il peut y en avoir plus d'un) je
				 * vais chercher les instances en relation
				 */
				AssociationRole roleExplore;
				for (int i = 0; i < listRolesAGarder.size(); i++) {
					roleExplore = (AssociationRole) listRolesAGarder.get(i);
					if (logger.isDebugEnabled()) logger.debug("role explor� = "+ roleExplore.getMemberName()); //$NON-NLS-1$
					String nomFieldAssoMaj;
					if (roleExplore.getNomFieldAsso().length() == 0) {nomFieldAssoMaj = roleExplore.getNomFieldAsso();}
					else {nomFieldAssoMaj = Character.toUpperCase(roleExplore.getNomFieldAsso().charAt(0))+roleExplore.getNomFieldAsso().substring(1);}
					nomGetMethod = "get" + nomFieldAssoMaj; //$NON-NLS-1$
					methodGetter = classeAsso.getDeclaredMethod(nomGetMethod,(Class[]) null);
					if (logger.isDebugEnabled()) logger.debug("methode de la classe-asso pour recuperer les instances en relation = "+methodGetter.getName()); //$NON-NLS-1$
					/**
					 *  je vais chercher les objets en relation via chaque instance de classe-association
					 */
					for (int j = 0; j < listInstancesAsso.size(); j++) {
						if (logger.isDebugEnabled()) logger.debug("j = " + j); //$NON-NLS-1$
						if (logger.isDebugEnabled()) logger.debug("instance = "+listInstancesAsso.get(j).getId()); //$NON-NLS-1$
						if (logger.isDebugEnabled()) logger.debug("class  "+listInstancesAsso.get(j).getClass()); //$NON-NLS-1$
						if (logger.isDebugEnabled()) logger.debug(methodGetter.invoke(listInstancesAsso.get(j), (Object[]) null));
						Object objResult = methodGetter.invoke(listInstancesAsso.get(j), (Object[]) null);
						if (objResult instanceof FT_Feature) {listResult.add((FT_Feature) objResult);}
						else if (objResult instanceof List) {listResult.addAll((List<FT_Feature>) objResult);}
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
		if (logger.isDebugEnabled()) logger.debug("\n**fin de la recherche des features en relation**"); //$NON-NLS-1$
		return listResult;
	}

	/**
	 * Methode pour Récupèrer la valeur d'un attribut dont le nom est donné en
	 * paramètre
	 * 
	 * @param nomAttribut
	 * @return la valeur de l'attribut sous forme d'Object
	 */
	public Object getAttribute(String nomAttribut) {
		FeatureType ft = this.getFeatureType();
		if (ft == null) {
			AttributeType type = new AttributeType();
			type.setNomField(nomAttribut);
			type.setMemberName(nomAttribut);
			//FIXME c'est un peu une bidouille
			return this.getAttribute(type);
			//logger.error("Le FeatureType correspondant à ce FT_Feature est introuvable: Impossible de remonter au AttributeType à partir de son nom.");
			//return null;
		}
		AttributeType attribute = ft.getFeatureAttributeByName(nomAttribut);
		return (this.getAttribute(attribute));
	}
	/**
	 * Methode pour Récupèrer les features en relation par l'intermédiaire du
	 * role donné en paramètre. Attention, cette méthode suppose que tous les
	 * éléments en relation ont été chargés en m�moire. Ce n'est pas toujours le
	 * cas avec OJB : pour des raisons de performances, le concepteur du fichier
	 * de mapping y représente parfois les relations de fa�on unidirectionnelle.
	 * Si la méthode renvoie une liste vide, vérifiez votre fichier de mapping.
	 * Si vous ne souhaitez pas le modifier, explorez la relation dans l'autre
	 * sens (par exemple, avec un fichier de mapping donné, le role "troncon
	 * route a pour noeud initial" sera explorable mais pas le role "noeud
	 * routier à pour arcs sortants".
	 * 
	 * @param nomFeatureType
	 * @param nomRole
	 * @return la liste des features en relation avec nomFeatureType via nomRole
	 */
	public List<? extends FT_Feature> getRelatedFeatures(String nomFeatureType,String nomRole) {
		// Initialisation de la liste des résultats
		List<? extends FT_Feature> listResultats = null;
		// On Récupère le featuretype nomm� nomFeatureType
		FeatureType ftt = (FeatureType) this.getFeatureType().getSchema()
		.getFeatureTypeByName(nomFeatureType);
		// On Récupère l'AssociationRole nomm� nomRole
		AssociationRole role = null;
		List<GF_AssociationRole> listeRoles = ftt.getRoles();
		for (GF_AssociationRole r : listeRoles) {
			if (r.getMemberName().equalsIgnoreCase(nomRole)) role = (AssociationRole) r;
			else continue;
		}
		if (role == null) {
			logger.error("Le FeatureType "+nomFeatureType+" ou l'AssociationRole "+nomRole //$NON-NLS-1$ //$NON-NLS-2$
					+ " est introuvable. Impossible de calculer les FT_Feature en relation!"); //$NON-NLS-1$
			return null;
		}
		listResultats = this.getRelatedFeatures(ftt, role);
		return listResultats;
	}

	/**
	 * La s�miologie de l'objet Géographique
	 */
	private Representation representation = null;
	/**
	 * Renvoie la représentation li�e à l'objet - Renvoie null si non définie
	 * @return la représentation li�e à l'objet - Renvoie null si non définie
	 */
	public Representation getRepresentation() {return this.representation;}
	/**
	 * Affecte une représentation à un objet
	 * @param rep représentation à affecter au FT_Feature
	 */
	public void setRepresentation(Representation rep) {this.representation = rep;}

	/**
	 * marqueur de suppression d'un objet (utilisé par exemple en généralisation)
	 */
	private boolean estSupprime = false;
	/**
	 * Marqueur de suppression d'un objet (utilisé par exemple en généralisation).
	 * @return vrai si l'objet a été supprim�, faux sinon 
	 */
	public boolean estSupprime() { return this.estSupprime; }
	/**
	 * Affecte le marqueur de suppression d'un objet (utilisé par exemple en généralisation).
	 * @param estSupprime vrai si l'objet a été supprim�, faux sinon
	 */
	public void setEstSupprime(boolean estSupprime) { this.estSupprime = estSupprime; }	
	/**
	 * Renvoie vrai si l'objet intersecte l'envelope, faux sinon
	 * @param env envelope
	 * @return vrai si l'objet intersecte l'envelope, faux sinon
	 */
	public boolean intersecte(GM_Envelope env) {
		if (this.getGeom() == null || env == null) return false;
		return this.getGeom().envelope().intersects(env);
	}
	@Override
	public String toString() {return this.getClass().getSimpleName()+" "+this.getGeom();} //$NON-NLS-1$
	
}

package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JOptionPane;

import fr.ign.cogit.geoxygene.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.feature.type.GF_AssociationType;
import fr.ign.cogit.geoxygene.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.feature.type.GF_InheritanceRelation;
import fr.ign.cogit.geoxygene.schema.SchemaConceptuel;





/**
 * 
 * @author Abadie, Balley
 * 
 * schéma conceptuel d'un produit, typiquement une base de données Géographique.
 * Correspond à la notion "Feature Catalogue" dans les normes ISO.
 * Un schéma est composé de classes et de relations (associations et héritage) comportant
 * des proprietés (attributs, rôles, opérations) et des contraintes.
 * 
 * Cette classe est similaire à la classe
 * fr.ign.cogit.appli.commun.metadata.schemaConceptuel.schemaJeu.schemaConceptuelJeu
 * à quelques nuances prés : elle utilise notamment des classes implémentant le GFM
 * mais dédiées aux produits et non pas aux jeux de données.
 * 
 */

public class SchemaConceptuelProduit implements SchemaConceptuel {

	/**
	 * Constructeur par défaut
	 */

	public SchemaConceptuelProduit() {
		this.featureTypes = new ArrayList<GF_FeatureType>();
	}

	/**
	 * Constructeur
	 * 
	 */
	public SchemaConceptuelProduit(String nom, String base) {
		this.nomSchema=nom;
		this.BD = base;
		this.featureTypes = new ArrayList<GF_FeatureType>();
	}

	/**
	 * Constructeur
	 * 
	 */
	public SchemaConceptuelProduit(String nom, String base, int tag) {
		this.nomSchema=nom;
		this.BD = base;
		this.tagBD = tag;
		this.featureTypes = new ArrayList<GF_FeatureType>();
	}

	/**Identifiant d'un objet*/
	protected int id;
	public int getId() {return this.id;}
	public void setId(int id) {this.id = id;}


	protected String sujet;
	/**
	 * @return the sujet
	 */
	public String getSujet() {return this.sujet;}
	/**
	 * @param sujet the sujet to set
	 */
	public void setSujet(String sujet) {this.sujet = sujet;}


	protected String version;
	/**
	 * @return the version
	 */
	public String getVersion() {return this.version;}
	/**
	 * @param version the version to set
	 */
	public void setVersion(String version) {this.version = version;}


	protected String date;
	/**
	 * @return the date
	 */
	public String getDate() {return this.date;}
	/**
	 * @param date the date to set
	 */
	public void setDate(String date) {this.date = date;}


	protected String source;
	/**
	 * @return the source
	 */
	public String getSource() {return this.source;}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {this.source = source;}


	/**
	 * Nom de la base de données (ou produit) correspondante
	 */
	protected String BD;

	public void setBD(String nomBD) {
		this.BD = nomBD;
	}

	public String getBD() {
		return this.BD;
	}

	/**
	 * Identifiant de la BD correspondante
	 */
	protected int tagBD;

	public void setTagBD(int ID) {
		this.tagBD = ID;
	}

	public int getTagBD() {
		return this.tagBD;
	}


	/**
	 * dèsignation usuelle du schéma
	 */
	protected String nomSchema;

	public String getNomSchema(){
		return this.nomSchema;
	}
	public void setNomSchema(String nom){
		this.nomSchema=nom;
	}


	/**
	 * Description du schéma
	 */
	protected String definition;

	public void setDefinition(String def){
		this.definition=def;
	}

	public String getDefinition(){
		return this.definition;
	}



	/**
	 * Liste des FeatureType du schéma
	 */
	protected List<GF_FeatureType> featureTypes;

	public List<GF_FeatureType> getFeatureTypes() {
		return this.featureTypes;
	}

	public void setFeatureTypes(List<GF_FeatureType> ftListValue) {
		this.featureTypes = ftListValue;
	}

	public void addFeatureType(GF_FeatureType ft) {
		if (!this.getFeatureTypes().contains(ft))
			this.featureTypes.add(ft);

		if (((FeatureType)ft).getSchema()!=this)
			((FeatureType)ft).setSchema(this);
	}

	public GF_FeatureType getFeatureTypeI(int i) {
		return this.featureTypes.get(i);
	}

	public void removeFeatureTypeFromSchema(GF_FeatureType ft) {
		if (ft == null)
			return;
		this.featureTypes.remove(ft);
		((FeatureType)ft).setSchema(null);
	}

	public GF_FeatureType getFeatureTypeByName(String name){
		GF_FeatureType ft = null;
		Iterator<GF_FeatureType> it = this.featureTypes.iterator();
		while(it.hasNext()){
			GF_FeatureType f = it.next();
			//			Modification Nathalie: on ignore la casse!
			if (f.getTypeName().equalsIgnoreCase(name)){ft=f;}
			else{continue;}
		}
		return ft;
	}

	/*
	 * *********************************************************
	 * méthodes pour manipuler les éléments
	 * *********************************************************
	 */

	/**
	 * Ajoute une classe au schéma en cours
	 */
	public void createFeatureType(String nomClasse) {
		// création d'un nouveau featuretype
		FeatureType ft = new FeatureType();

		// vérification qu'aucune classe du même nom n'existe dans le schéma
		if (!this.featureTypes.isEmpty()) {
			Iterator<GF_FeatureType> iTft = this.featureTypes.iterator();
			while (iTft.hasNext()) {
				GF_FeatureType feature = iTft.next();
				if (feature.getTypeName().equalsIgnoreCase(nomClasse)) {

					JOptionPane
					.showMessageDialog(
							null,
							"Erreur : il existe déjà dans cette base une classe de ce nom.",
							"Conflit de nom", JOptionPane.ERROR_MESSAGE);
					return;
				}
				continue;
			}// Fin du while
		}// Fin du premier if
		else {
		}
		ft.setTypeName(nomClasse);
		ft.setSchema(this);

		// Initialisation des listes
		List<GF_AssociationType> lasso = new ArrayList<GF_AssociationType>();
		ft.setMemberOf(lasso);
		List<GF_AssociationRole> lrole = new ArrayList<GF_AssociationRole>();
		ft.setRoles(lrole);
		List<GF_AttributeType> latt = new ArrayList<GF_AttributeType>();
		ft.setFeatureAttributes(latt);
		List<GF_InheritanceRelation> lgen = new ArrayList<GF_InheritanceRelation>();
		ft.setGeneralization(lgen);
		List<GF_InheritanceRelation> lspe = new ArrayList<GF_InheritanceRelation>();
		ft.setSpecialization(lspe);

		// Mise à jour de la liste des featuretype
		this.addFeatureType(ft);
	}// Fin de la méthode



	/**
	 * Supprime une classe du schéma en cours: Cette méthode se charge d'effacer
	 * toute trace des attributs de la classe, de leurs valeurs énumérées, des
	 * associations, des roles, etc.
	 */
	public void removeFeatureType(FeatureType ft) {

		/* Suppression des attributs de la classe */
		//Liste locale des attributs du featuretype
		List<GF_AttributeType> attList = new ArrayList<GF_AttributeType>();

		List<GF_AttributeType> attLies = ft.getFeatureAttributes();
		Iterator<GF_AttributeType> iTatt = attLies.iterator();
		while (iTatt.hasNext()) {
			AttributeType attbd = (AttributeType) iTatt.next();

			/* Suppression des valeurs d'attribut pour les types enumeres */
			if (attbd.getValueDomainType()) {
				//On crée une liste locale des valeurs d'attributs énumérés
				List<FeatureAttributeValue> valList = new ArrayList<FeatureAttributeValue>();
				// On Récupère la liste des valeurs
				List<FC_FeatureAttributeValue> valeurs = attbd.getValuesDomain();
				Iterator<FC_FeatureAttributeValue> iTval = valeurs.iterator();
				while (iTval.hasNext()) {
					FeatureAttributeValue val = (FeatureAttributeValue)iTval.next();
					// On remplit la liste locale
					valList.add(val);
				}
				//On détruit ces valeurs d'attribut
				Iterator<FeatureAttributeValue> iTvaleur = valList.iterator();
				while (iTval.hasNext()) {
					FeatureAttributeValue val = iTvaleur.next();
					// On supprime cette valeur dans la liste
					attbd.removeValuesDomain(val);

				}

			} else {
			}
			//On remplit la liste locale
			attList.add(attbd);
		}

		//On détruit tous les attributs du featuretype
		Iterator<GF_AttributeType> iTV=attList.iterator();
		while (iTV.hasNext()){
			GF_AttributeType fav=iTV.next();
			ft.removeFeatureAttribute(fav);
			fav.setFeatureType(null);
		}

		/* Suppression des associations dans lesquelles la classe est impliquée */
		// variables locales
		FeatureType scft=new FeatureType();
		List<GF_AssociationType> assoList = new ArrayList<GF_AssociationType>();

		List<GF_AssociationType> assoLiees = ft.getMemberOf();
		Iterator<GF_AssociationType> iTasso = assoLiees.iterator();
		while (iTasso.hasNext()) {
			// Pour chaque association impliquant ce featuretype...
			GF_AssociationType monAsso = iTasso.next();
			assoList.add(monAsso);

			//On Récupère la liste des roles joués par les ft dans cette asso
			List<GF_AssociationRole> mesRoles = monAsso.getRoles();

			//On Récupère le featuretype associé
			int i;
			for (i = 0; i > monAsso.getLinkBetween().size(); i++) {
				if (monAsso.getLinkBetweenI(i) != ft) {
					scft = (FeatureType) monAsso.getLinkBetweenI(i);}
				else{continue;}}

			//On va supprimer les roles au niveau des featuretype
			Iterator<GF_AssociationRole> iTrole = mesRoles.iterator();
			while (iTrole.hasNext()) {
				GF_AssociationRole monRole = iTrole.next();
				if (monRole.getFeatureType() == scft) {
					// Suppression du role au niveau du featuretype
					// associé. On vide aussi le ft au niveau du role
					scft.removeRole(monRole);
					break;
				} else if (monRole.getFeatureType() == ft){
					//Suppression du role au niveau du featuretype
					// associé. On vide aussi le ft au niveau du role
					ft.removeRole(monRole);
					break;
				} else {continue;}
			}// Fin du while interne
		}//Fin du while externe

		Iterator<GF_AssociationType> iTA=assoList.iterator();
		while (iTA.hasNext()){
			GF_AssociationType fa=iTA.next();

			//On supprime les roles au niveau de chaque asso (bidirectionnel)
			//fa.getRoles().clear();
			List<GF_AssociationRole> rlist=fa.getRoles();
			Iterator<GF_AssociationRole> iTr=rlist.iterator();
			while (iTr.hasNext()){
				GF_AssociationRole rol=iTr.next();
				rol.setAssociationType(null);
			}

			//On supprime les liens avec les featuretype (bidirectionnel)
			List<GF_FeatureType> ftlist=fa.getLinkBetween();
			Iterator<GF_FeatureType> iTft=ftlist.iterator();
			while (iTft.hasNext()){
				FeatureType ftype= (FeatureType) iTft.next();
				ftype.getMemberOf().remove(fa);
			}
		}

		/*
		 * Suppression des relations de generalisation dans lesquelles la classe
		 * a supprimer est impliquee
		 */
		List<GF_InheritanceRelation> listGeneral = ft.getGeneralization();
		Iterator<GF_InheritanceRelation> iTgeneral = listGeneral.iterator();
		while (iTgeneral.hasNext()) {
			GF_InheritanceRelation generalisation = iTgeneral.next();
			// Récupération de la classe mere
			FeatureType classeMere = (FeatureType) generalisation
			.getSuperType();
			// Suppression de la relation au niveau de la classe mere et de la
			// classe fille
			classeMere.removeSpecialization(generalisation);
		}

		// Suppression des relations de specialisation dans
		// lesquelles la classe a supprimer est impliquee
		List<GF_InheritanceRelation> listSpecial = ft.getSpecialization();
		Iterator<GF_InheritanceRelation> iTspecial = listSpecial.iterator();
		while (iTspecial.hasNext()) {
			GF_InheritanceRelation specialisation = iTspecial.next();
			// Récupération de la classe fille
			FeatureType classeFille = (FeatureType) specialisation
			.getSubType();
			// Suppression de la relation au niveau de la classe mere et de la
			// classe fille
			classeFille.removeGeneralization(specialisation);
		}

		/* Maintenant qu'on a tout vire, on peut enlever le FeatureType */
		this.removeFeatureTypeFromSchema(ft);
	}

	/**
	 * Ajoute un attribut à une classe
	 */
	public void createFeatureAttribute(FeatureType ft, String nomAtt, String type, boolean valueDomainType) {

		AttributeType AttBd = new AttributeType();


		/*
		 * vérification qu'aucun attribut du même nom n'existe pour cette classe
		 * et ajout dans la liste
		 */
		List<GF_AttributeType> AttExistants = ft.getFeatureAttributes();
		Iterator<GF_AttributeType> iT = AttExistants.iterator();
		while (iT.hasNext()) {
			GF_AttributeType AttEx = iT.next();
			if (AttEx.getMemberName().equalsIgnoreCase(nomAtt)) {
				JOptionPane
				.showMessageDialog(
						null,
						"Erreur : il existe déjà dans cette classe un attribut de ce nom.",
						"Conflit de nom", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// Ajout de ce nouvel attribut dans la liste de tous les attributs de
		// cette classe (bidirectionnel)
		ft.addFeatureAttribute(AttBd);

		// Ajout des attributs de cette instance
		AttBd.setMemberName(nomAtt);
		AttBd.setValueDomainType(false);
		AttBd.setValueType(type);
		AttBd.setValueDomainType(valueDomainType);
	}

	/**
	 * Supprime un attribut dans une classe
	 */
	public void removeFeatureAttribute(FeatureType ft, AttributeType att) {
		/*
		 * Supression du lien attribut-feature
		 */
		ft.removeFeatureAttribute(att);
		att.setFeatureType(null);

		/*
		 * Recuperation et destruction de toutes les valeurs de l'attribut dans
		 * le cas d'un type enumere
		 */
		if (att.getValueDomainType()) {
			List<FC_FeatureAttributeValue> mesValeurs = att.getValuesDomain();
			Iterator<FC_FeatureAttributeValue> iTvaleurs = mesValeurs.iterator();
			while (iTvaleurs.hasNext()) {
				FeatureAttributeValue maValeur = (FeatureAttributeValue)iTvaleurs.next();
				maValeur.setFeatureAttribute(null);
			}
		} else {
		}
		//On vide la liste des valeurs énumérées
		att.getValuesDomain().clear();
	}


	/**
	 * Cree une valeur d'attribut pour les types enumeres
	 */
	public void createFeatureAttributeValue(AttributeType attCorrespondant, String label) {

		// Creation d'une nouvelle valeur d'attribut
		FeatureAttributeValue valeurAtt = new FeatureAttributeValue();

		// On la relie a l'attribut correspondant (bidirectionnel)
		attCorrespondant.addValuesDomain(valeurAtt);

		//On la nomme
		valeurAtt.setLabel(label);

	}

	/**
	 * Supprime une valeur d'attribut pour un type enumere
	 */
	public void removeFeatureAttributeValue(FeatureAttributeValue valeurAtt) {

		// On Récupère l'attribut correspondant
		AttributeType attCorrespondant = (AttributeType)valeurAtt.getFeatureAttribute();

		// On supprime son lien avec cette valeur (bidirectionnel)
		attCorrespondant.removeValuesDomain(valeurAtt);

	}

	/**
	 * Ajoute une relation de généralisation entre classes
	 */
	public void createGeneralisation(FeatureType classeCurr, FeatureType classeMere) {
		/*
		 * Pour être en relation, les deux classes doivent appartenir à la même
		 * BDG
		 */
		if (classeCurr.getSchema() != classeMere.getSchema()) {
			JOptionPane
			.showMessageDialog(
					null,
					"Erreur : On ne peut établir de relation de généralisation entre classes issues de BD différentes.",
					"Erreur!", JOptionPane.ERROR_MESSAGE);
			return;
		}

		InheritanceRelation generalisation = new InheritanceRelation();

		/*
		 * Recuperation des relations de generalisation dans lesquelles le
		 * FeatureType courant est implique
		 */
		List<GF_InheritanceRelation> general = classeCurr.getGeneralization();

		Iterator<GF_InheritanceRelation> iTgeneral = general.iterator();
		while (iTgeneral.hasNext()) {
			GF_InheritanceRelation relGeneral = iTgeneral.next();
			/*
			 * Si cette relation de generalisation existe déjà, on avertit et on
			 * sort
			 */
			if (relGeneral.getSuperType() == classeMere) {
				JOptionPane
				.showMessageDialog(
						null,
						"Erreur : Une relation identique existe déjà entre ces deux classes.",
						"Relation redondante.",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			continue;
			// Sinon, on teste la suivante
		}// Fin du while

		/*
		 * Definition des roles de chaque classe impliquee dans la relation de
		 * generalisation
		 */
		generalisation.setSuperType(classeMere);
		generalisation.setSubType(classeCurr);
	}

	/**
	 * Supprime une relation de généralisation entre classes
	 */
	public void removeGeneralisation(FeatureType classeCurr, FeatureType classeMere) {
		/*
		 * Recuperation des relations de generalisation dans lesquelles le
		 * FeatureType courant est implique
		 */
		List<GF_InheritanceRelation> general = classeCurr.getGeneralization();
		Iterator<GF_InheritanceRelation> iTgeneral = general.iterator();
		while (iTgeneral.hasNext()) {
			GF_InheritanceRelation relGeneral = iTgeneral.next();
			// Si c'est bien la relation que l'on cherche...
			if (relGeneral.getSuperType() == classeMere) {
				/*
				 * Suppression de la relation de generalisation dans la liste
				 * generalization de FeatureType
				 */
				classeCurr.removeGeneralization(relGeneral);
				classeMere.removeSpecialization(relGeneral);
				return;
			}
			// Sinon, on passe à la suivante
		}// Fin du while
	}

	/**
	 * Ajoute une relation de spécialisation entre classes
	 */
	public void createSpecialisation(FeatureType classeCurr, FeatureType classeFille) {
		// Pour être en relation, les deux classes doivent appartenir à la même
		// BDG
		if (classeCurr.getSchema() != classeFille.getSchema()) {
			JOptionPane
			.showMessageDialog(
					null,
					"Erreur : On ne peut établir de relation de spécialisation entre classes issues de BD différentes.",
					"Erreur!", JOptionPane.ERROR_MESSAGE);
			return;
		}

		InheritanceRelation specialisation = new InheritanceRelation();

		/*
		 * Recuperation des relations de specialisation dans lesquelles le
		 * FeatureType courant est implique
		 */
		List<GF_InheritanceRelation> special = classeCurr.getSpecialization();
		Iterator<GF_InheritanceRelation> iTspecial = special.iterator();
		while (iTspecial.hasNext()) {
			GF_InheritanceRelation relSpecial = iTspecial.next();
			/*
			 * Si cette relation de generalisation existe déjà, on avertit et on
			 * sort
			 */
			if (relSpecial.getSubType() == classeFille) {
				JOptionPane
				.showMessageDialog(
						null,
						"Erreur : Une relation identique existe déjà entre ces deux classes.",
						"Relation redondante.",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			continue;
			// Sinon, on teste la suivante
		}// Fin du while

		/*
		 * Definition des roles de chaque classe impliquee dans la relation de
		 * generalisation
		 */
		specialisation.setSuperType(classeCurr);
		specialisation.setSubType(classeFille);
	}

	/**
	 * Supprime une relation de spécialisation entre classes
	 */
	public void removeSpecialisation(FeatureType classeCurr, FeatureType classeFille) {
		/*
		 * Recuperation des relations de specialisation dans lesquelles le
		 * FeatureType courant est implique
		 */
		List<GF_InheritanceRelation> special = classeCurr.getSpecialization();
		Iterator<GF_InheritanceRelation> iTspecial = special.iterator();
		while (iTspecial.hasNext()) {
			GF_InheritanceRelation relSpecial = iTspecial.next();
			// Si c'est bien la relation que l'on cherche...
			if (relSpecial.getSubType() == classeFille) {
				/*
				 * Suppression de la relation de specialisation dans la liste
				 * generalization de FeatureType
				 */
				classeCurr.removeSpecialization(relSpecial);
				classeFille.removeGeneralization(relSpecial);
				return;
			}// Sinon, on passe à la suivante
		}// Fin du while
	}

	/**
	 * Ajoute une association entre classes
	 */
	public void createFeatureAssociation(String nomAsso, FeatureType ft1, FeatureType ft2, String role1, String role2) {
		/* Il faut deux featuretypes du même schéma */
		if (ft1.getSchema() != ft2.getSchema()) {
			JOptionPane
			.showMessageDialog(
					null,
					"Erreur : On ne peut établir de lien entre classes issues de schémas différents.",
					"Erreur!", JOptionPane.ERROR_MESSAGE);
			return;
		}

		/*
		 * Recuperation des associations dans lesquelles le FeatureType ft1
		 * est implique
		 */
		List<GF_AssociationType> assoList = ft1.getMemberOf();
		/* Recuperation des featureType avec lesquels ft1 est déjà en relation */
		Iterator<GF_AssociationType> iTassoc = assoList.iterator();
		while (iTassoc.hasNext()) {
			GF_AssociationType scfa = iTassoc.next();
			List<GF_FeatureType> ftList = scfa.getLinkBetween();
			/*
			 * S'il existe déjà une association entre ces deux classes
			 * et qu'elle porte le même nom, on
			 * prévient et on sort
			 */
			if ((ftList.contains(ft2))&(scfa.getTypeName().equals(nomAsso))) {
				JOptionPane
				.showMessageDialog(
						null,
						"Erreur : Une association nommée "
						+nomAsso+ " existe déjà entre ces deux classes.",
						"Relation redondante.",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		// Declaration et allocation de place en memoire pour mes nouvelles
		// instances de AssociationRole et FeatureAssociation
		AssociationType association = new AssociationType();
		association.setTypeName(nomAsso);
		AssociationRole roleAssoc1 = new AssociationRole(ft1,association);
		roleAssoc1.setMemberName(role1);
		AssociationRole roleAssoc2 = new AssociationRole(ft2,association);
		roleAssoc2.setMemberName(role2);

		// Mise à jour manuelle du lien n:m: il ne sera pas stocké
		ft1.addMemberOf(association);
		ft2.addMemberOf(association);

		// Mise à jour des liens 1:n (persistents)
		association.addRole(roleAssoc1);
		association.addRole(roleAssoc2);
		ft1.addRole(roleAssoc1);
		ft2.addRole(roleAssoc2);

		//		roleAssoc12.setAssociationType(association);
		//		roleAssoc21.setAssociationType(association);
		//		roleAssoc1.setFeatureType(ft1);
		//		roleAssoc2.setFeatureType(ft2);




	}

	/**
	 * Supprime une relation d'association entre classes
	 */
	public void removeFeatureAssociation(AssociationType fa) {

		// Supression de l'association au niveau des FeatureTypes
		List<GF_FeatureType> ftList = fa.getLinkBetween();
		Iterator<GF_FeatureType> iTft = ftList.iterator();
		while (iTft.hasNext()) {
			FeatureType scft = (FeatureType)iTft.next();
			// Supression de l'association au niveau des FeatureTypes
			scft.removeMemberOf(fa);
			List<GF_AssociationRole> rolesList = scft.getRoles();
			Iterator<GF_AssociationRole> iTrole = rolesList.iterator();
			while (iTrole.hasNext()) {
				GF_AssociationRole monRole = iTrole.next();
				if (monRole.getAssociationType() == fa) {
					// Suppression du role au niveau des FeatureTypes et de l'association
					scft.removeRole(monRole);
					fa.removeRole(monRole);
					break;
				}
				continue;
			}// Fin du while interne
		}// Fin du while externe

	}

	/**
	 * Supprime une relation d'association entre classes
	 */
	public void removeFeatureAssociation(FeatureType ft1, FeatureType ft2) {

		AssociationType monAsso = null;

		// Recuperation de l'association existant entre ces deux classes (si
		// elle existe!)
		List<GF_AssociationType> assoList = ft1.getMemberOf();
		Iterator<GF_AssociationType> iTasso = assoList.iterator();
		while (iTasso.hasNext()) {
			GF_AssociationType scfa = iTasso.next();
			List<GF_FeatureType> ftList = scfa.getLinkBetween();
			if (ftList.contains(ft2)) {
				monAsso = (AssociationType) scfa;
				break;
			}
			continue;
		}

		// Si l'association existe, on continue
		if (monAsso == null) {
			JOptionPane
			.showMessageDialog(
					null,
					"Erreur : Aucune association n'existe entre ces deux classe.",
					"Suppression impossible!",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		// Récupération des roles
		List<GF_AssociationRole> mesRoles = monAsso.getRoles();

		// Supression de l'association au niveau des FeatureTypes
		ft1.removeMemberOf(monAsso);
		ft2.removeMemberOf(monAsso);

		// Supression des roles au niveau des FeatureType et dans la BD
		Iterator<GF_AssociationRole> iTrole = mesRoles.iterator();
		while (iTrole.hasNext()) {
			GF_AssociationRole monRole = iTrole.next();
			if ((monRole.getAssociationType() == monAsso)
					&& (monRole.getFeatureType() == ft1)) {
				ft1.removeRole(monRole);
				monAsso.removeRole(monRole);
				continue;
			} else if ((monRole.getAssociationType() == monAsso)
					&& (monRole.getFeatureType() == ft2)) {
				ft2.removeRole(monRole);
				monAsso.removeRole(monRole);
				continue;
			} else {
				continue;
			}
		}

	}


	/*
	 * *********************************************************
	 * méthodes héritées de Schema pour lister les éléments
	 * *********************************************************
	 */

	/**
	 * @return La liste de tous les attributs du schéma
	 */
	public List<AttributeType> getFeatureAttributes(){
		List<AttributeType> attList = new ArrayList<AttributeType>();
		Iterator<GF_FeatureType> iT = this.featureTypes.iterator();
		while (iT.hasNext())
		{
			GF_FeatureType ft=iT.next();
			List<GF_AttributeType> ftAttList = ft.getFeatureAttributes();
			Iterator<GF_AttributeType> iTatt = ftAttList.iterator();
			while (iTatt.hasNext()){
				AttributeType att=(AttributeType)iTatt.next();
				attList.add(att);
			}
		}
		return attList;
	}

	/**
	 * @return La liste de toutes les valeurs d'attributs énumérés du schéma
	 */
	public List<FeatureAttributeValue> getFeatureAttributeValues(){
		List<FeatureAttributeValue> valList = new ArrayList<FeatureAttributeValue>();
		Iterator<AttributeType> iT = this.getFeatureAttributes().iterator();
		while (iT.hasNext()){
			AttributeType att= iT.next();
			if (att.getValueDomainType()){
				List<FC_FeatureAttributeValue> attValList=att.getValuesDomain();
				Iterator<FC_FeatureAttributeValue> iTval=attValList.iterator();
				while(iTval.hasNext()){
					FeatureAttributeValue val=(FeatureAttributeValue)iTval.next();
					valList.add(val);
				}
			}
			else {continue;}
		}
		return valList;
	}

	/**
	 * @return La liste de toutes les associations du schéma
	 */
	public List<AssociationType> getFeatureAssociations(){
		List<AssociationType> assoList = new ArrayList<AssociationType>();
		Iterator<GF_FeatureType> iT = this.featureTypes.iterator();
		while (iT.hasNext()){
			FeatureType ft= (FeatureType) iT.next();
			List<GF_AssociationRole> ftRoleList=ft.getRoles();
			Iterator<GF_AssociationRole> iTrole=ftRoleList.iterator();
			while (iTrole.hasNext()){
				AssociationRole role= (AssociationRole) iTrole.next();
				AssociationType asso = (AssociationType)role.getAssociationType();
				if (!(assoList.contains(asso))){
					assoList.add(asso);}
				else {continue;}
			}
		}
		return assoList;
	}

	/**
	 * @return La liste de tous les roles du schéma
	 */
	public List<AssociationRole> getAssociationRoles(){
		List<AssociationRole> roleList = new ArrayList<AssociationRole>();
		Iterator<GF_FeatureType> iT = this.featureTypes.iterator();
		while (iT.hasNext()){
			FeatureType ft= (FeatureType) iT.next();
			List<GF_AssociationRole> ftRoleList=ft.getRoles();
			Iterator<GF_AssociationRole> iTrole = ftRoleList.iterator();
			while (iTrole.hasNext()){
				AssociationRole role=(AssociationRole)iTrole.next();
				roleList.add(role);
			}
		}
		return roleList;
	}

	/**
	 * @return la liste de toutes les relations d'héritage du schéma
	 */
	public List<InheritanceRelation> getInheritance(){
		List<InheritanceRelation> heritList = new ArrayList<InheritanceRelation>();
		Iterator<GF_FeatureType> iT = this.featureTypes.iterator();
		while (iT.hasNext()){
			GF_FeatureType ft=iT.next();
			List<GF_InheritanceRelation> ftHeritList=ft.getGeneralization();
			Iterator<GF_InheritanceRelation> iTherit = ftHeritList.iterator();
			while (iTherit.hasNext()){
				InheritanceRelation herit=(InheritanceRelation)iTherit.next();
				heritList.add(herit);
			}
		}
		return heritList;
	}


	/*
	 * *********************************************************
	 * méthodes héritées de Schema pour la persistance
	 * *********************************************************
	 */




	/*
	 * *******************************************************
	 * Initialisation des liens n:m
	 * *******************************************************
	 */

	/**
	 * Cette méthode s'utilise au chargement d'un schéma ISO:
	 * elle met à jour les listes non persistentes memberOf et linkBetween.
	 */
	public void initNM(){

		Iterator<AssociationRole> iTrol = this.getAssociationRoles().iterator();
		while (iTrol.hasNext()){
			AssociationRole role = iTrol.next();
			FeatureType ft = (FeatureType) role.getFeatureType();
			ft.addMemberOf(role.getAssociationType());
		}
	}











}

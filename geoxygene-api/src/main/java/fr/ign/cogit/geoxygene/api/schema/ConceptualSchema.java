package fr.ign.cogit.geoxygene.api.schema;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_InheritanceRelation;

/**
 * Interface pour tout schéma conceptuel. (Application schema dans les normes
 * ISO) Il n'a pas de type de données standard ApplicationSchema. Nous le
 * définissons comme un ensemble d'éléments standards définis dans le package
 * fr.ign.cogit.appli.commun.metadonnees.schemaConceptuel.interfacesISO.
 * @author Nathalie Abadie
 * @author Sandrine Balley
 */
public interface ConceptualSchema<FT extends GF_FeatureType, AT extends GF_AttributeType, FAV extends FC_FeatureAttributeValue, AssociationT extends GF_AssociationType, AR extends GF_AssociationRole, IR extends GF_InheritanceRelation> {
    /** Identifiant d'un objet */
    public int getId();
    /** Identifiant d'un objet */
    public void setId(int id);
    /**
     * Désignation usuelle du schéma.
     */
    public String getNomSchema();
    /**
     * Désignation usuelle du schéma.
     */
    public void setNomSchema(String nom);
    /**
     * Description du schéma.
     */
    public String getDefinition();
    /**
     * Description du schéma.
     */
    public void setDefinition(String def);
    /**
     * Liste des classes appartenant au schéma.
     */
    public List<FT> getFeatureTypes();
    /**
     * Liste des classes appartenant au schéma.
     */
    public void setFeatureTypes(List<FT> ftList);

    public void addFeatureType(FT ft);

    public void removeFeatureTypeFromSchema(FT ft);

    public FT getFeatureTypeByName(String name);

    /*
     * ******************************************************************
     * méthodes pour manipuler mon schéma
     * ******************************************************************
     */

    // méthodes enlevées, descendues dans schemaISOJeu et SchemaISOProduit

    /*
     * ******************************************************************
     * méthodes pour lister les différents éléments du schéma
     * ******************************************************************
     */

    // méthodes enlevées, descendues dans schemaISOJeu et SchemaISOProduit

    /*
     * ******************************************************************
     * méthodes pour sauvegarder mon schéma
     * ******************************************************************
     */

    /*
     * méthodes enlevées car elles obligeaient un import de classe "outil" dans le
     * modele. Les méthodes save et delete sont implementees de façon statique
     * dans SchemaPersistentOJB
     */

    public void initNM();
    
    /**
     * Ajoute une classe au schéma en cours
     */
    public void createFeatureType(String nomClasse);
    /**
     * Supprime une classe du schéma en cours: Cette méthode se charge d'effacer
     * toute trace des attributs de la classe, de leurs valeurs énumérées, des
     * associations, des roles, etc.
     */
    public void removeFeatureType(FT ft);
    /**
     * Ajoute un attribut à une classe
     */
    public void createFeatureAttribute(FT ft, String nomAtt,
        String type, boolean valueDomainType);
    /**
     * Supprime un attribut dans une classe
     */
    public void removeFeatureAttribute(FT ft, AT att);
    /**
     * Cree une valeur d'attribut pour les types enumeres
     */
    public void createFeatureAttributeValue(AT attCorrespondant,
        String label);
    /**
     * Supprime une valeur d'attribut pour un type enumere
     */
    public void removeFeatureAttributeValue(FAV valeurAtt);
    /**
     * Ajoute une relation de généralisation entre classes
     */
    public void createGeneralisation(FT classeCurr,
        FT classeMere);
    /**
     * Supprime une relation de généralisation entre classes
     */
    public void removeGeneralisation(FT classeCurr,
        FT classeMere);
    /**
     * Ajoute une relation de spécialisation entre classes
     */
    public void createSpecialisation(FT classeCurr,
        FT classeFille);
    /**
     * Supprime une relation de spécialisation entre classes
     */
    public void removeSpecialisation(FT classeCurr,
        FT classeFille);
    /**
     * Ajoute une association entre classes
     */
    public void createFeatureAssociation(String nomAsso, FT ft1,
        FT ft2, String role1, String role2);
    /**
     * Supprime une relation d'association entre classes
     */
    public void removeFeatureAssociation(AssociationT fa);
    /**
     * Supprime une relation d'association entre classes
     */
    public void removeFeatureAssociation(FT ft1, FT ft2);
    /**
     * @return La liste de tous les attributs du schéma
     */
    public List<AT> getFeatureAttributes();
    /**
     * @return La liste de toutes les valeurs d'attributs énumérés du schéma
     */
    public List<FAV> getFeatureAttributeValues();
    /**
     * @return La liste de toutes les associations du schéma
     */
    public List<AssociationT> getFeatureAssociations();
    /**
     * @return La liste de tous les roles du schéma
     */
    public List<AR> getAssociationRoles();
    /**
     * @return la liste de toutes les relations d'héritage du schéma
     */
    public List<IR> getInheritance();
}

package fr.ign.cogit.geoxygene.api.schema.product;

import fr.ign.cogit.geoxygene.api.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_InheritanceRelation;
import fr.ign.cogit.geoxygene.api.schema.ConceptualSchema;


/**
 * 
 * @author Abadie, Balley
 * 
 *         schéma conceptuel d'un produit, typiquement une base de données
 *         Géographique. Correspond à la notion "Feature Catalogue" dans les
 *         normes ISO. Un schéma est composé de classes et de relations
 *         (associations et héritage) comportant des proprietés (attributs,
 *         rôles, opérations) et des contraintes.
 * 
 *         Cette classe est similaire à la classe
 *         fr.ign.cogit.appli.commun.metadata
 *         .schemaConceptuel.schemaJeu.schemaConceptuelJeu à quelques nuances
 *         près : elle utilise notamment des classes implémentant le GFM mais
 *         dédiées aux produits et non pas aux jeux de données.
 * 
 */
public interface ProductConceptualSchema<FT extends GF_FeatureType, AT extends GF_AttributeType, FAV extends FC_FeatureAttributeValue, AssociationT extends GF_AssociationType, AR extends GF_AssociationRole, IR extends GF_InheritanceRelation>
        extends ConceptualSchema<FT, AT, FAV, AssociationT, AR, IR> {
    /**
     * @return the sujet
     */
    public String getSujet();
    /**
     * @param sujet the sujet to set
     */
    public void setSujet(String sujet);
    /**
     * @return the version
     */
    public String getVersion();
    /**
     * @param version the version to set
     */
    public void setVersion(String version);
    /**
     * @return the date
     */
    public String getDate();
    /**
     * @param date the date to set
     */
    public void setDate(String date);
    /**
     * @return the source
     */
    public String getSource();
    /**
     * @param source the source to set
     */
    public void setSource(String source);
    /**
     * Nom de la base de données (ou produit) correspondante
     */
    public void setBD(String nomBD);
    /**
     * Nom de la base de données (ou produit) correspondante
     */
    public String getBD();
    /**
     * Identifiant de la BD correspondante
     */
    public void setTagBD(int ID);
    /**
     * Identifiant de la BD correspondante
     */
    public int getTagBD();
}

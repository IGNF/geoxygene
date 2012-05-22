package fr.ign.cogit.geoxygene.api.schema.dataset;

import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.api.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationRole;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AssociationType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_AttributeType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.api.feature.type.GF_InheritanceRelation;
import fr.ign.cogit.geoxygene.api.schema.ConceptualSchema;
import fr.ign.cogit.geoxygene.api.schema.product.ProductConceptualSchema;

public interface DatasetConceptualSchema
<FT extends GF_FeatureType, AT extends GF_AttributeType, FAV extends FC_FeatureAttributeValue, AssociationT extends GF_AssociationType, AR extends GF_AssociationRole, IR extends GF_InheritanceRelation>
        extends ConceptualSchema<FT, AT, FAV, AssociationT, AR, IR> {
    /**
     * Non standard Utilisé dans les applications de transformation de schéma
     * Proprietaire de l'élément (producteur=1, le schéma n'est pas modifiable
     * utilisateur=2, le schéma est modifiable)
     ***/
    public int getProprietaire();

    /** Affecte le proprietaire */
    public void setProprietaire(int value);
    /**
     * @return le schéma de produit d'où est issu ce schéma de jeu
     */
    public ProductConceptualSchema<? extends GF_FeatureType, ? extends GF_AttributeType, ? extends FC_FeatureAttributeValue, ? extends GF_AssociationType, ? extends GF_AssociationRole, ? extends GF_InheritanceRelation> getSchemaProduitOrigine();
    /**
     * affecte le schéma de produit d'où est issu ce schéma de jeu
     */
    public void setSchemaProduitOrigine(ProductConceptualSchema<? extends GF_FeatureType, ? extends GF_AttributeType, ? extends FC_FeatureAttributeValue, ? extends GF_AssociationType, ? extends GF_AssociationRole, ? extends GF_InheritanceRelation> schemaProduit);
    /**
     * jeu de données caractérisée par ce schémaJeu.
     */
    public IDataSet<?> getDataset();
    /**
     * @param dataset the dataset to set
     */
    public void setDataset(IDataSet<?> dataset);
}

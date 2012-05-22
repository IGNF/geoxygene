package fr.ign.cogit.geoxygene.api.schema;

import java.util.List;

import fr.ign.cogit.geoxygene.api.feature.IDataSet;
import fr.ign.cogit.geoxygene.api.schema.product.ProductConceptualSchema;

/**
 * A Product.
 * <p>
 * Un produit qui a un producteur, des métadonnées minimales et notamment un
 * schéma conceptuel (de type SchemaISOProduit) D'autres métadonnées sont à
 * venir, notamment des MD ISO 19115 et des spécifications Cogit complètes
 * @author Sandrine Balley
 */
public interface IProduct<PCS extends ProductConceptualSchema<?, ?, ?, ?, ?, ?>> {
    /** Identifiant d'un objet */
    public int getId();
    /** Identifiant d'un objet */
    public void setId(int id);
    /**
     * @return the nom
     */
    public String getNom();
    /**
     * @param nom the nom to set
     */
    public void setNom(String nom);
    /**
     * @return the type
     */
    public int getType();
    /**
     * @param type the type to set 1 = BD 2 = série de cartes
     */
    public void setType(int type);
    /**
     * @return the producteur
     */
    public String getProducteur();
    /**
     * @param producteur the producteur to set
     */
    public void setProducteur(String producteur);
    /**
     * @return the echelleMin
     */
    public double getEchelleMin();
    /**
     * @param echelleMin the echelleMin to set
     */
    public void setEchelleMin(double echelleMin);
    /**
     * @return the echelleMax
     */
    public double getEchelleMax();
    /**
     * @param echelleMax the echelleMax to set
     */
    public void setEchelleMax(double echelleMax);
    /**
     * @return the schemaConceptuel
     */
    public PCS getSchemaConceptuel();
    /**
     * @param schemaConceptuel the schemaConceptuel to set
     */
    public void setSchemaConceptuel(PCS schemaConceptuel);
    /**
     * @return the jeuxDisponibles
     */
    public List<IDataSet<?>> getJeuxDisponibles();
    /**
     * @param jeuxDisponibles the jeuxDisponibles to set
     */
    public void setJeuxDisponibles(List<IDataSet<?>> jeuxDisponibles);
}

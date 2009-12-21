package fr.ign.cogit.geoxygene.schema;

import java.util.List;

import fr.ign.cogit.geoxygene.feature.type.GF_FeatureType;

/**
 * 
 * @author Abadie, Balley
 * Interface pour tout schéma conceptuel.
 * (Application schema dans les normes ISO)
 * 
 * Il n'a pas de type de données standard ApplicationSchema.
 * Nous le définissons comme un ensemble d'éléments standards
 * définis dans le package fr.ign.cogit.appli.commun.metadonnees.schemaConceptuel.interfacesISO.
 */
public interface SchemaConceptuel {


	/**
	 * D�signation usuelle du schéma
	 */
	public String getNomSchema();
	public void setNomSchema(String nom);


	/**
	 * Description du schéma
	 */
	public String getDefinition();
	public void setDefinition(String def);

	/**
	 * Liste des classes appartenant au schéma
	 */
	public List<GF_FeatureType> getFeatureTypes();
	public void setFeatureTypes(List<GF_FeatureType> ftList);
	public void addFeatureType(GF_FeatureType ft);
	public void removeFeatureTypeFromSchema(GF_FeatureType ft);
	public GF_FeatureType getFeatureTypeByName(String name);

	/*
	 * ******************************************************************
	 * méthodes pour manipuler mon schéma
	 * ******************************************************************
	 */

	//méthodes enlev�es, descendues dans schemaISOJeu et SchemaISOProduit

	/*
	 * ******************************************************************
	 * méthodes pour lister les différents éléments du schéma
	 * ******************************************************************
	 */

	//	méthodes enlev�es, descendues dans schemaISOJeu et SchemaISOProduit

	/*
	 * ******************************************************************
	 * méthodes pour sauvegarder mon schéma
	 * ******************************************************************
	 */

	/*	méthodes enlev�es car elles obligeaient un import de classe "outil"
	 * dans le modele. Les méthodes save et delete sont implementees de fa�on
	 * statique dans SchemaPersistentOJB
	 */

	public void initNM();

}

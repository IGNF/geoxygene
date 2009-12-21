package fr.ign.cogit.geoxygene.feature.type;

/**
 * @author Sandrine Balley
 *
 * FC_FeatureAttributeValue propos� par la norme ISO19110 "FeatureCataloguing"
 */
public interface FC_FeatureAttributeValue {



	/** Renvoie l'attribut auquel est rattach� la valeur. */
	public GF_AttributeType getFeatureAttribute();
	/** Affecte l'attribut auquel est rattach� la valeur. */
	public void setFeatureAttribute(GF_AttributeType FeatureAttribute);


	/** Renvoie la valeur d'attribut */
	public String getLabel();
	/** Affecte la valeur d'attribut */
	public void setLabel(String Label);


	/** Renvoie la valeur d'attribut */
	public int getcode();
	/** Affecte la valeur d'attribut */
	public void setCode(int value);


	/** Renvoie la définition. */
	public String getDefinition();
	/** Affecte une définition. */
	public void setDefinition(String Definition);



	/**renvoie la définition semantique du FeatureAttributeValue (sous la forme d'un String
	 * ou d'une classe d'ontologie)
	 * ou un pointeur vers cette définition (sous la forme d'une URI)
	 * 
	 * Correspond à FC_DescriptionReference et FC_DescriptionSource propos�s
	 * dans la norme ISO19110
	 * @return Object
	 */
	public Object getDefinitionReference();
	public void setDefinitionReference(Object ref);


}

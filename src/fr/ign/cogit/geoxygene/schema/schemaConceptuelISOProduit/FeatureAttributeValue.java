
package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit;

import fr.ign.cogit.geoxygene.feature.type.FC_FeatureAttributeValue;
import fr.ign.cogit.geoxygene.feature.type.GF_AttributeType;


/**
 * @author Sandrine Balley
 *
 */
public class FeatureAttributeValue implements FC_FeatureAttributeValue {

	/**
	 * Valeur d'attribut s'appliquant à un attribut de type �num�r�
	 */
	public FeatureAttributeValue() {super();}

	/**Identifiant d'un objet*/
	protected int id;
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}



	/**
	 * Non standard
	 * Utile aux applications de transformation de schéma
	 * caract�re implicite ou explicite de l'élément : un featureAttributeValue
	 * implicite n'est jamais affect� à priori mais il peut l'être par le
	 * biais de calculs à partir d'éléments explicites
	 **/
	protected boolean isExplicite;
	/** Renvoie le caractere explicite ou implicite */
	public boolean getIsExplicite(){return this.isExplicite;}
	/** Affecte le caract�re implicite ou explicite */
	public void setIsExplicite(boolean value){this.isExplicite = value;}



	// ///////////////////////////////////////////////////////////////////
	// Modifications Nathalie
	// ///////////////////////////////////////////////////////////////////
	/** attribut auquel s'applique cette valeur**/
	protected AttributeType featureAttribute;
	/** Renvoie le feature attribute auquel s'applique cette valeur. */
	public GF_AttributeType getFeatureAttribute(){return this.featureAttribute;}
	/** Affecte un feature attribute auquel s'applique cette valeur. */
	public void setFeatureAttribute(GF_AttributeType FeatureAttribute){
		AttributeType old = this.featureAttribute;
		this.featureAttribute = (AttributeType)FeatureAttribute;
		if (old != null){old.getValuesDomain().remove(this);}
		if (FeatureAttribute != null){
			if (! FeatureAttribute.getValuesDomain().contains(this)){
				FeatureAttribute.getValuesDomain().add(this);
			}
		}
	}
	// /////////////////////////////////////////////////////////////////////

	/** label pour une valeur d'attribut**/
	protected String label;
	/** Renvoie la valeur d'attribut */
	public String getLabel(){return this.label;}
	/** Affecte la valeur d'attribut */
	public void setLabel(String Label){this.label = Label;}

	/** code pour une valeur d'attribut**/
	protected int code;
	/** Renvoie la valeur d'attribut */
	public int getcode(){return this.code;}
	/** Affecte la valeur d'attribut */
	public void setCode(int value){this.code = value;}

	/** definition de la valeur.*/
	protected String definition;
	/** Renvoie la définition. */
	public String getDefinition (){return this.definition;}
	/** Affecte une définition. */
	public void setDefinition (String Definition) {this.definition = Definition;}


	/**la définition semantique du featureType (sous la forme d'un String
	 ou d'une classe d'ontologie)
	 ou un pointeur vers cette définition (sous la forme d'une URI)*/
	protected Object definitionReference;
	/**
	 * @return the definitionReference
	 */
	public Object getDefinitionReference() {
		return definitionReference;
	}

	/**
	 * @param definitionReference the definitionReference to set
	 */
	public void setDefinitionReference(Object definitionReference) {
		this.definitionReference = definitionReference;
	}



}

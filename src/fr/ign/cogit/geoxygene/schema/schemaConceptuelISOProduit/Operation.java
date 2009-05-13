package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit;

import fr.ign.cogit.geoxygene.feature.type.GF_FeatureType;
import fr.ign.cogit.geoxygene.feature.type.GF_Operation;




/**
 * @author Sandrine Balley
 *
 */
public class Operation implements GF_Operation {

	/**
	 * 
	 */
	public Operation() {
		super();
	}


	/**Identifiant d'un objet*/
	protected int id;
	public int getId() {return id;}
	public void setId(int id) {this.id = id;}

	/** FC_featureType auquel se rapporte l'operation*/
	protected GF_FeatureType featureType;
	/** definition de l'opeartion.*/
	protected String definition;
	/** Description du nom, des arguments et du renvoi de l'opération.  */
	protected String signature;
	/**definition formelle de l'operation*/
	protected String formalDefinition;
	/** le nom de l'opération*/
	protected String memberName;

	/**le nom de la methode correspondant a l'operation dans la classe ou classe-association Java correspondante*/
	protected String nomMethode;
	public String getNomMethode(){return this.nomMethode;}
	public void setNomMethode(String value){this.nomMethode = value;}

	/** proprietaire de l'élément (producteur=1, utilisateur=2)**/
	protected int proprietaire;
	/** Renvoie le proprietaire */
	public int getProprietaire(){return this.proprietaire;}
	/** Affecte le proprietaire */
	public void setProprietaire(int value){this.proprietaire = value;}



	/** Renvoie la définition formelle */
	public String getFormalDefinition() {return this.formalDefinition;}
	/** Affecte une definition formelle. */
	public void setFormalDefinition(String value) {this.formalDefinition = value;}


	/** Renvoie le feature type auquel est rattaché la propriété. */
	public GF_FeatureType getFeatureType(){return this.featureType;}
	/** Affecte un feature type à la propriété. */
	public void setFeatureType(GF_FeatureType FeatureType){this.featureType = FeatureType;}

	/** Renvoie la définition. */
	public String getDefinition (){return this.definition;}
	/** Affecte une définition. */
	public void setDefinition (String Definition) {this.definition = Definition;}

	/** Renvoie la signature. */
	public String getSignature () {return this.signature;}
	/** Affecte une signature. */
	public void setSignature(String Signature) {this.signature = Signature;}


	/** Renvoie le nom de la propriété. */
	public String getMemberName () {return this.memberName;}
	/** Affecte un nom de propriété. */
	public void setMemberName (String MemberName) {this.memberName = MemberName;}

}

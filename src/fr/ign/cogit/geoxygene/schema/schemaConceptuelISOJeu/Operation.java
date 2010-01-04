package fr.ign.cogit.geoxygene.schema.schemaConceptuelISOJeu;

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

	public Operation(fr.ign.cogit.geoxygene.schema.schemaConceptuelISOProduit.Operation ori) {
		this.id = ori.getId();
		this.definition = ori.getDefinition();
		this.formalDefinition = ori.getFormalDefinition();
		this.memberName = ori.getMemberName();
		this.signature = ori.getSignature();
		this.nomMethode = ori.getNomMethode();
	}

	/** Identifiant*/
	protected int id;
	/** FC_featureType auquel se rapporte l'operation*/
	protected GF_FeatureType featureType;
	/** nom de l'operation.*/
	protected String memberName;
	/** definition de l'opeartion.*/
	protected String definition;
	/** Description du nom, des arguments et du renvoi de l'opération.  */
	protected String signature;
	/**definition formelle de l'operation*/
	protected String formalDefinition;


	/**le nom de la methode correspondant a l'operation dans la classe ou classe-association Java correspondante*/
	protected String nomMethode;
	public String getNomMethode(){return this.nomMethode;}
	public void setNomMethode(String value){this.nomMethode = value;}



	/** Renvoie la définition formelle */
	public String getFormalDefinition() {return this.formalDefinition;}
	/** Affecte une definition formelle. */
	public void setFormalDefinition(String value) {this.formalDefinition = value;}

	/** Renvoie l'identifiant. */
	public int getId() {return this.id;}
	/** Affecte un identifiant. */
	public void setId(int Id) {this.id = Id;}

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



	/**
	 * commodité pour retrouver à quel schéma conceptuel appartient cette operation
	 * (en passant par le featureType qu'elle caractérise).
	 **/
	public SchemaConceptuelJeu getSchemaConceptuel() {
		if (this.getFeatureType()!=null){
			return ((FeatureType)this.getFeatureType()).getSchema();
		}
		return null;
	}


}

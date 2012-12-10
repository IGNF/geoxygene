package fr.ign.cogit.geoxygene.semio.legend.metadata;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import fr.ign.cogit.geoxygene.semio.legend.legendContent.LegendComponent;

/**
 * 
 *        This software is released under the licence CeCILL
 * 
 *        see Licence_CeCILL-C_fr.html
 *        see Licence_CeCILL-C_en.html
 * 
 *        see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * 
 * @copyright IGN
 * 
 * @author Sébastien Mustière - IGN / Laboratoire COGIT
 * @author Charlotte Hoarau - IGN / Laboratoire COGIT
 * 
 * Relationship between legend components.
 * <p>
 * <strong>French:</strong><br />
 * Relation entre éléments de légende (ordre, association, dissociation).
 * Le principe des codes d'amélioration de légende est de faire que 
 * ces relations théoriques soient bien en phase avec les relations
 *  prévues entre les couleurs.  
 * 
 *  @see SemanticRelationDescriptor
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SemanticRelation", propOrder = {
  "type",
  "relatedComponents"
})
public class SemanticRelation {
	public final static int UNKNOWN = -1;
	public final static int ASSOCIATION = 1;
	public final static int DIFFERENCE = 2;
	public final static int ORDER = 3;

	
	/**
	 * Type of the relationship.
	 * Possible values: see static variables.
	 */
	@XmlElement(name = "Type")
	private int type = UNKNOWN;
	
	/**
	 * Returns the type of the relationship.
	 * Possible values: see static variables.
	 */
	public int getType() {
	  return type;
	}
	
	/**
	 * Specifies the type of the relationship.
	 * Possible values: see static variables.
	 */
	public void setType(int type) {
	  this.type = type;
	}
	
	public boolean isAssociation() {
	  if (this.getType() == SemanticRelation.ASSOCIATION) {
	    return true;
	  } else {
	    return false;
	  }
	}
	
	public boolean isDifference() {
      if (this.getType() == SemanticRelation.DIFFERENCE) {
        return true;
      } else {
        return false;
      }
    }
	
	public boolean isOrder() {
      if (this.getType() == SemanticRelation.ORDER) {
        return true;
      } else {
        return false;
      }
    }

	/**
	 * Components concerned by the relationship (2 or more).
	 * NB: in case of ORDER, the order of components in the list goes
	 * from the least 'important' to the most 'important' 
	 */
	@XmlElement(name = "Layer")
	private List<LegendComponent> relatedComponents = new ArrayList<LegendComponent>();
	
	/**
	 * Returns the components concerned by the relationship (2 or more).
	 * NB: in case of ORDER, the order of components in the list goes
	 * from the least 'important' to the most 'important' 
	 */
	public List<LegendComponent> getRelatedComponents() {
	  return this.relatedComponents;
	}
	
	/**
	 * Specifies the components concerned by the relationship (2 or more).
	 * NB: in case of ORDER, the order of components in the list goes
	 * from the least 'important' to the most 'important' 
	 */
	public void setRelatedComponents(List<LegendComponent> elementsEnRelation) {
		List <LegendComponent>old = 
		  new ArrayList<LegendComponent>(this.relatedComponents);
		for (LegendComponent obj : old) {
			elementsEnRelation.remove(obj);
			obj.getRelations().remove(this);
		}
		for (LegendComponent obj : elementsEnRelation) {
			this.relatedComponents.add(obj);
			obj.getRelations().add(this);
		}
	}
	
	/** 
	 * Add a component to the components concerned by the relationship 
	 * (two or more).
	 * NB: in case of ORDER, the order of components in the list goes
	 * from the least 'important' to the most 'important' 
	 */
	public void addRelatedComponents(LegendComponent element) {
		if ( element == null ) return;
		this.relatedComponents.add(element);
		element.getRelations().add(this);
	}
	
	/**
	 * Remove a component from the list of components concerned by the relationship
	 * (two or more).
	 * NB: in case of ORDER, the order of components in the list goes
	 * from the least 'important' to the most 'important' 
	 */
	public void removeRelatedComponents(LegendComponent element) {
		if ( element == null ) return;
		this.relatedComponents.remove(element) ;
		element.getRelations().remove(this);
	}
	
	/**
     * SemanticRelationDescriptor to which this layer belongs to.
     * <p>
     * <strong>French:</strong><br />
     * Ensemble de relations auquel appartient la relation sémantique.
     */
	@XmlTransient
    private SemanticRelationDescriptor srd;

    /**
     * Returns the semanticRelationDescriptor to which this layer belongs to.
     * @return The semanticRelationDescriptor to which this layer belongs to.
     */
    public SemanticRelationDescriptor getSRD() {
        return this.srd;
    }

    /**
     * Specifies the semanticRelationDescriptor to which this layer belongs to.
     * @param srd The semanticRelationDescriptor to which this layer belongs to.
     */
    public void setSRD(SemanticRelationDescriptor srd) {
        SemanticRelationDescriptor old = this.srd;
        this.srd = srd;
        if (old != null) {
            old.getRelations().remove(this);
        }
        if (srd != null) {
            if (!srd.getRelations().contains(this))
                srd.getRelations().add(this);
        }
    }
    
	/**
	 * Empty Constructor.
	 */
	public SemanticRelation() {
		super();
	}
	
	public SemanticRelation(List<LegendComponent> relatedComponents, int type) {
		super();
		this.setRelatedComponents(relatedComponents);
		this.type = type;
	}
	
	@Override
  public String toString(){
		String text = new String("Relation of ");
		switch (type){
			case 1 :
				text += "Association";
				break;
			case 2 :
				text += "Differenciation";
				break;
			case 3 :
				text += "Order";
				break;
			case 4 :
				text += "Quantity";
				break;
		}
		text += " between the layers :"
			+ System.getProperty("line.separator");
		for (LegendComponent layer : relatedComponents) {
			text += "   > " + layer.getName() + System.getProperty("line.separator");
		}
		return text;
	}
}

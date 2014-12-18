package fr.ign.cogit.ontology;

import com.hp.hpl.jena.ontology.OntClass;

/**
 * 
 * @author Nathalie Abadie
 */
public class ValeurMatriceCreuse {
	
  private OntClass line;
  private OntClass row;
  private Integer value;
	
  /**
   * Constructor.
   * @param line RDFResource
   * @param row RDFResource
   * @param value value
   */
  public ValeurMatriceCreuse(OntClass line, OntClass row, int value) {
    super();
    this.line = line;
    this.row = row;
    this.value = value;
  }
	
	public OntClass getLine() {
		return line;
	}
	public OntClass getRow() {
		return row;
	}
	public Integer getValue() {
		return value;
	}
	public void setLine(OntClass line) {
		this.line = line;
	}
	public void setRow(OntClass row) {
		this.row = row;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	
}

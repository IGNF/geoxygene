package fr.ign.cogit.ontology;

import edu.stanford.smi.protegex.owl.model.RDFResource;

/**
 * 
 * @author Nathalie Abadie
 */
public class ValeurMatriceCreuse {
	
  private RDFResource line;
  private RDFResource row;
  private Integer value;
	
  /**
   * Constructor.
   * @param line RDFResource
   * @param row RDFResource
   * @param value value
   */
  public ValeurMatriceCreuse(RDFResource line, RDFResource row, int value) {
    super();
    this.line = line;
    this.row = row;
    this.value = value;
  }
	
	public RDFResource getLine() {
		return line;
	}
	public RDFResource getRow() {
		return row;
	}
	public Integer getValue() {
		return value;
	}
	public void setLine(RDFResource line) {
		this.line = line;
	}
	public void setRow(RDFResource row) {
		this.row = row;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	
}

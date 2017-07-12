package fr.ign.cogit.geoxygene.sig3d.calculation.parcelDecomposition.geom;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.ILineString;

public class Strip {
	
	
	
	public Strip(){
		
	}
	
	
	private ILineString generatorEdge;
	private ILineString aimEdge;
	private ILineString initLatEdge;
	private ILineString endEdge;
	
	
	public ILineString getGeneratorEdge() {
		return generatorEdge;
	}
	public void setGeneratorEdge(ILineString generatorEdge) {
		this.generatorEdge = generatorEdge;
	}
	public ILineString getAimEdge() {
		return aimEdge;
	}
	public void setAimEdge(ILineString aimEdge) {
		this.aimEdge = aimEdge;
	}
	public ILineString getInitLatEdge() {
		return initLatEdge;
	}
	public void setInitLatEdge(ILineString initLatEdge) {
		this.initLatEdge = initLatEdge;
	}
	public ILineString getEndEdge() {
		return endEdge;
	}
	public void setEndEdge(ILineString endEdge) {
		this.endEdge = endEdge;
	}
	
	
	
	

}

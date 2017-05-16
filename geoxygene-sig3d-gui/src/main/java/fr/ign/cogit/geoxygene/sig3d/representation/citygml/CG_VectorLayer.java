package fr.ign.cogit.geoxygene.sig3d.representation.citygml;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityModel;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertCityGMLtoGeometry;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;

public class CG_VectorLayer extends VectorLayer {

	IDirectPosition dpMin;
	IDirectPosition dpMax;

	public CG_VectorLayer(CG_CityModel cityModel, String string) {
		super(string);
		this.setElements(cityModel.getElements());

		this.dpMax = cityModel.getDpUR();
		this.dpMin = cityModel.getDpLL();
		
		if (this.dpMin != null) {
			this.dpMin = new DirectPosition(dpMin.getX() - ConvertCityGMLtoGeometry.coordXIni,
					dpMin.getY() - ConvertCityGMLtoGeometry.coordYIni,
					dpMin.getZ() - ConvertCityGMLtoGeometry.coordZIni);
		}

		if (this.dpMax != null) {
			this.dpMax = new DirectPosition(dpMax.getX() - ConvertCityGMLtoGeometry.coordXIni,
					dpMax.getY() - ConvertCityGMLtoGeometry.coordYIni,
					dpMax.getZ() - ConvertCityGMLtoGeometry.coordZIni);
		}

		if (this.dpMin == null) {
			this.dpMin = new DirectPosition(ConvertCityGMLtoGeometry.xMin - ConvertCityGMLtoGeometry.coordXIni,
					ConvertCityGMLtoGeometry.yMin - ConvertCityGMLtoGeometry.coordYIni,
					ConvertCityGMLtoGeometry.zMin - ConvertCityGMLtoGeometry.coordZIni);
		}

		if (this.dpMax == null) {
			this.dpMax = new DirectPosition(ConvertCityGMLtoGeometry.xMax - ConvertCityGMLtoGeometry.coordXIni,
					ConvertCityGMLtoGeometry.yMax - ConvertCityGMLtoGeometry.coordYIni,
					ConvertCityGMLtoGeometry.zMax - ConvertCityGMLtoGeometry.coordZIni);
		}

	}
	
	  @Override
	  public Box3D get3DEnvelope() {
	    
	    
	    return this.determine3DEnvelope();
	    

	    

	  }
	  
	  
	  private  Box3D determine3DEnvelope(){
	    
	    if(Double.isNaN(this.dpMax.getX()) || Double.isInfinite(this.dpMax.getX())){
	        
	     return new Box3D(0, 0, 0, 1, 1, 1);
	    }
	    return new Box3D(this.dpMin, this.dpMax);
	    
	    
	}

}

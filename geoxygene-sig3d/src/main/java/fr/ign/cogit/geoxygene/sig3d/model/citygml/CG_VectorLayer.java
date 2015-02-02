package fr.ign.cogit.geoxygene.sig3d.model.citygml;

import fr.ign.cogit.geoxygene.api.spatial.coordgeom.IDirectPosition;
import fr.ign.cogit.geoxygene.sig3d.geometry.Box3D;
import fr.ign.cogit.geoxygene.sig3d.semantic.VectorLayer;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityModel;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

public class CG_VectorLayer extends VectorLayer {

  IDirectPosition dpMin;
  IDirectPosition dpMax;

  public CG_VectorLayer(CG_CityModel cityModel, String string) {
    super(cityModel, string);
    this.dpMin = cityModel.getDpLL();
    this.dpMax = cityModel.getDpUR();
    
    
    if (this.dpMin != null) {
      this.dpMin = new DirectPosition(dpMin.getX()
          - ConvertyCityGMLGeometry.coordXIni, dpMin.getY()
          - ConvertyCityGMLGeometry.coordYIni, dpMin.getZ()
          - ConvertyCityGMLGeometry.coordZIni);
    }

    
    
    if (this.dpMax != null) {
      this.dpMax = new DirectPosition(dpMax.getX()
          - ConvertyCityGMLGeometry.coordXIni, dpMax.getY()
          - ConvertyCityGMLGeometry.coordYIni, dpMax.getZ()
          - ConvertyCityGMLGeometry.coordZIni);
    }


    if (this.dpMin == null) {
      this.dpMin = new DirectPosition(ConvertyCityGMLGeometry.xMin
          - ConvertyCityGMLGeometry.coordXIni, ConvertyCityGMLGeometry.yMin
          - ConvertyCityGMLGeometry.coordYIni, ConvertyCityGMLGeometry.zMin
          - ConvertyCityGMLGeometry.coordZIni);
    }

    if (this.dpMax == null) {
      this.dpMax = new DirectPosition(ConvertyCityGMLGeometry.xMax
          - ConvertyCityGMLGeometry.coordXIni, ConvertyCityGMLGeometry.yMax
          - ConvertyCityGMLGeometry.coordYIni, ConvertyCityGMLGeometry.zMax
          - ConvertyCityGMLGeometry.coordZIni);
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

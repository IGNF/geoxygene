package fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.citygml4j.model.citygml.appearance.ParameterizedTexture;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_ParameterizedTexture extends CG_AbstractTexture {

  public CG_ParameterizedTexture(ParameterizedTexture pT) {
    super(pT);

    
    if (pT.isSetTarget()) {

      int nbTarget = pT.getTarget().size();

      for (int i = 0; i < nbTarget; i++) {
    	  
    	  String key = pT.getTarget().get(i).getUri();
    	  CG_AbstractTextureParameterization value =  CG_AbstractTextureParameterization
                  .generateAbstractTextureParameterization(pT.getTarget().get(i)
                          .getTextureParameterization());
    	  
    	  mapTarget.put(key, value);
    	  

   

      }

    }

  }




  
  public Map<String, CG_AbstractTextureParameterization> mapTarget= new HashMap<>();
  
  
  public CG_AbstractTextureParameterization findTexture(String id){
	  
	  CG_AbstractTextureParameterization cgAT = mapTarget.get(id);
	  
	  if(cgAT == null){
		  Collection<CG_AbstractTextureParameterization> coll = mapTarget.values();
			
			
			for(CG_AbstractTextureParameterization param : coll){
				
				if(param instanceof CG_TexCoordList) {
					CG_TexCoordList cGT = (CG_TexCoordList) param;
					
					
					for(CG_TextureCoordinates coordList : cGT.getTextureCoordinates()){
					//	System.out.println("Ring : " + coordList.getRing());
						if(coordList.getRing().equalsIgnoreCase(id)){
							cgAT = param;
							break;
						}
					}
					
					
				}else{

					System.out.println("CG_StylePreparator : cas non traité" + param.getClass() );	
				}
				
			}
	  }
	
	  return cgAT;
  }

  
  
  
  
  public Map<String, CG_AbstractTextureParameterization> getMapTarget() {
	return mapTarget;
}





public List<String> getTextureAssociation(){
	  
	  List<String> outList = new ArrayList<>(this.mapTarget.keySet());

    return outList;
  }



  public List<CG_AbstractTextureParameterization> getTarget() {
	  
	  List<CG_AbstractTextureParameterization> outList = new ArrayList<>(this.mapTarget.values());

    return outList;
  }
  


  public boolean isSetTarget() {
    return ((this.mapTarget != null) && (!this.mapTarget.isEmpty()));
  }

  public void unsetTarget() {
    this.mapTarget = null;
  }

}

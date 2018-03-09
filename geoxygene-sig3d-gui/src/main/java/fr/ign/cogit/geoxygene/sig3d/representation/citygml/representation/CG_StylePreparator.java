package fr.ign.cogit.geoxygene.sig3d.representation.citygml.representation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Shape3D;

import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractSurfaceData;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_AbstractTextureParameterization;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_GeoreferencedTexture;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_ParameterizedTexture;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_TexCoordList;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_TextureCoordinates;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.appearance.CG_X3DMaterial;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.GML_Geometry;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSolid;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_OrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomprim.GM_Solid;

public class CG_StylePreparator {

  @SuppressWarnings("unchecked")
  public static BranchGroup generateRepresentation(IGeometry geom,
      List<CG_AbstractSurfaceData> lCGA) {

    if (geom instanceof GM_MultiSolid<?>) {

      return CG_StylePreparator.generateRepresentation(
          (GM_MultiSolid<GM_Solid>) geom, lCGA);
    } else if (geom instanceof GM_Solid) {
      return CG_StylePreparator.generateRepresentation((GM_Solid) geom, lCGA);
    } else if (geom instanceof GM_MultiSurface<?>) {
      return CG_StylePreparator.generateRepresentation(
          (GM_MultiSurface<GM_OrientableSurface>) geom, lCGA);

    } else if (geom instanceof GM_OrientableSurface) {

      BranchGroup bg = new BranchGroup();
      bg.addChild(CG_StylePreparator.generateRepresentation(
          (GM_OrientableSurface) geom, lCGA));

      return bg;

    }
    return null;
  }

  public static BranchGroup generateRepresentation(GM_MultiSolid<GM_Solid> sol,
      List<CG_AbstractSurfaceData> lCGA) {
    int nbSol = sol.size();

    BranchGroup bg = new BranchGroup();
    for (int i = 0; i < nbSol; i++) {

      bg.addChild(CG_StylePreparator.generateRepresentation(sol.get(i), lCGA));
    }

    return bg;
  }

  public static BranchGroup generateRepresentation(GM_Solid sol,
      List<CG_AbstractSurfaceData> lCGA) {
    int nbSurf = sol.getFacesList().size();

    BranchGroup bg = new BranchGroup();

    for (int i = 0; i < nbSurf; i++) {
      bg.addChild(CG_StylePreparator.generateRepresentation(sol.getFacesList()
          .get(i), lCGA));
    }

    return bg;
  }

  public static BranchGroup generateRepresentation(
	      List<? extends IOrientableSurface> multi,
	      List<CG_AbstractSurfaceData> lCGA) {
	    BranchGroup bg = new BranchGroup();
	    int nbSurf = multi.size();

	    for (int i = 0; i < nbSurf; i++) {

	      bg.addChild(CG_StylePreparator.generateRepresentation(multi.get(i), lCGA));

	    }

	    return bg;
	  }


  public static BranchGroup generateRepresentation(
      GM_MultiSurface<GM_OrientableSurface> multi,
      List<CG_AbstractSurfaceData> lCGA) {
	  
	  return generateRepresentation(multi.getList(), lCGA);
  }

  public static Shape3D generateRepresentation(GM_OrientableSurface oS,
      List<CG_AbstractSurfaceData> lCGA) {

	  GML_Geometry poly = (GML_Geometry) oS;

    List<Object> llOPolygon = new ArrayList<Object>();
    llOPolygon = CG_StylePreparator.retrieveStyle(poly, lCGA);

    CG_StyleGenerator sG = new CG_StyleGenerator(poly, llOPolygon);

    return sG.getShape();
  }

  private static List<Object> retrieveStyle(GML_Geometry poly,
      List<CG_AbstractSurfaceData> lCGA) {

    return CG_StylePreparator.retrieveStyle(poly.getID(), lCGA);

  }

  private static List<Object> retrieveStyle(String ID,
      List<CG_AbstractSurfaceData> lCGA) {

	  String searchedID = "#"+ID;
    // On génère les représentations qui sont appliquables
    List<Object> lRep = new ArrayList<Object>();

    int nbCGA = lCGA.size();

    // On récupère les éléments qui concernent le ring en cours
    for (int i = 0; i < nbCGA; i++) {

      CG_AbstractSurfaceData cABS = lCGA.get(i);

      if (cABS instanceof CG_X3DMaterial) {

        if ((((CG_X3DMaterial) cABS).getTarget().contains(searchedID))){

          lRep.add(cABS);

        }
        continue;

      } else if (cABS instanceof CG_GeoreferencedTexture) {

        if (((CG_GeoreferencedTexture) cABS).getTarget().contains(searchedID)) {
          lRep.add(cABS);
        }
        continue;

      } else if (cABS instanceof CG_ParameterizedTexture) {
    	  
    	  CG_ParameterizedTexture parameterizedTexture = ((CG_ParameterizedTexture) cABS);
    	  
    	  
    	  if(parameterizedTexture.getMapTarget().containsKey(searchedID)){
    		  lRep.add(cABS);
    	  }else{
					Collection<CG_AbstractTextureParameterization> coll = parameterizedTexture.getMapTarget().values();
					
					
					for(CG_AbstractTextureParameterization param : coll){
						
						if(param instanceof CG_TexCoordList) {
							CG_TexCoordList cGT = (CG_TexCoordList) param;
							
							
							for(CG_TextureCoordinates coordList : cGT.getTextureCoordinates()){
								//System.out.println("Ring : " + coordList.getRing());
								if(coordList.getRing().equalsIgnoreCase(searchedID)){
									 lRep.add(cABS);
								}
							}
							
							
						}else{

							System.out.println("CG_StylePreparator : cas non traité" + param.getClass() );	
						}
						
					}
    		  
    	  }
    	  
    	  
    	


      } else {
        System.out.println("Type non géré : " + cABS.getClass());
      }

    }

    return lRep;
  }



}

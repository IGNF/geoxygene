package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.citygml.building.CeilingSurfaceImpl;
import org.citygml4j.impl.citygml.building.ClosureSurfaceImpl;
import org.citygml4j.impl.citygml.building.FloorSurfaceImpl;
import org.citygml4j.impl.citygml.building.GroundSurfaceImpl;
import org.citygml4j.impl.citygml.building.OpeningPropertyImpl;
import org.citygml4j.impl.citygml.building.RoofSurfaceImpl;
import org.citygml4j.impl.citygml.building.WallSurfaceImpl;
import org.citygml4j.model.citygml.building.BoundarySurface;
import org.citygml4j.model.citygml.building.CeilingSurface;
import org.citygml4j.model.citygml.building.ClosureSurface;
import org.citygml4j.model.citygml.building.Door;
import org.citygml4j.model.citygml.building.FloorSurface;
import org.citygml4j.model.citygml.building.GroundSurface;
import org.citygml4j.model.citygml.building.InteriorWallSurface;
import org.citygml4j.model.citygml.building.Opening;
import org.citygml4j.model.citygml.building.OpeningProperty;
import org.citygml4j.model.citygml.building.RoofSurface;
import org.citygml4j.model.citygml.building.WallSurface;
import org.citygml4j.model.citygml.building.Window;
import org.citygml4j.model.citygml.core.CityObject;

import fr.ign.cogit.geoxygene.api.spatial.geomaggr.IMultiSurface;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IOrientableSurface;
import fr.ign.cogit.geoxygene.spatial.geomaggr.GM_MultiSurface;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_CityObject;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertToCityGMLGeometry;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertyCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public abstract class CG_AbstractBoundarySurface extends CG_CityObject {

  protected IMultiSurface<IOrientableSurface> lod2MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod3MultiSurface;
  protected IMultiSurface<IOrientableSurface> lod4MultiSurface;
  protected List<CG_AbstractOpening> opening;
  
  
  public CG_AbstractBoundarySurface(){
    super();
  }
  
  public CityObject export(){
    
    
    BoundarySurface bsOut = null;  
    
    
    if(this instanceof CG_RoofSurface){
      
      bsOut = new RoofSurfaceImpl();
      
      
    }else if (this instanceof CG_WallSurface){
      
      bsOut = new WallSurfaceImpl();
      
      
    }else if(this instanceof CG_GroundSurface){
      
      bsOut = new GroundSurfaceImpl();
      
      
    }else if(this instanceof CG_ClosureSurface){
      
      bsOut = new ClosureSurfaceImpl();
      
      
    }else if(this instanceof CG_CeilingSurface){
      
      bsOut = new CeilingSurfaceImpl();
      
      
    }else if(this instanceof CG_InteriorWallSurface){
      
      bsOut = new WallSurfaceImpl();
      
      
    }else if(this instanceof CG_FloorSurface){
      
      bsOut = new FloorSurfaceImpl();
      
      
    }
  
    if (this.isSetLod2MultiSurface()) {
      bsOut.setLod2MultiSurface(ConvertToCityGMLGeometry.convertMultiSurfaceProperty(this.getLod2MultiSurface()));
    }

    if (this.isSetLod3MultiSurface()) {
      bsOut.setLod3MultiSurface(ConvertToCityGMLGeometry.convertMultiSurfaceProperty(this.getLod3MultiSurface()));
    }


    
    if (this.isSetLod4MultiSurface()) {
      bsOut.setLod4MultiSurface(ConvertToCityGMLGeometry.convertMultiSurfaceProperty(this.getLod4MultiSurface()));
    }



    if (this.isSetOpening()) {
      
      bsOut.setOpening(new ArrayList<OpeningProperty>());
      
      int nbOp = this.getOpening().size();
      
      for (int i = 0; i < nbOp; i++) {

        CG_AbstractOpening oPP = this.getOpening().get(i);

        if (oPP instanceof CG_Door) {

          OpeningProperty op = new OpeningPropertyImpl();
          
          op.setOpening((Door)oPP.export());
          
          bsOut.addOpening(op);
          

        } else if (oPP instanceof CG_Window) {

          OpeningProperty op = new OpeningPropertyImpl();
          
          op.setOpening((Window)oPP.export());
          
          bsOut.addOpening(op);
        }

      }
      
    }
    return bsOut;
    
  }

  public static CG_AbstractBoundarySurface generateBoundarySurface(
      BoundarySurface bs) {

    if (bs instanceof RoofSurface) {

      return new CG_RoofSurface((RoofSurface) bs);

    } else if (bs instanceof WallSurface) {

      return new CG_WallSurface((WallSurface) bs);

    } else if (bs instanceof GroundSurface) {

      return new CG_GroundSurface((GroundSurface) bs);

    } else if (bs instanceof ClosureSurface) {

      return new CG_ClosureSurface((ClosureSurface) bs);

    } else if (bs instanceof CeilingSurface) {

      return new CG_CeilingSurface((CeilingSurface) bs);

    } else if (bs instanceof InteriorWallSurface) {

      return new CG_InteriorWallSurface((InteriorWallSurface) bs);

    } else if (bs instanceof FloorSurface) {

      return new CG_FloorSurface((FloorSurface) bs);

    }

    System.out.println("Classe inconnue " + bs.getClass().toString());

    return null;
  }

  public CG_AbstractBoundarySurface(BoundarySurface bS) {
    super(bS);
    if (bS.isSetLod2MultiSurface()) {
      this.setLod2MultiSurface(ConvertyCityGMLGeometry
          .convertGMLMultiSurface(bS.getLod2MultiSurface()));
    }

    if (bS.isSetLod3MultiSurface()) {
      this.setLod3MultiSurface(ConvertyCityGMLGeometry
          .convertGMLMultiSurface(bS.getLod3MultiSurface()));
    }

    if (bS.isSetLod4MultiSurface()) {
      this.setLod4MultiSurface(ConvertyCityGMLGeometry
          .convertGMLMultiSurface(bS.getLod4MultiSurface()));
    }

    if (bS.isSetOpening()) {
      List<OpeningProperty> oP = bS.getOpening();
      int nbOp = oP.size();
      for (int i = 0; i < nbOp; i++) {

        Opening oPP = oP.get(i).getOpening();

        if (oPP instanceof Door) {

          this.getOpening().add(new CG_Door((Door) oPP));

        } else if (oPP instanceof Window) {

          this.getOpening().add(new CG_Window((Window) oPP));
        }

      }

    }

  }

  /**
   * Gets the value of the lod2MultiSurface property.
   * 
   * @return possible object is {@link IMultiSurface<IOrientableSurface> }
   * 
   */
  public IMultiSurface<IOrientableSurface> getLod2MultiSurface() {
    return this.lod2MultiSurface;
  }

  /**
   * Sets the value of the lod2MultiSurface property.
   * 
   * @param value allowed object is {@link IMultiSurface<IOrientableSurface> * }
   * 
   */
  public void setLod2MultiSurface(IMultiSurface<? extends IOrientableSurface> value) {
    this.lod2MultiSurface = new GM_MultiSurface<IOrientableSurface>();
    
    this.lod2MultiSurface.addAll(value);
  }

  public boolean isSetLod2MultiSurface() {
    return (this.lod2MultiSurface != null);
  }

  /**
   * Gets the value of the lod3MultiSurface property.
   * 
   * @return possible object is {@link IMultiSurface<IOrientableSurface> }
   * 
   */
  public IMultiSurface<IOrientableSurface> getLod3MultiSurface() {
    return this.lod3MultiSurface;
  }

  /**
   * Sets the value of the lod3MultiSurface property.
   * 
   * @param value allowed object is {@link IMultiSurface<IOrientableSurface> * }
   * 
   */
  public void setLod3MultiSurface(IMultiSurface<IOrientableSurface> value) {
    this.lod3MultiSurface = value;
  }

  public boolean isSetLod3MultiSurface() {
    return (this.lod3MultiSurface != null);
  }

  /**
   * Gets the value of the lod4MultiSurface property.
   * 
   * @return possible object is {@link IMultiSurface<IOrientableSurface> }
   * 
   */
  public IMultiSurface<IOrientableSurface> getLod4MultiSurface() {
    return this.lod4MultiSurface;
  }

  /**
   * Sets the value of the lod4MultiSurface property.
   * 
   * @param value allowed object is {@link IMultiSurface<IOrientableSurface> * }
   * 
   */
  public void setLod4MultiSurface(IMultiSurface<IOrientableSurface> value) {
    this.lod4MultiSurface = value;
  }

  public boolean isSetLod4MultiSurface() {
    return (this.lod4MultiSurface != null);
  }

  public List<CG_AbstractOpening> getOpening() {
    if (this.opening == null) {
      this.opening = new ArrayList<CG_AbstractOpening>();
    }
    return this.opening;
  }

  public boolean isSetOpening() {
    return ((this.opening != null) && (!this.opening.isEmpty()));
  }

  public void unsetOpening() {
    this.opening = null;
  }

}

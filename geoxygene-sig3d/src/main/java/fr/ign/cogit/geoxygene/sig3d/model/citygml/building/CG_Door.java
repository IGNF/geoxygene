package fr.ign.cogit.geoxygene.sig3d.model.citygml.building;

import java.util.ArrayList;
import java.util.List;

import org.citygml4j.impl.citygml.building.DoorImpl;
import org.citygml4j.model.citygml.building.Door;
import org.citygml4j.model.citygml.core.CityObject;

import fr.ign.cogit.geoxygene.sig3d.model.citygml.core.CG_Address;
import fr.ign.cogit.geoxygene.sig3d.model.citygml.geometry.ConvertToCityGMLGeometry;

/**
 * 
 * @author MBrasebin
 * 
 */
public class CG_Door extends CG_AbstractOpening {

  protected List<CG_Address> address;

  public CG_Door(Door d) {
    super(d);

    int nbAdress = d.getAddress().size();

    for (int i = 0; i < nbAdress; i++) {

      this.getAddress().add(new CG_Address(d.getAddress().get(0).getAddress()));
    }

  }

  public List<CG_Address> getAddress() {
    if (this.address == null) {
      this.address = new ArrayList<CG_Address>();
    }
    return this.address;
  }

  public boolean isSetAddress() {
    return ((this.address != null) && (!this.address.isEmpty()));
  }

  public void unsetAddress() {
    this.address = null;
  }

  @Override
  public CityObject export() {
    Door d = new DoorImpl();

    if (this.isSetLod3MultiSurface()) {

      d.setLod3MultiSurface(ConvertToCityGMLGeometry
          .convertMultiSurfaceProperty(this.getLod3MultiSurface()));

    }

    if (this.isSetLod4MultiSurface()) {

      d.setLod4MultiSurface(ConvertToCityGMLGeometry
          .convertMultiSurfaceProperty(this.getLod4MultiSurface()));

    }

    // TODO Auto-generated method stub
    return d;
  }

}

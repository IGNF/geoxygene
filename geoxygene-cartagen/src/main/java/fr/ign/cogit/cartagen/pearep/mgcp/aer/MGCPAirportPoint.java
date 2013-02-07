package fr.ign.cogit.cartagen.pearep.mgcp.aer;

import java.awt.Image;
import java.net.URL;

import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.cartagen.pearep.mgcp.MGCPFeature;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;

public class MGCPAirportPoint extends MGCPFeature implements IPointOfInterest {

  private String name, nature;
  private String imageFileName = "/images/symbols/airport.png";
  private Image symbol;

  public MGCPAirportPoint(IPoint geom) {
    super();
    this.setInitialGeom(geom);
    this.setEliminated(false);
    this.setGeom(geom);
    ExternalGraphic graphic = new ExternalGraphic();
    URL url = MGCPAirportPoint.class.getResource(imageFileName);
    graphic.setHref(url.toString());
    this.symbol = graphic.getOnlineResource().getScaledInstance(20, 20,
        Image.SCALE_SMOOTH);
  }

  @Override
  public IPoint getGeom() {
    return (IPoint) this.geom;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getNature() {
    return nature;
  }

  @Override
  public Image getSymbol() {
    return symbol;
  }

}

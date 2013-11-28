package fr.ign.cogit.geoxygene.osm.schema.urban;

import java.awt.Image;
import java.net.URL;
import java.util.Date;

import fr.ign.cogit.cartagen.core.genericschema.misc.IPointOfInterest;
import fr.ign.cogit.geoxygene.api.spatial.geomprim.IPoint;
import fr.ign.cogit.geoxygene.api.spatial.geomroot.IGeometry;
import fr.ign.cogit.geoxygene.osm.schema.OsmGeneObjPoint;
import fr.ign.cogit.geoxygene.style.ExternalGraphic;

public class OsmPointOfInterest extends OsmGeneObjPoint implements
    IPointOfInterest {

  private String name, nature;
  private String imageFileName = "/images/symbols/poi.png";
  private Image symbol;

  public OsmPointOfInterest(String contributor, IGeometry geom, int id,
      int changeSet, int version, int uid, Date date) {
    super(contributor, geom, id, changeSet, version, uid, date);
  }

  public OsmPointOfInterest(IPoint point) {
    super(point);
    ExternalGraphic graphic = new ExternalGraphic();
    URL url = OsmPointOfInterest.class.getResource(imageFileName);
    graphic.setHref(url.toString());
    this.symbol = graphic.getOnlineResource().getScaledInstance(20, 20,
        Image.SCALE_SMOOTH);
  }

  @Override
  public String getName() {
    if ((nature == null) && (getTags().containsKey("name")))
      name = getTags().get("name");
    return name;
  }

  @Override
  public String getNature() {
    if (nature == null) {
      if (getTags().containsKey("amenity"))
        nature = getTags().get("amenity");
      if (getTags().containsKey("shop"))
        nature = getTags().get("shop");
      if (getTags().containsKey("highway"))
        nature = getTags().get("highway");
    }
    return nature;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setNature(String nature) {
    this.nature = nature;
  }

  @Override
  public Image getSymbol() {
    return symbol;
  }

  public void setSymbol(Image symbol) {
    this.symbol = symbol;
  }

}

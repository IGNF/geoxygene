
public OsmGeneObj createGeneObj(Class<?> classObj, OSMResource resource, Collection<OSMResource> nodes) {
    if (IRoadLine.class.isAssignableFrom(classObj)) {
      ILineString line = OsmGeometryConversion.convertOSMLine((OSMWay) resource.getGeom(), nodes);
      return (OsmGeneObj) this.createRoadLine(line, 0);
    }
    if (ICable.class.isAssignableFrom(classObj)) {
      ILineString line = OsmGeometryConversion.convertOSMLine((OSMWay) resource.getGeom(), nodes);
      return (OsmGeneObj) this.createCable(line);
    }
    // ...
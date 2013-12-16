
OsmMatching lakematching = new OsmMatching("natural", IWaterArea.class, GeometryType.POLYGON);
lakematching.addTagValue("water");
lakematching.addTagValue("lake");
this.matchings.add(lakematching);
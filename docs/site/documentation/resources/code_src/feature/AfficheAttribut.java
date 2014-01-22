
// IFeature feature = itCollection.next();
for (int k = 0; k < feature.getFeatureType().getFeatureAttributes().size(); k++) {
  GF_AttributeType attributeType = feature.getFeatureType().getFeatureAttributes().get(k);
  if (attributeType.getMemberName().equals("OBJECTID")) {
      System.out.println(feature.getAttribute("OBJECTID"));
  }
}
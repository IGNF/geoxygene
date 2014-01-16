
IFeature objetCT1 = itObjetsCT1PourUnLien.next();
for (int k = 0; k < objetCT1.getFeatureType().getFeatureAttributes().size(); k++) {
  GF_AttributeType attributeType = objetCT1.getFeatureType().getFeatureAttributes().get(k);
  String attributArc1 = attributeType.getMemberName();
  if (attribut.equals("OBJECTID1") && attributArc1.equals("OBJECTID")) {
      valAttribute[cpt] = objetCT1.getAttribute("OBJECTID");
  }
}
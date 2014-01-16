// arcsBruts est une population de DefaultFeature (IPopulation<DefaultFeature>)
for(Arc arc : arcsBruts) {
    GF_FeatureType ft = arc.getFeatureType();
    List<GF_AttributeType> listAttribut = ft.getFeatureAttributes();
    for (int j = 0; j < listAttribut.size(); j++) {
        System.out.println("attribut : " + listAttribut.get(j).getMemberName());
    }
}
import fr.ign.cogit.geoxygene.util.attribute.AttributeManager;
    ...

    IFeatureCollection<IFeature> collRoutes = new FT_FeatureCollection<>(); 

    for(){

        DefaultFeature route = new DefaultFeature(new GM_ .... );

        AttributeManager.addAttribute(route , "Largeur", 3, "Integer");
        AttributeManager.addAttribute(route , "Nom", "Rue de la Paix", "String");
        AttributeManager.addAttribute(route , "Longueur", 12.3, "Double");

        collRoutes.add(route);

    }

    ...

    // Export en fichier shape par exemple
    CoordinateReferenceSystem crs = CRS.decode("EPSG:3035");
    ShapefileWriter.write(collRoutes , "D:\\DATA\\routes.shp", crs);

    ...

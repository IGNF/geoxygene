
// Créer un featuretype du jeu correspondant
    FeatureType newFeatureType = new FeatureType();
    newFeatureType.setTypeName("troncon");
    newFeatureType.setGeometryType(ILineString.class);
                
    AttributeType idTroncon = new AttributeType("idTroncon", "String");
    AttributeType typeLargeurVoie = new AttributeType("largeurV", "double");
    AttributeType numVoie = new AttributeType("numVoie", "int");
    newFeatureType.addFeatureAttribute(idTroncon);
    newFeatureType.addFeatureAttribute(typeLargeurVoie);
    newFeatureType.addFeatureAttribute(numVoie);
    
    // Création d'un schéma associé au featureType
    SchemaDefaultFeature schema = new SchemaDefaultFeature();
    schema.setFeatureType(newFeatureType);
    
    newFeatureType.setSchema(schema);
                
    Map<Integer, String[]> attLookup = new HashMap<Integer, String[]>(0);
    attLookup.put(new Integer(0), new String[] { idTroncon.getNomField(), idTroncon.getMemberName() });
    attLookup.put(new Integer(1), new String[] { typeLargeurVoie.getNomField(), typeLargeurVoie.getMemberName() });
    attLookup.put(new Integer(2), new String[] { numVoie.getNomField(), numVoie.getMemberName() });
    schema.setAttLookup(attLookup);
    
    // Création de la population
    Population<DefaultFeature> entrees = new Population<DefaultFeature>(false, "entrees", DefaultFeature.class, true);
    entrees.setFeatureType(newFeatureType);
    
    // On ajoute les defaults features à la collection
    for () {
    DefaultFeature n = entrees.nouvelElement(first.toGM_Point());
        n.setSchema(schema);
        Object[] attributes = new Object[] { t.getId(), t.getLargeurVoie(), new Integer(i + 1) };
        n.setAttributes(attributes);
    }
    
    
    // Export en fichier shape par exemple
    CoordinateReferenceSystem crs = CRS.decode("EPSG:3035");
    ShapefileWriter.write(entrees, "D:\\DATA\\entrees.shp", crs);


    // Affichage dans l'interface graphique de GeOxygene
    ...
    ProjectFrame p1 = this.application.getFrame().newProjectFrame();
    Layer l1 = p1.addUserLayer(entrees, "Entrees", null);
    ...

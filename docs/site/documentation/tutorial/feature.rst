

Feature
###########                               

GeOxygene propose un schéma logique générique pour l'exploitation des données. Il est basé sur le concept OGC d'objets géographiques
(FT_Feature) et sur les concepts de jeu de données (DataSet), de populations d'objets d'un même type (Population), de collections d'objets
quelconques (FT_FeatureCollection). La dimension spatiale des objets géographiques repose sur la large gamme de primitives géométriques 
et topologiques spécifiée oar la norme ISO 19107. 


                                         
les classes dite géographiques héritent soit de « FT_Feature » (routes, rivières…) soit de DefaultFeature
  
.. .. container:: centerside
     
..    .. figure:: /documentation/resources/img/feature/DC-DefaultFeature.png
..       :width: 1000px
       
..       Figure 1 : Diagramme de classe Feature


.. container:: centerside

   .. figure:: /documentation/resources/img/feature/DC-FeatureType.png
      :width: 800px
       
       Figure 2 : Diagramme de classe Feature


Définitions
================

Feature
^^^^^^^^^^

Un *Feature* est un objet géographique. Définition d'un feature (ISO 19101) : 

A feature is an abstraction of a real world phenomenon; it is a geographic feature if it is associated with a location relative to the Earth. 

A feature is quite simply something that can be drawn on a map

Références OGC : 
* http://www.opengeospatial.org/standards/sfa - Simple Feature Access - Part 1: Common Architecture
* http://www.opengeospatial.org/standards/as - Topic 5 - Features

Par exemple : troncon_hydrographique, noeud _hydrographique, cours_d_eau, toponyme_d_hydrographie_surfacique, point_eau_isole, laisse

FeatureType
^^^^^^^^^^^^^^
définit d'un modèle

FeatureType provides metadata model describing the represented information. This is considered “metadata” as it is a description of the information stored in the features.

FeatureType is used when:

    Accessing information as a description of the available attribute names when making a Expression
    Creating a new feature you can check to ensure your values are valid
 
 
Occasionally you have two features that have a lot in common. You may have the LAX airport in Los Angeles and the SYD airport in Sydney. 
Because these two features have a couple of things in common it is nice to group them together - in Java we would create a Class called Airport. 
On a map we will create a Feature Type called Airport.

<FeatureType id="1">
    <typeName>route</typeName>
    <definition>les routes sur lesquelles on roule</definition>
    <positionInitiale>10,10</positionInitiale>
    <nomClasse>donnees.sandrine.classesGenerees.Route</nomClasse>
    <isExplicite>1</isExplicite>
    <AttributeType id="1">
        <memberName>revêtement</memberName>
    <valueType>text</valueType>
    </AttributeType>
    <AttributeType id="2">
        <memberName>geom</memberName>
        <valueType>polyligne</valueType>        
    </AttributeType>
</FeatureType>

<FeatureType id="2">
    <typeName>bâtiment</typeName>
    <definition>les maisons dans lesquelles on vit</definition>
    <positionInitiale>200,10</positionInitiale>
    <isExplicite>1</isExplicite>
    <nomClasse>donnees.sandrine.classesGenerees.Route</nomClasse>
</FeatureType>


FT_Feature
^^^^^^^^^^^^

 est la classe mère des classes géographiques. Des *FT_Feature* peuvent s'agréger en *FT_FeatureCollection*, 
classe qui représente donc un groupe de *FT_Feature* et qui porte des méthodes d'indexation spatiale.


4. Property et Attribute

Attributes of (either contained in or associated to) a feature describe measurable or describable properties about this entity.


5. FeatureCollection

6. Population

7. DataSet

8. *SchemaConceptuelJeu* : schéma conceptuel d'un jeu de données. Correspond à la notion "Application schema" dans les normes ISO, 
qui n'est pas définie par  un type de données formel. Nous définissons ici ce type comme un ensemble de classes et de 
relations (associations et héritage) comportant des proprietés (attributs, rôles, opérations) et des contraintes.

Attention dans GeoTools "schema" designe la structure d'un feature et non pas d'un jeu de données.

*SchemaDefaultFeature* : Description du schéma logique d'un DefaultFeature (table de SGBD). 
Ce schéma contient le nom de la table (ou du fichier GML ou autre...) et une lookup table indiquant le nom des attributs 
et leur emplacement dans la table attributes[] du defaultFeature. 

Dans le cas où une métadonnée de structure était disponible (soit stockée quelque part soit donnée par l'utilisateur lors du chargement), 
ce schéma contient aussi une référence vers le schéma conceptuel : le featureType correspondant au DefaultFeature.






AssociationType
^^^^^^^^^^^^^^^^^^

<AssociationType id="3">
    <typeName>permet d'accéder à</typeName>
    <definition>relation sémantique indiquant qu'un équipement est accessible à partir d'un tronçon de route</definition>
    <isAggregation>0</isAggregation>
    <idLinkBetween>1</idLinkBetween>
    <idLinkBetween>2</idLinkBetween>    
    <AssociationRole id="1">
        <featureTypeId>1</featureTypeId>
        <memberName>permet l'accès à</memberName>
        <cardMin>0</cardMin>
        <cardMax>n</cardMax>
        <isComponent>0</isComponent>
        <isComposite>0</isComposite>
    </AssociationRole>
    <AssociationRole id="2">
        <featureTypeId>2</featureTypeId>
        <memberName>est accedé par</memberName>
        <cardMin>0</cardMin>
        <cardMax>n</cardMax>
        <isComponent>0</isComponent>
        <isComposite>0</isComposite>
    </AssociationRole>      
</AssociationType>



Module
=============
Ce module contient l'implémentation des objets géographiques. 

DefaultFeature

FT_Feature









Trucs et astuces
--------------------

Afficher la liste des attributs d'une population
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^


// arcsBruts est une population de DefaultFeature (IPopulation<DefaultFeature>)
for(Arc arc : arcsBruts) {
    GF_FeatureType ft = arc.getFeatureType();
    List<GF_AttributeType> listAttribut = ft.getFeatureAttributes();
    for (int j = 0; j < listAttribut.size(); j++) {
        System.out.println("attribut : " + listAttribut.get(j).getMemberName());
    }
}


Création d'une collection de features (pour un export en shapefile ou un affichage dans l'interface graphique)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

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


Ajout d'un attribut dans un DefaultFeature (sans cohérence globale au niveau du schéma de la collection)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
L'export de la collection est possible si les objets ont les mêmes attributs


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







Blog Archive
##############

2016
------

* October 11, 2016 - **Version 1.8 released**

    This is the last stable version supporting Java 1.7 and GeoTools 8.4
    From the next snapshot (1.9-SNAPSHOT), the JDK 8 is forced in the main pom and the current set of dependencies my be updated (besides GeoTools which is upgraded to v15.1)

    * Line morphing algorithms in cartagen plugin
    * Functionalities to analyse the OSM contributors of a loaded OSM file
    * API loaders for Twitter, Foursquare, Panoramio and FlickR data
    * Expressive rendering tools and extension of the OGC specifications SLD and SE coming from the MapStyle project in the OpenGL rendering engine.
	* Added linear stylisation to Line Symbolizer through the SE extension `<ExpressiveStroke>`.
	* Added patch-fill rendering to Polygon Symbolizer, with graph-cut or alpha blending through the SE extension `<ExpressiveFill>`.
	* Added the possibility to set a global background texture to the rendered map through the SLD extension `<Background>`.
	* Added Overlay, Multiply, Normal and HighTone blending modes between layers in Symbolizers through the XML tag `<BlendingMode>`.
	* Enable animation of Raster Symbolizer + Tide simulation based on a DTM data, through the SE extensions `<Animation>` and `<Tide>`.


2015
------

* October 19, 2015 - **Version 1.7 released**

    Among the novelties :
    
    * Students works : dot density analysis, measures of complexity of map. 
    
    * Minkowski 2D operations (sum between two polygons A and B, subtraction of A and B with reference to the center of mass B, etc.)
    
    * Generic algorithms (linear and nonlinear) of the least squares estimator
    
    * Polygons squaring by least squares compensation
    
    * A "lenses" plugin that can : show a more detailed view over a less detailed layer, view a portion of the raster layer over the vector one, etc.
    
    * OpenGL rendered 2D GUI added.
    
    * Probably last version using java 7  


* October 1, 2015 - **Mapstyle demonstration** 

    Two demonstrations of using GeOxygene library at the GeoVIS Workshop, ISPRS GeoSpatial Week. Watch the video !



2014
------

* June 5, 2014 - **CartAGen demonstration**

    CartAGen is a component-based research platform dedicated to generalisation. It based on GeOxygene library. A demonstration entitled
    *Multi-Agent Multi-Level Cartographic Generalisation in CartAGen* will be held at `PAAMS'14 <http://www.paams.net>`_

* February 26, 2014 - **A presentation tools to estimate imprecision on geographical data**

    Jean-François Girres intervention at *Géoséminaire* entitled `Fondements théoriques et méthodologiques de la qualification des données géographiques
    <http://geoseminaire2014.teledetection.fr/index.php/programme/menu-downloads-presentations-2014/file/2-download-fondements-theoriques-et-methodologiques-de-la-qualification.html>`_ 
    showcased tools developed in his thesis to estimate geometric measurements imprecision on geographical data.
    

  
2013
------

* June 10, 2013 - **What’s New in GeOxygene 1.5**
 
    The presentation on GeOxygene 1.5 at `FROG 2013 <http://frog.osgeo.fr>`_ in Saint-Mandé will focus  
    on the new features, including a description of the three new modules : geoxygene-semio (semiology), 
    geoxygene-sig3d (3D) and geoxygene-cartagen (generalization).
      
    Presentation Slides : `"GeOxygene : une plate-forme de recherche pour le développement d'applications SIG" <https://github.com/OSGeo-fr/frog2013/raw/master/presentations/technique/01-van_damme/FROG13-GeOxygene.pdf>`_ 


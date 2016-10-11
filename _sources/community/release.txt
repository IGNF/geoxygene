

GeOxygene releases
####################

The current development version is GeOxygene-1.9-SNAPSHOT. The latest stable release is **GeOxygene 1.8**.

Releases can be obtained from the `GitHub releases <https://github.com/IGNF/geoxygene/releases>`_ .
Snapshots and releases are also available from the `COGIT repositories <https://forge-cogit.ign.fr/nexus/content/repositories/>`_.


History of Changes
-------------------

Version 1.8 (2016/10/11)
^^^^^^^^^^^^^^^^^^^^^^^^^^

   * Last version using java 7
  
   * Line morphing algorithms in cartagen plugin

   * Functionalities to analyse the OSM contributors of a loaded OSM file

   * API loaders for Twitter, Foursquare, Panoramio and FlickR data

   * Expressive rendering tools and extension of the OGC specifications SLD and SE coming from the MapStyle project in the OpenGL rendering engine.
      * Added linear stylisation to Line Symbolizer through the SE extension `<ExpressiveStroke>`.
      * Added patch-fill rendering to Polygon Symbolizer, with graph-cut or alpha blending through the SE extension `<ExpressiveFill>`.
      * Added the possibility to set a global background texture to the rendered map through the SLD extension `<Background>`.
      * Added Overlay, Multiply, Normal and HighTone blending modes between layers in Symbolizers through the XML tag `<BlendingMode>`.
      * Enable animation of Raster Symbolizer + Tide simulation based on a DTM data, through the SE extensions `<Animation>` and `<Tide>`.


Version 1.7 (2015/10/19)
^^^^^^^^^^^^^^^^^^^^^^^^^^

  * Students works : dot density analysis, measures of complexity of map. 
    
  * Minkowski 2D operations (sum between two polygons A and B, subtraction of A and B with reference to the center of mass B, etc.)
    
  * Genetic algorithms (linear and nonlinear) of the least squares estimator
    
  * Polygons squaring by least squares compensation
    
  * A "lenses" plugin that can : show a more detailed view over a less detailed layer, view a portion of the raster layer over the vector one, etc.
    
  * OpenGL rendered 2D GUI added.
    
  * Probably last version using java 7  


Version 1.6 (2014/01/23)
^^^^^^^^^^^^^^^^^^^^^^^^^^

  * Added the EstIM model developed during the Jean-François Girres PhD, 2012. 
  
  * The old GeOxygene 2D graphical interface and CartaGen merge 
  
  * Added a new OSM module 
  
  * Added editor, import and export style in GeOxygene 2D GUI


Version 1.5 (2013/01/07)
^^^^^^^^^^^^^^^^^^^^^^^^^^
  
  * Added 3D module
  
  * Added semiology module
  
  * Added generalization module
  
  * Added data matching process using theory of Evidence
  
  * Adopted multi-module Maven architecture

Version 1.4 (2009/06/22)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

  * Added an ISO1909 General Feature Model implementation. (cogit)

  * Added a SLD (Styled Layer Descriptor) implementation based on the OGC 02-070 implementation specification. (cogit)

  * Added an Hibernate support. (cogit)

  * Added a first version of the new GeOxygene interface. (cogit)

  * Added some code examples to learn how to use the topological map and the data matching tool. (cogit)

  * Added a Java 6 support in regard to Java 6 specifications (types, annotations, etc.). (cogit)

Version 1.3 (2008/01/31)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

  * Adding a data matching tool (a documentation about this tool is available). (cogit)

  * Adding a simple method to create minimal spanning trees. (cogit)

  * Adding a generator of DBMS tables and XML mapping files from Java classes. (cogit)

  * Improvement of the GeOxygeneReader methods to partially take into account the GM_Aggregate geometry type (useful to visualize data matching results). (cogit)

Version 1.2 (2007/08/08)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^

  * Adding geometric tools (angles, distances, shape indicators, vectors) (cogit)

  * Adding a topological map. (cogit)

  * Adding Delaunay's triangulation support. (cogit)

  * Correction of some minor bugs. (cogit)

Version 1.1 (2006/06/12)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

  * Compilation of the project without Oracle librairies is now possible. Some classes have been moved in the "datatools" package, calls to Oracle algorithms in GM_Object have been disabled and a new ant task named "compile-without-oracle" has been created. (abraun)

  * Improvment of the method GeoDatabase::loadAllFeatures for PostGIS (loading all the features contained in a geometry is now supported). (abraun)

  * PostGIS and Oracle can (and must) share the same XML mapping files. JDBC type is 'STRUCT' and the field conversion class is "fr.ign.cogit.geoxygene.datatools.ojb.GeomGeOxygene2Dbms". Examples of mapping files have been modified. (abraun)

  * Support of Postgis 1.0.x ! Compatibility with the previous versions of PostGIS is broken. (tbadard, abraun)

  * Generation of primary key column "COGITID" on geospatial tables in PostGIS is now possible (use the "Manage Data" item in the Console menu). (abraun)

  * Deletion of an explicit call to the TABLESPACE in the code of OracleSpatialQuery::spatialIndex. (abraun)

  * Correction of errors in the sql scripts for PostGIS. (abraun)

  * Correction of errors in the sql scripts for Oracle (since Oracle 10g, it is not possible to insert a null value in diminfo column of USER_SDO_GEOM_METADATA). (abraun)

  * A problem in the ant build file has been fixed (images and property files were not copied in the class folder). (abraun)

Version 1.0 (released)
^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

  * Initial release (tbadard)

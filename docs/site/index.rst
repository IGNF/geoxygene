
Introduction
############################

GeOxygene aims at providing an open framework which implements
`OGC <http://www.opengeospatial.org/>`_ / `ISO <http://www.isotc211.org/>`_
specifications for the development and deployment of geographic (GIS) applications.
It is a open source contribution of the `COGIT laboratory <http://recherche.ign.fr/labos/cogit/accueilCOGIT.php>`_ at the
`IGN <http://www.ign.fr/>`_ (Institut Géographique National), the French National Mapping Agency.
It is released under the terms of the `LGPL (GNU Lesser General Public License) license <http://www.fsf.org/licensing/licenses/lgpl.html#SEC1>`_

GeOxygene is based on Java and open source technologies and provides users with an extensible
object data model (geographic features, geometry, topology and metadata) which implements
OGC specifications and ISO standards in the geographic information domain.
The support of the Java interfaces developped by the open source
`GeoAPI project <http://geoapi.sourceforge.net/>`_ is planned.

.. Data are stored in a relational DBMS (RDBMS) to ensure a rapid and reliable access to the system
.. but users do not have to worry about any SQL statements: they model their applications in UML and code in Java.
.. Mapping between object and relational environments is performed with open source software.
.. At present, `OJB <http://db.apache.org/ojb/>`_ is supported and the mapping files for the storage of
.. geographic information in `Oracle <http://www.oracle.com/>`_ or `PostGIS <http://postgis.refractions.net/>`_
.. are provided to users.

News
----------

* June 10, 2013 - **What’s New in GeOxygene 1.5**
    
    The presentation on GeOxygene 1.5 at `FROG 2013 <http://frog.osgeo.fr>`_ in Saint-Mandé will focus  
    on the new features, including a description of the three new modules : geoxygene-semio (semiology), 
    geoxygene-sig3d (3D) and geoxygene-cartagen (generalization).
      
    Presentation Slides : `"GeOxygene : une plate-forme de recherche pour le développement d'applications SIG" <https://github.com/OSGeo-fr/frog2013/raw/master/presentations/technique/01-van_damme/FROG13-GeOxygene.pdf>`_ 
  

* January, 2013 - **Version 1.5 released**
    
    This new version includes : 

    * a new semiology module with a model of legend detailed :ref:`here <semiology>`.
      
    * a new 3D module provides a dedicated GUI mapping tool that is optimized to manipulate 3D geometry, to import special format like CityGML, postgis 3D, ..., to use objects and algorithms dedicated to 3D 

    * a new generalization module provides generalization algorithms, spatial analysis api (Multi-criteria decision analysis, ...), methods for calculating compactness, curves, congestion
        
    * data matching process using theory of Evidence
     
    Technically, the project has adopted a new multi-module Maven architecture.

* June 22, 2009 - **Version 1.4 released**

    This version provides an ISO1909 General Feature Model implementation, a SLD (Styled Layer Descriptor)
    implementation based on the OGC 02-070 implementation specification, an Hibernate support,
    a first version of the new GeOxygene interface, some code examples to learn how to use the
    topoligical map and the data matching tool, and a complete Java 6 support in regard to
    Java 6 specifications (types, annotations, etc.).

* January 31, 2008 - **Version 1.3 released**

    This version provides a data matching tool.

* August 8, 2007 - **Version 1.2 released**

    This version provides new tools (topological map, geometric tools, Delaunay's triangulation)
    and corrects some minor bugs.

* September 12-15, 2006 - **GeOxygene will be at FOSS4G**

    A technical workshop on GeOxygene is organised during the 4th edition of the Free and Open Source Software
    for Geoinformatics conference, `FOSS4G 2006 <http://www.foss4g2006.org/>`_, Lausanne, Switzerland,
    September 12-15, 2006.

* June 12, 2006 - **Version 1.1 released**

    This version corrects some minor bugs and enhances the support of PostGIS.

* May 8, 2005 - **GeOxygene 1.0 released**

    After two years of development, testing and debug, version 1.0 of GeOxygene is released.



Navigation
-------------

.. toctree::
   :maxdepth: 1

   community/index
   download/index
   documentation/index

   
   
.. Rapports Maven : lesquels (javadoc, ) et comment les intégrer

.. Gérer les liens du site internet

.. Lien vers le site de sourceforge !

.. Liens vers geoxygene-data




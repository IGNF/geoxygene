
Introduction
############################

GeOxygene aims at providing an open framework which implements
`OGC <http://www.opengeospatial.org/>`_ / `ISO <http://www.isotc211.org/>`_
specifications for the development and deployment of geographic (GIS) applications.
It is a open source contribution of the `COGIT laboratory <http://recherche.ign.fr/labos/cogit/accueilCOGIT.php>`_ at the
`IGN <http://www.ign.fr/>`_ (Institut Géographique National), the French National Mapping Agency.

GeOxygene is based on Java and open source technologies and provides users with an extensible
object data model (geographic features, geometry, topology and metadata) which implements
OGC specifications and ISO standards in the geographic information domain.
The support of the Java interfaces developped by the open source
`GeoAPI project <http://geoapi.sourceforge.net/>`_ is planned.

GeOxygene is an open source project made available to you using an open source license described :ref:`here <geoxlicense>`.

.. Data are stored in a relational DBMS (RDBMS) to ensure a rapid and reliable access to the system
.. but users do not have to worry about any SQL statements: they model their applications in UML and code in Java.
.. Mapping between object and relational environments is performed with open source software.
.. At present, `OJB <http://db.apache.org/ojb/>`_ is supported and the mapping files for the storage of
.. geographic information in `Oracle <http://www.oracle.com/>`_ or `PostGIS <http://postgis.refractions.net/>`_
.. are provided to users.


Démo
----------

.. raw:: html

    <br/>

    <script>
    jQuery(document).ready(function ($) {
            var options = {
                $AutoPlay: true,                    //[Optional] Whether to auto play, to enable slideshow, this option must be set to true, default value is false
                $DragOrientation: 3,                //[Optional] Orientation to drag slide, 0 no drag, 1 horizental, 2 vertical, 3 either, default value is 1 (Note that the $DragOrientation should be the same as $PlayOrientation when $DisplayPieces is greater than 1, or parking position is not 0)
                $AutoPlayInterval: 4000,            //[Optional] Interval (in milliseconds) to go for next slide since the previous stopped if the slider is auto playing, default value is 3000
                $PauseOnHover: 1,                   //[Optional] Whether to pause when mouse over if a slider is auto playing, 0 no pause, 1 pause for desktop, 2 pause for touch device, 3 pause for desktop and touch device, 4 freeze for desktop, 8 freeze for touch device, 12 freeze for desktop and touch device, default value is 1
                $ArrowKeyNavigation: true,          //[Optional] Allows keyboard (arrow key) navigation or not, default value is false

                $BulletNavigatorOptions: {                //[Optional] Options to specify and enable navigator or not
                    $Class: $JssorBulletNavigator$,       //[Required] Class to create navigator instance
                    $ChanceToShow: 2,               //[Required] 0 Never, 1 Mouse Over, 2 Always
                    $ActionMode: 1,                 //[Optional] 0 None, 1 act by click, 2 act by mouse hover, 3 both, default value is 1
                    $AutoCenter: 1,                 //[Optional] Auto center navigator in parent container, 0 None, 1 Horizontal, 2 Vertical, 3 Both, default value is 0
                    $Steps: 1,                      //[Optional] Steps to go for each navigation request, default value is 1
                    $Lanes: 1,                      //[Optional] Specify lanes to arrange items, default value is 1
                    $SpacingX: 10,                   //[Optional] Horizontal space between each item in pixel, default value is 0
                    $SpacingY: 0,                   //[Optional] Vertical space between each item in pixel, default value is 0
                    $Orientation: 1                 //[Optional] The orientation of the navigator, 1 horizontal, 2 vertical, default value is 1
                }
            };

            var jssor_slider1 = new $JssorSlider$("slider1_container", options);

            //responsive code begin
            //you can remove responsive code if you don't want the slider scales while window resizes
            function ScaleSlider() {
                var parentWidth = jssor_slider1.$Elmt.parentNode.clientWidth;
                if (parentWidth)
                    jssor_slider1.$ScaleWidth(Math.min(parentWidth, 640));
                else
                    window.setTimeout(ScaleSlider, 30);
            }

            //Scale slider immediately
            ScaleSlider();

            $(window).bind("load", ScaleSlider);
            $(window).bind("resize", ScaleSlider);
            $(window).bind("orientationchange", ScaleSlider);
            //responsive code end

            //fetch and initialize youtube players
            $JssorPlayer$.$FetchPlayers(document.body);
        });
    </script>
    <style>
        /* jssor slider bullet navigator skin 03 css */
        /*
            .jssorb03 div           (normal)
            .jssorb03 div:hover     (normal mouseover)
            .jssorb03 .av           (active)
            .jssorb03 .av:hover     (active mouseover)
            .jssorb03 .dn           (mousedown)
        */
        .jssorb03 div, .jssorb03 div:hover, .jssorb03 .av
        {
           background: url(_images/b03.png) no-repeat;
           overflow:hidden;
           cursor: pointer;
        }
        .jssorb03 div { background-position: -5px -4px; }
        .jssorb03 div:hover, .jssorb03 .av:hover { background-position: -35px -4px; }
        .jssorb03 .av { background-position: -65px -4px; }
        .jssorb03 .dn, .jssorb03 .dn:hover { background-position: -95px -4px; }
    </style>
    
    <center>
    <div id="slider1_container" style="position: relative; top: 0px; left: 0px; width: 640px;
        height: 390px;">
        <!-- Slides Container -->
        <div u="slides" style="cursor: move; position: absolute; left: 0px; top: 0px; width: 640px; height: 390px;
            overflow: hidden;">
            <div>
                <div u="player" style="position: relative; top: 0px; left: 0px; width: 640px; height: 390px; overflow: hidden;">
                    
                    <!-- Charlotte Hoarau -->
                    <iframe width="560" height="315" src="https://www.youtube.com/embed/OzlMcKwFRRs" frameborder="0" allowfullscreen></iframe>
                    
                    <!-- play cover begin (optional, can remove play cover) -->
                    <div u="cover" class="videoCover" style="position: absolute; top: 0px; left: 0px; background-color: #000; background-image: url(_images/play.png); background-position: center center; background-repeat: no-repeat; filter: alpha(opacity=40); opacity: .4; cursor: pointer; display: none; z-index: 1;"></div>
                    <!-- play cover end -->
                    <!-- close button begin (optional, can remove close button) -->
                    <style>
                        .closeButton { background-image: url(_images/close.png); }
                        .closeButton:hover { background-position: -30px 0px; }
                    </style>
                    <div u="close" class="closeButton" style="position: absolute; top: 0px; right: 1px;
                        width: 30px; height: 30px; background-color: #000; cursor: pointer; display: none; z-index: 2;">
                    </div>
                    <!-- close button end -->
                </div>
            </div>
            <div>
                <div u="player" style="position: relative; top: 0px; left: 0px; width: 640px; height: 390px; overflow: hidden;">
                    
                    <!-- Antoine Masse -->
                    <iframe width="560" height="315" src="https://www.youtube.com/embed/DhZP6XjCukk" frameborder="0" allowfullscreen></iframe>
                    
                    <!-- play cover begin (optional, can remove play cover) -->
                    <div u="cover" class="videoCover" style="position: absolute; top: 0px; left: 0px; background-color: #000; background-image: url(_images/play.png); background-position: center center; background-repeat: no-repeat; filter: alpha(opacity=40); opacity: .4; cursor: pointer; display: none; z-index: 1;"></div>
                    <!-- play cover end -->
                    <!-- close button begin (optional, can remove close button) -->
                    <style>
                        .closeButton { background-image: url(_images/close.png); }
                        .closeButton:hover { background-position: -30px 0px; }
                    </style>
                    <div u="close" class="closeButton" style="position: absolute; top: 0px; right: 1px;
                        width: 30px; height: 30px; background-color: #000; cursor: pointer; display: none; z-index: 2;">
                    </div>
                    <!-- close button end -->
                </div>
            </div>
            <div>
                <div u="player" style="position: relative; top: 0px; left: 0px; width: 640px; height: 390px; overflow: hidden;">
                    
                    <!-- Guillaume Touya -->
                    <iframe width="480" height="270" src="//www.dailymotion.com/embed/video/x2cu8tx" frameborder="0" allowfullscreen></iframe>
                    
                    <!-- play cover begin (optional, can remove play cover) -->
                    <div u="cover" class="videoCover" style="position: absolute; top: 0px; left: 0px; background-color: #000; background-image: url(_images/play.png); background-position: center center; background-repeat: no-repeat; filter: alpha(opacity=40); opacity: .4; cursor: pointer; display: none; z-index: 1;"></div>
                    <!-- play cover end -->
                    <!-- close button begin (optional, can remove close button) -->
                    <style>
                        .closeButton { background-image: url(_images/close.png); }
                        .closeButton:hover { background-position: -30px 0px; }
                    </style>
                    <div u="close" class="closeButton" style="position: absolute; top: 0px; right: 1px;
                        width: 30px; height: 30px; background-color: #000; cursor: pointer; display: none; z-index: 2;">
                    </div>
                    <!-- close button end -->
                </div>
            </div>
            <div>
                <div u="player" style="position: relative; top: 0px; left: 0px; width: 640px; height: 390px; overflow: hidden;">
                    
                    <!-- Mickael Brasebin -->
                    <iframe width="420" height="315" src="https://www.youtube.com/embed/dH9woKexsVw" frameborder="0" allowfullscreen></iframe>
                    
                    <!-- play cover begin (optional, can remove play cover) -->
                    <div u="cover" class="videoCover" style="position: absolute; top: 0px; left: 0px; background-color: #000; background-image: url(_images/play.png); background-position: center center; background-repeat: no-repeat; filter: alpha(opacity=40); opacity: .4; cursor: pointer; display: none; z-index: 1;"></div>
                    <!-- play cover end -->
                    <!-- close button begin (optional, can remove close button) -->
                    <style>
                        .closeButton { background-image: url(_images/close.png); }
                        .closeButton:hover { background-position: -30px 0px; }
                    </style>
                    <div u="close" class="closeButton" style="position: absolute; top: 0px; right: 1px;
                        width: 30px; height: 30px; background-color: #000; cursor: pointer; display: none; z-index: 2;">
                    </div>
                    <!-- close button end -->
                </div>
            </div>
            
            <div>
                <div u="player" style="position: relative; top: 0px; left: 0px; width: 640px; height: 390px; overflow: hidden;">
                    
                    <!-- Guillaume Touya - GAEL -->
                    <iframe width="420" height="315" src="https://www.youtube.com/embed/wkMZnOnFxgY" frameborder="0" allowfullscreen></iframe>
                    
                    <!-- play cover begin (optional, can remove play cover) -->
                    <div u="cover" class="videoCover" style="position: absolute; top: 0px; left: 0px; background-color: #000; background-image: url(_images/play.png); background-position: center center; background-repeat: no-repeat; filter: alpha(opacity=40); opacity: .4; cursor: pointer; display: none; z-index: 1;"></div>
                    <!-- play cover end -->
                    <!-- close button begin (optional, can remove close button) -->
                    <style>
                        .closeButton { background-image: url(_images/close.png); }
                        .closeButton:hover { background-position: -30px 0px; }
                    </style>
                    <div u="close" class="closeButton" style="position: absolute; top: 0px; right: 1px;
                        width: 30px; height: 30px; background-color: #000; cursor: pointer; display: none; z-index: 2;">
                    </div>
                    <!-- close button end -->
                </div>
            </div>
            
            
            
            <!-- bullet navigator container -->
            <div u="navigator" class="jssorb03" style="position: absolute; bottom: 6px; left: 6px;">
                <!-- bullet navigator item prototype -->
                <div u="prototype" style="POSITION: absolute; WIDTH: 21px; HEIGHT: 21px; text-align:center; line-height:21px; color:White; font-size:12px;"><div u="numbertemplate"></div></div>
            </div>
            <!-- Bullet Navigator Skin End -->
            <a style="display: none" href="http://www.jssor.com">jQuery Slider</a>
        </div>
        </center>

News
----------

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

    Two demonstrations of using GeOxygene library at the GeoVIS Workshop, ISPRS GeoSpatial Week. Watch the video above !

* June 5, 2014 - **CartAGen demonstration**

    CartAGen is a component-based research platform dedicated to generalisation. It based on GeOxygene library. A demonstration entitled
    *Multi-Agent Multi-Level Cartographic Generalisation in CartAGen* will be held at `PAAMS'14 <http://www.paams.net>`_

* February 26, 2014 - **A presentation tools to estimate imprecision on geographical data**

    Jean-François Girres intervention at *Géoséminaire* entitled `Fondements théoriques et méthodologiques de la qualification des données géographiques
    <http://geoseminaire2014.teledetection.fr/index.php/programme/menu-downloads-presentations-2014/file/2-download-fondements-theoriques-et-methodologiques-de-la-qualification.html>`_ 
    showcased tools developed in his thesis to estimate geometric measurements imprecision on geographical data.
    
* January, 2014 - **Version 1.6 released**

    This new version includes the EstIM model developed during the Jean-François Girres PhD (2012), a new OSM module,
    new tools for managing style (editor, import and export SLD).
    The old GeOxygene 2D graphical interface and CartaGen graphical interface merge.

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

.. * June 22, 2009 - **Version 1.4 released**
.. This version provides an ISO1909 General Feature Model implementation, a SLD (Styled Layer Descriptor)
..    implementation based on the OGC 02-070 implementation specification, an Hibernate support,
..    a first version of the new GeOxygene interface, some code examples to learn how to use the
..    topoligical map and the data matching tool, and a complete Java 6 support in regard to
..    Java 6 specifications (types, annotations, etc.).


Screenshots
--------------

========================================================= ============================================================= ==============================================================
.. figure:: /screenshot/geox2D_ign_style.png               .. figure:: /screenshot/ecrangeoxygene1.png                   .. figure:: /screenshot/geox3d.png
.. figure:: /screenshot/QUALITE-Estimation-Echelle.png     .. figure:: /screenshot/geoxygene_thematiqueSymbolizer.png    .. figure:: /screenshot/interfacecartagen.png
.. figure:: /screenshot/geoxygenegeopensim.png             .. figure:: /screenshot/InterfaceGraphiqueGeOxygene.png       .. figure:: /screenshot/bdtopo.jpg
========================================================= ============================================================= ==============================================================



Navigation
-------------

.. toctree::
   :maxdepth: 1

   community/index
   download/index
   documentation/index

   
   
.. Rapports Maven : lesquels (javadoc, ) et comment les intégrer

.. Gérer les liens du site internet







Plugins GeOxygene for OpenJump
########################################


GeOxygene library is used by these plugins

* Licence : GPL
* Publication date : 2009-06-27
* Version : 0.1

Installation
***********************

Set up OpenJump as a prerequisite.

1. Download the plugin 

.. container:: svnurl

   http://sourceforge.net/projects/oxygene-project/files/GeOxygene%20Plugin%20for%20OpenJUMP/0.1/geoxygene-plugin-0.1.zip/download
     
2. Copy the whole folder contents into your **<myOpenjumpFolder>**/lib/ext

3. Start openjump.
 
4. You will now see a new item menu called "GeOxygene" 


Example : Network matching
******************************

Playing
--------------

1. Download demo data :
   
   * Network 1 : http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-data/ign-echantillon/bdcarto/BDC_3-1_SHP_LAMB93_X072-ED111/RESEAU_ROUTIER/TRONCON_ROUTE.zip
   
   * Network 2 : http://svn.code.sf.net/p/oxygene-project/code/main/trunk/geoxygene-data/ign-echantillon/bdtopo/72_BDTOPO_shp/BDT_2-0_SHP_LAMB93_X062-ED111/A_RESEAU_ROUTIER/ROUTE.zip

2. Start OpenJUMP.

3. Load both shapefiles

4. Select menu "GeOxygene >> Appariement >> Appariement de réseaux".

5. Launch matching 

   .. container:: centerside
   
      .. figure:: /documentation/resources/img/openjump/GeoxygeneOpenJump01.png
         :width: 450px
       
         Figure 1 : Dataset
               
6. Results of matching will be displayed on windows.

   .. container:: centerside
   
      .. figure:: /documentation/resources/img/openjump/GeoxygeneOpenJump02.png
         :width: 450px
       
         Figure 2 : Results of matching
      
7. Finish.


Reference
--------------
  
* Mustière S., Devogele T., 2008, `Matching networks with different levels of detail 
  <http://www.informaworld.com/smpp/1673074808-66010030/content~db=all~content=a902412390>`_ , 
  GeoInformatica, Vol.12 n°4, pp 435-453


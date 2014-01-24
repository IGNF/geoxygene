

Viewer GeOxygene 3D
====================

* Licence : open source license described :ref:`here <geoxlicense>`
* Publication date : 2014-01-23
* Version : 1.6


Installation
**************

GeOxygene 3D (v1.6) executable installation
----------------------------------------------

1. Download and unzip folder corresponding to the user operating system.

2. GeOxygene3D rendering relies on Java 3D library which must be downloaded from address below :

   .. container:: chemin

      https://java3d.java.net/binary-builds.html

   Please pay attention to download zip version matching with user operating system and processing unit type
   (for example : j3d-1_5_2-windows-i586.zip for Windows 32 bits...). 
   
   Extract files from downloaded zip. 
   
   Move following jar and dll files (usually located in **bin** and **bin\\ext** folders) 
   to GeOxygene 3D libraries folder (**exe\\lib** from root) :

   - j3d-core-1.5.2.jar
   - j3d-core-utils-1.5.2.jar
   - vecmath-1.5.2.jar
   - j3dcore-ogl.dll


3. Launch GeOxygene 3D module with GeOxygene.bat (GeOxygene 3D folder root).

4. Optional : in order to use GeOxygene 3D in full capabilities, TetGen tetraedrisation must be downloaded
   from following location :
   
   .. container:: chemin

      http://wias-berlin.de/software/tetgen/#Download

   Use C compiler to produce dll from source code and add it in same folder (exe\lib).


Documents to download
-----------------------
The english version of GeOxygene3d install guide can be downloaded 
:download:`here </download/resources/GeOxygene3d-install.pdf>`

The french version of GeOxygene3d install guide can be downloaded 
:download:`here </download/resources/GeOxygene3d-installation.pdf>`


User Guide
************
The French version of user guide of the GeOyxgene 3D module can be downloaded 
:download:`here </documentation/resources/doc/geoxygene3D-Utilisateur-V3.pdf>`, 
in PDF file format.

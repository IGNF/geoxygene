.. _rendugeoxogl:

Configuration des librairies OpenGL 
#####################################

L'interface graphique 2D de GeOxygene est doté de 2 modes de rendu : AWT et OpenGL. Le rendu graphique OpenGL nécessite une configuration spécifique dans Eclipse : l'appel aux librairies natives 
(`Lightweight Java Game Library, version 2.9.1 <http://lwjgl.org/>`_).
 

Configuration & Installation
******************************
Il n'y a rien à installer, les librairies natives sont déjà téléchargées, elles sont placées dans le répertoire du projet **geoxygene-appli/lib**

Pour configurer Eclipse :

1. Dans l'*explorateur*, faire un clic droit sur le projet *geoxygene-appli*, puis choisir **properties**

   .. figure:: /documentation/resources/img/ogl/ConfigOGL_01.png 
        :width: 400px
        
        Figure 1 : Configurer les librairies natives
       
2. Sélectionner **Java Build Path**

3. Dans le 3ème onglet **libraries**, dépacketer **Maven Dependencies**

4. Sélectionner **Native library location**

5. Cliquer sur **Edit**

6. Cliquer sur **Workspace**

7. Sélectionner les librairies suivant votre système d'exploitation, par exemple :

   .. container:: chemin

      geoxygene-appli/lib/lwjgl/windows

8. Cliquer sur **OK**, c'est tout bon, les librairies natives sont configurées.

   .. figure:: /documentation/resources/img/ogl/NativeLibrary.png 
        :width: 650px
        
        Figure 2 : Configurer les librairies natives
        

Si l'erreur suivante se présente, c'est qu'Eclipse n'a pas réussi à charger les librairies natives lwjgl :


.. literalinclude:: /documentation/resources/code_src/ogl/erreur01.java
        :language: none




Tester l'interface
***************************

1. Ouvrir un nouveau projet GL :

   .. figure:: /documentation/resources/img/ogl/New_GeOxygeneProjet_gl.png 
        :width: 350px
         
        Figure 3 : Ouvrir un projet OG


2. Importer un shapefile, par exemple une toute petite collection de polygone :

   .. container:: chemin 

      geoxygene-matching/data/ign-surface/EauBDTopo.shp
      
   .. figure:: /documentation/resources/img/ogl/GeOxygeneOpenShape.png 
        :width: 350px
         
        Figure 4 : Ouvrir un shapefile


3. Modifier le style

   .. container:: twocol

      .. container:: leftside

         .. literalinclude:: /documentation/resources/code_src/ogl/style1.xml
            :language: xml

      .. container:: rightside

         Click droit sur la couleur du style
         
         .. figure:: /documentation/resources/img/ogl/GeOxygeneEditStyle.png 
            :width: 350px
         
            Figure 5 : Modifier le style


4. Et vous obtenez :

   .. figure:: /documentation/resources/img/ogl/TestGeOxygeneOpenGL.png 
      :width: 800px
         
      Figure 6 : Test OpenGL dans l'interface graphique 2D (à gauche AWT, à droite OpenGL)



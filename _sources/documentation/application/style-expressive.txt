.. _style-expressive:


:Author: Nicolas Mellado, Jeremie Turbet
:Version: 0.1
:License: Create Commons with attribution
:Date: 2 Mars 2015 

Expressive Styles
##############################

Expressive styles have been developed during the ANR project `Mapstyle <http://mapstyle.ign.fr/>`_, by Jeremie Turbet.

Expressive styles extend the SLD norm to introduce brush and texture-based appearance using OpenGL+GLSL rendering. They can be visualized in GeOxygene using the OpenGL project window:

.. figure:: ../resources/img/expressiveStyle/openGLproject.png
    :align: center
    :alt: Open OpenGL Project
    :figclass: align-center
    
    Use OpenGL to render your map using the GL Project window


This page covers the definition of expressive *Stroke* and *Fill* styles.


Background
**********
Background textures can be defined using the ``<Background>`` tag:

 .. code-block:: xml

   <StyledLayerDescriptor xmlns:ogc="http://www.opengis.net/ogc">
    <Background>
        <url>./samples/expressive samples/images/canvas-bg.png</url>
        <PaperHeightInCm>4.0</PaperHeightInCm>
        <PaperReferenceMapScale>100000.0</PaperReferenceMapScale>
        <Color>#FF0000</Color>
    </Background>
    <NamedLayer>

with:

* ``url``: Path of the image file
    .. warning:: The tag ``<url>`` is not internally used as an url but as a ==path==.    
* ``PaperHeightInCm``: paper size according to the reference scale
* ``PaperReferenceMapScale``: reference scale
* ``Color``: alternative color used when the image cannot be loaded/displayed


Stroke styles
*************

Expressive stroke
=================

Parameters
==========

[DOMDD2001]_

Shaders
=======

Fill styles
***********


Texture filling
===============

Perlin Noise Texture
====================

Tile Distribution Texture
=========================

Gradient Texture
================

Expressive Gradient
===================


Bibliographie
=============


.. [DOMDD2001] Frédo Durand, Victor Ostromoukhov, Mathieu Miller, Francois Duranleau, and Julie Dorsey. 2001. 

          Decoupling strokes and high-level attributes for interactive traditional drawing. 
          
          In Proceedings of the 12th Eurographics conference on Rendering (EGWR'01). 
          
          http://dx.doi.org/10.2312/EGWR/EGWR01/071-082 





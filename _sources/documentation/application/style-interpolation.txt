.. _style-expressive:


:Author: Nicolas Mellado
:Version: 0.1
:License: Create Commons with attribution
:Date: 3 Mars 2015 

Styles Interpolation
####################

Style interpolation have been developed during the ANR project `Mapstyle <http://mapstyle.ign.fr/>`_, by Nicolas Mellado. It can be used either using GL an AWT projects.

Last Update: 3rd March 2015

Introduction
************
Style interpolation has been implemented as SLD extensions, and is based on the new symbolizers ``<PolygonInterpolationSymbolizer>`` and ``<LineInterpolationSymbolizer>``, structured as follow:

 .. code-block:: xml
 
    <LineInterpolationSymbolizer uom="http://www.opengeospatial.org/se/units/metre">
        < *Interpolated style* >
        <FirstSymbolizer uom="http://www.opengeospatial.org/se/units/metre">
            < *First style definition* >
        </FirstSymbolizer>
        <SecondSymbolizer uom="http://www.opengeospatial.org/se/units/metre">
            < *Second style definition* >
        </SecondSymbolizer>
        <alpha>0.5</alpha>
    </LineInterpolationSymbolizer>

The first part of the interpolation symbolizer ``< *Interpolated style* >`` describes the result of the interpolation. There is no need to edit this part, which is **automatically** updated each time any of the input styles is modified. More details are given at the end of this document about the technical aspects of this automatic procedure. 

The ``<FirstSymbolizer>`` and ``<SecondSymbolizer>`` tags define the input symbolizers to be interpolated. Their type is deduced from the main symbolizer type, as ``LineSymbolizer`` for ``LineInterpolationSymbolizer`` and ``PolygonSymbolizer`` for ``PolygonInterpolationSymbolizer``.

The parameter ``alpha`` is defined as :math:`\alpha \in [0:1]` and controls the interpolation between the two symbolizers. 

Symbolizers are interpolated parameter-wise, ie. each parameter is interpolated independently. In the current status only linear interpolation is available, and all the parameters of a symbolizer are interpolated using the same :math:`\alpha` (see section advanced_control_ for advanced control techniques). When an Stroke or a Fill element is defined only for one of the two input symbolizers, its alpha value is interpolated from the defined value to :math:`0`.

Samples are available in ``geoxygene-appli/samples/interpolation``.

Stroke interpolation
********************

.. _stroke_interpolation:

Many parameters of a classical stroke can be interpolated, except the colormap fields. 

Current supported parameter list:

* Color: interpolation in RGB space,
* Opacity,
* Width,
* Cap and Join: use first style value when :math:`\alpha < 0.5`, and second one otherwise
* Expressive Stroke: requires to use the same shader and identical parameter set (different value). Textures can be interpolated if they share the same color space. GL viewer is required.

Example basic stroke
====================

This example can be reproduced using ``samples/interpolation/SLD/stroke_interpolation.sld.xml``. We interpolate both the color and the thickness of the stroke:

.. literalinclude:: ../../../../geoxygene/geoxygene-appli/samples/interpolation/SLD/stroke_interpolation.sld.xml
    :linenos:
    :language: xml
    :emphasize-lines: 13, 14, 22, 23, 30, 31, 36

Which leads to the following transition:

.. figure:: ../resources/img/styleInterpolation/simple-stroke.gif
    :align: center
    :alt: Transition between two simple strokes
    :figclass: align-center
    :width: 40%
    
    Interpolating from a thin blueish stroke (:math:`\alpha = 0`) to a thick red stroke (:math:`\alpha = 1`). 
    Click to enlarge.

Example expressive stroke
=========================

.. _expressiveStroke:

The syntax used to describe expressive strokes is not stable yet. However, in the current status it is possible to interpolate between two ExpressiveStroke, leading to the following result:

.. figure:: ../resources/img/styleInterpolation/expressiveStroke.gif
    :align: center
    :alt: Transition between two expressive strokes
    :figclass: align-center
    :width: 40%
    
    ExpressiveStroke interpolation. Top: interpolation result, bottom: interpolated paint brush.
    
The sample used to generate this file is available at ``samples/interpolation/SLD/expressiveStroke_interpolation.sld.xml``.


Filling interpolation
*********************

Current supported parameter list:

* Color: interpolation in RGB space,
* Opacity,
* Stroke (see section stroke_interpolation_)

Example basic polygon style
===========================

This example can be reproduced using ``samples/interpolation/SLD/fill_interpolation.sld.xml``. We interpolate both the color used to fill the polygon and its stroke:

.. literalinclude:: ../../../../geoxygene/geoxygene-appli/samples/interpolation/SLD/fill_interpolation.sld.xml
    :linenos:
    :language: xml
    :emphasize-lines: 19, 20, 30, 31, 37, 38

Note that the stoke is not defined in the second style, so the final interpolated style is defined using the first style stroke, with an opacity computed according to :math:`\alpha`.
Which leads to the following transition:

.. figure:: ../resources/img/styleInterpolation/simple-fill.gif
    :align: center
    :alt: Transition between two polygon styles
    :figclass: align-center
    :width: 40%
    
    Transition between two polygon styles.
    Click to enlarge.

Graphic User Interface
**********************
Interpolated styles cannot be edited from the style edition frame. Modifying the style from this window will edit only the result of the interpolation, which is recomputed when clicking on the Apply or Ok buttons.


It is however possible to edit the parameter :math:`\alpha` for the selected layer, using the slider(s) located in the *Style Interpolation* tab. Multiple sliders can be used when multiple interpolation symbolizers are used alongside in a single layer (see section advanced_control_)

.. figure:: ../resources/img/styleInterpolation/gui.png
    :align: center
    :alt: Using GUI to manipulate interpolation
    :figclass: align-center
    :width: 100%
    
    Using GUI to manipulate interpolation.
    Click to enlarge.

Advanced Control
****************
.. _advanced_control:

Multiple symbolizers can be used side by side to use different alpha for different set of parameters, or define constant parameters.

Partial interpolation example
=============================

The sample ``samples/interpolation/SLD/advanced_partial_interpolation.sld.xml`` is an example of partial interpolation, where the filling properties are interpolated and the stroke style kept uniform using a dedicated PolygonSymbolizer.

.. literalinclude:: ../../../../geoxygene/geoxygene-appli/samples/interpolation/SLD/advanced_partial_interpolation.sld.xml
    :linenos:
    :language: xml
    :emphasize-lines: 10, 31, 32, 40

.. figure:: ../resources/img/styleInterpolation/partial.gif
    :align: center
    :alt: Partial interpolation
    :figclass: align-center
    :width: 40%
    
    Use constant stroke properties and interpolates only filling parameters.
    Click to enlarge.

Concurrent interpolation example
================================

In this example there are two interpolation symbolizers: one to interpolate the polygon color, and another one for the stroke parameters.

.. literalinclude:: ../../../../geoxygene/geoxygene-appli/samples/interpolation/SLD/advanced_split_interpolation.sld.xml
    :linenos:
    :language: xml
    :emphasize-lines: 10, 30, 31, 32, 57, 58
Hence two sliders are available to manipulate each parameter independently:

.. figure:: ../resources/img/styleInterpolation/split.gif
    :align: center
    :alt: Concurrent interpolation
    :figclass: align-center
    :width: 80%
    
    Use different interpolation values for the stroke and filling parameters.
    Click to enlarge.


Automatic SLD pairing
*********************

Two SLD sharing the same structure can be automatically parsed and merged using interpolation symbolizers.

During the mixing procedure, layers are matched by name. For each couple of layers, we traverse the styles, features, and rules to pair the symbolizers and put them in interpolation symbolizers. When a different number of symbolizers is available on each side, only the shared one are interpolated and the others discarded. 

To use this functionality you need to:

.. figure:: ../resources/img/styleInterpolation/mix.png
    :align: right
    :alt: Concurrent interpolation
    :figclass: right
    
* Load a first SLD (right inset: green button)
* Load a second SLD for automatic mixing (right inset: red button)

The two inputs are processed and a new SLD generated. 
This resulting SLD can be mixed with another one by using a same procedure twice.



SLD validation and automatic style interpolation
************************************************

Interpolating between two symbolizers requires to read there definition, interpolate, and put the result back to the SLD. By construction the SLD is purely static and do not include any logic.

We have implemented the interpolation as a validation step, which occurs each time a SLD is modified. During this stage the modified SLD is parsed, and depending on its content eventually modified. Further processing can be used, for instance for raster interpolation (see expressiveStroke_). In that case a new raster is generated on the fly according to input parameters, then saved to the disk, and linked in the SLD as an usual raster file.

.. note:: The generated file is temporary and not saved alongside the SLD. Each time the SLD is loaded and validated, a new interpolated raster is generated and used for visualization. Exporting such SLD outside of Geoxygen would require to manually edit it to refer to a copy of the interpolated raster stored in a known asset folder.

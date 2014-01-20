  /**
   * Le Graphic Stroke permet de répéter une forme le long d'une ligne. Cette
   * forme peut être une image (ExternalGraphic) ou une forme prédéfinie (Mark).
   */
  public void exampleGraphicStroke_Stroke_Polygon() {
    Layer layer = projectFrame.getSld().createLayer(
        "GraphicStroke_Stroke_Polygon", //$NON-NLS-1$
        GM_Polygon.class, Color.red, Color.yellow, 1f, 1);
    PolygonSymbolizer symbolizer = (PolygonSymbolizer) layer.getSymbolizer();
    GraphicStroke graphicStroke = new GraphicStroke();

    // --------------- Exemple avec un graphic de type image
    // ----------------------------------
    Graphic graphicCircle = new Graphic();
    graphicCircle.setSize(20f);
    ExternalGraphic externalGraphicCircle = new ExternalGraphic();
    URL urlCircle = SLDDemoApplication.class.getResource("/images/circle.png"); //$NON-NLS-1$
    externalGraphicCircle.setHref(urlCircle.toString());
    externalGraphicCircle.setFormat("png"); //$NON-NLS-1$
    graphicCircle.getExternalGraphics().add(externalGraphicCircle);
    graphicStroke.getGraphics().add(graphicCircle);

    // --------------- Exemple avec un graphic de type Mark
    // ----------------------------------
    // Graphic graphicStar = new Graphic();
    // graphicStar.setSize(8f);
    // Mark markStar = new Mark();
    //        markStar.setWellKnownName("star"); //$NON-NLS-1$
    // Fill fillStar = new Fill();
    // fillStar.setColor(new Color(1.f,0.4f,0.4f));
    // markStar.setFill(fillStar);
    // graphicStar.getMarks().add(markStar);
    // graphicStroke.getGraphics().add(graphicStar);

    symbolizer.getStroke().setGraphicType(graphicStroke);

    Population<DefaultFeature> pop = new Population<DefaultFeature>(
        "GraphicStroke_Stroke_Polygon"); //$NON-NLS-1$
    pop.add(new DefaultFeature(new GM_Polygon(
        new GM_Envelope(120, 220, 10, 110))));
    projectFrame.getDataSet().addPopulation(pop);
    projectFrame.getSld().add(layer);
  }


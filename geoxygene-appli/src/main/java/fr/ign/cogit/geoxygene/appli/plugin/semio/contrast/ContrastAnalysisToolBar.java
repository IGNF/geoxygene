/*
 * This file is part of the GeOxygene project source files. GeOxygene aims at
 * providing an open framework which implements OGC/ISO specifications for the
 * development and deployment of geographic (GIS) applications. It is a open
 * source contribution of the COGIT laboratory at the Institut Géographique
 * National (the French National Mapping Agency). See:
 * http://oxygene-project.sourceforge.net Copyright (C) 2005 Institut
 * Géographique National This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of the License,
 * or any later version. This library is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details. You should have received a copy of
 * the GNU Lesser General Public License along with this library (see file
 * LICENSE if present); if not, write to the Free Software Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package fr.ign.cogit.geoxygene.appli.plugin.semio.contrast;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JToolBar;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.filechooser.FileFilter;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.gui.FileChooser;
import fr.ign.cogit.geoxygene.appli.plugin.semio.toolbar.SpecificationToolBar;
import fr.ign.cogit.geoxygene.semio.legend.improvement.BasicStopCriteria;
import fr.ign.cogit.geoxygene.semio.legend.improvement.LucilContrastAnalysis;
import fr.ign.cogit.geoxygene.semio.legend.improvement.QualityStopCriteria;
import fr.ign.cogit.geoxygene.semio.legend.legendContent.Legend;
import fr.ign.cogit.geoxygene.semio.legend.legendContent.LegendLeaf;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.Map;
import fr.ign.cogit.geoxygene.semio.legend.mapContent.SymbolisedFeatureCollection;
import fr.ign.cogit.geoxygene.semio.legend.metadata.SemanticRelationDescriptor;
import fr.ign.cogit.geoxygene.semio.legend.symbol.GraphicSymbol;

import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.Viewport;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.PointSymbolizer;
import fr.ign.cogit.geoxygene.style.PolygonSymbolizer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.Symbolizer;

/**
 * @author Charlotte Hoarau
 * 
 * Contrast Analysis ToolBar.
 * ToolBar for the evaluation and the improvement of color contrasts in a map.
 *
 */
public class ContrastAnalysisToolBar  extends JToolBar
                                      implements ActionListener, ItemListener {
  
    private static final Logger logger = Logger.getLogger(ContrastAnalysisToolBar.class);
	private static final long serialVersionUID = 1L;

	private JButton btnAnalyse;
	private JSpinner spinnerNeighborsDist;
	private JSpinner spinnerNbSteps;
	private JLabel improvementSteps;
	private JComboBox comboContrastAnalysis;
	private JComboBox comboStopCriteria;
	private JButton btnloadMap;
	private JCheckBox checkWeight;
	
	private List<Layer> layers;
	private Map currentMap;
	private Legend currentLegend;
	private SemanticRelationDescriptor relations;
	
	private ProjectFrame projectFrame;
	
	public ContrastAnalysisToolBar(final ProjectFrame p){
		this.projectFrame = p;
		
		SpinnerModel model =
	        new SpinnerNumberModel(50, //initial value
	                               1, //min
	                               1000, //max
	                               10); 
		spinnerNeighborsDist = new JSpinner(model);
		spinnerNeighborsDist.setMaximumSize(spinnerNeighborsDist.getPreferredSize());
		
		String[] contrastAnalysisString = {"LucilAnalysis"};
		comboContrastAnalysis = new JComboBox(contrastAnalysisString);
		comboContrastAnalysis.setMaximumSize(comboContrastAnalysis.getPreferredSize());
		
		String[] stopCriteriaString = {"Basic Stop Criteria", "Quality Stop Criteria"};
		comboStopCriteria = new JComboBox(stopCriteriaString);
		comboStopCriteria.setMaximumSize(comboStopCriteria.getPreferredSize());
		comboStopCriteria.addItemListener(this);
		
		improvementSteps = new JLabel(" Improvement steps :  ");
		
		model =
          new SpinnerNumberModel(10, //initial value
                                 1, //min
                                 150, //max
                                 1); 
        spinnerNbSteps = new JSpinner(model);
        spinnerNbSteps.setMaximumSize(spinnerNbSteps.getPreferredSize());
        
        checkWeight = new JCheckBox("Surface weights");
        
		btnloadMap = new JButton("Load Legend & Semantic Relations");
		btnloadMap.addActionListener(this);
        
		btnAnalyse = new JButton("Contrasts Improvement");
		btnAnalyse.addActionListener(this);

		add(new JLabel("Neighbourhood :"));
		add(spinnerNeighborsDist, BorderLayout.PAGE_START);
		add(comboContrastAnalysis);
		add(comboStopCriteria);
		add(improvementSteps);
		add(spinnerNbSteps);
		add(checkWeight);
		add(btnloadMap);
		add(new JLabel("    "));
		add(btnAnalyse);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
	  if (e.getSource() == this.btnloadMap) {
	    logger.info("chargement de légende et relation sémantique");
	    
	    FileFilter xmlFilter = new FileFilter() {
	      @Override
	      public boolean accept(final File f) {
	        return (f.isFile() && (f.getAbsolutePath().endsWith(".xml") //$NON-NLS-1$
	            || f.getAbsolutePath().endsWith(".XML") //$NON-NLS-1$
	            ) || f.isDirectory());
	      }

	      @Override
	      public String getDescription() {
	        return "XML Files"; //$NON-NLS-1$
	      }
	    };
	    FileChooser fileChooser = MainFrame.getFilechooser();
	    fileChooser.getFileChooser().addChoosableFileFilter(xmlFilter);
	    fileChooser.getFileChooser().removeChoosableFileFilter(
	        fileChooser.getFileChooser().getChoosableFileFilters()[0]);
	    fileChooser.getFileChooser().removeChoosableFileFilter(
	        fileChooser.getFileChooser().getChoosableFileFilters()[0]);
	    fileChooser.getFileChooser().removeChoosableFileFilter(
	        fileChooser.getFileChooser().getChoosableFileFilters()[0]);

	    fileChooser.getFileChooser().setDialogTitle("Choisir un fichier contenant un arbre de légende");
	    File legendFile = fileChooser.getFile(projectFrame.getMainFrame());
	    logger.info(legendFile.getAbsolutePath());
	    fileChooser.getFileChooser().setDialogTitle("Choisir un fichier contenant des relations sémantiques");
	    File semanticRelationsFile = fileChooser.getFile(projectFrame.getMainFrame());
	    logger.info(semanticRelationsFile.getAbsolutePath());

	    currentLegend = Legend.unmarshall(legendFile.getPath());
        for (LegendLeaf leaf : currentLegend.getLeaves()) {
            leaf.setSymbol(new GraphicSymbol());
        }
        logger.info("legend tree loaded");
        
        relations= SemanticRelationDescriptor.unmarshall(
            semanticRelationsFile.getPath(),
            currentLegend);
        logger.info(relations.getNbRelations() + " semantic relations loaded");
	    
	  } else if (e.getSource() == this.btnAnalyse){
			//TODO ajouter une vérification de spécifications de l'arbre de légende et des relations sémantiques.

			///////////////////////////////////////////////////////////////////
			//Getting the specifications (legend tree and semantic relations)
		    //from the SpecificationsToolBar if they have not been loaded from xml files.
			//
			
			if (currentLegend != null && relations !=null){
    			for (Component component : ContrastAnalysisToolBar.this.projectFrame
    					.getContentPane().getComponents()) {
    				if (component.getClass().isAssignableFrom(SpecificationToolBar.class)) {
    					currentLegend = ((SpecificationToolBar)component).getCurrentLegend();
    					relations = ((SpecificationToolBar)component).getCurrentSemanticRelations();
    				}
    			}
			}
			if (currentLegend == null || relations == null) {
			  logger.error("Legend or Semantic Rlations specifications are missing.");
			}
			
			///////////////////////////////////////////////////////////////////
			//Creating the Map with the displayed layers
			layers = this.projectFrame.getLayers();
			if (layers.size() == 0) {
			  logger.error("Data have not been loaded");
			}
			currentMap = new Map(layers, currentLegend, Viewport.getMETERS_PER_PIXEL());
			currentMap.setName(this.projectFrame.getName());
			logger.info(layers.size() + " couches chargées");
			logger.info(currentMap.toString());
			
			if (comboContrastAnalysis.getSelectedIndex()==0){
			  logger.info("Lucil Contrast Analysis");
			  LucilContrastAnalysis analysis = new LucilContrastAnalysis();
			  analysis.setSurfaceWeights(checkWeight.isSelected());
			  if (comboStopCriteria.getSelectedIndex()==0){
			    logger.info("Basic Stop Criteria");
			    BasicStopCriteria stop = new BasicStopCriteria(((SpinnerNumberModel)this.spinnerNbSteps.getModel()).getNumber().intValue());
			    analysis.initialize(currentMap, stop);
			    
			  } else if (comboStopCriteria.getSelectedIndex()==1){
			    logger.info("Quality Stop Criteria");
			    QualityStopCriteria stop = new QualityStopCriteria();
                analysis.initialize(currentMap, stop);
                
			  }
			  analysis.run(
                    ((SpinnerNumberModel)this.spinnerNeighborsDist.getModel()).getNumber().doubleValue());
              this.updateProjectFrame(analysis.getMap());
			}
		}
	}

	public void updateProjectFrame(Map map) {
	 
	    for (Layer layer : this.projectFrame.getLayers()) {
	      for (Style style : layer.getStyles()) {
	          for (Rule rule : style.getFeatureTypeStyles().get(0).getRules()) {
	              Symbolizer symbolizer = rule.getSymbolizers().get(0);
	              for (SymbolisedFeatureCollection familleCarto : map.getSymbolisedFeatureCollections()) {
	                  if (familleCarto.getName().equalsIgnoreCase(rule.getName())) {
	                      if (symbolizer.isLineSymbolizer()) {
	                          symbolizer.getStroke().setColor(
	                                  familleCarto.getLegend().getSymbol().getColor().toColor());
	                      } else if (layer.getSymbolizer().isPolygonSymbolizer()) {
	                          ((PolygonSymbolizer)symbolizer)
	                              .getFill().setColor(
	                                  familleCarto.getLegend().getSymbol().getColor().toColor());
	                      } else if (layer.getSymbolizer().isPointSymbolizer()) {
	                          ((PointSymbolizer)symbolizer)
	                              .getGraphic().getMarks().get(0).getFill().setColor(
	                                  familleCarto.getLegend().getSymbol().getColor().toColor());
	                      }
	                  }
	              }
	          }
	      }
	  }
	  
	  this.projectFrame.getSld().fireActionPerformed(null);
	  
	  this.projectFrame.getLayerLegendPanel().repaint();
      this.projectFrame.getLayerViewPanel().repaint();
      this.projectFrame.repaint();
      logger.info("Contrast analysis finished, Map updated.");
  }

  @Override
  public void itemStateChanged(ItemEvent e) {
    if (e.getSource() == comboStopCriteria) {      
      if (comboStopCriteria.getSelectedIndex()==0){
        add(improvementSteps);
        add(spinnerNbSteps, 4);
      } else if (comboStopCriteria.getSelectedIndex()==1){
        remove(improvementSteps);
        remove(spinnerNbSteps);
      }
    }
    
    projectFrame.pack();
    try {
        projectFrame.setMaximum(true);
    } catch (PropertyVetoException e1) {
        e1.printStackTrace();
    }
    projectFrame.repaint();
    
  }
}

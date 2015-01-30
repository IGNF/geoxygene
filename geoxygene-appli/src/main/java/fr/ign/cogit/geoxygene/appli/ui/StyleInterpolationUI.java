package fr.ign.cogit.geoxygene.appli.ui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.log4j.Logger;

import fr.ign.cogit.geoxygene.appli.AbstractProjectFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.validation.SymbolizerValidator;
import fr.ign.cogit.geoxygene.appli.validation.SymbolizerValidatorFactory;
import fr.ign.cogit.geoxygene.appli.validation.SymbolizerValidator.InvalidSymbolizerException;
import fr.ign.cogit.geoxygene.style.FeatureTypeStyle;
import fr.ign.cogit.geoxygene.style.Layer;
import fr.ign.cogit.geoxygene.style.Rule;
import fr.ign.cogit.geoxygene.style.Style;
import fr.ign.cogit.geoxygene.style.StyledLayerDescriptor;
import fr.ign.cogit.geoxygene.style.Symbolizer;
import fr.ign.cogit.geoxygene.style.interpolation.InterpolationSymbolizerInterface;
import fr.ign.util.ui.SliderWithSpinner;
import fr.ign.util.ui.SliderWithSpinner.SliderWithSpinnerModel;

public class StyleInterpolationUI implements GenericParameterUI {
  private static Logger logger = Logger.getLogger(AbstractProjectFrame.class
      .getName());
  
  private Layer layer = null;
  private StyledLayerDescriptor sld = null;
  private JPanel main = null;
  private List<InterpolationSymbolizerInterface> layerSymbolizers = 
      new ArrayList<InterpolationSymbolizerInterface>();
  private ProjectFrame parentProjectFrame = null;
    
  public StyleInterpolationUI(Layer l, StyledLayerDescriptor s,
      ProjectFrame projectFrame) {
      this.layer = l;
      this.sld   = s;  
      this.parentProjectFrame = projectFrame;
      
      extractSymbolizers();
  }

  @Override
  public JComponent getGui() {
    if (layer == null || sld == null)
      return null;
    return new JScrollPane(this.getMainPanel());
  }

  @Override
  public void setValuesFromObject() {
    // TODO Auto-generated method stub

  }

  @Override
  public void setValuesToObject() {
    // TODO Auto-generated method stub

  }
  
  private void extractSymbolizers() {
      if (layer == null)
          return;
      
      layerSymbolizers.clear();
      
      for (Style style : layer.getStyles()) {
          for (FeatureTypeStyle fts : style.getFeatureTypeStyles()) {
              for (Rule rule : fts.getRules()) {
                  for (Symbolizer symbolizer : rule.getSymbolizers()){
                      if( symbolizer instanceof InterpolationSymbolizerInterface ){
                          layerSymbolizers.add((InterpolationSymbolizerInterface) symbolizer);
                      }
                  }
              }
          }
      }
  }
  protected void refresh() {
      this.setValuesToObject();
      this.parentProjectFrame.repaint();
  }
  
  private JPanel getMainPanel() {
      if (this.main == null) {
          this.main = new JPanel();
          this.main.setLayout(new BoxLayout(this.main, BoxLayout.Y_AXIS));
          this.main.setBorder(BorderFactory
                  .createEtchedBorder(EtchedBorder.LOWERED));
          
          for( final InterpolationSymbolizerInterface symbolizer : layerSymbolizers){
              double alpha = (double) symbolizer.getAlpha();
              
              SliderWithSpinnerModel alphaModel = new SliderWithSpinnerModel(
                  alpha, 0, 1, 0.1);
              final SliderWithSpinner alphaSpinner = new SliderWithSpinner(
                  alphaModel);
              JSpinner.NumberEditor alphaEditor = (JSpinner.NumberEditor) alphaSpinner
                      .getEditor();
              alphaEditor.getTextField().setHorizontalAlignment(
                      SwingConstants.CENTER);
              alphaEditor.setBorder(BorderFactory
                      .createTitledBorder("alpha"));
              alphaSpinner.addChangeListener(new ChangeListener() {  
                  @Override
                  public void stateChanged(ChangeEvent e) {
                    symbolizer.setAlpha(alphaSpinner.getValue().floatValue());
                    SymbolizerValidator validator = SymbolizerValidatorFactory
                        .getOrCreateValidator(symbolizer);
                    if (validator != null)
                      try {
                        validator.validate(symbolizer);
                      } catch (InvalidSymbolizerException exception) {
                        logger.error(exception.getStackTrace().toString());
                      }
                    StyleInterpolationUI.this.refresh();
                  }
              });
              this.main.add(alphaSpinner);
            }
        }
        this.main.add( Box.createVerticalGlue() );
        return this.main;
    }

}

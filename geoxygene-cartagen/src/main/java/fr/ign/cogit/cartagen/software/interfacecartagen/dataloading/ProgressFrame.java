/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.dataloading;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

public class ProgressFrame extends JFrame{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;


  /**
   *  
   * @param args
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub

  }
  ////////////////////////////////////////////
  //                Fields                  //
  ////////////////////////////////////////////

  // All static fields //

  // Public fields //
  JProgressBar progressBar=new JProgressBar(0,100);
  // Protected fields //

  // Package visible fields //

  // Private fields with public getter //

  // Very private fields (no public getter) //

  ////////////////////////////////////////
  //           All constructors         //
  ////////////////////////////////////////

  
  
  public ProgressFrame(String titleText,boolean isStringPainted) {
    
    setUndecorated(true);
    setLocation(500, 400);
    setMinimumSize(new Dimension(400, 100));
    progressBar.setValue(0);
    progressBar.setStringPainted(isStringPainted);
    
    add(progressBar);
    
    setResizable(false);
   
    setTitle(titleText);

    setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    pack();
    //info.setVisible(true);
    
    progressBar.update(progressBar.getGraphics());
    repaint();
  }


  
  
  
 
  ////////////////////////////////////////////
  //           Static methods               //
  ////////////////////////////////////////////

  ////////////////////////////////////////////////////////////
  //           All getters and setters                      //
  ////////////////////////////////////////////////////////////

  public void setStringPainted(boolean b){
    progressBar.setStringPainted(b);
  }
  
  public void setTextAndValue(String text,int value ){
    
    progressBar.setString(text);
    setValue(value);
    
  }
  public void setValue(int value){
    progressBar.setValue(value);
    progressBar.update(progressBar.getGraphics());
    repaint();
  }
  
  ///////////////////////////////////////////////
  //           Other public methods            //
  ///////////////////////////////////////////////

  ////////////////////////////////////////////
  //           Protected methods            //
  ////////////////////////////////////////////

  ////////////////////////////////////////////
  //         Package visible methods        //
  ////////////////////////////////////////////

  //////////////////////////////////////////
  //           Private methods            //
  //////////////////////////////////////////

}


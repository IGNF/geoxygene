/*******************************************************************************
 * This software is released under the licence CeCILL
 *  
 *  see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 *  
 *  see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 *  
 *  @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.cartagen.software.interfacecartagen.annexes;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class CartAGenProgressBar extends JFrame implements PropertyChangeListener {

  private static final long serialVersionUID = 1L;
  private String frameTitle = "Progress";
  private JProgressBar bar;
  
  private static CartAGenProgressBar progressBar;
  
  public static CartAGenProgressBar getInstance(){
    if(progressBar != null) return progressBar;
    return new CartAGenProgressBar();
  }
  
  private CartAGenProgressBar(){
    super();
    this.setTitle(frameTitle);
    this.setSize(300, 80);
    bar = new JProgressBar(0, 100);
    bar.setStringPainted(true);
    bar.setValue(0);
    JPanel panel = new JPanel();
    panel.add(bar);
    this.getContentPane().add(panel);
    progressBar = this;
    this.setAlwaysOnTop(true);
  }
  
  
  public JProgressBar getBar() {
    return bar;
  }

  public void setBar(JProgressBar bar) {
    this.bar = bar;
  }

  public void setFrameTitle(String title){
    this.frameTitle = title;
    this.setTitle(title);
  }

  /**
   * Invoked when task's progress property changes.
   */
  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if ("progress" == evt.getPropertyName()) {
      int progress = (Integer) evt.getNewValue();
      bar.setValue(progress);
      bar.setString(progress + " %");
    } 
  }
  
  public void showProgressBar(){
    this.bar.setValue(0);
    this.setVisible(true);
  }
  
  public void closeProgressBar(){
    this.bar.setValue(100);
    this.setVisible(false);
  }

}

/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.util;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import fr.ign.cogit.cartagen.software.dataset.CartAGenDB;
import fr.ign.cogit.cartagen.software.dataset.CartAGenDoc;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.I18N;
import fr.ign.cogit.cartagen.software.interfacecartagen.utilities.swingcomponents.frame.ShowWKTFrame;
import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.bookmarks.Bookmark;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.bookmarks.BookmarkSet;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.bookmarks.LoadBookmarkFrame;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.AttributeQueryAction;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.LoadObjectSelection;
import fr.ign.cogit.geoxygene.appli.plugin.cartagen.selection.SaveObjectSelection;

/**
 * Extra menu that contains utility functions of CartAGen.
 * @author GTouya
 * 
 */
public class UtilitiesGUIComponent extends JMenu {

  private ProjectFrame view;

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  public UtilitiesGUIComponent(GeOxygeneApplication appli, String title) {
    super(title);
    this.view = appli.getMainFrame().getSelectedProjectFrame();
    JMenu bookmarkMenu = new JMenu("Bookmarks");
    this.add(bookmarkMenu);
    bookmarkMenu.add(new JMenuItem(new SaveBookmark()));
    bookmarkMenu.add(new JMenuItem(new GoToBookmark()));
    this.addSeparator();
    JMenu selectionMenu = new JMenu("Selection");
    selectionMenu.add(new JMenuItem(new AttributeQueryAction(appli)));
    selectionMenu.add(new JMenuItem(new SaveObjectSelection(appli)));
    selectionMenu.add(new JMenuItem(new LoadObjectSelection(appli)));
    this.add(selectionMenu);
    this.addSeparator();
    this.add(new JMenuItem(new ShowWktAction()));
    this.add(new JMenuItem(new InternationalisationAction()));
  }

  /**
   * Action that allows to save the current window view as a bookmark, and store
   * it in XML file.
   * @author GTouya
   * 
   */
  class SaveBookmark extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDB dataset = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB();
      BookmarkSet bookSet = new BookmarkSet(dataset, view);

      Bookmark book = bookSet.buildNewBookmark();
      JFileChooser fc = new JFileChooser();
      int returnVal = fc.showSaveDialog(view.getGui());
      if (returnVal != JFileChooser.APPROVE_OPTION) {
        return;
      }
      File fic = fc.getSelectedFile();
      try {
        bookSet.addBookmarkToXml(fic, book);
      } catch (ParserConfigurationException e) {
        e.printStackTrace();
      } catch (SAXException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      } catch (TransformerException e) {
        e.printStackTrace();
      }
    }

    public SaveBookmark() {
      putValue(Action.SHORT_DESCRIPTION,
          "Save the current window view as a bookmark, stored in XML");
      putValue(Action.NAME, "Bookmark window");
    }
  }

  /**
   * Action that allows to load a previously stored in XML bookmark, and to move
   * the current window to the bookmark extents.
   * @author GTouya
   * 
   */
  class GoToBookmark extends AbstractAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void actionPerformed(ActionEvent arg0) {
      CartAGenDB dataset = CartAGenDoc.getInstance().getCurrentDataset()
          .getCartAGenDB();
      BookmarkSet bookSet = new BookmarkSet(dataset, view);
      try {
        bookSet.loadXmlBookmarks();
      } catch (ParserConfigurationException e) {
        e.printStackTrace();
      } catch (SAXException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      LoadBookmarkFrame frame = new LoadBookmarkFrame(bookSet);
      frame.setVisible(true);
    }

    public GoToBookmark() {
      putValue(Action.SHORT_DESCRIPTION,
          "Go to a bookmark previously stored in XML file");
      putValue(Action.NAME, "Load a stored bookmark");
    }
  }

  class InternationalisationAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    public InternationalisationAction() {
      putValue(Action.SHORT_DESCRIPTION,
          I18N.getString("MainLabels.changeLanguageDescr"));
      putValue(Action.NAME, I18N.getString("MainLabels.changeLanguage"));
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      InternationalisationFrame frame = new InternationalisationFrame();
      frame.setVisible(true);
    }
  }

  class InternationalisationFrame extends JFrame implements ActionListener {

    /****/
    private static final long serialVersionUID = 1L;
    private JComboBox combo;

    InternationalisationFrame() {
      super(I18N.getString("MainLabels.changeLanguage"));
      this.setSize(200, 150);
      combo = new JComboBox(new Locale[] { Locale.FRANCE, Locale.ENGLISH });
      combo.setPreferredSize(new Dimension(150, 20));
      combo.setMaximumSize(new Dimension(150, 20));
      combo.setMinimumSize(new Dimension(150, 20));
      this.getContentPane().add(combo);
      JPanel pButtons = new JPanel();
      JButton btnOk = new JButton("OK");
      btnOk.addActionListener(this);
      btnOk.setActionCommand("ok");
      JButton btnCancel = new JButton(I18N.getString("MainLabels.lblCancel"));
      btnCancel.addActionListener(this);
      btnCancel.setActionCommand("cancel");
      pButtons.add(btnOk);
      pButtons.add(btnCancel);
      pButtons.setLayout(new BoxLayout(pButtons, BoxLayout.X_AXIS));
      this.getContentPane().add(pButtons);
      this.getContentPane().setLayout(
          new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
      this.pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (e.getActionCommand().equals("cancel"))
        this.setVisible(false);
      else {
        I18N.changeLocale((Locale) combo.getSelectedItem());
        this.setVisible(false);
      }
    }

  }

  class ShowWktAction extends AbstractAction {

    /****/
    private static final long serialVersionUID = 1L;

    public ShowWktAction() {
      putValue(Action.SHORT_DESCRIPTION,
          I18N.getString("Draw a WKT (String) geometry in the geometry pool"));
      putValue(Action.NAME, "Draw WKT geometry");
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
      ShowWKTFrame frame = new ShowWKTFrame();
      frame.setVisible(true);
    }
  }

}

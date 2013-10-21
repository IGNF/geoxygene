/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.bookmarks;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.util.HashSet;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.xml.transform.TransformerException;

public class LoadBookmarkFrame extends JFrame implements ActionListener {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  JList listeBooks;
  BookmarkSet bookSet;

  /*
   * (non-Javadoc)
   * 
   * @see
   * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
   */
  @Override
  public void actionPerformed(ActionEvent e) {
    try {

      if (e.getActionCommand().equals("Zoom")) {
        Bookmark book = (Bookmark) listeBooks.getSelectedValue();
        bookSet.zoomToBookmark(book);
        this.setExtendedState(Frame.ICONIFIED);
      } else if (e.getActionCommand().equals("Close")) {
        this.setVisible(false);
      } else if (e.getActionCommand().equals("Add")) {
        Bookmark book = bookSet.buildNewBookmark();
        bookSet.addBookmark(book);
        DefaultListModel dlm = (DefaultListModel) listeBooks.getModel();
        dlm.addElement(book);
        listeBooks.setModel(dlm);
        this.pack();
      } else if (e.getActionCommand().equals("Save")) {
        bookSet.saveToXml();
      }
    } catch (IOException exc2) {
      exc2.printStackTrace();
    } catch (TransformerException e1) {
      e1.printStackTrace();
    } catch (NoninvertibleTransformException e1) {
      e1.printStackTrace();
    }
  }

  public LoadBookmarkFrame(BookmarkSet bookmarkSet) {
    super("List of the Bookmarks for the current dataset");
    this.setSize(400, 400);

    // *********************************
    // PANNEAU CONTENANT LA LISTE DES BOOKMARKS
    // *********************************
    JPanel panelListe = new JPanel();
    DefaultListModel dlm = new DefaultListModel();
    bookSet = bookmarkSet;
    HashSet<Bookmark> setBooks = bookSet.filterBookmarks(bookmarkSet
        .getCurrentDataset().getName());
    for (Bookmark book : setBooks)
      dlm.addElement(book);
    listeBooks = new JList(dlm);
    listeBooks.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    listeBooks.setPreferredSize(new Dimension(70, 200));
    panelListe.add(new JScrollPane(listeBooks));
    panelListe.setLayout(new BoxLayout(panelListe, BoxLayout.X_AXIS));

    // *********************************
    // PANNEAU CONTENANT LES BOUTONS D'ACTION
    // *********************************
    JPanel panelBoutons = new JPanel();
    JButton btnZoom = new JButton("Zoom");
    btnZoom.addActionListener(this);
    btnZoom.setActionCommand("Zoom");
    btnZoom.setPreferredSize(new Dimension(100, 50));
    JButton btnAjouter = new JButton("Add Bookmark");
    btnAjouter.addActionListener(this);
    btnAjouter.setActionCommand("Add");
    btnAjouter.setPreferredSize(new Dimension(100, 50));
    JButton btnSauver = new JButton("Save");
    btnSauver.addActionListener(this);
    btnSauver.setActionCommand("Save");
    btnSauver.setToolTipText("Save bookmarks to XML");
    btnSauver.setPreferredSize(new Dimension(100, 50));
    panelBoutons.add(btnZoom);
    panelBoutons.add(btnAjouter);
    panelBoutons.add(btnSauver);
    panelBoutons.setLayout(new BoxLayout(panelBoutons, BoxLayout.X_AXIS));

    // ***********************************
    // PANNEAU CONTENANT LE BOUTON FERMER
    // ***********************************
    JButton btnFermer = new JButton("Close");
    btnFermer.addActionListener(this);
    btnFermer.setActionCommand("Close");
    btnFermer.setPreferredSize(new Dimension(100, 50));

    // *********************************
    // LA FRAME
    // *********************************
    this.getContentPane().add(panelListe);
    this.getContentPane().add(panelBoutons);
    this.getContentPane().add(btnFermer);
    this.getContentPane().setLayout(
        new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
    this.setVisible(true);
    this.pack();
  }

}

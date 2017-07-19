/*******************************************************************************
 * This software is released under the licence CeCILL
 * 
 * see Licence_CeCILL-C_fr.html see Licence_CeCILL-C_en.html
 * 
 * see <a href="http://www.cecill.info/">http://www.cecill.info/a>
 * 
 * @copyright IGN
 ******************************************************************************/
package fr.ign.cogit.geoxygene.appli.plugin.cartagen.dataset;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.TransferHandler;

public class ReportingListTransferHandler extends TransferHandler {
  private static final long serialVersionUID = -6992190369890036500L;

  DataFlavor localArrayListFlavor, serialArrayListFlavor;
  String localArrayListType = DataFlavor.javaJVMLocalObjectMimeType
      + ";class=java.util.ArrayList";
  JList source = null; // ////////// SOURCE
  int[] indices = null;
  int addIndex = -1; // Location where items were added
  int addCount = 0; // Number of items added

  /**
   * 
   */

  public ReportingListTransferHandler() {
    try {
      this.localArrayListFlavor = new DataFlavor(this.localArrayListType);
    } catch (ClassNotFoundException e) {
      System.out.println(
          "ReportingListTransferHandler: unable to create data flavor");
    }
    this.serialArrayListFlavor = new DataFlavor(ArrayList.class, "ArrayList");
  }

  /**
   * 
   */

  @Override
  public boolean importData(JComponent c, Transferable t) {
    JList target = null;
    ArrayList<?> alist = null;
    if (!this.canImport(c, t.getTransferDataFlavors())) {
      return false;
    }
    try {
      target = (JList) c;
      if (this.hasLocalArrayListFlavor(t.getTransferDataFlavors())) {
        alist = (ArrayList<?>) t.getTransferData(this.localArrayListFlavor);
      } else if (this.hasSerialArrayListFlavor(t.getTransferDataFlavors())) {
        alist = (ArrayList<?>) t.getTransferData(this.serialArrayListFlavor);
      } else {
        return false;
      }
    } catch (UnsupportedFlavorException ufe) {
      System.out.println("importData: unsupported data flavor");
      return false;
    } catch (IOException ioe) {
      System.out.println("importData: I/O exception");
      return false;
    }

    // if(target.getName().equals("right") && target != source) {
    // if(target != source) {
    // String message = "ok";
    // int retVal = JOptionPane.showConfirmDialog(target, message,
    // "Confirm",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
    // System.out.println("retVal = " + retVal);
    // if(retVal == JOptionPane.NO_OPTION || retVal ==
    // JOptionPane.CLOSED_OPTION)
    // {System.out.println("pb");
    // return false;}
    // }

    // At this point we use the same code to retrieve the data
    // locally or serially.

    // We'll drop at the current selected index.
    int index = target.getSelectedIndex();

    // Prevent the user from dropping data back on itself.
    // For example, if the user is moving items #4,#5,#6 and #7 and
    // attempts to insert the items after item #5, this would
    // be problematic when removing the original items.
    // This is interpreted as dropping the same data on itself
    // and has no effect.

    if (this.source.equals(target)) {
      if (this.indices != null && index >= this.indices[0] - 1
          && index <= this.indices[this.indices.length - 1]) {
        this.indices = null;

        return true;
      }
    }

    // action d'import
    DefaultListModel listModel = (DefaultListModel) target.getModel();
    int max = listModel.getSize();
    if (index < 0) {
      index = max;
    } else {
      index++;
      if (index > max) {
        index = max;
      }
    }

    this.addIndex = index;
    this.addCount = alist.size();

    for (int i = 0; i < alist.size(); i++) {
      listModel.add(index++, alist.get(i));
    }
    return true;

  }

  /**
   * 
   */

  @Override
  protected void exportDone(JComponent c, Transferable data, int action) {

    if ((action == TransferHandler.MOVE) && (this.indices != null)) {
      DefaultListModel model = (DefaultListModel) this.source.getModel();

      // If we are moving items around in the same list, we
      // need to adjust the indices accordingly since those
      // after the insertion point have moved.
      if (this.addCount > 0) {
        for (int i = 0; i < this.indices.length - 1; i++) {
          if (this.indices[i] > this.addIndex
              && this.indices[i] + this.addCount < model.getSize()) {
            this.indices[i] += this.addCount;
          }
        }
      }
      for (int i = this.indices.length - 1; i >= 0; i--) {
        model.remove(this.indices[i]);
      }
    }
    this.indices = null;
    this.addIndex = -1;
    this.addCount = 0;

  }

  /**
   * 
   * @param flavors
   * @return
   */

  private boolean hasLocalArrayListFlavor(DataFlavor[] flavors) {
    if (this.localArrayListFlavor == null) {
      return false;
    }
    for (DataFlavor flavor : flavors) {
      if (flavor.equals(this.localArrayListFlavor)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   * @param flavors
   * @return
   */

  private boolean hasSerialArrayListFlavor(DataFlavor[] flavors) {
    if (this.serialArrayListFlavor == null) {
      return false;
    }

    for (DataFlavor flavor : flavors) {
      if (flavor.equals(this.serialArrayListFlavor)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 
   */

  @Override
  public boolean canImport(JComponent c, DataFlavor[] flavors) {
    if (this.hasLocalArrayListFlavor(flavors)) {
      return true;
    }
    if (this.hasSerialArrayListFlavor(flavors)) {
      return true;
    }
    return false;
  }

  /**
   * 
   */

  @Override
  protected Transferable createTransferable(JComponent c) {
    if (c instanceof JList) {
      this.source = (JList) c;
      this.indices = this.source.getSelectedIndices();
      Object[] values = this.source.getSelectedValues();
      if (values == null || values.length == 0) {
        return null;
      }
      ArrayList<String> alist = new ArrayList<String>(values.length);
      for (Object o : values) {
        String str = o.toString();
        if (str == null) {
          str = "";
        }
        alist.add(str);
      }
      return new ReportingListTransferable(alist);
    }
    return null;
  }

  /**
   * 
   */

  @Override
  public int getSourceActions(JComponent c) {
    return TransferHandler.MOVE;
  }

  /**
   * 
   * @author JRenard
   * 
   */

  public class ReportingListTransferable implements Transferable {
    ArrayList<String> data;

    public ReportingListTransferable(ArrayList<String> alist) {
      this.data = alist;
    }

    @Override
    public Object getTransferData(DataFlavor flavor)
        throws UnsupportedFlavorException {
      if (!this.isDataFlavorSupported(flavor)) {
        throw new UnsupportedFlavorException(flavor);
      }
      return this.data;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[] {
          ReportingListTransferHandler.this.localArrayListFlavor,
          ReportingListTransferHandler.this.serialArrayListFlavor };
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
      if (ReportingListTransferHandler.this.localArrayListFlavor
          .equals(flavor)) {
        return true;
      }
      if (ReportingListTransferHandler.this.serialArrayListFlavor
          .equals(flavor)) {
        return true;
      }
      return false;
    }
  }
}

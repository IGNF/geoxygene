/*******************************************************************************
 * This file is part of the GeOxygene project source files.
 * 
 * GeOxygene aims at providing an open framework which implements OGC/ISO
 * specifications for the development and deployment of geographic (GIS)
 * applications. It is a open source contribution of the COGIT laboratory at the
 * Institut Géographique National (the French National Mapping Agency).
 * 
 * See: http://oxygene-project.sourceforge.net
 * 
 * Copyright (C) 2005 Institut Géographique National
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library (see file LICENSE if present); if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 *******************************************************************************/

package fr.ign.cogit.geoxygene.scripting;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Logger;

/**
 * @author JeT
 *
 */
public class ConsoleOutputStream extends OutputStream {

  private static Logger logger = Logger.getLogger(ConsoleOutputStream.class.getName());
  private StyledDocument doc = null;
  private SimpleAttributeSet attr = null;

  /**
   * Constructor
   * @param textPane
   * @param textColor
   */
  public ConsoleOutputStream(final StyledDocument doc, final Color textColor) {
    super();
    this.doc = doc;
    this.attr = new SimpleAttributeSet();
    StyleConstants.setForeground(this.attr, textColor);
  }

  private void updateDocument(final String text) {

    try {
      ConsoleOutputStream.this.doc.insertString(ConsoleOutputStream.this.doc.getLength(), text, this.attr);
    } catch (BadLocationException e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see java.io.OutputStream#close()
   */
  @Override
  public void close() throws IOException {
    super.close();
  }

  /* (non-Javadoc)
   * @see java.io.OutputStream#flush()
   */
  @Override
  public void flush() throws IOException {
    super.flush();
  }

  @Override
  public void write(final int i) throws IOException {
    this.updateDocument(String.valueOf((char) i));
  }

  /* (non-Javadoc)
   * @see java.io.OutputStream#write(byte[], int, int)
   */
  @Override
  public void write(final byte[] b, final int off, final int len) throws IOException {
    this.updateDocument(new String(b, off, len));
  }

  /* (non-Javadoc)
   * @see java.io.OutputStream#write(byte[])
   */
  @Override
  public void write(final byte[] b) throws IOException {
    this.updateDocument(new String(b, 0, b.length));
  }

}

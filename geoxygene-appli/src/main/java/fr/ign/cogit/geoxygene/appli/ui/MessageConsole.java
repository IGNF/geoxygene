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

package fr.ign.cogit.geoxygene.appli.ui;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

/**
 * @author JeT
 *         manage and display messages
 */
public class MessageConsole implements MouseListener, WindowFocusListener {
    private static final Color backgroundColor = new Color(255, 250, 235);
    private static final int MAX_MESSAGE_NUMBER = 100;
    private final Collection<Message> messages = Collections.synchronizedCollection(new LimitedQueue<Message>(MAX_MESSAGE_NUMBER));
    private JPanel mainPanel = null;
    private JLabel lastMessageLabel = null;
    static final DateFormat shortDateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private JPanel expandedPanel = null;
    private JDialog expandedDialog = null;
    boolean displayed = false; // expanded dialog visibility
    private static final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();

    private static final Logger logger = Logger.getLogger(MessageConsole.class.getName()); // logger

    /**
     * Constructor
     */
    public MessageConsole() {
        super();
    }

    /**
     * get message console gui
     */
    public JComponent getGui() {
        return this.getMainPanel();
    }

    /**
     * Main panel containing the last message label
     */
    private JPanel getMainPanel() {
        if (this.mainPanel == null) {
            this.mainPanel = new JPanel(new BorderLayout());
            this.mainPanel.add(this.getLastMessageLabel(), BorderLayout.CENTER);
            this.mainPanel.setBackground(Color.white);
            this.mainPanel.addMouseListener(this);
        }
        return this.mainPanel;
    }

    private JLabel getLastMessageLabel() {
        if (this.lastMessageLabel == null) {
            this.lastMessageLabel = new JLabel("console");
            this.lastMessageLabel.setFont(new Font("Serif", Font.PLAIN, 10));
            this.lastMessageLabel.setBackground(backgroundColor);
            this.lastMessageLabel.setOpaque(false);
            this.lastMessageLabel.addMouseListener(this);
        }
        return this.lastMessageLabel;
    }

    /**
     * @return the mainPanel
     */
    private JPanel getExpandedPanel() {
        if (this.expandedPanel == null) {
            this.expandedPanel = new JPanel(new GridLayout(0, 1));
            for (Message message : this.messages) {
                JLabel label = new JLabel(this.generateLabelContent(message));
                label.addMouseListener(this);
                label.setOpaque(true);
                label.setFocusable(false);
                label.setBackground(backgroundColor);
                label.setToolTipText(message.content);
                this.expandedPanel.add(label);
            }
        }
        return this.expandedPanel;
    }

    /**
     * display all messages in a autonomous dialog
     */
    private void showExpandedDialog() {
        if (this.displayed) {
            return;
        }
        this.getExpandedDialog().pack();
        Dimension size = this.getExpandedDialog().getSize();
        // limit the dialog height
        if (size.height > Toolkit.getDefaultToolkit().getScreenSize().height / 2) {
            size.height = Toolkit.getDefaultToolkit().getScreenSize().height / 2;
        }
        size.width = this.getMainPanel().getWidth();
        this.getExpandedDialog().setSize(size);
        Point location = this.getLastMessageLabel().getLocationOnScreen();
        if (location == null) {
            return;
        }
        this.displayed = true;
        this.getExpandedDialog().setLocation(location.x, (int) (location.y - size.getHeight()));
        this.getExpandedDialog().setVisible(true);
    }

    /**
     * display all messages in a autonomous dialog
     */
    private void hideExpandedDialog() {
        this.getExpandedDialog().setVisible(false);
        this.invalidateExpandedDialog();
    }

    /**
     * invalidate expanded dialog. It will be reconstructed next time
     * it will be shown
     */
    private void invalidateExpandedDialog() {
        if (this.expandedDialog == null) {
            return;
        }
        for (Component child : this.expandedDialog.getComponents()) {
            child.removeMouseListener(this);
        }
        this.expandedDialog.dispose();
        this.expandedDialog.removeMouseListener(this);
        this.expandedDialog = null;
        this.expandedPanel = null;
    }

    /**
     * expanded dialog is used to keep the expanded panel on top of all other
     * windows
     * 
     * @return the expanded dialog containing the expanded panel
     */
    private JDialog getExpandedDialog() {
        if (this.expandedDialog == null) {
            this.expandedDialog = new JDialog(SwingUtilities.getWindowAncestor(this.getGui()), "expanded message console");
            this.expandedDialog.add(new JScrollPane(this.getExpandedPanel()));
            this.expandedDialog.setModalityType(ModalityType.MODELESS);
            this.expandedDialog.setUndecorated(true);
            this.expandedDialog.setResizable(true);
            this.expandedDialog.addWindowFocusListener(this);
            Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
                @Override
                public void eventDispatched(AWTEvent event) {
                    MouseEvent me = (MouseEvent) event;
                    Component mouseComponent = me.getComponent();
                    if (mouseComponent == MessageConsole.this.getExpandedDialog()) {
                        if (me.getID() == MouseEvent.MOUSE_EXITED) {
                            MessageConsole.this.requestCloseExpandedDialog();
                        }
                    }
                }
            }, AWTEvent.MOUSE_EVENT_MASK);
            this.expandedDialog.pack();
        }
        return this.expandedDialog;
    }

    /**
     * add a message to the message console
     * 
     * @param type
     *            message type
     * @param content
     *            message content
     */
    public void addMessage(final Message.MessageType type, final String messageContent) {
        if (messageContent == null) {
            logger.warn("try to add a null message");
        }
        Message message = new Message(type, messageContent);
        this.messages.add(message);
        this.getLastMessageLabel().setText(this.generateLabelContent(message));
    }

    /**
     * Set the message content depending on message type.
     * This method should be in an extern Renderer class to be modified
     * 
     * @param message
     * @return
     */
    private String generateLabelContent(Message message) {
        Color messageColor = Color.black;
        Color dateColor = Color.black;
        if (message.type == Message.MessageType.DEBUG) {
            messageColor = Color.green;
        }
        if (message.type == Message.MessageType.WARNING) {
            messageColor = Color.yellow;
        }
        if (message.type == Message.MessageType.ERROR) {
            messageColor = Color.red;
        }
        if (message.type == Message.MessageType.FATAL) {
            messageColor = Color.red;
        }
        String date = shortDateFormat.format(message.date);
        return "<html><font color=" + String.format("#%06X", (0xFFFFFF & dateColor.getRGB())) + " size='2' ><i>[" + date + "]</i></font>&nbsp;<font color="
                + String.format("#%06X", (0xFFFFFF & messageColor.getRGB())) + "><b>" + message.content + "</b></font></html>";
    }

    //    /**
    //     * constraints the list size to a limited number of elements.
    //     * IMPORTANT: Use only add(T) method. Not add(index, T) or set()
    //     * 
    //     * @author JeT
    //     * 
    //     * @param <T>
    //     */
    //    private class LimitedArrayList<T> extends ArrayList<T> {
    //        private static final long serialVersionUID = -4163971329925949275L; // serializable UID
    //        private final int limit;
    //
    //        public LimitedArrayList(int limit) {
    //            this.limit = limit;
    //        }
    //
    //        @Override
    //        public synchronized boolean add(T item) {
    //            if (this.size() > this.limit) {
    //                this.remove(0);
    //            }
    //            return super.add(item);
    //        }
    //    }

    /**
     * constraints the list size to a limited number of elements.
     * IMPORTANT: Use only add(T) method. Not add(index, T) or set()
     * 
     * @author JeT
     * 
     * @param <T>
     */
    public class LimitedQueue<E> extends LinkedList<E> {
        private static final long serialVersionUID = 1L;
        private final int limit;

        public LimitedQueue(int limit) {
            this.limit = limit;
        }

        @Override
        public boolean add(E o) {
            super.add(o);
            while (this.size() > this.limit) {
                super.remove();
            }
            return true;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (((JComponent) e.getSource()).getParent() == this.getMainPanel() && e.getButton() == MouseEvent.BUTTON1) {
            this.showExpandedDialog();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // nothing to do

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // nothing to do

    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getSource() instanceof JLabel && ((JComponent) e.getSource()).getParent() == this.getMainPanel()) {
            this.showExpandedDialog();
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    /**
     * 
     */
    private void requestCloseExpandedDialog() {
        this.displayed = false;
        // wait a small time before really closing the dialog
        Runnable hideTask = new Runnable() {
            @Override
            public void run() {
                if (MessageConsole.this.displayed == false) {
                    MessageConsole.this.hideExpandedDialog();
                }
            }
        };
        worker.schedule(hideTask, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {

    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        this.requestCloseExpandedDialog();
    }

}

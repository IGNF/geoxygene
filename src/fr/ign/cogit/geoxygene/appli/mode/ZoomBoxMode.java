package fr.ign.cogit.geoxygene.appli.mode;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.NoninvertibleTransformException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import fr.ign.cogit.geoxygene.I18N;
import fr.ign.cogit.geoxygene.appli.MainFrame;
import fr.ign.cogit.geoxygene.appli.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.render.RenderUtil;
import fr.ign.cogit.geoxygene.feature.DefaultFeature;
import fr.ign.cogit.geoxygene.feature.FT_Feature;
import fr.ign.cogit.geoxygene.spatial.coordgeom.DirectPosition;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Envelope;
import fr.ign.cogit.geoxygene.spatial.coordgeom.GM_Polygon;

/**
 * @author Charlotte Hoarau
 *
 */
public class ZoomBoxMode extends AbstractMode {
    /**
     * @param theMainFrame the main frame
     * @param theModeSelector the mode selector
     */
    public ZoomBoxMode(final MainFrame theMainFrame,
            final ModeSelector theModeSelector) {
        super(theMainFrame, theModeSelector);
    }

    private DirectPosition initialPoint = null;
    private DirectPosition lastPoint = null;

    @Override
    protected final JButton createButton() {
        return new JButton(new ImageIcon(this.getClass().
                getResource("/images/icons/16x16/zoomBox.png"))); //$NON-NLS-1$
    }
    @Override
    public void mousePressed(final MouseEvent e) {
    	ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
        if((SwingUtilities.isLeftMouseButton(e))) {
            try {
                DirectPosition p = frame.getLayerViewPanel().getViewport().
                toModelDirectPosition(e.getPoint());
                this.initialPoint = p;
            } catch (NoninvertibleTransformException e1) { e1.printStackTrace(); }
        }
    }
    @Override
    public void mouseDragged(final MouseEvent e) {
    	ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
        if((SwingUtilities.isLeftMouseButton(e))) {
            try {
                DirectPosition p = frame.getLayerViewPanel().getViewport().
                							toModelDirectPosition(e.getPoint());

                GM_Envelope envelope = getEnvelope(initialPoint, p);
                GM_Polygon rect = new GM_Polygon(envelope);
                FT_Feature feature = new DefaultFeature(rect);

                Graphics2D graphics2D = (Graphics2D) frame.getLayerViewPanel().getGraphics();
                graphics2D.setXORMode(new Color(4, 52, 87));
                graphics2D.setStroke(new BasicStroke(2));
                if (lastPoint != null) {
                    GM_Envelope lastEnvelope = getEnvelope(initialPoint, lastPoint);
                    GM_Polygon lastRect = new GM_Polygon(lastEnvelope);
                    FT_Feature lastFeature = new DefaultFeature(lastRect);
                    RenderUtil.draw(lastFeature.getGeom(),
                                frame.getLayerViewPanel().getViewport(),
                                graphics2D);
                }
                RenderUtil.draw(feature.getGeom(),
                        frame.getLayerViewPanel().getViewport(),
                        graphics2D);
                lastPoint = p;
            } catch (NoninvertibleTransformException e1) {
            	e1.printStackTrace();
            }
        }
    }

    private GM_Envelope getEnvelope(DirectPosition p1,
                DirectPosition p2) {
        double xmin = Math.min(p1.getX(), p2.getX());
        double ymin = Math.min(p1.getY(), p2.getY());
        double xmax = Math.max(p1.getX(), p2.getX());
        double ymax = Math.max(p1.getY(), p2.getY());
        return new GM_Envelope(xmin, xmax, ymin, ymax);
    }
    @Override
	public void mouseReleased(final MouseEvent e) {
    	 ProjectFrame frame = this.mainFrame.getSelectedProjectFrame();
         if((SwingUtilities.isLeftMouseButton(e))) {
             try {
                DirectPosition p = frame.getLayerViewPanel().getViewport()
                			.toModelDirectPosition(e.getPoint());
	            GM_Envelope env = getEnvelope(initialPoint, p);
	            initialPoint = null;
	            lastPoint = null;
	            frame.getLayerViewPanel().getViewport().zoom(env);
	            frame.getLayerViewPanel().superRepaint();
             } catch (NoninvertibleTransformException e1) {
            	 e1.printStackTrace();
             }
         }
    }
    @Override
    protected String getToolTipText() {
        return I18N.getString("ZoomBox.ToolTip"); //$NON-NLS-1$
    }
    @Override
    public Cursor getCursor() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Cursor cursor = toolkit.createCustomCursor(
                toolkit.getImage(
                "src/resources/images/cursors/32x32/zoomCursor.gif"), //$NON-NLS-1$
                new Point(16, 16), "Zoom"); //$NON-NLS-1$
        return cursor;
    }
}

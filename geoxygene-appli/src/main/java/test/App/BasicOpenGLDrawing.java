package test.App;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import org.apache.log4j.Logger;
import org.lwjgl.LWJGLException;

import fr.ign.cogit.geoxygene.util.gl.GLException;

public class BasicOpenGLDrawing {

    private static final Logger logger = Logger.getLogger(BasicOpenGLDrawing.class.getName()); // logger

    private JFrame frame = null;

    public BasicOpenGLDrawing() throws LWJGLException, GLException {
        this.frame = new JFrame();
        BasicOpenGLCanvas canvas = new BasicOpenGLCanvas();
        canvas.setBackground(Color.pink);
        JPanel canvasContainer = new JPanel(new BorderLayout());
        canvasContainer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5),
                BorderFactory.createBevelBorder(BevelBorder.LOWERED)));
        canvasContainer.add(canvas, BorderLayout.CENTER);
        this.frame.getContentPane().add(canvasContainer, BorderLayout.CENTER);
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void run() {
        this.frame.setSize(800, 600);
        this.frame.setVisible(true);
    }

    public static void main(String[] args) {
        BasicOpenGLDrawing app;
        try {
            app = new BasicOpenGLDrawing();
            app.run();
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
    }
}

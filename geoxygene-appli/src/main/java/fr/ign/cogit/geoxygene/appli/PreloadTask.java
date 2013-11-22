package fr.ign.cogit.geoxygene.appli;

import java.io.File;
import java.util.Date;

import fr.ign.cogit.geoxygene.appli.api.MainFrame;
import fr.ign.cogit.geoxygene.appli.api.ProjectFrame;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory;
import fr.ign.cogit.geoxygene.appli.layer.LayerViewPanelFactory.RenderingType;
import fr.ign.cogit.geoxygene.appli.task.AbstractTask;
import fr.ign.cogit.geoxygene.appli.task.TaskState;

public class PreloadTask extends AbstractTask {

    private String filename = null;
    private RenderingType renderingType = RenderingType.AWT;
    private MainFrame mainFrame = null;
    private final static int MAX_CREATION_TIME = 5000;
    private ProjectFrame projectFrame = null;

    /**
     * Constructor
     * 
     * @param mainFrame
     *            main frame
     * @param projectFrame
     *            project frame
     * @param filename
     *            file to load
     * @param renderingType
     *            visualization type
     */
    public PreloadTask(MainFrame mainFrame, String filename) {
        super("loading " + filename);
        this.filename = filename;
        this.mainFrame = mainFrame;
    }

    /**
     * Constructor
     * 
     * @param mainFrame
     *            main frame
     * @param projectFrame
     *            project frame (one is created if null)
     * @param filename
     *            file to load
     * @param renderingType
     *            visualization type
     */
    public PreloadTask(MainFrame mainFrame, ProjectFrame projectFrame, String filename, RenderingType renderingType) {
        super("loading " + filename);
        this.filename = filename;
        this.renderingType = renderingType;
        this.mainFrame = mainFrame;
        this.projectFrame = projectFrame;
    }

    @Override
    public boolean isProgressable() {
        return false;
    }

    @Override
    public boolean isPausable() {
        return false;
    }

    @Override
    public boolean isStopable() {
        return false;
    }

    @Override
    public void run() {
        this.setState(TaskState.INITIALIZING);
        // configuration: switch to AWT or GL Layer view
        try {
            LayerViewPanelFactory.setRenderingType(this.renderingType);

            // create a new project frame or use the existing one
            // wait for desktopFrames to be created
            long startTime = new Date().getTime();
            while (this.projectFrame == null && new Date().getTime() - startTime < MAX_CREATION_TIME) {
                if (this.mainFrame.getDesktopProjectFrames().length != 0) {
                    this.projectFrame = this.mainFrame.getDesktopProjectFrames()[this.mainFrame.getDesktopProjectFrames().length - 1];
                }
                if (this.projectFrame == null) {
                    try {
                        this.projectFrame = this.mainFrame.newProjectFrame();
                    } catch (Exception e) {
                        // mainFrame is not ready to generate project frame...
                    }
                }
            }

            if (this.projectFrame == null) {
                this.setError(new IllegalStateException("project frame is not defined"));
                this.setState(TaskState.ERROR);
                return;
            }

            this.setState(TaskState.RUNNING);

            try {
                File file = new File(this.filename);
                this.projectFrame.addLayer(file);
            } catch (Exception e) {
                System.err.println("Exception type " + e.getClass() + " = " + e.getMessage());
                System.err.println("Cannot load file " + this.filename);
                e.printStackTrace();
            }
            this.setState(TaskState.FINISHED);
        } catch (Exception e) {
            this.setError(e);
            this.setState(TaskState.ERROR);
            e.printStackTrace();
        }

    }

}

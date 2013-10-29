package fr.ign.cogit.geoxygene.appli.panel;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;

/**
 * File Choosers.
 * 
 * 
 */
public class FileChooser {

    private JFileChooser fileChooser;

    /** The previous opened directory. */
    private File previousDirectory = new File(""); //$NON-NLS-1$

    /**
     * Return the previous opened directory.
     * 
     * @return the previous opened directory
     */
    public File getPreviousDirectory() {
        return this.previousDirectory;
    }

    /**
     * Affect the previous opened directory.
     * 
     * @param directory
     *            the previous opened directory
     */
    public void setPreviousDirectory(File directory) {
        this.previousDirectory = directory;
    }

    public FileChooser() {
        this.fileChooser = new JFileChooser();
        FileFilter shapefileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return (f.isFile() && (f.getAbsolutePath().endsWith(".shp") //$NON-NLS-1$
                        || f.getAbsolutePath().endsWith(".SHP") //$NON-NLS-1$
                        ) || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return I18N.getString("MainFrame.ShapefileDescription" //$NON-NLS-1$
                        );
            }
        };
        FileFilter geotiffFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return (f.isFile() && (f.getAbsolutePath().endsWith(".tif") //$NON-NLS-1$
                        || f.getAbsolutePath().endsWith(".TIF") //$NON-NLS-1$
                        ) || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return "GeoTiff Images"; //$NON-NLS-1$
            }
        };
        FileFilter ascFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return (f.isFile() && (f.getAbsolutePath().endsWith(".asc") //$NON-NLS-1$
                        || f.getAbsolutePath().endsWith(".ASC") //$NON-NLS-1$
                        ) || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return "Arc/Info ASCII Grids"; //$NON-NLS-1$
            }
        };
        FileFilter gpsTextFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return (f.isFile() && (f.getAbsolutePath().endsWith(".txt") //$NON-NLS-1$
                        || f.getAbsolutePath().endsWith(".TXT") //$NON-NLS-1$
                        ) || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return "GPS Text Files"; //$NON-NLS-1$
            }
        };
        FileFilter roadNetworkTextFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return (f.isFile() && (f.getAbsolutePath().endsWith(".txt") //$NON-NLS-1$
                        || f.getAbsolutePath().endsWith(".TXT") //$NON-NLS-1$
                        ) || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return "Road Network Text Files"; //$NON-NLS-1$
            }
        };
        this.fileChooser.addChoosableFileFilter(shapefileFilter);
        this.fileChooser.addChoosableFileFilter(geotiffFilter);
        this.fileChooser.addChoosableFileFilter(ascFilter);
        this.fileChooser.addChoosableFileFilter(gpsTextFilter);
        this.fileChooser.addChoosableFileFilter(roadNetworkTextFilter);
        this.fileChooser.setFileFilter(shapefileFilter);
        this.fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        this.fileChooser.setMultiSelectionEnabled(false);
        this.fileChooser.setAcceptAllFileFilterUsed(false);
        this.fileChooser.setCurrentDirectory(this.getPreviousDirectory());
    }

    public JFileChooser getFileChooser() {
        return this.fileChooser;
    }

    public File getFile(Component parent) {
        this.fileChooser.setCurrentDirectory(this.getPreviousDirectory());
        Frame window = new Frame();
        window.setIconImage(new ImageIcon(GeOxygeneApplication.class.getResource("/images/icons/16x16/page_white_add.png")).getImage());
        int returnVal = this.fileChooser.showOpenDialog(window);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.setPreviousDirectory(new File(this.fileChooser.getSelectedFile().getAbsolutePath()));
            return this.fileChooser.getSelectedFile();
        }
        return null;
    }

    public File[] getFiles(Component parent) {
        this.fileChooser.setMultiSelectionEnabled(true);
        this.fileChooser.setCurrentDirectory(this.getPreviousDirectory());
        int returnVal = this.fileChooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.setPreviousDirectory(new File(this.fileChooser.getSelectedFile().getAbsolutePath()));
            return this.fileChooser.getSelectedFiles();
        }
        return null;
    }

    public String getDescription() {
        return this.fileChooser.getFileFilter().getDescription();
    }
}

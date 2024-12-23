package fr.ign.cogit.geoxygene.appli.panel;

import java.awt.Component;
import java.awt.Frame;
import java.io.File;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fr.ign.cogit.geoxygene.appli.GeOxygeneApplication;
import fr.ign.cogit.geoxygene.appli.I18N;
import fr.ign.util.ui.JRecentFileChooser;

/**
 * File Choosers.
 * 
 * 
 */
public class FileChooser {

    private JRecentFileChooser fileChooser;

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
        this.fileChooser = new JRecentFileChooser();
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
        
        // New filter, @amasse, for Raster
        FileFilter RasterFilter = new FileFilter() {
            @Override
            // TODO : add other raster type, do not forget to implement the reading ...
            public boolean accept(final File f) {
                return (f.isFile() && (f.getAbsolutePath().endsWith(".tif") //$NON-NLS-1$
                        || f.getAbsolutePath().endsWith(".TIF") //$NON-NLS-1$
                        || f.getAbsolutePath().endsWith(".jpg") //$NON-NLS-1$
                        || f.getAbsolutePath().endsWith(".JPG") //$NON-NLS-1$
                        || f.getAbsolutePath().endsWith(".png") //$NON-NLS-1$
                        || f.getAbsolutePath().endsWith(".PNG") //$NON-NLS-1$
                        ) || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return "Raster Images"; //$NON-NLS-1$
            }
        };
 
        this.fileChooser.addChoosableFileFilter(shapefileFilter);
        this.fileChooser.addChoosableFileFilter(geotiffFilter);
        this.fileChooser.addChoosableFileFilter(ascFilter);
        this.fileChooser.addChoosableFileFilter(gpsTextFilter);
        this.fileChooser.addChoosableFileFilter(roadNetworkTextFilter);
        this.fileChooser.addChoosableFileFilter(RasterFilter);
        this.fileChooser.setFileFilter(shapefileFilter);
        this.fileChooser
                .setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        this.fileChooser.setMultiSelectionEnabled(false);
        this.fileChooser.setAcceptAllFileFilterUsed(false);
        this.fileChooser.setCurrentDirectory(this.getPreviousDirectory());
    }

    public JRecentFileChooser getFileChooser() {
        return this.fileChooser;
    }

    public JRecentFileChooser getFileChooser(String directory) {
        this.fileChooser.setCurrentDirectory(new File(directory));
        return this.fileChooser;
    }

    public File getFile(Component parent) {
        this.fileChooser.setCurrentDirectory(this.getPreviousDirectory());
        Frame window = new Frame();
        window.setIconImage(new ImageIcon(GeOxygeneApplication.class
                .getResource("/images/icons/16x16/page_white_add.png"))
                .getImage());
        int returnVal = this.fileChooser.showOpenDialog(window);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.setPreviousDirectory(new File(this.fileChooser
                    .getSelectedFile().getAbsolutePath()));
            return this.fileChooser.getSelectedFile();
        }
        return null;
    }

    public File[] getFiles(Component parent) {
        this.fileChooser.setMultiSelectionEnabled(true);
        this.fileChooser.setCurrentDirectory(this.getPreviousDirectory());
        int returnVal = this.fileChooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            this.setPreviousDirectory(new File(this.fileChooser
                    .getSelectedFile().getAbsolutePath()));
            return this.fileChooser.getSelectedFiles();
        }
        return null;
    }

    public String getDescription() {
        return this.fileChooser.getFileFilter().getDescription();
    }

    public void setRecents(List<String> recents) {
        this.fileChooser.clearRecentDirectories();
        for (String s : recents) {
            this.fileChooser.addRecentDirectories(new File(s));
        }

    }

}

package fr.ign.cogit.geoxygene.appli;

import java.awt.Component;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import fr.ign.cogit.geoxygene.I18N;

public class FileChooser {

    /**
     * The previous opened directory.
     */
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
     * @param directory the previous opened directory
     */
    public void setPreviousDirectory(File directory) {
        this.previousDirectory = directory;
    }

    public FileChooser() {
        this.fileChooser = new JFileChooser();
        FileFilter shapefileFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return (f.isFile()
                        && (f.getAbsolutePath()
                                .endsWith(".shp") //$NON-NLS-1$
                                ||
                                f.getAbsolutePath()
                                .endsWith(".SHP") //$NON-NLS-1$
                        )
                        || f.isDirectory());
            }
            @Override
            public String getDescription() {
                return I18N.getString(
                        "MainFrame.ShapefileDescription" //$NON-NLS-1$
                );
            }
        };
        FileFilter geotiffFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return (f.isFile()
                        && (f.getAbsolutePath()
                                .endsWith(".tif") //$NON-NLS-1$
                                ||
                                f.getAbsolutePath()
                                .endsWith(".TIF") //$NON-NLS-1$
                                )
                                || f.isDirectory());
            }
            @Override
            public String getDescription() {
                return "GeoTiff Images"; //$NON-NLS-1$
            }
        };
        FileFilter ascFilter = new FileFilter() {
            @Override
            public boolean accept(final File f) {
                return (f.isFile()
                        && (f.getAbsolutePath()
                                .endsWith(".asc") //$NON-NLS-1$
                                ||
                                f.getAbsolutePath()
                                .endsWith(".ASC") //$NON-NLS-1$
                                )
                                || f.isDirectory());
            }
            @Override
            public String getDescription() {
                return "Arc/Info ASCII Grids"; //$NON-NLS-1$
            }
        };
        this.fileChooser.addChoosableFileFilter(shapefileFilter);
        this.fileChooser.addChoosableFileFilter(geotiffFilter);
        this.fileChooser.addChoosableFileFilter(ascFilter);
        this.fileChooser.setFileFilter(shapefileFilter);
        this.fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.fileChooser.setMultiSelectionEnabled(false);
        this.fileChooser.setAcceptAllFileFilterUsed(false);
        this.fileChooser.setCurrentDirectory(getPreviousDirectory());
    }
    JFileChooser fileChooser;
    public File getFile(Component parent) {
        int returnVal = this.fileChooser.showOpenDialog(parent);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            setPreviousDirectory(
                    new File(this.fileChooser
                            .getSelectedFile()
                            .getAbsolutePath()));
            return this.fileChooser.getSelectedFile();
        }
        return null;
    }
}

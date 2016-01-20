package fr.ign.cogit.geoxygene.util.conversion;

import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.geotools.referencing.operation.matrix.AffineTransform2D;

/**
 * 
 * FIXME : This class should be replaced by the WorldFileWriter from Geotools.
 *
 */

public class WorldFileWriter {

    public static void write(File file, double sx, double sy, double x0, double y0, double h) {
        try {
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(Double.toString(1.0 / sx));
            bw.newLine();
            bw.write(Double.toString(0.0));
            bw.newLine();
            bw.write(Double.toString(0.0));
            bw.newLine();
            bw.write(Double.toString(1.0 / sy));
            bw.newLine();
            bw.write(Double.toString(x0));
            bw.newLine();
            bw.write(Double.toString(y0 - (h / sy)));
            bw.newLine();
            bw.flush();
            bw.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void write(File file, AffineTransform aff) {
        try {
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(Double.toString(aff.getScaleX()));
            bw.newLine();
            bw.write(Double.toString(aff.getShearX()));
            bw.newLine();
            bw.write(Double.toString(aff.getShearY()));
            bw.newLine();
            bw.write(Double.toString(aff.getScaleY()));
            bw.newLine();
            bw.write(Double.toString(aff.getTranslateX()));
            bw.newLine();
            bw.write(Double.toString(aff.getTranslateY()));
            bw.flush();
            bw.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

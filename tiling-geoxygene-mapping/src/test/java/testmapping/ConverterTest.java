package testmapping;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import fr.ign.cogit.mapping.clients.Converter;
import fr.ign.cogit.mapping.clients.geoxygene.GeoxConverter;
import fr.ign.cogit.mapping.util.ConverterUtil;

/*
 * Dr Tsatcha D.
 */
public class ConverterTest {

    @Test
    public void testConverter() {

        Coordinate c1 = new Coordinate(0, 0, 0);
        Point interest = new GeometryFactory().createPoint(c1);

        Converter conv = new GeoxConverter(interest, 10, 20, "hello");
        String textH = conv.toString();
        System.out.println("------1-" + conv.toString());
        // Converter con2=ConverterUtil.obtainConverterFromtext(textH);
        String test = "scale" + 15 + "cadre" + 0;
        int taille = test.length();

        // System.out.println("---2----"+ con2.toString());

    }

}

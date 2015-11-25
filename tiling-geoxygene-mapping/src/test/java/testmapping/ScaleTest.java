package testmapping;

import org.junit.Test;

import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.util.ScaleInfoUtil;

public class ScaleTest {

    @Test
    public void testScale() {

        String nameTable = "route_25";

        ScaleInfo info = ScaleInfoUtil.generateScale(nameTable);

        System.out.println(info.toString());

        nameTable = "route";

        info = ScaleInfoUtil.generateScale(nameTable);

        System.out.println(info.toString());

        nameTable = "e250_route";

        info = ScaleInfoUtil.generateScale(nameTable);

        System.out.println(info.toString());

        nameTable = "e_25route";

        info = ScaleInfoUtil.generateScale(nameTable);

        System.out.println(info.toString());

    }

}

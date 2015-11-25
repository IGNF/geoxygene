package testmapping;

import org.deegree.io.rtree.HyperBoundingBox;
import org.deegree.io.rtree.HyperPoint;
import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;

import fr.ign.cogit.mapping.datastructure.Node;
import fr.ign.cogit.mapping.datastructure.hexaTree.TransmittedNode;
import fr.ign.cogit.mapping.datastructure.management.ScaleInfo;
import fr.ign.cogit.mapping.util.NodeUtil;

public class NodeTest {

    @Test
    public void testNode() {

        // ce test fonctionne uniquement pour quatre elements
        ScaleInfo scale = new ScaleInfo(15, 25);
        TransmittedNode trans = new TransmittedNode(0, 0, 1,"POINT");
        System.out.println("Impression de l'echelle" + "avec HyperBound"
                + scale.printScale());
        Node node = new Node("4589", scale, "geometrie", "myName", 1, trans);

        System.out.println("Info node" + node.toString());
        String text = node.toString();
        Node newNode = NodeUtil.buildNode(text);
        System.out.println("Info new node" + newNode.toString());

        String textH = "HyperBoundingBox:BOX: P-Min (0.0, 0.0, ), P-Max (0.0, 0.0, )";

        String[] val = textH.split("\\(");
        String[] val2 = null;
        String pontText = null;
        double a, b;
        HyperPoint bornSupScale = new HyperPoint(new double[] { 0, 0 });
        HyperPoint bornInfScale = new HyperPoint(new double[] { 0, 0 });

        for (int i = 0; i < val.length; i++) {

            switch (i) {
            case 1:
                val2 = val[1].split("\\)");
                pontText = val2[0].substring(0, val2[0].length() - 2);

                a = Double.parseDouble(pontText.split(",")[0]);
                b = Double.parseDouble(pontText.split(",")[1]);
                bornInfScale = new HyperPoint(new double[] { a, b });
                break;

            case 2:
                val2 = val[2].split("\\)");
                pontText = val2[0].substring(0, val2[0].length() - 2);

                a = Double.parseDouble(pontText.split(",")[0]);
                b = Double.parseDouble(pontText.split(",")[1]);

                bornSupScale = new HyperPoint(new double[] { a, b });

                break;

            default:
                break;
            }

        }

        HyperBoundingBox MaxRectangle = new HyperBoundingBox(bornInfScale,
                bornSupScale);

        System.out.println("MaxRectangle" + MaxRectangle.toString());
       

    }
    
   
}

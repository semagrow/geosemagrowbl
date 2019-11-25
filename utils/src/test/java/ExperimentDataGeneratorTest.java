import junit.framework.TestCase;
import org.junit.Test;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExperimentDataGeneratorTest extends TestCase {

    @Test
    public void testGenerateData() throws IOException {
        List<String> list = new ArrayList<>();
        list.add("blue");
        list.add("red");
        list.add("green");

        ExperimentDataGenerator generator = new ExperimentDataGenerator(
                10, 10, 100, list);

        FileWriter sqw = new FileWriter("/tmp/polygons.nt");
        generator.generateSquares(sqw);
        sqw.close();

        FileWriter ptw = new FileWriter("/tmp/points.nt");
        generator.generatePoints(ptw);
        ptw.close();

        FileWriter clw = new FileWriter("/tmp/colors.nt");
        generator.generateColors(clw);
        clw.close();
    }
}

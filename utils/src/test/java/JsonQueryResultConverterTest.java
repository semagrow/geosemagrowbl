import junit.framework.TestCase;

import org.junit.Test;

import java.io.*;

public class JsonQueryResultConverterTest extends TestCase {

    String query = "" +
            "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
            "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
            "PREFIX geof: <http://www.opengis.net/def/function/geosparql/>\n" +
            "PREFIX opengis: <http://www.opengis.net/def/uom/OGC/1.0/>\n" +
            "SELECT ?s1 ?s2 \n" +
            "WHERE {\n" +
            "        ?s1 rdf:type <http://iit.demokritos.gr/blue> . \n" +
            "        ?s2 rdf:type <http://iit.demokritos.gr/red> . \n" +
            "        ?s1 <http://www.opengis.net/ont/geosparql#asWKT> ?pt1 .\n" +
            "        ?s2 <http://www.opengis.net/ont/geosparql#asWKT> ?pt2 .\n" +
            "    FILTER (geof:distance(?pt1, ?pt2, opengis:metre) < \"100000.0\"^^<http://www.w3.org/2001/XMLSchema#double>)\n" +
            "}";

    String json = "{\n" +
            "  \"head\":{\"vars\":[\"pt1\",\"s1\",\"s2\",\"pt2\"]},\n" +
            "  \"results\":{\n" +
            "    \"bindings\":[\n" +
            "      {\n" +
            "        \"pt1\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (-2.228282100000008 53.429741200000045)\"},\n" +
            "        \"s1\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21600796/\"},\n" +
            "        \"s2\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/15034614/\"},\n" +
            "        \"pt2\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (-2.1902948999999827 53.05621939999934)\"}\n" +
            "      },{\n" +
            "        \"pt1\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (0.1367897000000298 52.218387600000014)\"},\n" +
            "        \"s1\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21137164/\"},\n" +
            "        \"s2\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21260358/\"},\n" +
            "        \"pt2\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (0.1674762999999896 52.24374899999999)\"}\n" +
            "      },{\n" +
            "        \"pt1\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (-1.23073120000006 51.71776580000016)\"},\n" +
            "        \"s1\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21545925/\"},\n" +
            "        \"s2\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21307199/\"},\n" +
            "        \"pt2\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (-1.9767279000000024 51.94936360000004)\"}\n" +
            "      },{\n" +
            "        \"pt1\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (-2.2449909000000403 51.86095770000028)\"},\n" +
            "        \"s1\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21586983/\"},\n" +
            "        \"s2\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21307199/\"},\n" +
            "        \"pt2\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (-1.9767279000000024 51.94936360000004)\"}\n" +
            "      },{\n" +
            "        \"pt1\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (1.193066100000027 52.050867800000056)\"},\n" +
            "        \"s1\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21770266/\"},\n" +
            "        \"s2\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21260358/\"},\n" +
            "        \"pt2\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (0.1674762999999896 52.24374899999999)\"}\n" +
            "      },{\n" +
            "        \"pt1\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (0.1367897000000298 52.218387600000014)\"},\n" +
            "        \"s1\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21137164/\"},\n" +
            "        \"s2\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/20458831/\"},\n" +
            "        \"pt2\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (0.1529170999999697 52.198022700000124)\"}\n" +
            "      },{\n" +
            "        \"pt1\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (1.193066100000027 52.050867800000056)\"},\n" +
            "        \"s1\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/21770266/\"},\n" +
            "        \"s2\":{\"type\":\"uri\",\"value\":\"http://openstreetmap.org/20458831/\"},\n" +
            "        \"pt2\":{\"datatype\":\"http://www.opengis.net/ont/geosparql#wktLiteral\",\"type\":\"literal\",\"value\":\"POINT (0.1529170999999697 52.198022700000124)\"}\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}";

    String result = "" +
            "<http://openstreetmap.org/21600796/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/blue> .\n" +
            "<http://openstreetmap.org/15034614/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/red> .\n" +
            "<http://openstreetmap.org/21600796/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (-2.228282100000008 53.429741200000045)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/15034614/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (-2.1902948999999827 53.05621939999934)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/21137164/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/blue> .\n" +
            "<http://openstreetmap.org/21260358/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/red> .\n" +
            "<http://openstreetmap.org/21137164/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (0.1367897000000298 52.218387600000014)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/21260358/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (0.1674762999999896 52.24374899999999)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/21545925/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/blue> .\n" +
            "<http://openstreetmap.org/21307199/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/red> .\n" +
            "<http://openstreetmap.org/21545925/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (-1.23073120000006 51.71776580000016)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/21307199/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (-1.9767279000000024 51.94936360000004)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/21586983/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/blue> .\n" +
            "<http://openstreetmap.org/21307199/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/red> .\n" +
            "<http://openstreetmap.org/21586983/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (-2.2449909000000403 51.86095770000028)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/21307199/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (-1.9767279000000024 51.94936360000004)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/21770266/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/blue> .\n" +
            "<http://openstreetmap.org/21260358/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/red> .\n" +
            "<http://openstreetmap.org/21770266/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (1.193066100000027 52.050867800000056)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/21260358/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (0.1674762999999896 52.24374899999999)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/21137164/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/blue> .\n" +
            "<http://openstreetmap.org/20458831/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/red> .\n" +
            "<http://openstreetmap.org/21137164/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (0.1367897000000298 52.218387600000014)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/20458831/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (0.1529170999999697 52.198022700000124)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/21770266/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/blue> .\n" +
            "<http://openstreetmap.org/20458831/> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://iit.demokritos.gr/red> .\n" +
            "<http://openstreetmap.org/21770266/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (1.193066100000027 52.050867800000056)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n" +
            "<http://openstreetmap.org/20458831/> <http://www.opengis.net/ont/geosparql#asWKT> \"POINT (0.1529170999999697 52.198022700000124)\"^^<http://www.opengis.net/ont/geosparql#wktLiteral> .\n";
    @Test
    public void testQuery() throws IOException {
        JsonQueryResultConverter converter = new JsonQueryResultConverter();
        StringReader iw = new StringReader(json);
        StringWriter ow = new StringWriter();
        converter.convertJsonToRdf(query, iw, ow);
        ow.close();
        iw.close();
        String output = ow.toString();
        assertEquals(output, result);
    }
}

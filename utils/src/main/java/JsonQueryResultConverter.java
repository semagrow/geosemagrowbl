import org.apache.commons.cli.*;
import org.eclipse.rdf4j.model.*;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.query.algebra.StatementPattern;
import org.eclipse.rdf4j.query.algebra.Var;
import org.eclipse.rdf4j.query.algebra.helpers.StatementPatternCollector;
import org.eclipse.rdf4j.query.parser.ParsedQuery;
import org.eclipse.rdf4j.query.parser.sparql.SPARQLParser;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonQueryResultConverter {

    private final ValueFactory vf = SimpleValueFactory.getInstance();
    private final Logger logger = LoggerFactory.getLogger(JsonQueryResultConverter.class);

    public void convertJsonToRdf(String queryString, Reader is, Writer os) {

        JSONTokener tokener = new JSONTokener(is);
        JSONObject object = new JSONObject(tokener);
        JSONArray bindings = object.getJSONObject("results").getJSONArray("bindings");

        SPARQLParser parser = new SPARQLParser();
        ParsedQuery query = parser.parseQuery(queryString, null);
        List<StatementPattern> statementPatterns = StatementPatternCollector.process(query.getTupleExpr());

        RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, os);
        writer.startRDF();

        for (int i = 0; i < bindings.length(); i++) {

            Map<String, Value> valueMap = new HashMap<>();
            JSONObject binding = (JSONObject) bindings.get(i);

            logger.info("Handling bindingset " + binding);

            for (String var: binding.keySet()) {
                valueMap.put(var, JSONobjectToValue(binding.getJSONObject(var)));
            }

            for (StatementPattern pattern : statementPatterns) {

                Statement statement = vf.createStatement(
                        (Resource) substitute(valueMap, pattern.getSubjectVar()),
                        (IRI) substitute(valueMap, pattern.getPredicateVar()),
                        substitute(valueMap, pattern.getObjectVar())
                );
                writer.handleStatement(statement);

                logger.info("Writing statement " + statement);
            }
        }

        writer.endRDF();
    }

    private Value JSONobjectToValue(JSONObject object) {

        switch (object.getString("type")) {
            case "literal":
                try {
                    IRI datatype = vf.createIRI(object.getString("datatype"));
                    return vf.createLiteral(object.getString("value"), datatype);
                } catch (NullPointerException e) {
                    return vf.createLiteral(object.getString("value"));
                }
            case "uri":
                return vf.createURI(object.getString("value"));
            case "bnode":
                return vf.createBNode(object.getString("value"));
        }
        return null;
    }

    private Value substitute(Map<String, Value> bindings, Var var) {

        if (bindings.containsKey(var.getName())) {
            return bindings.get(var.getName());
        }
        else {
            return var.getValue();
        }
    }

    public static void main(String[] args) throws ParseException, IOException {

        Option help = new Option("help", "print this message");
        Option query = OptionBuilder.withArgName("path")
                .hasArg()
                .withDescription("File that contains SPARQL query that results are the json file" )
                .create("query");
        Option jsonPath = OptionBuilder.withArgName("path")
                .hasArg()
                .withDescription("Path of the (input) json file" )
                .create("input");
        Option rdfPath = OptionBuilder.withArgName("path")
                .hasArg()
                .withDescription("Path of the (output) N-Triples file" )
                .create("output");

        Options options = new Options();
        options.addOption(help);
        options.addOption(query);
        options.addOption(jsonPath);
        options.addOption(rdfPath);

        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        if (line.hasOption(query.getOpt()) && line.hasOption(jsonPath.getOpt()) && line.hasOption(rdfPath.getOpt())) {

            JsonQueryResultConverter converter = new JsonQueryResultConverter();
            FileReader iw = new FileReader(line.getOptionValue(jsonPath.getOpt()));
            FileWriter ow = new FileWriter(line.getOptionValue(rdfPath.getOpt()));
            String q = new String(Files.readAllBytes(Paths.get(line.getOptionValue(query.getOpt()))), "UTF-8");
            converter.convertJsonToRdf(q, iw, ow);
            ow.close();
            iw.close();
        }

        if (line.hasOption(help.getOpt())) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("JsonQueryResultConverter", options);
        }
    }

}

import org.apache.commons.cli.*;
import org.eclipse.rdf4j.model.IRI;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.ValueFactory;
import org.eclipse.rdf4j.model.impl.SimpleValueFactory;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFWriter;
import org.eclipse.rdf4j.rio.Rio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ExperimentDataGenerator {

    private final ValueFactory vf = SimpleValueFactory.getInstance();
    private final Logger logger = LoggerFactory.getLogger(ExperimentDataGenerator.class);

    private float squareDimension;
    private int squaresPerDimension;
    private int numberOfPoints;
    private List<String> listOfColors;

    private String base = "http://example.org";
    private IRI asWKT = vf.createIRI("http://www.opengis.net/ont/geosparql#asWKT");
    private IRI rdfType = vf.createIRI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    private IRI WKTdatatype = vf.createIRI("http://www.opengis.net/ont/geosparql#wktLiteral");

    public ExperimentDataGenerator(float squareDimension, int squaresPerDimension, int numberOfPoints, List<String> listOfColors) {
        this.squareDimension = squareDimension;
        this.squaresPerDimension = squaresPerDimension;
        this.numberOfPoints = numberOfPoints;
        this.listOfColors = listOfColors;
    }

    public void generateSquares(Writer w) {
        logger.info("Generating Squares Dataset");

        RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, w);
        writer.startRDF();

        int id = 1;
        for (int i=0; i<squaresPerDimension; i++) {
            for (int j=0; j<squaresPerDimension; j++) {

                Statement statement = vf.createStatement(
                        vf.createURI(base + "/sq/" + id),
                        asWKT,
                        vf.createLiteral(stSquare(i*squareDimension,j*squareDimension,squareDimension), WKTdatatype)
                );
                writer.handleStatement(statement);
                id++;

                logger.info("Writing statement " + statement);
            }
        }
        writer.endRDF();
    }

    public void generatePoints(Writer w) {
        logger.info("Generating Points Dataset");

        RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, w);
        writer.startRDF();
        Random r = new Random();

        for (int i=0; i<numberOfPoints; i++) {

            float x = r.nextFloat() * (squareDimension * squaresPerDimension);
            float y = r.nextFloat() * (squareDimension * squaresPerDimension);

            Statement statement = vf.createStatement(
                    vf.createURI(base + "/pt/" + i),
                    asWKT,
                    vf.createLiteral(stPoint(x,y), WKTdatatype)
            );
            writer.handleStatement(statement);

            logger.info("Writing statement " + statement);
        }
        writer.endRDF();
    }

    public void generateColors(Writer w) {
        logger.info("Generating Colors Dataset");

        RDFWriter writer = Rio.createWriter(RDFFormat.NTRIPLES, w);
        writer.startRDF();

        for (int i=0; i<numberOfPoints; i++) {
            String color = listOfColors.get(i % listOfColors.size());

            Statement statement1 = vf.createStatement(
                    vf.createURI(base + "/pt/" + i),
                    vf.createURI(base + "/color"),
                    vf.createURI(base + "/" + color)
            );
            writer.handleStatement(statement1);

            Statement statement2 = vf.createStatement(
                    vf.createURI(base + "/pt/" + i),
                    rdfType,
                    vf.createURI(base + "/point")
            );
            writer.handleStatement(statement2);

            logger.info("Writing statement " + statement2);
        }

        for (int i=0; i<squaresPerDimension*squaresPerDimension; i++) {
            String color = listOfColors.get(i % listOfColors.size());

            Statement statement1 = vf.createStatement(
                    vf.createURI(base + "/sq/" + i),
                    vf.createURI(base + "/color"),
                    vf.createURI(base + "/" + color)
            );
            writer.handleStatement(statement1);

            logger.info("Writing statement " + statement1);

            Statement statement2 = vf.createStatement(
                    vf.createURI(base + "/sq/" + i),
                    rdfType,
                    vf.createURI(base + "/square")
            );
            writer.handleStatement(statement2);

            logger.info("Writing statement " + statement2);
        }

        writer.endRDF();
    }

    private String stSquare(float x, float y, float d) {
        return "POLYGON(("
                + stCoordinates(x,y)
                + ", "
                + stCoordinates(x+d,y)
                + ", "
                + stCoordinates(x+d,y+d)
                + ", "
                + stCoordinates(x,y+d)
                + ", "
                + stCoordinates(x,y)
                + "))";
    }

    private String stPoint(float x, float y) {
        return "POINT(" + stCoordinates(x,y) + ")";
    }

    private String stCoordinates(float x, float y) {
        return x + " " + y;
    }

    public static void main(String[] args) throws ParseException, IOException {

        Option help = new Option("help", "print this message");
        Option clpath = OptionBuilder.withArgName("path")
                .hasArg()
                .withDescription("Path to write the Colors Dataset" )
                .create("clpath");
        Option ptpath = OptionBuilder.withArgName("path")
                .hasArg()
                .withDescription("Path to write the Points Dataset" )
                .create("ptpath");
        Option sqpath = OptionBuilder.withArgName("path")
                .hasArg()
                .withDescription("Path to write the Squares Dataset" )
                .create("sqpath");
        Option npoints = OptionBuilder.withArgName("int")
                .hasArg()
                .withDescription("Number of points" )
                .create("npoints");
        Option nsquares = OptionBuilder.withArgName("int")
                .hasArg()
                .withDescription("Number of squares" )
                .create("nsquares");
        Option squaresz = OptionBuilder.withArgName("float")
                .hasArg()
                .withDescription("Size of each square" )
                .create("squaresz");
        Option colors = OptionBuilder.withArgName("col1,col2,...,coln")
                .hasArg()
                .withDescription("Comma-separated list of colors" )
                .create("colors");

        Options options = new Options();
        options.addOption(help);
        options.addOption(clpath);
        options.addOption(ptpath);
        options.addOption(sqpath);
        options.addOption(npoints);
        options.addOption(nsquares);
        options.addOption(squaresz);
        options.addOption(colors);

        CommandLineParser parser = new DefaultParser();
        CommandLine line = parser.parse(options, args);

        if (line.hasOption(clpath.getOpt()) &&
                line.hasOption(ptpath.getOpt()) &&
                line.hasOption(sqpath.getOpt()) &&
                line.hasOption(npoints.getOpt()) &&
                line.hasOption(nsquares.getOpt()) &&
                line.hasOption(squaresz.getOpt()) &&
                line.hasOption(colors.getOpt())) {


            int n = Integer.parseInt(line.getOptionValue(npoints.getOpt()));
            int m = Integer.parseInt(line.getOptionValue(nsquares.getOpt()));
            float d = Float.parseFloat(line.getOptionValue(npoints.getOpt()));
            List<String> list = new ArrayList<>(Arrays.asList(line.getOptionValue(colors.getOpt()).split(",")));

            ExperimentDataGenerator generator = new ExperimentDataGenerator(d,m,n,list);

            FileWriter sqw = new FileWriter(line.getOptionValue(sqpath.getOpt()));
            generator.generateSquares(sqw);
            sqw.close();

            FileWriter ptw = new FileWriter(line.getOptionValue(ptpath.getOpt()));
            generator.generatePoints(ptw);
            ptw.close();

            FileWriter clw = new FileWriter(line.getOptionValue(clpath.getOpt()));
            generator.generateColors(clw);
            clw.close();
        }

        if (line.hasOption(help.getOpt())) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ExperimentDataGenerator", options);
        }
    }
}

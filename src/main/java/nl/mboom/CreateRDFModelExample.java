package nl.mboom;

import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * @author Maikel
 * @since 28-3-2016
 */
public class CreateRDFModelExample {
    public static void main(String[] args) throws FileNotFoundException {

        /**
         * The base uri for the Model we are going to create, think of it
         * like a Database Schema.
         */
        final String baseUri = "http://domain/myproject#";

        /**
         * This is a basic RDF model for storing RDF Triples
         */
        Model rdfModel = ModelFactory.createDefaultModel();
        rdfModel.setNsPrefix("myproject", baseUri);

        /**
         * Here we create the predicates, in Jena known as properties. These
         * are our 'edges' between our nodes.
         */
        Property hasOccupation = rdfModel.createProperty(baseUri.concat("hasOccupation"));
        Property hasAge = rdfModel.createProperty(baseUri.concat("hasAge"));

        /**
         * Here we create two resources to connect and a literal. An integer object is
         * used because Jena has automatic Java primitives and XSD primitives mapping.
         */
        Resource bob = rdfModel.createResource(baseUri.concat("Bob"));
        Resource occupation = rdfModel.createResource(baseUri.concat("SoftwareEngineer"));
        Literal age = rdfModel.createTypedLiteral(new Integer(33));

        /**
         * Here we connect the dots and add an occupation and an Age for Bob.
         */
        bob.addProperty(hasOccupation, occupation);
        bob.addLiteral(hasAge, age);

        /**
         * Write the Model to the File example-rdf.ttl. This will be a turtle file which is a way
         * of serializing RDF models.
         */
        RDFDataMgr.write(
                new FileOutputStream(new File("example-rdf.rdf")),
                rdfModel,
                Lang.RDFXML
        );
    }

}

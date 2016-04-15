package nl.mboom;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * @author mboom
 * @since 15-4-2016
 */
public class OntologyExample {
    public static void main(String[] args) {

        /*
        Before you create an ontology, you can provide a specification on how to treat
        the ontology. OWL_DL_MEM for example tells the OntModel to do NO reasoning.
         */
        OntModelSpec modelSpec = OntModelSpec.OWL_DL_MEM;

        //Instead of of creating a Model, I create an OntModel which gives me more utility functions for working with Semantic Data
        OntModel ontModel = ModelFactory.createOntologyModel(modelSpec);

        /*
        Here I create three classes which I store in an OntClass. Ontclass is a special kind of RDF resource defined
        in the OWL standard. The OntClass gives me utilities to perform on the ontClass and anything changed within the
        OntClass is automatically changed in the OntModel as will since ontModel.createClass will inject a dependency within
        the OntClass.
         */
        OntClass animal = ontModel.createClass("http://example#Animal");
        OntClass mammal = ontModel.createClass("http://example#Mammal");
        OntClass fish = ontModel.createClass("http://example#Fish");

        //Just like OO programming with an Ontology you can create an inheritance tree
        animal.addSubClass(mammal);
        animal.addSubClass(fish);

        /*
        Just like a model has properties, an OntModel has OntProperties. Here I create properties
        for the classes, Just like fields in a OO class.
         */
        OntProperty hasEyes = ontModel.createOntProperty("http://example#hasEyes");
        OntProperty hasLegs = ontModel.createOntProperty("http://example#hasLegs");
        OntProperty hasFins = ontModel.createOntProperty("http://example#hasFins");

        /*
        Of course, before we can bind our ontProperties, they need to point somewhere, so I have
        to create the body parts
        * */
        OntClass eye = ontModel.createClass("http://example#Eye");
        OntClass leg = ontModel.createClass("http://example#Leg");
        OntClass fin = ontModel.createClass("http://example#Fin");

        /*
        Now make our model complete!
         */
        animal.addProperty(hasEyes, eye);
        mammal.addProperty(hasLegs, leg);
        fish.addProperty(hasFins, fin);

        //Print the model to console
        ontModel
                .listStatements()
                .forEachRemaining(stm -> System.out.println(stm.toString()));

        /*
        The interesting bit now in OWL is we can create restrictions. We can limit how many eyes a Mamal can have
         */
        Restriction eyeRestriction = ontModel.createMaxCardinalityRestriction(
                null, //Make the restriction anonymous
                hasEyes, //The property on which the restriction applies
                2 //The maximum amount of eyes a mammal can have
        );

        //Now we want the restriction to apply only to mammals, so We need to add the restriction as a superclass to mammal
        mammal.addSuperClass(eyeRestriction);
        //Now A reasoner can protect your data anytime somebody else will try to add a third eye to a mammal!
    }
}

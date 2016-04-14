package nl.mboom;

import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Maikel
 * @since 5-4-2016
 */
public class ReadingRDFDataExample {

    /**
     * A model to store the RDF file into
     */
    private Model rdfModel;


    public ReadingRDFDataExample(){

        //Initializes the basic model
        rdfModel = ModelFactory.createDefaultModel();

        System.out.println("Reading sample RDF Dataset");

        //Reads the sample data into our newly created rdfModel.
        RDFDataMgr.read(
                rdfModel, //The RDF model to load data INTO
                new File("rdfdata.rdf").getAbsolutePath() //The absolute path to the sample file
        );

        System.out.println("Reading done...");
    }

    public List<QuerySolution> selectData(String selectSparql){
        List<QuerySolution> solutions = new ArrayList<>();

        try(QueryExecution exec = createQueryExecution(selectSparql)){ //QueryExecutions need to be closed!
            ResultSet rs = exec.execSelect(); //Kind of like JDBC, Jena uses a resultset for queries.

            //Transform the results to a list of QuerySolutions, where each query solution represents a result row
            while(rs.hasNext())
                solutions.add(rs.nextSolution());
        }

        return solutions;
    }

    public Model describeData(String describeQuery){
        Optional<Model> describeModel = Optional.empty();

        try(QueryExecution exec = createQueryExecution(describeQuery)){ //Don't forget to close Query Executions

            describeModel = Optional.ofNullable(
                    exec.execDescribe() //As you can see, the Query Execution Factory has multiple executions. One for each type of SPARQL query
            );

        }

        return describeModel.orElse(ModelFactory.createDefaultModel()); //Return just an empty model when nothing is found
    }

    public boolean askData(String askQuery){
        boolean isDataPresent = false;

        try(QueryExecution exec = createQueryExecution(askQuery)){ //Again, close the Query Execution
            isDataPresent = exec.execAsk(); //And again, a special execution for a special type of query
        }

        return isDataPresent;
    }

    private QueryExecution createQueryExecution(String sparql){
        Query q = QueryFactory.create(sparql); //Jena uses a query object for storing SPARQL queries
        return QueryExecutionFactory.create(
                q, //The query to execute
                rdfModel //The model to execute the query on
        );
    }

    public static void main(String[] args) {
        //This will also load the Sample dataset
        ReadingRDFDataExample readingRDFDataExample = new ReadingRDFDataExample();

        //First lets see whether there is any data by creating a kind of select all query.
        String askQuery = "" +
                "ASK " + //Tells the Query Execution we are going to perform an ASK sparql query
                "WHERE " + //Our WHERE clause were the pattern we want to find is described
                "{" +
                "   ?subject ?predicate ?object" + //We are searching for anything here
                "}" +
                "limit 1"; //Just interested in 1 results, with very large datasets and complex queries the above structure might take a while to execute.

        boolean isDataPresent = readingRDFDataExample.askData(askQuery);
        System.out.println("ASK Result: " + isDataPresent);

        //So there is data, now print the first 10 results!
        String selectQuery = "" +
                /*
                Tells the Query Execition this is a SELECT query, I want to select all variables from the where clause
                 */
                "SELECT ?subject ?predicate ?object " +
                "WHERE " + //Our WHERE clause were the pattern we want to find is described
                "{" +
                "   ?subject ?predicate ?object" + //Again we are searching for anything
                "}" +
                "limit 10"; //Just limit to 10 results, very large datasets can be slow

        List<QuerySolution> results = readingRDFDataExample.selectData(selectQuery);
        System.out.println("Amount of rows found: " + results.size());

        //Print the first 10 results
        results.forEach( querySolution -> {
            String format = "(%s) [%s] (%s)"; //A format to print to console

        //As you can see, the QuerySolution objects has some utilities to work with result rowsa
            if(querySolution.contains("subject") && //This is our ?subject variable
                    querySolution.contains("predicate") && //This is our ?predicate variable
                    querySolution.contains("object")){ //This is our ?object variable

                //QuerySolutions always return Jena RDF Nodes
                String subjectUri = querySolution.get("subject").toString();
                String predicateUri = querySolution.get("predicate").toString();
                String object = querySolution.get("object").toString(); //Note objects could also be literals

                System.out.println(String.format(format,
                        subjectUri,
                        predicateUri,
                        object
                        ));
            }

        });

        //One of the returning subjects was http://www.xml.com/pub/a/2004/09/01/hack-congress.html I want to know more about this
        //This is all for describing! Just DESCRIBE and the uri of a resource!
        //Note: URL's within sparql need to be between < >
        String describeQuery = "DESCRIBE <http://www.xml.com/pub/a/2004/09/01/hack-congress.html>";
        Model hackCongressModel = readingRDFDataExample.describeData(describeQuery);

        //Lets see whats inside the model, you can compare statements with triples
        System.out.println("Printing model;");
        hackCongressModel
                .listStatements()
                .forEachRemaining(s -> System.out.println(s.toString()));

        //Now the hackCongressModel tells me everything I could query bout the resource I asked!
    }
}

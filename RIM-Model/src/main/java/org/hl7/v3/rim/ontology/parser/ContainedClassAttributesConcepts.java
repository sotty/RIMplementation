package org.hl7.v3.rim.ontology.parser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;

public class ContainedClassAttributesConcepts extends RIMOntologyBuilder {

    private static final String CLASSNAME = "ClassName";
    private static final String ATTRIBNAME = "AttributeName";
    private static final String DOMAIN = "Vocabulary";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        // actually do nothing here
    }


    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( ATTRIBNAME, CLASSNAME, DOMAIN ) );
        return parser;
    }
}

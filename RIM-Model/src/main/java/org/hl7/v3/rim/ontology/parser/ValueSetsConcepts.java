package org.hl7.v3.rim.ontology.parser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class ValueSetsConcepts extends RIMOntologyBuilder {

    private static final String NAME = "Name";
    private static final String ID = "Id";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String name = record.get( NAME );
        String id = record.get( ID );

        OWLNamedIndividual ind = createValueset( id, name, ontology, f );
        addAnnotation( ind, INTERNAL_ID, id, ontology, f );

        context.addValueSet( id, ind );
    }

    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( NAME, ID ) );
        return parser;
    }

}

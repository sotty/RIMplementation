package org.hl7.v3.rim.ontology.parser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;

public class ValueSetsProperties extends RIMOntologyBuilder {

    private static final String VAL = "Value";
    private static final String ID = "Id";
    private static final String PROP = "PropertyName";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String value = record.get( VAL );
        String id = record.get( ID );
        String prop = record.get( PROP );

        if ( "Description".equals( prop ) ) {
            addDescription( context.getValueSets().get( id ).getIRI(), value, ontology, f  );
        } else if ( "IsImmutable".equals( prop ) ) {
            // nothing to do here for the moment
        } else {
            throw new IllegalStateException( "Unrecognized valueset property " + PROP );
        }

    }


    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( ID, PROP, VAL ) );
        return parser;
    }
}

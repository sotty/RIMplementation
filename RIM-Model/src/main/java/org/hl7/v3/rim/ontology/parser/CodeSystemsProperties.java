package org.hl7.v3.rim.ontology.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;

import static org.hl7.v3.rim.ontology.RIMNames.getConceptSchemeIRI;

public class CodeSystemsProperties extends RIMOntologyBuilder {

    private static final String ID = "Id";
    private static final String PROP = "PropertyName";
    private static final String VALUE = "Value";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String id = record.get( ID );
        String prop = record.get( PROP );
        String val = record.get( VALUE );

        IRI cs = getConceptSchemeIRI( id );

        if ( "Title".equals( prop ) ) {
            addLabel( f.getOWLNamedIndividual( cs ), val, ontology, f );
        } else if ( "ReleaseDate".equals( prop ) ) {
            // IGNORED
        } else if ( "PublisherVersionId".equals( prop ) ) {
            // IGNORED
        } else {
            throw new IllegalStateException( "Unknown property for codeSystem " + prop );
        }

    }

    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( ID, PROP, VALUE ) );
        return parser;
    }
}

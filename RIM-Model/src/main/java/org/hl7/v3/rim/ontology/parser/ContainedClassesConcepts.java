package org.hl7.v3.rim.ontology.parser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class ContainedClassesConcepts extends RIMOntologyBuilder {

    private static final String NAME = "Name";
    private static final String TYPE = "Type";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String name = record.get( NAME );
        String type = record.get( TYPE );

        OWLClass klass = f.getOWLClass( qnameToIRI( RIM, name ) );

        if ( ! isWellKnownClass( name ) ) {
            addAxiom( ontology,
                      f.getOWLDeclarationAxiom( klass ) );
            if ( isAbstractClass( type ) ) {
                markAsAbstract( klass.getIRI(), ontology, f );
            }
        }
    }

    private boolean isAbstractClass( String type ) {
        if ( "Abstract".equals( type ) ) {
            return true;
        } else if ( "Class".equals( type ) ) {
            return false;
        } else {
            throw new IllegalStateException( "Unknown type " + type );
        }
    }


    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( NAME, TYPE ) );
        return parser;
    }
}

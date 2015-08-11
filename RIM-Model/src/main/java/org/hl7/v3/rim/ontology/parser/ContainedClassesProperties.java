package org.hl7.v3.rim.ontology.parser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class ContainedClassesProperties extends RIMOntologyBuilder {

    private static final String SRC = "Name";
    private static final String REL = "PropertyName";
    private static final String TGT = "PropertyValue";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String src = record.get( SRC );
        String prop = record.get( REL );
        String tgt = record.get( TGT );

        OWLClass s = f.getOWLClass( IRI.create( isWellKnownClass( src ) ? UPP : RIM, src ) );

        if ( "Description".equals( prop ) ) {
            addDescription( s.getIRI(), tgt, ontology, f );
        } else {
            throw new UnsupportedOperationException( "Unknown relationship " + prop );
        }

    }


    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( SRC, REL, TGT ) );
        return parser;
    }
}

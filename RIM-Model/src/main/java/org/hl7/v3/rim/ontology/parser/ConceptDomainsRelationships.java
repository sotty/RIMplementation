package org.hl7.v3.rim.ontology.parser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;

public class ConceptDomainsRelationships extends RIMOntologyBuilder {

    private static final String PARENT = "ParentNode";
    private static final String CHILD = "ChildNode";
    private static final String REL = "Relationship";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String parent = record.get( PARENT );
        String child = record.get( CHILD );
        String rel = record.get( REL );

        if ( "specializesDomain".equals( rel ) ) {
            String parentOid = context.getOIDForCodeSystemDomain( parent );
            String childOid = context.getOIDForCodeSystemDomain( child );

            if ( parentOid == null ) {
                System.err.println( this.getClass().getName() + " WARNING : Unknown concept domain or no OID specified : " + parent );
            }
            if ( childOid == null ) {
                System.err.println( this.getClass().getName() + " WARNING : Unknown concept domain or no OID specified : " + child );
            }

        } else {
            throw new IllegalStateException( "Unrecognized relationship between concept domains " + rel );
        }

        // Not much to do here...
    }

    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( PARENT, CHILD, REL ) );
        return parser;
    }
}

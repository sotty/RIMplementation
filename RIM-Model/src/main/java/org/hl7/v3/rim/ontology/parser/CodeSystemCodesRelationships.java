package org.hl7.v3.rim.ontology.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hl7.v3.rim.ontology.RIMNames;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;
import static org.hl7.v3.rim.ontology.RIMNames.*;

public class CodeSystemCodesRelationships extends RIMOntologyBuilder {

    private static final String SRC = "Source";
    private static final String REL = "Relationship";
    private static final String TGT = "Target";
    private static final String CSID = "CodeSystemId";
    private static final String TSID = "TargetCodeSystemId";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String rel = record.get( REL );
        String tgt = record.get( TGT );
        String csid = record.get( CSID );
        String tsid = record.get( TSID );
        if ( tsid == null || tsid.isEmpty() ) {
            tsid = csid;
        }

        for ( String src : splitCodes( record.get( SRC ) ) ) {
            IRI sid = getConceptIRI( src, csid );
            IRI tid = getConceptIRI( tgt, tsid );

            if ( !context.getConcepts().containsKey( sid.toString() ) ) {
                //throw new IllegalStateException( "Concept not found " + sid + ", unable to assert relationship " + rel );
                System.err.println( "Concept not found " + sid + ", unable to assert SUB " + rel );
            }
            if ( !context.getConcepts().containsKey( tid.toString() ) ) {
                //throw new IllegalStateException( "Concept not found " + tid + ", unable to assert relationship " + rel );
                System.err.println( "Concept not found " + tid + ", unable to assert SUP " + rel );
            }

            OWLNamedIndividual con;
            if ( "Specializes".equals( rel ) ) {
                addSubConceptOf( f.getOWLNamedIndividual( sid ),
                                 f.getOWLNamedIndividual( tid ),
                                 ontology,
                                 f );
                context.addParent( tid.toString(), sid.toString() );
            } else if ( "ComponentOf".equals( rel ) ) {
                addRelationship( COMPONENT_OF,
                                 f.getOWLNamedIndividual( sid ),
                                 f.getOWLNamedIndividual( tid ),
                                 ontology,
                                 f );
            } else if ( "MayBeQualifiedBy".equals( rel ) ) {
                // Not supported yet
            } else if ( "OwningSubSection".equals( rel ) ) {
                // Not supported yet
            } else if ( "OwningSection".equals( rel ) ) {
                // Not supported yet
            } else if ( "OwningAffiliate".equals( rel ) ) {
                // Not supported yet
            } else if ( "SmallerThan".equals( rel ) ) {
                // Not supported yet
            } else {
                throw new UnsupportedOperationException( "Unrecognized Concept Code Relationship " + rel );
            }

        }
    }




    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( SRC, REL, TGT, CSID, TSID ) );
        return parser;
    }
}

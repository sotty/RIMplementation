package org.hl7.v3.rim.ontology.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class CodeSystemCodesConcepts extends RIMOntologyBuilder {

    private static final String COD = "Code";
    private static final String NAME = "Name";
    private static final String KLASS = "Class";
    private static final String CSID = "CodeSystemId";

    @Override
    public void fixUp( RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        // apparently, maybe because "CRTs" are deprecated, the concept of "Criterion" is not available
        String cod = "CRT";
        String csd = "2.16.840.1.113883.5.1001";
        IRI cid = getConceptIRI( cod, csd );

        OWLNamedIndividual crt = createConcept( cid, "Criterion", cod, createConceptScheme( csd, ontology, f ), ontology, f  );
        context.addConcept( cid.toString(), crt );
    }

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String code = record.get( COD );
        String name = record.get( NAME );
        String klass = record.get( KLASS );
        String csid = record.get( CSID );

        if ( code.contains( " " ) ) {
            for ( String sub : splitCodes( code ) ) {
                makeConcept( sub, csid, klass, name, ontology, f, context );
            }
        } else {
            makeConcept( code, csid, klass, name, ontology, f, context );
        }
    }

    private void makeConcept( String cd, String csid, String klass, String name, OWLOntology ontology, OWLDataFactory f, RIMContext context ) {
        IRI cid = getConceptIRI( cd, csid );

        OWLNamedIndividual con;
        if ( "Concept".equals( klass ) ) {
            con = createConcept( cid, name, cd, createConceptScheme( csid, ontology, f ), ontology, f );
        } else if ( "NonSelectableConcept".equals( klass ) ) {
            con = createConcept( cid, name, cd, createConceptScheme( csid, ontology, f ), ontology, f );
            markAsAbstract( con.getIRI(), ontology, f );
            context.addAbstract( con.getIRI() );
        } else {
            throw new UnsupportedOperationException( "Unrecognized Concept Type " + klass );
        }

        context.addConcept( cid.toString(), con );
    }


    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( NAME, COD, KLASS, CSID ) );
        return parser;
    }
}

package org.hl7.v3.rim.ontology.parser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class ContainedClassesRelationships extends RIMOntologyBuilder {

    private static final String SRC = "Source";
    private static final String REL = "Relationship";
    private static final String TGT = "Target";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String src = record.get( SRC );
        String prop = record.get( REL );
        String tgt = record.get( TGT );

        OWLClass s = f.getOWLClass( qnameToIRI( RIM, src ) );
        OWLClass t = f.getOWLClass( getActualParent( tgt ) );

        if ( "ParentClass".equals( prop ) ) {
            if ( isRoot( tgt ) ) {
                if ( ! isWellKnownClass( src ) ) {
                    subClassOf( s, t, context, ontology, f );
                }
            } else {
                subClassOf( s, t, context, ontology, f );
            }
        } else {
            throw new UnsupportedOperationException( "Unknown relationship " + prop );
        }

    }

    private void subClassOf( OWLClass s, OWLClass t, RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        addAxiom( ontology, f.getOWLSubClassOfAxiom( s, t ) );
        context.addAncestor( s, t );
        addSubConceptOf( s, t, context, ontology, f );
    }

    private void addSubConceptOf( OWLClass s, OWLClass t, RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        OWLNamedIndividual sub = context.getClassConcepts().get( getConceptIdForClass( s.getIRI(), context ) );
        OWLNamedIndividual sup = context.getClassConcepts().get( getConceptIdForClass( t.getIRI(), context ) );
        if ( sup != null && sub != null ) {
            addSubConceptOf( sub, sup, ontology, f );
        }
    }

    private String getConceptIdForClass( IRI iri, RIMContext context ) {
        return context.getClassToConceptMap().get( iri );
    }


    private IRI getActualParent( String tgt ) {
        if ( ! isRoot( tgt ) ) {
            return qnameToIRI( RIM, tgt );
        } else {
            return qnameToIRI( UPP, tgt );
        }
    }


    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( SRC, REL, TGT ) );
        return parser;
    }
}

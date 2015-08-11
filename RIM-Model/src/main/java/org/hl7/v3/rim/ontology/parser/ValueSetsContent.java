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

import static org.hl7.v3.rim.ontology.RIMNames.CONCEPT;
import static org.hl7.v3.rim.ontology.RIMNames.DRAWSFROM;
import static org.hl7.v3.rim.ontology.RIMNames.MEMBER;
import static org.hl7.v3.rim.ontology.RIMNames.SPECIALIZES;
import static org.hl7.v3.rim.ontology.RIMNames.getConceptIRI;
import static org.hl7.v3.rim.ontology.RIMNames.getValuesetIRI;

public class ValueSetsContent extends RIMOntologyBuilder {

    private static final String ID = "ValueSetId";
    private static final String NAME = "Name";
    private static final String MEMBR = "Member";
    private static final String TYPE = "Type";
    private static final String MEMBER_NS = "MemberNS";
    private static final String TRANSITIVE = "Transitive";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String vsId = record.get( ID );
        String vsName = record.get( NAME );
        String member = record.get( MEMBR );
        String ns = record.get( MEMBER_NS );
        String contentType = record.get( TYPE );
        String transitive = record.get( TRANSITIVE );

        IRI vsIRI = getValuesetIRI( vsName, vsId );
        if ( "Code".equals( contentType ) ) {
            IRI memberCdIRI = getConceptIRI( member, ns );
            addType( memberCdIRI, CONCEPT, ontology, f );

            if ( "TransitiveClosure".equals( transitive ) ) {
                addRelationship( MEMBER, f.getOWLNamedIndividual( vsIRI ), f.getOWLNamedIndividual( memberCdIRI ), ontology, f );
            } else if ( "DirectRelationsOnly".equals( transitive ) ) {
                addRelationship( MEMBER, f.getOWLNamedIndividual( vsIRI ), f.getOWLNamedIndividual( memberCdIRI ), ontology, f );
            } else if ( "NONE".equals( transitive ) ) {
                addRelationship( MEMBER, f.getOWLNamedIndividual( vsIRI ), f.getOWLNamedIndividual( memberCdIRI ), ontology, f );
            } else {
                throw new IllegalStateException( "Unexpected valueset content type " + transitive );
            }
        } else if ( "ValueSet".equals( contentType ) ) {
            IRI memberVsIRI = getValuesetIRI( member, ns );
            addRelationship( MEMBER, f.getOWLNamedIndividual( vsIRI ), f.getOWLNamedIndividual( memberVsIRI ), ontology, f );
        } else {
            throw new IllegalStateException( "Unexpected valueset content type " + contentType );
        }
    }

    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( ID, NAME, MEMBR, TYPE, MEMBER_NS, TRANSITIVE ) );
        return parser;
    }

}

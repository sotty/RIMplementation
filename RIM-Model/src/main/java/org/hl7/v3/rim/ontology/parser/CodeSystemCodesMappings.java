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

import static org.hl7.v3.rim.ontology.RIMNames.SPECIALIZES;
import static org.hl7.v3.rim.ontology.RIMNames.getConceptIRI;

public class CodeSystemCodesMappings extends RIMOntologyBuilder {

    private static final String SRC = "Source";
    private static final String MAP = "Mapping";
    private static final String TGT = "Target";
    private static final String SRC_CS = "CodeSystemId";
    private static final String TGT_CS = "TargetCodeSystem";

    @Override
    public void fixUp( RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
    }

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String src = record.get( SRC );
        String src_cs = record.get( SRC_CS );
        String tgt = record.get( TGT );
        String tgt_cs = record.get( TGT_CS );
        String map = record.get( MAP );

        if ( "ClassifiesClassCode".equals( map ) ) {
            addRelationship( SPECIALIZES, getConceptIRI( src, src_cs ), getConceptIRI( tgt, tgt_cs ), ontology, f );
            context.addParent( getConceptIRI( tgt, tgt_cs ).toString(), getConceptIRI( src, src_cs ).toString() );
        } else {
            throw new IllegalStateException( "Unrecognized mapping " + map );
        }
    }



    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( SRC, MAP, TGT, TGT_CS, SRC_CS ) );
        return parser;
    }
}

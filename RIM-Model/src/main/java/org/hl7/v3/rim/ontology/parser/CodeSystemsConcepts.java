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
import java.util.HashMap;
import java.util.Map;

import static org.hl7.v3.rim.ontology.RIMNames.INTERNAL_ID;
import static org.hl7.v3.rim.ontology.RIMNames.NOTATION;
import static org.hl7.v3.rim.ontology.RIMNames.RIM;
import static org.hl7.v3.rim.ontology.RIMNames.VOC;
import static org.hl7.v3.rim.ontology.RIMNames.formatClassName;
import static org.hl7.v3.rim.ontology.RIMNames.getConceptIRI;
import static org.hl7.v3.rim.ontology.RIMNames.getConceptSchemeIRI;

public class CodeSystemsConcepts extends RIMOntologyBuilder {

    private static final String ID = "Id";
    private static final String NAME = "Name";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String id = record.get( ID );
        String name = record.get( NAME );

        IRI cs = getConceptSchemeIRI( id );

        context.addCodeSystem( name, id );
    }

    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( NAME, ID ) );
        return parser;
    }
}

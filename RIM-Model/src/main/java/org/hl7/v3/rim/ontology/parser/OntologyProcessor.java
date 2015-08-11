package org.hl7.v3.rim.ontology.parser;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public interface OntologyProcessor {

    public OWLOntology process( List<CSVRecord> recordList, RIMContext context );

    public CSVParser getParser( BufferedReader rdr ) throws IOException;

    public boolean isStateful();

    public void fixUp( RIMContext context, OWLOntology ontology, OWLDataFactory owlDataFactory );

    public void wrapUp( RIMContext context, OWLOntology ontology, OWLDataFactory f );
}

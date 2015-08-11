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

import static org.hl7.v3.rim.ontology.RIMNames.DRAWSFROM;
import static org.hl7.v3.rim.ontology.RIMNames.INTERNAL_ID;
import static org.hl7.v3.rim.ontology.RIMNames.getValuesetIRI;

public class ValueSetsCodeSystems extends RIMOntologyBuilder {

    private static final String ID = "ValueSetId";
    private static final String CD = "Name";
    private static final String CS = "CodeSystem";

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String id = record.get( ID );
        String cd = record.get( CD );
        String cs = record.get( CS );

        OWLNamedIndividual vs = f.getOWLNamedIndividual( getValuesetIRI( cd, id ) );
        OWLNamedIndividual ind = createConceptScheme( cs, ontology, f );
        addRelationship( DRAWSFROM, vs, ind, ontology, f );
    }

    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( ID, CD, CS ) );
        return parser;
    }

}

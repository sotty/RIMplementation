package org.hl7.v3.rim.ontology;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.FileNotFoundException;

public class RIMOntology {
    private AxiomTester tester;
    private OWLOntology rim;

    public RIMOntology() throws OWLOntologyCreationException, FileNotFoundException {
        rim = RIMOntologyAssembler.loadOntology();
        tester = new AxiomTester( rim );
    }

    public AxiomTester getTester() {
        return tester;
    }

    public OWLOntology getOntology() {
        return rim;
    }
}

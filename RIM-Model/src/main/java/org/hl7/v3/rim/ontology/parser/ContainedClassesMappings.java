package org.hl7.v3.rim.ontology.parser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hl7.v3.rim.ontology.RIMNames;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class ContainedClassesMappings extends RIMOntologyBuilder {



    private static final String SRC = "Source";
    private static final String MAP = "Mapping";
    private static final String COD = "TargetCode";
    private static final String SCH = "TargetCodeSystem";


    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String src = record.get( SRC );
        String map = record.get( MAP );
        String kind = record.get( SCH );
        String cod = record.get( COD );


        String schId = context.getOIDForCodeSystem( kind );
        OWLNamedIndividual cs = createConceptScheme( schId, ontology, f );
        IRI cid =  getConceptIRI( cod, schId );

        OWLNamedIndividual con = createConcept( cid, cod, cod, cs, ontology, f );

        if ( "DefiningVocabulary".equals( map ) ) {


            OWLClass klass = classNameToOWLClass( src, f );

            createConceptMediatedDefinition( klass ,
                                             f.getOWLClass( getActualParent( src, kind ) ),
                                             f.getOWLObjectProperty( CLASS_CODE ),
                                             con,
                                             ontology,
                                             f );
            context.addClassConcept( cid.toString(), con );
            context.addClassCode( klass.getIRI(), cid.toString() );


        } else {
            throw new IllegalStateException( "Unknown Mapping " + map );
        }


    }


    private IRI getActualParent( String src, String tgt ) {
        if ( isWellKnownClass( src ) ) {
            return qnameToIRI( UPP, "InfrastructureRoot" );
        }
        tgt = tgt.replace( "Class", "" );
        return qnameToIRI( RIM, tgt );
    }


    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( SRC, MAP, COD, SCH ) );
        return parser;
    }
}

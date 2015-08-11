package org.hl7.v3.rim.ontology.parser;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class ContainedClassAttributesRelationships extends RIMOntologyBuilder {

    private static final String ATT = "AttributeName";
    private static final String REL = "Relationship";
    private static final String CLS = "ClassName";

    private Map<String,Set<String>> attributes = new HashMap<String,Set<String>>();

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String attribName = record.get( ATT );
        String propName = record.get( REL );
        String className = record.get( CLS );

        if ( "AttributeOf".equals( propName ) ) {
            addAttributeToClass( attribName, className );
        } else {
            throw new UnsupportedOperationException( "Unknown attribute property " + propName );
        }
    }

    private void addAttributeToClass( String attribName, String className ) {
        if ( ! attributes.containsKey( className ) ) {
            attributes.put( className, new HashSet<String>() );
        }
        attributes.get( className ).add( attribName );
    }

    @Override
    public void wrapUp( RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        cleanUpInheritedAttributes( context, ontology, f );
        for ( String kls : this.attributes.keySet() ) {
            if ( ! isWellKnownClass( kls ) ) {
                Set<String> localAttribs = this.attributes.get( kls );
                for ( String attrib : localAttribs ) {
                    context.addAttributeDomain( classNameToOWLClass( kls, f ).getIRI() , qnameToIRI( RIM, attrib ) );
                }
            }
        }
    }

    private void cleanUpInheritedAttributes( RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        for ( String klass : attributes.keySet() ) {
            Set<String> attribs = attributes.get( klass );
            String parent = getParent( klass, context, f );
            removeInherited( klass, parent, attribs, context, f );
        }
    }

    private String getParent( String klass, RIMContext context, OWLDataFactory f ) {
        IRI iri = classNameToOWLClass( klass, f ).getIRI();
        IRI parent = context.getAncestors().get( iri );
        return parent != null ? parent.getFragment() : null;
    }

    private void removeInherited( String klass, String parent, Set<String> attribs, RIMContext context, OWLDataFactory f ) {
        if ( parent != null && attributes.containsKey( parent ) ) {
            attribs.removeAll( attributes.get( parent ) );
        }
        IRI ancestor = context.getAncestors().get( classNameToOWLClass( parent, f ) );
        if ( ancestor != null ) {
            removeInherited( klass, ancestor.getFragment(), attribs, context, f );
        }
    }

    @Override
    public boolean isStateful() {
        return true;
    }

    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( ATT, CLS, REL ) );
        return parser;
    }
}

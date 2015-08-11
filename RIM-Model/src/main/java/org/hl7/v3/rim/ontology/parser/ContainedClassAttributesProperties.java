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
import java.util.Map;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class ContainedClassAttributesProperties extends RIMOntologyBuilder {

    private static final String ATTRIBNAME = "AttributeName";
    private static final String PROPNAME = "PropertyName";
    private static final String PROPVALUE = "PropertyValue";
    private static final String CLASSNAME = "ClassName";

    private Map<IRI,AttrDescr> attrs = new HashMap<IRI,AttrDescr>();

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String attribName = record.get( ATTRIBNAME );
        String propName = record.get( PROPNAME );
        String propVal = record.get( PROPVALUE );
        String className = record.get( CLASSNAME );

        if ( isWellKnownClass( className ) ) {
            return;
        }

        IRI attrib = qnameToIRI( RIM, attribName );
        IRI klass = qnameToIRI( RIM, className );

        if ( ! context.isInDomain( attrib, klass ) ) {
            return;
        }

        AttrDescr ad = getDescr( attrib, klass );

        if ( "Type".equals( propName ) ) {
            if ( ! isGeneric( propVal ) ) {
                ad.type.append( getBaseType( propVal ) );
            }
        } else if ( "Description".equals( propName ) ) {
            ad.descr = propVal;
        } else if ( "ArgumentDatatype".equals( propName ) ) {
            if ( ad.type.length() > 0 ) {
                ad.type.append( "_" );
            }
            ad.type.append( getBaseType( propVal ) );
        } else if ( "Vocabulary".equals( propName ) ) {
            if ( "CD".equals( ad.type.toString() )
                 || "CS".equals( ad.type.toString() )
                 || "SC".equals( ad.type.toString() )
                    ) {
                String valSet = context.getValueSetBoundToDomain( propVal );
                // some valuesets are still bound to domains, but have been deprecated themselves
                if ( valSet != null && context.getValueSets().containsKey( valSet ) ) {
                    IRI vsIRI = context.getValueSets().get( valSet ).getIRI();
                    ad.voc = vsIRI;
                } else {
                    // a few domains are unbound, like "job name"
                }
            } else {
                if ( ! "ANY".equals( ad.type.toString() ) ) {
                    throw new IllegalStateException( "Found vocabulary domain " + propVal + " for attribute " + attribName + " of unexpected type " + ad.type );
                }
            }
        } else if ( "Min".equals( propName ) ) {

        } else if ( "Max".equals( propName ) ) {
            if ( "*".equals( propVal ) ) {
                ad.card = null;
            } else {
                ad.card = Integer.parseInt( propVal );
            }
        }

    }

    private String getBaseType( String type ) {
        int idx = type.indexOf( "." );
        if ( idx < 0 ) {
            return type;
        }
        return type.substring( 0, idx );
    }

    private boolean isGeneric( String type ) {
        if ( "COLL".equals( type ) || type.contains( "SET" ) || "LIST".equals( type ) ) {
            return true;
        }
        return false;
    }

    private AttrDescr getDescr( IRI attrib, IRI klass ) {
        if ( ! attrs.containsKey( attrib ) ) {
            AttrDescr ad = new AttrDescr();
            ad.domain = klass;
            ad.property = attrib;
            attrs.put( attrib, ad );
            return ad;
        } else {
            AttrDescr ad = attrs.get( attrib );
            if ( ! ad.domain.equals( klass ) ) {
                throw new IllegalStateException( "Attrib with multiple domains" );
            }
            return ad;
        }
    }


    @Override
    public void wrapUp( RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        for ( AttrDescr ad : attrs.values() ) {
            addAttribute( ad.domain, ad.property, qnameToIRI( DAT, ad.type.toString() ), ad.card, ad.descr, ontology, f );

            if ( ad.voc != null ) {
                addVSMediatedAttributeRangeRestriction( ad.domain, ad.property, ad.voc, ontology, f );
            }
        }
    }


    public boolean isStateful() {
        return true;
    }

    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( ATTRIBNAME, PROPNAME, PROPVALUE, CLASSNAME ) );
        return parser;
    }


    private static class AttrDescr {
        public IRI domain;
        public IRI property;
        public StringBuilder type = new StringBuilder();
        public Integer card = null;
        public String descr;
        public IRI voc;

        @Override
        public String toString() {
            return "AttrDescr{" +
                   "domain=" + domain +
                   ", property=" + property +
                   ", type=" + type +
                   ", card=" + card +
                   ( voc != null ? ", voc=" + voc  : "" ) +
                   ", descr='" + descr + '\'' +
                   '}';
        }
    }
}

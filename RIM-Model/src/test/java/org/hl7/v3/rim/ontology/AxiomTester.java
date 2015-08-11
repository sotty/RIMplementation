package org.hl7.v3.rim.ontology;

import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectAllValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.OWLSubPropertyChainOfAxiom;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import uk.ac.manchester.cs.owl.owlapi.OWLDatatypeImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AxiomTester {

    private OWLOntology ontology;
    private OWLDataFactory odf;
    private PrefixManager pfm;

    public AxiomTester( OWLOntology ontology ) {
        this.ontology = ontology;
        this.odf = ontology.getOWLOntologyManager().getOWLDataFactory();

        this.pfm = new DefaultPrefixManager();
        RIMNames.fillPrefixes( this.pfm );
    }

    public AxiomTester checkRelationship( String subject, IRI predicate, String object ) {
        return checkRelationship( subject, predicate.toString(), object  );
    }

    public AxiomTester checkRelationship( String subject, String predicate, String object ) {

        subject = resolvePrefix( subject );
        object = resolvePrefix( object );
        predicate = resolvePrefix( predicate );

        OWLNamedIndividual sub = odf.getOWLNamedIndividual( IRI.create( subject ) );
        OWLNamedIndividual obj = odf.getOWLNamedIndividual( IRI.create( object ) );
        OWLObjectProperty prp = odf.getOWLObjectProperty( IRI.create( predicate ) );

        OWLObjectPropertyAssertionAxiom testX = odf.getOWLObjectPropertyAssertionAxiom( prp, sub, obj );
        for ( OWLOntology onto : ontology.getOWLOntologyManager().getOntologies() ) {
            Set<OWLObjectPropertyAssertionAxiom> info = onto.getObjectPropertyAssertionAxioms( sub );
            if ( info.contains( testX ) ) {
                return this;
            }
        }

        fail( "Expected relationship not found : " + subject + " " + predicate + " " + object );

        return this;
    }

    public AxiomTester checkClassExists( String klass ) {
        return checkClassExists( klass, true );
    }

    public AxiomTester checkClassExists( String klass, boolean pos ) {
        klass = resolvePrefix( klass );
        OWLClass cls = odf.getOWLClass( IRI.create( klass ) );

        boolean exists = ontology.getClassesInSignature( true ).contains( cls );
        if ( pos ) {
            assertTrue( exists );
        } else {
            assertFalse( exists );
        }

        return this;
    }

    public AxiomTester checkClassNotExists( String klass ) {
        return checkClassExists( klass, false );    }

    public AxiomTester checkType( String subject, IRI type ) {
        return checkType( subject, type.toString() );
    }

    public AxiomTester checkType( String subject, String type ) {

        subject = resolvePrefix( subject );
        type = resolvePrefix( type );

        OWLNamedIndividual sub = odf.getOWLNamedIndividual( IRI.create( subject ) );
        OWLClass cls = odf.getOWLClass( IRI.create( type ) );

        Set<OWLClassExpression> info = sub.getTypes( ontology.getImportsClosure() );
        //OWLClassAssertionAxiom testX = odf.getOWLClassAssertionAxiom( cls, sub );

        if( ! info.contains( cls ) ) {
            fail( "Expected type " + type + " not found for individual " + subject );
        }

        return this;
    }

    private String resolvePrefix( String qname ) {
        int idx = qname.indexOf( ":" );
        if ( idx < 0 ) {
            return qname;
        }
        String candidatePrefix = qname.substring( 0, idx + 1 );
        if ( pfm.containsPrefixMapping( candidatePrefix ) ) {
            String prefix = pfm.getPrefix( candidatePrefix );
            return prefix + qname.substring( idx + 1 );
        } else {
            return qname;
        }
    }


    public AxiomTester checkValue( String subject, IRI predicate, String val ) {
        return checkValue( subject, predicate.toString(), val );
    }

    public AxiomTester checkValue( String subject, String predicate, String val ) {
        return checkValue( subject, predicate, val, null );
    }

    public AxiomTester checkValue( String subject, String predicate, String val, String type ) {
        subject = resolvePrefix( subject );
        predicate = resolvePrefix( predicate );

        OWLNamedIndividual sub = odf.getOWLNamedIndividual( IRI.create( subject ) );
        OWLDataProperty prp = odf.getOWLDataProperty( IRI.create( predicate ) );

        OWLDataPropertyAssertionAxiom testX = odf.getOWLDataPropertyAssertionAxiom( prp, sub,
                                                                                    type == null ? odf.getOWLLiteral( val ) : odf.getOWLTypedLiteral( val, new OWLDatatypeImpl( IRI.create( type ) ) ) );

        for ( OWLOntology onto : ontology.getOWLOntologyManager().getOntologies() ) {
            Set<OWLDataPropertyAssertionAxiom> info = onto.getDataPropertyAssertionAxioms( sub );
            if ( info.contains( testX ) ) {
                return this;
            }
        }

        fail( "Expected attribute not found : " + subject + " " + predicate + " " + val );

        return this;
    }


    public AxiomTester checkAnnotation( String source, IRI annotationProp, String value ) {
        return checkAnnotation( source, annotationProp.toString(), value );
    }

    public AxiomTester checkAnnotation( String source, String annotationProp, String value ) {
        source = resolvePrefix( source );
        annotationProp = resolvePrefix( annotationProp );

        for ( OWLOntology onto : ontology.getOWLOntologyManager().getOntologies() ) {
            for ( OWLAnnotationAssertionAxiom ann : onto.getAnnotationAssertionAxioms( IRI.create( source ) ) ) {
                if ( ann.getProperty().getIRI().equals( IRI.create( annotationProp ) ) ) {
                    String tgt = ann.getValue().toString();
                    if ( tgt.contains( "^^" ) ) {
                        tgt = tgt.substring( 0, tgt.indexOf( "^^" ) );
                    }
                    if ( tgt.startsWith( "\"" ) ) {
                        tgt = tgt.substring( 1, tgt.length() - 1 );
                    }
                    if ( tgt.equals( value ) ) {
                        return this;
                    }
                }
            }
        }
        fail( "Expected annotation not found : " + source + " " + annotationProp + " " + value );

        return this;
    }

    public AxiomTester checkSubClass( String sub, String sup ) {
        sub = resolvePrefix( sub );
        sup = resolvePrefix( sup );

        OWLClass subKlass = odf.getOWLClass( IRI.create( sub ) );
        OWLClass supKlass = odf.getOWLClass( IRI.create( sup ) );

        Set<OWLClassExpression> subs = subKlass.getSuperClasses( ontology.getImportsClosure() );
        for ( OWLClassExpression sca : subs ) {
            if ( ! sca.isAnonymous() && sca.asOWLClass().equals( supKlass ) ) {
                return this;
            }
        }
        fail( "Expected subClass relationship not found : " + sub + " " + sup );

        return this;
    }

    public AxiomTester checkConceptCodeDefinition( String klassName, IRI attrib, String code ) {
        return checkConceptCodeDefinition( klassName, attrib.toString(), code );
    }

    public AxiomTester checkConceptCodeDefinition( String klassName, String attrib, String code ) {
        klassName = resolvePrefix( klassName );
        attrib = resolvePrefix( attrib );
        code = resolvePrefix( code );

        OWLClass klass = odf.getOWLClass( IRI.create( klassName ) );
        OWLObjectProperty prop = odf.getOWLObjectProperty( IRI.create( attrib ) );
        OWLNamedIndividual cd = odf.getOWLNamedIndividual( IRI.create( code ) );

        Set<OWLEquivalentClassesAxiom> eqs = new HashSet<OWLEquivalentClassesAxiom>();
        for ( OWLOntology onto : ontology.getOWLOntologyManager().getOntologies() ) {
            eqs.addAll( onto.getEquivalentClassesAxioms( klass ) );
        }
        assertFalse( eqs.isEmpty() );

        for ( OWLEquivalentClassesAxiom equiv : eqs ) {
            OWLClassExpression def = equiv.getClassExpressionsMinus( klass ).iterator().next();
            assertNotNull( def );
            OWLObjectSomeValuesFrom some = lookupDefBySome( def, prop );
            assertNotNull( some );
            if ( some.getFiller() instanceof OWLObjectHasValue ) {
                OWLObjectHasValue val = (OWLObjectHasValue) some.getFiller();
                if ( val.getProperty().getNamedProperty().getIRI().equals( RIMNames.EXPRESSES ) ) {
                    if ( val.getFiller().equals( cd ) ) {
                        return this;
                    }
                }
            }
        }
        fail( "Unable to find concept-mediated definition for class " + klassName + " using attribute " + attrib + " and concept " + code );

        return this;
    }

    private OWLObjectSomeValuesFrom lookupDefBySome( OWLClassExpression def, OWLObjectProperty attrib ) {
        if ( def instanceof OWLObjectSomeValuesFrom ) {
            if ( ( (OWLObjectSomeValuesFrom) def ).getProperty().getNamedProperty().getIRI().equals( attrib.getIRI() ) ) {
                return (OWLObjectSomeValuesFrom) def;
            }
        } else if ( def instanceof OWLObjectIntersectionOf ) {
            OWLObjectIntersectionOf and = (OWLObjectIntersectionOf) def;
            for ( OWLClassExpression arg : and.getOperandsAsList() ) {
                OWLObjectSomeValuesFrom some = lookupDefBySome( arg, attrib );
                if ( some != null ) {
                    return some;
                }
            }
        }
        return null;
    }

    public AxiomTester checkAttribute( String klassName, String attrib, Integer card, String type ) {
        klassName = resolvePrefix( klassName );
        attrib = resolvePrefix( attrib );
        type = resolvePrefix( type );

        OWLClass klass = odf.getOWLClass( IRI.create( klassName ) );
        OWLObjectProperty prop = odf.getOWLObjectProperty( IRI.create( attrib ) );
        OWLClass tgt = odf.getOWLClass( IRI.create( type ) );

        Set<OWLClassExpression> sups = klass.getSuperClasses( ontology.getImportsClosure() );
        for ( OWLClassExpression sup : sups ) {
            if ( card == null ) {
                if ( sup instanceof OWLObjectAllValuesFrom ) {
                    OWLObjectAllValuesFrom all = (OWLObjectAllValuesFrom) sup;
                    if ( all.getProperty().asOWLObjectProperty().getIRI().equals( prop.getIRI() ) )  {
                        if ( all.getFiller().asOWLClass().getIRI().equals( tgt.getIRI() ) ) {
                            return this;
                        }
                    }
                }
            } else {
                if ( sup instanceof OWLObjectMaxCardinality ) {
                    OWLObjectMaxCardinality max = (OWLObjectMaxCardinality) sup;
                    if ( max.getProperty().asOWLObjectProperty().getIRI().equals( prop.getIRI() ) )  {
                        if ( max.getFiller().asOWLClass().getIRI().equals( tgt.getIRI() )
                                && max.getCardinality() == card ) {
                            return this;
                        }
                    }
                }
            }
        }
        fail( "Could not validate attribute " + attrib + " on class " + klassName );

        return this;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public AxiomTester checkSubProperty( String sub, String sup ) {
        sub = resolvePrefix( sub );
        sup = resolvePrefix( sup );

        OWLObjectProperty subProp = odf.getOWLObjectProperty( IRI.create( sub ) );
        OWLObjectProperty supProp = odf.getOWLObjectProperty( IRI.create( sup ) );

        boolean hasSup = false;
        Set<OWLObjectPropertyExpression> supers = subProp.getSuperProperties( ontology.getImportsClosure() );
        for ( OWLObjectPropertyExpression dx : supers ) {
            if ( dx.equals( supProp ) ) {
                hasSup = true;
            }
        }
        if ( ! hasSup ) {
            fail( "Property " + subProp.getIRI() + " does not have required super " + supProp.getIRI() );
        }
        return this;
    }

    public AxiomTester checkChain( String prop, List<String> chain ) {
        prop = resolvePrefix( prop );
        List<OWLObjectPropertyExpression> resolved = new ArrayList<OWLObjectPropertyExpression>( chain.size() );

        for ( String s : chain ) {
            resolved.add( odf.getOWLObjectProperty( IRI.create( resolvePrefix( s ) ) ) );
        }

        OWLObjectProperty subProp = odf.getOWLObjectProperty( IRI.create( prop ) );

        boolean hasChain = false;
        for ( OWLOntology o : ontology.getImportsClosure() ) {
            for ( OWLSubPropertyChainOfAxiom dx : o.getAxioms( AxiomType.SUB_PROPERTY_CHAIN_OF ) ) {
                if ( dx.getSuperProperty().equals( subProp ) && dx.getPropertyChain().equals( resolved ) ) {
                    hasChain = true;
                }
            }
        }
        if ( ! hasChain ) {
            fail( "Property " + subProp.getIRI() + " does not have required chain " + chain );
        }
        return this;
    }

    public AxiomTester checkPropertyDomainRange( String prop, String dom, String ran ) {
        prop = resolvePrefix( prop );
        dom = resolvePrefix( dom );
        ran = resolvePrefix( ran );
        
        OWLObjectProperty p = odf.getOWLObjectProperty( IRI.create( prop ) );
        OWLClass d = odf.getOWLClass( IRI.create( dom ) ); 
        OWLClass r = odf.getOWLClass( IRI.create( ran ) ); 
        
        boolean hasDom = false;
        Set<OWLClassExpression> domains = p.getDomains( ontology.getImportsClosure() );
        for ( OWLClassExpression dx : domains ) {
            if ( dx.equals( d ) ) {
                hasDom = true;
            }
        }
        boolean hasRan = false;
        Set<OWLClassExpression> ranges = p.getRanges( ontology.getImportsClosure() );
        for ( OWLClassExpression dx : ranges ) {
            if ( dx.equals( r ) ) {
                hasRan = true;
            }
        }
        if ( ! hasDom ) {
            fail( "Property " + p.getIRI() + " does not have required domain " + d.getIRI() );
        }
        if ( ! hasRan ) {
            fail( "Property " + p.getIRI() + " does not have required range " + r.getIRI() );
        }
        return this;
    }

    private OWLOntology getDefiningOntology( OWLOntology ontology, OWLObjectProperty p ) {
        IRI ns = IRI.create( p.getIRI().getNamespace().replace( "#", "" ) );
        OWLOntology candidate = ontology.getOWLOntologyManager().getOntology( ns );
        return candidate != null ? candidate : ontology;
    }
}

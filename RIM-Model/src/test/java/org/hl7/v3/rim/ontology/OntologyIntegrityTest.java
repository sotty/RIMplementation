package org.hl7.v3.rim.ontology;

import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hl7.v3.rim.ontology.RIMNames.ABSTRACT;
import static org.hl7.v3.rim.ontology.RIMNames.BASE_AR;
import static org.hl7.v3.rim.ontology.RIMNames.CLASS_CODE;
import static org.hl7.v3.rim.ontology.RIMNames.COMPONENT_OF;
import static org.hl7.v3.rim.ontology.RIMNames.CONCEPT;
import static org.hl7.v3.rim.ontology.RIMNames.DAT;
import static org.hl7.v3.rim.ontology.RIMNames.DESCRIPTION;
import static org.hl7.v3.rim.ontology.RIMNames.EXPRESSES;
import static org.hl7.v3.rim.ontology.RIMNames.INBOUND;
import static org.hl7.v3.rim.ontology.RIMNames.INTERNAL_ID;
import static org.hl7.v3.rim.ontology.RIMNames.IN_SCHEME;
import static org.hl7.v3.rim.ontology.RIMNames.NOTATION;
import static org.hl7.v3.rim.ontology.RIMNames.OUTBOUND;
import static org.hl7.v3.rim.ontology.RIMNames.PREF_LABEL;
import static org.hl7.v3.rim.ontology.RIMNames.RIM;
import static org.hl7.v3.rim.ontology.RIMNames.SPECIALIZES;
import static org.hl7.v3.rim.ontology.RIMNames.VALUESET;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class OntologyIntegrityTest {

    private static OWLOntology rim;

    @BeforeClass
    public static void loadOntology() throws OWLOntologyCreationException, FileNotFoundException {
        RIMOntology ontology = new RIMOntology();
        rim = ontology.getOntology();
        assertNotNull( rim );
    }


    @Test
    public void testUniqueHierarchyBranchAttribution() {
        OWLOntology ontology = rim;
        for ( OWLClass klass : ontology.getClassesInSignature() ) {
            Set<OWLClass> ancestors = new HashSet<OWLClass>();
            Set<IRI> codeProperties = new HashSet<IRI>();
            if ( klass.getIRI().toString().startsWith( RIM ) ) {
                Set<OWLEquivalentClassesAxiom> equivalences = ontology.getEquivalentClassesAxioms( klass );
                for ( OWLEquivalentClassesAxiom eqX : equivalences ) {
                    OWLClassExpression def = eqX.getClassExpressionsAsList().get( 1 );
                    if ( def instanceof OWLObjectIntersectionOf
                         && ( (OWLObjectIntersectionOf) def ).getOperandsAsList().get( 1 ) instanceof OWLObjectSomeValuesFrom ) {
                        OWLClass ancestor = (OWLClass) ( (OWLObjectIntersectionOf) def ).getOperandsAsList().get( 0 );
                        OWLObjectSomeValuesFrom expr = (OWLObjectSomeValuesFrom) ( (OWLObjectIntersectionOf) def ).getOperandsAsList().get( 1 );
                        if ( expr.getFiller() instanceof OWLObjectHasValue && ( (OWLObjectHasValue) expr.getFiller() ).getProperty().getNamedProperty().getIRI().equals( EXPRESSES ) ) {
                            ancestors.add( ancestor );
                            codeProperties.add( expr.getProperty().getNamedProperty().getIRI() );
                        }
                    }
                }
                if ( ancestors.size() > 1 ) {
                    fail( "Class " + klass + " has many ancestors " + ancestors );
                }
                if ( codeProperties.size() > 1 ) {
                    fail( "Class " + klass + " has many codes " + codeProperties );
                }
            }
        }
    }

    @Test
    public void testTopLevelObjectPropertiesRanges() {
        boolean foundAttr = false;
        boolean foundLink = false;

        Set<OWLObjectProperty> errs = new HashSet<OWLObjectProperty>();
        Set<OWLObjectProperty> mult = new HashSet<OWLObjectProperty>();

        for ( OWLObjectProperty prop : rim.getObjectPropertiesInSignature( true ) ) {
            if ( ! RIMNames.RIM.equals( prop.getIRI().getNamespace() ) ) {
                continue;
            }

            Set<OWLObjectPropertyExpression> parents = prop.getSuperProperties( rim.getImportsClosure() );

            if ( parents.isEmpty() ) {
                foundAttr = true;
                for ( OWLClassExpression range : prop.getRanges( rim.getImportsClosure() ) ) {
                    assertTrue( !range.isAnonymous() );
                    String ns = range.asOWLClass().getIRI().getNamespace();
                    if ( ! RIMNames.DAT.equals( ns ) ) {
                        errs.add( prop );
                    }
                }
            }
            else {
                foundLink = true;
                Set<OWLClassExpression> ranges = prop.getRanges( rim.getImportsClosure() );
                if ( ranges.size() != 1 ) {
                    mult.add( prop );
                }

                Set<OWLClassExpression> domains = prop.getDomains( rim.getImportsClosure() );
                if ( domains.size() != 1 ) {
                    mult.add( prop );
                }

            }
        }

        String errMsg = null;
        String mltMsg = null;
        if ( ! errs.isEmpty() ) {
            errMsg = "Fail : top level properties detected with non-datatype range : \n";
            for ( OWLObjectProperty p : errs ) {
                errMsg += "\t" + p.getIRI() + " : " + p.getRanges( rim.getImportsClosure() ) + " \n";
            }
            System.err.print( errMsg );
        }
        if ( ! mult.isEmpty() ) {
            mltMsg = "Fail : top level properties detected with abnormal dom/range cardinalities : \n";
            for ( OWLObjectProperty p : mult ) {
                mltMsg += "\t" + p.getIRI() + " : " + p.getRanges( rim.getImportsClosure() ) + " \n";
            }
            System.err.println( mltMsg );
        }

        if ( errMsg != null ) {
            fail( errMsg );
        }
        if ( mltMsg != null ) {
            fail( mltMsg );
        }

        assertTrue( foundAttr );
        assertTrue( foundLink );
    }

    @Test
    public void testTopLevelObjectPropertiesHierarchy() {
        OWLDataFactory f = rim.getOWLOntologyManager().getOWLDataFactory();
        OWLObjectProperty arel = f.getOWLObjectProperty( BASE_AR );
        OWLObjectProperty inbo = f.getOWLObjectProperty( INBOUND );
        OWLObjectProperty outb = f.getOWLObjectProperty( OUTBOUND );

        for ( OWLObjectProperty prop : rim.getObjectPropertiesInSignature() ) {
            if ( ! RIMNames.RIM.equals( prop.getIRI().getNamespace() ) ) {
                continue;
            }

            Set<OWLObjectPropertyExpression> parents = prop.getSuperProperties( rim.getImportsClosure() );
            if ( ! parents.isEmpty() ) {
                boolean hasInbound = parents.contains( inbo );
                boolean hasOutbound = parents.contains( outb );
                boolean hasBase = parents.contains( arel );
                boolean hasUpperRel = hasBase || hasInbound || hasOutbound;

                Set<OWLObjectPropertyExpression> others = new HashSet<>( parents );
                others.remove( arel );
                others.remove( inbo );
                others.remove( outb );

                boolean hasOthers = others.size() >= 1;
                if ( ! ( hasOthers ^ hasUpperRel ) ) {
                    System.out.print( prop );
                }
                assertTrue( hasOthers ^ hasUpperRel );
            }
        }
    }

    @Test
    public void testUniqueDomainsAndRanges() {
        OWLOntology onto = rim;
        for ( OWLObjectProperty op : onto.getObjectPropertiesInSignature() ) {
            if ( onto.getObjectPropertyDomainAxioms( op ).size() > 1 ) {
                System.err.println( "WARN : multiple domains " + op.getIRI() );
                fail( "Multiple domains detected on " + op.getIRI() );
            }
            if ( onto.getObjectPropertyRangeAxioms( op ).size() > 1 ) {
                System.err.println( "WARN : multiple ranges " + op.getIRI() );
                fail( "Multiple ranges detected on " + op.getIRI() );
            }
        }
    }


    @Test
    public void testAttributeAnnotations() {
        OWLOntology onto = rim;
        OWLAnnotationProperty ap = onto.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty( IRI.create( "http://drools.org/shapes/attribute" ) );
        boolean foundDT = false;
        for ( OWLObjectProperty op : onto.getObjectPropertiesInSignature( true ) ) {
            boolean isDatatype = false;
            for ( OWLClassExpression ran : op.getRanges( onto.getImportsClosure() ) ) {
                if ( ! ran.isAnonymous() && ran.asOWLClass().getIRI().getNamespace().equals( DAT ) ) {
                    isDatatype = true;
                    foundDT = true;
                    break;
                } else if ( ran instanceof OWLObjectUnionOf ) {
                    for ( OWLClassExpression member : ( (OWLObjectUnionOf) ran ).getOperandsAsList() ) {
                        if ( ! member.isAnonymous() && member.asOWLClass().getIRI().getNamespace().equals( DAT ) ) {
                            isDatatype = true;
                            foundDT = true;
                            break;
                        }
                    }
                }
            }
            boolean isAttribute = false;
            for ( OWLOntology o : onto.getImportsClosure() ) {
                if ( ! op.getAnnotations( o, ap ).isEmpty() ) {
                    isAttribute = true;
                    break;
                }
            }
            if( isAttribute ^ isDatatype ) {
                //fail( "Mismatched @attribute on " + op.getIRI() );
            }
        }

        if ( ! foundDT ) {
            fail( "No datatype properties found, something is missing" );
        }
    }


}


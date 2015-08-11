package org.hl7.v3.rim.ontology.parser;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public abstract class RIMOntologyBuilder implements OntologyProcessor {

    protected Logger logger = Logger.getLogger( RIMOntologyBuilder.class.getName() );


    protected void addAxiom( OWLOntology ontology, OWLAxiom axiom ) {
        ontology.getOWLOntologyManager().applyChanges( ontology.getOWLOntologyManager().addAxiom( ontology,
                                                                                                  axiom ) );

    }

    protected void removeAxiom( OWLOntology ontology, OWLAxiom axiom ) {
        ontology.getOWLOntologyManager().applyChanges( ontology.getOWLOntologyManager().removeAxiom( ontology,
                                                                                                     axiom ) );

    }

    protected OWLNamedIndividual createConceptScheme( String sch, OWLOntology ontology, OWLDataFactory f ) {
        ontology = cast( ontology, RMV_ONTO );

        OWLNamedIndividual conceptScheme = f.getOWLNamedIndividual( getConceptSchemeIRI( sch ) );
        // the concept exists
        addAxiom( ontology, f.getOWLDeclarationAxiom( conceptScheme ) );
        addAxiom( ontology,
                  f.getOWLClassAssertionAxiom( f.getOWLClass( CONCEPT_SCHEME ),
                                               conceptScheme ) );
        addAnnotation( conceptScheme, OID, sch, ontology, f );
        return conceptScheme;
    }

    protected void addAnnotation( OWLEntity ind, IRI annotation, String value, OWLOntology ontology, OWLDataFactory f ) {
        addAxiom( ontology,
                  f.getOWLAnnotationAssertionAxiom(
                          ind.getIRI(),
                          f.getOWLAnnotation(
                                  f.getOWLAnnotationProperty( annotation ) ,
                                  f.getOWLLiteral( value ) )
                  )
        );
    }


    protected OWLNamedIndividual createValueset( String id, String code, OWLOntology ontology, OWLDataFactory f ) {
        ontology = cast( ontology, RMV_ONTO );

        // create a concept
        OWLNamedIndividual vset = f.getOWLNamedIndividual( getValuesetIRI( code, id ) );
        // the concept exists
        addAxiom( ontology,
                  f.getOWLDeclarationAxiom( vset ) );
        addAxiom( ontology,
                  f.getOWLClassAssertionAxiom( f.getOWLClass( VALUESET ),
                                               vset ) );
        addLabel( vset, code, ontology, f );
        return vset;
    }


    protected void addSubClassOf( IRI klass, IRI parentClass, OWLOntology ontology, OWLDataFactory f ) {
        addAxiom( ontology,
                  f.getOWLSubClassOfAxiom( f.getOWLClass( klass ), f.getOWLClass( parentClass ) ) );
    }



    protected OWLNamedIndividual createConcept( IRI id, String cod, String notation, OWLNamedIndividual scheme, OWLOntology ontology, OWLDataFactory f ) {
        ontology = cast( ontology, RMV_ONTO );

        // create a concept
        OWLNamedIndividual concept = f.getOWLNamedIndividual( id );
        // the concept exists
        addAxiom( ontology,
                  f.getOWLDeclarationAxiom( concept ) );
        addAxiom( ontology,
                  f.getOWLClassAssertionAxiom( f.getOWLClass( CONCEPT ),
                                               concept ) );
        if ( notation != null ) {
            addAxiom( ontology,
                      f.getOWLDataPropertyAssertionAxiom(
                              f.getOWLDataProperty( NOTATION ),
                                                    concept,
                                                    f.getOWLLiteral( notation ) ) );
        }
        if ( scheme != null ) {
            addAxiom( ontology,
                      f.getOWLObjectPropertyAssertionAxiom( f.getOWLObjectProperty( IN_SCHEME ),
                                                            concept,
                                                            scheme ) );
        }
        addSubConceptOf( concept, concept, ontology, f );
        addLabel( concept, cod, ontology, f );
        return concept;
    }

    protected void addType( IRI ind, IRI klass, OWLOntology ontology, OWLDataFactory factory ) {
        ontology = cast( ontology, RMV_ONTO );

        addAxiom( ontology,
                  factory.getOWLClassAssertionAxiom( factory.getOWLClass( klass ), factory.getOWLNamedIndividual( ind ) ) );
    }

    protected OWLNamedIndividual addLabel( OWLNamedIndividual concept, String label, OWLOntology ontology, OWLDataFactory f ) {
        ontology = cast( ontology, RMV_ONTO );

        addAxiom( ontology,
                  f.getOWLAnnotationAssertionAxiom(
                          concept.getIRI(),
                          f.getOWLAnnotation(
                                  f.getOWLAnnotationProperty( PREF_LABEL ),
                                  f.getOWLLiteral( label ) )
                  )
        );
        return concept;
    }

    protected void addSubConceptOf( OWLNamedIndividual sub, OWLNamedIndividual sup, OWLOntology ontology, OWLDataFactory factory ) {
        addRelationship( SPECIALIZES, sub, sup, ontology, factory );
    }

    protected void addRelationship( IRI rel, IRI src, IRI tgt, OWLOntology ontology, OWLDataFactory factory ) {
        addRelationship( rel, factory.getOWLNamedIndividual( src ), factory.getOWLNamedIndividual( tgt ), ontology, factory );
    }

    protected void addRelationship( IRI rel, OWLNamedIndividual src, OWLNamedIndividual tgt, OWLOntology ontology, OWLDataFactory factory ) {
        ontology = cast( ontology, RMV_ONTO );
        addAxiom( ontology,
                  factory.getOWLObjectPropertyAssertionAxiom(
                          factory.getOWLObjectProperty( rel ),
                          src,
                          tgt
                  ) );
    }

    protected void addVSMediatedAttributeRangeRestriction( IRI klass, IRI prop, IRI vs, OWLOntology ontology, OWLDataFactory f ) {
        ontology = cast( ontology, RMV_ONTO );

        // assert denotes
        OWLSubClassOfAxiom subk = f.getOWLSubClassOfAxiom( f.getOWLClass( klass ),
                                                           f.getOWLObjectAllValuesFrom(
                                                                   f.getOWLObjectProperty( prop ),
                                                                   f.getOWLObjectHasValue(
                                                                           f.getOWLObjectProperty( MEMBER_OF ),
                                                                           f.getOWLNamedIndividual( vs )
                                                                   )
                                                           ) );
        addAxiom( ontology, subk );
    }

    public OWLNamedIndividual createRoleWithProperties( IRI code, IRI className, String inbound, String outbound, String link, boolean asClass, OWLOntology ontology, OWLDataFactory f ) {
        OWLClass role = f.getOWLClass( LINKED_ROLE );
        OWLClass entity = f.getOWLClass( LINKED_ENTITY );
        OWLClass klass = f.getOWLClass( className );

        OWLNamedIndividual ind = createConceptMediatedDefinition( klass,
                                         role,
                                         f.getOWLObjectProperty( asClass ? CLASS_CODE : CODE ),
                                         f.getOWLNamedIndividual( code ),
                                         ontology,
                                         f );

        OWLObjectProperty linkP = f.getOWLObjectProperty( IRI.create( RIM, link ) );
        createObjectProperty( linkP, entity, entity, ontology, f );

        if ( ! StringUtils.isEmpty( inbound ) && ! StringUtils.isEmpty( outbound ) ) {
            OWLObjectProperty linkIn = f.getOWLObjectProperty( IRI.create( RIM, inbound ) );
            OWLObjectProperty linkOut = f.getOWLObjectProperty( IRI.create( RIM, outbound ) );

            createObjectProperty( linkIn, entity, klass, ontology, f );
            createObjectProperty( linkOut, entity, klass, ontology, f );

            addSubPropertyOf( linkP.getIRI(), BASE_ROL, ontology, f );
            addSubPropertyOf( linkIn.getIRI(), PLAYED_ROLE, ontology, f );
            addSubPropertyOf( linkOut.getIRI(), SCOPED_ROLE, ontology, f );

            addAxiom( ontology,
                      f.getOWLSubPropertyChainOfAxiom( Arrays.asList( linkIn, f.getOWLObjectProperty( SCOPER ) ),
                                                       linkP ) );
        }
        return ind;
    }

    public OWLNamedIndividual createParticipationWithProperties( IRI code, IRI className, String inbound, String outbound, String link, boolean asClass, OWLOntology ontology, OWLDataFactory f ) {
        OWLClass participation = f.getOWLClass( PART );
        OWLClass role = f.getOWLClass( LINKED_ROLE );
        OWLClass act = f.getOWLClass( LINKED_ACT );
        OWLClass klass = f.getOWLClass( className );

        OWLNamedIndividual ind = createConceptMediatedDefinition( klass,
                                         participation,
                                         f.getOWLObjectProperty( TYPECODE ),
                                         f.getOWLNamedIndividual( code ),
                                         ontology,
                                         f );

        OWLObjectProperty linkP = f.getOWLObjectProperty( IRI.create( RIM, link ) );
        createObjectProperty( linkP, act, role, ontology, f );

        if ( ! StringUtils.isEmpty( inbound ) && ! StringUtils.isEmpty( outbound ) ) {
            OWLObjectProperty linkIn = f.getOWLObjectProperty( IRI.create( RIM, inbound ) );
            OWLObjectProperty linkOut = f.getOWLObjectProperty( IRI.create( RIM, outbound ) );

            createObjectProperty( linkIn, role, klass, ontology, f );
            createObjectProperty( linkOut, act, klass, ontology, f );

            addSubPropertyOf( linkP.getIRI(), BASE_PART, ontology, f );
            addSubPropertyOf( linkIn.getIRI(), ROL_PART, ontology, f );
            addSubPropertyOf( linkOut.getIRI(), ACT_PART, ontology, f );

            addAxiom( ontology,
                      f.getOWLSubPropertyChainOfAxiom( Arrays.asList( linkOut, f.getOWLObjectProperty( PART_TO_ROLE ) ),
                                                       linkP ) );
        }
        return ind;
    }

    public OWLNamedIndividual createActRelationshipWithProperties( IRI code, IRI className, String inbound, String outbound, String link, boolean hasParents, OWLOntology ontology, OWLDataFactory f ) {
        OWLClass actRel = f.getOWLClass( LINKED_ACTREL );
        OWLClass act = f.getOWLClass( LINKED_ACT );
        OWLClass klass = f.getOWLClass( className );

        OWLNamedIndividual ind = createConceptMediatedDefinition( klass,
                                                                  actRel,
                                                                  f.getOWLObjectProperty( TYPECODE ),
                                                                  f.getOWLNamedIndividual( code ),
                                                                  ontology,
                                                                  f );

        OWLObjectProperty linkP = f.getOWLObjectProperty( IRI.create( RIM, link ) );
        createObjectProperty( linkP, act, act, ontology, f );

        if ( ! StringUtils.isEmpty( inbound ) && ! StringUtils.isEmpty( outbound ) ) {
            OWLObjectProperty linkIn = f.getOWLObjectProperty( IRI.create( RIM, inbound ) );
            OWLObjectProperty linkOut = f.getOWLObjectProperty( IRI.create( RIM, outbound ) );

            createObjectProperty( linkIn, act, klass, ontology, f );
            createObjectProperty( linkOut, act, klass, ontology, f );

            addSubPropertyOf( linkIn.getIRI(), INBOUND, ontology, f );
            addSubPropertyOf( linkOut.getIRI(), OUTBOUND, ontology, f );
            addSubPropertyOf( linkP.getIRI(), BASE_AR, ontology, f );

            addAxiom( ontology,
                      f.getOWLSubPropertyChainOfAxiom( Arrays.asList( linkOut, f.getOWLObjectProperty( TARGET ) ),
                                                       linkP ) );
        }
        return ind;
    }

    public void addSubPropertyOf( IRI sub, IRI sup, OWLOntology ontology, OWLDataFactory f ) {
        addAxiom( ontology,
                  f.getOWLSubObjectPropertyOfAxiom( f.getOWLObjectProperty( sub ), f.getOWLObjectProperty( sup ) ) );
    }

    public void removeSubPropertyOf( IRI sub, IRI sup, OWLOntology ontology, OWLDataFactory f ) {
        removeAxiom( ontology,
                     f.getOWLSubObjectPropertyOfAxiom( f.getOWLObjectProperty( sub ), f.getOWLObjectProperty( sup ) ) );
    }

    public void createObjectProperty( OWLObjectProperty property, OWLClass domain, OWLClass range, OWLOntology ontology, OWLDataFactory f ) {
        addAxiom( ontology,
                  f.getOWLDeclarationAxiom( property ) );
        addAxiom( ontology,
                  f.getOWLObjectPropertyDomainAxiom( property, domain ));
        addAxiom( ontology,
                  f.getOWLObjectPropertyRangeAxiom( property, range ));

    }

    public void makeInverse( OWLObjectProperty p1, OWLObjectProperty p2, OWLOntology ontology, OWLDataFactory f ) {
        addAxiom( ontology, f.getOWLInverseObjectPropertiesAxiom( p1, p2 ) );
    }


    protected OWLNamedIndividual createConceptMediatedDefinition( OWLClass klass, OWLClass parent, OWLObjectProperty prop, OWLNamedIndividual con, OWLOntology ontology, OWLDataFactory f ) {
        ontology = cast( ontology, RMV_ONTO );

        // assert denotes
        OWLEquivalentClassesAxiom ekk = f.getOWLEquivalentClassesAxiom( klass,
                                                                        f.getOWLObjectIntersectionOf(
                                                                                parent,
                                                                                f.getOWLObjectSomeValuesFrom(
                                                                                        prop,
                                                                                        f.getOWLObjectHasValue(
                                                                                                f.getOWLObjectProperty( EXPRESSES ),
                                                                                                con
                                                                                        )
                                                                                )
                                                                        )
        );
        addAxiom( ontology, ekk );

        // closure
        OWLSubClassOfAxiom cls = f.getOWLSubClassOfAxiom( klass,
                                                                f.getOWLObjectAllValuesFrom(
                                                                        prop,
                                                                        f.getOWLObjectHasValue(
                                                                                f.getOWLObjectProperty( EXPRESSES ),
                                                                                con
                                                                        )
                                                                )
        );
        addAxiom( ontology, cls );

        return con;
    }

    protected void markAsAbstract( IRI iri, OWLOntology ontology, OWLDataFactory f ) {
        addAxiom( ontology,
                  f.getOWLAnnotationAssertionAxiom( iri, f.getOWLAnnotation( f.getOWLAnnotationProperty( ABSTRACT ),
                                                                             f.getOWLLiteral( true ) ) ) );
    }

    protected boolean isWellKnownClass( String name ) {
        return "InfrastructureRoot".equals( name )
               || "Act".equals( name )
               || "ActRelationship".equals( name )
               || "Participation".equals( name )
               || "Role".equals( name )
               || "RoleLink".equals( name )
               || "Entity".equals( name );
    }

    protected boolean isRoot( String tgt ) {
        return "InfrastructureRoot".equals( tgt );
    }

    protected boolean isAbstract( String cod ) {
        return cod.startsWith( "_" );
    }

    @Override
    public OWLOntology process( List<CSVRecord> recordList, RIMContext context ) {
        try {
            OWLOntology ontology = context.getOntology();
            fixUp( context, ontology, ontology.getOWLOntologyManager().getOWLDataFactory() );
            for ( CSVRecord record : recordList ) {
                if ( record.getRecordNumber() == 1 ) {
                    // exclude header
                    continue;
                }
                process( record, context, ontology, ontology.getOWLOntologyManager(), ontology.getOWLOntologyManager().getOWLDataFactory() );
            }
            if ( isStateful() ) {
                wrapUp( context, ontology, ontology.getOWLOntologyManager().getOWLDataFactory() );
            }
            return ontology;
        } catch ( Exception e ) {
            logger.log( Level.SEVERE, e.getMessage() );
            e.printStackTrace();
        }
        return null;
    }
    
    protected OWLClass classNameToOWLClass( String src, OWLDataFactory f ) {
        return f.getOWLClass( IRI.create( isRoot( src ) ? UPP : RIM, src ) );
    }

    protected void addAttribute( IRI domainIRI, IRI propertyIRI, IRI typeIRI, Integer card, String descr, OWLOntology ontology, OWLDataFactory f ) {
        OWLObjectProperty prop = f.getOWLObjectProperty( propertyIRI );
        OWLClass dom = f.getOWLClass( domainIRI );
        OWLClass range = f.getOWLClass( typeIRI );

        addAxiom( ontology,
                  f.getOWLDeclarationAxiom( prop ) );
        addAxiom( ontology,
                  f.getOWLObjectPropertyDomainAxiom( prop, dom ) );
        addAxiom( ontology,
                  f.getOWLObjectPropertyRangeAxiom( prop, range ) );
        addDescription( propertyIRI, descr, ontology, f );

        if ( card != null ) {
            addAxiom( ontology,
                      f.getOWLSubClassOfAxiom( dom,
                                               f.getOWLObjectMaxCardinality( card,
                                                                             prop,
                                                                             range ) )
                                               );
        } else {
            addAxiom( ontology,
                      f.getOWLSubClassOfAxiom( dom,
                                               f.getOWLObjectAllValuesFrom( prop,
                                                                            range ) )
            );

        }
        addAnnotation( prop, IRI.create( "http://drools.org/shapes/attribute" ), "", ontology, f );
    }

    protected void addDescription( IRI subject, String descr, OWLOntology ontology, OWLDataFactory f ) {
        addAxiom( ontology,
                  f.getOWLAnnotationAssertionAxiom( subject,
                                                    f.getOWLAnnotation( f.getOWLAnnotationProperty( DESCRIPTION ),
                                                                        f.getOWLLiteral( descr, OWL2Datatype.RDF_XML_LITERAL ) ) ) );

    }


    protected String[] splitCodes( String s ) {
        if ( s.contains( " " ) ) {
            String[] subs = s.split( " " );
            if ( subs.length > 2 ) {
                return subs;
            }
            if ( isFullUppercase( subs[ 0 ] ) && isFullUppercase( subs[ 1 ] ) ) {
                return new String[] { s };
            } else {
                return subs;
            }
        } else {
            return new String[] { s };
        }
    }

    private boolean isFullUppercase( String sub ) {
        for ( int j = 0; j < sub.length(); j++ ) {
            if ( Character.isLowerCase( sub.charAt( j ) ) ) {
                return false;
            }
        }
        return true;
    }



    protected abstract void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory owlDataFactory );

    @Override
    public boolean isStateful() {
        return false;
    }

    @Override
    public void wrapUp( RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        // do nothing unless overwritten
    }

    public void fixUp( RIMContext context, OWLOntology ontology, OWLDataFactory owlDataFactory ) {
        // do nothing by default
    }

    protected OWLOntology cast( OWLOntology ontology, String iri ) {
        return ontology.getOWLOntologyManager().getOntology( getOntologyIRI( iri ) );
    }
}

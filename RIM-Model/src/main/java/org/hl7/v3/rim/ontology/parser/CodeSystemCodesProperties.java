package org.hl7.v3.rim.ontology.parser;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.hl7.v3.rim.ontology.RIMNames;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class CodeSystemCodesProperties extends RIMOntologyBuilder {

    private static final String ID = "Id";
    private static final String PRO = "PropertyName";
    private static final String VAL = "Value";
    private static final String CSID = "CodeSystemId";

    private Map<String,String> classedConcepts = new HashMap<String,String>();
    private Map<IRI,OWLObjectProperty> knownProperties = new HashMap<IRI,OWLObjectProperty>();

    private Map<IRI, Arc> links = new HashMap<>();

    @Override
    protected void process( CSVRecord record, RIMContext context, OWLOntology ontology, OWLOntologyManager owlOntologyManager, OWLDataFactory f ) {
        String id = record.get( ID );
        String pro = record.get( PRO );
        String val = checkReserved( record.get( VAL ) );
        String csid = record.get( CSID );

        for ( String subId : splitCodes( id ) ) {
            processProperty( subId, csid, pro, val, context, ontology, f );
        }

        OWLObjectProperty arel = f.getOWLObjectProperty( BASE_AR );
        OWLObjectProperty inbo = f.getOWLObjectProperty( INBOUND );
        OWLObjectProperty outb = f.getOWLObjectProperty( OUTBOUND );

        OWLObjectProperty scop = f.getOWLObjectProperty( PLAYED_ROLE );
        OWLObjectProperty play = f.getOWLObjectProperty( SCOPED_ROLE );
        OWLObjectProperty role = f.getOWLObjectProperty( BASE_ROL );

        OWLObjectProperty ptng = f.getOWLObjectProperty( BASE_PART );
        OWLObjectProperty ptps = f.getOWLObjectProperty( ROL_PART );
        OWLObjectProperty ptnt = f.getOWLObjectProperty( ACT_PART );

        knownProperties.put( BASE_AR, arel );
        knownProperties.put( INBOUND, inbo );
        knownProperties.put( OUTBOUND, outb );

        knownProperties.put( SCOPED_ROLE, play );
        knownProperties.put( PLAYED_ROLE, scop );
        knownProperties.put( BASE_ROL, role );

        knownProperties.put( BASE_PART, ptng );
        knownProperties.put( ROL_PART, ptps );
        knownProperties.put( ACT_PART, ptnt );

    }

    private String checkReserved( String s ) {
        if ( "participation".equals( s ) ) {
            return s + "_";
        }
        return s;
    }

    private void processProperty( String id, String csid, String pro, String val, RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        String cid = getConceptIRI( id, csid ).toString();
        if ( ! context.getConcepts().containsKey( cid ) ) {
            System.err.println( "Concept not found " + cid + ", unable to assert properties" );
            return;
        }

        OWLNamedIndividual con = context.getConcepts().get( cid.toString() );

        boolean asCore = true;
        if ( "PrintName".equals( pro ) ) {
            addLabel( con, val, ontology, f );
            Arc arc = getArc( id, csid, context );
            arc.label = val;
            arc.link = disambiguatePropertyName( RIMNames.getRelationshipName( val, null ), context.getUsedNames() );
            arc.className = IRI.create( RIM, RIMNames.formatClassName( val ) );
        } else if ( "Description".equals( pro ) ) {
            addDescription( con.getIRI(), val, ontology, f );
        } else if ( "status".equals( pro ) ) {
            // TODO Not supported yet
        } else if ( "Name:Class".equals( pro ) ) {
            if ( ! context.getClassConcepts().containsKey( cid ) ) {
                classedConcepts.put( cid, formatClassName( val ) );
            }
        } else if ( "internalId".equals( pro ) ) {
            addAnnotation( con, INTERNAL_ID, val, ontology, f );
        } else if ( "conductible".equals( pro ) ) {

        } else if ( "conceptStatusQualifier".equals( pro ) ) {

        } else if ( "isDocumentCharacteristic".equals( pro ) ) {
            //
        } else {
            asCore = false;
        }


        boolean asRel = true;
        if ( "appliesTo".equals( pro ) ) {

        } else if ( "howApplies".equals( pro ) ) {

        } else {
            asRel = false;
        }

        boolean asLink = true;
        if ( "Sort:Act:inboundRelationship:ActRelationship".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( ACT_REL );
        } else if ( "Name:Act:inboundRelationship:ActRelationship".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( ACT_REL );
            arc.inbound = disambiguatePropertyName( val, context.getUsedNames() );
        } else if ( "Name:Act:outboundRelationship:ActRelationship".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( ACT_REL );
            arc.outbound = disambiguatePropertyName( val, context.getUsedNames() );
        } else if ( "Sort:Act:outboundRelationship:ActRelationship".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( ACT_REL );
        }


        if ( "Name:Act:participation:Participation".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( PART );
            arc.outbound = disambiguatePropertyName( val, context.getUsedNames() );;
        } else if ( "Sort:Act:participation:Participation".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( PART );
        } else if ( "Name:Role:participation:Participation".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( PART );
            arc.inbound = disambiguatePropertyName( val, context.getUsedNames() );;
        }


        if ( "Name:Role:player:Entity".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( ROLE );
            arc.inbound = disambiguatePropertyName( RIMNames.getRelationshipName( val, ROLE ), context.getUsedNames() );
        } else if ( "Sort:Role:player:Entity".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( ROLE );
        } else if ( "Sort:Entity:playedRole:Role".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( ROLE );
        } else if ( "Name:Entity:playedRole:Role".equalsIgnoreCase( pro ) ) {
            // NOT present in MIF
        }
        if ( "Name:Entity:scopedRole:Role".equalsIgnoreCase( pro ) ) {
            // NOT present in MIF
        } else if ( "Sort:Role:scoper:Entity".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( ROLE );
        } else if ( "Sort:Entity:scopedRole:Role".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( ROLE );
        } else if ( "Name:Role:scoper:Entity".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( ROLE );
            arc.outbound = disambiguatePropertyName( RIMNames.getRelationshipName( val, ROLE ), context.getUsedNames() );
        }

        if ( "Name:Role:outboundLink:RoleLink".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( LINK );
            arc.outbound = val;
        } else if ( "Sort:Role:outboundLink:RoleLink".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( LINK );
        } else if ( "Name:Role:inboundLink:RoleLink".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( LINK );
            arc.inbound = val;
        } else if ( "Sort:Role:inboundLink:RoleLink".equalsIgnoreCase( pro ) ) {
            Arc arc = getArc( id, csid, context );
            arc.ensureType( LINK );
        }

    }

    private Arc getArc( String cid, String csid, RIMContext context ) {
        IRI id = getConceptIRI( cid, csid );
        if ( ! links.containsKey( id ) ) {
            Arc arc = new Arc();
            arc.code = id;
            arc.abstrakt = isAbstract( cid );
            arc.parents = context.getChildOfRelationships().get( id.toString() );
            if ( arc.parents == null ) {
                arc.parents = Collections.emptySet();
            }
            links.put( id, arc );
        }
        return links.get( id );
    }

    @Override
    public void wrapUp( RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        checkAmbiguousInOutLinks();

        wrapUpClassedConcepts( context, ontology, f );
        wrapUpRelationedConcepts( context, ontology, f );

        cleanupPropertyHierarchies( context, ontology, f );
    }

    private void cleanupPropertyHierarchies( RIMContext context, OWLOntology ontology, OWLDataFactory f ) {

        for ( OWLObjectProperty op : ontology.getObjectPropertiesInSignature() ) {
            Set<OWLObjectPropertyExpression> sups = op.getSuperProperties( ontology.getImportsClosure() );
            if ( sups.size() > 1 ) {
                for ( IRI iri : knownProperties.keySet() ) {
                    OWLObjectProperty prop = knownProperties.get( iri );
                    if ( sups.contains( prop ) ) {
                        removeSubPropertyOf( op.getIRI(), iri, ontology, f );
                        sups.remove( prop );
                    }
                }
            }
        }
    }

    public void checkAmbiguousInOutLinks() {
        Set<String> outLinks = new HashSet<String>( );

        for ( Arc arc : links.values() ) {
            if ( arc.outbound != null ) {
                if ( arc.outbound.equals( arc.link ) || outLinks.contains( arc.outbound ) ) {
                    arc.outbound = disambiguatePropertyName( arc.outbound, outLinks );
                }
            }
        }
        for ( Arc arc : links.values() ) {
            if ( arc.inbound != null ) {
                if ( outLinks.contains( arc.inbound ) ) {
                    arc.inbound = disambiguatePropertyName( arc.inbound, outLinks );
                }
            }
        }
    }

    private static String disambiguatePropertyName( String name, Set<String> outLinks ) {
        String n = name;
        while ( outLinks.contains( n ) || outLinks.contains( NameUtils.pluralize( n ) ) ) {
            n = n + "_";
        }
        outLinks.add( n );
        outLinks.add( NameUtils.pluralize( n ) );
        return n;
    }

    public void wrapUpRelationedConcepts( RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        for ( Arc arc : links.values() ) {
            if ( arc.type == null ) {
                for ( String parent : arc.parents ) {
                    Arc parentArc = links.get( IRI.create( parent ) );
                    if ( parentArc != null && parentArc.type != null ) {
                        arc.type = parentArc.type;
                        break;
                    }
                }
                if ( arc.type == null ) {
                    continue;
                }
            }

            arc.className = disambiguate( arc.className, arc.code.toString(), context );

            if ( arc.type.equals( ROLE ) ) {

                if ( ! arc.className.equals( ROLE ) ) {
                    OWLNamedIndividual kls = createRoleWithProperties( arc.code, arc.className, arc.inbound, arc.outbound, arc.link,
                                                                       classedConcepts.containsKey( arc.code.toString() ) || context.getClassConcepts().containsKey( arc.code.toString() ),
                                                                       ontology, f );

                    for ( String parent : arc.parents ) {
                        Arc parentArc = links.get( IRI.create( parent ) );


                        if ( "EquivalentEntity".equals( parentArc.label )
                             || "RoleClassOntological".equals( arc.label )
                             || "RoleClassPartitive".equals( arc.label )
                             || "RoleClassPassive".equals( arc.label )
                             || "RoleClassRelationshipFormal".equals( arc.label )
                             || "RoleClassAssociative".equals( arc.label ) ) {
                            addSubClassOf( arc.className, parentArc.className, ontology, f );
                            addSubPropertyOf( roleLink( arc.link ), BASE_ROL, ontology, f );
                            addSubPropertyOf( roleLink( arc.outbound ), SCOPED_ROLE, ontology, f );
                            addSubPropertyOf( roleLink( arc.inbound ), PLAYED_ROLE, ontology, f );
                        } else {
                            if ( parentArc.type != null ) {
                                addSubClassOf( arc.className, parentArc.className, ontology, f );
                                if ( !StringUtils.isEmpty( parentArc.link ) && !StringUtils.isEmpty( arc.link ) ) {
                                    addSubPropertyOf( roleLink( arc.link ), roleLink( parentArc.link ), ontology, f );
                                }
                                if ( !StringUtils.isEmpty( parentArc.outbound ) && !StringUtils.isEmpty( arc.outbound ) ) {
                                    addSubPropertyOf( roleLink( arc.outbound ), roleLink( parentArc.outbound ), ontology, f );
                                }
                                if ( !StringUtils.isEmpty( parentArc.inbound ) && !StringUtils.isEmpty( arc.inbound ) ) {
                                    addSubPropertyOf( roleLink( arc.inbound ), roleLink( parentArc.inbound ), ontology, f );
                                }
                            }
                        }
                    }
                    context.addClassCode( arc.className, arc.code.toString() );
                }

            } else if ( arc.type.equals( ACT_REL ) ) {
                OWLNamedIndividual kls = createActRelationshipWithProperties( arc.code,
                        arc.className,
                        arc.inbound,
                        arc.outbound,
                        arc.link,
                        ! arc.parents.isEmpty(),
                        ontology, f );

                for ( String parent : arc.parents ) {
                    Arc parentArc = links.get( IRI.create( parent ) );
                    if ( parentArc.className.getFragment().equals( "ActRelationshipType" ) ) {
                        addSubClassOf( arc.className, IRI.create( RIM, "ActRelationship" ), ontology, f );

                        if ( !StringUtils.isEmpty( arc.link ) ) {
                            addSubPropertyOf( IRI.create( RIM, arc.link ), BASE_AR, ontology, f );
                        }
                        if ( !StringUtils.isEmpty( arc.outbound ) ) {
                            addSubPropertyOf( IRI.create( RIM, arc.outbound ), OUTBOUND, ontology, f );
                        }

                    } else {
                        addSubClassOf( arc.className, parentArc.className, ontology, f );

                        if ( !StringUtils.isEmpty( parentArc.link ) ) {
                            addSubPropertyOf( IRI.create( RIM, arc.link ), IRI.create( RIM, parentArc.link ), ontology, f );
                        }
                        if ( !StringUtils.isEmpty( parentArc.outbound ) && !StringUtils.isEmpty( arc.outbound ) ) {
                            addSubPropertyOf( IRI.create( RIM, arc.outbound ), IRI.create( RIM, parentArc.outbound ), ontology, f );
                        }
                    }
                }
                context.addClassCode( arc.className, arc.code.toString() );


            } else if ( arc.type.equals( PART ) ) {

                if ( ! arc.className.equals( PART ) ) {
                    OWLNamedIndividual kls = createParticipationWithProperties( arc.code, arc.className, arc.inbound, arc.outbound, arc.link,
                                                                                classedConcepts.containsKey( arc.code.toString() ) || context.getClassConcepts().containsKey( arc.code.toString() ),
                                                                                ontology, f );

                    for ( String parent : arc.parents ) {
                        Arc parentArc = links.get( IRI.create( parent ) );
                        addSubClassOf( arc.className, parentArc.className, ontology, f );

                        if ( parentArc.type != null ) {
                            if ( !StringUtils.isEmpty( parentArc.link ) && !StringUtils.isEmpty( arc.link ) ) {
                                addSubPropertyOf( participationLink( arc.link ), participationLink( parentArc.link ), ontology, f );
                            }
                            if ( !StringUtils.isEmpty( parentArc.outbound ) && !StringUtils.isEmpty( arc.outbound ) ) {
                                addSubPropertyOf( participationLink( arc.outbound ), participationLink( parentArc.outbound ), ontology, f );
                            }
                            if ( !StringUtils.isEmpty( parentArc.inbound ) && !StringUtils.isEmpty( arc.inbound ) ) {
                                addSubPropertyOf( participationLink( arc.inbound ), participationLink( parentArc.inbound ), ontology, f );
                            }
                        }

                    }
                    context.addClassCode( arc.className, arc.code.toString() );
                }

            } else if ( arc.type.equals( LINK ) ) {

            } else {
                throw new IllegalStateException( "Unexpected link type " + arc.type );
            }

        }
    }


    public void wrapUpClassedConcepts( RIMContext context, OWLOntology ontology, OWLDataFactory f ) {
        for ( String cid : classedConcepts.keySet() ) {
            if ( ! context.isAbstract( IRI.create( VOC + cid ) ) ) {
                if ( context.getClassForConcept( cid ) != null ) {
                    System.err.println( "Found code for already existing class " + cid + ", skipping " );
                    continue;
                }

                String pro = classedConcepts.get( cid );
                OWLNamedIndividual con = context.getConcepts().get( cid );

                OWLClass klass = f.getOWLClass( disambiguate( IRI.create( RIM, pro ), cid, context ) );

                String ancestorCode = getAncestor( cid, context );
                IRI ancestorClass = context.getClassForConcept( ancestorCode );
                if ( ancestorClass == null && classedConcepts.containsKey( ancestorCode ) ) {
                    ancestorClass = IRI.create( RIM, classedConcepts.get( ancestorCode ) );
                }
                
                String parentCode = getParent( cid, context ) != null ? getParent( cid, context ).iterator().next() : null;
                IRI parentClass = context.getClassForConcept( parentCode );
                if ( parentClass == null && classedConcepts.containsKey( parentCode ) ) {
                    parentClass = IRI.create( RIM, classedConcepts.get( parentCode ) );
                }

                if ( ancestorClass != null ) {
                    OWLClass ancestor = f.getOWLClass( ancestorClass );

                    //System.out.println( "Crating class " + klass + " child of " + parent + " with code " + con );
                    String frag = ancestor.getIRI().getFragment();

                    OWLNamedIndividual kls = createConceptMediatedDefinition( klass,
                                                     isWellKnownClass( frag ) ? ancestor : f.getOWLClass( ACT ),
                                                     f.getOWLObjectProperty( isWellKnownClass( frag ) ? CLASS_CODE : MOOD_CODE ),
                                                     con,
                                                     ontology,
                                                     f );

                    if ( parentClass != null ) {
                        addSubClassOf( klass.getIRI(), parentClass, ontology, f );
                    }

                    if ( ! isWellKnownClass( frag ) ) {
                        addSubClassOf( klass.getIRI(), MOODED, ontology, f );
                    }

                    context.addClassCode( klass.getIRI(), con.getIRI().toString() );
                } else {
                    System.err.println( "Unable to created concept mediated definition, parent not found. Child with no parent = " + cid );
                }

            }
        }
    }

    private IRI disambiguate( IRI iri, String concept, RIMContext context ) {
        if ( context.getClassToConceptMap().containsKey( iri ) ) {
            if ( ! context.getClassToConceptMap().get( iri ).equals( concept ) ) {
                IRI alter = IRI.create( iri.toString() + "_" );
                return disambiguate( alter, concept, context );
            }
        }
        return iri;
    }

    private String getAncestor( String cid, RIMContext context ) {
        String parent = cid;
        do {
            Set<String> sup = getParent( parent, context );
            if ( sup != null ) {
                parent = sup.iterator().next();
            } else {
                return parent;
            }
        } while ( parent != null );
        return null;
    }
    private Set<String> getParent( String cid, RIMContext context ) {
        if ( context.getChildOfRelationships().containsKey( cid ) ) {
            Set<String> classIri = context.getChildOfRelationships().get( cid );
            return classIri;
        }
        return null;
    }

    public boolean isStateful() {
        return true;
    }

    @Override
    public CSVParser getParser( BufferedReader rdr ) throws IOException {
        CSVParser parser = new CSVParser( rdr, CSVFormat.TDF.withHeader( ID, PRO, VAL, CSID ) );
        return parser;
    }


    private static class Arc {
        public IRI code;

        public IRI type;

        public IRI domain;
        public IRI range;

        public IRI className;
        public Set<String> parents = new HashSet<String>(  );

        public String label;
        public String inbound;
        public String outbound;
        public String link;

        public boolean abstrakt;

        public void ensureType( IRI typ ) {
            if ( type != null && ! type.equals( typ ) ) {
                throw new IllegalStateException( "Link with inconsistent types detected! "  + this );
            }
            type = typ;
        }

        @Override
        public String toString() {
            return "Arc{" +
                   "code=" + code +
                   ", type=" + type +
                   ", domain=" + domain +
                   ", range=" + range +
                   ", className=" + className +
                   ", parents=" + parents +
                   ", label='" + label + '\'' +
                   ", inbound='" + inbound + '\'' +
                   ", outbound='" + outbound + '\'' +
                   ", link='" + link + '\'' +
                   '}';
        }

        public void validate() {
        }
    }

}

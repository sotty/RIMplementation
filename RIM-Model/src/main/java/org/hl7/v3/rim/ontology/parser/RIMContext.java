package org.hl7.v3.rim.ontology.parser;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RIMContext {

    private OWLOntology ontology;

    private Map<IRI,IRI> ancestors = new HashMap<IRI,IRI>();
    private Map<IRI,IRI> domains = new HashMap<IRI,IRI>();
    private Map<String,String> codeSystemDomains = new HashMap<String,String>();
    private Map<String,String> codeSystems = new HashMap<String,String>();
    private Map<String,OWLNamedIndividual> classConcepts = new HashMap<String,OWLNamedIndividual>();

    private Map<IRI,String> classToConcept = new HashMap<IRI,String>();
    private Map<String,IRI> conceptToClass = new HashMap<String,IRI>();

    private Map<String,String> domainBindings = new HashMap<String,String>();


    private Map<String,OWLNamedIndividual> concepts = new HashMap<String,OWLNamedIndividual>();
    private Map<String,Set<String>> childOf = new HashMap<String,Set<String>>();
    private Set<IRI> abstracts = new HashSet<IRI>();


    private Map<String,OWLNamedIndividual> valueSets = new HashMap<String,OWLNamedIndividual>();

    // for disambiguation purposes
    private Set<String> usedNames = new HashSet<String>();

    public RIMContext( OWLOntology ontology ) {
        this.ontology = ontology;
    }

    public OWLOntology getOntology() {
        return ontology;
    }

    public Map<IRI,IRI> getAncestors() {
        return ancestors;
    }

    public Map<String,OWLNamedIndividual> getClassConcepts() {
        return classConcepts;
    }

    public void addAncestor( OWLClass s, OWLClass t ) {
        if ( ! ancestors.containsKey( s.getIRI() ) ) {
            ancestors.put( s.getIRI(), t.getIRI() );
        } else {
            throw new IllegalStateException( "Child with more than 1 parent ! " + s + " " + t );
        }
    }

    public void addClassConcept( String cid, OWLNamedIndividual con ) {
        classConcepts.put( cid, con );
    }
    public void addClassCode( IRI classIRI, String code ) {
        classToConcept.put( classIRI, code );
        conceptToClass.put( code, classIRI );
    }

    public Map<IRI,String> getClassToConceptMap() {
        return classToConcept;
    }
    public IRI getClassForConcept( String code ) {
        return conceptToClass.get( code );
    }

    public void addAttributeDomain( IRI kls, IRI attrib ) {
        domains.put( attrib, kls );
    }

    public boolean isInDomain( IRI attrib, IRI klass ) {
        return domains.containsKey( attrib ) && domains.get( attrib ).equals( klass );
    }

    public void addConcept( String code, OWLNamedIndividual concept ) {
        if ( concepts.containsKey( code ) ) {
            throw new IllegalStateException( "Duplicate concept " + code );
        }
        if ( concepts.containsValue( concept ) ) {
            throw new IllegalStateException( "Duplicate concept " + concept );
        }
        concepts.put( code, concept );

    }

    public Map<String, OWLNamedIndividual> getConcepts() {
        return concepts;
    }

    public void addValueSet( String code, OWLNamedIndividual concept ) {
        if ( valueSets.containsKey( code ) ) {
            throw new IllegalStateException( "Duplicate concept " + code );
        }
        if ( valueSets.containsValue( concept ) ) {
            throw new IllegalStateException( "Duplicate concept " + concept );
        }
        valueSets.put( code, concept );

    }

    public Map<String, OWLNamedIndividual> getValueSets() {
        return valueSets;
    }


    public Map<String, Set<String>> getChildOfRelationships() {
        return childOf;
    }

    public void addParent( String parent, String child ) {
//        if ( parents.containsKey( sid ) ) {
//            throw new IllegalStateException( "Multiple inheritance concept!" + sid );
//        }
        addChild( child, parent );
    }

    public void addChild( String child, String parent ) {
//        if ( parents.containsKey( sid ) ) {
//            throw new IllegalStateException( "Multiple inheritance concept!" + sid );
//        }
        if ( ! childOf.containsKey( child ) ) {
            childOf.put( child, new HashSet<String>() );
        }
        childOf.get( child ).add( parent );
    }

    public void addAbstract( IRI iri ) {
        abstracts.add( iri );
    }

    public boolean isAbstract( IRI iri ) {
        return abstracts.contains( iri );
    }

    public void addCodeSystemDomain( String name, String oid ) {
        if ( this.codeSystemDomains.containsKey( name ) && ! oid.equals( this.codeSystemDomains.get( name ) ) ) {
            throw new IllegalStateException( "Duplicate domain found " + name );
        }
        this.codeSystemDomains.put( name, oid );
    }

    public String getOIDForCodeSystemDomain( String name ) {
        return this.codeSystemDomains.get( name );
    }

    public void addCodeSystem( String name, String oid ) {
        if ( this.codeSystems.containsKey( name ) && ! oid.equals( this.codeSystems.get( name ) ) ) {
            throw new IllegalStateException( "Duplicate codeSystem found " + name );
        }
        this.codeSystems.put( name, oid );
    }

    public String getOIDForCodeSystem( String name ) {
        return this.codeSystems.get( name );
    }

    public void bind( String domain, String vsId ) {
        domainBindings.put( domain, vsId );
    }

    public String getDomainForValueSet( String vsId ) {
        for ( Map.Entry<String,String> e : domainBindings.entrySet() ) {
            if ( e.getValue().equals( vsId ) ) {
                return e.getKey();
            }
        }
        return null;
    }

    public String getValueSetBoundToDomain( String domain ) {
        return domainBindings.get( domain );
    }

    public Set<String> getUsedNames() {
        return usedNames;
    }
}

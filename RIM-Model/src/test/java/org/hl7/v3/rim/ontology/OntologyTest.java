package org.hl7.v3.rim.ontology;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectHasValue;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLObjectSomeValuesFrom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class OntologyTest {


    private static AxiomTester tester;

    @BeforeClass
    public static void loadOntology() throws OWLOntologyCreationException, FileNotFoundException {
        RIMOntology ontology = new RIMOntology();
        tester = ontology.getTester();
        assertNotNull( tester );
    }


    @Test
    public void testReifiedRole() {
        tester.checkPropertyDomainRange( "rim:patient", "rim:LinkedEntity", "rim:LinkedEntity" )
                .checkPropertyDomainRange( "rim:provider_", "rim:LinkedEntity", "rim:Patient" )
                .checkPropertyDomainRange( "rim:patient_", "rim:LinkedEntity", "rim:Patient" )
                .checkSubProperty( "rim:provider_", "rim:related__" )
                .checkSubProperty( "rim:related__", "upper:scopedRole" )
                .checkSubProperty( "rim:patient_", "rim:related_" )
                .checkSubProperty( "rim:related_", "upper:playedRole" )
                .checkChain( "rim:patient", Arrays.asList( "rim:patient_", "upper:scoper" ) );
    }

    @Test
    public void testReifiedParticipation() {
        tester.checkPropertyDomainRange( "rim:location_", "rim:LinkedAct", "rim:LinkedRole" )
                .checkPropertyDomainRange( "rim:location__", "rim:LinkedAct", "rim:Location" )
                .checkSubProperty( "rim:location_", "upper:participating" )
                .checkSubProperty( "rim:location__", "upper:participant" )
                .checkChain( "rim:location_", Arrays.asList( "rim:location__", "upper:role" ) )
        ;
    }

    @Test
    public void testCodeSystemCodesPropertiesActRelationshipsInheritance() {
        OWLOntology ontology = tester.getOntology();
        for ( OWLObjectProperty prop : ontology.getObjectPropertiesInSignature( true ) ) {
            if ( prop.getIRI().toString().startsWith( RIMNames.RIM ) ) {
                Set<OWLObjectPropertyExpression> parents = prop.getSuperProperties( ontology );
                assertFalse(
                        parents.contains( ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty( RIMNames.OUTBOUND ) ) &&
                        parents.contains( ontology.getOWLOntologyManager().getOWLDataFactory().getOWLObjectProperty( RIMNames.INBOUND ) ) );
            }
        }
    }

    @Test
    public void testCodeSystemCodesPropertiesSubProperties() {
        tester.checkSubProperty( "rim:appendage", "rim:sequelTo" )
                .checkSubProperty( "rim:charge", "rim:costTracking" )
                .checkClassExists( "rim:Arrival" )
                .checkPropertyDomainRange( "rim:hasContraIndication", "rim:LinkedAct", "rim:LinkedAct" )
                .checkPropertyDomainRange( "rim:contraindication_", "rim:LinkedAct", "rim:HasContraIndication" )
                .checkChain( "rim:hasReason" , Arrays.asList( "rim:reason", "upper:target" ) );
    }

    @Test
    public void testCodeSystemCodesPropertiesSpecialization() {
        tester.checkRelationship( "voc:HL7_5.41_C_CITY", SPECIALIZES, "voc:HL7_5.41_C_PLC" );

        tester.checkClassExists( "rim:City" );
    }

    @Test
    public void testAmbiguousNames() {
        tester.checkClassExists( "rim:HealthChart" )
                .checkClassExists( "rim:HealthChart_" )
                .checkClassNotExists( "rim:HealthChart__" );
    }

    @Test
    public void testCodeSystemCodesPropertiesNameAndDescription() {
        tester.checkAnnotation( "voc:HL7_5.16_C_SAL", PREF_LABEL, "street address line" )
                .checkAnnotation( "voc:HL7_5.16_C_SAL", INTERNAL_ID, "14822" )
                .checkAnnotation( "voc:HL7_5.16_C_SAL", DESCRIPTION,
                                  "<p>Description: A street address line is frequently used instead of breaking out build number, street name, " +
                                  "street type, etc. An address generally has only a delivery address line or a street address line, but not both.</p>" );

        tester.checkAnnotation( "voc:HL7_5.104_C_1180-9", PREF_LABEL, "Coos" )
                .checkAnnotation( "voc:HL7_5.104_C_1180-9", INTERNAL_ID, "15052" );

    }

    @Test
    public void testCodeSystemCodesRelationships() {
        tester.checkRelationship( "voc:HL7_5.1124_C_DK", SPECIALIZES, "voc:HL7_5.1124_C_AffiliateRealms" )
                .checkRelationship( "voc:HL7_5.1124_C_DK", SPECIALIZES, "voc:HL7_5.1124_C_AffiliateRealms" )
                .checkRelationship( "voc:HL7_5.1124_C_DK", SPECIALIZES, "voc:HL7_5.1124_C_AffiliateRealms" )
                .checkRelationship( "voc:HL7_5.1060_C_BED", SPECIALIZES, "voc:HL7_5.1060_C__PlaceEntityType" );

        tester.checkRelationship( "voc:HL7_5.16_C_ADL", COMPONENT_OF, "voc:HL7_5.16_C_AL" );
    }

    @Test
    public void testCodeSystemCodesMappings() {
        tester.checkType( "voc:HL7_5.4_C__ActAccountCode", CONCEPT )
                .checkRelationship( "voc:HL7_5.4_C__ActAccountCode", IN_SCHEME, "voc:HL7_5.4" )
                .checkAnnotation( "voc:HL7_5.4_C__ActAccountCode", ABSTRACT, "true" )
                .checkType( "voc:HL7_5.6_C_ACCT", CONCEPT )
                .checkRelationship( "voc:HL7_5.6_C_ACCT", IN_SCHEME, "voc:HL7_5.6" )
                .checkRelationship( "voc:HL7_5.4_C__ActAccountCode", SPECIALIZES, "voc:HL7_5.6_C_ACCT" );
    }

    @Test
    public void testCodeSystemCodesConcepts() {
        tester.checkType( "voc:HL7_5.4_C_RAT", CONCEPT )
                .checkAnnotation( "voc:HL7_5.4_C_RAT", PREF_LABEL, "rationale" )
                .checkRelationship( "voc:HL7_5.4_C_RAT", SPECIALIZES, "voc:HL7_5.4_C_RAT" )
                .checkRelationship( "voc:HL7_5.4_C_RAT", IN_SCHEME, "voc:HL7_5.4" )
                .checkValue( "voc:HL7_5.4_C_RAT", "skos:notation", "RAT" );

        tester.checkType( "voc:HL7_6.101_C_101YM0800X", CONCEPT )
                .checkAnnotation( "voc:HL7_6.101_C_101YM0800X", PREF_LABEL, "Mental Health" )
                .checkRelationship( "voc:HL7_6.101_C_101YM0800X", SPECIALIZES, "voc:HL7_6.101_C_101YM0800X" )
                .checkRelationship( "voc:HL7_6.101_C_101YM0800X", IN_SCHEME, "voc:HL7_6.101" )
                .checkValue( "voc:HL7_6.101_C_101YM0800X", NOTATION, "101YM0800X" );

        tester.checkType( "voc:HL7_5.4_C__ActAccountCode", CONCEPT )
                .checkAnnotation( "voc:HL7_5.4_C__ActAccountCode", PREF_LABEL, "ActAccountCode" )
                .checkAnnotation( "voc:HL7_5.4_C__ActAccountCode", ABSTRACT, "true" );
    }

    @Test
    public void testValueSets() {
        tester.checkType( "voc:HL7_1.11.8_V_AcknowledgementType", VALUESET )
            .checkAnnotation( "voc:HL7_1.11.8_V_AcknowledgementType", PREF_LABEL, "AcknowledgementType" )
            .checkAnnotation( "voc:HL7_1.11.8_V_AcknowledgementType", INTERNAL_ID, "2.16.840.1.113883.1.11.8" );
    }

    @Test
    public void testContainedClassesAttributes() {
        tester.checkAttribute( "rim:Observation", "rim:value", null, "dt:ANY" )
            .checkAttribute( "rim:Document", "rim:bibliographicDesignationText", null, "dt:ED" );
    }

    @Test
    public void testContainedClassesMappingsClassDefinitions() {
        tester.checkConceptCodeDefinition( "rim:Act", CLASS_CODE, "voc:HL7_5.6_C_ACT" )
                .checkConceptCodeDefinition( "rim:Document", CLASS_CODE, "voc:HL7_5.6_C_DOC" )
                .checkConceptCodeDefinition( "rim:Observation", CLASS_CODE, "voc:HL7_5.6_C_OBS" )
                .checkConceptCodeDefinition( "rim:SubstanceAdministration", CLASS_CODE, "voc:HL7_5.6_C_SBADM" );
    }

    @Test
    public void testContainedClassesConcepts() {
        tester.checkClassExists( "upper:AbstractAct" )
                .checkClassExists( "upper:AbstractRole" )
                .checkClassExists( "rim:Role" )
                .checkClassExists( "rim:SubstanceAdministration" )
                .checkClassExists( "rim:Place" )
        ;
    }

    @Test
    public void testDomainRanges() {
        tester.checkPropertyDomainRange( "upper:playedRole", "rim:LinkedEntity", "rim:Role" );
    }

    @Test
    public void testContainedClassesConceptsAbstract() {
        tester.checkAnnotation( "rim:ActHeir", ABSTRACT, "true" );
    }

    @Test
    public void testContainedClassPropertiesDescription() {
        tester.checkAnnotation( "rim:QueryAck", DESCRIPTION, "<p>A response to a query.</p>" );
    }

    @Test
    public void testContainedClassRelationshipsSubclass() {
        tester.checkSubClass( "rim:ControlAct", "rim:Act" )
                .checkSubClass("rim:SubstanceAdministration", "rim:Procedure" );
    }


}


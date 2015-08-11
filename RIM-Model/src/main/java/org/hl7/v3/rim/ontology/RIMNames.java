package org.hl7.v3.rim.ontology;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class RIMNames {

    public final static Pattern URN_PATTERN = Pattern.compile(
            "^urn:[a-z0-9][a-z0-9-]{0,31}:([a-z0-9()+,\\-.:=@;$_!*']|%[0-9a-f]{2})+$",
            Pattern.CASE_INSENSITIVE);

    public static final String RIM_ONTO = "http://org.hl7.v3/rim/core#";
    public static final String RMV_ONTO = "urn:hl7-org:rim-voc:";
    public static final String SKOS_ONTO = "http://edu.mayo.cts2/terms-metamodel/skos/core#";

    public static final String RIM_ALL = "http://org.hl7.v3/rim#";
    public static final String RIM = "http://org.hl7.v3/rim#";
    public static final String EXT = "http://org.hl7.v3/rim/ext#";
    public static final String CLS = "http://org.hl7.v3/rim/classes#";
    public static final String UPP = "http://org.hl7.v3/rim/upper#";
    public static final String VOC = "urn:hl7-org:voc:";
    public static final String UPV = "urn:hl7-org:upp-voc:";
    public static final String LMM = "http://edu.mayo.cts2/terms-metamodel/LMM_L1.owl#";
    public static final String REL = "http://org.hl7.v3/rim/types/rel#";
    public static final String DAT = "urn:hl7-org:cdsdt:r2#";

    public static final String SKOS = "http://www.w3.org/2004/02/skos/core#";
    public static final String CTS2 = "http://edu.mayo.cts2/terms-metamodel/omg/spec/CTS2#";
    public static final String TRMS = "http://edu.mayo.cts2/terms-metamodel/terms#";

    public static final String DC = "http://purl.org/dc/terms/";
    public static final String COD = "http://org.hl7.v3/rim/types/res#";


    public static final String RIM_BASE_VOC = "2.16.840.1.113883.5.6";
    public static final String HL7 = "2.16.840.1.113883";
    public static final String ISO = "1.0.3166.1";
    public static final String HL7_BASE = HL7 + ".";
    public static final String ISO_BASE = ISO + ".";


    public static final IRI CONCEPT_SCHEME = qnameToIRI( SKOS, "ConceptScheme" );
    public static final IRI CONCEPT = qnameToIRI( SKOS, "Concept" );
    public static final IRI VALUESET = qnameToIRI( CTS2, "ValueSet" );

    public static final IRI ACT = qnameToIRI( RIMNames.RIM, "Act" );
    public static final IRI MOODED = qnameToIRI( RIMNames.UPP, "MoodedAct" );

    public static final IRI ACT_REL = qnameToIRI( RIMNames.RIM, "ActRelationship" );
    public static final IRI ROLE = qnameToIRI( RIMNames.RIM, "Role" );
    public static final IRI ENTITY = qnameToIRI( RIMNames.RIM, "Entity" );
    public static final IRI LINK = qnameToIRI( RIMNames.RIM, "RoleLink" );
    public static final IRI PART = qnameToIRI( RIMNames.RIM, "Participation" );


    public static final IRI NOTATION = qnameToIRI( RIMNames.SKOS, "notation" );
    public static final IRI IN_SCHEME = qnameToIRI( RIMNames.SKOS, "inScheme" );
    public static final IRI INTERNAL_ID = qnameToIRI( RIM, "internalId" );
    public static final IRI OID = IRI.create( "https://www.hl7.org/oid" );
    public static final IRI PREF_LABEL = qnameToIRI( SKOS, "prefLabel" );
    public static final IRI SPECIALIZES = qnameToIRI( VOC, "specializes" );
    public static final IRI EXPRESSES = qnameToIRI( TRMS, "expresses" );
    public static final IRI ABSTRACT = qnameToIRI( RIM, "abstract" );
    public static final IRI DESCRIPTION = qnameToIRI( DC, "description" );
    public static final IRI COMPONENT_OF = qnameToIRI( VOC, "componentOf" );
    public static final IRI CLASS_CODE = qnameToIRI( UPP, "classCode" );
    public static final IRI MOOD_CODE = qnameToIRI( UPP, "moodCode" );
    public static final IRI CODE = qnameToIRI( UPP, "code" );
    public static final IRI TYPECODE = qnameToIRI( UPP, "typeCode" );
    public static final IRI DRAWSFROM = qnameToIRI( TRMS, "drawsFrom" );
    public static final IRI MEMBER = qnameToIRI( SKOS, "member" );
    public static final IRI MEMBER_OF = qnameToIRI( SKOS, "memberOf" );

    public static final IRI BASE_PART = qnameToIRI( UPP, "participating" );
    public static final IRI ROL_PART = qnameToIRI( UPP, "participates" );
    public static final IRI ACT_PART = qnameToIRI( UPP, "participant" );
    public static final IRI PART_TO_ACT = qnameToIRI( UPP, "act" );
    public static final IRI PART_TO_ROLE = qnameToIRI( UPP, "role" );

    public static final IRI BASE_AR = qnameToIRI( UPP, "actRelationship" );
    public static final IRI INBOUND = qnameToIRI( UPP, "inboundRelationship" );
    public static final IRI OUTBOUND = qnameToIRI( UPP, "outboundRelationship" );
    public static final IRI TARGET = qnameToIRI( UPP, "target" );
    public static final IRI SOURCE = qnameToIRI( UPP, "source" );

    public static final IRI BASE_ROL = qnameToIRI( UPP, "relatedEntity" );
    public static final IRI PLAYER = qnameToIRI( UPP, "player" );
    public static final IRI SCOPER = qnameToIRI( UPP, "scoper" );
    public static final IRI SCOPED_ROLE = qnameToIRI( UPP, "scopedRole" );
    public static final IRI PLAYED_ROLE = qnameToIRI( UPP, "playedRole" );

    public static final IRI LINKED_ACT = qnameToIRI( RIM, "LinkedAct" );
    public static final IRI LINKED_ROLE = qnameToIRI( RIM, "LinkedRole" );
    public static final IRI LINKED_ACTREL = qnameToIRI( RIM, "LinkedActRelationship" );
    public static final IRI LINKED_PART = qnameToIRI( RIM, "LinkedParticipation" );
    public static final IRI LINKED_ENTITY = qnameToIRI( RIM, "LinkedEntity" );
    public static final IRI LINKED_LINK = qnameToIRI( RIM, "LinkedRoleLink" );


    private static final String SEPARATOR_CHAR = "_";
    private static final String V = "_V";
    private static final String C = "_C";


    public static String schemeToFragment( String sch ) {
        if ( sch.startsWith( HL7_BASE  ) ) {
            return sch.replace( HL7_BASE, "HL7" + SEPARATOR_CHAR );
        } else if ( sch.startsWith( ISO_BASE ) ) {
            return sch.replace( ISO_BASE, "ISO" + SEPARATOR_CHAR );
        } else if ( sch.equals( ISO ) ) {
            return "ISO";
        } else {
            return sch;
        }
    }

    public static IRI getConceptIRI( String code, String csid ) {
        String ns = csid;
        if ( ns.endsWith( "Class" ) ) {
            ns = ns.replace( "Class", "" );
        }
        if ( ns.indexOf( "." ) >= 0 ) {
            ns = schemeToFragment( ns );
        }

        code = interceptIllegalCode( code );

        String id = ns + C + SEPARATOR_CHAR + sanitize( code );
        return qnameToIRI( VOC, id );
    }

    public static IRI getValuesetIRI( String code, String vsid ) {
        String ns = vsid;
        if ( ns.indexOf( "." ) >= 0 ) {
            ns = schemeToFragment( ns );
        }

        code = interceptIllegalCode( code );

        String id = ns + V + SEPARATOR_CHAR + sanitize( code );
        return qnameToIRI( VOC, id );
    }

    public static String formatClassName( String name ) {
        return upperCase( sanitize( name ) );
    }

    public static IRI getConceptSchemeIRI( String sch ) {
        sch = schemeToFragment( sch );
        IRI iri = IRI.create( VOC + sch );
        return iri;
    }

    public static String getRelationshipName( String baseName, IRI kind ) {
        if ( baseName.startsWith( "_" ) ) {
            baseName = baseName.substring( 1 );
        }
        if ( kind == null || kind.equals( ACT_REL ) ) {
            if ( baseName.startsWith( "ActClass" ) ) {
                baseName = baseName.substring( "ActClass".length() );
            }
            if ( baseName.startsWith( "ActRelationship" ) ) {
                baseName = "ar" + baseName.substring( "ActRelationship".length() );
            }
        } else if ( kind.equals( ROLE ) ) {
            if ( baseName.contains( "|" ) ) {
                if ( baseName.startsWith( "(" ) ) {
                    int stop = Math.max( baseName.lastIndexOf( '|' ), baseName.lastIndexOf( ')' ) );
                    baseName = baseName.substring( stop + 1 );
                } else if ( baseName.endsWith( ")" ) ) {
                    int stop = Math.min( baseName.lastIndexOf( '|' ), baseName.lastIndexOf( '(' ) );
                    baseName = baseName.substring( 0, stop );
                } else {
                    throw new IllegalStateException( "Can't understand this" + baseName );
                }
            }
        }
        String name = camelCase( baseName );
        return name;
    }

    public static IRI qnameToIRI( String ns, String loc ) {
        return IRI.create( ns, loc );
    }


    private static String interceptIllegalCode( String code ) {
        if ( ">".equals( code ) || "&gt;".equals( code ) ) {
            return "__GT";
        } else if ( "<".equals( code ) || "&lt;".equals( code ) ) {
            return "__LT";
        }
        return code;
    }

    public static IRI getOntologyIRI( String name ) {
        String parentName = name;
        if ( parentName.endsWith( "#" )
             // || parentName.endsWith( "/" )
             || parentName.endsWith( ":" )
                ) {
            parentName = parentName.substring( 0, parentName.length() - 1 );
        }
        return IRI.create( parentName );
    }

    protected static String codeToConceptName( String code ) {
        String s = sanitize( code );
        if ( Character.isDigit( s.charAt( 0 ) ) ) {
            s = "_" + s;
        }
        return s;
    }

    protected static String sanitize( String s ) {
        s = s.replaceAll( "\'", "" );
        s = s.replaceAll( "&amp;", "" );
        if ( s.contains( "|" ) ) {
            System.out.println( "Dash found " + s );
        }
        s = s.replaceAll( "\\|", "" );
        s = s.replaceAll( ";", "_" );
        s = s.replaceAll( "\\(", "" );
        s = s.replaceAll( "\\)", "" );
        s = s.replaceAll( "\\.", "_" );
        s = s.replaceAll( ",", "_" );
        s = s.replaceAll( "/", "_" );
        s = s.replaceAll( "\\%", "p" );
        s = s.replaceAll( "&", "And" );
        s = s.replaceAll( " ", "_" );
        s = s.replaceAll( "=", "_" );
        s = s.replaceAll( "#", "_" );
        s = s.replaceAll( "\\[", "_" );
        s = s.replaceAll( "\\]", "_" );
        s = s.replaceAll( ":", "_" );
        s = s.replaceAll( ">", "_GT" );
        s = s.replaceAll( "<", "_LT" );
        s = s.replaceAll( "\\+", "_" );
        return s;
    }

    public static String upperCase( String p ) {
        return upperCase( p, true );
    }

    public static String camelCase( String p ) {
        return upperCase( p, false );
    }

    protected static String upperCase( String printName, boolean flag ) {
        printName = sanitize( printName );
        boolean first = true;

        StringTokenizer tok = new StringTokenizer( printName, " -_" );
        StringBuilder builder = new StringBuilder();
        while ( tok.hasMoreTokens() ) {
            if ( first ) {
                String s = tok.nextToken();
                builder.append( flag ? toUpperCase( s ) : toLowerCase( s ) );
                first = false;
            } else {
                builder.append( toUpperCase( tok.nextToken() ) );
            }
        }
        String s = builder.toString();
        if ( Character.isDigit( s.charAt( 0 ) ) ) {
            s = "_" + s;
        }
        return s;
    }

    protected static String toUpperCase( String s ) {
        return s.substring( 0, 1 ).toUpperCase() + s.substring( 1 );
    }
    protected static String toLowerCase( String s ) {
        return s.substring( 0, 1 ).toLowerCase() + s.substring( 1 );
    }



    public static void fillPrefixes( PrefixManager pm ) {
        //FIXME : The PrefixManager interface is read-only
        if ( pm instanceof DefaultPrefixManager ) {
            DefaultPrefixManager pfm = (DefaultPrefixManager) pm;
            pfm.setPrefix( "dt:", RIMNames.DAT );
            pfm.setPrefix( "rim:", RIMNames.RIM );
            pfm.setPrefix( "upper:", RIMNames.UPP );
            pfm.setPrefix( "cls:", RIMNames.CLS );
            pfm.setPrefix( "ext:", RIMNames.EXT );
            pfm.setPrefix( "voc:", RIMNames.VOC );
            pfm.setPrefix( "upv:", RIMNames.UPV );
            pfm.setPrefix( "skos:", RIMNames.SKOS );
            pfm.setPrefix( "dct:", RIMNames.DC );
            pfm.setPrefix( "cod:", RIMNames.COD );
            pfm.setPrefix( "rel:", RIMNames.REL );
            pfm.setPrefix( "cts2:", RIMNames.CTS2 );
            pfm.setPrefix( "trms:", RIMNames.TRMS );
        } else if ( pm instanceof PrefixOWLOntologyFormat ) {
            PrefixOWLOntologyFormat pfm = (PrefixOWLOntologyFormat) pm;
            pfm.setPrefix( "dt:", RIMNames.DAT );
            pfm.setPrefix( "rim:", RIMNames.RIM );
            pfm.setPrefix( "upper:", RIMNames.UPP );
            pfm.setPrefix( "cls:", RIMNames.CLS );
            pfm.setPrefix( "ext:", RIMNames.EXT );
            pfm.setPrefix( "voc:", RIMNames.VOC );
            pfm.setPrefix( "upv:", RIMNames.UPV );
            pfm.setPrefix( "skos:", RIMNames.SKOS );
            pfm.setPrefix( "dct:", RIMNames.DC );
            pfm.setPrefix( "cod:", RIMNames.COD );
            pfm.setPrefix( "rel:", RIMNames.REL );
            pfm.setPrefix( "cts2:", RIMNames.CTS2 );
            pfm.setPrefix( "trms:", RIMNames.TRMS );
        }
    }

    public static IRI roleLink( String link ) {
        return IRI.create( RIM, link );
    }

    public static IRI participationLink( String link ) {
        return IRI.create( RIM, link );
    }
}

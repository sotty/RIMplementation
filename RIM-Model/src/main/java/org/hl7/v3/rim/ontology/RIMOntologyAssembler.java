package org.hl7.v3.rim.ontology;

import org.coode.owlapi.owlxml.renderer.OWLXMLOntologyStorer;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLXMLOntologyFormat;
import org.semanticweb.owlapi.model.AddImport;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLOntologyStorer;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hl7.v3.rim.ontology.RIMNames.*;

public class RIMOntologyAssembler {

    public static String rootPath;

    private OWLOntology ontology;


    public static Map<String,String> knownFiles = new HashMap<String,String>();
    public static Map<String,List<String>> dependencies = new HashMap<String,List<String>>();

    public static Map<IRI,String> outputs = new HashMap<IRI,String>();

    static {
        knownFiles.put( LMM, "/edu/mayo/cts2/terms/lmm_l1.owl" );
        knownFiles.put( SKOS_ONTO, "/edu/mayo/cts2/terms/skos.owl" );
        knownFiles.put( TRMS, "/edu/mayo/cts2/terms/terms.owl" );
        knownFiles.put( CTS2, "/edu/mayo/cts2/terms/cts2.owl" );

        knownFiles.put( CLS, "/owl/rim-classes.owl" );
        knownFiles.put( DAT, "/owl/datatype.owl" );
        knownFiles.put( UPP, "/owl/rim-upper.owl" );
        knownFiles.put( UPV, "/owl/rim-upper-voc.owl" );
        knownFiles.put( RIM, "/owl/rim-core.owl" );
        knownFiles.put( COD, "/owl/rim-types.owl" );
        knownFiles.put( REL, "/owl/rim-actRels.owl" );
        knownFiles.put( RIM_ONTO, "/owl/rim_core.owl" );
        knownFiles.put( RIM_ALL, "/owl/rim_all.owl" );
        knownFiles.put( RMV_ONTO, "/owl/rim_voc.owl" );

        dependencies.put( RIM_ALL, Arrays.asList( DAT, LMM, SKOS_ONTO, CTS2, TRMS, UPV, RMV_ONTO, UPP, RIM_ONTO ) );
        dependencies.put( RIM_ONTO, Arrays.asList( DAT, UPP ) );
        dependencies.put( RMV_ONTO, Arrays.asList( LMM, SKOS_ONTO, CTS2, TRMS, UPV ) );

        outputs.put( getOntologyIRI( RIM_ONTO ), knownFiles.get( RIM_ONTO ) );
        outputs.put( getOntologyIRI( RMV_ONTO ), knownFiles.get( RMV_ONTO ) );
    }


    public static void setRootPath(String path) {
        rootPath = path;
        for (String key: knownFiles.keySet()) {
            String value = rootPath + knownFiles.get(key);
            knownFiles.put(key, value);
        }
    }

    public static OWLOntology loadOntology() throws OWLOntologyCreationException, FileNotFoundException {
        OWLOntology ontology;
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();

        for ( String ancestor : dependencies.get( RIM_ALL ) ) {
            loadOntologyPiece( knownFiles.get( ancestor ), manager );

        }
        ontology = loadOntologyPiece( knownFiles.get( RIM_ALL ), manager );
        return ontology;
    }

    private static OWLOntology loadOntologyPiece( String file, OWLOntologyManager manager ) throws OWLOntologyCreationException, FileNotFoundException {
        System.err.println( "Loooking for  " + file );
        URL res = RIMOntologyAssembler.class.getResource( file );

        try {
            return manager.loadOntologyFromOntologyDocument(
                        res.openStream()
                    );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return null;
    }


    protected OWLOntology initOntology( String NS, String PARENT_NS, OWLOntologyManager manager ) throws OWLOntologyCreationException, FileNotFoundException {
        OWLOntology ontology;
        OWLDataFactory factory = manager.getOWLDataFactory();
        ontology = manager.createOntology( new OWLOntologyID(
                getOntologyIRI( NS ),
                IRI.create( getOntologyIRI( NS ).toString() + "/1.0" ) ) );

        for ( String ancestor : dependencies.get( PARENT_NS ) ) {
            if ( manager.getOntology( getOntologyIRI( ancestor ) ) == null ) {
                loadOntologyPiece( knownFiles.get( ancestor ), manager );
            }
        }

        if ( ! PARENT_NS.equals( NS ) ) {
            manager.loadOntologyFromOntologyDocument( new FileInputStream( new File( knownFiles.get( PARENT_NS ) ) ) );
            manager.applyChange( new AddImport( ontology, factory.getOWLImportsDeclaration( getOntologyIRI( PARENT_NS ) ) ) );
        } else {
            for ( String ancestor : dependencies.get( PARENT_NS ) ) {
                if ( ( ! isBasic( ancestor ) ) ) {
                    manager.applyChange( new AddImport( ontology, factory.getOWLImportsDeclaration( getOntologyIRI( ancestor ) ) ) );
                }
            }
        }
        return ontology;
    }

    private static boolean isBasic( String ancestor ) {
        return SKOS_ONTO.equals( ancestor )
                || LMM.equals( ancestor )
                || CTS2.equals( ancestor )
                || TRMS.equals( ancestor )
                || DAT.equals( ancestor );
    }


    protected void save( OWLOntology ontology, IRI target ) throws IOException, OWLOntologyStorageException {
        OWLOntologyStorer storer = new OWLXMLOntologyStorer();
        PrefixOWLOntologyFormat pfm = new OWLXMLOntologyFormat();
        fillPrefixes( pfm );
        storer.storeOntology( ontology, target, pfm );
        System.out.println( "Done saving ontology " + target );
    }



    public void streamOut( OWLOntology onto, String path ) {
        try {
            save( onto, IRI.create( new File( path ) ) );
        } catch ( IOException e ) {
            e.printStackTrace();
        } catch ( OWLOntologyStorageException e ) {
            e.printStackTrace();
        }
    }



    public RIMOntologyAssembler() {
        try {
            try {
                OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
                initOntology( RMV_ONTO, RMV_ONTO, manager );
                ontology = initOntology( RIM_ONTO, RIM_ONTO, manager );
            } catch ( FileNotFoundException e ) {
                e.printStackTrace();
            }
        } catch ( OWLOntologyCreationException e ) {
            e.printStackTrace();
        }
    }

    public void save( String path ) {
        for ( IRI outIri : outputs.keySet() ) {
            streamOut( ontology.getOWLOntologyManager().getOntology( outIri ),
                       getFile( outIri, path ) );
        }
    }

    private String getFile( IRI ontologyIRI, String path ) {
        return path + outputs.get( ontologyIRI );
    }

    public OWLOntology getOntology() {
        return ontology;
    }
}

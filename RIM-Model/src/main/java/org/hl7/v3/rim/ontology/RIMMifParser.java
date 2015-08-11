package org.hl7.v3.rim.ontology;

import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVParser;
import org.coode.owlapi.functionalrenderer.OWLFunctionalSyntaxOntologyStorer;
import org.hl7.v3.rim.ontology.parser.InMemoryTransformer;
import org.hl7.v3.rim.ontology.parser.OntologyProcessor;
import org.hl7.v3.rim.ontology.parser.RIMContext;
import org.hl7.v3.rim.ontology.util.LinkedProperties;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLOntologyStorer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author m130944
 */
public class RIMMifParser {

    private String path;
	private String transformSavePath;

	private Map<String,OWLOntology> sections = new HashMap<String,OWLOntology>();

	private static Logger logger = Logger.getLogger(RIMMifParser.class.getName());

    public static void main(String args[]) {
		try {
			new RIMMifParser(args);
		} catch (Exception ex) {
			logger.log(Level.SEVERE, null, ex);
		}
    }

	private void processSection(Properties props, String sectionName, RIMContext context) {
		String sourceHeading = sectionName.replaceAll("section", "source");
		InMemoryTransformer.TransformSource src = 
				InMemoryTransformer.TransformSource.valueOf(props.getProperty(sourceHeading));
		String sectionNameSplit[] = sectionName.split("\\.");
		String sectionValues[] = props.getProperty(sectionName).split(",");
		InMemoryTransformer xformer = new InMemoryTransformer();
		for (int c = 0; c < sectionValues.length; c++) {
			StringBuilder bldr = new StringBuilder("xslt.");
			bldr.append(sectionNameSplit[1]).append(".").append(sectionValues[c]);
			String fileRootName = props.getProperty(bldr.toString());
			String resourceName = "/xslt/"+fileRootName;
			String outFileName = this.transformSavePath+"/"+fileRootName.replaceAll("xslt", "out");
			try {
				BufferedReader rdr = xformer.getTransformedStream(resourceName, src, true, outFileName);
				OntologyProcessor processor = lookupProcessor( fileRootName );
				if ( processor != null ) {
					CSVParser csvParser = processor.getParser( rdr );
					processor.process( csvParser.getRecords(), context );
				}
			} catch (Exception ex) {
				logger.log(Level.SEVERE,"******* TRANSFORMATION ERROR *******");
				logger.log( Level.SEVERE, "Section: " + sectionName );
				logger.log( Level.SEVERE, "Resource: " + resourceName );
				logger.log( Level.SEVERE, null, ex );
				logger.log( Level.SEVERE, "******* END TRANSFORMER ERROR *******" );
			}
		}
	}

	private OntologyProcessor lookupProcessor( String fileRootName ) {
		String simpleName = fileRootName.substring( 0, fileRootName.lastIndexOf( '.' ) );
		String className = OntologyProcessor.class.getPackage().getName() + "." + simpleName;
		className = className.replaceAll( "-", "" );
		try {
			Class<? extends OntologyProcessor> processorClass = (Class<? extends OntologyProcessor>) Class.forName( className );
			return processorClass.newInstance();
		} catch ( ClassNotFoundException e ) {
			logger.log( Level.WARNING, "No processor found or defined for " + fileRootName );
		} catch ( InstantiationException e ) {
			logger.log( Level.SEVERE, "Unable to instantiate processor class " + className );
		} catch ( IllegalAccessException e ) {
			logger.log( Level.SEVERE, "Unable to instantiate processor class " + className );
		}
		return null;
	}

	private RIMMifParser( String args[] ) throws ParseException, IOException, URISyntaxException, OWLOntologyCreationException {
		if ( args.length > 0 ) {
			this.path = args[ 0 ];
		} else {
			throw new IllegalArgumentException( "Missing arguments.. output paths are mandatory" );
		}
		if ( args.length >= 2 ) {
			this.transformSavePath = args[ 1 ];
		}

		File mainPath = new File( this.path );
		if ( ! mainPath.exists() ) {
			mainPath.mkdirs();
		}
		File tgtPath = new File( transformSavePath );
		if ( ! tgtPath.exists() ) {
			tgtPath.mkdirs();
		}
		logger.log( Level.INFO, "Output path for OWL : " + this.path );
		logger.log( Level.INFO, "Target path for CSV : " + this.transformSavePath );

		Properties props = new LinkedProperties();
		props.load(RIMMifParser.class.getResourceAsStream("/parser.properties"));
		String defXlateValue = props.getProperty( "default.translate" );

		RIMOntologyAssembler assembler = new RIMOntologyAssembler();
		OWLOntology ontology = assembler.getOntology();

		RIMContext context = new RIMContext( ontology );
		if (defXlateValue.equalsIgnoreCase("all")) {
			for (String propName: props.stringPropertyNames()) {
				if (propName.startsWith("section")) {
					processSection(props,propName,context);
				}
			}
		}

		assembler.save( path );
	}

    
}

package org.hl7.v3.rim.ontology.parser;

import org.apache.commons.lang3.StringUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


public class InMemoryTransformer {
	private static final TransformerFactory xformFactory = TransformerFactory.newInstance();

	public enum TransformSource {
		VOCAB,
		RIM;
	}
	
	public InMemoryTransformer() {
	}

	public BufferedReader getTransformedStream(String transformerName, 
			TransformSource xformSource, boolean saveOutput, String outFileName) throws TransformerException, IOException {
		InputStream xsltStream = InMemoryTransformer.class.getResourceAsStream(transformerName);
		if (xsltStream == null) {
			throw new IllegalArgumentException("Unable to load resource: "+transformerName);
		}
		StreamSource input = null;
		switch (xformSource) {
			case VOCAB:
				input = new StreamSource(InMemoryTransformer.class.getResourceAsStream( "/mif/voc.mif2" ));
				break;
			case RIM:
				input = new StreamSource(InMemoryTransformer.class.getResourceAsStream( "/mif/rim.mif2" ));
				break;
			default:
				throw new IllegalArgumentException("Invalid or missing transformer source file designated");
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Transformer xformer = xformFactory.newTransformer(new StreamSource(xsltStream));
		StreamResult output = new StreamResult(baos);
		xformer.transform(input, output);
		byte[] bout = ((ByteArrayOutputStream)output.getOutputStream()).toByteArray();
		if (saveOutput && !StringUtils.isBlank(outFileName)) {
			FileOutputStream fos = new FileOutputStream( new File( outFileName ) );
			fos.write(bout);
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(bout);
		if ( bais.available() == 0 ) {
			throw new IllegalStateException( "Unable to do anything..." );
		}
		return new BufferedReader(new InputStreamReader(bais));
	}
	
	
}

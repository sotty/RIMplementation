package org.hl7.v3.rim.ontology.parser;

import com.sun.codemodel.JJavaName;

public class NameUtils {

   public static String pluralize( String name ) {
      if ( name.endsWith( "_" ) ) {
         int start = name.length() - 1;
         while ( name.charAt( start - 1 ) == '_' ) {
            start--;
         }
         String root = JJavaName.getPluralForm( name.substring( 0, start ) );
         String trail = name.substring( start );
         return root + trail;
      } else {
         return JJavaName.getPluralForm(name);
      }
   }

}

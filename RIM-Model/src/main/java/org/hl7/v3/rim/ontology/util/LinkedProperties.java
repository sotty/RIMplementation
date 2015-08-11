package org.hl7.v3.rim.ontology.util;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class LinkedProperties extends Properties {
    
    private Map<String,String> linkedMap = new LinkedHashMap<String,String>();

    @Override
    public synchronized Object put(Object key, Object value){
        return linkedMap.put((String)key, (String)value);
    }

    @Override
    public String getProperty( String key ) {
        return linkedMap.get( key );
    }

    @Override
    public synchronized boolean contains(Object value){
        return linkedMap.containsValue(value);
    }

    @Override
    public boolean containsValue(Object value){
        return linkedMap.containsValue(value);
    }

    @Override
    public synchronized void clear(){
        linkedMap.clear();
    }

    @Override
    public synchronized boolean containsKey(Object key){
        return linkedMap.containsKey(key);
    }

    @Override
    public Set<String> stringPropertyNames() {
        return linkedMap.keySet();
    }
}
package com.thoughtworks.xstream.mapper.newtest1;

import java.io.InputStream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.security.AnyTypePermission;


/**
 * test verify duplicated field
 */
public class TestMapper {
	
    public static void main(String[] args) throws Exception {
        XStream xs = new XStream();
    	
        xs.addPermission(AnyTypePermission.ANY);
        xs.ignoreUnknownElements();
        xs.processAnnotations(Mapper.class);
         
        InputStream is = TestMapper.class.getResourceAsStream("mapper1.xml");
        Mapper o = (Mapper)xs.fromXML(is);
        for(ResultMap rm:o.resultMaps) {
        	System.out.println(rm);
        }
    }
}

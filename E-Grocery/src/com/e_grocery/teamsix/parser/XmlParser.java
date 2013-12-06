package com.e_grocery.teamsix.parser;



import javax.xml.parsers.*;  

import org.xml.sax.InputSource;  
import org.w3c.dom.*;  

import android.util.Log;

import java.io.*;  
  
public class XmlParser  
{  
   String s_xml_data = null;  
  
    public XmlParser() {  
    }  
      
    public void setData(String s_set_data) {  
        s_xml_data = s_set_data;  
       // System.out.println("inside Bean setdata"+s_xml_data);  
    }  
      
    public String processData(String data) {  
        String s_final_data = null;  
        s_xml_data = data;
        System.out.println("isetting data");
        if (s_xml_data != null) {  
  
  
  
            try {  
            	Log.v("test",s_xml_data);
  
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
                DocumentBuilder db = dbf.newDocumentBuilder();  
                InputSource is = new InputSource();  
                is.setCharacterStream(new StringReader(s_xml_data)); 
                System.out.println(s_xml_data);
                Document doc = db.parse(is);  
                // System.out.println(doc.getTextContent());
                 Element e = (Element)doc.getElementsByTagName("itemname").item(0);  

               // System.out.println("Name: " + getCharacterDataFromElement(e));

                s_final_data =getCharacterDataFromElement(e);
                if(s_final_data==null||"".equalsIgnoreCase(s_final_data)||"?".equalsIgnoreCase(s_final_data)){
                	 e = (Element)doc.getElementsByTagName("description").item(0); 
                	 s_final_data =getCharacterDataFromElement(e);
                }
            } catch (Exception ex) {  
                System.out.println("Exception in xmlParseString ::" + ex);  
            }  
  
        }  
  
        return s_final_data;  
    }    
    
    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
           CharacterData cd = (CharacterData) child;
           return cd.getData();
        }
        return "?";
      }
}  
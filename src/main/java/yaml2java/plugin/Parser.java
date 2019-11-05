package yaml2java.plugin;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.yaml.snakeyaml.Yaml;
import yaml2java.plugin.beans.Definition;
import yaml2java.plugin.beans.Property;

import java.io.*;
import java.util.*;

public class Parser {

  private static final boolean DEBUG_MODE = false;
  //private static final String YAML_TO_PARSE = "/Users/it001366/Desktop/yaml2Java/src/main/java/yaml2java/api_iwo_wil.yaml";
  private static final String CURRENT_PATH = "src/main/java/yaml2java/";
  private ArrayList<Definition> generalObjArrayList = new ArrayList<>();
  private String outputdir;

  public Parser() {
  }

  public void startParsing(Yaml yaml,String filePath2Parse, String outputdir) throws IOException {
    this.outputdir=outputdir;
    InputStream input = new FileInputStream(new File(CURRENT_PATH+filePath2Parse));
    Object data = yaml.load(input);
    //System.out.println(data);
    this.fromJSON2Java(data);
  }

  private void fromJSON2Java(Object o) throws IOException {
    String jsonString = new Gson().toJson(o, Map.class);
    JsonObject obj = new JsonParser().parse(jsonString).getAsJsonObject();
    //System.out.println(obj.get("definitions").getAsJsonObject().keySet().toArray());

    // this array contains all the classes
    ArrayList<String> arr = new ArrayList<>();

    for (int i = 0; i < obj.get("definitions").getAsJsonObject().keySet().toArray().length; i++) {
      arr.add((String) obj.get("definitions").getAsJsonObject().keySet().toArray()[i]);
    }
    //System.out.println(arr);

    // Looking for object's fields
    // ex: CustomerExtension

    for (int i = 0; i < obj.get("definitions").getAsJsonObject().keySet().toArray().length; i++) {
      String myClass = arr.get(i);
      JsonElement el = obj.get("definitions").getAsJsonObject().get(myClass);
      createDefinitions(el, myClass);
      Generator gen = new Generator(generalObjArrayList,this.outputdir);
      gen.startGeneration();
    }
    //System.out.println(generalObjArrayList);
  }

  private void createDefinitions(JsonElement elem, String myClass) {

    JsonObject obj2 = elem.getAsJsonObject();
    Set<Map.Entry<String, JsonElement>> entries = obj2.entrySet(); // will return members of your object
    for (Map.Entry<String, JsonElement> entry : entries) {
      String type="";
      ArrayList<Property> fields = new ArrayList<>();

      if(entry.getKey().equals("type")){
        if(entry.getValue().toString().contains("object")){
          type="object";
          fields = convertProperties2List(elem);
          //System.out.println(fields);
        } else if(entry.getValue().toString().contains("string")){
          type="string";
        } else if(entry.getValue().toString().contains("number")){
          type="number";
        } else if(entry.getValue().toString().contains("array")){
          type="array";
        } else if(entry.getValue().toString().contains("boolean")){
          type="boolean";
        }
      }else if(entry.getKey().equals("$ref")){
        type="$ref";
      }
      if(type!=""){
        generalObjArrayList.add(new Definition(myClass, type, fields));
      }

      }
    }



  private ArrayList<Property> convertProperties2List(JsonElement el) {

    ArrayList<Property> fields = new ArrayList<>();

    for (int i = 0; i < el.getAsJsonObject().get("properties").getAsJsonObject().keySet().toArray().length; i++) {

      String propertyName = (String) el.getAsJsonObject().get("properties").getAsJsonObject().keySet().toArray()[i];
      HashMap<String,String> propertiesField = extractProperties(el.getAsJsonObject(),propertyName);
      String href="";
      String type="";
      int minLength=0;
      try{
        href = propertiesField.get("$ref");
      }catch (Exception e){
        if(DEBUG_MODE) System.out.println("No $ref field for property "+propertyName);
      }
      try{
        type = propertiesField.get("type");
      }catch (Exception e){
        if(DEBUG_MODE) System.out.println("No type field for property "+propertyName);
      }
      try{
        minLength = Integer.parseInt(propertiesField.get("minLength"));
      }catch (Exception e){
        if(DEBUG_MODE) System.out.println("No minLength field for property "+propertyName);
      }
      Property newProperty = new Property(propertyName,href,type,minLength);
      fields.add(newProperty);
    }
    return fields;
  }

    private HashMap<String, String> extractProperties(JsonElement el,String propertyName){

      HashMap<String, String> propAttributes = new HashMap<>();


      for (int i = 0; i < el.getAsJsonObject().get("properties").getAsJsonObject().get(propertyName).getAsJsonObject().keySet().toArray().length; i++) {

          String singleProp = el.getAsJsonObject().get("properties").getAsJsonObject().get(propertyName).getAsJsonObject().keySet().toArray()[i].toString();
          //System.out.println("\t"+singleProp);
          String singleValue = el.getAsJsonObject().get("properties").getAsJsonObject().get(propertyName).getAsJsonObject().get(singleProp).toString();
          //System.out.println("\t"+singleValue);

          if(singleProp.equals("$ref")){
            propAttributes.put(singleProp,singleValue);
          }
          if(singleProp.equals("type")){
            propAttributes.put(singleProp,singleValue);
          }
          if(singleProp.equals("minLength")){
            propAttributes.put(singleProp,singleValue);
        }

      }

      return propAttributes;
    }

  private void printType(Object o) {
    System.out.println(o.getClass().getName());
  }

}

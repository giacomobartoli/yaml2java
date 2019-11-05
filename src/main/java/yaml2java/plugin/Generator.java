package yaml2java.plugin;

import yaml2java.plugin.beans.Definition;

import java.io.*;
import java.util.ArrayList;
import java.util.function.Predicate;


public class Generator {

        private static final boolean DEBUG_MODE = false;
        private static final String OUTPUT_PATH = "output/";

        private ArrayList<Definition> classes;
        private String outputDir;

        public Generator(ArrayList<Definition> classes,String outputdir) {
                this.outputDir=outputdir;
                this.classes = classes;
        }

        public ArrayList<Definition> getClasses() {
                return classes;
        }


        public void startGeneration() throws IOException {
                System.out.println("Output directory set to: "+this.outputDir);
                System.out.println("Starting generating..");

                /*
                * RIMUOVO GLI ELEMENTI DALL'ARRAY IL CUI TYPE=="$ref"
                * */

                Predicate<Definition> condition = def -> def.getType()=="$ref";
                this.classes.removeIf(condition);

                for (int i = 0; i < this.getClasses().size(); i++) {
                        /*
                         * General Obj - String className - ArrayList fields
                         */
                        Definition myDef = this.getClasses().get(i);
                        String str = stringCodeConcat(myDef);

                        if (DEBUG_MODE) {
                                System.out.println("OUTPUT: \n" + str);
                        }

                        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                                        OUTPUT_PATH + this.getClasses().get(i).getName() + ".java"), "utf-8"))) {
                                writer.write(str);
                        }
                }

        }

        private String stringCodeConcat(Definition obj) {
                String className = obj.getName();

                // CASO IN CUI NON SIA UN OGGETTO MA SEMPLICEMENTE TYPE: STRING OPPURE NUMBER
                if(obj.getType().equals("string")) {
                        String extStr = "public class " + className + " extends String {\n\n}\n";
                        return extStr;
                }
                if(obj.getType().equals("number")) {
                        String extStr = "public class " + className + " extends Float {\n\n}\n";
                        return extStr;
                }
                if(obj.getType().equals("array")) {
                        String extStr = "public class " + className + " extends ArrayList {\n\n}\n";
                        return extStr;
                }

                String code = "public class " + className + " { \n";

                String allFields = generateFields(obj);

                //System.out.println("\nFIELDS");
                //System.out.println(allFields);

                String constructor = generateConstructor(obj);

                //System.out.println("\nCOSTRUTTORE");
                //System.out.println(constructor);

                String setters = generateSetters(obj);
                //System.out.println("\nSETTERS");
                //System.out.println(setters);

                String getters = generateGetters(obj);
                //System.out.println("\nGETTERS");
                //System.out.println(getters);

                System.out.println(code + allFields + constructor + setters + getters + "\n}");
                return code + allFields + constructor + setters + getters + "\n}";
        }

        private String generateConstructor(Definition def){

                String constructor = "\npublic " + def.getName() + "(";

                for (int i = 0; i < def.getProperties().size(); i++) {

                        String classType = checkClassType(def.getProperties().get(i).getName());
                        String paramName = "";

                        if(def.getType().equals("object")){
                                if(def.getProperties().get(i).getRef()!=null){
                                        paramName = extractFieldNameFromRef(def.getProperties().get(i).getRef(),false);
                                }else{
                                        classType=org.springframework.util.StringUtils.capitalize(def.getProperties().get(i).getType().replace("\"", ""));
                                        //classType="String";
                                        paramName=def.getProperties().get(i).getName();
                                }
                        }
                        constructor+=classType + " " + paramName;

                        if(i<def.getProperties().size()-1){
                                constructor+=",";
                        }
                }

                constructor += "){\n";

                for (int i = 0; i < def.getProperties().size(); i++) {
                        String singleField=def.getProperties().get(i).getName();
                        String paramName ="";
                        if(def.getProperties().get(i).getRef()!=null){
                                paramName=extractFieldNameFromRef(def.getProperties().get(i).getRef(),false);
                        }else{
                                paramName=def.getProperties().get(i).getName();
                        }

                        constructor += "\t\tthis." + singleField + " = " + paramName + ";\n";
                }
                constructor += "\t}\n";

                return constructor;
        }

        private String generateFields(Definition def){

                String str = "";

                for (int i = 0; i < def.getProperties().size(); i++) {

                        str += "\nprivate ";

                        if(def.getProperties().get(i).getRef()!=null){
                                //str += extractFieldNameFromRef(def.getProperties().get(i).getRef(),true);
                                str += checkClassType(def.getProperties().get(i).getName());
                        }
                        if(def.getProperties().get(i).getType()!=null){
                                //str += "String";
                                str += org.springframework.util.StringUtils.capitalize(def.getProperties().get(i).getType().replace("\"", ""));
                        }

                        str+=" "+def.getProperties().get(i).getName()+";";

                }

                return str+"\n";
        }

        private String generateSetters(Definition def){

                String str = "";
                for (int i = 0; i < def.getProperties().size(); i++) {
                        String singleField = def.getProperties().get(i).getName();;
                        String paramName="";
                        String paramType="";
                        if(def.getProperties().get(i).getRef()==null){
                                paramName=def.getProperties().get(i).getName();
                                paramType=org.springframework.util.StringUtils.capitalize(def.getProperties().get(i).getType().replace("\"", ""));
                                //paramType="String";
                        }else{
                                paramType=def.getProperties().get(i).getName();
                                paramName=extractFieldNameFromRef(def.getProperties().get(i).getRef(),false);
                        }
                        str += "\n\tpublic void set"
                                + checkClassType(singleField) +"("+checkClassType(paramType)+ " "+ paramName + "){\n";
                        str += "\t\tthis." + singleField + " = " + paramName + ";\n\t}\n";
                }

                return str;
        }

        private String generateGetters(Definition def){

                String getters = "";
                for (int i = 0; i < def.getProperties().size(); i++) {

                        String singleField=def.getProperties().get(i).getName();
                        String returnType ="";
                        if(def.getProperties().get(i).getRef()==null){
                                returnType=org.springframework.util.StringUtils.capitalize(def.getProperties().get(i).getType().replace("\"",""));
                        }else{
                                returnType=extractFieldNameFromRef(def.getProperties().get(i).getRef(),true);
                        }

                        getters += "\n\tpublic "+returnType+ " get"
                                + org.springframework.util.StringUtils.capitalize(singleField)
                                + "(){\n";
                        getters += "\t\treturn this." + singleField + ";\n\t}\n";
                }

                return getters;
        }


        private String extractFieldNameFromRef(String s,Boolean capitalized) {
                //String prova = "api_iwo.yaml#/definitions/Channel";
                //String prova2 = "#/definitions/Channel";
                String extraction = "";
                //System.out.println("extractFieldNameFromRef STRINGA CHE MI ARRIVA "+s);

                if (s.startsWith("\"api_iwo.yaml#/")) {
                        extraction = s.substring(27).replace("\"","");
                }
                if (s.startsWith("\"#/definitions/")) {
                        extraction = s.substring(15).replace("\"","");
                }

                if (capitalized){
                        return org.springframework.util.StringUtils.capitalize(extraction);
                }else{
                        return org.springframework.util.StringUtils.uncapitalize(extraction);
                }


        }

        private String checkClassType(String s){
                /*
                *  This class is used to remove '_' char from definition
                *  Eg: technical_message -> TechnicalMessage
                * */

                if (s.contains("_")){
                        //System.out.println("STRING CHE MI ARRIVA: "+s);
                        String p = s.substring(s.indexOf("_")+1,s.length());
                        //System.out.println("STRING P: "+p);
                        //s=s.replace("_","");
                        p=org.springframework.util.StringUtils.capitalize(p);
                        //System.out.println("STRING CHE RITORNO: "+s.substring(0,s.indexOf("_"))+p);
                        return org.springframework.util.StringUtils.capitalize(s.substring(0,s.indexOf("_"))+p);
                }else{
                        return  org.springframework.util.StringUtils.capitalize(s);
                }

        }


}

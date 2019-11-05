package yaml2java.plugin.beans;

import yaml2java.plugin.Utils;

public class Property {
  private String name; // "channel"
  private String ref; // "#/definitions/TechnicalMessage"
  private String type; // "string"
  private int minLength;

  public Property() {

  }

  public Property(String name, String ref, String type, int minLength) {

    System.out.println("\tname: "+name);
    this.name = name;
    new Utils();
    if(Utils.isNotNullOrEmpty(ref)){
      System.out.println("\tref: "+ref);
      this.ref = ref;
    }
    if(Utils.isNotNullOrEmpty(type)){
      System.out.println("\ttype: "+type);
      this.type = type;
    }
    if(minLength!=0){
      System.out.println("\tminLength: "+minLength);
      this.minLength = minLength;
    }
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getRef() {
    return ref;
  }

  public void setRef(String ref) {
    this.ref = ref;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getMinLength() {
    return minLength;
  }

  public void setMinLength(int minLength) {
    this.minLength = minLength;
  }
}




/*
 * public class WilPostConversationResponse { private TechnicalMessage channel;
 * }
 */
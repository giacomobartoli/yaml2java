package yaml2java.plugin.beans;

import yaml2java.beans.Property;

import java.util.ArrayList;

public class Definition{

  private ArrayList<Property> properties;
  private String name;
  private String type;

  public Definition(String name,String type, ArrayList<Property> properties) {
    this.name = name;
    this.type = type;
    this.properties = properties;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public ArrayList<Property> getProperties() {
    return this.properties;
  }

  public void setProperties(ArrayList<Property> properties) {
    this.properties = properties;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}

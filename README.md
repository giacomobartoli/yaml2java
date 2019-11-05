# Yaml2Java ♨️

This project aims at generating Java source code starting from a .yaml file containing the requirements.

## Requirements
 - Java 7+
 - .yaml file for code generation
 - Maven

## How does it work
The algorithm scans the file system looking for a .yaml file.
Once find it, the parsing phase starts. Thus, given a .yaml as follows:
```yaml
  WilPostConversationRequest:
    type: object
    properties:
      message:
        $ref: "#/definitions/TextMessage"
      technical_message:
        $ref: "#/definitions/TechnicalMessage"
      customer:
        $ref: "#/definitions/WilCustomer"
```

It will produce the following output:
```java
public class WilPostConversationRequest { 

private String message;
private String technical_message;
private String customer;

public WilPostConversationRequest(String message, String technical_message, String customer){
		this.message = message;
		this.technical_message = technical_message;
		this.customer = customer;
	}

	public String setMessage(String message){
		this.message = message;
	}

	public String setTechnical_message(String technical_message){
		this.technical_message = technical_message;
	}

	public String setCustomer(String customer){
		this.customer = customer;
	}

	public String getMessage(){
		return this.message;
	}

	public String getTechnical_message(){
		return this.technical_message;
	}

	public String getCustomer(){
		return this.customer;
	}

}
```

## Usage

Clone this repo by using any Java IDE (such as Eclipse or IntelliJ). 
Dependencies are located inside the .pom file and should be automatically built by Maven.



## Contributing
Pull requests are welcome. 
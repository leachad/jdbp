package model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Beer {

	private String name;

	public Beer() {
		// BeerInfo 'doNothing' constructor
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

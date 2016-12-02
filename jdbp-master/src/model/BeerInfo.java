package model;

public class BeerInfo extends DBInfo {

	private String name;

	public BeerInfo() {
		// BeerInfo 'doNothing' constructor
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

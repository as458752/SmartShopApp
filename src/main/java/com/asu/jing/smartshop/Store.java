package com.asu.jing.smartshop;

/**
 * @author Jing
 * 
 *         This class encapsulate the name, longitude and latitude of a store
 *
 */
public class Store {
	private String name;
	private Location loc;

	public Store(String name, double longitude, double latitude) {
		super();
		this.name = name;
		this.loc = new Location(longitude, latitude);
	}

	public Store(String name, Location loc) {
		super();
		this.name = name;
		this.loc = loc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((loc == null) ? 0 : loc.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Store other = (Store) obj;
		if (loc == null) {
			if (other.loc != null)
				return false;
		} else if (!loc.equals(other.loc))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}
	
	public double getDistance(Store s2)
	{
		return this.loc.distanceTo(s2.getLoc());
	}
}

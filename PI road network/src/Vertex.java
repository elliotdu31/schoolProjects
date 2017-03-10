/* sommets
 *   les sommets sont stockés dans une table
 *   et repérés par un entier unique (champ id)
 */

class Vertex {
	final long id;
	final double lat, lon; // en 10^-6 deg

	Vertex(long id, double lon, double lat) {
		this.id = id;
		this.lat = lat;
		this.lon = lon;
	}

	public String toString() {
		return "[" + lat * Math.pow(10, -6) + "," + lon * Math.pow(10, -6) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Vertex))
			return false;
		Vertex other = (Vertex) obj;
		if (id != other.id)
			return false;
		return true;
	}

	
}

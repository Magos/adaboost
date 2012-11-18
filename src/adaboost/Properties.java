package adaboost;

public class Properties extends java.util.Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8110413121149714207L;

	/**
	 * @param key
	 * @return String property value corresponding to <code>key</code>; throws runtime exception
	 * if key not found
	 */
	public String getProperty( String key ) {
		String value = super.getProperty( key );
		if ( value == null )
			throw new IllegalArgumentException( "no value for " + key );
		return value;
	}

	/**
	 * @param key
	 * @return boolean property value corresponding to <code>key</code>; throws runtime exception
	 * if key not found
	 */
	public boolean getBooleanProperty( String key ) {
		String value = super.getProperty( key );
		if ( value == null )
			throw new IllegalArgumentException( "no value for " + key );
		return Boolean.valueOf( value ).booleanValue();
	}

	/**
	 * @param key
	 * @return long property value corresponding to <code>key</code>; throws runtime exception if
	 * key not found or invalid long
	 */
	public long getLongProperty( String key ) {
		try {
			String value = super.getProperty( key );
			if ( value == null )
				throw new IllegalArgumentException( "no value for " + key );
			return Long.parseLong( value );
		}
		catch ( NumberFormatException e ) {
			throw new IllegalArgumentException( "bad value for property " + key + ": " + e );
		}
	}

	/**
	 * @param key
	 * @return int property value corresponding to
	 * <code>key</code; throws runtime exception if key not found or invalid integer
	 */
	public int getIntProperty( String key ) {
		try {
			String value = super.getProperty( key );
			if ( value == null )
				throw new IllegalArgumentException( "no value for " + key );
			return Integer.parseInt( value );
		}
		catch ( NumberFormatException e ) {
			throw new IllegalArgumentException( "bad value for property " + key + ": " + e );
		}
	}

	/**
	 * @param key
	 * @return short property value corresponding to
	 * <code>key</code; throws runtime exception if key not found or invalid short
	 */
	public short getShortProperty( String key ) {
		try {
			String value = super.getProperty( key );
			if ( value == null )
				throw new IllegalArgumentException( "no value for " + key );
			return Short.parseShort( value );
		}
		catch ( NumberFormatException e ) {
			throw new IllegalArgumentException( "bad value for property " + key + ": " + e );
		}
	}

	/**
	 * @param key
	 * @return double property value corresponding to <code>key</code>; throws runtime exception
	 * if key not found or invalid double
	 */
	public double getFloatProperty( String key ) {
		try {
			String value = super.getProperty( key );
			if ( value == null )
				throw new IllegalArgumentException( "no value for " + key );
			return Float.parseFloat( value );
		}
		catch ( NumberFormatException e ) {
			throw new IllegalArgumentException( "bad value for property " + key + ": " + e );
		}
	}

	/**
	 * @param key
	 * @return double property value corresponding to <code>key</code>; throws runtime exception
	 * if key not found or invalid double
	 */
	public double getDoubleProperty( String key ) {
		try {
			String value = super.getProperty( key );
			if ( value == null )
				throw new IllegalArgumentException( "no value for " + key );
			return Double.parseDouble( value );
		}
		catch ( NumberFormatException e ) {
			throw new IllegalArgumentException( "bad value for property " + key + ": " + e );
		}
	}
}

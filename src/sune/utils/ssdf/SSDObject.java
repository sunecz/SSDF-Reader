package sune.utils.ssdf;

import java.util.regex.Pattern;

/**
 * Represents a data object that is used for storing
 * information about objects in SSD File.<br>
 * It is also the base of SSD File.
 * @author Sune*/
public class SSDObject
{
	/**
	 * The name of object*/
	private final String name;
	/**
	 * The value of object*/
	private final String value;
	/**
	 * The Data Type of object*/
	private final SSDType type;

	/**
	 * Creates new instance of Data Object
	 * @param name 	The object's name
	 * @param value The object's value*/
	public SSDObject(String name, String value)
	{
		this.name 	= name;
		this.type 	= findDataType(value);
		this.value 	= SSDFUtils.regex(type.getRegex(), value, 1);
	}
	
	/**
	 * Creates new instance of Data Object
	 * @param name 	The object's name
	 * @param type	The object's type
	 * @param value The object's value*/
	protected SSDObject(String name, SSDType type, String value)
	{
		this.name 	= name;
		this.type 	= type;
		this.value 	= SSDFUtils.regex(type.getRegex(), value, 1);
	}
	
	/**
	 * Finds the Data Type of the object defined by the
	 * object's value
	 * @param value The object's value*/
	private static SSDType findDataType(String value)
	{
		for(SSDType type : SSDType.values())
			if(Pattern.matches(type.getRegex(), value))
				return type;
		
		return SSDType.UNDEFINED;
	}
	
	/**
	 * Gets the name of object
	 * @return The object's name*/
	public String getName()
	{
		return name;
	}
	
	/**
	 * Gets the string value of object
	 * @return The object's value*/
	public String getStringValue()
	{
		return value;
	}
	
	/**
	 * Gets the number value of object
	 * @return The object's value*/
	public int getIntegerValue()
	{
		return Integer.parseInt(value);
	}
	
	/**
	 * Gets the floating-point number value of object
	 * @return The object's value*/
	public double getDoubleValue()
	{
		return Double.parseDouble(value);
	}
	
	/**
	 * Gets the truth value of object
	 * @return The object's value*/
	public boolean getBooleanValue()
	{
		return Boolean.parseBoolean(value);
	}
	
	/**
	 * Gets the value of object
	 * @deprecated Due to new methods this method
	 * 		should not be used. It is just a default
	 * 		method for getting values when you do not
	 * 		know certainly the type of the value.
	 * @return The object's value*/
	@Deprecated
	public String getValue()
	{
		return value;
	}
	
	/**
	 * Gets the data type of object
	 * @return The object's data type*/
	public SSDType getType()
	{
		return type;
	}
	
	/**
	 * Called when the object should be converted
	 * into a string
	 * @return The string of the object*/
	@Override
	public String toString()
	{
		return "[name=" + name + ", type=" + type + ", value=" + value + "]";
	}
}
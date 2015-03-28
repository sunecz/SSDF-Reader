package sune.utils.ssdf;

/**
 * Stores all supported data types of
 * objects in SSD Files.
 * @author Sune*/
public enum SSDType
{
	/**
	 * Represents a string object. In SSD File
	 * it is defined as a collection of characters
	 * surrounded by quotes (double or single).*/
	STRING("^[\"|\'](.*?|)[\"|\']$"),
	/**
	 * Represents an integer object. In SSD File
	 * it is defined as a collection of only-number
	 * characters.*/
	INTEGER("^(\\d+)$"),
	/**
	 * Represents a floating number object. In SSD
	 * File it is defined as a collection of number
	 * characters and dot character, that is used
	 * to split the number on two parts, where the
	 * first defines the normal numbers and the
	 * second defines the decimal numbers.*/
	FLOAT("^(\\d+\\.\\d+)$"),
	/**
	 * Represents a truth object. In SSD File
	 * it is defined by two words: true and false.*/
	BOOLEAN("^(true|false)$"),
	/**
	 * Represents a null object or nothing. In SSD
	 * File it is defined by word null, or if an
	 * object does not have a value.*/
	NULL("^(null)$"),
	/**
	 * Represents an undefined object. In SSD File
	 * it is only used for objects that could not
	 * be determined.*/
	UNDEFINED("^(.*?)$");
	
	/**
	 * The string of the object type written in
	 * regular expression.*/
	private String regex;
	
	/**
	 * Creates new instance of Data Type
	 * @param regex The string written in regular expression*/
	private SSDType(String regex)
	{
		this.regex = regex;
	}
	
	/**
	 * Gets the regular expression string of the object type
	 * @return The string written in regular expression*/
	public String getRegex()
	{
		return regex;
	}
}
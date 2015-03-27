package sune.utils.ssdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

/**
 * Helps with reading SSDF Syntax and contains
 * useful methods for manipulating with objects
 * in SSD Files.
 * @author Sune*/
public class SSDFReader
{
	/**
	 * The main SSD Array object*/
	private SSDArray array;
	
	/**
	 * Opening object brackets*/
	private char oOB = '{';
	/**
	 * Closing object brackets*/
	private char cOB = '}';
	/**
	 * Opening array brackets*/
	private char oAB = '[';
	/**
	 * Closing array brackets*/
	private char cAB = ']';
	/**
	 * Name/value delimiter*/
	private char nvd = ':';
	/**
	 * Items delimiter*/
	private char itd = ',';
	
	/**
	 * Stores all special words that should
	 * not be read as a string*/
	private String[] words = new String[]
	{
		"true", "false", "null"
	};
	
	/**
	 * Creates new instance of SSDF Reader
	 * @param file The file object to read*/
	public SSDFReader(File file)
	{
		this.array = getObjects(format(getContent(file)));
	}

	/**
	 * Gets the content of the given file
	 * @param file The file object from where to get the
	 * 			   content
	 * @return The content of the given file*/
	private String getContent(File file)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			StringBuilder builder = new StringBuilder();
			
			String line = "";
			while((line = reader.readLine()) != null)
				builder.append(line);
			
			reader.close();
			return builder.toString();
		}
		catch(Exception ex) {}
		
		return null;
	}
	
	/**
	 * Formats the string. It removes useless and not
	 * important spaces, breaks (line delimiters) and
	 * other characters.
	 * @param string The string to format
	 * @return The formatted string*/
	private String format(String string)
	{
		boolean idq = false;
		boolean isq = false;
		
		StringBuilder sb = new StringBuilder();
		for(int p = 0; p < string.length(); p++)
		{
			char c = string.charAt(p);
			
			if(c == '\"' && !isq) idq = !idq;
			if(c == '\'' && !idq) isq = !isq;
			
			if((c == ' ' || c == '\t' || c == '\n' || c == '\r') && !(idq || isq))
				continue;
			
			sb.append(c);
		}
		
		return sb.toString();
	}
	
	/**
	 * Formats object's name
	 * @param name The object's name to format
	 * @return The formatted object's name*/
	private String formatName(String name)
	{
		List<String> list = SSDFUtils.regex("([A-Za-z0-9\\_]+)", name);
		return list.size() == 0 ? null : list.get(0);
	}

	/**
	 * Formats object's value
	 * @param value The object's value to format
	 * @return The formatted object's value*/
	private String formatValue(String value)
	{
		boolean idq = false;
		boolean isq = false;
		boolean dig = false;
		
		// Special words
		boolean add = false;
		int addInt 	= 0;

		StringBuilder sb = new StringBuilder();
		for(int p = 0; p < value.length(); p++)
		{
			char c = value.charAt(p);
			
			if(p == 0 && Character.isDigit(c))
				dig = true;

			if(c == '\"' && !isq) idq = !idq;
			if(c == '\'' && !idq) isq = !isq;

			if(!dig && !(isq || idq) && !add)
			{
				int k = 0;
				for(String word : words)
				{
					if(((k = p)+word.length()) < value.length())
					{
						for(int i = 0; i < word.length(); i++)
						{
							if(word.charAt(i) != value.charAt(k++))
								break;
							if(i == word.length()-1)
								add = true;
						}
						
						if(add)
						{
							addInt = word.length();
							break;
						}
					}
				}
			}
			
			if((dig && (Character.isDigit(c) || c == '.')) || (isq || idq) || ((!idq && c == '\"') || (!isq && c == '\'')) || (add && addInt-- > 0))
				sb.append(c);
		}

		return sb.toString();
	}
	
	/**
	 * Gets all objects in the given SSD File's content string
	 * @param string The SSD file's content
	 * @return The Map (list) of all read objects*/
	private SSDArray getObjects(String string)
	{
		return getObjects(getBracketsContent(string, oOB, cOB), "", false);
	}

	/**
	 * Gets content between the two given brackets' characters
	 * @param string 		The string where to get the content
	 * @param openBrackets 	The character of opening brackets
	 * @param closeBrackets The character of closing brackets
	 * @return The content between the two given brackets' characters*/
	private String getBracketsContent(String string, char openBrackets, char closeBrackets)
	{
		int b = 0;
		int l = 0;
		
		StringBuilder sb = new StringBuilder();
		for(int p = 0; p < string.length(); p++)
		{
			char c = string.charAt(p);

			if(!(b == 0 && c == openBrackets) && !(b == 1 && c == closeBrackets))
				sb.append(c);
			
			if(c == openBrackets) 	b++;
			if(c == closeBrackets) 	b--;
			
			if(b < 0)
			{
				l = 1;
				break;
			}
		}
		
		return SSDFUtils.substringEnd(sb.toString(), 0, l);
	}
	
	/**
	 * Gets all objects in the given SSD File's content string with
	 * pre-defined parent name of the all read objects and information
	 * if the parent object is an array, or not.
	 * @param string 		The SSD file's content
	 * @param parentName 	The object's parent name
	 * @param array 		If true, the parent object is an array, otherwise is not
	 * @return The Map (list) of all read objects*/
	private SSDArray getObjects(String string, String parentName, boolean array)
	{
		// Is in double-quotes
		boolean idq = false;
		// is in single-quotes
		boolean isq = false;
		// Is escaped
		boolean esp = false;

		// Can write name
		boolean wn = !array;
		// Can write value
		boolean wv = array;
		
		SSDArray ssdArray = new SSDArray();
		
		String lastName = "";
		int lastCount 	= 0;
		
		StringBuilder sb = new StringBuilder();
		for(int p = 0; p < string.length(); p++)
		{
			char c = string.charAt(p);
		
			if(wn || wv)
				sb.append(c);
			
			if(c == nvd)
			{
				lastName = formatName(sb.toString());
				
				sb.setLength(0);
				wn = false;
				wv = true;
			}
			
			if(c == itd || p == string.length()-1)
			{
				String name = parentName + (parentName.isEmpty() ? "" : ".") + (array ? Integer.toString(lastCount++) : lastName);
				ssdArray.put(name, new SSDObject(name, formatValue(sb.toString())));
				
				sb.setLength(0);
				wn = !array;
				wv = array;
			}
			
			if(c == oOB || c == oAB)
			{
				String content = getBracketsContent(string.substring(p+1), c == oAB ? oAB : oOB, c == oAB ? cAB : cOB);
				SSDArray ssdar = getObjects(content, parentName + (parentName.isEmpty() ? "" : ".") +
										    (array ? Integer.toString(lastCount++) : lastName), c == oAB);
				
				ssdArray.putAll(ssdar.getObjects());
				p += content.length()+2;
				continue;
			}
			
			esp = false;
			if(c == '\\' && !esp) esp = true;
			if(c == '\"' && !isq) idq = !idq;
			if(c == '\'' && !idq) isq = !isq;
		}

		return ssdArray;
	}
	
	/**
	 * Gets the object by the given name.
	 * 
	 * @throws NoSuchFieldException
	 * 
	 * 		   When the object does not exist
	 * 		   in the array. You should use method
	 * 		   <code>hasObject(name)</code> to
	 * 		   prevent this exception.
	 * 
	 * @param name The name of the object to get
	 * @return The object*/
	public SSDObject getObject(String name)
	{
		return array.getObject(name);
	}
	
	/**
	 * Gets an array of all objects that are in the
	 * given array object.
	 * 
	 * @throws NoSuchFieldException
	 * 
	 * 		   When the array object does not exist
	 * 		   in the array. You should use method
	 * 		   <code>hasObject(name)</code> to
	 * 		   prevent this exception.
	 * 
	 * @param name The name of the array to get
	 * @return The Data Array object with all objects in the
	 * 		   given array*/
	public SSDArray getArray(String name)
	{
		return array.getArray(name);
	}
	
	/**
	 * Sets the object value
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, String value)
	{
		array.put(name, new SSDObject(name, "\"" + value + "\""));
	}
	
	/**
	 * Sets the object value
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, int value)
	{
		array.put(name, new SSDObject(name, Integer.toString(value)));
	}
	
	/**
	 * Sets the object value
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, double value)
	{
		array.put(name, new SSDObject(name, Double.toString(value)));
	}
	
	/**
	 * Sets the object value
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, boolean value)
	{
		array.put(name, new SSDObject(name, Boolean.toString(value)));
	}
	
	/**
	 * Sets the object value to null
	 * @param name 	The object's name*/
	public void setObject(String name)
	{
		array.put(name, new SSDObject(name, "null"));
	}
	
	/**
	 * Sets the array
	 * @param name 	The array's name
	 * @param array The Map (list) of all objects to set*/
	public void setArray(String name, Map<String, SSDObject> array)
	{
		array.putAll(array);
	}
	
	/**
	 * Checks if the SSD File contains an object
	 * by the given name.
	 * @param The object's name
	 * @return True, if the object was found, otherwise false*/
	public boolean hasObject(String name)
	{
		return array.hasObject(name);
	}
	
	/**
	 * Removes the object
	 * @param name The object's name*/
	public void removeObject(String name)
	{
		array.removeObject(name);
	}
}
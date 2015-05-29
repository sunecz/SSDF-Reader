package sune.utils.ssdf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

/**
 * Helps with reading SSDF Syntax and contains
 * useful methods for manipulating with objects
 * in SSD Files.
 * @author Sune*/
public class SSDFCore
{
	/**
	 * The main SSD Array object*/
	protected SSDArray array;
	
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
	 * Creates new instance of SSDF Core*/
	public SSDFCore()
	{
		this("");
	}
	
	/**
	 * Creates new instance of SSDF Core
	 * @param content The content of SSDF file in SSDF Syntax*/
	public SSDFCore(String content)
	{
		this.array = getObjects(format(content));
	}
	
	/**
	 * Creates new instance of SSDF Core
	 * @param file The file object to read*/
	public SSDFCore(File file)
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
				builder.append(line).append("\n");
			
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
		// Is in double-quoted
		boolean idq = false;
		// Is in single-quoted
		boolean isq = false;
		// Is escaped
		boolean esc = false;
		
		// Is line-commented
		boolean islc = false;
		// Is block-commented
		boolean isbc = false;
		
		/* How many characters should be
		 * skipped*/
		int skip = 0;
		
		StringBuilder sb = new StringBuilder();
		for(int p = 0; p < string.length(); p++)
		{
			// Characters skipping
			if(skip > 0) { skip--; continue; }
			
			// Gets character on the position
			char c = string.charAt(p);
			
			// Quoting
			if(c == '\"' && !isq && !esc) idq = !idq;
			if(c == '\'' && !idq && !esc) isq = !isq;
			
			// Line comments support
			if(c == '#' && !(idq || isq)) 			islc = true;
			if((c == '\n' || c == '\r') && islc)	islc = false;
			
			// Block comments support
			if((c == '/' && string.charAt(p+1) == '*') && !(idq || isq)) { isbc = true; continue; }
			if((c == '*' && string.charAt(p+1) == '/') && !(idq || isq)) { isbc = false; skip = 1; continue; }

			// Ignores special characters or comments
			if(((c == ' ' || c == '\t' || c == '\n' || c == '\r') && !(idq || isq)) || islc || isbc)
				continue;
			
			// Adds the character
			sb.append(c);
			
			/* Removes the escaping. This allows to escape
			 * only one character at the time.*/
			if(esc)	esc = false;
			// Escapes the next character
			if(c == '\\') esc = true;
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
		// Is in double-quoted
		boolean idq = false;
		// Is in single-quoted
		boolean isq = false;
		// Is escaped
		boolean esc = false;
		// Is a digit
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

			// Quoting
			if(c == '\"' && !isq && !esc) idq = !idq;
			if(c == '\'' && !idq && !esc) isq = !isq;
			// Escapes the next character
			if(c == '\\') { esc = true; continue; }
			
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
			
			/* Removes the escaping. This allows to escape
			 * only one character at the time.*/
			if(esc)	esc = false;
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
			
			if(b == 0)
				break;
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
		// Is in double-quoted
		boolean idq = false;
		// Is in single-quoted
		boolean isq = false;
		// Is escaped
		boolean esc = false;

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
			
			if(c == nvd && !(idq || isq))
			{
				lastName = formatName(sb.toString());
				
				sb.setLength(0);
				wn = false;
				wv = true;
			}
			
			if((c == itd && !(idq || isq)) || p == string.length()-1)
			{
				String name = parentName + (parentName.isEmpty() ? "" : ".") + (array ? Integer.toString(lastCount++) : lastName);
				ssdArray.put(name, new SSDObject(name, formatValue(sb.toString())));
				
				sb.setLength(0);
				wn = !array;
				wv = array;
			}
			
			if((c == oOB || c == oAB) && !(idq || isq))
			{
				String content = getBracketsContent(string.substring(p), c == oAB ? oAB : oOB, c == oAB ? cAB : cOB);
				SSDArray ssdar = getObjects(content, parentName + (parentName.isEmpty() ? "" : ".") +
										    (array ? Integer.toString(lastCount++) : lastName), c == oAB);
				
				ssdArray.putAll(ssdar.getObjects());
				p += content.length()+2;
				continue;
			}
			
			// Quoting
			if(c == '\"' && !isq && !esc) idq = !idq;
			if(c == '\'' && !idq && !esc) isq = !isq;
			
			/* Removes the escaping. This allows to escape
			 * only one character at the time.*/
			if(esc)	esc = false;
			// Escapes the next character
			if(c == '\\') { esc = true; continue; }
		}

		return ssdArray;
	}
	
	/**
	 * Gets the content (all the objects) as a string.
	 * @return The content as a string.*/
	public String getContentString()
	{
		StringBuilder sb 				= new StringBuilder();
		Map<String, SSDArray> arrays 	= new HashMap<>();
		Map<String, SSDObject> objects 	= array.getAllObjects();
		
		for(Entry<String, SSDObject> entry : objects.entrySet())
		{
			String keyPath 	  = entry.getKey();
			String[] splitKey = keyPath.split("\\.");
			
			while(splitKey.length > 1)
			{
				splitKey = keyPath.split("\\.");
				keyPath	 = String.join(".", Arrays.copyOfRange(splitKey, 0, splitKey.length-1)).trim();
				
				if(!keyPath.isEmpty() && !arrays.containsKey(keyPath))
					arrays.put(keyPath, new SSDArray(new HashMap<>(), keyPath));
			}
		}
		
		sb.append("{\n");
		sb.append(getArrayContentString(
			"", arrays, 1, false));
		sb.append("\n}");
		
		return sb.toString();
	}
	
	// HELPS WITH GETTING THE CONTENT AS A STRING
	private String getArrayContentString(String startsWith, Map<String, SSDArray> arrays, int depth, boolean wasItems)
	{
		StringBuilder sb 				= new StringBuilder();
		Map<String, SSDObject> objects 	= array.getAllObjects();
		
		boolean isFirstArray = true;
		for(Entry<String, SSDArray> array : arrays.entrySet())
		{
			String arrayKey = array.getKey();
			String[] splitArrayKey = arrayKey.split("\\.");
			
			if(arrayKey.startsWith(startsWith) && splitArrayKey.length == depth)
			{
				String arrayName = splitArrayKey[splitArrayKey.length-1];
				String arrayTab	 = SSDFUtils.repeatString("\t", depth);
				boolean isArray  = Pattern.matches("\\d+", arrayName);
				
				if(!isFirstArray || wasItems) sb.append(",\n\n");
				if(isFirstArray) 			  isFirstArray = false;
				
				if(!isArray)
				{
					sb.append(arrayTab);
					sb.append(arrayName);
					sb.append(":\n");
				}

				sb.append(arrayTab);
				sb.append(isArray ? "[" : "{");
				sb.append("\n");
				
				boolean isFirstItem = true;
				for(Entry<String, SSDObject> object : objects.entrySet())
				{
					String objectKey 		= object.getKey();
					String[] splitObjectKey = objectKey.split("\\.");
					
					if(splitObjectKey.length == depth+1)
					{
						String objectPath = String.join(".", Arrays.copyOfRange(splitObjectKey, 0, splitObjectKey.length-1));
						String objectTab  = SSDFUtils.repeatString("\t", depth+1);
						String objectName = splitObjectKey[splitObjectKey.length-1];
						
						if(arrayKey.equals(objectPath))
						{
							if(!isFirstItem) sb.append(",\n");
							if(isFirstItem)  isFirstItem = false;
							
							sb.append(objectTab);
							sb.append(objectName);
							sb.append(": ");
							
							SSDType objectType = object.getValue().getType();
							String objectValue = object.getValue().getValue();
							
							if(objectType == SSDType.STRING)
							{
								sb.append("\"");
								sb.append(objectValue);
								sb.append("\"");
							}
							else
							{
								sb.append(objectValue);
							}
						}
					}
				}
				
				sb.append(getArrayContentString(
					arrayKey, arrays, depth+1, !isFirstItem));
				sb.append("\n");
				sb.append(arrayTab);
				sb.append(isArray ? "]" : "}");
			}
		}
		
		return sb.toString();
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
	 * @return The SSD object*/
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
	 * @return The SSD Array object with all objects in the
	 * 		   given array*/
	public SSDArray getArray(String name)
	{
		return array.getArray(name);
	}
	
	/**
	 * Gets an array of all existing objects.
	 * @return The SSD Array object with all
	 * 		   existing objects*/
	public SSDArray getAll()
	{
		return array.getAll();
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
	 * Sets the object
	 * @param name 	 The object's name
	 * @param object The object*/
	public void setObject(String name, SSDObject object)
	{
		array.put(name, object);
	}
	
	/**
	 * Sets the array (from a map of objects)
	 * @param array The Map (list) of all objects to set*/
	public void setArray(Map<String, SSDObject> array)
	{
		this.array.putAll(array);
	}
	
	/**
	 * Sets the array
	 * @param array The array object*/
	public void setArray(SSDArray array)
	{
		this.array.putAll(array.getAllObjects());
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
	
	/**
	 * Gets all objects that are stored in the array
	 * @return The Map (list) of all stored objects*/
	public Map<String, SSDObject> getAllObjects()
	{
		return array.getAllObjects();
	}
}
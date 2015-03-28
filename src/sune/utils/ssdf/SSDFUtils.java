package sune.utils.ssdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

/**
 * Contains useful methods.
 * @author Sune*/
public class SSDFUtils
{
	/**
	 * Gets the resource
	 * @param path Path to the resource
	 * @return The URL object for the resource*/
	public static URL getResource(String path)
	{
		return SSDFUtils.class.getResource(path);
	}

	/**
	 * Gets the resource as stream
	 * @param path Path to the resource
	 * @return The input stream for the resource*/
	public static InputStream getResourceAsStream(String path)
	{
		return SSDFUtils.class.getResourceAsStream(path);
	}

	/**
	 * Gets the resource as image
	 * @param path Path to the resource
	 * @return The buffered image object from the resource*/
	public static BufferedImage getResourceAsImage(String path)
	{
		try
		{
			return ImageIO.read(getResourceAsStream(path));
		}
		catch(Exception ex) {}
		
		return null;
	}
	
	/**
	 * Gets the resource as file
	 * @param path Path to the resource
	 * @return The file object from the resource*/
	public static File getResourceAsFile(String path)
	{
		try
		{
			return new File(getResource(path).toURI());
		}
		catch(Exception ex) {}
		
		return null;
	}
	
	/**
	 * Matches all the groups from the given regular
	 * expressions pattern string and an input.
	 * @param regex The regular expression pattern
	 * @param input The input string
	 * @return List of all matched groups*/
	public static List<String> regex(String regex, String input)
	{
		List<String> list = new ArrayList<>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		while(matcher.find())
			list.add(matcher.group());
		
		return list;
	}
	
	/**
	 * Matches a group by index from the given regular
	 * expressions pattern string and an input.
	 * @param regex The regular expression pattern
	 * @param input The input string
	 * @param group The group index
	 * @return The matched group string*/
	public static String regex(String regex, String input, int group)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		
		return matcher.matches() ? matcher.group(group) : null;
	}
	
	/**
	 * Substrings the string using start index and
	 * length, that defines the number of characters
	 * in the substringed string. The end index is
	 * as following: <code>end = start + length</code>.
	 * @param string The string
	 * @param start  The start index
	 * @param length The length
	 * @return The substringed string*/
	public static String substringEnd(String string, int start, int length)
	{
	    return string.substring(start, Math.max(start, string.length() - Math.min(start + length, string.length())));
	}
	
	/**
	 * Checks if the map of strings and SSD Objects
	 * contains a key that starts with the given string.
	 * @param map The map, where to check
	 * @param key The string
	 * @return True, if the map has key that start with
	 * 		   the given string, otherwise false*/
	public static boolean containsKeyStartsWith(Map<String, SSDObject> map, String key)
	{
		for(Iterator<Entry<String, SSDObject>> it = map.entrySet().iterator(); it.hasNext();)
			if(it.next().getKey().startsWith(key))
				return true;
		
		return false;
	}
}
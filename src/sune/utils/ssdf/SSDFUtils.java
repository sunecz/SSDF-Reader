package sune.utils.ssdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

public class SSDFUtils
{
	public static URL getResource(String path)
	{
		return SSDFUtils.class.getResource(path);
	}

	public static InputStream getResourceAsStream(String path)
	{
		return SSDFUtils.class.getResourceAsStream(path);
	}

	public static BufferedImage getResourceAsImage(String path)
	{
		try
		{
			return ImageIO.read(getResourceAsStream(path));
		}
		catch(Exception ex) {}
		
		return null;
	}
	
	public static File getResourceAsFile(String path)
	{
		try
		{
			return new File(getResource(path).toURI());
		}
		catch(Exception ex) {}
		
		return null;
	}
	
	public static List<String> regex(String regex, String input)
	{
		List<String> list = new ArrayList<>();
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);

		while(matcher.find())
			list.add(matcher.group());
		
		return list;
	}
	
	public static String regex(String regex, String input, int group)
	{
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		
		return matcher.matches() ? matcher.group(group) : null;
	}
	
	public static String substringEnd(String string, int start, int length)
	{
	    return string.substring(start, Math.max(start, string.length() - Math.min(start + length, string.length())));
	}
}
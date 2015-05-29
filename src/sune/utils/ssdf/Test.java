package sune.utils.ssdf;

import java.util.Iterator;

public class Test
{
	public static void main(String[] args)
	{
		SSDFCore ssdf = new SSDFCore(SSDFUtils.getResourceAsFile("/test.ssdf"));
		
		for(Iterator<SSDObject> it = ssdf.getAll().iterator(); it.hasNext();)
			System.out.println(it.next());
	}
}
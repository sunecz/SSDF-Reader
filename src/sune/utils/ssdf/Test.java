package sune.utils.ssdf;
import java.util.Iterator;

public class Test
{
	public static void main(String[] args)
	{
		SSDFReader reader = new SSDFReader(SSDFUtils.getResourceAsFile("/test.ssdf"));

		for(Iterator<SSDObject> it = reader.getAll().iterator(); it.hasNext();)
			System.out.println(it.next());
	}
}
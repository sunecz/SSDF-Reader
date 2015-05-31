package sune.utils.ssdf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a data array that is used
 * for storing objects and their information
 * in SSD File.
 * @author Sune*/
public class SSDArray implements Iterable<SSDObject>
{
	/**
	 * The Map (list) of stored objects*/
	private final Map<String, SSDObject> objects;
	/**
	 * The array name*/
	private final String name;
	
	/**
	 * Creates new instance of Data Array*/
	protected SSDArray()
	{
		this.objects = new HashMap<>();
		this.name	 = "";
	}
	
	/**
	 * Puts an object with the given name to the array
	 * @param name 		The object's name
	 * @param object 	The object*/
	protected void put(String name, SSDObject object)
	{
		objects.put(name, object);
	}
	
	/**
	 * Puts a Map (list) of objects to the array
	 * @param data The Map (list) of objects to put*/	
	protected void putAll(Map<String, SSDObject> data)
	{
		objects.putAll(data);
	}
	
	/**
	 * Gets all objects that are stored in the array
	 * @return The Map (list) of all stored objects*/
	protected Map<String, SSDObject> getObjects()
	{
		return objects;
	}
	
	/**
	 * Creates new instance of Data Array
	 * @param name The name of the array*/
	public SSDArray(String name)
	{
		this(name, new HashMap<>());
	}
	
	/**
	 * Creates new instance of Data Array
	 * @param name The name of the array
	 * @param objects The Map (list) of objects*/
	public SSDArray(String name, Map<String, SSDObject> objects)
	{
		this.name 	 = name;
		this.objects = objects;
	}
	
	/**
	 * Gets the true name of an object name. It
	 * is name with the name of the array at the
	 * beginning.
	 * @param name The object name
	 * @return The true name of the object*/
	private String getTrueName(String name)
	{
		return this.name + (this.name.isEmpty() ? "" : ".") + name;
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
		try
		{
			String objectName = getTrueName(name);
			if(!objects.containsKey(objectName))
				throw new NoSuchFieldException
				("The object '" + name + "' does not exist!");
			
			return objects.get(objectName);
		}
		catch(NoSuchFieldException ex)
		{ ex.printStackTrace(); }
		
		return null;
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
		try
		{
			String objectName = getTrueName(name);
			if(!SSDFUtils.containsKeyStartsWith(objects, objectName))
				throw new NoSuchFieldException
				("The array object '" + name + "' does not exist!");
			
			Map<String, SSDObject> map = new HashMap<>();
			for(Iterator<Entry<String, SSDObject>> it = objects.entrySet().iterator(); it.hasNext();)
			{
				Entry<String, SSDObject> entry = it.next();
				if(entry.getKey().startsWith(objectName) && !entry.getKey().equals(objectName))
					map.put(this.name + (this.name.isEmpty() ? "" : ".") +
							entry.getKey().substring(objectName.length() + (objectName.isEmpty() ? 0 : 1)),
							entry.getValue());
			}
			
			return new SSDArray(this.name + (this.name.isEmpty() ? "" : ".") + objectName, map);
		}
		catch(NoSuchFieldException ex)
		{ ex.printStackTrace(); }
		
		return null;
	}
	
	/**
	 * Gets an array of all existing objects.
	 * @return The SSD Array object with all
	 * 		   existing objects*/
	public SSDArray getAll()
	{
		return getArray("");
	}
	
	/**
	 * Sets the object value
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, String value)
	{
		String objectName = getTrueName(name);
		objects.put(objectName, new SSDObject(objectName, "\"" + value + "\""));
	}
	
	/**
	 * Sets the object value
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, int value)
	{
		String objectName = getTrueName(name);
		objects.put(objectName, new SSDObject(objectName, Integer.toString(value)));
	}
	
	/**
	 * Sets the object value
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, double value)
	{
		String objectName = getTrueName(name);
		objects.put(objectName, new SSDObject(objectName, Double.toString(value)));
	}
	
	/**
	 * Sets the object value
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, boolean value)
	{
		String objectName = getTrueName(name);
		objects.put(objectName, new SSDObject(objectName, Boolean.toString(value)));
	}
	
	/**
	 * Sets the object value to null
	 * @param name 	The object's name*/
	public void setObject(String name)
	{
		String objectName = getTrueName(name);
		objects.put(objectName, new SSDObject(objectName, "null"));
	}
	
	/**
	 * Sets the object
	 * @param name 	 The object's name
	 * @param object The object*/
	public void setObject(String name, SSDObject object)
	{
		objects.put(getTrueName(name), object);
	}
	
	/**
	 * Sets the array (from a map of objects)
	 * @param array The Map (list) of all objects to set*/
	public void setArray(Map<String, SSDObject> array)
	{
		objects.putAll(array);
	}
	
	/**
	 * Sets the array
	 * @param array The array object*/
	public void setArray(SSDArray array)
	{
		Map<String, SSDObject> map = new HashMap<>();
		for(Entry<String, SSDObject> entry : array.getAllObjects().entrySet())
		{
			String fullName 	= getTrueName(entry.getKey());
			SSDObject object 	= entry.getValue();
			SSDType objectType  = object.getType();
			String objectValue	= object.getValue();
			
			if(objectType == SSDType.STRING)
				objectValue = "\"" + objectValue + "\"";
			
			SSDObject newObject = new SSDObject(fullName,
				object.getType(), objectValue);
			
			map.put(fullName, newObject);
		}
		
		objects.putAll(map);
	}
	
	/**
	 * Checks if the SSD File contains an object
	 * by the given name.
	 * @param The object's name
	 * @return True, if the object was found, otherwise false*/
	public boolean hasObject(String name)
	{
		return objects.containsKey(getTrueName(name));
	}
	
	/**
	 * Removes the object. When the object
	 * does not exist, nothing happens.
	 * @param name The object's name*/
	public void removeObject(String name)
	{
		objects.remove(getTrueName(name));
	}
	
	/**
	 * Gets all objects that are stored in the array
	 * @return The Map (list) of all stored objects*/
	public Map<String, SSDObject> getAllObjects()
	{
		Map<String, SSDObject> newObjects = new HashMap<>();
		for(Entry<String, SSDObject> entry : objects.entrySet())
			newObjects.put(entry.getKey(), entry.getValue());
		
		return newObjects;
	}
	
	/**
	 * Called when the object should be converted
	 * into a string
	 * @return The string of the object*/
	@Override
	public String toString()
	{
		return objects.toString();
	}

	/**
	 * SSD Array iterator. Allows to use SSD Array object
	 * in loops easily.
	 * @author Sune*/
	private class SSDArrayIterator implements Iterator<SSDObject>
	{
		/**
		 * The index of current item*/
		private int index;

		/**
		 * Checks if the array has next item
		 * @return True, if the array has next item,
		 * 		   otherwise false*/
		@Override
		public boolean hasNext()
		{
			return index < SSDArray.this.objects.size();
		}
		
		/**
		 * Gets next item in the array. It should be
		 * checked with <code>hasNext()</code> method
		 * before use.
		 * 
		 * @throws ArrayIndexOutOfBoundsException
		 * 
		 * 		   When the current index is bigger than
		 * 		   the array size. It can happen when the
		 * 		   method <code>hasNext()</code> was not
		 * 		   called and so it was not checked if the
		 * 		   array contains next item.
		 * 
		 * @return The next item in array, if it is
		 * 		   available*/
		@Override
		public SSDObject next()
		{
			if(index >= SSDArray.this.objects.size())
				throw new ArrayIndexOutOfBoundsException
				("Index is bigger than the array size!");
			
			Iterator<Entry<String, SSDObject>> it
				= SSDArray.this.objects.entrySet().iterator();
			
			int k = 0;
			while(k++ < index && it.hasNext())
				it.next();
			
			index++;
			if(it.hasNext())
				return it.next().getValue();
			
			return null;
		}
	}
	
	/**
	 * Gets the iterator object
	 * @return The iterator object*/
	@Override
	public Iterator<SSDObject> iterator()
	{
		return new SSDArrayIterator();
	}
}
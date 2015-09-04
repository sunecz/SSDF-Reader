package sune.utils.ssdf;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents a data array that is used
 * for storing objects and their information
 * in SSD File.
 * @author Sune*/
public class SSDArray implements Iterable<SSDObject> {
	
	/**
	 * The Map (list) of stored objects*/
	private final Map<String, SSDObject> objects;
	/**
	 * The array name*/
	private final String name;
	
	/**
	 * Current array index*/
	private int currentIndex;
	
	/**
	 * Creates new instance of Data Array.*/
	protected SSDArray() {
		this.objects = new HashMap<>();
		this.name	 = "";
	}
	
	/**
	 * Puts an object with the given name to the array.
	 * @param name 		The object's name
	 * @param object 	The object*/
	protected void put(String name, SSDObject object) {
		objects.put(name, object);
	}
	
	/**
	 * Puts a Map (list) of objects to the array.
	 * @param data The Map (list) of objects to put*/	
	protected void putAll(Map<String, SSDObject> data) {
		objects.putAll(data);
	}
	
	/**
	 * Gets all objects that are stored in the array.
	 * @return The Map (list) of all stored objects*/
	protected Map<String, SSDObject> getObjects() {
		return objects;
	}
	
	/**
	 * Creates new instance of Data Array.
	 * @param name The name of the array*/
	public SSDArray(String name) {
		this(name, new HashMap<>());
	}
	
	/**
	 * Creates new instance of Data Array.
	 * @param name The name of the array
	 * @param objects The Map (list) of objects*/
	public SSDArray(String name, Map<String, SSDObject> objects) {
		this.name 	 = name;
		this.objects = objects;
	}
	
	/**
	 * Gets the true name of an object name. It
	 * is name with the name of the array at the
	 * beginning.
	 * @param name The object name
	 * @return The true name of the object*/
	private String getTrueName(String name) {
		return this.name + (this.name.isEmpty() ? "" : ".") + name;
	}
	
	/**
	 * Gets the object by the given name.
	 * @param name The name of the object to get
	 * @return The SSD object*/
	public SSDObject getObject(String name) {
		try {
			if(!hasObject(name)) throw new NoSuchFieldException
				("The object '" + name + "' does not exist!");
			return objects.get(name);
		} catch(NoSuchFieldException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}

	/**
	 * Gets an array of all objects that are in the
	 * given array object.
	 * @param name The name of the array to get
	 * @return The SSD Array object with all objects in the
	 * 		   given array*/
	public SSDArray getArray(String name) {
		try {
			if(!hasArray(name)) throw new NoSuchFieldException
				("The array '" + name + "' does not exist!");
			
			Map<String, SSDObject> map = new HashMap<>();
			for(Iterator<Entry<String, SSDObject>> it = objects.entrySet().iterator(); it.hasNext();) {
				Entry<String, SSDObject> entry = it.next();
				String entryName 			   = entry.getKey();
				SSDObject entryObject 		   = entry.getValue();
				
				String[] splitEntryName = entryName.split("\\.");
				String joinedEntryName	= String.join(".", Arrays.copyOfRange(
					splitEntryName, 0, name.split("\\.").length));
				
				if(joinedEntryName.equals(name) && !entryName.equals(name))
					map.put(entryName.substring(name.length() + (name.isEmpty() ? 0 : 1)),
							entryObject);
			}
			
			return new SSDArray(this.name + (this.name.isEmpty() ? "" : ".") + name, map);
		} catch(NoSuchFieldException ex) {
			ex.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Gets an array of all existing objects.
	 * @return The SSD Array object with all
	 * 		   existing objects*/
	protected SSDArray getAll() {
		return getArray("");
	}
	
	/**
	 * Sets the object value.
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, String value) {
		String objectName = getTrueName(name);
		objects.put(objectName, new SSDObject(objectName, "\"" + value + "\""));
	}
	
	/**
	 * Sets the object value.
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, int value) {
		String objectName = getTrueName(name);
		objects.put(objectName, new SSDObject(objectName, Integer.toString(value)));
	}
	
	/**
	 * Sets the object value.
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, double value) {
		String objectName = getTrueName(name);
		objects.put(objectName, new SSDObject(objectName, Double.toString(value)));
	}
	
	/**
	 * Sets the object value.
	 * @param name 	The object's name
	 * @param value New object's value*/
	public void setObject(String name, boolean value) {
		String objectName = getTrueName(name);
		objects.put(objectName, new SSDObject(objectName, Boolean.toString(value)));
	}
	
	/**
	 * Sets the object value to null.
	 * @param name 	The object's name*/
	public void setObject(String name) {
		String objectName = getTrueName(name);
		objects.put(objectName, new SSDObject(objectName, SSDType.NULL, "null"));
	}
	
	/**
	 * Sets the object.
	 * @param name 	 The object's name
	 * @param object The object*/
	public void setObject(String name, SSDObject object) {
		objects.put(getTrueName(name), object);
	}
	
	/**
	 * Sets the object value on the array's index.
	 * @param index	The index
	 * @param value New object's value*/
	public void setObject(int index, String value) {
		String objectName = getTrueName(Integer.toString(index));
		objects.put(objectName, new SSDObject(objectName, "\"" + value + "\""));
	}
	
	/**
	 * Sets the object value on the array's index.
	 * @param index	The index
	 * @param value New object's value*/
	public void setObject(int index, int value) {
		String objectName = getTrueName(Integer.toString(index));
		objects.put(objectName, new SSDObject(objectName, Integer.toString(value)));
	}
	
	/**
	 * Sets the object value on the array's index.
	 * @param index	The index
	 * @param value New object's value*/
	public void setObject(int index, double value) {
		String objectName = getTrueName(Integer.toString(index));
		objects.put(objectName, new SSDObject(objectName, Double.toString(value)));
	}
	
	/**
	 * Sets the object value on the array's index.
	 * @param index	The index
	 * @param value New object's value*/
	public void setObject(int index, boolean value) {
		String objectName = getTrueName(Integer.toString(index));
		objects.put(objectName, new SSDObject(objectName, Boolean.toString(value)));
	}
	
	/**
	 * Sets the object value to null on the array's index.
	 * @param index	The index*/
	public void setObject(int index) {
		String objectName = getTrueName(Integer.toString(index));
		objects.put(objectName, new SSDObject(objectName, SSDType.NULL, "null"));
	}
	
	/**
	 * Sets the object on the array's index.
	 * @param index	 The index
	 * @param object The object*/
	public void setObject(int index, SSDObject object) {
		objects.put(getTrueName(Integer.toString(index)), object);
	}
	
	/**
	 * Appends the string object.
	 * @param value Object's value*/
	public void appendObject(String value) {
		String objectName = getTrueName(Integer.toString(currentIndex++));
		objects.put(objectName, new SSDObject(objectName, "\"" + value + "\""));
	}
	
	/**
	 * Appends the number object.
	 * @param value Object's value*/
	public void appendObject(int value) {
		String objectName = getTrueName(Integer.toString(currentIndex++));
		objects.put(objectName, new SSDObject(objectName, Integer.toString(value)));
	}
	
	/**
	 * Appends the floating-point number object.
	 * @param value Object's value*/
	public void appendObject(double value) {
		String objectName = getTrueName(Integer.toString(currentIndex++));
		objects.put(objectName, new SSDObject(objectName, Double.toString(value)));
	}
	
	/**
	 * Appends the boolean object.
	 * @param value Object's value*/
	public void appendObject(boolean value) {
		String objectName = getTrueName(Integer.toString(currentIndex++));
		objects.put(objectName, new SSDObject(objectName, Boolean.toString(value)));
	}
	
	/**
	 * Appends the null object.*/
	public void appendObject() {
		String objectName = getTrueName(Integer.toString(currentIndex++));
		objects.put(objectName, new SSDObject(objectName, SSDType.NULL, "null"));
	}
	
	/**
	 * Appends the object.
	 * @param object The object*/
	public void appendObject(SSDObject object) {
		objects.put(getTrueName(Integer.toString(currentIndex++)), object);
	}
	
	/**
	 * Appends the array (from a map of objects).
	 * @param array The Map (list) of all objects to set*/
	public void appendArray(Map<String, SSDObject> array) {
		objects.putAll(array);
	}
	
	/**
	 * Appends the array.
	 * @param array The array object*/
	public void appendArray(SSDArray array) {
		Map<String, SSDObject> map = new HashMap<>();
		for(Entry<String, SSDObject> entry : array.getAllObjects().entrySet()) {
			String fullName 	= getTrueName(entry.getKey());
			SSDObject object 	= entry.getValue();
			SSDType objectType  = object.type();
			String objectValue	= object.stringValue();
			
			if(objectType == SSDType.STRING)
				objectValue = "\"" + objectValue + "\"";
			SSDObject newObject = new SSDObject(fullName,
				objectType, objectValue);
			map.put(fullName, newObject);
		}
		
		objects.putAll(map);
	}
	
	/**
	 * Checks if the SSD File contains an object
	 * by the given name.
	 * @param name The object's name
	 * @return True, if the object was found, otherwise false*/
	public boolean hasObject(String name) {
		return objects.containsKey(name);
	}
	
	/**
	 * Checks if the SSD File contains an array
	 * by the given name.
	 * @param name The array's name
	 * @return True, if the array was found, otherwise false*/
	public boolean hasArray(String name) {
		for(Entry<String, SSDObject> entry : objects.entrySet()) {
			String objectName = entry.getKey();
			if(!objectName.equals(name) && objectName.startsWith(name))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Removes the object. When the object
	 * does not exist, nothing happens.
	 * @param name The object's name*/
	public void removeObject(String name) {
		objects.remove(getTrueName(name));
	}
	
	/**
	 * Removes the array. When the array
	 * does not exist, nothing happens.
	 * @param name The object's name*/
	public void removeArray(String name) {
		for(Iterator<Entry<String, SSDObject>> it = objects.entrySet().iterator(); it.hasNext();) {
			Entry<String, SSDObject> entry = it.next();
			if(entry.getKey().startsWith(name))
				it.remove();
		}
	}
	
	/**
	 * Clears the whole array.*/
	public void clear() {
		objects.clear();
	}
	
	/**
	 * Gets all objects that are stored in the array.
	 * @return The Map (list) of all stored objects*/
	public Map<String, SSDObject> getAllObjects() {
		return new HashMap<>(objects);
	}
	
	/**
	 * Called when the object should be converted
	 * into a string.
	 * @return The string of the object*/
	@Override
	public String toString() {
		return objects.toString();
	}

	/**
	 * SSD Array iterator. Allows to use SSD Array object
	 * in loops easily.
	 * @author Sune*/
	private class SSDArrayIterator implements Iterator<SSDObject> {
		
		/**
		 * The index of current item*/
		private int index;

		/**
		 * Checks if the array has next item.
		 * @return True, if the array has next item,
		 * 		   otherwise false*/
		@Override
		public boolean hasNext() {
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
		public SSDObject next() {
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
	 * Gets the iterator object.
	 * @return The iterator object*/
	@Override
	public Iterator<SSDObject> iterator() {
		return new SSDArrayIterator();
	}
	
	/**
	 * Gets the array's name.
	 * @return The name of the array*/
	public String getName() {
		return name;
	}
}
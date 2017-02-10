package generator.commands;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.objectweb.asm.ClassReader;

/**
 * ClassLoader that loads Classes from specified locations not in the classpath
 * 
 * @author AMcKee
 *
 */
public class ExternalClassLoader extends ClassLoader {
	private static final boolean ENABLE_CL_DEBUG = false;
	
	private static ExternalClassLoader singleton;
	
	protected Properties classes;

	private Map<URL, byte[]> byteCache;
	
	protected ExternalClassLoader() {
		super(Thread.currentThread().getContextClassLoader());
		this.classes = new Properties();
		this.byteCache = new HashMap<URL, byte[]>();
	}

	
	public static ClassReader getClassReader(String name) throws IOException {
		if (singleton == null)
			ExternalClassLoader.getClassLoader(new Properties());
		try {
			if (singleton.classes.containsKey(name))
				return new ClassReader(singleton.readBytesFromResource(new URI(singleton.classes.getProperty(name)).toURL()));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return new ClassReader(name);
	}
	
	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		
		//System.out.printf("ExternalClassLoader.loadClass(\"%s\", %s)%n", name, (resolve)?"true":"false");
		
		Class<?> clazz = null;
		
		if (this.classes.containsKey(name)) {
			clazz = this.findLoadedClass(name);
			if (clazz == null) {
				try {
					clazz = this.readClassFromURI(name, this.classes.getProperty(name));
					//this.cache.put(name, clazz);
				} catch (Exception e) {
					e.printStackTrace();
					throw new ClassNotFoundException("ClassNotFoundException: Failed due to " + e.getMessage());
				}
			}
		}
		
		if (clazz == null)
			return super.loadClass(name, resolve);
		
		if (resolve)
			this.resolveClass(clazz);
		
		return clazz;
	}
	

	private void registerAll(Properties add) {
		for (Object key : add.keySet()) {
			if (this.classes.containsKey(key)) {
				System.err.printf("Warning: Conflicting definitions for class '%s'!%n\t%s%n\t%s%n%n", 
						this.classes.get(key), add.get(key));
			}
			this.classes.put(key, add.get(key));
		}
	}
	
	public static ExternalClassLoader getClassLoader(Properties add) {
		if (singleton == null) {
			singleton = new ExternalClassLoader();
		}
		singleton.registerAll(add);
		return singleton;
	}
	
	private byte[] readBytesFromResource(URL url) throws IOException {
		if (byteCache.containsKey(url))
			return byteCache.get(url);
		if (ENABLE_CL_DEBUG)
			System.out.println("Reading bytes from URL: " + url.toExternalForm());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream fis = url.openStream();
		byte[] buffer = new byte[4096];
		int read = 0;
		while ((read = fis.read(buffer, 0, 1024)) > -1) {
			bos.write(buffer, 0, read);
		}
		buffer = bos.toByteArray();
		System.out.printf("Read %d bytes from source '%s'%n", buffer.length, url.toExternalForm());
		byteCache.put(url, buffer);
		return buffer;
	}

	private Class<?> readClassFromURI(String key, String path) throws IOException, URISyntaxException {
		if (ENABLE_CL_DEBUG) {
			System.out.printf("Loading class '%s' from URI '%s'...%n", key, new File(new URI(path)).getAbsolutePath());
		}
		
		// load class
		byte[] buffer = this.readBytesFromResource(new URI(path).toURL());
		// define class
		return this.defineClass(key, buffer, 0, buffer.length);
	}
	
}

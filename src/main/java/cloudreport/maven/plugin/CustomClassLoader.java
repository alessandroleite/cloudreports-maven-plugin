package cloudreport.maven.plugin;

import static cloudreport.maven.plugin.util.IOUtil.toURL;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.transform;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.logging.SystemStreamLog;

import com.google.common.base.Function;

public final class CustomClassLoader extends ClassLoader {

	private final Log log = new SystemStreamLog();
	
	private final ClassLoader loader_;

	private final List<Class<?>> classes_ = new LinkedList<Class<?>>();

	public CustomClassLoader(final Collection<URL> artifacts, final String outputDirectory) {
		checkArgument(!isNullOrEmpty(outputDirectory));

		List<URL> urls = new ArrayList<URL> (artifacts);
		urls.add(toURL(outputDirectory));
		
		loader_ = new URLClassLoader(urls.toArray(new URL[urls.size()]), this.getClass().getClassLoader());
		
		for (String name : getClassesName(outputDirectory)) {
			try {
				Class<?> clazz = loader_.loadClass(name);
				
				if (ExtensionTypes.isClassAssignableFrom(clazz, this)) 
				{
					classes_.add(clazz);
				}
			} catch (ClassNotFoundException exception) {
				log.debug("Class " + name + " not found in this class loader!", exception);
			}
		}
	}
	
	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return this.loader_.loadClass(name);
	}
	
	@Override
	public URL getResource(String name) {
		return this.loader_.getResource(name);
	}
	
	@Override
	public InputStream getResourceAsStream(String name) {
		return this.loader_.getResourceAsStream(name);
	}

	public List<Class<?>> extensionClasses() 
	{
		return Collections.unmodifiableList(classes_);
	}
	
	private List<String> getClassesName(final String outputDirectory) {
		List<String> names = new ArrayList<String>();

		names.addAll(transform(listClassesFiles(new File(outputDirectory)),
				new Function<File, String>() {
					@Override
					public String apply(File input) {
						return input.getAbsolutePath()
								.replace(outputDirectory, "")
								.replaceAll("/", "\\.")
								.replaceAll(".class", "").substring(1);
					}
				}));
		return names;
	}

	private Collection<File> listClassesFiles(File directory) {
		Collection<File> files = new ArrayList<File>();

		for (File file : directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				return pathname.isDirectory()
						|| pathname.getName().endsWith(".class");
			}
		})) {
			if (file.isDirectory()) {
				files.addAll(listClassesFiles(file));
			} else {
				files.add(file);
			}
		}
		return files;
	}
}
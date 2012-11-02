package cloudreport.maven.plugin.util;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


public final class IOUtil {

	private IOUtil() {
		throw new UnsupportedOperationException();
	}

	public static void write(String fileName, String content) throws IOException {
		checkArgument(!isNullOrEmpty(fileName));
		
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(fileName)));
			writer.write(content);
			writer.flush();
		} finally {
			closeSilently(writer);
		}
	}

	public static void closeSilently(Closeable resource) {
		if (resource != null)
			try {
				resource.close();
			} catch (IOException ignore) {
			}
	}
	
	public static String addSlashAtTheEnd(String path) 
	{
		checkArgument(!isNullOrEmpty(path));
		
		File f = new File(path);
		
		if (f.isDirectory() && (path.charAt(path.length() - 1) != '/' || path.charAt(path.length() - 1) != '\\'))
		{
			return path + "/";
		}
		return path;
	}
	
	
	public static boolean mkdirs(String path) 
	{
		checkArgument(!isNullOrEmpty(path));
		
		File file = new File(addSlashAtTheEnd(path));
		
		if (!file.exists())
		{
			return file.mkdirs();
		}
		
		return false;
	}
	
	public static URL toURL(String fileName) throws RuntimeException{
		checkArgument(!isNullOrEmpty(fileName));
		try {
			return new File(fileName).toURI().toURL();
		} catch (MalformedURLException exception) {
			throw new RuntimeException(exception);
		}
	}
}
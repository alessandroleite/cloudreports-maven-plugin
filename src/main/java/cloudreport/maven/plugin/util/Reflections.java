package cloudreport.maven.plugin.util;

import java.lang.reflect.Field;

import static com.google.common.base.Preconditions.*;

public final class Reflections {


	private Reflections() {
		throw new UnsupportedOperationException();
	}

	public static Field getField(String name, Class<?> clazz) {
		Field f = null;
		try {
			f = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException execption) {
			if (Object.class.equals(clazz.getSuperclass())) {
				throw new RuntimeException(execption);
			}
			f = getField(name, clazz.getSuperclass());
		}
		return f;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getFieldValue(String fieldName, ClassLoader loader) {
		Field f = getField(checkNotNull(fieldName), checkNotNull(loader).getClass());

		if (f != null) {
			try {
				f.setAccessible(true);
				return (T) f.get(loader);
			} catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}
		return null;
	}

	public static Class<?> asClass(String className) {
		return asClass(className, Reflections.class.getClassLoader());
	}
	
	
	public static Class<?> asClass(String className, ClassLoader loader) throws RuntimeException {
		try {
			return loader.loadClass(className);
		} catch (ClassNotFoundException exception) {
			throw new RuntimeException(exception.getMessage(), exception);
		}
	}
}
package cloudreport.maven.plugin.xml;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

import cloudreport.maven.plugin.ExtensionTypes;

@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlType(propOrder = { "name", "type", "alias", "fileName" }, name = "class")
public class ClassEntry implements Serializable {

	/**
	 * Serial code version <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = -3648848922464386835L;

	@XmlAttribute(required = true)
	private String name;

	@XmlAttribute(required = true)
	private String type;

	@XmlAttribute(required = true)
	private String alias;

	@XmlAttribute(required = true)
	private String fileName;

	public ClassEntry() {
		super();
	}

	public ClassEntry(String name, String type, String alias) {
		this.name = name;
		this.type = type;
		this.alias = alias;
	}

	public ClassEntry(String name, String type, String alias, String fileName) {
		this(name, type, alias);
		this.fileName = fileName;
	}

	public static ClassEntry valueOf(Class<?> clazz) {
		return valueOf(clazz, ClassEntry.class.getClassLoader());
	}

	public static ClassEntry valueOf(Class<?> clazz, ClassLoader cl) {
		return new ClassEntry(clazz.getName(), ExtensionTypes
				.valueOf(clazz, cl).getType(), clazz.getSimpleName());
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}

	/**
	 * @param alias
	 *            the alias to set
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}
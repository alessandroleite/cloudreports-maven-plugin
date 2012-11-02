package cloudreport.maven.plugin.xml;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "classnames")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Classnames implements Serializable {

	/**
	 * Serial code version <code>serialVersionUID</code>
	 */
	private static final long serialVersionUID = 6163183978034700948L;

	@XmlElement(name = "class")
	private List<ClassEntry> classes = new ArrayList<ClassEntry>();

	/**
	 * @return the classes
	 */
	public List<ClassEntry> getClasses() {
		synchronized (this) 
		{
			if (this.classes == null)
				return new ArrayList<ClassEntry>();
		}
		
		return classes;
	}

	/**
	 * @param classes
	 *            the classes to set
	 */
	public void setClasses(List<ClassEntry> classes) 
	{
		this.classes = classes;
	}
}
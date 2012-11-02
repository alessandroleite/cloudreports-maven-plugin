package cloudreport.maven.plugin.xml;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class JAXBMarshallAndUnMarshall<E> {

	/**
	 * Encoding padrão do XML.
	 */
	public static final String DEFAULT_XML_ENCODING = "UTF-8";

	/**
	 * A instância da fábrica DOM que o DAO utiliza.
	 */
	protected final DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

	private Schema schema;

	private String[] contextPath;

	protected static final Map<Object, JAXBContext> JAXB_CONTEXT = new ConcurrentHashMap<Object, JAXBContext>();

	public JAXBMarshallAndUnMarshall() throws JAXBException {
	}

	public JAXBMarshallAndUnMarshall(Class<?>... types) throws JAXBException {
		if (types != null && types.length > 0) {
			synchronized (JAXB_CONTEXT) {
				this.contextPath = new String[] { types[0].getPackage()
						.getName() };
				JAXBMarshallAndUnMarshall.JAXB_CONTEXT.put(
						types[0].getPackage().getName(),
						JAXBContext.newInstance(types));
			}
		}
	}

	public JAXBMarshallAndUnMarshall(Schema schema, String... contextPath)
			throws JAXBException {
		this.contextPath = contextPath;
		this.setSchema(schema);

		if (contextPath != null && contextPath.length > 0) {
			StringBuilder sb = new StringBuilder(contextPath[0]);
			for (int i = 1; i < contextPath.length; i++) {
				sb.append(":" + contextPath[i]);
			}

			synchronized (JAXB_CONTEXT) {
				JAXBMarshallAndUnMarshall.JAXB_CONTEXT.put(contextPath,
						JAXBContext.newInstance(sb.toString()));
			}
		}
	}

	protected void setSchema(Schema schema) {
		this.schema = schema;
		if (!(schema == null)) {
			this.documentFactory.setSchema(schema);
			this.documentFactory.setValidating(Boolean.TRUE);
			documentFactory.setNamespaceAware(Boolean.TRUE);
			documentFactory.setIgnoringComments(Boolean.TRUE);
		}
	}

	public JAXBMarshallAndUnMarshall(Schema schema) {
		setSchema(schema);
	}

	public static Schema getSchema(final URL url) {
		try {
			return SchemaFactory
					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(
							url);
		} catch (SAXException e) {
			return null;
		}
	}

	protected DocumentBuilderFactory getDocumentFactory() {
		return this.documentFactory;
	}

	protected Document getDocument(E type) throws ParserConfigurationException,
			JAXBException {
		Document document = this.getDocumentFactory().newDocumentBuilder()
				.newDocument();
		Marshaller marshaller = JAXBContext.newInstance(type.getClass())
				.createMarshaller();
		marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, DEFAULT_XML_ENCODING);
		marshaller.marshal(type, document);
		return document;
	}

	protected Schema getSchema() {
		return schema;
	}

	private String getContextPath() {
		StringBuilder sb = new StringBuilder(contextPath[0]);
		for (int i = 1; i < contextPath.length; i++) {
			sb.append(":" + contextPath[i]);
		}
		return sb.toString();
	}

	private String getContextPath(String... path) {
		StringBuilder sb = new StringBuilder(path[0]);
		for (int i = 1; i < path.length; i++) {
			sb.append(":" + path[i]);
		}
		return sb.toString();
	}

	protected synchronized JAXBContext getContext(String... contextPath)
			throws JAXBException {
		JAXBContext context;
		if ((context = JAXBMarshallAndUnMarshall.JAXB_CONTEXT.get(contextPath)) == null) {
			JAXBMarshallAndUnMarshall.JAXB_CONTEXT.put(
					contextPath,
					context = JAXBContext
							.newInstance(getContextPath(contextPath)));
		}
		return context;
	}

	protected synchronized JAXBContext getContext() throws JAXBException {
		return this.getContext(this.contextPath);
	}

	protected Marshaller createMarshaller(String packageName)
			throws JAXBException {
		final Marshaller marshaller = getContext(packageName)
				.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		marshaller.setProperty(Marshaller.JAXB_ENCODING, DEFAULT_XML_ENCODING);

		if (!(this.getSchema() == null))
			marshaller.setSchema(getSchema());

		return marshaller;
	}

	protected Marshaller createMarshaller() throws JAXBException {
		return createMarshaller(getContextPath());
	}

	protected Unmarshaller createUnmarshaller(String contextPath)
			throws JAXBException {

		final Unmarshaller unmarshaller = getContext(contextPath)
				.createUnmarshaller();

		if (!(getSchema() == null))
			unmarshaller.setSchema(getSchema());

		return unmarshaller;
	}

	protected Unmarshaller createUnmarshaller() throws JAXBException {
		return this.createUnmarshaller(getContextPath());
	}

	@SuppressWarnings("unchecked")
	public E unmarshal(final String xml, final String charsetName)
			throws JAXBException {
		ByteArrayInputStream input = new ByteArrayInputStream(
				xml.getBytes(Charset.forName((charsetName != null && charsetName
						.isEmpty()) ? charsetName : DEFAULT_XML_ENCODING)));
		try {
			return (E) this.createUnmarshaller().unmarshal(input);
		} finally {
			try {
				input.close();
			} catch (IOException ignore) {
			}
		}
	}

	public E unmarshal(final String xml) throws JAXBException {
		return this.unmarshal(xml, DEFAULT_XML_ENCODING);
	}

	public final String marshall(Object obj) throws JAXBException {
		StringWriter writer = null;
		try {
			writer = new StringWriter();
			createMarshaller().marshal(obj, writer);
			return writer.toString();
		} finally {
			try {
				if (!(writer == null))
					writer.close();
			} catch (IOException exception) {
				// logger.warn("Error in close writer.", exception);
			}
		}
	}

	public Document loadFromXmlFile(File xmlFile) throws IOException,
			ParserConfigurationException, SAXException {
		Document document = this.documentFactory.newDocumentBuilder().parse(
				xmlFile);
		document.normalizeDocument();
		return document;
	}

	@SuppressWarnings("unchecked")
	public final E unmarshall(final File file) throws JAXBException,
			SAXException {
		return (E) this.createUnmarshaller().unmarshal(file);
	}

	@SuppressWarnings("unchecked")
	public final E unmarshall(final InputStream input) throws JAXBException,
			SAXException {
		return (E) this.createUnmarshaller().unmarshal(input);
	}

	public Document getXmlDocumentFromText(final String xmlText)
			throws IOException, ParserConfigurationException, SAXException {
		Document document = this
				.getDocumentFactory()
				.newDocumentBuilder()
				.parse(new ByteArrayInputStream(xmlText
						.getBytes(DEFAULT_XML_ENCODING)));
		document.normalizeDocument();
		return document;
	}

}

package cloudreport.maven.plugin;

import static cloudreport.maven.plugin.util.IOUtil.addSlashAtTheEnd;
import static cloudreport.maven.plugin.util.IOUtil.mkdirs;
import static cloudreport.maven.plugin.util.IOUtil.write;
import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.collect.Collections2.transform;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBException;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import cloudreport.maven.plugin.xml.ClassEntry;
import cloudreport.maven.plugin.xml.Classnames;
import cloudreport.maven.plugin.xml.JAXBMarshallAndUnMarshall;

import com.google.common.base.Function;

/**
 * @version $Id$
 * @goal generate-file
 * @execute phase=process-resources
 * @requiresDependencyResolution test
 * @description Goal which generates the CloudReports extensions file 'classnames.xml' based on source code of the project.
 */
public class ExtensionClassNamesMojo extends AbstractMojo {
	
	private static final String CLASSNAMES_FILE = "classnames.xml";

	// --------------------------------------------------------------------- //
	// - Mojo Parameters - 													 //
	// --------------------------------------------------------------------- //

	/**
	 * @parameter expression="${extensionDir}"
	 */
	 String extensionDir;

	/**
	 * The output jar file.
	 * 
	 * @parameter expression=
	 *            "${project.build.outputDirectory}/${project.build.finalName}.jar"
	 * @required
	 */
	 String outputJar;

	/**
	 * Location of the directory where all files generated by the build are
	 * placed.
	 * 
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	File outputDirectory;

	/**
	 * Location of the file.
	 * 
	 * @parameter expression="${project.build.sourceDirectory}"
	 * @required
	 */
	File sourceDirectory;

	// --------------------------------------------------------------------- //
	// - Mojo Runtime Information 										   - //
	// --------------------------------------------------------------------- //

	/**
	 * Name of the Maven Project
	 * 
	 * @parameter expression="${project}"
	 * @required
	 * @readonly
	 */
	private MavenProject project;

	/** {@inheritDoc} */
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		final Collection<URL> artifacts = transform(
				this.getProjectDependencies(), new Function<Artifact, URL>() {
					@Override
					public URL apply(Artifact input) {
						try {
							return input.getFile().toURI().toURL();
						} catch (MalformedURLException exception) {
							throw new RuntimeException(exception.getMessage(), exception);
						}
					}
				});
		
		CustomClassLoader cl = new CustomClassLoader(artifacts, project.getBuild().getOutputDirectory());
		generateClassNameFile(cl.extensionClasses(), cl);
	}

	void generateClassNameFile(final List<Class<?>> classes, final ClassLoader loader) throws MojoExecutionException {

		if (!classes.isEmpty()) {
			Classnames names = new Classnames();

			names.getClasses().addAll(transform(classes, new Function<Class<?>, ClassEntry>() {
						@Override
						public ClassEntry apply(Class<?> input) {
							ClassEntry entry = ClassEntry.valueOf(input, loader);
							entry.setFileName(String.format("%s.jar", project.getBuild().getFinalName()));
							return entry;
						}
					}));
			try {

				final String content = new JAXBMarshallAndUnMarshall<Classnames>(Classnames.class, ClassEntry.class).marshall(names);
				final String extensionsDir = isNullOrEmpty(this.extensionDir) ? this.project.getBuild().getOutputDirectory() : this.extensionDir;
				mkdirs(extensionsDir);
				write(addSlashAtTheEnd(extensionsDir) + CLASSNAMES_FILE, content);
			} catch (JAXBException exception) {
				throw new MojoExecutionException(exception.getMessage(), exception);
			} catch (IOException exception) {
				throw new MojoExecutionException(exception.getMessage(), exception);
			}
		}
	}

	/**
	 * Return a read-only {@link List} with all dependencies of the project,
	 * including compile, provided, system and runtime scopes.
	 * 
	 * @return A read-only {@link List} with all dependencies of the project,
	 *         including compile, provided, system and runtime scopes.
	 */
	@SuppressWarnings("unchecked")
	List<Artifact> getProjectDependencies() {
		
		Set<Artifact> artifacts = new LinkedHashSet<Artifact>();
		artifacts.addAll(project.getCompileArtifacts());
		artifacts.addAll(project.getRuntimeArtifacts());

		return Collections.unmodifiableList(new LinkedList<Artifact>(artifacts));
	}
}
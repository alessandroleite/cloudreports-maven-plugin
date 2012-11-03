CloudReports Maven Plugin
===================

>Current version: 1.0.0-SNAPSHOT

What is it ?
------------

A [Maven][maven] plugin to generate the file with the extension(s) to be loaded by [CloudReports][cloudreports].

What is a CloudReports Extension?
------------

See [Developing a CloudReport's extension][cloudreports-developing-extensions]

How use it ?
------------

### Plugin Repository

Add to your pom.xml:

```
	<repositories>
		<repository>
			<id>cloudreports-snapshot-repo</id>
			<url>https://github.com/alessandroleite/maven-repository/raw/master/snapshots</url>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</repository>

		<repository>
			<id>cloudreports-repo</id>
			<url>https://github.com/alessandroleite/maven-repository/raw/master/releases</url>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
		</repository>
	</repositories>


	<pluginRepositories>
		<pluginRepository>
			<id>cloudreports-snapshots</id>
			<name>CloudReports Maven Plugin Repository</name>
			<url>https://github.com/alessandroleite/maven-repository/raw/master/snapshots</url>
			<layout>default</layout>
			<snapshots>
				<enabled>true</enabled>
			</snapshots>
		</pluginRepository>

		<pluginRepository>
			<id>cloudreports-releases</id>
			<name>CloudReports Maven Plugin Repository</name>
			<url>https://github.com/alessandroleite/maven-repository/raw/master/releases</url>
			<layout>default</layout>
			<snapshots>
				<enabled>false</enabled>
			</snapshots>
			<releases>
				<updatePolicy>never</updatePolicy>
			</releases>
		</pluginRepository>
	</pluginRepositories>


### Build Configuration

To add the _cloudreports-maven-plugin_ in your project just add the following lines:

```
<plugins>
	<plugin>
		<groupId>cloudreports</groupId>
		<artifactId>cloudreports-maven-plugin</artifactId>
		<version>1.0.0-SNAPSHOT</version>
		<configuration>
			<extensionDir>your cloudreports's extension directory! (Example: /home/m/cloudreports/extensions/)</extensionDir>
		</configuration>
		<executions>
			<execution>
				<phase>package</phase>
				<goals>
					<goal>generate-file</goal>
				</goals>
			</execution>
		</executions>
	</plugin>
</plugins>

How contribute
--------------

### Reporting a Bug / Requesting a Feature

To report an issue or request a new feature you just have to open an issue in the repository issue tracker (<https://github.com/alessandroleite/cloudreports-maven-plugin/issues>).

### Contributing some code

To contribute, follow this steps:

 1. Fork this project
 2. Add the progress label to the issue you want to solve (add a comments to say that you work on it)
 3. Create a topic branch for this issue
 4. When you have finish your work, open a pull request (use the issue title for the pull request title)
 
[maven]: http://maven.apache.org/
[cloudreports]:https://github.com/thiagotts/CloudReports/
[cloudreports-developing-extensions]:https://github.com/thiagotts/CloudReports/wiki/Developing-extensions
 
# A0125975Ureused
###### build.xml
``` xml
<!--this code is adapted from this project https://github.com/pavlobaron/travis-java-playground </!-->
<project name="travis" default="dist" basedir="." xmlns:fx="javafx:com.sun.javafx.tools.ant">
	<description>
        For Travis CI.
    </description>

	<property name="src" location="src" />
	<property name="build" location="build" />
	<property name="dist" location="dist" />
	<property name="test" location="test" />
	<property name="antlib" location="libs/ant-javafx.jar" />
	<property name="junit" location="libs/junit.jar" />

	<path id="classpath.src">
			<pathelement location="${antlib}" />
			<pathelement location="libs/gson lib/gson-2.4.jar" />
			  <fileset dir="libs/">
					    <include name="**/*.jar"/>
				</fileset>
    </path>

	<path id="classpath.test">
		<pathelement location="${junit}" />
		<pathelement location="${test}" />
		<pathelement location="${build}/main" />
		<pathelement location="${build}/test" />

		  <fileset dir="libs/">
				    <include name="**/*.jar"/>
			</fileset>

	</path>

	<target name="init">
		<tstamp />
		<mkdir dir="${build}" />
		<mkdir dir="${build}/test" />
		<mkdir dir="${build}/main" />
	</target>

	<target name="compile" depends="init" description="compile">
		<javac srcdir="${src}" destdir="${build}/main">
			<classpath refid="classpath.src" />
		</javac>


	</target>

	<target name="compile-tests" depends="compile" description="compile-tests">


		<javac srcdir="${test}" destdir="${build}/test">
			<classpath>
				<pathelement location="${build}/main" />
				<pathelement location="${antlib}" />
				<pathelement location="libs/gson lib/gson-2.4.jar" />
				<pathelement location="libs/" />
				<pathelement location="${junit}" />
			</classpath>
		</javac>
	</target>



	<target name="dist" depends="compile">
		<mkdir dir="${dist}/lib" />
		<jar jarfile="${dist}/lib/travis-${DSTAMP}.jar" basedir="${build}" />
	</target>

	<target name="test" depends="compile-tests">
		<junit fork="off" haltonfailure="true">
			<classpath refid="classpath.test" />
			<classpath location="${build}/test" />




			<batchtest fork="yes" todir="tres">
				<formatter type="brief" usefile="false" />
				<fileset dir="${test}">
					<include name="**/*Test*.java" />
				</fileset>
			</batchtest>
		</junit>
	</target>

	<target name="clean" description="clean up">
		<delete dir="${build}" />
		<delete dir="${dist}" />
	</target>
</project>
```
###### src\application\utils\GoogleCalendarUtility.java
``` java
import java.io.BufferedReader;
```
###### src\application\utils\GoogleCalendarUtility.java
``` java
	/**
	 * Disclaimer : this method is from :
	 * http://www.journaldev.com/833/how-to-delete-a-directoryfolder-in-java-
	 * recursion
	 * 
	 * @param file
	 */
	public static void recursiveDelete(File file) {
		// to end the recursive loop
		if (!file.exists())
			return;

		// if directory, go inside and call recursively
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				// call recursively
				recursiveDelete(f);
			}
		}
		// call delete to delete files and empty directory
		file.delete();
	}

}
```

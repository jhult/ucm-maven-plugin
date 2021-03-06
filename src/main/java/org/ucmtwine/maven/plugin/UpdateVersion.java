package org.ucmtwine.maven.plugin;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.ucmtwine.maven.plugin.FileUpdateHelper.replaceLine;

/**
 *    Increments the Maven Version Number 
 *    and synchronizes the component version with the maven version. 
 *    <p>
 *    <strong>Additionally:</strong> 
 *    Updates the Create Date to the current date/time.
 *    </p>
 *    
 *    @author txburton
 */
@Mojo(name = StringConstants.UPDATE_VERSION,
      defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
      requiresDependencyResolution = ResolutionScope.COMPILE)
//@Execute(goal = "compile", phase = LifecyclePhase.COMPILE)
public class UpdateVersion extends AbstractComponentMojo
{     
   /** Current Build-number */
   @Parameter(defaultValue="${buildNumber}")
   protected String buildNumber;

   /** Current Build-number */
   @Parameter(defaultValue="${project.version}")
   protected String version;
   
  /**
   *  Update ComponentName.hda "version" w/ Maven version String 
   *         (replace '.' W/ '_' for compatibility)
   *  Update manifest.hda "CreateDate" w/ timestamp
   *         (Format "M/d/yy h:mm aa" (EG: 5/2/16 2:16 PM) )
   */
  public void execute() throws MojoExecutionException {
     // find componentName
     determineComponentName();
     
     /* parse current version for increment */
     // @formatter:off
     executeMojo(plugin("org.codehaus.mojo", "build-helper-maven-plugin", "1.12"),
                 goal("parse-version"), configuration(),
                 executionEnvironment(project, session, pluginManager));
     // @formatter:on
     
     final Properties props = project.getProperties();
     final StringBuilder verbuilder = new StringBuilder(4);
     verbuilder.append(props.getProperty("parsedVersion.majorVersion")).append(".")
               .append(props.getProperty("parsedVersion.minorVersion")).append(".")
               .append(props.getProperty("parsedVersion.nextIncrementalVersion"))
               .append("-").append(props.getProperty("parsedVersion.qualifier"));
    final String nextVersion = verbuilder.toString();
     
     /* increment current version in pom */
     // @formatter:off
     executeMojo(plugin("org.codehaus.mojo", "versions-maven-plugin", "2.3" ),
                 goal("set"), 
                 configuration(element(name("newVersion"), nextVersion)),
                 executionEnvironment(project, session, pluginManager));
     //commit (clean-up after) the change
     /*
     executeMojo(plugin("org.codehaus.mojo", "versions-maven-plugin", "2.3" ),
                 goal("commit"), configuration(),
                 executionEnvironment(project, session, pluginManager));
     */
     // @formatter:on
     
     getLog().info("InitVersion: " + version);
     version = nextVersion.replace(".", "_");
     getLog().info("Synchronizing version to: " + version);

     final File componentHDA = new File(componentName+".hda");
     
     try { replaceLine("version", version, componentHDA); }
     catch(final IOException ioe)
     { getLog().warn("Error Updating Component Version.", ioe); }
     
     final File manifest = getManifestFile();
     final SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy h:mm aa");
     final String createDate = sdf.format(new Date());
     try { replaceLine("CreateDate", createDate, manifest); }
     catch(final IOException ioe)
     { getLog().warn("Error Updating Component Create Date.", ioe); }
  }
  
}
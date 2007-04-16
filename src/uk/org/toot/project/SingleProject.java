// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

/**
 * Simple-minded single project manager.
 * Typically certain parts of an overall system will need to know
 * when the current project changes so that they may save and load
 * relevant settings.
 * Projects each have a directory path based on their name.
 * i.e. ~/<applicationName>/projects/<projectName>
 * Directories are automatically created.
 *
 * Note that there is no single project file, just a project directory into
 * which application components should write their relevant files.
 * There is a concurrency weakness if listeners are added
 * or removed during an open or close, so don't do that.
 *
 * Suggested usage is to have a single instance in your
 * application and to pass it into the constructor of application
 * components. Those components should then typically create a private
 * ProjectListener instance and add and remove it as appropriate.
 *
 * When the current project is changed,
 * if there is an existing current project, it is closed,
 * the new current project is opened.
 * When a project is opened the open() method of all ProjectListeners is called.
 * Similarly their close() methods are called when a project is closed.
 * No arguments are passed to the ProjectListeners, they should call
 * getCurrentProjectName() and/or getCurrentProjectPath() as appropriate.
 */
public class SingleProject
{
    private Properties currentProjectProperties = null;
	private String currentProjectTitle = "unknown";
    private String currentProjectArtist = "unknown";
    private File applicationPath; 		// e.g. ~/toot/
    private File projectsPath;			// e.g. ~/toot/projects/
    private File currentProjectPath;	// e.g. ~/toot/projects/default/
    private List<ProjectListener> listeners;

    public SingleProject() {
        this("toot");
    }

    public SingleProject(String appDir) {
        this(new File(System.getProperty("user.home"), appDir));
    }

    public SingleProject(File appPath) {
        applicationPath = appPath;
        applicationPath.mkdirs();
        // provisional projects root
        setProjectsRoot(applicationPath.getPath());
        // probably no point having a project if nothing listening
        // so we create a listener list non-lazily to simplify implementation
		listeners = new java.util.ArrayList<ProjectListener>();
    }

    public void openProject(String name) {
        if ( name == null ) return; // silently ignore
        currentProjectPath = new File(projectsPath, name);
        if ( !currentProjectPath.exists() ) {
            currentProjectPath.mkdirs();
        }
        // if the path doesn't exist
        // should create new directory, open default, save as new !!! !!!
        currentProjectProperties = new Properties();
        File propertyFile = new File(currentProjectPath, "project.properties");
        if ( propertyFile.exists() ) {
            try {
//        		System.out.println(propertyFile.getPath()+" loaded");
		        FileInputStream fis = new FileInputStream(propertyFile);
    		    currentProjectProperties.load(fis);
            } catch ( FileNotFoundException fnfe ) {
            } catch ( IOException ioe ) {
            }
    	} else {
        	System.out.println(propertyFile.getPath()+" not found");
    	}
    	// if there's no title property we use the project name
        currentProjectTitle = currentProjectProperties.getProperty("title", name);
        currentProjectArtist = currentProjectProperties.getProperty("artist", "unknown");

        for ( ProjectListener l : listeners ) {
            l.open();
        }
    }

    public void saveProject() {
        if ( !canSaveProject() ) return; // silently ignore
        // check there's somewhere to save projects
        if ( !projectsPath.exists() ) {
	        projectsPath.mkdirs();
    	    System.out.println("Created projects path "+projectsPath.getPath());
        }
        for ( ProjectListener l : listeners ) {
            l.save();
        }
    }

    public void saveAsProject(String name) {
        currentProjectPath = new File(projectsPath, name);
        saveProject();
    }

    /**
     * Import the named project from the specified file.
     * i.e. project is uncompressed from file.
     */
    public void importProject(String name, File file) {
        if ( name == null ) return; // silently ignore
        File projectPath = new File(projectsPath, name);
        if ( projectPath.exists() ) return; // !!! already exists
    }

    /**
     * Export the named project to the specified file.
     * i.e. project is compressed to file.
     */
    public void exportProject(String name) {
        if ( name == null ) return; // silently ignore
        File projectPath = new File(projectsPath, name);
        if ( !projectPath.exists() ) return; // !!! doesn't exist
    }

    public boolean canOpenProject() {
        return true;
    }

    public boolean canSaveProject() {
        return currentProjectProperties != null;
    }

    // for cases when files are not related to a single project
    public File getApplicationPath() {
        return applicationPath;
    }

    public String getCurrentProjectTitle() {
        return currentProjectTitle;
    }

    public String getCurrentProjectArtist() {
        return currentProjectArtist;
    }

    public File getCurrentProjectPath() {
        return currentProjectPath;
    }

    public void setProjectsRoot(String path) {
        projectsPath = new File(path, "projects");
    }

    public File getProjectsRoot() {
        return projectsPath;
    }

    public void addProjectListener(ProjectListener listener) {
        if ( !listeners.contains(listener) ) {
        	listeners.add(listener);
        }
    }

    public void removeProjectListener(ProjectListener listener) {
        if ( listeners.contains(listener) ) {
			listeners.remove(listener);
        }
    }
}

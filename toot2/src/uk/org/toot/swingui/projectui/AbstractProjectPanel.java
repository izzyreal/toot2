// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.projectui;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import uk.org.toot.project.*;
import uk.org.toot.swing.DisposablePanel;

abstract public class AbstractProjectPanel extends DisposablePanel
{
    private SingleProject project;
    private ProjectListener projectListener;
    private JFileChooser fileChooser;
    protected Action openAction;
    protected Action saveAction;
//    protected Action saveAsAction;

    public AbstractProjectPanel(SingleProject p) {
        project = p;
        openAction = new OpenAction();
        saveAction = new SaveAction();
//        saveAsAction = new SaveAsAction();
        projectListener = new ProjectListener() {
            public void open() {
	            updateTitle();
            }
            public void save() {
	            updateTitle();
            }
        };
        fileChooser = new JFileChooser(project.getProjectsRoot());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
    }

    protected void dispose() {
        saveIfConfirmed();
        projectListener = null;
        removeAll();
    }

    protected void saveIfConfirmed() {
        if ( !project.canSaveProject() ) return;
        // confirm whether project should be saved
        // should be cancellable, called earlier
        int opt = JOptionPane.showConfirmDialog(this,
            "Save "+project.getCurrentProjectTitle(),
            "Toot", JOptionPane.YES_NO_OPTION);
        if ( opt == JOptionPane.YES_OPTION ) {
        	project.saveProject(); // !!! and close PD !!! !!! ???
        }
    }

    public void addNotify() {
        super.addNotify();
        project.addProjectListener(projectListener);
        updateTitle();
    }

    public void removeNotify() {
        project.removeProjectListener(projectListener);
        super.removeNotify();
    }

    protected void updateTitle() {
        String name = project.getCurrentProjectTitle() +
            " / " + project.getCurrentProjectArtist();
        if ( getTopLevelAncestor() instanceof Frame ) {
            ((Frame)getTopLevelAncestor()).setTitle(name+" - Toot");
    	}
    }

    protected class OpenAction extends AbstractAction
    {
        public OpenAction() {
            super("Open"); // icon required
        }

        public void actionPerformed(ActionEvent ae) {
            // need to select directory in projects directory
            int ret = fileChooser.showOpenDialog(AbstractProjectPanel.this);
            if ( ret == JFileChooser.APPROVE_OPTION) {
				saveIfConfirmed();
            	project.openProject(fileChooser.getSelectedFile().getName());
            }
        }
    }

    protected class SaveAction extends AbstractAction
    {
        public SaveAction() {
            super("Save"); // icon required
        }

        public void actionPerformed(ActionEvent ae) {
            if ( project.canSaveProject() ) {
            	project.saveProject();
            }
        }
    }


/*    protected class SaveAsAction extends AbstractAction
    {
        public SaveAsAction() {
            super("Save As"); // icon required
        }

        public void actionPerformed(ActionEvent ae) {
            String name = "Copy of "+project.getCurrentProjectName(); // !!! !!!
            project.saveAsProject(name);
        }
    } */
}

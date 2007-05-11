// Copyright (C) 2006 Steve Taylor.
// Distributed under the Toot Software License, Version 1.0. (See
// accompanying file LICENSE_1_0.txt or copy at
// http://www.toot.org/LICENSE_1_0.txt)

package uk.org.toot.swingui.projectui;

import uk.org.toot.project.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Component;

public class SingleProjectPanel extends AbstractProjectPanel 
{
    protected JToolBar toolBar;
    private JTabbedPane tabbedPane;

    public SingleProjectPanel(SingleProject p, JToolBar toolBar) {
        super(p);
        setLayout(new BorderLayout());
        // add toolbar open/save/saveAs
        toolBar.putClientProperty("JToolBar.isRollover", Boolean.TRUE);
        toolBar.add(openAction);
        toolBar.add(saveAction);
//        toolBar.add(saveAsAction);

        tabbedPane = new JTabbedPane();

        add(toolBar, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    protected void dispose() {
// !!! !!!       toolBar.removeAll();
// !!! !!!        toolBar = null;
        tabbedPane.removeAll();
        tabbedPane = null;
        removeAll();
        super.dispose();
    }

    public void addTab(String title, Component comp) {
        tabbedPane.addTab(title, comp);
    }
}

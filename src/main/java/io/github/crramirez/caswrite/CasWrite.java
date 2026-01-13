/*
 * CasWrite - Text editor with table support based on Casciian
 *
 * Copyright 2025 Carlos Rafael Ramirez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.crramirez.caswrite;

import casciian.TApplication;
import casciian.TEditorWindow;
import casciian.TTableWindow;
import casciian.TWidget;
import casciian.event.TMenuEvent;
import casciian.menu.TMenu;
import casciian.menu.TMenuItem;

import java.io.File;
import java.io.IOException;

/**
 * CasWrite - A text editor with table support.
 * 
 * This is a TUI text editor that can open text files in editor windows
 * and CSV files in table windows, with support for multiple overlapping
 * windows and mouse operations.
 */
public class CasWrite extends TApplication {

    // Custom menu item IDs
    private static final int MID_OPEN_AS_TABLE = 2000;

    /**
     * Constructor.
     *
     * @throws Exception if any error occurs
     */
    public CasWrite() throws Exception {
        super(BackendType.XTERM);
        
        // Add all menus
        addFileMenuWithTable();
        addEditMenu();
        addSystemMenu();
        addWindowMenu();
        addTableMenu();
    }

    /**
     * Add the File menu with custom options.
     */
    private void addFileMenuWithTable() {
        TMenu fileMenu = addFileMenu();
        
        // Add "Open as Table" option after "Open File"
        fileMenu.addItem(MID_OPEN_AS_TABLE, "&Open as Table...");
    }

    /**
     * Add the System menu with OS shell option.
     */
    private void addSystemMenu() {
        TMenu systemMenu = addMenu("&System");
        systemMenu.addItem(TMenu.MID_SHELL, "&Shell");
    }

    /**
     * Handle menu events.
     *
     * @param menu menu event
     */
    @Override
    public boolean onMenu(TMenuEvent menu) {
        switch (menu.getId()) {
            case MID_OPEN_AS_TABLE:
                openAsTable();
                return true;
            default:
                return super.onMenu(menu);
        }
    }

    /**
     * Open a file in a table window.
     */
    private void openAsTable() {
        try {
            String filename = fileOpenBox(".");
            if (filename != null) {
                File file = new File(filename);
                if (file.exists() && file.isFile()) {
                    new TTableWindow(this, file);
                }
            }
        } catch (IOException e) {
            messageBox("Error", "Error opening file: " + e.getMessage());
        }
    }

    /**
     * Main entry point.
     *
     * @param args Command line arguments (optional filename to open)
     */
    public static void main(String[] args) {
        try {
            CasWrite app = new CasWrite();
            
            // If a filename was provided as an argument, open it
            if (args.length > 0) {
                File file = new File(args[0]);
                if (file.exists() && file.isFile()) {
                    // Determine if it's a CSV file or text file
                    String filename = file.getName().toLowerCase();
                    if (filename.endsWith(".csv") || filename.endsWith(".tsv")) {
                        new TTableWindow(app, file);
                    } else {
                        new TEditorWindow(app, file);
                    }
                }
            }
            
            (new Thread(app)).start();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

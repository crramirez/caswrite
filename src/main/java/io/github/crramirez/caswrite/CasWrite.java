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
import casciian.TCommand;
import casciian.TEditor;
import casciian.TEditorWindow;
import casciian.TMessageBox;
import casciian.TTableWindow;
import casciian.TWidget;
import casciian.TWindow;
import casciian.event.TCommandEvent;
import casciian.event.TMenuEvent;
import casciian.menu.TMenu;

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

    // File type detector for determining how to open files
    private final FileTypeDetector fileTypeDetector = new FileTypeDetector();

    // Track previous menu states to avoid unnecessary updates
    // volatile ensures visibility across threads (main thread vs application thread)
    private volatile boolean lastSaveEnabled = false;
    private volatile boolean lastSaveAsEnabled = false;

    /**
     * Constructor.
     *
     * @throws Exception if any error occurs
     */
    public CasWrite() throws Exception {
        super(BackendType.XTERM);
        
        // Add all menus
        addSystemMenu();
        addFileMenuWithTable();
        addEditMenu();
        addTableMenu();
        addWindowMenu();

        // Initialize menu states (no windows at startup)
        disableMenuItem(TMenu.MID_SAVE_FILE);
        disableMenuItem(TMenu.MID_SAVE_AS_FILE);
    }

    /**
     * Add the File menu with custom options.
     */
    private void addFileMenuWithTable() {
        TMenu fileMenu = addMenu("&File");
        
        fileMenu.addDefaultItem(TMenu.MID_NEW);
        fileMenu.addDefaultItem(TMenu.MID_OPEN_FILE);
        fileMenu.addItem(MID_OPEN_AS_TABLE, "&Open as Table...");
        fileMenu.addDefaultItem(TMenu.MID_SAVE_FILE);
        fileMenu.addDefaultItem(TMenu.MID_SAVE_AS_FILE);
        fileMenu.addSeparator();
        fileMenu.addDefaultItem(TMenu.MID_EXIT);
    }

    /**
     * Add the System menu with OS shell option.
     */
    private void addSystemMenu() {
        TMenu systemMenu = addToolMenu();
        systemMenu.addDefaultItem(TMenu.MID_SHELL);
    }

    /**
     * Handle menu events.
     *
     * @param menu menu event
     */
    @Override
    public boolean onMenu(TMenuEvent menu) {
        switch (menu.getId()) {
            case TMenu.MID_NEW:
                createNewEditor();
                return true;
            case TMenu.MID_OPEN_FILE:
                openFile();
                return true;
            case MID_OPEN_AS_TABLE:
                openAsTable();
                return true;
            case TMenu.MID_SAVE_FILE:
                postMenuEvent(new TCommandEvent(menu.getBackend(), TCommand.cmSave));
                return true;
            case TMenu.MID_SAVE_AS_FILE:
                postMenuEvent(new TCommandEvent(menu.getBackend(), TCommand.cmSaveAs));
                return true;
            default:
                return super.onMenu(menu);
        }
    }

    /**
     * Handle command events.
     *
     * @param command command event
     * @return true if the command was handled, false otherwise
     */
    @Override
    protected boolean onCommand(TCommandEvent command) {
        if (command.getCmd().equals(TCommand.cmWindowClose)) {
            TWindow activeWindow = getActiveWindow();
            if (activeWindow instanceof TEditorWindow) {
                TEditor editor = findEditor((TEditorWindow) activeWindow);
                if (editor != null && editor.isDirty()) {
                    TMessageBox.Result result = messageBox(
                        "Save Changes?",
                        "The file has unsaved changes. Do you want to save before closing?",
                        TMessageBox.Type.YESNOCANCEL
                    ).getResult();

                    switch (result) {
                        case YES:
                            // Save the file (synchronous); only close if editor is no longer dirty
                            activeWindow.onCommand(new TCommandEvent(
                                command.getBackend(), TCommand.cmSave));
                            // Check if the save succeeded by verifying the editor is no longer dirty
                            if (!editor.isDirty()) {
                                closeWindow(activeWindow);
                            } else {
                                // Save failed or was cancelled, keep the window open
                                messageBox(
                                    "Save Failed",
                                    "The file could not be saved. The window will remain open.",
                                    TMessageBox.Type.OK
                                ).getResult();
                            }
                            return true;
                        case NO:
                            // Close without saving
                            closeWindow(activeWindow);
                            return true;
                        case CANCEL:
                        default:
                            // Don't close
                            return true;
                    }
                }
            }
        }
        return super.onCommand(command);
    }

    /**
     * Find the TEditor widget within a TEditorWindow.
     *
     * @param window the editor window to search
     * @return the TEditor widget, or null if not found
     */
    private TEditor findEditor(TEditorWindow window) {
        for (TWidget child : window.getChildren()) {
            if (child instanceof TEditor) {
                return (TEditor) child;
            }
        }
        return null;
    }

    /**
     * Create a new empty editor window.
     */
    private void createNewEditor() {
        new TEditorWindow(this);
    }

    /**
     * Open a file in an appropriate window (editor or table based on extension).
     */
    private void openFile() {
        try {
            String filename = fileOpenBox(".");
            if (filename != null) {
                File file = new File(filename);
                openFileInWindow(file);
            }
        } catch (IOException e) {
            messageBox("Error Opening File", "Error opening file: " + e.getMessage());
        }
    }

    /**
     * Open a file in the appropriate window type based on its extension.
     *
     * @param file the file to open
     * @throws IOException if an error occurs opening the file
     */
    private void openFileInWindow(File file) throws IOException {
        openFileInWindow(file, false);
    }

    /**
     * Open a file in the appropriate window type.
     *
     * @param file the file to open
     * @param forceTable if true, always open as table regardless of extension
     * @throws IOException if an error occurs opening the file
     */
    private void openFileInWindow(File file, boolean forceTable) throws IOException {
        if (!file.exists()) {
            throw new IOException("File does not exist: " + file.getPath());
        }
        if (!file.isFile()) {
            throw new IOException("Not a regular file: " + file.getPath());
        }
        // Determine if it's a CSV/TSV file or text file
        if (forceTable || fileTypeDetector.isTableFile(file.getName())) {
            new TTableWindow(this, file);
        } else {
            new TEditorWindow(this, file);
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
                openFileInWindow(file, true);
            }
        } catch (IOException e) {
            messageBox("Error Opening File", "Error opening file: " + e.getMessage());
        }
    }

    /**
     * Called before drawing the screen. Updates menu item states based on
     * current application state.
     */
    @Override
    protected void onPreDraw() {
        super.onPreDraw();
        updateMenuStates();
    }

    /**
     * Update the enabled/disabled state of menu items based on current
     * application state. Only updates menu items when their state changes.
     */
    private void updateMenuStates() {
        boolean hasWindow = windowCount() > 0;
        boolean shouldEnableSave = hasWindow && hasUnsavedChangesInActiveWindow();
        boolean shouldEnableSaveAs = hasWindow;

        // Only update menu items when state changes
        if (shouldEnableSave != lastSaveEnabled) {
            if (shouldEnableSave) {
                enableMenuItem(TMenu.MID_SAVE_FILE);
            } else {
                disableMenuItem(TMenu.MID_SAVE_FILE);
            }
            lastSaveEnabled = shouldEnableSave;
        }

        if (shouldEnableSaveAs != lastSaveAsEnabled) {
            if (shouldEnableSaveAs) {
                enableMenuItem(TMenu.MID_SAVE_AS_FILE);
            } else {
                disableMenuItem(TMenu.MID_SAVE_AS_FILE);
            }
            lastSaveAsEnabled = shouldEnableSaveAs;
        }
    }

    /**
     * Check if the active window has unsaved changes.
     *
     * @return true if the active window has unsaved changes, false otherwise
     */
    private boolean hasUnsavedChangesInActiveWindow() {
        TWindow activeWindow = getActiveWindow();
        if (activeWindow == null) {
            return false;
        }

        // For TEditorWindow, find the TEditor child and check if it's dirty
        if (activeWindow instanceof TEditorWindow) {
            TEditor editor = findEditor((TEditorWindow) activeWindow);
            if (editor != null) {
                return editor.isDirty();
            }
        }

        // For TTableWindow, we assume it can always be saved (no isDirty method available)
        // So return true to keep Save enabled
        if (activeWindow instanceof TTableWindow) {
            return true;
        }

        return false;
    }

    /**
     * Main entry point.
     *
     * @param args Command line arguments (optional filenames to open)
     */
    public static void main(String[] args) {
        try {
            CasWrite app = new CasWrite();
            
            // Start the application thread first to ensure the event loop is running
            Thread appThread = new Thread(app, "CasWrite-Main");
            appThread.start();
            
            // Schedule file opening to run on the application thread
            // This avoids race conditions by ensuring UI operations happen
            // in the application's event loop after it has started
            if (args.length > 0) {
                app.invokeLater(() -> {
                    for (String arg : args) {
                        File file = new File(arg);
                        try {
                            app.openFileInWindow(file);
                        } catch (Exception e) {
                            // Show error message to user via message box
                            app.messageBox("Error Opening File",
                                "Failed to open '" + file.getName() + "': " + e.getMessage());
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}

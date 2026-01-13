# CasWrite Features

## Overview

CasWrite is a TUI (Text User Interface) text editor with integrated spreadsheet/table editing capabilities. It provides a familiar menu-driven interface similar to traditional desktop applications, but runs entirely in a terminal.

## Application Interface

```
┌─────────────────────────────────────────────────────────────────────────┐
│ File  Edit  System  Window  Table                                       │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                           │
│  ┌─── myfile.txt ───────────────────┐  ┌─── data.csv ──────────────┐  │
│  │                                   │  │ Name      | Age  | City   │  │
│  │  Hello, this is a text file!     │  ├───────────┼──────┼────────┤  │
│  │                                   │  │ John Doe  |  30  | NYC    │  │
│  │  Line 3                           │  │ Jane Smith|  25  | London │  │
│  │  Line 4                           │  │ Bob Jones |  35  | Paris  │  │
│  │                                   │  └───────────┴──────┴────────┘  │
│  │                                   │                                  │
│  └───────────────────────────────────┘                                  │
│                                                                           │
└─────────────────────────────────────────────────────────────────────────┘
```

## Menu Structure

### File Menu
- **New** - Create a new empty editor window
- **Open File** - Open a file in an editor window
- **Open as Table** - Open a CSV/TSV file in a table window
- **Save** - Save the active file
- **Save As** - Save with a new filename
- **Shell** - Access to OS shell
- **Exit** - Quit the application

### Edit Menu
Standard editing operations that work in the active window:
- **Undo/Redo** - Revert or reapply changes
- **Cut/Copy/Paste** - Clipboard operations
- **Clear** - Delete selected content
- **Find/Replace** - Search functionality
- **Go to Line** - Navigate to specific line number

### System Menu
- **Shell** - Launch an OS command shell within the application

### Window Menu
Manage multiple open windows:
- **Tile** - Arrange windows in a grid
- **Cascade** - Stack windows with offset
- **Close All** - Close all open windows
- **Next/Previous** - Cycle through windows

### Table Menu
Operations specific to table/spreadsheet windows:
- **Rename Row/Column** - Edit labels
- **View Options** - Toggle row/column labels, highlighting
- **Border Styles** - Customize table appearance

## File Type Handling

### Text Files
The following file types open in **TEditorWindow** (text editor):
- `.txt` - Plain text
- `.md` - Markdown
- `.java`, `.kt`, `.py`, `.js`, `.c`, `.cpp` - Source code
- Any file without `.csv` or `.tsv` extension

Features:
- Syntax-aware editing
- Line numbers
- Full clipboard support
- Find/replace
- Multi-level undo/redo

### Table Files
The following file types open in **TTableWindow** (spreadsheet):
- `.csv` - Comma-separated values
- `.tsv` - Tab-separated values

Features:
- Cell-based editing
- Row and column labels
- Spreadsheet navigation
- Border customization
- Import/export CSV format

## Command Line Usage

```bash
# Launch CasWrite with empty desktop
caswrite

# Open a specific file
caswrite myfile.txt

# Open multiple files
caswrite file1.txt file2.txt

# Open CSV in table view (automatic)
caswrite data.csv

# Mix text and table files
caswrite readme.md data.csv notes.txt
```

## Key Features

### 1. Multiple Overlapping Windows
Work with multiple files simultaneously in separate windows that can overlap, be moved, resized, and managed independently.

### 2. Mouse Support
Full mouse support for:
- Window selection and focus
- Text selection in editor
- Cell selection in tables
- Menu interaction
- Window dragging and resizing

### 3. Dual Mode Editing
Seamlessly work with both text files and spreadsheet data in the same application session.

### 4. System Integration
Access OS shell without leaving the editor, allowing you to run commands, check file status, or execute scripts.

### 5. Familiar Interface
Menu-driven design similar to traditional desktop applications, making it easy to learn for users familiar with GUI text editors.

## Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| Alt+F | Open File menu |
| Alt+E | Open Edit menu |
| Alt+S | Open System menu |
| Alt+W | Open Window menu |
| Alt+T | Open Table menu |
| Ctrl+O | Open file |
| Ctrl+S | Save file |
| Ctrl+Q | Quit application |
| Ctrl+X | Cut |
| Ctrl+C | Copy |
| Ctrl+V | Paste |
| F3 | Find |
| Ctrl+F | Find (alternate) |
| Alt+X | Exit |

## Comparison with Traditional Editors

CasWrite bridges the gap between simple terminal editors (nano) and complex ones (vim/emacs), while adding unique spreadsheet capabilities:

**vs. nano:**
- ✅ Similar ease of use
- ✅ More features (multiple windows, table editing)
- ✅ Better mouse support
- ✅ More powerful window management

**vs. vim:**
- ✅ No modal editing - easier to learn
- ✅ Native spreadsheet support
- ✅ Visual menu system
- ✅ Better for mixed text/data workflows

**vs. GUI editors (gedit, notepad++):**
- ✅ Runs in terminal - works over SSH
- ✅ Lower resource usage
- ✅ Fast startup
- ❌ No pixel-perfect graphics (but full Unicode support)

## Use Cases

1. **System Administration** - Edit config files and review log data in tables
2. **Data Analysis** - View CSV data while writing analysis scripts
3. **Remote Editing** - Full-featured editor over SSH connections
4. **Mixed Workflows** - Work with documentation and data simultaneously
5. **Terminal-First Environments** - When GUI isn't available or desired
6. **Quick Edits** - Fast startup for simple text editing tasks

## Technical Details

- **Built on**: [Casciian](https://github.com/crramirez/casciian) TUI library
- **Language**: Java 21
- **Platform**: Any system with terminal supporting Xterm sequences
- **License**: Apache 2.0
- **Terminal Requirements**: 
  - Xterm-compatible terminal
  - UTF-8 support recommended
  - Mouse reporting support (optional but recommended)

## Building and Installation

See [README.md](README.md) for detailed build and installation instructions.

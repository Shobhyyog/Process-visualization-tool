# Process Visualization Tool

A Java-based application for creating and managing visual process workflows. This tool allows users to create, connect, and organize different types of process nodes in a graphical interface.

## Overview

This application is divided into three main modules:

1. **User Interface (GUI) Module** (`UserInterfaceModule.java`)
   - Handles all user interface components
   - Manages toolbar and drawing area
   - Processes user interactions

2. **Node Management Module** (`NodeManagementModule.java`)
   - Handles creation and management of different node types
   - Manages node movement and deletion
   - Controls node visual representation

3. **Connection Management Module** (`ConnectionManagementModule.java`)
   - Manages connections between nodes
   - Handles arrow rendering and direction
   - Controls connection validation

## Features

- Interactive graphical interface
- Multiple node types (Process, Decision, Start/End, Input/Output)
- Drag-and-drop functionality
- Visual node connections with arrows
- Real-time feedback
- Node deletion with connection cleanup

## Requirements

- Java Runtime Environment (JRE) 8 or higher
- Swing and AWT libraries (included in JRE)

## Getting Started

1. Run the application:
   ```bash
   java ProcessVisualizationTool
   ```

2. Using the application:
   - Select node type from dropdown
   - Click "Add Node" to place nodes
   - Use "Connect Nodes" to create connections
   - Drag nodes to reposition
   - Use "Delete" to remove nodes
   - "Clear All" resets workspace

## Project Structure

```
src/
├── UserInterfaceModule.java     # GUI components and event handling
├── NodeManagementModule.java    # Node types and management
└── ConnectionManagementModule.java  # Connection handling and validation
```

For detailed documentation of each module, please refer to their respective files.

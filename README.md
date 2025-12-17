# OSE-Final-project

## Project Overview
Event Management System - A JavaFX desktop application for managing event information with CSV/JSON file persistence.

## Design Choices
- **MVC Architecture**: Controller handles UI logic, Model represents event data
- **ObservableList**: For automatic TableView updates
- **File Persistence**: Dual format support (CSV and JSON)
- **Auto-save**: Automatic backup on application close

## Challenges Faced
1. CSV parsing with quoted fields containing commas
2. Coordinating multiple file format imports/exports
3. Ensuring thread safety with ObservableList operations
4. Handling empty/malformed auto-save files gracefully

## Video Demonstration
[Link to demonstration video]

## Algorithms and Data Structures
- **CSV Parser**: State machine for handling quoted fields
- **Event List Management**: ObservableList for reactive UI updates
- **File I/O**: Buffered readers/writers for efficient file operations
- **Data Validation**: Input checking and number parsing

## Improvements Made
1. **Unified Auto-save System**: Single method handling both formats
2. **Error Resilience**: Graceful handling of corrupted files
3. **User Feedback**: Informative alerts for all operations
4. **Data Validation**: Comprehensive input checking

## Input/Output File Usage
### Supported Formats:
- **CSV**: Comma-separated with quoted text fields
- **JSON**: Standard JSON array of event objects

### File Operations:
- Manual save/load in both formats
- Automatic backup on exit
- Selective auto-save clearing
- Export formatted list view

### Auto-save Logic:
- Saves to both formats on window close
- Deletes auto-save files when list is empty
- Prioritizes JSON, falls back to CSV


## Key Features
1. **CRUD Operations**: Add, delete, clear events
2. **File Management**: Import/export in multiple formats
3. **Data Persistence**: Automatic backup system
4. **List Printing**: Formatted text output of all events
5. **Input Validation**: Comprehensive field checking

## Technical Details
- **Framework**: JavaFX for UI
- **Data Binding**: PropertyValueFactory for TableView
- **Serialization**: Jackson ObjectMapper for JSON
- **File Handling**: Java NIO and traditional I/O

## Usage Notes
- CSV files must include header row
- Date and time are combined in single field
- Phone numbers stored as strings
- Empty lists trigger auto-save file cleanup

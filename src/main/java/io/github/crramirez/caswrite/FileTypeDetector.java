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

/**
 * Detects file types based on filename extension to determine
 * the appropriate viewer/editor mode.
 */
public class FileTypeDetector {

    /**
     * Enum representing the type of file viewer to use.
     */
    public enum FileType {
        /** Text file to be opened in text editor */
        TEXT,
        /** Table file (CSV/TSV) to be opened in table viewer */
        TABLE
    }

    /**
     * Determines the file type based on the filename.
     * CSV and TSV files are detected as TABLE type, all other files as TEXT.
     * If the filename is null, returns TEXT as the default type.
     *
     * @param filename the name of the file (can include path), may be null
     * @return the detected FileType, never null
     */
    public FileType detectFileType(String filename) {
        if (filename == null) {
            return FileType.TEXT;
        }
        String lowerName = filename.toLowerCase(java.util.Locale.ROOT);
        if (lowerName.endsWith(".csv") || lowerName.endsWith(".tsv")) {
            return FileType.TABLE;
        }
        return FileType.TEXT;
    }

    /**
     * Checks if the given filename represents a table file (CSV/TSV).
     *
     * @param filename the name of the file
     * @return true if the file is a CSV or TSV file
     */
    public boolean isTableFile(String filename) {
        return detectFileType(filename) == FileType.TABLE;
    }
}

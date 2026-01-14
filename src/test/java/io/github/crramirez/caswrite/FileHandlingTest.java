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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the file handling API.
 * Tests the business logic for determining how files should be opened
 * based on their characteristics.
 */
@DisplayName("File Handling API")
class FileHandlingTest {

    private FileTypeDetector fileTypeDetector;

    @BeforeEach
    void setUp() {
        fileTypeDetector = new FileTypeDetector();
    }

    @Nested
    @DisplayName("when detecting file types by extension")
    class FileTypeDetectionByExtension {

        @ParameterizedTest
        @ValueSource(strings = {"data.csv", "DATA.CSV", "Data.Csv", "report.tsv", "REPORT.TSV"})
        @DisplayName("should recognize tabular data files regardless of case")
        void shouldRecognizeTabularDataFiles(String filename) {
            assertThat(fileTypeDetector.detectFileType(filename))
                    .isEqualTo(FileTypeDetector.FileType.TABLE);
        }

        @ParameterizedTest
        @ValueSource(strings = {
                "readme.txt",
                "script.py",
                "Main.java",
                "config.json",
                "style.css",
                "document.md",
                "noextension"
        })
        @DisplayName("should treat non-tabular files as text")
        void shouldTreatNonTabularFilesAsText(String filename) {
            assertThat(fileTypeDetector.detectFileType(filename))
                    .isEqualTo(FileTypeDetector.FileType.TEXT);
        }

        @Test
        @DisplayName("should handle null filename gracefully")
        void shouldHandleNullFilename() {
            assertThat(fileTypeDetector.detectFileType(null))
                    .isEqualTo(FileTypeDetector.FileType.TEXT);
        }

        @Test
        @DisplayName("should handle empty filename as text")
        void shouldHandleEmptyFilename() {
            assertThat(fileTypeDetector.detectFileType(""))
                    .isEqualTo(FileTypeDetector.FileType.TEXT);
        }
    }

    @Nested
    @DisplayName("when checking if a file is tabular")
    class IsTableFileCheck {

        @ParameterizedTest
        @ValueSource(strings = {"data.csv", "export.tsv", "/path/to/file.CSV"})
        @DisplayName("should return true for tabular file formats")
        void shouldReturnTrueForTabularFormats(String filename) {
            assertThat(fileTypeDetector.isTableFile(filename)).isTrue();
        }

        @ParameterizedTest
        @ValueSource(strings = {"document.txt", "image.png", "config.yaml"})
        @DisplayName("should return false for non-tabular formats")
        void shouldReturnFalseForNonTabularFormats(String filename) {
            assertThat(fileTypeDetector.isTableFile(filename)).isFalse();
        }
    }

    @Nested
    @DisplayName("when handling files with paths")
    class FilesWithPaths {

        @Test
        @DisplayName("should correctly detect file type from full path")
        void shouldDetectFileTypeFromFullPath() {
            assertThat(fileTypeDetector.detectFileType("/home/user/documents/report.csv"))
                    .isEqualTo(FileTypeDetector.FileType.TABLE);

            assertThat(fileTypeDetector.detectFileType("/var/log/system.log"))
                    .isEqualTo(FileTypeDetector.FileType.TEXT);
        }

        @Test
        @DisplayName("should correctly detect file type from relative path")
        void shouldDetectFileTypeFromRelativePath() {
            assertThat(fileTypeDetector.detectFileType("./data/export.tsv"))
                    .isEqualTo(FileTypeDetector.FileType.TABLE);

            assertThat(fileTypeDetector.detectFileType("../config/settings.ini"))
                    .isEqualTo(FileTypeDetector.FileType.TEXT);
        }
    }

    @Nested
    @DisplayName("edge cases")
    class EdgeCases {

        @Test
        @DisplayName("should handle files with multiple dots in name")
        void shouldHandleFilesWithMultipleDots() {
            assertThat(fileTypeDetector.detectFileType("report.2024.01.csv"))
                    .isEqualTo(FileTypeDetector.FileType.TABLE);

            assertThat(fileTypeDetector.detectFileType("backup.old.txt"))
                    .isEqualTo(FileTypeDetector.FileType.TEXT);
        }

        @Test
        @DisplayName("should handle filenames that contain csv or tsv as part of name")
        void shouldNotMatchPartialExtensions() {
            // "csvfile.txt" should be text, not table
            assertThat(fileTypeDetector.detectFileType("csvfile.txt"))
                    .isEqualTo(FileTypeDetector.FileType.TEXT);

            // "mytsv_data.json" should be text, not table
            assertThat(fileTypeDetector.detectFileType("mytsv_data.json"))
                    .isEqualTo(FileTypeDetector.FileType.TEXT);
        }

        @Test
        @DisplayName("should handle hidden files (dot prefix)")
        void shouldHandleHiddenFiles() {
            assertThat(fileTypeDetector.detectFileType(".hidden.csv"))
                    .isEqualTo(FileTypeDetector.FileType.TABLE);

            assertThat(fileTypeDetector.detectFileType(".gitignore"))
                    .isEqualTo(FileTypeDetector.FileType.TEXT);
        }
    }
}

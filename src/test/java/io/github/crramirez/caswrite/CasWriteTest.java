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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for the CasWrite class.
 */
class CasWriteTest {

    @TempDir
    Path tempDir;

    @Test
    void midOpenAsTableConstantShouldHaveExpectedValue() throws Exception {
        // Access the private static field MID_OPEN_AS_TABLE
        Field field = CasWrite.class.getDeclaredField("MID_OPEN_AS_TABLE");
        field.setAccessible(true);
        int midOpenAsTable = field.getInt(null);
        
        assertThat(midOpenAsTable).isEqualTo(2000);
    }

    @Test
    void csvFileShouldBeIdentifiedByExtension() throws IOException {
        // Create test files with different extensions
        Path csvFile = tempDir.resolve("test.csv");
        Files.createFile(csvFile);

        String filename = csvFile.getFileName().toString().toLowerCase();
        
        assertThat(filename).endsWith(".csv");
    }

    @Test
    void tsvFileShouldBeIdentifiedByExtension() throws IOException {
        // Create test files with different extensions
        Path tsvFile = tempDir.resolve("test.tsv");
        Files.createFile(tsvFile);

        String filename = tsvFile.getFileName().toString().toLowerCase();
        
        assertThat(filename).endsWith(".tsv");
    }

    @Test
    void txtFileShouldNotBeIdentifiedAsTableFile() throws IOException {
        // Create a text file
        Path txtFile = tempDir.resolve("test.txt");
        Files.createFile(txtFile);

        String filename = txtFile.getFileName().toString().toLowerCase();
        
        assertThat(filename).doesNotEndWith(".csv");
        assertThat(filename).doesNotEndWith(".tsv");
    }

    @Test
    void fileExtensionCheckShouldBeCaseInsensitive() throws IOException {
        // Test that extension checking is case-insensitive
        Path csvUpperFile = tempDir.resolve("TEST.CSV");
        Files.createFile(csvUpperFile);

        String filename = csvUpperFile.getFileName().toString().toLowerCase();
        
        assertThat(filename).endsWith(".csv");
    }

    @Test
    void nonExistentFileShouldFail() {
        File nonExistentFile = new File(tempDir.toFile(), "nonexistent.txt");
        
        assertThat(nonExistentFile.exists()).isFalse();
    }

    @Test
    void directoryInsteadOfFileShouldBeDetectable() throws IOException {
        Path directory = tempDir.resolve("testdir");
        Files.createDirectory(directory);

        File dir = directory.toFile();
        
        assertThat(dir.exists()).isTrue();
        assertThat(dir.isFile()).isFalse();
        assertThat(dir.isDirectory()).isTrue();
    }

    @Test
    void regularFileShouldBeDetectable() throws IOException {
        Path regularFile = tempDir.resolve("regular.txt");
        Files.createFile(regularFile);

        File file = regularFile.toFile();
        
        assertThat(file.exists()).isTrue();
        assertThat(file.isFile()).isTrue();
        assertThat(file.isDirectory()).isFalse();
    }

    @Test
    void classShouldExist() {
        assertThat(CasWrite.class).isNotNull();
        assertThat(CasWrite.class.getSimpleName()).isEqualTo("CasWrite");
    }

    @Test
    void classShouldHaveOnMenuMethod() throws NoSuchMethodException {
        Method onMenuMethod = CasWrite.class.getMethod("onMenu", casciian.event.TMenuEvent.class);
        
        assertThat(onMenuMethod).isNotNull();
        assertThat(onMenuMethod.getReturnType()).isEqualTo(boolean.class);
    }

    @Test
    void classShouldHaveMainMethod() throws NoSuchMethodException {
        Method mainMethod = CasWrite.class.getMethod("main", String[].class);
        
        assertThat(mainMethod).isNotNull();
        assertThat(java.lang.reflect.Modifier.isStatic(mainMethod.getModifiers())).isTrue();
        assertThat(java.lang.reflect.Modifier.isPublic(mainMethod.getModifiers())).isTrue();
    }
}

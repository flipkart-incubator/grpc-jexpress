/*
 * Copyright (c) The original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flipkart.gjex.core.config;

import com.flipkart.gjex.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;

/**
*
* The <code>FileLocator</code> is a utility for locating configuration files deployed under the projects root. This class provides methods to locate
* all occurrences of a file with the specified name. This class looks into specific directories under the project root folder to locate files.
*
* @author regunath.balasubramanian
*/
public class FileLocator {

    /**
    * The Log instance for this class
    */
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLocator.class);

    /** The config root folder of GJEX */
    private static final String DEPLOYED_CONFIG_ROOT;
    static {
        DEPLOYED_CONFIG_ROOT = FileLocator.class.getClassLoader().getResource(Constants.CONFIG_ROOT).getPath();
    }

    /**
    * Finds the unique instance of config file with name as the specified string.
    * @param fileName name of the file
    * @return the unique file occurrence
    * @throws ConfigurationException in case multiple files are found with the specified name
    */
    public static File findUniqueFile(String fileName) throws ConfigurationException {
        return findUniqueFile(fileName, null);
    }

    /**
    * Finds the unique instance of config file with name as the specified string.
    * @param fileName name of the file
    * @return the unique file occurrence
    * @throws ConfigurationException in case multiple files are found with the specified name
    */
    public static File findUniqueFile(String fileName, String path) throws ConfigurationException {
        File[] files = findFiles(fileName, path);
        if (files.length == 0) {
            StringBuilder errorMessage = new StringBuilder("No file found that matches specified name : ");
            errorMessage.append(fileName);
            if (path != null) {
                errorMessage.append(" under path " + path);
            }
            LOGGER.error(errorMessage.toString());
            throw new RuntimeException(errorMessage.toString());
        } else if (files.length > 1) {
            StringBuilder foundFiles = new StringBuilder();
            for (int i = 0; i < files.length; i++) {
                foundFiles.append(files[i].getAbsolutePath() + "\n");
            }
            StringBuffer errorMessage = new StringBuffer("Multiple files found that match specified name : ");
            errorMessage.append(fileName);
            if (path != null) {
                errorMessage.append(" under path " + path);
            }
            errorMessage.append(" as: \n ");
            errorMessage.append(foundFiles.toString());
            LOGGER.error(errorMessage.toString());
            throw new RuntimeException(errorMessage.toString());
        }
        return files[0];
    }

    /**
    * Returns an array of files that match the specified name located under
    * the projects' configuration folders.
    * This method tries to load the file as a resource using the classloader
    * in case the specified file is not found under projects' root.
    * @param fileName the case insensitive name of the file to be located
    * @return array of matching files. May be an empty array if no matching files are found
    */
    public static File[] findFiles(String fileName) {
        return findFiles(fileName,null);
    }

    /**
    * Returns an array of files that match the specified name located under
    * the projects' configuration folders.
    * This method tries to load the file as a resource using the classloader
    * in case the specified file is not found under projects' root.
    * @param fileName the case insensitive name of the file to be located
    * @param  path Will narrow search to specific path within project root.
    * @return array of matching files. May be an empty array if no matching files are found
    */
    public static File[] findFiles(String fileName,String path) {
        return findFiles(fileName, path, false);
    }

    /**
    * Returns an array of directories that match the specified name located under
    * the projects' configuration folders.
    * This method tries to load the directory as a resource using the classloader
    * in case the specified directory is not found under projects' root.
    * @param directoryName the case insensitive name of the directory to be located
    * @param  path Will narrow search to specific path within project root.
    * @return array of matching directories. May be an empty array if no matching directories are found
    */
    public static File[] findDirectories(String directoryName,String path) {
        return findFiles(directoryName, path, true);
    }

    /**
    * Returns an array of files that match the specified name located under
    * the projects' configuration folders.
    * This method tries to load the file as a resource using the classloader
    * in case the specified file is not found under projects' root.
    * @param fileName the case insensitive name of the file to be located
    * @param  path Will narrow search to specific path within project root.
    * @param isDirectory if true, find only directory
    *                    if false, find only file.
    * @return array of matching files. May be an empty array if no matching files are found
    */
    private static File[] findFiles(String fileName,String path, boolean isDirectory) {
        File projectRootFolder = null;
        if(path!=null) {
            File pathFile = new File(path);
            if (pathFile.isAbsolute() || pathFile.isDirectory()) { // use the path as-is if it is absolute or a directory by itself
                projectRootFolder = pathFile;
            } else {
                projectRootFolder = new File(DEPLOYED_CONFIG_ROOT,path);
            }
        } else {
            projectRootFolder = new File(DEPLOYED_CONFIG_ROOT);
        }
        ArrayList<File> locatedFiles = new ArrayList<File>();
        locateFiles(fileName, locatedFiles, projectRootFolder, false, isDirectory);
        if (locatedFiles.size() == 0) {
            try {
                Enumeration<URL> enumeration = FileLocator.class.getClassLoader().getResources(fileName);
                while (enumeration.hasMoreElements()) {
                    URL resource = enumeration.nextElement();
                    if (resource.getFile().contains(".jar!") && !isDirectory) { // resource is a file inside a jar
                        locatedFiles.add(getTempFileFromResource(fileName, resource.openStream()));
                    } else {
                        locatedFiles.add(new File(resource.getFile()));
                    }
                }
            } catch (IOException e) {
                // log the error and return an empty array
                LOGGER.error("Unable to locate files that match: " + fileName, e);
            }
        }
        return (File[])locatedFiles.toArray(new File[locatedFiles.size()]);
    }

    /**
    * Helper method to create a physical temporary file containing the contents read from the specified InputStream
    * @param fileName the File name prefix for the temporary file
    * @param input the InputStream to read contents from
    * @return temporary File
    * @throws IOException
    */
    private static File getTempFileFromResource(String fileName, InputStream input) throws IOException {
            File tempFile = File.createTempFile(fileName, null);
            OutputStream out = new FileOutputStream(tempFile);
        int read;
        byte[] bytes = new byte[1024];
        while ((read = input.read(bytes)) != -1) {
            out.write(bytes, 0, read);
        }
        out.flush();
        out.close();
        input.close();
        tempFile.deleteOnExit();
            return tempFile;
    }

    /**
    * Helper method to recursively look for files with the specified name
    * under the specified folder or match the specified name with the specified file name.
    * @param fileName the name of the file to search for
    * @param locatedFiles ArrayList containing File instances post matching
    * @param isConfigFolder true if the specified file is a config folder or one of its sub folders
    * @param isDirectory if true, find only directory
    *                    if false, find only file.
    * @param file file or folder to match filename against
    */
    private static void locateFiles(String fileName, ArrayList<File> locatedFiles,
            File file, boolean isConfigFolder, boolean isDirectory) {
        // trim any leading and trailing spaces in the specified file name
        fileName = fileName.trim();
        if (file.exists()) {
            if (file.isDirectory()) {
                if (!isConfigFolder) {
                    isConfigFolder = file.getName().equalsIgnoreCase(Constants.CONFIG_ROOT);
                }
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    locateFiles(fileName, locatedFiles, files[i], isConfigFolder, isDirectory);
                }
            }
            if (isConfigFolder && file.getName().equalsIgnoreCase(fileName)) {
                if(isDirectory ^ file.isFile()) {
                    locatedFiles.add(file);
                }
            }
        }
    }

}

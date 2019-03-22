import javassist.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Input {

    private final String path;
    private final List<String> directories = new ArrayList<>();
    public final static List<String> classes = new ArrayList<>();
    public final static List<String> classesFromDirectory = new ArrayList<>();
    private ClassPool classPool;
    private CtClass ctClass;
    private final List<String> methods = new ArrayList<>();
    private final List<String> fields = new ArrayList<>();
    private final List<String> constructors = new ArrayList<>();


    public Input(String loadedPath) {
        this.path = loadedPath;
    }

    public void findAllFromJar() throws IOException {
        JarFile jarFile = new JarFile(path);
        Enumeration whichJar = jarFile.entries();
        while (whichJar.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) whichJar.nextElement();
            if (jarEntry.isDirectory()) {
                directories.add(jarEntry.getName());
            } else if (jarEntry.getName().endsWith(".class")) {
                classes.add(jarEntry.getName().substring(0, jarEntry.getName().length() - 6));
            }
        }
    }

    public void findClassesFromDirectory(String directory) throws IOException {
        JarFile jarFile = new JarFile(path);
        Enumeration whichJar = jarFile.entries();
        while (whichJar.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) whichJar.nextElement();
            if (jarEntry.getName().endsWith(".class") && jarEntry.getName().startsWith(directory)) {
                classesFromDirectory.add(jarEntry.getName().substring(0, jarEntry.getName().length() - 6));
            }
        }

        for (CtClass i : Functions.createdClasses) {
            if (i.getName().replace(".", "/").startsWith(directory)) {
                classesFromDirectory.add(i.getName().replace(".", "/"));
            }
        }
    }

    public void findMethodsFromClass(String classPath) throws NotFoundException {
        String tempClass = classPath.replace("/", ".");
        classPool = ClassPool.getDefault();
        classPool.insertClassPath(path);
        try {
            ctClass = classPool.get(tempClass);
            for (CtMethod i : ctClass.getDeclaredMethods()) {
                methods.add(i.getName());
            }
            for (CtField i : ctClass.getDeclaredFields()) {
                fields.add(i.getName());
            }
            for (CtConstructor i : ctClass.getDeclaredConstructors()) {
                constructors.add(i.getLongName());
            }
        } catch (Exception e) {
            System.out.println("Problem with reading from class path");
        }

    }

    public void saveFile(String pathToSave) throws NotFoundException, IOException, CannotCompileException {
        classPool = classPool.getDefault();
        classPool.insertClassPath(path);
        for (String i : classes) {
            String classPath = i.replace("/", ".");
            ctClass = classPool.get(classPath);
            ctClass.defrost();
            for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
                ctMethod.getMethodInfo();
                ctClass.writeFile();
                ctClass.defrost();
                CtClass.debugDump = pathToSave;
            }
            for (CtField cfField : ctClass.getDeclaredFields()) {
                cfField.getFieldInfo();
                ctClass.writeFile();
                ctClass.defrost();
                CtClass.debugDump = pathToSave;
            }
            for (CtConstructor ctConstructor : ctClass.getDeclaredConstructors()) {
                ctConstructor.getMethodInfo();
                ctClass.writeFile();
                ctClass.defrost();
                CtClass.debugDump = pathToSave;
            }
        }
        copyFile(path, pathToSave);
        ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "cd \"" + pathToSave + "\" && jar cfm Invaders.jar META-INF/MANIFEST.MF *");
        processBuilder.start();
        Functions.showAlert("Project saved as .jar saved into directory:\n" + pathToSave);

    }

    private void copyFile(String path, String pathToSave) throws IOException {
        JarFile jarFile = new JarFile(path);
        Enumeration enumeration = jarFile.entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            if (!jarEntry.getName().endsWith(".class") && !jarEntry.isDirectory()) {
                File file = new File(pathToSave + "/" + jarEntry.getName());
                file.getParentFile().mkdirs();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                InputStream inputStream = jarFile.getInputStream(jarEntry);
                while (inputStream.available() > 0) {
                    fileOutputStream.write(inputStream.read());
                }
                fileOutputStream.close();
                inputStream.close();
            }

        }
    }

    public void deleteClassesFromList() {
        classesFromDirectory.clear();
    }

    public List<String> getDirectories() {
        return directories;
    }


    public List<String> getClassesFromDirectory() {
        return classesFromDirectory;
    }

    public List<String> getMethods() {
        return methods;
    }

    public List<String> getFields() {
        return fields;
    }

    public List<String> getConstructors() {
        return constructors;
    }

    public void deleteFromListFromClass() {
        methods.clear();
        fields.clear();
        constructors.clear();
    }

}

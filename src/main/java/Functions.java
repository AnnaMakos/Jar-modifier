import javafx.scene.control.Alert;
import javassist.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Functions {

    public static final List<CtClass> createdClasses = new ArrayList<>();

    public static void createMethod(String path, String fieldText) {
        try {
            CtClass ctClass = createCtClass(path);
            ctClass.addMethod(CtMethod.make(fieldText, ctClass));
            showAlert("A new method created into class:\n" + path);
        } catch (Exception e) {
            showAlertError("Wrong structure of method or wrong path chosen.");
        }
    }

    public static void createField(String path, String fieldText) {
        try {
            CtClass ctClass = createCtClass(path);
            ctClass.addField(CtField.make(fieldText, ctClass));
            showAlert("A new field created into class:\n" + path);
        } catch (Exception e) {
            showAlertError("Wrong structure of field or wrong path chosen.");
        }
    }

    public static void createConstructor(String path, String fieldText) {
        try {
            CtClass ctClass = createCtClass(path);
            ctClass.addConstructor(CtNewConstructor.make(fieldText, ctClass));
            showAlert("A new constructor created into class:\n" + path);
        } catch (Exception e) {
            showAlertError("Wrong structure of constructor or wrong path chosen.");
        }
    }

    public static void createClass(String path, String fieldText) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.insertClassPath(path);
            CtClass ctClass = classPool.makeClass((path.replace("/", ".") + fieldText));
            Input.classes.add(path + fieldText);
            createdClasses.add(ctClass);
            showAlert("A new class created into directory:\n" + path);
            ctClass.writeFile();
            ctClass.defrost();
        } catch (Exception e) {
            showAlertError("Wrong structure of class or wrong path chosen.");
        }
    }

    public static void createClassInheritingByThread(String path, String fieldText) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.insertClassPath(path);
            CtClass threadClass = classPool.get("java.lang.Thread");
            CtClass ctClass = classPool.makeClass((path.replace("/", ".") + fieldText));
            ctClass.setSuperclass(threadClass);
            Input.classes.add(path + fieldText);
            createdClasses.add(ctClass);
            showAlert("A new class inheriting by Thread created into directory:\n" + path);
            ctClass.writeFile();
            ctClass.defrost();
        } catch (Exception e) {
            showAlertError("Wrong structure of class or wrong path chosen.");
        }
    }

    public static void createDirectory(String path, String text) {
        try {
            File file = new File(path + "/" + text);
            file.mkdirs();
            showAlert("A new directory created into directory:\n" + path);
        } catch (Exception e) {
            showAlertError("Ups! something's wrong..");
        }
    }

    private static CtClass createCtClass(String path) throws NotFoundException {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(path);
        String classPath = path.replace("/", ".");
        return classPool.get(classPath);
    }

    public static void showAlert(String text) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(text);
        alert.show();
    }

    private static void showAlertError(String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(text);
        alert.show();
    }

    public static void deleteMethod(String path, String method) {
        try {
            CtClass ctClass = createCtClass(path);
            CtMethod ctMethod = ctClass.getDeclaredMethod(method);
            ctClass.removeMethod(ctMethod);
            showAlert("Method deleted");
        } catch (Exception e) {
            showAlertError("Wrong path or no methods to delete");
        }
    }

    public static void deleteField(String path, String field) {
        try {
            CtClass ctClass = createCtClass(path);
            CtField ctField = ctClass.getDeclaredField(field);
            ctClass.removeField(ctField);
            showAlert("Field deleted");
        } catch (Exception e) {
            showAlertError("Wrong path or no fields to delete");
        }
    }

    public static void deleteConstructor(String path, String constructor) {
        boolean flag = false;
        try {
            CtClass ctClass = createCtClass(path);
            for (CtConstructor i : ctClass.getDeclaredConstructors()) {
                if (i.getLongName().equals(constructor)) {
                    ctClass.removeConstructor(i);
                    showAlert("Constructor deleted");
                    flag = true;
                }
            }
            if (!flag) {
                showAlertError("Wrong path or no constructors to delete");
            }
        } catch (Exception e) {
            showAlertError("Wrong path or no constructors to delete");
        }
    }

    public static void writeEndMethod(String path, String method, String bodyArea) {
        try {
            CtClass ctClass = createCtClass(path);
            CtMethod ctMethod = ctClass.getDeclaredMethod((method));
            ctMethod.insertAfter(bodyArea);
            ctClass.writeFile();
            showAlert("The code added at the end of the method.");
        } catch (Exception e) {
            showAlertError("Wrong path or wrong structure of code.");
        }
    }


    public static void writeBeginMethod(String path, String method, String bodyArea) {
        try {
            CtClass ctClass = createCtClass(path);
            CtMethod ctMethod = ctClass.getDeclaredMethod((method));
            ctMethod.insertBefore(bodyArea);
            ctClass.writeFile();
            showAlert("The code added at the beginning of the method.");
        } catch (Exception e) {
            showAlertError("Wrong path or wrong structure of code.");
        }
    }

    public static void overwriteMethod(String path, String method, String bodyArea) {
        try {
            CtClass ctClass = createCtClass(path);
            CtMethod ctMethod = ctClass.getDeclaredMethod((method));
            ctMethod.setBody(bodyArea);
            ctClass.writeFile();
            showAlert("The method overwritten.");
        } catch (Exception e) {
            showAlertError("Wrong path or wrong structure of code.");
        }
    }

    public static void overwriteConstructor(String path, String constructor, String bodyArea) {
        try {
            boolean flag = false;
            CtClass ctClass = createCtClass(path);
            for (CtConstructor i : ctClass.getDeclaredConstructors()) {
                if (i.getLongName().equals(constructor)) {
                    i.setBody(bodyArea);
                    ctClass.writeFile();
                    flag = true;
                    showAlert("Constructor overwritten.");
                }
            }
            if (!flag) {
                showAlertError("Wrong path or wrong structure of code.");
            }
        } catch (Exception e) {
            showAlertError("Wrong path or wrong structure of code.");
        }

    }

    public static void deleteClass(String classPath) {
        try {
            CtClass ctClass = createCtClass(classPath);
            String className = ctClass.getName().replace(".", "/");
            ctClass.detach();
            for (String i : Input.classesFromDirectory) {
                if (i.equals(className)) {
                    Input.classesFromDirectory.remove(i);
                    break;
                }
            }
            for (String i : Input.classes) {
                if (i.equals(className)) {
                    Input.classes.remove(i);
                    break;
                }
            }
            for (CtClass i : createdClasses) {
                if (i.getName().replace(".", "/").equals(className)) {
                    createdClasses.remove(i);
                    break;
                }
            }
            showAlert("Class deleted.");
        } catch (Exception e) {
            showAlertError("Wrong path or no classes to delete.");
            e.printStackTrace();
        }
    }

    public static void deleteDirectory(String path) {
        try {
            System.out.println(path);
            showAlert("Directory deleted.");
        } catch (Exception e) {
            showAlertError("Wrong path or no directories to delete.");
        }
    }
}

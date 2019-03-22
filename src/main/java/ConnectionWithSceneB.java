import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javassist.CannotCompileException;
import javassist.NotFoundException;

import java.io.File;
import java.io.IOException;

public class ConnectionWithSceneB {

    private Input input;
    @FXML
    private ListView<String> directoryList;
    @FXML
    private ListView<String> classList;
    @FXML
    private ListView<String> methodsList;
    @FXML
    private ListView<String> fieldsList;
    @FXML
    private ListView<String> constructorsList;
    @FXML
    private TextArea nameArea;
        FileChooser fileChooser = new FileChooser();
        @FXML
        private TextArea bodyArea;


        public void loadFile(ActionEvent actionEvent) throws IOException {
            File loadedFile;
        loadedFile = fileChooser.showOpenDialog(null);
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("jar", "*.jar"));
        String loadedPath = loadedFile.getPath();
        input = new Input(loadedPath);
        input.findAllFromJar();

        writeToListViews();


    }

    private void writeToListViews() {
        directoryList.setItems(FXCollections.observableArrayList(input.getDirectories()));

        directoryList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event1) {
                String clickedDirectory = directoryList.getSelectionModel().getSelectedItem();

                try {
                    input.deleteClassesFromList();
                    input.findClassesFromDirectory(clickedDirectory);
                    classList.setItems(FXCollections.observableArrayList(input.getClassesFromDirectory()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        classList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                String clickedClass = classList.getSelectionModel().getSelectedItem();
                try {
                    input.deleteFromListFromClass();
                    input.findMethodsFromClass(clickedClass);
                    methodsList.setItems(FXCollections.observableArrayList(input.getMethods()));
                    fieldsList.setItems(FXCollections.observableArrayList(input.getFields()));
                    constructorsList.setItems(FXCollections.observableArrayList(input.getConstructors()));
                } catch (NotFoundException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void saveProject(ActionEvent actionEvent) throws IOException, CannotCompileException, NotFoundException {
        input.saveFile("C:/Users/Ania/Desktop/jfk2");
    }

    public void createMethodButton(ActionEvent actionEvent) {
        Functions.createMethod(classList.getSelectionModel().getSelectedItem(), nameArea.getText());
    }

    public void createFieldButton(ActionEvent actionEvent) {
        Functions.createField(classList.getSelectionModel().getSelectedItem(), nameArea.getText());
    }

    public void createConstructorButton(ActionEvent actionEvent) {
        Functions.createConstructor(classList.getSelectionModel().getSelectedItem(), nameArea.getText());
    }

    public void createClassButton(ActionEvent actionEvent) {
        Functions.createClass(directoryList.getSelectionModel().getSelectedItem(), nameArea.getText());
    }

    public void createDirectoryButton(ActionEvent actionEvent) {
        Functions.createDirectory(directoryList.getSelectionModel().getSelectedItem(), nameArea.getText());
    }

    public void deleteMethodButton(ActionEvent actionEvent) {
        Functions.deleteMethod(classList.getSelectionModel().getSelectedItem(), methodsList.getSelectionModel().getSelectedItem());
    }

    public void deleteFieldButton(ActionEvent actionEvent) {
        Functions.deleteField(classList.getSelectionModel().getSelectedItem(), fieldsList.getSelectionModel().getSelectedItem());
    }

    public void deleteConstructorButton(ActionEvent actionEvent) {
        Functions.deleteConstructor(classList.getSelectionModel().getSelectedItem(), constructorsList.getSelectionModel().getSelectedItem());
    }

    public void deleteClassButton(ActionEvent actionEvent) {
        Functions.deleteClass(classList.getSelectionModel().getSelectedItem());
    }

    public void deleteDirectoryButton(ActionEvent actionEvent) {
        Functions.deleteDirectory(directoryList.getSelectionModel().getSelectedItem());
    }

    public void writeEndMethodButton(ActionEvent actionEvent) {
        Functions.writeEndMethod(classList.getSelectionModel().getSelectedItem(), methodsList.getSelectionModel().getSelectedItem(), bodyArea.getText());
    }

    public void writeBeginMethodButton(ActionEvent actionEvent) {
        Functions.writeBeginMethod(classList.getSelectionModel().getSelectedItem(), methodsList.getSelectionModel().getSelectedItem(), bodyArea.getText());
    }

    public void overwriteMethodButton(ActionEvent actionEvent) {
        Functions.overwriteMethod(classList.getSelectionModel().getSelectedItem(), methodsList.getSelectionModel().getSelectedItem(), bodyArea.getText());
    }

    public void overwriteConstructorButton(ActionEvent actionEvent) {
        Functions.overwriteConstructor(classList.getSelectionModel().getSelectedItem(), constructorsList.getSelectionModel().getSelectedItem(), bodyArea.getText());
    }

    public void createClassInheritingByThreadButton(ActionEvent actionEvent) {
        Functions.createClassInheritingByThread(directoryList.getSelectionModel().getSelectedItem(), nameArea.getText());
    }
}

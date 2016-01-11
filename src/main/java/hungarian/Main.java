package hungarian;

import hungarian.algorithm.HungarianDouble;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Main extends Application {

    private TextField[][] textFields;
    Label totalCostLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("Венгерский алгоритм");

        BorderPane borderPane = new BorderPane();
        VBox topPanel = new VBox();

        ///MENU
        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("Помощь");
        MenuItem menuItem = new MenuItem("О алгоритме");

        menuItem.setOnAction((ae) -> {
            Stage helpStage = new Stage();
            FlowPane pane = new FlowPane();
            helpStage.setTitle("О венгерском алгоритме");

            WebView browser = new WebView();
            browser.setPrefHeight(600);
            browser.setPrefWidth(800);
            browser.getEngine().load(Main.class.getResource("/help.html").toExternalForm());

            pane.getChildren().add(browser);

            Scene helpScene = new Scene(pane, 800, 600);
            helpStage.setResizable(false);
            helpStage.setScene(helpScene);
            helpStage.show();
        });

        fileMenu.getItems().add(menuItem);
        menuBar.getMenus().add(fileMenu);
        ////////////

        ///Matrix
        VBox matrix = new VBox();
        textFields = addTextFields(3, matrix);
        //////////

        ///TOP
        FlowPane flowPane = new FlowPane(10, 10);
        flowPane.setPadding(new Insets(10));
        flowPane.setAlignment(Pos.CENTER);

        Label sizeLabel = new Label("Размер:");

        ObservableList<Integer> sizes = FXCollections.observableArrayList(2, 3, 4, 5, 6, 7, 8, 9, 10,
                11, 12, 13, 14, 15, 16, 17, 18, 19, 20);
        ComboBox<Integer> matrixSizeComboBox = new ComboBox<>(sizes);
        matrixSizeComboBox.setValue(3);
        matrixSizeComboBox.setOnAction((ae) -> {
            matrix.getChildren().clear();
            borderPane.setBottom(null);
            textFields = addTextFields(matrixSizeComboBox.getValue(), matrix);
        });

        Button solveButton = new Button("Решить");
        solveButton.setOnAction((ae) -> {
            try {
                HungarianDouble hungarian = new HungarianDouble(textFieldsToMatrix(textFields));
                int[] result = hungarian.getResult();
                totalCostLabel = new Label("Общая стоимость: " + hungarian.getTotal());
                totalCostLabel.setPadding(new Insets(10, 0, 100, 0));
                totalCostLabel.setAlignment(Pos.CENTER);
                borderPane.setBottom(totalCostLabel);
                borderPane.setAlignment(totalCostLabel, Pos.CENTER);

                for (int row = 0; row < textFields.length; row++) {
                    for (int col = 0; col < textFields.length; col++) {
                        textFields[row][col].setEffect(null);
                    }
                }

                for (int i = 0; i < result.length; i++) {
                    textFields[i][result[i]].setEffect(new InnerShadow(20, Color.GREEN));
                }
            } catch (NumberFormatException e) {
                System.err.println("Wrong input!");
            }
        });

        flowPane.getChildren().addAll(sizeLabel, matrixSizeComboBox, solveButton);

        topPanel.getChildren().addAll(menuBar, flowPane);
        /////////////

        borderPane.setTop(topPanel);
        borderPane.setCenter(matrix);

        Scene mainScene = new Scene(borderPane, 300, 300);
        stage.setScene(mainScene);
        stage.show();
    }

    public static TextField[][] addTextFields(int size, VBox parentNode) {
        TextField[][] textFields = new TextField[size][size];

        for (int row = 0; row < size; row++) {
            HBox matrixRow = new HBox();
            matrixRow.setAlignment(Pos.CENTER);

            for (int col = 0; col < size; col++) {
                TextField tField = new TextField();
                tField.setPrefColumnCount(3);
                tField.setAlignment(Pos.CENTER);
                tField.textProperty().addListener(((observable, oldValue, newValue) -> {
                    try {
                        Double.parseDouble(newValue);
                        tField.setEffect(null);
                    } catch (NumberFormatException e) {
                        tField.setEffect(new InnerShadow(10, Color.RED));
                    }
                }));

                textFields[row][col] = tField;
                matrixRow.getChildren().add(tField);
            }

            parentNode.getChildren().add(matrixRow);
        }

        return textFields;
    }

    public static double[][] textFieldsToMatrix(TextField[][] textFields) throws NumberFormatException {

        double[][] matrix = new double[textFields.length][textFields.length];
        try {
            for (int row = 0; row < textFields.length; row++) {
                for (int col = 0; col < textFields.length; col++) {
                    matrix[row][col] = Double.parseDouble(textFields[row][col].getText());
                }
            }
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Проверьте правильность ввода!");
            alert.setContentText("Матрица должна содержать только числа.");
            alert.showAndWait();
            throw e;
        }
        return matrix;
    }
}

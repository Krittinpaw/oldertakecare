package com.elderlycare.ui;

import com.elderlycare.DatabaseManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class MedicationPanel extends BorderPane {

    private final VBox listContainer;

    public MedicationPanel() {
        setPadding(new Insets(20));

        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Medications");
        title.getStyleClass().add("title-label");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnAdd = new Button("➕");
        btnAdd.setOnAction(e -> showAddDialog());
        topBox.getChildren().addAll(title, spacer, btnAdd);
        setTop(topBox);

        listContainer = new VBox(15);
        listContainer.setAlignment(Pos.TOP_CENTER);
        listContainer.setPadding(new Insets(10, 0, 10, 0));

        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #121212;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        setCenter(scrollPane);
        loadMedications();
    }

    private void loadMedications() {
        listContainer.getChildren().clear();
        List<DatabaseManager.Medication> meds = DatabaseManager.getMedications();
        if (meds.isEmpty()) {
            Label placeholder = new Label("No medications added.");
            placeholder.getStyleClass().add("label");
            listContainer.getChildren().add(placeholder);
        } else {
            for (DatabaseManager.Medication med : meds) {
                VBox card = new VBox(8);
                card.getStyleClass().add("card");
                
                HBox header = new HBox(10);
                Label lblIcon = new Label("💊");
                lblIcon.setStyle("-fx-font-size: 24px;");

                Label lblName = new Label(med.name);
                lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 20px; -fx-text-fill: #4CAF50;");
                
                Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
                Button btnDelete = new Button("🗑");
                btnDelete.setStyle("-fx-background-color: transparent; -fx-text-fill: #F44336; -fx-padding:0;");
                btnDelete.setOnAction(e -> {
                    DatabaseManager.deleteMedication(med.id);
                    loadMedications();
                });
                header.getChildren().addAll(lblIcon, lblName, spacer, btnDelete);
                
                Label lblDoseTime = new Label("⏰ " + med.time + " | " + med.dosage);
                
                HBox tags = new HBox(10);
                String freq = (med.frequency == null || med.frequency.isEmpty()) ? "Daily" : med.frequency;
                String meal = (med.mealTiming == null || med.mealTiming.isEmpty()) ? "Any time" : med.mealTiming;
                
                Label lFreq = new Label(freq);
                lFreq.setStyle("-fx-background-color: #388E3C; -fx-padding: 3px 8px; -fx-background-radius: 5px; -fx-font-size: 12px;");
                Label lMeal = new Label(meal);
                String mealBg = meal.contains("Before") ? "#FF9800" : (meal.contains("After") ? "#2196F3" : "#757575");
                lMeal.setStyle("-fx-background-color: " + mealBg + "; -fx-padding: 3px 8px; -fx-background-radius: 5px; -fx-font-size: 12px;");
                
                tags.getChildren().addAll(lFreq, lMeal);

                card.getChildren().addAll(header, lblDoseTime, tags);
                listContainer.getChildren().add(card);
            }
        }
    }

    private void showAddDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Medication");
        dialog.getDialogPane().setStyle("-fx-background-color: #1E1E1E;");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField nameField = new TextField(); nameField.setPromptText("Med Name");
        TextField dosageField = new TextField(); dosageField.setPromptText("e.g. 1 Pill");
        TextField timeField = new TextField(); timeField.setPromptText("08:00 AM");
        
        ComboBox<String> freqBox = new ComboBox<>();
        freqBox.getItems().addAll("Daily", "Weekly", "As Needed");
        freqBox.getSelectionModel().selectFirst();
        
        ComboBox<String> mealBox = new ComboBox<>();
        mealBox.getItems().addAll("Any time", "Before Meal", "After Meal");
        mealBox.getSelectionModel().selectFirst();

        Label lName = new Label("Name:"); lName.setStyle("-fx-text-fill: white;");
        Label lDose = new Label("Dosage:"); lDose.setStyle("-fx-text-fill: white;");
        Label lTime = new Label("Time:"); lTime.setStyle("-fx-text-fill: white;");
        Label lFreq = new Label("Recur:"); lFreq.setStyle("-fx-text-fill: white;");
        Label lMeal = new Label("Meal:"); lMeal.setStyle("-fx-text-fill: white;");

        grid.add(lName, 0, 0); grid.add(nameField, 1, 0);
        grid.add(lDose, 0, 1); grid.add(dosageField, 1, 1);
        grid.add(lTime, 0, 2); grid.add(timeField, 1, 2);
        grid.add(lFreq, 0, 3); grid.add(freqBox, 1, 3);
        grid.add(lMeal, 0, 4); grid.add(mealBox, 1, 4);

        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if(!nameField.getText().isEmpty() && !timeField.getText().isEmpty()) {
                    DatabaseManager.addMedication(
                        nameField.getText(), dosageField.getText(), timeField.getText(), "pill",
                        freqBox.getValue(), mealBox.getValue()
                    );
                    loadMedications();
                }
            }
        });
    }
}

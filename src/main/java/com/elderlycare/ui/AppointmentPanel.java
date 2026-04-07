package com.elderlycare.ui;

import com.elderlycare.DatabaseManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.List;

public class AppointmentPanel extends BorderPane {

    private final VBox listContainer;

    public AppointmentPanel() {
        setPadding(new Insets(20));

        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Appointments");
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
        loadAppointments();
    }

    private void loadAppointments() {
        listContainer.getChildren().clear();
        List<DatabaseManager.Appointment> apps = DatabaseManager.getAppointments();
        if (apps.isEmpty()) {
            Label placeholder = new Label("No appointments.");
            placeholder.getStyleClass().add("label");
            listContainer.getChildren().add(placeholder);
        } else {
            for (DatabaseManager.Appointment app : apps) {
                VBox card = new VBox(8);
                card.getStyleClass().add("card");
                if (app.status == 1) {
                    card.setStyle("-fx-opacity: 0.6; -fx-background-color: #1A1A1A;"); // fade out if completed
                }
                
                HBox header = new HBox(10);
                Label lblDoctor = new Label("👨‍⚕️ " + app.doctorName);
                lblDoctor.setStyle("-fx-font-weight: bold; -fx-font-size: 18px; -fx-text-fill: #4CAF50;");
                
                Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
                
                CheckBox checkDone = new CheckBox("Done");
                checkDone.setStyle("-fx-text-fill: white;");
                checkDone.setSelected(app.status == 1);
                checkDone.setOnAction(e -> {
                    DatabaseManager.updateAppointmentStatus(app.id, checkDone.isSelected() ? 1 : 0);
                    loadAppointments();
                });
                header.getChildren().addAll(lblDoctor, spacer, checkDone);
                
                Label lblTime = new Label("🕒 " + app.date + " at " + app.time);
                Label lblLoc = new Label("📍 " + (app.location == null ? "N/A" : app.location));
                Label lblNotes = new Label("📝 " + app.notes);
                lblNotes.setStyle("-fx-text-fill: #BBBBBB;");
                lblNotes.setWrapText(true);

                card.getChildren().addAll(header, lblTime, lblLoc, lblNotes);
                listContainer.getChildren().add(card);
            }
        }
    }

    private void showAddDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Appointment");
        dialog.getDialogPane().setStyle("-fx-background-color: #1E1E1E;");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField docName = new TextField(); docName.setPromptText("Doctor Name");
        TextField location = new TextField(); location.setPromptText("Hospital / Clinic");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField timeField = new TextField(); timeField.setPromptText("10:00 AM");
        TextArea notesArea = new TextArea(); notesArea.setPromptText("Advice / Notes");
        notesArea.setPrefRowCount(2);

        Label lDoc = new Label("Doctor:"); lDoc.setStyle("-fx-text-fill: white;");
        Label lLoc = new Label("Place:"); lLoc.setStyle("-fx-text-fill: white;");
        Label lDate = new Label("Date:"); lDate.setStyle("-fx-text-fill: white;");
        Label lTime = new Label("Time:"); lTime.setStyle("-fx-text-fill: white;");
        Label lNotes = new Label("Notes:"); lNotes.setStyle("-fx-text-fill: white;");

        grid.add(lDoc, 0, 0); grid.add(docName, 1, 0);
        grid.add(lLoc, 0, 1); grid.add(location, 1, 1);
        grid.add(lDate, 0, 2); grid.add(datePicker, 1, 2);
        grid.add(lTime, 0, 3); grid.add(timeField, 1, 3);
        grid.add(lNotes, 0, 4); grid.add(notesArea, 1, 4);

        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                if(!docName.getText().isEmpty() && datePicker.getValue() != null && !timeField.getText().isEmpty()) {
                    DatabaseManager.addAppointment(
                        docName.getText(), datePicker.getValue().toString(), timeField.getText(), 
                        notesArea.getText(), location.getText()
                    );
                    loadAppointments();
                }
            }
        });
    }
}

package com.elderlycare.ui;

import com.elderlycare.DatabaseManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.List;

public class HealthMetricsPanel extends BorderPane {

    private LineChart<String, Number> lineChart;
    private Label warningLabel;

    public HealthMetricsPanel() {
        setPadding(new Insets(20));

        HBox topBox = new HBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Health Metrics");
        title.getStyleClass().add("title-label");
        Region spacer = new Region(); HBox.setHgrow(spacer, Priority.ALWAYS);
        Button btnAdd = new Button("➕");
        btnAdd.setOnAction(e -> showAddDialog());
        topBox.getChildren().addAll(title, spacer, btnAdd);
        setTop(topBox);

        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        
        warningLabel = new Label();
        warningLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #F44336; -fx-wrap-text: true;");
        warningLabel.setVisible(false);

        VBox chartCard = new VBox(10);
        chartCard.getStyleClass().add("card");
        chartCard.setAlignment(Pos.CENTER);

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Value");

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Recent History");
        lineChart.getStyleClass().add("chart-title");
        lineChart.setPrefHeight(450);

        chartCard.getChildren().addAll(warningLabel, lineChart);
        content.getChildren().add(chartCard);
        
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: #121212;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        setCenter(scrollPane);
        loadChartData();
    }

    private void loadChartData() {
        lineChart.getData().clear();

        XYChart.Series<String, Number> seriesHR = new XYChart.Series<>(); seriesHR.setName("Heart");
        XYChart.Series<String, Number> seriesSys = new XYChart.Series<>(); seriesSys.setName("Sys BP");
        XYChart.Series<String, Number> seriesDia = new XYChart.Series<>(); seriesDia.setName("Dia BP");
        XYChart.Series<String, Number> seriesSugar = new XYChart.Series<>(); seriesSugar.setName("Sugar");
        XYChart.Series<String, Number> seriesWeight = new XYChart.Series<>(); seriesWeight.setName("Weight");

        boolean highBpWarning = false;
        
        List<DatabaseManager.HealthMetric> metrics = DatabaseManager.getMetrics();
        // take last 7 for display so it fits nicely
        int start = Math.max(0, metrics.size() - 7);
        for (int i = start; i < metrics.size(); i++) {
            DatabaseManager.HealthMetric m = metrics.get(i);
            String dateLabel = m.date.substring(5); // mm-dd
            
            if (m.heartRate > 0) seriesHR.getData().add(new XYChart.Data<>(dateLabel, m.heartRate));
            if (m.bpSys > 0) {
                seriesSys.getData().add(new XYChart.Data<>(dateLabel, m.bpSys));
                if (m.bpSys > 140) highBpWarning = true;
            }
            if (m.bpDia > 0) seriesDia.getData().add(new XYChart.Data<>(dateLabel, m.bpDia));
            if (m.bloodSugar > 0) seriesSugar.getData().add(new XYChart.Data<>(dateLabel, m.bloodSugar));
            if (m.weight > 0) seriesWeight.getData().add(new XYChart.Data<>(dateLabel, m.weight));
        }

        lineChart.getData().addAll(seriesSys, seriesDia, seriesHR, seriesSugar, seriesWeight);
        
        if (highBpWarning) {
            warningLabel.setText("⚠️ Warning: System detected recent High Blood Pressure (>140 Sys). Please consult doctor.");
            warningLabel.setVisible(true);
        } else {
            warningLabel.setVisible(false);
        }
    }

    private void showAddDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("New Metric");
        dialog.getDialogPane().setStyle("-fx-background-color: #1E1E1E;");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new Insets(20));

        DatePicker datePicker = new DatePicker(LocalDate.now());
        TextField bpSysField = new TextField(); bpSysField.setPromptText("120");
        TextField bpDiaField = new TextField(); bpDiaField.setPromptText("80");
        TextField hrField = new TextField(); hrField.setPromptText("72");
        TextField sugarField = new TextField(); sugarField.setPromptText("100");
        TextField weightField = new TextField(); weightField.setPromptText("65.5");

        Label lDate = new Label("Date:"); lDate.setStyle("-fx-text-fill: white;");
        Label lSys = new Label("BP Sys:"); lSys.setStyle("-fx-text-fill: white;");
        Label lDia = new Label("BP Dia:"); lDia.setStyle("-fx-text-fill: white;");
        Label lHr = new Label("Heart:"); lHr.setStyle("-fx-text-fill: white;");
        Label lSugar = new Label("Sugar:"); lSugar.setStyle("-fx-text-fill: white;");
        Label lWeight = new Label("Weight:"); lWeight.setStyle("-fx-text-fill: white;");

        grid.add(lDate, 0, 0); grid.add(datePicker, 1, 0);
        grid.add(lSys, 0, 1); grid.add(bpSysField, 1, 1);
        grid.add(lDia, 0, 2); grid.add(bpDiaField, 1, 2);
        grid.add(lHr, 0, 3); grid.add(hrField, 1, 3);
        grid.add(lSugar, 0, 4); grid.add(sugarField, 1, 4);
        grid.add(lWeight, 0, 5); grid.add(weightField, 1, 5);

        dialog.getDialogPane().setContent(grid);
        
        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                int sys = safelyParseInt(bpSysField.getText());
                int dia = safelyParseInt(bpDiaField.getText());
                int hr = safelyParseInt(hrField.getText());
                int sugar = safelyParseInt(sugarField.getText());
                double weight = safelyParseDouble(weightField.getText());

                if(datePicker.getValue() != null && (sys > 0 || dia > 0 || hr > 0 || sugar > 0 || weight > 0)) {
                    DatabaseManager.addMetric(
                        datePicker.getValue().toString(), sys, dia, hr, sugar, weight
                    );
                    loadChartData();
                }
            }
        });
    }

    private int safelyParseInt(String s) {
        try { return Integer.parseInt(s); } catch (Exception e) { return 0; }
    }
    
    private double safelyParseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
    }
}

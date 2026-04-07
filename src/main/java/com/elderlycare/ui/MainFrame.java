package com.elderlycare.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class MainFrame extends BorderPane {

    private final AppointmentPanel appointmentPanel;
    private final MedicationPanel medicationPanel;
    private final HealthMetricsPanel healthPanel;

    public MainFrame() {
        // Initialize Panels
        appointmentPanel = new AppointmentPanel();
        medicationPanel = new MedicationPanel();
        healthPanel = new HealthMetricsPanel();

        // Top Header
        HBox header = new HBox();
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER);
        Label titleLabel = new Label("Elderly Care");
        titleLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
        header.getChildren().add(titleLabel);
        setTop(header);

        // Bottom Navigation Bar
        HBox navBar = new HBox();
        navBar.getStyleClass().add("bottom-nav");
        navBar.setAlignment(Pos.CENTER);

        ToggleGroup navGroup = new ToggleGroup();

        ToggleButton btnAppt = createNavTab("📅\nAppts");
        btnAppt.setToggleGroup(navGroup);
        btnAppt.setSelected(true);
        btnAppt.setOnAction(e -> setCenter(appointmentPanel));

        ToggleButton btnMed = createNavTab("💊\nMeds");
        btnMed.setToggleGroup(navGroup);
        btnMed.setOnAction(e -> setCenter(medicationPanel));

        ToggleButton btnHealth = createNavTab("❤️\nHealth");
        btnHealth.setToggleGroup(navGroup);
        btnHealth.setOnAction(e -> setCenter(healthPanel));

        Region spacer1 = new Region(); HBox.setHgrow(spacer1, Priority.ALWAYS);
        Region spacer2 = new Region(); HBox.setHgrow(spacer2, Priority.ALWAYS);

        navBar.getChildren().addAll(btnAppt, spacer1, btnMed, spacer2, btnHealth);
        setBottom(navBar);

        // Set default view
        setCenter(appointmentPanel);
    }

    private ToggleButton createNavTab(String text) {
        ToggleButton btn = new ToggleButton(text);
        btn.getStyleClass().add("nav-tab");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setMinHeight(80);
        return btn;
    }
}

package com.elderlycare;

public class Main {
    public static void main(String[] args) {
        // We use a separate Main class that doesn't extend Application
        // to avoid module path issues and simplify packaging/running
        ElderlyCareApp.main(args);
    }
}

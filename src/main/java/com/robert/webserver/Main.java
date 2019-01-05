package com.robert.webserver;

public class Main {

    public static void main(String[] args) {
        try {
            new WebServer().bind();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

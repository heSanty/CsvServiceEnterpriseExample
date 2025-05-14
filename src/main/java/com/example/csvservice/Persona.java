package com.example.csvservice;

public class Persona {
    private int id;
    private String nombre;
    private String apellido;
    private String curp;
    private boolean isDuplicated;

    public Persona(int id, String nombre, String apellido, String curp) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.curp = curp;
        this.isDuplicated = false;
    }

    public String getCurp() {
        return curp;
    }

    public void setDuplicated(boolean duplicated) {
        isDuplicated = duplicated;
    }

    @Override
    public String toString() {
        return id + ": " + nombre + " " + apellido + " | CURP: " + curp + " | Duplicado: " + isDuplicated;
    }
}
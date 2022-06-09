package com.wikigami.wikigami;

import androidx.annotation.NonNull;

public class Tutorial {
    private String id;
    private String nombre;
    private String descripcion;
    private String idVideo;
    private String idCreador;
    private String miniatura;
    private String dificultad;

    public Tutorial() {
    }

    public Tutorial(String id, String nombre, String descripcion, String idVideo, String idCreador, String miniatura, String dificultad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idVideo = idVideo;
        this.idCreador = idCreador;
        this.miniatura = miniatura;
        this.dificultad = dificultad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIdVideo() {
        return idVideo;
    }

    public void setIdVideo(String idVideo) {
        this.idVideo = idVideo;
    }

    public String getIdCreador() {
        return idCreador;
    }

    public void setIdCreador(String idCreador) {
        this.idCreador = idCreador;
    }

    public String getMiniatura() {
        return miniatura;
    }

    public void setMiniatura(String miniatura) {
        this.miniatura = miniatura;
    }

    @NonNull
    public String toString() {
        String returnStr = "{\n";
        returnStr += "\tid: " + id + ",\n";
        returnStr += "\tnombre: " + nombre + ",\n";
        returnStr += "\tdescripcion: " + descripcion + ",\n";
        returnStr += "\tidVideo: " + idVideo + ",\n";
        returnStr += "\tidCreador: " + idCreador + ",\n";
        returnStr += "\tDificultad: " + dificultad + ",\n";
        returnStr += "\tminiatura: " + miniatura + "";
        returnStr += "}";
        return returnStr;
    }

    public String getDificultad() {
        return dificultad;
    }

    public void setDificultad(String dificultad) {
        this.dificultad = dificultad;
    }
}

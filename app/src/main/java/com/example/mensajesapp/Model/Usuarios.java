package com.example.mensajesapp.Model;

public class Usuarios {

    private String id;
    private String usuario;
    private String imageURL;

    public Usuarios() {

    }

    public Usuarios(String id, String usuario, String imageURL) {
        this.id = id;
        this.usuario = usuario;
        this.imageURL = imageURL;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

}

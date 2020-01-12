package com.example.tp;

public class Model {
    private int id;
    private String name ;
    private String adresse;
    private String phone;
    private String formation;
    private  String specialite;
    private byte[] image;

    public Model(int id, String name, String adresse, String phone, String formation ,String specialite , byte[] image) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.phone = phone;
        this.formation = formation;
        this.specialite = specialite;
        this.image = image;
    }


    public Model(int id, String name, String adresse, String phone ,byte[] image) {
        this.id = id;
        this.name = name;
        this.adresse = adresse;
        this.phone = phone;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }
}

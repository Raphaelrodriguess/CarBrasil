package com.example.carbrasilapp.model;

import com.example.carbrasilapp.helper.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

public class Anuncio {

    private String idanuncio;
    private String estados;
    private String marca;
    private String titulo;
    private String valor;
    private String telefone;
    private String descricao;
    private List <String> fotos;

    public Anuncio() {

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getReferenciaFirebase()
                .child("meus_anúncios");

            setIdanuncio(anuncioRef.push().getKey());

    }

    public void salvar(){

        String IdUsuario = ConfiguracaoFirebase.getIdUsuario();
        DatabaseReference anuncioRef = ConfiguracaoFirebase.getReferenciaFirebase()
                .child("meus_anúncios");

        anuncioRef.child(IdUsuario)
                .child(getIdanuncio())
                .setValue(this);

        salvarAnuncioPublico();
    }

    public void salvarAnuncioPublico(){

        DatabaseReference anuncioRef = ConfiguracaoFirebase.getReferenciaFirebase()
                .child("anúncios");

        anuncioRef.child(getEstados())
                .child(getMarca())
                .child(getIdanuncio())
                .setValue(this);
    }


    public String getIdanuncio() {
        return idanuncio;
    }

    public void setIdanuncio(String idanuncio) {
        this.idanuncio = idanuncio;
    }

    public String getEstados() {
        return estados;
    }

    public void setEstados(String estados) {
        this.estados = estados;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }
}



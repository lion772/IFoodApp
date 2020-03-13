package williamlopes.cursoandroid.ifoodapp.model;

import android.provider.ContactsContract;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import williamlopes.cursoandroid.ifoodapp.helper.ConfiguracaoFirebase;

public class Empresa implements Serializable {

    private String nome;
    private String categoria;
    private String tempo;
    private Double precoEntrega;
    private String idUsuario;
    private String urlImagem;

    public Empresa() {
    }

    public String getNome() {
        return nome;
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child(getIdUsuario());
        empresaRef.setValue(this);
    }

    public void remover(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef
                .child("empresas");
        empresaRef.removeValue();

    }

    public Double getPrecoEntrega() {
        return precoEntrega;
    }

    public void setPrecoEntrega(Double precoEntrega) {
        this.precoEntrega = precoEntrega;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }


}

package williamlopes.cursoandroid.ifoodapp.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import williamlopes.cursoandroid.ifoodapp.helper.ConfiguracaoFirebase;

public class Produto implements Serializable {

    private String idUsuario;
    private String idProduto;
    private String nome;
    private String descricao;
    private Double preco;

    public Produto() {

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos");
        setIdProduto(produtoRef.push().getKey()); //Toda vez que instanciar a classe Produto, irá configurar o idProduto

    }


    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child(getIdUsuario())
                .child(getIdProduto());
        produtoRef.setValue(this);
    }

    public void remover(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase();
        DatabaseReference produtoRef = firebaseRef
                .child("produtos")
                .child(getIdUsuario())
                .child(getIdProduto());
        produtoRef.removeValue(); //removendo o produto
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(String idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }
}

package williamlopes.cursoandroid.ifoodapp.model;

import com.google.firebase.database.DatabaseReference;

import williamlopes.cursoandroid.ifoodapp.helper.ConfiguracaoFirebase;

public class Usuario {

    private String idUsuario;
    private String nome;
    private String endereco;
    private String urlImagem;

    public Usuario() {
    }


    public void salvar(){

        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebase(); // Objeto que permite salvar os dados no firebase
        DatabaseReference usuariosRef = firebaseRef.child("usuarios")
                .child( getIdUsuario() );
        usuariosRef.setValue( this );

    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }


}

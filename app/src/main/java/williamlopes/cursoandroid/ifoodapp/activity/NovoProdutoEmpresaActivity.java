package williamlopes.cursoandroid.ifoodapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import williamlopes.cursoandroid.ifoodapp.R;
import williamlopes.cursoandroid.ifoodapp.helper.UsuarioFirebase;
import williamlopes.cursoandroid.ifoodapp.model.Produto;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao,
            editProdutoPreco;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        //Configurações iniciais
        inicializarComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurando a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo produto");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void validaDadosProduto(View view){

        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        if ( !nome.isEmpty() ){
            if ( !descricao.isEmpty() ){
                if ( !preco.isEmpty() ){

                    Produto produto = new Produto();
                    produto.setNome( nome );
                    produto.setDescricao( descricao );
                    produto.setPreco( Double.parseDouble(preco) );
                    produto.setIdUsuario( idUsuarioLogado );
                    produto.salvar();
                    finish();

                    exibirMensagem("Produto salvo com sucesso!");

                }else{

                    exibirMensagem("Preencha o campo preço!");
                }

            }else{

                exibirMensagem("Preencha o campo descrição!");
            }

        }else{

            exibirMensagem("Preencha o campo nome produto!");
        }

    }


    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }

    private void inicializarComponentes(){
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
    }
}

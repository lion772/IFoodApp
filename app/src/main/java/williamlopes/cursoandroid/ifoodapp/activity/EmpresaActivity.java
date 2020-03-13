package williamlopes.cursoandroid.ifoodapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import williamlopes.cursoandroid.ifoodapp.R;
import williamlopes.cursoandroid.ifoodapp.adapter.AdapterProduto;
import williamlopes.cursoandroid.ifoodapp.helper.ConfiguracaoFirebase;
import williamlopes.cursoandroid.ifoodapp.helper.UsuarioFirebase;
import williamlopes.cursoandroid.ifoodapp.listener.RecyclerItemClickListener;
import williamlopes.cursoandroid.ifoodapp.model.Produto;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private AdapterProduto adapterProduto;
    private RecyclerView recyclerProdutos;
    private List<Produto> produtos = new ArrayList<>();

    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        //Configurações iniciais
        inicializarProdutos();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurando a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood - empresa");
        setSupportActionBar(toolbar);

        //Configurar o recyclerview
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutos.setHasFixedSize(true);
        recyclerProdutos.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutos.setAdapter( adapterProduto );

        //Recupera produtos para empresa
        recuperarProdutos();

        //Evento de clique no recyclerview
        recyclerProdutos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerProdutos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                        Produto produtoSelecionado = produtos.get(position);
                        produtoSelecionado.remover();
                        Toast.makeText(EmpresaActivity.this,
                                "Produto excluído com sucesso!",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));



    }


    private void recuperarProdutos(){

        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child(idUsuarioLogado); //idUsuarioLogado se refere a uma empresa

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    //Recupera os produtos e exibir dentro do recyclerview
                    Produto produto = ds.getValue(Produto.class);
                    produtos.add( produto );

                }
                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    private void inicializarProdutos(){

        recyclerProdutos = findViewById(R.id.recyclerProdutos);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuNovoProduto:
                abrirNovoProduto();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
            case R.id.menuSair:
                deslogarUsuario();
                break;
            case R.id.menuPedidos:
                abrirPedidos();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deslogarUsuario(){

        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirPedidos(){
        startActivity(new Intent(EmpresaActivity.this, PedidosActivity.class));
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracoesEmpresaActivity.class));
    }

    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }
}

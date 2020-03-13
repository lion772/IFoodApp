package williamlopes.cursoandroid.ifoodapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

import williamlopes.cursoandroid.ifoodapp.R;
import williamlopes.cursoandroid.ifoodapp.adapter.AdapterEmpresa;
import williamlopes.cursoandroid.ifoodapp.helper.ConfiguracaoFirebase;
import williamlopes.cursoandroid.ifoodapp.helper.UsuarioFirebase;
import williamlopes.cursoandroid.ifoodapp.listener.RecyclerItemClickListener;
import williamlopes.cursoandroid.ifoodapp.model.Empresa;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private MaterialSearchView searchView;
    private RecyclerView recyclerEmpresa;

    private AdapterEmpresa adapterEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //inicializar as componentes
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurando a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood");
        setSupportActionBar(toolbar);

        //Configurando o recyclerview
        adapterEmpresa = new AdapterEmpresa( empresas );
        recyclerEmpresa.setHasFixedSize(true);
        recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this));
        recyclerEmpresa.setAdapter( adapterEmpresa );

        //Recupera empresas
        recuperarEmpresas();

        //configurar searchview
        searchView.setHint("Pesquisar restaurantes");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                pesquisarEmpresas(newText);
                return true;
            }
        });

        recyclerEmpresa.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerEmpresa,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        Empresa empresaSelecionada = empresas.get( position );

                        Intent i = new Intent(HomeActivity.this, CardapioActivity.class);
                        i.putExtra("empresa", empresaSelecionada);
                        startActivity( i );

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        Empresa empresaSelecionada = empresas.get(position);
                        empresaSelecionada.remover();
                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

    }


    private void pesquisarEmpresas(String pesquisa){

        DatabaseReference empresasRef = firebaseRef
                .child("empresas");
        Query query = empresasRef.orderByChild( "nome" )
                .startAt( pesquisa )
                .endAt( pesquisa + "\uf8ff" );

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empresas.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void recuperarEmpresas(){

        DatabaseReference empresasRef = firebaseRef
                .child("empresas");
        empresasRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empresas.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        //Configurar botao de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem( item );

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
            case R.id.menuSair:
                deslogarUsuario();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void inicializarComponentes(){
        searchView = findViewById(R.id.materialSearchView);
        recyclerEmpresa = findViewById(R.id.recyclerEmpresa);
    }



    private void deslogarUsuario(){

        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private void abrirConfiguracoes(){
        startActivity(new Intent(getApplicationContext(), ConfiguracoesUsuarioActivity.class));
    }

}

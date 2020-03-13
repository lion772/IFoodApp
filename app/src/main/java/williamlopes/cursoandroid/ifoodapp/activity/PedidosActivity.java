package williamlopes.cursoandroid.ifoodapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import williamlopes.cursoandroid.ifoodapp.R;
import williamlopes.cursoandroid.ifoodapp.adapter.AdapterPedido;
import williamlopes.cursoandroid.ifoodapp.helper.ConfiguracaoFirebase;
import williamlopes.cursoandroid.ifoodapp.helper.UsuarioFirebase;
import williamlopes.cursoandroid.ifoodapp.listener.RecyclerItemClickListener;
import williamlopes.cursoandroid.ifoodapp.model.Pedido;

public class PedidosActivity extends AppCompatActivity {

    private RecyclerView recyclerPedidos;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        //Configurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = UsuarioFirebase.getIdUsuario();

        //Configurar toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar( toolbar );
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Configurar recyclerView
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedidos.setHasFixedSize(true);
        recyclerPedidos.setLayoutManager(new LinearLayoutManager(this));
        recyclerPedidos.setAdapter( adapterPedido );

        //Recuperar os pedidos
        recuperarPedidos();

        //Adicionar evento de clique no recyclerView
        recyclerPedidos.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerPedidos,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                    }

                    @Override
                    public void onLongItemClick(View view, final int position) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(PedidosActivity.this);
                        builder.setMessage("Você realmente deseja finalizar esse pedido?");
                        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Pedido pedido = pedidos.get(position);
                                pedido.setStatus("finalizado");
                                pedido.atualizarStatus();

                            }
                        });
                        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

    }

    private void recuperarPedidos() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("Carregando")
                .build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos")
                .child( idEmpresa );

        Query pedidoPesquisa = pedidoRef.orderByChild("status") //Filtrar primeiro por status através de Query, pois só queremos os confirmados
                .equalTo("confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pedidos.clear();
                if ( dataSnapshot.getValue() != null ) { //testar se foi possível recuperar o datasnapshot
                    for ( DataSnapshot ds: dataSnapshot.getChildren() ){

                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add( pedido );
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }




    private void inicializarComponentes() {

        recyclerPedidos = findViewById(R.id.recyclerPedidos);
    }
}

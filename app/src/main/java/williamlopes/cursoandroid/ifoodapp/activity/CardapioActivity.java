package williamlopes.cursoandroid.ifoodapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import williamlopes.cursoandroid.ifoodapp.R;
import williamlopes.cursoandroid.ifoodapp.adapter.AdapterProduto;
import williamlopes.cursoandroid.ifoodapp.helper.ConfiguracaoFirebase;
import williamlopes.cursoandroid.ifoodapp.helper.UsuarioFirebase;
import williamlopes.cursoandroid.ifoodapp.listener.RecyclerItemClickListener;
import williamlopes.cursoandroid.ifoodapp.model.Empresa;
import williamlopes.cursoandroid.ifoodapp.model.ItemPedido;
import williamlopes.cursoandroid.ifoodapp.model.Pedido;
import williamlopes.cursoandroid.ifoodapp.model.Produto;
import williamlopes.cursoandroid.ifoodapp.model.Usuario;

public class CardapioActivity extends AppCompatActivity {

    private Empresa empresaSelecionada;
    private RecyclerView recyclerProdutosCardapio;
    private ImageView imageEmpresaCardapio;
    private TextView textNomeEmpresaCardapio;
    private AlertDialog dialog;
    private TextView textCarrinhoQtd, textCarrinhoTotal;

    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private List<ItemPedido> itensCarrinho = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private Usuario usuario;

    private String idEmpresa;
    private String idUsuarioLogado;
    private Pedido pedidoRecuperado;

    private int qtdItensCarrinho;
    private Double totalCarrinho;
    private int metodoPagamento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardapio);

        //Congiurações iniciais
        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Cardápio");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Recuperar empresa selecionada
        Bundle bundle = getIntent().getExtras();
        if (  bundle != null ){

            empresaSelecionada = (Empresa) bundle.getSerializable("empresa");

            textNomeEmpresaCardapio.setText(empresaSelecionada.getNome());
            idEmpresa = empresaSelecionada.getIdUsuario();

            String urlImagem = empresaSelecionada.getUrlImagem();
            Picasso.get().load( urlImagem ).into( imageEmpresaCardapio );
        }

        //Configurando recyclerView
        adapterProduto = new AdapterProduto(produtos, this);
        recyclerProdutosCardapio.setLayoutManager(new LinearLayoutManager(this));
        recyclerProdutosCardapio.setHasFixedSize(true);
        recyclerProdutosCardapio.setAdapter(adapterProduto);

        //Configurar evento de clique
        recyclerProdutosCardapio.addOnItemTouchListener(new RecyclerItemClickListener(
                this,
                recyclerProdutosCardapio,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        confirmarQuantidade(position);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

        //Recuperar produtos para empresa
        recuperarProdutos();
        recuperarDadosUsuario();

    }

    private void confirmarQuantidade(final int posicao) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Quantidade");
        builder.setMessage("Digite a quantidade");

        final EditText editQuantidade = new EditText(this);
        editQuantidade.setText("1");
        builder.setView( editQuantidade );

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String quantidade = editQuantidade.getText().toString();

                Produto produtoSelecionado = produtos.get( posicao );
                ItemPedido itemPedido = new ItemPedido();
                itemPedido.setIdProduto( produtoSelecionado.getIdProduto() );
                itemPedido.setNomeProduto( produtoSelecionado.getNome() );
                itemPedido.setPreco( produtoSelecionado.getPreco() );
                itemPedido.setQuantidade( Integer.parseInt(quantidade) );

                itensCarrinho.add( itemPedido );

                if (pedidoRecuperado == null){
                    pedidoRecuperado = new Pedido(idUsuarioLogado, idEmpresa); //Quando instanciarmos a classe Pedido, já teremos o idPedido configurado pelo push() e capturado pela classe ItemPedido

                }

                pedidoRecuperado.setNome( usuario.getNome() );
                pedidoRecuperado.setEndereco( usuario.getEndereco() );
                pedidoRecuperado.setItens( itensCarrinho );
                pedidoRecuperado.salvar();




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

    private void recuperarDadosUsuario() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false)
                .build();
        dialog.show();

        DatabaseReference usuariosRef = firebaseRef
                .child("usuarios")
                .child(idUsuarioLogado);

        usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() != null) { //testar se temos essas informações em "usuarios"

                    usuario = dataSnapshot.getValue(Usuario.class);
                }

                recuperarPedido();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuperarPedido() {

        DatabaseReference pedidoRef = firebaseRef
                .child("pedidos_usuario")
                .child( idEmpresa )
                .child( idUsuarioLogado );
        pedidoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                qtdItensCarrinho = 0;
                totalCarrinho = 0.0;
                itensCarrinho = new ArrayList<>(); //temos que zerar esses parâmetros antes de fazer um novo pedido, senão a cada novo pedido ele vai somar com os pedidos anteriores, e não queremos isso

                if ( dataSnapshot.getValue() != null ){ //testar se realmente há resultado dentro do snapshot para pedidos, para saber se o cliente fez algum pedido ou não

                    pedidoRecuperado = dataSnapshot.getValue(Pedido.class);
                    itensCarrinho = pedidoRecuperado.getItens();

                    for ( ItemPedido itemPedido: itensCarrinho ){

                        int qtde = itemPedido.getQuantidade();
                        Double preco = itemPedido.getPreco();

                        totalCarrinho += (qtde * preco);
                        qtdItensCarrinho += qtde;
                    }

                }
                DecimalFormat df = new DecimalFormat("0.00");

                textCarrinhoQtd.setText( "qtd: " + qtdItensCarrinho );
                textCarrinhoTotal.setText("R$: " + df.format(totalCarrinho));

                dialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void recuperarProdutos(){

        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child( idEmpresa ); // Como um cliente estará logado, não usaremos idUsuarioLogado, pois queremos o id da empresa!

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                produtos.clear();

                for (DataSnapshot ds: dataSnapshot.getChildren()){

                    //Recupera os produtos e exibir dentro do reyclerview
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_cardapio, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){

            case R.id.menuPedido:
                confirmarPedido();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmarPedido() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecione um método de pagamento");
        //builder.setMessage();

        CharSequence[] itens = new CharSequence[]{
            "Dinheiro", "Máquina de cartão"
        };
        builder.setSingleChoiceItems(itens, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                metodoPagamento = which; //which se refere ao item que foi selecionado, se for o índice 0 é o dinheiro, se for 1, a máquina de cartão
            }
        });

        final EditText editObservacao = new EditText(this);
        editObservacao.setHint("Digite uma observação");
        builder.setView( editObservacao );

        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String observacao = editObservacao.getText().toString();
                pedidoRecuperado.setMetodoPagamento( metodoPagamento);
                pedidoRecuperado.setObservacao( observacao );
                pedidoRecuperado.setStatus("confirmado");
                pedidoRecuperado.confirmar();
                pedidoRecuperado.remover();
                pedidoRecuperado = null;
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

    private void inicializarComponentes() {
            recyclerProdutosCardapio = findViewById(R.id.recyclerProdutosCardapio);
            imageEmpresaCardapio = findViewById(R.id.imageEmpresaCardapio);
            textNomeEmpresaCardapio = findViewById(R.id.textNomeEmpresaCardapio);

        textCarrinhoQtd = findViewById(R.id.textCarrinhoQtd);
        textCarrinhoTotal = findViewById(R.id.textCarrinhoTotal);

    }
}

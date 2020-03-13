package williamlopes.cursoandroid.ifoodapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import williamlopes.cursoandroid.ifoodapp.R;
import williamlopes.cursoandroid.ifoodapp.helper.ConfiguracaoFirebase;
import williamlopes.cursoandroid.ifoodapp.helper.UsuarioFirebase;
import williamlopes.cursoandroid.ifoodapp.model.Empresa;
import williamlopes.cursoandroid.ifoodapp.model.Usuario;

public class ConfiguracoesUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioEndereco;
    private ImageView imagePerfilUsuario;


    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuario;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuario);

        //Configurações iniciais
        inicializarComponentes();
        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuario = UsuarioFirebase.getIdUsuario();


        //Configurando a toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações usuario");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if ( i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        //Recuperar dados do usuario
        recuperarDadosUsuario();
    }

    private void recuperarDadosUsuario(){

        DatabaseReference usarioRef = firebaseRef
                .child("usuarios")
                .child(idUsuario);
        usarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if ( dataSnapshot.getValue() != null ){

                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    editUsuarioNome.setText( usuario.getNome() );
                    editUsuarioEndereco.setText( usuario.getEndereco() );
                    urlImagemSelecionada = usuario.getUrlImagem();

                    if (urlImagemSelecionada != ""){

                        Picasso.get().load(urlImagemSelecionada).into(imagePerfilUsuario);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    public void validarDadosUsuario(View view){

        String nome = editUsuarioNome.getText().toString();
        String endereco = editUsuarioEndereco.getText().toString();


        if (!nome.isEmpty()) {
            if (!endereco.isEmpty()) {

                Usuario usuario = new Usuario();
                usuario.setIdUsuario(idUsuario);
                usuario.setNome(nome);
                usuario.setEndereco(endereco);
                usuario.setUrlImagem(urlImagemSelecionada);
                usuario.salvar();
                finish();


            }else{

                Toast.makeText(this,
                        "Preencha o campo nome!",
                        Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,
                    "Preencha o campo nome!",
                    Toast.LENGTH_SHORT).show();
        }
    }






    @Override //Atualizar a foto no imagePerfilEmpresa e dar upload no firebase
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if( imagem != null){

                    imagePerfilUsuario.setImageBitmap( imagem );

                    //Formatação
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[]dadosImagem = baos.toByteArray();
                    //Referenciar a imagem no storageReference
                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuario + ".jpeg");
                    //Upload da imagem
                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracoesUsuarioActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });


                }




            }catch (Exception e){
                e.printStackTrace();
            }

        }
    }

    private void inicializarComponentes(){
        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editUsuarioEndereco = findViewById(R.id.editUsuarioEndereco);
        imagePerfilUsuario = findViewById(R.id.imagePerfilUsuario);

    }
}

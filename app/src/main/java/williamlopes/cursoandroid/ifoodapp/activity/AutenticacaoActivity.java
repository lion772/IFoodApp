package williamlopes.cursoandroid.ifoodapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import williamlopes.cursoandroid.ifoodapp.R;
import williamlopes.cursoandroid.ifoodapp.helper.ConfiguracaoFirebase;
import williamlopes.cursoandroid.ifoodapp.helper.UsuarioFirebase;
import williamlopes.cursoandroid.ifoodapp.model.Usuario;

public class AutenticacaoActivity extends AppCompatActivity {

    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso, tipoUsuario;
    private Button botaoAcessar;
    private LinearLayout linearTipoUsuario;

    private FirebaseAuth autenticacao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticacao);


        inicializarComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

        //Verificar usuario logado
        verificarUsuarioLogado();

        //Verificar se o switch está configurado ou não, e os diferentes Intents - 1ª
        tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked){//empresa
                    linearTipoUsuario.setVisibility(View.VISIBLE);
                }else{//usuario
                    linearTipoUsuario.setVisibility(View.GONE);

                }
            }
        });

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if ( !email.isEmpty() ){
                    if ( !senha.isEmpty() ){

                        if ( tipoAcesso.isChecked() ){//cadastrar

                            autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
                            autenticacao.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Sucesso ao cadastrar!",
                                                Toast.LENGTH_SHORT).show();
                                        String tipoUsuario = getTipoUsuario();
                                        UsuarioFirebase.atualizarTipoUsuario( tipoUsuario );
                                        abrirTelaPrincipal( tipoUsuario ); //6º

                                    }else{

                                        String excecao = "";
                                        try {
                                            throw task.getException();
                                        }catch (FirebaseAuthWeakPasswordException e){
                                            excecao = "Digite uma senha mais forte";
                                        }catch (FirebaseAuthInvalidCredentialsException e){
                                            excecao ="Digite um email válido";
                                        }catch (FirebaseAuthUserCollisionException e){
                                            excecao = "Esse email já existe";
                                        }catch (Exception e){
                                            excecao = "Erro ao cadastrar o usuário" + e.getMessage();
                                            e.printStackTrace();
                                        }

                                    }

                                }
                            });

                        }else{//logar

                            autenticacao.signInWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Sucesso ao logar!",
                                                Toast.LENGTH_SHORT).show();
                                        String tipoUsuario = task.getResult().getUser().getDisplayName();
                                        abrirTelaPrincipal( tipoUsuario ); //5ª


                                    }else{
                                        Toast.makeText(AutenticacaoActivity.this,
                                                "Erro ao logar" + task.getException(),
                                                Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });

                        }

                    }else{
                        Toast.makeText(AutenticacaoActivity.this,
                                "preencha o email",
                                Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AutenticacaoActivity.this,
                            "preencha o email",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void verificarUsuarioLogado(){

        FirebaseUser usuarioAtual = autenticacao.getCurrentUser();
        if (usuarioAtual != null){
            String tipoUsuario = usuarioAtual.getDisplayName(); //O getDisplayName() não está retornando o nome, e sim o tipo de usuário, segundo atualizarTipoUsuario() no UsuarioFirebase
            abrirTelaPrincipal( tipoUsuario );
        }
    }


    private String getTipoUsuario(){ // 2ª

        return tipoUsuario.isChecked()? "E" : "U";
    }


    private void abrirTelaPrincipal(String tipoUsuario){ //4ª

        if ( tipoUsuario.equals("E") ){ //empresa
            startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));

        }else{//usuario
            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
        }
    }




    private void inicializarComponentes(){

        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        tipoAcesso = findViewById(R.id.switchAcesso);
        tipoUsuario = findViewById(R.id.switchTipoUsuario);
        linearTipoUsuario = findViewById(R.id.linearTipoUsuario);
        botaoAcessar = findViewById(R.id.buttonAcesso);
    }
}

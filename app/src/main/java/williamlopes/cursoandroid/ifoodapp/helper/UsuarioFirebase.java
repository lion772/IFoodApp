package williamlopes.cursoandroid.ifoodapp.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import williamlopes.cursoandroid.ifoodapp.model.Usuario;

public class UsuarioFirebase {

    public static String getIdUsuario(){

        return getUsuarioAtual().getUid();
    }

    public static FirebaseUser getUsuarioAtual(){

        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();

    }

    public static boolean atualizarNomeUsuario(String nome){

        try{
            //Usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();
            //Configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName( nome )
                    .build();

            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( !task.isSuccessful() ){

                        Log.d("Perfil", "Erro ao atualizar nome do perfil");
                    }
                }
            });
            return true;

        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }


    public static boolean atualizarTipoUsuario(String tipo){ //3ª

        try {

            FirebaseUser user = getUsuarioAtual();
            //Configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                .setDisplayName( tipo )
                                .build();
            user.updateProfile( profile );


            return true;

        }catch ( Exception e ){
            e.printStackTrace();
            return false;
        }



    }


    public static void atualizarFotoUsuario(Uri url){

        try{
            //Usuario logado no app
            FirebaseUser usuarioLogado = getUsuarioAtual();
            //Configurar objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri( url )
                    .build();

            usuarioLogado.updateProfile( profile ).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if ( !task.isSuccessful() ){

                        Log.d("Perfil", "Erro ao atualizar a foto do perfil");
                    }
                }
            });


        } catch (Exception e){
            e.printStackTrace();

        }

    }


    public static Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();

        Usuario usuario = new Usuario();
        usuario.setNome(firebaseUser.getDisplayName());
        usuario.setIdUsuario(firebaseUser.getUid());

        if ( firebaseUser.getPhotoUrl() == null ){

            usuario.setUrlImagem("");

        }else {

            usuario.setUrlImagem(firebaseUser.getPhotoUrl().toString());

        }

        return  usuario;

    }
}

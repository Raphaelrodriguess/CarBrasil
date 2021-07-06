package com.example.carbrasilapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carbrasilapp.R;
import com.example.carbrasilapp.helper.ConfiguracaoFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import static android.widget.Toast.LENGTH_SHORT;

public class CadastroActivity extends AppCompatActivity {

    //Criação de Atributos
    private Button botaoAcessar;
    private Switch tipoAcesso;
    private EditText campoEmail, campoSenha;
    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        // Iniciar Método dos atributos criados
        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Recuperando o que o usuário digitou
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();
                // Validando o que foi digitado pelo usuário
                if (!email.isEmpty()) {
                    if (!senha.isEmpty()) {

                        // verificando em seguida se o switch foi marcado
                        if (tipoAcesso.isChecked()) {

                            autenticacao.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) { //cadastro
                                        Toast.makeText(CadastroActivity.this, "Cadastro Realizado com sucesso!",
                                                Toast.LENGTH_SHORT).show();
                                        //Direcionar usuário para tela principal

                                    } else //Erro em exceções
                                    {
                                        String erroExcecao = "";
                                        try {
                                            throw task.getException();
                                        } catch (FirebaseAuthWeakPasswordException e) {
                                            erroExcecao = "Digite uma senha mais forte";
                                        } catch (FirebaseAuthInvalidCredentialsException e) {
                                            erroExcecao = "Por favor, digite um e-mail válido";
                                        } catch (FirebaseAuthUserCollisionException e) {
                                            erroExcecao = "esta conta já foi cadastrada";
                                        } catch (Exception e) {
                                            erroExcecao = "Ao cadastrar usuário" + e.getMessage();
                                            e.printStackTrace();
                                        }
                                        Toast.makeText(CadastroActivity.this, "Erro " + erroExcecao,
                                                LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                     else { //login

                        autenticacao.signInWithEmailAndPassword(
                                email, senha
                        ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CadastroActivity.this, "Logado com sucesso!",
                                            LENGTH_SHORT).show();
                                    // Enviar usuario para tela principal
                                    startActivity( new Intent(getApplicationContext(),AnunciosActivity.class));
                                } else {
                                    Toast.makeText(CadastroActivity.this, "Erro ao fazer login:" + task.getException(),
                                            LENGTH_SHORT).show();

                                }

                            }
                        });//login
                    }

                }
            else
                {

                Toast.makeText(CadastroActivity.this, "Prencha a senha",
                        LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(CadastroActivity.this, "Prencha o e-mail",
                    LENGTH_SHORT).show();

        }
    }
  });


  }

    private void inicializaComponentes() {
        botaoAcessar = findViewById(R.id.buttonAcesso);
        campoEmail = findViewById(R.id.editCadastroEmail);
        campoSenha = findViewById(R.id.editCadastroSenha);
        tipoAcesso = findViewById(R.id.switchAcesso);
    }


}
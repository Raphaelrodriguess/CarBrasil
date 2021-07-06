package com.example.carbrasilapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carbrasilapp.R;
import com.example.carbrasilapp.helper.ConfiguracaoFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class AnunciosActivity extends AppCompatActivity {
    private  FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
       // autenticacao.signOut();

        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {


        if(autenticacao.getCurrentUser() == null) {//usuario deslogado
            menu.setGroupVisible(R.id.group_deslogado, true);
        }else {//usuario logado
            menu.setGroupVisible(R.id.group_logado,true);

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        switch ( item.getItemId()){
            case R.id.menu_cadastrar:
                startActivity(new Intent(getApplicationContext(),CadastroActivity.class));
                break;
            case R.id.menu_sair:
                autenticacao.signOut();
                break;
            case R.id.menu_anuncios:
                startActivity(new Intent(getApplicationContext(),MeusAnunciosActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
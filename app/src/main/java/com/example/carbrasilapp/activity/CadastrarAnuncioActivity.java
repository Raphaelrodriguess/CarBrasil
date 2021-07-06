package com.example.carbrasilapp.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.example.carbrasilapp.R;
import com.example.carbrasilapp.helper.ConfiguracaoFirebase;
import com.example.carbrasilapp.helper.Permissoes;
import com.example.carbrasilapp.model.Anuncio;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

import static android.widget.Toast.LENGTH_SHORT;

public class CadastrarAnuncioActivity extends AppCompatActivity

    implements View.OnClickListener {



        private EditText campoTitulo, campoDescricao;
        private CurrencyEditText campoValor;
        private MaskEditText campoTelefone;
        private ImageView imagem1, imagem2,imagem3;
        private Spinner campoEstado, campoMarca;
        private Anuncio anuncio;
        private StorageReference storage;
        private AlertDialog dialog;
        private List<String> ListaFotosRecuperadas = new ArrayList<>();
        private List<String> listaUrlFotos = new ArrayList<>();
        private String [] permissoes = new String[]{
        Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
        protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastrar_anuncio);

        storage = ConfiguracaoFirebase.getFirebaseStorage();
        //Validar permissões
        Permissoes.validarPermissoes(permissoes,this,1);

        //após configurar o método inicializarcomponentes, hora de inicializa-lo
        inicializarComponentes();
        carregarDadosSpinner();
    }

        @SuppressLint("WrongViewCast")
        private void inicializarComponentes () {

        campoTitulo = findViewById(R.id.editTitulo);
        campoDescricao = findViewById((R.id.editDescricao));
        campoValor = findViewById(R.id.editValor);
        campoTelefone = findViewById(R.id.editTelefone);
        // configurar localidade para pt - br
        Locale locale = new Locale("pt", "BR");
        campoValor.setLocale(locale);
        imagem1 = findViewById(R.id.imageCadastro1);
        imagem2 = findViewById(R.id.imageCadastro2);
        imagem3 = findViewById(R.id.imageCadastro3);
        campoEstado = findViewById(R.id.spinnerEstado);
        campoMarca = findViewById(R.id.spinnerMarca);

      //    MÉTODO PARA VALIDAR OS CAMPOS DOS ANUNCIOS PREENCHIDOS ANTES DE PUBLICA-LOS
    }




    public void salvarAnuncio() {

        /*carregar janela de progresso DialogSpots*/
        dialog = new SpotsDialog.Builder()
            .setContext(this)
            .setMessage("Salvando Anúncio")
            .setCancelable(false)
            .build();
        dialog.show();


        /* Salva imagem no Storage*/

        for (int i=0; i < ListaFotosRecuperadas.size();i++){
            String urlImagem = ListaFotosRecuperadas.get(i);
            int tamanhoLista = ListaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem, tamanhoLista, i );
        }
    }

    private void salvarFotoStorage(String urlString, final int totalFotos, int contador) {
        // criar nó no storage

        StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdanuncio())
                .child("imagem"+contador);

        // Realizar upload de arquivos
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString) );
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemAnuncio.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        Uri firebaseUrl = task.getResult();
                        String urlConvertida = firebaseUrl.toString();
                        listaUrlFotos.add(urlConvertida);
                        if(totalFotos == listaUrlFotos.size()){
                            anuncio.setFotos(listaUrlFotos);
                            anuncio.salvar();

                            dialog.dismiss();
                            finish();
                        }
                    }

                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                exibirMensagemErro("Falha ao realizar upload");
                Log.i("INFO","Falha ao fazer upload"+ e.getMessage());
            }
        });
    }

    private Anuncio configurarAnuncio(){

        String estados = campoEstado.getSelectedItem().toString();
        String marcas = campoMarca.getSelectedItem().toString();
        String titulo = campoTitulo.getText().toString();
        String valor = campoValor.getText().toString();
        String telefone = campoTelefone.getText().toString();
        String fone = "";
        fone = campoTelefone.getUnMasked().toString();
        if (campoTelefone.getUnMasked() != null) {
            fone = campoTelefone.getUnMasked().toString();
        }
        String descricao = campoDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstados(estados);
        anuncio.setMarca(marcas);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;


    }


            public void validarAnuncio(View view){

                anuncio = configurarAnuncio();
                String valor = String.valueOf(campoValor.getRawValue());

                if( ListaFotosRecuperadas.size() != 0  ){
                    if( !anuncio.getEstados().isEmpty() ){
                        if( !anuncio.getMarca().isEmpty() ){
                            if( !anuncio.getTitulo().isEmpty() ){
                                if( !valor.isEmpty() && !valor.equals("0") ){
                                    if( !anuncio.getTelefone().isEmpty()  ){
                                        if( !anuncio.getDescricao().isEmpty() ){

                                            salvarAnuncio();

                                        }else {
                                            exibirMensagemErro("Preencha o campo descrição");
                                        }
                                    }else {
                                        exibirMensagemErro("Preencha o campo telefone");
                                    }
                                }else {
                                    exibirMensagemErro("Preencha o campo valor");
                                }
                            }else {
                                exibirMensagemErro("Preencha o campo título");
                            }
                        }else {
                            exibirMensagemErro("Preencha o campo categoria");
                        }
                    }else {
                        exibirMensagemErro("Preencha o campo estado");
                    }
                }else {
                    exibirMensagemErro("Selecione ao menos uma foto!");
                }



            }


        // MÉTODO PARA RETORNAR MENSAGEM DE ERRO CASO O CAMPO NÃO FOR PREENCHIDO
        private void exibirMensagemErro(String mensagem){

            Toast.makeText(this,mensagem, LENGTH_SHORT).show();
        }



            //MÉTODO PARA CARREGADOS DADOS DO SPINNER
        private void carregarDadosSpinner(){
            //Configurar Spinner de Estados
            String[] estados = new String[]{
                    "Acre", "Alagoas", "Amapá", "Amazonas", "Bahia", "Ceará", "Distrito Federal", "Espírito Santo",
                    "Goiás", "Maranhão", "Mato Grosso", "Mato Grosso do Sul", "Minas Gerais",
                    "Paraná", "Paraíba", "Pará", "Pernambuco", "Piauí", "Rio Grande do Norte",
                    "Rio Grande do Sul", "Rio de Janeiro", "Rondônia",
                    "Roraima", "Santa Catarina", "Sergipe",
                    "São Paulo", "Tocantins"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,estados);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            campoEstado.setAdapter(adapter);

            String[] marcas = new String[]{

                    "Chevrolet","Fiat","Ford","Honda","Hyundai","Jeep","Nissan","Renault","Toyota","Volkswagen"

                    };
            ArrayAdapter<String> adapterMarca = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,marcas);
            adapterMarca.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            campoMarca.setAdapter(adapterMarca);
        }


    @Override
    public void onClick(View view) {

            switch (view.getId()){
                case  R.id.imageCadastro1:
                    escolherImagem(1);
                    break;
                case R.id.imageCadastro2:
                    escolherImagem(2);
                    break;
                case R.id.imageCadastro3:
                    escolherImagem(3);
                    break;
            }


    }

    public void escolherImagem(int requestCode){
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i,requestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){

            // recuperar imagem
            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();
            //configurar imagem no image view
            if ( requestCode ==1){
                    imagem1.setImageURI(imagemSelecionada);
            }else if(requestCode ==2){
                imagem2.setImageURI(imagemSelecionada);
            }else if(requestCode ==3){
                imagem3.setImageURI(imagemSelecionada);
            }

            ListaFotosRecuperadas.add(caminhoImagem);
        }
    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }



    /*public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for( int permissaoResultado : grantResults ){
            if( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }else {
                continue;
            }
        }

    }*/

}
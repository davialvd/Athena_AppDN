package com.example.athena_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    EditText txtCorreos,textPassword;
   Button btnIngresar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCorreos=(EditText)findViewById(R.id.txtCorreo);
        textPassword=(EditText)findViewById(R.id.textPassword);


        btnIngresar=(Button)findViewById(R.id.btnIngresar);
        btnIngresar.setOnClickListener(new View.OnClickListener(){



            public void onClick(View v) {

                Thread tr= new Thread(){

                    @Override
                    public void run() {
                        // Recibe paramatros usuario y contraseña y los envia a enviarPost
                        String res=enviarPost(txtCorreos.getText().toString(),textPassword.getText().toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                int r=objJson(res);
                                if (r>0){

                                    Intent i=new Intent(getApplicationContext(),principalUsuarios.class);
                                    startActivity(i);

                                }else {

                                    Toast.makeText(getApplicationContext(),"Usuario o Contraseña Incorrectos",Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                };
                tr.start();
            }
        });
}
        // Funcion para consultar usuario y contraseña
            public String enviarPost(String cor, String pas) {
                String TAG = MainActivity.class.getSimpleName();
                    String parametros="cor="+cor+"pas="+pas;

                HttpURLConnection connection=null;
                String respuesta="";

                try {
                    URL url= new URL("http://192.168.56.1/AthenaUsb/App_Athena/validar.php?correo="+cor+"&pass="+pas);
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Length",""+Integer.toString(parametros.getBytes().length));
                    connection.setDoOutput(true);
                    DataOutputStream wr= new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(parametros);
                    wr.close();
                    Scanner insStream=new Scanner(connection.getInputStream());

                    while (insStream.hasNextLine())
                        respuesta+=(insStream.nextLine());


                }catch (Exception e){
                    respuesta="NO RESPONDE";
                }

                return respuesta.toString();
            }
            // Funcion conteo registros encontrados
            public int objJson(String respuesta){

                int res=0;
                try {
                    JSONArray json = new JSONArray(respuesta);


                    if (json.length()==0){

                        res=0;
                    }
                    else if (json.length()>=1){
                        res=1;
                    }

                }catch (Exception e){}

                return res;

            }
}
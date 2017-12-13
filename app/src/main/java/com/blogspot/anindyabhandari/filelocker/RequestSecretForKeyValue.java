package com.blogspot.anindyabhandari.filelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class RequestSecretForKeyValue extends AppCompatActivity {
    final int intentcode = 1441;
    String networkName="DoesNotExist";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_secret_for_key_value);
    }
    public void onSubmit(View view)
    {
        EditText editText = (EditText) findViewById(R.id.editText10);
        networkName = editText.getText().toString();
        Intent intent = new Intent(this,CredRetrieval.class);
        intent.putExtra("NetworkName",networkName);
        startActivityForResult(intent,intentcode);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == intentcode) {
            if(resultCode == RESULT_OK){
                int status = data.getIntExtra("Status",2);
                if(status==0)
                {
                    Intent intent = new Intent(this, ShowSecretForKey.class);
                    intent.putExtra("NetworkName",networkName);
                    intent.putExtra("Password",data.getStringExtra("Password"));
                    startActivity(intent);
                }
                else if(status==1)
                {
                    showMessage("Secret does not exist for this key.");
                }
                else
                {
                    showMessage("An error occurred.");
                }
                finish();
            }
            if (resultCode == RESULT_CANCELED) {
                showMessage("User cancelled.");
                finish();
            }
        }
    }
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}
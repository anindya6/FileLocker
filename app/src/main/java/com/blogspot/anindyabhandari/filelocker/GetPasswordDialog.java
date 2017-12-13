package com.blogspot.anindyabhandari.filelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class GetPasswordDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_password_dialog);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if(title==null)
            title="Enter password: ";
        TextView textView = (TextView) findViewById(R.id.textView18);
        textView.setText(title);
    }
    public void onCheckedChanged(View view) {
        int start,end;
        boolean isChecked = ((CheckBox) view).isChecked();
        final EditText passWordEditText = (EditText) findViewById(R.id.editText5);
        if(!isChecked){
            start=passWordEditText.getSelectionStart();
            end=passWordEditText.getSelectionEnd();
            passWordEditText.setTransformationMethod(new PasswordTransformationMethod());;
            passWordEditText.setSelection(start,end);
        }
        else{
            start=passWordEditText.getSelectionStart();
            end=passWordEditText.getSelectionEnd();
            passWordEditText.setTransformationMethod(null);
            passWordEditText.setSelection(start,end);
        }
    }
    public void onSubmit(View view) {
        EditText editText = (EditText) findViewById(R.id.editText5);
        String password = editText.getText().toString();
        Intent _result = new Intent();
        if (password.length() != 0) {
            _result.putExtra("password",password);
            _result.putExtra("password_empty",0);
            setResult(RESULT_OK, _result);
            finish();
        } else {
            _result.putExtra("password_empty",1);
            setResult(RESULT_OK, _result);
            finish();
        }
    }
    public void onCancel(View view){
        finish();
    }
}

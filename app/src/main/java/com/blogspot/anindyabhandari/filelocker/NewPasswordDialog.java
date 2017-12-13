package com.blogspot.anindyabhandari.filelocker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class NewPasswordDialog extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_password_dialog);
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if(title==null)
            title="Enter password: ";
        TextView textView = (TextView) findViewById(R.id.textView16);
        textView.setText(title);
        TextView textView2 = (TextView) findViewById(R.id.textView17);
        textView2.setText("Re-enter Password: ");
    }
    public void onCheckedChanged(View view) {
        int start,end,start2,end2;
        boolean isChecked = ((CheckBox) view).isChecked();
        final EditText passWordEditText = (EditText) findViewById(R.id.editText3);
        final EditText passWordEditText2 = (EditText) findViewById(R.id.editText4);
        if(!isChecked){
            start=passWordEditText.getSelectionStart();
            end=passWordEditText.getSelectionEnd();
            passWordEditText.setTransformationMethod(new PasswordTransformationMethod());
            passWordEditText.setSelection(start,end);
            start2=passWordEditText2.getSelectionStart();
            end2=passWordEditText2.getSelectionEnd();
            passWordEditText2.setTransformationMethod(new PasswordTransformationMethod());
            passWordEditText2.setSelection(start2,end2);
        }
        else{
            start=passWordEditText.getSelectionStart();
            end=passWordEditText.getSelectionEnd();
            passWordEditText.setTransformationMethod(null);
            passWordEditText.setSelection(start,end);
            start2=passWordEditText2.getSelectionStart();
            end2=passWordEditText2.getSelectionEnd();
            passWordEditText2.setTransformationMethod(null);
            passWordEditText2.setSelection(start2,end2);
        }
    }
    public void onSubmit(View view) {
        EditText editText = (EditText) findViewById(R.id.editText3);
        String password = editText.getText().toString();
        EditText editText2 = (EditText) findViewById(R.id.editText4);
        String re_password = editText.getText().toString();
        Intent _result = new Intent();
        if (password.length() != 0 && password.equals(re_password)) {
            _result.putExtra("password",password);
            _result.putExtra("password_mismatch",0);
            setResult(RESULT_OK, _result);
            finish();
        } else {
            _result.putExtra("password_mismatch",1);
            setResult(RESULT_OK, _result);
            finish();
        }
    }
    public void onCancel(View view){
        finish();
    }
}

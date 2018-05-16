package com.example.powder.monica;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.net.SocketException;

public class FTPSettingActivity extends Activity {

    Button saveConfig, defaultConfig;
    EditText hostnameEdit, loginEdit, passwordEdit, directoryEdit;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Thread testThread;
    boolean connectionResult = false;

    public void init() {
        hostnameEdit = (EditText) findViewById(R.id.hostname_edit);
        loginEdit = (EditText) findViewById(R.id.login_edit);
        passwordEdit = (EditText) findViewById(R.id.password_edit);
        directoryEdit = (EditText) findViewById(R.id.directory_edit);
        saveConfig = (Button) findViewById(R.id.save_button);
        defaultConfig = (Button) findViewById(R.id.default_button);
        sharedPref = getSharedPreferences("defaultFTP.xml", 0);
        editor = sharedPref.edit();
    }

    public void setCustomValues() {
        hostnameEdit.setText(sharedPref.getString("Custom_hostname", getString(R.string.default_hostname)));
        loginEdit.setText(sharedPref.getString("Custom_login", getString(R.string.default_login)));
        passwordEdit.setText(sharedPref.getString("Custom_password", getString(R.string.default_password)));
        directoryEdit.setText(sharedPref.getString("Custom_directory", getString(R.string.default_directory)));
    }

    public void setDefaultValues() {
        hostnameEdit.setText(getString(R.string.default_hostname));
        loginEdit.setText(getString(R.string.default_login));
        passwordEdit.setText(getString(R.string.default_password));
        directoryEdit.setText(getString(R.string.default_directory));
    }

    public void connectionTest() {
        testThread = new Thread() {
            @Override
            public void run() {
                try {
                    FTPClient client = new FTPClient();
                    client.connect(hostnameEdit.getText().toString());
                    client.login(loginEdit.getText().toString(), passwordEdit.getText().toString());
                    connectionResult = true;
                } catch (SocketException e) {
                    e.printStackTrace();
                    connectionResult = false;
                } catch (IOException e) {
                    e.printStackTrace();
                    connectionResult = false;
                }
            }
        };
        testThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ftpsetting);

        init();
        setCustomValues();

        saveConfig.setOnClickListener(view -> {
            connectionTest();
            if (connectionResult) {
                editor.putString("Custom_hostname", hostnameEdit.getText().toString());
                editor.putString("Custom_login", loginEdit.getText().toString());
                editor.putString("Custom_password", passwordEdit.getText().toString());
                editor.putString("Custom_directory", directoryEdit.getText().toString());
                editor.commit();
                Toast.makeText(FTPSettingActivity.this, "FTP configured properly!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FTPSettingActivity.this, "Wrong hostname, login or password!", Toast.LENGTH_SHORT).show();
            }
            testThread.interrupt();
        });

        defaultConfig.setOnClickListener(view -> {
            setDefaultValues();
            Toast.makeText(FTPSettingActivity.this, "Restored default values!", Toast.LENGTH_SHORT).show();
        });
    }


}

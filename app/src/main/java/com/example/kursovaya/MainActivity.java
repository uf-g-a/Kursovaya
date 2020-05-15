package com.example.kursovaya;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private Button button;
    private String fileText;
    private final String FILENAME = "name";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fileText = openFile(FILENAME);
        if (fileText != "") {
            for (String part : fileText.split("_!!!!_")) {
                newLine(part);
            }
        }
    }

    public void newLine(String name){
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
        final View view = getLayoutInflater().inflate(R.layout.dynamic_element, null);
        button = (Button) view.findViewById(R.id.btn);
        button.setText(name);
        linearLayout.addView(view);
        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                button = view.findViewById(R.id.btn);
                String ff = openFile(FILENAME);
                if (ff != null) {
                    ff = ff.replaceAll(button.getText() + "_!!!!_", "");
                }
                saveRemove(FILENAME, ff);
                linearLayout.removeView(view);
                deleteFile(button.getText().toString());
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Список удален", Toast.LENGTH_SHORT);
                toast.show();

                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button = view.findViewById(R.id.btn);
                Intent intent = new Intent(MainActivity.this, PriceList.class);
                String f = button.getText().toString();
                intent.putExtra("nameList", f);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        setTitle("PriceList");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.create) {
            addList();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    private void addList() {
        final LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linear);
        final View view = getLayoutInflater().inflate(R.layout.dynamic_element, null);
        button = (Button) view.findViewById(R.id.btn);


        button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                button = view.findViewById(R.id.btn);
                String ff = openFile(FILENAME);
                if (ff != null) {
                    ff = ff.replaceAll(button.getText() + "_!!!!_", "");
                }
                saveRemove(FILENAME, ff);
                linearLayout.removeView(view);
                deleteFile(button.getText().toString());
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Список удален", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button = view.findViewById(R.id.btn);
                Intent intent = new Intent(MainActivity.this, PriceList.class);
                String f = button.getText().toString();
                intent.putExtra("nameList", f);
                startActivity(intent);
                finish();
            }
        });

        LayoutInflater li = LayoutInflater.from(this);
        final View dialog_view = li.inflate(R.layout.dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(dialog_view);
        final EditText nameInput = (EditText) dialog_view.findViewById(R.id.edit);
        alertDialogBuilder.setTitle("Введите название Прайс листа")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if(nameInput.getText().toString().length() == 0){
                            button.setText("Без названия");
                        }else {
                            button.setText(nameInput.getText());
                        }

                        String ff;
                        ff = openFile(FILENAME);
                        fileText = ff + button.getText().toString();
                        saveFile(FILENAME, fileText);

                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                                linearLayout.removeView(view);
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        linearLayout.addView(view);
    }

    private void saveRemove(String fileName, String line){
        try {
            OutputStream outputStream = openFileOutput(fileName, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);

            osw.write(line);
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }

    private void saveFile(String fileName, String line) {
        try {
            OutputStream outputStream = openFileOutput(fileName, 0);
            OutputStreamWriter osw = new OutputStreamWriter(outputStream);

            osw.write( line + "_!!!!_");
            osw.close();
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
    }




    private String openFile(String fileName) {
        try {
            InputStream inputStream = openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader isr = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }

                inputStream.close();
                return builder.toString();
            }
        } catch (Throwable t) {
            Toast.makeText(getApplicationContext(),
                    "Exception: " + t.toString(), Toast.LENGTH_LONG).show();
        }
        return null;
    }
}

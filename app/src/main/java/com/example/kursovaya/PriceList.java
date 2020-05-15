package com.example.kursovaya;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class PriceList extends AppCompatActivity {

    private Button enterBtn;
    private EditText name;
    private EditText number;
    private EditText price;
    private TextView sumText;
    private float sum;
    private String fileText;

    TextView nameDelete;
    TextView priceDelete;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_list);
        enterBtn = (Button) findViewById(R.id.enterBtn);
        name = (EditText) findViewById(R.id.name);
        number = (EditText) findViewById(R.id.number);
        price = (EditText) findViewById(R.id.price);
        sumText = (TextView) findViewById(R.id.sumText);
        Bundle arguments = getIntent().getExtras();
        String nameList = "";
        if (arguments != null) {
            nameList = arguments.getString("nameList");
        }
        final String finalNameList = nameList;
        File file = getFileStreamPath(nameList);
        setTitle(nameList);
        if(file.exists()) {

            fileText = openFile(nameList);
            if (fileText != "") {
                newLine(fileText, nameList);
            }
            // sumText.setText(fileText); // не забудь
        }
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((name.getText().toString().equals("")) || (number.getText().toString().equals("")) || (price.getText().toString().equals(""))) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(PriceList.this);
                    adb.setTitle("Ошибка ввода")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            })
                            .setMessage("Не заполнены все поля. Введите оставшиеся значения");

                    AlertDialog alertDialog = adb.create();
                    alertDialog.show();
                } else {
                    String info;
                    final LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
                    final View view = getLayoutInflater().inflate(R.layout.price_dynamic_element, null);
                    TextView nameText = (TextView) view.findViewById(R.id.nameText);
                    final TextView numberText = (TextView) view.findViewById(R.id.numberText);
                    final TextView priceText = (TextView) view.findViewById(R.id.priceText);
                    ImageButton deleteBnt = (ImageButton) view.findViewById(R.id.deleteBtn);
                    deleteBnt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String oldText;
                            oldText = openFile(finalNameList);
                            nameDelete = view.findViewById(R.id.nameText);
                            priceDelete = view.findViewById(R.id.priceText);
                            final String deleteLine;
                            deleteLine = "_name:_" + oldText.substring(oldText.indexOf(nameDelete.getText().toString()),
                                    oldText.indexOf(priceDelete.getText().toString()) + priceDelete.getText().toString().length());
                            layout.removeView(view);
                            oldText = oldText.replace(deleteLine, "");
                            saveLine(finalNameList, oldText);
                            sum = sum - Float.parseFloat(priceText.getText().toString()) * Float.parseFloat(numberText.getText().toString());
                            sumText.setText(String.valueOf(sum));
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Позиция удалена", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    });
                    nameText.setText(name.getText().toString());
                    priceText.setText(price.getText().toString());
                    numberText.setText(number.getText().toString());
                    sum = sum + Float.parseFloat(price.getText().toString()) * Float.parseFloat(number.getText().toString());
                    layout.addView(view);
                    sumText.setText(String.valueOf(sum));

                    String file = "";
                    File fFile = getFileStreamPath(finalNameList);
                    if (fFile.exists()) {
                        file = openFile(finalNameList);
                    }
                    info = "_name:_" + name.getText().toString() + "_number:_" + number.getText().toString() + "_price:_" + price.getText().toString();
                    info = file + info;
                    saveLine(finalNameList, info);


                    name.setText(null);
                    price.setText(null);
                    number.setText(null);
                }
            }
        });
    }

    public void newLine(String text, final String fileName){
        while (!text.equals("")) {
            sumText = (TextView) findViewById(R.id.sumText);
            int indexName = text.indexOf("_name:_");
            int indexNumber = text.indexOf("_number:_");
            int indexPrice = text.indexOf("_price:_");

            String Name = text.substring(indexName + 7, indexNumber);
            String Number = text.substring(indexNumber + 9, indexPrice);

            text = text.replaceFirst("_name:_", "");

            indexName = text.indexOf("_name:_");
            String Price;

            if (indexName == -1) {
                Price = text.substring(indexPrice + 1);
                text = "";
            } else {
                Price = text.substring(indexPrice + 1, indexName);
                text = text.substring(indexName);
            }


            final LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
            final View view = getLayoutInflater().inflate(R.layout.price_dynamic_element, null);
            TextView nameText = (TextView) view.findViewById(R.id.nameText);
            final TextView numberText = (TextView) view.findViewById(R.id.numberText);
            final TextView priceText = (TextView) view.findViewById(R.id.priceText);

            final String finalText = text;

            final ImageButton deleteBnt = (ImageButton) view.findViewById(R.id.deleteBtn);
            deleteBnt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String oldText;
                    oldText = openFile(fileName);
                    nameDelete = view.findViewById(R.id.nameText);
                    priceDelete = view.findViewById(R.id.priceText);
                    final String deleteLine;
                    deleteLine = "_name:_" + oldText.substring(oldText.indexOf(nameDelete.getText().toString()),
                            oldText.indexOf(priceDelete.getText().toString()) + priceDelete.getText().toString().length());
                    layout.removeView(view);
                    oldText = oldText.replace(deleteLine, "");
                    saveLine(fileName, oldText);
                    sum = sum - Float.parseFloat(priceText.getText().toString()) * Float.parseFloat(numberText.getText().toString());
                    sumText.setText(String.valueOf(sum));
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Позиция удалена", Toast.LENGTH_SHORT);
                    toast.show();
                }
            });


            sum = sum + (Float.parseFloat(Price) * Float.parseFloat(Number));
            sumText.setText(String.valueOf(sum));

            nameText.setText(Name);
            priceText.setText(Price);
            numberText.setText(Number);
            layout.addView(view);
        }
    }

    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(getApplicationContext(),
                "Список сохранен", Toast.LENGTH_SHORT);
        toast.show();
        super.onBackPressed();
    }

    private void saveLine(String fileName, String line){
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

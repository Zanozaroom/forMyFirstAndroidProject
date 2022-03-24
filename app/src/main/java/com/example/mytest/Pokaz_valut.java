package com.example.mytest;


import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.view.View;
import android.widget.Button;// для работы с кнопками - Button xml - из Activity
import android.widget.TextView; // для работы с TextView xml блоком из Activity
import android.widget.Toast;// для вывода короткого сообщения внизу экрана при сохранении файла


import java.io.BufferedReader;// для буферизированного чтения данных со страницы (чтобы использовать символьный поток)
import java.io.BufferedWriter;// для буферной записи данных в файл (чтобы использовать символьный поток)
import java.io.FileWriter;// для открытия потока на запись
import java.io.IOException;// для обработки ошибок Exception
import java.io.InputStreamReader;// для открытия потока на чтение
import java.net.HttpURLConnection; // подключение к сайту по протоколу Http
import java.net.URL; // работа с сетью


public class Pokaz_valut extends AppCompatActivity {
    private String url_name = "https://www.cbr-xml-daily.ru/daily_json.js"; //переменная хранит URL адрес страницы
    private String stroka = " "; //переменная будет хранить значения, полученные из сети
    static String strokaVar="Нет данных пока"; //переменная для сохранения полученных данных при повороте экрана
    final static String strokaVariableKey = "NAME_STROKA";//ключ переменной strokaVar
    TextView Text_downloadind;// переменная вывода курса валют
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pokaz_valut);

        Text_downloadind = findViewById(R.id.content); //создаю переменную для изменения текста
        Button DownLoad = findViewById(R.id.button);//создаю переменную для работы с кнопкой загрузки
        Button SaveText = findViewById(R.id.button2);//создаю переменную для работы с кнопкой сохранения
        //Блок загружает курсы валют при первом запуске экрана
        {
            new Thread(new Runnable() {
                public void run() {
                String stroka = getContent(url_name);
                Text_downloadind.post(new Runnable() {
                public void run() {
                Text_downloadind.setText(stroka);strokaVar=stroka;
                        }
                    });
                }
            }).start();
        }
        //начало работы кнопки для загрузки валюты
        DownLoad.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Text_downloadind.setText("Загрузка...");
                new Thread(new Runnable() {
                    public void run() {

                        String stroka = getContent(url_name);
                        strokaVar=stroka;
                        Text_downloadind.post(new Runnable() {
                            public void run() {
                                Text_downloadind.setText(stroka);
                            }
                        });
                    }

                }).start();
            }
        });
        //начало работы кнопки на сохранение загруженного списка валют
        SaveText.setOnClickListener(new View.OnClickListener(){

                public void onClick (View v){

                new Thread(new Runnable() {
                    public void run() {
                        Text_downloadind.post(new Runnable() {
                            public void run() {
                                saveText(getContent(stroka));
                            }
                        });
                    }

                }).start();

                SaveText.setText("...сохранить ещё раз?");


            }
        });


    }

//метод для сохранения текста при повороте экрана
    protected void onSaveInstanceState(Bundle onSaved) {
        onSaved.putString(strokaVariableKey,strokaVar);
        super.onSaveInstanceState(onSaved);
    }
    //метод для вывода сохраненного текста после поворота экрана
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        strokaVar = savedInstanceState.getString(strokaVariableKey);
        Text_downloadind.setText(strokaVar);
    }

//метод для получения списка валют с сайта
    private String getContent(String url_name)  {
//блок try для обработки исключений, связанных с соединением URL
//при завершении блока try необходимо принудительно закрыть соединение
try{
            URL my_url = new URL(url_name);

            HttpURLConnection my_connection = (HttpURLConnection) my_url.openConnection(); //открываю соединение http
            my_connection.setRequestMethod("GET");
            my_connection.setReadTimeout(10000);
            my_connection.connect();

// создаю поток на чтение информации и
//создаю объект BufferReader для считывания информации
//поток обрабатывается во вложенном блоке try с ресурсами,
//что дает возможность не закрывать принудительно поток
          try( BufferedReader in = new BufferedReader
                    (new InputStreamReader(my_connection.getInputStream(), "UTF-8"));) {

              String stroka = ""; //
              char c;
              StringBuffer reader = new StringBuffer();

              while ((stroka = in.readLine()) != null) {

                  if (stroka.contains("CharCode")) {
                      stroka = stroka.trim().replace('"', ' ').replace(',', ' ').replace("CharCode :  ", "").trim();
                      reader.append(stroka).append(" ");
                  }
                  if (stroka.contains("Name")) {
                      stroka = stroka.trim().replace('"', ' ').replace(',', ' ').replace("Name :  ", "").trim();
                      reader.append(stroka).append("\n");
                  }
                  if (stroka.contains("Value")) {
                      stroka = stroka.trim().replace('"', ' ').replace(',', ' ').replace("Value : ", "").trim();
                      reader.append("Курс ").append(stroka).append("\n").append("\n");
                  }

              }
              return reader.toString();
          } catch (IOException e) {

              return new StringBuffer().append("Ошибка во время считывания данных ").append(e.getMessage()).toString();
                  }

          }catch(IOException e){
    return new StringBuffer().append("Ошибка во время установки соединения с URL ").append(e.getMessage()).toString();

}

    }

//метод для сохранения полученного списка в файл на телефоне
    public void saveText(String s){

        //создаю переменную BufferedWriter для построчной записи данных в файл
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("/data/data/com.example.mytest/files/content1.txt"))){

            StringBuffer text = new StringBuffer();
            text.append(s);
            bw.write(text.toString());
            Toast.makeText(this, "Файл сохранен", Toast.LENGTH_SHORT).show();

        }
        catch(IOException ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

}
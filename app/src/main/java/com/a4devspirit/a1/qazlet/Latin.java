package com.a4devspirit.a1.qazlet;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;

public class Latin extends AppCompatActivity implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener{
    EditText cyril;
    DatabaseReference firebase = FirebaseDatabase.getInstance().getReference();
    TextView latin,saved;
    TabHost.TabSpec tabSpec;
    TabHost tabHost;
    ListView list_alphabet, list_saved, list_literature;
    String[] letters = {"A a","Á á","B b","D d","E e","F f","G g","Ǵ ǵ","H h","I i","I ı","J j","K k","L l","M m","N n","Ń ń","O o","Ó ó","P p","Q q","R r","S s","Sh sh","Ch ch","T t","U u","Ú ú","V v","Y y","Ý ý","Z z"};
    ArrayList<String> array_list_saved, array_list_literature;
    ArrayAdapter<String> adapter_saved, adapter_alphabet, adapter_literature;
    MediaPlayer mediaPlayer;
    DatabaseHelper mDatabaseHelper;
    Button button_save, button_copy, button_share;
    int itemID;
    String item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_latin);
        list_alphabet = (ListView) findViewById(R.id.list_alphabet);
        list_literature = (ListView)findViewById(R.id.list_literature);
        array_list_literature = new ArrayList<>();
        adapter_literature = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,array_list_literature);
        list_literature.setAdapter(adapter_literature);
        saved = (TextView)findViewById(R.id.text_saved);
        mDatabaseHelper = new DatabaseHelper(this);
        list_saved = (ListView)findViewById(R.id.list_saved);
        adapter_alphabet = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, letters);
        array_list_saved = new ArrayList<>();
        adapter_saved = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, array_list_saved);
        button_copy = (Button)findViewById(R.id.button_copy);
        button_share = (Button)findViewById(R.id.button_share);
        final Cursor data = mDatabaseHelper.getData();
        while (data.moveToNext()){
            array_list_saved.add(data.getString(1));
        }
        list_saved.setAdapter(adapter_saved);
        list_alphabet.setAdapter(adapter_alphabet);
        if (adapter_saved.getCount() == 0){
            saved.setText("Пока нет сохранённых");
        }
        latin = (TextView)  findViewById(R.id.text_latin);
        cyril = (EditText)  findViewById(R.id.text_cyrillic);
        tabHost = (TabHost) findViewById(R.id.tabhost);
        button_save = (Button)findViewById(R.id.button_save);
        firebase.child("literature").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                array_list_literature.add(dataSnapshot.getKey());
                adapter_literature.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        list_literature.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(Latin.this, Literature.class);
                intent.putExtra("data", adapterView.getItemAtPosition(i).toString());
                startActivity(intent);
            }
        });
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEntry = cyril.getText().toString();
                if (cyril.length() != 0){
                    AddData(newEntry);
                    array_list_saved.add(newEntry);
                    saved.setText("Сохранённые");
                }else{
                    Toast.makeText(Latin.this, "Введите текст, чтобы сохранить", Toast.LENGTH_SHORT).show();
                }
            }
        });
        button_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied text", latin.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Latin.this, "Успешно скопировано", Toast.LENGTH_SHORT).show();
            }
        });
        button_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, cyril.getText().toString());
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, latin.getText().toString());
                startActivity(sharingIntent);
            }
        });
        list_saved.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                cyril.setText(item);
            }
        });
        list_saved.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view,final  int i, long l) {
                item = adapterView.getItemAtPosition(i).toString();
                Cursor data = mDatabaseHelper.getItemID(item);
                itemID = -1;
                while(data.moveToNext()){
                    itemID = data.getInt(0);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(Latin.this);
                builder.setTitle("Удалить")
                        .setMessage("Вы уверены?")
                        .setCancelable(true)
                        .setPositiveButton("Отмена",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                        .setNegativeButton("Да",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        mDatabaseHelper.deleteItem(itemID,item);
                                        array_list_saved.remove(i);
                                        adapter_saved.notifyDataSetChanged();
                                        Toast.makeText(Latin.this, "Удалено", Toast.LENGTH_SHORT).show();
                                        if (adapter_saved.getCount() == 0){
                                            saved.setText("Пока нет сохранённых");
                                        }
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            }
        });
        cyril.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String b = cyril.getText().toString();
                if (cyril.length()==0){
                    saved.setVisibility(View.VISIBLE);
                    list_saved.setVisibility(View.VISIBLE);
                }else {
                    saved.setVisibility(View.GONE);
                    list_saved.setVisibility(View.GONE);
                }
                String c = b.replace("а", "a").replace("ә", "á").replace("б", "b")
                        .replace("д", "d").replace("е", "e").replace("ф", "f").replace("г", "g")
                        .replace("ғ", "ǵ").replaceAll("[һх]", "h").replace("і", "i").replaceAll("[ий]", "ı")
                        .replace("ж", "j").replace("к", "k").replace("л", "l").replace("м", "m")
                        .replace("н", "n").replace("ң", "ń").replace("о", "o").replace("ө", "ó")
                        .replace("п", "p").replace("қ", "q").replace("р", "r")
                        .replace("с", "s").replaceAll("[шщ]", "sh").replace("ч", "ch").replace("т", "t")
                        .replace("ұ", "u").replace("ү", "ú").replace("в", "v").replace("ы", "y")
                        .replace("у", "ý").replace("з", "z").replaceAll("[ъь]", "").replaceAll("[ЪЬ]", "")
                        .replace("А", "A").replace("Ә", "Á").replace("Б", "B")
                        .replace("Д", "D").replace("Е", "E").replace("Ф", "F").replace("Г", "G")
                        .replace("Ғ", "Ǵ").replaceAll("[ХҺ]", "H").replace("І", "I").replaceAll("[ИЙ]", "I")
                        .replace("Ж", "J").replace("К", "K").replace("Л", "L").replace("М", "M")
                        .replace("Н", "N").replace("Ң", "Ń").replace("О", "O").replace("Ө", "Ó")
                        .replace("П", "P").replace("Қ", "Q").replace("Р", "R")
                        .replace("С", "S").replaceAll("[ШЩ]", "Sh").replace("Ч", "Ch").replace("Т", "T")
                        .replace("Ұ", "U").replace("Ү", "Ú").replace("В", "V").replace("Ы", "Y")
                        .replace("У", "Ý").replace("З", "Z").replace("э","e").replace("Э","E")
                        .replace("ю", "ıý").replace("Ю", "Iý").replace("я", "ıa").replace("Я", "Ia").replace("ц","ts").replace("Ц","Ts");
                latin.setText(c);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        latin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copied text", latin.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(Latin.this, "Успешно скопировано", Toast.LENGTH_SHORT).show();
            }
        });
        tabHost.setup();
        tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setIndicator("Конвертер");
        tabSpec.setContent(R.id.tab1);
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setIndicator("Алфавит");
        tabSpec.setContent(R.id.tab2);
        tabHost.addTab(tabSpec);
        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setIndicator("Литература");
        tabSpec.setContent(R.id.tab3);
        tabHost.addTab(tabSpec);
        tabHost.setCurrentTabByTag("tag1");
        list_alphabet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                letterSound(position, "A a",   R.raw.le1);
                letterSound(position, "Á á",   R.raw.le2);
                letterSound(position, "B b",   R.raw.le3);
                letterSound(position, "D d",   R.raw.le4);
                letterSound(position, "E e",   R.raw.le5);
                letterSound(position, "F f",   R.raw.le6);
                letterSound(position, "G g",   R.raw.le7);
                letterSound(position, "Ǵ ǵ",   R.raw.le8);
                letterSound(position, "H h",   R.raw.le9);
                letterSound(position, "I i",   R.raw.le10);
                letterSound(position, "I ı",   R.raw.le11);
                letterSound(position, "J j",   R.raw.le12);
                letterSound(position, "K k",   R.raw.le13);
                letterSound(position, "L l",   R.raw.le14);
                letterSound(position, "M m",   R.raw.le15);
                letterSound(position, "N n",   R.raw.le16);
                letterSound(position, "Ń ń",   R.raw.le17);
                letterSound(position, "O o",   R.raw.le18);
                letterSound(position, "Ó ó",   R.raw.le19);
                letterSound(position, "P p",   R.raw.le20);
                letterSound(position, "Q q",   R.raw.le21);
                letterSound(position, "R r",   R.raw.le22);
                letterSound(position, "S s",   R.raw.le23);
                letterSound(position, "Sh sh", R.raw.le24);
                letterSound(position, "Ch ch", R.raw.le25);
                letterSound(position, "T t",   R.raw.le26);
                letterSound(position, "U u",   R.raw.le27);
                letterSound(position, "Ú ú",   R.raw.le28);
                letterSound(position, "V v",   R.raw.le29);
                letterSound(position, "Y y",   R.raw.le30);
                letterSound(position, "Ý ý",   R.raw.le31);
                letterSound(position, "Z z",   R.raw.le32);
            }
        });
        list_alphabet.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DialogExample(i, "A a",   R.raw.alma,R.drawable.apple, "Alma", "Яблоко");
                DialogExample(i, "Á á",   R.raw.aje,R.drawable.grandmother,"Áje", "Бабушка");
                DialogExample(i, "B b",   R.raw.balyk, R.drawable.fish, "Balyq", "Рыба");
                DialogExample(i, "D d",   R.raw.dop,R.drawable.ball, "Dop","Мяч");
                DialogExample(i, "E e",   R.raw.esik,R.drawable.door, "Esіk", "Дверь");
                DialogExample(i, "F f",   R.raw.foto,R.drawable.photo, "Foto", "Фото");
                DialogExample(i, "G g",   R.raw.gas,R.drawable.gas, "Gas", "Газ");
                DialogExample(i, "Ǵ ǵ",   R.raw.garyshker,R.drawable.cosmonaut, "Ǵaryshker", "Космонавт");
                DialogExample(i, "H h",   R.raw.hayuanat,R.drawable.leo, "Haıýanat", "Животные");
                DialogExample(i, "I i",   R.raw.iz, R.drawable.track, "Iz", "След");
                DialogExample(i, "I ı",   R.raw.it, R.drawable.dog, "It", "Собака");
                DialogExample(i, "J j",   R.raw.zhanbyr, R.drawable.flood, "Jan'byr", "Дождь");
                DialogExample(i, "K k",   R.raw.koz, R.drawable.eye, "Kóz", "Глаза");
                DialogExample(i, "L l",   R.raw.lak, R.drawable.sheep, "Laq", "Козленок");
                DialogExample(i, "M m",   R.raw.mysyk, R.drawable.cat, "Mysyq", "Кошка");
                DialogExample(i, "N n",   R.raw.nan, R.drawable.bread, "Nan", "Хлеб");
                DialogExample(i, "Ń ń",   R.raw.anshy, R.drawable.hunter, "Ańshy", "Охотник");
                DialogExample(i, "O o",   R.raw.ot, R.drawable.fire, "Ot", "Огонь");
                DialogExample(i, "Ó ó",   R.raw.osimdik, R.drawable.plant, "Ósіmdіk", "Растение");
                DialogExample(i, "P p",   R.raw.pil, R.drawable.elephant, "Pil", "Слон");
                DialogExample(i, "Q q",   R.raw.qasyk, R.drawable.spoon, "Qasyq", "Ложка");
                DialogExample(i, "R r",   R.raw.raushan, R.drawable.rose, "Raýshan", "Роза");
                DialogExample(i, "S s",   R.raw.sabiz, R.drawable.carrot, "Sábіz", "Морковь");
                DialogExample(i, "Sh sh", R.raw.sham, R.drawable.candle, "Sham", "Свеча");
                DialogExample(i, "Ch ch", R.raw.chemodan, R.drawable.suitcase, "Chemodan", "Чемодан");
                DialogExample(i, "T t",   R.raw.tyshkan, R.drawable.mouse, "Tyshqan", "Мышь");
                DialogExample(i, "U u",   R.raw.ushak, R.drawable.airplane,"Ushaq", "Самолет");
                DialogExample(i, "Ú ú",   R.raw.uyrek, R.drawable.duck, "Úırek", "Утка");
                DialogExample(i, "V v",   R.raw.velosiped, R.drawable.bicycle, "Velosıped", "Велосипед");
                DialogExample(i, "Y y",   R.raw.ydys, R.drawable.dishes, "Ydys", "Посуда");
                DialogExample(i, "Ý ý",   R.raw.u, R.drawable.flask, "Ý", "Яд");
                DialogExample(i, "Z z",   R.raw.zymyran, R.drawable.rocket, "Zymyran", "Ракета");
                return false;
            }
        });
    }
    public void AddData(String newEntry){
        boolean insertData = mDatabaseHelper.addData(newEntry);

        if (insertData) {
            Toast.makeText(this, "Успешно сохранено", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Произошла ошибка", Toast.LENGTH_SHORT).show();
        }
    }
    private void releaseMP() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
                mediaPlayer = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMP();
    }
    public void DialogExample(int position, String post,final int example, int image, String string, String stringtranslate){
        if (list_alphabet.getItemAtPosition(position).toString().equals(post)){
            View view2 = (LayoutInflater.from(Latin.this)).inflate(R.layout.dialogforletters, null);
            AlertDialog.Builder alertbuilder = new AlertDialog.Builder(Latin.this);
            alertbuilder.setView(view2);
            final ImageView letterimage = (ImageView)view2.findViewById(R.id.letterimage);
            final TextView original = (TextView)view2.findViewById(R.id.exampleoriginal);
            final TextView translate = (TextView)view2.findViewById(R.id.exampletranslate);
            alertbuilder.setCancelable(true);
            final Button transcriptionbutton = (Button)view2.findViewById(R.id.btntranscription);
            transcriptionbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    releaseMP();
                    mediaPlayer = MediaPlayer.create(Latin.this, example);
                    mediaPlayer.start();
                    if (mediaPlayer == null)
                        return;
                    mediaPlayer.setOnCompletionListener(Latin.this);
                }
            });
            letterimage.setImageDrawable(getResources().getDrawable(image));
            original.setText(string);
            translate.setText(stringtranslate);
            android.app.Dialog dialog = alertbuilder.create();
            dialog.show();
        }
    }
    public void letterSound(int position, String post, int example) {
        if (list_alphabet.getItemAtPosition(position).toString().equals(post)) {
            releaseMP();
            mediaPlayer = MediaPlayer.create(Latin.this, example);
            mediaPlayer.start();
            if (mediaPlayer == null)
                return;
            mediaPlayer.setOnCompletionListener(Latin.this);
        }
    }
    @Override
    public void onBackPressed() {
        if (!cyril.getText().toString().equals("")){
            cyril.setText("");
        }else{
            finishAffinity();
        }
    }
}

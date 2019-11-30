package com.example.my_documenst;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class PaintActivity extends AppCompatActivity implements View.OnClickListener {
    PaintView paintView;
    SeekBar brush_size;
    Spinner page_selector,shape_selector;
    TextView btn_undo,brush_size_selector;
    ImageButton color_selector;
    private int PICK_IMAGE=133;
    private ArrayAdapter<String> adapter;
    ArrayList<String> filelocations=new ArrayList<>();
    View lastclicked=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_NOACTIONBAR);
        setContentView(R.layout.activity_paint);
        paintView = findViewById(R.id.paintview);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        paintView.normal();
       // paintView.blur();
        //paintView.emboss();
        lastclicked=findViewById(R.id.btn_paint);
        paintView.setDrawingCacheEnabled(true);
        color_selector=findViewById(R.id.color_selector);
        color_selector.setBackgroundColor(paintView.getCurrentColor());
        brush_size_selector=findViewById(R.id.brush_size_selector);
        brush_size = findViewById(R.id.brush_size);
        brush_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                paintView.setStrokeWidth(seekBar.getProgress());
                brush_size_selector.setText(String.valueOf(seekBar.getProgress()));
                seekBar.setVisibility(View.GONE);
            }
        });
        brush_size.setProgress(PaintView.BRUSH_SIZE);
        brush_size.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    if(brush_size.getVisibility()==View.VISIBLE)brush_size.setVisibility(View.GONE);
                }

            }
        });
        shape_selector=findViewById(R.id.shape_selector);
        final List<Integer> list=new ArrayList<>();
        list.add(R.drawable.icon_round_filled);
        list.add(R.drawable.icon_embosh);
        list.add(R.drawable.icon_blur);
        ArrayAdapter<Integer> dataAdapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, list){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // this part is needed for hiding the original view
                View view = super.getView(position, convertView, parent);
                ((TextView)view).setCompoundDrawablesRelativeWithIntrinsicBounds(list.get(position),0,0,0);
                ((TextView)view).setText("");
                ((TextView)view).setGravity(Gravity.CENTER);
                //view.setVisibility(View.GONE);
                return view;
            }
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view=super.getDropDownView(position,convertView,parent);
                ((TextView)view).setText("");
                ((TextView)view).setGravity(Gravity.CENTER_HORIZONTAL);
                ((TextView)view).setCompoundDrawablesRelativeWithIntrinsicBounds(list.get(position),0,0,0);
                return view;
            }
        };
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        shape_selector.setAdapter(dataAdapter);
        shape_selector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        paintView.normal();
                        break;
                    case 1:
                        paintView.emboss();
                        break;
                    case 2:
                        paintView.blur();
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_undo:
                paintView.undo();
                break;
            case R.id.btn_redo:
                paintView.redo();
                break;
            case R.id.brush_size_selector: 
                if(brush_size.getVisibility()==View.GONE){
                    brush_size.setVisibility(View.VISIBLE);
                    brush_size.requestFocus();
                }
                else
                    brush_size.setVisibility(View.GONE);
                break;
            case R.id.btn_erase:
                lastclicked.setBackgroundColor(Color.WHITE);
                lastclicked=findViewById(R.id.btn_erase);
                lastclicked.setBackgroundColor(Color.RED);
                paintView.setDrawing(false);
                break;
            case R.id.btn_paint:
                lastclicked.setBackgroundColor(Color.WHITE);
                lastclicked=findViewById(R.id.btn_paint);
                lastclicked.setBackgroundColor(Color.RED);
                paintView.setDrawing(true);
                break;
            case R.id.color_selector:
                ColorPickerDialogBuilder
                        .with(PaintActivity.this)
                        .setTitle("Choose color")
                        .initialColor(Color.RED)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                // toast("onColorSelected: 0x" + Integer.toHexString(selectedColor));
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                paintView.setCurrentColor(selectedColor);
                                color_selector.setBackgroundColor(paintView.getCurrentColor());

                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
                break;
            case R.id.page_clear:
                lastclicked=findViewById(R.id.brush_size_selector);
                lastclicked.setBackgroundColor(Color.RED);
                AlertDialog.Builder builder=new AlertDialog.Builder(PaintActivity.this).setTitle("Warning")
                        .setMessage("Are sure want to clear the drawing?\n it cannot be undone!!!")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                paintView.clear();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                builder.show();
                break;
            case R.id.page_save:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
                builder1.setTitle("Enter file name");
                final EditText input = new EditText(this);
                input.setInputType(InputType.TYPE_CLASS_TEXT );
                builder1.setView(input);

                builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String filename = input.getText().toString();
                        boolean flag=false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                flag=true;
                            } else {
                                flag=false;
                                ActivityCompat.requestPermissions(PaintActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            }
                        }
                        else {
                            flag=true;
                        }
                        if(flag){
                            try {
                                Bitmap b = paintView.getDrawingCache();
                                File sdCard = Environment.getExternalStorageDirectory();
                                File folder=new File(sdCard.toString()+"/drawingapp");
                                folder.mkdir();
                                File file = new File(folder,filename+".jpeg");
                                FileOutputStream fos=null;
                                fos = new FileOutputStream(file);
                                b.setHasAlpha(true);
                                b.compress(Bitmap.CompressFormat.JPEG, 95, fos);
                                Toast.makeText(PaintActivity.this,"Image Saved",Toast.LENGTH_SHORT).show();
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                                Toast.makeText(PaintActivity.this,"Error saving image",Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
                builder1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder1.show();
                break;

        }
    }


}

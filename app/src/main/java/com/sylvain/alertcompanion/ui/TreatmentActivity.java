package com.sylvain.alertcompanion.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.data.DatabaseTreatment;
import com.sylvain.alertcompanion.data.Treatment;
import com.sylvain.alertcompanion.data.TreatmentDao;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TreatmentActivity extends AppCompatActivity {

    @BindView(R.id.activity_treatment_linearlayout_treatment_morning)
    LinearLayout linearLayoutMorning;
    @BindView(R.id.activity_treatment_linearlayout_treatment_midday)
    LinearLayout linearLayoutMidday;
    @BindView(R.id.activity_treatment_linearlayout_treatment_evening)
    LinearLayout linearLayoutEvening ;

    AlertDialog dialogAddTreatment;
    Spinner spinner ;
    EditText edittextDrugName;
    CheckBox checkBoxMorning;
    CheckBox checkBoxMidday ;
    CheckBox checkBoxEvening;
    EditText editTextDosageQuantity ;
    Button buttonBack ;
    Button buttonAdd ;
    List<Treatment> treatmentListMorning;
    List<Treatment> treatmentListMidday ;
    List<Treatment> treatmentListEvening;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment);
        ButterKnife.bind(this);
        configureAll();
    }

    private void configureAll(){
        configureToolbar();
        configureDialogAddTreatment();
        displayTreatment();
    }

    /*UI*/

    //Toolbar
    void configureToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        Objects.requireNonNull(ab).setDisplayHomeAsUpEnabled(true);}

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        menu.findItem(R.id.menu_toolbar_addtreatment).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_toolbar_addtreatment: displayDialogTreatment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void configureDialogAddTreatment(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate (R.layout.dialog_add_tratment, null);

         spinner = view.findViewById(R.id.activity_treatment_spinner_dosage_unit);
         edittextDrugName = view.findViewById(R.id.activity_treatment_edittext_namedrug);
         checkBoxMorning = view.findViewById(R.id.activity_treatment_checkbox_morning);
         checkBoxMidday = view.findViewById(R.id.activity_treatment_checkbox_midday);
         checkBoxEvening = view.findViewById(R.id.activity_treatment_checkbox_evening);
         editTextDosageQuantity = view.findViewById(R.id.activity_treatment_edittext_dosage_quantity);
         buttonBack = view.findViewById(R.id.activity_treatment_button_cancel);
         buttonAdd = view.findViewById(R.id.activity_treatment_button_add);

         buttonBack.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 dialogAddTreatment.cancel();
             }
         });

         buttonAdd.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(dialogTreatmentIsCorrect()){
                     addTreatmentDatabase();
                     displayTreatment();
                     dialogAddTreatment.cancel();
                 }else{
                     Toast.makeText(TreatmentActivity.this, "error", Toast.LENGTH_SHORT).show();
                 }
             }
         });

        builder.setView(view);
        dialogAddTreatment =   builder.create();
        Objects.requireNonNull(dialogAddTreatment.getWindow()).setDimAmount(0.8f);
    }

    private String getTextSpinner(){
        return spinner.getSelectedItem().toString();
    }

    private void addTreatmentDatabase(){
        DatabaseTreatment.getInstance(TreatmentActivity.this).treatmentDao().createTreatment(new Treatment
                (edittextDrugName.getText().toString(),
                        checkBoxMorning.isChecked(),
                        checkBoxMidday.isChecked(),
                        checkBoxEvening.isChecked(),
                        Integer.valueOf(editTextDosageQuantity.getText().toString()),
                        getTextSpinner()
                        ));
    }

    private void displayDialogTreatment(){

        dialogAddTreatment.show();

    }

    private boolean dialogTreatmentIsCorrect(){
        if(edittextDrugName.getText() == null || edittextDrugName.getText().length() == 0)
            return false;
        if(!checkBoxEvening.isChecked() && !checkBoxMidday.isChecked() && !checkBoxMorning.isChecked())
            return false;
        if(editTextDosageQuantity.getText() == null || editTextDosageQuantity.getText().length() == 0)
            return false;
        if(spinner.getSelectedItemPosition() == 0)
            return false;

        return true;

    }

    private void displayTreatment(){
        linearLayoutEvening.removeAllViews();
        linearLayoutMidday.removeAllViews();
        linearLayoutMorning.removeAllViews();
        treatmentListMorning = DatabaseTreatment.getInstance(TreatmentActivity.this).treatmentDao().getListTreatmentMorning();
        treatmentListMidday = DatabaseTreatment.getInstance(TreatmentActivity.this).treatmentDao().getListTreatmentMidday();
        treatmentListEvening = DatabaseTreatment.getInstance(TreatmentActivity.this).treatmentDao().getListTreatmentEvening();
        addViewTreatment(treatmentListMorning, linearLayoutMorning);
        addViewTreatment(treatmentListMidday, linearLayoutMidday);
        addViewTreatment(treatmentListEvening, linearLayoutEvening);
    }
    
    private void addViewTreatment(List<Treatment> lstTreatment, LinearLayout linearLayout){
        StringBuilder builder = new StringBuilder();

        LayoutInflater inflater = getLayoutInflater();

        for (Treatment treatment : lstTreatment){
            builder.setLength(0);
            View view = inflater.inflate(R.layout.item_contact_number_and_delete, null);
            TextView textView = view.findViewById(R.id.item_contact_number_and_delete_textview_name_and_number);
            builder.append(treatment.getName()).append(" , ").append(treatment.getDosageQuantity()).append(" ").append(treatment.getDosageUnit());
            textView.setText(builder.toString());
            ImageView imageView = view.findViewById(R.id.item_contact_number_and_delete_imageview_delete);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prepareDeleteTreatmentDatabase(v);
                }
            });
            linearLayout.addView(view);
        }
    }

    private void prepareDeleteTreatmentDatabase(View v){
        Treatment targetTreatment = null;
        String time ="";


        if (((View) v.getParent().getParent()).getId() == linearLayoutMorning.getId()  ){
          targetTreatment = treatmentListMorning.get(linearLayoutMorning.indexOfChild((View)v.getParent())) ;
          time = "morning";
        } else if (((View) v.getParent().getParent()).getId() == linearLayoutMidday.getId()){
            targetTreatment = treatmentListMidday.get(linearLayoutMidday.indexOfChild((View)v.getParent()));
            time = "midday";
        }else if(((View) v.getParent().getParent()).getId() == linearLayoutEvening.getId()){
            targetTreatment = treatmentListEvening.get(linearLayoutEvening.indexOfChild((View)v.getParent()));
            time = "evening";
        }

        assert targetTreatment != null;
        int morningInt = targetTreatment.isMorning() ? 1 : 0;
        int middayInt = targetTreatment.isMidday() ? 1 : 0;
        int eveningInt = targetTreatment.isEvening() ? 1 : 0;
        int count = morningInt + middayInt + eveningInt;

        if(count < 2 ){
            displayAlertDialogDeleteTreatment("delete?", "ok",  null, targetTreatment);
        }else{
            StringBuilder builder = new StringBuilder();
            builder.append("Delete just ")
                    .append(time)
                    .append(" or ");
            if(morningInt == 1)
                builder.append("morning ");
            if(middayInt == 1)
                builder.append("midday ");
            if(eveningInt == 1)
                builder.append("evening");
            builder.append("?");
            displayAlertDialogDeleteTreatment(builder.toString(), time ,"all delete", targetTreatment);
        }
    }

    private void displayAlertDialogDeleteTreatment(String title, String positivButton, String neutralButton, Treatment targetTreatment){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(title)
                    .setPositiveButton(positivButton, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (neutralButton == null){
                                DatabaseTreatment.getInstance(TreatmentActivity.this).treatmentDao().deleteTreatment(targetTreatment.getId());
                            }else{
                                updateTreatment(targetTreatment, positivButton);
                            }
                            displayTreatment();
                        }
                    }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            }).setNeutralButton(neutralButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseTreatment.getInstance(TreatmentActivity.this).treatmentDao().deleteTreatment(targetTreatment.getId());
                    displayTreatment();
                }
            }).create().show();
    }

    private void updateTreatment(Treatment treatment, String time){
        if (time.equals("morning")){
            treatment.setMorning(false);
        } else if(time.equals("midday")){
            treatment.setMidday(false);
        }else if (time.equals("evening")){
            treatment.setEvening(false);
        }
        DatabaseTreatment.getInstance(this).treatmentDao().updateTreatment(treatment);
    }

}

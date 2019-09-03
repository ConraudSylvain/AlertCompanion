package com.sylvain.alertcompanion.ui;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
        if (item.getItemId() == R.id.menu_toolbar_addtreatment) {
            displayDialogTreatment();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Display dialog add treatment
    private void displayDialogTreatment(){
        spinner.setSelection(0);
        edittextDrugName.setText("");
        checkBoxMorning.setChecked(false);
        checkBoxMidday.setChecked(false);
        checkBoxEvening.setChecked(false);
        editTextDosageQuantity.setText("");

        dialogAddTreatment.show();
    }

    //Display treatment
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

    //Add treatment display
    private void addViewTreatment(List<Treatment> lstTreatment, LinearLayout linearLayout){
        StringBuilder builder = new StringBuilder();

        LayoutInflater inflater = getLayoutInflater();

        for (Treatment treatment : lstTreatment){
            builder.setLength(0);
            @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.item_contact_number_and_delete, null);
            TextView textView = view.findViewById(R.id.item_contact_number_and_delete_textview_name_and_number);
            builder.append(treatment.getName()).append(" , ").append(treatment.getDosageQuantity()).append(" ").append(treatment.getDosageUnit());
            textView.setText(builder.toString());
            ImageView imageView = view.findViewById(R.id.item_contact_number_and_delete_imageview_delete);
            imageView.setOnClickListener(this::prepareDeleteTreatmentDatabase);
            linearLayout.addView(view);
        }
    }

    /*DATA*/
    //Save treatment in database
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

    //Update treatment database
    private void updateTreatment(Treatment treatment, String time){
        switch (time) {
            case "morning":
                treatment.setMorning(false);
                break;
            case "midday":
                treatment.setMidday(false);
                break;
            case "evening":
                treatment.setEvening(false);
                break;
        }
        DatabaseTreatment.getInstance(this).treatmentDao().updateTreatment(treatment);
    }


    /*UTILS*/
    private String getTextSpinner(){
        return spinner.getSelectedItem().toString();
    }

    //Check if coorect fields
    private boolean dialogTreatmentIsCorrect(){
        if(edittextDrugName.getText() == null || edittextDrugName.getText().length() == 0)
            return false;
        if(!checkBoxEvening.isChecked() && !checkBoxMidday.isChecked() && !checkBoxMorning.isChecked())
            return false;
        if(editTextDosageQuantity.getText() == null || editTextDosageQuantity.getText().length() == 0)
            return false;
        return spinner.getSelectedItemPosition() != 0;
    }

    //prepare for delete treatment
    private void prepareDeleteTreatmentDatabase(View v){
        Treatment targetTreatment = null;
        String time ="";


        if (((View) v.getParent().getParent()).getId() == linearLayoutMorning.getId()  ){
            targetTreatment = treatmentListMorning.get(linearLayoutMorning.indexOfChild((View)v.getParent())) ;
            time = getResources().getString(R.string.morning);
        } else if (((View) v.getParent().getParent()).getId() == linearLayoutMidday.getId()){
            targetTreatment = treatmentListMidday.get(linearLayoutMidday.indexOfChild((View)v.getParent()));
            time = getResources().getString(R.string.midday);
        }else if(((View) v.getParent().getParent()).getId() == linearLayoutEvening.getId()){
            targetTreatment = treatmentListEvening.get(linearLayoutEvening.indexOfChild((View)v.getParent()));
            time = getResources().getString(R.string.evening);
        }

        assert targetTreatment != null;
        int morningInt = targetTreatment.isMorning() ? 1 : 0;
        int middayInt = targetTreatment.isMidday() ? 1 : 0;
        int eveningInt = targetTreatment.isEvening() ? 1 : 0;
        int count = morningInt + middayInt + eveningInt;

        if(count < 2 ){
            displayAlertDialogDeleteTreatment(getResources().getString(R.string.delete) + " ?", "ok",  null, targetTreatment);
        }else{
            StringBuilder builder = new StringBuilder();
            builder.append(getResources().getString(R.string.delete_just))
                    .append(time)
                    .append(getResources().getString(R.string.or));
            if(morningInt == 1)
                builder.append(getResources().getString(R.string.morning));
            if(middayInt == 1)
                builder.append(getResources().getString(R.string.midday));
            if(eveningInt == 1)
                builder.append(getResources().getString(R.string.evening));
            builder.append("?");
            displayAlertDialogDeleteTreatment(builder.toString(), time ,getResources().getString(R.string.all_delete), targetTreatment);
        }
    }

    //configure dialog add Treatment
    private void configureDialogAddTreatment(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        @SuppressLint("InflateParams")
        View view = inflater.inflate (R.layout.dialog_add_tratment, null);

        spinner = view.findViewById(R.id.activity_treatment_spinner_dosage_unit);
        edittextDrugName = view.findViewById(R.id.activity_treatment_edittext_namedrug);
        checkBoxMorning = view.findViewById(R.id.activity_treatment_checkbox_morning);
        checkBoxMidday = view.findViewById(R.id.activity_treatment_checkbox_midday);
        checkBoxEvening = view.findViewById(R.id.activity_treatment_checkbox_evening);
        editTextDosageQuantity = view.findViewById(R.id.activity_treatment_edittext_dosage_quantity);
        buttonBack = view.findViewById(R.id.activity_treatment_button_cancel);
        buttonAdd = view.findViewById(R.id.activity_treatment_button_add);

        buttonBack.setOnClickListener(v -> dialogAddTreatment.cancel());

        buttonAdd.setOnClickListener(v -> {
            if(dialogTreatmentIsCorrect()){
                addTreatmentDatabase();
                displayTreatment();
                dialogAddTreatment.cancel();
            }else{
                Toast.makeText(TreatmentActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setView(view);
        dialogAddTreatment =   builder.create();
        Objects.requireNonNull(dialogAddTreatment.getWindow()).setDimAmount(0.8f);
    }


    private void displayAlertDialogDeleteTreatment(String title, String positivButton, String neutralButton, Treatment targetTreatment){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setPositiveButton(positivButton, (dialog, which) -> {
                    if (neutralButton == null){
                        DatabaseTreatment.getInstance(TreatmentActivity.this).treatmentDao().deleteTreatment(targetTreatment.getId());
                    }else{
                        updateTreatment(targetTreatment, positivButton);
                    }
                    displayTreatment();
                }).setNegativeButton(getResources().getString(R.string.cancel), (dialog, which) -> {
                }).setNeutralButton(neutralButton, (dialog, which) -> {
                    DatabaseTreatment.getInstance(TreatmentActivity.this).treatmentDao().deleteTreatment(targetTreatment.getId());
                    displayTreatment();
                }).create().show();
    }
}

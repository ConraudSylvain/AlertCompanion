package com.sylvain.alertcompanion.ui.fragmentTuto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.utils.Converter;
import com.sylvain.alertcompanion.utils.Keys;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ContactFragment extends Fragment {


    @BindView(R.id.fragment_contact_edittext_phone_number)
    EditText editTextPhoneNumber;
    @BindView(R.id.fragment_contact_linearlayout_display_contact)
    LinearLayout linearLayoutContactContnair;
    @OnClick(R.id.fragment_contact_button_add)
    void clickAddContact(){addContactCustom();}
    @OnClick(R.id.fragment_contact_button_phonebook)
    void clickPhoneBook(){openContact();}
    @BindView (R.id.fragment_contact_button_next)
    Button buttonNext;

    private List<String> lstContact = new ArrayList<>();

    public ContactFragment() {
        // Required empty public constructor
    }

    static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this, view);
        buttonNext.setOnClickListener(v -> nextPage());
        return view;
    }

    private void openContact(){
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, 1);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null)
            return;

        String name;
        Uri contactData = data.getData();
        Cursor cur = Objects.requireNonNull(getContext()).getContentResolver().query(Objects.requireNonNull(contactData), null, null, null, null);
        if (Objects.requireNonNull(cur).getCount() > 0) {
            if (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
                    while (Objects.requireNonNull(phones).moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        addContactList(phoneNumber, name);
                    }
                    phones.close();
                }
            }
        }
        cur.close();
    }

    private void addContactList(String phoneNumber, String name) {
        lstContact.add(name + "/" + phoneNumber);
        displayListContact();
    }

    private void displayListContact(){
        linearLayoutContactContnair.removeAllViews();
        LayoutInflater layoutInflater = getLayoutInflater();
        if(lstContact.size() != 0){
            for (String contact : lstContact){
                @SuppressLint("InflateParams") View view = layoutInflater.inflate(R.layout.item_contact_number_and_delete, null);
                TextView textView = view.findViewById(R.id.item_contact_number_and_delete_textview_name_and_number);
                String[] nameAndNumber = contact.split("/");
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(nameAndNumber[0]).append(" ").append(nameAndNumber[1]);
                textView.setText(stringBuilder);
                ImageView imageViewDelete = view.findViewById(R.id.item_contact_number_and_delete_imageview_delete);
                imageViewDelete.setOnClickListener(v -> {
                    View parent = (View) v.getParent();
                    deleteContactList(linearLayoutContactContnair.indexOfChild(parent));
                });
                linearLayoutContactContnair.addView(view);
            }
        }
        if(lstContact.size() > 0){
            buttonNext.setEnabled(true);
        }else{
            buttonNext.setEnabled(false);
        }
    }

    private void deleteContactList(int position){
            lstContact.remove(position);
            displayListContact();
    }

    private void addContactCustom(){
        if(editTextPhoneNumber.getText().length() > 0){
            addContactList(editTextPhoneNumber.getText().toString(),"no name");
        }
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                Objects.requireNonNull(getActivity().getCurrentFocus()).getWindowToken(), 0);
    }

    private void saveContact(){
        SharedPreferences preferences = Objects.requireNonNull(getActivity()).getSharedPreferences(Keys.KEY_MAIN_SAVE, Context.MODE_PRIVATE);
        preferences.edit().putString(Keys.KEY_LIST_CONTACT_SOS, Converter.convertListContactToString(lstContact)).apply();
        preferences.edit().putString(Keys.KEY_LIST_CONTACT_ALARM, Converter.convertListContactToString(lstContact)).apply();
    }

    private void nextPage(){
        saveContact();
        ViewPagerTutoAdapter.mViewPager.setCurrentItem(ViewPagerTutoAdapter.mViewPager.getCurrentItem() + 1);
    }
}

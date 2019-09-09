package com.sylvain.alertcompanion.ui.fragmentTuto;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.sylvain.alertcompanion.R;
import com.sylvain.alertcompanion.ui.MainActivity;
import com.sylvain.alertcompanion.utils.Keys;
import com.sylvain.alertcompanion.utils.Tuto;
import java.util.Objects;

public class EndTutoFragment extends Fragment {

    public EndTutoFragment() {
        // Required empty public constructor
    }

    static EndTutoFragment newInstance() {
        return  new EndTutoFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_end_tuto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button buttonFinish = view.findViewById(R.id.fragment_endtuto_button_finish);
        buttonFinish.setOnClickListener(v -> {
            save();
            Tuto.endTuto((MainActivity) Objects.requireNonNull(getActivity()));
        });
    }

    private void save(){
        SharedPreferences preferences =  Objects.requireNonNull(getContext()).getSharedPreferences(Keys.KEY_MAIN_SAVE, Context.MODE_PRIVATE);
        preferences.edit() .putBoolean(Keys.KEY_FIST_START, false).apply();
        preferences.edit().putInt(Keys.KEY_TIMER_ALARM, 30).apply();
        preferences.edit().putString(Keys.KEY_MESSAGE_CONTENT_ALARM, getResources().getString(R.string.text_alarm_not_disable)).apply();
        preferences.edit().putString(Keys.KEY_MESSAGE_CONTENT_SOS, getResources().getString(R.string.text_sos)).apply();
        preferences.edit().putBoolean(Keys.KEY_TYPE_ALARM_VOICE, true).apply();
        preferences.edit().putBoolean(Keys.KEY_VIBRATE, true).apply();
        preferences.edit().putBoolean(Keys.KEY_FLASH, true).apply();
        preferences.edit().putBoolean(Keys.KEY_POPUP_CONFIRM_SEND_SMS, true).apply();
        preferences.edit().putBoolean(Keys.KEY_NOTIFICATION_TREATMENT, true).apply();
        preferences.edit().putString(Keys.KEY_HOUR_NOTIFICATION_MORNING, "08:00").apply();
        preferences.edit().putString(Keys.KEY_HOUR_NOTIFICATION_MIDDAY, "12:00").apply();
        preferences.edit().putString(Keys.KEY_HOUR_NOTIFICATION_EVENING, "19:00").apply();

        for (Fragment fragment : Objects.requireNonNull(getActivity()).getSupportFragmentManager().getFragments()) {
           getActivity(). getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }
}

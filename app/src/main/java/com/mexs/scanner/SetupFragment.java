package com.mexs.scanner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SetupFragment extends Fragment implements View.OnClickListener{

    private Context mContext;
    private MainViewModel mViewModel;
    private View mLayout;

    private UserSettings curSettings = new UserSettings();

    public SetupFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get ViewModel
        mViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication())).get(MainViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_setup, container, false);

        // Set adapter for Test Type
        ArrayAdapter<String> adapterTest = new ArrayAdapter<>(mContext, R.layout.dropdown_menu_popup_item, mContext.getResources().getStringArray(R.array.tests));
        ((AutoCompleteTextView) mLayout.findViewById(R.id.field_test_type)).setAdapter(adapterTest);

        // Set adapter for Lab
        ArrayAdapter<String> adapterLab = new ArrayAdapter<>(mContext, R.layout.dropdown_menu_popup_item, mContext.getResources().getStringArray(R.array.labs));
        ((AutoCompleteTextView) mLayout.findViewById(R.id.field_lab)).setAdapter(adapterLab);

        // Set adapter for Printer
        ArrayAdapter<String> adapterPrinter = new ArrayAdapter<>(mContext, R.layout.dropdown_menu_popup_item, mContext.getResources().getStringArray(R.array.printer_names));
        ((AutoCompleteTextView) mLayout.findViewById(R.id.field_printer)).setAdapter(adapterPrinter);

        return mLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Re-populate form if required
        mViewModel.mSettings.observe(this, new Observer<UserSettings>() {
            @Override
            public void onChanged(UserSettings userSettings) {
                curSettings = userSettings;

                ((AutoCompleteTextView) mLayout.findViewById(R.id.field_test_type)).setText(curSettings.getTestType(), false);
                ((EditText) mLayout.findViewById(R.id.field_location)).setText(curSettings.getLocationName());
                ((AutoCompleteTextView) mLayout.findViewById(R.id.field_lab)).setText(curSettings.getLab(), false);
                ((EditText) mLayout.findViewById(R.id.field_recorder)).setText(curSettings.getRecorder());

                ((CheckBox) mLayout.findViewById(R.id.checkbox_specimen_id)).setChecked(curSettings.useSpecimenId());

                ((CheckBox) mLayout.findViewById(R.id.checkbox_print)).setChecked(curSettings.isPrintLabels());
                ((EditText) mLayout.findViewById(R.id.print_qty)).setText(String.valueOf(curSettings.getPrintQty()));
                ((AutoCompleteTextView) mLayout.findViewById(R.id.field_printer)).setText(curSettings.getPrinterModel(), false);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_1)).setChecked(curSettings.getPrintStyle() == 1);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_2)).setChecked(curSettings.getPrintStyle() == 2);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_3)).setChecked(curSettings.getPrintStyle() == 3);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_4)).setChecked(curSettings.getPrintStyle() == 4);
            }
        });

        // Set listeners for text fields
        ((EditText) mLayout.findViewById(R.id.field_test_type)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                curSettings.setTestType(s.toString());
            }
        });
        ((EditText) mLayout.findViewById(R.id.field_location)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                curSettings.setLocationName(s.toString());
            }
        });
        ((EditText) mLayout.findViewById(R.id.field_lab)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                curSettings.setLab(s.toString());
            }
        });
        ((EditText) mLayout.findViewById(R.id.field_recorder)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                curSettings.setRecorder(s.toString());
            }
        });
        ((EditText) mLayout.findViewById(R.id.field_printer)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                curSettings.setPrinterModel(s.toString());
            }
        });

        // Set listeners for buttons
        mLayout.findViewById(R.id.btn_print_minus).setOnClickListener(this);
        mLayout.findViewById(R.id.btn_print_plus).setOnClickListener(this);
        mLayout.findViewById(R.id.card_print_style_1).setOnClickListener(this);
        mLayout.findViewById(R.id.card_print_style_2).setOnClickListener(this);
        mLayout.findViewById(R.id.card_print_style_3).setOnClickListener(this);
        mLayout.findViewById(R.id.card_print_style_4).setOnClickListener(this);
        mLayout.findViewById(R.id.radio_print_style_1).setOnClickListener(this);
        mLayout.findViewById(R.id.radio_print_style_2).setOnClickListener(this);
        mLayout.findViewById(R.id.radio_print_style_3).setOnClickListener(this);
        mLayout.findViewById(R.id.radio_print_style_4).setOnClickListener(this);

        // Set listener for print checkbox
        ((CheckBox) mLayout.findViewById(R.id.checkbox_print)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                curSettings.setPrintLabels(isChecked);

                mLayout.findViewById(R.id.layout_print_qty).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.layout_print_settings).setVisibility(isChecked ? View.VISIBLE : View.GONE);

                mLayout.findViewById(R.id.radio_print_style_2).setVisibility(curSettings.useSpecimenId() ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.card_print_style_2).setVisibility(curSettings.useSpecimenId() ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.radio_print_style_3).setVisibility(curSettings.useSpecimenId() ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.card_print_style_3).setVisibility(curSettings.useSpecimenId() ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.radio_print_style_4).setVisibility(curSettings.useSpecimenId() ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.card_print_style_4).setVisibility(curSettings.useSpecimenId() ? View.VISIBLE : View.GONE);
            }
        });

        // Set listener for specimen ID checkbox
        ((CheckBox) mLayout.findViewById(R.id.checkbox_specimen_id)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                curSettings.setUseSpecimenId(isChecked);

                if (!isChecked)
                    mLayout.findViewById(R.id.radio_print_style_1).callOnClick();

                mLayout.findViewById(R.id.radio_print_style_2).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.card_print_style_2).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.radio_print_style_3).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.card_print_style_3).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.radio_print_style_4).setVisibility(isChecked ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.card_print_style_4).setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_print_minus:
                curSettings.incrementPrintQty(-1);
                break;
            case R.id.btn_print_plus:
                curSettings.incrementPrintQty(1);
                break;
            case R.id.card_print_style_1:
            case R.id.radio_print_style_1:
                curSettings.setPrintStyle(1);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_1)).setChecked(curSettings.getPrintStyle() == 1);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_2)).setChecked(curSettings.getPrintStyle() == 2);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_3)).setChecked(curSettings.getPrintStyle() == 3);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_4)).setChecked(curSettings.getPrintStyle() == 4);
                break;
            case R.id.card_print_style_2:
            case R.id.radio_print_style_2:
                curSettings.setPrintStyle(2);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_1)).setChecked(curSettings.getPrintStyle() == 1);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_2)).setChecked(curSettings.getPrintStyle() == 2);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_3)).setChecked(curSettings.getPrintStyle() == 3);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_4)).setChecked(curSettings.getPrintStyle() == 4);
                break;
            case R.id.card_print_style_3:
            case R.id.radio_print_style_3:
                curSettings.setPrintStyle(3);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_1)).setChecked(curSettings.getPrintStyle() == 1);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_2)).setChecked(curSettings.getPrintStyle() == 2);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_3)).setChecked(curSettings.getPrintStyle() == 3);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_4)).setChecked(curSettings.getPrintStyle() == 4);
                break;
            case R.id.card_print_style_4:
            case R.id.radio_print_style_4:
                curSettings.setPrintStyle(4);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_1)).setChecked(curSettings.getPrintStyle() == 1);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_2)).setChecked(curSettings.getPrintStyle() == 2);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_3)).setChecked(curSettings.getPrintStyle() == 3);
                ((RadioButton) mLayout.findViewById(R.id.radio_print_style_4)).setChecked(curSettings.getPrintStyle() == 4);
                break;
        }

        mViewModel.mSettings.setValue(curSettings);
    }

    @Override
    public void onPause() {
        super.onPause();
        mViewModel.writeSettings(curSettings);
    }

}

package com.mexs.scanner;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;

public class FormFragment extends Fragment implements View.OnClickListener{

    private Context mContext;
    private MainViewModel mViewModel;
    private View mLayout;

    private Record curRecord = new Record();
    private UserSettings curSettings;
    private boolean isNewRecord = true;

    public FormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context){
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
        mLayout = inflater.inflate(R.layout.fragment_form, container, false);

        // Set adapter for Nationalities
        String[] listTopNat = mContext.getResources().getStringArray(R.array.top_nat);
        String[] mTopNat = new String[listTopNat.length];
        for (int i=0; i<listTopNat.length; i++)
            mTopNat[i] = listTopNat[i].split(";")[0];

        ArrayAdapter<String> adapterNationalities = new ArrayAdapter<>(mContext, R.layout.dropdown_menu_popup_item, mTopNat);
        ((AutoCompleteTextView) mLayout.findViewById(R.id.field_nationality)).setAdapter(adapterNationalities);

        // Set adapter for Gender
        ArrayAdapter<String> adapterGender = new ArrayAdapter<>(mContext, R.layout.dropdown_menu_popup_item, mContext.getResources().getStringArray(R.array.gender));
        ((AutoCompleteTextView) mLayout.findViewById(R.id.field_gender)).setAdapter(adapterGender);

        // Re-populate form if required
        MutableLiveData<Record> readRecord = mViewModel.mCurRecord;
        readRecord.observe(this, new Observer<Record>() {
            @Override
            public void onChanged(Record record) {
                curRecord = record;

                // Check whether new record or editing
                isNewRecord = curRecord.getSn() == 0;

                setEditMode(!isNewRecord);

                ((EditText) mLayout.findViewById(R.id.field_name)).setText(record.getName());
                ((EditText) mLayout.findViewById(R.id.field_id)).setText(record.getId());
                ((EditText) mLayout.findViewById(R.id.field_dob)).setText(record.getDob());
                ((AutoCompleteTextView) mLayout.findViewById(R.id.field_gender)).setText(record.getGender(), false);
                ((AutoCompleteTextView) mLayout.findViewById(R.id.field_nationality)).setText(record.getNationality(), false);
                ((EditText) mLayout.findViewById(R.id.field_address)).setText(record.getAddress());
                ((EditText) mLayout.findViewById(R.id.field_contact)).setText(record.getContact());
                ((EditText) mLayout.findViewById(R.id.field_remarks)).setText(record.getRemarks());

                // Observe once only
                readRecord.removeObserver(this);
            }
        });

        // Get current settings and set UI
        MutableLiveData<UserSettings> readSettings = mViewModel.mSettings;
        readSettings.observe(this, new Observer<UserSettings>() {
            @Override
            public void onChanged(UserSettings userSettings) {
                curSettings = userSettings;

                // Show save button if print quantity is zero
                mLayout.findViewById(R.id.button_print).setVisibility(curSettings.isPrintLabels() ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.button_save_records).setVisibility(curSettings.isPrintLabels() ? View.GONE : View.VISIBLE);

                // Set specimen ID
                if (isNewRecord){
                    mLayout.findViewById(R.id.layout_specimen_id).setVisibility(curSettings.useSpecimenId() ? View.VISIBLE : View.GONE);
                } else {
                    mLayout.findViewById(R.id.layout_specimen_id).setVisibility(!curRecord.getSpecimenId().isEmpty() ? View.VISIBLE : View.GONE);
                }

                // Observe once only
                readSettings.removeObserver(this);
            }
        });

        // Set listeners for text changes
        ((EditText) mLayout.findViewById(R.id.field_name)).addTextChangedListener(textWatcherName);
        ((EditText) mLayout.findViewById(R.id.field_id)).addTextChangedListener(textWatcherId);
        ((EditText) mLayout.findViewById(R.id.field_dob)).addTextChangedListener(textWatcherDob);
        ((EditText) mLayout.findViewById(R.id.field_gender)).addTextChangedListener(textWatcherGender);
        ((EditText) mLayout.findViewById(R.id.field_nationality)).addTextChangedListener(textWatcherNationality);
        ((EditText) mLayout.findViewById(R.id.field_address)).addTextChangedListener(textWatcherAddress);
        ((EditText) mLayout.findViewById(R.id.field_contact)).addTextChangedListener(textWatcherContact);
        ((EditText) mLayout.findViewById(R.id.field_remarks)).addTextChangedListener(textWatcherRemarks);
        ((EditText) mLayout.findViewById(R.id.field_specimen_id)).addTextChangedListener(textWatcherSpecimenId);

        mLayout.findViewById(R.id.button_rescan).setOnClickListener(this);
        mLayout.findViewById(R.id.button_discard).setOnClickListener(this);
        mLayout.findViewById(R.id.button_save_records).setOnClickListener(this);
        mLayout.findViewById(R.id.button_print).setOnClickListener(this);

        return mLayout;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_rescan:
                mViewModel.setCurState("scan");
                break;
            case R.id.button_discard:
                mViewModel.mCurRecord.setValue(new Record());
                mViewModel.setCurState("records");
                break;
            case R.id.button_save_records:
                // Save only
                recordsSavePrint(false);
                break;
            case R.id.button_print:
                // Save and print labels
                recordsSavePrint(true);
                break;
        }
    }

    private void setEditMode(boolean isEditMode){
        // Set mode indicator
        ((MaterialButtonToggleGroup) mLayout.findViewById(R.id.btn_grp_mode)).check(isEditMode ? R.id.btn_mode_edit : R.id.btn_mode_new);

        // Show discard button
        mLayout.findViewById(R.id.button_discard).setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    private TextWatcher textWatcherName = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            curRecord.setName(s.toString());
        }
    };
    private TextWatcher textWatcherId = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            curRecord.setId(s.toString());
        }
    };
    private TextWatcher textWatcherDob = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            curRecord.setDob(s.toString());
        }
    };
    private TextWatcher textWatcherGender = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            curRecord.setGender(s.toString());
        }
    };
    private TextWatcher textWatcherNationality = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            curRecord.setNationality(s.toString());
        }
    };
    private TextWatcher textWatcherAddress = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            curRecord.setAddress(s.toString());
        }
    };
    private TextWatcher textWatcherContact = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            curRecord.setContact(s.toString());
        }
    };
    private TextWatcher textWatcherRemarks = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            curRecord.setRemarks(s.toString());
        }
    };
    private TextWatcher textWatcherSpecimenId = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            curRecord.setSpecimenId(s.toString());
        }
    };

    private void recordsSavePrint(Boolean print){
        // Do input validation here

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(mContext);
        dialog.setTitle(print ? "Save and Print" : "Save");
        dialog.setMessage("Please check details for accuracy.\n\n" + (print ? "Proceed to save new record and print labels?" : "Proceed to save new record?"));
        dialog.setNegativeButton("Cancel", null);
        dialog.setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Include additional details
                if (isNewRecord)
                    curRecord.setSwabTimestamp(new Date().getTime());

                curRecord.setTestType(curSettings.getTestType());
                curRecord.setLocationName(curSettings.getLocationName());
                curRecord.setLab(curSettings.getLab());
                curRecord.setRecorder(curSettings.getRecorder());

                // Print labels
                if (print) {
                    if (curSettings.getPrinterModel().equals("BROTHER QL-820NWB"))
                        new PrintLabelBrother(mContext.getResources().getString(R.string.printer_brother_ip), mContext.getResources().getInteger(R.integer.printer_brother_port), curSettings, curRecord).start();

                    if (curSettings.getPrinterModel().equals("ZEBRA ZQ520"))
                        new PrintLabelZebra(mContext, curSettings, curRecord).print();
                }

                // Insert record into database
                if (isNewRecord){
                    // New record
                    mViewModel.insertRecord(curRecord);
                } else {
                    // Editing previous record
                    mViewModel.updateRecord(curRecord);
                }

                // Go back to scanning or records page
                mViewModel.mCurRecord.setValue(new Record());
                mViewModel.setCurState(isNewRecord ? "scan" : "records");
            }
        });
        dialog.show();
    }

}

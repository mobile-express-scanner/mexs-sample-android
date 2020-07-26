package com.mexs.scanner;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecordsFragment extends Fragment {

    private Context mContext;
    private MainViewModel mViewModel;
    private View mLayout;

    private List<Record> mRecords = new ArrayList<>();
    private RecordsAdapter mRecordsAdapter;
    private UserSettings curSettings = new UserSettings();
    private Handler mHandler;

    public RecordsFragment() {
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
        mLayout = inflater.inflate(R.layout.fragment_records, container, false);
        setHasOptionsMenu(true);

        // Create RecyclerView
        RecyclerView recyclerView = mLayout.findViewById(R.id.list_records);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        mRecordsAdapter = new RecordsAdapter(mRecords);
        recyclerView.setAdapter(mRecordsAdapter);

        // Show or hide list based on records count
        mViewModel.countRecords().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer count) {
                mLayout.findViewById(R.id.text_empty).setVisibility(count == 0 ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.list_records).setVisibility(count == 0 ? View.GONE : View.VISIBLE);
            }
        });

        // Set listeners for records list
        mViewModel.readRecords().observe(this, new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {
                mRecords.clear();
                mRecords.addAll(records);
                mRecordsAdapter.notifyDataSetChanged();
            }
        });

        // Set listeners for user settings
        mViewModel.mSettings.observe(this, new Observer<UserSettings>() {
            @Override
            public void onChanged(UserSettings settings) {
                curSettings = settings;
            }
        });

        return mLayout;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_records, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_export:
                exportRecords();
                return true;
            case R.id.menu_delete:
                deleteRecords();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void exportRecords(){
        // Check number of records to export
        if (mRecords == null || mRecords.size() == 0){
            Toast.makeText(mContext, "No records to export.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Ask user for filename
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(mContext);
        dialogBuilder.setTitle("Export Records");
        dialogBuilder.setView(getLayoutInflater().inflate(R.layout.dialog_filename, null));
        dialogBuilder.setNegativeButton("Cancel", null);
        dialogBuilder.setPositiveButton("Export", null);

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button buttonPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                buttonPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText fieldFilename = ((Dialog) dialog).findViewById(R.id.field_filename);
                        if (fieldFilename != null) {
                            // Do filename validation here
                            saveExcelFile(fieldFilename.getText().toString());
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void deleteRecords(){
        // Check number of records to delete
        if (mRecords == null || mRecords.size() == 0){
            Toast.makeText(mContext, "No records to delete.", Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(mContext);
        dialog.setTitle("Delete all data");
        dialog.setMessage("WARNING: This will delete all recorded data from the database and cannot be undone.\n\nAre you sure you want to proceed?");
        dialog.setNegativeButton("Cancel", null);
        dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Clear from local database
                mViewModel.deleteAllRecords();

                // Clear settings
                mViewModel.writeSettings(new UserSettings());

                Toast.makeText(mContext, "All recorded data deleted from database.", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void saveExcelFile(String filename){
        Runnable runnableWriteFile = new Runnable() {
            @Override
            public void run() {
                try {
                    File fileExport = new File(mContext.getExternalFilesDir(null), String.format(Locale.US, "/%s.csv", filename));
                    FileOutputStream fileOutputStream = new FileOutputStream(fileExport);
                    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);

                    StringBuilder stringExport = new StringBuilder();

                    // Create header row
                    stringExport.append("\"Test Type\",");
                    stringExport.append("\"Venue\",");
                    stringExport.append("\"Laboratory\",");
                    stringExport.append("\"Recorded by\",");
                    stringExport.append("\"Date & Time\",");
                    stringExport.append("\"ID Number\",");
                    stringExport.append("\"Name\",");
                    stringExport.append("\"DOB\",");
                    stringExport.append("\"Sex\",");
                    stringExport.append("\"Nationality\",");
                    stringExport.append("\"Address\",");
                    stringExport.append("\"Contact Number\",");
                    stringExport.append("\"Remarks\"\n");
                    stringExport.append("\"Specimen ID\",");

                    // Create data rows
                    for (int i=0; i<mRecords.size(); i++) {
                        Record record = mRecords.get(i);
                        stringExport.append(String.format("\"%s\",",record.getTestType()));
                        stringExport.append(String.format("\"%s\",",record.getLocationName()));
                        stringExport.append(String.format("\"%s\",",record.getLab()));
                        stringExport.append(String.format("\"%s\",",record.getRecorder()));
                        stringExport.append(String.format("\"%s\",",record.getSwabDateTime()));
                        stringExport.append(String.format("\"%s\",",record.getId()));
                        stringExport.append(String.format("\"%s\",",record.getName()));
                        stringExport.append(String.format("\"%s\",",record.getDob()));
                        stringExport.append(String.format("\"%s\",",record.getGender()));
                        stringExport.append(String.format("\"%s\",",record.getNationality()));
                        stringExport.append(String.format("\"%s\",",record.getAddress()));
                        stringExport.append(String.format("\"%s\",",record.getContact()));
                        stringExport.append(String.format("\"%s\"\n",record.getRemarks()));
                        stringExport.append(String.format("\"%s\",",record.getSpecimenId()));
                    }

                    // Write to file
                    outputStreamWriter.write(stringExport.toString());
                    outputStreamWriter.flush();
                    outputStreamWriter.close();

                    // Send file
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(mContext, "com.mexs.scanner.fileprovider", fileExport));
                    shareIntent.setType("application/octet-stream");
                    startActivity(Intent.createChooser(shareIntent, "Select recipient"));
                } catch (IOException e) {
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(mContext, "Unable to write file! Please check filename or storage space.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        };

        mHandler = new Handler();
        mHandler.post(runnableWriteFile);
    }

    private class RecordsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<Record> nRecords;

        public class ItemViewHolder extends RecyclerView.ViewHolder{
            public Button buttonEdit, buttonDelete, buttonPrint;
            public Chip chipSn;
            public TextView textViewId, textViewName, textViewDetails;

            public ItemViewHolder(View v){
                super(v);

                buttonEdit = (Button) v.findViewById(R.id.button_record_edit);
                buttonDelete = (Button) v.findViewById(R.id.button_record_delete);
                buttonPrint = (Button) v.findViewById(R.id.button_record_print);
                chipSn = (Chip) v.findViewById(R.id.text_sn);
                textViewId = (TextView) v.findViewById(R.id.text_id);
                textViewName = (TextView) v.findViewById(R.id.text_name);
                textViewDetails = (TextView) v.findViewById(R.id.text_details);
            }
        }

        public RecordsAdapter(List<Record> records) {
            this.nRecords = records;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.records_text_view, parent, false);
            return new ItemViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Record record = nRecords.get(position);

            // Get running count for chip s/n
            ((ItemViewHolder) holder).chipSn.setText(String.format(Locale.US, "%d", nRecords.size() - position));

            ((ItemViewHolder) holder).textViewId.setText(String.format("%s", record.getId()));
            ((ItemViewHolder) holder).textViewName.setText(String.format("%s", record.getName()));

            StringBuilder textDetailsBuilder = new StringBuilder();
            if (!record.getDob().isEmpty() && !record.getGender().isEmpty()) {
                textDetailsBuilder.append(String.format("DOB: %s               Sex: %s\n", record.getDob(), record.getGender()));
            } else {
                textDetailsBuilder.append(record.getDob().isEmpty() ? "" : String.format("DOB: %s\n", record.getDob()));
                textDetailsBuilder.append(record.getGender().isEmpty() ? "" : String.format("Sex: %s\n", record.getGender()));
            }
            textDetailsBuilder.append(record.getNationality().isEmpty() ? "" : String.format("Nationality: %s\n", record.getNationality()));
            textDetailsBuilder.append(record.getAddress().isEmpty() ? "" : String.format("Address: %s\n", record.getAddress()));
            textDetailsBuilder.append(record.getContact().isEmpty() ? "" : String.format("Contact Number: %s\n", record.getContact()));
            textDetailsBuilder.append(record.getRemarks().isEmpty() ? "" : String.format("Remarks: %s\n", record.getRemarks()));
            textDetailsBuilder.append(record.getSpecimenId().isEmpty() ? "" : String.format("Specimen ID: %s\n", record.getSpecimenId()));
            textDetailsBuilder.append(record.getSwabDateTime().isEmpty() ? "" : String.format("Test Done: %s\n\n", record.getSwabDateTime()));
            textDetailsBuilder.append(record.getLocationName().isEmpty() ? "" : String.format("Venue: %s\n", record.getLocationName()));
            textDetailsBuilder.append(record.getLab().isEmpty() ? "" : String.format("Lab: %s\n", record.getLab()));
            textDetailsBuilder.append(record.getRecorder().isEmpty() ? "" : String.format("Recorded By: %s\n", record.getRecorder()));

            String textDetails = textDetailsBuilder.toString();
            ((ItemViewHolder) holder).textViewDetails.setVisibility(textDetails.isEmpty() ? View.GONE : View.VISIBLE);
            ((ItemViewHolder) holder).textViewDetails.setText(textDetails.trim());

            ((ItemViewHolder) holder).buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.mCurRecord.setValue(record);
                    mViewModel.setCurState("form");
                }
            });

            ((ItemViewHolder) holder).buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(mContext);
                    dialog.setTitle("Delete Record");
                    dialog.setMessage("WARNING: This action cannot be undone. Are you sure you want to proceed?");
                    dialog.setNegativeButton("Cancel", null);
                    dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mViewModel.deleteRecord(record);
                        }
                    });
                    dialog.show();
                }
            });

            ((ItemViewHolder) holder).buttonPrint.setVisibility(curSettings.isPrintLabels() ? View.VISIBLE : View.GONE);
            ((ItemViewHolder) holder).buttonPrint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MaterialAlertDialogBuilder dialog = new MaterialAlertDialogBuilder(mContext);
                    dialog.setTitle("Print");
                    dialog.setMessage("Proceed to print labels?");
                    dialog.setNegativeButton("Cancel", null);
                    dialog.setPositiveButton("Print", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Print labels
                            if (curSettings.getPrinterModel().equals("BROTHER QL-820NWB"))
                                new PrintLabelBrother(mContext.getResources().getString(R.string.printer_brother_ip), mContext.getResources().getInteger(R.integer.printer_brother_port), curSettings, record).start();

                            if (curSettings.getPrinterModel().equals("ZEBRA ZQ520"))
                                new PrintLabelZebra(mContext, curSettings, record).print();
                        }
                    });
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return nRecords.size();
        }
    }

}

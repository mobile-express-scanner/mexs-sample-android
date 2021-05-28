package com.mexs.scanner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.mexs.idparser.IdData;
import com.mexs.idparser.IdParser;
import com.mexs.idparser.IdParser.NAT_RETURN_TYPE;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21;

public class CameraFragment extends Fragment implements View.OnClickListener{

    private Context mContext;
    private MainViewModel mViewModel;
    private View mLayout;

    private CameraView mCamera;
    private MutableLiveData<Boolean> mProcessing = new MutableLiveData<>();

    private IdParser mIdParser;
    private IdData mIdData;

    public CameraFragment() {
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mLayout = inflater.inflate(R.layout.fragment_camera, container, false);

        // Set onClick for buttons
        mLayout.findViewById(R.id.button_start).setOnClickListener(this);
        mLayout.findViewById(R.id.button_pause).setOnClickListener(this);
        mLayout.findViewById(R.id.button_skip).setOnClickListener(this);
        mLayout.findViewById(R.id.button_scan_again).setOnClickListener(this);
        mLayout.findViewById(R.id.button_next).setOnClickListener(this);

        mProcessing.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean start) {
                if (start){
                    mCamera.addFrameProcessor(frameProcessor);
                } else {
                    mCamera.removeFrameProcessor(frameProcessor);
                }

                mLayout.findViewById(R.id.button_start).setVisibility(start ? View.GONE : View.VISIBLE);
                mLayout.findViewById(R.id.button_pause).setVisibility(start ? View.VISIBLE : View.GONE);
                mLayout.findViewById(R.id.progress_scanning).setVisibility(start ? View.VISIBLE : View.INVISIBLE);
            }
        });

        // Set up listeners for data field preview
        mViewModel.mIdData.observe(this, new Observer<IdData>() {
            @Override
            public void onChanged(IdData idData) {
                mIdData = idData;

                // Remove frame processors if all IdData fields are extracted (confident)
                if (idData.isConfidentAll())
                    mProcessing.setValue(false);

                // Display text
                ((TextView) mLayout.findViewById(R.id.text_preview_name)).setText(idData.getName());
                ((TextView) mLayout.findViewById(R.id.text_preview_sgid)).setText(idData.getSgId());
                ((TextView) mLayout.findViewById(R.id.text_preview_dob)).setText(idData.getDob());
                ((TextView) mLayout.findViewById(R.id.text_preview_gender)).setText(idData.getGender());
                ((TextView) mLayout.findViewById(R.id.text_preview_nationality)).setText(idData.getNationality());

                // Show green tick if detection confident
                mLayout.findViewById(R.id.ic_check_name).setVisibility(idData.isNameConfident() ?  View.VISIBLE : View.INVISIBLE);
                mLayout.findViewById(R.id.ic_check_sgid).setVisibility(idData.isSgIdConfident() ?  View.VISIBLE : View.INVISIBLE);
                mLayout.findViewById(R.id.ic_check_dob).setVisibility(idData.isDobConfident() ?  View.VISIBLE : View.INVISIBLE);
                mLayout.findViewById(R.id.ic_check_gender).setVisibility(idData.isGenderConfident() ?  View.VISIBLE : View.INVISIBLE);
                mLayout.findViewById(R.id.ic_check_nationality).setVisibility(idData.isNationalityConfident() ?  View.VISIBLE : View.INVISIBLE);

                // Show/hide buttons
                mLayout.findViewById(R.id.layout_start).setVisibility(idData.isConfidentAll() ? View.GONE : View.VISIBLE);
                mLayout.findViewById(R.id.layout_done).setVisibility(idData.isConfidentAll() ? View.VISIBLE : View.GONE);
            }
        });

        return mLayout;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Set up camera
        mCamera = mLayout.findViewById(R.id.view_camera);
        mCamera.setLifecycleOwner(getViewLifecycleOwner());

        // Create string array for top nationalities
        String[] listTopNat = mContext.getResources().getStringArray(R.array.top_nat);
        String[][] mTopNat = new String[listTopNat.length][3];
        for (int i=0; i<listTopNat.length; i++)
            mTopNat[i] = listTopNat[i].split(";");

        // Initialise and reset scan fields
        mViewModel.mIdData.setValue(new IdData());
        mIdParser = new IdParser(Calendar.getInstance().get(Calendar.YEAR), mTopNat, NAT_RETURN_TYPE.NATIONALITY);
        mProcessing.setValue(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_start:
                // Start processing frames
                mProcessing.setValue(true);
                break;
            case R.id.button_pause:
                // Stop processing frames
                mProcessing.setValue(false);
                break;
            case R.id.button_scan_again:
                // Reset fields
                mIdParser.clear();
                mViewModel.mIdData.setValue(new IdData());

                // Start processing frames
                mProcessing.setValue(true);
                break;
            case R.id.button_next:
            case R.id.button_skip:
                // Store detected fields into current record
                Record record = new Record();
                record.setName(mIdData.getName());
                record.setId(mIdData.getSgId());
                record.setDob(mIdData.getDob());
                record.setGender(mIdData.getGender());
                record.setNationality(mIdData.getNationality());
                mViewModel.mCurRecord.setValue(record);

                mViewModel.setCurState("form");
                break;
        }
    }

    private FrameProcessor frameProcessor = new FrameProcessor() {
        @Override
        @WorkerThread
        public void process(@NonNull Frame frame) {
            Size size = frame.getSize();
            FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                    .setFormat(IMAGE_FORMAT_NV21)
                    .setHeight(size.getHeight())
                    .setWidth(size.getWidth())
                    .setRotation(degreesToFirebaseRotation(frame.getRotationToUser()))
                    .build();

            FirebaseVisionImage image = FirebaseVisionImage.fromByteArray(frame.getData(), metadata);
            FirebaseVisionTextRecognizer fvRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
            Task<FirebaseVisionText> task = fvRecognizer.processImage(image);

            try {
                FirebaseVisionText result = Tasks.await(task);

                IdData idData = mIdParser.process(result.getText());
                mViewModel.mIdData.postValue(idData);
            } catch (ExecutionException | InterruptedException ignored) {
            }
        }
    };

    private int degreesToFirebaseRotation(int degrees) {
        switch (degrees) {
            case 0:
                return FirebaseVisionImageMetadata.ROTATION_0;
            case 90:
                return FirebaseVisionImageMetadata.ROTATION_90;
            case 180:
                return FirebaseVisionImageMetadata.ROTATION_180;
            case 270:
                return FirebaseVisionImageMetadata.ROTATION_270;
            default:
                throw new IllegalArgumentException("Rotation must be 0, 90, 180, or 270.");
        }
    }

}

package andriod.bignerdranch.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static android.widget.CompoundButton.OnCheckedChangeListener;
import static android.widget.CompoundButton.OnClickListener;

public class CrimeFragment extends Fragment {

    private static final String DATE_FORMAT = "EEE, MMM dd, yyyy";
    private static final String TIME_FORMAT = "hh:mm a";
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final int REQUEST_PHOTO = 3;
    private static final int REQUEST_CONTACT = 2;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_DATE = 0;


    private Crime mCrime;
    private File mPhotoFile;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    public static Button mTopButton;
    public static Button mBottomButton;
    private Button mReportButton;
    private Button mSuspectButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    public Callbacks mCallbacks;


    public interface Callbacks {
        void onCrimeUpdated(Crime crime);
    }



    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);

       mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

       mPhotoFile = CrimeLab.get(getActivity()).getPhotoFle(mCrime);

    }


    @Override
    public void onPause() {
        super.onPause();

            CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,  ViewGroup container,
                             Bundle savedInstanceState) {
         super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(andriod.bignerdranch.com.R.layout.fragment_crime,
                container, false);

        mTitleField = (EditText)
                v.findViewById(andriod.bignerdranch.com.R.id.crime_title);
                    editText();

        mDateButton = (Button)
                v.findViewById(andriod.bignerdranch.com.R.id.crime_date);
                    setDate();

        mTimeButton = (Button)
                v.findViewById(R.id.crime_time);
                    setTime();

        mSolvedCheckBox = (CheckBox)
                v.findViewById(andriod.bignerdranch.com.R.id.crime_solved);
                    checkBox();


        mTopButton = (Button)
                v.findViewById(R.id.jump_to_top);
        mTopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrimePagerActivity.mViewPager.setCurrentItem(0);
            }
        });

        mBottomButton = (Button)
                v.findViewById(R.id.jump_to_bottom);
        mBottomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CrimePagerActivity.mViewPager.setCurrentItem(CrimePagerActivity.mViewPager.getAdapter().getCount() -1);
            }
        });

        mReportButton = (Button)
                v.findViewById(R.id.crime_report);
                mReportButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());
                        i.putExtra(Intent.EXTRA_SUBJECT,
                                getString(R.string.crime_report_subject));
                        i = Intent.createChooser(i, getString(R.string.send_report));
                        startActivity(i);
                    }
                });

        final Intent pickContact = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton = (Button)
                v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        PackageManager packageManager = getActivity()
                .getPackageManager();
        if (packageManager.resolveActivity(pickContact,
                PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mPhotoButton = (ImageButton)
                v.findViewById(R.id.crime_camera);
        final Intent captureImagae = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null && captureImagae
                .resolveActivity(packageManager) != null;
                mPhotoButton.setEnabled(canTakePhoto);


        mPhotoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "android.bignerdranch.com.criminalintent.fileprovider",
                        mPhotoFile);
                captureImagae.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities =
                        getActivity().getPackageManager()
                        .queryIntentActivities(captureImagae, PackageManager
                                .MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(String.valueOf(activity.activityInfo),
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                startActivityForResult(captureImagae, REQUEST_PHOTO);
            }
        });


        mPhotoView = (ImageView)
                v.findViewById(R.id.crime_photo);
        updatePhotoView();

        return v;
    }










































   public void editText() {
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence text, int start,
                                          int count, int after) {
                //Action
            }

            @Override
            public void onTextChanged(CharSequence text, int start,
                                      int before, int count) {
                mCrime.setTitle(text.toString());
                updateCrime();
            }

            @Override
            public void afterTextChanged(Editable text) {

                //Action

            }
        });

   }


   public void setDate() {
       updateDate();
       mDateButton.setOnClickListener(new OnClickListener() {
          @Override
          public void onClick(View view) {
              FragmentManager manager = getFragmentManager();
              DatePickerFragment dialog = DatePickerFragment
                      .newInstance(mCrime.getDate());

              dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
              dialog.show(manager, DIALOG_DATE);
          }
      });

   }


    public void setTime() {
        updateTime();
        mTimeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment
                        .newInstance(mCrime.getTime());

                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

    }


   public void checkBox() {
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void
        onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
                updateCrime();
            }
        });
   }

    public void returnResult() {
        getActivity().setResult(Activity.RESULT_OK, null);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE) {
           Date date = (Date) data
                    .getSerializableExtra(DatePickerFragment.EXTRA_DATE);
        mCrime.setDate(date);
        updateCrime();

            updateDate();
        }

       else if (requestCode == REQUEST_TIME) {
            Date time = (Date) data
                    .getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setTime(time);
            updateCrime();

            updateTime();

        } else if (requestCode == REQUEST_CONTACT && data != null)
        {
            Uri contactUri = data.getData();
            // Specify which field query should return;

            String [] queryFields = new String[] {
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            // Perform query. ContractUri is like a "where".

            Cursor c = getActivity().getContentResolver()
                    .query(contactUri, queryFields, null, null, null);

            try {
                // check for results

                if (c.getCount() == 0) {
                    return;
                }
                // Pull our the first column of the first row of data
                // That is your suspect's name.
                c.moveToFirst();
                String suspect = c.getString(0);
                mCrime.setSuspect(suspect);
                updateCrime();
                mSuspectButton.setText(suspect);

            } finally {
                c.close();
            }
        }  else if (requestCode == REQUEST_PHOTO) {
           Uri uri = FileProvider.getUriForFile(getActivity(),
                   "android.bignerdranch.com.criminalintent.fileprovider",
                   mPhotoFile);
           getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
           updateCrime();

           updatePhotoView();
        }


    }


    private void updateCrime() {
        CrimeLab.get(getActivity()).updateCrime(mCrime);
        mCallbacks.onCrimeUpdated(mCrime);
    }


    private void updateDate() {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        mDateButton.setText(dateFormat.format(mCrime.getDate()));
    }

    private void updateTime() {
        DateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
        mTimeButton.setText(timeFormat.format(mCrime.getTime()));
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateFormat = "EEE, MM dd";
        DateFormat dateformat  = new SimpleDateFormat(dateFormat);
        String dateString = dateformat.format(mCrime.getDate());

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        String report = getString(R.string.crime_report, mCrime.getTitle(),
                dateString, solvedString, suspect);

        return report;
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_no_image_description));
        } else {
            Bitmap bitmap = PictureUtils.getScaleBitmap(mPhotoFile.getPath(),
                    getActivity());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_image_description));
        }
    }




}

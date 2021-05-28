package andriod.bignerdranch.com;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class CrimeListFragment  extends Fragment {

    private static final String DATE_FORMAT = "EEE, MMM dd, yyyy hh:mm";
    private static final String CLICKED_ITEM_POSITOIN = "andriod.bignerdranch.com.clicked_item_position";
    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";
    private static final int REQUEST_CRIME = 1;
        private RecyclerView mCrimeRecyclerView;
        private CrimeAdapter mAdapter;
        private int itemPosition;
        private boolean mSubtitleVisible;
        private Callbacks mCallbacks;


        public interface Callbacks {
            void onCrimeSelected(Crime crime);
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mCallbacks = (Callbacks) context;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setHasOptionsMenu(true);
        }




        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
                View view = inflater.inflate(R.layout.fragment_crime_list,
                        container, false);

                mCrimeRecyclerView = (RecyclerView)
                        view.findViewById(R.id.crime_recycler_view);
                                setLayoutManager();


            if (savedInstanceState != null) {
                itemPosition = savedInstanceState.getInt(CLICKED_ITEM_POSITOIN);
                mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
            }

                return view;
        }





























    public void setLayoutManager() {
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }



    public void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes);
            mAdapter.notifyItemChanged(itemPosition);
        }
        updateSubtitle();
    }




        private class CrimeHolder extends RecyclerView.ViewHolder
                implements View.OnClickListener  {



                        private TextView mTitleTextView;
                        private TextView mDateTextView;
                        private ImageView mSolvedImageView;
                        private Button mButton;

                        private Crime mCrime;

                public CrimeHolder(View v) {
                        super(v);

                        itemView.setOnClickListener(this);


                        mTitleTextView = (TextView)
                                itemView.findViewById(R.id.crime_title);
                        mDateTextView = (TextView)
                                itemView.findViewById(R.id.crime_date);
                        mSolvedImageView  = (ImageView)
                                itemView.findViewById(R.id.crime_solved);

                }


                public void bind(Crime crime) {
                        mCrime = crime;
                        mTitleTextView.setText(mCrime.getTitle());

                    DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
                    mDateTextView.setText(dateFormat.format(mCrime.getDate()));

                 mSolvedImageView.setVisibility(crime.isSolved() ?
                         View.VISIBLE : View.GONE);

                 if (getItemViewType() == 1) {
                     mButton = (Button)
                             itemView.findViewById(R.id.crime_button);
                     mButton.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View view) {
                             Toast.makeText(getContext(), "The Police Will Contact You Soon", Toast.LENGTH_LONG).show();
                         }
                     });
                 }

                }

                @Override
                public void onClick(View view) {
                    mCallbacks.onCrimeSelected(mCrime);
                   }
        }




        public  class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
                private List<Crime> mCrimes;

                public CrimeAdapter(List<Crime> crimes) {
                        mCrimes = crimes;
                }



         @Override
                public int getItemViewType(int position) {
                    itemPosition = position;
                        boolean PoliceRequired = mCrimes.get(position).isPoliceRequired();
                        if (PoliceRequired) {
                            return 1;
                         } else {
                            return 0;
                        }

         }

        @Override
                public CrimeHolder onCreateViewHolder( ViewGroup parent, int viewType) {

                       LayoutInflater layoutInflater = LayoutInflater
                               .from(parent.getContext());

                       int layout = 0;

                        if (viewType == 0) {
                               layout = R.layout.list_item_crime;
                        } else if (viewType == 1) {
                                layout = R.layout.list_item_serious_crime;
                        }

                 View view = layoutInflater.inflate(layout, parent, false);
                        return new CrimeHolder(view);

                }


                @Override
                public int getItemCount() {
                        return mCrimes.size();
                }


                public void setCrimes(List<Crime> crimes) {
                    mCrimes = crimes;
                }


                @Override
                public void onBindViewHolder( CrimeHolder holder, int position) {
                        Crime crime = mCrimes.get(position);
                        holder.bind(crime);
                        }


        }











        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
        {
            super.onCreateOptionsMenu(menu, inflater);
            inflater.inflate(R.menu.fragment_list_crime, menu);


            MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
            if (mSubtitleVisible) {
                subtitleItem.setTitle(R.string.hide_subtitle);

            } else {
                subtitleItem.setTitle(R.string.show_subtitle);
            }

        }


        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.new_crime:
                    Crime crime = new Crime();

            CrimeLab.get(getActivity()).addCrime(crime);
                    updateUI();
                    mCallbacks.onCrimeSelected(crime);
                    return true;

                case R.id.show_subtitle:
                    mSubtitleVisible = !mSubtitleVisible;
                    getActivity().invalidateOptionsMenu();
                    updateSubtitle();
                    return true;


                default:
                    return super.onOptionsItemSelected(item);
            }

        }


        private void updateSubtitle() {
            CrimeLab crimeLab = CrimeLab.get(getActivity());
            int crimeCount = crimeLab.getCrimes().size();
            String subtitle =
                    getString(R.string.subtitle_format, crimeCount);

            if (!mSubtitleVisible) {
                subtitle = null;
            }

            AppCompatActivity activity = (AppCompatActivity)
                    getActivity();

            activity.getSupportActionBar().setSubtitle(subtitle);
        }



        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode != Activity.RESULT_OK) {
                return;

            }
            if (requestCode == REQUEST_CRIME) {
                if (data == null) {
                    return;
                }



            }
        }

        @Override
    public void onSaveInstanceState(Bundle onSavedInstanceState) {
            super.onSaveInstanceState(onSavedInstanceState);
            onSavedInstanceState.putSerializable(CLICKED_ITEM_POSITOIN, itemPosition);
            onSavedInstanceState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
        }

        @Override
    public void onDetach() {
            super.onDetach();
            mCallbacks = null;
        }


}

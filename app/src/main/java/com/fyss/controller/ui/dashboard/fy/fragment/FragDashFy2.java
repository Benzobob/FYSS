package com.fyss.controller.ui.dashboard.fy.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fyss.R;
import com.fyss.controller.SyAddMeeting;
import com.fyss.controller.SyMeetingPageActivity;
import com.fyss.controller.ui.dashboard.adapter.MeetingsAdapter;
import com.fyss.model.GroupMeeting;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class FragDashFy2 extends Fragment {

    private ArrayList<GroupMeeting> meetingsList;
    private RecyclerView recyclerView;
    private MeetingsAdapter mAdapter;
    private Retrofit retrofit;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private MeetingsAdapter.RecyclerViewClickListener listener;
    private SessionManager sm;

    private OnFragmentInteractionListener mListener;

    public FragDashFy2() {
    }

    public static FragDashFy2 newInstance() {
        FragDashFy2 fragment = new FragDashFy2();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sm = new SessionManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View frag2 = inflater.inflate(R.layout.fragment_frag_dash_fy2, container, false);
        Button profileBtn = frag2.findViewById(R.id.addMeetingBtn);
        retrofit = RetrofitClientInstance.getRetrofitInstance();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        recyclerView = (RecyclerView) frag2.findViewById(R.id.recyclerView);

        listener = new MeetingsAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                GroupMeeting m = meetingsList.get(position);
                Toast.makeText(FragDashFy2.this.getContext(), "Week num " + m.getWeekNum(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SyMeetingPageActivity.class);
                intent.putExtra("meeting", m);
                startActivity(intent);

            }
        };



        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_ID) != null) {
            int id = Integer.parseInt(user.get(SessionManager.KEY_USER_ID));
            prepareMeetingsData(id);
        }

        return frag2;
    }


    private void prepareMeetingsData(int id) {
        //get user data from session
        Call<List<GroupMeeting>> call = jsonPlaceHolderApi.getMeetingsForFy(id);

        call.enqueue(new Callback<List<GroupMeeting>>() {
            @Override
            public void onResponse(Call<List<GroupMeeting>> call, Response<List<GroupMeeting>> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }

                meetingsList = new ArrayList<>(response.body());
                String k = "" + meetingsList.size();
                Toast.makeText(getActivity().getApplicationContext(), k, Toast.LENGTH_LONG).show();



                mAdapter = new MeetingsAdapter(meetingsList, listener);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);



            }

            @Override
            public void onFailure(Call<List<GroupMeeting>> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

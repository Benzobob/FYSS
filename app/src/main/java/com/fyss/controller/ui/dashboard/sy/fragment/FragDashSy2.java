package com.fyss.controller.ui.dashboard.sy.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.fyss.R;
import com.fyss.controller.SyAddMeeting;
import com.fyss.controller.SyMeetingPageActivity;
import com.fyss.controller.ui.dashboard.adapter.MeetingsAdapter;
import com.fyss.model.GroupMeeting;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.service.MyFirebaseMessagingService;
import com.fyss.session.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class FragDashSy2 extends Fragment {

    private ArrayList<GroupMeeting> meetingsList;
    private RecyclerView recyclerView;
    private MeetingsAdapter mAdapter;
    private Retrofit retrofit;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private MeetingsAdapter.RecyclerViewClickListener listener;
    private SessionManager sm;

    private OnFragmentInteractionListener mListener;

    public FragDashSy2() {
        // Required empty public constructor
    }

    public static FragDashSy2 newInstance() {
        FragDashSy2 fragment = new FragDashSy2();
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

        final View frag2 = inflater.inflate(R.layout.fragment_frag_dash_sy2, container, false);
        Button addMeetingBtn = frag2.findViewById(R.id.addMeetingBtn);
        retrofit = RetrofitClientInstance.getRetrofitInstance();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        recyclerView = (RecyclerView) frag2.findViewById(R.id.recyclerView);

        listener = new MeetingsAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                GroupMeeting m = meetingsList.get(position);
               // Toast.makeText(FragDashSy2.this.getContext(), "Week num " + m.getWeekNum(), Toast.LENGTH_SHORT).show();
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


        addMeetingBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), SyAddMeeting.class);
                startActivity(intent);
            }
        });

        return frag2;
    }


    private void prepareMeetingsData(int id) {
        //get user data from session
        Call<List<GroupMeeting>> call = jsonPlaceHolderApi.getMeetingsForSy(id);

        call.enqueue(new Callback<List<GroupMeeting>>() {
            @Override
            public void onResponse(Call<List<GroupMeeting>> call, Response<List<GroupMeeting>> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    return;
                }

                meetingsList = new ArrayList<>(response.body());
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

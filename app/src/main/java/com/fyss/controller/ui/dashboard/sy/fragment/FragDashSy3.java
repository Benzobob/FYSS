package com.fyss.controller.ui.dashboard.sy.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.fyss.R;
import com.fyss.controller.FyProfileActivity;
import com.fyss.controller.SyMeetingPageActivity;
import com.fyss.controller.ui.dashboard.adapter.MeetingsAdapter;
import com.fyss.controller.ui.dashboard.adapter.MembersAdapter;
import com.fyss.model.FyUser;
import com.fyss.model.GroupMeeting;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.session.SessionManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragDashSy3.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragDashSy3#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragDashSy3 extends Fragment {

    private ArrayList<FyUser> membersList;
    private RecyclerView recyclerView;
    private MembersAdapter mAdapter;
    private Retrofit retrofit;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private MembersAdapter.RecyclerViewClickListener listener;
    private SessionManager sm;

    private OnFragmentInteractionListener mListener;

    public FragDashSy3() {
    }

    public static FragDashSy3 newInstance() {
        FragDashSy3 fragment = new FragDashSy3();
        Bundle args = new Bundle();
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
        // Inflate the layout for this fragment
        final View frag3 = inflater.inflate(R.layout.fragment_frag_dash_sy3, container, false);
        retrofit = RetrofitClientInstance.getRetrofitInstance();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        recyclerView = (RecyclerView) frag3.findViewById(R.id.recyclerView1);


        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_ID) != null) {
            getGroupId(Integer.parseInt(user.get(SessionManager.KEY_USER_ID)));
        }


        listener = new MembersAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {

                FyUser m = membersList.get(position);
                Intent intent = new Intent(getActivity(), FyProfileActivity.class);
                intent.putExtra("FyId", m.getFyid().toString());
                startActivity(intent);

            }
        };


        return frag3;
    }

    private void getGroupId(int id) {
        Call<ResponseBody> call = jsonPlaceHolderApi.getGroupId(id);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    String gid = null;
                    try {
                        gid = response.body().string();
                        prepareMembersData(Integer.parseInt(gid));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
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


    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void prepareMembersData(int gid) {
        //get user data from session
        Call<List<FyUser>> call = jsonPlaceHolderApi.getGroupMembers(gid);

        call.enqueue(new Callback<List<FyUser>>() {
            @Override
            public void onResponse(Call<List<FyUser>> call, Response<List<FyUser>> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    return;
                }

                membersList = new ArrayList<>(response.body());
                mAdapter = new MembersAdapter(membersList, listener);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onFailure(Call<List<FyUser>> call, Throwable t) {
                Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}

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
import com.fyss.controller.LoginActivity;
import com.fyss.controller.SyAddMeeting;
import com.fyss.controller.SyAddPostActivity;
import com.fyss.controller.SyDashboardActivity;
import com.fyss.controller.SyEditPostActivity;
import com.fyss.controller.SyMeetingPageActivity;
import com.fyss.controller.ui.dashboard.adapter.MeetingsAdapter;
import com.fyss.controller.ui.dashboard.adapter.PostsAdapter;
import com.fyss.model.GroupMeeting;
import com.fyss.model.Posts;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.service.MyFirebaseMessagingService;
import com.fyss.session.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragDashSy1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragDashSy1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragDashSy1 extends Fragment {

    private OnFragmentInteractionListener mListener;
    private String fcmToken;
    private Button addPostBtn;
    private ArrayList<Posts> posts;
    private RecyclerView recyclerView;
    private PostsAdapter mAdapter;
    private Retrofit retrofit;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private PostsAdapter.RecyclerViewClickListener listener;
    private SessionManager sm;
    private int syid;

    public FragDashSy1() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragDashSy1 newInstance() {
        FragDashSy1 fragment = new FragDashSy1();
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
        // Inflate the layout for this fragment

        final View frag1 = inflater.inflate(R.layout.fragment_frag_dash_sy1, container, false);

        retrofit = RetrofitClientInstance.getRetrofitInstance();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        recyclerView = (RecyclerView) frag1.findViewById(R.id.recyclerViewPost);


        listener = new PostsAdapter.RecyclerViewClickListener() {
            @Override
            public void onClick(View view, int position) {
                Posts post = posts.get(position);
                Intent intent = new Intent(getActivity(), SyEditPostActivity.class);
                intent.putExtra("post", post);
                startActivity(intent);
            }
        };


        HashMap<String, String> user = sm.getUserDetails();
        if (user.get(SessionManager.KEY_USER_ID) != null) {
            syid = Integer.parseInt(user.get(SessionManager.KEY_USER_ID));
        }

        addPostBtn = frag1.findViewById(R.id.addPostBtn);

        addPostBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(getActivity(), SyAddPostActivity.class);
                startActivity(intent);
            }
        });
        preparePosts();
        return frag1;

    }

    private void preparePosts() {
        Call<List<Posts>> call = jsonPlaceHolderApi.getPostsSy(syid);

        call.enqueue(new Callback<List<Posts>>() {
            @Override
            public void onResponse(Call<List<Posts>> call, Response<List<Posts>> response) {
                if (!response.isSuccessful()) {
                    String result = "Code: " + response.code();
                    return;
                }

                posts = new ArrayList<>(response.body());
                mAdapter = new PostsAdapter(posts, listener);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
                recyclerView.setLayoutManager(mLayoutManager);
                recyclerView.setItemAnimator(new DefaultItemAnimator());
                recyclerView.setAdapter(mAdapter);

            }

            @Override
            public void onFailure(Call<List<Posts>> call, Throwable t) {
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

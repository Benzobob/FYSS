package com.fyss.controller.ui.dashboard.fy.fragment;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.fyss.R;
import com.fyss.controller.LoginActivity;
import com.fyss.controller.ui.dashboard.adapter.PostsAdapter;
import com.fyss.controller.ui.dashboard.sy.fragment.FragDashSy1;
import com.fyss.model.FyUser;
import com.fyss.model.Posts;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.service.MyFirebaseMessagingService;
import com.fyss.session.SessionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragDashFy1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragDashFy1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragDashFy1 extends Fragment{

    private SwipeRefreshLayout swipeRefreshLayout;
    private SessionManager session;
    private FragDashFy1.OnFragmentInteractionListener mListener;
    private String fcmToken;
    private ArrayList<Posts> posts;
    private RecyclerView recyclerView;
    private PostsAdapter mAdapter;
    private Retrofit retrofit;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private PostsAdapter.RecyclerViewClickListener listener;
    private FyUser user;
    private int fyid;

    public FragDashFy1() {
    }


    public static FragDashFy1 newInstance() {
        FragDashFy1 fragment = new FragDashFy1();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        session = new SessionManager(getActivity().getApplicationContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View frag1 = inflater.inflate(R.layout.fragment_frag_dash_fy1, container, false);

        MyFirebaseMessagingService m = new MyFirebaseMessagingService();
        fcmToken = m.getToken(getActivity().getApplicationContext());
        retrofit = RetrofitClientInstance.getRetrofitInstance();
        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        recyclerView = (RecyclerView) frag1.findViewById(R.id.recyclerViewPosts);

        HashMap<String, String> user = session.getUserDetails();
        if (user.get(SessionManager.KEY_USER_ID) != null) {
            fyid = Integer.parseInt(user.get(SessionManager.KEY_USER_ID));
        }

        swipeRefreshLayout = getActivity().findViewById(R.id.pullToRefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                preparePosts();
            }
        });
        setUser();

        return frag1;

    }


    private void setUser() {
            Call<FyUser> call = jsonPlaceHolderApi.findFyUserById(fyid);
            call.enqueue(new Callback<FyUser>() {
                @Override
                public void onResponse(Call<FyUser> call, Response<FyUser> response) {
                    if (!response.isSuccessful()) {
                        String result = "Cod: " + response.code();
                        Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                    } else {
                        user = response.body();
                        preparePosts();
                    }
                }
                @Override
                public void onFailure(Call<FyUser> call, Throwable t) {
                    Toast.makeText(getActivity().getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void preparePosts() {
        Call<List<Posts>> call = jsonPlaceHolderApi.getPostsGroup(user.getGid().getGid());

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

    private void removeFcmToken(String fcmToken) {
        Retrofit retrofit = RetrofitClientInstance.getRetrofitInstance();
        final JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        Call<Void> call = jsonPlaceHolderApi.removeFcmToken(fcmToken);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Log.i("TAG", "success");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("TAG", "=======onFailure: " + t.toString());
                t.printStackTrace();

            }
        });
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

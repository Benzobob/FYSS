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

import androidx.fragment.app.Fragment;

import com.fyss.R;
import com.fyss.controller.LoginActivity;
import com.fyss.controller.ui.dashboard.sy.fragment.FragDashSy1;
import com.fyss.network.JsonPlaceHolderApi;
import com.fyss.network.RetrofitClientInstance;
import com.fyss.service.MyFirebaseMessagingService;
import com.fyss.session.SessionManager;

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
public class FragDashFy1 extends Fragment {

    private SessionManager session;
    private FragDashFy1.OnFragmentInteractionListener mListener;
    private String fcmToken;

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
        Button logout = frag1.findViewById(R.id.button6);

        MyFirebaseMessagingService m = new MyFirebaseMessagingService();
        fcmToken = m.getToken(getActivity().getApplicationContext());


        logout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                session.logoutUser();
                removeFcmToken(fcmToken);
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        return frag1;

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

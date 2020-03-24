package com.fyss.controller.ui.dashboard.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;
import static com.fyss.service.Const.PREFS_NAME;
import static com.fyss.service.Const.PREF_DARK_THEME;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.fyss.R;
import com.fyss.model.FyUser;
import com.fyss.service.Cache;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.MyViewHolder>{

    private ArrayList<FyUser> membersList;
    private RecyclerViewClickListener mListener;
    private LruCache<String, Bitmap> memoryCache;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Bitmap bitmap;
    private Context context;


    public MembersAdapter(ArrayList<FyUser> membersList, RecyclerViewClickListener listener) {
        this.membersList = membersList;
        this.mListener = listener;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView nameTxt, emailTxt;
        private RecyclerViewClickListener mListener;
        private ConstraintLayout constInner;
        private de.hdodenhof.circleimageview.CircleImageView profPic;


        public MyViewHolder(View view, RecyclerViewClickListener listener) {
            super(view);
            mListener = listener;
            view.setOnClickListener(this);
            nameTxt =  view.findViewById(R.id.nameItem);
            constInner = view.findViewById(R.id.innerConst);
            emailTxt = view.findViewById(R.id.emailTextView);
            profPic = view.findViewById(R.id.imageView5);

            storage = FirebaseStorage.getInstance();
            storageReference = storage.getReference();

            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;

            memoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // The cache size will be measured in kilobytes rather than
                    // number of items.
                    return bitmap.getByteCount() / 1024;
                }
            };
        }

        @Override
        public void onClick(View view) {
            mListener.onClick(view, getAdapterPosition());
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();

        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);

        if(useDarkTheme) {
            parent.getContext().setTheme(R.style.AppTheme_DarkTheme_NoActionBar);
        }else{
            parent.getContext().setTheme(R.style.AppTheme_LightTheme_NoActionBar);
        }

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.members_items, parent, false);

        return new MyViewHolder(itemView, mListener);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        FyUser user = membersList.get(position);

        if (membersList.get(position).getProfileImg() != null) {
            if (getBitmapFromMemCache(membersList.get(position).getProfileImg()) == null) {

                setPic(holder, membersList.get(position));

            }
            else{
                holder.profPic.setImageBitmap(getBitmapFromMemCache(membersList.get(position).getProfileImg()));
            }
        }



        String name = user.getFirstname() + " " + user.getSurname();

        holder.nameTxt.setText(name);
        holder.emailTxt.setText(user.getEmail());


        if(position %2 == 1)
        {
            holder.constInner.setBackgroundColor(Color.parseColor("#EDE7F6"));
        }

    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        Cache.getInstance().getLru().put(key, bitmap);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return (Bitmap) Cache.getInstance().getLru().get(key);
    }



    private void setPic(final MyViewHolder holder, final FyUser user){

        StorageReference imgRef = storageReference.child("images/" + user.getProfileImg());
        try {
            final File localFile = File.createTempFile("Images", "bmp");
            imgRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                    addBitmapToMemoryCache(user.getProfileImg(), bitmap);
                    holder.profPic.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("Failure", e.getLocalizedMessage());
                }
            });
        }catch(IOException e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }


    public interface RecyclerViewClickListener {

        void onClick(View view, int position);
    }
}

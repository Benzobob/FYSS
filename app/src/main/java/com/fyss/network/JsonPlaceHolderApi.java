package com.fyss.network;
import com.fyss.model.FyUser;
import com.fyss.model.Group;
import com.fyss.model.GroupMeeting;
import com.fyss.model.SyUser;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface JsonPlaceHolderApi {

   @POST("fyss.usersfy")
   Call<Void> create(@Body FyUser user);

   @POST("fyss.userssy")
   Call<Void> create(@Body SyUser user);

   @POST("fyss.groupmeetings")
   Call<Void> create(@Body GroupMeeting meeting);

   @GET("fyss.userssy/{id}")
   Call<SyUser> findSyUserById(@Path("id") int id);

   @GET("fyss.groups/{id}")
   Call<Group> findGroupById(@Path("id") int id);


}

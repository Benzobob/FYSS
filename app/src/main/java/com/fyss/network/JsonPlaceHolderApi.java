package com.fyss.network;
import com.fyss.model.Attendance;
import com.fyss.model.FCM_Token;
import com.fyss.model.FyUser;
import com.fyss.model.Group;
import com.fyss.model.GroupMeeting;
import com.fyss.model.Posts;
import com.fyss.model.SyUser;
import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface JsonPlaceHolderApi {

   @POST("fyss.usersfy")
   Call<Void> create(@Body FyUser user);

   @POST("fyss.userssy")
   Call<Void> create(@Body SyUser user);

   @POST("fyss.fcmtokens")
   Call<Void> create(@Body FCM_Token token);

   @POST("fyss.posts")
   Call<Void> create(@Body Posts post);

   @PUT("fyss.attendance/edit/{id}")
   Call<Void> editAttendance(@Path("id") int id, @Body Attendance attendance);

   @POST("fyss.groupmeetings")
   Call<Void> create(@Body GroupMeeting meeting);

   @FormUrlEncoded
   @POST("fyss.usersfy/auth")
   Call<ResponseBody> loginFy(@Field("email") String email, @Field("password") String password, @Field("fcmToken") String token);

   @FormUrlEncoded
   @POST("fyss.userssy/auth")
   Call<ResponseBody> loginSy(@Field("email") String email, @Field("password") String password, @Field("fcmToken") String token);

   @GET("fyss.userssy/getSyUser/{id}")
   Call<SyUser> findSyUserById(@Path("id") int id);

   @GET("fyss.usersfy/getFyUser/{id}")
   Call<FyUser> findFyUserById(@Path("id") int id);

   @DELETE("fyss.fcmtokens/{id}")
   Call<Void> removeFcmToken(@Path("id") String token);

   @DELETE("fyss.groupmeetings/{id}")
   Call<Void> removeMeeting(@Path("id") String id);

   @GET("fyss.attendance/remove/{gmid}")
   Call<Void> removeAttendance(@Path("gmid") int gmid);

   @GET("fyss.groups/getGroup/{gid}")
   Call<Group> findGroupById(@Path("gid") int gid);

   @GET("fyss.groupmeetings/syid/{syid}")
   Call<List<GroupMeeting>> getMeetingsForSy(@Path("syid") int syid);

   @GET("fyss.groupmeetings/fyid/{fyid}")
   Call<List<GroupMeeting>> getMeetingsForFy(@Path("fyid") int fyid);

   @GET("fyss.groups/syid/{syid}")
   Call<ResponseBody> getGroupId(@Path("syid") int syid);

   @GET("fyss.usersfy/members/{gid}")
   Call<List<FyUser>> getGroupMembers(@Path("gid") int gid);

   @GET("fyss.fcmtokens/groups/{syid}")
   Call<List<FCM_Token>> getFcmTokensForGroup(@Path("syid") int syid);

   @PUT("fyss.groupmeetings/{id}")
   Call<Void> editGroupMeeting(@Path("id") int id, @Body GroupMeeting meeting);

   @GET("fyss.attendance/checkDuplicate/{id}/{gmid}")
   Call<String> checkDuplicate(@Path("id") int id, @Path("gmid") int gmid);

   @GET("fyss.attendance/getAttendance/{id}")
   Call<Attendance> getAttendance(@Path("id") int id);

   @GET("fyss.usersfy/getRemainingStudents/{gmid}")
   Call<List<FyUser>> getRemainingStudents(@Path("gmid") int gmid);

   @GET("fyss.groups/getGroupSy/{syid}")
   Call<Group> getGroupSy(@Path("syid") int syid);

   @GET("fyss.posts/getPostsSy/{syid}")
   Call<List<Posts>> getPostsSy(@Path("syid") int syid);

   @GET("fyss.posts/getPostsByGroup/{gid}")
   Call<List<Posts>> getPostsGroup(@Path("gid") int gid);

   @PUT("fyss.usersfy/{id}")
   Call<Void> editFyUser(@Path("id") int id, @Body FyUser user);
}

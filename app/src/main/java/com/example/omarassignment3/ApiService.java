package com.example.omarassignment3;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("login") // Replace with your actual login endpoint
    Call<ResponseBody> login(
            @Field("username") String username,
            @Field("password") String password
    );
    @GET("/checkfriendship")
    Call<CheckFriendShipResponse> checkfriendship( @Query("user1") String user1,
                                                   @Query("user2") String user2);
    @POST("/addfriend")
    Call<FriendResponse> addFriend(@Body FriendRequest friendRequest);
    @GET("/test") // Replace with your actual test endpoint path
    Call<ResponseBody> testConnection();
    // Example with @GET and @Query
    @GET("/pic")
    Call<PicResponse> getPic(@Query("email") String email);
    @GET("/bio")
    Call<BioResponse> getBio(@Query("email") String email);
    @GET("/profile")
    Call<ProfileResponse> getProfile(@Query("email") String email);
    @POST("/removefriend")
    Call<RemoveFriendResponse> removeFriend(@Body RemoveFriendRequest request);
    @POST("/changePassword")
    Call<ChangePasswordResponse> changePassword(@Body ChangePasswordRequest request);
    @POST("/updatebio")
    Call<UpdateResponse> updateBio(@Body UpdateRequest request);

    @POST("/updateprofilepic")
    Call<UpdateResponse> updateProfilePicURL(@Body UpdateRequest request);
    @POST("/uploadPicture")
    Call<UploadPictureResponse> uploadPicture(@Body UploadPictureRequest request);
    @POST("/addSport")
    Call<AddSportResponse> addSport(@Body AddSportRequest addSportRequest);
    @POST("/send-match-request")
    Call<SendMatchResponse> sendMatchRequest(@Body SendMatchRequest request);
}

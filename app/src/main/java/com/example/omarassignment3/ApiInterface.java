package com.example.omarassignment3;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @POST("/addfriend")
    Call<FriendResponse> addFriend(@Body FriendRequest friendRequest);
    @GET("/bio")
    Call<BioResponse> getBio(@Body BioRequest bioRequest);
    @GET("/pic")
    Call<PicResponse> getPic(@Body PicRequest picRequest);
    @GET("/checkfriendship")
    Call<CheckFriendShipResponse> checkfriendship(@Body CheckFriendshipRequest checkFriendshipRequest);
    @GET("/profile")
    Call<ProfileResponse> getProfile(@Body ProfileRequest profileRequest);
    @POST("/addSport")
    Call<AddSportResponse> addSport(@Body AddSportRequest addSportRequest);
    @POST("/send-match-request")
    Call<SendMatchResponse> sendMatchRequest(@Body SendMatchRequest request);
}

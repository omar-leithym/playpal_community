package com.example.omarassignment3;

import com.google.gson.annotations.SerializedName;

public class RemoveFriendResponse {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private FriendData data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public FriendData getData() {
        return data;
    }

    public void setData(FriendData data) {
        this.data = data;
    }

    public static class FriendData {
        @SerializedName("user_1")
        private String user1;

        @SerializedName("user_2")
        private String user2;

        public String getUser1() {
            return user1;
        }

        public void setUser1(String user1) {
            this.user1 = user1;
        }

        public String getUser2() {
            return user2;
        }

        public void setUser2(String user2) {
            this.user2 = user2;
        }
    }
}

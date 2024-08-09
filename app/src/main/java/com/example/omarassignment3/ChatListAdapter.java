package com.example.omarassignment3;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ChatListAdapter extends BaseAdapter {

    private Context context;
    private List<Friend> friends;

    public ChatListAdapter(Context context, List<Friend> friends) {
        this.context = context;
        this.friends = friends;
    }

    @Override
    public int getCount() {
        return friends.size();
    }

    @Override
    public Friend getItem(int position) {
        return friends.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            // convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_friend, parent, false);
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        }

        Friend friend = getItem(position);
        Log.e(TAG,"friend: "+friend.getName());
        TextView friendName = convertView.findViewById(R.id.textViewName);
        ImageView friendProfilePic = convertView.findViewById(R.id.imageViewProfilePicture);

        friendName.setText(friend.getName());

        String profilePicUrl = friend.getProfilePicUrl();
        Log.e(TAG,"profilepic: "+profilePicUrl);
        if (profilePicUrl != null) { //&& !profilePicUrl.isEmpty()
            //Picasso.get().load(profilePicUrl).placeholder(R.drawable.default_profile_pic).into(friendProfilePic);

            byte[] bytes= Base64.decode(profilePicUrl,Base64.DEFAULT);
            // Initialize bitmap
            Bitmap bitmap= BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            // set bitmap on imageView
            friendProfilePic.setImageBitmap(bitmap);

        } else {
            friendProfilePic.setImageResource(R.drawable.default_profile_pic);
        }

        return convertView;
    }
}

package com.example.omarassignment3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_DATE_SEPARATOR = 3;

    private List<ChatMessage> chatMessages;
    private String user1Email;

    public ChatAdapter(List<ChatMessage> chatMessages, String user1Email) {
        this.chatMessages = chatMessages;
        this.user1Email = user1Email;
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        if (message.isDateSeparator()) {
            return VIEW_TYPE_DATE_SEPARATOR;
        } else if (message.getSenderEmail().equals(user1Email)) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else if (viewType == VIEW_TYPE_RECEIVED) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_separator, parent, false);
            return new DateSeparatorViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else if (holder.getItemViewType() == VIEW_TYPE_RECEIVED) {
            ((ReceivedMessageViewHolder) holder).bind(message);
        } else {
            ((DateSeparatorViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.right_chat_textview);
            timestampTextView = itemView.findViewById(R.id.right_chat_timestamp);
        }

        void bind(ChatMessage message) {
            messageTextView.setText(message.getMessage());
            timestampTextView.setText(formatTime(message.getTimestamp()));
        }
    }

    class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timestampTextView;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.left_chat_textview);
            timestampTextView = itemView.findViewById(R.id.left_chat_timestamp);
        }

        void bind(ChatMessage message) {
            messageTextView.setText(message.getMessage());
            timestampTextView.setText(formatTime(message.getTimestamp()));
        }
    }

    class DateSeparatorViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        DateSeparatorViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_textview);
        }

        void bind(ChatMessage message) {
            dateTextView.setText(message.getDate());
        }
    }

    private String formatTime(String timestamp) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
            Date date = inputFormat.parse(timestamp);

            SimpleDateFormat outputFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}

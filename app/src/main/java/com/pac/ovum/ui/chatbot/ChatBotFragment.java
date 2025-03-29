package com.pac.ovum.ui.chatbot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.pac.ovum.databinding.FragmentChatBotBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatBotFragment extends Fragment {

    private FragmentChatBotBinding binding;

    private ChatBotViewModel mViewModel;

    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText messageEditText;
    private ImageButton sendButton;
    private Chip cycleQuestion, symptomsQuestion, fertilityQuestion;

    public static ChatBotFragment newInstance() {
        return new ChatBotFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBotBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        initViews();

        // Initialize chat messages
        chatMessages = new ArrayList<>();

        // Add welcome message
        chatMessages.add(new ChatMessage(
                "Hello! I'm your Ovum assistant. How can I help you today?",
                getCurrentTime(),
                false
        ));

        // Set up RecyclerView
        chatAdapter = new ChatAdapter(chatMessages);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatRecyclerView.setAdapter(chatAdapter);

        // Set up send button click listener
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        // Set up suggested questions click listeners
        cycleQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSuggestedQuestion("I want to know about my cycle right now");
            }
        });

        symptomsQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSuggestedQuestion("Why am I experiencing these symptoms?");
            }
        });

        fertilityQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleSuggestedQuestion("When is my fertile window?");
            }
        });

        return root;
    }

    private void initViews(){
        // Initialize views
        chatRecyclerView = binding.chatRecyclerView;
        messageEditText = binding.messageEditText;
        sendButton = binding.sendButton;
        cycleQuestion = binding.cycleQuestion;
        symptomsQuestion = binding.symptomsQuestion;
        fertilityQuestion = binding.fertilityQuestion;
    }

    private void sendMessage() {
        String messageText = messageEditText.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Add user message
            chatMessages.add(new ChatMessage(
                    messageText,
                    getCurrentTime(),
                    true
            ));
            messageEditText.setText("");

            // Update RecyclerView
            int newPosition = chatMessages.size() - 1;
            chatAdapter.notifyItemInserted(newPosition);
            chatRecyclerView.scrollToPosition(newPosition);

            // Here you would normally call your chatbot API
            // For now, we'll just simulate a response
            simulateBotResponse(messageText);
        }
    }

    private void handleSuggestedQuestion(String question) {
        // Add the suggested question as a user message
        chatMessages.add(new ChatMessage(
                question,
                getCurrentTime(),
                true
        ));

        // Update RecyclerView
        int newPosition = chatMessages.size() - 1;
        chatAdapter.notifyItemInserted(newPosition);
        chatRecyclerView.scrollToPosition(newPosition);

        // Simulate bot response
        simulateBotResponse(question);
    }

    private void simulateBotResponse(String userMessage) {
        // This is a placeholder for your actual chatbot integration
        // For now, we'll just add a dummy response after a short delay

        new android.os.Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        String botResponse;
                        if (userMessage.toLowerCase().contains("cycle")) {
                            botResponse = "Based on your data, you're currently on day 14 of your cycle. Your next period is expected in about 14 days.";
                        } else if (userMessage.toLowerCase().contains("symptom")) {
                            botResponse = "The symptoms you're describing could be related to your hormonal fluctuations. Would you like me to explain more about how hormones affect your body during your cycle?";
                        } else if (userMessage.toLowerCase().contains("fertile")) {
                            botResponse = "Based on your logged data, your fertile window is likely between March 12-17. Would you like me to provide more detailed fertility information?";
                        } else {
                            botResponse = "Thank you for your message. How else can I assist you with your health questions today?";
                        }

                        // Add bot response
                        chatMessages.add(new ChatMessage(
                                botResponse,
                                getCurrentTime(),
                                false
                        ));

                        // Update RecyclerView
                        int newPosition = chatMessages.size() - 1;
                        chatAdapter.notifyItemInserted(newPosition);
                        chatRecyclerView.scrollToPosition(newPosition);
                    }
                },
                1000 // 1 second delay to simulate thinking
        );
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return sdf.format(new Date());
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChatBotViewModel.class);
        // TODO: Use the ViewModel
    }

}
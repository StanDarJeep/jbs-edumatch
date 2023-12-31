package com.example.edumatch.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.edumatch.R;

public class CustomChatInputView extends RelativeLayout {

    private EditText editText;
    private Button sendButton;

    // ChatGPT usage: Yes
    public CustomChatInputView(Context context) {
        super(context);
        init(context);
    }

    // ChatGPT usage: Yes
    public CustomChatInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // ChatGPT usage: Yes
    public CustomChatInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    // ChatGPT usage: Yes
    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.chat_input_component, this, true);

        // Find views by their IDs
        editText = findViewById(R.id.edit_text);
        sendButton = findViewById(R.id.send_button);
    }

    // ChatGPT usage: Yes
    public EditText getEditText() {
        return editText;
    }

    // ChatGPT usage: Yes
    public Button getSendButton() {
        return sendButton;
    }
}

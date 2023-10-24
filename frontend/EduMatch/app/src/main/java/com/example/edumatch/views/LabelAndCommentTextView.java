
package com.example.edumatch.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;

import com.example.edumatch.R;
import com.example.edumatch.util.LoginSignupHelper;

public class LabelAndCommentTextView extends GridLayout {

    private TextView label;
    private EditText editText;
    private TextView content;

    public LabelAndCommentTextView(Context context) {
        super(context);
        init(context, null);
    }

    public LabelAndCommentTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LabelAndCommentTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public EditText getEditText() {
        return editText;
    }

    public TextView getContentText() {
        return content;
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.label_and_comment_text, this, true);

        label = findViewById(R.id.label);
        content = findViewById(R.id.content);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LabelAndTextView);

            String labelText = typedArray.getString(R.styleable.LabelAndTextView_label);
            String contentText = typedArray.getString(R.styleable.LabelAndTextView_contentText);

            typedArray.recycle();

            if (labelText != null) {
                label.setText(labelText);
            }

            if (contentText != null) {
                content.setText(contentText);
            }
        }
    }
}
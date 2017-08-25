package app.youkai.placeholdertextview.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import app.youkai.placeholdertextview.PlaceholderTextView;

public class MainActivity extends AppCompatActivity {

    private int originalPlaceholderColor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final PlaceholderTextView placeholder = (PlaceholderTextView) findViewById(R.id.placeholderText);
        final TextView plainText = (TextView) findViewById(R.id.plainText);
        final EditText inputText = (EditText) findViewById(R.id.inputText);
        final EditText inputPlaceholder = (EditText) findViewById(R.id.inputPlaceholder);
        Button submitText = (Button) findViewById(R.id.submitText);
        Button submitPlaceholder = (Button) findViewById(R.id.submitPlaceholder);

        submitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeholder.setText(inputText.getText());
                originalPlaceholderColor = placeholder.getPlaceholderColor();
                placeholder.setPlaceholderColor(Color.GREEN);
            }
        });

        submitPlaceholder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeholder.setSampleText(inputPlaceholder.getText().toString());
                plainText.setText(inputPlaceholder.getText());
                placeholder.setPlaceholderColor(originalPlaceholderColor);
            }
        });
    }
}

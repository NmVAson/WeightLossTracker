package edu.rosehulman.weightlosstracker;

import java.util.Random;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RandomInspiration extends Activity implements OnClickListener{

	private String[] mQuoteList = {"If it's to be, it's up to me!","Take care of your body. It's the only place you have to live!","The best revenge on a gain is an even biffer loss. Vengence can feel good!","Persevere and you will win the prize.","It comes down to a simple question: what do you want out of life, and what are you willing to do to get it?","Rather than aiming for being perfect, just aim to be little bit better today than you were yesterday."};
	private TextView mQuoteView;
	private Button mNewButton;
	private Button mCloseButton;
	private String randomQuote;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inspire);
        
        this.mQuoteView = (TextView) findViewById(R.id.inspire_textview);
        this.mNewButton = (Button) findViewById(R.id.inspire_new_button);
        this.mNewButton.setOnClickListener(this);
        this.mCloseButton = (Button) findViewById(R.id.inspire_close_button);
        this.mCloseButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.inspire_new_button:
			Resources res = getResources();
			Random r = new Random();
			this.randomQuote = res.getString(R.string.inspire_quote, this.mQuoteList[r.nextInt(this.mQuoteList.length)]);
			this.mQuoteView.setText(this.randomQuote);
			break;
		case R.id.inspire_close_button:
			finish();
			break;
		}
		
	}

}

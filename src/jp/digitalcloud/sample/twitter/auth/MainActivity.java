package jp.digitalcloud.sample.twitter.auth;

import sample.twitter.tweet.TimeLine;
import sample.twitter.tweet.TweetActivity;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private Twitter mTwitter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Button buttonAuth = (Button) findViewById(R.id.buttonAuth);
		buttonAuth.setOnClickListener(this);
		Button buttonSendPin = (Button) findViewById(R.id.buttonSendPin);
		buttonSendPin.setOnClickListener(this);
		Button buttonShowTweet = (Button) findViewById(R.id.button_showTimeline);
		buttonShowTweet.setOnClickListener(this);
		Button buttonTweet = (Button) findViewById(R.id.button_tweet);
		buttonTweet.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent ;
		switch (v.getId()) {
		case R.id.buttonAuth:
			mTwitter = new TwitterFactory().getInstance();
			mTwitter.setOAuthConsumer(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_key_secret));
			mTwitter.setOAuthAccessToken(null);
			LoaderManager.LoaderCallbacks<RequestToken> requestTokenCallbacks = new TwitterOAuthRequestTokenCallbacks(this, mTwitter);
			getSupportLoaderManager().initLoader(0, null, requestTokenCallbacks);
			break;
		case R.id.buttonSendPin:
			EditText editTextPin = (EditText) findViewById(R.id.editTextPin);
			if (editTextPin.getText().length() > 0 && mTwitter != null) {
				LoaderManager.LoaderCallbacks<AccessToken> accessTokenCallbacks = new TwitterOAuthAccessTokenCallbacks(this, mTwitter, editTextPin.getText().toString());
				getSupportLoaderManager().initLoader(1, null, accessTokenCallbacks);
			}
			break;
		case R.id.button_showTimeline:
			intent = new Intent(MainActivity.this,TimeLine.class);
			startActivity(intent);
			break;
		case R.id.button_tweet:
			intent = new Intent(MainActivity.this,TweetActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}

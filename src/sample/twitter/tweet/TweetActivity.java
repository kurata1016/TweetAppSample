package sample.twitter.tweet;

import jp.digitalcloud.sample.twitter.auth.R;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TweetActivity extends Activity {
	// twitterオブジェクト
	Twitter tw;
	// AccessTokenオブジェクト
	AccessToken at;
	// AccessToken
	String AccessToken;
	// AccessTokenSecret
	String AccessTokenSecret;
	// ボタンタグ
	final String BTN_BACK = "btn_back";
	final String BTN_TWEET = "btn_tweet";
	// 非同期タスク
	AsyncTask<String, Void, Boolean> task;
	// ツイート内容初期化
	EditText et = null;

	// onCreateメソッド(画面初期表示イベント)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// レイアウト設定ファイル指定
		setContentView(jp.digitalcloud.sample.twitter.auth.R.layout.tweet);

		// ボタンオブジェクト取得
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setTag(BTN_BACK);
		btn_back.setOnClickListener(onBtnClickListener);
		Button btn_tweet = (Button) findViewById(R.id.btn_tweet);
		btn_tweet.setTag(BTN_TWEET);
		btn_tweet.setOnClickListener(onBtnClickListener);
		
		// editText取得
		et = (EditText) findViewById(R.id.et_tweet);
		// editTextにイベントリスナー登録
		et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				//　入力可能文字数表示TextView取得
				TextView charcounts = (TextView) findViewById(R.id.tv_charscount);
				// 入力可能文字数設定
				int count = 140 - s.length();
				charcounts.setText(String.valueOf(count));
				// 140文字以上入力されたらトースト表示
				if(count < 0){
					Toast.makeText(TweetActivity.this, "ツイートは140文字までです", Toast.LENGTH_SHORT).show();;
				}
			}
		});

		// preferenceからAccessToken取得
		SharedPreferences pref = getSharedPreferences(this.getString(R.string.shared_pref_name), MODE_PRIVATE);
		AccessToken = pref.getString(getString(R.string.shared_pref_key_twitter_access_token), "");
		AccessTokenSecret = pref.getString(getString(R.string.shared_pref_key_twitter_access_token_secret), "");
		// twitterオブジェクト取得
		tw = new TwitterFactory().getInstance();
		// AccessTokenオブジェクト取得
		at = new AccessToken(AccessToken, AccessTokenSecret);
		// APIkey と APIkeysecretの設定
		tw.setOAuthConsumer(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_key_secret));
		// AccessTokenの設定
		tw.setOAuthAccessToken(at);

		// ネットワーク通信は非同期処理
		task = new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				// ツイート内容取得
				et = (EditText) findViewById(R.id.et_tweet);
				// ツイート処理
				try {
					tw.updateStatus(et.getText().toString());
					return true;
				} catch (TwitterException e) {
					e.printStackTrace();
					return false;
				}
			}

			@Override
			protected void onPostExecute(Boolean tweetResult) {
				if (tweetResult) {
					Toast.makeText(TweetActivity.this, "ツイートしました！", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(TweetActivity.this, "ツイートできませんでした", Toast.LENGTH_SHORT).show();
				}
			}
		};
	}

	private OnClickListener onBtnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// ボタンオブジェクト取得
			Button button = (Button) v;
			// ボタン判別
			switch (button.getTag().toString()) {
			case BTN_BACK:
				finish();
				break;
			case BTN_TWEET:
				task.execute();
				break;
			}
		}
	};
}

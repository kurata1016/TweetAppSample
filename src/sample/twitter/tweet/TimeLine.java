package sample.twitter.tweet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jp.digitalcloud.sample.twitter.auth.R;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TimeLine extends ListActivity {
	// twitterオブジェクト
	Twitter tw;
	// adapter
	ArrayAdapter<String> adapter;
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
	AsyncTask<Void, Void, List<String>> task;

	// onCreateメソッド(画面初期表示イベント)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// レイアウト設定ファイル指定
		setContentView(R.layout.timeline);

		// ボタンオブジェクト取得
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setTag(BTN_BACK);
		btn_back.setOnClickListener(onBtnClickListener);
		Button btn_tweet = (Button) findViewById(R.id.btn_tweet);
		btn_tweet.setTag(BTN_TWEET);
		btn_tweet.setOnClickListener(onBtnClickListener);

		// listadapter設定
		adapter = new TweetAdapter(this);
		setListAdapter(adapter);

		// twitterオブジェクト設定
		tw = getTwitterInstance();

		// timeline取得
		reloadTimeLine();
	}

	private void reloadTimeLine() {
		// ネットワーク通信は非同期処理
		task = new AsyncTask<Void, Void, List<String>>() {

			@Override
			protected List<String> doInBackground(Void... params) {
				try {
					// HomeTimeLine取得
					ResponseList<twitter4j.Status> timeline = tw.getHomeTimeline();
					ArrayList<String> list = new ArrayList<>();
					// tweet取得
					for (twitter4j.Status status : timeline) {
						String userName = status.getUser().getScreenName();
						String tweet = status.getText();
						// Date型をString型へ
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm",Locale.JAPAN);
						String time = sdf.format(status.getCreatedAt());
						list.add("ユーザーID：" + userName + "\r\n" + "tweet：" + "\r\n" + tweet + "\r\n" + "time：" + time);
					}
					return list;
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<String> result) {
				if (result != null) {
					adapter.clear();
					for (String status : result) {
						adapter.add(status);
					}
					getListView().setSelection(0);
				} else {
					showToast("タイムラインの取得に失敗しました。。。");
				}
			}
		};
		task.execute();
	}

	private class TweetAdapter extends ArrayAdapter<String> {
		//コンストラクタ
        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
        }
        
        // listviewカスタム
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	TextView view = (TextView)super.getView(position, convertView,parent);
        	view.setTextColor(Color.BLACK);
			return view;
        }
    }
	
	private Twitter getTwitterInstance() {
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

		return tw;
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
				Intent intent = new Intent(TimeLine.this,TweetActivity.class);
				startActivity(intent);
				break;
			}
		}
	};

	//トースト表示メソッド
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}

package sample.twitter.tweet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.image.SmartImageView;

import jp.digitalcloud.sample.twitter.auth.R;
import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TimeLine extends Activity {
	// listview
	PullToRefreshListView listview;
	// twitterオブジェクト
	Twitter tw;
	// adapter
	TweetAdapter adapter;
	// AccessTokenオブジェクト
	AccessToken at;
	// AccessToken
	String AccessToken;
	// AccessTokenSecret
	String AccessTokenSecret;
	// ボタンタグ
	final String BTN_BACK = "btn_back";
	final String BTN_TWEET = "btn_tweet";
	final String BTN_RELOAD = "btn_reload";
	// 非同期タスク
	AsyncTask<Void, Void, List<twitter4j.Status>> task;

	// onCreateメソッド(画面初期表示イベント)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// レイアウト設定ファイル指定
		setContentView(R.layout.timeline);

		// ボタンオブジェクト取得
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setTag(BTN_BACK);
		btn_back.setOnClickListener(onBtnClickListener);
		Button btn_tweet = (Button) findViewById(R.id.btn_tweet);
		btn_tweet.setTag(BTN_TWEET);
		btn_tweet.setOnClickListener(onBtnClickListener);
		Button btn_reload = (Button) findViewById(R.id.btn_reload);
		btn_reload.setTag(BTN_RELOAD);
		btn_reload.setOnClickListener(onBtnClickListener);

		// ListView取得
//		ListView listview = getListView();
		listview = (PullToRefreshListView) findViewById(R.id.listview);
		
		// listadapter設定
		adapter = new TweetAdapter(this);
		listview.setAdapter(adapter);
		
		// PulltoRefresh設定
		listview.setMode(Mode.BOTH);
		listview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				showToast("タイムライン更新中");
				reloadTimeLine();
				new FinishRefresh().execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				showToast("タイムライン更新中");
				reloadBackTimeLine();
				new FinishRefresh().execute();
			}
		});

		// twitterオブジェクト設定
		tw = getTwitterInstance();

		// timeline取得
		reloadTimeLine();
	}

	// タイムライン更新
	private void reloadTimeLine() {
		// ネットワーク通信は非同期処理
		task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {

			@SuppressWarnings("unused")
			@Override
			protected List<twitter4j.Status> doInBackground(Void... params) {
				try {
					// HomeTimeLineオブジェクト取得
					ResponseList<twitter4j.Status> timeline = null;
					ArrayList<twitter4j.Status> list = new ArrayList<>();
					Paging pages = null;

					// 初回取得かどうか
					if (pages == null) {
						pages = new Paging(1, 20);
						// 2回目以降
					} else {
						// 最後のつぶやき取得
						twitter4j.Status s = timeline.get(timeline.size());
						// Pagingオブジェクト取得
						pages = new Paging();
						pages.setMaxId(s.getId());
					}

					// Pagingオブジェクトで取得済みのつぶやき以降のつぶやきを取得
					timeline = tw.getHomeTimeline(pages);

					return timeline;
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<twitter4j.Status> result) {
				if (result != null) {
					adapter.clear();
					for (twitter4j.Status status : result) {
						adapter.add(status);
					}
					listview.getRefreshableView().setSelection(0);
				} else {
					showToast("タイムラインの取得に失敗しました");
				}
			}
		};
		task.execute();
	}
	
	// 過去のタイムライン更新
	private void reloadBackTimeLine() {
		// ネットワーク通信は非同期処理
		task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {

			@SuppressWarnings("unused")
			@Override
			protected List<twitter4j.Status> doInBackground(Void... params) {
				try {
					// HomeTimeLineオブジェクト取得
					ResponseList<twitter4j.Status> timeline = null;
					ArrayList<twitter4j.Status> list = new ArrayList<>();
					Paging pages = null;

					// 初回取得かどうか
					if (pages == null) {
						pages = new Paging(1, 20);
						// 2回目以降
					} else {
						// 最後のつぶやき取得
						twitter4j.Status s = timeline.get(timeline.size());
						// Pagingオブジェクト取得
						pages = new Paging();
						pages.setMaxId(s.getId());
					}

					// Pagingオブジェクトで取得済みのつぶやき以降のつぶやきを取得
					timeline = tw.getHomeTimeline(pages);

					return timeline;
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<twitter4j.Status> result) {
				if (result != null) {
					adapter.clear();
					for (twitter4j.Status status : result) {
						adapter.add(status);
					}
					listview.getRefreshableView().setSelection(0);
				} else {
					showToast("タイムラインの取得に失敗しました");
				}
			}
		};
		task.execute();
	}
	
	//リスト更新終了
    private class FinishRefresh extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result){
        	//更新アニメーション終了
            listview.onRefreshComplete();
        }
    }

	private class TweetAdapter extends ArrayAdapter<twitter4j.Status> {
		// レイアウトインフレーター取得
		private LayoutInflater mInflater;

		// コンストラクタ
		public TweetAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1);
			mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		}

		// listviewカスタム
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// Viewインスタンスがなければ生成
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_tweet, null);
			}
			
			// ツイート内容取得
			Status item = getItem(position);
			// アイコン画像取得
			SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
			icon.setImageUrl(item.getUser().getProfileImageURL());
			// 名前取得
			TextView name = (TextView) convertView.findViewById(R.id.name);
			name.setText(item.getUser().getName());
			name.setTextColor(Color.BLACK);
			// スクリーンネーム取得
			TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
			screenName.setText("@" + item.getUser().getScreenName());
			screenName.setTextColor(Color.BLACK);
			//　時間取得
			TextView time = (TextView) convertView.findViewById(R.id.time);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.JAPAN);
			time.setText(sdf.format(item.getCreatedAt()));
			time.setTextColor(Color.BLACK);
			//　つぶやき取得
			TextView text = (TextView) convertView.findViewById(R.id.text);
			text.setText(item.getText());
			text.setTextColor(Color.BLACK);
			
			return convertView;
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
				Intent intent = new Intent(TimeLine.this, TweetActivity.class);
				startActivity(intent);
				break;
			case BTN_RELOAD:
				showToast("タイムライン更新中");
				reloadTimeLine();
			}
		}
	};

	// トースト表示メソッド
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}

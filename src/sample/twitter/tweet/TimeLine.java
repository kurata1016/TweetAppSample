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
	// twitter�I�u�W�F�N�g
	Twitter tw;
	// adapter
	ArrayAdapter<String> adapter;
	// AccessToken�I�u�W�F�N�g
	AccessToken at;
	// AccessToken
	String AccessToken;
	// AccessTokenSecret
	String AccessTokenSecret;
	// �{�^���^�O
	final String BTN_BACK = "btn_back";
	final String BTN_TWEET = "btn_tweet";
	// �񓯊��^�X�N
	AsyncTask<Void, Void, List<String>> task;

	// onCreate���\�b�h(��ʏ����\���C�x���g)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���C�A�E�g�ݒ�t�@�C���w��
		setContentView(R.layout.timeline);

		// �{�^���I�u�W�F�N�g�擾
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setTag(BTN_BACK);
		btn_back.setOnClickListener(onBtnClickListener);
		Button btn_tweet = (Button) findViewById(R.id.btn_tweet);
		btn_tweet.setTag(BTN_TWEET);
		btn_tweet.setOnClickListener(onBtnClickListener);

		// listadapter�ݒ�
		adapter = new TweetAdapter(this);
		setListAdapter(adapter);

		// twitter�I�u�W�F�N�g�ݒ�
		tw = getTwitterInstance();

		// timeline�擾
		reloadTimeLine();
	}

	private void reloadTimeLine() {
		// �l�b�g���[�N�ʐM�͔񓯊�����
		task = new AsyncTask<Void, Void, List<String>>() {

			@Override
			protected List<String> doInBackground(Void... params) {
				try {
					// HomeTimeLine�擾
					ResponseList<twitter4j.Status> timeline = tw.getHomeTimeline();
					ArrayList<String> list = new ArrayList<>();
					// tweet�擾
					for (twitter4j.Status status : timeline) {
						String userName = status.getUser().getScreenName();
						String tweet = status.getText();
						// Date�^��String�^��
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm",Locale.JAPAN);
						String time = sdf.format(status.getCreatedAt());
						list.add("���[�U�[ID�F" + userName + "\r\n" + "tweet�F" + "\r\n" + tweet + "\r\n" + "time�F" + time);
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
					showToast("�^�C�����C���̎擾�Ɏ��s���܂����B�B�B");
				}
			}
		};
		task.execute();
	}

	private class TweetAdapter extends ArrayAdapter<String> {
		//�R���X�g���N�^
        public TweetAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_1);
        }
        
        // listview�J�X�^��
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
        	TextView view = (TextView)super.getView(position, convertView,parent);
        	view.setTextColor(Color.BLACK);
			return view;
        }
    }
	
	private Twitter getTwitterInstance() {
		// preference����AccessToken�擾
		SharedPreferences pref = getSharedPreferences(this.getString(R.string.shared_pref_name), MODE_PRIVATE);
		AccessToken = pref.getString(getString(R.string.shared_pref_key_twitter_access_token), "");
		AccessTokenSecret = pref.getString(getString(R.string.shared_pref_key_twitter_access_token_secret), "");
		// twitter�I�u�W�F�N�g�擾
		tw = new TwitterFactory().getInstance();
		// AccessToken�I�u�W�F�N�g�擾
		at = new AccessToken(AccessToken, AccessTokenSecret);
		// APIkey �� APIkeysecret�̐ݒ�
		tw.setOAuthConsumer(getString(R.string.twitter_consumer_key), getString(R.string.twitter_consumer_key_secret));
		// AccessToken�̐ݒ�
		tw.setOAuthAccessToken(at);

		return tw;
	}

	private OnClickListener onBtnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// �{�^���I�u�W�F�N�g�擾
			Button button = (Button) v;
			// �{�^������
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

	//�g�[�X�g�\�����\�b�h
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}

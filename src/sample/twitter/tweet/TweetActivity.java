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
	// twitter�I�u�W�F�N�g
	Twitter tw;
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
	AsyncTask<String, Void, Boolean> task;
	// �c�C�[�g���e������
	EditText et = null;

	// onCreate���\�b�h(��ʏ����\���C�x���g)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ���C�A�E�g�ݒ�t�@�C���w��
		setContentView(jp.digitalcloud.sample.twitter.auth.R.layout.tweet);

		// �{�^���I�u�W�F�N�g�擾
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setTag(BTN_BACK);
		btn_back.setOnClickListener(onBtnClickListener);
		Button btn_tweet = (Button) findViewById(R.id.btn_tweet);
		btn_tweet.setTag(BTN_TWEET);
		btn_tweet.setOnClickListener(onBtnClickListener);
		
		// editText�擾
		et = (EditText) findViewById(R.id.et_tweet);
		// editText�ɃC�x���g���X�i�[�o�^
		et.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				//�@���͉\�������\��TextView�擾
				TextView charcounts = (TextView) findViewById(R.id.tv_charscount);
				// ���͉\�������ݒ�
				int count = 140 - s.length();
				charcounts.setText(String.valueOf(count));
				// 140�����ȏ���͂��ꂽ��g�[�X�g�\��
				if(count < 0){
					Toast.makeText(TweetActivity.this, "�c�C�[�g��140�����܂łł�", Toast.LENGTH_SHORT).show();;
				}
			}
		});

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

		// �l�b�g���[�N�ʐM�͔񓯊�����
		task = new AsyncTask<String, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(String... params) {
				// �c�C�[�g���e�擾
				et = (EditText) findViewById(R.id.et_tweet);
				// �c�C�[�g����
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
					Toast.makeText(TweetActivity.this, "�c�C�[�g���܂����I", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(TweetActivity.this, "�c�C�[�g�ł��܂���ł���", Toast.LENGTH_SHORT).show();
				}
			}
		};
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
				task.execute();
				break;
			}
		}
	};
}

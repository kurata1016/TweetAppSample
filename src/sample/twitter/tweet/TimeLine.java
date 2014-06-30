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
	// twitter�I�u�W�F�N�g
	Twitter tw;
	// adapter
	TweetAdapter adapter;
	// AccessToken�I�u�W�F�N�g
	AccessToken at;
	// AccessToken
	String AccessToken;
	// AccessTokenSecret
	String AccessTokenSecret;
	// �{�^���^�O
	final String BTN_BACK = "btn_back";
	final String BTN_TWEET = "btn_tweet";
	final String BTN_RELOAD = "btn_reload";
	// �񓯊��^�X�N
	AsyncTask<Void, Void, List<twitter4j.Status>> task;

	// onCreate���\�b�h(��ʏ����\���C�x���g)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// ���C�A�E�g�ݒ�t�@�C���w��
		setContentView(R.layout.timeline);

		// �{�^���I�u�W�F�N�g�擾
		Button btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setTag(BTN_BACK);
		btn_back.setOnClickListener(onBtnClickListener);
		Button btn_tweet = (Button) findViewById(R.id.btn_tweet);
		btn_tweet.setTag(BTN_TWEET);
		btn_tweet.setOnClickListener(onBtnClickListener);
		Button btn_reload = (Button) findViewById(R.id.btn_reload);
		btn_reload.setTag(BTN_RELOAD);
		btn_reload.setOnClickListener(onBtnClickListener);

		// ListView�擾
//		ListView listview = getListView();
		listview = (PullToRefreshListView) findViewById(R.id.listview);
		
		// listadapter�ݒ�
		adapter = new TweetAdapter(this);
		listview.setAdapter(adapter);
		
		// PulltoRefresh�ݒ�
		listview.setMode(Mode.BOTH);
		listview.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				showToast("�^�C�����C���X�V��");
				reloadTimeLine();
				new FinishRefresh().execute();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				showToast("�^�C�����C���X�V��");
				reloadBackTimeLine();
				new FinishRefresh().execute();
			}
		});

		// twitter�I�u�W�F�N�g�ݒ�
		tw = getTwitterInstance();

		// timeline�擾
		reloadTimeLine();
	}

	// �^�C�����C���X�V
	private void reloadTimeLine() {
		// �l�b�g���[�N�ʐM�͔񓯊�����
		task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {

			@SuppressWarnings("unused")
			@Override
			protected List<twitter4j.Status> doInBackground(Void... params) {
				try {
					// HomeTimeLine�I�u�W�F�N�g�擾
					ResponseList<twitter4j.Status> timeline = null;
					ArrayList<twitter4j.Status> list = new ArrayList<>();
					Paging pages = null;

					// ����擾���ǂ���
					if (pages == null) {
						pages = new Paging(1, 20);
						// 2��ڈȍ~
					} else {
						// �Ō�̂Ԃ₫�擾
						twitter4j.Status s = timeline.get(timeline.size());
						// Paging�I�u�W�F�N�g�擾
						pages = new Paging();
						pages.setMaxId(s.getId());
					}

					// Paging�I�u�W�F�N�g�Ŏ擾�ς݂̂Ԃ₫�ȍ~�̂Ԃ₫���擾
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
					showToast("�^�C�����C���̎擾�Ɏ��s���܂���");
				}
			}
		};
		task.execute();
	}
	
	// �ߋ��̃^�C�����C���X�V
	private void reloadBackTimeLine() {
		// �l�b�g���[�N�ʐM�͔񓯊�����
		task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {

			@SuppressWarnings("unused")
			@Override
			protected List<twitter4j.Status> doInBackground(Void... params) {
				try {
					// HomeTimeLine�I�u�W�F�N�g�擾
					ResponseList<twitter4j.Status> timeline = null;
					ArrayList<twitter4j.Status> list = new ArrayList<>();
					Paging pages = null;

					// ����擾���ǂ���
					if (pages == null) {
						pages = new Paging(1, 20);
						// 2��ڈȍ~
					} else {
						// �Ō�̂Ԃ₫�擾
						twitter4j.Status s = timeline.get(timeline.size());
						// Paging�I�u�W�F�N�g�擾
						pages = new Paging();
						pages.setMaxId(s.getId());
					}

					// Paging�I�u�W�F�N�g�Ŏ擾�ς݂̂Ԃ₫�ȍ~�̂Ԃ₫���擾
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
					showToast("�^�C�����C���̎擾�Ɏ��s���܂���");
				}
			}
		};
		task.execute();
	}
	
	//���X�g�X�V�I��
    private class FinishRefresh extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }
 
        @Override
        protected void onPostExecute(Void result){
        	//�X�V�A�j���[�V�����I��
            listview.onRefreshComplete();
        }
    }

	private class TweetAdapter extends ArrayAdapter<twitter4j.Status> {
		// ���C�A�E�g�C���t���[�^�[�擾
		private LayoutInflater mInflater;

		// �R���X�g���N�^
		public TweetAdapter(Context context) {
			super(context, android.R.layout.simple_list_item_1);
			mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		}

		// listview�J�X�^��
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// View�C���X�^���X���Ȃ���ΐ���
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item_tweet, null);
			}
			
			// �c�C�[�g���e�擾
			Status item = getItem(position);
			// �A�C�R���摜�擾
			SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
			icon.setImageUrl(item.getUser().getProfileImageURL());
			// ���O�擾
			TextView name = (TextView) convertView.findViewById(R.id.name);
			name.setText(item.getUser().getName());
			name.setTextColor(Color.BLACK);
			// �X�N���[���l�[���擾
			TextView screenName = (TextView) convertView.findViewById(R.id.screen_name);
			screenName.setText("@" + item.getUser().getScreenName());
			screenName.setTextColor(Color.BLACK);
			//�@���Ԏ擾
			TextView time = (TextView) convertView.findViewById(R.id.time);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.JAPAN);
			time.setText(sdf.format(item.getCreatedAt()));
			time.setTextColor(Color.BLACK);
			//�@�Ԃ₫�擾
			TextView text = (TextView) convertView.findViewById(R.id.text);
			text.setText(item.getText());
			text.setTextColor(Color.BLACK);
			
			return convertView;
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
				Intent intent = new Intent(TimeLine.this, TweetActivity.class);
				startActivity(intent);
				break;
			case BTN_RELOAD:
				showToast("�^�C�����C���X�V��");
				reloadTimeLine();
			}
		}
	};

	// �g�[�X�g�\�����\�b�h
	private void showToast(String text) {
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}
}

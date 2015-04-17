package com.yangpeiyong.demo.umengfeedbackdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.fb.FeedbackAgent;
import com.umeng.fb.SyncListener;
import com.umeng.fb.model.Conversation;
import com.umeng.fb.model.Reply;
import com.umeng.fb.model.UserInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class MainActivity extends ActionBarActivity {

    //private FeedbackFragment mFeedbackFragment;
    private Conversation mConversation;
    private FeedbackAgent mAgent;

    @InjectView(R.id.feedbacklistview)
    ListView mListView;
    @InjectView(R.id.umeng_fb_send_content)
    EditText mEditText;
    private BaseAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ButterKnife.inject(this);

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mAgent = new FeedbackAgent(this);
        mConversation = mAgent.getDefaultConversation();


        mAdapter = new FeedbackAdapter();
        mListView.setAdapter(mAdapter);


        //updateUserInfo();
        sync();

    }
    @OnClick(R.id.fd_send)
    public void sendReply(){
        String content = mEditText.getText().toString();
        if(TextUtils.isEmpty(content)){
            Toast.makeText(this, R.string.reply_no_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, 0);
        mEditText.setText(null);
        mConversation.addUserReply(content);
        sync();
        scrollListViewToBottom();
    }
    private void scrollListViewToBottom() {
        mListView.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                mListView.setSelection(mAdapter.getCount() - 1);
            }
        });
    }
    private void sync() {

        mConversation.sync(new SyncListener() {

            @Override
            public void onSendUserReply(List<Reply> replyList) {
            }

            @Override
            public void onReceiveDevReply(List<Reply> replyList) {
                // SwipeRefreshLayout停止刷新
                //mSwipeRefreshLayout.setRefreshing(false);
                // 刷新ListView
                mAdapter.notifyDataSetChanged();
                //scrollToBottom();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUserInfo(){
        FeedbackAgent agent = new FeedbackAgent(this);

        UserInfo fdUserInfo = agent.getUserInfo();
        if (fdUserInfo == null)
            fdUserInfo = new UserInfo();
        Map<String, String> contact = fdUserInfo.getContact();
        if (contact == null)
            contact = new HashMap<>();


        contact.put("id", "demoId");
        contact.put("name","demoUserName");
        fdUserInfo.setContact(contact);

        agent.setUserInfo(fdUserInfo);

        new UpdateInfoTask().execute();

    }
    private class UpdateInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            mAgent.updateUserInfo();
            return null;
        }
    }
    class FeedbackAdapter extends BaseAdapter {

        class ViewHolder {
            @InjectView(R.id.umeng_fb_reply_content)
            TextView replyContent;
            @InjectView(R.id.umeng_fb_reply_date) TextView replyDate;
            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
        /*
        private class ViewHolder {
            TextView replyContent;
            TextView replyDate;
        }
        */
        @Override
        public int getCount() {
            return mConversation.getReplyList().size()+1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==0){
                return 0;
            }
            Reply reply = mConversation.getReplyList().get(position - 1);
            if (reply.type.equals(Reply.TYPE_DEV_REPLY)) {
                return 0;
            }else {
                return 1;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView == null){
                switch (getItemViewType(position)){
                    case 0:
                        convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.feedback_item_dev,null);
                        break;
                    case 1:
                        convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.feedback_item_user,null);
                        break;
                    default:
                        convertView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.feedback_item_dev,null);
                        break;
                }
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if(position==0){
                holder.replyContent.setText(getString(R.string.fb_reply_content_default));

            } else {
                Reply reply = mConversation.getReplyList().get(position - 1);
                holder.replyContent.setText(reply.content);
                holder.replyDate.setText(PrettyDate.getPresentDate(MainActivity.this.getApplicationContext(), reply.created_at));

            }

            if(position%5==0){
                holder.replyDate.setVisibility(View.VISIBLE);
            } else {
                holder.replyDate.setVisibility(View.GONE);
            }
            return convertView;
        }
    }

}

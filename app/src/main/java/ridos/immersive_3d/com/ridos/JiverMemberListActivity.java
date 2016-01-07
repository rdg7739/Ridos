package ridos.immersive_3d.com.ridos;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.smilefam.jia.Jiver;
import com.smilefam.jia.MemberListQuery;
import com.smilefam.jia.model.Member;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class JiverMemberListActivity extends FragmentActivity {
    private JiverMemberListFragment mJiverMemberListFragment;

    private ImageButton mBtnClose;
    private Button mBtnOK;
    private TextView mTxtChannelUrl;
    private View mTopBarContainer;

    private List<Member> mSelectedMembers;

    public static Bundle makeJiverArgs(String appKey, String uuid, String nickname, String channelUrl) {
        Bundle args = new Bundle();
        args.putString("appKey", appKey);
        args.putString("uuid", uuid);
        args.putString("nickname", nickname);
        args.putString("channelUrl", channelUrl);
        return args;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.jiver_slide_in_from_bottom, R.anim.jiver_slide_out_to_top);
        setContentView(R.layout.activity_jiver_member_list);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        initJiver(getIntent().getExtras());
        initFragment(getIntent().getExtras().getString("channelUrl"));
        initUIComponents();
    }

    private void initJiver(Bundle extras) {
        if(extras != null) {
            String appKey = extras.getString("appKey");
            String uuid = extras.getString("uuid");
            String nickname = extras.getString("nickname");


            Jiver.init(appKey);
            Jiver.login(uuid, nickname);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        resizeMenubar();
    }


    private void resizeMenubar() {
        ViewGroup.LayoutParams lp = mTopBarContainer.getLayoutParams();
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            lp.height = (int) (28 * getResources().getDisplayMetrics().density);
        } else {
            lp.height = (int) (48 * getResources().getDisplayMetrics().density);
        }
        mTopBarContainer.setLayoutParams(lp);
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.jiver_slide_in_from_top, R.anim.jiver_slide_out_to_bottom);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void initFragment(String channelUrl) {
        mSelectedMembers = new ArrayList<Member>();
        mJiverMemberListFragment = new JiverMemberListFragment();
        mJiverMemberListFragment.setChannelUrl(channelUrl);

        mJiverMemberListFragment.setJiverMemberListHandler(new JiverMemberListFragment.JiverMemberListHandler() {
            @Override
            public void onMemberSelected(HashSet<Member> members) {
                mSelectedMembers = Arrays.asList(members.toArray(new Member[0]));
                if(mSelectedMembers.size() <= 0) {
                    mBtnOK.setTextColor(Color.parseColor("#6f5ca7"));
                } else {
                    mBtnOK.setTextColor(Color.parseColor("#35f8ca"));
                }
            }
        });

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, mJiverMemberListFragment)
                .commit();
    }

    private void initUIComponents() {
        mTopBarContainer = findViewById(R.id.top_bar_container);
        mTxtChannelUrl = (TextView)findViewById(R.id.txt_channel_url);

        mBtnClose = (ImageButton)findViewById(R.id.btn_close);
        mBtnOK = (Button)findViewById(R.id.btn_ok);

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSelectedMembers.size() > 0) {
                    String [] memberIds = new String[mSelectedMembers.size()];
                    for(int i = 0; i < memberIds.length; i++) {
                        memberIds[i] = mSelectedMembers.get(i).getId();
                    }
                    Intent data = new Intent();
                    data.putExtra("userIds", memberIds);
                    setResult(RESULT_OK, data);
                } else {
                    setResult(RESULT_CANCELED);
                }
                finish();
            }
        });

        resizeMenubar();
    }

    public static class JiverMemberListFragment extends Fragment {
        private String mChannelUrl;
        private JiverMemberListHandler mHandler;
        private ListView mListView;
        private MemberListQuery mMemberListQuery;
        private JiverMemberAdapter mAdapter;
        private HashSet<Member> mSelectedMembers;

        public static interface JiverMemberListHandler {
            public void onMemberSelected(HashSet<Member> members);
        }

        public void setJiverMemberListHandler(JiverMemberListHandler handler) {
            mHandler = handler;
        }

        public JiverMemberListFragment() {
        }

        public void setChannelUrl(String channelUrl) {
            mChannelUrl = channelUrl;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.jiver_fragment_member_list, container, false);
            initUIComponents(rootView);

            mMemberListQuery = Jiver.queryMemberList(mChannelUrl);
            mMemberListQuery.next(new MemberListQuery.MemberListQueryResult() {
                @Override
                public void onResult(Collection<Member> members) {
                    mAdapter.addAll(members);
                    if (members.size() <= 0) {
                        Toast.makeText(getActivity(), "No members.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onError(Exception e) {
                }
            });


            return rootView;

        }
        private void initUIComponents(View rootView) {
            mSelectedMembers = new HashSet<Member>();
            mListView = (ListView)rootView.findViewById(R.id.list);
            mAdapter = new JiverMemberAdapter(getActivity());
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                }
            });
            mListView.setAdapter(mAdapter);
        }


        public class JiverMemberAdapter extends BaseAdapter {
            private final Context mContext;
            private final LayoutInflater mInflater;
            private final ArrayList<Member> mItemList;

            public JiverMemberAdapter(Context context) {
                mContext = context;
                mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                mItemList = new ArrayList<Member>();
            }

            @Override
            public int getCount() {
                return mItemList.size();
            }

            @Override
            public Member getItem(int position) {
                return mItemList.get(position);
            }

            public void clear() {
                mItemList.clear();
            }

            public Member remove(int index) {
                return mItemList.remove(index);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            public void addAll(Collection<Member> members) {
                mItemList.addAll(members);
                notifyDataSetChanged();
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder viewHolder;

                if(convertView == null) {
                    viewHolder = new ViewHolder();

                    convertView = mInflater.inflate(R.layout.jiver_view_member, parent, false);
                    viewHolder.setView("root_view", convertView);
                    viewHolder.setView("img_thumbnail", convertView.findViewById(R.id.img_thumbnail));
                    viewHolder.setView("txt_name", convertView.findViewById(R.id.txt_name));
                    viewHolder.setView("chk_select", convertView.findViewById(R.id.chk_select));


                    convertView.setTag(viewHolder);
                }

                final Member item = getItem(position);
                viewHolder = (ViewHolder) convertView.getTag();
                displayUrlImage(viewHolder.getView("img_thumbnail", ImageView.class), item.getImageUrl());
                viewHolder.getView("txt_name", TextView.class).setText(item.getName());
                viewHolder.getView("chk_select", CheckBox.class).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        if(isChecked) {
                            mSelectedMembers.add(item);
                        } else {
                            mSelectedMembers.remove(item);
                        }

                        if (mHandler != null)  {
                            mHandler.onMemberSelected(mSelectedMembers);
                        }
                    }
                });
                viewHolder.getView("chk_select", CheckBox.class).setChecked(mSelectedMembers.contains(item));
                return convertView;
            }

            private class ViewHolder {
                private Hashtable<String, View> holder = new Hashtable<String, View>();

                public void setView(String k, View v) {
                    holder.put(k, v);
                }

                public View getView(String k) {
                    return holder.get(k);
                }

                public <T> T getView(String k, Class<T> type) {
                    return type.cast(getView(k));
                }
            }
        }
    }

    private static void displayUrlImage(ImageView imageView, String url) {
        UrlDownloadAsyncTask.display(url, imageView);
    }

    private static class UrlDownloadAsyncTask extends AsyncTask<Void, Void, Object> {
        private static LRUCache cache = new LRUCache((int) (Runtime.getRuntime().maxMemory() / 16)); // 1/16th of the maximum memory.
        private final UrlDownloadAsyncTaskHandler handler;
        private String url;

        public static void download(String url, final File downloadFile, final Context context) {
            UrlDownloadAsyncTask task = new UrlDownloadAsyncTask(url, new UrlDownloadAsyncTaskHandler() {
                @Override
                public void onPreExecute() {
                    Toast.makeText(context, "Start downloading", Toast.LENGTH_SHORT).show();
                }

                @Override
                public Object doInBackground(File file) {
                    if(file == null) {
                        return null;
                    }

                    try {
                        BufferedInputStream in = null;
                        BufferedOutputStream out = null;

                        //create output directory if it doesn't exist
                        File dir = downloadFile.getParentFile();
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }

                        in = new BufferedInputStream(new FileInputStream(file));
                        out = new BufferedOutputStream(new FileOutputStream(downloadFile));

                        byte[] buffer = new byte[1024 * 100];
                        int read;
                        while ((read = in.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                        }
                        in.close();
                        out.flush();
                        out.close();

                        return downloadFile;
                    } catch(IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                public void onPostExecute(Object object, UrlDownloadAsyncTask task) {
                    if(object != null && object instanceof File) {
                        Toast.makeText(context, "Finish downloading: " + ((File)object).getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Error downloading", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }
        }

        public static void display(String url, final ImageView imageView) {
            UrlDownloadAsyncTask task = null;

            if(imageView.getTag() != null && imageView.getTag() instanceof UrlDownloadAsyncTask) {
                try {
                    task = (UrlDownloadAsyncTask) imageView.getTag();
                    task.cancel(true);
                } catch(Exception e) {}

                imageView.setTag(null);
            }

            task = new UrlDownloadAsyncTask(url, new UrlDownloadAsyncTaskHandler() {
                @Override
                public void onPreExecute() {
                    imageView.setImageResource(R.drawable.jiver_img_placeholder);
                }

                @Override
                public Object doInBackground(File file) {
                    if(file == null) {
                        return null;
                    }

                    Bitmap bm = null;
                    try {
                        int targetHeight = 256;
                        int targetWidth = 256;

                        BufferedInputStream bin = new BufferedInputStream(new FileInputStream(file));
                        bin.mark(bin.available());

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeStream(bin, null, options);

                        Boolean scaleByHeight = Math.abs(options.outHeight - targetHeight) >= Math.abs(options.outWidth - targetWidth);

                        if(options.outHeight * options.outWidth >= targetHeight * targetWidth) {
                            double sampleSize = scaleByHeight
                                    ? options.outHeight / targetHeight
                                    : options.outWidth / targetWidth;
                            options.inSampleSize = (int)Math.pow(2d, Math.floor(Math.log(sampleSize)/Math.log(2d)));
                        }

                        try {
                            bin.reset();
                        } catch(IOException e) {
                            bin = new BufferedInputStream(new FileInputStream(file));
                        }

                        // Do the actual decoding
                        options.inJustDecodeBounds = false;
                        bm = BitmapFactory.decodeStream(bin, null, options);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return bm;
                }

                @Override
                public void onPostExecute(Object object, UrlDownloadAsyncTask task) {
                    if(object != null && object instanceof Bitmap && imageView.getTag() == task) {
                        imageView.setImageBitmap((Bitmap)object);
                    }
                }
            });

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                task.execute();
            }

            imageView.setTag(task);
        }

        public UrlDownloadAsyncTask(String url, UrlDownloadAsyncTaskHandler handler) {
            this.handler = handler;
            this.url = url;
        }

        public interface UrlDownloadAsyncTaskHandler {
            public void onPreExecute();
            public Object doInBackground(File file);
            public void onPostExecute(Object object, UrlDownloadAsyncTask task);
        }

        @Override
        protected void onPreExecute() {
            if(handler != null) {
                handler.onPreExecute();
            }
        }

        protected Object doInBackground(Void... args) {
            File outFile = null;
            try {
                if(cache.get(url) != null && new File(cache.get(url)).exists()) { // Cache Hit
                    outFile = new File(cache.get(url));
                } else { // Cache Miss, Downloading a file from the url.
                    outFile = File.createTempFile("jiver-download", ".tmp");
                    OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(outFile));

                    InputStream input = new BufferedInputStream(new URL(url).openStream());
                    byte[] buf = new byte[1024 * 100];
                    int read = 0;
                    while ((read = input.read(buf, 0, buf.length)) >= 0) {
                        outputStream.write(buf, 0, read);
                    }

                    outputStream.flush();
                    outputStream.close();
                    cache.put(url, outFile.getAbsolutePath());
                }



            } catch(IOException e) {
                e.printStackTrace();

                if(outFile != null) {
                    outFile.delete();
                }

                outFile = null;
            }


            if(handler != null) {
                return handler.doInBackground(outFile);
            }

            return outFile;
        }

        protected void onPostExecute(Object result) {
            if(handler != null) {
                handler.onPostExecute(result, this);
            }
        }

        private static class LRUCache {
            private final int maxSize;
            private int totalSize;
            private ConcurrentLinkedQueue<String> queue;
            private ConcurrentHashMap<String, String> map;

            public LRUCache(final int maxSize) {
                this.maxSize = maxSize;
                this.queue	= new ConcurrentLinkedQueue<String>();
                this.map	= new ConcurrentHashMap<String, String>();
            }

            public String get(final String key) {
                if (map.containsKey(key)) {
                    queue.remove(key);
                    queue.add(key);
                }

                return map.get(key);
            }

            public synchronized void put(final String key, final String value) {
                if(key == null || value == null) {
                    throw new NullPointerException();
                }

                if (map.containsKey(key)) {
                    queue.remove(key);
                }

                queue.add(key);
                map.put(key, value);
                totalSize = totalSize + getSize(value);

                while (totalSize >= maxSize) {
                    String expiredKey = queue.poll();
                    if (expiredKey != null) {
                        totalSize = totalSize - getSize(map.remove(expiredKey));
                    }
                }
            }

            private int getSize(String value) {
                return value.length();
            }
        }
    }
}

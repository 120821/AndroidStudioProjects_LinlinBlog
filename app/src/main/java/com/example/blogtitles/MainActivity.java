package com.example.blogtitles;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Intent;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String API_URL = "https://admin.linlin.fun/api/v1/blogs";
    private ListView blogListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        blogListView = findViewById(R.id.blogListView);

        // 执行异步任务来获取博客列表
        new FetchBlogsTask().execute();
    }

    private class FetchBlogsTask extends AsyncTask<Void, Void, List<Blog>> {

        @Override
        protected List<Blog> doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API_URL)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String jsonData = response.body().string();
                return parseJsonData(jsonData);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Blog> blogs) {
            if (blogs != null) {
                // 创建适配器
                BlogTitleAdapter adapter = new BlogTitleAdapter(MainActivity.this, blogs);
                Log.d("API Response", "blog: " + blogs);
                // 设置适配器
                blogListView.setAdapter(adapter);
            }
        }
    }

    private List<Blog> parseJsonData(String jsonData) {
        List<Blog> blogs = new ArrayList<>();

        try {
            JSONObject json = new JSONObject(jsonData);
            JSONArray jsonArray = json.getJSONArray("blogs");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String title = jsonObject.getString("title");

                // 如果有content字段，解析content
                String content = jsonObject.optString("content", "");

                Blog blog = new Blog(title, content);

                blogs.add(blog);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return blogs;
    }

    private class Blog {
        private String title;
        private String content;

        public Blog(String title, String content) {
            this.title = title;
            this.content = content;

        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }

    }


    private class BlogTitleAdapter extends ArrayAdapter<Blog> {

        public BlogTitleAdapter(MainActivity context, List<Blog> blogs) {
            super(context, 0, blogs);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            }

            Blog blog = getItem(position);

            TextView titleTextView = convertView.findViewById(android.R.id.text1);
            titleTextView.setText(blog.getTitle());
            // 添加点击事件监听器
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 在点击时跳转到详情页面，并传递博客内容
                    //Intent intent = new Intent(getContext(), BlogDetailActivity.class);
                    //intent.putExtra("content", blog.getContent());
                    //getContext().startActivity(intent);
                    // 在点击时跳转到详情页面，并传递博客内容
                    Intent intent = new Intent(getContext(), BlogDetailActivity.class);
                    intent.putExtra("content", blog.getContent());
                    intent.putExtra("title", blog.getTitle());
                    getContext().startActivity(intent);
                }
            });
            return convertView;
        }
    }
}
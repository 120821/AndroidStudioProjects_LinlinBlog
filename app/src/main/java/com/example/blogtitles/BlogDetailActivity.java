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
import okhttp3.Call;
import okhttp3.Callback;
import java.io.IOException;
import android.os.Build;
import android.text.Html;

public class BlogDetailActivity extends AppCompatActivity {

    private TextView contentTextView;
    public class Blog {
        private String content;
        private String title;
        public String getFormattedContent() {
            if (content != null) {
                // 将内容转换为可显示的HTML格式
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    return Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY).toString();
                } else {
                    return Html.fromHtml(content).toString();
                }
            } else {
                return "";
            }
        }

        public String getContent() {
            return content;
        }
        public String getTitle() {
            return title;
        }

        public void setContent(String content, String title) {
            this.content = content;
            this.title = title;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_detail);

        contentTextView = findViewById(R.id.contentTextView);

        // 获取传递过来的博客ID
        String blogId = getIntent().getStringExtra("id");

        // 构建请求的URL
        String url = "https://admin.linlin.fun/api/v1/blogs/" + "1189";
      //  String url = "https://admin.linlin.fun/api/v1/blogs/" + blogId;

        // 创建OkHttpClient实例
        OkHttpClient client = new OkHttpClient();

        // 创建请求对象
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 发送异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                // 处理请求失败的情况
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    // 使用Gson解析接口响应数据
                    Gson gson = new Gson();
                    Blog blog = gson.fromJson(responseData, Blog.class);
                    // 打印接口返回的内容

                    Log.d("API Response", "blog: " + blog);


                    // 在UI线程更新UI
                    runOnUiThread(() -> {
                        // 在TextView中显示博客内容
                        // contentTextView.setText(blog.getContent());

                        contentTextView.setText(blog.getFormattedContent());
                    });
                } else {
                    // 处理请求失败的情况
                }
            }
        });
    }
}

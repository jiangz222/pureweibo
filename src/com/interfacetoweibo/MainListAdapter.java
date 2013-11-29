package com.interfacetoweibo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.pureweibo.R;
import com.weibodatahandle.WeiboStatus;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * User: return
 * Date: 13-2-27
 * Time: 上午1:57
 * Blog: www.creturn.com
 * Email:master@creturn.com
 */
public class MainListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private List<WeiboStatus> weiboStatusList;
    //点三放图片下载库
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private DisplayImageOptions displayImageOptions;
    static class ListItemView{
        public TextView content; //微博内容
        public TextView username;//用戶名
        public ImageView avatar;//用戶頭像
        public TextView source;//微博来源
        public TextView time; //微博时间
        public ImageView thumbnailpic;   //微博图片
    }

    public MainListAdapter(Context context,List<WeiboStatus> weiboStatusList){
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
        this.weiboStatusList = weiboStatusList;
        //init imgLoader
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .memoryCacheSize(1500000) // 1.5 Mb
                .denyCacheImageMultipleSizesInMemory()
                .discCacheFileNameGenerator(new Md5FileNameGenerator())
                .enableLogging() // Not necessary in common
                .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
        //初始化 imageOptioin
        displayImageOptions = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.default_avatar)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

    }
    @Override
    public int getCount() {
        return this.weiboStatusList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long getItemId(int i) {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        //自定义控件
        ListItemView listItemView = null;

        if (convertView == null){
            convertView = this.layoutInflater.inflate(R.layout.main_list_item,null);
            listItemView = new ListItemView();
            //获取空间对象
            listItemView.content = (TextView) convertView.findViewById(R.id.main_list_item_content);
            listItemView.username = (TextView) convertView.findViewById(R.id.main_list_item_username);
            listItemView.avatar = (ImageView) convertView.findViewById(R.id.main_list_item_avatar);
            listItemView.source = (TextView) convertView.findViewById(R.id.main_list_item_source);
            listItemView.time = (TextView) convertView.findViewById(R.id.main_list_item_time);
            listItemView.thumbnailpic = (ImageView) convertView.findViewById(R.id.main_list_item_Thumbnail_pic);
            convertView.setTag(listItemView);
        }else{
            listItemView = (ListItemView) convertView.getTag();
        }
			//weiboStatusList.get获取到的是一个WeiboStatus
        listItemView.username.setText(this.weiboStatusList.get(i).getUser().getScreen_name());
        listItemView.content.setText(this.weiboStatusList.get(i).getText());
        listItemView.time.setText(this.weiboStatusList.get(i).getCreated_at());
        if (this.weiboStatusList.get(i).getIs_ret() == 1){
            listItemView.source.setText(this.weiboStatusList.get(i).getRetweeted_status().getText());
            listItemView.source.setVisibility(View.VISIBLE);
        } else {
            //listItemView.source.setText("");
            listItemView.source.setVisibility(View.GONE);

        }
        imageLoader.displayImage(this.weiboStatusList.get(i).getUser().getProfile_image_url(),listItemView.avatar,displayImageOptions);
     if(1 == this.weiboStatusList.get(i).get_thumb_pic_exsit())
      {
    	   imageLoader.displayImage(this.weiboStatusList.get(i).getThumbnail_pic(),listItemView.thumbnailpic,displayImageOptions);
          listItemView.thumbnailpic.setVisibility(View.VISIBLE);
     }
     else
     {
           listItemView.thumbnailpic.setVisibility(View.GONE);
     }
    return convertView;  //To change body of implemented methods use File | Settings | File Templates.
    }

}

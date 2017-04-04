package com.example.john.finalproject.Life.express;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.john.finalproject.R;

import java.util.List;

public class MessListAdapter extends BaseAdapter {

    private List<Content> allContent;
    private Context context;
    private LayoutInflater layoutInflater;

    MessListAdapter(Context context, List<Content> allContent){
        this.allContent = allContent;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return allContent.size();
    }

    @Override
    public Object getItem(int position) {
        return allContent.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = layoutInflater.inflate(R.layout.item_express_data,null);
            holder.viewTopLine = convertView.findViewById(R.id.view_top_line);
            holder.ivExpresSpot = (ImageView) convertView.findViewById(R.id.iv_expres_spot);
            holder.tvExpressText = (TextView) convertView.findViewById(R.id.tv_express_text);
            holder.tvExpressTime = (TextView) convertView.findViewById(R.id.tv_express_time);

            //将ViewHolder与convertView进行绑定
            convertView.setTag(holder);
        }else
            holder = (ViewHolder)convertView.getTag();

        Content content = allContent.get(position);
        //开始设置数据
        if(position == 0 ){  //上顶部背景透明，点是灰色,字体是绿色
            holder.viewTopLine.setBackgroundColor(Color.TRANSPARENT);
            holder.ivExpresSpot.setBackgroundResource(R.drawable.express_point_new);
            holder.tvExpressText.setTextColor(context.getResources().getColor(R.color.mainColor));
            holder.tvExpressTime.setTextColor(context.getResources().getColor(R.color.mainColor));
        }else{
            holder.viewTopLine.setBackgroundColor(context.getResources().getColor(R.color.lightgray));
            holder.ivExpresSpot.setBackgroundResource(R.drawable.express_point_old);
            holder.tvExpressText.setTextColor(context.getResources().getColor(R.color.gray));
            holder.tvExpressTime.setTextColor(context.getResources().getColor(R.color.lightgray));
        }

        holder.tvExpressText.setText(content.getContext());
        holder.tvExpressTime.setText(content.getTime());

        return convertView;
    }

    public class ViewHolder{
        public View viewTopLine;
        private ImageView ivExpresSpot;
        private TextView tvExpressText;
        private TextView tvExpressTime;

    }
}

package com.drishi.nytimessearch.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drishi.nytimessearch.R;
import com.drishi.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by drishi on 8/10/16.
 */
public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, R.layout.item_article_result, articles);
    }

    public class ViewHolder {
        TextView tvTitle;
        ImageView ivImage;

        public ViewHolder(View view) {
            tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            ivImage = (ImageView) view.findViewById(R.id.ivImage);

        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        // get the data item for position
        Article article = (Article) getItem(position);

        // check to see if existing view is being reused
        // not using a recycled view -> inflate the layout
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //clear out recycled image from ConvertView from last time
        viewHolder.ivImage.setImageResource(0);

        viewHolder.tvTitle.setText(article.getHeadline());

        // populate the thumbnail
        // remotely download the image in the background
        String thumbnail = article.getThumbNail();

        if (!TextUtils.isEmpty(thumbnail)) {
            Picasso.with(getContext()).load(thumbnail).into(viewHolder.ivImage);
        }

        return convertView;
    }

}

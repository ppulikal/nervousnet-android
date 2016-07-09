package ch.ethz.coss.nervousnet.hub.ui.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.ethz.coss.nervousnet.hub.R;

public class FaqExpandableListAdapter extends BaseExpandableListAdapter {

    public FaqExpandableListAdapter(Context context, ArrayList<String> questionList,
                                       ArrayList<String> answerList) {
        this.context = context;
        this.questionList = questionList;
        this.answerList = answerList;
    }

    private ArrayList<String> questionList;
    private ArrayList<String> answerList;
    Context context;

    @Override
    public int getGroupCount() {
        return this.questionList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.questionList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.answerList.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group_faq, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.txt_faqQuestion);
        listTitleTextView.setText(listTitle);
        return convertView;
    }


    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedListText = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_item_faq, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.txt_faqAnswer);
        expandedListTextView.setText(expandedListText);
        return convertView;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}

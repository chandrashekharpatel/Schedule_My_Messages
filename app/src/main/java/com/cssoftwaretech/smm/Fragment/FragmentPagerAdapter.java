package com.cssoftwaretech.smm.Fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.cssoftwaretech.smm.MessageList;
import com.cssoftwaretech.smm.gp.GroupList;
import com.cssoftwaretech.smm.sentMessages.SentSMS_List;

public class FragmentPagerAdapter extends FragmentStatePagerAdapter {
    int tabCount;

    public FragmentPagerAdapter(FragmentManager fm, int tabCount) {
        super(fm);
        this.tabCount = tabCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                MessageList tabMessages = new MessageList();
                return tabMessages;
            case 1:
                 GroupList tabGroupMessages = new GroupList();
                return tabGroupMessages;
            case 2:
                 SentSMS_List sentSMS_list = new SentSMS_List();
                return sentSMS_list;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabCount;
    }
}

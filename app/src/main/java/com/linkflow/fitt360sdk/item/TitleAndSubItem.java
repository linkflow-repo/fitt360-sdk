package com.linkflow.fitt360sdk.item;

import com.linkflow.fitt360sdk.activity.setting.SettingBaseRPActivity;
import com.linkflow.fitt360sdk.activity.setting.SettingGPSActivity;
import com.linkflow.fitt360sdk.activity.setting.SettingLanguageActivity;

public class TitleAndSubItem extends TitleItem {
    public SettingBaseRPActivity.ID mRPId;
    public SettingGPSActivity.ID mGPSId;
    public SettingLanguageActivity.ID mLanguageId;
    public String mSubtitle;

    public TitleAndSubItem(SettingBaseRPActivity.ID id, String title, String subtitle) {
        super(title);
        mRPId = id;
        mSubtitle = subtitle;
    }

    public TitleAndSubItem(SettingGPSActivity.ID id, String title, String subtitle) {
        super(title);
        mGPSId = id;
        mSubtitle = subtitle;
    }

    public TitleAndSubItem(SettingLanguageActivity.ID id, String title, String subtitle) {
        super(title);
        mLanguageId = id;
        mSubtitle = subtitle;
    }
}

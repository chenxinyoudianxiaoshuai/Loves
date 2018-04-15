package com.ocnyang.qbox.app.module.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;


import com.ocnyang.qbox.app.R;
import com.ocnyang.qbox.app.base.BaseCommonActivity;
import com.ocnyang.qbox.app.config.Const;
import com.ocnyang.qbox.app.module.mains.MainsActivity;
import com.ocnyang.qbox.app.utils.DataCleanManager;
import com.ocnyang.qbox.app.utils.FileInfoUtils;
import com.ocnyang.qbox.app.utils.SPUtils;

import butterknife.BindView;
import butterknife.OnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import static com.ocnyang.qbox.app.utils.FileInfoUtils.getFileSize;

public class SettingActivity extends BaseCommonActivity implements CompoundButton.OnCheckedChangeListener {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.switch_sllms_setting)
    Switch mSwitchSllmsSetting;
    @BindView(R.id.sllms_setting)
    LinearLayout mSllmsSetting;
    @BindView(R.id.qchc_setting)
    LinearLayout mQchcSetting;
    @BindView(R.id.share_setting)
    TextView mShareSetting;
    @BindView(R.id.exit_setting)
    TextView mExitSetting;
    @BindView(R.id.qlcz_setting)
    LinearLayout mQlczSetting;
    @BindView(R.id.text_qchc_setting)
    TextView mTextQchcSetting;

    @Override
    public void initContentView() {
        setContentView(R.layout.activity_setting);

    }

    @Override
    public void initView() {
        ShareSDK.initSDK(this);
        initSwitch();

    }



    private void initSwitch() {
        showCacheSize();
        mSwitchSllmsSetting.setChecked((boolean) SPUtils.get(this, Const.SLLMS, false));
        mSwitchSllmsSetting.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {
            case R.id.switch_sllms_setting:
                SPUtils.put(this, Const.SLLMS, isChecked);
                break;
            default:
                break;
        }
    }



    boolean magic_step1 = false;



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (android.R.id.home == item.getItemId()) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initPresenter() {

    }


    @OnClick({R.id.sllms_setting, R.id.qchc_setting,  R.id.qlcz_setting,
               R.id.share_setting,
            R.id.exit_setting, R.id.weatheraddress_setting})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sllms_setting:
                mSwitchSllmsSetting.setChecked(!mSwitchSllmsSetting.isChecked());
                break;
            case R.id.qchc_setting://清除 内外 cache/、files/ 3个文件夹里的数据。
                DataCleanManager.cleanInternalCache(this);
                DataCleanManager.cleanExternalCache(this);
                DataCleanManager.cleanFiles(this);
                showCacheSize();
                break;
            case R.id.share_setting:
                showShare();
                break;
            case R.id.exit_setting:
                exitApp();
                break;
            case R.id.qlcz_setting://强力清除
                Snackbar.make(mAppBar, "重置完成后，将会退出软件一次！", Snackbar.LENGTH_LONG)
                        .setAction("知道了，开始清理", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clearAllData();
                            }
                        }).show();
                break;
            case R.id.weatheraddress_setting:
                startActivity(new Intent(this,WeatherAddressActivity.class));
                break;
            default:
                break;
        }
    }

    private void showShare() {
//        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();

        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间等使用
        oks.setTitle("LoveCedar");
        // titleUrl是标题的网络链接，QQ和QQ空间等使用
        oks.setTitleUrl("http://baidu.com");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("给你推荐一个我淘到的很不错的App");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://ocnyang.com");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("功能很强大，设计的也很不错，也很纯净。");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://baidu.com");

        // 启动分享GUI
        oks.show(this);
    }

    private void exitApp() {
        Intent intent = new Intent(this, MainsActivity.class);
        intent.putExtra(Const.TAG_EXIT, true);
        startActivity(intent);
    }

    private void clearAllData() {

        new AlertDialog.Builder(this)
                .setTitle("友情提示")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("此操作你将失去LoveCedar中所有的数据和设置，回到初始安装状态，确定要这样做吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DataCleanManager.cleanApplicationData(SettingActivity.this);
                        //关闭App
                        exitApp();
                    }
                })
                .setNegativeButton("取消", null)
                .create().show();

    }

    private void showCacheSize() {
        long externalCacheSize = 0;
        long cacheSize = 0;
        long filesSize = 0;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            try {
                externalCacheSize = getFileSize(getExternalCacheDir());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            cacheSize = getFileSize(SettingActivity.this.getCacheDir());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            filesSize = FileInfoUtils.getFileSize(getFilesDir());
        } catch (Exception e) {
            e.printStackTrace();
        }

        mTextQchcSetting.setText(
                FileInfoUtils.FormetFileSize(externalCacheSize + cacheSize + filesSize));

    }

}

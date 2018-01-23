package com.learn.wlnutter.learntorecognizedigit;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener{
    private RadioGroup rgTabBar;
    private RadioButton rbChannel;
    private TextView titleTv;
    private Fragment welcomeFragment, displayFragment;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager = getFragmentManager();
        titleTv = findViewById(R.id.txt_topbar);
        rgTabBar = (RadioGroup) findViewById(R.id.rg_tab_bar);
        rgTabBar.setOnCheckedChangeListener(this);
        //获取第一个单选按钮，并设置其为选中状态
        rbChannel = (RadioButton) findViewById(R.id.rb_main);
        rbChannel.setChecked(true);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction f_transaction = fragmentManager.beginTransaction();
        hideAllFragment(f_transaction);
        switch (checkedId){
            case R.id.rb_main:

                if(welcomeFragment == null){
                    welcomeFragment = WelcomeFragment.newInstance();
                    f_transaction.add(R.id.ly_content, welcomeFragment);
                } else {
                    f_transaction.show(welcomeFragment);
                }

                break;
            case R.id.rb_display:
                if(displayFragment == null){
                    displayFragment = DisplayFragment.newInstance();
                    f_transaction.add(R.id.ly_content, displayFragment);
                } else {
                    f_transaction.show(displayFragment);
                }

                break;

        }
        f_transaction.commit();
    }

    private void hideAllFragment(FragmentTransaction fragmentTransaction){
        if(welcomeFragment != null)fragmentTransaction.hide(welcomeFragment);
        if(displayFragment != null)fragmentTransaction.hide(displayFragment);
    }
}

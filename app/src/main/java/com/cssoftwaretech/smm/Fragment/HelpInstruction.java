package com.cssoftwaretech.smm.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.cssoftwaretech.smm.MainActivity;
import com.cssoftwaretech.smm.R;

import static com.cssoftwaretech.smm.MessNotice.excMess;

public class HelpInstruction extends AppCompatActivity {
     int images = 5, i = 1;
    ImageView imgScreenSortBack, imgScreenSort;
    Button btnSkipAll, btnPrevious, btnNext;
    TextView tvWelcome, tvSlideCount, leftClick, rightClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_help_instruction);
            imgScreenSort = (ImageView) findViewById(R.id.helpIntro_imageShow);
            imgScreenSortBack = (ImageView) findViewById(R.id.helpIntro_imageShowBack);
            btnSkipAll = (Button) findViewById(R.id.helpIntro_skipAll);
            btnPrevious = (Button) findViewById(R.id.helpIntro_previous);
            btnNext = (Button) findViewById(R.id.helpIntro_next);
            imgScreenSortBack.setImageResource(R.drawable.vt_phone_body);
            tvWelcome = (TextView) findViewById(R.id.helpIntro_welcome);
            tvSlideCount = (TextView) findViewById(R.id.helpIntro_slideCount);
            leftClick = (TextView) findViewById(R.id.helpIntro_leftSide);
            rightClick = (TextView) findViewById(R.id.helpIntro_rightSide);
            setData(1);
            btnSkipAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
            });
            btnPrevious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    action(0);
                }
            });
            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    action(1);
                }
            });
            leftClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    action(0);
                }
            });
            rightClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    action(1);
                }
            });
        } catch (Exception e) {
            excMess(getApplicationContext(), "HelpInstruction", e);
        }
    }

    private void action(int action) {
        if (action == 0) {
            if (i > 1) {
                i--;
                setData(i);
            }
        } else if (action == 1) {
            if (i < images) {
                i++;
                setData(i);
            } else {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                return;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    private void setData(int i) {
        tvSlideCount.setText(i + "/" + images);
        switch (i) {
            case 1:
                tvWelcome.setText("WELCOME");
                imgScreenSort.setImageResource(R.drawable.sc_help_intro_1);
                break;
            case 2:
                tvWelcome.setText("Set Birthdays, Anniversarys or Special Days");
                imgScreenSort.setImageResource(R.drawable.sc_help_intro_2);
                break;
            case 3:
                tvWelcome.setText("Team Messages \n Or \n Group Messages ");
                imgScreenSort.setImageResource(R.drawable.sc_help_intro_3);
                break;
            case 4:
                tvWelcome.setText("Set Special Tasks");
                imgScreenSort.setImageResource(R.drawable.sc_help_intro_4);
                break;
            case 5:
                tvWelcome.setText("schedule a meeting \n or \n schedule an appointment");
                imgScreenSort.setImageResource(R.drawable.sc_help_intro_5);
                break;
            /*case 6:
                tvWelcome.setText("Member Information");
                imgScreenSort.setImageResource(R.drawable.sc_help_intro_6);
                break;
            case 7:
                tvWelcome.setText("What message do you want to send in Group");
                imgScreenSort.setImageResource(R.drawable.sc_help_intro_3);
                break;
            case 8:
                tvWelcome.setText("Message sent details");
                imgScreenSort.setImageResource(R.drawable.sc_help_intro_8);
                break;*/
            default:
                break;
        }
    }
}

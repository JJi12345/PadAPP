package com.padApp.view;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.padApp.R;
import com.padApp.application.MainApplication;
public class EventDialog extends Dialog {

        private Window window = null;

        public EventDialog(Context context)
        {
            super(context);
        }

        public void showDialog(int layoutResID, int x, int y,MainApplication mainApplication){

            setContentView(layoutResID);
            window = getWindow(); //得到对话框
            window.findViewById(R.id.checkButton)
                    .setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dismiss();
                            mainApplication.setEvent_dialog_flag(true);
                        }
                    });
            windowDeploy(x, y);

            setCanceledOnTouchOutside(false);
            setCancelable(false);

            show();
        }

        //设置窗口显示
        public void windowDeploy(int x, int y){


            window.setWindowAnimations(R.style.dialogWindowAnim); //设置窗口弹出动画
            window.setBackgroundDrawableResource(R.color.white); //设置对话框背景为透明
            WindowManager.LayoutParams wl = window.getAttributes();
            //根据x，y坐标设置窗口需要显示的位置
            wl.x = 0; //x小于0左移，大于0右移
            wl.y = -400; //y小于0上移，大于0下移
//            wl.alpha = 0.6f; //设置透明度
//            wl.gravity = Gravity.BOTTOM; //设置重力
            window.setAttributes(wl);
        }


}

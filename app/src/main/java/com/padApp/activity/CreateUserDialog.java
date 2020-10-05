package com.padApp.activity;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.padApp.R;

public class CreateUserDialog extends Dialog {

    /**
     * 上下文对象 *
     */
    Activity context;

    private Button btn_save;
    private Button btn_exit;
    public TextView event_time;
    public EditText accident_des;
    public TextView state;
    public RadioGroup handle_states;



    private View.OnClickListener mClickListener;


    public CreateUserDialog(Activity context, int theme,View.OnClickListener clickListener) {
        super(context,theme);
        this.context = context;
        this.mClickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.event_handle);
        state = (TextView)findViewById(R.id.state);
        accident_des = (EditText) findViewById(R.id.event_detail);
        handle_states = (RadioGroup) findViewById(R.id.rg_1);
        handle_states.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                state.setText(radioButton.getText());
            }
        });


        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = this.getWindow();

        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
        p.width = (int) (d.getWidth() * 0.45); // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL);

        // 根据id在布局中找到控件对象
        btn_save = (Button) findViewById(R.id.btn_save_pop);
        btn_exit = (Button) findViewById(R.id.btn_exit_pop);

        // 为按钮绑定点击事件监听器
        btn_save.setOnClickListener(mClickListener);
        btn_exit.setOnClickListener(mClickListener);

        this.setCancelable(true);
    }
}
package com.example.AndroidSSHWithRaspberryPi.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.AndroidSSHWithRaspberryPi.R;

public class ConnectionDialog {
    public interface ClickConfirm {
        void clickConfirm();
    }
    public ConnectionDialog(Context context, ClickConfirm clickConfirm) {
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_connection);
        dlg.show();

        Window window = dlg.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.invisible)));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final Button confirm = (Button) dlg.findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                clickConfirm.clickConfirm();
            }
        });
    }
}

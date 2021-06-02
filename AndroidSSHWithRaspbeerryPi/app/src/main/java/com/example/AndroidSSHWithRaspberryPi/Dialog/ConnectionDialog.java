package com.example.AndroidSSHWithRaspberryPi.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.AndroidSSHWithRaspberryPi.R;

public class ConnectionDialog {
    public static final int CONFIRM = 0;
    public static final int OKAY = 1;
    public static final int CLOSE = 2;
    public interface ClickConfirm {
        default void clickConfirm() {}
        default void clickOkay() {}
        default void clickClose() {}
    }
    public ConnectionDialog(Context context, ClickConfirm clickConfirm, int branch) {
        final Dialog dlg = new Dialog(context);
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dlg.setContentView(R.layout.dialog_connection);
        dlg.show();

        Window window = dlg.getWindow();
        window.setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.invisible)));
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final TextView content = dlg.findViewById(R.id.check_connection);


      switch (branch) {
          case OKAY:
              content.setText(R.string.connection_dialog_okay_message);
              break;
          case CLOSE:
              content.setText(R.string.connection_dialog_close_message);
              break;
      }

        final Button confirm = dlg.findViewById(R.id.confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dlg.dismiss();
                switch (branch) {
                    case CONFIRM:
                        clickConfirm.clickConfirm();
                    case OKAY:
                        clickConfirm.clickOkay();
                    case CLOSE:
                        clickConfirm.clickClose();
                }
            }
        });
    }
}

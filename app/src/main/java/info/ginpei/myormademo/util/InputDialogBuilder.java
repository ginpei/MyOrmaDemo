package info.ginpei.myormademo.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

public class InputDialogBuilder extends AlertDialog.Builder {
    private String hint;
    private String defaultValue;
    private InputDialogBuilder.Callback callback;

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public InputDialogBuilder(Context context) {
        super(context);
    }

    public InputDialogBuilder.Callback getCallback() {
        return callback;
    }

    public void setCallback(InputDialogBuilder.Callback callback) {
        this.callback = callback;
    }

    @Override
    public AlertDialog create() {
        final EditText editTextView = new EditText(getContext());
        editTextView.setText(defaultValue);
        editTextView.setHint(hint);
        setView(editTextView);

        setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String result = editTextView.getText().toString();
                callback.onClick(result);
            }
        });

        setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                callback.onClick(null);
            }
        });

        return super.create();
    }

    public interface Callback {
        public void onClick(String result);
    }
}

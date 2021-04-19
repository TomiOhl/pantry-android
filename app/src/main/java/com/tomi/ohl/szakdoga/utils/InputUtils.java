package com.tomi.ohl.szakdoga.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class InputUtils {

    public static void hideKeyboard(Activity activity) {
        hideKeyboard(activity, activity.getCurrentFocus());
    }

    public static void hideKeyboardOnDialog(Dialog dialog) {
        hideKeyboard(dialog.getContext(), dialog.getCurrentFocus());
    }

    private static void hideKeyboard(Context ctx, View focusedView) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && focusedView != null)
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void clearInputLayoutErrors(TextInputLayout inputLayout, EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputLayout.setError(null);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

}

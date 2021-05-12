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

/**
 * A bevitel kezelésével kapcsolatos segédosztály.
 */
public class InputUtils {
    /**
     * A billentyűzet elrejtése Activityből.
     * @param activity az Activity, ahol el szeretnénk rejteni a billentyüzetet.
     */
    public static void hideKeyboard(Activity activity) {
        hideKeyboard(activity, activity.getCurrentFocus());
    }

    /**
     * A billentyűzet elrejtése Dialogből.
     * @param dialog a Dialog, ahol el szeretnénk rejteni a billentyüzetet.
     */
    public static void hideKeyboardOnDialog(Dialog dialog) {
        hideKeyboard(dialog.getContext(), dialog.getCurrentFocus());
    }

    /**
     * A billentyűzet elrejtését kezelő belső metódus.
     * @param ctx kontextus.
     * @param focusedView a fókuszban lévő felületi elem.
     */
    private static void hideKeyboard(Context ctx, View focusedView) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null && focusedView != null)
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * A beviteli mezők hibaüzeneteinek eltüntetése írás közben.
     * @param inputLayout a beviteli mezőt tartalmazó felületi elem.
     * @param editText a beviteli mező.
     */
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

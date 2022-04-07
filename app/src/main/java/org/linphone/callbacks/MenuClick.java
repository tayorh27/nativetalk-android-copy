package org.linphone.callbacks;

import android.view.View;

/**
 * Created by sanniAdewale on 13/01/2018.
 */

public interface MenuClick {
    void onClick(View view, int position);
    void onLongPress(View view, int position);
}

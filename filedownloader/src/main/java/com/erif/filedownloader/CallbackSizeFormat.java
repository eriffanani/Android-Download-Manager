package com.erif.filedownloader;

import androidx.annotation.NonNull;

public interface CallbackSizeFormat {
    public void onFormat(@NonNull String unit, @NonNull String size);
}

package com.veyxstudio.shulehu.object;

import android.support.annotation.Nullable;

/**
 * Created by Veyx Shaw on 16-1-27.
 * Declare an interface to parse Http result.
 */
public interface Parseable {
    void ParseResult(String html, @Nullable String url);
}

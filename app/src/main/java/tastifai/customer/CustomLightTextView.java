package tastifai.customer;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import static java.security.AccessController.getContext;

/**
 * Created by Rohan Nevrikar on 22-02-2018.
 */

public class CustomLightTextView extends TextView{
    public CustomLightTextView(Context context) {
        super(context);
        setFont();
    }
    public CustomLightTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }
    public CustomLightTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setFont();
    }

    private void setFont() {
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/GT-Walsheim-Pro-Light.ttf");
        setTypeface(font, Typeface.NORMAL);
    }
}

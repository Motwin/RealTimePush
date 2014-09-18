/**
 * 
 */
package com.motwin.sample.realTimePush.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.motwin.sample.realTimePush.R;

/**
 * Flashing layout containing one text view and an image indicating variation.
 * 
 * @author Motwin
 * 
 */
public class FlashingTextView extends LinearLayout {

    private final TextView  textView;
    private final ImageView imageView;

    public FlashingTextView(Context aContext) {
        super(aContext);

        textView = newTextView(aContext);
        addView(textView);

        imageView = new ImageView(aContext);
        addView(imageView);
    }

    public FlashingTextView(Context aContext, AttributeSet aAttrs) {
        super(aContext, aAttrs);

        textView = newTextView(aContext);
        addView(textView);

        imageView = new ImageView(aContext);
        addView(imageView);

    }

    /**
     * Set Price : set value in text view, and display progression (flashing
     * background + image)
     * 
     * @param price
     *            the new price
     * @param previousPrice
     *            the previous price
     */
    public void setPrice(int price, int previousPrice) {
        // Set flashing background.
        if (price > previousPrice) {
            // case UP:
            setBackgroundResource(R.drawable.green_background_transition);
            ((TransitionDrawable) getBackground()).startTransition(500);

            imageView.setImageResource(R.drawable.indicator_variation_increase_mini);
        } else if (price < previousPrice) {
            // case DOWN:
            setBackgroundResource(R.drawable.red_background_transition);
            ((TransitionDrawable) getBackground()).startTransition(500);

            imageView.setImageResource(R.drawable.indicator_variation_decrease_mini);
        } else {
            setBackgroundResource(R.drawable.black_background);
            imageView.setImageDrawable(null);
        }

        // Display the new price value. The price text box will flash if
        // price != previousPrice
        textView.setText(String.valueOf(price));
    }

    /**
     * Create the text view
     * 
     * @param aContext
     * @return the text view
     */
    private TextView newTextView(Context aContext) {
        TextView textView;
        textView = new TextView(aContext);
        textView.setTypeface(null, Typeface.BOLD);
        return textView;
    }

}

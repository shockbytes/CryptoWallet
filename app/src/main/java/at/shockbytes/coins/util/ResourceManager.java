package at.shockbytes.coins.util;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.TypedValue;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import at.shockbytes.coins.R;
import at.shockbytes.coins.currency.CryptoCurrency;
import at.shockbytes.coins.currency.Currency;

/**
 * @author Martin Macheiner
 *         Date: 15.06.2017.
 */

public class ResourceManager {

    @Nullable
    public static Uri getProfileImage(Context context) {

        Cursor c = context.getContentResolver().query(
                ContactsContract.Profile.CONTENT_URI, null, null, null, null);

        if (c == null) {
            return null;
        }

        int index_photo_uri = c
                .getColumnIndex(ContactsContract.Profile.PHOTO_URI);

        if (c.moveToNext()) {
            String uri_string = c.getString(index_photo_uri);

            if (uri_string != null) {
                c.close();
                return Uri.parse(uri_string);
            }
        }

        c.close();
        return null;
    }

    public static String getProfileName(Context context) {

        Cursor c = context.getContentResolver().query(
                ContactsContract.Profile.CONTENT_URI, null, null, null, null);

        if (c == null) {
            return "";
        }

        int index_name = c
                .getColumnIndex(ContactsContract.Profile.DISPLAY_NAME);

        if (c.moveToNext()) {

            String name = c.getString(index_name);
            c.close();

            return name;
        }

        c.close();
        return "";
    }

    public static int convertDpInPixel(int dp, Context context) {
        Resources res = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, res.getDisplayMetrics());
    }

    private static Bitmap getBitmap(VectorDrawableCompat vectorDrawable, int padding) {

        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(padding, padding, canvas.getWidth() - padding, canvas.getHeight() - padding);
        vectorDrawable.draw(canvas);
        return bitmap;
    }

    private static Bitmap getBitmap(Context context, int drawableId) {

        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (drawable instanceof BitmapDrawable) {
            return BitmapFactory.decodeResource(context.getResources(), drawableId);
        } else if (drawable instanceof VectorDrawableCompat) {
            int px = convertDpInPixel(24, context);
            return getBitmap((VectorDrawableCompat) drawable, px);
        } else {
            throw new IllegalArgumentException("unsupported drawable type");
        }
    }

    public static RoundedBitmapDrawable createRoundedBitmapFromResource(Context context,
                                                                        @DrawableRes int resId,
                                                                        @ColorRes int bgRes) {

        Bitmap original = getBitmap(context, resId);
        Bitmap image = Bitmap.createBitmap(original.getWidth(), original.getHeight(),
                Bitmap.Config.ARGB_8888);
        image.eraseColor(ContextCompat.getColor(context, bgRes));

        Canvas c = new Canvas(image);
        c.drawBitmap(original, 0, 0, null);

        RoundedBitmapDrawable rdb = RoundedBitmapDrawableFactory.create(context.getResources(), image);
        rdb.setCircular(true);
        return rdb;
    }

    @Nullable
    public static RoundedBitmapDrawable createRoundedBitmap(Context context, Uri uri) {

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
            return createRoundedBitmap(context, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap createStringBitmap(int width, int color, String text) {

        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bmp = Bitmap.createBitmap(width, width, config);

        //Text paint settings
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setTextSize(20f);
        paint.setDither(true);
        paint.setColor(Color.WHITE);
        paint.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(width / 2);

        Canvas canvas = new Canvas(bmp);
        canvas.drawColor(color);

        canvas.drawText(text, width / 2,
                width / 2 - ((paint.descent() + paint.ascent()) / 2), paint);

        return bmp;
    }

    public static RoundedBitmapDrawable createRoundedBitmap(Context context, Bitmap bitmap) {
        RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
        dr.setCornerRadius(Math.max(bitmap.getWidth(), bitmap.getHeight()) / 2.0f);
        dr.setAntiAlias(true);
        dr.setDither(false);
        return dr;
    }

    public static double roundDoubleWithDigits(double value, int digits) {

        if (value == 0) {
            return 0.00;
        }

        if (digits < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(digits, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static int getResourceForCryptoCurrency(CryptoCurrency currency) {

        switch (currency) {

            case BTC:
                return R.drawable.ic_btc;
            case ETH:
                return R.drawable.ic_eth;
            case LTC:
                return R.drawable.ic_ltc;
        }
        return 0;
    }

    public static int getResourceForCurrency(Currency currency) {


        switch (currency) {

            case EUR:
                return R.drawable.ic_eur;
            case USD:
                return R.drawable.ic_usd;
        }
        return 0;

    }

    public static String getSymbolForCurrency(Currency currency) {

        switch (currency) {

            case EUR:
                return "€";
            case USD:
                return "$";
        }
        return "";
    }

}

package andriod.bignerdranch.com;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtils {

    public static Bitmap getScaleBitmap(String path, Activity activity) {
        Point size = new Point();

        activity.getWindowManager().getDefaultDisplay().getRealSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }



    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // Read in image dimensions on disk

        BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by

        int inSampledSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;

            inSampledSize = Math.round(heightScale > widthScale ? heightScale :
                    widthScale);
        }
                options = new BitmapFactory.Options();
                options.inSampleSize = inSampledSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);

    }
}

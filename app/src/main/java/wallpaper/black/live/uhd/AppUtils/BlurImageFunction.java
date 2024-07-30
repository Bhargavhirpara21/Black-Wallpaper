package wallpaper.black.live.uhd.AppUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.renderscript.Allocation;
import androidx.renderscript.Element;
import androidx.renderscript.RenderScript;
import androidx.renderscript.ScriptIntrinsicBlur;

import java.lang.ref.WeakReference;

public class BlurImageFunction {

    private  float BITMAP_SCALE=0.3f;
    private Bitmap image;
    private Context context;
    private float intensity = 08f;
    private float MAX_RADIUS=25;
    private boolean async = false;

    private BlurImageFunction(Context context) {
        this.context = context;
    }

    Bitmap doBlur(){

        if (image == null) {
            return image;
        }

        int width= Math.round(image.getWidth()*BITMAP_SCALE);
        int height = Math.round(image.getHeight()*BITMAP_SCALE);

        Bitmap input= Bitmap.createScaledBitmap(image,width,height,false);

        Bitmap output= Bitmap.createBitmap(input);

        try {
            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur intrinsicBlur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            Allocation inputallocation= Allocation.createFromBitmap(rs,input);
            Allocation outputallocation= Allocation.createFromBitmap(rs,output);
            intrinsicBlur.setRadius(intensity);
            intrinsicBlur.setInput(inputallocation);
            intrinsicBlur.forEach(outputallocation);

            outputallocation.copyTo(output);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return output;

    }

    @NonNull
    public static BlurImageFunction with(Context context) {
        return new BlurImageFunction(context);
    }

    /*
     * Here we get bitmap on which we apply the blur process
     * */

    public BlurImageFunction load(Bitmap bitmap) {
        this.image = bitmap;
        return this;
    }

    public BlurImageFunction load(int res) {
        image = BitmapFactory.decodeResource(context.getResources(), res);
        return this;
    }

    public BlurImageFunction intensity(float intensity) {
        if (intensity<MAX_RADIUS && intensity > 0)
            this.intensity = intensity;
        else
            this.intensity = MAX_RADIUS;
        return this;
    }

    public BlurImageFunction Async(boolean async) {
        this.async = async;
        return this;
    }

    public void into(ImageView imageView) {

        if (async) {
            new AsyncBlurImage(imageView).execute();
        }
        else {
            try {
                Bitmap temp= doBlur();
                if(temp!=null)
                    imageView.setImageBitmap(temp);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Bitmap getImageBlur() {
        return doBlur();
    }

    /*
     * when developer make async true the this class will executed and it perform the blurring process in background
     * */

    private class AsyncBlurImage extends AsyncTask<Void, Void, Bitmap> {
        private WeakReference<ImageView> weakReference;

        public AsyncBlurImage(ImageView image) {
            this.weakReference = new WeakReference<ImageView>(image);
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            return doBlur();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            ImageView imageView = weakReference.get();

            if (imageView != null && bitmap != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}


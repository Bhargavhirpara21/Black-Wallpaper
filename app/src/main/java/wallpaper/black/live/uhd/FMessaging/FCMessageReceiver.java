package wallpaper.black.live.uhd.FMessaging;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import wallpaper.black.live.uhd.AppUtils.AppUtility;
import wallpaper.black.live.uhd.AppUtils.Constants;
import wallpaper.black.live.uhd.AppUtils.InAppPreference;
import wallpaper.black.live.uhd.AppUtils.LoggerCustom;
import wallpaper.black.live.uhd.Model.CategoryDataModel;
import wallpaper.black.live.uhd.Model.IModel;
import wallpaper.black.live.uhd.Model.WallpaperModel;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.RedirectActivity;
import wallpaper.black.live.uhd.retrofit.RetroApiRequest;
import wallpaper.black.live.uhd.retrofit.RetroCallbacks;

public class FCMessageReceiver extends FirebaseMessagingService implements RetroCallbacks {

    InAppPreference store;
    private final String TAG = "MyFCMService";
    private static final String CHANNEL_ID = "BlackNotification";
    String msg_display,title_display;

    private final String[] sampleText=new String[]{"Let's Check Out New Dark Wallpapers ❤",
            "Check Out Awesome Wallpapers",
            "Awesome black Wallpapers Added",
            "\uD83E\uDD70 Lovely wallpapers waiting for you. Just Check them out! \uD83E\uDD70",
            "Wallpapers Added. Let's Check Them Out ❤",
            "Set Live Wallpaper and Make your phone to smart \uD83D\uDCF1",
            "Cool Wallpapers Added. Let's Check Them Out ❤",
            "\uD83D\uDC49 You have new black wallpapers. Click to check them",
            "Take a moment and see our new wallpaper category we are sure you love it ❤",
            "Hey! It's time to change your wallpaper. tap and check our new category",
            "It's true. The double wallpapers are on Black.ly",
            "Set anything you want with Pitch black wallpaper",
            "Get the aesthetic wallpaper that you will surely love to put on your screen",
            "Waiting for cool and live wallpaper?",
            "Black and HD wallpaper are a amazing",
            "Very easy to use and it has live unique wallpaper",
            "High quality Black wallpapers and there are almost no ads",
            "Awesome wallpaper with a large variety of unique, high-quality pictures",
            "its nice to be able to choose a new wallpaper every day",
            "Black.ly: easy to use good wonderful pics",
            "WOW felt like seen the most beautiful thing on earth",
            "If you want that wallpaper you can just download it easily by a simple click and boom",
            "It has all the interesting wallpapers and this wallpapers make a phone look new",
            "Simple way to pick out a both screen easy to use",
            "We have lot of categorises like Anime, Abstract,Cars,Nature..etc",
            "It gives really good wallpapers that fit to any phone screen",
            "We have plenty of pics to choose from",
            "Absolutely amazing wallpaper❤",
            "The app has tons of cool wallpapers and themes",
            "The best part of it all is that it's all free now"
    };
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        store = InAppPreference.getInstance(getApplicationContext());
        String message = remoteMessage.getData().toString();
        LoggerCustom.erorr(TAG, "From: " + remoteMessage.getFrom());

        if (store.getNotification()) {
            if (TextUtils.isEmpty(message))
                return;
            try {
                message = remoteMessage.getData().get("body");
                LoggerCustom.erorr(TAG, "message: " + message);


                //////////////
                WallpaperModel post = null;
                CategoryDataModel category = null;
                String type="";
                try {
                    if(TextUtils.isEmpty(message))
                        return;

                    try {
                        JSONObject jsonObject=new JSONObject(message);
                        type=jsonObject.optString("type");
                        msg_display=jsonObject.optString("msg");
                        title_display=jsonObject.optString("title_display");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if(!TextUtils.isEmpty(type) && type.equalsIgnoreCase("5")){
                        Gson gson = new Gson();
                        category = gson.fromJson(message, CategoryDataModel.class);
                    }else {
                        Gson gson = new Gson();
                        post = gson.fromJson(message, WallpaperModel.class);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }catch (Error e) {
                    e.printStackTrace();
                }


                try {
                    if(!TextUtils.isEmpty(type) && type.equalsIgnoreCase("3")){
                        showNotification(title_display,msg_display);
                    }else if(!TextUtils.isEmpty(type) && type.equalsIgnoreCase("5")){
                        sendNotification(post,category);
                    }else
                        sendNotification(post,category);
                } catch (Exception e) {
                    e.printStackTrace();
                }

////////////////////
//                JSONObject json = new JSONObject(message);
//                Logger.e("json", String.valueOf(json));
//
//
//                String title = (String) json.get("title_display");
//                Logger.e("title", title);
//                String msg = (String) json.get("msg");
//                Logger.e("msg", msg);
//
//                showNotification(title, msg);

            } catch (Exception e) {
                e.printStackTrace();
                LoggerCustom.erorr(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    public void showNotification(String title, String msg) {

        createNotificationChannel();

        Intent intent = new Intent(getApplicationContext(), RedirectActivity.class);

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    100,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE
            );
        }else{
            pendingIntent = PendingIntent.getActivity(
                    getApplicationContext(),
                    100,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
            );
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_launcher);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID);
        notificationBuilder.setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setSmallIcon(getNotificationIcon(notificationBuilder))
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setContentText(msg);

        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        manager.notify(9999, notificationBuilder.build());
        LoggerCustom.erorr("msg", ".....   end   ......");
    }

    private int getNotificationIcon(NotificationCompat.Builder notificationBuilder) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color = 0x454544;
            notificationBuilder.setColor(color);
            return R.mipmap.notificaton_icon;

        } else {
            return R.mipmap.ic_launcher;
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "BlackWall";
            String description = "Black Wallpaper";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        store = InAppPreference.getInstance(getApplicationContext());
        store.setFCM(s);
        LoggerCustom.erorr("token", "" + s);
        updateData(s);
    }

    private void updateData(String s) {
        if (TextUtils.isEmpty(store.getUserId())) {
            return;
        } else {
            String user_id = store.getUserId();
            RetroApiRequest.updateFCMTokenRetro(getApplicationContext(), user_id, s, this);
        }
    }

    /////////////////////
    private void sendNotification(final WallpaperModel post, final CategoryDataModel category) {

        class sendNotificationTask extends AsyncTask<String,Void,Bitmap> {
            Context context;
            private sendNotificationTask(Context c) {
                this.context=c;
            }
            @Override
            protected Bitmap doInBackground(String... strings) {
                Bitmap bitmapImage = null;
                try {

                    if(category!=null) {
                        String path =  store.getImgUrl() + Constants.catImgPath + category.getImagePath();
                        bitmapImage = Glide
                                .with(FCMessageReceiver.this)
                                .asBitmap()
                                .load(path)
                                .submit()
                                .get();
                    }else{
                        String imagePath = post.getImgPath();
                        if (!TextUtils.isEmpty(imagePath)) {
                            String path="";

                            if (imagePath.startsWith("http")) {
                                path = imagePath;
                            }else{
                                path=store.getImgUrl() + Constants.thumbPath +imagePath;
                            }
                            bitmapImage = Glide
                                    .with(FCMessageReceiver.this)
                                    .asBitmap()
                                    .load(path)
                                    .submit()
                                    .get();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return bitmapImage;
            }

            @Override
            protected void onPostExecute(Bitmap bitmapImage) {
                super.onPostExecute(bitmapImage);
                displayNotification(FCMessageReceiver.this,post,category,bitmapImage,msg_display);
            }
        }
        new sendNotificationTask(this).execute();
    }

    private void displayNotification(Context context, WallpaperModel post, CategoryDataModel category, Bitmap bitmapImage, String msg){

        createNotificationChannel();
        int notificationId= 0;
        try {
            notificationId = Integer.parseInt(post.getImgId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            notificationId = Integer.parseInt(category.getCategoryId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context,CHANNEL_ID);

            Intent intent = new Intent(context, RedirectActivity.class);
            if(post!=null && bitmapImage!=null){
                LoggerCustom.erorr(TAG,"post added:"+post.getImgId());
                Bundle bundle = new Bundle();
                bundle.putSerializable("post", post);
                intent.putExtra("bundle", bundle);
//                intent.putExtra("post",post);
            }else if(category!=null){
                Bundle bundle = new Bundle();
                bundle.putSerializable("category", category);
                intent.putExtra("bundle", bundle);
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                    Intent.FLAG_ACTIVITY_SINGLE_TOP);

            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                pendingIntent = PendingIntent.getActivity(context, notificationId, intent,
                        PendingIntent.FLAG_IMMUTABLE);
            }else{
                pendingIntent = PendingIntent.getActivity(context, notificationId, intent,
                        PendingIntent.FLAG_ONE_SHOT);
            }
            Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher);

            String title= AppUtility.capitalizeText(getResources().getString(R.string.app_name));
            if(!TextUtils.isEmpty(title_display))
                title=title_display;

            if(TextUtils.isEmpty(msg_display))
                msg="Click to download and explore more 4K Wallpaper";

            try {
                if(bitmapImage==null && post!=null){
                    int position = getRandomNumber();
                    msg = sampleText[position];
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            notificationBuilder.setSmallIcon(getNotificationIcon(notificationBuilder))
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setLargeIcon(bitmap)
                    //                .setSmallIcon(getNotificationIcon())
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setAutoCancel(true)
                    //                .setColor(getColor(R.color.placeholder_grey_20))
                    //                .setVibrate(new long[] { 1000, 1000, 1000 })
                    .setDefaults(RingtoneManager.TYPE_NOTIFICATION)
                    .setSound(defaultSoundUri)
                    .setContentIntent(pendingIntent);

            if(bitmapImage!=null)
                notificationBuilder.setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmapImage).setSummaryText(msg));

            if(notificationId==0){
                notificationId = 100;
            }
            LoggerCustom.erorr(TAG,"ID: "+notificationId);
            notificationManager.notify(notificationId, notificationBuilder.build());
        } catch (Exception e) {
            e.printStackTrace();
        }catch (Error e) {
            e.printStackTrace();
        }
    }

    private int getRandomNumber(){
        final int min = 0;
        final int max = sampleText.length;
        final int random = new Random().nextInt((max - min) + 1) + min;
        return random;
    }

    @Override
    public void onDataSuccess(IModel result) throws JSONException {

    }

    @Override
    public void onDataError(String result) throws Exception {

    }
    /////////
}

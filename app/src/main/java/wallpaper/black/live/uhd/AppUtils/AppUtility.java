package wallpaper.black.live.uhd.AppUtils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import wallpaper.black.live.uhd.BlackApplication;
import wallpaper.black.live.uhd.BuildConfig;
import wallpaper.black.live.uhd.Interface.InternetNetworkInterface;
import wallpaper.black.live.uhd.R;
import wallpaper.black.live.uhd.SingletonControl;

public class AppUtility {
    public static final int CAROUSEL_ROTATE_TIME = 2000;
    public static final boolean CAROUSEL_AUTO_ROTATE = true;
    public static final String NEW_FOLDER_NAME = "Black Wallpapers";
    public static boolean isInterNetworkAvailable(Context context) {
        try {

            final ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()
                    && networkInfo.isAvailable()) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public static void showInterNetworkDialog(Context c, InternetNetworkInterface networkInterface) {

        try {
            if(c==null || ((Activity)c)==null || ((Activity)c).isFinishing()){
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(c, R.style.AlertDialogCustom);
        } else {
            builder = new AlertDialog.Builder(c);
        }

        builder.setCancelable(false);
        builder.setMessage(c.getString(R.string.no_internet));
        builder.setTitle(c.getString(R.string.info_btn));

        builder.setPositiveButton(c.getString(R.string.ok_btn),(dialogInterface, i) -> {
            dialogInterface.dismiss();
            try {
                networkInterface.onOkClick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }
    public static String getDeviceTextName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalizeText(model);
        } else {
            return capitalizeText(manufacturer) + " " + model;
        }
    }

    public static String capitalizeText(String s) {
        if (s == null) return null;
        if (s.length() == 1) {
            return s.toUpperCase();
        }
        if (s.length() > 1) {
            return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        }
        return s;
    }

    public static String getHashKeyApp(Context context) {
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                if (something != null) {
                    something = something.trim();
                }
                Log.i("MyKey",something);
                return something;
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
        return null;
    }

    public static String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }

    public static String[] storge_permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public static String[] storge_permissions_33 = {
            Manifest.permission.READ_MEDIA_IMAGES,
    };

    public static String[] permissions() {
        String[] p;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storge_permissions_33;
        } else {
            p = storge_permissions;
        }
        return p;
    }

    private static String mHardwareId;
    public static String getHardwareId(Context mContext) {

        if (mHardwareId == null) {

            mHardwareId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);

            if (TextUtils.isEmpty(mHardwareId)) {
                mHardwareId = Build.SERIAL;
//                mDeviceHardwareIdType = DeviceHardwareIdType.SERIAL_ID;
            }

            if (TextUtils.isEmpty(mHardwareId)) {
                mHardwareId = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
//                mDeviceHardwareIdType = DeviceHardwareIdType.ANDROID_ID;
            }
        }

        return mHardwareId;// "64551335898452230";//"645513358984522310";//"6455133589845";//
    }

    public static String appVertion(Context c) {
        PackageInfo pInfo = null;
        String version = "";
        try {
            pInfo = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
            version = "" + pInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return version;
    }

    public static String getLandCodeSystem(Context context){
        Locale primaryLocale = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            primaryLocale = context.getResources().getConfiguration().getLocales().get(0);
        }else
            primaryLocale = context.getResources().getConfiguration().locale;
        String deviceLangName="";
        String deviceLangCode="";
        if(primaryLocale!=null){
            deviceLangName=primaryLocale.getDisplayLanguage();
            deviceLangCode=primaryLocale.getLanguage();
            LoggerCustom.erorr("loadLocal","Name:"+primaryLocale.getLanguage()+" Languege:"+deviceLangName);
        }
        return deviceLangCode;
    }
    public static String getOsVersion() {
        int sdkVersion = android.os.Build.VERSION.SDK_INT;
        return sdkVersion + "";
    }

    public static String createBaseDirectory() {
        File pictureFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        if (!pictureFolder.exists()) {
            pictureFolder.mkdirs();
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + File.separator + NEW_FOLDER_NAME);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LoggerCustom.erorr("getReceiveFilePath :: ", "Problem creating Image folder");
            }
        }
        LoggerCustom.erorr("file.getAbsolutePath()", file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static String getSavedWallpaperPath() {

        File file = new File(createBaseDirectory());
        if (!file.exists()) {
            if (!file.mkdirs()) {
                LoggerCustom.erorr("getReceiveFilePath :: ", "Problem creating Image folder");
            }
        }
        LoggerCustom.erorr("file.getSavedFilePath()", file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static void sharedWallpaper(Context activity, String text, String downloadedPath, String package_name, boolean isVideo) {

        LoggerCustom.erorr("downloadedPath", "" + downloadedPath);

        if (TextUtils.isEmpty(text)) {
            try {
                text = SingletonControl.getInstance().getDataList().getLogic().getShareText();
            } catch (Exception e) {
                e.printStackTrace();
                text="Most Beautiful and Unique collection of Black Wallpapers. Download it from here. https://bit.ly/478PoVI";
            }
        }
        try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);

            File file = new File(downloadedPath);
            Uri photoURI = FileProvider.getUriForFile(activity, activity.getPackageName() + ".provider", file);

            //Add text and then Image URI
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
            shareIntent.putExtra(Intent.EXTRA_STREAM, photoURI);
            if (isVideo)
                shareIntent.setType("video/mp4");
            else
                shareIntent.setType("image/jpg");

            if (TextUtils.isEmpty(package_name)) {
                try {
                    activity.startActivity(shareIntent);
                } catch (android.content.ActivityNotFoundException ex) {}
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deletePostImage(Context activity, String downloadedPath) {
        File fdelete = new File(downloadedPath);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                LoggerCustom.erorr("-->", "file Deleted :" + downloadedPath);
                scanToGallery(activity, downloadedPath);
            } else {
                LoggerCustom.erorr("-->", "file not Deleted :" + downloadedPath);
            }
        }
    }
    public static void scanToGallery(Context context, String filePath) {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                String[] strArr = new String[1];
                strArr[0] = new File(filePath).getAbsolutePath();
                MediaScannerConnection.scanFile(context, strArr, null, new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String str, Uri uri) {
                    }
                });
            } else {
                context.sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showDialogUser(Context mContext, String title, String message, String ok, String cancel, boolean isCancelable, final DialogOnButtonClickListener dialogOnButtonClickListener) {
        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Light_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (dialogOnButtonClickListener != null) {
                            dialog.dismiss();
                            dialogOnButtonClickListener.onOKButtonCLick();
                        }
                    }
                });
        if (!TextUtils.isEmpty(cancel))
            builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
        builder.setCancelable(isCancelable);

        builder.show();
    }

    public interface DialogOnButtonClickListener {
        void onOKButtonCLick();

        void onCancelButtonCLick();
    }

    public static boolean isStoreVersion(Context context) {
        boolean result = false;
        try {
            String installer = context.getPackageManager()
                    .getInstallerPackageName(context.getPackageName());
            Log.e("installer: ", "isStoreVersion::" + installer);
            if (TextUtils.isEmpty(installer)) {
                result = false;
            } else if (installer.contains("com.android.vending")) {
                result = true;
            }
        } catch (Throwable e) {
        }
        if (BuildConfig.DEBUG) {
            return Constants.IS_TEST_AD_ACTIVE;
        }
        return result;
    }

    public static String createBaseDirectoryCache() {
        File file = new File(getCacheDir(), "Videos");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("getReceiveFilePath :: ", "Problem creating Image folder");

            }
        }
        LoggerCustom.erorr("file.getAbsolutePath()", file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static String getCacheDir() {

        try {
            return BlackApplication.bContext.getCacheDir().getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return createBaseDirectory();
        }
    }

    public static int linkProgramGLES30(
            final int vertShader,
            final int fragShader
    ) throws RuntimeException {
        int program = GLES30.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Failed to create program");
        }
        GLES30.glAttachShader(program, vertShader);
        GLES30.glAttachShader(program, fragShader);
        GLES30.glLinkProgram(program);
        final int[] status = new int[1];
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            final String log = GLES30.glGetProgramInfoLog(program);
            GLES30.glDeleteProgram(program);
            throw new RuntimeException(log);
        }
        return program;
    }


    public static int compileShaderResourceGLES30(
            @NonNull Context context,
            final int shaderType,
            final int shaderRes
    ) throws RuntimeException {
        final InputStream inputStream = context.getResources().openRawResource(shaderRes);
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        final String shaderSource = stringBuilder.toString();
        int shader = GLES30.glCreateShader(shaderType);
        if (shader == 0) {
            throw new RuntimeException("Failed to create shader");
        }
        GLES30.glShaderSource(shader, shaderSource);
        GLES30.glCompileShader(shader);
        final int[] status = new int[1];
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            final String log = GLES30.glGetShaderInfoLog(shader);
            GLES30.glDeleteShader(shader);
            throw new RuntimeException(log);
        }
        return shader;
    }

    public static int compileShaderResourceGLES20(
            @NonNull Context context,
            final int shaderType,
            final int shaderRes
    ) throws RuntimeException {
        final InputStream inputStream = context.getResources().openRawResource(shaderRes);
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        final StringBuilder stringBuilder = new StringBuilder();
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        final String shaderSource = stringBuilder.toString();
        int shader = GLES20.glCreateShader(shaderType);
        if (shader == 0) {
            throw new RuntimeException("Failed to create shader");
        }
        GLES20.glShaderSource(shader, shaderSource);
        GLES20.glCompileShader(shader);
        final int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) {
            final String log = GLES20.glGetShaderInfoLog(shader);
            GLES20.glDeleteShader(shader);
            throw new RuntimeException(log);
        }
        return shader;
    }

    public static int linkProgramGLES20(
            final int vertShader,
            final int fragShader
    ) throws RuntimeException {
        int program = GLES20.glCreateProgram();
        if (program == 0) {
            throw new RuntimeException("Failed to create program");
        }
        GLES20.glAttachShader(program, vertShader);
        GLES20.glAttachShader(program, fragShader);
        GLES20.glLinkProgram(program);
        final int[] status = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) {
            final String log = GLES20.glGetProgramInfoLog(program);
            GLES20.glDeleteProgram(program);
            throw new RuntimeException(log);
        }
        return program;
    }

    public static void showDialogConfirmationUser(Context mContext, String title, String message, final DialogOnButtonClickListener dialogOnButtonClickListener) {
        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, R.style.AlertDialog);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        LoggerCustom.erorr("dialog", "declare");
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            if (dialogOnButtonClickListener != null) {
                                dialog.dismiss();
                                dialogOnButtonClickListener.onOKButtonCLick();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mContext.getResources().getColor(R.color.black));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mContext.getResources().getColor(R.color.black));
            }
        });
        dialog.show();
        LoggerCustom.erorr("dialog", "show");
    }
    public static int getScreenWidth(Activity mActivity) {
        final WindowManager w = (WindowManager) mActivity
                .getSystemService(Context.WINDOW_SERVICE);
        final Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        return metrics.widthPixels;
    }

    public static String createBaseDirectoryCacheDoubleWall() {
        File file = new File(getCacheDir(), "Double");
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("getReceiveFilePath :: ", "Problem creating Image folder");
            }
        }
        LoggerCustom.erorr("file.getAbsolutePath()", file.getAbsolutePath());
        return file.getAbsolutePath();
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showDialogOK(Context context, String title, String buttonName, String message, DialogInterface.OnClickListener okListener) {

        AlertDialog.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        } else {
            builder = new AlertDialog.Builder(context);
        }

        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonName, okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
}
